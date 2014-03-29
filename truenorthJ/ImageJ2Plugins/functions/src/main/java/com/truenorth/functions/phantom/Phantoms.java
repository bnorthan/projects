package com.truenorth.functions.phantom;

import net.imglib2.Point;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.region.hypersphere.HyperSphere;
import net.imglib2.algorithm.region.hypersphere.HyperSphereCursor;

import net.imglib2.type.numeric.RealType;

import net.imglib2.RandomAccess;

public class Phantoms 
{
	public static <T extends RealType<T> > void drawSphere(final RandomAccessibleInterval<T> randomAccessible,
			final Point center,
			final int radius,
			final double intensity)
	{
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
		RandomAccess<T> randomAccess= randomAccessible.randomAccess();
		
		randomAccess.setPosition(position);
		
		randomAccess.get().setReal(intensity);	
	}
	
	public static <T extends RealType<T> > void drawCube(final RandomAccessibleInterval<T> randomAccessible, final Point start,
		final Point size, final double intensity)
	{
		// assume 2-D or 3-D space for now
		if (start.numDimensions()==2)
		{
			Point position=new Point(2);
			
			int yStart=start.getIntPosition(1);
			int xStart=start.getIntPosition(0);
			
			for (int y=0;y<size.getIntPosition(1);y++)
			{		
				position.setPosition(y+yStart,1);
				for (int x=0;x<size.getIntPosition(0);x++)
				{
					position.setPosition(x+xStart,0);
					drawPoint(randomAccessible, position, intensity);
				}
			}
		}
		if (start.numDimensions()==3)
		{
			Point position=new Point(3);
			
			int zStart=start.getIntPosition(2);
			int yStart=start.getIntPosition(1);
			int xStart=start.getIntPosition(0);
			
			for (int z=0;z<size.getIntPosition(2);z++)
			{
				position.setPosition(z+zStart,2);
				for (int y=0;y<size.getIntPosition(1);y++)
				{	
					position.setPosition(y+yStart,1);
					for (int x=0;x<size.getIntPosition(0);x++)
					{
						position.setPosition(x+xStart,0);
						drawPoint(randomAccessible, position, intensity);
					}
				}
			}
		}
			
	}
}
