package com.truenorth.commands.noise;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

import com.truenorth.commands.CommandUtilities;
import com.truenorth.functions.StaticFunctions;
import com.truenorth.functions.noise.NoiseGenerator;

import org.scijava.plugin.Plugin;
import imagej.command.Command;

@Plugin(type=AddPoissonNoiseCommand.class, menuPath="Plugins>Noise>Add Poisson Noise Gallo")
public class AddPoissonNoiseCommandGallo<T extends RealType<T>& NativeType<T>> extends AddPoissonNoiseCommand<T>
{
	@Override
	protected Img<T> processVolume(RandomAccessibleInterval<T> volume)
	{
		Img<T> in=(Img<T>)input.getImgPlus().getImg();		
		Img<T> out=CommandUtilities.createVolume(input);
		StaticFunctions.copy2(volume, out);
		
		NoiseGenerator.AddPoissonNoise(Views.iterable(out));
				
		return out;
	}
	
	@Override
	protected void setName()
	{
		output.setName("Poisson Noise Gallo");
	}

}
