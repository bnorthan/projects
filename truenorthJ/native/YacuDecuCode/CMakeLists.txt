# this project is used to wrap YacuDecu cuda deconvolution by Bob Yepin
project(YacuDecuWrapperProject)

# Find Swig
FIND_PACKAGE(SWIG REQUIRED)
INCLUDE(${SWIG_USE_FILE})

# Find JNI
find_package(JNI REQUIRED)

# Find Cuda
find_package(CUDA REQUIRED)

# Find Java
find_package(Java REQUIRED)
INCLUDE(UseJava)

include_directories(${JNI_INCLUDE_DIRS})

cuda_add_library(YacuDecuWrapper SHARED com_truenorth_gpu_wrappers_YacuDecuWrapper.cpp deconv.cu)

CUDA_ADD_CUFFT_TO_TARGET( YacuDecuWrapper )
                                   
#cuda_add_library(YacuDecuWrapper SHARED deconv.cu)
#CUDA_ADD_CUFFT_TO_TARGET( YacuDecuWrapper )

set(CMAKE_SWIG_OUTDIR ${CMAKE_CURRENT_BINARY_DIR}/javasrc)

SET(CMAKE_SWIG_FLAGS -package com.truenorth.wrappers)
swig_add_module(YacuDecuSwig java YacuDecuWrapper.i "deconv.cu")
swig_link_libraries(YacuDecuSwig YacuDecuWrapper)

FILE(GLOB JAVA_SOURCE ${CMAKE_SWIG_OUTDIR}/*.java)
set(CMAKE_JAVA_COMPILE_FLAGS "-source" "1.6" "-target" "1.6")
add_jar(YacuDecuJavaWrapper ${JAVA_SOURCE} OUTPUT_DIR ${JAR_PATH})

add_dependencies(YacuDecuJavaWrapper YacuDecuSwig)

INSTALL(TARGETS YacuDecuWrapper YacuDecuSwig DESTINATION ${LIB_PATH})
#INSTALL(TARGETS YacuDecuSwig DESTINATION ${LIB_PATH})



