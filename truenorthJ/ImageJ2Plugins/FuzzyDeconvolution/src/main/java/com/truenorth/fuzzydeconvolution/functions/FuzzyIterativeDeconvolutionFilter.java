package com.truenorth.fuzzydeconvolution.functions;

import com.truenorth.functions.psf.PsfGenerator;

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.MultiThreaded;
import net.imglib2.algorithm.OutputAlgorithm;  

import java.io.*;

import com.truenorth.functions.FuzzyUtilities;

import com.truenorth.functions.fft.filters.IterativeFilter;
import com.truenorth.functions.fft.filters.IterativeFilterCallback;
import com.truenorth.functions.fft.filters.RichardsonLucyFilter;
import com.truenorth.functions.fft.filters.DeconvolutionStats;

import com.truenorth.functions.fft.filters.IterativeFilterFactory;
import com.truenorth.functions.fft.filters.IterativeFilterFactory.IterativeFilterType;

/**
 * Implements the fuzzy logic deconvolution routine presented at Photonics West 2013
 * 
 * Brian Northan "Fuzzy Logic Components for Iterative Deconvolution Systems" 
 * Progress in Biomedical Optics and Imaging Vol. 14., No. 25
 * 
 * 
 * @author bnorthan
 *
 * @param <T>
 */
public class FuzzyIterativeDeconvolutionFilter <T extends RealType<T>, S extends RealType<S>> implements MultiThreaded, OutputAlgorithm<Img<T>>, Benchmark
{	
	IterativeFilter<T,FloatType> iterativeFilter;
	IterativeFilter<T,FloatType> iterativeFilterCandidate;
	
	RandomAccessibleInterval<T> image;
	ImgFactory<T> imgFactory;
	
	Img<FloatType> kernelOne;
	Img<FloatType> kernelTwo;
		
	Img<T> output;
	
	String errorMessage="";
	int numThreads;
	long processingTime;
	
	int[] dim;
	
	float[] space;
	double emissionWavelength;
	double numericalAperture;
	double designImmersionOilRefractiveIndex;
	double designSpecimenLayerRefractiveIndex;
	double actualImmersionOilRefractiveIndex;
	double actualSpecimenLayerRefractiveIndex;
	double actualPointSourceDepthInSpecimenLayer;
	
	String dataDirectory=null;
	String dataFileName="fuzzydata.txt";
	
	PsfGenerator generator;
	Img<T> truth;
	
	String tempName=null;
	
	double firstRIToTry=1.40;
	
	int iterations=100;
	
	IterativeFilterCallback callback=null;
	
	IterativeFilterType type;
	
	public FuzzyIterativeDeconvolutionFilter(Img<T> image,
			float[] space,
			double emissionWavelength,
			double numericalAperture,
			double designImmersionOilRefractiveIndex,
			double designSpecimenLayerRefractiveIndex,
			double actualImmersionOilRefractiveIndex,
			double actualSpecimenLayerRefractiveIndex,
			double actualPointSourceDepthInSpecimenLayer,
			double firstRIToTry,
			IterativeFilterType type)
	{
		this(image,
				image.factory(),
				space,
				emissionWavelength,
				numericalAperture,
				designImmersionOilRefractiveIndex,
				designSpecimenLayerRefractiveIndex,
				actualImmersionOilRefractiveIndex,
				actualSpecimenLayerRefractiveIndex,
				actualPointSourceDepthInSpecimenLayer,
				firstRIToTry,
				type);
	}
	
	public FuzzyIterativeDeconvolutionFilter(RandomAccessibleInterval<T> image,
			ImgFactory<T> imgFactory,
			float[] space,
			double emissionWavelength,
			double numericalAperture,
			double designImmersionOilRefractiveIndex,
			double designSpecimenLayerRefractiveIndex,
			double actualImmersionOilRefractiveIndex,
			double actualSpecimenLayerRefractiveIndex,
			double actualPointSourceDepthInSpecimenLayer,
			double firstRIToTry,
			IterativeFilterType type)
	{
		this.image=image;
		this.imgFactory=imgFactory;
		this.type=type;//=(F)(new RichardsonLucyFilter<T, FloatType>(image, kernelOne, imgFactory, kernelOne.factory()));
		//rl = new RichardsonLucyFilter<T, FloatType>(image, kernelOne, imgFactory, kernelOne.factory());
		
		
		this.firstRIToTry=firstRIToTry;
		
		this.dim = new int[image.numDimensions()];
		
		for (int i=0;i<image.numDimensions();i++)
		{
			this.dim[i]=(int)(image.dimension(i));
		}
		
		generator = new PsfGenerator(dim,
							space,
							emissionWavelength,
							numericalAperture,
							designImmersionOilRefractiveIndex,
							designSpecimenLayerRefractiveIndex,
							actualImmersionOilRefractiveIndex,
							actualSpecimenLayerRefractiveIndex,
							actualPointSourceDepthInSpecimenLayer,
							PsfGenerator.PsfType.WIDEFIELD,
							PsfGenerator.PsfModel.GIBSON_LANI);
	}
	
	@Override
	public boolean process()
	{
		boolean result=true;
		double inc=0.01; 
	
		double ri=firstRIToTry;
		double riCandidate=ri-inc;
			
		System.out.println("generating kernel 1: ");
			
		// generate kernel 1
		kernelOne=generator.CallGeneratePsf(ri);
			
		System.out.println("generating kernel 2: ");
			
		// generate kernel 2
		kernelTwo=generator.CallGeneratePsf(riCandidate);
				
		iterativeFilter = IterativeFilterFactory.GetIterativeFilter(type, image, kernelOne, imgFactory, kernelOne.factory());		
		iterativeFilterCandidate = IterativeFilterFactory.GetIterativeFilter(type, image, kernelTwo, imgFactory, kernelTwo.factory());
			
		iterativeFilter.setFlipKernel(false);
		iterativeFilterCandidate.setFlipKernel(false);
				
		iterativeFilter.setMaxIterations(iterations);
		iterativeFilterCandidate.setMaxIterations(iterations);
				
		iterativeFilter.initialize();
		iterativeFilterCandidate.initialize();
		
		iterativeFilter.setTruth(truth);
		iterativeFilterCandidate.setTruth(truth);
				
		final DeconvolutionStats<T> stats1 = new DeconvolutionStats<T>(iterations);
		final DeconvolutionStats<T> stats2 = new DeconvolutionStats<T>(iterations);

		double std_T=-1.0;
		double max_T=-1.0;
		double L_T=-1.0;
		
		int j=0;
		for (int i=1;i<iterations;i++)
		{
			
			System.out.println("Iteration: "+(i));
			
			System.out.println("Perform iteration with current RI: ="+ri);
			iterativeFilter.performIterations(i);
			System.out.println("Perform iteration with candidate RI: ="+riCandidate);
			iterativeFilterCandidate.performIterations(i);
			
			// calculate stats of estimate produced with current ri
			stats1.CalculateStats(i-1, image, iterativeFilter.getEstimate(), iterativeFilter.getReblurred(), null, null, null, false);
			// calculate stats of estimate produced with candidate ri
			stats2.CalculateStats(i-1, image, iterativeFilterCandidate.getEstimate(), iterativeFilterCandidate.getReblurred(), null, null, null, false);
		
			std_T=(stats1.getArrayStdEstimate()[i-1]+stats2.getArrayStdEstimate()[i-1])/2;
			max_T=(stats1.getArrayMax()[i-1]+stats2.getArrayMax()[i-1])/2;
			L_T=(stats1.getArrayLikelihood()[i-1]+stats2.getArrayLikelihood()[i-1])/2;
			
			double fuzzyOne = fuzzyFitness(ri, stats1.getArrayStdEstimate()[i-1], std_T,
									stats1.getArrayMax()[i-1], max_T,
									stats1.getArrayLikelihood()[i-1], L_T);
			
			double fuzzyTwo = fuzzyFitness(riCandidate, stats2.getArrayStdEstimate()[i-1], std_T,
											stats2.getArrayMax()[i-1], max_T,
											stats2.getArrayLikelihood()[i-1], L_T);
			
			j++;
			
			System.out.println("RIs are: "+ri+" "+riCandidate);
			System.out.println("Fuzzy values are: "+fuzzyOne+" "+fuzzyTwo);
			
	/*		System.out.println("sums are: "+stats1.getArraySumEstimate()[i-1]+" "+stats2.getArraySumEstimate()[i-1]);
			System.out.println("stds are: "+stats1.getArrayStdEstimate()[i-1]+" "+stats2.getArrayStdEstimate()[i-1]);
			System.out.println("maxes are: "+stats1.getArrayMax()[i-1]+" "+stats2.getArrayMax()[i-1]);
		*/
			
			System.out.println();
			System.out.println();
			
			double fuzzyThreshold=0.13;
			
			if (fuzzyOne>fuzzyThreshold)
			{
				// ri stays the same, compute a new riCandidate
				
				// if riCandidate was above ri
				if (riCandidate>ri)
				{
					// try going below ri
					riCandidate = ri-inc;
				}
				// if riCandidate was below ri
				else
				{
					// try going above ri
					riCandidate=ri+inc;
				}
				
				// generate a new kernel with the new ri
				kernelTwo=generator.CallGeneratePsf(riCandidate);
				
				// set the kernel and current image
				iterativeFilterCandidate.setKernel(kernelTwo);
				iterativeFilterCandidate.setEstimate(iterativeFilter.getEstimate());	
				
				j=0;
				
				std_T=stats1.getArrayStdEstimate()[i-1];
				max_T=stats1.getArrayMax()[i-1];
				L_T=stats1.getArrayLikelihood()[i-1];
				
			}
			else if (fuzzyTwo>fuzzyThreshold)
			{
				// ri becomes riCandidate
				// compute the new riCandidate
				if (riCandidate>ri)
				{
					ri=riCandidate;
					riCandidate=ri+inc;
				}
				else
				{
					ri=riCandidate;
					riCandidate=ri-inc;
				}
				
				// switch iterativeFilter and iterativeFilterCandidate
				IterativeFilter<T,FloatType> temp = iterativeFilter;
				iterativeFilter=iterativeFilterCandidate;
				iterativeFilterCandidate=temp;
				
				// generate a new kernel with the new ri
				kernelTwo=generator.CallGeneratePsf(riCandidate);
				
				// set the kernel and current image
				iterativeFilterCandidate.setKernel(kernelTwo);
				iterativeFilterCandidate.setEstimate(iterativeFilter.getEstimate());
				j=0;
				
				std_T=stats2.getArrayStdEstimate()[i-1];
				max_T=stats2.getArrayMax()[i-1];
				L_T=stats2.getArrayLikelihood()[i-1];
			}
			
			// don't search above ri=1.51
			if (ri>1.51)
			{
				ri=1.51;
				riCandidate=1.51-inc;
			}
			
			if (callback != null)
			{
				callback.DoCallback(i, image, iterativeFilter.getEstimate(), iterativeFilter.getReblurred());
			}
			// write data to the log file
			if (dataDirectory!=null)
			{
			
				String dataFileFullName=dataDirectory+dataFileName;
			
				try
				{
					FileWriter fstream = new FileWriter(dataFileFullName, true);
					BufferedWriter out = new BufferedWriter(fstream);
				
					out.newLine();
					out.write(ri+" "+riCandidate+" "+j);
					out.newLine();
					out.newLine();
				
					out.write("sums are: "+std_T+" "+stats1.getArraySumEstimate()[i-1]+" "+stats2.getArraySumEstimate()[i-1]);
					out.newLine();
					out.write("stds are: "+std_T+" "+stats1.getArrayStdEstimate()[i-1]+" "+stats2.getArrayStdEstimate()[i-1]);
					out.newLine();
					out.write("maxes are: "+max_T+" "+stats1.getArrayMax()[i-1]+" "+stats2.getArrayMax()[i-1]);
					out.newLine();
					out.write("likelihoods are: "+L_T+" "+stats1.getArrayLikelihood()[i-1]+" "+stats2.getArrayLikelihood()[i-1]);
					out.newLine();
				
					out.write("fuzzy values: "+fuzzyOne+" "+fuzzyTwo);
					out.newLine();
				
					out.close();
				
					if (tempName!=null)
					{
						Img<T> temp=iterativeFilter.getEstimate();
						//new ImgSaver<T>().saveImg(tempName, temp);
					}
				}
				catch(IOException ex)
				{
				
				}
			}
		}
		
		output = iterativeFilter.getEstimate();
		
		return result;
	}
	
	double fuzzyFitness(double ri, double std, double std_T, double max, double max_T, double L, double L_T)
	{
		// calculate condition "is standard deviation high??"
		double stdIsHigh=FuzzyUtilities.sigmoid( (std-std_T)/std_T, 100);
		
		// calculate condition "is max high??"
		double maxIsHigh=FuzzyUtilities.sigmoid( (max-max_T)/std_T, 0.01);
		
		// calculate condition "is likelihood high??"
		double likelihoodIsHigh=FuzzyUtilities.sigmoid( (L-L_T)/L_T, 1);
		
		// calculate final fuzzy value as the fuzzy and of the other values
		double finalFuzzy=stdIsHigh*maxIsHigh*likelihoodIsHigh;;
		
		// print out information about the calculation
	/*	System.out.println("*************************************");
		System.out.println("ri: "+ri);
		System.out.println("std: "+std+" "+std_T+" "+stdIsHigh);
		System.out.println("max: "+max+" "+max_T+" "+maxIsHigh);
		System.out.println("likelihood: "+L+" "+L_T+" "+likelihoodIsHigh);
		System.out.println("final fuzzy value: "+finalFuzzy);
		System.out.println("*************************************");*/
	
		return finalFuzzy;
	}

	@Override
	public long getProcessingTime() 
	{ 
		return processingTime; 
	}
	
	@Override
	public void setNumThreads() 
	{ 
		this.numThreads = Runtime.getRuntime().availableProcessors(); 
	}
	
	@Override
	public void setNumThreads( final int numThreads ) 
	{ 
		this.numThreads = numThreads; 
	}

	@Override
	public int getNumThreads() 
	{ 
		return numThreads; 
	}	
	
	@Override
	public Img<T> getResult() 
	{ 
		return output; 
	}

	@Override
	public boolean checkInput() 
	{
		return false;
	}
	
	@Override
	public String getErrorMessage()  
	{ 
		return errorMessage; 
	}
	
	public IterativeFilter<T,FloatType> getIterativeFilter()
	{
		return iterativeFilter;
	}
	
	public IterativeFilter<T,FloatType> getIterativeFilterCandidate()
	{
		return iterativeFilterCandidate;
	}
	
	public void setDataDirectory(String dataDirectory)
	{
		this.dataDirectory = dataDirectory;
	}
	
	public void setTruth(Img<T> truth)
	{
		this.truth=truth;
	}
	
	public Img<FloatType> getPsf()
	{
		return kernelOne;
	}
	
	public void setDataFileName(String dataFileName)
	{
		this.dataFileName=dataFileName;
	}
	
	public void setIterations(int iterations)
	{
		this.iterations=iterations;
	}
	
	public void setTempName(String tempName)
	{
		this.tempName=tempName;
	}
	
	public void setCallback(IterativeFilterCallback callback)
	{
		this.callback = callback;
	}
}


