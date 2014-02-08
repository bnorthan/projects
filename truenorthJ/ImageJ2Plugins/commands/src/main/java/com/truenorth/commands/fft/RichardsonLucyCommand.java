package com.truenorth.commands.fft;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.NativeType;

import net.imglib2.exception.IncompatibleTypeException;

import com.truenorth.functions.fft.filters.FrequencyFilter;
import com.truenorth.functions.fft.filters.IterativeFilter;
import com.truenorth.functions.fft.filters.IterativeFilter.FirstGuessType;
import com.truenorth.functions.fft.filters.RichardsonLucyFilter;

import com.truenorth.commands.Constants;

import imagej.command.Command;

import net.imglib2.img.Img;

import org.scijava.plugin.Plugin;

/**
 * 
 * @author bnorthan
 * Richardson Lucy filter
 * @param <T>
 */
@Plugin(type=Command.class, menuPath="Plugins>Deconvolution>Richardson Lucy")
public class RichardsonLucyCommand<T extends RealType<T> & NativeType<T>> extends IterativeFilterCommand<T>
{	
	FrequencyFilter<T,T> createAlgorithm(RandomAccessibleInterval<T> region)
	{
		try 
		{
			Img<T> inputImg=(Img<T>)(input.getImgPlus().getImg());
			Img<T> psfImg=(Img<T>)(psf.getImgPlus().getImg());
		
			// create a RichardsonLucy filter for the region
			RichardsonLucyFilter<T,T>richardsonLucy = new RichardsonLucyFilter<T,T>(region,
					psfImg, 
					inputImg.factory(), 
					psfImg.factory());
			
			// set the number of iterations and the callback
			richardsonLucy.setMaxIterations(iterations);
			richardsonLucy.setCallback(callback);
			
			if (this.firstGuessType.equals(Constants.FirstGuess.measuredImage))
			{
				richardsonLucy.setFirstGuessType(FirstGuessType.MEASURED);
			}
			else if (this.firstGuessType.equals(Constants.FirstGuess.constant))
			{
				richardsonLucy.setFirstGuessType(FirstGuessType.CONSTANT);
			}
			else if (this.firstGuessType.equals(Constants.FirstGuess.blurredInputImage))
			{
				richardsonLucy.setFirstGuessType(FirstGuessType.BLURRED_INPUT);
			}
			else if (this.firstGuessType.equals(Constants.FirstGuess.input))
			{
				if (firstGuess!=null)
				{
					// TODO: handle multi-volume first guess data set -- this code will only work
					// for the case of 1 volume.
					Img<T> firstGuessImg=(Img<T>)(firstGuess.getImgPlus().getImg());
					richardsonLucy.setFirstGuessType(FirstGuessType.INPUT_IMAGE);
					richardsonLucy.setEstimate(firstGuessImg);
				}
				else
				{
					richardsonLucy.setFirstGuessType(FirstGuessType.MEASURED);
				}	
			}
		
			// return the filter
			return richardsonLucy;
		}
		catch (IncompatibleTypeException ex)
		{
			return null;
		}
	}

}
