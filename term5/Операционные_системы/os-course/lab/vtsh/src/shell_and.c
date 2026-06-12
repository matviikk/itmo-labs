#define _GNU_SOURCE
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>

static int run_command(const char *cmd) {
    pid_t pid = fork();
    if (pid < 0) {
        perror("fork");
        return 1;
    }
    if (pid == 0) {
        execlp("sh", "sh", "-c", cmd, (char *)NULL);
        perror("execlp");
        _exit(127);
    }

    int status;
    if (waitpid(pid, &status, 0) < 0) {
        perror("waitpid");
        return 1;
    }
    if (WIFEXITED(status)) {
        return WEXITSTATUS(status);
    } else if (WIFSIGNALED(status)) {
        return 128 + WTERMSIG(status);
    } else {
        return 1;
    }
}

int main(int argc, char *argv[]) {
    if (argc != 3) {
        fprintf(stderr, "Usage: %s \"cmd1\" \"cmd2\"\n", argv[0]);
        fprintf(stderr, "Runs cmd1 && cmd2 semantics.\n");
        return 1;
    }

    int st1 = run_command(argv[1]);
    if (st1 == 0) {
        int st2 = run_command(argv[2]);
        return st2;
    }

    return st1;
}
