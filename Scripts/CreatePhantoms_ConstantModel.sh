
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

experimentDir=$rootImageDir/Tests/ConstantTest/
scriptDir=~/Brian2014/Projects/deconware/code/bin/Generated/

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
convolutionSizeZ=168

psfSizeX=128
psfSizeY=128
psfSizeZ=127

# set the name of the images
phantomPrefix=constant
phantomName=$phantomPrefix.ome.tif
finalPhantomName=$phantomPrefix.final.ome.tif
psfName=psf.ome.tif

extendedName=$phantomPrefix$convolutionSizeX.ome.tif
extendedPsfName=psf.extended.ome.tif

convName=$phantomPrefix.conv.$convolutionSizeX.ome.tif

imageNoNoiseName=$phantomPrefix.image.ome.tif
imageNoisyName=$phantomPrefix.image.noisy.ome.tif

finalPsfName=finalPsf$convolutionSizeX.ome.tif

scriptName=$scriptDir/CreatePhantoms.hackscript

cd $projectsDir/DeconvolutionTest

# create the phantom
echo "com.truenorth.commands.phantom.CreatePhantomCommand silent output=$phantomDir$phantomName xSize=$convolutionSizeX ySize=$convolutionSizeY zSize=$convolutionSizeZ background=1.0" > $scriptName

# add the sphere
#echo "com.truenorth.commands.phantom.AddSphereCommand silent input=$phantomDir$phantomName output=$phantomDir$phantomName xCenter=$sphere1X yCenter=$sphere1Y zCenter=$sphere1Z radius=10 intensity=120.1877" >> $scriptName

# add the shell
#echo "com.truenorth.commands.phantom.AddShellCommand silent input=$phantomDir$phantomName output=$phantomDir$phantomName xCenter=$sphere1X yCenter=$sphere1Y zCenter=$sphere1Z radius=50 innerRadius=49 intensity=1920.1877" >> $scriptName

# crop it down to the final size
echo "com.truenorth.commands.dim.CropCommand silent input=$phantomDir$phantomName output=$phantomDir$finalPhantomName xSize=$xSize ySize=$ySize zSize=$zSize" >> $scriptName

# extend the phantom
echo "com.truenorth.commands.dim.ExtendCommandDimension silent input=$phantomDir$phantomName output=$extendedDir$extendedName dimensionX=$convolutionSizeX dimensionY=$convolutionSizeY dimensionZ=$convolutionSizeZ boundaryType=boundaryZero fftType=speed" >> $scriptName

# create the psf
echo "com.truenorth.commands.psf.CreatePsfCommand silent output=$psfDir$psfName xSize=$psfSizeX ySize=$psfSizeY zSize=$psfSizeZ fftType=none scopeType=Widefield psfModel=GibsonLanni xySpace=40 zSpace=100 emissionWavelength=500.0 numericalAperture=1.3 designImmersionOilRefractiveIndex=1.515 designSpecimenLayerRefractiveIndex=1.515 actualImmersionOilRefractiveIndex=1.515 actualSpecimenLayerRefractiveIndex=1.51 actualPointSourceDepthInSpecimenLayer=10 centerPsf=true" >> $scriptName

# extend psf for convolution
echo "com.truenorth.commands.dim.ExtendCommandDimension silent input=$psfDir$psfName output=$extendedDir$extendedPsfName dimensionX=$convolutionSizeX dimensionY=$convolutionSizeY dimensionZ=$convolutionSizeZ boundaryType=boundaryZero fftType=speed" >> $scriptName

# convolve phantom and psf to create an image
echo "com.truenorth.commands.fft.ConvolutionCommand silent input=$phantomDir$phantomName psf=$psfDir$extendedPsfName output=$outputDir$convName" >> $scriptName

# crop image
echo "com.truenorth.commands.dim.CropCommand silent input=$outputDir$convName output=$outputDir$imageNoNoiseName xSize=$xSize ySize=$ySize zSize=$zSize" >> $scriptName

# add Poisson noise
echo "com.truenorth.commands.noise.AddPoissonNoiseCommand silent input=$outputDir$imageNoNoiseName output=$outputDir$imageNoisyName" >> $scriptName

# crop psf
#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.dim.CropCommand silent input=$psfDir$psfName output=$finalPsfDir$finalPsfName xSize=$finalPsfSizeX ySize=$finalPsfSizeY zSize=$finalPsfSizeZ"

mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="$scriptName"

