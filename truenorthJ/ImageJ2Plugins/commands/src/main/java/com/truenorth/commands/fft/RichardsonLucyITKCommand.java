package com.truenorth.commands.fft;

//TODO: possibly restore this once some problems with linking to SimpleITK are worked out

/*
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import com.truenorth.commands.Constants;
import com.truenorth.functions.fft.filters.IterativeFilterITK;
import com.truenorth.functions.fft.filters.RichardsonLucyITK;

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

}*/
