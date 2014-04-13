package com.truenorth.itk.ops;

import imagej.ops.Op;

import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Util;

import org.itk.simple.Image;
import org.itk.simple.RichardsonLucyDeconvolutionImageFilter;
import org.itk.simple.RichardsonLucyDeconvolutionImageFilter.BoundaryConditionType;
import org.scijava.plugin.Plugin;
import org.scijava.Priority;

import com.truenorth.itk.ItkImagejUtilities;

@Plugin(type = Op.class, name = "RichardsonLucyITK", priority = Priority.HIGH_PRIORITY + 1)
public class RichardsonLucyITK<T extends RealType<T>, S extends RealType<S>> 
		extends IterativeFilterOpITK<T,S>
{
	org.itk.simple.RichardsonLucyDeconvolutionImageFilter itkRL;
	
	public void run()
	{
		// convert input to itk Images
		Image itkImage=ItkImagejUtilities.simple3DITKImageFromInterval(input);
		Image itkPsf=ItkImagejUtilities.simple3DITKImageFromInterval(kernel);
				
		itkRL=new RichardsonLucyDeconvolutionImageFilter();
				
		// call itk rl using simple itk wrapper
		Image out=itkRL.execute(itkImage, itkPsf, numIterations, true, BoundaryConditionType.ZERO_PAD, 
				RichardsonLucyDeconvolutionImageFilter.OutputRegionModeType.SAME);
				
		T inputType=Util.getTypeFromInterval(input);
				
		// convert output to ImageJ Img
		output=ItkImagejUtilities.simple3DITKImageToImg(out, input.factory(), inputType);
				
	}
		
}