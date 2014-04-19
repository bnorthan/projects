# this project is used to wrap YacuDecu cuda deconvolution by Bob Yepin
project(YacuDecuWrapperProject)

# Find JNI
find_package(JNI REQUIRED)
find_package(CUDA REQUIRED)

include_directories(${JNI_INCLUDE_DIRS})
                                   
cuda_add_library(YacuDecuWrapper SHARED com_truenorth_gpu_wrappers_YacuDecuWrapper.cpp deconv.cu)

CUDA_ADD_CUFFT_TO_TARGET( YacuDecuWrapper )

