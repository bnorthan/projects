package com.truenorth.commands;

import imagej.data.Dataset;

import net.imglib2.meta.Axes;
import net.imglib2.meta.AxisType;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import imagej.data.DatasetService;

import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;


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
	
	public static long[] get3dDimensions(Dataset d)
	{
		long[] dim=new long[3];
		
		dim[0]=d.dimension(d.dimensionIndex(Axes.X));
		dim[1]=d.dimension(d.dimensionIndex(Axes.Y));
		
		if (d.dimensionIndex(Axes.Z)!=-1)
	    {
	    	dim[2]=d.dimension(d.dimensionIndex(Axes.Z));
	    }
	    else
	    {
	    	dim[2]=1;
	    }
		
		return dim;
	}
	
	public static <T extends RealType<T> & NativeType<T>> Img<T> createVolume(Dataset d)
	{
		long[] dim = get3dDimensions(d);
		
		Img<T> img=(Img<T>)d.getImgPlus().getImg();
		
		ImgFactory<T> factory=(ImgFactory<T>)d.getImgPlus().getImg().factory();
	
		Img<T> out=(Img<T>)(factory.create(dim, img.firstElement()));
	
		return out;
	}
	
	


}
