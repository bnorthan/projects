package com.truenorth.commands;

import net.imagej.Dataset;
import net.imagej.DatasetService;

import org.scijava.plugin.Parameter;
import org.scijava.command.Command;

import net.imglib2.meta.ImgPlus;
import net.imglib2.meta.Axes;

import org.scijava.plugin.Plugin;

/**
 * A simple command that displays a message box containing info about the dataset
 * 
 * @author bnorthan
 *
 */
@Plugin(type=Command.class, menuPath="Plugins>Deconvolution>Deconvolution Info", initializer="initializer")
public class InfoCommand implements Command
{
	
	@Parameter
	protected DatasetService datasetService;
	
	@Parameter
	protected Dataset input;
	
	@Parameter(persist=false)
	protected int numDimensions=-1;
	
	@Parameter(persist=false)
	protected int xdim=1;
	
	@Parameter(persist=false)
	protected int ydim=1;
	
	@Parameter(persist=false)
	protected int zdim=1;
	
	@Parameter(persist=false)
	protected float ri=1.37f;
	
	@Override
	public void run()
	{
		long freeMemory=Runtime.getRuntime().freeMemory();
		System.out.println("free memory "+freeMemory);
		
		long maxMemory=Runtime.getRuntime().maxMemory();
		System.out.println("max memory"+maxMemory);
		
		long totalMemory=Runtime.getRuntime().totalMemory();
		
		long presumableFreeMemory = Runtime.getRuntime().maxMemory() - totalMemory;
		
		for(int d=0;d<input.numDimensions();d++)
		{ 	
			System.out.println("axes "+d+" is: "+input.axis(d).type());
		}
	}
	
	protected void initializer()
	{
		if (input!=null)
		{
			ImgPlus plus = input.getImgPlus();
			
			numDimensions = input.getImgPlus().numDimensions();
			
			for (int n=0;n<numDimensions;n++)
			{
			
				if (input.axis(n).type()==Axes.X)
				{
					xdim=(int)plus.dimension(n);
				}
				
				else if (input.axis(n).type()==Axes.Y)
				{
					ydim=(int)plus.dimension(n);
				}
				
				else if (input.axis(n).type()==Axes.Z)
				{
					zdim=(int)plus.dimension(n);
				}
			}
		}
	}
}
