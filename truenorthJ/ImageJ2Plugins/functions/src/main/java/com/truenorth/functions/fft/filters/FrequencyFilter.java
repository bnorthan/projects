package com.truenorth.functions.fft.filters;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.MultiThreaded;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

/**
 * Interface for frequency filters
 * 
 * @author bnorthan
 *
 * @param <T>
 * @param <S>
 */
public interface FrequencyFilter<T extends RealType<T>, S extends RealType<S>>
	extends MultiThreaded, OutputAlgorithm<Img<T>>, Benchmark
{
	public void setKernel(RandomAccessibleInterval<S> kernel);
	
	public void setFlipKernel(boolean flipKernel);
}

