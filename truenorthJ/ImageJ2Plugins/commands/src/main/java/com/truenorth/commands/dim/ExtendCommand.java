package com.truenorth.commands.dim;

import org.scijava.plugin.Parameter;

import net.imglib2.RandomAccessibleInterval;

import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory.Boundary;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import net.imglib2.img.Img;
import net.imglib2.meta.Axes;
import net.imglib2.meta.AxisType;

import com.truenorth.functions.dim.ExtendImage;

import com.truenorth.functions.fft.SimpleFFTFactory;

import com.truenorth.commands.AbstractVolumeProcessorCommand;
import com.truenorth.commands.Constants;
import com.truenorth.commands.Constants.FFTOptimization;

/**
 * 
 * @author bnorthan
 * 
 * abstract class for extension commands
 * 
 * if the fft type is not 'none' then the image is extended further for the fft optimization strategy 
 * @param <T>
 */
public abstract class ExtendCommand<T extends RealType<T> & NativeType<T>> extends AbstractVolumeProcessorCommand<T>
{	
	@Parameter(label="boundary type", choices = {Constants.Boundary.boundaryZero, Constants.Boundary.boundaryMirror})
	private String boundaryType;
	
	@Parameter(label="fft type", choices={FFTOptimization.fftOptimizationNone, FFTOptimization.fftOptimizationSpeed, FFTOptimization.fftOptimizationSize})
	private String fftType;
	
	Boundary boundary;
	
	// the extended dimensions of the dataset (including channels, timepoints etc.)
	long[] initialExtendedDimensions;
	//long[] initialExtendedVolumeDimensions;
	long[] finalExtendedVolumeDimensions;
	
	@Override 
	protected void preProcess()
	{
		initialExtendedDimensions = new long[input.numDimensions()];
	
	//	initialExtendedVolumeDimensions = new long[3];
		finalExtendedVolumeDimensions= new long[3];
		
		long[] finalExtendedDimensions = new long[input.numDimensions()];
				
		CalculateExtendedDimensions();
		
		long[] originalDimensions = new long[input.numDimensions()];
		
		int v=0;
		
		long[] volumeDimensions = new long[3];
		
		input.dimensions(originalDimensions);
		
		for (int d=0;d<originalDimensions.length;d++)
		{	
			// if it is a volume dimension
			if ( (input.axis(d).type()==Axes.X) ||(input.axis(d).type()==Axes.Y)||(input.axis(d).type()==Axes.Z) ) 
			{
				volumeDimensions[v]=initialExtendedDimensions[d];//input.dimension(d);
				v++;
			}		
		}
			
		v=0;
	
		// if the image should be extended further to the nearest FFT size that is optimized for speed
		if (fftType.equals(Constants.FFTOptimization.fftOptimizationSpeed))
		{
			System.out.println("fft speed");
				
			finalExtendedVolumeDimensions=SimpleFFTFactory.GetPaddedInputSizeLong(volumeDimensions);
		}
		// if the image should be extended further to the nearest FFT size that is optimized for volume
		else if (fftType.equals(Constants.FFTOptimization.fftOptimizationSize))
		{
			System.out.println("fft size");
		}
		// if not optimizing for fft the final extended volume dimensions don't need to be adjusted any further
		else if (fftType.equals(Constants.FFTOptimization.fftOptimizationNone))
		{
			System.out.println("fft none");
			
			finalExtendedVolumeDimensions=volumeDimensions;
		}
		
		for (int d=0;d<originalDimensions.length;d++)
		{
			// if it is a volume dimension
			if ((input.axis(d).type()==Axes.X)||(input.axis(d).type()==Axes.Y)||(input.axis(d).type()==Axes.Z)) 
			{
				finalExtendedDimensions[d]=finalExtendedVolumeDimensions[v];
				v++;
			}		
			else
			{
				finalExtendedDimensions[d]=input.dimension(d);
			}
		}
		
		Img<T> imgInput=(Img<T>)(input.getImgPlus().getImg());
		
		AxisType[] axisType=new AxisType[finalExtendedDimensions.length];
		
		for (int d=0;d<finalExtendedDimensions.length;d++)
		{
			axisType[d]=input.axis(d).type();
		}
		
		String name= input.getName()+" extended";
		
		//input.getImgPlus().
		output=datasetService.create(imgInput.firstElement(), finalExtendedDimensions, name, axisType);//input.getAxes());
		
	}
	
	abstract void CalculateExtendedDimensions();
	
	@Override
	protected Img<T> processVolume(RandomAccessibleInterval<T> volume)
	{
		System.out.println("processVolume!");
		Img<T> imgInput=(Img<T>)(input.getImgPlus().getImg());
		
		OutOfBoundsFactory< T, RandomAccessibleInterval<T> > outOfBoundsFactory;
		
		// create the OutOfBoundsFactory according to the boundary type
		if (boundaryType.equals(Constants.Boundary.boundaryMirror))
		{
			outOfBoundsFactory 
			= new OutOfBoundsMirrorFactory< T, RandomAccessibleInterval<T> >( Boundary.SINGLE );
		}
		else
		{
			T t=imgInput.firstElement().createVariable();
			
			t.setReal(0.0);
			
			outOfBoundsFactory 
				= new OutOfBoundsConstantValueFactory<T, RandomAccessibleInterval<T>>(t);
		}
		
		// call the extend routine on the volume
		Img<T> extended=ExtendImage.Extend(volume, imgInput.factory(), finalExtendedVolumeDimensions, outOfBoundsFactory, imgInput.firstElement());
		
		if (extended==null)
		{
			System.out.println("null");
		}
		
		System.out.println("returning");
		return extended;
	}
}
