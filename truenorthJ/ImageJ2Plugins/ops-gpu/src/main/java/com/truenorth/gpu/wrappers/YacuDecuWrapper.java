package com.truenorth.gpu.wrappers;

/**
 * Wrapper for YacuDecu deconvolution.  JNI is used to generate the c-header file.
 * 
 * TODO: mavenized JNI generation
 * 
 * @author bnorthan
 *
 */
public class YacuDecuWrapper 
{
	/**
	 * 
	 * interface to yacudecu c function
	 * 
	 * @param iter
	 * @param xSize
	 * @param ySize
	 * @param zSize
	 * @param imageBuffer
	 * @param psfBuffer
	 * @param outputBuffer
	 * @return
	 */
	public native int runYacuDecu(int iter, int xSize, 
			int ySize, int zSize, float[] imageBuffer, float[] psfBuffer, float[] outputBuffer);
	
	/**
	 * load the yacudecu wrapper library
	 */
	public void LoadLib()
	{
		System.loadLibrary("YacuDecuWrapper");
	}
}
