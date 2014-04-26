package com.truenorth.ops.fft;


import net.imagej.ops.Contingent;
import net.imagej.ops.Op;  

import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ItemIO;
import org.scijava.Priority;

import com.truenorth.functions.fft.filters.Convolution;

@Plugin(type = Op.class, name = "Convolution", priority = Priority.HIGH_PRIORITY + 1)
public class ConvolutionTrueNorthWrapper<T extends RealType<T>, S extends RealType<S>> 
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
		System.out.println("Convolution True North Wrapper");
			
		System.out.println("input dim: "+input.dimension(0)+" "+input.dimension(1)+" "+input.dimension(2));
		System.out.println("kernel dim: "+kernel.dimension(0)+" "+kernel.dimension(1)+" "+kernel.dimension(2));
			
		output = input.copy();
			
		System.out.println("output dim: "+output.dimension(0)+" "+output.dimension(1)+" "+output.dimension(2));
		
		Convolution<T,T> convolver=null;
		
		try
		{
			convolver= new Convolution<T,T>(input,
					kernel, 
					input.factory(), 
					kernel.factory());
		}
		catch(Exception ex)
		{
			
		}
		
		convolver.process();
		
		output=convolver.getResult();
	}
	
	@Override
	public boolean conforms() 
	{
		return true;
	}
}
