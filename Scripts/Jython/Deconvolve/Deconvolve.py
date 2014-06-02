# @StatusService status
# @DatasetService data
# @CommandService command
# @DisplayService display
# @IOService io

# this script extends and deconvolves an image using the model from the second deconvolution grand challenge
# described here: http://bigwww.epfl.ch/deconvolution/challenge/index.html?p=documentation/overview

import sys

jythonCurrentDir='/home/bnorthan/Brian2014/Projects/deconware/code/projects/Scripts/Jython/'
sys.path.insert(0, jythonCurrentDir+'/Psfs/')

import os
from net.imglib2.meta import Axes;

#from PSF_NA14_DAPI_65_200_coverslip import PSF_NA14_DAPI_65_200_coverslip 

rootImageDir="/home/bnorthan/Brian2014/Images/General/Deconvolution/From Ferreol/MyTests/"

inputDir=rootImageDir
psfDir=rootImageDir
outputDir=rootImageDir
algorithm="rltv_acc_snc"

if not os.path.exists(outputDir):
    os.makedirs(outputDir)

inputName="FluoCells3_1_Cropped.extended.tif"
psfName="psf.extended.tif"
finalName=outputDir+"final.tif"
deconvolvedName=outputDir+"deconvolved.tif"

# open and display the psf
psf=data.open(psfDir+psfName)
display.createDisplay(psf.getName(), psf);

# open and display the input image
inputData=data.open(inputDir+inputName)
display.createDisplay(inputData.getName(), inputData);	

# size of the measurement 
measurementSizeX=799;
measurementSizeY=799;
#measurementSizeZ=inputData.dimension(inputData.dimensionIndex(Axes.Z));
measurementSizeZ=132;

iterations=5
regularizationFactor=0.002

# call RL
deconvolved = command.run("com.truenorth.commands.fft.RichardsonLucyCommand", True, "input", inputData, "psf", psf, "truth", None,"firstGuess", None, "iterations", iterations, "firstGuessType", "constant", "convolutionStrategy", "seminoncirculant", "regularizationFactor", regularizationFactor, "acceleration", "multiplicative","imageWindowX", measurementSizeX, "imageWindowY", measurementSizeY, "imageWindowZ", measurementSizeZ, "psfWindowX",0,"psfWindowY",0,"psfWindowZ",0).get().getOutputs().get("output")

io.save(deconvolved, deconvolvedName);

# crop back down to image window size
final = command.run("com.truenorth.commands.dim.CropCommand", True, "input", deconvolved, "xSize", measurementSizeX, "ySize", measurementSizeY, "zSize", measurementSizeZ).get().getOutputs().get("output");

io.save(final, finalName);

