package com.truenorth.commands.psf;

import imagej.command.Command;

import org.scijava.plugin.Plugin;
import org.scijava.plugin.Parameter;
import org.scijava.ItemIO;

import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

import imagej.data.DatasetService;
import imagej.data.Dataset;

/**
 * A commmand to create a psf
 * @author bnorthan
 *
 * @param <T>
 */
@Plugin(type = Command.class)
public abstract class CreatePsfCommand <T extends RealType<T> & NativeType<T>> implements Command
{
	@Parameter
	protected DatasetService datasetService;
	
	@Parameter(type = ItemIO.OUTPUT)
	protected Dataset output;
	
}
