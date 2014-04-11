package com.truenorth.ops.fft;

import imagej.ops.AbstractFunction;
import imagej.ops.Contingent;
import imagej.ops.Op;

import net.imglib2.RandomAccessibleInterval;

import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

@Plugin(type = Op.class, name = "RichardsonLucy")
public class RichardsonLucy<T extends RealType<T>, S extends RealType<S>> 
		extends AbstractFunction<RandomAccessibleInterval<T>, RandomAccessibleInterval<S>> 
		implements Op, Contingent
{
	@Parameter
	private RandomAccessibleInterval<T> kernel;
	
	@Override
	public RandomAccessibleInterval<S> compute(RandomAccessibleInterval<T> input,
		RandomAccessibleInterval<S> output)
		{
			System.out.println("Richardson Lucy");
			
			System.out.println("input dim: "+input.dimension(0));
			System.out.println("output dim: "+output.dimension(1));
			System.out.println("psf dim: "+kernel.dimension(0));
			
			return null;
		}
	
	@Override
	public boolean conforms() 
	{
		return true;
	}
}
