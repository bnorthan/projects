# @StatusService status
# @DatasetService data
# @CommandService command
# @DisplayService display
# @IOService io

# this script extends an image using reflection

rootImageDir="/home/bnorthan/Brian2014/Images/General/Deconvolution/Grand_Challenge/EvaluationData/"
inputDir=rootImageDir+"Measurements/"
psfDir=rootImageDir+"PSFs/"
outputDir=rootImageDir+"Extended/reflection/"

baseName="2"

# set the name of the images
inputName=inputDir+baseName+".tif"
psfName=psfDir+baseName+".tif"
extendedName=outputDir+baseName+".extended.ome.tif"
extendedPsfName=outputDir+baseName+".psf.extended.ome.tif"

# desired dimensions of the image
desiredSizeX=215
desiredSizeY=215
desiredSizeZ=131

# open and display the input image
inputData=data.open(inputName)
display.createDisplay(inputData.getName(), inputData);	

# open and display the psf
psf=data.open(psfName)
display.createDisplay(psf.getName(), psf);

# extend the image
extended=command.run("com.truenorth.commands.dim.ExtendCommandDimension", True, "input", inputData, "dimensionX", desiredSizeX, \
		"dimensionY", desiredSizeY, "dimensionZ", desiredSizeZ, "boundaryType", "mirror", "fftType", "speed").get().getOutputs().get("output");
io.save(extended, extendedName);

# extend the psf
psfExtended=command.run("com.truenorth.commands.dim.ExtendCommandDimension", True, "input", psf, "dimensionX", desiredSizeX, \
		"dimensionY", desiredSizeY, "dimensionZ", desiredSizeZ, "boundaryType", "zero", "fftType", "speed").get().getOutputs().get("output");
io.save(psfExtended, extendedPsfName);

