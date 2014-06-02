# @StatusService status
# @DatasetService data
# @CommandService command
# @DisplayService display
# @IOService io

import sys

jythonCurrentDir='/home/bnorthan/Brian2014/Projects/deconware/code/projects/Scripts/Jython/'
sys.path.insert(0, jythonCurrentDir+'/Psfs/')

import os
from net.imglib2.meta import Axes

# psf to use
#from PSF_NA14_DAPI_65_200_coverslip import PSF_NA14_DAPI_65_200_coverslip 
                                                                                
from PSF_NA14_CY3_65_200_coverslip import PSF_NA14_CY3_65_200_coverslip 

# experiment parameters
algorithm="rltv"
acceleration="multiplicative"
iterations=150
regularizationFactor=0.00001

rootImageDir="/home/bnorthan/Brian2014/Images/General/Deconvolution/From Kurt Thorn/MyTestsFixedCell4/ROI1"

psfSizeX=256;
psfSizeY=256;

# create output directory based on experiment parameters
outputDir=str(algorithm)+"."+acceleration+"."+str(iterations)+"."+str(regularizationFactor)+"psf"+str(psfSizeX)+"/"
outputDir=rootImageDir+outputDir

psf=PSF_NA14_CY3_65_200_coverslip(outputDir)
outputDir=psf.directory

if not os.path.exists(outputDir):
    os.makedirs(outputDir)

inputName="FixedCell_4croppedalligned.ome.tif"

#names to save various steps of the process
finalName=outputDir+"finalResult.ome.tif"

# open and display the input image
inputData=data.open(rootImageDir+inputName)
display.createDisplay(inputData.getName(), inputData);	

# size of the measurement 
inputSizeX=inputData.dimension(inputData.dimensionIndex(Axes.X));
inputSizeY=inputData.dimension(inputData.dimensionIndex(Axes.Y));
inputSizeZ=inputData.dimension(inputData.dimensionIndex(Axes.Z));
#measurementSizeZ=inputData.dimension(2);

print "input size is: "+str(inputSizeX)+" "+str(inputSizeY)+" "+str(inputSizeZ)

# extended size
extendedSizeX=inputSizeX+20;
extendedSizeY=inputSizeY+20;
extendedSizeZ=inputSizeZ+20;

psfSizeZ=extendedSizeZ

# extend the image
extended=command.run("com.truenorth.commands.dim.ExtendCommandDimension", True, "input", inputData, "dimensionX", extendedSizeX, \
		"dimensionY", extendedSizeY, "dimensionZ", extendedSizeZ, "boundaryType", "zero", "fftType", "speed").get().getOutputs().get("output");

print "extended size is: "+str(extendedSizeX)+" "+str(extendedSizeY)+" "+str(extendedSizeZ)

psf=psf.CreatePsf(command, "com.truenorth.commands.psf.CreatePsfCommandCosmos", \
		psfSizeX, psfSizeY, psfSizeZ)

#extend PSF
psfExtended=command.run("com.truenorth.commands.dim.ExtendCommandDimension", True, "input", psf, "dimensionX", extendedSizeX, \
		"dimensionY", extendedSizeY, "dimensionZ", extendedSizeZ, "boundaryType", "zero", "fftType", "speed").get().getOutputs().get("output");

#deconvolve
deconvolved = command.run("com.truenorth.commands.fft.TotalVariationRLCommand", True, "input", extended, "psf", psfExtended, "truth", None,"firstGuess", None, "iterations", iterations, "firstGuessType", "constant", "convolutionStrategy", "seminoncirculant", "regularizationFactor", regularizationFactor, "accelerationStrategy", acceleration, "imageWindowX", inputSizeX, "imageWindowY", inputSizeY, "imageWindowZ", inputSizeZ).get().getOutputs().get("output")

#crop back to original size
final = command.run("com.truenorth.commands.dim.CropCommand", True, "input", deconvolved, "xSize", inputSizeX, "ySize", inputSizeY, "zSize", inputSizeZ).get().getOutputs().get("output");
io.save(final, finalName);

		