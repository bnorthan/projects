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
import com.truenorth.functions.fft.SimpleImgLib2FFT;
import com.truenorth.functions.phantom.Phantoms;

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

import net.imglib2.Point;

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
	public static enum FirstGuessType{MEASURED, CONSTANT, BLURRED_INPUT, INPUT_IMAGE};
	public static enum BoundaryStrategy{NONE, NO_WRAP};

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
	
	FirstGuessType firstGuessType=FirstGuessType.MEASURED;
	BoundaryStrategy boundaryStrategy=BoundaryStrategy.NONE;
	
	// size of PSF space
	long[] k;
	long[] l;
	
	// size of measured image space
	
	Img<T> normalization=null;
	
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
		
		// set first guess of the estimate if it has not been set explicitly
		if (estimate==null)
		{
			final T type = Util.getTypeFromInterval(image);
			estimate = imgFactory.create(image, type);
			
			if (firstGuessType==FirstGuessType.MEASURED)
			{
				setEstimate(image);
			}
			else if (firstGuessType==FirstGuessType.BLURRED_INPUT)
			{
				setEstimate(image);
				
				setEstimate(reblurred);
			}
			else if (firstGuessType==FirstGuessType.CONSTANT)
			{
				
				Iterable<T> iterableImage=Views.iterable(image);
				Iterable<T> iterableEstimate=Views.iterable(estimate);
				
				final double sum=StaticFunctions.sum(iterableImage);
				
				final long numImagePixels=192*192*64;//image.dimension(0)*image.dimension(1)*image.dimension(2);
				final long numPixels=image.dimension(0)*image.dimension(1)*image.dimension(2);
				
				final double constant=0.5*sum/(numImagePixels);
				
				StaticFunctions.set(iterableEstimate, constant);
				
			/*	System.out.println("sum is: "+sum);
				System.out.println("numPixels is: "+numPixels);
				System.out.println("constant is: "+constant);
				
				final double sumEstimate1=StaticFunctions.sum(iterableEstimate);
				
				double summer=0.0;
				
				for (final T t:iterableEstimate)
				{
					t.setReal(constant);
					summer+=constant;
				}
				
				final double sumEstimate2=StaticFunctions.sum(iterableEstimate);
				
				System.out.println("summer is: "+summer);
				System.out.println("sum estimate1 is: "+sumEstimate1);
				System.out.println("sum estimate2 is: "+sumEstimate2);*/
				
				createReblurred();
				
			}

		}
		
		// create normalization if needed
		// TEMP code for testing
		{
			final T type = Util.getTypeFromInterval(image);
			normalization = imgFactory.create(image, type);
			
			Img<T> mask = imgFactory.create(image, type);
			
			////////////////////////////////////////////
			// TESTS
			
			Point size=new Point(3);
			
			size.setPosition(192, 0);
			size.setPosition(192, 1);
			size.setPosition(64, 2);
			
			Point start=new Point(3);
			
			start.setPosition(72, 0);
			start.setPosition(84, 1);
			start.setPosition(73, 2);
			
			Point maskSize=new Point(3);
			
			maskSize.setPosition(319, 0);
			maskSize.setPosition(319, 1);
			maskSize.setPosition(190, 2);
			
			Point maskStart=new Point(3);
			
			maskStart.setPosition(9, 0);
			maskStart.setPosition(21, 1);
			maskStart.setPosition(11, 2);
			
			Phantoms.drawCube(normalization, start, size, 1.0);
			Phantoms.drawCube(mask, maskStart, maskSize, 1.0);
			
			
			SimpleFFT<T, ComplexFloatType> fftTemp = 
					new SimpleImgLib2FFT<T, ComplexFloatType>(normalization, imgFactory, fftImgFactory, new ComplexFloatType() );
			
			Img<ComplexFloatType> temp1FFT= fftTemp.forward(normalization);
			
			StaticFunctions.SaveImg(normalization, "/home/bnorthan/Brian2014/Projects/deconware/Images/Tests/ShellTest/Deconvolve/normalcube.tif");
			StaticFunctions.SaveImg(mask, "/home/bnorthan/Brian2014/Projects/deconware/Images/Tests/ShellTest/Deconvolve/mask.tif");
			
			// complex conjugate multiply fft of output of step 2 and fft of psf.  		
			StaticFunctions.InPlaceComplexConjugateMultiply(temp1FFT, kernelFFT);
			
			normalization = fftTemp.inverse(temp1FFT);
			StaticFunctions.InPlaceMultiply(normalization, mask);
			
			StaticFunctions.SaveImg(normalization, "/home/bnorthan/Brian2014/Projects/deconware/Images/Tests/ShellTest/Deconvolve/normalfirst.tif");
			
		}
					
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
	

	protected void CreateNormalizationImage() throws IncompatibleTypeException
	{
		int length=k.length;
	
		long[] n=new long[length];
		long[] fft_n;
		
		for (int i=0;i<length;i++)
		{
			n[i]=k[i]+l[i]-1;	
		}
		
		fft_n=SimpleFFTFactory.GetPaddedInputSizeLong(n);
		
		final T type = Util.getTypeFromInterval(image);
		normalization = imgFactory.create(image, type);
		Img<T> mask = imgFactory.create(image, type);
			
		Point size=new Point(3);
	
		size.setPosition(k[0], 0); //192
		size.setPosition(k[1], 1); //192
		size.setPosition(k[2], 2); //64
	
		Point start=new Point(3);
	
		start.setPosition((fft_n[0]-k[0])/2, 0); //72
		start.setPosition((fft_n[1]-k[1])/2, 1); //84
		start.setPosition((fft_n[2]-k[2])/2, 2); //73
	
		Point maskSize=new Point(3);
	
		maskSize.setPosition(n[0], 0); //319
		maskSize.setPosition(n[1], 1); //319
		maskSize.setPosition(n[2], 2); //190
	
		Point maskStart=new Point(3);
	
		maskStart.setPosition((fft_n[0]-n[0])/2, 0); //9
		maskStart.setPosition((fft_n[1]-n[1])/2, 1); //21
		maskStart.setPosition((fft_n[2]-n[2])/2, 2); //11
	
		Phantoms.drawCube(normalization, start, size, 1.0);
		Phantoms.drawCube(mask, maskStart, maskSize, 1.0);
	
		SimpleFFT<T, ComplexFloatType> fftTemp = 
				new SimpleImgLib2FFT<T, ComplexFloatType>(normalization, imgFactory, fftImgFactory, new ComplexFloatType() );
		
		Img<ComplexFloatType> temp1FFT= fftTemp.forward(normalization);
		
		StaticFunctions.SaveImg(normalization, "/home/bnorthan/Brian2014/Projects/deconware/Images/Tests/ShellTest/Deconvolve/normalcube.tif");
		StaticFunctions.SaveImg(mask, "/home/bnorthan/Brian2014/Projects/deconware/Images/Tests/ShellTest/Deconvolve/mask.tif");
		
		// complex conjugate multiply fft of output of step 2 and fft of psf.  		
		StaticFunctions.InPlaceComplexConjugateMultiply(temp1FFT, kernelFFT);
		
		normalization = fftTemp.inverse(temp1FFT);
		StaticFunctions.InPlaceMultiply(normalization, mask);
		
		StaticFunctions.SaveImg(normalization, "/home/bnorthan/Brian2014/Projects/deconware/Images/Tests/ShellTest/Deconvolve/normalfirst.tif");
			
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
	
	public void setFirstGuessType(FirstGuessType firstGuessType)
	{
		this.firstGuessType=firstGuessType;
	}
	
	public void setNormalizationType(BoundaryStrategy boundaryStrategy)
	{
		this.boundaryStrategy=boundaryStrategy;
	}
	
	public void setNormalization(Img<T> normalization)
	{
		this.normalization=normalization;
	}
	
	public void setUpForNoWrap(long[] k, long[] l)
	{
		this.k=k;
		this.l=l;
		
		this.boundaryStrategy=BoundaryStrategy.NO_WRAP;
	}
		
	protected abstract boolean performIteration( final Img< ComplexFloatType > a, final Img< ComplexFloatType > b );

}
