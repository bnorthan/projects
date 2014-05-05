package com.truenorth.commands.fft;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import net.imglib2.exception.IncompatibleTypeException;

import com.truenorth.functions.fft.filters.FrequencyFilter;
import com.truenorth.functions.fft.filters.WienerFilter;

import net.imglib2.img.Img;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * 
 * @author bnorthan
 * Regularized Inverse Filter
 * @param <T>
 */
@Plugin(type=AbstractFrequencyFilterCommand.class, menuPath="Plugins>Deconvolution>Regularized Inverse Filter")
public class RegularizedInverseFilterCommand<T extends RealType<T>& NativeType<T>> extends AbstractFrequencyFilterCommand<T>
{
	@Parameter(persist=false)
	float regularizationFactor;  
	
	/**
	 * create an regularized inverse filter algorithm to process the region
	 */
	protected FrequencyFilter<T,T> createAlgorithm(RandomAccessibleInterval<T> region)
	{
		try 
		{
			Img<T> inputImg=(Img<T>)(input.getImgPlus().getImg());
			Img<T> psfImg=(Img<T>)(psf.getImgPlus().getImg());
			
			// create an inverse filter algorithm to process the region
			WienerFilter<T,T> wiener= new WienerFilter<T,T>(region,
					psfImg, 
					inputImg.factory(), 
					psfImg.factory());
			
			wiener.setRegularizationFactor(regularizationFactor);
			
			return wiener;
		}
		catch (IncompatibleTypeException ex)
		{
			return null;
		}
	}

}
