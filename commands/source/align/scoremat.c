#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "matrix.h"

#ifdef DEBUG
main()
{
	printf("%s %s\n", filename,amino);
	make_matrix(filename, amino);
}
#endif
make_char2num(letters, char2num)
	char *letters;
	char *char2num;
{
	unsigned char c;
	int i;
	int defchar;

	/* DEFAULT_CHAR = 'X' */
	for (i = 0; letters[i] && letters[i] != DEFAULT_CHAR; i++) {
	}
	defchar = i;
	for (c = 0; c < MAXCHR; c++) {
		char2num[c] = defchar;
	}
	for (i = 0; letters[i]; i++) {
		char2num[ letters[i] ] = i;
	}
}
Score **create_matrix(int letnum)
{
	int i;
	Score **wm;
	if ((wm = (Score **) malloc(sizeof(Score*) * letnum)) == NULL) {
		fprintf(stderr, "can't alloc memory\n");
		exit(1);
	}
	for ( i = 0; i < letnum; i++) {
		if ((wm[i] = (Score *) malloc(sizeof(Score) * letnum)) == NULL) {
			fprintf(stderr, "can't alloc memory\n");
			exit(1);
		}
	}
	return wm;
}
Score **read_matrix(char *filename, char *letters, char *char2num)
{
	int letnum = strlen(letters);
	Score **wm = create_matrix(letnum);
	make_matrix(wm, filename, letters, char2num);
	return wm;
}
make_matrix(wm, filename, letters, char2num)
	Score **wm;
	char *filename;
	char *letters;
	char *char2num;
{
	char buf[BUFSIZ];
	char *p;
	int flag = 0;
	int i, j;
	unsigned char c;
	int letnum = strlen(letters);
	char chd[MAXCHR], chd2;
	FILE *fp;

	if (*filename == 0) {
		sprintf(filename, "%s/%s", MATDIR, MATFILE);
	}

	if ((fp = fopen(filename, "r")) == NULL) {
		char filename2[100];
		sprintf(filename2, "%s/%s",MATDIR,filename);
		if ((fp = fopen(filename2, "r")) == NULL) {
			fprintf(stderr,
				"Can't open matrix file %s\n", filename);
			exit(12);
		}
	}
	while ((p = fgets(buf, BUFSIZ, fp)) != NULL) {
		if (*buf == '#') continue;
		if (*buf == '\n') continue;
		if (! flag) {
			i = 0;
			while (*p != '\n') {
				if (isalpha(*p) && char2num[*p] < letnum) {
					chd[i++] = char2num[*p];
				}
				p++;
			}
			flag = 1;
			i = 0;
		} else {
			j = 0;
			p = strtok(p, " ");
			chd2 = char2num[*p];
			while (*p && *p != '\n') {
				if ((p = strtok(NULL, " \n")) == NULL)
					break;
#ifndef  MINUS
				wm[chd2][chd[j++]] = (Score) atoi(p);
#else
				wm[chd2][chd[j++]] = - (Score) atoi(p);
#endif
			}
			i++;
		}
	}
#ifdef DEBUG
	for (i = 0; i < letnum; i++) {
		for (j = 0; j < letnum; j++) {
			printf("%3d ", (int)wm[i][j]);
		}
		putchar('\n');
	}
#endif
	fclose(fp);
}
