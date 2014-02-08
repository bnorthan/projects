package com.truenorth.commands.norm;

import imagej.command.Command;

import org.scijava.plugin.Plugin;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

import com.truenorth.commands.AbstractVolumeProcessorCommandInPlace;
import com.truenorth.functions.StaticFunctions;

@Plugin(type=Command.class, menuPath="Plugins>Deconvolution>Normalize")
public class NormalizeCommand<T extends RealType<T> & NativeType<T>> extends AbstractVolumeProcessorCommandInPlace<T>
{
	@Override
	protected Img<T> processVolume(RandomAccessibleInterval<T> volume)
	{
		Img<T> imgInput=(Img<T>)(input.getImgPlus().getImg());
		
     	StaticFunctions.norm(Views.iterable(volume));
     		
		return null;
	}
}
