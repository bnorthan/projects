package com.truenorth.commands;

import imagej.data.Dataset;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

import org.scijava.plugin.Parameter;

import com.truenorth.functions.StaticFunctions;

public abstract class InputOutputVolumeProcessorCommand<T extends RealType<T> & NativeType<T>> extends HackAbstractVolumeProcessingCommand<T>
{
	
	//@Parameter(type=ItemIO.OUTPUT)
	protected Dataset output;
	
	protected abstract Img<T> createOutput(RandomAccessibleInterval<T> input);
	
	protected void processInput(RandomAccessibleInterval<T> in)
	{
		Img<T> temp=createOutput(in);
		
		RandomAccessibleInterval<T> out=extractData(output);
		
		// here we copy the result into the output dataset
		// Todo: work out a better way to do this... give the algorithm direct access to the memory 
		// so we don't need to copy?? 
		
		if (!inPlace)
		{
			StaticFunctions.copy3(temp, out);
		}
		else
		{
			StaticFunctions.copy(Views.iterable(in), Views.iterable(out));
		}
		
		/*for (Img<T> img:imageList)
		{
			arrExtractedTimePoints.add(Views.hyperSlice(img, timePosition, t));
		}*/
		
	/*	for (RandomAccessibleInterval<T> channel:arrExtractedTimePoints)
		{
			arrExtractedChannels.add(Views.hyperSlice(channel, channelPosition, c));
		}*/
		
		
	//	constructImageList();
	
	}
	

}
