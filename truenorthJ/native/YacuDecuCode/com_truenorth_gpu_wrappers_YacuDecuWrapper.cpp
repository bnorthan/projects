#include <iostream>
#include <cuda_runtime.h>

#include "com_truenorth_gpu_wrappers_YacuDecuWrapper.h"

//#include "deconv.cu"

extern "C" int deconv_device(unsigned int iter, size_t N1, size_t N2, size_t N3,
                  float *h_image, float *h_psf, float *h_object);

// jni wrapper to call YacuDecu
JNIEXPORT jint JNICALL Java_com_truenorth_gpu_wrappers_YacuDecuWrapper_runYacuDecu
  (JNIEnv * env, jobject obj, jint iterations, jint xDim, jint yDim, jint zDim, jfloatArray input, jfloatArray psf, jfloatArray output)
  {
  	// calculate the size of the buffers using the dimensions that were passed in
  	long arraySize=xDim*yDim*zDim;

  	// get the actual sizes of the buffers 
  	long inputSize=env->GetArrayLength(input);
	long psfSize=env->GetArrayLength(psf);
  	long outputSize=env->GetArrayLength(output);

  	if ( (arraySize!=inputSize) || (arraySize!=psfSize) || (arraySize!=outputSize) )
  	{
  		return -1;
  	}

  	float* inputPointer=env->GetFloatArrayElements(input,0);
  	float* psfPointer=env->GetFloatArrayElements(psf,0);
  	float* outputPointer=env->GetFloatArrayElements(output,0);

  	deconv_device(iterations, xDim, yDim, zDim,
  	                  inputPointer, psfPointer, outputPointer);

  	env->ReleaseFloatArrayElements(input,inputPointer, 0);
  	env->ReleaseFloatArrayElements(psf, psfPointer, 0);
  	env->ReleaseFloatArrayElements(output, outputPointer, 0);

  	return 1;
  }

