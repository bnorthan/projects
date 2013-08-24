package com.truenorth.simplecommands;

import imagej.command.Command;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.Parameter;

@Plugin(type = Command.class, menuPath = "Plugins>Sandbox>Simple Test Two", headless=true)
public class SimpleToo implements Command
{
	@Parameter
	private int thesimplest;

	@Override
	public void run() {
	}
}
