package com.truenorth.simplecommands;

import imagej.command.Command;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.Parameter;

/**
 * A simple command to test the ImageJ plugin and menu system
 * 
 * @author bnorthan
 *
 */
@Plugin(type = Command.class, menuPath = "Plugins>TrueNorthJ>Very Simple", headless=true)
public class SuperSimple implements Command
{
	@Parameter
	private int thesimpleone;

	@Override
	public void run() {
	}
}
