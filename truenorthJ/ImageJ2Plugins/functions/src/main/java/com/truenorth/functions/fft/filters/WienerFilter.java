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
 * 
 * Wiener filter
 * 
 * @author bnorthan
 *
 * @param <T>
 * @param <S>
 */
public class WienerFilter<T extends RealType<T>, S extends RealType<S>> extends LinearFilter<T,S>
{
	// takes image and kernel and returns the result
	
	public static <T extends RealType<T>, S extends RealType<S>> Img<T> inverse(final Img<T> img, final Img<S> kernel) throws IncompatibleTypeException
	{
		final WienerFilter<T,S> inverse = new WienerFilter<T,S>(img, kernel);
		inverse.process();
		return inverse.getResult();
	}
	
	// takes image and kernel as random accessibles, and takes factories for image, kernel and fft and returns the result
	
	public static<T extends RealType<T>, S extends RealType<S>> Img<T> inverse(final RandomAccessibleInterval<T> img, final RandomAccessibleInterval<S> kernel,
			final ImgFactory<T> imgFactory, final ImgFactory<S> kernelImgFactory, final ImgFactory<ComplexFloatType> fftImgFactory)
	{
		final WienerFilter<T,S> inverse = new WienerFilter<T,S>(img, kernel, imgFactory, kernelImgFactory, fftImgFactory);
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
		
		final WienerFilter<T,S> inverse = new WienerFilter<T,S>(view, kernel, imgFactory, kernelImgFactory, fftImgFactory);
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
		
		final WienerFilter<T,S> inverse = new WienerFilter<T,S>(view, kernel, imgFactory, kernelImgFactory, fftImgFactory);
		inverse.process();
		
		RandomAccessibleInterval<T> result =inverse.getResult();
		
		IterableInterval<T> iterableTarget = Views.iterable(view);
		IterableInterval<T> iterableSource = Views.iterable(result);
		
		StaticFunctions.copy(iterableSource, iterableTarget);
		
	} 
	
	public WienerFilter( final RandomAccessibleInterval<T> image, final RandomAccessibleInterval<S> kernel,
			   final ImgFactory<T> imgFactory, final ImgFactory<S> kernelImgFactory,
			   final ImgFactory<ComplexFloatType> fftImgFactory )
	{
		super( image, kernel, imgFactory, kernelImgFactory, fftImgFactory );
	}
	
	public WienerFilter( final Img<T> image, final Img<S> kernel, final ImgFactory<ComplexFloatType> fftImgFactory )
	{
		super( image, kernel, fftImgFactory );
	}
	
	public WienerFilter( final Img< T > image, final Img< S > kernel ) throws IncompatibleTypeException
	{
		super( image, kernel );
	}
	
	double regularizationFactor=10000000000000.0;
	
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
		
		ComplexFloatType Pn= new ComplexFloatType();
		Pn.setReal(regularizationFactor);
		Pn.setImaginary(regularizationFactor);
		
		while ( cursorA.hasNext() )
		{
			cursorA.fwd();
			cursorB.fwd();
			
			ComplexFloatType conjA = cursorA.get().copy();
			conjA.complexConjugate();
			
			ComplexFloatType Pf = cursorA.get().copy();
			Pf.mul(conjA);
				
			ComplexFloatType conjB = cursorB.get().copy();
			conjB.complexConjugate();
			
			ComplexFloatType normB = cursorB.get().copy();
			normB.mul(conjB);
			
			float abs = normB.getRealFloat()+normB.getImaginaryFloat();
			abs = (float)java.lang.Math.sqrt(abs);
			
			if (abs>0.001)
			{
				ComplexFloatType ratio = Pn.copy();
				ratio.div(Pf);
				
				normB.add(Pn);
				conjB.div(normB);
				cursorA.get().mul(conjB);
			}
			else
			{
				cursorA.get().set(zero);
			}
			
		}
	}
	
	public void setRegularizationFactor(double regularizationFactor)
	{
		this.regularizationFactor=regularizationFactor;
	}
	
}

