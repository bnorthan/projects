echo Running the fuzzy script

export MAVEN_OPTS="-Xmx4096m"

cd ../truenorthJ/ImageJ2Plugins/
mvn

cd DeconvolutionTest

actualSpecimenLayerRefractiveIndex=1.47

rootImageDir=../../../Images/

inputDir=$rootImageDir/inputs/

dataDir=$rootImageDir/data/
mkdir $dataDir

outputDir=$rootImageDir/outputs/
mkdir $outputDir

imageName=image$actualSpecimenLayerRefractiveIndex.ome.tif
outputName=fuzzyOutput.ome.tif

mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.fuzzydeconvolution.commands.FuzzyDeconvolutionCommand input=$inputDir$imageName output=$outputDir$outputName xySpace=40 zSpace=100 emissionWavelength=500.0 numericalAperture=1.3 designImmersionOilRefractiveIndex=1.515 designSpecimenLayerRefractiveIndex=1.515 actualImmersionOilRefractiveIndex=1.515 actualSpecimenLayerRefractiveIndex=1.33 actualPointSourceDepthInSpecimenLayer=10 dataDirectory=$dataDir firstRIToTry=1.45 dataFileBase=DataTest1.51 iterations=250"
