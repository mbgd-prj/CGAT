#include <stdio.h>
#include <math.h>
#include "mathmat.h"

/*
#define DEBUG
*/
#ifdef DEBUG
main(int argc, char **argv)
{
	MathMat *mat1 = create_MathMat(10,10);
	MathMat *mat2 = create_MathMat(10,10);
	MathMat *mat3;
	int i;
	int j;
	for (i = 0; i <10; i++) {
		for (j = 0; j <10; j++) {
			set_MathMat(mat1, i, j, (double) i+j);
			set_MathMat(mat2, i, j, (double) i*j);
		}
	}
	mat3 = MathMat_multiply(mat1,mat2);
/*
	printf("%lf\n", get_MathMat(mat3, 1, 2));
*/
	printf_MathMat(mat3);
	free_MathMat(mat1);
	free_MathMat(mat2);
	free_MathMat(mat3);
}
#endif
MathMat *create_MathMat(int row, int col)
{
	int i;
	MathMat *mat;

	if ((mat = (MathMat *) malloc(sizeof(MathMat))) == NULL) {
		fprintf(stderr, "Can't alloc memory\n");
		exit(1);
	}
	mat->row = row;
	mat->col = col;
	if ((mat->matrix = (double **) malloc(sizeof(double *) * row)) == NULL) {
		fprintf(stderr, "Can't alloc memory\n");
		exit(1);
	}
	for (i = 0; i < row; i++) {
		if ((mat->matrix[i] = (double *) malloc(sizeof(double) * col)) == NULL) {
			fprintf(stderr, "Can't alloc memory\n");
			exit(1);
		}
	}
	return mat;
}
MathMat *create_MathMat_init(double initval, int row, int col)
{
	int i, j;
	MathMat *mat = create_MathMat(row,col);
	for (i = 0; i < row; i++) {
		for (j = 0; j < col; j++) {
			mat->matrix[i][j] = initval;
		}
	}
	return mat;
}
MathMat *create_MathMat_diag(double initval, int rowcol)
{
	int i, j;
	MathMat *mat = create_MathMat(rowcol,rowcol);
	for (i = 0; i < rowcol; i++) {
		mat->matrix[i][i] = initval;
	}
	return mat;
}
MathMat *create_MathMat_diagvect(double *initval, int rowcol)
{
	int i, j;
	MathMat *mat = create_MathMat(rowcol,rowcol);
	for (i = 0; i < rowcol; i++) {
		mat->matrix[i][i] = initval[i];
	}
	return mat;
}
MathMat *copy_MathMat(MathMat *mat)
{
	int i, j;
	MathMat *newmat = create_MathMat(mat->row, mat->col);
	for (i = 0; i < mat->row; i++) {
		for (j = 0; j < mat->row; j++) {
			newmat->matrix[i][j] = mat->matrix[i][j];
		}
	}
	return newmat;
}
MathMat *submat_MathMat(MathMat *mat, int rowinit, int colinit,
		int rowsiz, int colsiz)
{
	MathMat *submat;

	if (rowsiz > mat->row || colsiz > mat->col || rowinit >= rowsiz || colinit >= colsiz) {
		return NULL;
	}
	if ((submat = (MathMat *) malloc(sizeof(MathMat))) == NULL) {
		fprintf(stderr, "Can't alloc memory\n");
		exit(1);
	}
	submat->row = rowsiz;
	submat->col = colsiz;
	if (rowinit == 0 && colinit == 0) {
		submat->matrix = mat->matrix;
	} else {
		int i;
		double **tmpmat;

		if ((tmpmat = (double **) malloc(sizeof(double *) * rowsiz)) == NULL) {
			free(submat);
			return NULL;
		}
		for (i = 0; i < rowsiz; i++) {
			tmpmat[i] = &(mat->matrix[i][colinit]);
		}
		submat->matrix = tmpmat;
	}
	return submat;
}
MathMat *transpose_MathMat(MathMat *mat)
{
	int i, j;
	MathMat *tmat = create_MathMat(mat->col,mat->row);
	for (i = 0; i < mat->row; i++) {
		for (j = 0; j < mat->col; j++) {
			tmat->matrix[j][i] = mat->matrix[i][j];
		}
	}
	return tmat;
}
/*
set_MathMat(mathmat, i, j, value)
	MathMat *mathmat;
	int i, j;
	double value;
{
	mathmat->matrix[i][j] = value;
}
*/

free_MathMat(MathMat *mathmat)
{
	int i;
	for (i = 0; i < mathmat->row; i++) {
		free(mathmat->matrix[i]);
	}
	free(mathmat->matrix);
	free(mathmat);
}
free_subMat(MathMat *submat)
{
	free(submat);
}
printf_MathMat(MathMat *mathmat,char *fmt)
{
	int i, j;
	for (i = 0; i < mathmat->row; i++) {
		for (j = 0; j < mathmat->col; j++) {
			if (j > 0) putchar(' ');
			printf(fmt, mathmat->matrix[i][j]);
		}
		putchar('\n');
	}
}
printd_MathMat(MathMat *mathmat, char *fmt)
{
	int i, j;
	for (i = 0; i < mathmat->row; i++) {
		for (j = 0; j < mathmat->col; j++) {
			if (j > 0) putchar(' ');
			printf(fmt, (int) rint(mathmat->matrix[i][j]));
		}
		putchar('\n');
	}
}

MathMat *MathMat_multiply(MathMat *mat1, MathMat *mat2)
{
	int i, j, k;
	MathMat *mat3;
	if (mat1->col != mat2->row) {
		return NULL;
	}
	mat3 = create_MathMat(mat1->row, mat2->col);
	for (i = 0; i < mat1->row; i++) {
		for (j = 0; j < mat2->col; j++) {
			mat3->matrix[i][j] = 0;
			for (k = 0; k < mat1->col; k++) {
				mat3->matrix[i][j] += mat1->matrix[i][k] * mat2->matrix[k][j];
			}
		}
	}
	mat3->row = mat1->row;
	mat3->col = mat2->col;
	return mat3;
}
apply_all_MathMat(MathMat *mat, void (*func)()) 
{
	int i, j;
	for (i = 0; i < mat->row; i++) {
		for (j = 0; j < mat->col; j++) {
			func(&(mat->matrix[i][j]));
		}
	}
}

MathVect *create_MathVect(int size)
{
	MathVect *vect;
	if ((vect = (MathVect *) malloc(sizeof(MathVect))) == NULL) {
		fprintf(stderr, "Can't alloc memory\n");
		exit(1);
	}
	vect->size = size;
	if ((vect->vect = (double *) malloc(sizeof(double) * size)) == NULL) {
		fprintf(stderr, "Can't alloc memory\n");
		exit(1);
	}
	return vect;
}

MathVect *create_MathVect_init(double initval, int size)
{
	MathVect *vect = create_MathVect(size);
	int i;

	for (i = 0; i < size; i++) {
		vect->vect[i] = initval;
	}
	return vect;
}
free_MathVect(MathVect *vect)
{
	free(vect->vect);
	free(vect);
}
double sum_MathVect(MathVect *vect)
{
	int i;
	double sum = 0.0;
	for (i = 0; i < vect->size; i++) {
		sum += vect->vect[i];
	}
	return sum;
}
div_MathVect(MathVect *vect, double val)
{
	int i;
	double sum = 0.0;
	for (i = 0; i < vect->size; i++) {
		vect->vect[i] /= val;
	}
}
double dot_MathVect(MathVect *vect1, MathVect *vect2)
{
	int i;
	double dotval = 0.0;
	if (vect1->size != vect2->size)
		return MAT_ERROR;
	for (i = 0; i < vect1->size; i++) {
		dotval += vect1->vect[i] * vect2->vect[i];
	}
	return dotval;
}
Mat_div_Vect(MathMat *mat, MathVect *vect)
{
	int i, j;
/***
	if (mat->col != vect->size) {
		return -1;
	}
***/
	for (i = 0; i < mat->row; i++) {
		for (j = 0; j < mat->col && j < vect->size; j++) {
			mat->matrix[i][j] /= vect->vect[j];
		}
	}
	return 0;
}
symmetrize_MathMat(MathMat *mat)
{
	int i, j;
	if (mat->row != mat->col) {
		return -1;
	}
	for (i = 0; i < mat->row; i++) {
		for (j = 0; j < i; j++) {
			mat->matrix[i][j] = mat->matrix[j][i] =
				sqrt(mat->matrix[i][j] * mat->matrix[j][i]);
		}
	}
	return 0;
}
