package com.truenorth.commands.phantom;

import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import com.truenorth.functions.phantom.Phantoms;

import imagej.command.Command;
import imagej.data.Dataset;
import imagej.data.DatasetService;
import net.imglib2.Point;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.real.FloatType;

/**
 * A command that adds a sphere to an image
 */
@Plugin(type=Command.class, menuPath="Plugins>Phantoms>Add Asymetric Sphere")
public class AddAsymetricSphereCommand  implements Command
{
	@Parameter
	protected DatasetService datasetService;
	
	@Parameter(type = ItemIO.INPUT)
	protected Dataset input;
	
	@Parameter(type = ItemIO.OUTPUT)
	protected Dataset output;
	
	// center of sphere
	@Parameter
	long xCenter=-1;
	
	@Parameter
	long yCenter=-1;
	
	@Parameter
	long zCenter=-1;
	
	@Parameter
	long xRadius=20;
	
	@Parameter
	long yRadius=20;
	
	@Parameter
	long zRadius=10;
	
	@Parameter
	double intensity=255;
	
	@Override
	public void run()
	{	
		Img<FloatType> image=(Img<FloatType>)(input.getImgPlus().getImg());
		
		// create a center position
		Point center = new Point(image.numDimensions());
		
		center.setPosition(xCenter,0);
		center.setPosition(yCenter,1);
		center.setPosition(zCenter, 2);
		
		long[] radius=new long[3];
		
		radius[0]=xRadius;
		radius[1]=yRadius;
		radius[2]=zRadius;
			
		// draw the sphere
		Phantoms.drawAsymetricSphere(image, center, radius, intensity);
		
		output=input;
	}
}

