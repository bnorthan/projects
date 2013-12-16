
# this script creates a phantom image
#
# steps are
#
# 1. Create a blank image that is much larger then the final desired size
#    (done to minimize edge artifacts during the convolution step)
# 2. Add some spheres
# 3. Extend the image (to effecient FFT size)
# 4. Create a psf (same size as the image)
# 5. Convolve the phantom and psf
# 6. Crop the image to the desired final size
# 7. Add Poisson noise to the image

echo Running create phantom script

export MAVEN_OPTS="-Xmx4096m"

# set the directories

rootDir=~/Brian2014/Projects/deconware/
projectsDir=$rootDir/code/projects/truenorthJ/ImageJ2Plugins/
rootImageDir=$rootDir/Images/

experimentDir=$rootImageDir/Tests/multiSphereTest/

mkdir $experimentDir

# different directories for each phase??
#phantomDir=$rootImageDir/Tests/phantoms/
#psfDir=$rootImageDir/Tests/psfs/
#extendedDir=$rootImageDir/Tests/extended/
#outputDir=$rootImageDir/Tests/outputs/

# or everything in same directory??
phantomDir=$experimentDir
psfDir=$experimentDir
extendedDir=$experimentDir
outputDir=$experimentDir

trainingPhantoms=$rootImageDir/TrainingPhantoms/
mkdir $trainingPhantoms

finalPhantomDir=$trainingPhantoms/Measurements/
finalPsfDir=$trainingPhantoms/PSFs/

mkdir $finalPhantomDir
mkdir $finalPsfDir

# desired size of phantom
xSize=192
ySize=192
zSize=64

# extended size for convolutions
convolutionSizeX=360
convolutionSizeY=360
convolutionSizeZ=150

sphere1X=$(expr $convolutionSizeX / 3)
sphere1Y=$(expr $convolutionSizeY / 2)
sphere1Z=$(expr $convolutionSizeZ / 2)

sphere2X=$(expr $convolutionSizeX / 2)
sphere2Y=$(expr $convolutionSizeY / 3)
sphere2Z=$(expr $convolutionSizeZ / 2)

echo $sphere1X and $sphere1Y and $sphere1Z 

finalPsfSizeX=128
finalPsfSizeY=128
finalPsfSizeZ=127

# set the name of the images
phantomPrefix=doubleSphereLowSignal
phantomName=$phantomPrefix.ome.tif
finalPhantomName=$phantomPrefix.final.ome.tif
psfName=psf$convolutionSizeX.ome.tif
extendedName=$phantomPrefix$convolutionSizeX.ome.tif
convName=$phantomPrefix.conv.$convolutionSizeX.ome.tif
finalName=$phantomPrefix$convolutionSizeX.ome.tif
noisyName=$phantomPrefix$convolutionSizeX.noisy.ome.tif
finalPsfName=finalPsf$convolutionSizeX.ome.tif

cd $projectsDir/DeconvolutionTest

# create the phantom
mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.phantom.CreatePhantomCommand silent output=$phantomDir$phantomName xSize=$convolutionSizeX ySize=$convolutionSizeY zSize=$convolutionSizeZ"

# add the sphere
#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.phantom.AddSphereCommand silent input=$phantomDir$phantomName output=$phantomDir$phantomName xCenter=$sphere1X yCenter=$sphere1Y zCenter=$sphere1Z radius=10 intensity=12.1877"

# add another sphere
#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.phantom.AddSphereCommand silent input=$phantomDir$phantomName output=$phantomDir$phantomName xCenter=20 yCenter=20 zCenter=20 radius=10 intensity=36.363"

# add another sphere
#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.phantom.AddSphereCommand silent input=$phantomDir$phantomName output=$phantomDir$phantomName xCenter=$sphere2X yCenter=$sphere2Y zCenter=$sphere2Z radius=10 intensity=0.547"

# crop it down to the final size
#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.dim.CropCommand silent input=$phantomDir$phantomName output=$finalPhantomDir$finalPhantomName xSize=$xSize ySize=$ySize zSize=$zSize"

# extend the phantom
#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.dim.ExtendCommandDimension silent input=$phantomDir$phantomName output=$extendedDir$extendedName dimensionX=$convolutionSizeX dimensionY=$convolutionSizeY dimensionZ=$convolutionSizeZ boundaryType=boundaryZero fftType=speed"

# create the psf
#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.psf.CreatePsfCommand silent output=$psfDir$psfName xSize=$convolutionSizeX ySize=$convolutionSizeY zSize=$convolutionSizeZ fftType=speed scopeType=Widefield psfModel=GibsonLanni xySpace=40 zSpace=100 emissionWavelength=500.0 numericalAperture=1.3 designImmersionOilRefractiveIndex=1.515 designSpecimenLayerRefractiveIndex=1.515 actualImmersionOilRefractiveIndex=1.515 actualSpecimenLayerRefractiveIndex=1.51 actualPointSourceDepthInSpecimenLayer=10 centerPsf=true"

# convolve phantom and psf to create an image
#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.fft.ConvolutionCommand silent input=$extendedDir$extendedName psf=$psfDir$psfName output=$outputDir$convName"

# crop image
#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.dim.CropCommand silent input=$outputDir$convName output=$finalPhantomDir$finalName xSize=$xSize ySize=$ySize zSize=$zSize"

# add Poisson noise
#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.noise.AddPoissonNoiseCommand silent input=$finalPhantomDir$finalName output=$finalPhantomDir$noisyName"

# crop psf
#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.dim.CropCommand silent input=$psfDir$psfName output=$finalPsfDir$finalPsfName xSize=$finalPsfSizeX ySize=$finalPsfSizeY zSize=$finalPsfSizeZ"

