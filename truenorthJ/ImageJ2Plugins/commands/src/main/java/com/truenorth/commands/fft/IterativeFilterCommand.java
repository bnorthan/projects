package com.truenorth.commands.fft;

import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.RandomAccessibleInterval;

//import imagej.ui.UIService;

import org.scijava.plugin.Parameter;

import com.truenorth.functions.fft.filters.DeconvolutionStats;
import com.truenorth.functions.fft.filters.IterativeFilterCallback;

/**
 * 
 * @author bnorthan
 * base class for iterative frequency based filters
 * @param <T>
 */
public abstract class IterativeFilterCommand<T extends RealType<T> & NativeType<T>> extends AbstractFrequencyFilterCommand<T>
{
		//@Parameter
	//	private UIService uiService;

		@Parameter
		int iterations;
		
		// The callback.  Can be over-ridden to implement more complex status and info updates.
		
		DeconvolutionStats<FloatType> stats;
		
		IterativeFilterCallback callback=new IterativeFilterCallback() {
			public void DoCallback(int iteration, RandomAccessibleInterval image, Img estimate, Img reblurred)
			{
				System.out.println("Iteration: "+iteration);
				
	//			uiService.getStatusService().showStatus(iteration, iterations, "Iteration: "+iteration);
				stats.CalculateStats(iteration, image, estimate, reblurred, null, null, true);
				
				System.out.println();
			}
		};
		
		@Override 
		protected void preProcess()
		{
			stats= new DeconvolutionStats<FloatType>(iterations);
			super.preProcess();
		}
}
