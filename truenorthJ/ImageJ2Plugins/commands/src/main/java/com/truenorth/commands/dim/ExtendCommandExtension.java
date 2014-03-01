package com.truenorth.commands.dim;

import imagej.command.Command;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imglib2.meta.Axes;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

/**
 * 
 * extends the image by a specified number of voxels
 * 
 * if fft type is not none extends further for fft optimization
 * 
 * @author bnorthan
 *
 * @param <T>
 */
@Plugin(type = ExtendCommand.class, menuPath = "Plugins>Dimensions>Extend to Extension")
public class ExtendCommandExtension<T extends RealType<T> & NativeType<T>> extends ExtendCommand<T> 
{
	@Parameter 
	int extensionXY;
	
	@Parameter
	int extensionZ;
	
	void CalculateExtendedDimensions()
	{
		System.out.println("Extend--Extension version!");
		
		int v=0;
		
		for(int d=0;d<input.numDimensions();d++)
		{ 	
						
			//if ( (input.axis(d).type()==Axes.X) || (input.axis(d).type()==Axes.Y))
			if ( (input.axis(d).type()==Axes.X) || (input.axis(d).type()==Axes.Y))
			{
				initialExtendedDimensions[d]=input.dimension(d)+extensionXY*2;
			//	initialExtendedVolumeDimensions[v]=input.dimension(d)+extensionXY*2;
				v++;
			}
			//else if ( (input.axis(d).type()==Axes.Z))
			else if ( (input.axis(d).type()==Axes.Z))
			{
				initialExtendedDimensions[d]=input.dimension(d)+extensionZ*2;
			//	initialExtendedVolumeDimensions[v]=input.dimension(d)+extensionZ*2;
				v++;
			}
			else
			{
				initialExtendedDimensions[d]=input.dimension(d);
			}
		}
	}
	
}
