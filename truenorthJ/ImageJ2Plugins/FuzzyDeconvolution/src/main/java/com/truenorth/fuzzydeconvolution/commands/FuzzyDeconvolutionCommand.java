package com.truenorth.fuzzydeconvolution.commands;

import imagej.command.Command;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.meta.ImgPlus;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Plugin;

import com.truenorth.commands.AbstractVolumeProcessorCommand;

import com.truenorth.functions.StaticFunctions;
import com.truenorth.fuzzydeconvolution.functions.RichardsonLucyFuzzyFilter;

import org.scijava.plugin.Parameter;

/**
 * A commmand to call the fuzzy deconvolution algorithm
 * 
 * @author bnorthan
 *
 * @param <T>
 */
@Plugin(type=Command.class, menuPath="Plugins>Deconvolution>Fuzzy Deconvolution")
public class FuzzyDeconvolutionCommand <T extends RealType<T> & NativeType<T>> extends AbstractVolumeProcessorCommand<T>
{
		// x, y, spacing measured in nm
		@Parameter
		double xySpace=40;
		
		// z spacing measured in nm
		@Parameter
		double zSpace=100;
		
		// emission wavelength measured in nm
		@Parameter
		double emissionWavelength=500.0;
		
		// numerical Aperture
		@Parameter
		double numericalAperture=1.3;
		
		// The design index of refraction of the immersion oil
		@Parameter
		double designImmersionOilRefractiveIndex=1.515;
		 
		// The design index of refraction of the specimen
		@Parameter
		double designSpecimenLayerRefractiveIndex=1.515;
		
		// actual index of refraction of the immersion oil
		@Parameter
		double actualImmersionOilRefractiveIndex=1.515;
		
		// actual index of refraction of the specimen
		@Parameter
		double actualSpecimenLayerRefractiveIndex=1.33;
		
		// depth in the specimen at which the psf will be calculated
		// (in other words the psf is the image of a point source at this depth)
		@Parameter
		double actualPointSourceDepthInSpecimenLayer=10;
		
		@Parameter
		String dataDirectory;
		
		@Parameter
		double firstRIToTry=1.43;
		
		@Parameter
		String dataFileBase;
		
		@Parameter
		int iterations=200;
		
	/*	IterativeFilterCallback callback=new IterativeFilterCallback() {
			public void DoCallback(int iteration, RandomAccessibleInterval image, Img estimate, Img reblurred)
			{
				System.out.println("Iteration: "+iteration);
				
				StaticFunctions.PrintMemoryStatuses(true);
				
				//uiService.showDialog("Iteration: "+iteration);
				uiService.getStatusService().showStatus("Iteration: "+iteration);
				uiService.getStatusService().showStatus(iteration, iterations, "Iteration: "+iteration);
				
				System.out.println();
			}
		};*/
		
	@Override
	protected void preProcess()
	{
		Img<T> inputImg=(Img<T>)(input.getImgPlus().getImg());
		
		// use the input dataset to create an output dataset of the same dimensions
		//output=datasetService.create(inputImg.firstElement(), input.getDims(), "output", input.getAxes());
		
		ImgPlus<T> inputImgPlus=(ImgPlus<T>)(input.getImgPlus());
		output=datasetService.create(inputImgPlus);
		
	}
	
	RichardsonLucyFuzzyFilter<T> createAlgorithm(RandomAccessibleInterval<T> region)
	{
		Img<T> inputImg=(Img<T>)(input.getImgPlus().getImg());
			
		System.out.println("xySpace: "+xySpace);
		System.out.println("zSpace: "+zSpace);
		System.out.println("emissionWavelength: "+emissionWavelength);
		System.out.println("numericalAperture: "+numericalAperture);
		System.out.println("designImmersionOilRefractiveIndex: "+designImmersionOilRefractiveIndex);
		System.out.println("actualSpecimenLayerRefractiveIndex: "+actualSpecimenLayerRefractiveIndex);
		System.out.println("actualPointSourceDepthInSpecimenLayer: "+actualPointSourceDepthInSpecimenLayer);
		
		//StaticFunctions.Pause();
		
		float[] space=new float[3];
		space[0]=(float)xySpace;
		space[1]=(float)xySpace;
		space[2]=(float)zSpace;	
/*		double emissionWavelength=500.0;
		double numericalAperture=1.3;
		double designImmersionOilRefractiveIndex=1.515;
		double designSpecimenLayerRefractiveIndex=1.515;
		double actualImmersionOilRefractiveIndex=1.515;
		double actualSpecimenLayerRefractiveIndex=1.33;
		double actualPointSourceDepthInSpecimenLayer=10;*/
			
		// create a RichardsonLucy filter for the region
		RichardsonLucyFuzzyFilter<T> fuzzy = new RichardsonLucyFuzzyFilter<T>(inputImg,
				space,
				emissionWavelength,
				numericalAperture,
				designImmersionOilRefractiveIndex,
				designSpecimenLayerRefractiveIndex,
				actualImmersionOilRefractiveIndex,
				actualSpecimenLayerRefractiveIndex,
				actualPointSourceDepthInSpecimenLayer,
				firstRIToTry);
		
		// set the data directory and data file name
		fuzzy.setDataDirectory(dataDirectory);
		String dataFileName = dataFileBase+"_"+firstRIToTry+".txt";
		fuzzy.setDataFileName(dataFileName);
		
		// set the number of iterations
		fuzzy.setIterations(iterations);
			
		return fuzzy;
	}

	@Override
	protected Img<T> processVolume(RandomAccessibleInterval<T> volume)
	{
		// create the specific algorithm that will be applied
		RichardsonLucyFuzzyFilter<T> filter=createAlgorithm(volume);
	
		// process the volume
		filter.process();
	
		// and return the result
		return filter.getResult();
	}

}
