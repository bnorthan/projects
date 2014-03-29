package com.truenorth.commands.fft;

import org.scijava.plugin.Parameter;

import com.truenorth.commands.Constants;
import com.truenorth.functions.fft.filters.FrequencyFilter;
import com.truenorth.functions.fft.filters.IterativeFilterITK.ITKBoundaryCondition;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import com.truenorth.functions.fft.filters.IterativeFilterITK;


public abstract class IterativeFilterCommandITK<T extends RealType<T> & NativeType<T>> extends AbstractFrequencyFilterCommand<T> 
{
	@Parameter
	protected int iterations;
	
	@Parameter(label="extension type", choices = { Constants.Boundary.boundaryZero, Constants.Boundary.boundaryPeriodic, Constants.Boundary.boundaryNeumann})
	protected String boundaryType;
	
	/**
	 * abstract function used to create the iterative algorithm that will be applied
	 * @param region
	 * @return
	 */
	protected abstract IterativeFilterITK<T,T> createIterativeITKAlgorithm(RandomAccessibleInterval<T> region);
	
	FrequencyFilter<T,T> createAlgorithm(RandomAccessibleInterval<T> region)
	{
		IterativeFilterITK<T,T> iterativeFilterITK=  createIterativeITKAlgorithm(region);
		
		// set the number of iterations and the callback
		iterativeFilterITK.setMaxIterations(iterations);
		
		if (boundaryType.equals(Constants.Boundary.boundaryZero))
		{
			iterativeFilterITK.setBoundaryCondition(ITKBoundaryCondition.ZERO_PAD);
		}
		else if (boundaryType.equals(Constants.Boundary.boundaryPeriodic))
		{
			iterativeFilterITK.setBoundaryCondition(ITKBoundaryCondition.PERIODIC_PAD);
		}
		else if (boundaryType.equals(Constants.Boundary.boundaryNeumann))
		{
			iterativeFilterITK.setBoundaryCondition(ITKBoundaryCondition.ZERO_FLUX_NEUMANN_PAD);
		}
		
		return iterativeFilterITK;
	}

}
