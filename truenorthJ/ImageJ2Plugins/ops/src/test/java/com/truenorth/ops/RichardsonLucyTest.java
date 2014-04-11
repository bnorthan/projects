package com.truenorth.ops;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertEquals;
import imagej.ops.Op;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.type.numeric.integer.ByteType;

import org.junit.Test;

public class RichardsonLucyTest extends AbstractOpsTest
{
	@Test
	public void TestBasic()
	{
		final Img<FloatType> in =
                new ArrayImgFactory<FloatType>().create(new int[]{20, 20, 20},
                        new FloatType());
		final Img<FloatType> out = in.copy();
        final Img<FloatType> kernel=in.copy();
        
        final Img<FloatType> in2 =
                new ArrayImgFactory<FloatType>().create(new int[]{30, 40, 50},
                        new FloatType());
        
        final Img<FloatType> in3 =
                new ArrayImgFactory<FloatType>().create(new int[]{60, 70, 80},
                        new FloatType());
        
		Object result;
		
		ops.convolve(out, in, kernel);
		
		result = ops.run("RichardsonLucy", in, in2, in3);
		
		result = ops.run("RichardsonLucy", in, kernel);
		
		result = ops.run("Convolution", in, kernel);
		
		Img<FloatType> imgOut=(Img<FloatType>)result;
		
		System.out.println("img out size: "+imgOut.dimension(0));
		
		System.out.println("called rl");
		
	}

}
