package com.truenorth.commands.dim;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import imagej.data.Dataset;

import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;

import net.imglib2.img.ImgPlus;
//import net.imglib2.meta.ImgPlus;

import net.imglib2.meta.Axes;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import com.truenorth.commands.AbstractCommand;
import com.truenorth.functions.StaticFunctions;

public class ConvertDimensionCommand<T extends RealType<T> & NativeType<T>> extends AbstractCommand
{
	@Parameter
	protected Dataset input;
	
	@Parameter(type=ItemIO.OUTPUT)
	protected Dataset output;
	
	public void run()
	{
		long dimensions[]=new long[input.numDimensions()];
	
		for(int d=0;d<input.numDimensions();d++)
		{ 	
			System.out.println("axes "+d+" is: "+input.axis(d));
			
			dimensions[d]=input.dimension(d);
			
			if (input.axis(d)==Axes.TIME)
			{
				try
				{
					System.out.println("xyt dataset: Press 'y' to change time to z...");
					BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
							
					String str=br.readLine();
								
					if (str.toUpperCase().equals("Y"))
					{
						input.setAxis(Axes.Z, d);
						System.out.println("axes "+d+" has been changed to: "+input.axis(d));
					}
				}
					catch (IOException ex)
					{
								
					}
							
					//StaticFunctions.Pause();
							
				}
			}
		
		ImgPlus<T> imgInput=(ImgPlus<T>)(input.getImgPlus());
		
		//output=datasetService.create(imgInput);
		output=datasetService.create(imgInput.firstElement(), dimensions, "converted", input.getAxes());
		
		ImgPlus<T> imgOutput=(ImgPlus<T>)(output.getImgPlus());
		
		StaticFunctions.copy(imgInput, imgOutput);
		
		for(int d=0;d<output.numDimensions();d++)
		{ 	
			System.out.println("axes "+d+" is: "+output.axis(d));
			
			dimensions[d]=input.dimension(d);
			
			if (output.axis(d)==Axes.TIME)
			{
				try
				{
					System.out.println("xyt dataset: Press 'y' to change time to z...");
					BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
							
					String str=br.readLine();
								
					if (str.toUpperCase().equals("Y"))
					{
						output.setAxis(Axes.Z, d);
						System.out.println("axes "+d+" has been changed to: "+output.axis(d));
						}
					}
					catch (IOException ex)
					{
								
					}
							
					//StaticFunctions.Pause();
							
				}
			}
		
	}

}
