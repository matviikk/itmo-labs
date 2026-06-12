#define _GNU_SOURCE
#include <errno.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

static uint32_t crc_table[256];

static void crc32_init(void) {
    uint32_t poly = 0xEDB88320u;
    for (uint32_t i = 0; i < 256; ++i) {
        uint32_t c = i;
        for (int j = 0; j < 8; ++j) {
            if (c & 1)
                c = poly ^ (c >> 1);
            else
                c >>= 1;
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

static uint8_t *read_file(const char *path, size_t *out_len) {
    FILE *f = fopen(path, "rb");
    if (!f) {
        perror("fopen");
        return NULL;
    }
    if (fseek(f, 0, SEEK_END) != 0) {
        perror("fseek");
        fclose(f);
        return NULL;
    }
    long sz = ftell(f);
    if (sz < 0) {
        perror("ftell");
        fclose(f);
        return NULL;
    }
    rewind(f);

    uint8_t *buf = malloc((size_t)sz);
    if (!buf) {
        perror("malloc");
        fclose(f);
        return NULL;
    }
    size_t n = fread(buf, 1, (size_t)sz, f);
    if (n != (size_t)sz) {
        perror("fread");
        free(buf);
        fclose(f);
        return NULL;
    }
    fclose(f);
    *out_len = (size_t)sz;
    return buf;
}

int main(int argc, char *argv[]) {
    if (argc != 3) {
        fprintf(stderr, "Usage: %s <iterations> <file>\n", argv[0]);
        return 1;
    }

    char *end = NULL;
    long iters = strtol(argv[1], &end, 10);
    if (end == argv[1] || iters <= 0) {
        fprintf(stderr, "Invalid iterations: %s\n", argv[1]);
        return 1;
    }

    size_t len = 0;
    uint8_t *buf = read_file(argv[2], &len);
    if (!buf) {
        return 1;
    }
    if (len == 0) {
        fprintf(stderr, "File is empty\n");
        free(buf);
        return 1;
    }

    crc32_init();
    srand((unsigned)time(NULL));

    uint32_t crc = 0;
    for (long i = 0; i < iters; ++i) {
        size_t start = (size_t)(rand() % (int)len);
        size_t max_len = len - start;
        size_t sub_len = 1 + (size_t)(rand() % (int)max_len);
        crc = crc32_update(crc, buf + start, sub_len);
    }

    printf("%08x\n", crc);
    free(buf);
    return 0;
}
