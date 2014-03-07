package com.truenorth.commands;

import com.truenorth.functions.psf.PsfGenerator.PsfType;
import com.truenorth.functions.psf.PsfGenerator.PsfModel;

/**
 * A list of constants that are used with commands
 * 
 * Todo: As it grows the inner classes should be put in their own file
 * 
 * @author bnorthan
 *
 */
public class Constants 
{
	public class FFTOptimization
	{
		public static final String fftOptimizationNone="none";
		public static final String fftOptimizationSpeed="speed";
		public static final String fftOptimizationSize="size";	
	}
	
	public class Boundary
	{
		public static final String boundaryZero="zero";
		public static final String boundaryMirror="mirror";
	}
	
	public class ConvolutionStrategy
	{
		public static final String circulant="circulant";
		public static final String noncirculant="noncirculant";
	}
	
	public class FirstGuess
	{
		public static final String measuredImage="measured image";
		public static final String constant="constant";
		public static final String blurredMeasured="blurred measured image";
		public static final String input="input";
	}
	
	public class PsfTypeStrings
	{
		public static final String widefield="Widefield";
		public static final String twoPhoton="Two Photon";
		public static final String confocalCircular="Confocal Circular";
		public static final String confocalLine="Confocal Line";
	}
	
	public static PsfType PsfStringToPsfType(String psfString)
	{
		if (psfString.equals(PsfTypeStrings.widefield))
		{
			return PsfType.WIDEFIELD;
		}
		else if (psfString.equals(PsfTypeStrings.twoPhoton))
		{
			return PsfType.TWO_PHOTON;
		}
		else if (psfString.equals(PsfTypeStrings.confocalCircular))
		{
			return PsfType.CONFOCAL_CIRCULAR;
		}
		else if(psfString.equals(PsfTypeStrings.confocalLine))
		{
			return PsfType.CONFOCAL_LINE;
		}
		else
		{
			return null;
		}
	}
	
	public static PsfModel PsfModelStringToModelType(String modelString)
	{
		if (modelString.equals(PsfModelStrings.GibsonLanni))
		{
			return PsfModel.GIBSON_LANI;
		}
		else if (modelString.equals(PsfModelStrings.Haeberle))
		{
			return PsfModel.HAEBERLE;
		}
		else
		{
			return null;
		}
	}
	
	public class PsfModelStrings
	{
		public static final String GibsonLanni="GibsonLanni";
		public static final String Haeberle="Haeberle";
	}
}
