#SRCDIR = ..
SRCDIR = .
VPATH=$(SRCDIR)
LIBDIR = ../lib
MYLIB = $(LIBDIR)/mylib.a
INCLUDEDIR = $(HOME)/include
#CFLAGS = -g
CFLAGS = -O2 -g -DDOUBLESCORE
#CFLAGS = -g -DDOUBLESCORE

#CFLAGS = -xO2 -DDOUBLESCORE
#CFLAGS = -fast -DDOUBLESCORE
AMINODEF = WITH_X
CC = gcc
#CC = icc
MPICC = mpicc

TEST_DPOPT=-P0 -G -l -d -L

#.c.o:
#	$(COMPILE.c) -c $(SRCDIR)/$<

pdpc:	dp2.o seqdb.o scoremat.o create_pam.o mathmat.o pdpc.o hash.o
	$(CC) dp2.o seqdb.o scoremat.o create_pam.o mathmat.o pdpc.o hash.o -o $@ -lm -L$(PVM_ROOT)/lib/$(PVM_ARCH) -lpvm3 -lnsl 

dp2:	dp2.o seqdb.o scoremat.o create_pam.o mathmat.o dpmain.o hash.o \
	align.o alignlib.o
	$(CC) dp2.o seqdb.o scoremat.o create_pam.o mathmat.o dpmain.o \
		hash.o align.o alignlib.o -o $@ -lm

align:	align_main.o alignlib.o seqdb.o scoremat.o hash.o
	$(CC) align_main.o alignlib.o seqdb.o scoremat.o hash.o -o $@
align_main.o:	$(SRCDIR)/align.c
	$(CC) $(CFLAGS) $(SRCDIR)/align.c -c -o align_main.o -DDOUBLESCORE -DDEBUGMAIN

#dp2.o:	$(SRCDIR)/dp2.c $(SRCDIR)/dp.h
dp2.o:	$(SRCDIR)/dp2.c
	$(CC) -c $(CFLAGS) $(SRCDIR)/dp2.c

dpmain.o:	$(SRCDIR)/dpmain.c
	$(CC) -c $(CFLAGS) $(SRCDIR)/dpmain.c

pdpc.o:	$(SRCDIR)/dpmain.c
	$(CC) -c $(CFLAGS) $(SRCDIR)/dpmain.c -DUSE_PVM=1 -I$(PVM_ROOT)/include -o $@

mdp:	mdp.o dp2.o align.o seqdb.o scoremat.o create_pam.o mathmat.o \
		mdpmain.o hash.o alignlib.o
	$(MPICC) mdp.o dp2.o align.o seqdb.o scoremat.o create_pam.o \
		mathmat.o mdpmain.o hash.o alignlib.o -o $@ -lm
mdpmain.o:	$(SRCDIR)/dpmain.c
	$(MPICC) -c $(CFLAGS) -DUSE_MPI=1 $(SRCDIR)/dpmain.c -o $@
mdp.o:	$(SRCDIR)/mdp.c
	$(MPICC) -c $(CFLAGS) $(SRCDIR)/mdp.c

create_pam:	create_pam.main.o mathmat.o eigen.o
	$(CC) create_pam.main.o mathmat.o eigen.o -o $@ -lm

create_pam.main.o:	$(SRCDIR)/create_pam.c
	$(CC) -c -DMAIN $(CFLAGS) $(SRCDIR)/create_pam.c -o $@

create_pam.o:	$(SRCDIR)/create_pam.c
	$(CC) -c $(CFLAGS) $(SRCDIR)/create_pam.c

mathmat.o:	$(SRCDIR)/mathmat.c
	$(CC) -c $(CFLAGS) $(SRCDIR)/mathmat.c
	
scoremat.o:	$(SRCDIR)/scoremat.c
	$(CC) -c $(CFLAGS) $(SRCDIR)/scoremat.c

clean:
	-rm *.o dp2 dp3 align pdp mdp $(PVM_ARCH)/*.o

pdp:	$(PVM_ARCH)/pdp
	cp $(PVM_ARCH)/pdp $(HOME)/pvm3/bin/$(PVM_ARCH)
	echo Done

allpamout:	create_pam
	create_pam -c -j

$(PVM_ARCH)/pdp:	$(PVM_ARCH)/pdp.o $(PVM_ARCH)/seqdb.o $(PVM_ARCH)/hash.o
	$(CC) $(PVM_ARCH)/pdp.o -L/usr/local/pvm3/lib/$(PVM_ARCH) -lpvm3 \
		$(PVM_ARCH)/seqdb.o $(PVM_ARCH)/hash.o -g -o $@ 

$(PVM_ARCH)/pdp.o:	pdp.c
	$(CC) -I/usr/local/pvm3/include pdp.c -c $(CFLAGS) -o $@

$(PVM_ARCH)/seqdb.o:	seqdb.c
	$(CC) seqdb.c -c $(CFLAGS) -o $@

$(PVM_ARCH)/hash.o:	hash.c
	$(CC) hash.c -c $(CFLAGS) -o $@

testout:	dp2
	dp2 -N t/eco_eco.lis t/eco $(TEST_DPOPT) > t/testP.out
test:	dp2
	dp2 -N t/eco_eco.lis t/eco $(TEST_DPOPT) | diff - t/testP.out
tarfile:
	tar cvf dp.tar *.c *.h Makefile.* t
