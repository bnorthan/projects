package com.truenorth.commands.dim;

import org.scijava.command.Command;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.Menu;
import org.scijava.menu.MenuConstants;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
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
@Plugin(type=Command.class, menu = {
    @Menu(label = MenuConstants.PLUGINS_LABEL,
            weight = MenuConstants.PLUGINS_WEIGHT,
            mnemonic = MenuConstants.PLUGINS_MNEMONIC),
        @Menu(label = "Truenorth", mnemonic = 't'), @Menu(label = "Crop354", weight = 1) })
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
     	
     	AxisType[] axes=new AxisType[input.numDimensions()];
     	ImgPlus<T> imgPlusInput=(ImgPlus<T>)(input.getImgPlus());
     	
     	for (int i=0;i<input.numDimensions();i++)
     	{
     		dimensions[i]=(int)input.dimension(i);
     		start[i]=(dimensions[i]-newDimensions[i])/2;
     		
     		axes[i]=imgPlusInput.axis(i).type();
     	}
     	
     	Img<T> imgInput=(Img<T>)(imgPlusInput.getImg());
     	
     	
     	
     	output=datasetService.create(imgInput.firstElement(), newDimensions, "cropped", axes);
	}
	
	@Override
	protected Img<T> processVolume(RandomAccessibleInterval<T> volume)
	{
		Img<T> imgInput=(Img<T>)(input.getImgPlus().getImg());
		
		return StaticFunctions.crop(volume, imgInput.factory(), imgInput.firstElement(),  start, newDimensions);
	}
}
