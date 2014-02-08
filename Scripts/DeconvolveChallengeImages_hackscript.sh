# this script deconvolves the challenge images

echo running deconvolve challenge images

export MAVEN_OPTS="-Xmx4096m"

# set the directories

# set the directories
scriptDir=~/Brian2014/Projects/deconware/code/bin/Generated/
projectsDir=~/Brian2014/Projects/deconware/code/projects/truenorthJ/ImageJ2Plugins/
rootImageDir=~/Brian2014/Images/General/Deconvolution/Grand_Challenge/EvaluationData/

# the directory where we expect to find the images that have been extended 

inputDir=$rootImageDir/Extended/zeros/
psfDir=$rootImageDir/Extended/zeros/
outputDir=$rootImageDir/Extended/zeros/

baseName=1
inputName=$baseName.extended.ome.tif
psfName=$baseName.psf.extended.ome.tif

iterations=1000
regularizationFactor=0.0005
algorithm=rltv_tn
convolution=conv
deconvolvedName=$baseName.$algorithm.$regularizationFactor.$iterations.ome.tif
convolvedName=$baseName.$convolution.ome.tif

cd $projectsDir/
mvn

cd $projectsDir/DeconvolutionTest

scriptName=$scriptDir/DeconvolveChallengeImages.hackscript

#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.fft.ConvolutionCommand silent input=$inputDir$inputName psf=$psfDir$psfName output=$outputDir$convolvedName"

#echo "com.truenorth.commands.fft.RichardsonLucyCommand silent input=$inputDir$inputName psf=$psfDir$psfName output=$outputDir$deconvolvedName iterations=$iterations" > $scriptName

echo "com.truenorth.commands.fft.TotalVariationRLCommand silent input=$inputDir$inputName psf=$psfDir$psfName output=$outputDir$deconvolvedName iterations=$iterations firstGuessType=constant regularizationFactor=$regularizationFactor" > $scriptName

mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="$scriptName"


