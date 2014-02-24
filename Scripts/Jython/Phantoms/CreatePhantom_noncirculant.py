# @StatusService status
# @DatasetService data
# @CommandService command
# @DisplayService display
# @IOService io

# this script creates a phantomn and convolves it with a psf using the model 
# from the second deconvolution grand challenge described here: 
# http://bigwww.epfl.ch/deconvolution/challenge/index.html?p=documentation/overview

import sys
sys.path.insert(0, '/home/bnorthan/Brian2014/Projects/deconware/code/projects/Scripts/Jython/Phantoms/')

import PsfParameters

# size of the measurement 
measurementSizeX=192
measurementSizeY=192
measurementSizeZ=64

# size of the psf
psfSizeX=129
psfSizeY=129
psfSizeZ=127

# size of the object space
objectSizeX=measurementSizeX+psfSizeX-1
objectSizeY=measurementSizeY+psfSizeY-1
objectSizeZ=measurementSizeZ+psfSizeZ-1

background=0.0

# parameters of the shell
shellPositionX=objectSizeX / 2
shellPositionY=objectSizeY / 2
shellPositionZ=objectSizeZ / 2
shellOuterRadius=40
shellInnerRadius=39
shellIntensity=1000

directory="/home/bnorthan/Brian2014/Images/General/Deconvolution/Phantoms/ScriptTest/"

phantomPrefix="shell"
phantomName=directory+phantomPrefix+".ome.tif"
psfName=directory+"psf.ome.tif"
extendedName=directory+phantomPrefix+".extfft.ome.tif"
extendedPsfName=directory+"psf.extfft.ome.tif"
convolvedName=directory+phantomPrefix+".conv.ome.tif"
imageNoNoiseName=directory+phantomPrefix+".image.ome.tif"
imageNoisyName=directory+phantomPrefix+".image.noisy.ome.tif"

module=command.run("com.truenorth.commands.phantom.CreatePhantomCommand", True, "xSize", objectSizeX, "ySize", objectSizeY, "zSize", objectSizeZ, "background", background).get();

phantom=module.getOutputs().get("output");

command.run("com.truenorth.commands.phantom.AddShellCommand", True, "xCenter", shellPositionX, "yCenter", shellPositionY, "zCenter", shellPositionZ, "radius", shellOuterRadius, "innerRadius", shellInnerRadius, "intensity", shellIntensity).get();

io.save(phantom, phantomName);

module=command.run("com.truenorth.commands.psf.CreatePsfCommand", True, \
		"xSize", psfSizeX, \
		"ySize", psfSizeY, \
		"zSize", psfSizeZ, \
		"fftType", "none", \
		"scopeType", PsfParameters.scopeType, \
		"psfModel", PsfParameters.psfModel, \
		"xySpace", PsfParameters.xySpace, \
		"zSpace", PsfParameters.zSpace, \
		"emissionWavelength", PsfParameters.emissionWavelength, \
		"numericalAperture", PsfParameters.numericalAperture, \
		"designImmersionOilRefractiveIndex", PsfParameters.designImmersionOilRefractiveIndex, \
		"designSpecimenLayerRefractiveIndex", PsfParameters.designSpecimenLayerRefractiveIndex, \
		"actualImmersionOilRefractiveIndex", PsfParameters.actualImmersionOilRefractiveIndex, \
		"actualSpecimenLayerRefractiveIndex", PsfParameters.actualSpecimenLayerRefractiveIndex, \
		"actualPointSourceDepthInSpecimenLayer", PsfParameters.actualPointSourceDepthInSpecimenLayer, \
		"centerPsf", True).get()

psf=module.getOutputs().get("output");
io.save(psf, psfName);

module=command.run("com.truenorth.commands.dim.ExtendCommandDimension", True, "input", phantom, "dimensionX", objectSizeX, \
		"dimensionY", objectSizeY, "dimensionZ", objectSizeZ, "boundaryType", "boundaryZero", "fftType", "speed").get()
extended=module.getOutputs().get("output");
io.save(extended, extendedName);

module=command.run("com.truenorth.commands.dim.ExtendCommandDimension", True, "input", psf, "dimensionX", objectSizeX, \
		"dimensionY", objectSizeY, "dimensionZ", objectSizeZ, "boundaryType", "boundaryZero", "fftType", "speed").get()
extendedPsf=module.getOutputs().get("output");
io.save(extendedPsf, extendedPsfName);

module=command.run("com.truenorth.commands.fft.ConvolutionCommand", True, "input", extended, "psf", extendedPsf).get()
convolved=module.getOutputs().get("output");
io.save(convolved, convolvedName);

module=command.run("com.truenorth.commands.dim.CropCommand", True, "input", convolved, "xSize", measurementSizeX, \
		"ySize", measurementSizeY, "zSize", measurementSizeZ).get()
cropped=module.getOutputs().get("output");
io.save(cropped, imageNoNoiseName);

module=command.run("com.truenorth.commands.noise.AddPoissonNoiseCommand", True, "input", cropped).get()
noisy=module.getOutputs().get("output");
io.save(noisy, imageNoisyName);


