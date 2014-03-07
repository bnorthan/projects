package com.truenorth.commands;

import imagej.data.Dataset;

import net.imglib2.meta.AxisType;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import imagej.data.DatasetService;

public class CommandUtilities 
{
	/**
	 * Creates a dataset with bounds constrained by the minimum of the two input
	 * datasets.
	 */
	public static <T extends RealType<T> & NativeType<T>> Dataset create(
		final DatasetService datasetService, final Dataset d, final T type)
	{
		final int dimCount = d.numDimensions();
		final long[] dims = new long[dimCount];
		final AxisType[] axes = new AxisType[dimCount];
		for (int i = 0; i < dimCount; i++) {
			dims[i] = d.dimension(i);
			axes[i] = d.axis(i).type();
		}
		return datasetService.create(type, dims, "result", axes);
	}

}
