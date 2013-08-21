package com.truenorth.commands.dim;

import org.scijava.plugin.Parameter;

//import net.imglib2.meta.CalibratedAxis;
import net.imglib2.meta.Axes;

public class ExtendCommandDimension extends ExtendCommand 
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
			if (input.axis(d)==Axes.X)
			{
				extendedDimensions[d]=dimensionX;
				extendedVolumeDimensions[v]=dimensionX;
				v++;
			}
			//else if (input.axis(d).type()==Axes.Y)
			else if (input.axis(d)==Axes.Y)
			{
				extendedDimensions[d]=dimensionY;
				extendedVolumeDimensions[v]=dimensionY;
				v++;			
			}
			else if ( input.axis(d)==Axes.Z)
			{
				extendedDimensions[d]=dimensionZ;
				extendedVolumeDimensions[v]=dimensionZ;
				v++;
			}
			else
			{
				extendedDimensions[d]=input.dimension(d);
			}
		}
	}
}
