/**
 * Based on code by Ignazio Gallo.  Code has been modified for imglib2. 
 * Original copyright notice is below...
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * <p>
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <p>
 * This plugin adds Poisson distributed noise to each pixel of an image. This
 * plugin uses a simple way to generate random Poisson-distributed numbers given
 * by Knuth (http://en.wikipedia.org/wiki/Donald_Knuth).
 * <p>
 * Poisson noise or (particularly in electronics) as shot noise is a type of
 * electronic noise that occurs when the finite number of particles that carry
 * energy, such as electrons in an electronic circuit or photons in an optical
 * device, is small enough to give rise to detectable statistical fluctuations
 * in a measurement. It is important in electronics, telecommunications, and
 * fundamental physics.
 * <p>
 * Changes: <br>
 * 30/nov/2008<br>
 * - subtracted the mean value before adding the noise to the signal. This in
 * order to distribute the noise around the signal.<br>
 * - Added the MEAN_FACTOR constant to obtain a significant noise working with
 * float images and with a small Lambda (mean) value.
 * 
 * @author Ignazio Gallo(ignazio.gallo@gmail.com,
 *         http://www.dicom.uninsubria.it/~ignazio.gallo/)
 * @since 18/nov/2008
 * 
 * @version 1.1
 */

package com.truenorth.functions.noise;

import net.imglib2.img.Img;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.IterableInterval;
import net.imglib2.Cursor;

import net.imglib2.type.numeric.RealType;

//import com.sun.tools.javac.resources.javac;

public class NoiseGenerator
{
	final static double MEAN_FACTOR = 2.0;
	static double noiseMean = 2;
	
	public static<T extends RealType<T>> void AddPoissonNoise(RandomAccessibleInterval<T> img)
	{
		
	}
	
	/*public static<T extends RealType<T>> void AddPoissonNoise(Img<T> img)
	{
		Cursor<T> cursor = img.cursor();
		
		while (cursor.hasNext())
		{
			cursor.fwd();
			
			double val = cursor.get().getRealDouble();
			
			int noisy = poissonValue(val);
			
			cursor.get().setReal(noisy);
		}		
	}*/
	
	public static<T extends RealType<T>> void AddPoissonNoise(IterableInterval<T> img)
	{
		Cursor<T> cursor = img.cursor();
		
		final long startTime = System.currentTimeMillis();

		while (cursor.hasNext())
		{
			cursor.fwd();
			
			double val = cursor.get().getRealDouble();
			
			int noisy = poissonValue(val);
			
			cursor.get().setReal(noisy);
		}		
		
		System.out.println("Processsing time: "+(System.currentTimeMillis() - startTime));
	}
	
	public static<T extends RealType<T>> void AddPoissonNoise(Img<T> img, double mean)
	{
		Cursor<T> cursor = img.cursor();
		
		while (cursor.hasNext())
		{
			cursor.fwd();
			
			double val = cursor.get().getRealDouble();
			
			int noisy = poissonValue(mean);
			
			cursor.get().setReal(val+noisy);
			
		}		
	}
	
	public static int poissonValue(double lambda)
	{
		double L = Math.exp(-lambda);
		double p = 1.0;
		int k=0;
		
		do
		{
			k++;
			p*=java.lang.Math.random();
		}while (p>L);
		
		return k-1;		
	}
}
