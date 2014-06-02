# @StatusService status
# @DatasetService data
# @CommandService command
# @DisplayService display
# @IOService io

# this script extends and deconvolves an image using the model from the second deconvolution grand challenge
# described here: http://bigwww.epfl.ch/deconvolution/challenge/index.html?p=documentation/overview

import sys
import os
from net.imglib2.meta import Axes;

rootImageDir="/home/bnorthan/Brian2012/Round2/Images/Imgs/"

inputDir=rootImageDir
psfDir=inputDir
outputDir=inputDir
algorithm="rl"

inputName="IMG.tif"
psfName="PSF.tif"

# open and display the psf
psf=data.open(psfDir+psfName)
display.createDisplay(psf.getName(), psf);

# open and display the input image
inputData=data.open(inputDir+inputName)
display.createDisplay(inputData.getName(), inputData);	

iterations=20
#regularizationFactor=0.002

outputName=outputDir+"out"+str(algorithm)+"."+str(iterations)+".ome.tif"

# call RL
deconvolved = command.run("com.truenorth.commands.fft.RichardsonLucyCommand", True, "input", inputData, "psf", psf, "truth", None,"firstGuess", None, "iterations", iterations, "firstGuessType", "constant", "convolutionStrategy", "circulant", "regularizationFactor", 0, "imageWindowX", -1, "imageWindowY", -1, "imageWindowZ", -1, "psfWindowX", -1, "psfWindowY", -1, "psfWindowZ", -1).get().getOutputs().get("output");
io.save(deconvolved, deconvolvedName);

