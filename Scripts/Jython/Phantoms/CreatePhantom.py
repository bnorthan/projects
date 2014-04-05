# @StatusService status
# @DatasetService data
# @CommandService command
# @DisplayService display
# @IOService io

# this script creates a phantom using the model 
# from the second deconvolution grand challenge described here: 
# http://bigwww.epfl.ch/deconvolution/challenge/index.html?p=documentation/overview
#
# The object space (reality) is bigger then the image space (the portion of reality we have a picture of)
# the model creates an object in object space, convolves it with the psf, then crops it to create the image
# the dimensions of object space and image space are chosen such that wrap around does not occur in the convolution

import sys

sys.path.insert(0, '/home/bnorthan/Brian2014/Projects/deconware/code/projects/Scripts/Jython/Phantoms/')
sys.path.insert(0, '/home/bnorthan/Brian2014/Projects/deconware/code/projects/Scripts/Jython/Phantoms/Experiments')
sys.path.insert(0, '/home/bnorthan/Brian2014/Projects/deconware/code/projects/Scripts/Jython/Psfs/')

import os
import Phantoms
from Experiment import Experiment
from Spheres2 import Spheres2
from RandomPoints import RandomPoints
from Sphere import Sphere
from RandomSpheres import RandomSpheres

homeDirectory="/home/bnorthan/Brian2014/Images/General/Deconvolution/Phantoms/"

experiment=RandomSpheres(192, 192, 64, 129, 129, 127, homeDirectory)
directory=experiment.directory

if not os.path.exists(directory):
    os.makedirs(directory)

if not os.path.exists(psfDirectory):
    os.makedirs(psfDirectory)

phantomPrefix="phantom_"
phantomName=directory+phantomPrefix+".ome.tif"
extendedName=directory+phantomPrefix+".extfft.ome.tif"
extendedPsfName=psfDirectory+"psf.extfft.ome.tif"
convolvedName=psfDirectory+phantomPrefix+".conv.ome.tif"
croppedName=directory+phantomPrefix+"cropped.ome.tif"
imageNoNoiseName=psfDirectory+phantomPrefix+".image.ome.tif"
imageNoisyName=psfDirectory+phantomPrefix+".image.noisy.ome.tif"

# create the phantom

#phantom=module.getOutputs().get("output");

phantom=experiment.CreatePhantom(command);

#command.run("com.truenorth.commands.phantom.AddShellCommand", True, "xCenter", shellPositionX, "yCenter", shellPositionY, "zCenter", shellPositionZ, "radius", shellOuterRadius, "innerRadius", shellInnerRadius, "intensity", shellIntensity, "innerIntensity", innerIntensity).get();

#Phantoms.AddSphere(command, spherePositionX, spherePositionY, spherePositionZ, sphereRadius, sphereIntensity)

#Phantoms.AddPoint(command, 120, 20, 130, 100)
#Phantoms.AddRandomPointsInROI(command, 10, experiment.psfSizeX/2, experiment.psfSizeY/2, experiment.psfSizeZ/2, experiment.measurementSizeX, experiment.measurementSizeY, experiment.measurementSizeZ, 100)

#command.run("com.truenorth.commands.phantom.AddSphereCommand", True, "xCenter", spherePositionX, "yCenter", spherePositionY, "zCenter", spherePositionZ, "radius", sphereRadius, "intensity", sphereIntensity).get();

#command.run("com.truenorth.commands.phantom.AddSphereCommand", True, "xCenter", spherePosition2X, "yCenter", spherePosition2Y, "zCenter", spherePosition2Z, "radius", sphereRadius2, "intensity", sphereIntensity2).get();

io.save(phantom, phantomName);

#module=command.run("com.truenorth.commands.psf.CreatePsfCommandCosmos", True, \
#		"xSize", experiment.psfSizeX, \
#		"ySize", experiment.psfSizeY, \
#		"zSize", experiment.psfSizeZ, \
#		"fftType", "none", \
#		"scopeType", PsfParameters.scopeType, \
#		"psfModel", PsfParameters.psfModel, \
#		"xySpace", PsfParameters.xySpace, \
#		"zSpace", PsfParameters.zSpace, \
#		"emissionWavelength", PsfParameters.emissionWavelength, \
#		"numericalAperture", PsfParameters.numericalAperture, \
#		"designImmersionOilRefractiveIndex", PsfParameters.designImmersionOilRefractiveIndex, \
#		"designSpecimenLayerRefractiveIndex", PsfParameters.designSpecimenLayerRefractiveIndex, \
#		"actualImmersionOilRefractiveIndex", PsfParameters.actualImmersionOilRefractiveIndex, \
#		"actualSpecimenLayerRefractiveIndex", PsfParameters.actualSpecimenLayerRefractiveIndex, \
#		"actualPointSourceDepthInSpecimenLayer", PsfParameters.actualPointSourceDepthInSpecimenLayer, \
#		"centerPsf", True).get()

#psf=module.getOutputs().get("output");

# extend
module=command.run("com.truenorth.commands.dim.ExtendCommandDimension", True, "input", phantom, "dimensionX", experiment.objectSizeX, \
		"dimensionY", experiment.objectSizeY, "dimensionZ", experiment.objectSizeZ, "boundaryType", "boundaryZero", "fftType", "speed").get()
extended=module.getOutputs().get("output");
io.save(extended, extendedName);

# crop phantom
module=command.run("com.truenorth.commands.dim.CropCommand", True, "input", phantom, "xSize", experiment.measurementSizeX, \
		"ySize", experiment.measurementSizeY, "zSize", experiment.measurementSizeZ).get()
phantomCropped=module.getOutputs().get("output");
io.save(phantomCropped, croppedName);


# create psf
psf=psf.CreatePsf(command, "com.truenorth.commands.psf.CreatePsfCommandCosmos", \
			experiment.psfSizeX, experiment.psfSizeY, experiment.psfSizeZ)
io.save(psf, psfName);

# extend psf
module=command.run("com.truenorth.commands.dim.ExtendCommandDimension", True, "input", psf, "dimensionX", experiment.objectSizeX, \
		"dimensionY", experiment.objectSizeY, "dimensionZ", experiment.objectSizeZ, "boundaryType", "boundaryZero", "fftType", "speed").get()
extendedPsf=module.getOutputs().get("output");
io.save(extendedPsf, extendedPsfName);

# convolve
module=command.run("com.truenorth.commands.fft.ConvolutionCommand", True, "input", extended, "psf", extendedPsf).get()
convolved=module.getOutputs().get("output");
io.save(convolved, convolvedName);

# crop convolved
module=command.run("com.truenorth.commands.dim.CropCommand", True, "input", convolved, "xSize", experiment.measurementSizeX, \
		"ySize", experiment.measurementSizeY, "zSize", experiment.measurementSizeZ).get()
cropped=module.getOutputs().get("output");
io.save(cropped, imageNoNoiseName);

# add noise
module=command.run("com.truenorth.commands.noise.AddPoissonNoiseCommandGallo", True, "input", cropped).get()
noisy=module.getOutputs().get("output");
io.save(noisy, imageNoisyName);


