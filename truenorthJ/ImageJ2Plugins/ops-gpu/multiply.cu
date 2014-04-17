#include <iostream>
#include <stdio.h>

__global__ void multiply(float a, float b, float *c)
{
	*c=a*b;
}

extern "C"
__global__ void multiply2(float *a, float *b, float *c)
        /*************** Kernel Code **************/
{
        c[0]= a[0] * b[0];
}

int main()
{
  float a, b, c;
  float *c_pointer;
  a=1.35;
  b=2.5;

  cudaMalloc((void**)&c_pointer, sizeof(float));
  multiply<<<1,1>>>(a, b, c_pointer);
  cudaMemcpy(&c, c_pointer, sizeof(float),cudaMemcpyDeviceToHost);
/*** This is C!!! You manage your garbage on your own!  ***/    
  cudaFree(c_pointer);
  printf("Result = %f\n",c);
}
