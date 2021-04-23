#include <stdio.h>
#include <string.h>
#include "alphabet.h"

Alphabet *create_alphabet(string)
	char *string;
{
	Alphabet *alphabet;
	if ((alphabet = (Alphabet *) malloc(sizeof(Alphabet))) == NULL)
		return NULL;
	if (alphabet_set_string(alphabet, string) < 0) {
		return NULL;
	}
	alphabet->class = NULL;
	return alphabet;
}

alphabet_free_alphabet(alphabet)
	Alphabet *alphabet;
{
	if (alphabet->chars != NULL)
		free(alphabet->chars);
	free(alphabet);
	return 0;
}

alphabet_set_string(alphabet, string)
	Alphabet *alphabet;	
	char *string;
{
	alphabet->num = strlen(string);
	if (alphabet->num) {
		if ((alphabet->chars = (char *) malloc(alphabet->num)) == NULL) {
			return -1;
		}
		strcpy(alphabet->chars, string);
	}
	alphabet_create_idx(alphabet);
	return 0;
}

alphabet_create_idx(alphabet)
Alphabet *alphabet;
{
	unsigned char c;
	int i;

	for (c = 0; c < MAXCHR; c++) {
		for (i = 0; alphabet->chars[i] && alphabet->chars[i] != c; i++)
			;
		alphabet->idx[c] = i;
	}
}

alphabet_set_class(alphabet,class)
Alphabet *alphabet;
char **class;
{
	int i;

	alphabet->class = class;
}

alphabet_set_freq(alp, freq)
Alphabet *alp;
 double *freq;
{
	int i, j, clsnum, am;
	if (alp->class) {
		for (i = 0; i < alp->num; i++) {
			if ((clsnum = strlen(alp->class[i])) > 1) {
				for (j = 0; j < clsnum; j++) {
					am = alp2num(alp,alp->class[i][j]);
					if (am > alp->num) {
						fprintf(stderr, "Illegal class\n");
						exit(1);
					}
					freq[i] += freq[am];
				}
			}
		}
	}
	alp->freq = freq;
}

alp_get_class(alp,c)
	Alphabet *alp;
	char c;
{
	static char *p;
	if (c) {
		p = alp->class[c];
	}
	if (*p == '\0') return -1;
	p++;
	return alp->idx[*(p-1)];
}
