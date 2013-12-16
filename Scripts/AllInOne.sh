
# this script tests several related components of the deconvolution process.  It performs the following 
#
# 1.  Creates a phantom
# 2.  Creates a psf
# 3.  Extends the image to an effecient size for fft
# 4.  Convolves the image
# 5.  Adds noise to the image
# 6.  Deconvolves the image using Richardson Lucy
# 7.  Crops the image back to the original size

echo Running all in one

export MAVEN_OPTS="-Xmx4096m"

# set the size of the image
xSize=288
ySize=280
zSize=144

# set the specimen refractive 
actualSpecimenLayerRefractiveIndex=1.47

# make the ImageJ2 projects
cd ../truenorthJ/ImageJ2Plugins/

mvn

cd DeconvolutionTest

# set the directories

rootDir=~/Brian2014/Projects/deconware/
projectsDir=$rootDir/code/projects/
rootImageDir=$rootDir/Images/AllInOne/

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
psfName=psf.ome.tif
extendedName=extended.ome.tif
convName=image$actualSpecimenLayerRefractiveIndex.ome.tif
noisyConvName=noisyImage$actualSpecimenLayerRefractiveIndex.ome.tif
deconvolvedName=deconvolved.ome.tif
outputName=output.ome.tif

# call deconvolution test with "CreatePhantomCommand" to create the phantom
mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.phantom.CreatePhantomCommand silent output=$phantomDir$phantomName xSize=$xSize ySize=$ySize zSize=$zSize"

# call deconvolution test with "CreatePsfCommand" to create the psf
#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.psf.CreatePsfCommand silent output=$psfDir$psfName xSize=$xSize ySize=$ySize zSize=$zSize fftType=speed xySpace=40 zSpace=100 emissionWavelength=500.0 numericalAperture=1.3 designImmersionOilRefractiveIndex=1.515 designSpecimenLayerRefractiveIndex=1.515 actualImmersionOilRefractiveIndex=1.515 actualSpecimenLayerRefractiveIndex=$actualSpecimenLayerRefractiveIndex actualPointSourceDepthInSpecimenLayer=10"

# call deconvolution test with "ExtendCommand" to extend the phantom to the nearest fast fft size
#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.dim.ExtendCommand silent input=$phantomDir$phantomName psf=$psfDir$psfName output=$outputDir$extendedName extensionXY=20 extensionZ=10 boundaryType=boundaryZero fftType=speed"

# call deconvolution test with the "ConvolutionCommand" to create an image
#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.fft.ConvolutionCommand silent input=$outputDir$extendedName psf=$psfDir$psfName output=$outputDir$convName"

# call deconvolution test with the "AddPoissonNoise" command to create an image with noise
#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.noise.AddPoissonNoiseCommand silent input=$outputDir$convName output=$outputDir$noisyConvName"

# call deconvolution test with the "RichardsonLucyCommand" to deconvolve the image
#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.fft.RichardsonLucyCommand silent input=$outputDir$noisyConvName psf=$psfDir$psfName output=$outputDir$deconvolvedName iterations=100"

# call deconvolution test with the "CropCommand" to recrop back to the original size (from the fft size)
#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.dim.CropCommand input=$outputDir$deconvolvedName output=$outputDir$outputName xSize=$xSize ySize=$ySize zSize=$zSize"

