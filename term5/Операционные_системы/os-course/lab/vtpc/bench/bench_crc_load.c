#define _GNU_SOURCE
#include <errno.h>
#include <fcntl.h>
#include <inttypes.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <time.h>
#include <unistd.h>

#include "vtpc.h"

static uint32_t crc_table[256];

static void best_effort_disable_os_cache(int osfd) {
    (void)osfd;
#if defined(__APPLE__) && defined(F_NOCACHE)
    (void)fcntl(osfd, F_NOCACHE, 1);
#endif
}

static void crc32_init(void) {
    uint32_t poly = 0xEDB88320u;
    for (uint32_t i = 0; i < 256; ++i) {
        uint32_t c = i;
        for (int j = 0; j < 8; ++j) {
            c = (c & 1) ? (poly ^ (c >> 1)) : (c >> 1);
        }
        crc_table[i] = c;
    }
}

static uint32_t crc32_update(uint32_t crc, const uint8_t *buf, size_t len) {
    crc = ~crc;
    for (size_t i = 0; i < len; ++i) {
        uint8_t idx = (uint8_t)((crc ^ buf[i]) & 0xFFu);
        crc = crc_table[idx] ^ (crc >> 8);
    }
    return ~crc;
}

static uint64_t rng_state = 88172645463325252ull;

static uint64_t xorshift64star(void) {
    uint64_t x = rng_state;
    x ^= x >> 12;
    x ^= x << 25;
    x ^= x >> 27;
    rng_state = x;
    return x * 2685821657736338717ull;
}

static double elapsed_sec(struct timespec a, struct timespec b) {
    long sec = b.tv_sec - a.tv_sec;
    long nsec = b.tv_nsec - a.tv_nsec;
    if (nsec < 0) { sec--; nsec += 1000000000L; }
    return (double)sec + (double)nsec / 1e9;
}

static void usage(const char *prog) {
    fprintf(stderr,
        "Usage:\n"
        "  %s --libc-direct <iterations> <file> [seed]\n"
        "  %s --vtpc       <iterations> <file> [seed]\n"
        "\n"
        "Does random 4K block reads and computes CRC32 of read bytes.\n"
        "Baseline uses O_DIRECT (or platform equivalent) to bypass OS page cache.\n"
        "VTPC mode uses your userspace LRU cache via vtpc_* API.\n",
        prog, prog);
}

int main(int argc, char **argv) {
    if (argc < 4) {
        usage(argv[0]);
        return 1;
    }

    int mode_vtpc = 0;
    if (strcmp(argv[1], "--vtpc") == 0) mode_vtpc = 1;
    else if (strcmp(argv[1], "--libc-direct") == 0) mode_vtpc = 0;
    else {
        usage(argv[0]);
        return 1;
    }

    errno = 0;
    char *end = NULL;
    long iters = strtol(argv[2], &end, 10);
    if (errno != 0 || end == argv[2] || iters <= 0) {
        fprintf(stderr, "Invalid iterations: %s\n", argv[2]);
        return 1;
    }

    const char *path = argv[3];
    if (argc >= 5) {
        errno = 0;
        uint64_t seed = strtoull(argv[4], &end, 10);
        if (errno == 0 && end != argv[4] && *end == '\0') rng_state = seed;
    }

    const size_t block = 4096; 
    void *buf = NULL;
    if (posix_memalign(&buf, block, block) != 0 || !buf) {
        perror("posix_memalign");
        return 1;
    }

    int fd = -1;
    int osfd = -1;

    if (mode_vtpc) {
        fd = vtpc_open(path, O_RDONLY, 0);
        if (fd < 0) {
            perror("vtpc_open");
            free(buf);
            return 1;
        }
        osfd = open(path, O_RDONLY);
        if (osfd < 0) {
            perror("open(for fstat)");
            vtpc_close(fd);
            free(buf);
            return 1;
        }
    } else {
#ifdef O_DIRECT
        osfd = open(path, O_RDONLY | O_DIRECT);
        if (osfd < 0) {
            perror("open(O_DIRECT)");
            fprintf(stderr, "Hint: file must be on filesystem supporting O_DIRECT.\n");
            free(buf);
            return 1;
        }
#else
        osfd = open(path, O_RDONLY);
        if (osfd < 0) {
            perror("open");
            free(buf);
            return 1;
        }
        best_effort_disable_os_cache(osfd);
#endif
    }

    struct stat st;
    if (fstat(osfd, &st) != 0) {
        perror("fstat");
        if (mode_vtpc) { close(osfd); vtpc_close(fd); }
        else { close(osfd); }
        free(buf);
        return 1;
    }

    off_t fsize = (off_t)st.st_size;
    if (fsize <= 0) {
        fprintf(stderr, "File is empty\n");
        if (mode_vtpc) { close(osfd); vtpc_close(fd); }
        else { close(osfd); }
        free(buf);
        return 1;
    }

    off_t npages = (fsize + (off_t)block - 1) / (off_t)block;
    if (npages <= 0) npages = 1;

    crc32_init();

    struct timespec t0, t1;
    clock_gettime(CLOCK_MONOTONIC, &t0);

    uint32_t crc = 0;

    for (long i = 0; i < iters; ++i) {
        off_t page = (off_t)(xorshift64star() % (uint64_t)npages);
        off_t off = page * (off_t)block;

        size_t want = block;
        if (off + (off_t)block > fsize) {
            off_t tail = fsize - off;
            if (tail < 0) tail = 0;
            want = (size_t)tail;
        }

        if (want == 0) continue;

        if (mode_vtpc) {
            if (vtpc_lseek(fd, off, SEEK_SET) == (off_t)-1) {
                perror("vtpc_lseek");
                close(osfd);
                vtpc_close(fd);
                free(buf);
                return 1;
            }
            ssize_t n = vtpc_read(fd, buf, block);
            if (n < 0) {
                perror("vtpc_read");
                close(osfd);
                vtpc_close(fd);
                free(buf);
                return 1;
            }
            if ((size_t)n < want) want = (size_t)n;
        } else {
            ssize_t n = pread(osfd, buf, block, off);
            if (n < 0) {
                perror("pread");
                close(osfd);
                free(buf);
                return 1;
            }
            if ((size_t)n < want) want = (size_t)n;
        }

        crc = crc32_update(crc, (const uint8_t *)buf, want);
    }

    (void)mode_vtpc;

    clock_gettime(CLOCK_MONOTONIC, &t1);
    double dt = elapsed_sec(t0, t1);

    printf("mode=%s iters=%ld file=%s size=%jd crc=%08x time=%.6f s\n",
           mode_vtpc ? "vtpc" : "libc-direct",
           iters, path, (intmax_t)fsize, crc, dt);

    if (mode_vtpc) { close(osfd); vtpc_close(fd); }
    else { close(osfd); }

    free(buf);
    return 0;
}
