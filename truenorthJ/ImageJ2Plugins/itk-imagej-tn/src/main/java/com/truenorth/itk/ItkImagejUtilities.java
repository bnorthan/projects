package com.truenorth.itk;

import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.iterator.LocalizingZeroMinIntervalIterator;
import net.imglib2.type.numeric.RealType;

import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;

import org.itk.simple.Image;
import org.itk.simple.VectorUInt32;

public class ItkImagejUtilities 
{
	/** a main that launches imagej for debugging */
	public static void main(final String... args) throws Exception 
	{
		// Launch ImageJ
		imagej.Main.launch(args);
	}
	
	public static<T extends RealType<T>> Image simple3DITKImageFromInterval(RandomAccessibleInterval<T> interval)
	{
		int numDimensions = interval.numDimensions();
		
		long[] dimensions=new long[numDimensions];
		
		for (int i=0;i<numDimensions;i++)
		{
			dimensions[i]=interval.dimension(i);
		}
		
		Image itkImage=new Image( dimensions[0], dimensions[1], dimensions[2], org.itk.simple.PixelIDValueEnum.sitkFloat32);
				
		LocalizingZeroMinIntervalIterator i = new LocalizingZeroMinIntervalIterator(interval);
		RandomAccess<T> s = interval.randomAccess();
		
		VectorUInt32 index=new VectorUInt32(3);
		
		while (i.hasNext()) 
		{
		   i.fwd();
		   s.setPosition(i);
		   
		   index.set(0, i.getLongPosition(0));
		   index.set(1, i.getLongPosition(1));
		   index.set(2, i.getLongPosition(2));
		   
		   float pix=s.get().getRealFloat();
		   
		   itkImage.setPixelAsFloat(index, pix);
		}
		
		return itkImage;
	}
	
	public static<T extends RealType<T>> Img<T> simple3DITKImageToImg(Image itkImage, ImgFactory<T> imgFactory, T type)
	{
		
		//Img<T> output=StaticFunctions.Create3dImage(image, this.imgFactory, inputType);
		long[] dims=new long[3];
		
		dims[0]=itkImage.getWidth();
		dims[1]=itkImage.getHeight();
		dims[2]=itkImage.getDepth();
		
		Img<T> output=imgFactory.create(dims, type);
		
		LocalizingZeroMinIntervalIterator i = new LocalizingZeroMinIntervalIterator(output);
		RandomAccess<T> s = output.randomAccess();
		
		VectorUInt32 index=new VectorUInt32(3);
		
		while (i.hasNext()) 
		{
		   i.fwd();
		   s.setPosition(i);
		   
		   index.set(0, i.getLongPosition(0));
		   index.set(1, i.getLongPosition(1));
		   index.set(2, i.getLongPosition(2));
		   
		   float pix=itkImage.getPixelAsFloat(index);
		   
		   s.get().setReal(pix);
		}
		
		return output;
	}
}
