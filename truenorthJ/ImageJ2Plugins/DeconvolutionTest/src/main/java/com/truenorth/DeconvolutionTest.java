package com.truenorth;

import net.imglib2.meta.ImgPlus;

import io.scif.img.ImgIOException;
import com.truenorth.functions.StaticFunctions;

import net.imagej.ImageJ;
import net.imagej.Dataset;
import org.scijava.command.CommandModule;
import java.util.concurrent.*;

import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import io.scif.SCIFIO;
import io.scif.img.ImgSaver;
import io.scif.AbstractSCIFIOPlugin;
import net.imglib2.exception.IncompatibleTypeException;

/**
 * This is a class meant to test deconvolution commands.
 * It's customized for deconvolution commands but could also be used for other filters and commands
 * 
 * Run from the command line with the following inputs
 * 
 * 1. fully qualified name of class
 * 2. parameters in the format: {parameter}={value}"
 * 
 * Or alternatively put multiple commands in a script (use extension .hackscript)
 * 
 * the parameter names correspond to the actual parameter names in the class
 * 
 * For example to test com.truenorth.commands.fft.ConvolutionCommand
 * 
 * DeconvolutionTest com.truenorth.commands.fft.ConvolutionCommand input=/home/Test.tif psf=/home/Psf.tif output=/home/output.tif
 * 
 */
public class DeconvolutionTest 
{	
	// Launch main instance of imagej
	ImageJ ij=null;
	
    public void Test( String[] args ) throws ImgIOException, IncompatibleTypeException
    {
    	System.out.println("Deconvolution Test Program" );
    	
    	boolean silent=false;
		
    	// look through all the input args to see if the silent parameter was passed in
		for (String s:args)
		{
			if (s.equals("silent"))
			{
				silent=true;
			}
		}
			
		if (ij==null)
		{
			if (!silent)
			{
				ij = net.imagej.Main.launch(args);
			}
			else
			{
				System.out.println("silent mode...");
				ij=new ImageJ();
			}
		}
		
	
		
        System.out.println("Number of arguments: "+args.length);
    
		Class cl=null;
		
	    // the first parameter should be the name of the class to test.  So try using
		// the first argument to create the class
		try
		{
			cl = Class.forName(args[0]);
		}
		catch (ClassNotFoundException ex)
		{
			// if the class can't be created then quit
			System.out.println("class "+args[0]+" not found!");
			return;
		}
			
		InputParser parser=null;
		
		// try and create an input parser class to decipher the inputs
		try
		{
			parser=new InputParser(ij, cl);
			
			// parse the input argument string
			parser.parseArgs(args);		
		}
		catch (ClassNotFoundException ex)
		{
			// if the input parser couldn't be created because the class wasn't found quit
			System.out.println("cl io.scif.AbstractSCIFIOPluginass not found!");
			return;
		}
		
		//print out information about the input datasets
		System.out.println("");
		System.out.println("Number of input datasets: "+parser.getInputDatasets().size());
		System.out.println("");
		
		// loop through all the input datasets
		for(Dataset dataset:parser.getInputDatasets())
		{
			System.out.println("");
			System.out.println(dataset.getName()+" num dimensions are: "+dataset.numDimensions());
			
			// print out the type and size of each dimensions
			for(int d=0;d<dataset.numDimensions();d++)
			{
				System.out.println("axes "+d+" is: "+dataset.axis(d).type());
			}
			
			if (!silent)
			{
				System.out.println("creating display for: "+dataset.getName());
			
				// create a display for the input dataset
				ij.display().createDisplay(dataset.getName(), dataset);	
			}
		}
		
		System.out.println();
		System.out.println("inputs are: ");
		for (Object obj: parser.getInputMap().values())
		{
			System.out.println(obj);
		}
		System.out.println();
		
		// run the command and get the future
		Future<CommandModule> future =ij.command().run(cl, true, parser.getInputMap());
		//Future<Module> future =ij.command().run(args[0], parser.getInputMap());
				
		CommandModule commandModule=null;
		
		// use the future to block io.scif.AbstractSCIFIOPlugin until the command is finished
		try
		{
			commandModule=future.get();
		}
		catch (Exception ex)
		{
			return;
		}
		
		StaticFunctions.PrintMemoryStatuses();
		
		// get the output
		Map<String, Object> outputs=commandModule.getOutputs();
		
		Dataset output=null;
		
		Object test=outputs.values().toArray()[0];
		
		if (outputs.containsKey("output"))
		{
			output=(Dataset)outputs.get("output");
		}
		
		// get the name of the output file
		String outputName = (String)(parser.getOutputMap().get("output"));
	
		// if the outputname is not null save the output dataset
		if ( (outputName!=null) && (output!=null) )
		{
			System.out.println("saving image as: ");
			
			System.out.println("the output name is: "+outputName);
			
			for(int d=0;d<output.numDimensions();d++)
			{ 	
				System.out.println("axes "+d+" is: "+output.axis(d).type());
			}
						
			ImgPlus<?> out=output.getImgPlus();
			
			final SCIFIO scifio = new SCIFIO();
			final ImgSaver saver = new ImgSaver(scifio.getContext());
			
			saver.saveImg(outputName, (ImgPlus)out);
		
		}
		else
		{
			System.out.println("No output was generated.");
		}
    }
    
    public static void PrintParameterInfo()
	{
    	System.out.println();
    	System.out.println("first parameter: name of class to test");
    	System.out.println("followed by parameters in the format: {parameter}={value}");
    	System.out.println();
	}
    
    public static void main( String[] args ) throws ImgIOException, IncompatibleTypeException
    {	
    	// if args length is 1 check for a script
    	if (args.length==1)
    	{
    		// check to see if the first input is a script
    		String extension = "";

    		int i = args[0].lastIndexOf('.');
    		if (i > 0) {
    			extension = args[0].substring(i+1);
    		}
    
    		// if there is a script run it
    		if (extension.equals("hackscript"))
    		{
    			System.out.println("hack script detected");
    			
    			// create a new instance of the test class
    			DeconvolutionTest deconvolutionTest=new DeconvolutionTest();
    			
    			try
    			{
    				BufferedReader br = new BufferedReader(new FileReader(args[0]));
    				String line;
    				
    				// Each line of the script should be a command and (optionally) parameters
    				// loop through it running each command
    				while ((line = br.readLine()) != null) 
    				{
    					System.out.println(line);
    					deconvolutionTest.Test(line.split(" "));
    				}
    				
    				br.close();
    				
    				return;
    			}
    			catch (IOException e)
    			{
    				// if something went wrong reading the script print a message and return
    				System.out.println("error reading script");
    				return;
    			}
    		}
    	}
    	
    	// if we get this far there wasn't a script so just pass all the arguments
    	// on to the test class
    	
    	new DeconvolutionTest().Test(args);
    	
    	return;
    }
    
}
