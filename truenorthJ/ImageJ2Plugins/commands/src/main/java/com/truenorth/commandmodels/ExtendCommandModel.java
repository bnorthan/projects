package com.truenorth.commandmodels;

import imagej.command.Command;
import com.truenorth.commands.dim.ExtendCommand;
import com.truenorth.commands.dim.ExtendCommandDimension;

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
