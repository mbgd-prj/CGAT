#ifndef ALPHA_H
#define MAXCHR 127
#define alp2num(alp,chr) ((alp)->idx[chr])
#define num2alp(alp,num) ((alp)->chars[num])

typedef struct Alphabet {
	char *chars;
	int num;
	char idx[MAXCHR];
	char **class;
	double *freq;
	char *comple;	 /** for DNA only **/
	char compleNuc[MAXCHR]; /** for DNA only **/
	int origfrom, origto;
} Alphabet;

Alphabet *create_alphabet();

Alphabet *create_amino(), *create_dna(), *create_dna2(), *create_alpha();

#define ALPHA_H
#endif
