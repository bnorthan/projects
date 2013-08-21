package com.truenorth.commands.dim;

import org.scijava.plugin.Parameter;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import com.truenorth.commands.AbstractVolumeProcessorCommand;
import com.truenorth.functions.StaticFunctions;

/**
 * Crops an image from it's original dimensions to xSize, ySize, and zSize
 * 
 * Todo: add in a starting point.  Right now just crops a centered region. 
 * 
 * @author bnorthan
 *
 * @param <T>
 */
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
	long[] start;
	
	/**
	 * fills an array with the new dimensions and calculates the starting point for cropping
	 */
	@Override 
	protected void preProcess()
	{
		newDimensions = new long[3];
		
		newDimensions[0]=xSize;
		newDimensions[1]=ySize;
		newDimensions[2]=zSize;
		
		dimensions = new int[input.numDimensions()];
     	start=new long[input.numDimensions()];
     	
     	for (int i=0;i<input.numDimensions();i++)
     	{
     		dimensions[i]=(int)input.dimension(i);
     		start[i]=(dimensions[i]-newDimensions[i])/2;  
     	}
     	
     	Img<T> imgInput=(Img<T>)(input.getImgPlus().getImg());
     	
     	output=datasetService.create(imgInput.firstElement(), newDimensions, "cropped", input.getAxes());
	}
	
	@Override
	protected Img<T> processVolume(RandomAccessibleInterval<T> volume)
	{
		Img<T> imgInput=(Img<T>)(input.getImgPlus().getImg());
		
		return StaticFunctions.crop(volume, imgInput.factory(), imgInput.firstElement(),  start, newDimensions);
	}
}
