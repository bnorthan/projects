package com.truenorth.functions.fft.filters;

import com.truenorth.functions.StaticFunctions;
import com.truenorth.functions.fft.SimpleFFT;
import com.truenorth.functions.fft.SimpleImgLib2FFT;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexFloatType;

/**
 * Implementation of Richardson Lucy algorithm
 * 
 * @author bnorthan
 * 
 * @param <T>
 * @param <S>
 */
public class RichardsonLucyFilter <T extends RealType<T>, S extends RealType<S>> extends AbstractIterativeFilter<T,S>
{
	public static <T extends RealType<T>, S extends RealType<S>> Img<T> deconvolve(final Img<T> img, final Img<S> kernel, int maxIterations, final Img<T> truth) throws IncompatibleTypeException
	{
		final RichardsonLucyFilter<T,S> rl = new RichardsonLucyFilter<T,S>(img, kernel);
		rl.setMaxIterations(maxIterations);
		rl.setTruth(truth);
		rl.process();
		return rl.getResult();
	}
	
	public static <T extends RealType<T>, S extends RealType<S>> Img<T> deconvolve(final Img<T> img, final Img<S> kernel, int maxIterations) throws IncompatibleTypeException
	{
		return deconvolve(img, kernel, maxIterations, null);
	}
	
	public RichardsonLucyFilter( final RandomAccessibleInterval<T> image, final RandomAccessibleInterval<S> kernel,
			   final ImgFactory<T> imgFactory, final ImgFactory<S> kernelImgFactory,
			   final ImgFactory<ComplexFloatType> fftImgFactory )
	{
		super( image, kernel, imgFactory, kernelImgFactory, fftImgFactory );
	}
	
	public RichardsonLucyFilter( final Img<T> image, final Img<S> kernel, final ImgFactory<ComplexFloatType> fftImgFactory )
	{
		super( image, kernel, fftImgFactory );
	}
	
	public RichardsonLucyFilter( final Img< T > image, final Img< S > kernel ) throws IncompatibleTypeException
	{	
		super( image, kernel );
	}
	
	public RichardsonLucyFilter(final RandomAccessibleInterval<T> image, 
			final RandomAccessibleInterval<S> kernel,
			final ImgFactory<T> imgFactory,
			final ImgFactory<S> kernelImgFactory) throws IncompatibleTypeException
	{
		super(image, kernel, imgFactory, kernelImgFactory);
	}
	
	public boolean initialize()
	{
		// create a new fft		
		fftInput = 
				new SimpleImgLib2FFT<T, ComplexFloatType>(image, imgFactory, fftImgFactory, new ComplexFloatType() );
					
		return super.initialize();
	}
	
	
	protected boolean performIteration( final Img< ComplexFloatType > a, final Img< ComplexFloatType > b )
	{
		// 1. Reblurred should have allready been created in previous iteration
		
		// 2.  divide observed image by reblurred
		
		long start=System.currentTimeMillis();
		
		StaticFunctions.InPlaceDivide3(reblurred, image);
		
		long total=System.currentTimeMillis()-start;
		
		System.out.println("Divide Time: "+total);
		
		start=System.currentTimeMillis();
		
		// 3. correlate psf with the output of step 2.			
		Img<T> correlation = correlationStep();
		
		total=System.currentTimeMillis()-start;
		System.out.println("Correlation Time: "+total);
		
		if (correlation==null)
		{
			return false;
		}
		
		start=System.currentTimeMillis();
		
		// multiply output of correlation step and current estimate
		ComputeEstimate(correlation);

		total=System.currentTimeMillis()-start;
		System.out.println("Compute Estimate Time: "+total);
		
		start=System.currentTimeMillis();
		
	//	StaticFunctions.SaveImg(normalization, "/home/bnorthan/Brian2014/Projects/deconware/Images/Tests/ShellTest/Deconvolve/normalization.tif");
	//	StaticFunctions.SaveImg(estimate, "/home/bnorthan/Brian2014/Projects/deconware/Images/Tests/ShellTest/Deconvolve/estimate.tif");
		
		if (this.convolutionStrategy==ConvolutionStrategy.NON_CIRCULANT)
		{
			StaticFunctions.InPlaceDivide2(normalization, estimate);
		}
		
		total=System.currentTimeMillis()-start;
		System.out.println("Divide Time: "+total);
				
		start=System.currentTimeMillis();
		
		// create reblurred so we can use it to calculate likelihood and so it is ready for next time
		createReblurred();
		
		total=System.currentTimeMillis()-start;
		System.out.println("Reblur Time: "+total);
			
		return true;
	}
	
	protected Img<T> correlationStep()
	{
		//System.out.println();
		//System.out.println("CORRELATION!");
		
		SimpleFFT<T, ComplexFloatType> fftTemp = 
				new SimpleImgLib2FFT<T, ComplexFloatType>(reblurred, imgFactory, fftImgFactory, new ComplexFloatType() );
		
		long start=System.currentTimeMillis();
		Img<ComplexFloatType> temp1FFT= fftTemp.forward(reblurred);
		long total=System.currentTimeMillis()-start;
		
		//System.out.println("Forward FFT: "+total);
	
		start=System.currentTimeMillis();
		// complex conjugate multiply fft of output of step 2 and fft of psf.  		
		StaticFunctions.InPlaceComplexConjugateMultiply(temp1FFT, kernelFFT);
		total=System.currentTimeMillis()-start;
		
		//System.out.println("Conjugate Multiply: "+total);
		
		start=System.currentTimeMillis();
		Img<T> returner=fftInput.inverse(temp1FFT);
		total=System.currentTimeMillis()-start;
		
		//System.out.println("Inverse FFT: "+total);
		
		//System.out.println();
		
		return returner;
	}
	
	protected void ComputeEstimate(Img<T> correlation)
	{
		StaticFunctions.InPlaceMultiply(estimate, correlation);
	}

}
