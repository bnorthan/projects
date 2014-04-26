# @StatusService status
# @DatasetService data
# @CommandService command
# @DisplayService display
# @IOService io

# this script deconvolves an image.
#
# It is assumed the images have allready been extended.  See ExtendChallengeImages_reflection

rootImageDir="/home/bnorthan/Brian2014/Images/General/Deconvolution/Grand_Challenge/EvaluationData/"

inputDir=rootImageDir+"/Extended/reflection/"
outputDir=rootImageDir+"/Extended/reflection/"

baseName=2
inputName=inputDir+str(baseName)+".extended.ome.tif"
psfName=inputDir+str(baseName)+".psf.extended.ome.tif"

imageWindowX=192
imageWindowY=192
imageWindowZ=64

iterations=200
regularizationFactor=0.0005
algorithm="rltv_tn"
deconvolvedName=str(baseName)+str(algorithm)+"."+str(regularizationFactor)+"."+str(iterations)+".ome.tif"
finalName=str(baseName)+str(algorithm)+"."+str(regularizationFactor)+"."+str(iterations)+".final.ome.tif"

# open and display the input image
inputData=data.open(inputName)
display.createDisplay(inputData.getName(), inputData);	

# open and display the psf
psf=data.open(psfName)
display.createDisplay(psf.getName(), psf);

# call RL with total variation
deconvolved = command.run("com.truenorth.commands.fft.TotalVariationRLCommand", True, "input", inputData, "psf", psf, "truth", None,"firstGuess", None, "iterations", iterations, "firstGuessType", "constant", "convolutionStrategy", "circulant", "regularizationFactor", regularizationFactor, "imageWindowX", 0, "imageWindowY", 0, "imageWindowZ", 0, "psfWindowX", 0, "psfWindowY", 0, "psfWindowZ", 0).get().getOutputs().get("output");
io.save(deconvolved, outputDir+deconvolvedName);

# crop back down to image window size
final = command.run("com.truenorth.commands.dim.CropCommand", True, "input", deconvolved, "xSize", imageWindowX, "ySize", imageWindowY, "zSize", imageWindowZ).get().getOutputs().get("output");
io.save(final, outputDir+finalName);




