package com.truenorth.commands;

import imagej.data.Dataset;
import imagej.data.DatasetService;

import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;

import imagej.command.Command;

import net.imglib2.meta.ImgPlus;
//import net.imglib2.img.ImgPlus;
import net.imglib2.meta.Axes;
import net.imglib2.meta.AxisType;

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
		for(int d=0;d<input.numDimensions();d++)
		{ 	
			System.out.println("axes "+d+" is: "+input.axis(d));
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
				
				if (plus.axis(n).type()==Axes.X)
				{
					xdim=(int)plus.dimension(n);
				}
				
				else if (plus.axis(n).type()==Axes.Y)
				{
					ydim=(int)plus.dimension(n);
				}
				
				else if (plus.axis(n).type()==Axes.Z)
				{
					zdim=(int)plus.dimension(n);
				}
			}
		}
	}
}
