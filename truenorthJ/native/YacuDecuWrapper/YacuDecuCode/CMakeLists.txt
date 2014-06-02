# this project is used to wrap YacuDecu cuda deconvolution by Bob Yepin
project(YacuDecuWrapperProject)

# Find Swig
FIND_PACKAGE(SWIG REQUIRED)
INCLUDE(${SWIG_USE_FILE})

# Find JNI
find_package(JNI REQUIRED)
find_package(CUDA REQUIRED)

# Find Java
find_package(Java REQUIRED)
INCLUDE(UseJava)

include_directories(${JNI_INCLUDE_DIRS})

#make deconv library            
cuda_add_library(deconv SHARED deconv.cu)
CUDA_ADD_CUFFT_TO_TARGET( deconv )

# set output dir for java files
set(CMAKE_SWIG_OUTDIR ${CMAKE_CURRENT_BINARY_DIR}/javasrc)

# use swig to generate java wrappers
swig_add_module(YacuDecu_swig java YacuDecuWrapper.i "deconv.cu")
swig_link_libraries(YacuDecu_swig YacuDecuWrapper)

# compile the java files into a jar
FILE(GLOB JAVA_SOURCE ${CMAKE_SWIG_OUTDIR}/*.java)
add_jar(YacuDecuJavaWrapper ${JAVA_SOURCE} POST_BUILD)

add_dependencies(YacuDecuJavaWrapper YacuDecu_swig)

install (TARGETS MathFunctions DESTINATION bin