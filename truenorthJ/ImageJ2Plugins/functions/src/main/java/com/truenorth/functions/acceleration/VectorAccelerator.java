package com.truenorth.functions.acceleration;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

import com.truenorth.functions.StaticFunctions;

public class VectorAccelerator <T extends RealType<T>> implements Accelerator<T>
{
	Img<T> xkm1_previous=null;
	Img<T> yk_prediction=null;
	Img<T> hk_vector=null;
	
	Img<T> gk;
	Img<T> gkm1;
	
	double accelerationFactor=0.0f;
	
	@Override
	public Img<T> Accelerate(Img<T> yk_iterated)
	{
		// use the iterated prediciton and the previous value of the prediciton
		// to calculate the acceleration factor
		if (yk_prediction!=null)
		{
			//StaticFunctions.showStats(yk_iterated);
			//StaticFunctions.showStats(yk_prediction);
			
			accelerationFactor=computeAccelerationFactor(yk_iterated, yk_prediction);
			
			System.out.println(accelerationFactor);
		}
		
		// current estimate for x is yk_iterated
		Img<T> xk_estimate=yk_iterated;
		
		// calculate the change vector between x and x previous
		if (xkm1_previous!=null)
		{
			hk_vector=StaticFunctions.Subtract(xk_estimate, xkm1_previous);
			
			// make the next prediction
			yk_prediction=StaticFunctions.AddAndScale(xk_estimate, hk_vector, (float)accelerationFactor);
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
	
	double computeAccelerationFactor(Img<T> yk_iterated, Img<T> yk)
	{
		gk=StaticFunctions.Subtract(yk_iterated, yk_prediction);
		
		if (gkm1!=null)
		{
			double numerator=StaticFunctions.DotProduct(gk, gkm1);
			double denominator=StaticFunctions.DotProduct(gkm1, gkm1);
			
			return numerator/denominator; 
		}
		
		gkm1=gk;
		
		return 0.0f;
	}
	
	
}
