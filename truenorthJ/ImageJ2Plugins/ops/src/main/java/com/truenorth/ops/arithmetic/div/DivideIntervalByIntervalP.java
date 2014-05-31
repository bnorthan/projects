package com.truenorth.ops.arithmetic.div;

import net.imagej.ops.Op;
import net.imagej.ops.Contingent;
import net.imglib2.IterableInterval;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

import org.scijava.ItemIO;
import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import com.truenorth.functions.parallel.arithmetic.ParallelDivide;

/**
 * 
 * @author bnorthan
 *
 */
@Plugin(type = Op.class, name = Divide.NAME, priority = Priority.HIGH_PRIORITY + 10)
public class DivideIntervalByIntervalP<T extends RealType<T>> implements Divide, Contingent 
{
	@Parameter
	private IterableInterval<T> denominator;
	
	@Parameter
	private IterableInterval<T> numerator;
	
	@Parameter(type = ItemIO.BOTH)
	private IterableInterval<T> output;
	
	@Override
	public void run()
	{
		ParallelDivide.Divide(denominator, numerator, output);
	}
	
	@Override
	public boolean conforms() 
	{
		return true;
	}
}
