package com.truenorth.commandmodels;

import imagej.command.Command;

import com.truenorth.commands.fft.AbstractFrequencyFilterCommand;
import com.truenorth.commands.fft.RichardsonLucyCommand;

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
