
# this script creates a shell phantom image and convolves using a noncirculant convolution model
#
# steps are
#
# 1. Create a blank image that is much larger then the final desired size
#    (done to minimize edge artifacts during the convolution step)
# 2. Add a shell
# 3. Given psf size and measurement size extend the image to largest efficient fft size that avoids overlap 
# 4. Create a psf and extend
# 5. Convolve the phantom and psf
# 6. Crop the image to the final "measurement" size
# 7. Add Poisson noise to the image

echo Running create phantom script

export MAVEN_OPTS="-Xmx4096m"

# set the directories

# directory where the imagej projects are
projectsDir=~/Brian2014/Projects/deconware/code/projects/truenorthJ/ImageJ2Plugins/
# root image directory
rootImageDir=~/Brian2014/Images/General/Deconvolution/Phantoms/
# subdirectory to place generated images in
experimentDir=$rootImageDir/ShellTest/
# directory to place generated script in
scriptDir=~/Brian2014/Projects/deconware/code/bin/Generated/

mkdir $experimentDir

# put all intermediate images in the same directory
phantomDir=$experimentDir
psfDir=$experimentDir
extendedDir=$experimentDir
outputDir=$experimentDir

# size of the measurement 
measurementSizeX=192
measurementSizeY=192
measurementSizeZ=64

# size of the psf
psfSizeX=129
psfSizeY=129
psfSizeZ=127

# size of the object space
objectSizeX=$(($measurementSizeX+$psfSizeX-1))
objectSizeY=$(($measurementSizeY+$psfSizeY-1))
objectSizeZ=$(($measurementSizeZ+$psfSizeZ-1))

# location of the shell
shellPositionX=$(expr $objectSizeX / 2)
shellPositionY=$(expr $objectSizeY / 2)
shellPositionZ=$(expr $objectSizeZ / 2)

# parameters of the shell
shellOuterRadius=40
shellInnerRadius=39
shellIntensity=1920.1877

echo $shellPositionX and $shellPositionY and $shellPositionZ 

# set the name of the images
phantomPrefix=shell
phantomName=$phantomPrefix.ome.tif
psfName=psf.ome.tif

extendedName=$phantomPrefix.extfft.ome.tif
extendedPsfName=psf.extfft.ome.tif

convName=$phantomPrefix.conv.ome.tif

imageNoNoiseName=$phantomPrefix.image.ome.tif
imageNoisyName=$phantomPrefix.image.noisy.ome.tif

scriptName=$scriptDir/CreatePhantoms.hackscript

cd $projectsDir/DeconvolutionTest

# create the phantom
echo "com.truenorth.commands.phantom.CreatePhantomCommand silent output=$phantomDir$phantomName xSize=$objectSizeX ySize=$objectSizeY zSize=$objectSizeZ" > $scriptName

# add the shell
echo "com.truenorth.commands.phantom.AddShellCommand silent input=$phantomDir$phantomName output=$phantomDir$phantomName xCenter=$shellPositionX yCenter=$shellPositionY zCenter=$shellPositionZ radius=$shellOuterRadius innerRadius=$shellInnerRadius intensity=$shellIntensity" >> $scriptName

# extend the phantom for ffts
echo "com.truenorth.commands.dim.ExtendCommandDimension silent input=$phantomDir$phantomName output=$extendedDir$extendedName dimensionX=$objectSizeX dimensionY=$objectSizeY dimensionZ=$objectSizeZ boundaryType=boundaryZero fftType=speed" >> $scriptName

# create the psf
echo "com.truenorth.commands.psf.CreatePsfCommand silent output=$psfDir$psfName xSize=$psfSizeX ySize=$psfSizeY zSize=$psfSizeZ fftType=none scopeType=Widefield psfModel=GibsonLanni xySpace=100 zSpace=300 emissionWavelength=500.0 numericalAperture=1.3 designImmersionOilRefractiveIndex=1.515 designSpecimenLayerRefractiveIndex=1.515 actualImmersionOilRefractiveIndex=1.515 actualSpecimenLayerRefractiveIndex=1.51 actualPointSourceDepthInSpecimenLayer=10 centerPsf=true" >> $scriptName

# extend psf for ffts
echo "com.truenorth.commands.dim.ExtendCommandDimension silent input=$psfDir$psfName output=$extendedDir$extendedPsfName dimensionX=$objectSizeX dimensionY=$objectSizeY dimensionZ=$objectSizeZ boundaryType=boundaryZero fftType=speed" >> $scriptName

# convolve phantom and psf to create an image
echo "com.truenorth.commands.fft.ConvolutionCommand silent input=$phantomDir$phantomName psf=$psfDir$extendedPsfName output=$outputDir$convName" >> $scriptName

# crop image
echo "com.truenorth.commands.dim.CropCommand silent input=$outputDir$convName output=$outputDir$imageNoNoiseName xSize=$measurementSizeX ySize=$measurementSizeY zSize=$measurementSizeZ" >> $scriptName

# add Poisson noise
echo "com.truenorth.commands.noise.AddPoissonNoiseCommand silent input=$outputDir$imageNoNoiseName output=$outputDir$imageNoisyName" >> $scriptName

mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="$scriptName"

