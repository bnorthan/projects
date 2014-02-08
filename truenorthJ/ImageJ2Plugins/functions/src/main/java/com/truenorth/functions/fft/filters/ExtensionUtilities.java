package com.truenorth.functions.fft.filters;

import com.truenorth.functions.StaticFunctions;
import com.truenorth.functions.fft.SimpleFFT;
import com.truenorth.functions.fft.SimpleFFTFactory;
import com.truenorth.functions.fft.SimpleImgLib2FFT;
import com.truenorth.functions.phantom.Phantoms;

import net.imglib2.Point;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.util.Util;

public class ExtensionUtilities<T extends RealType<T>> 
{
	public static enum ExtensionSize{FIXED, NOOVERLAP};
	
	long[] k;
	long[] l;
	long[] n;
	
	long[] fft_n;
	
	public ExtensionUtilities(long[] k, long[] l)
	{
		int length=k.length;
		
		this.k=new long[length];
		this.l=new long[length];
		this.n=new long[length];
		
		for (int i=0;i<length;i++)
		{
			this.k[i]=k[i];
			this.l[i]=l[i];
			this.n[i]=k[i]+l[i]-1;
			
			fft_n=SimpleFFTFactory.GetPaddedInputSizeLong(n);
		}
	}
	
	public Img<T> CreateNormalizationImage(ImgFactory<T> factory, final T type) throws IncompatibleTypeException
	{
		Img<T> normalization = factory.create(fft_n, type);
		Img<T> mask = factory.create(fft_n, type);
		
		////////////////////////////////////////////
		// TESTS
		
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
		
		ImgFactory<ComplexFloatType> fftFactory=factory.imgFactory(new ComplexFloatType());
			
		SimpleFFT<T, ComplexFloatType> fftTemp = 
				new SimpleImgLib2FFT<T, ComplexFloatType>(normalization, factory, fftFactory, new ComplexFloatType() );
/*		
		Img<ComplexFloatType> temp1FFT= fftTemp.forward(normalization);
		
		StaticFunctions.SaveImg(normalization, "/home/bnorthan/Brian2014/Projects/deconware/Images/Tests/ShellTest/Deconvolve/normalcube.tif");
		StaticFunctions.SaveImg(mask, "/home/bnorthan/Brian2014/Projects/deconware/Images/Tests/ShellTest/Deconvolve/mask.tif");
		
		// complex conjugate multiply fft of output of step 2 and fft of psf.  		
		StaticFunctions.InPlaceComplexConjugateMultiply(temp1FFT, kernelFFT);
		
		normalization = fftTemp.inverse(temp1FFT);
		StaticFunctions.InPlaceMultiply(normalization, mask);
		
		StaticFunctions.SaveImg(normalization, "/home/bnorthan/Brian2014/Projects/deconware/Images/Tests/ShellTest/Deconvolve/normalfirst.tif");
	*/	
		return null;
	}
	
	public long[] getObjectSize()
	{
		return n;
	}
	
	public long[] getFFTSize()
	{
		return fft_n;
	}
}
