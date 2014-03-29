package com.truenorth.commands.dim;

import net.imglib2.meta.Axes;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * 
 * Extends the image for non-circulant convolution model
 * Extended image size is calculated based on the size of the psf. 
 * 
 *  http://bigwww.epfl.ch/deconvolution/challenge/index.html?p=documentation/overview
 * 
 * @author bnorthan
 *
 * @param <T>
 */
@Plugin(type = ExtendCommand.class, menuPath = "Plugins>Dimensions>Extend Noncirculant!")
public class ExtendCommandNoncirculant <T extends RealType<T> & NativeType<T>> extends ExtendCommand<T> 
{
	@Parameter 
	int psfSizeX;
	
	@Parameter 
	int psfSizeY;
	
	@Parameter 
	int psfSizeZ;
	
	int measurementSizeX;
	int measurementSizeY;
	int measurementSizeZ;
	
	void CalculateExtendedDimensions()
	{
		System.out.println("extending using dimensions!");
		int v=0;
		
		measurementSizeX=(int)input.dimension(input.dimensionIndex(Axes.X));
		measurementSizeY=(int)input.dimension(input.dimensionIndex(Axes.Y));
		measurementSizeZ=(int)input.dimension(input.dimensionIndex(Axes.Z));
		
		for(int d=0;d<input.numDimensions();d++)
		{ 	
			// size of the extended space is calculated to avoid circular overlap of the psf
			// into the measurement space
			int extendedSizeX=measurementSizeX+psfSizeX-1;
			int extendedSizeY=measurementSizeY+psfSizeY-1;
			int extendedSizeZ=measurementSizeZ+psfSizeZ-1;
						
			if (input.axis(d).type()==Axes.X)
			{
				
				initialExtendedDimensions[d]=extendedSizeX;
				v++;
			}
			else if (input.axis(d).type()==Axes.Y)
			{
				initialExtendedDimensions[d]=extendedSizeY;
				v++;			
			}
			else if ( input.axis(d).type()==Axes.Z)
			{
				initialExtendedDimensions[d]=extendedSizeZ;
				v++;
			}
			else
			{
				initialExtendedDimensions[d]=input.dimension(d);
			}
		}
	}

}
