package com.truenorth.commands.fft;

import imagej.data.Dataset;

import com.truenorth.commands.CommandUtilities;

import com.truenorth.commands.AbstractVolumeProcessorCommand;
import com.truenorth.functions.fft.filters.FrequencyFilter;
import com.truenorth.functions.fft.filters.AbstractFrequencyFilter;

import org.scijava.plugin.Attr;
import org.scijava.plugin.Parameter;

import net.imglib2.img.Img;
import net.imglib2.meta.ImgPlus;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import net.imglib2.RandomAccessibleInterval;

/**
 * 
 * @author bnorthan
 * base class for frequency filters
 * @param <T>
 */
public abstract class AbstractFrequencyFilterCommand<T extends RealType<T>& NativeType<T>> extends AbstractVolumeProcessorCommand<T>
{
	
	@Parameter(attrs=@Attr(name="ShowInChainedGUI", value="false"))
	protected Dataset psf;
	
	/**
	 * abstract function used to create the algorithm that will be applied
	 * @param region
	 * @return
	 */
	abstract FrequencyFilter<T,T> createAlgorithm(RandomAccessibleInterval<T> region);
	
	/**
	 * virtual function used to set parameters of the algorithm
	 * override to set algrorithm specific parameters 
	 */
	protected void setParameters(FrequencyFilter filter)
	{
		
	}
	
	@Override 
	protected void preProcess()
	{
		// Todo:  look over type safety at this step
		// use the input dataset to create an output dataset of the same dimensions
		//output=datasetService.create(imgInput.firstElement(), input.getDims(), "output", input.getAxes());
		
		ImgPlus<T> imgPlusInput=(ImgPlus<T>)(input.getImgPlus());	
		output=CommandUtilities.create(datasetService, input, imgPlusInput.getImg().firstElement());
		
		setName();
		
	}
	
	protected void setName()
	{
		if (output!=null)
		{
			output.setName("frequency filter");
		}
	}
	
	@Override
	protected Img<T> processVolume(RandomAccessibleInterval<T> volume)
	{
		// create the specific algorithm that will be applied
		FrequencyFilter<T, T> filter=createAlgorithm(volume);
		
		setParameters(filter);
		
		// process the volume
		filter.process();
		
		// and return the result
		return filter.getResult();
	}
}
