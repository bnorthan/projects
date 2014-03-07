package com.truenorth.functions.fft.filters;


import net.imglib2.RandomAccessibleInterval;

import com.truenorth.functions.fft.filters.AbstractIterativeFilter.FirstGuessType;

import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

/**
 * Interface for an iterative deconvolution filter
 * 
 * @author bnorthan
 *
 * @param <T>
 * @param <S>
 */
public interface IterativeFilter<T extends RealType<T>, S extends RealType<S>> extends FrequencyFilter<T, S>
{
	public boolean initialize();
	
	public Img<T> getEstimate();
	
	public Img<T> getReblurred();
	
	public void setMaxIterations(int maxIterations);
	
	public int getMaxIterations();
	
	public void setEstimateImg(Img<T> estimate);
	
	public void setCallback(IterativeFilterCallback<T> callback);
	
	public void setEstimate(RandomAccessibleInterval<T> estimate);
	
	public void setFirstGuessType(FirstGuessType firstGuessType);
	
	public void setTruth(Img<T> truth);
	
	public boolean performIterations(int n);
	
	public void setNonCirculantConvolutionStrategy(long[] k, long[] l);
			
}
