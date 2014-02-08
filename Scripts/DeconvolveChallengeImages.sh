# this script deconvolves the challenge images

echo running deconolve challenge images

export MAVEN_OPTS="-Xmx4096m"

# set the directories

projectsDir=~/Brian2012/Round2/projects/truenorthJ/ImageJ2Plugins/
rootImageDir=~/Brian2012/Round2/Grand_Challenge/Images/EvaluationData/

# the directory where we expect to find the images that have been extended 

inputDir=$rootImageDir/Extended/temp/
psfDir=$rootImageDir/Extended/temp/
outputDir=$rootImageDir/Extended/temp/

baseName=0
inputName=$baseName.extended.ome.tif
psfName=$baseName.psf.normalized.ome.tif

iterations=3
algorithm=rl
convolution=conv
deconvolvedName=$baseName.$algorithm.$iterations.ome.tif
convolvedName=$baseName.$convolution.ome.tif

cd $projectsDir/
mvn

cd $projectsDir/DeconvolutionTest

#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.fft.ConvolutionCommand silent input=$inputDir$inputName psf=$psfDir$psfName output=$outputDir$convolvedName"

mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.fft.RichardsonLucyCommand silent input=$inputDir$inputName psf=$psfDir$psfName output=$outputDir$deconvolvedName iterations=$iterations"


