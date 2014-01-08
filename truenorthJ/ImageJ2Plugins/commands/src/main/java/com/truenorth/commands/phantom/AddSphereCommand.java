package com.truenorth.commands.phantom;

import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import com.truenorth.functions.StaticFunctions;
import com.truenorth.functions.phantom.Phantoms;

import imagej.command.Command;
import imagej.data.Dataset;
import imagej.data.DatasetService;
import net.imglib2.Point;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.planar.PlanarImgFactory;
import net.imglib2.type.numeric.real.FloatType;

//import net.imglib2.img.ImgPlus;
import net.imglib2.meta.ImgPlus;

/**
 * A command that creates a phantom
 * 
 * Todo: Add some sophistication and perhaps expand this to multiple commands
 * (for example CreatBlankImage, then commands to place objects in the image (PutSphereCommand, PutPointCommand, etc.)
 * @author bnorthan
 *
 */
@Plugin(type=Command.class, menuPath="Plugins>Phantoms>Add Sphere")
public class AddSphereCommand  implements Command
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
	long radius=20;
	
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
			
		// draw the sphere
		Phantoms.drawSphere(image, center, (int)radius, intensity);
		
		output=input;
	}
}
