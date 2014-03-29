package com.truenorth.commandmodels;

import com.truenorth.commands.noise.AddPoissonNoiseCommand;

public class PoissonNoiseModel extends ModuleModel
{
	public PoissonNoiseModel(String commandClassName)
	{
		super(commandClassName);
	}
	
	public PoissonNoiseModel()
	{
		super();
	}
	
	public Class getBaseClass()
	{
		return AddPoissonNoiseCommand.class;
	}

}
