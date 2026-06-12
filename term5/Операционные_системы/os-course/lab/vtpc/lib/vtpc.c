#define _GNU_SOURCE

#include "vtpc.h"

#include <errno.h>
#include <fcntl.h>
#include <inttypes.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <unistd.h>

static void best_effort_disable_os_cache(int osfd) {
    (void)osfd;
#if defined(__APPLE__) && defined(F_NOCACHE)
    (void)fcntl(osfd, F_NOCACHE, 1);
#endif
}

#ifndef VTPC_DEFAULT_CACHE_PAGES
#define VTPC_DEFAULT_CACHE_PAGES 256u
#endif

#ifndef VTPC_MAX_CACHE_PAGES
#define VTPC_MAX_CACHE_PAGES 65536u
#endif

typedef struct VtpcFile {
    int   osfd;
    int   readable;
    int   writable;
    int   append;       
    off_t pos;

    off_t logical_size;    
    off_t known_disk_size; 
} VtpcFile;

typedef struct Page {
    int   osfd;
    off_t page_no;

    unsigned char *data; 
    size_t valid;        
    int dirty;

    struct Page *hnext; 
    struct Page *prev;  
    struct Page *next;  
} Page;

typedef struct Cache {
    Page  **buckets;
    size_t nbuckets;

    Page  *lru_head;
    Page  *lru_tail;

    size_t pages;
    size_t max_pages;
    size_t block;
} Cache;

static int g_inited = 0;
static Cache g_cache = {0};

static VtpcFile **g_files = NULL;
static size_t g_files_cap = 0;

static uint64_t splitmix64(uint64_t x) {
    x += 0x9e3779b97f4a7c15ULL;
    x = (x ^ (x >> 30)) * 0xbf58476d1ce4e5b9ULL;
    x = (x ^ (x >> 27)) * 0x94d049bb133111ebULL;
    return x ^ (x >> 31);
}

static size_t next_pow2(size_t x) {
    if (x < 2) return 2;
    size_t p = 1;
    while (p < x) p <<= 1;
    return p;
}

static size_t hash_key(int osfd, off_t page_no) {
    uint64_t k = (((uint64_t)(uint32_t)osfd) << 32) ^ (uint64_t)page_no;
    return (size_t)splitmix64(k);
}

static ssize_t pread_retry(int fd, void *buf, size_t count, off_t off) {
    for (;;) {
        ssize_t n = pread(fd, buf, count, off);
        if (n < 0 && errno == EINTR) continue;
        return n;
    }
}

static ssize_t pwrite_retry(int fd, const void *buf, size_t count, off_t off) {
    size_t done = 0;
    while (done < count) {
        ssize_t n = pwrite(fd, (const unsigned char *)buf + done,
                           count - done, off + (off_t)done);
        if (n < 0) {
            if (errno == EINTR) continue;
            return -1;
        }
        if (n == 0) {
            errno = EIO;
            return -1;
        }
        done += (size_t)n;
    }
    return (ssize_t)done;
}

static void lru_remove(Page *p) {
    if (!p) return;
    if (p->prev) p->prev->next = p->next;
    else g_cache.lru_head = p->next;

    if (p->next) p->next->prev = p->prev;
    else g_cache.lru_tail = p->prev;

    p->prev = p->next = NULL;
}

static void lru_push_front(Page *p) {
    p->prev = NULL;
    p->next = g_cache.lru_head;
    if (g_cache.lru_head) g_cache.lru_head->prev = p;
    g_cache.lru_head = p;
    if (!g_cache.lru_tail) g_cache.lru_tail = p;
}

static void lru_touch(Page *p) {
    if (g_cache.lru_head == p) return;
    lru_remove(p);
    lru_push_front(p);
}


static Page *hash_find(int osfd, off_t page_no) {
    size_t idx = hash_key(osfd, page_no) & (g_cache.nbuckets - 1);
    for (Page *p = g_cache.buckets[idx]; p; p = p->hnext) {
        if (p->osfd == osfd && p->page_no == page_no) return p;
    }
    return NULL;
}

static void hash_insert(Page *p) {
    size_t idx = hash_key(p->osfd, p->page_no) & (g_cache.nbuckets - 1);
    p->hnext = g_cache.buckets[idx];
    g_cache.buckets[idx] = p;
}

static void hash_remove(Page *p) {
    size_t idx = hash_key(p->osfd, p->page_no) & (g_cache.nbuckets - 1);
    Page **cur = &g_cache.buckets[idx];
    while (*cur) {
        if (*cur == p) {
            *cur = p->hnext;
            p->hnext = NULL;
            return;
        }
        cur = &(*cur)->hnext;
    }
}

static int parse_env_cache_pages(size_t *out_pages) {
    const char *e = getenv("VTPC_CACHE_PAGES");
    if (!e || !*e) return 0;

    errno = 0;
    char *end = NULL;
    unsigned long long v = strtoull(e, &end, 10);
    if (errno != 0 || end == e || *end != '\0') return -1;

    if (v == 0 || v > VTPC_MAX_CACHE_PAGES) return -1;
    *out_pages = (size_t)v;
    return 0;
}

static int vtpc_init_once(void) {
    if (g_inited) return 0;

    long ps = sysconf(_SC_PAGESIZE);
    size_t block = (ps > 0) ? (size_t)ps : 4096u;
    if (block < 512) block = 512;

    size_t pages = VTPC_DEFAULT_CACHE_PAGES;
    if (parse_env_cache_pages(&pages) != 0) {
        pages = VTPC_DEFAULT_CACHE_PAGES;
    }

    g_cache.block = block;
    g_cache.max_pages = pages;
    g_cache.nbuckets = next_pow2(pages * 2);
    g_cache.buckets = (Page **)calloc(g_cache.nbuckets, sizeof(Page *));
    if (!g_cache.buckets) {
        errno = ENOMEM;
        return -1;
    }

    g_inited = 1;
    return 0;
}

static VtpcFile *get_file(int fd) {
    if (fd <= 0) {
        errno = EBADF;
        return NULL;
    }
    size_t idx = (size_t)(fd - 1);
    if (idx >= g_files_cap || !g_files || !g_files[idx]) {
        errno = EBADF;
        return NULL;
    }
    return g_files[idx];
}

static int alloc_handle(VtpcFile *f) {
    for (size_t i = 0; i < g_files_cap; ++i) {
        if (g_files[i] == NULL) {
            g_files[i] = f;
            return (int)(i + 1);
        }
    }

    size_t ncap = g_files_cap ? g_files_cap * 2 : 32;
    VtpcFile **nf = (VtpcFile **)realloc(g_files, ncap * sizeof(VtpcFile *));
    if (!nf) {
        errno = ENOMEM;
        return -1;
    }
    for (size_t i = g_files_cap; i < ncap; ++i) nf[i] = NULL;

    g_files = nf;
    g_files[g_files_cap] = f;
    int h = (int)(g_files_cap + 1);
    g_files_cap = ncap;
    return h;
}

static void free_handle(int fd) {
    if (fd <= 0) return;
    size_t idx = (size_t)(fd - 1);
    if (idx < g_files_cap && g_files) g_files[idx] = NULL;
}

static int flush_page(Page *p) {
    if (!p || !p->dirty) return 0;
    off_t off = p->page_no * (off_t)g_cache.block;
    if (pwrite_retry(p->osfd, p->data, g_cache.block, off) < 0) return -1;
    p->dirty = 0;
    return 0;
}

static void free_page(Page *p) {
    if (!p) return;
    free(p->data);
    free(p);
}

static int evict_one(void) {
    Page *victim = g_cache.lru_tail;
    if (!victim) return 0;

    if (victim->dirty) {
        if (flush_page(victim) != 0) return -1;
    }

    hash_remove(victim);
    lru_remove(victim);
    g_cache.pages--;
    free_page(victim);
    return 0;
}

static int ensure_capacity(void) {
    while (g_cache.pages >= g_cache.max_pages) {
        if (evict_one() != 0) return -1;
    }
    return 0;
}

static Page *page_get_or_load(VtpcFile *vf, off_t page_no) {
    Page *p = hash_find(vf->osfd, page_no);
    if (p) {
        lru_touch(p);
        return p;
    }

    if (ensure_capacity() != 0) return NULL;

    Page *np = (Page *)calloc(1, sizeof(Page));
    if (!np) {
        errno = ENOMEM;
        return NULL;
    }
    np->osfd = vf->osfd;
    np->page_no = page_no;

    void *mem = NULL;
    int rc = posix_memalign(&mem, g_cache.block, g_cache.block);
    if (rc != 0 || !mem) {
        free(np);
        errno = (rc == ENOMEM) ? ENOMEM : EINVAL;
        return NULL;
    }
    np->data = (unsigned char *)mem;
    memset(np->data, 0, g_cache.block);

    off_t off = page_no * (off_t)g_cache.block;
    ssize_t n = pread_retry(vf->osfd, np->data, g_cache.block, off);
    if (n < 0) {
        int e = errno;
        free_page(np);
        errno = e;
        return NULL;
    }
    np->valid = (n > 0) ? (size_t)n : 0;

    hash_insert(np);
    lru_push_front(np);
    g_cache.pages++;
    return np;
}

static void drop_pages_for_osfd(int osfd) {
    Page *p = g_cache.lru_head;
    while (p) {
        Page *next = p->next;
        if (p->osfd == osfd) {
            hash_remove(p);
            lru_remove(p);
            g_cache.pages--;
            free_page(p);
        }
        p = next;
    }
}


int vtpc_open(const char *path, int flags, int mode) {
    if (vtpc_init_once() != 0) return -1;
    if (!path) {
        errno = EINVAL;
        return -1;
    }

    int acc = (flags & O_ACCMODE);
    int readable = (acc == O_RDONLY) || (acc == O_RDWR);
    int writable = (acc == O_WRONLY) || (acc == O_RDWR);

    int os_flags = flags | O_CLOEXEC;

    int osfd = -1;
#ifdef O_DIRECT
    osfd = open(path, os_flags | O_DIRECT, (mode_t)mode);
    if (osfd < 0 && (errno == EINVAL || errno == EOPNOTSUPP)) {
        osfd = open(path, os_flags, (mode_t)mode);
    }
#else
    osfd = open(path, os_flags, (mode_t)mode);
#endif

    if (osfd < 0) return -1;
    best_effort_disable_os_cache(osfd);

    struct stat st;
    if (fstat(osfd, &st) != 0) {
        int e = errno;
        close(osfd);
        errno = e;
        return -1;
    }

    VtpcFile *vf = (VtpcFile *)calloc(1, sizeof(VtpcFile));
    if (!vf) {
        close(osfd);
        errno = ENOMEM;
        return -1;
    }

    vf->osfd = osfd;
    vf->readable = readable;
    vf->writable = writable;
    vf->append = (flags & O_APPEND) ? 1 : 0;

    vf->logical_size = (off_t)st.st_size;
    vf->known_disk_size = (off_t)st.st_size;

    vf->pos = 0;
    if (vf->append) vf->pos = vf->logical_size;

    int h = alloc_handle(vf);
    if (h < 0) {
        int e = errno;
        free(vf);
        close(osfd);
        errno = e;
        return -1;
    }

    return h;
}

int vtpc_close(int fd) {
    VtpcFile *vf = get_file(fd);
    if (!vf) return -1;

    int saved_errno = 0;

    if (vtpc_fsync(fd) != 0) {
        saved_errno = errno;
    }

    drop_pages_for_osfd(vf->osfd);

    if (close(vf->osfd) != 0 && saved_errno == 0) {
        saved_errno = errno;
    }

    free_handle(fd);
    free(vf);

    if (saved_errno != 0) {
        errno = saved_errno;
        return -1;
    }
    return 0;
}

ssize_t vtpc_read(int fd, void *buf, size_t count) {
    if (count == 0) return 0;

    VtpcFile *vf = get_file(fd);
    if (!vf) return -1;
    if (!vf->readable) {
        errno = EBADF;
        return -1;
    }
    if (!buf) {
        errno = EINVAL;
        return -1;
    }

    if (vf->pos >= vf->logical_size) return 0;

    size_t done = 0;
    unsigned char *out = (unsigned char *)buf;

    while (done < count) {
        if (vf->pos >= vf->logical_size) break;

        off_t page_no = vf->pos / (off_t)g_cache.block;
        size_t page_off = (size_t)(vf->pos % (off_t)g_cache.block);

        Page *p = page_get_or_load(vf, page_no);
        if (!p) return -1;

        size_t can = g_cache.block - page_off;
        size_t need = count - done;
        size_t chunk = (can < need) ? can : need;

        off_t remain = vf->logical_size - vf->pos;
        if (remain <= 0) break;
        if ((off_t)chunk > remain) chunk = (size_t)remain;

        memcpy(out + done, p->data + page_off, chunk);
        vf->pos += (off_t)chunk;
        done += chunk;
    }

    return (ssize_t)done;
}

ssize_t vtpc_write(int fd, const void *buf, size_t count) {
    if (count == 0) return 0;

    VtpcFile *vf = get_file(fd);
    if (!vf) return -1;
    if (!vf->writable) {
        errno = EBADF;
        return -1;
    }
    if (!buf) {
        errno = EINVAL;
        return -1;
    }

    const unsigned char *in = (const unsigned char *)buf;
    size_t done = 0;

    while (done < count) {
        if (vf->append) vf->pos = vf->logical_size;

        off_t page_no = vf->pos / (off_t)g_cache.block;
        size_t page_off = (size_t)(vf->pos % (off_t)g_cache.block);

        Page *p = page_get_or_load(vf, page_no);
        if (!p) return -1;

        size_t can = g_cache.block - page_off;
        size_t need = count - done;
        size_t chunk = (can < need) ? can : need;

        memcpy(p->data + page_off, in + done, chunk);
        p->dirty = 1;

        size_t new_valid = page_off + chunk;
        if (new_valid > p->valid) p->valid = new_valid;

        vf->pos += (off_t)chunk;
        done += chunk;

        if (vf->pos > vf->logical_size) vf->logical_size = vf->pos;
    }

    if (vf->logical_size > vf->known_disk_size) {
        if (ftruncate(vf->osfd, vf->logical_size) != 0) return -1;
        vf->known_disk_size = vf->logical_size;
    }

    return (ssize_t)done;
}

off_t vtpc_lseek(int fd, off_t offset, int whence) {
    VtpcFile *vf = get_file(fd);
    if (!vf) return (off_t)-1;

    off_t newpos;
    if (whence == SEEK_SET) {
        newpos = offset;
    } else if (whence == SEEK_CUR) {
        newpos = vf->pos + offset;
    } else if (whence == SEEK_END) {
        newpos = vf->logical_size + offset;
    } else {
        errno = EINVAL;
        return (off_t)-1;
    }

    if (newpos < 0) {
        errno = EINVAL;
        return (off_t)-1;
    }

    vf->pos = newpos;
    return vf->pos;
}

int vtpc_fsync(int fd) {
    VtpcFile *vf = get_file(fd);
    if (!vf) return -1;

    for (Page *p = g_cache.lru_head; p; p = p->next) {
        if (p->osfd == vf->osfd && p->dirty) {
            if (flush_page(p) != 0) return -1;
        }
    }

    if (ftruncate(vf->osfd, vf->logical_size) != 0) return -1;
    vf->known_disk_size = vf->logical_size;

    if (fsync(vf->osfd) != 0) return -1;
    return 0;
}
