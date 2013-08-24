package com.truenorth.commands.fft;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import net.imglib2.exception.IncompatibleTypeException;

import com.truenorth.functions.fft.filters.FrequencyFilter;
import com.truenorth.functions.fft.filters.InverseFilter;


import imagej.command.Command;

import net.imglib2.img.Img;

import org.scijava.plugin.Plugin;

/**
 * 
 * @author bnorthan
 * Inverse Filter
 * @param <T>
 */
@Plugin(type=Command.class, menuPath="Process>Inverse Filter Test")
public class InverseFilterCommand<T extends RealType<T>& NativeType<T>> extends AbstractFrequencyFilterCommand<T>
{
	/**
	 * create an inverse filter algorithm to process the region
	 */
	FrequencyFilter<T,T> createAlgorithm(RandomAccessibleInterval<T> region)
	{
		try 
		{
			Img<T> inputImg=(Img<T>)(input.getImgPlus().getImg());
			Img<T> psfImg=(Img<T>)(psf.getImgPlus().getImg());
			
			// create an inverse filter algorithm to process the region
			return new InverseFilter<T,T>(region,
					psfImg, 
					inputImg.factory(), 
					psfImg.factory());
		}
		catch (IncompatibleTypeException ex)
		{
			return null;
		}
	}

}
