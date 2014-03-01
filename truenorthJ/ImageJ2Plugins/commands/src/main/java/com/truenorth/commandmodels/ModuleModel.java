package com.truenorth.commandmodels;

import imagej.module.Module;
import imagej.command.Command;

public abstract class ModuleModel 
{
	Module module;
	
	String commandClassName;
	
	public ModuleModel()
	{
		this.commandClassName="";
	}
	
	public ModuleModel(String commandClassName)
	{
		this.commandClassName=commandClassName;
	}
	
	public Module getModule()
	{
		return module;
	}
	
	public void setModule(Module module)
	{
		this.module=module;
	}
	
	public String getCommandClassName()
	{
		return commandClassName;
	}
	
	public void setCommandClassName(String commandClassName)
	{
		this.commandClassName=commandClassName;
	}
	
	@Override
	public String toString()
	{
		return commandClassName;
	}
	
	public abstract Class getBaseClass();
	
}