# @StatusService status
# @DatasetService data
# @CommandService command
# @DisplayService display
# @IOService io

# this script creates a phantom and convolves it with a psf using the model 
# from the second deconvolution grand challenge described here: 
# http://bigwww.epfl.ch/deconvolution/challenge/index.html?p=documentation/overview

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

# parameters of the shell
shellPositionX=objectSizeX / 2
shellPositionY=objectSizeY / 2
shellPositionZ=objectSizeZ / 2
shellOuterRadius=42
shellInnerRadius=40
shellIntensity=1000
innerIntensity=100

# parameters of the sphere
spherePositionX=objectSizeX / 2
spherePositionY=objectSizeY / 2
spherePositionZ=psfSizeZ/2+3
sphereRadius=5
sphereIntensity=100

# PSF parameters
scopeType="Widefield" 
psfModel="GibsonLanni" 
xySpace=100 
zSpace=300 
emissionWavelength=500.0 
numericalAperture=1.3 
designImmersionOilRefractiveIndex=1.515 
designSpecimenLayerRefractiveIndex=1.515 
actualImmersionOilRefractiveIndex=1.515 
actualSpecimenLayerRefractiveIndex=1.51 
actualPointSourceDepthInSpecimenLayer=10 


background=0.0

directory="/home/bnorthan/Brian2014/Images/General/Deconvolution/Phantoms/ScriptTest/"

phantomPrefix="point_"
phantomName=directory+phantomPrefix+".ome.tif"
psfName=directory+"psf.ome.tif"
extendedName=directory+phantomPrefix+".extfft.ome.tif"
extendedPsfName=directory+"psf.extfft.ome.tif"
convolvedName=directory+phantomPrefix+".conv.ome.tif"
imageNoNoiseName=directory+phantomPrefix+".image.ome.tif"
imageNoisyName=directory+phantomPrefix+".image.noisy.ome.tif"

# create the phantom
themodule=command.run("com.truenorth.commands.phantom.CreatePhantomCommand", True, "xSize", objectSizeX, "ySize", objectSizeY, "zSize", objectSizeZ, "background", background).get();

phantom=themodule.getOutputs().get("output");

#command.run("com.truenorth.commands.phantom.AddShellCommand", True, "xCenter", shellPositionX, "yCenter", shellPositionY, "zCenter", shellPositionZ, "radius", shellOuterRadius, "innerRadius", shellInnerRadius, "intensity", shellIntensity, "innerIntensity", innerIntensity).get();

command.run("com.truenorth.commands.phantom.AddSphereCommand", True, "xCenter", spherePositionX, "yCenter", spherePositionY, "zCenter", spherePositionZ, "radius", sphereRadius, "intensity", sphereIntensity).get();

io.save(phantom, phantomName);

themodule=command.run("com.truenorth.commands.psf.CreatePsfCommandCosmos", True, \
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

psf=themodule.getOutputs().get("output");
io.save(psf, psfName);

themodule=command.run("com.truenorth.commands.dim.ExtendCommandDimension", True, "input", phantom, "dimensionX", objectSizeX, \
		"dimensionY", objectSizeY, "dimensionZ", objectSizeZ, "boundaryType", "boundaryZero", "fftType", "speed").get()
extended=themodule.getOutputs().get("output");
io.save(extended, extendedName);

themodule=command.run("com.truenorth.commands.dim.ExtendCommandDimension", True, "input", psf, "dimensionX", objectSizeX, \
		"dimensionY", objectSizeY, "dimensionZ", objectSizeZ, "boundaryType", "boundaryZero", "fftType", "speed").get()
extendedPsf=themodule.getOutputs().get("output");
io.save(extendedPsf, extendedPsfName);

themodule=command.run("com.truenorth.commands.fft.ConvolutionCommand", True, "input", extended, "psf", extendedPsf).get()
convolved=themodule.getOutputs().get("output");
io.save(convolved, convolvedName);

themodule=command.run("com.truenorth.commands.dim.CropCommand", True, "input", convolved, "xSize", measurementSizeX, \
		"ySize", measurementSizeY, "zSize", measurementSizeZ).get()
cropped=themodule.getOutputs().get("output");
io.save(cropped, imageNoNoiseName);

themodule=command.run("com.truenorth.commands.noise.AddPoissonNoiseCommandGallo", True, "input", cropped).get()
noisy=themodule.getOutputs().get("output");
io.save(noisy, imageNoisyName);


