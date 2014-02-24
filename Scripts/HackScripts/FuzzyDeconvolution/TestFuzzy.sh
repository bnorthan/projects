echo Running the fuzzy script

export MAVEN_OPTS="-Xmx4096m"

# set the directories

# directory where the imagej projects are
projectsDir=~/Brian2014/Projects/deconware/code/projects/truenorthJ/ImageJ2Plugins/
# root image directory
rootImageDir=~/Brian2014/Images/General/Deconvolution/Phantoms/
# subdirectory to place generated images in
experimentDir=$rootImageDir/Aberrated/
# directory to place generated script in
scriptDir=~/Brian2014/Projects/deconware/code/bin/Generated/

# get psf params
source PsfParams.sh

cd $projectsDir/
mvn

cd DeconvolutionTest

inputDir=$experimentDir
outputDir=$experimentDir

dataDir=$experimentDir/data/
mkdir $dataDir

phantomPrefix=sphere
experimentPrefix=$actualSpecimenLayerRefractiveIndex.$actualPointSourceDepthInSpecimenLayer

imageName=$phantomPrefix.$experimentPrefix.image.noisy.ome.tif
iterations=23
outputName=fuzzyOutput2.$iterations.ome.tif

firstRIToTry=1.45
dataFileBase=$phantomPrefix.$experimentPrefix.DataTest


scriptName=$scriptDir/TestFuzzy.hackscript

echo "com.truenorth.fuzzydeconvolution.commands.FuzzyDeconvolutionCommand silent input=$inputDir$imageName output=$outputDir$outputName xySpace=$xySpace zSpace=$zSpace emissionWavelength=$emissionWavelength numericalAperture=$numericalAperture designImmersionOilRefractiveIndex=$designImmersionOilRefractiveIndex designSpecimenLayerRefractiveIndex=$designSpecimenLayerRefractiveIndex actualImmersionOilRefractiveIndex=$actualImmersionOilRefractiveIndex actualSpecimenLayerRefractiveIndex=$actualSpecimenLayerRefractiveIndex actualPointSourceDepthInSpecimenLayer=$actualPointSourceDepthInSpecimenLayer dataDirectory=$dataDir firstRIToTry=$firstRIToTry dataFileBase=$dataFileBase iterations=$iterations" > $scriptName

mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="$scriptName"
