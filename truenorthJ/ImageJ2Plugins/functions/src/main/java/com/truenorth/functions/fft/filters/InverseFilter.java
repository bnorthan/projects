package com.truenorth.functions.fft.filters;

import com.truenorth.functions.StaticFunctions;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.IterableInterval;
import net.imglib2.exception.IncompatibleTypeException;

import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;

import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.view.Views;

/**
 * Inverse filter class (based on Stephan Preibisch's imglib2 FFT code)
 * 
 * @author bnorthan
 *
 * @param <T>
 * @param <S>
 */
public class InverseFilter<T extends RealType<T>, S extends RealType<S>> extends LinearFilter<T,S>
{
	// takes image and kernel and returns the result
	
	public static <T extends RealType<T>, S extends RealType<S>> Img<T> inverse(final Img<T> img, final Img<S> kernel) throws IncompatibleTypeException
	{
		final InverseFilter<T,S> inverse = new InverseFilter<T,S>(img, kernel);
		inverse.process();
		return inverse.getResult();
	}
	
	// takes image and kernel as random accessibles, and takes factories for image, kernel and fft and returns the result
	
	public static<T extends RealType<T>, S extends RealType<S>> Img<T> inverse(final RandomAccessibleInterval<T> img, final RandomAccessibleInterval<S> kernel,
			final ImgFactory<T> imgFactory, final ImgFactory<S> kernelImgFactory, final ImgFactory<ComplexFloatType> fftImgFactory)
	{
		final InverseFilter<T,S> inverse = new InverseFilter<T,S>(img, kernel, imgFactory, kernelImgFactory, fftImgFactory);
		inverse.process();
		return inverse.getResult();
	}
	
	// takes image and kernel as random accessibles, and takes factories for image, kernel and fft and the beginning and end of the ROI region and returns the result
	
	public static<T extends RealType<T>, S extends RealType<S>> Img<T> inverse(final RandomAccessibleInterval<T> img, final RandomAccessibleInterval<S> kernel,
			final ImgFactory<T> imgFactory, final ImgFactory<S> kernelImgFactory, final ImgFactory<ComplexFloatType> fftImgFactory,
			long[] begin, long[] end)
	{
		RandomAccessibleInterval< T > view =
            Views.interval( img, begin, end );
		
		final InverseFilter<T,S> inverse = new InverseFilter<T,S>(view, kernel, imgFactory, kernelImgFactory, fftImgFactory);
		inverse.process();
		return inverse.getResult();
	}

	// takes image and kernel as random accessibles, and takes factories for image, kernel and fft and the beginning and end of the ROI region	
	// and performs the inverse in place

	public static<T extends RealType<T>, S extends RealType<S>> void convolveInPlace(final RandomAccessibleInterval<T> img, final RandomAccessibleInterval<S> kernel,
			final ImgFactory<T> imgFactory, final ImgFactory<S> kernelImgFactory, final ImgFactory<ComplexFloatType> fftImgFactory,
			long[] begin, long[] end)
	{
		RandomAccessibleInterval< T > view =
            Views.interval( img, begin, end );
		
		final InverseFilter<T,S> inverse = new InverseFilter<T,S>(view, kernel, imgFactory, kernelImgFactory, fftImgFactory);
		inverse.process();
		
		RandomAccessibleInterval<T> result =inverse.getResult();
		
		IterableInterval<T> iterableTarget = Views.iterable(view);
		IterableInterval<T> iterableSource = Views.iterable(result);
		
		StaticFunctions.copy(iterableSource, iterableTarget);
		
	} 
	public InverseFilter( final RandomAccessibleInterval<T> image, final RandomAccessibleInterval<S> kernel,
			   final ImgFactory<T> imgFactory, final ImgFactory<S> kernelImgFactory,
			   final ImgFactory<ComplexFloatType> fftImgFactory )
	{
		super( image, kernel, imgFactory, kernelImgFactory, fftImgFactory );
	}
	
	public InverseFilter(final RandomAccessibleInterval<T> image, 
			final RandomAccessibleInterval<S> kernel,
			final ImgFactory<T> imgFactory,
			final ImgFactory<S> kernelImgFactory) throws IncompatibleTypeException
	{
		super(image, kernel, imgFactory, kernelImgFactory);
	}
	
	public InverseFilter( final Img<T> image, final Img<S> kernel, final ImgFactory<ComplexFloatType> fftImgFactory )
	{
		super( image, kernel, fftImgFactory );
	}
	
	public InverseFilter( final Img< T > image, final Img< S > kernel ) throws IncompatibleTypeException
	{
		super( image, kernel );
	}
	
	double threshold=0.001;
	
	/**
	 * Divide in Fourier Space
	 * 
	 * @param a
	 * @param b
	 */
	@Override
	protected void frequencyOperation( final Img< ComplexFloatType > a, final Img< ComplexFloatType > b ) 
	{
		final Cursor<ComplexFloatType> cursorA = a.cursor();
		final Cursor<ComplexFloatType> cursorB = b.cursor();
		
		ComplexFloatType zero = new ComplexFloatType();
		zero.setReal(0.0);
		zero.setImaginary(0.0);
		
		while ( cursorA.hasNext() )
		{
			cursorA.fwd();
			cursorB.fwd();
				
			ComplexFloatType conj = cursorB.get().copy();
			conj.complexConjugate();
			
			conj.mul(cursorB.get());
			
			float abs = conj.getRealFloat();
			abs = (float)java.lang.Math.sqrt(abs);
			
			// BN: Need to check over this
			if (abs>threshold)
			{
				
				cursorA.get().div( cursorB.get() );
			}
			else
			{
				cursorA.get().set(zero);
			}
			
		}
	}
	
	public void setThreshold(double threshold)
	{
		this.threshold=threshold;
	}
	
}

