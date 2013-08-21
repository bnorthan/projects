package com.truenorth.commands.phantom;

import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;

import com.truenorth.functions.StaticFunctions;
import com.truenorth.functions.phantom.Phantoms;

import imagej.command.Command;
import imagej.data.Dataset;
import imagej.data.DatasetService;
import net.imglib2.Point;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.planar.PlanarImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

import net.imglib2.img.ImgPlus;

/**
 * A simple command that creates a phantom
 * 
 * Todo: Add some sophistication and perhaps expand this to multiple commands
 * (for example CreatBlankImage, then commands to place objects in the image (PutSphereCommand, PutPointCommand, etc.)
 * @author bnorthan
 *
 */
public class CreatePhantomCommand  implements Command
{
	@Parameter
	protected DatasetService datasetService;
	
	@Parameter(type = ItemIO.OUTPUT)
	protected Dataset output;
	
	@Parameter
	long xSize=256;
	
	@Parameter
	long ySize=256;
	
	@Parameter
	long zSize=128;
	
	@Override
	public void run()
	{
		long[] size = new long[3];
		
		size[0]=xSize;
		size[1]=ySize;
		size[2]=zSize;
		
		// create a planer image factory
		ImgFactory<FloatType> imgFactory = new PlanarImgFactory<FloatType>();
							
		// use the image factory to create an img
		Img<FloatType> image = imgFactory.create(size, new FloatType());
		
		StaticFunctions.set(image, 0);
		
		// create a center position
		Point center = new Point(image.numDimensions());
		
		center.setPosition(xSize/2,0);
		center.setPosition(ySize/2,1);
		center.setPosition(zSize/2, 2);
		
		// draw a sphere in the center of the image
	//	Phantoms.drawSphere(image, center, 50, 155);
		
		// draw another sphere inside the first sphere
		Phantoms.drawSphere(image, center, 25, 255);
		
		// wrap as an image plus
		ImgPlus<FloatType> imgPlus=StaticFunctions.Wrap3DImg(image, "phantom");
		
		// use the image plus to create an output dataset
		output = datasetService.create(imgPlus);
	}

}
