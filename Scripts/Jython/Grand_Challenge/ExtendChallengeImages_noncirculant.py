# @StatusService status
# @DatasetService data
# @CommandService command
# @DisplayService display
# @IOService io

# this script extends an image using the model from the second deconvolution grand challenge
# described here: http://bigwww.epfl.ch/deconvolution/challenge/index.html?p=documentation/overview

rootImageDir="/home/bnorthan/Brian2014/Images/General/Deconvolution/Grand_Challenge/EvaluationData/"
inputDir=rootImageDir+"Measurements/"
psfDir=rootImageDir+"PSFs/"
outputDir=rootImageDir+"Extended/TestFeb19/"

baseName="2"

# set the name of the images
inputName=inputDir+baseName+".tif"
psfName=psfDir+baseName+".tif"
extendedName=outputDir+baseName+".extended.ome.tif"
extendedPsfName=outputDir+baseName+".psf.extended.ome.tif"

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

# open and display the input image
inputData=data.open(inputName)
display.createDisplay(inputData.getName(), inputData);	

# open and display the psf
psf=data.open(psfName)
display.createDisplay(psf.getName(), psf);

# extend the image
extended=command.run("com.truenorth.commands.dim.ExtendCommandDimension", True, "input", inputData, "dimensionX", objectSizeX, \
		"dimensionY", objectSizeY, "dimensionZ", objectSizeZ, "boundaryType", "zero", "fftType", "speed").get().getOutputs().get("output");
io.save(extended, extendedName);

# extend the psf
psfExtended=command.run("com.truenorth.commands.dim.ExtendCommandDimension", True, "input", psf, "dimensionX", objectSizeX, \
		"dimensionY", objectSizeY, "dimensionZ", objectSizeZ, "boundaryType", "zero", "fftType", "speed").get().getOutputs().get("output");
io.save(psfExtended, extendedPsfName);


