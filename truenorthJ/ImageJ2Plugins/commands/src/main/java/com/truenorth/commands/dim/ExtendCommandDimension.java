package com.truenorth.commands.dim;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imglib2.meta.Axes;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

/**
 * extends image to specified dimensions
 * 
 * if fft type is not none extends further for fft optimization
 * 
 * @author bnorthan
 *
 * @param <T>
 */
@Plugin(type = ExtendCommand.class, menuPath = "Plugins>Dimensions>Extend to Dimensions")
public class ExtendCommandDimension<T extends RealType<T> & NativeType<T>> extends ExtendCommand<T> 
{
	@Parameter 
	int dimensionX;
	
	@Parameter
	int dimensionY;
	
	@Parameter
	int dimensionZ;
	
	void CalculateExtendedDimensions()
	{
		System.out.println("extending using dimensions!");
		int v=0;
		
		for(int d=0;d<input.numDimensions();d++)
		{ 	
						
			//if ( input.axis(d).type()==Axes.X)
			if (input.axis(d).type()==Axes.X)
			{
				initialExtendedDimensions[d]=dimensionX;
			//	initialExtendedVolumeDimensions[v]=dimensionX;
				v++;
			}
			//else if (input.axis(d).type()==Axes.Y)
			else if (input.axis(d).type()==Axes.Y)
			{
				initialExtendedDimensions[d]=dimensionY;
			//	initialExtendedVolumeDimensions[v]=dimensionY;
				v++;			
			}
			else if ( input.axis(d).type()==Axes.Z)
			{
				initialExtendedDimensions[d]=dimensionZ;
		//		initialExtendedVolumeDimensions[v]=dimensionZ;
				v++;
			}
			else
			{
				initialExtendedDimensions[d]=input.dimension(d);
			}
		}
	}
}
