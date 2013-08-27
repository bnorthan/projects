package com.truenorth.commands;

import org.scijava.plugin.Parameter;

import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import imagej.command.Command;
import imagej.data.Dataset;
import imagej.data.DatasetService;

import com.truenorth.functions.StaticFunctions;

public class CalculateErrorCommand<T extends RealType<T> & NativeType<T>> implements Command
{
	@Parameter
	protected DatasetService datasetService;
	
	@Parameter
	protected Dataset input1;
	
	@Parameter
	protected Dataset input2;
	
	@Override
	public void run()
	{
		Img<T> imgInput1=(Img<T>)(input1.getImgPlus().getImg());
		Img<T> imgInput2=(Img<T>)(input2.getImgPlus().getImg());
		
		double error=StaticFunctions.squaredError(imgInput1, imgInput2);
		
		System.out.println("error is: "+error);
	}

}
