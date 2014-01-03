package com.truenorth.commands.norm;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

import com.truenorth.commands.AbstractVolumeProcessorCommandInPlace;
import com.truenorth.functions.StaticFunctions;

public class NormalizeCommand<T extends RealType<T> & NativeType<T>> extends AbstractVolumeProcessorCommandInPlace<T>
{
	@Override
	protected Img<T> processVolume(RandomAccessibleInterval<T> volume)
	{
		Img<T> imgInput=(Img<T>)(input.getImgPlus().getImg());
		
		// normalize the psf
     	StaticFunctions.norm(Views.iterable(volume));
     		
		return null;
	}
}
