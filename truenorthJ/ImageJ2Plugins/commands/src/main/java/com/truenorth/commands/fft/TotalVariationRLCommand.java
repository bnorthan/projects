package com.truenorth.commands.fft;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.NativeType;

import net.imglib2.exception.IncompatibleTypeException;

import com.truenorth.functions.fft.filters.IterativeFilter;
import com.truenorth.functions.fft.filters.TotalVariationRL;

import net.imglib2.img.Img;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * 
 * @author bnorthan
 * Total Variation Richardson Lucy filter
 * @param <T>
 */
@Plugin(type=AbstractFrequencyFilterCommand.class, menuPath="Plugins>Deconvolution>Total Variation Richardson Lucy")
public class TotalVariationRLCommand<T extends RealType<T> & NativeType<T>> extends IterativeFilterCommand<T>
{
	@Parameter(persist=false)
	float regularizationFactor;      
	
	protected IterativeFilter<T,T> createIterativeAlgorithm(RandomAccessibleInterval<T> region)
	{
		try 
		{
			Img<T> inputImg=(Img<T>)(input.getImgPlus().getImg());
			Img<T> psfImg=(Img<T>)(psf.getImgPlus().getImg());
		
			TotalVariationRL<T,T> totalVariationRL = new TotalVariationRL<T,T>(region,
					psfImg, 
					inputImg.factory(), 
					psfImg.factory());
			
			totalVariationRL.setRegularizationFactor(regularizationFactor);
			
			// return the filter
			return totalVariationRL;
		}
		catch (IncompatibleTypeException ex)
		{
			return null;
		}
	}
	
	@Override
	protected void setName()
	{
		if (output!=null)
		{
			String name=input.getName()+" TVRL";
			output.setName(name);
		}
	}
}
