#pragma once

#include <stddef.h>
#include <sys/types.h>
#include <unistd.h>

#ifdef __cplusplus
extern "C" {
#endif

int vtpc_open(const char *path, int flags, int mode);
int vtpc_close(int fd);

ssize_t vtpc_read(int fd, void *buf, size_t count);
ssize_t vtpc_write(int fd, const void *buf, size_t count);

off_t vtpc_lseek(int fd, off_t offset, int whence);

int vtpc_fsync(int fd);

#ifdef __cplusplus
}
#endif
