package com.truenorth.commands.noise;

import imagej.command.Command;
import imagej.command.CommandModule;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import com.truenorth.commandmodels.PoissonNoiseModel;

import com.truenorth.commands.UmbrellaCommand;

import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import imagej.module.Module;

@Plugin(type=Command.class, menuPath="Plugins>Noise>Add Poisson Noise")
public class PoissonNoiseUmbrellaCommand <T extends RealType<T> & NativeType<T>> extends UmbrellaCommand
{
	@Parameter(label="Poisson Noise Command")
	PoissonNoiseModel poissonNoiseCommand;
	
	@Override
	public void run()
	{
		String commandName=poissonNoiseCommand.getCommandClassName();
		Module m=poissonNoiseCommand.getModule();
		
		CommandModule commandModule= runAndBlock(commandName, m.getInputs());
		
	}

}
