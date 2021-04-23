#include <stdio.h>
#include <math.h>
#include "codon.h"

#define MAXSQLEN 500000
#define MAXTITLEN 500
#define CODSIZ 64
#define DICODSIZ 4096
#define FILNAME 150

char filename[FILNAME];
char wval_filename[FILNAME];
char amoutfile[FILNAME];
char buf[BUFSIZ];
char seq[MAXSQLEN];
int codn;
int SynCod[25];
double RSCU[65];
double W[65];

int Count[DICODSIZ+1];
int TotCount[DICODSIZ+1];
int supr_geneout;
int supr_usage;
int cal_rscu;
int cal_karlin;
int cal_w;
int cal_cai;
int amcount_out;
int tot_cnt;
int skip_end;
int verb_print;
int add_tit;
int minlen;
int dispInfo;
int codsiz = CODSIZ;
int codlen = 3;
int header;
char tit[MAXTITLEN];
FILE *amout;

main(argc, argv)
	int argc;
	char **argv;
{
	int i, j;
	FILE *fp, *fp2;
	char *p;
	int seqlen;

	getargs(argc, argv);
	if (cal_cai) {
		readW(wval_filename);
	}
	cal_syncod(CodonTrans[codn]);
	if (amcount_out) {
		if ((amout = fopen(amoutfile, "w")) == NULL) {
			fprintf(stderr, "Can't open %s\n",amoutfile);
			exit(1);
		}
	}
	if (! *filename) {
		fp = stdin;
	} else if ((fp = fopen(filename, "r")) == NULL) {
		fprintf(stderr, "Can't open file\n");
		exit(1);
	}
	if (header) {
		print_header();
	}
	while (fgets(buf, BUFSIZ, fp) != NULL) {
		if (buf[0] == '>') {
			if (*seq) {
				seq[i] = '\0';
				if ((seqlen = strlen(seq)/3) > minlen) {
					if ((header || dispInfo)
							&& ! supr_usage) {
						printf("%s", tit);
						if (dispInfo) {
							printf(" %d", seqlen);
						}
					}
					usage(seq);
				}
			}
			for (p = buf+1; *p && *p == ' '; p++)
				;
			for (j = 0; *p && *p != '\n' && *p != ' '; p++) {
				tit[j++] = *p;
			}
			tit[j] = '\0';
			i = 0;
		} else {
			for (p = buf; *p; p++) {
				if (isNUC(*p)) {
					seq[i++] = *p;
				}
			}
		}
	}
	if (*seq && (seqlen = strlen(seq)) > minlen) {
		if ((header || dispInfo) && ! supr_usage) {
			printf("%s", tit);
			if (dispInfo) {
				printf(" %d", seqlen);
			}
		}
		usage(seq);
	}
	if (cal_rscu && supr_usage) {
		/* option 'r' */
		calcRSCU(TotCount);
	} else if (cal_w) {
		calcW(TotCount);
	} else if (tot_cnt) {
		if (verb_print) {
			print_usage_verbose(TotCount);
		} else {
			print_usage(TotCount);
		}
	}
	close(fp);
}
getargs(argc, argv)
	int argc;
	char **argv;
{
	int i;
	int status = 0;

	for (i = 1; i < argc; i++) {
		if (*argv[i] == '-') {
			switch (*++argv[i]) {
			case 'n':
				codn = atoi(++argv[i]);
				break;
			case 'r':
				supr_usage = 1;
			case 'R':
				if (*++argv[i] == 'n') {
					/* do not normalize by
						# of synonymous codons */
					cal_rscu = 2;
				} else {
					cal_rscu = 1;
				}
				break;
			case 'B':
				cal_rscu = 2;
				amcount_out = 1;
				status = 2;
				break;
			case 'w':
				cal_w = 1;
				supr_usage = 1;
				break;
			case 'c':
				cal_cai = 1;
				supr_usage = 1;
				status = 1;
				break;
			case 'v':
				verb_print = 1;
				break;
			case 't':
				tot_cnt = 1;
				supr_usage = 1;
				break;
			case 'T':
				add_tit = 1;
				break;
			case 'l':
				minlen = atoi(++argv[i]);
				break;
			case 'i':
				dispInfo = 1;
				break;
			case 'h':
				header = 1;
				break;
			case 'E':
				skip_end = 1;
				break;
			case '2':
				codsiz = DICODSIZ;
				codlen = 6;
				if (cal_rscu) {
					fprintf(stderr,
					    "Can't calculate dicodon rscu\n");
					exit(1);
				}
				break;
			}
		} else {
			switch (status) {
			case 0:
				strcpy(filename, argv[i]);
				break;
			case 1:
				strcpy(wval_filename, argv[i]);
				status = 0;
				break;
			case 2:
				strcpy(amoutfile, argv[i]);
				status = 0;
				break;
			}
		}
	}
}

usage(seq)
	char *seq;
{
	char *p, c;
	int cod, flag;
	int i;
	int seqlen = strlen(seq);
	char str[4];

	i = cod = flag = 0;

	for (cod = 0; cod < codsiz+1; cod++) {
		Count[cod] = 0;
	}
	for (p = seq; *p; p++) {
		c = NID[*p];
		flag <<= 1;
		if (c == 4) {
			c = 0;
			flag |= 1;
		}
		flag &= 7;

		cod *= 4;
		cod += c;
		cod %= codsiz;

		if (++i >= codlen && i % 3 == 0) {
			if (skip_end && (i == 3 || i == seqlen)) {
				continue;
			}
			if (! flag) {
				Count[cod]++;
				TotCount[cod]++;
			} else {
				Count[codsiz]++;
				TotCount[codsiz]++;
			}
		}
	}
	if (cal_cai) {
		cai(Count);
	}
	if (! supr_usage) {
		if (cal_rscu) {
			calcRSCU(Count);
		} else if (verb_print) {
			print_usage_verbose(Count);
		} else {
			print_usage(Count);
		}
	}
}

cai(Count)
	int *Count;
{
	int i;
	int cod;
	char aa;
	double CAI;
	int L;

	L = 0; CAI = 0.0;
	for (cod = 0; cod < codsiz; cod++) {
		aa = AID[CodonTrans[codn][cod]];
		if (aa == '*' || SynCod[aa] <= 1) continue;
		if (Count[cod]) {
			CAI += Count[cod] * log(W[cod]);
			L += Count[cod];
		}
	}
	printf("%s %lf\n", tit, exp(CAI/(double)L));
}

calcRSCU(Count)
	int *Count;
{
	int cod;
	int AMCount[25];
	int i;
	double RSCU;
	char aa;
	for (i = 0; i < 24; i++) {
		AMCount[i] = 0;
	}

	for (cod = 0; cod < codsiz; cod++) {
		AMCount[AID[CodonTrans[codn][cod]]] += Count[cod];
	}
	for (cod = 0; cod < codsiz; cod++) {
		aa = AID[CodonTrans[codn][cod]];
		if (SynCod[aa]<=1) {
			/* M,W */
			continue;
		}
		if (skip_end && aa >= 20) {
			continue;
		}
		if (Count[cod]) {
			RSCU = (double) Count[cod] / (double) AMCount[aa];
			if (cal_rscu == 2) {
				RSCU *= (double) SynCod[aa];
			}
			printf(" %.4lf", RSCU);
		} else {
			printf(" %.4lf", 0.0);
		}
	}
	putchar('\n');
	if (amcount_out) {
		fprintf(amout, "%s", tit);
		for (aa = 0; aa < 24; aa++) {
			fprintf(amout, " %d", AMCount[aa]);
		}
		fprintf(amout, "\n");
	}
}

calcW(Count)
	int *Count;
{
	int i;
	int cod;
	char aa;
	int MaxAACount[25];

	for (i = 0; i < 24; i++) {
		MaxAACount[i] = 0;
	}
	for (cod = 0; cod < codsiz; cod++) {
		aa = AID[CodonTrans[codn][cod]];
		if (MaxAACount[aa] < Count[cod]) {
			MaxAACount[aa] = Count[cod];
		}
	}
	for (cod = 0; cod < codsiz; cod++) {
		aa = AID[CodonTrans[codn][cod]];
		if (Count[cod]) {
			W[cod] = (double) Count[cod] / MaxAACount[aa];
		} else {
			W[cod] = (double) 0.5 / MaxAACount[aa];
		}
		if (verb_print) {
			printf("%d %c%c%c %c %d %.4lf\n", cod,
				COD1(cod),COD2(cod),COD3(cod),
				CodonTrans[codn][cod], Count[cod], W[cod]);
		} else {
			printf(" %.4lf", (double) W[cod]);
		}
	}
	putchar('\n');
}

num2str(num,seq)
	int num;
	char *seq;
{
	int i;
	for (i = 0; i < codlen; i++) {
		seq[codlen-1-i] = NUC[num % 4];
		num /= 4;
	}
	seq[codlen] = '\0';
}
print_header()
{
	int cod, c, i;
	char seq[10];
	seq[codlen] = '\0';
	for (cod = 0; cod < codsiz; cod++) {
		if (skip_end && CodonTrans[codn][cod] == '*') {
			/* stop codons */
			continue;
		}
		if (cal_rscu && SynCod[AID[CodonTrans[codn][cod]]] <= 1) {
			continue;
		}
		for (c = cod, i = 0; i < codlen; c /= 4, i++) {
			seq[codlen-1-i] = NUC[c % 4];
		}
		printf(" %s", seq);
	}
	putchar('\n');
}
print_usage(Count)
	int *Count;
{
	int cod;

	if (add_tit) {
		printf("%s", tit);
	}
	for (cod = 0; cod < codsiz; cod++) {
		if (skip_end && CodonTrans[codn][cod] == '*') {
			/* stop codons */
			continue;
		}
		printf(" %d", Count[cod]);
	}
	putchar('\n');
}

print_usage_verbose(Count)
	int *Count;
{
	int cod, cod1, cod2;

	if (codlen == 6) {
	    for (cod = 0; cod < codsiz; cod++) {
		cod1 = cod / CODSIZ;
		cod2 = cod % CODSIZ;
		printf("%d %c%c%c%c%c%c %c%c %d\n",
			cod,COD1(cod1),COD2(cod1),COD3(cod1),
			COD1(cod2),COD2(cod2),COD3(cod2),
			CodonTrans[codn][cod1], CodonTrans[codn][cod2],
			Count[cod]);
	    }
	    printf("%d NNNNNN XX %d\n", codsiz, Count[codsiz]);
	} else {
	    for (cod = 0; cod < codsiz; cod++) {
		printf("%d %c%c%c %c %d\n", cod,COD1(cod),COD2(cod),COD3(cod),
				CodonTrans[codn][cod], Count[cod]);
	    }
	    printf("%d NNN X %d\n", codsiz, Count[codsiz]);
	}
}

cal_syncod(transtab)
	char *transtab;
{
	while (*transtab) {
		SynCod[AID[*transtab]]++;
		transtab++;
	}
}

readW(filename)
	char *filename;
{
	FILE *fp;
	char *p, *q;
	int i = 0;
	char buf[BUFSIZ];

	if ((fp = fopen(filename, "r")) == NULL) {
		fprintf(stderr, "Can't open W-value file\n");
		exit(1);
	}
	while (fgets(buf, BUFSIZ, fp) != NULL) {
		for ( p = buf; i < codsiz; i++, p = q) {
			if ((W[i] = strtod(p, &q)) == 0) {
				if (p == q) break;
			}
		}
	}
	fclose(fp);
	if (i < codsiz) {
		fprintf(stderr, "Too few rscu %d\n", i);
		exit(1);
	}
/* for DEBUG
	for (i = 0; i < codsiz; i++) {
		printf("%d %c%c%c %lf\n", i, COD1(i),COD2(i),COD3(i),W[i]);
	}
*/
}
