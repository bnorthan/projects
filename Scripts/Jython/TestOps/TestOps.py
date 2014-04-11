# @StatusService status
# @DatasetService data
# @CommandService command
# @DisplayService display
# @IOService io
# @OpService ops

from net.imglib2.meta import ImgPlus
from net.imglib2.meta import Axes;

rootImageDir="/home/bnorthan/Brian2014/Images/General/Deconvolution/"

inputDir=rootImageDir+"/Phantoms/RandomSpheres/PSF_UltraLowNA/"

inputName="phantom_.image.ome.tif"
psfName="psf.ome.tif"

# open and display the input image
inputData=data.open(inputDir+inputName)
display.createDisplay(inputData.getName(), inputData);	

# open and display the psf
psf=data.open(inputDir+psfName)
display.createDisplay(psf.getName(), psf);

# size of the measurement 
measurementSizeX=inputData.dimension(inputData.dimensionIndex(Axes.X));
measurementSizeY=inputData.dimension(inputData.dimensionIndex(Axes.Y));
measurementSizeZ=inputData.dimension(2);

# size of the psf
psfSizeX=psf.dimension(psf.dimensionIndex(Axes.X));
psfSizeY=psf.dimension(psf.dimensionIndex(Axes.Y));
#psfSizeZ=psf.dimension(psf.dimensionIndex(Axes.Z));
psfSizeZ=psf.dimension(2);

dims=[measurementSizeX+psfSizeX+100, measurementSizeY+psfSizeY+100, measurementSizeZ+psfSizeZ+100]

out=ops.create(dims, inputData.getImgPlus().getImg().firstElement())

print out.dimension(0)
print out.dimension(1)
print out.dimension(2)

convolved=ops.run("Convolution", inputData.getImgPlus(), psf.getImgPlus())

display.createDisplay("Convolved", convolved)

dataConvolved=data.create(ImgPlus(convolved))

display.createDisplay("Convolved", dataConvolved)

#out = inputData.getImgPlus().getImg().copy();

ops.convolve(inputData.getImgPlus().getImg(), psf.getImgPlus().getImg(), out)

#out=convolved=ops.run("convolve",  inputData.getImgPlus().getImg(), psf.getImgPlus().getImg(), out)

#seven=ops.add(2,15)
#print("result is: "+str(seven))

#narf=ops.run("narf", "Put some trousers on")

#print("the narf said: "+narf)

#ops.run("RichardsonLucy", None, None)
#ops.narf("I said put some pants on")


#ops.RichardsonLucy(None, None)
