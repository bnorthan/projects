package com.truenorth.commands.noise;

import imagej.command.Command;

import org.scijava.plugin.Plugin;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import com.truenorth.functions.roi.MedianFilter;
import com.truenorth.commands.AbstractVolumeProcessorCommand;
import com.truenorth.commands.CommandUtilities;

import net.imglib2.meta.ImgPlus;

@Plugin(type=Command.class, menuPath="Plugins>Noise>Median Filter Volume")
public class MedianFilterVolumeCommand<T extends RealType<T>& NativeType<T>> extends AbstractVolumeProcessorCommand<T>
{
	long size =3;
	
	Img<T> imgInput;

	@Override 
	protected void preProcess()
	{
		// Todo:  look over type safety at this step
		//imgInput=(Img<T>)(input.getImgPlus().getImg());
		// use the input image to create an output image of the same dimensions
		ImgPlus<T> imgPlusInput=(ImgPlus<T>)(input.getImgPlus());
		
		output=CommandUtilities.create(datasetService, input, imgPlusInput.getImg().firstElement());
		
	}

	@Override
	protected Img<T> processVolume(RandomAccessibleInterval<T> volume)
	{	
		long sizeArray[] = new long[volume.numDimensions()];
		
		for (int i=0;i<volume.numDimensions();i++)
		{
			sizeArray[i]=size;
		}
	
		// create median and dif closest filters
		// Todo: need to make median filter constructer that takes RandomAccessibleInterval
		MedianFilter<T> medianFilter = new MedianFilter<T>(imgInput.factory(), imgInput.firstElement(), volume, sizeArray);
			
		medianFilter.process();
		
		return medianFilter.getResult();
		
	}
}
