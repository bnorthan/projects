package com.truenorth.commands.dim;

import net.imglib2.meta.Axes;

public class ExtendCommandFFT extends ExtendCommand
{
	void CalculateExtendedDimensions()
	{
		System.out.println("Extend--FFT version!");
		
		int v=0;
		
		// in this step set extended dimensions the same as the input dimensions
		// the result will be the image will be extended only as needed for effecient FFTs
		for(int d=0;d<input.numDimensions();d++)
		{ 	
						
			//if ( (input.axis(d).type()==Axes.X) || (input.axis(d).type()==Axes.Y))
			if ( (input.axis(d)==Axes.X) || (input.axis(d)==Axes.Y))
			{
				extendedDimensions[d]=input.dimension(d);
				extendedVolumeDimensions[v]=input.dimension(d);
				v++;
			}
			//else if ( (input.axis(d).type()==Axes.Z))
			else if ( (input.axis(d)==Axes.Z))
			{
				extendedDimensions[d]=input.dimension(d);
				extendedVolumeDimensions[v]=input.dimension(d);
				v++;
			}
			else
			{
				extendedDimensions[d]=input.dimension(d);
			}
		}
		
	}
}
