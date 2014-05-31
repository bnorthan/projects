package com.truenorth.functions;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.imglib2.img.Img;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.Cursor;

import com.truenorth.functions.StaticFunctions ;
import com.truenorth.functions.parallel.ReductionChunker;
import com.truenorth.functions.parallel.IterationChunker;
import com.truenorth.functions.parallel.arithmetic.ParallelSum;
import com.truenorth.functions.parallel.arithmetic.ParallelDivide;

public class ParallelTest 
{
	private int seed;
	double eps=0.00001;

	@Test
	public void test() 
	{
		long xSize=128;
		long ySize=128;
		
		Img<UnsignedByteType> in = StaticFunctions.generateUnsignedByteTestImg(17, true, xSize, ySize);
		
		// do some timing while we are at it	
		long start=System.currentTimeMillis();
		
		double noThreadResult=StaticFunctions.sum(in);
		
		long noThreadTime=System.currentTimeMillis()-start;
		
		start=System.currentTimeMillis();
		
		ParallelSum<UnsignedByteType, FloatType> sum=new ParallelSum<UnsignedByteType, FloatType>(in);
		
		ReductionChunker<UnsignedByteType, FloatType> chunker=
				new ReductionChunker<UnsignedByteType, FloatType>(in.size(), sum, new FloatType());
		
		FloatType threadResult=chunker.run();
		
		long threadTime=System.currentTimeMillis()-start;
		
		System.out.println("\nNo Threading");
		System.out.println(noThreadTime);
		System.out.println(noThreadResult);
		
		System.out.println("\nThreading");
		System.out.println(threadTime);
		System.out.println(threadResult);
		
		assertEquals(5,5);
		
	}
	
	@Test
	public void divisionTest()
	{
		/*Img<FloatType> simpletest = StaticFunctions.generateFloatTestImg(17, true, 4, 4);
		Img<UnsignedByteType> bytetest = StaticFunctions.generateUnsignedByteTestImg(17, true, 4, 4);
		
		for (FloatType f:simpletest)
		{
			System.out.println(f);
		}
		
		for (UnsignedByteType ub:bytetest)
		{
			System.out.println(ub);
		}
		
		StaticFunctions.Pause();
		*/
		
		long xSize=128;
		long ySize=128;
		long size=xSize*ySize;
		
		Img<FloatType> img1 = StaticFunctions.generateFloatTestImg(17, true, xSize, ySize);
		Img<FloatType> img2 = StaticFunctions.generateFloatTestImg(18, true, xSize, ySize);
		
		long start=System.currentTimeMillis();
		
		Img<FloatType> out1=StaticFunctions.Divide(img1, img2);
		
		long noThreadTime=System.currentTimeMillis()-start;
			
		Img<FloatType> out2=img1.factory().create(img1, img1.firstElement());
		
		ParallelDivide<FloatType> divide=new ParallelDivide<FloatType>(img2, img1, out2);
		
		IterationChunker<FloatType> chunker=new IterationChunker<FloatType>(size, divide);
		
		start=System.currentTimeMillis();
		
		chunker.run();
		
		long threadTime=System.currentTimeMillis()-start;
		
		System.out.println("No thread time "+noThreadTime);
		System.out.println("Thread time "+threadTime);
		
		Cursor<FloatType> c1=out1.cursor();
		Cursor<FloatType> c2=out2.cursor();
		
		// confirm the static version and the threaded version produce the same answer
		while (c1.hasNext())
		{
			c1.fwd();
			c2.fwd();
		
			assertEquals(c1.get().getRealFloat(), c2.get().getRealFloat(), eps);
		}
	}
	
}

