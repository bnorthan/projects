package com.truenorth.commands.psf;

import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.Attr;

import com.truenorth.commands.Constants;

import imagej.command.Command;
import imagej.data.Dataset;
import imagej.data.DatasetService;
import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

//import net.imglib2.img.ImgPlus;
import net.imglib2.meta.ImgPlus;

import com.truenorth.functions.StaticFunctions;

import com.truenorth.functions.fft.SimpleFFTFactory;
import com.truenorth.functions.psf.FlipPsfQuadrants;
import com.truenorth.functions.psf.PsfGenerator;

/**
 * A commmand to create a psf
 * @author bnorthan
 *
 * @param <T>
 */
@Plugin(type = CreatePsfCommand.class, menuPath = "Plugins>Deconvolution>Create PSF")
public class CreatePsfCommand <T extends RealType<T> & NativeType<T>> implements Command
{
	@Parameter
	protected DatasetService datasetService;
	
	@Parameter(type = ItemIO.OUTPUT)
	protected Dataset output;
	
	@Parameter(attrs=@Attr(name="ShowInChainedGUI", value="false"))
	long xSize=256;
	
	@Parameter
	long ySize=256;
	
	@Parameter
	long zSize=128;
	
	// if the fft type is not fftOptimizationNone, then the size of the PSF will be extended to the appropriate size for the
	// given optimization scheme
	@Parameter(label="fft type", choices={Constants.FFTOptimization.fftOptimizationNone, Constants.FFTOptimization.fftOptimizationSpeed, Constants.FFTOptimization.fftOptimizationSize})
	private String fftType;
	
	@Parameter(label="scope type", choices={Constants.PsfTypeStrings.widefield, Constants.PsfTypeStrings.twoPhoton, Constants.PsfTypeStrings.confocalCircular})
	private String scopeType;
	
	@Parameter(label="psf model", choices={Constants.PsfModelStrings.GibsonLanni, Constants.PsfModelStrings.Haeberle})
	private String psfModel;
	
	// x, y, and z spacing measured in nm
	@Parameter
	double xySpace=40;
	
	@Parameter
	double zSpace=100;
	
	// emission wavelength measured in nm
	@Parameter
	double emissionWavelength=500.0;
	
	// numerical Aperture
	@Parameter
	double numericalAperture=1.3;
	
	// The design index of refraction of the immersion oil
	@Parameter
	double designImmersionOilRefractiveIndex=1.515;
	 
	// The design index of refraction of the specimen
	@Parameter
	double designSpecimenLayerRefractiveIndex=1.515;
	
	// actual index of refraction of the immersion oil
	@Parameter
	double actualImmersionOilRefractiveIndex=1.515;
	
	// actual index of refraction of the specimen
	@Parameter
	double actualSpecimenLayerRefractiveIndex=1.33;
	
	// depth in the specimen at which the psf will be calculated
	// (in other words the psf is the image of a point source at this depth)
	@Parameter
	double actualPointSourceDepthInSpecimenLayer=10;
	
	@Parameter
	boolean centerPsf=true;

	@Override
	public void run()
	{
		System.out.println("Create PSF Command");
		
		System.out.println("Dimensions: "+xSize+" "+ySize+" "+zSize);
		
		System.out.println("XY Spacing: "+xySpace+" Z Spacing: "+zSpace);
		
		System.out.println("EMW: "+emissionWavelength+" NA: "+numericalAperture);
		
		System.out.println("Design lens RI: "+designImmersionOilRefractiveIndex+" Design specimen RI: "+designSpecimenLayerRefractiveIndex);
		
		System.out.println("Actual lens RI: "+actualImmersionOilRefractiveIndex+" Actual specimen RI: "+actualSpecimenLayerRefractiveIndex);
		
		System.out.println("Specimen Depth: "+actualPointSourceDepthInSpecimenLayer);
		
		if (fftType.equals(Constants.FFTOptimization.fftOptimizationSpeed))
		{
			long[] originalDim=new long[3];
			
			originalDim[0]=xSize;
			originalDim[1]=ySize;
			originalDim[2]=zSize;
			
			long[] fftDim=SimpleFFTFactory.GetPaddedInputSizeLong(originalDim);
			
			xSize=fftDim[0];
			ySize=fftDim[1];
			zSize=fftDim[2];
			
			System.out.println("fft dim is: "+xSize+" "+ySize+" "+zSize);
			
		}
		
		// current version of COSM seems to only accept PSF dimensions that are the same in x and y.  So define
		// psf dimensions using max(x,y); 
		int[] psfDim = new int[3];
				
		if (xSize>ySize)
		{
			psfDim[0]=(int)xSize;
			psfDim[1]=(int)xSize;
		}
		else
		{
			psfDim[0]=(int)ySize;
			psfDim[1]=(int)ySize;
		}
		
		psfDim[2]=(int)zSize;
		
		float[] psfSpace=new float[3];
		
		psfSpace[0]=(float)xySpace;
		psfSpace[1]=(float)xySpace;
		psfSpace[2]=(float)zSpace;
		
		// call the psf generator to create the psf...,.
		Img<FloatType> psf=PsfGenerator.CallGeneratePsf(psfDim, 
														psfSpace, 
														emissionWavelength,
														numericalAperture,
														designImmersionOilRefractiveIndex,
														designSpecimenLayerRefractiveIndex, 
														actualImmersionOilRefractiveIndex, 
														actualSpecimenLayerRefractiveIndex,
														actualPointSourceDepthInSpecimenLayer,
														Constants.PsfStringToPsfType(scopeType),
														Constants.PsfModelStringToModelType(psfModel));
		
		
		if (centerPsf)
		{
			psf=FlipPsfQuadrants.flip(psf, psf.factory(), psfDim);
		}
		else
		{
			StaticFunctions.Pause();
		}
		
		// since COSM creates a psf that is symmetric in xy need to crop to the size of dimensions that were passed in
        
        long[] start=new long[3];
        
        // calculate the starting point for the cropping operation
        if (xSize>ySize)
        {
        	start[0]=0;
        	start[1]=(xSize-ySize)/2;
        	start[2]=0;
        }
        else
        {
        	start[0]=(ySize-xSize)/2;
        	start[1]=0;
        	start[2]=0;
        }
        
        System.out.println(start[0]+" "+start[1]+" "+start[2]);
        
        long[] size= new long[3];
        
        size[0]=xSize;
        size[1]=ySize;
        size[2]=zSize;
        
        System.out.println(size[0]+" "+size[1]+" "+size[2]);
        
        // crop the psf
        Img<FloatType> cropped=StaticFunctions.crop(psf, start, size);
        
        // normalize the psf
     	StaticFunctions.norm(cropped);
     	
     	// wrap the psf as an image plus
		ImgPlus<FloatType> psfPlus = StaticFunctions.Wrap3DImg(cropped, "psf");
		
		// use the image plus to create the output dataset
		output = datasetService.create(psfPlus);
			
	}
	
	
}
