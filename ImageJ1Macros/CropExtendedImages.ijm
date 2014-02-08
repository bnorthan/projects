inputDir="/home/bnorthan/Brian2014/Images/General/Deconvolution/Grand_Challenge/EvaluationData/Extended/temp/";
outputDir = "/home/bnorthan/Brian2014/Images/General/Deconvolution/Grand_Challenge/EvaluationData/Extended/temp/";

base="1";
inputExperiment="extended.rltv.0001.1000";
outputExperiment="rltv.0001.1000";
extension="ome.tif";

input=inputDir+base+"."+inputExperiment+"."+extension;
output=outputDir+base+"."+outputExperiment+"."+extension;

open(input);

makeRectangle(25, 25, 192, 192);
run("Crop");
run("Make Substack...", "  slices=41-104");

saveAs("Tiff", output);