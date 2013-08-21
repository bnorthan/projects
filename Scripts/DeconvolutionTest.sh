echo Running the DeconvolutionTest script

export MAVEN_OPTS="-Xmx4096m"


cd ../truenorthJ/ImageJ2Plugins/functions
mvn

cd ../commands
mvn

cd ../DeconvolutionTest


#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.InverseFilterCommand input=/home/bnorthan/Brian2012/Round2/Images/Easy_Tests/MultiChannel1.ome.tif psf=/home/bnorthan/Brian2012/Round2/Images/Easy_Tests/AllInOne/psf.tif output=/home/bnorthan/Brian2012/Round2/Images/Easy_Tests/out.ome.tif iterations=19"

#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.fft.RichardsonLucyCommand input=/home/bnorthan/Brian2012/Round2/Images/Easy_Tests/AllInOne/April22/convExtended.ome.tif psf=/home/bnorthan/Brian2012/Round2/Images/Easy_Tests/AllInOne/April22/psf.tif output=/home/bnorthan/Brian2012/Round2/Images/Easy_Tests/AllInOne/April22/DeConvMay1.tif iterations=11 extensionXY=80 extensionZ=15"

#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.ConvolutionCommand input=/home/bnorthan/Brian2012/Round2/Images/Easy_Tests/MultiChannel1.ome.tif psf=/home/bnorthan/Brian2012/Round2/Images/Easy_Tests/AllInOne/psf.tif output=/home/bnorthan/Brian2012/Round2/Images/Easy_Tests/AllInOne/DToutputConv.tif iterations=19"

#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.RichardsonLucyCommand input=/home/bnorthan/Brian2012/Round2/Images/Easy_Tests/MultiChannel1.ome.tif psf=/home/bnorthan/Brian2012/Round2/Images/Easy_Tests/AllInOne/psf.tif output=/home/bnorthan/Brian2012/Round2/Images/Easy_Tests/out.ome.tif iterations=9"

#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.fft.RichardsonLucyCommand input=/home/bnorthan/Brian2012/Round2/Images/For_SPIE/Images/288_280_144/Bars_Psf_500.0_1.51_10.0Noisy.ome.tif psf=/home/bnorthan/Brian2012/Round2/Images/For_SPIE/PSFs/288_280_144/Psf_500.0_1.51_10.0.tif output=/home/bnorthan/Brian2012/Round2/Images/For_SPIE/Deconvolved/June19th/out.ome.tif iterations=10"

#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.noise.MedianFilterCommand input=/home/bnorthan/Brian2012/Round2/Images/Easy_Tests/AllInOne/April29/convApril22_2Noisy.tif psf=/home/bnorthan/Brian2012/Round2/Images/Easy_Tests/AllInOne/April22/psf.tif output=/home/bnorthan/Brian2012/Round2/Images/Easy_Tests/AllInOne/April29/Median.tif iterations=11 extensionXY=80 extensionZ=15"

#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.psf.CreatePsfCommand output=/home/bnorthan/Brian2012/Round2/Images/Easy_Tests/AllInOne/April22/DeConvMay1.ome.tif xSize=363 ySize=354 zSize=344"

#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.phantom.CreatePhantomCommand output=/home/bnorthan/Brian2012/Round2/Images/Easy_Tests/AllInOne/May28/Sphere.ome.tif xSize=363 ySize=254 zSize=344"

#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.ExtendCommand input=/home/bnorthan/Brian2012/Round2/Images/Easy_Tests/MultiChannel1.ome.tif output=/home/bnorthan/Brian2012/Round2/Images/Easy_Tests/AllInOne/May28/multiExtended.ome.tif extensionXY=20 extensionZ=10 boundaryType=boundaryZero fftType=speed"

#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.fuzzydeconvolution.commands.FuzzyDeconvolutionCommand input=/home/bnorthan/Brian2012/Round2/Images/Easy_Tests/AllInOne/May28/Phantom.ome.tif output=/home/bnorthan/Brian2012/Round2/Images/Easy_Tests/AllInOne/May28/multiExtended.ome.tif extensionXY=20 extensionZ=10 boundaryType=boundaryZero fftType=speed"

#mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.fuzzydeconvolution.commands.FuzzyDeconvolutionCommand input=/home/bnorthan/Brian2012/Round2/Images/For_SPIE/Images/288_280_144/Bars_Psf_500.0_1.51_10.0.tif  output=/home/bnorthan/Brian2012/Round2/Paper/Analysis_Fuzzy/Experiment2/out.ome.tif xySpace=40 zSpace=100 emissionWavelength=500.0 numericalAperture=1.3 designImmersionOilRefractiveIndex=1.515 designSpecimenLayerRefractiveIndex=1.515 actualImmersionOilRefractiveIndex=1.515 actualSpecimenLayerRefractiveIndex=1.33 actualPointSourceDepthInSpecimenLayer=10 dataDirectory=/home/bnorthan/Brian2012/Round2/Paper/Analysis_Fuzzy/June2013Tests/Experiment2/ firstRIToTry=1.45 dataFileBase=DataTest1.51 iterations=240"


mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.fft.RichardsonLucyCommand input=/home/bnorthan/Brian2012/Round2/projects/Images/FuzzyDeconvolution/images/bars32.ome.tif psf=/home/bnorthan/Brian2012/Round2/projects/Images/FuzzyDeconvolution/psfFuzzy.tif output=/home/bnorthan/Brian2012/Round2/projects/Images/FuzzyDeconvolution/outputs/outputTest.ome.tif iterations=10"

