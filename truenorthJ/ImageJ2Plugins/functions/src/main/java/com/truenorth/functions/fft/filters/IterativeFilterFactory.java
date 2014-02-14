package com.truenorth.functions.fft.filters;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.numeric.ComplexType;
import net.imglib2.type.numeric.RealType;

public class IterativeFilterFactory 
{
	public static enum IterativeFilterType{RICHARDSON_LUCY, TOTAL_VARIATION_RICHARDSON_LUCY};
	
	public static <T extends RealType<T>, S extends RealType<S>> IterativeFilter<T,S> GetIterativeFilter(
			IterativeFilterType type,
			final RandomAccessibleInterval<T> image, 
			final RandomAccessibleInterval<S> kernel,
			final ImgFactory<T> imgFactory,
			final ImgFactory<S> kernelImgFactory)
	{
		try
		{
			if (type==IterativeFilterType.RICHARDSON_LUCY)
			{
				return new RichardsonLucyFilter<T,S>(image, kernel, imgFactory, kernelImgFactory);
			}
			if (type==IterativeFilterType.TOTAL_VARIATION_RICHARDSON_LUCY)
			{
				return new TotalVariationRL<T,S>(image, kernel, imgFactory, kernelImgFactory);
			}
		}
		catch (IncompatibleTypeException ex)
		{
			return null;
		}
		
		return null;
	}
}
