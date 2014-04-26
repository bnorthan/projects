package com.truenorth.commands;

import org.scijava.plugin.Parameter;

import org.scijava.command.Command;
import org.scijava.command.CommandModule;
import org.scijava.command.CommandService;

import java.util.concurrent.Future;
import java.util.Map;

/**
 * A base class for a high level command that is built from low level commands
 * 
 * @author bnorthan
 *
 */
abstract public class UmbrellaCommand implements Command
{
	@Parameter
	protected CommandService commandService;
	
	/**
	 * 
	 * runs a command and blocks
	 * 
	 * @param commandName
	 * @param inputMap
	 * @return
	 */
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
	
	/**
	 * 
	 * runs a command and blocks
	 * 
	 * @param commandName
	 * @param inputs
	 * @return
	 */
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
