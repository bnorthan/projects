
echo Noise Denoise

export MAVEN_OPTS="-Xmx4096m"

# make the ImageJ2 projects
cd ../truenorthJ/ImageJ2Plugins/
mvn

cd DeconvolutionTest

# set the directories

projectsDir=~/Brian2012/Round2/projects/
rootImageDir=$projectsDir/Images/

inputDir=$projectsDir/Images/inputs/
outputDir=$projectsDir/Images/NoiseDenoise/

# set the name of the images
imageName=MultiChannel1_32bit_scaled.ome.tif
outputName=MultiChannel1_Noisy.ome.tif

# call deconvolution test with the "AddPoissonNoise" command to create an image with noise
mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.noise.AddPoissonNoiseCommand silent input=$inputDir$imageName output=$outputDir$outputName"

