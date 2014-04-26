package com.truenorth;

import net.imagej.ImageJ;
import org.scijava.script.ScriptModule;
import org.scijava.script.ScriptService;
import org.scijava.Context;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import javax.script.ScriptException;

import java.io.File;

public class ScriptTest 
{
	public static void main( String[] args ) throws InterruptedException, ExecutionException,
	IOException, ScriptException
	{
		System.out.println("Hello Script Test");
		
		String scriptName=null;
		String script;
		
		final ImageJ ij = net.imagej.Main.launch(args);
		
		System.out.println("Launched");
    	
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
	
}
