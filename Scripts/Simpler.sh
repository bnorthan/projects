echo Running all in one

# this script tests several related components of the deconvolution process.  It performs the following 
#
# 1.  Creates a phantom
# 2.  Creates a psf
# 3.  Extends the image to an effecient size for fft
# 4.  Convolves the image
# 5.  Adds noise to the image
# 6.  Deconvolves the image using Richardson Lucy
# 7.  Crops the image back to the original size

# set the size of the image
xSize=288
ySize=280
zSize=144

# set the specimen refractive 
actualSpecimenLayerRefractiveIndex=1.47

# make the ImageJ2 projects
cd ../truenorthJ/ImageJ2Plugins/
mvn clean install -U

cd DeconvolutionTest
mvn clean install -U

# set the directories
