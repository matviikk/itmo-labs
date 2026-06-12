CC      ?= cc
CFLAGS  ?= -std=gnu17 -Wall -Wextra -Wshadow -Wpointer-arith -Wcast-qual \
           -Wstrict-prototypes -Wmissing-prototypes -Wundef -g -O0
GNUFLAGS = -D_GNU_SOURCE

BIN_DIR = bin
SRC_DIR = src
# Тесты ожидают vtsh в ../build/bin относительно каталога lab/vtsh
TEST_BIN_DIR = ../build/bin

.PHONY: all clean test

BINS = \
	$(BIN_DIR)/mysh \
	$(BIN_DIR)/proc-vfork \
	$(BIN_DIR)/shell-and \
	$(BIN_DIR)/cpu-calc-crc \
	$(BIN_DIR)/ema-join-hash

# Опциональные таргеты добавляем только если есть исходники
ifneq ("$(wildcard $(SRC_DIR)/proc_fork.c)","")
  BINS += $(BIN_DIR)/proc-fork
endif

ifneq ("$(wildcard $(SRC_DIR)/proc_clone3.c)","")
  BINS += $(BIN_DIR)/proc-clone3
endif

ifneq ("$(wildcard $(SRC_DIR)/cpu_factorize.c)","")
  BINS += $(BIN_DIR)/cpu-factorize
endif

ifneq ("$(wildcard $(SRC_DIR)/cpu_calc_md5.c)","")
  BINS += $(BIN_DIR)/cpu-calc-md5
endif

ifneq ("$(wildcard $(SRC_DIR)/ema_join_nl.c)","")
  BINS += $(BIN_DIR)/ema-join-nl
endif

ifneq ("$(wildcard $(SRC_DIR)/ema_replace_str.c)","")
  BINS += $(BIN_DIR)/ema-replace-str
endif

all: $(BINS)

$(BIN_DIR):
	mkdir -p $(BIN_DIR)

$(BIN_DIR)/mysh: $(SRC_DIR)/mysh.c | $(BIN_DIR)
	$(CC) $(CFLAGS) $(SRC_DIR)/mysh.c -o $(BIN_DIR)/mysh

$(BIN_DIR)/proc-fork: $(SRC_DIR)/proc_fork.c | $(BIN_DIR)
	$(CC) $(CFLAGS) $(SRC_DIR)/proc_fork.c -o $(BIN_DIR)/proc-fork

$(BIN_DIR)/proc-vfork: $(SRC_DIR)/proc_vfork.c | $(BIN_DIR)
	$(CC) $(CFLAGS) $(SRC_DIR)/proc_vfork.c -o $(BIN_DIR)/proc-vfork

$(BIN_DIR)/shell-and: $(SRC_DIR)/shell_and.c | $(BIN_DIR)
	$(CC) $(CFLAGS) $(SRC_DIR)/shell_and.c -o $(BIN_DIR)/shell-and

$(BIN_DIR)/cpu-calc-md5: $(SRC_DIR)/cpu_calc_md5.c | $(BIN_DIR)
	$(CC) $(CFLAGS) $(SRC_DIR)/cpu_calc_md5.c -o $(BIN_DIR)/cpu-calc-md5

$(BIN_DIR)/cpu-calc-crc: $(SRC_DIR)/cpu_calc_crc.c | $(BIN_DIR)
	$(CC) $(CFLAGS) $(SRC_DIR)/cpu_calc_crc.c -o $(BIN_DIR)/cpu-calc-crc

$(BIN_DIR)/ema-join-nl: $(SRC_DIR)/ema_join_nl.c | $(BIN_DIR)
	$(CC) $(CFLAGS) $(SRC_DIR)/ema_join_nl.c -o $(BIN_DIR)/ema-join-nl

$(BIN_DIR)/ema-join-hash: $(SRC_DIR)/ema_join_hash.c | $(BIN_DIR)
	$(CC) $(CFLAGS) $(SRC_DIR)/ema_join_hash.c -o $(BIN_DIR)/ema-join-hash

$(BIN_DIR)/proc-clone3: $(SRC_DIR)/proc_clone3.c | $(BIN_DIR)
	$(CC) $(CFLAGS) $(GNUFLAGS) $(SRC_DIR)/proc_clone3.c -o $(BIN_DIR)/proc-clone3

$(BIN_DIR)/cpu-factorize: $(SRC_DIR)/cpu_factorize.c | $(BIN_DIR)
	$(CC) $(CFLAGS) $(SRC_DIR)/cpu_factorize.c -o $(BIN_DIR)/cpu-factorize -lm

$(BIN_DIR)/ema-replace-str: $(SRC_DIR)/ema_replace_str.c | $(BIN_DIR)
	$(CC) $(CFLAGS) $(GNUFLAGS) $(SRC_DIR)/ema_replace_str.c -o $(BIN_DIR)/ema-replace-str

test: all
	mkdir -p $(TEST_BIN_DIR)
	cp $(BIN_DIR)/mysh $(TEST_BIN_DIR)/vtsh
	python3 -m pytest -q test

clean:
	rm -rf $(BIN_DIR)/*.o \
	       $(BIN_DIR)/mysh \
	       $(BIN_DIR)/proc-fork \
	       $(BIN_DIR)/proc-vfork \
	       $(BIN_DIR)/proc-clone3 \
	       $(BIN_DIR)/cpu-calc-md5 \
	       $(BIN_DIR)/cpu-calc-crc \
	       $(BIN_DIR)/cpu-factorize \
	       $(BIN_DIR)/ema-join-nl \
	       $(BIN_DIR)/ema-join-hash \
	       $(BIN_DIR)/ema-replace-str
