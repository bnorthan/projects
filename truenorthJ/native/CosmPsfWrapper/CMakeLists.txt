# this project is used to wrap cosm psf code
project(CosmPsfWrapperProject)

# Find JNI
find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})

FIND_PATH( COSM_PATH $ENV{COSM_DIR} [DOC "Cosm directory"] )

set(COSM_INCLUDE_DIRS
  "${COSM_PATH}/psf"
  "${COSM_PATH}/psf/psf"
  "${COSM_PATH}/util"
)

FIND_PATH( BLITZ_DIR $ENV{BLITZ_DIR} [DOC "Blitz directory path"])

<<<<<<< HEAD
<<<<<<< HEAD
include_directories(${COSM_INCLUDE_DIRS})
=======
include_directories(${COSM_INCLUDE_DIRS} ${BLITZ_DIR})
>>>>>>> 12faaad5f5f26a961e7930520e2d698efc51fd71
=======
include_directories(${COSM_INCLUDE_DIRS} ${BLITZ_DIR})
>>>>>>> origin/master

add_definitions(-fPIC)

set( QUADPACK_PATH
  ${COSM_PATH}/util/quadpack
  )
file(GLOB QUADPACK_C_SRC
  ${QUADPACK_PATH}/*.c
)
file(GLOB QUADPACK_CXX_SRC
  ${QUADPACK_PATH}/*.cxx
)
set( QUADPACK_SRC
  ${QUADPACK_C_SRC}
  ${QUADPACK_CXX_SRC} 
)

set( PSF_SRC ${COSM_PATH}/psf/psf)
set( XCOSM_SRC ${COSM_PATH}/psf/xcosm)

SET(PSF_LIB_SRCS ${PSF_SRC}/dqagFunction 
	${XCOSM_SRC}/rdlinesum
	${XCOSM_SRC}/rdcircsum
    ${XCOSM_SRC}/rotnone
    ${XCOSM_SRC}/rotsum
    ${XCOSM_SRC}/rotdiskline
    # ${XCOSM_SRC}/rotdicline
   # ${XCOSM_SRC}/rotinfdicline
    ${XCOSM_SRC}/rotdiskcirc
    ${XCOSM_SRC}/spline
    ${XCOSM_SRC}/real_fft3d
    ${XCOSM_SRC}/write_dataset
    ${XCOSM_SRC}/misc
    ${XCOSM_SRC}/tmp
    # ${XCOSM_SRC}/fftn
    )



add_library(CosmPsfWrapper SHARED ${PSF_LIB_SRCS} ${QUADPACK_SRC} com_truenorth_functions_psf_PsfGenerator.cpp)

