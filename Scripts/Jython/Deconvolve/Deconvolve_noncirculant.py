# @StatusService status
# @DatasetService data
# @CommandService command
# @DisplayService display
# @IOService io

# this script extends and deconvolves an image using the model from the second deconvolution grand challenge
# described here: http://bigwww.epfl.ch/deconvolution/challenge/index.html?p=documentation/overview

from net.imglib2.meta import Axes;

rootImageDir="/home/bnorthan/Brian2014/Images/General/Deconvolution/"

inputDir=rootImageDir+"/Phantoms/2Spheres3/"
outputDir=rootImageDir+"/Tests/2Spheres3/noncirculant/"

inputName="phantom_.image.ome.tif"
psfName="psf.ome.tif"

outputBase="phantom"

# open and display the input image
inputData=data.open(inputDir+inputName)
display.createDisplay(inputData.getName(), inputData);	

# open and display the psf
psf=data.open(inputDir+psfName)
display.createDisplay(psf.getName(), psf);


# size of the measurement 
measurementSizeX=inputData.dimension(inputData.dimensionIndex(Axes.X));
measurementSizeY=inputData.dimension(inputData.dimensionIndex(Axes.Y));
#measurementSizeZ=inputData.dimension(inputData.dimensionIndex(Axes.Z));
measurementSizeZ=inputData.dimension(2);

# size of the psf
psfSizeX=psf.dimension(psf.dimensionIndex(Axes.X));
psfSizeY=psf.dimension(psf.dimensionIndex(Axes.Y));
#psfSizeZ=psf.dimension(psf.dimensionIndex(Axes.Z));
psfSizeZ=psf.dimension(2);

# size of the object space
objectSizeX=measurementSizeX+psfSizeX-1
objectSizeY=measurementSizeY+psfSizeY-1
objectSizeZ=measurementSizeZ+psfSizeZ-1

iterations=200
regularizationFactor=0.009
algorithm="rltv_tn_nc"

extendedName=outputDir+outputBase+".extended.ome.tif"
extendedPsfName=outputDir+"psf.extended.ome.tif"
deconvolvedName=outputDir+outputBase+str(algorithm)+"."+str(regularizationFactor)+"."+str(iterations)+".ome.tif"
finalName=outputDir+outputBase+str(algorithm)+"."+str(regularizationFactor)+"."+str(iterations)+".final.ome.tif"

# extend the image
extended=command.run("com.truenorth.commands.dim.ExtendCommandDimension", True, "input", inputData, "dimensionX", objectSizeX, \
		"dimensionY", objectSizeY, "dimensionZ", objectSizeZ, "boundaryType", "zero", "fftType", "speed").get().getOutputs().get("output");
io.save(extended, extendedName);

# extend the psf
psfExtended=command.run("com.truenorth.commands.dim.ExtendCommandDimension", True, "input", psf, "dimensionX", objectSizeX, \
		"dimensionY", objectSizeY, "dimensionZ", objectSizeZ, "boundaryType", "zero", "fftType", "speed").get().getOutputs().get("output");
io.save(psfExtended, extendedPsfName);

# call RL
deconvolved = command.run("com.truenorth.commands.fft.RichardsonLucyCommand", True, "input", extended, "psf", psfExtended, "truth", None,"firstGuess", None, "iterations", iterations, "firstGuessType", "constant", "convolutionStrategy", "noncirculant", "regularizationFactor", regularizationFactor, "imageWindowX", measurementSizeX, "imageWindowY", measurementSizeY, "imageWindowZ", measurementSizeZ, "psfWindowX", psfSizeX, "psfWindowY", psfSizeY, "psfWindowZ", psfSizeZ).get().getOutputs().get("output");
io.save(deconvolved, deconvolvedName);

# crop back down to image window size
final = command.run("com.truenorth.commands.dim.CropCommand", True, "input", deconvolved, "xSize", measurementSizeX, "ySize", measurementSizeY, "zSize", measurementSizeZ).get().getOutputs().get("output");
io.save(final, finalName);

