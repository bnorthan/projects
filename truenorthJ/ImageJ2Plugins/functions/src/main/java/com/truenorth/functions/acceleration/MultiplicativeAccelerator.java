package com.truenorth.functions.acceleration;

import com.truenorth.functions.StaticFunctions;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

/**
 * Implemenation of Multiplicative Vector Acceleration
 * 
 * Biggs, Andrews "Acceleration of Iterative image restoration algorithms"
 * 
 * https://researchspace.auckland.ac.nz/handle/2292/1760
 * 
 * @author bnorthan
 *
 * @param <T>
 */
public class MultiplicativeAccelerator <T extends RealType<T>> implements Accelerator<T>
{
	Img<T> xkm1_previous=null;
	Img<T> yk_prediction=null;
	Img<T> hk_vector=null;
	
	Img<T> gk;
	Img<T> gkm1;
	
	double accelerationFactor=0.0f;
	
	public Img<T> Accelerate(Img<T> yk_iterated)
	{
		// use the iterated prediciton and the previous value of the prediciton
		// to calculate the acceleration factor
		if (yk_prediction!=null)
		{
			//StaticFunctions.showStats(yk_iterated);
			//StaticFunctions.showStats(yk_prediction);
			
			accelerationFactor=computeAccelerationFactorMultiplicative(yk_iterated, yk_prediction, yk_iterated, xkm1_previous);
			
			System.out.println("acceleration factor is: "+accelerationFactor);
			
			// if acceleration factor is negative or above 1 restart the acceleration process
			if ( (accelerationFactor<0) )
			{
				xkm1_previous=null;
				gkm1=null;
				accelerationFactor=0.0;
			}
		}
		
		// current estimate for x is yk_iterated
		Img<T> xk_estimate=yk_iterated;
		
		
		// calculate the change vector between x and x previous
		if (xkm1_previous!=null)
		{
			// --------------------divide
			hk_vector=StaticFunctions.Divide(xk_estimate, xkm1_previous);
			
			// make the next prediction -------------- MulAndExp
			yk_prediction=StaticFunctions.MulAndExponent(xk_estimate, hk_vector, (float)accelerationFactor);
		}
		else
		{
			// can't make a prediction yet
			yk_prediction=xk_estimate.copy();
		}
		
		// make a copy of the estimate to use as previous next time
		xkm1_previous=xk_estimate.copy();
		
		// return the prediction
		return yk_prediction.copy();
				
	}
	
	double computeAccelerationFactorMultiplicative(Img<T> yk_iterated, Img<T> yk, Img<T> xk, Img<T> xkm1)
	{
		// divide
		gk=StaticFunctions.Divide(yk_iterated, yk_prediction);
		
		if (gkm1!=null)
		{
			// log dot
			double numerator=DotProductLog(xk, gk, xkm1, gkm1);
			double denominator=DotProductLog(xkm1, gkm1, xkm1, gkm1);
			
			System.out.println("num: "+numerator+" denom "+denominator);			
			gkm1=gk.copy();
			
			//double accelerationFactor = Math.max(numerator/denominator, 0.0);
			double accelerationFactor=numerator/denominator;
			
			return accelerationFactor;
		}
		
		gkm1=gk.copy();
		
		return 0.0f;
	}
	
	/*
	 * multiply inputOutput by input and place the result in input
	 */
	public double DotProductLog(final Img<T> x1, final Img<T> u1, final Img<T> x2, final Img<T> u2)
	{
		final Cursor<T> cursorU1 = u1.cursor();
		final Cursor<T> cursorU2 = u2.cursor();
		
		final Cursor<T> cursorX1 = x1.cursor();
		final Cursor<T> cursorX2 = x2.cursor();
		
		double dotProduct=0.0d;
		
		
		while (cursorU1.hasNext())
		{
			cursorU1.fwd();
			cursorU2.fwd();
			cursorX1.fwd();
			cursorX2.fwd();
			
			double u1val=cursorU1.get().getRealDouble();
			double u2val=cursorU2.get().getRealDouble();
			
			double thresh=0.00001;
			
			if ( (u1val>thresh) && (u2val>thresh) )
			{
				double val1=cursorX1.get().getRealDouble()*Math.log(u1val);
				double val2=cursorX2.get().getRealDouble()*Math.log(u2val);
				
				dotProduct+=val1*val2;
			}
			
			//double val1=Math.log(cursorU1.get().getRealDouble());
			//double val2=Math.log(cursorU2.get().getRealDouble());
			
			
		}
		
		
		return dotProduct;
	}
}
