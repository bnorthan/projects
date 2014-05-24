package com.truenorth.commands.dim;

import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.Menu;
import org.scijava.menu.MenuConstants;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.meta.Axes;
import net.imglib2.meta.ImgPlus;
import net.imglib2.meta.AxisType;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import com.truenorth.commands.AbstractVolumeProcessorCommand;
import com.truenorth.functions.StaticFunctions;


/**
 * Crops an image from it's original dimensions to xSize, ySize, and zSize
 * 
 * TODO: add in a starting point.  Right now just crops a centered region. 
 * TODO: erase this once I get a chance to test the official imagej2 crop. 
 * 
 * @author bnorthan
 *
 * @param <T>
 */
@Plugin(type = ExtendCommand.class, menuPath = "Plugins>Dimensions>Crop")
public class CropCommand<T extends RealType<T> & NativeType<T>> extends AbstractVolumeProcessorCommand<T>
{
	@Parameter 
	int xSize;
	
	@Parameter 
	int ySize;
	
	@Parameter
	int zSize;
	
	int[] dimensions;
	long[] newDimensions;
	
	long[] volumeCroppedDimensions;
	long[] volumeCroppedStart;
	
	/**
	 * fills an array with the new dimensions and calculates the starting point for cropping
	 */
	@Override 
	protected void preProcess()
	{
		volumeCroppedDimensions = new long[3];
		
		volumeCroppedDimensions[0]=xSize;
		volumeCroppedDimensions[1]=ySize;
		volumeCroppedDimensions[2]=zSize;
		
		newDimensions=new long[input.numDimensions()];
     	volumeCroppedStart=new long[input.numDimensions()];
     	
     	AxisType[] axes=new AxisType[input.numDimensions()];
     	ImgPlus<T> imgPlusInput=(ImgPlus<T>)(input.getImgPlus());
     	
     	int v=0;
     	
     	for (int d=0;d<input.numDimensions();d++)
     	{
     		if ( (input.axis(d).type()==Axes.X) ||(input.axis(d).type()==Axes.Y)||(input.axis(d).type()==Axes.Z) ) 
			{
     			newDimensions[d]=volumeCroppedDimensions[v];
     			
				volumeCroppedStart[v]=(input.dimension(d)-volumeCroppedDimensions[v])/2;
	     		
				v++;
			}	
     		else
     		{
     			newDimensions[d]=(int)input.dimension(d);
     		}
     		
     		axes[d]=imgPlusInput.axis(d).type();
     	}
     	
     	Img<T> imgInput=(Img<T>)(imgPlusInput.getImg());  	
     	
     	output=datasetService.create(imgInput.firstElement(), newDimensions, "cropped", axes);
	}
	
	@Override
	protected Img<T> processVolume(RandomAccessibleInterval<T> volume)
	{
		Img<T> imgInput=(Img<T>)(input.getImgPlus().getImg());
		
		return StaticFunctions.crop(volume, imgInput.factory(), imgInput.firstElement(),  volumeCroppedStart, volumeCroppedDimensions);
	}
}
