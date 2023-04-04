#include <stdio.h>
#include <gsl/gsl_blas.h>
#include <time.h>

void naive_multiplication(const double a[], const double b[], double c[], int n) {
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            for (int k = 1; k < n; k++) {
                c[j * n + i] = c[j * n + i] + a[i * n + k] * b[k * n + j];
            }
        }
    }
}

void better_multiplication(const double a[], const double b[], double c[], int n) {
    for (int j = 0; j < n; j++) {
        for (int k = 1; k < n; k++) {
            for (int i = 0; i < n; i++) {
                    c[j * n + i] = c[j * n + i] + a[i * n + k] * b[k * n + j];
            }
        }
    }
}


int main() {
    FILE* csv_file;
    csv_file = fopen("cresults.csv", "w+");
    fprintf(csv_file, "size,multiplication_type,time\n");

    double a[160000];
    double b[160000];
    double c[160000];

    clock_t t;

    for (int i = 0; i < 160000; i++) {
        a[i] = (double) rand() / RAND_MAX;
        b[i] = (double) rand() / RAND_MAX;
    }

    for (int n = 100; n <= 400; n += 20) {
        for (int i = 0; i < 10; i++) {

            t = clock();
            naive_multiplication(a, b, c, n);
            t = clock() - t;
            fprintf(csv_file, "%d,naive,%f\n", n, ((double) t) / CLOCKS_PER_SEC);

            t = clock();
            better_multiplication(a, b, c, n);
            t = clock() - t;
            fprintf(csv_file, "%d,better,%f\n", n, ((double) t) / CLOCKS_PER_SEC);

            gsl_matrix_view A = gsl_matrix_view_array(a, n, n);
            gsl_matrix_view B = gsl_matrix_view_array(b, n, n);
            gsl_matrix_view C = gsl_matrix_view_array(c, n, n);

            t = clock();
            gsl_blas_dgemm (CblasNoTrans, CblasNoTrans,
                            1.0, &A.matrix, &B.matrix,
                            0.0, &C.matrix);
            t = clock() - t;
            fprintf(csv_file, "%d,BLAS,%f\n", n, ((double) t) / CLOCKS_PER_SEC);
        }
    }
    return 0;
}