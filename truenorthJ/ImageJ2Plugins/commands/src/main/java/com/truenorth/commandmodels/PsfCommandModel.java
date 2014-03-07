package com.truenorth.commandmodels;

import com.truenorth.commands.psf.CreatePsfCommand;;

public class PsfCommandModel extends ModuleModel
{
	public PsfCommandModel(String commandClassName)
	{
		super(commandClassName);
	}
	
	public PsfCommandModel()
	{
		super();
	}
	
	public Class getBaseClass()
	{
		return CreatePsfCommand.class;
	}
}
