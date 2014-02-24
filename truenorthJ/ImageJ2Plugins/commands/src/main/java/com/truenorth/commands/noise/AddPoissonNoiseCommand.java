package com.truenorth.commands.noise;

import imagej.command.Command;

import org.scijava.plugin.Plugin;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.meta.ImgPlus;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

import com.truenorth.commands.AbstractVolumeProcessorCommand;

import com.truenorth.functions.noise.NoiseGenerator;

@Plugin(type=Command.class, menuPath="Plugins>Noise>Add Poisson Noise")
public class AddPoissonNoiseCommand<T extends RealType<T>& NativeType<T>> extends AbstractVolumeProcessorCommand<T>
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
		output.setName("Poisson Noise");
		inPlace=true;
	}
	
	@Override
	protected Img<T> processVolume(RandomAccessibleInterval<T> volume)
	{
		NoiseGenerator.AddPoissonNoise(Views.iterable(volume));
				
		return null;
	}
}
