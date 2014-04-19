package com.truenorth.ops;

import imagej.ops.Op;

import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ItemIO;
import org.scijava.Priority;

import com.truenorth.functions.StaticFunctions;
import com.truenorth.functions.psf.FlipPsfQuadrants;

import com.truenorth.gpu.wrappers.YacuDecuWrapper;

/**
 * Op tha calls Yacu Decu deconvolution
 * @author bnorthan
 *
 * @param <T>
 * @param <S>
 */
@Plugin(type = Op.class, name = "YacuDecu", priority = Priority.HIGH_PRIORITY + 1)
public class YacuDecuWrapperOp<T extends RealType<T>, S extends RealType<S>> 
		implements Op
{	
	@Parameter
	protected Img<T> input;
	
	@Parameter
	protected Img<T> kernel;
	
	@Parameter
	protected int numIterations;
	
	@Parameter(type = ItemIO.OUTPUT, required=false)
	protected Img<T> output;
	
	public void run()
	{
		int size=1;
		int[] dimensions=new int[input.numDimensions()];
		
		// calculate size of image
		for (int d=0;d<input.numDimensions();d++)
		{
			size*=input.dimension(d);
			dimensions[d]=(int)input.dimension(d);
		}
		
		// Flip PSF Quadrants to place the center at 0, 0, 0
		Img<T> flippedKernel = FlipPsfQuadrants.flip(kernel, kernel.factory(), dimensions);
	
		// convert Imgs to arrays (TODO: research better way to do this)
		float[] inputBuffer=StaticFunctions.convertImageToFloatBuffer(input);
		float[] kernelBuffer=StaticFunctions.convertImageToFloatBuffer(flippedKernel);	
		
		// declare membory for output buffer
		float[] outputBuffer=new float[size];
		
		for (int i=0;i<size;i++)
		{
			outputBuffer[i]=inputBuffer[i];
		}
		
		// create wrapper class
		YacuDecuWrapper wrapper=new YacuDecuWrapper();
    	
		// load the library
    	wrapper.LoadLib();
    	
    	// try calling the wrapper
    	try
    	{
    		int test=wrapper.runYacuDecu(numIterations, dimensions[0], dimensions[1], dimensions[2], inputBuffer, kernelBuffer, outputBuffer);
    	}
    	catch (Exception ex)
    	{
    		return;
    	}
    	
    	// convert float buffer to output Img
    	output=StaticFunctions.convertFloatBufferToImage(outputBuffer, dimensions, input.factory(), input.firstElement());
    }
		
}