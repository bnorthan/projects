package com.truenorth.commands;

import imagej.ImageJ;

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
		// Launch ImageJ as usual.
		final ImageJ ij = imagej.Main.launch(args);
	}
}
