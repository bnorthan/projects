package com.truenorth;

import net.imagej.ImageJ;

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
		final ImageJ ij = net.imagej.Main.launch(args);
	}
}
