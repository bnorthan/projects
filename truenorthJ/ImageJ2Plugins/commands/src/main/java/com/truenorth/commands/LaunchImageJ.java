package com.truenorth.commands;

/**
 * Launches ImageJ
 * 
 * @author bnorthan
 *
 */
public class LaunchImageJ 
{
	/** Launches imagej. */
	public static void main(final String... args) throws Exception 
	{
		// Launch ImageJ
		imagej.Main.launch(args);
	}
}
