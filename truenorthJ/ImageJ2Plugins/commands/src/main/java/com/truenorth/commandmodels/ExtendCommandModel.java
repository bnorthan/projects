package com.truenorth.commandmodels;

import com.truenorth.commands.dim.ExtendCommand;

public class ExtendCommandModel extends ModuleModel
{
	public ExtendCommandModel(String commandClassName)
	{
		super(commandClassName);
	}
	
	public ExtendCommandModel()
	{
		super();
	}
	
	public Class getBaseClass()
	{
		return ExtendCommand.class;
	}
}
