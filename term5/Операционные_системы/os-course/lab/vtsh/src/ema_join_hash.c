#define _GNU_SOURCE
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>


typedef long key_t;

typedef struct HashNode {
    key_t key;
    char  value[9];          
    struct HashNode *next;
} HashNode;

typedef struct {
    HashNode **buckets;
    size_t nbuckets;
} HashTable;

static unsigned long hash_key(key_t k) {
    unsigned long x = (unsigned long)k;
    x = (x ^ (x >> 33)) * 0xff51afd7ed558ccdULL;
    x = (x ^ (x >> 33)) * 0xc4ceb9fe1a85ec53ULL;
    x = x ^ (x >> 33);
    return x;
}

static HashTable *ht_create(size_t nbuckets) {
    HashTable *ht = calloc(1, sizeof(*ht));
    if (!ht) return NULL;
    ht->buckets = calloc(nbuckets, sizeof(HashNode *));
    if (!ht->buckets) {
        free(ht);
        return NULL;
    }
    ht->nbuckets = nbuckets;
    return ht;
}

static void ht_insert(HashTable *ht, key_t key, const char *value) {
    unsigned long h = hash_key(key);
    size_t idx = h % ht->nbuckets;
    HashNode *node = malloc(sizeof(*node));
    if (!node) return;
    node->key = key;
    strncpy(node->value, value, 8);
    node->value[8] = '\0';
    node->next = ht->buckets[idx];
    ht->buckets[idx] = node;
}

static HashNode *ht_find_bucket(HashTable *ht, key_t key) {
    unsigned long h = hash_key(key);
    size_t idx = h % ht->nbuckets;
    return ht->buckets[idx];
}

static void ht_free(HashTable *ht) {
    if (!ht) return;
    for (size_t i = 0; i < ht->nbuckets; ++i) {
        HashNode *n = ht->buckets[i];
        while (n) {
            HashNode *next = n->next;
            free(n);
            n = next;
        }
    }
    free(ht->buckets);
    free(ht);
}


typedef struct {
    key_t key;
    char left[9];
    char right[9];
} JoinRow;

typedef struct {
    JoinRow *rows;
    size_t size;
    size_t cap;
} JoinVec;

static void jvec_push(JoinVec *v, key_t key,
                      const char *l, const char *r) {
    if (v->size == v->cap) {
        size_t ncap = v->cap ? v->cap * 2 : 16;
        JoinRow *nr = realloc(v->rows, ncap * sizeof(*nr));
        if (!nr) return;
        v->rows = nr;
        v->cap = ncap;
    }
    v->rows[v->size].key = key;
    strncpy(v->rows[v->size].left, l, 8);
    v->rows[v->size].left[8] = '\0';
    strncpy(v->rows[v->size].right, r, 8);
    v->rows[v->size].right[8] = '\0';
    v->size++;
}


static int read_table_and_build_hash(const char *path, HashTable **out_ht) {
    FILE *f = fopen(path, "r");
    if (!f) {
        perror("fopen");
        return -1;
    }

    long n;
    if (fscanf(f, "%ld", &n) != 1 || n < 0) {
        fprintf(stderr, "Invalid first line in %s\n", path);
        fclose(f);
        return -1;
    }

    size_t nbuckets = 1;
    while (nbuckets < (size_t)n * 2) nbuckets <<= 1;
    if (nbuckets < 16) nbuckets = 16;

    HashTable *ht = ht_create(nbuckets);
    if (!ht) {
        perror("ht_create");
        fclose(f);
        return -1;
    }

    for (long i = 0; i < n; ++i) {
        key_t id;
        char word[64];
        if (fscanf(f, "%ld %63s", &id, word) != 2) {
            fprintf(stderr, "Invalid data in %s at line %ld\n", path, i + 2);
            ht_free(ht);
            fclose(f);
            return -1;
        }
        ht_insert(ht, id, word);
    }

    fclose(f);
    *out_ht = ht;
    return 0;
}

static int read_second_and_join(const char *path,
                                HashTable *ht,
                                JoinVec *out) {
    FILE *f = fopen(path, "r");
    if (!f) {
        perror("fopen");
        return -1;
    }

    long n;
    if (fscanf(f, "%ld", &n) != 1 || n < 0) {
        fprintf(stderr, "Invalid first line in %s\n", path);
        fclose(f);
        return -1;
    }

    for (long i = 0; i < n; ++i) {
        key_t id;
        char word[64];
        if (fscanf(f, "%ld %63s", &id, word) != 2) {
            fprintf(stderr, "Invalid data in %s at line %ld\n", path, i + 2);
            fclose(f);
            return -1;
        }

        HashNode *node = ht_find_bucket(ht, id);
        for (; node; node = node->next) {
            if (node->key == id) {
                jvec_push(out, id, node->value, word);
            }
        }
    }

    fclose(f);
    return 0;
}

int main(int argc, char *argv[]) {
    if (argc != 4) {
        fprintf(stderr, "Usage: %s <left_table> <right_table> <out_file>\n",
                argv[0]);
        return 1;
    }

    HashTable *ht = NULL;
    if (read_table_and_build_hash(argv[1], &ht) != 0) {
        return 1;
    }

    JoinVec jv = {0};
    if (read_second_and_join(argv[2], ht, &jv) != 0) {
        ht_free(ht);
        free(jv.rows);
        return 1;
    }

    ht_free(ht);

    FILE *out = fopen(argv[3], "w");
    if (!out) {
        perror("fopen(out)");
        free(jv.rows);
        return 1;
    }

    fprintf(out, "%zu\n", jv.size);
    for (size_t i = 0; i < jv.size; ++i) {
        fprintf(out, "%ld %s %s\n",
                jv.rows[i].key,
                jv.rows[i].left,
                jv.rows[i].right);
    }

    fclose(out);
    free(jv.rows);
    return 0;
}
