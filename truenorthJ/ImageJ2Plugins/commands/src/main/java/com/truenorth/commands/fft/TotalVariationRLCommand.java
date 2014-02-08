package com.truenorth.commands.fft;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.NativeType;

import net.imglib2.exception.IncompatibleTypeException;

import com.truenorth.commands.Constants;
import com.truenorth.functions.fft.filters.FrequencyFilter;
import com.truenorth.functions.fft.filters.TotalVariationRL;
import com.truenorth.functions.fft.filters.IterativeFilter.FirstGuessType;

import imagej.command.Command;

import net.imglib2.img.Img;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * 
 * @author bnorthan
 * Total Variation Richardson Lucy filter
 * @param <T>
 */
@Plugin(type=Command.class, menuPath="Plugins>Deconvolution>Total Variation Richardson Lucy")
public class TotalVariationRLCommand<T extends RealType<T> & NativeType<T>> extends IterativeFilterCommand<T>
{
	@Parameter(persist=false)
	float regularizationFactor;      
	
	FrequencyFilter<T,T> createAlgorithm(RandomAccessibleInterval<T> region)
	{
		try 
		{
			Img<T> inputImg=(Img<T>)(input.getImgPlus().getImg());
			Img<T> psfImg=(Img<T>)(psf.getImgPlus().getImg());
		
			TotalVariationRL<T,T> totalVariationRL = new TotalVariationRL<T,T>(region,
					psfImg, 
					inputImg.factory(), 
					psfImg.factory());
			
			// set the number of iterations and the callback
			totalVariationRL.setMaxIterations(iterations);
			totalVariationRL.setCallback(callback);
			totalVariationRL.setRegularizationFactor(regularizationFactor);
			
			if (this.firstGuessType.equals(Constants.FirstGuess.measuredImage))
			{
				totalVariationRL.setFirstGuessType(FirstGuessType.MEASURED);
			}
			else if (this.firstGuessType.equals(Constants.FirstGuess.constant))
			{
				totalVariationRL.setFirstGuessType(FirstGuessType.CONSTANT);
			}
			else if (this.firstGuessType.equals(Constants.FirstGuess.blurredInputImage))
			{
				totalVariationRL.setFirstGuessType(FirstGuessType.BLURRED_INPUT);
			}
			else if (this.firstGuessType.equals(Constants.FirstGuess.input))
			{
				if (firstGuess!=null)
				{
					// TODO: handle multi-volume first guess data set -- this code will only work
					// for the case of 1 volume.
					Img<T> firstGuessImg=(Img<T>)(firstGuess.getImgPlus().getImg());
					totalVariationRL.setFirstGuessType(FirstGuessType.INPUT_IMAGE);
					totalVariationRL.setEstimate(firstGuessImg);
					
				}
				else
				{
					totalVariationRL.setFirstGuessType(FirstGuessType.MEASURED);
				}
			}
			
			
			// return the filter
			return totalVariationRL;
		}
		catch (IncompatibleTypeException ex)
		{
			return null;
		}
	}
}
