package com.truenorth.itk.commands;


import net.imglib2.RandomAccessibleInterval;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import org.scijava.plugin.Plugin;
import com.truenorth.itk.functions.IterativeFilterITK;
import com.truenorth.itk.functions.RichardsonLucyITK;

import com.truenorth.commands.fft.AbstractFrequencyFilterCommand;

@Plugin(type=AbstractFrequencyFilterCommand.class, menuPath="Plugins>Deconvolution>Richardson Lucy ITK")
public class RichardsonLucyITKCommand <T extends RealType<T> & NativeType<T>> extends IterativeFilterCommandITK<T>
{	
	protected IterativeFilterITK<T,T> createIterativeITKAlgorithm(RandomAccessibleInterval<T> region)
	{
		try 
		{
			Img<T> inputImg=(Img<T>)(input.getImgPlus().getImg());
			Img<T> psfImg=(Img<T>)(psf.getImgPlus().getImg());
		
			// create a RichardsonLucy filter for the region
			RichardsonLucyITK<T,T> rlITK = new RichardsonLucyITK<T,T>(region,
					psfImg, 
					inputImg.factory(), 
					psfImg.factory());
			
			// return the filter
			return rlITK;
		}
		catch (IncompatibleTypeException ex)
		{
			return null;
		}
	}
	
	@Override
	protected void setName()
	{
		if (output!=null)
		{
			String name=input.getName()+" Richardson Lucy";
			output.setName(name);
		}
	}

}
