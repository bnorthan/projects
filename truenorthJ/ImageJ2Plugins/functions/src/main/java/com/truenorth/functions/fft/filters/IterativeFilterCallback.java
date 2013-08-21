package com.truenorth.functions.fft.filters;

import net.imglib2.type.numeric.RealType;
import net.imglib2.img.Img;
import net.imglib2.RandomAccessibleInterval;

public interface IterativeFilterCallback<T extends RealType<T>> 
{
    public void DoCallback(int iteration, RandomAccessibleInterval<T> image, Img<T> estimate, Img<T> reblurred);
}