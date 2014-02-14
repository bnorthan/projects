# this script deconvolves the challenge images
# the non-circulant model is described in
# http://bigwww.epfl.ch/deconvolution/challenge/index.html?p=documentation/theory/forwardmodel

echo running deconvolve challenge images

export MAVEN_OPTS="-Xmx4096m"

# set the directories

# set the directories
scriptDir=~/Brian2014/Projects/deconware/code/bin/Generated/
projectsDir=~/Brian2014/Projects/deconware/code/projects/truenorthJ/ImageJ2Plugins/
rootImageDir=~/Brian2014/Projects/deconware/Images/Tests/

# the directory where we expect to find the images that have been extended 

inputDir=$rootImageDir/ShellTest2/
psfDir=$rootImageDir/ShellTest2/
outputDir=$rootImageDir/ShellTest2/Deconvolve/

inputName=shell.image.noisy.ome.tif
psfName=psf.ome.tif
extendedName=extended.ome.tif
psfExtendedName=psf.extended.ome.tif

iterations=200
regularizationFactor=0.0002
algorithm=rltv
convolution=conv
deconvolvedName=$inputName.$algorithm.$iterations.ome.tif
croppedName=$inputName.$algorithm.$iterations.cropped.ome.tif

# size of the measurement 
measurementSizeX=192
measurementSizeY=192
measurementSizeZ=64

# size of the psf
psfSizeX=129
psfSizeY=129
psfSizeZ=127

# size of the object
extendedSizeX=$(($measurementSizeX+$psfSizeX-1))
extendedSizeY=$(($measurementSizeY+$psfSizeY-1))
extendedSizeZ=$(($measurementSizeZ+$psfSizeZ-1))

cd $projectsDir/
mvn

cd $projectsDir/DeconvolutionTest

scriptName=$scriptDir/DeconvolveChallengeImages.hackscript

# extend the phantom
echo "com.truenorth.commands.dim.ExtendCommandDimension silent input=$inputDir$inputName output=$outputDir$extendedName dimensionX=$extendedSizeX dimensionY=$extendedSizeY dimensionZ=$extendedSizeZ boundaryType=boundaryZero fftType=speed" > $scriptName

# extend the psf
echo "com.truenorth.commands.dim.ExtendCommandDimension silent input=$inputDir$psfName output=$outputDir$psfExtendedName dimensionX=$extendedSizeX dimensionY=$extendedSizeY dimensionZ=$extendedSizeZ boundaryType=boundaryZero fftType=speed" >> $scriptName

echo "com.truenorth.commands.fft.TotalVariationRLCommand silent input=$outputDir$extendedName psf=$outputDir$psfExtendedName output=$outputDir$deconvolvedName firstGuessType=constant iterations=$iterations regularizationFactor=$regularizationFactor convolutionStrategy=noncirculant imageWindowX=$measurementSizeX imageWindowY=$measurementSizeY imageWindowZ=$measurementSizeZ psfWindowX=$psfSizeX psfWindowY=$psfSizeY psfWindowZ=$psfSizeZ" >> $scriptName

echo "com.truenorth.commands.dim.CropCommand silent input=$outputDir$deconvolvedName output=$outputDir$croppedName xSize=$measurementSizeX ySize=$measurementSizeY zSize=$measurementSizeZ" >> $scriptName

mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="$scriptName"


