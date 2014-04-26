package com.truenorth.ops.fft;


import net.imagej.ops.AbstractFunction;
import net.imagej.ops.Contingent;
import net.imagej.ops.Op;

import net.imglib2.RandomAccessibleInterval;

import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ItemIO;
import org.scijava.Priority;

@Plugin(type = Op.class, name = "RichardsonLucy", priority = Priority.HIGH_PRIORITY + 1)
public class RichardsonLucyTrueNorthWrapper<T extends RealType<T>, S extends RealType<S>> 
	//	extends AbstractFunction<RandomAccessibleInterval<T>, RandomAccessibleInterval<S>> 
		implements Op, Contingent
{
	@Parameter
	private Img<T> input;
	
	@Parameter
	private Img<T> kernel;
	
	@Parameter(type = ItemIO.OUTPUT, required=false)
	private Img<T> output;
	
	@Override
	public void run()
		{
			System.out.println("Richardson Lucy True North Wrapper");
			
			System.out.println("input dim: "+input.dimension(0));
			System.out.println("psf dim: "+kernel.dimension(0));
			
			output = input.copy();
			
			System.out.println("output dim: "+output.dimension(1));
			
		}
	
	@Override
	public boolean conforms() 
	{
		return true;
	}
}
