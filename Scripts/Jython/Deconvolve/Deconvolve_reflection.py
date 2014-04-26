# @StatusService status
# @DatasetService data
# @CommandService command
# @DisplayService display
# @IOService io

# this script extends and deconvolves an image using the model from the second deconvolution grand challenge
# described here: http://bigwww.epfl.ch/deconvolution/challenge/index.html?p=documentation/overview

import os
from net.imglib2.meta import Axes;

rootImageDir="/home/bnorthan/Brian2014/Images/General/Deconvolution/"

#inputDir=rootImageDir+"/Phantoms/BigShellShift/"
#outputDir=rootImageDir+"/Tests/BigShellShift/reflection2/"
#inputName="phantom_.image.ome.tif"
#outputBase="phantom"

inputDir=rootImageDir+"/Phantoms/2Spheres3/"
outputDir=rootImageDir+"/Tests/2Spheres3/reflection_fullextension/"
algorithm="rltv"

if not os.path.exists(outputDir):
    os.makedirs(outputDir)

inputName="phantom_.image.ome.tif"
outputBase="phantom"

psfName="psf.ome.tif"


# open and display the input image
inputData=data.open(inputDir+inputName)
display.createDisplay(inputData.getName(), inputData);	

# open and display the psf
psf=data.open(inputDir+psfName)
display.createDisplay(psf.getName(), psf);

# desired dimensions of the image
desiredSizeX=340
desiredSizeY=340
desiredSizeZ=200

# original size of the image
sizeX=192
sizeY=192
sizeZ=64

iterations=200
regularizationFactor=0.002

extendedName=outputDir+outputBase+".extended.ome.tif"
extendedPsfName=outputDir+"psf.extended.ome.tif"
deconvolvedName=outputDir+outputBase+str(algorithm)+"."+str(regularizationFactor)+"."+str(iterations)+".ome.tif"
finalName=outputDir+outputBase+str(algorithm)+"."+str(regularizationFactor)+"."+str(iterations)+".final.ome.tif"

# extend the image
extended=command.run("com.truenorth.commands.dim.ExtendCommandDimension", True, "input", inputData, "dimensionX", desiredSizeX, \
		"dimensionY", desiredSizeY, "dimensionZ", desiredSizeZ, "boundaryType", "mirror", "fftType", "speed").get().getOutputs().get("output");
io.save(extended, extendedName);

# extend the psf
psfExtended=command.run("com.truenorth.commands.dim.ExtendCommandDimension", True, "input", psf, "dimensionX", desiredSizeX, \
		"dimensionY", desiredSizeY, "dimensionZ", desiredSizeZ, "boundaryType", "zero", "fftType", "speed").get().getOutputs().get("output");
io.save(psfExtended, extendedPsfName);

# if cropping required 

# call RL with total variation
deconvolved = command.run("com.truenorth.commands.fft.RichardsonLucyCommand", True, "input", extended, "psf", psfExtended, "truth", None,"firstGuess", None, "iterations", iterations, "firstGuessType", "constant", "convolutionStrategy", "circulant", "regularizationFactor", regularizationFactor, "imageWindowX", 0, "imageWindowY", 0, "imageWindowZ", 0, "psfWindowX", 0, "psfWindowY", 0, "psfWindowZ", 0).get().getOutputs().get("output");
io.save(deconvolved, deconvolvedName);

# crop back down to image window size
final = command.run("com.truenorth.commands.dim.CropCommand", True, "input", deconvolved, "xSize", sizeX, "ySize", sizeY, "zSize", sizeZ).get().getOutputs().get("output");
io.save(final, finalName);
