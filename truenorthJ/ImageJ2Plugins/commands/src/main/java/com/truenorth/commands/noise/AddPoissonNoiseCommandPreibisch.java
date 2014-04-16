package com.truenorth.commands.noise;


import com.truenorth.commands.CommandUtilities;
import com.truenorth.functions.noise.NoiseGenerator;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;

import net.imglib2.type.numeric.real.FloatType;

//import simulation.Tools;

import java.util.Random;

import org.scijava.plugin.Parameter;  
import org.scijava.plugin.Plugin;

import com.truenorth.functions.StaticFunctions;

@Plugin(type=AddPoissonNoiseCommand.class, menuPath="Plugins>Noise>Add Poisson Noise Preibisch")
public class AddPoissonNoiseCommandPreibisch extends AddPoissonNoiseCommand<FloatType>
{
	@Parameter(persist=false)
	float snr=1.0f;  
	
	@Override
	protected Img<FloatType> processVolume(RandomAccessibleInterval<FloatType> volume)
	{
		// create output memory and copy input into it
		Img<FloatType> in=(Img<FloatType>)input.getImgPlus().getImg();		
		Img<FloatType> out=CommandUtilities.createVolume(input);
		StaticFunctions.copy2(volume, out);
		
		Random rnd=new Random();
		//simulation.Tools.poissonProcess(out, snr, rnd);
		NoiseGenerator.poissonProcessPreibisch(out, snr, rnd);	
		
		return out;
	}
	
	@Override
	protected void setName()
	{
		output.setName("Poisson Noise Preibisch");
	}
}
