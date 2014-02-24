# this script extends an image using the model from the second deconvolution grand challenge
# described here: http://bigwww.epfl.ch/deconvolution/challenge/index.html?p=documentation/overview

# set the directories
scriptDir=~/Brian2014/Projects/deconware/code/bin/Generated/
projectsDir=~/Brian2014/Projects/deconware/code/projects/truenorthJ/ImageJ2Plugins/
rootImageDir=~/Brian2014/Images/General/Deconvolution/Grand_Challenge/EvaluationData/
outputDir=challenge

echo Running extend images script

export MAVEN_OPTS="-Xmx4096m"

inputDir=$rootImageDir/Measurements/
psfDir=$rootImageDir/PSFs/
extendedDir=$rootImageDir/Extended/$outputDir/

mkdir $extendedDir

baseName=2

# set the name of the images
inputName=$baseName.tif
psfName=$baseName.tif
extendedName=$baseName.extended.ome.tif
extendedPsfName=$baseName.psf.extended.ome.tif

# size of the measurement 
measurementSizeX=192
measurementSizeY=192
measurementSizeZ=64

# size of the psf
psfSizeX=129
psfSizeY=129
psfSizeZ=127

# size of the extended space
extendedSizeX=$(($measurementSizeX+$psfSizeX-1))
extendedSizeY=$(($measurementSizeY+$psfSizeY-1))
extendedSizeZ=$(($measurementSizeZ+$psfSizeZ-1))

scriptName=$scriptDir/extendChallengeImages.hackscript

cd $projectsDir/DeconvolutionTest

# extend the image using zero extension
echo "com.truenorth.commands.dim.ExtendCommandDimension silent input=$inputDir$inputName output=$extendedDir$extendedName dimensionX=$extendedSizeX dimensionY=$extendedSizeY dimensionZ=$extendedSizeZ boundaryType=zero fftType=speed" > $scriptName

# extend the psf constant zero extension
echo "com.truenorth.commands.dim.ExtendCommandDimension silent input=$psfDir$psfName output=$extendedDir$extendedPsfName dimensionX=$extendedSizeX dimensionY=$extendedSizeY dimensionZ=$extendedSizeZ boundaryType=zero fftType=speed" >> $scriptName

mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="$scriptName"

