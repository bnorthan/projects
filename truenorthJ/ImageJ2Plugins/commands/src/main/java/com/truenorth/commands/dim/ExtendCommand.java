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
	long[] extendedDimensions;
	
	// the extended volume (x,y,z) dimensions of the dataset
	long[] extendedVolumeDimensions;
	
	@Override 
	protected void preProcess()
	{
		extendedDimensions = new long[input.numDimensions()];
		extendedVolumeDimensions = new long[3];
				
		CalculateExtendedDimensions();
		
		// if the image should be extended further to the nearest FFT size
		if (fftType.equals(Constants.FFTOptimization.fftOptimizationSpeed))
		{
			long[] originalDimensions = new long[input.numDimensions()];
			long[] volumeDimensions = new long[3];
			
			System.out.println("fft speed");
			
			input.dimensions(originalDimensions);
			
			int v=0;
			
			for (int d=0;d<originalDimensions.length;d++)
			{
				// if it is a volume dimension
				if ( (input.axis(d)==Axes.X) ||(input.axis(d)==Axes.Y)||(input.axis(d)==Axes.Z) ) 
				{
					volumeDimensions[v]=extendedDimensions[d];//input.dimension(d);
					v++;
				}		
			}
			
			extendedVolumeDimensions=SimpleFFTFactory.GetPaddedInputSizeLong(volumeDimensions);
			
			v=0;
			
			for (int d=0;d<originalDimensions.length;d++)
			{
				// if it is a volume dimension
				if ((input.axis(d)==Axes.X)||(input.axis(d)==Axes.Y)||(input.axis(d)==Axes.Z)) 
				{
					extendedDimensions[d]=extendedVolumeDimensions[v];
					v++;
				}		
				else
				{
					extendedDimensions[d]=input.dimension(d);
				}
			}
		}
		else if (fftType.equals(Constants.FFTOptimization.fftOptimizationSize))
		{
			System.out.println("fft size");
		}
		
		Img<T> imgInput=(Img<T>)(input.getImgPlus().getImg());
		
		output=datasetService.create(imgInput.firstElement(), extendedDimensions, "extended", input.getAxes());
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
		Img<T> extended=ExtendImage.Extend(volume, imgInput.factory(), extendedVolumeDimensions, outOfBoundsFactory, imgInput.firstElement());
		
		if (extended==null)
		{
			System.out.println("null");
		}
		
		System.out.println("returning");
		return extended;
	}
}
