package com.truenorth.commands.dim;

import org.scijava.command.Command;

import org.scijava.plugin.Plugin;

import net.imglib2.meta.Axes;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

/**
 * 
 * @author bnorthan
 *
 * extends the image for a fft optimization strategy
 *
 * @param <T>
 */
@Plugin(type = ExtendCommand.class, menuPath = "Plugins>Dimensions>Extend for FFT")
public class ExtendCommandFFT<T extends RealType<T> & NativeType<T>> extends ExtendCommand<T>
{
	void CalculateExtendedDimensions()
	{
		System.out.println("Extend--FFT version!");
		
		int v=0;
		
		// The image will be extended only as needed for FFT. 
		// so first set extended dimensions the same as the input dimensions
		for(int d=0;d<input.numDimensions();d++)
		{ 	
						
			if ( (input.axis(d).type()==Axes.X) || (input.axis(d).type()==Axes.Y))
			{
				initialExtendedDimensions[d]=input.dimension(d);
				v++;
			}
			else if ( (input.axis(d).type()==Axes.Z))
			{
				initialExtendedDimensions[d]=input.dimension(d);
				v++;
			}
			else
			{
				initialExtendedDimensions[d]=input.dimension(d);
			}
		}
	}
}
