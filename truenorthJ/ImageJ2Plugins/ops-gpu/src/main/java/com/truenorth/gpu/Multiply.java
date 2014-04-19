package com.truenorth.gpu;

import static jcuda.driver.JCudaDriver.*;

import java.io.*;

import jcuda.*;
import jcuda.driver.*;

import jcuda.runtime.JCuda;

/**
 * Simple multiply class copied from Jcuda help.  Used to test jcuda
 * 
 * TODO: delete when more realistic examples are implemented
 *
 */
public class Multiply 
{
	public static void MultiplyTest() throws IOException
	{
		float[] a = new float[] {(float)1.35};
		float[] b = new float[] {(float)2.5};
		float[] c = new float[1];
		
		String javaLibPath = System.getProperty("java.library.path");
        
		System.out.println(javaLibPath);
		
		// Enable exceptions and omit all subsequent error checks
        JCudaDriver.setExceptionsEnabled(true);
        
        // Create the PTX file by calling the NVCC
        //String ptxFileName = preparePtxFile("JCudaVectorAddKernel.cu");
        String ptxFileName = preparePtxFile("multiply.cu");
        
        // Initialize the driver and create a context for the first device.
        cuInit(0);
        CUdevice device = new CUdevice();
        cuDeviceGet(device, 0);
        CUcontext context = new CUcontext();
        cuCtxCreate(context, 0, device);
        
        // Load the ptx file.
        CUmodule module = new CUmodule();
        cuModuleLoad(module, ptxFileName);
        
        // Obtain a function pointer to the "multiply" function.
        CUfunction function = new CUfunction();
        cuModuleGetFunction(function, module, "multiply2");
        
        CUdeviceptr a_dev = new CUdeviceptr();
        cuMemAlloc(a_dev, Sizeof.FLOAT);
        cuMemcpyHtoD(a_dev, Pointer.to(a), Sizeof.FLOAT);
        
        CUdeviceptr b_dev = new CUdeviceptr();
        cuMemAlloc(b_dev, Sizeof.FLOAT);
        cuMemcpyHtoD(b_dev, Pointer.to(b), Sizeof.FLOAT);
        
        CUdeviceptr c_dev = new CUdeviceptr();
        cuMemAlloc(c_dev, Sizeof.FLOAT);
        
        Pointer kernelParameters = Pointer.to(
        		Pointer.to(a_dev),
        		Pointer.to(b_dev),
        		Pointer.to(c_dev)
        		);
        
        cuLaunchKernel(function, 1, 1, 1, 1, 1, 1, 0, null, kernelParameters, null);
        cuMemcpyDtoH(Pointer.to(c), c_dev, Sizeof.FLOAT);	    
        
        JCuda.cudaFree(a_dev);
        JCuda.cudaFree(b_dev);
        JCuda.cudaFree(c_dev);
        
        System.out.println("Result = "+c[0]);

	}
	
	 /**
     * The extension of the given file name is replaced with "ptx".
     * If the file with the resulting name does not exist, it is
     * compiled from the given file using NVCC. The name of the
     * PTX file is returned.
     *
     * @param cuFileName The name of the .CU file
     * @return The name of the PTX file
     * @throws IOException If an I/O error occurs
     */
    private static String preparePtxFile(String cuFileName) throws IOException
    {
        int endIndex = cuFileName.lastIndexOf('.');
        if (endIndex == -1)
        {
            endIndex = cuFileName.length()-1;
        }
        String ptxFileName = cuFileName.substring(0, endIndex+1)+"ptx";
        File ptxFile = new File(ptxFileName);
        if (ptxFile.exists())
        {
            return ptxFileName;
        }

        File cuFile = new File(cuFileName);
        if (!cuFile.exists())
        {
            throw new IOException("Input file not found: "+cuFileName);
        }
        String modelString = "-m"+System.getProperty("sun.arch.data.model");
        String command =
            "/usr/local/cuda-6.0/bin/nvcc " + modelString + " -ptx "+
            cuFile.getPath()+" -o "+ptxFileName;

        System.out.println("Executing\n"+command);
        
        Process process=null;
        
        try
        {
        	process = Runtime.getRuntime().exec(command);
        }
        catch (Exception ex)
        {
        	System.out.println("Exception\n"+ex.getMessage());
        }

        String errorMessage =
            new String(toByteArray(process.getErrorStream()));
        String outputMessage =
            new String(toByteArray(process.getInputStream()));
        int exitValue = 0;
        try
        {
            exitValue = process.waitFor();
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new IOException(
                "Interrupted while waiting for nvcc output", e);
        }

        if (exitValue != 0)
        {
            System.out.println("nvcc process exitValue "+exitValue);
            System.out.println("errorMessage:\n"+errorMessage);
            System.out.println("outputMessage:\n"+outputMessage);
            throw new IOException(
                "Could not create .ptx file: "+errorMessage);
        }

        System.out.println("Finished creating PTX file");
        return ptxFileName;
    }

    /**
     * Fully reads the given InputStream and returns it as a byte array
     *
     * @param inputStream The input stream to read
     * @return The byte array containing the data from the input stream
     * @throws IOException If an I/O error occurs
     */
    private static byte[] toByteArray(InputStream inputStream)
        throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte buffer[] = new byte[8192];
        while (true)
        {
            int read = inputStream.read(buffer);
            if (read == -1)
            {
                break;
            }
            baos.write(buffer, 0, read);
        }
        return baos.toByteArray();
    }

}
