CFLAGS = -O
#PROGS = usage kmatch rep align/align
PROGS = usage kmatch rep 
BINDIR = ../binary

all:	$(PROGS)

install: all $(BINDIR)
	cp $(PROGS) $(BINDIR)

$(BINDIR):
	mkdir -p $(BINDIR)

rep:	rep.o
	cc $(CFLAGS) rep.o -lm -o $@

usage:	usage.o
	cc $(CFLAGS) usage.o -lm -o $@

align/align:
	cd align; make align;

kmatch_main.o:	kmatch.c
	cc $(CFLAGS) -c kmatch.c -o $@ -DKMATCH_MAIN

kmatch:	kmatch_main.o alphabet.o aminodna.o getseq.o segments.o
	cc $(CFLAGS) kmatch_main.o alphabet.o aminodna.o getseq.o segments.o -lm -o $@

clean:
	-rm $(PROGS) *.o
	cd align; make clean
