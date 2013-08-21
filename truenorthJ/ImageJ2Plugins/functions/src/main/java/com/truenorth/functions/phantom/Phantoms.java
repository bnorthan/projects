package com.truenorth.functions.phantom;

import net.imglib2.Point;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.region.hypersphere.HyperSphere;
import net.imglib2.algorithm.region.hypersphere.HyperSphereCursor;

import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;

public class Phantoms 
{
	public static <T extends RealType<T> > void drawSphere(final RandomAccessibleInterval<T> randomAccessible,
			final Point center,
			final int radius,
			final double intensity)
	{
		System.out.println("Adding a Sphere at: ");
		
		HyperSphere<T> hyperSphere = new HyperSphere<T>(randomAccessible, center, radius);
		
		HyperSphereCursor<T> cursor = hyperSphere.cursor();
		
		for (final T value:hyperSphere)
		{
			value.setReal(intensity);
		}
		
	}
	
	public static <T extends RealType<T> > void drawPoint(final RandomAccessibleInterval<T> randomAccessible,
			final Point position,
			final double intensity)
	{
		System.out.println("Adding a Point at: ");
		
		RandomAccess<T> randomAccess= randomAccessible.randomAccess();
		
		randomAccess.setPosition(position);
		
		randomAccess.get().setReal(intensity);	
	}
}
