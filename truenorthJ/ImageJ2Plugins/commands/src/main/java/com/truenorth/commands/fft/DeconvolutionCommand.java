package com.truenorth.commands.fft;

import imagej.command.Command;
import imagej.command.CommandService;

import imagej.module.Module;

import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import com.truenorth.commandmodels.DeconvolutionModel;
import com.truenorth.commandmodels.ExtendCommandModel;
import com.truenorth.commandmodels.PsfCommandModel;
import com.truenorth.commands.dim.ExtendCommandDimension;

import java.util.Map;
import java.util.Iterator;

@Plugin(type=Command.class, menuPath="Plugins>Deconvolution>Deconware")
public class DeconvolutionCommand<T extends RealType<T> & NativeType<T>> implements Command
{
	@Parameter
	CommandService commandService;
	
	@Parameter
	DeconvolutionModel deconvolutionModel;
	
	@Parameter
	PsfCommandModel psfCommand;
	
	@Parameter
	ExtendCommandModel extendCommand;
	
	@Override
	public void run()
	{
		int stop=5;
		
		Iterator it = extendCommand.getModule().getInputs().entrySet().iterator();
	    while (it.hasNext()) 
	    {
	        Map.Entry pairs = (Map.Entry)it.next();
	        System.out.println(pairs.getKey() + " = " + pairs.getValue());
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	    String psfCommandName=psfCommand.getCommandClassName();
	    Module module=psfCommand.getModule();
	
	    commandService.run(psfCommandName, true, psfCommand.getModule().getInputs());
	    
	}

}
