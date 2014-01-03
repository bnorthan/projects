package com.truenorth.commands;

import net.imglib2.meta.ImgPlus;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import com.truenorth.commands.AbstractVolumeProcessorCommand;

public abstract class AbstractVolumeProcessorCommandInPlace <T extends RealType<T> & NativeType<T>> extends AbstractVolumeProcessorCommand<T>
{
	@Override 
	protected void preProcess()
	{
		// Todo:  look over type safety at this step
		//Img<T> imgInput=(Img<T>)(input.getImgPlus().getImg());
		// use the input dataset to create an output dataset of the same dimensions
		//output=datasetService.create(imgInput.firstElement(), input.getDims(), "output", input.getAxes());
		
		ImgPlus<T> imgPlusInput=(ImgPlus<T>)(input.getImgPlus());
		output=datasetService.create(imgPlusInput);
		
		inPlace=true;
	}

}
