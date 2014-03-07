package com.truenorth.commands;

import org.scijava.plugin.Parameter;

import imagej.command.Command;
import imagej.command.CommandModule;
import imagej.command.CommandService;

import java.util.concurrent.Future;
import java.util.Map;

abstract public class UmbrellaCommand implements Command
{
	@Parameter
	protected CommandService commandService;
	
	public CommandModule runAndBlock(String commandName, Map<String, Object> inputMap)
	{	
		Future<CommandModule> future= commandService.run(commandName, true, inputMap);
		
	    CommandModule commandModule=null;
	    
	    try
		{
			return future.get();
		}
		catch (Exception ex)
		{
			return null;
		}
	}
	
	public CommandModule runAndBlock(String commandName, Object... inputs)
	{	
		Future<CommandModule> future= commandService.run(commandName, true, inputs);
		
	    CommandModule commandModule=null;
	    
	    try
		{
			return future.get();
		}
		catch (Exception ex)
		{
			return null;
		}
	}
}
