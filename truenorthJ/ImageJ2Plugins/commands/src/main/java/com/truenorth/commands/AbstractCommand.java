package com.truenorth.commands;

import imagej.command.Command;
import imagej.data.DatasetService;

import org.scijava.plugin.Parameter;

/**
 * High level abstract command that contains common parameters and functionality for other commands. 
 * 
 * @author bnorthan
 *
 */
public abstract class AbstractCommand implements Command
{
	@Parameter
	protected DatasetService datasetService;
}
