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
outputDir=$rootImageDir/Extended/zeros/

baseName=3.rltv_tn.0.0002.1000
inputName=$baseName.ome.tif
croppedName=$baseName.cropped.ome.tif

xSize=192
ySize=192
zSize=64

cd $projectsDir/
mvn

cd $projectsDir/DeconvolutionTest

scriptName=$scriptDir/cropChallengeImages.hackscript

echo "com.truenorth.commands.dim.CropCommand silent input=$inputDir$inputName output=$outputDir$croppedName xSize=$xSize ySize=$ySize zSize=$zSize" > $scriptName

mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="$scriptName"


