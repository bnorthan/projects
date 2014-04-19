package com.truenorth.gpu.commands;

import imagej.command.Command;

import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Plugin;
import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;

import net.imglib2.img.Img;
import net.imglib2.meta.ImgPlus;

import imagej.data.Dataset;
import imagej.data.DatasetService;

import imagej.ops.OpService;

/**
 * A command for GPU deconvolution.  Calls a JNI wrapper to yacudecu.
 * @author bnorthan
 *
 * @param <T>
 */
@Plugin(type=Command.class, menuPath="Plugins>Deconvolution>Yacu Decu")
public class GPUDeconvolutionCommand <T extends RealType<T> & NativeType<T>> implements Command
{
	@Parameter
	protected DatasetService data;
	
	@Parameter
	protected OpService ops;
	
	@Parameter
	protected Dataset input;
	
	@Parameter
	protected Dataset kernel;
	
	@Parameter
	protected int numIterations=2;
	
	@Parameter(type=ItemIO.OUTPUT)
	protected Dataset output;
	
	public void run()
	{
		// get the Img from the dataset
		Img<T> imgIn=(Img<T>)input.getImgPlus().getImg();
		Img<T> imgKernel=(Img<T>)kernel.getImgPlus().getImg();
		
		// call yacudecu op. 
		Img<T> imgOut = (Img<T>)(ops.run("YacuDecu", imgIn, imgKernel, numIterations));
		
		// put the output into a new dataset
		output=data.create(new ImgPlus(imgOut));
	}
}


