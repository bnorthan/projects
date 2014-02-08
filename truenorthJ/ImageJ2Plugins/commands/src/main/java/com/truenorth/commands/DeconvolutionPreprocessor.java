package com.truenorth.commands;

import imagej.module.process.AbstractPreprocessorPlugin;
import imagej.module.Module;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;

import org.scijava.Priority;
import org.scijava.plugin.Plugin;

import imagej.module.process.PreprocessorPlugin;

import com.truenorth.commands.fft.IterativeFilterCommand;
import com.truenorth.functions.StaticFunctions;
import com.truenorth.functions.fft.filters.IterativeFilterCallback;

/**
 * a preprocessor for deconvolution commands.
 * @author bnorthan
 *
 */
@Plugin(type = PreprocessorPlugin.class,priority = Priority.VERY_LOW_PRIORITY + 1)
public class DeconvolutionPreprocessor extends AbstractPreprocessorPlugin
{
	@Override
	public void process(final Module module) 
	{
		System.out.println("deconvolution preprocessing!!!!!!!");
		System.out.println(module.getInfo().getDelegateClassName());
		
		Class cl;
		
		try
		{
			cl = Class.forName(module.getInfo().getDelegateClassName());
		}
		catch (ClassNotFoundException ex)
		{
			// if the class can't be created then quit
			System.out.println("class not found!");
			return;
		}
		
		// check to see if the class we are testing is derived from the ItervativeFilterCommand
		boolean isIterative = IterativeFilterCommand.class.isAssignableFrom(cl);
				
		//if this is an iterative filter create a callback to print stats at each iteration
		if (isIterative)
		{
			System.out.println("iterative!");
			//final DeconvolutionStats<FloatType> stats = new DeconvolutionStats<FloatType>(200);
					
			IterativeFilterCallback callback=new IterativeFilterCallback() 
			{
						
				public void DoCallback(int iteration, RandomAccessibleInterval image, Img estimate, Img reblurred)
				{
					System.out.println("Iteration: "+iteration);
							
				//	StaticFunctions.PrintMemoryStatuses(true);
							
				//	stats.CalculateStats(iteration, image, estimate, reblurred, null, null, true);
							
					System.out.println();
				}
			};
			
			
		//	module.setInput("callback", callback);
			
			/*final Iterable<ModuleItem<?>> inputs = module.getInfo().inputs();
			for (final ModuleItem<?> item : inputs) 
			{
				if (item.getName().equals("callback"))
				{
					
	//				item.setValue(module, callback);
				}
			}
			
			module.getInfo().//
			
		//	module.setInput("callback", callback);*/
		}
		else
		{
			System.out.println("not iterative");
		}
		
	}
}
