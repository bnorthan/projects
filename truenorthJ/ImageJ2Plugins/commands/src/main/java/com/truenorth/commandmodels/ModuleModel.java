package com.truenorth.commandmodels;

import imagej.module.Module;

/**
 * 
 * Abstract class to model a module.  
 * 
 * input widgets can use the model to render a GUI for a module.  in this way 
 * GUIs for multiple modules can be rendered on the same panel.
 * 
 * @author bnorthan
 *
 */
public abstract class ModuleModel 
{
	// name of the command
	String commandClassName;
	
	Module module;
	
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