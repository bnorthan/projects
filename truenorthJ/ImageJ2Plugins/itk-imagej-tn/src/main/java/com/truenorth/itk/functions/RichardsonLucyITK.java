package com.truenorth.itk.functions;

//TODO solve dependency problems and reconnect... or move to it's own project.

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexFloatType;

import org.itk.simple.*;

import com.truenorth.itk.ItkImagejUtilities;

public class RichardsonLucyITK <T extends RealType<T>, S extends RealType<S>> extends IterativeFilterITK<T,S>
{
	org.itk.simple.RichardsonLucyDeconvolutionImageFilter itkRL;
	
	public static <T extends RealType<T>, S extends RealType<S>> Img<T> deconvolve(final Img<T> img, final Img<S> kernel, int maxIterations, final Img<T> truth) throws IncompatibleTypeException
	{
		final RichardsonLucyITK<T,S> rl = new RichardsonLucyITK<T,S>(img, kernel);
		rl.setMaxIterations(maxIterations);
		rl.setTruth(truth);
		rl.process();
		return rl.getResult();
	}
	
	public static <T extends RealType<T>, S extends RealType<S>> Img<T> deconvolve(final Img<T> img, final Img<S> kernel, int maxIterations) throws IncompatibleTypeException
	{
		return deconvolve(img, kernel, maxIterations, null);
	}
	
	public RichardsonLucyITK( final RandomAccessibleInterval<T> image, final RandomAccessibleInterval<S> kernel,
			   final ImgFactory<T> imgFactory, final ImgFactory<S> kernelImgFactory,
			   final ImgFactory<ComplexFloatType> fftImgFactory )
	{
		super( image, kernel, imgFactory, kernelImgFactory, fftImgFactory );
	}
	
	public RichardsonLucyITK( final Img<T> image, final Img<S> kernel, final ImgFactory<ComplexFloatType> fftImgFactory )
	{
		super( image, kernel, fftImgFactory );
	}
	
	public RichardsonLucyITK( final Img< T > image, final Img< S > kernel ) throws IncompatibleTypeException
	{	
		super( image, kernel );
	}
	
	public RichardsonLucyITK(final RandomAccessibleInterval<T> image, 
			final RandomAccessibleInterval<S> kernel,
			final ImgFactory<T> imgFactory,
			final ImgFactory<S> kernelImgFactory) throws IncompatibleTypeException
	{
		super(image, kernel, imgFactory, kernelImgFactory);
	}
	
	public boolean initialize()
	{
		return true;
	}
	
	public boolean process()
	{
		performIterations(maxIterations);
		
		return true;
	}
	
	public boolean performIterations(int numIterations)
	{
		
	//	Image itkImage=createSimpleITKImageFromInterval(image);
	//	Image itkPsf=createSimpleITKImageFromInterval(kernel);
		
		Image itkImage=ItkImagejUtilities.simple3DITKImageFromInterval(image);
		Image itkPsf=ItkImagejUtilities.simple3DITKImageFromInterval(kernel);
		
		itkRL=new RichardsonLucyDeconvolutionImageFilter();
		
		Image out=itkRL.execute(itkImage, itkPsf, maxIterations, true, boundaryCondition, 
				RichardsonLucyDeconvolutionImageFilter.OutputRegionModeType.SAME);
		
		copySimpleITKImageToOutput(out);
			
		return true;
	}
	
}
