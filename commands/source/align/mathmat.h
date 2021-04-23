#ifndef MATHMAT_H
typedef struct {
	int row, col;
	double **matrix;
} MathMat;

typedef struct {
	int size;
	double *vect;
} MathVect;

MathMat *create_MathMat();
MathMat *create_MathMat_init();
MathMat *create_MathMat_diag();
MathMat *create_MathMat_diagvect();
MathMat *MathMat_multiply();
MathMat *submat_MathMat();
MathMat *copy_MathMat();
MathMat *transpose_MathMat();
MathVect *create_MathVect();
MathVect *create_MathVect_init();
double sum_MathVect();
double dot_MathVect();
double get_MathVect();

#define set_MathMat(mat,i,j,val) ((mat)->matrix[(i)][(j)] = (val))
#define get_MathMat(mat,i,j) ((mat)->matrix[(i)][(j)])
#define get_MathMatRow(mat) ((mat)->row)
#define get_MathMatCol(mat) ((mat)->col)

#define set_MathVect0(vec,array) ((vec)->vect = (array))
#define set_MathVect(vec,i,val) ((vec)->vect[(i)] = (val))
#define get_MathVect(vec,i) ((vec)->vect[(i)])

#define MAT_ERROR -999999

#define MATHMAT_H
#endif /* ifndef MATHMAT_H */
