#define _GNU_SOURCE
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <time.h>
#include <unistd.h>

static double elapsed_sec(const struct timespec *a, const struct timespec *b) {
    long sec  = b->tv_sec  - a->tv_sec;
    long nsec = b->tv_nsec - a->tv_nsec;
    if (nsec < 0) {
        sec--;
        nsec += 1000000000L;
    }
    return (double)sec + (double)nsec / 1e9;
}

int main(int argc, char *argv[]) {
    if (argc < 2) {
        fprintf(stderr, "Usage: %s <program> [args...]\n", argv[0]);
        return 1;
    }

    struct timespec t0, t1;
    if (clock_gettime(CLOCK_MONOTONIC, &t0) != 0) {
        perror("clock_gettime");
        return 1;
    }

    pid_t pid = vfork();
    if (pid < 0) {
        perror("vfork");
        return 1;
    }

    if (pid == 0) {
  
        execvp(argv[1], &argv[1]);
        perror("execvp");
        _exit(127);  
    }

    int status;
    if (waitpid(pid, &status, 0) < 0) {
        perror("waitpid");
        return 1;
    }

    if (clock_gettime(CLOCK_MONOTONIC, &t1) != 0) {
        perror("clock_gettime");
        return 1;
    }

    double dt = elapsed_sec(&t0, &t1);

    if (WIFEXITED(status)) {
        int code = WEXITSTATUS(status);
        printf("child %d exited with code %d, time = %.6f s\n", pid, code, dt);
        return code;
    } else if (WIFSIGNALED(status)) {
        int sig = WTERMSIG(status);
        printf("child %d killed by signal %d, time = %.6f s\n", pid, sig, dt);
        return 128 + sig;
    } else {
        printf("child %d finished with unknown status, time = %.6f s\n", pid, dt);
        return 1;
    }
}
