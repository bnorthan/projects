# this script deconvolves the challenge images.  It assumes a circulant convolution model. 

echo running deconolve challenge images

export MAVEN_OPTS="-Xmx4096m"

# set the directories
scriptDir=~/Brian2014/Projects/deconware/code/bin/Generated/
projectsDir=~/Brian2014/Projects/deconware/code/projects/truenorthJ/ImageJ2Plugins/
rootImageDir=~/Brian2014/Images/General/Deconvolution/Grand_Challenge/EvaluationData/

# the directory where we expect to find the images that have been extended 
inputDir=$rootImageDir/Extended/mirrors/
psfDir=$rootImageDir/Extended/mirrors/
outputDir=$rootImageDir/Extended/mirrors/Deconvolved/

baseName=0
inputName=$baseName.extended.ome.tif
psfName=$baseName.psf.extended.ome.tif

iterations=30
algorithm=rldecon
outputName=$baseName.$algorithm.$iterations.ome.tif
finalName=$baseName.$algorithm.$iterations.final.ome.tif

scriptName=$scriptDir/DeconvolveChallengeImages.hackscript

cd $projectsDir/
mvn

cd $projectsDir/DeconvolutionTest

# original size of the image
sizeX=192
sizeY=192
sizeZ=64

#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.fft.ConvolutionCommand silent input=$inputDir$inputName psf=$psfDir$psfName output=$outputDir$convolvedName"

echo "com.truenorth.commands.fft.RichardsonLucyCommand silent input=$inputDir$inputName psf=$psfDir$psfName output=$outputDir$outputName iterations=$iterations" < $scriptName

echo "com.truenorth.commands.dim.CropCommand silent input=$outputDir$outputName output=$outputDir$finalName xSize=$sizeX ySize=$sizeY zSize=$sizeZ" >> $scriptName

mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="$scriptName"



