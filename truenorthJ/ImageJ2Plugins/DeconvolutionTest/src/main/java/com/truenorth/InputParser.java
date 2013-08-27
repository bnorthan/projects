package com.truenorth;

import java.lang.reflect.*;

import java.util.Map;
import java.util.HashMap;

import imagej.ImageJ;
import imagej.data.Dataset;

//import io.scif.img.ImgIOException;
import net.imglib2.exception.IncompatibleTypeException;

import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;
import com.truenorth.functions.StaticFunctions;
import java.util.*;

/**
 * A utility class that takes parameters from a command line input string and 
 * maps them to inputs for a imagej command
**/
public class InputParser 
{
	ImageJ ij;
	Class cl;
	
	Map<String, Class> classInputParameters = new HashMap<String, Class>();
	Map<String, Class> classOutputParameters = new HashMap<String, Class>();
	
	Map<String, Object> inputMap =new HashMap<String, Object>();
	Map<String, Object> outputMap=new HashMap<String, Object>();
	
	ArrayList<Dataset> inputDatasets = new ArrayList<Dataset>();
	
	String outputName;
	
	// constructor for an InputParser 
	public InputParser(ImageJ ij, Class cl) throws ClassNotFoundException
	{
		// set the imagej instance and the class
		this.ij=ij;
		this.cl=cl;
		
		try
		{
			parseClass();
		}
		catch (ClassNotFoundException ex)
		{
			throw ex;
		}
	}
	
	// parse the class to find all annotated parameters
	public void parseClass() throws ClassNotFoundException
	{
		Class cl=this.cl;
		
		// loop, parsing all super classes
		while ( cl!=null)
		{
			// parse the fields of the class
			parseFields(cl);
			
			// set cl equal to the superclass in order to parse it next time through the loop
			cl = cl.getSuperclass();
		}
	}
	
	// parse all fields of class cl
	private void parseFields(Class cl)
	{
		// get all the fields
		Field[] fields=cl.getDeclaredFields();
		
		// for each field
		for (Field f:fields)
		{
			// get the name of the field
			String name = f.getName();
			
			// check to see if the field is annotated as a parameter
			if (f.isAnnotationPresent(Parameter.class)==true)
			{
				// if so get the parameter info
				Parameter p=f.getAnnotation(Parameter.class);
			
				// if the field is an input
				if (p.type()==ItemIO.INPUT)
				{
					// put the name and class of the parameter into the input parameters map
					classInputParameters.put(f.getName(), f.getType());
				}
				// otherwise if the field is an output
				else if (p.type()==ItemIO.OUTPUT)
				{
					// put the name and class of the parameter into the output parameters map
					classOutputParameters.put(f.getName(), f.getType());
				}
			}
		}
	}
	
	// parse the argument string
	public void parseArgs(String[] args)
	{
		// loop through the arguments and parse each one
		for (String s:args)
        {
			parseArg(s);
	    }
	}
	
	// parse an argment in the form name=value
	void parseArg(String arg)
	{
		// make sure the = sign is there
		if (arg.contains("=")!=true)
		{
			return;
		}
	
		// split the argument into name and value
		String[] split = arg.split("=");
			
		// see if the name of the parameter matches a input parameter for the class
		Class cl = classInputParameters.get(split[0]);
		
		// if so...
		if (cl!=null)
		{
			// if the input parameter is a dataset
			if (cl==Dataset.class)
			{
				// load the dataset into the imagej environment
				try
				{
					
					//Dataset dataset = ij.io().loadDataset(split[1]);
					Dataset dataset = ij.dataset().open(split[1]);
					//Object obj=ij.io().open(split[1]);
					
		
					
					//Dataset dataset = (Dataset)obj;
					
					inputMap.put(split[0], dataset);
				
					inputDatasets.add(dataset);
				}
				catch(Exception ex)
				{
				}
				
			}
			// if the input parameter is an integer
			if (cl==int.class)
			{
				// cast the value to an int and put it into the map
				inputMap.put(split[0], Integer.parseInt(split[1]));
			}
			// if the input parameter is a long
			if (cl==long.class)
			{
				// cast the value to an int and put it into the map
				inputMap.put(split[0], Long.parseLong(split[1]));
			}
			// if the input parameter is a double
			if (cl==double.class)
			{
				// cast the value to a double and put it into the map
				inputMap.put(split[0], Double.parseDouble(split[1]));
			}
			// if the input parameter is a string
			if (cl==String.class)
			{
				inputMap.put(split[0], split[1]);
			}
			// if the input parameter is a boolean
			if (cl==boolean.class)
			{
				inputMap.put(split[0], Boolean.parseBoolean(split[1]));
			}

			return;
		}
		
		// see if the name of the parameter matches an output parameter
		cl=classOutputParameters.get(split[0]);
		
		// if so...
		if (cl!=null)
		{	
			if (cl==String.class)
			{
				outputMap.put(split[0], split[1]);
			}
			if (cl==Dataset.class)
			{
				outputMap.put(split[0], split[1]);
			}
			return;
		}
		
	}
	
	public Map<String, Object> getInputMap()
	{
		return inputMap;
	}
	
	public Map<String, Object> getOutputMap()
	{
		return outputMap;
	}
	
	public ArrayList<Dataset> getInputDatasets()
	{
		return inputDatasets;
	}
}
