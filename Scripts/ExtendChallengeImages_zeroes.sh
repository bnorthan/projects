# this script extends an image

# set the directories
scriptDir=~/Brian2014/Projects/deconware/code/bin/Generated/
projectsDir=~/Brian2014/Projects/deconware/code/projects/truenorthJ/ImageJ2Plugins/
rootImageDir=~/Brian2014/Images/General/Deconvolution/Grand_Challenge/EvaluationData/

echo Running extend images script

export MAVEN_OPTS="-Xmx4096m"

# desired dimensions of the image
desiredSizeX=320
desiredSizeY=320
desiredSizeZ=192

separator=_
#directory=$desiredSizeX$separator$desiredSizeY$separator$desiredSizeZ
directory=zeros

inputDir=$rootImageDir/Measurements/
psfDir=$rootImageDir/PSFs/
extendedDir=$rootImageDir/Extended/$directory/

mkdir $extendedDir

baseName=1

# set the name of the images
inputName=$baseName.tif
psfName=$baseName.tif
extendedName=$baseName.extended.ome.tif
extendedPsfName=$baseName.psf.extended.ome.tif
normalizedPsfName=$baseName.psf.normalized.ome.tif
finalPsfName=$baseName.psfFinal.ome.tif

# original dimensions of the image
imageSizeX=192
imageSizeY=192
imageSizeZ=64

# original dimensions of the psf
psfSizeX=129
psfSizeY=129
psfSizeZ=127

# extended PSF Size
extendedPsfSizeX=320
extendedPsfSizeY=320
extendedPsfSizeZ=192

scriptName=$scriptDir/extendChallengeImages.hackscript

cd $projectsDir/DeconvolutionTest

# resize the psf to be the same size as the image
#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.dim.ExtendCommandDimension silent input=$psfDir$psfName output=$extendedDir$extendedPsfName dimensionX=$extendedPsfSizeX dimensionY=$extendedPsfSizeY dimensionZ=$extendedPsfSizeZ boundaryType=boundaryZero fftType=speed"

# extend the image using zero extension
echo "com.truenorth.commands.dim.ExtendCommandDimension silent input=$inputDir$inputName output=$extendedDir$extendedName dimensionX=$desiredSizeX dimensionY=$desiredSizeY dimensionZ=$desiredSizeZ boundaryType=zero fftType=speed" > $scriptName

# extend the psf constant zero extension
echo "com.truenorth.commands.dim.ExtendCommandDimension silent input=$psfDir$psfName output=$extendedDir$extendedPsfName dimensionX=$desiredSizeX dimensionY=$desiredSizeY dimensionZ=$desiredSizeZ boundaryType=zero fftType=speed" >> $scriptName

# crop the psf to the same size as the image
#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.dim.CropCommand input=$extendedDir$extendedPsfName output=$extendedDir$finalPsfName xSize=$finalPsfSizeX ySize=$finalPsfSizeY zSize=$finalPsfSizeZ"

mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="$scriptName"

