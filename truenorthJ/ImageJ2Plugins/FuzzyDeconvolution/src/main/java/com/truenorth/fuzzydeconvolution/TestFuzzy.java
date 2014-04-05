package com.truenorth.fuzzydeconvolution;

import java.io.*;

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.planar.PlanarImgFactory;
//import net.imglib2.io.ImgIOException;
//import net.imglib2.io.ImgOpener;
//import net.imglib2.io.ImgSaver;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.img.Img;
//import net.imglib2.img.ImgPlus;

import com.truenorth.functions.StaticFunctions;
import com.truenorth.functions.fft.filters.DeconvolutionStats;

// used to test fuzzy deconvolution
class TestFuzzy       
{
	// args[0] is the name of the image
	// args[1] is the directory to write the stats file too
	
	public static void main(String[] args) //throws ImgIOException, IncompatibleTypeException, IOException
	{
	/*	System.out.println("Test Fuzzy");
		System.out.println("Number arguments..."+ args.length);
		
    	Img<FloatType> image = null;//new ImgOpener().openImg(args[0], new PlanarImgFactory<FloatType>(), new FloatType() );
    	
		float[] space=new float[3];
		
		System.out.println("Spacing: "+space[0]+" "+space[1]+" "+space[2]);
		
		// hard code the meta data Todo:  Read from file
	
		// todo: read dimensions and params properly from some args but for now 
		// just use default values
		
		space[0]=40;
		space[1]=40;
		space[2]=100;	
		double emissionWavelength=500.0;
		double numericalAperture=1.3;
		double designImmersionOilRefractiveIndex=1.515;
		double designSpecimenLayerRefractiveIndex=1.515;
		double actualImmersionOilRefractiveIndex=1.515;
		double actualSpecimenLayerRefractiveIndex=1.33;
		double actualPointSourceDepthInSpecimenLayer=10;
		
		String dataDirectory;
		
		// if there are at least 2 input arguments use the second argument as the name of the directory to 
		// save the data file too. 
		if (args.length>=2)
		{
			dataDirectory=args[1];
		}
		else
		{
			dataDirectory="/home/bnorthan/Brian2012/Round2/Paper/Data/";
		}
		
		Img<FloatType> truth=null;
		
		double firstRIToTry=1.43;
		
		if (args.length>=4)
		{
			System.out.println("First RI to try: "+args[4]);
			
			firstRIToTry = Double.parseDouble(args[4]);
		}
			
		// instantiate the fuzzy filter
		RichardsonLucyFuzzyFilter<FloatType> fuzzy = new RichardsonLucyFuzzyFilter<FloatType>(image,
																				space,
																				emissionWavelength,
																				numericalAperture,
																				designImmersionOilRefractiveIndex,
																				designSpecimenLayerRefractiveIndex,
																				actualImmersionOilRefractiveIndex,
																				actualSpecimenLayerRefractiveIndex,
																				actualPointSourceDepthInSpecimenLayer,
																				firstRIToTry);
		
		//todo: iterations should be a parameter
		int iterations=20;
		
		fuzzy.setIterations(iterations);
		
		if (args.length>=4)
		{
			String dataFileName = args[3]+"_"+firstRIToTry+".txt";
			fuzzy.setDataFileName(dataFileName);
		}
		
		
		fuzzy.setDataDirectory(dataDirectory);
		fuzzy.setTruth(truth);
		
		fuzzy.process();
		
		final DeconvolutionStats<FloatType> stats1 = new DeconvolutionStats<FloatType>(iterations);
		final DeconvolutionStats<FloatType> stats2 = new DeconvolutionStats<FloatType>(iterations);
		
		stats1.CalculateStats(iterations, image, fuzzy.getRL().getEstimate(), null, null, truth, false);
		stats2.CalculateStats(iterations, image, fuzzy.getRLCandidate().getEstimate(), null, null, truth, false);
		
		double[] arraySumEstimate1 = stats1.getArraySumEstimate();
		double[] arrayPowerEstimate1 = stats1.getArrayPowerEstimate();
		double[] arrayStdEstimate1 = stats1.getArrayStdEstimate();
		double[] arrayTvEstimate1 = stats1.getArrayTvEstimate();
		double[] arrayRelativeChange1 = stats1.getArrayRelativeChange();
		double[] arrayLikelihood1 = stats1.getArrayLikelihood();
		double[] arrayMax1 = stats1.getArrayMax();
		double[] arrayError1 = stats1.getArrayError();
		
		double[] arraySumEstimate2 = stats2.getArraySumEstimate();
		double[] arrayPowerEstimate2 = stats2.getArrayPowerEstimate();
		double[] arrayStdEstimate2 = stats2.getArrayStdEstimate();
		double[] arrayTvEstimate2 = stats2.getArrayTvEstimate();
		double[] arrayRelativeChange2 = stats2.getArrayRelativeChange();
		double[] arrayLikelihood2 = stats2.getArrayLikelihood();
		double[] arrayMax2 = stats2.getArrayMax();
		double[] arrayError2 = stats2.getArrayError();
		
		int numStats = arraySumEstimate1.length;
		
		String dataFileName;
		String resultFileName;
		String psfFileName;
		
		dataFileName = dataDirectory + "Data.txt";
		resultFileName = dataDirectory + "Result.tif";
		psfFileName = dataDirectory+"Psf.tif";
		
		FileWriter fstream = new FileWriter(dataFileName);
		BufferedWriter out = new BufferedWriter(fstream);
		
		for (int i=0;i<numStats;i++)
		{
			String str1 = i + " "
						+ arraySumEstimate1[i] + " " 
						+ arrayPowerEstimate1[i]+ " "
						+ arrayStdEstimate1[i]+" "
						+ arrayTvEstimate1[i]+" "
						+arrayRelativeChange1[i]+" "
						+arrayLikelihood1[i]+" "
						+arrayMax1[i]+" "
						+arrayError1[i];
			
		
			//System.out.println("Error is: "+arrayError[i]+" Max is: "+arrayMax[i]);
			//System.out.println();
						
			out.write(str1);
			out.newLine();
		}
		
		out.newLine();
		
		for (int i=0;i<numStats;i++)
		{
			String str1 = i + " "
						+ arraySumEstimate1[i] + " " 
						+ arrayPowerEstimate1[i]+ " "
						+ arrayStdEstimate1[i]+" "
						+ arrayTvEstimate1[i]+" "
						+arrayRelativeChange1[i]+" "
						+arrayLikelihood1[i]+" "
						+arrayMax1[i]+" "
						+arrayError1[i];
			
		
			//System.out.println("Error is: "+arrayError[i]+" Max is: "+arrayMax[i]);
			//System.out.println();
						
			out.write(str1);
			out.newLine();
		}
		
		for (int i=0;i<numStats;i++)
		{
			String str1 = i + " "
						+ arraySumEstimate1[i] + " " 
						+ arrayPowerEstimate1[i]+ " "
						+ arrayStdEstimate1[i]+" "
						+ arrayTvEstimate1[i]+" "
						+arrayRelativeChange1[i]+" "
						+arrayLikelihood1[i]+" "
						+arrayMax1[i]+" "
						+arrayError1[i];
			
			String str2 = i + " "
					+ arraySumEstimate2[i] + " " 
					+ arrayPowerEstimate2[i]+ " "
					+ arrayStdEstimate2[i]+" "
					+ arrayTvEstimate2[i]+" "
					+arrayRelativeChange2[i]+" "
					+arrayLikelihood2[i]+" "
					+arrayMax2[i]+" "
					+arrayError2[i];
		
			//System.out.println("Error is: "+arrayError[i]+" Max is: "+arrayMax[i]);
			//System.out.println();
						
			out.write(str1);
			out.newLine();
			out.write(str2);
			out.newLine();
			out.newLine();
		}
	
		out.close();
	
		ImgPlus<FloatType> resultPlus=StaticFunctions.Wrap3DImg(fuzzy.getRL().getResult(), "result");
		new ImgSaver().saveImg(resultFileName, resultPlus);
		
		ImgPlus<FloatType> psfPlus=StaticFunctions.Wrap3DImg(fuzzy.getPsf(),"psf");
		new ImgSaver().saveImg(psfFileName, psfPlus);
		*/
	}
}
