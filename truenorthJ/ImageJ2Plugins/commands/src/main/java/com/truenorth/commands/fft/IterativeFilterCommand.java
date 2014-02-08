package com.truenorth.commands.fft;

import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import net.imglib2.RandomAccessibleInterval;

//import imagej.ui.UIService;

import org.scijava.plugin.Parameter;

import com.truenorth.commands.Constants;
import com.truenorth.functions.StaticFunctions;
import com.truenorth.functions.fft.filters.FrequencyFilter;
import com.truenorth.functions.fft.filters.DeconvolutionStats;
import com.truenorth.functions.fft.filters.IterativeFilterCallback;

import imagej.data.Dataset;

/**
 * 
 * @author bnorthan
 * base class for iterative frequency based filters
 * @param <T>
 */
public abstract class IterativeFilterCommand<T extends RealType<T> & NativeType<T>> extends AbstractFrequencyFilterCommand<T>
{
		@Parameter
		int iterations;
		
		@Parameter(required=false, persist=false)
		Dataset truth=null;
		
		@Parameter(required=false, persist=false)
		Dataset firstGuess=null;
		
		//@Parameter(required=false, persist=false)
		//Dataset firstGuess=null;
		
		@Parameter(label="first guess", choices = {Constants.FirstGuess.measuredImage, Constants.FirstGuess.constant, Constants.FirstGuess.blurredInputImage, Constants.FirstGuess.input})
		String firstGuessType;
				
		Img<T> imgTruth=null;
		
		// The callback.  Can be over-ridden to implement more complex status and info updates.
		DeconvolutionStats<FloatType> stats;
			
		IterativeFilterCallback callback=new IterativeFilterCallback() {
			public void DoCallback(int iteration, RandomAccessibleInterval image, Img estimate, Img reblurred)
			{
				System.out.println("Iteration: "+iteration);
				
	//			uiService.getStatusService().showStatus(iteration, iterations, "Iteration: "+iteration);
				stats.CalculateStats(iteration, image, estimate, reblurred, null, null, null, true);
				
				if (truth!=null)
				{
					Img<T> truthImg=(Img<T>)(truth.getImgPlus().getImg());
					
					long[] imgSize=new long[image.numDimensions()];
					long[] truthSize=new long[truthImg.numDimensions()];
					
					long[] start=new long[image.numDimensions()];
					long[] finish=new long[image.numDimensions()];
								
					for (int i=0;i<image.numDimensions();i++)
					{
						imgSize[i]=image.dimension(i);
						truthSize[i]=truth.dimension(i);
							
						start[i]=(imgSize[i]-truthSize[i])/2;
						finish[i]=start[i]+truthSize[i]-1;
									
						System.out.println("start/finish: "+start[i]+" "+finish[i]);
					}
					
					double error=StaticFunctions.squaredErrorWithOffset(truthImg, estimate, start);
					System.out.println("Error is: "+error);
				}
				System.out.println();
			}
		};
		
		@Override 
		protected void preProcess()
		{
			stats= new DeconvolutionStats<FloatType>(iterations);
			
			super.preProcess();
		}
		
		@Override
		protected void setParameters(FrequencyFilter filter)
		{
			
		}
}
