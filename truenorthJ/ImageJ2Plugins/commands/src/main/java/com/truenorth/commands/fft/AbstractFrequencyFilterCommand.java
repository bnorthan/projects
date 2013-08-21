package com.truenorth.commands.fft;

import imagej.data.Dataset;

import com.truenorth.commands.AbstractVolumeProcessorCommand;
import com.truenorth.functions.StaticFunctions;
import com.truenorth.functions.fft.filters.FrequencyFilter;

import org.scijava.plugin.Parameter;

import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import net.imglib2.RandomAccessibleInterval;

import com.truenorth.commands.AbstractVolumeProcessorCommand;

/**
 * 
 * @author bnorthan
 * base class for frequency filters
 * @param <T>
 */
public abstract class AbstractFrequencyFilterCommand<T extends RealType<T>& NativeType<T>> extends AbstractVolumeProcessorCommand<T>
{
	
	@Parameter
	protected Dataset psf;
	
	/**
	 * abstract function used to create the algorithm that will be applied
	 * @param region
	 * @return
	 */
	abstract FrequencyFilter<T,T> createAlgorithm(RandomAccessibleInterval<T> region);

	@Override 
	protected void preProcess()
	{
		// Todo:  look over type safety at this step
		Img<T> imgInput=(Img<T>)(input.getImgPlus().getImg());
	
		// use the input dataset to create an output dataset of the same dimensions
		output=datasetService.create(imgInput.firstElement(), input.getDims(), "output", input.getAxes());
	}
	
	@Override
	protected Img<T> processVolume(RandomAccessibleInterval<T> volume)
	{
		// create the specific algorithm that will be applied
		FrequencyFilter filter=createAlgorithm(volume);
		
		// process the volume
		filter.process();
		
		// and return the result
		return filter.getResult();
	}
}
