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
import net.imglib2.meta.CalibratedAxis;
import net.imglib2.meta.axis.DefaultLinearAxis;

import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

import java.io.IOException;
import java.util.ArrayList;
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
public abstract class AbstractVolumeProcessorCommand<T extends RealType<T> & NativeType<T>> extends AbstractCommand
{
	@Parameter
	protected Dataset input;
	
	@Parameter(type=ItemIO.OUTPUT)
	protected Dataset output;
	
	int channelPosition=-1;
	long numChannels;
	
	int timePosition=-1;
	long numTimePoints;
	
	OutputAlgorithm<Img<T>> algorithm;
	
	ArrayList<Img<T>> imageList;
	
	protected boolean inPlace=false;
	
	/**
	 * an abstract function to process the volume
	 * @param region
	 * @return
	 */
	protected abstract Img<T> processVolume(RandomAccessibleInterval<T> volume);
	
	/**
	 * a function that can be overridden to perform preprocessing
	 */
	protected void preProcess()
	{
		
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
					//try
			    	{
						System.out.println("xyt dataset: Press 'y' to change time to z...");
						//BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
					
						//String str=br.readLine();						
						//if (str.toUpperCase().equals("Y"))
						{

							CalibratedAxis axis=new DefaultLinearAxis();
							axis.setType(Axes.Z);
							input.setAxis(axis, d);
							//input.setAxis(Axes.Z, d);
							
							System.out.println("axes "+d+" has been changed to: "+input.axis(d).type());
						}
			    	}
					//catch (IOException ex)
					//{
						
					//}
					//StaticFunctions.Pause();
				}
			}
		}
		
		preProcess();
		
		//////////////////////////////////////////////////////////////////////////////////////
			
		// Todo:  look into these warnings
		Img<T> inputImg=(Img<T>)(input.getImgPlus().getImg());
		Img<T> outputImg=(Img<T>)(output.getImgPlus().getImg());
		
		// construct image list
		imageList=new ArrayList<Img<T>>();
		imageList.add(inputImg);
		imageList.add(outputImg);
		
		numChannels=1;
		numTimePoints=1;
		
		System.out.println();
		System.out.println("parsing axes: ");
	
		// Todo:  rework all below code more generic so it will handle 5d+ datasets
		
		// loop through all dimensions looking for time and channel
		for(int d=0;d<input.numDimensions();d++)
		{ 	
			System.out.println("axes "+d+" is: "+input.axis(d).type());
			
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
		
		// loop through all time points
		for (int t=0;t<numTimePoints;t++)
		{
			
			RandomAccessibleInterval<T> inputTimepoint;
			RandomAccessibleInterval<T> outputTimepoint;
			
			ArrayList<RandomAccessibleInterval<T>> arrExtractedTimePoints=new ArrayList<RandomAccessibleInterval<T>>();
			
			// if multiple timepoints were found extract a hyperslice at the current time point
			if (timePosition>-1)
			{
				inputTimepoint=Views.hyperSlice(inputImg, timePosition, t);
				outputTimepoint=Views.hyperSlice(outputImg, timePosition, t);
				
				for (Img<T> img:imageList)
				{
					arrExtractedTimePoints.add(Views.hyperSlice(img, timePosition, t));
				}
			}
			// otherwise there is only one timepoint, so no need to extract the hyperslice
			else
			{
				inputTimepoint=inputImg;
				outputTimepoint=outputImg;
			
				for (Img<T> img:imageList)
				{
					arrExtractedTimePoints.add(img);
				}
			}
			
			// loop through all channels
			for (int c=0;c<numChannels;c++)
			{
				RandomAccessibleInterval<T> inputChannel;
				RandomAccessibleInterval<T> outputChannel;
				
				ArrayList<RandomAccessibleInterval<T>> arrExtractedChannels=new ArrayList<RandomAccessibleInterval<T>>();
				
				// if multiple channels were found extract a hyperslice at the current channel
				if (channelPosition>-1)
				{
					inputChannel = Views.hyperSlice(inputTimepoint, channelPosition, c);
					outputChannel = Views.hyperSlice(outputTimepoint, channelPosition, c);
					
					for (RandomAccessibleInterval<T> channel:arrExtractedTimePoints)
					{
						arrExtractedChannels.add(Views.hyperSlice(channel, channelPosition, c));
					}
				}
				// otherwise there is just one channel so no need to extract the hyperslice
				else
				{
					inputChannel = inputTimepoint;
					outputChannel=outputTimepoint;
					
					for (RandomAccessibleInterval<T> channel:arrExtractedTimePoints)
					{
						arrExtractedChannels.add(channel);
					}
				}
				
				System.out.println("processing: c"+c+"t"+t);
				
				// Todo:  Parralelize volume processing??  Or assume that the algorithm itself will be parrallel?? 
				// different hardware may require different solutions. 
				
				// process this volume
				Img<T> result=processVolume(inputChannel);
				
				// here we copy the result into the output dataset
				// Todo: work out a better way to do this... give the algorithm direct access to the memory 
				// so we don't need to copy?? 
				
				if (!inPlace)
				{
					StaticFunctions.copy3(result, outputChannel);
				}
				else
				{
					StaticFunctions.copy(Views.iterable(inputChannel), Views.iterable(outputChannel));
				}
			}
		}
	}
}

