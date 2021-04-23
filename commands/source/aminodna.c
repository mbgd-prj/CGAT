#include "alphabet.h"
#include <stdio.h>

#define AMINO "-ARNDCQEGHILKMFPSTWYVBZX*"
#define DNA "-ATGCSWRYKMBVHDN"
#define DNA2 "-ATGCN"
#define AMINONUM 25
#define NUCNUM 17
#define NUCNUM2 6

#define ALPHABET "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
#define ALPNUM 26

char *aminoclass[AMINONUM] = {
	"", "A", "R", "N", "D", "C", "Q", "E", "G", "H", "I",
	    "L", "K", "M", "F", "P", "S", "T", "W", "Y", "V",
	    "NE", "QE", "ARNDCQEGHILKMFPSTWYV", "*"	/* BZX* */
};

double aminofreq[AMINONUM] = {
	0.00000, 0.07600-.00003, 0.05237, 0.04477, 0.05288, 0.01784, 0.04030,
	0.06274, 0.06971, 0.02254, 0.05590, 0.09221-.00003, 0.05836, 0.02360,
	0.04009, 0.05022, 0.07157-.00003, 0.05828, 0.01294, 0.03221, 0.06524,
	0.00003, 0.00003, 0.00026, 0.00000
};

char *dnaclass[NUCNUM] = {
	"", "A", "T", "G", "C",			/*ATGC*/
	    "GC", "AT", "AG", "TC",		/*SWRY*/
	    "AC", "TG",				/*KM*/
	    "TGC", "AGC", "ATC", "ATG",		/*BVHD*/
	    "ATGC"				/*N*/
};

double dnafreq[NUCNUM] = {
	0.000, 0.250, 0.250, 0.250, 0.250,
	0.000, 0.000, 0.000, 0.000,
	0.000, 0.000, 0.000, 0.000,
	0.000, 0.000, 0.000, 0.000,
};

char *dnaclass2[NUCNUM2] = {
	"", "A", "T", "G", "C","ACGT"			/*ATGC*/
};
double dnafreq2[NUCNUM2] = {
	0.000, 0.250, 0.250, 0.250, 0.250
};
char dna_comple[NUCNUM2] = {
	0, 2, 1, 4, 3, 5		/* complement */
};

Alphabet *create_amino()
{
	Alphabet *amino;
	amino = create_alphabet(AMINO);
	alphabet_set_class(amino, aminoclass);
	alphabet_set_freq(amino, aminofreq);
	amino->origfrom = 1;
	amino->origto = 20;
	amino->comple = NULL;
	return amino;
}

Alphabet *create_dna()
{
	Alphabet *dna;
	dna = create_alphabet(DNA);
	alphabet_set_class(dna, dnaclass);
	alphabet_set_freq(dna, dnafreq);
	dna->origfrom = 1;
	dna->origto = 4;
	return dna;
}

Alphabet *create_dna2()
{
	Alphabet *dna;
	int i;

	dna = create_alphabet(DNA2);
	alphabet_set_class(dna, dnaclass2);
	alphabet_set_freq(dna, dnafreq2);
	dna->origfrom = 1;
	dna->origto = 4;
	dna->comple = dna_comple;
	for (i = 0; i < dna->num; i++) {
		dna->compleNuc[dna->chars[i]] = dna->chars[dna->comple[i]];
	}
	return dna;
}

Alphabet *create_alpha()
{
	Alphabet *alpha = create_alphabet(ALPHABET);
	alpha->origfrom = 1;
	alpha->origto = ALPNUM;
	alpha->comple = NULL;
	return alpha;
}

revdna2(char *seq, int len, char *rev)
{
	int i;
	for (i = 0; i < len/2; i++) {
		rev[len-1-i] = seq[i];
	}
	rev[len] = 0;
}
