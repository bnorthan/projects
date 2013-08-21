package com.truenorth.commands.dim;

import org.scijava.plugin.Parameter;

import net.imglib2.meta.Axes;

public class ExtendCommandExtension extends ExtendCommand 
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
			if ( (input.axis(d)==Axes.X) || (input.axis(d)==Axes.Y))
			{
				extendedDimensions[d]=input.dimension(d)+extensionXY*2;
				extendedVolumeDimensions[v]=input.dimension(d)+extensionXY*2;
				v++;
			}
			//else if ( (input.axis(d).type()==Axes.Z))
			else if ( (input.axis(d)==Axes.Z))
			{
				extendedDimensions[d]=input.dimension(d)+extensionZ*2;
				extendedVolumeDimensions[v]=input.dimension(d)+extensionZ*2;
				v++;
			}
			else
			{
				extendedDimensions[d]=input.dimension(d);
			}
		}
	}
	
}
