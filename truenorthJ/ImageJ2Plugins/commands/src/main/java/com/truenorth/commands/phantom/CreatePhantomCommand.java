package com.truenorth.commands.phantom;

import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import com.truenorth.functions.StaticFunctions;

import imagej.command.Command;
import imagej.data.Dataset;
import imagej.data.DatasetService;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.planar.PlanarImgFactory;
import net.imglib2.type.numeric.real.FloatType;

import net.imglib2.meta.ImgPlus;

/**
 * 
 * A command that creates an empty phantom
 * 
 */
@Plugin(type=Command.class, menuPath="Plugins>Phantoms>Create empty phantom")
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
	
	@Parameter
	double background=0.0;
	
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
		
		// create a blank phantom
		StaticFunctions.set(image, background);
		
		// wrap as an image plus
		ImgPlus<FloatType> imgPlus=StaticFunctions.Wrap3DImg(image, "phantom");
		
		// use the image plus to create an output dataset
		output = datasetService.create(imgPlus);
	}
}
