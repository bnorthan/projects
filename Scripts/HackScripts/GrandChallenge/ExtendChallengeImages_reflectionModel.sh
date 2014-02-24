# this script extends an image

# set the directories
scriptDir=~/Brian2014/Projects/deconware/code/bin/Generated/
projectsDir=~/Brian2014/Projects/deconware/code/projects/truenorthJ/ImageJ2Plugins/
rootImageDir=~/Brian2014/Images/General/Deconvolution/Grand_Challenge/EvaluationData/

echo Running extend images script

export MAVEN_OPTS="-Xmx4096m"

separator=_
#directory=$desiredSizeX$separator$desiredSizeY$separator$desiredSizeZ
outputDir=mirrors

inputDir=$rootImageDir/Measurements/
psfDir=$rootImageDir/PSFs/
extendedDir=$rootImageDir/Extended/$outputDir/

mkdir $extendedDir

baseName=0

# set the name of the images
inputName=$baseName.tif
psfName=$baseName.tif
extendedName=$baseName.extended.ome.tif
extendedPsfName=$baseName.psf.extended.ome.tif

# desired dimensions of the image
desiredSizeX=215
desiredSizeY=215
desiredSizeZ=131

scriptName=$scriptDir/extendChallengeImages.hackscript

cd $projectsDir/DeconvolutionTest

# extend the image using mirror extension
echo "com.truenorth.commands.dim.ExtendCommandDimension silent input=$inputDir$inputName output=$extendedDir$extendedName dimensionX=$desiredSizeX dimensionY=$desiredSizeY dimensionZ=$desiredSizeZ boundaryType=mirror fftType=speed" > $scriptName

# extend the psf constant zero extension
echo "com.truenorth.commands.dim.ExtendCommandDimension silent input=$psfDir$psfName output=$extendedDir$extendedPsfName dimensionX=$desiredSizeX dimensionY=$desiredSizeY dimensionZ=$desiredSizeZ boundaryType=zero fftType=speed" >> $scriptName

mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="$scriptName"

