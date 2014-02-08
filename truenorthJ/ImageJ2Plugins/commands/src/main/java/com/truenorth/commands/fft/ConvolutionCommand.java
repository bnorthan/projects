package com.truenorth.commands.fft;

import imagej.command.Command;

import org.scijava.plugin.Plugin;

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import com.truenorth.functions.fft.filters.Convolution;
import com.truenorth.functions.fft.filters.FrequencyFilter;

import net.imglib2.RandomAccessibleInterval;

/**
 * 
 * @author bnorthan
 * Convolution command
 * @param <T>
 */
@Plugin(type=Command.class, menuPath="Plugins>Deconvolution>Convolution")
public class ConvolutionCommand<T extends RealType<T>& NativeType<T>> extends AbstractFrequencyFilterCommand<T>
{	
	/**
	 * create and return a convolution algorithm to process the region
	 */
	FrequencyFilter<T,T> createAlgorithm(RandomAccessibleInterval<T> region)
	{
		try 
		{
			// get the input image and the psf image, we need these to access their image factories
			Img<T> inputImg=(Img<T>)(input.getImgPlus().getImg());
			Img<T> psfImg=(Img<T>)(psf.getImgPlus().getImg());
			
			// create a convolution algorithm to process the region
			return new Convolution<T,T>(region,
					psfImg, 
					inputImg.factory(), 
					psfImg.factory());
		}
		catch (IncompatibleTypeException ex)
		{
			return null;
		}
	}
}
