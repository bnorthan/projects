package com.truenorth.commands.fft;

import imagej.command.Command;
import imagej.command.CommandModule;
import imagej.command.CommandService;

import imagej.data.Dataset;

import imagej.module.Module;

import net.imglib2.meta.Axes;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import com.truenorth.commandmodels.DeconvolutionModel;
import com.truenorth.commandmodels.ExtendCommandModel;
import com.truenorth.commandmodels.PsfCommandModel;
import com.truenorth.commands.UmbrellaCommand;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Future;

/**
 * 
 * High level command to run deconvolution process. 
 * 
 * @author bnorthan
 *
 * @param <T>
 */
@Plugin(type=Command.class, menuPath="Plugins>Deconvolution>Deconware")
public class DeconvolutionCommand<T extends RealType<T> & NativeType<T>> extends UmbrellaCommand
{
	// deconvolution model will be used to set deconvolution command to use and related 
	// parameters 
	@Parameter
	DeconvolutionModel deconvolutionModel;
	
	// psf model will be used to set psf commmand to run and related parameters
	@Parameter
	PsfCommandModel psfModel;
	
	// extension model 
	@Parameter
	ExtendCommandModel extendModel;
	
	Dataset input;
	
	int measurementSizeX;
	int measurementSizeY;
	int measurementSizeZ;
	
	@Parameter(type=ItemIO.OUTPUT)
	protected Dataset output;
	
	@Override
	public void run()
	{
		// temp
		input=(Dataset)extendModel.getModule().getInput("input");
		String name=input.getName();
				
		measurementSizeX=(int)input.dimension(input.dimensionIndex(Axes.X));
		measurementSizeY=(int)input.dimension(input.dimensionIndex(Axes.Y));
		
		if (input.dimensionIndex(Axes.Z)!=-1)
	    {
	    	measurementSizeZ=(int)input.dimension(input.dimensionIndex(Axes.Z));
	    }
	    else
	    {
	    	measurementSizeZ=(int)input.dimension(input.dimensionIndex(Axes.Z));;
	    }
		
		if (isNonCirculent())
	    {
			runNonCirculent();
			
	    }
		else
		{
			runCirculent();
		}
		
		output.setName(name+" Deconvolved");
		output.setDirty(true);  
	}
	
	void runCirculent()
	{
		Dataset extended=extendImage();
		
		String psfCommandName=psfModel.getCommandClassName();
	    Module module=psfModel.getModule();
	     
	    Map<String, Object> inputMap=psfModel.getModule().getInputs();
	      
	    // PSF size is same as extended size
    	inputMap.put("xSize", extended.dimension(extended.dimensionIndex(Axes.X)));
	    inputMap.put("ySize", extended.dimension(extended.dimensionIndex(Axes.Y)));
	    
	    if (extended.dimensionIndex(Axes.Z)!=-1)
	    {
	    	inputMap.put("zSize",extended.dimension(extended.dimensionIndex(Axes.Z)));
	    }
	    else
	    {
	    	inputMap.put("zSize",1);
	    }
	    
	    CommandModule commandModule=runAndBlock(psfCommandName, inputMap);
    
	    Dataset psf = (Dataset)commandModule.getOutputs().get("output");
	    
	    inputMap=this.deconvolutionModel.getModule().getInputs();
	    
	    inputMap.put("input", extended);
	    inputMap.put("psf", psf);
	    
	    String deconCommandName=deconvolutionModel.getCommandClassName();
	    Module deconModule=deconvolutionModel.getModule();
	    
	    commandModule=runAndBlock(deconCommandName, inputMap);
	    
	    Dataset deconvolved = (Dataset)commandModule.getOutputs().get("output");
	    
	    commandModule=runAndBlock("com.truenorth.commands.dim.CropCommand", "input", deconvolved, "xSize", measurementSizeX, "ySize", measurementSizeY, "zSize", measurementSizeZ);
	
	    output=(Dataset)commandModule.getOutputs().get("output");
	    
	}
	
	void runNonCirculent()
	{
		
		
		Dataset extended=extendImage();
		
		Integer psfSizeX=(Integer)extendModel.getModule().getInput("psfSizeX");
    	Integer psfSizeY=(Integer)extendModel.getModule().getInput("psfSizeY");
    	Integer psfSizeZ=(Integer)extendModel.getModule().getInput("psfSizeZ");
    	
    	String psfCommandName=psfModel.getCommandClassName();
	    Module module=psfModel.getModule();
	     
	    Map<String, Object> inputMap=psfModel.getModule().getInputs();
	     
    	// size of valid region of PSF is defined by extension model
    	inputMap.put("xSize", psfSizeX);
	    inputMap.put("ySize", psfSizeY);
	    inputMap.put("zSize", psfSizeZ);
	    
	    CommandModule commandModule= runAndBlock(psfCommandName, inputMap);
	    
	    Dataset psfTemp = (Dataset)commandModule.getOutputs().get("output");
	    
	    // now make psf same size as extended image
	    int xSize=(int)extended.dimension(extended.dimensionIndex(Axes.X));
	    int ySize=(int)extended.dimension(extended.dimensionIndex(Axes.Y));
	    
	    Integer zSize;
	    if (extended.dimensionIndex(Axes.Z)!=-1)
	    {
	    	zSize=(int)extended.dimension(extended.dimensionIndex(Axes.Z));
	    }
	    else
	    {
	    	zSize=(int)extended.dimension(extended.dimensionIndex(Axes.Z));;
	    }
	      
	    commandModule=runAndBlock("com.truenorth.commands.dim.ExtendCommandDimension", "input", psfTemp,
	    		"dimensionX",xSize, "dimensionY",ySize, "dimensionZ",zSize, "boundaryType", "zero", "fftType", "speed");
	    				    
	    Dataset psf = (Dataset)commandModule.getOutputs().get("output");
	    
	    inputMap=this.deconvolutionModel.getModule().getInputs();
	    
	    inputMap.put("input", extended);
	    inputMap.put("psf", psf);
	    inputMap.put("convolutionStrategy", "noncirculant");
	    inputMap.put("imageWindowX", measurementSizeX);
	    inputMap.put("imageWindowY", measurementSizeY);
	    inputMap.put("imageWindowZ", measurementSizeZ);
	    inputMap.put("psfWindowX", psfSizeX);
	    inputMap.put("psfWindowY", psfSizeY);
	    inputMap.put("psfWindowZ", psfSizeZ);
	    inputMap.put("firstGuessType", "constant");
	    
	    String deconCommandName=deconvolutionModel.getCommandClassName();
	    Module deconModule=deconvolutionModel.getModule();
	    
	    commandModule=runAndBlock(deconCommandName, inputMap);
	    //commandService.run(deconCommandName, true, inputMap);
	    
	    Dataset deconvolved = (Dataset)commandModule.getOutputs().get("output");
	    
	    commandModule=runAndBlock("com.truenorth.commands.dim.CropCommand", "input", deconvolved, "xSize", measurementSizeX, "ySize", measurementSizeY, "zSize", measurementSizeZ);
	
	    output=(Dataset)commandModule.getOutputs().get("output");
	   
	}
	
	Dataset extendImage()
	{
		// get the name of the extension command
		String extendCommandName=extendModel.getCommandClassName();
			    
		// get the extension module
		Module extendModule=extendModel.getModule();
						
		CommandModule commandModule= runAndBlock(extendCommandName, extendModule.getInputs());
					
		return (Dataset)commandModule.getOutputs().get("output");
	}
	
	// utility function to find out if convolution model is noncirculent
	private boolean isNonCirculent()
	{
		if (extendModel.getCommandClassName().equals("com.truenorth.commands.dim.ExtendCommandNoncirculant"))
		{
			return true;
		}
		return false;
	}

}
