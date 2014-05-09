package com.truenorth.functions.fft.filters;

import com.truenorth.functions.StaticFunctions;
import com.truenorth.functions.fft.SimpleFFT;
import com.truenorth.functions.fft.SimpleFFTFactory;
import com.truenorth.functions.fft.SimpleImgLib2FFT;
import com.truenorth.functions.phantom.Phantoms;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

import net.imglib2.Point;

/**
 * Base class for an iterative deconvolution filter
 * 
 * @author bnorthan
 *
 * @param <T>
 * @param <S>
 */
public abstract class AbstractIterativeFilter<T extends RealType<T>, S extends RealType<S>> extends AbstractFrequencyFilter<T,S> 
	implements IterativeFilter<T, S>
{
	/**
	 * first guess types 
	 *
	 * MEASURED - the measured image (the input image)  
	 * CONSTANT - flat sheet
	 * BLURRED_INPUT - blurred version of the measured input image
	 * USER_IMAGE - user specifies first guess
	 */
	public static enum FirstGuessType{MEASURED, CONSTANT, BLURRED_MEASURED, USER_INPUT};
	
	/**
	 * convolution strategy
	 *
	 * circulant - convolution and correlation performed using circulant model
	 * 
	 * noncirculant - images have been zero extended as to avoid circular deconvolution.  
	 * 	http://bigwww.epfl.ch/deconvolution/challenge/index.html?p=documentation/overview
	 */
	public static enum ConvolutionStrategy{CIRCULANT, NON_CIRCULANT};

	/**
	 * @param image
	 * @param kernel
	 * @param imgFactory
	 * @param kernelImgFactory
	 * @param fftImgFactory
	 */
	public AbstractIterativeFilter( final RandomAccessibleInterval<T> image, final RandomAccessibleInterval<S> kernel,
			   final ImgFactory<T> imgFactory, final ImgFactory<S> kernelImgFactory,
			   final ImgFactory<ComplexFloatType> fftImgFactory )
	{
		super( image, kernel, imgFactory, kernelImgFactory, fftImgFactory );
	}
	
	/**
	 * @param image
	 * @param kernel
	 * @param imgFactory
	 * @param kernelImgFactory
	 * @throws IncompatibleTypeException
	 */
	public AbstractIterativeFilter(final RandomAccessibleInterval<T> image, 
			final RandomAccessibleInterval<S> kernel,
			final ImgFactory<T> imgFactory,
			final ImgFactory<S> kernelImgFactory) throws IncompatibleTypeException
	{
		super(image, kernel, imgFactory, kernelImgFactory);
	}
	
	/**
	 * @param image
	 * @param kernel
	 * @param fftImgFactory
	 */
	public AbstractIterativeFilter( final Img<T> image, final Img<S> kernel, final ImgFactory<ComplexFloatType> fftImgFactory )
	{
		super( image, kernel, fftImgFactory );
	}
	
	/**
	 * @param image
	 * @param kernel
	 * @throws IncompatibleTypeException
	 */
	public AbstractIterativeFilter( final Img< T > image, final Img< S > kernel ) throws IncompatibleTypeException
	{
		super( image, kernel );
	}	
	
	Img<T> estimate;
	Img<T> reblurred;
		
	Img<ComplexFloatType> estimateFFT;
	
	SimpleFFT<T, ComplexFloatType> fftEstimate;
	
	int iteration=0;
	
	int maxIterations = 10;
	
	int computeStatsInterval = 50;
			
	IterativeFilterCallback<T> callback=null;
	
	boolean keepOldEstimate=false;
	
	FirstGuessType firstGuessType=FirstGuessType.MEASURED;
	
	ConvolutionStrategy convolutionStrategy=ConvolutionStrategy.CIRCULANT;
	
	// size of PSF space
	long[] l;
	
	// size of measured image space
	long[] k;
	
	Img<T> normalization=null;
	
	/**
	 * perform the initial ffts, set first guess
	 * @return
	 */
	public boolean initialize()
	{
		boolean result;
		
		// perform fft of input
		
		result = performInputFFT();
		
		if (!result)
		{
			return result;
		}
		
		// perform fft of psf	
		result = performPsfFFT();
		
		if (!result)
		{
			return result;
		}
		
		// set first guess of the estimate if it has not been set explicitly
		if (estimate==null)
		{
			final T type = Util.getTypeFromInterval(image);
			estimate = imgFactory.create(image, type);
			
			if (firstGuessType==FirstGuessType.MEASURED)
			{
				setEstimate(image);
			}
			else if (firstGuessType==FirstGuessType.BLURRED_MEASURED)
			{
				setEstimate(image);
				
				setEstimate(reblurred);
			}
			else if (firstGuessType==FirstGuessType.CONSTANT)
			{
				
				Iterable<T> iterableImage=Views.iterable(image);
				Iterable<T> iterableEstimate=Views.iterable(estimate);
				
				final double sum=StaticFunctions.sum(iterableImage);
				
				final long numPixels=image.dimension(0)*image.dimension(1)*image.dimension(2);
				
				final double constant=sum/(numPixels);
				
				StaticFunctions.set(iterableEstimate, constant);
						
				createReblurred();
				
			}
		}
		
		// create normalization if needed
		if(this.convolutionStrategy==ConvolutionStrategy.NON_CIRCULANT)
		{
			this.CreateNormalizationImage();
		}
					
		if (!result)
		{
			return result;
		}
					
		return true;
	}
	
	/**
	 * overrride process to perform deconvolution iterations.  Return true if successful.
	 */
	@Override
	public boolean process() 
	{		
		boolean result;
		
		initialize();
			
		result=performIterations(maxIterations);
		    
		if (result==true)
		{
			output = estimate;
		}
        
		return result;
	}
	
	/**
	 * Perform iterations
	 * 
	 * TODO: check a stopping criteria
	 * @param n
	 * @return
	 */
	public boolean performIterations(int n)  
	{
		boolean result=true;
		
		while (iteration<n)
		{
			Img<T> oldEstimate=null;
			
			long startTime=System.currentTimeMillis();
			
			// if tracking stats keep track of current estimate in order to compute relative change
			if (keepOldEstimate)
			{
				oldEstimate = estimate.copy();
			}
			
			// perform the iteration
			result=performIteration(imgFFT, kernelFFT);
			
			if (result!=true)
			{
				return result;
			}
			
			long iterationTime=System.currentTimeMillis()-startTime;
			
			// if a callback has been set
			if (callback!=null)
			{ 
				
				double remainder = java.lang.Math.IEEEremainder(iteration, computeStatsInterval);
			
				// call the callback if the callbackInteral is a divisor of the current iteration
				if (remainder == 0)
				{
					callback.DoCallback(iteration, image, estimate, reblurred, iterationTime);
				}
			}
			
			iterationTime=System.currentTimeMillis()-startTime;
			
			System.out.println("Iteration:"+iteration);
			System.out.println();
			
			output=estimate;
			
			iteration++;
			
		}
	
		return result;
	}
	
	/**
	 * Perform estimate FFT
	 * @return
	 */
	protected boolean performEstimateFFT()
	{
		if (estimateFFT == null)
		{
			// create a new fft
			fftEstimate = SimpleFFTFactory.GetSimpleFFt(image, imgFactory, fftImgFactory, new ComplexFloatType());
		}
						
		// perform the fft
		estimateFFT = fftEstimate.forward(estimate);
		
		return true;
	}
	
	/** 
	 * Create reblurred by convolving estimate and PSF
	 * @return
	 */
	protected boolean createReblurred()
	{
		// estimate FFT
		boolean result = performEstimateFFT();
		
		if (!result)
		{
			return result;
		}
				
		// complex multiply transformed current estimate with transformed psf
		StaticFunctions.InPlaceComplexMultiply(estimateFFT, kernelFFT);
		
		// compute inverse to get reblurred
		reblurred = fftEstimate.inverse(estimateFFT);
		
		return true;
	}
	
	/**
	 *  create the normalization image needed for the model described here 
	 *	http://bigwww.epfl.ch/deconvolution/challenge/index.html?p=documentation/overview
	 *	Richardson Lucy with the noncirculant model is described in the RLdeblur3D.m script
	 */
	protected void CreateNormalizationImage() 
	{
		int length=k.length;
	
		long[] n=new long[length];
		long[] fft_n;
		
		// calculate n - size of object space
		// n = k (measurement space) + l (psf space) -1
		for (int i=0;i<length;i++)
		{
			n[i]=k[i]+l[i]-1;	
		}
		
		// get size of fft space- may not be same size as object space due to fft padding
		fft_n=SimpleFFTFactory.GetPaddedInputSizeLong(n);
		
		// create the normalization image
		final T type = Util.getTypeFromInterval(image);
		normalization = imgFactory.create(image, type);
		
		
		Img<T> mask = imgFactory.create(image, type);
			
		// size of the measurement window
		Point size=new Point(3);
		size.setPosition(k[0], 0); //192
		size.setPosition(k[1], 1); //192
		size.setPosition(k[2], 2); //64
	
		// starting point of the measurement window when it is centered in fft space
		Point start=new Point(3);
		start.setPosition((fft_n[0]-k[0])/2, 0); //72
		start.setPosition((fft_n[1]-k[1])/2, 1); //84
		start.setPosition((fft_n[2]-k[2])/2, 2); //73
	
		// size of the object space
		Point maskSize=new Point(3);
		maskSize.setPosition(n[0], 0); //319
		maskSize.setPosition(n[1], 1); //319
		maskSize.setPosition(n[2], 2); //190
		
		// starting point of the object space within the fft space
		Point maskStart=new Point(3);
		maskStart.setPosition((fft_n[0]-n[0])/2+1, 0); //9
		maskStart.setPosition((fft_n[1]-n[1])/2+1, 1); //21
		maskStart.setPosition((fft_n[2]-n[2])/2+1, 2); //11
	
		// draw a cube the size of the measurement space
		Phantoms.drawCube(normalization, start, size, 1.0);
		
		// draw a cube the size of the object space
		Phantoms.drawCube(mask, maskStart, maskSize, 1.0);
	
		// forward FFT
		SimpleFFT<T, ComplexFloatType> fftTemp = 
				new SimpleImgLib2FFT<T, ComplexFloatType>(normalization, imgFactory, fftImgFactory, new ComplexFloatType() );
		Img<ComplexFloatType> temp1FFT= fftTemp.forward(normalization);
		
		//StaticFunctions.SaveImg(normalization, "/home/bnorthan/Brian2014/Images/General/Deconvolution/Grand_Challenge/EvaluationData/Extended/testFeb10/normalcube_.tif");
		//StaticFunctions.SaveImg(mask, "/home/bnorthan/Brian2014/Images/General/Deconvolution/Grand_Challenge/EvaluationData/Extended/testFeb10/mask_.tif");
		
		// complex conjugate multiply fft of output of step 2 and fft of psf to get normalization factor
		// as done in RLdeblur3D.m script from EPFL
		StaticFunctions.InPlaceComplexConjugateMultiply(temp1FFT, kernelFFT);
		
		// inverse fft
		normalization = fftTemp.inverse(temp1FFT);
		//StaticFunctions.SaveImg(normalization, "/home/bnorthan/Brian2014/Images/General/Deconvolution/Grand_Challenge/EvaluationData/Extended/testFeb10/normalconv_.tif");
		
		// fft space can be slightly larger then the object space so so use a mask to get
		// rid of any values outside the object space.   
		StaticFunctions.InPlaceMultiply(normalization, mask);
		
		//StaticFunctions.SaveImg(normalization, "/home/bnorthan/Brian2014/Images/General/Deconvolution/Grand_Challenge/EvaluationData/Extended/testFeb10/normalfirst_.tif");	
	}
	
	/**
	 *  sets max iterations
	 */
	public void setMaxIterations(int maxIterations)
	{
		this.maxIterations = maxIterations;
	}
	
	/**
	 * returns max iterations
	 * @return
	 */
	public int getMaxIterations()
	{
		return maxIterations;
	}
	
	/**
	 * set image estimate (without creating reblurred)
	 * @param estimate
	 */
	public void setEstimateImg(Img<T> estimate)
	{
		this.estimate=estimate;
	}
	
	/**
	 * set a callback for updating status
	 */
	public void setCallback(IterativeFilterCallback<T> callback)
	{
		this.callback = callback;
	}
	
	/**
	 * set the estimate and use it to create the reblurred signal 
	 */
	public void setEstimate(RandomAccessibleInterval<T> estimate)
	{
		StaticFunctions.copy2(estimate, this.estimate);
	
		// create reblurred (so it is ready for the first iteration)
	
		boolean result = createReblurred();
	
		if (!result)
		{
			// handle error
		}
	}
	
	/**
	 * get the current estimate
	 */
	public Img<T> getEstimate()
	{
		return estimate;
	}
	
	/**
	 * return the reblurred
	 */
	public Img<T> getReblurred()
	{
		return reblurred;
	}
	
	/**
	 * set the first guess type
	 */
	public void setFirstGuessType(FirstGuessType firstGuessType)
	{
		this.firstGuessType=firstGuessType;
	}
	
	/**
	 * set up for non-circulant convolution model.  the "measurement window size" and the
	 * "psf window size" are needed to calculate the normalization factor
	 *  
	 * @param k - measurement window size
	 * @param l - psf window size
	 */
	public boolean setNonCirculantConvolutionStrategy(long[] k, long[] l)
	{
		this.k=k;
		this.l=l;
		
		this.convolutionStrategy=ConvolutionStrategy.NON_CIRCULANT;
		
		System.out.println("set noncirculant convolution window");
		
		return true;
	}
	
	/**
	 * Abstract function that needs to be implemented as to perform an iteration of 
	 * a type of deconvolution.  
	 * @param a
	 * @param b
	 * @return
	 */
	protected abstract boolean performIteration( final Img< ComplexFloatType > a, final Img< ComplexFloatType > b );

}
