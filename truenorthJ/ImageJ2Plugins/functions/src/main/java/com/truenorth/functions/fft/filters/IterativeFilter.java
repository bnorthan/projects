package com.truenorth.functions.fft.filters;

import com.truenorth.functions.StaticFunctions;

import net.imglib2.RandomAccessibleInterval;

/*import net.imglib2.algorithm.fft.FourierTransform.FFTOptimization;
import net.imglib2.algorithm.fft.FourierTransform.PreProcessing;
import net.imglib2.algorithm.fft.FourierTransform.Rearrangement;
import net.imglib2.algorithm.fft.FourierTransform;
import net.imglib2.algorithm.fft.InverseFourierTransform;*/

import com.truenorth.functions.fft.SimpleFFT;
import com.truenorth.functions.fft.SimpleFFTFactory;

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.util.Util;

/**
 * Base class for an iterative deconvolution filter
 * 
 * @author bnorthan
 *
 * @param <T>
 * @param <S>
 */
public abstract class IterativeFilter<T extends RealType<T>, S extends RealType<S>> extends FrequencyFilter<T,S>
{
	
	public IterativeFilter( final RandomAccessibleInterval<T> image, final RandomAccessibleInterval<S> kernel,
			   final ImgFactory<T> imgFactory, final ImgFactory<S> kernelImgFactory,
			   final ImgFactory<ComplexFloatType> fftImgFactory )
	{
		super( image, kernel, imgFactory, kernelImgFactory, fftImgFactory );
	}
	
	public IterativeFilter(final RandomAccessibleInterval<T> image, 
			final RandomAccessibleInterval<S> kernel,
			final ImgFactory<T> imgFactory,
			final ImgFactory<S> kernelImgFactory) throws IncompatibleTypeException
	{
		super(image, kernel, imgFactory, kernelImgFactory);
	}
	
	public IterativeFilter( final Img<T> image, final Img<S> kernel, final ImgFactory<ComplexFloatType> fftImgFactory )
	{
		super( image, kernel, fftImgFactory );
	}
	
	public IterativeFilter( final Img< T > image, final Img< S > kernel ) throws IncompatibleTypeException
	{
		super( image, kernel );
	}
	
	Img<T> estimate;
	Img<T> reblurred;
		
	Img<ComplexFloatType> estimateFFT;
	
	SimpleFFT<T, ComplexFloatType> fftEstimate;
	
	int iteration=0;
	
	int maxIterations = 10;
	
	int callbackInterval = 1;
			
	IterativeFilterCallback callback=null;
	
	boolean keepOldEstimate=false;
	
	public boolean initialize()
	{
		boolean result;
		
		// perform fft of input
		
		result = performInputFFT();
		
		if (!result)
		{
			return result;
		}
		
		// perform fft of psf
		
		result = performPsfFFT();
		
		if (!result)
		{
			return result;
		}
		
		// set first guess of the estimate
		if (estimate==null)
		{
			final T type = Util.getTypeFromInterval(image);
			estimate = imgFactory.create(image, type);
		}
		
		setEstimate(image);
		
		if (!result)
		{
			return result;
		}
					
		return true;
	}
	
	@Override
	public boolean process() 
	{
		final long startTime = System.currentTimeMillis();
		
		boolean result;
		
		initialize();
			
		result=performIterations(maxIterations);
		    
		if (result==true)
		{
			output = estimate;
		}
        
		return result;
	}
	
	public boolean performIterations(int n)
	{
		boolean result=true;
		
		while (iteration<n)
		{
			Img<T> oldEstimate=null;
			
			// if tracking stats keep track of current estimate in order to compute relative change
			if (keepOldEstimate)
			{
				oldEstimate = estimate.copy();
			}
			
			// perform the iteration
			result=performIteration(imgFFT, kernelFFT);
			
			if (result!=true)
			{
				return result;
			}
			
			
			// create reblurred image that is used to calculate the likelihood (it will also end up being ready for the 
			// next iteration).
			result = createReblurred();
			
			if (result!=true)
			{
				return result;
			}
			
			// if a callback has been set
			if (callback!=null)
			{ 
				double remainder = java.lang.Math.IEEEremainder(iteration, callbackInterval);
			
				// call the callback if the callbackInteral is a divisor of the current iteration
				if (remainder == 0)
				{
					callback.DoCallback(iteration, image, estimate, reblurred);
				}
			}
			
			output=estimate;
			
			iteration++;
			
		}
	
		return result;
	}
	
	protected boolean performEstimateFFT()
	{
		// if the estimate fft is null
		if (estimateFFT == null)
		{
			// create a new fft
			fftEstimate = SimpleFFTFactory.GetSimpleFFt(image, imgFactory, fftImgFactory, new ComplexFloatType());
		}
						
		// perform the fft
		estimateFFT = fftEstimate.forward(estimate);
		
		return true;
	}
	
	protected boolean createReblurred()
	{
		// 1.  create reblurred image by convolving current estimate with the psf
		
		// transform current estimate
		boolean result = performEstimateFFT();
				
		if (!result)
		{
			return result;
		}
				
		// complex multiply transformed current estimate with transformed psf
				
		StaticFunctions.InPlaceComplexMultiply(estimateFFT, kernelFFT);
				
		reblurred = fftEstimate.inverse(estimateFFT);
		
		return true;
	}
	
	public int getMaxIterations()
	{
		return maxIterations;
	}
	
	public void setMaxIterations(int maxIterations)
	{
		this.maxIterations = maxIterations;
	}
	
	
	public void setEstimateImg(Img<T> estimate)
	{
		this.estimate=estimate;
	}
	
	public void setCallback(IterativeFilterCallback callback)
	{
		this.callback = callback;
	}
	
	public void setEstimate(RandomAccessibleInterval<T> estimate)
	{
		StaticFunctions.copy2(estimate, this.estimate);
	
		// create reblurred (so it is ready for the first iteration)
	
		boolean result = createReblurred();
	
		if (!result)
		{
			// handle error
		}
	}
	
	public Img<T> getEstimate()
	{
		return estimate;
	}
	
	public Img<T> getReblurred()
	{
		return reblurred;
	}
	
	protected abstract boolean performIteration( final Img< ComplexFloatType > a, final Img< ComplexFloatType > b );

}
