package com.truenorth.ops;

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
		implements Op
{
	@Override
	public RandomAccessibleInterval<S> compute(RandomAccessibleInterval<T> input,
		RandomAccessibleInterval<S> output)
		{
			System.out.println("Richardson Lucy");
			return null;
		}
}
