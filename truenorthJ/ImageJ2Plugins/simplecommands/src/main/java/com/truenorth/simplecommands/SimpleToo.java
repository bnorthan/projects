package com.truenorth.simplecommands;

import imagej.command.Command;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.Parameter;

/**
 * Just a simple command for testing the ImageJ plugin and menu system
 * 
 * @author bnorthan
 *
 */
@Plugin(type = Command.class, menuPath = "Plugins>Sandbox>Simple Test 363", headless=true)
public class SimpleToo implements Command
{
	@Parameter
	private int thesimpletest;

	@Override
	public void run() {
	}
}
