#include <stdio.h>
#include <stdlib.h>

#define DEFAULT_CUTOFF 13
#define MAXSEQLEN 200000000

char seq[MAXSEQLEN];
char flag[MAXSEQLEN];
char filename[100];
char buf[BUFSIZ];
int w;
int matchscore = 1, mismatchscore = -3;
int scoreout = 0;
int allout = 0;
int cutoff = 0;
int dropcut = 0;
int scorecut;
int maxscore, minscore;
int beginpos;
int matchlen = 1;
int minrep = 1;
int minoutrep = 2;
int maxrep = 10;
int delsimple = 0;
int cutrep;
double cutratio = 1.0;

main(int argc, char **argv)
{
	int i, j;
	char *p = buf, *q;
	FILE *fp;
	int score;
	int beginpos = -1, maxpos = 0;
	int match;

	getargs(argc, argv);
	if (*filename) {
		if ((fp = fopen(filename, "r")) == NULL) {
			fprintf(stderr, "Can't open file\n");
			exit(1);
		}
	} else {
		fp = stdin;
	}
	q = seq;
	while (fgets(buf, BUFSIZ, fp) != NULL) {
		if (buf[0] == '>') {
			continue;
		}
		for (p = buf; *p; p++) {
			if (isalpha(*p)) {
				*q++ = *p;
			}
		}
	}
	*p = '\0';
	fclose(fp);
	score = 0;
	if (! cutoff && ! cutratio) {
		cutoff = DEFAULT_CUTOFF;
	}
	if (! dropcut) {
		dropcut = cutoff;
	}
	for (w = minrep; w <= maxrep; w++) {
		i = 0;
		if (cutrep) {
			scorecut = (int) (
			    ((double)(matchscore - mismatchscore) * cutratio
				+ mismatchscore )
				* (cutrep - 1) * w - (matchlen - 1) );
			if (scorecut < cutoff) {
				scorecut = cutoff;
			}
		} else {
			scorecut = cutoff;
		}
		maxscore = 0; score = 0; beginpos = maxpos = i;
		for (p = seq; *p; p++) {
/*
			if (! allout && flag[i]) {
				beginpos = maxpos = i;
				maxscore = score = 0;
				i++;
				continue;
			}
*/
			if (! allout && delsimple) {
				int j, flag; 
				flag = 1;
				for (j = 1; j < matchlen; j++) {
					if (p[j-1] != p[j]) {
						flag = 0; break;
					}
				}
				if (flag) {
					i++;
					continue;
				}
			}
			match = string_match(p, p+w, matchlen);
			if (match == 1) {
				/* match */
				score+=matchscore;
			} else if (match == 0) {
				/* mismatch */
				score+=mismatchscore;
			} else {
				/* contain 'n' */
			}
			if (scoreout) {
				if (score < 0) score = 0;
				printf("%d %d\n", w, score);
				continue;
			}
			if (score > maxscore) {
				maxscore = score;
				maxpos = i;
			}
			if (score < 0 || maxscore - score > dropcut
					|| (! allout && flag[i+1])) {
				if (scorecut <= maxscore) {
					if (w >= minoutrep) {
						printf("%d %d %d %d ",
						/** beginning from 1 **/
						  beginpos+1+1,maxpos+w+1,
						  w, maxscore);
						for (j = beginpos+1;
					  		j <= maxpos + w; j++) {
/*
					  j <= maxpos + (matchlen-1) + w; j++) {
*/
							putchar(seq[j]);
						}
						putchar('\n');
					}
					for (j = beginpos+1;
						j <= maxpos + (matchlen-1) + w;
							j++) {
						flag[j] = 1;
					}
				}
				maxscore = 0;
				score = 0;
				beginpos = maxpos = i;
			}
			i++;
		}
	}
	exit(0);
}
getargs(int argc, char **argv)
{
	int i;
	for (i = 1; i < argc; i++) {
		if (*argv[i] == '-') {
			switch (*++argv[i]) {
			case 'c':
				cutoff = atoi(++argv[i]);
				break;
			case 'x':
				dropcut = atoi(++argv[i]);
				break;
			case 'm':
				minoutrep = atoi(++argv[i]);
				break;
			case 'M':
				maxrep = atoi(++argv[i]);
				break;
			case 'S':
				matchscore = atoi(++argv[i]);
				break;
			case 'P':
				mismatchscore = atoi(++argv[i]);
				if (mismatchscore > 0) {
					mismatchscore *= -1;
				}
				break;
			case 'd':
				/* eliminate single nucleotide repeat patterns;
					for compatibility */
				delsimple = 1;
				break;
			case 'r':
				/* cutoff for identity ratio */
				cutratio = atof(++argv[i]);
				break;
			case 'R':
				/* cutoff for a number of repeat times */
				cutrep = atoi(++argv[i]);
				break;
			case 'l':
				/* unit length for comparison */
				matchlen = atoi(++argv[i]);
				break;
			case 's':
				scoreout = 1;
			case 'a':
				allout = 1;
				break;
			}
		} else {
			strcpy(filename, argv[i]);
		}
	}
}
string_match(char *p, char *q, int len)
{
	while (len > 0 && *p && *q) {
		if (*p != *q) {
			return 0;
		} else if (*p == 'n' || *p == 'N' || *p == 'x' || *p == 'X') {
			/* masked */
			return -1;
		}
		p++; q++; len--;
	}
	return 1;
}
