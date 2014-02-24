# this script deconvolves the challenge images using the noncirculant model.
# the non-circulant model is described in
# http://bigwww.epfl.ch/deconvolution/challenge/index.html?p=documentation/theory/forwardmodel

echo running deconvolve challenge images

export MAVEN_OPTS="-Xmx4096m"

# set the directories
scriptDir=~/Brian2014/Projects/deconware/code/bin/Generated/
projectsDir=~/Brian2014/Projects/deconware/code/projects/truenorthJ/ImageJ2Plugins/
rootImageDir=~/Brian2014/Images/General/Deconvolution/Grand_Challenge/EvaluationData/

# the directory where we expect to find the images that have been extended 
inputDir=$rootImageDir/Extended/testFeb10/
psfDir=$rootImageDir/Extended/testFeb10/
outputDir=$rootImageDir/Extended/testFeb10/

baseName=2
inputName=$baseName.extended.ome.tif
psfName=$baseName.psf.extended.ome.tif

iterations=17
regularizationFactor=0.009
algorithm=rltv_tn
deconvolvedName=$baseName.$algorithm.$regularizationFactor.$iterations.ome.tif
finalName=$baseName.$algorithm.$regularizationFactor.$iterations.final.ome.tif

# it is assumed the input images have allready been extended for fft so we need the size of the image and psf window # for the noncirculant deconvolution
imageWindowX=192
imageWindowY=192
imageWindowZ=64

psfWindowX=129
psfWindowY=129
psfWindowZ=127

cd $projectsDir/
mvn

cd $projectsDir/DeconvolutionTest

scriptName=$scriptDir/DeconvolveChallengeImages.hackscript

# call RL with total variation
echo "com.truenorth.commands.fft.TotalVariationRLCommand silent input=$inputDir$inputName psf=$psfDir$psfName output=$outputDir$deconvolvedName iterations=$iterations firstGuessType=constant convolutionStrategy=noncirculant regularizationFactor=$regularizationFactor imageWindowX=$imageWindowX imageWindowY=$imageWindowY imageWindowZ=$imageWindowZ psfWindowX=$psfWindowX psfWindowY=$psfWindowY psfWindowZ=$psfWindowZ" > $scriptName

echo "com.truenorth.commands.dim.CropCommand silent input=$outputDir$deconvolvedName output=$outputDir$finalName xSize=$imageWindowX ySize=$imageWindowY zSize=$imageWindowZ" >> $scriptName

mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="$scriptName"


