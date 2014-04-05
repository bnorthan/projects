package com.truenorth.functions.fft.filters;


import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.iterator.LocalizingZeroMinIntervalIterator;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.util.Util;

import com.truenorth.functions.StaticFunctions;
import com.truenorth.functions.fft.SimpleFFT;
import com.truenorth.functions.fft.filters.AbstractIterativeFilter.ConvolutionStrategy;
import com.truenorth.functions.fft.filters.AbstractIterativeFilter.FirstGuessType;

/*
import org.itk.simple.*;
import org.itk.simple.RichardsonLucyDeconvolutionImageFilter.BoundaryConditionType;


public abstract class IterativeFilterITK<T extends RealType<T>, S extends RealType<S>> extends AbstractFrequencyFilter<T,S>
	implements IterativeFilter<T, S>
{
	// ITK specific boundary strategies
	public static enum ITKBoundaryCondition{ZERO_PAD, PERIODIC_PAD, ZERO_FLUX_NEUMANN_PAD}; 
	
	public IterativeFilterITK( final RandomAccessibleInterval<T> image, final RandomAccessibleInterval<S> kernel,
			   final ImgFactory<T> imgFactory, final ImgFactory<S> kernelImgFactory,
			   final ImgFactory<ComplexFloatType> fftImgFactory )
	{
		super( image, kernel, imgFactory, kernelImgFactory, fftImgFactory );
	}
	
	public IterativeFilterITK(final RandomAccessibleInterval<T> image, 
			final RandomAccessibleInterval<S> kernel,
			final ImgFactory<T> imgFactory,
			final ImgFactory<S> kernelImgFactory) throws IncompatibleTypeException
	{
		super(image, kernel, imgFactory, kernelImgFactory);
	}
	
	public IterativeFilterITK( final Img<T> image, final Img<S> kernel, final ImgFactory<ComplexFloatType> fftImgFactory )
	{
		super( image, kernel, fftImgFactory );
	}
	
	public IterativeFilterITK( final Img< T > image, final Img< S > kernel ) throws IncompatibleTypeException
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
			
	IterativeFilterCallback<T> callback=null;
	
	boolean keepOldEstimate=false;
	
	FirstGuessType firstGuessType=FirstGuessType.MEASURED;
	ConvolutionStrategy convolutionStrategy=ConvolutionStrategy.CIRCULANT;
	
	BoundaryConditionType boundaryCondition=BoundaryConditionType.ZERO_FLUX_NEUMANN_PAD;
	
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
	
	public void setCallback(IterativeFilterCallback<T> callback)
	{
		this.callback = callback;
	}
	
	public void setEstimate(RandomAccessibleInterval<T> estimate)
	{
		System.out.println("not implemented");
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
	
	
	// set flag indicating that non-circulant convolution model is being used. 
	// @param k - measurement window size
	// @param l - psf window size
	public void setNonCirculantConvolutionStrategy(long[] k, long[] l)
	{
		System.out.println("not implemented");
	}
	
	protected Image createSimpleITKImageFromInterval(RandomAccessibleInterval interval)
	{
		int numDimensions = interval.numDimensions();
		
		long[] dimensions=new long[numDimensions];
		
		for (int i=0;i<numDimensions;i++)
		{
			dimensions[i]=interval.dimension(i);
		}
		
		Image itkImage=new Image( dimensions[0], dimensions[1], dimensions[2], org.itk.simple.PixelIDValueEnum.sitkFloat32);
				
		LocalizingZeroMinIntervalIterator i = new LocalizingZeroMinIntervalIterator(interval);
		RandomAccess<T> s = interval.randomAccess();
		
		VectorUInt32 index=new VectorUInt32(3);
		
		while (i.hasNext()) 
		{
		   i.fwd();
		   s.setPosition(i);
		   
		   index.set(0, i.getLongPosition(0));
		   index.set(1, i.getLongPosition(1));
		   index.set(2, i.getLongPosition(2));
		   
		   float pix=s.get().getRealFloat();
		   
		   itkImage.setPixelAsFloat(index, pix);
		}
		
		return itkImage;
	}
	
	protected void copySimpleITKImageToOutput(Image itkImage)
	{
		T inputType=Util.getTypeFromInterval(image);
		
		output=StaticFunctions.Create3dImage(image, this.imgFactory, inputType);
		
		LocalizingZeroMinIntervalIterator i = new LocalizingZeroMinIntervalIterator(output);
		RandomAccess<T> s = output.randomAccess();
		
		VectorUInt32 index=new VectorUInt32(3);
		
		while (i.hasNext()) 
		{
		   i.fwd();
		   s.setPosition(i);
		   
		   index.set(0, i.getLongPosition(0));
		   index.set(1, i.getLongPosition(1));
		   index.set(2, i.getLongPosition(2));
		   
		   float pix=itkImage.getPixelAsFloat(index);
		   
		   s.get().setReal(pix);
		}
	}
	
	public void setBoundaryCondition(ITKBoundaryCondition boundaryCondition)
	{
		if (boundaryCondition==ITKBoundaryCondition.PERIODIC_PAD)
		{
			this.boundaryCondition=BoundaryConditionType.PERIODIC_PAD;
		}
		else if (boundaryCondition==ITKBoundaryCondition.ZERO_PAD)
		{
			this.boundaryCondition=BoundaryConditionType.ZERO_PAD;
		}
		else if (boundaryCondition==ITKBoundaryCondition.ZERO_FLUX_NEUMANN_PAD)
		{
			this.boundaryCondition=BoundaryConditionType.ZERO_FLUX_NEUMANN_PAD;
		}
	}

}*/
