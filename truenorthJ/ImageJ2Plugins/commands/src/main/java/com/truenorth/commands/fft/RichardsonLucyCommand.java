package com.truenorth.commands.fft;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.NativeType;

import net.imglib2.exception.IncompatibleTypeException;

import com.truenorth.functions.StaticFunctions;
import com.truenorth.functions.fft.filters.IterativeFilter;
import com.truenorth.functions.fft.filters.RichardsonLucyFilter;

import net.imglib2.img.Img;

import org.scijava.plugin.Plugin;

/**
 * 
 * @author bnorthan
 * Richardson Lucy filter
 * @param <T>
 */
@Plugin(type=AbstractFrequencyFilterCommand.class, menuPath="Plugins>Deconvolution>Richardson Lucy")
public class RichardsonLucyCommand<T extends RealType<T> & NativeType<T>> extends IterativeFilterCommand<T>
{	
	protected IterativeFilter<T,T> createIterativeAlgorithm(RandomAccessibleInterval<T> region)
	{
		try 
		{
			Img<T> inputImg=(Img<T>)(input.getImgPlus().getImg());
			Img<T> psfImg=(Img<T>)(psf.getImgPlus().getImg());
					
			// create a RichardsonLucy filter for the region
			RichardsonLucyFilter<T,T> richardsonLucy = new RichardsonLucyFilter<T,T>(region,
					psfImg, 
					inputImg.factory(), 
					psfImg.factory());
			
			// return the filter
			return richardsonLucy;
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
