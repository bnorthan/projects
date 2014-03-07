package com.truenorth.commandmodels;

import com.truenorth.commands.fft.AbstractFrequencyFilterCommand;

public class DeconvolutionModel extends ModuleModel 
{
	public DeconvolutionModel(String commandClassName)
	{
		super(commandClassName);
	}
	
	public DeconvolutionModel()
	{
		super();
	}
	
	public Class getBaseClass()
	{
		return AbstractFrequencyFilterCommand.class;
	}
}
