package com.truenorth.commands.fft;

import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.RandomAccessibleInterval;

//import imagej.ui.UIService;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Attr;

import com.truenorth.commands.Constants;
import com.truenorth.functions.StaticFunctions;
import com.truenorth.functions.fft.filters.FrequencyFilter;
import com.truenorth.functions.fft.filters.DeconvolutionStats;
import com.truenorth.functions.fft.filters.IterativeFilterCallback;
import com.truenorth.functions.fft.filters.IterativeFilter;
import com.truenorth.functions.fft.filters.AbstractIterativeFilter.FirstGuessType;

import net.imagej.Dataset;

/**
 * 
 * @author bnorthan
 * base class for iterative frequency based filters
 * @param <T>
 */
public abstract class IterativeFilterCommand<T extends RealType<T> & NativeType<T>> extends AbstractFrequencyFilterCommand<T>
{
		@Parameter
		protected int iterations;
		
		@Parameter(required=false, persist=false)
		protected Dataset truth=null;
		
		@Parameter(required=false, persist=false)
		protected Dataset firstGuess=null;
		
		@Parameter(label="first guess", choices = {Constants.FirstGuess.measuredImage, Constants.FirstGuess.constant, Constants.FirstGuess.blurredMeasured, Constants.FirstGuess.input})
		protected String firstGuessType;
		
		@Parameter(required=false, persist=false, label="convolution strategy", choices = {Constants.ConvolutionStrategy.circulant, Constants.ConvolutionStrategy.noncirculant})
		protected String convolutionStrategy=Constants.ConvolutionStrategy.circulant;
		
		// TODO: Think through how to configure for non-circulant mode.   
		
		@Parameter(required=false, persist=false, attrs=@Attr(name="ShowInChainedGUI", value="false"))
		protected long imageWindowX=-1;
		
		@Parameter(required=false, persist=false, attrs=@Attr(name="ShowInChainedGUI", value="false"))
		protected long imageWindowY=-1;
		
		@Parameter(required=false, persist=false, attrs=@Attr(name="ShowInChainedGUI", value="false"))
		protected long imageWindowZ=-1;
		
		@Parameter(required=false, persist=false, attrs=@Attr(name="ShowInChainedGUI", value="false"))
		protected long psfWindowX=-1;
		
		@Parameter(required=false, persist=false, attrs=@Attr(name="ShowInChainedGUI", value="false"))
		protected long psfWindowY=-1;
		
		@Parameter(required=false, persist=false, attrs=@Attr(name="ShowInChainedGUI", value="false"))
		protected long psfWindowZ=-1;
		
		// The callback.  Can be over-ridden to implement more complex status and info updates.
		protected DeconvolutionStats<FloatType> stats;
		
		Img<T> imgTruth=null;
		
		/**
		 * abstract function used to create the iterative algorithm that will be applied
		 * @param region
		 * @return
		 */
		protected abstract IterativeFilter<T,T> createIterativeAlgorithm(RandomAccessibleInterval<T> region);
		
		protected FrequencyFilter<T,T> createAlgorithm(RandomAccessibleInterval<T> region)
		{
			IterativeFilter<T,T> iterativeFilter=  createIterativeAlgorithm(region);
			
			// set the number of iterations and the callback
			iterativeFilter.setMaxIterations(iterations);
			iterativeFilter.setCallback(callback);
						
			if (this.firstGuessType.equals(Constants.FirstGuess.measuredImage))
			{
				iterativeFilter.setFirstGuessType(FirstGuessType.MEASURED);
			}
			else if (this.firstGuessType.equals(Constants.FirstGuess.constant))
			{
				iterativeFilter.setFirstGuessType(FirstGuessType.CONSTANT);
			}
			else if (this.firstGuessType.equals(Constants.FirstGuess.blurredMeasured))
			{
				iterativeFilter.setFirstGuessType(FirstGuessType.BLURRED_MEASURED);
			}
			else if (this.firstGuessType.equals(Constants.FirstGuess.input))
			{
				if (firstGuess!=null)
				{
					// TODO: handle multi-volume first guess data set -- this code will only work
					// for the case of 1 volume.
					Img<T> firstGuessImg=(Img<T>)(firstGuess.getImgPlus().getImg());
					iterativeFilter.setFirstGuessType(FirstGuessType.MEASURED);
					iterativeFilter.setEstimate(firstGuessImg);
				}
				else
				{
					iterativeFilter.setFirstGuessType(FirstGuessType.MEASURED);
				}
			}
			
			System.out.println("convolution strategy: "+this.convolutionStrategy);
			
			if (this.convolutionStrategy.equals(Constants.ConvolutionStrategy.noncirculant))
			{
				System.out.println("noncirculant convolution strategy detected");
				
				System.out.println("image window x "+imageWindowX);
				System.out.println("image window y "+imageWindowY);
				System.out.println("image window z "+imageWindowZ);
				System.out.println();
				System.out.println("psf window x "+psfWindowX);
				System.out.println("psf window y "+psfWindowY);
				System.out.println("psf window z "+psfWindowZ);
				
				long[] k=new long[3];
				k[0]=imageWindowX;
				k[1]=imageWindowY;
				k[2]=imageWindowZ;
				
				long[] l=new long[3];
				l[0]=psfWindowX;
				l[1]=psfWindowY;
				l[2]=psfWindowZ;
				
				iterativeFilter.setNonCirculantConvolutionStrategy(k, l);
			}
			
			return iterativeFilter;
		}

		IterativeFilterCallback callback=new IterativeFilterCallback() {
			public void DoCallback(int iteration, RandomAccessibleInterval image, Img estimate, Img reblurred, long executionTime)
			{
				System.out.println("Iteration: "+iteration);
				System.out.println("Execution time: "+executionTime);
				System.out.println();
				
	//			uiService.getStatusService().showStatus(iteration, iterations, "Iteration: "+iteration);
				stats.CalculateStats(iteration, image, estimate, reblurred, null, null, null, true);
				
			/*	if (truth!=null)
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
				}*/
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
