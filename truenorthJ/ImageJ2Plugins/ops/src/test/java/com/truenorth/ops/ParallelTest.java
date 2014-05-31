package com.truenorth.ops;

import net.imglib2.img.Img;
import net.imglib2.Cursor;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;
import net.imagej.ops.Op;
import net.imagej.ops.arithmetic.add.parallel.AddConstantToArrayDoubleImageP;

import org.junit.Test;
import org.junit.Assert;

import com.truenorth.functions.StaticFunctions;
import com.truenorth.ops.statistics.SumRealTypeParallel;
import com.truenorth.ops.arithmetic.div.DivideIntervalByIntervalP;

public class ParallelTest extends AbstractOpsTest
{
	double eps=0.000001;

	@Test
	public void TestAdd()
	{
		long xSize=2550;
		long ySize=2550;
		
		// generate a random test image
		Img<DoubleType> in = generateDoubleTestImg(true, xSize, ySize);
		
		long size=xSize*ySize;
		
		// create the add constant op
		Op addCToDoubleArray= new AddConstantToArrayDoubleImageP();
		
		// calculate the sum before we add a constant
		double sum1=StaticFunctions.sum(in);
		
		double constant=5.0;
		
		// add a constant
		ops.run(addCToDoubleArray, in, constant);
		
		double sum2=StaticFunctions.sum(in);
		
		System.out.println(sum1/size);
		System.out.println(sum2/size);
		double dif=sum2/size-sum1/size;
		System.out.println(dif);
		
		// check that the constant is equal to the difference between the sums
		Assert.assertEquals(constant, dif, eps);
		
		// create a parallel sum operation
		Op parallelSum=new SumRealTypeParallel<DoubleType, DoubleType>();
		
		DoubleType out=new DoubleType();
		
		// run parallel sum
		ops.run(parallelSum, out, in, out);
		
		System.out.println("test out is "+out);
		
		double sum3=out.getRealDouble();
		
		System.out.println(sum2/size);
		System.out.println(sum3/size);
		
		// make sure the normalized sums are equal within the epsilon
		Assert.assertEquals(sum2/sum2, sum3/sum2, eps);
		
	}
	
	@Test
	public void TestDivide()
	{
		long xSize=2550;
		long ySize=2550;
		long size=xSize*ySize;
		
		Img<FloatType> img1 = StaticFunctions.generateFloatTestImg(17, true, xSize, ySize);
		Img<FloatType> img2 = StaticFunctions.generateFloatTestImg(18, true, xSize, ySize);
		
		Img<FloatType> out=img1.factory().create(new long[]{xSize, ySize}, img1.firstElement());
	
		Op divide=new DivideIntervalByIntervalP<FloatType>();
		
		ops.run(divide, img1, img2, out);
		
		Cursor<FloatType> c1=img1.cursor();
		Cursor<FloatType> c2=img2.cursor();
		Cursor<FloatType> cout=out.cursor();
		
		for (FloatType f:img1)
		{
			c1.fwd();
			c2.fwd();
			cout.fwd();
			
			float f1=c1.get().getRealFloat();
			float f2=c2.get().getRealFloat();
			float fout=cout.get().getRealFloat();
			
			// make sure the normalized sums are equal within the epsilon
			Assert.assertEquals(f2/f1, fout, (float)eps);
		}
	}

}

