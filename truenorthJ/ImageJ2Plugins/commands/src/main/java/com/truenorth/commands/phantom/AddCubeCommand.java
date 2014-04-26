package com.truenorth.commands.phantom;

import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import com.truenorth.functions.phantom.Phantoms;

import org.scijava.command.Command;
import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imglib2.Point;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.real.FloatType;

/**
 * A command that adds a cube to an image
 */
@Plugin(type=Command.class, menuPath="Plugins>Phantoms>Add Cube")
public class AddCubeCommand  implements Command
{
	@Parameter
	protected DatasetService datasetService;
	
	@Parameter(type = ItemIO.INPUT)
	protected Dataset input;
	
	@Parameter(type = ItemIO.OUTPUT)
	protected Dataset output;
	
	// start of cube
	@Parameter
	long xStart=-1;
	
	@Parameter
	long yStart=-1;
	
	@Parameter
	long zStart=-1;
	
	// size of cube
	@Parameter
	long xSize=-1;
		
	@Parameter
	long ySize=-1;
		
	@Parameter
	long zSize=-1;
		
	@Parameter
	double intensity=255;
	
	@Override
	public void run()
	{	
		Img<FloatType> image=(Img<FloatType>)(input.getImgPlus().getImg());
		
		// create a start position
		Point start = new Point(image.numDimensions());
		
		start.setPosition(xStart,0);
		start.setPosition(yStart,1);
		start.setPosition(zStart, 2);
		
		// create a size position
		Point size = new Point(image.numDimensions());
		
		size.setPosition(xSize,0);
		size.setPosition(ySize,1);
		size.setPosition(zSize, 2);
		
		// draw the sphere
		Phantoms.drawCube(image, start, size, intensity);
		
		output=input;
	}
}
