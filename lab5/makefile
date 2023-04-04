CC=gcc
CFLAGS=-Wall

all: matmuls

matmuls:
	$(CC) $(CFLAGS) -c matmuls.c
	$(CC) $(CFLAGS) matmuls.c -lgsl -lgslcblas -lm -o matmuls

clean:
	rm *a *o matmuls
