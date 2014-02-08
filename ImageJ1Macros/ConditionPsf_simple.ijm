inputDir = "/home/bnorthan/Brian2012/Round2/Grand_Challenge/Images/final data/EvaluationData/PSFs/";
outputDir = "/home/bnorthan/Brian2012/Round2/Grand_Challenge/Images/final data/EvaluationData/PSFs_resized/";

list = getFileList(inputDir);
for (i = 0; i < list.length; i++)
        action(inputDir, outputDir, list[i]);


function action(input, output, filename) {
        open(input + filename);

        run("Canvas Size...", "width=192 height=192 position=Center zero");
	run("Make Substack...", "  slices=33-96");
	saveAs("Tif", output + filename+"_psf");
        //close();
}