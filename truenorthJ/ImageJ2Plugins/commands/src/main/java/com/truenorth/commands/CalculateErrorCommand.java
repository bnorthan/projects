package com.truenorth.commands;

import org.scijava.plugin.Parameter;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

import imagej.command.Command;
import imagej.data.Dataset;
import imagej.data.DatasetService;

import com.truenorth.functions.StaticFunctions;

public class CalculateErrorCommand<T extends RealType<T> & NativeType<T>> extends HackAbstractVolumeProcessingCommand<T>
{
	@Parameter
	protected DatasetService datasetService;
	
	@Parameter
	protected Dataset input2;
	
	protected void processInput(RandomAccessibleInterval<T> in)
	{
		
		RandomAccessibleInterval<T> in2=extractData(input2);
		
		double error=0;//StaticFunctions.squaredErrorRnd(in, in2);
		
		System.out.println();
		System.out.println("=============");
		System.out.println("time is: "+timeIndex);
		System.out.println("channel is: "+channelIndex);
		System.out.println("error is: "+error);
		System.out.println("=============");
		System.out.println();
		
		/*for (Img<T> img:imageList)
		{
			arrExtractedTimePoints.add(Views.hyperSlice(img, timePosition, t));
		}*/
		
	/*	for (RandomAccessibleInterval<T> channel:arrExtractedTimePoints)
		{
			arrExtractedChannels.add(Views.hyperSlice(channel, channelPosition, c));
		}*/
		
		
	//	constructImageList();
	
	}
	
}

/*public class CalculateErrorCommand<T extends RealType<T> & NativeType<T>> implements Command
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

}*/
