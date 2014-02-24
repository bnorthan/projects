
# this script creates a phantom to test the fuzzy deconvolution
#
# steps are
#
# 1. Create a blank image 
# 2. Add a sphere
# 3. Extend the image (to effecient FFT size)
# 4. Create a psf (same size as the image)
# 5. Convolve the phantom and psf
# 6. Crop the image to the desired final size
# 7. Add Poisson noise to the image

echo Running create phantom script

export MAVEN_OPTS="-Xmx4096m"

# set the directories

# directory where the imagej projects are
projectsDir=~/Brian2014/Projects/deconware/code/projects/truenorthJ/ImageJ2Plugins/
# root image directory
rootImageDir=~/Brian2014/Images/General/Deconvolution/Phantoms/
# subdirectory to place generated images in
experimentDir=$rootImageDir/Aberrated/
# directory to place generated script in
scriptDir=~/Brian2014/Projects/deconware/code/bin/Generated/

mkdir $experimentDir

# put all images in the same directory
phantomDir=$experimentDir
psfDir=$experimentDir
extendedDir=$experimentDir
outputDir=$experimentDir

# size of the phantom
sizeX=192
sizeY=192
sizeZ=64

spherePositionX=$(expr $sizeX / 2)
spherePositionY=$(expr $sizeY / 2)
spherePositionZ=$(expr $sizeZ / 2)
sphereRadius=10
sphereIntensity=1000

echo $spherePositionX and $spherePositionY and $spherePositionZ 

# get psf params
source PsfParams.sh

# set the name of the images
phantomPrefix=sphere
experimentPrefix=$actualSpecimenLayerRefractiveIndex.$actualPointSourceDepthInSpecimenLayer
phantomName=$phantomPrefix.ome.tif
psfName=psf.$experimentPrefix.ome.tif

extendedName=$phantomPrefix.extfft.ome.tif
extendedPsfName=psf.extfft.$experimentPrefix.ome.tif

imageNoNoiseName=$phantomPrefix.$experimentPrefix.image.ome.tif
imageNoisyName=$phantomPrefix.$experimentPrefix.image.noisy.ome.tif

scriptName=$scriptDir/CreatePhantoms.hackscript

cd $projectsDir/DeconvolutionTest

# create the phantom
echo "com.truenorth.commands.phantom.CreatePhantomCommand silent output=$phantomDir$phantomName xSize=$sizeX ySize=$sizeY zSize=$sizeZ" > $scriptName

# add the sphere
echo "com.truenorth.commands.phantom.AddSphereCommand silent input=$phantomDir$phantomName output=$phantomDir$phantomName xCenter=$spherePositionX yCenter=$spherePositionY zCenter=$spherePositionZ radius=$sphereRadius intensity=$sphereIntensity" >> $scriptName

# extend the phantom for ffts
echo "com.truenorth.commands.dim.ExtendCommandDimension silent input=$phantomDir$phantomName output=$extendedDir$extendedName dimensionX=$sizeX dimensionY=$sizeY dimensionZ=$sizeZ boundaryType=mirror fftType=speed" >> $scriptName

# create the psf and extend for fft
echo "com.truenorth.commands.psf.CreatePsfCommand silent output=$psfDir$extendedPsfName xSize=$sizeX ySize=$sizeY zSize=$sizeZ fftType=speed scopeType=$scopeType psfModel=$psfModel xySpace=$xySpace zSpace=$zSpace emissionWavelength=$emissionWavelength numericalAperture=$numericalAperture designImmersionOilRefractiveIndex=$designImmersionOilRefractiveIndex designSpecimenLayerRefractiveIndex=$designSpecimenLayerRefractiveIndex actualImmersionOilRefractiveIndex=$actualImmersionOilRefractiveIndex actualSpecimenLayerRefractiveIndex=$actualSpecimenLayerRefractiveIndex actualPointSourceDepthInSpecimenLayer=$actualPointSourceDepthInSpecimenLayer centerPsf=true" >> $scriptName

# convolve phantom and psf to create an image
echo "com.truenorth.commands.fft.ConvolutionCommand silent input=$phantomDir$phantomName psf=$psfDir$extendedPsfName output=$outputDir$imageNoNoiseName" >> $scriptName

# add Poisson noise
echo "com.truenorth.commands.noise.AddPoissonNoiseCommand silent input=$outputDir$imageNoNoiseName output=$outputDir$imageNoisyName" >> $scriptName

mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="$scriptName"

