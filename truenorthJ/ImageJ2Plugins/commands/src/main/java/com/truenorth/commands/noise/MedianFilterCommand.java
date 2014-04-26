package com.truenorth.commands.noise;

import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import org.scijava.command.Command;
import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imglib2.img.Img;
import net.imglib2.meta.ImgPlus;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import com.truenorth.commands.CommandUtilities;
import com.truenorth.functions.StaticFunctions;
import com.truenorth.functions.roi.MedianFilter;

@Plugin(type=Command.class, menuPath="Plugins>Noise>Median Filter")
public class MedianFilterCommand<T extends RealType<T>& NativeType<T>> implements Command
{
	@Parameter
	protected DatasetService datasetService;
	
	@Parameter
	protected Dataset input;
	
	@Parameter(type = ItemIO.OUTPUT)
	protected Dataset output;
	
	long size =3;
	
	@Override
	public void run()
	{
		// Todo:  look over type safety at this step
		Img<T> imgInput=(Img<T>)(input.getImgPlus().getImg());		
		// use the input image to create an output image of the same dimensions
		
		ImgPlus<T> imgPlusInput=(ImgPlus<T>)(input.getImgPlus());
		output=CommandUtilities.create(datasetService, input, imgPlusInput.getImg().firstElement());
		
		long sizeArray[] = new long[imgInput.numDimensions()];
		
		for (int i=0;i<imgInput.numDimensions();i++)
		{
			sizeArray[i]=size;
		}
		
		// create median filter
		MedianFilter<T> medianFilter = new MedianFilter<T>(imgInput.factory(), imgInput.firstElement(), imgInput, sizeArray);
		
		medianFilter.process();
		
		// place the result in the output dataset
		output.setImgPlus(StaticFunctions.Wrap3DImg(medianFilter.getResult(), "median"));
	}
}
