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

rootImageDir=../../../../Images/

psfDir=$rootImageDir/psfs/
mkdir $psfDir
phantomDir=$rootImageDir/phantoms/
mkdir $phantomDir
imageDir=$rootImageDir/images/
mkdir $imageDir
outputDir=$rootImageDir/outputs/
mkdir $outputDir

# set the name of the images
phantomName=Phantom.ome.tif
psfName=psf.ome.tif
extendedName=extended.ome.tif
convName=image$actualSpecimenLayerRefractiveIndex.ome.tif
noisyConvName=noisyImage$actualSpecimenLayerRefractiveIndex.ome.tif
deconvolvedName=deconvolved.ome.tif
outputName=output.ome.tif

# call deconvolution test with "CreatePhantomCommand" to create the phantom
#mvn clean install -U exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.phantom.CreatePhantomCommand silent output=$phantomDir$phantomName xSize=$xSize ySize=$ySize zSize=$zSize"

# call deconvolution test with "CreatePsfCommand" to create the psf
mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.psf.CreatePsfCommand silent output=$psfDir$psfName xSize=$xSize ySize=$ySize zSize=$zSize fftType=speed xySpace=40 zSpace=100 emissionWavelength=500.0 numericalAperture=1.3 designImmersionOilRefractiveIndex=1.515 designSpecimenLayerRefractiveIndex=1.515 actualImmersionOilRefractiveIndex=1.515 actualSpecimenLayerRefractiveIndex=$actualSpecimenLayerRefractiveIndex actualPointSourceDepthInSpecimenLayer=10"

