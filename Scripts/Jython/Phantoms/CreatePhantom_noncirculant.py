# @StatusService status
# @DatasetService data
# @CommandService command
# @DisplayService display
# @IOService io

# this script creates a phantom and convolves it with a psf using the model 
# from the second deconvolution grand challenge described here: 
# http://bigwww.epfl.ch/deconvolution/challenge/index.html?p=documentation/overview

import sys

jythonCurrentDir='/home/bnorthan/Brian2014/Projects/deconware/code/projects/Scripts/Jython/'

# TODO: relative paths!!  I'm waiting until I start running this in ImageJ2 Script editor and figure out how the relative paths will
#  be organized from there. 
sys.path.insert(0, jythonCurrentDir+'/Phantoms/')
sys.path.insert(0, jythonCurrentDir+'/Phantoms/Experiments')
sys.path.insert(0, jythonCurrentDir+'/Psfs/')

import os
import PsfParameters
import Phantoms
from Experiment import Experiment
from Spheres2 import Spheres2
from RandomPoints import RandomPoints
from Sphere import Sphere
from RandomSpheres import RandomSpheres
from RandomSpheresZRatio import RandomSpheresZRatio

from PSFExample import PSFExample
from PSFUltraLowNA import PSFUltraLowNA
from PSF_NA14_DAPI_65_200_coverslip import PSF_NA14_DAPI_65_200_coverslip 
from PSFAberrated import PSFAberrated

homeDirectory="/home/bnorthan/Brian2014/Images/General/Deconvolution/Phantoms/"

experiment=RandomSpheresZRatio(256, 256, 50, 129, 129, 101, homeDirectory)
directory=experiment.directory

psf=PSF_NA14_DAPI_65_200_coverslip(directory)
psfDirectory=psf.directory

print directory

if not os.path.exists(directory):
	os.makedirs(directory)

if not os.path.exists(psfDirectory):
	os.makedirs(psfDirectory)

phantomPrefix="phantom_"
phantomName=directory+phantomPrefix+".ome.tif"
psfName=psfDirectory+"psf.ome.tif"
extendedName=directory+phantomPrefix+".extfft.ome.tif"
extendedPsfName=psfDirectory+"psf.extfft.ome.tif"
convolvedName=psfDirectory+phantomPrefix+".conv.ome.tif"
croppedName=directory+phantomPrefix+"cropped.ome.tif"
imageNoNoiseName=psfDirectory+phantomPrefix+".image.ome.tif"
imageNoisyName=psfDirectory+phantomPrefix+".image.noisy.ome.tif"

# create the phantom

# first check and see if the phantom exists.  If it does load it otherwise create it

if not os.path.isfile(phantomName):
	phantom=experiment.CreatePhantom(command);
	io.save(phantom, phantomName);
else:
	phantom=data.open(phantomName)
	display.createDisplay(phantom.getName(), phantom);	
	
#command.run("com.truenorth.commands.phantom.AddShellCommand", True, "xCenter", shellPositionX, "yCenter", shellPositionY, "zCenter", shellPositionZ, "radius", shellOuterRadius, "innerRadius", shellInnerRadius, "intensity", shellIntensity, "innerIntensity", innerIntensity).get();

# extend the phantom for fft
module=command.run("com.truenorth.commands.dim.ExtendCommandDimension", True, "input", phantom, "dimensionX", experiment.objectSizeX, \
		"dimensionY", experiment.objectSizeY, "dimensionZ", experiment.objectSizeZ, "boundaryType", "boundaryZero", "fftType", "speed").get()
extended=module.getOutputs().get("output");
io.save(extended, extendedName);

# crop the phantom down to the image size (used to calculate error)
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
module=command.run("com.truenorth.commands.noise.AddPoissonNoiseCommandPreibisch", True, "input", cropped, "snr", 2.24).get()
noisy=module.getOutputs().get("output");
io.save(noisy, imageNoisyName);


