#include <stdio.h>
#include <gsl/gsl_ieee_utils.h>

int main() {
  float n = 0.001;
  while (n > 0.0) {
    n /= 2;
    gsl_ieee_printf_float(&n);
    printf("\n");
  }
  return 0;
}
