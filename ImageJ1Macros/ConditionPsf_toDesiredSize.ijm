
inputDir = "/home/bnorthan/Brian2012/Round2/Grand_Challenge/Images/EvaluationData/PSFs/";
//inputDir = "/home/bnorthan/Brian2012/Round2/Grand_Challenge/Images/final data/EvaluationData/PSFs/";
outputDir = "/home/bnorthan/Brian2012/Round2/Grand_Challenge/Images/EvaluationData/Extended/temp/";

desiredX=192;
desiredY=192;
desiredZ=127;

currentZ=127;

base="2";
extension=".tif";
inputName=base+extension;
outputName=base+"_"+desiredX+"_"+desiredZ+extension;


list = getFileList(inputDir);
for (i = 0; i < list.length; i++)
        action(inputDir, outputDir, list[i]);

function action(inputDir, outputDir, inputName)
{
	open(inputDir+inputName);

	run("Canvas Size...", "width="+desiredX+" height="+desiredY+" position=Center zero");

	padding=(desiredZ-currentZ)/2;
	paddingBottom=floor(padding);
	paddingTop=round(padding);

	if (padding>0)
	{
		newImage("paddBottom", "32-bit black", desiredX, desiredY, paddingBottom);
		newImage("paddTop", "32-bit black", desiredX, desiredY, paddingTop);
	
		run("Concatenate...", "  title=[psf] image1=paddBottom image2="+inputName+" image3=paddTop");
	}

	// normalize
	Stack.getStatistics(count, mean, min, max, std);
	sum=count*mean;
	print("sum: "+sum);

	run("Multiply...", "value="+1.0/sum+" stack");
	Stack.getStatistics(count, mean, min, max, std);
	sum=count*mean;
	print("normalized sum"+sum);

	saveAs("Tif", outputDir + "psf_"+inputName);
}
