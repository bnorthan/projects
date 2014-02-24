# @StatusService status
# @DatasetService data
# @CommandService command
# @DisplayService display
# @IOService io

# this script deconvolves an image using the model from the second deconvolution grand challenge
# described here: http://bigwww.epfl.ch/deconvolution/challenge/index.html?p=documentation/overview
#
# It is assumed the images have allready been extended.  See ExtendChallengeImages_noncirculant

rootImageDir="/home/bnorthan/Brian2014/Images/General/Deconvolution/Grand_Challenge/EvaluationData/"

inputDir=rootImageDir+"/Extended/TestFeb19/"
outputDir=rootImageDir+"/Extended/TestFeb19/"

baseName=2
inputName=inputDir+str(baseName)+".extended.ome.tif"
psfName=inputDir+str(baseName)+".psf.extended.ome.tif"

iterations=20
regularizationFactor=0.009
algorithm="rltv_tn"
deconvolvedName=str(baseName)+str(algorithm)+"."+str(regularizationFactor)+"."+str(iterations)+".ome.tif"
finalName=str(baseName)+str(algorithm)+"."+str(regularizationFactor)+"."+str(iterations)+".final.ome.tif"

# it is assumed the input images have allready been extended for fft so we need the size of the image and psf window # for the noncirculant deconvolution
# NOTE:  This version of the script is customized for the Grand Challenge contest.  Todo: Generic version which stores and reads the window size to/from meta data.
imageWindowX=192
imageWindowY=192
imageWindowZ=64

psfWindowX=129
psfWindowY=129
psfWindowZ=127

# open and display the input image
inputData=data.open(inputName)
display.createDisplay(inputData.getName(), inputData);	

# open and display the psf
psf=data.open(psfName)
display.createDisplay(psf.getName(), psf);

# call RL with total variation
deconvolved = command.run("com.truenorth.commands.fft.TotalVariationRLCommand", True, "input", inputData, "psf", psf, "truth", None,"firstGuess", None, "iterations", iterations, "firstGuessType", "constant", "convolutionStrategy", "noncirculant", "regularizationFactor", regularizationFactor, "imageWindowX", imageWindowX, "imageWindowY", imageWindowY, "imageWindowZ", imageWindowZ, "psfWindowX", psfWindowX, "psfWindowY", psfWindowY, "psfWindowZ", psfWindowZ).get().getOutputs().get("output");
io.save(deconvolved, deconvolvedName);

# crop back down to image window size
final = command.run("com.truenorth.commands.dim.CropCommand", True, "input", deconvolved, "xSize", imageWindowX, "ySize", imageWindowY, "zSize", imageWindowZ).get().getOutputs().get("output");
io.save(final, finalName);




