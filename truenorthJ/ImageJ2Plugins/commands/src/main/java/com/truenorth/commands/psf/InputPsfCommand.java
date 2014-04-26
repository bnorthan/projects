package com.truenorth.commands.psf;

import org.scijava.command.Command;
import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

@Plugin(type = CreatePsfCommand.class)
public class InputPsfCommand <T extends RealType<T> & NativeType<T>> extends CreatePsfCommand
{
	@Parameter(type = ItemIO.INPUT)
	protected Dataset input;
	
	@Override
	public void run()
	{
		output=input;
	}
}
