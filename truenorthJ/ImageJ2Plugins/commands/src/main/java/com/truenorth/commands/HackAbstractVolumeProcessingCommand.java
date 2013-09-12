package com.truenorth.commands;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import net.imglib2.RandomAccessibleInterval;

import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;

import com.truenorth.functions.StaticFunctions;

import imagej.data.Dataset;
 
import net.imglib2.algorithm.OutputAlgorithm;

import net.imglib2.img.Img;
import net.imglib2.meta.Axes;
import net.imglib2.meta.DefaultCalibratedAxis;

import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

import java.io.IOException;
import java.util.ArrayList;

import net.imglib2.Interval;
/**
 * Abstract class for an input/output command that processes each 3d x,y,z volume in a dataset
 * and creates a new output
 * 
 * a volume is considered to consist of a x,y,z 3d hyperslice.
 * 
 * For example a dataset with 10 timepoints and 3 channels would have 30 volumes.  
 * This class is used when it is desired to extract and apply a 3d algorithm to each volume
 * @author bnorthan
 *
 * @param <T>
 */
public abstract class HackAbstractVolumeProcessingCommand<T extends RealType<T> & NativeType<T>> extends AbstractCommand
{
	// ever command needs an input dataset
	@Parameter
	protected Dataset input;
	
	// current time and channel... this should be an abstraction somehow...
	int timePosition=-1;
	int timeIndex=-1;
	long numTimePoints;
	
	int channelPosition=-1;
	int channelIndex=-1;
	long numChannels;
	
	// algorithm to apply... possibly not needed
	OutputAlgorithm<Img<T>> algorithm;

	// array of other images (inputs, outpus, etc... revisit)
	//ArrayList<Img<T>> imageList;
	
	// in place command?? revisit
	protected boolean inPlace=false;
	
	/**
	 * an abstract function to process an input interval
	 * @param region
	 * @return
	 */
	protected abstract void processInput(RandomAccessibleInterval<T> input);
	
	/**
	 * a function that can be overridden to perform preprocessing
	 */
	protected void preProcess()
	{
		
	}
	
	/**
	 * a function that can be overriden to construct the image list
	 */
	/*protected void constructImageList()
	{
		Img<T> inputImg=(Img<T>)(input.getImgPlus().getImg());
		Img<T> outputImg=(Img<T>)(output.getImgPlus().getImg());
		
		// construct image list
		imageList=new ArrayList<Img<T>>();
		imageList.add(inputImg);
		imageList.add(outputImg);		
	}*/
	
	/**
	 * 
	 * @param dataset
	 * @return
	 */
	protected RandomAccessibleInterval<T> extractData(Dataset dataset)
	{
		
		Img<T> img=(Img<T>)(dataset.getImgPlus().getImg());
		RandomAccessibleInterval<T> time;
		
		if (timePosition!=-1)
		{
			time=Views.hyperSlice(img, timePosition, timeIndex);
		}
		else
		{
			time=img;
		}
		
		RandomAccessibleInterval<T> channel;
	
		if (channelPosition!=-1)
		{
			channel=Views.hyperSlice(time, channelPosition, channelIndex);
		}
		else
		{
			channel=time;
		}
		
		//Views.interval(img,null);
		
		return channel;	
	}
	
	@Override
	public void run()
	{
		// TEMP_HACK !!!!!!
		// check to see if this is a xyt dataset... if meta data was not set properly a dataset that
		// should be xyz may be interpreted as xyt so give the user a chance to change to xyz
		if (input.numDimensions()==3)
		{
			for(int d=0;d<input.numDimensions();d++)
			{ 	
				System.out.println("axes "+d+" is: "+input.axis(d).type());
				
				
				if (input.axis(d).type()==Axes.TIME)
				{
					try
			    	{
						System.out.println("xyt dataset: Press 'y' to change time to z...");
						BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
					
						String str=br.readLine();						
						if (str.toUpperCase().equals("Y"))
						{

							DefaultCalibratedAxis axis=new DefaultCalibratedAxis();
							axis.setType(Axes.Z);
							input.setAxis(axis, d);
							//input.setAxis(Axes.Z, d);
							
							System.out.println("axes "+d+" has been changed to: "+input.axis(d).type());
						}
			    	}
					catch (IOException ex)
					{
						
					}
					StaticFunctions.Pause();
				}
			}
		}
		
		preProcess();
		
		//////////////////////////////////////////////////////////////////////////////////////
			
		
		numChannels=1;
		numTimePoints=1;
		
		System.out.println();
		System.out.println("parsing axes: ");
	
		// Todo:  rework all below code more generic so it will handle 5d+ datasets!!
		
		// analyze dimensionality
		// make more complex,,,
		// ... does it have x,y,z??  if so what other dimensions?? 
		// 
		// for ow loop through all dimensions looking for time and channel
		for(int d=0;d<input.numDimensions();d++)
		{ 	
			System.out.println("axes "+d+" is: "+input.axis(d));
			
			// if the current axis is a channel or a timepoint...
			if (input.axis(d).type()==Axes.CHANNEL)
			{
				channelPosition=d;
				numChannels=input.dimension(d);
						
				System.out.println(numChannels+" channels found at pos: "+d);
			}
			else if (input.axis(d).type()==Axes.TIME)
			{
				System.out.println(input.dimension(d)+" timepoints found at pos: "+d);
				
				timePosition=d;
				numTimePoints=input.dimension(d);
			}	
		}
		
		// main loop... need to make more complex depending on what dimensions were found above
		//
		// for now just loop through all time points
		for (timeIndex=0;timeIndex<numTimePoints;timeIndex++)
		{
			//ArrayList<RandomAccessibleInterval<T>> arrExtractedTimePoints=new ArrayList<RandomAccessibleInterval<T>>();
			
			// now loop through all channels
			for (channelIndex=0;channelIndex<numChannels;channelIndex++)
			{
				//Interval test = new Interval()
				
				System.out.println("processing: c"+channelIndex+"t"+timeIndex);
				
				// Todo:  Parralelize volume processing??  Or assume that the algorithm itself will be parrallel?? 
				// different hardware may require different solutions. 
				
				// process this volume
				processInput(extractData(input));
							
			}
		}
	}
}


