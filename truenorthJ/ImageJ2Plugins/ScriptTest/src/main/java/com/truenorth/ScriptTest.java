package com.truenorth;

import imagej.ImageJ;
import imagej.ops.*;  
import imagej.script.ScriptLanguage;
import imagej.script.ScriptModule;
import imagej.script.ScriptService;
import org.scijava.Context;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import javax.script.ScriptException;

import java.io.File;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ItemIO;

public class ScriptTest 
{
	public static void main( String[] args ) throws InterruptedException, ExecutionException,
	IOException, ScriptException
	{
		System.out.println("Hello Script Test");
		
		String scriptName=null;
		String script;
		
		// if args length is 1 check for a script
    	
		final ImageJ ij = imagej.Main.launch(args);
    	
		final Context context = ij.getContext();//new Context(ScriptService.class);
		final ScriptService scriptService = context.getService(ScriptService.class);
		
		for (int i=1;i<10;i++)
		{
			System.out.println("||||||||||||||||||||||||");
		}
		ScriptModule m;
		if (args.length==1)
    	{
    		File file=new File(args[0]);
    		System.out.println("scriptName: "+args[0]);
    		m=scriptService.run(file, true).get();
    	}
    	else
    	{
    		scriptName="add.py";
    		script = "1 + 6";
    		m=scriptService.run(scriptName, script, true).get();
    	}
		//final ScriptModule ;		
		final Object result = m.getReturnValue();
		
		
		System.out.println("||||||||||||||||||||||||");
		System.out.println("Script Result: "+result);
		System.out.println("||||||||||||||||||||||||");
		
    }
	
	@Plugin(type = Op.class, name = "narf")
	public static class Narf implements Op {

		@Parameter(type = ItemIO.BOTH)
		private String string;

		@Override
		public void run() {
			string = "Egads! " + string.toUpperCase();
		}
	}
}
