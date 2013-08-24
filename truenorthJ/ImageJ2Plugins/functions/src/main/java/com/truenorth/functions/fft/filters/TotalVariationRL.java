// this implementation is based on IOCBIO code
// Function div_unit_grad has been converted to java from c.
// c code is from iocbio package, iocbio/microscope/src/ops_ext.c

// below is copyright notice

/*Copyright (c) 2009-2010, Laboratory of Systems Biology, Institute of
Cybernetics at Tallinn University of Technology.  All rights reserved.

Copyright (c) 2009-2010, Laboratory of Systems Biology, Institute of
Cybernetics at Tallinn University of Technology.  All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.

    * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the following
      disclaimer in the documentation and/or other materials provided
      with the distribution.

    * Neither the name of the Laboratory of Systems Biology nor the
      names of its contributors may be used to endorse or promote
      products derived from this software without specific prior
      written permission.

THIS SOFTWARE IS PROVIDED BY COPYRIGHT HOLDER AND CONTRIBUTORS ''AS
IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL COPYRIGHT HOLDER
OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

In addition to the terms of the license, we ask to acknowledge the use
of packages in scientific articles by citing the corresponding papers:

IOCBio Microscope:
*/

package com.truenorth.functions.fft.filters;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RandomAccess;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexFloatType;

import net.imglib2.Cursor;

/**
 * 
 * Total Variation Richardson Lucy
 * 
 * @author bnorthan
 *
 * @param <T>
 * @param <S>
 */
public class TotalVariationRL <T extends RealType<T>, S extends RealType<S>> extends RichardsonLucyFilter<T,S>
{
	float regularizationFactor;
	
	public TotalVariationRL( final RandomAccessibleInterval<T> image, final RandomAccessibleInterval<S> kernel,
			   final ImgFactory<T> imgFactory, final ImgFactory<S> kernelImgFactory,
			   final ImgFactory<ComplexFloatType> fftImgFactory )
	{
		super( image, kernel, imgFactory, kernelImgFactory, fftImgFactory );
	}
	
	public TotalVariationRL( final Img<T> image, final Img<S> kernel, final ImgFactory<ComplexFloatType> fftImgFactory )
	{
		super( image, kernel, fftImgFactory );
	}
	
	public TotalVariationRL( final Img< T > image, final Img< S > kernel ) throws IncompatibleTypeException
	{	
		super( image, kernel );
	}
	
	public TotalVariationRL(final RandomAccessibleInterval<T> image, 
			final RandomAccessibleInterval<S> kernel,
			final ImgFactory<T> imgFactory,
			final ImgFactory<S> kernelImgFactory) throws IncompatibleTypeException
	{
		super(image, kernel, imgFactory, kernelImgFactory);
	}
	
	@Override
	protected void ComputeEstimate(Img<T> correlation)
	{
		Img<T> dv_estimate = div_unit_grad();
		
		final Cursor<T> cursorCorrelation = correlation.cursor();
		final Cursor<T> cursorDV_estimate = dv_estimate.cursor();
		final Cursor<T> cursorEstimate = estimate.cursor();
		
		while (cursorEstimate.hasNext())
		{
			cursorCorrelation.fwd();
			cursorDV_estimate.fwd();
			cursorEstimate.fwd();
			
			cursorEstimate.get().mul(cursorCorrelation.get());
			cursorEstimate.get().mul(1/(1-regularizationFactor*cursorDV_estimate.get().getRealFloat()));
		}
	}

	static double hypot3(double a, double b, double c)
	{
	  return java.lang.Math.sqrt(a*a + b*b + c*c);
	}
	
	static double m(double a, double b)
	{
	  if (a<0 && b<0)
	    {
	      if (a >= b) return a;
	      return b;
	    }
	  if (a>0 && b>0)
	    {
	      if (a < b) return a;
	      return b;
	    }
	  return 0.0;
	}
	
	final double FLOAT32_EPS= 0.0;
	
	//Img<T> in

	Img<T> div_unit_grad()
	{
		  //PyObject* f = NULL;
		  int Nx, Ny, Nz;
		  int i, j, k, im1, im2, ip1, jm1, jm2, jp1, km1, km2, kp1;
		  
		  //npy_float64* f_data_dp = NULL;
		  //npy_float64* r_data_dp = NULL;
		  //npy_float32* f_data_sp = NULL;
		  //npy_float32* r_data_sp = NULL;
		  double hx, hy, hz;
		  double hx2, hy2, hz2;
		  
		  //PyArrayObject* r = NULL;
		  
		  double fip, fim, fjp, fjm, fkp, fkm, fijk;
		  double fimkm, fipkm, fjmkm, fjpkm, fimjm, fipjm, fimkp, fjmkp, fimjp;
		  double aim, bjm, ckm, aijk, bijk, cijk;
		  double Dxpf, Dxmf, Dypf, Dymf, Dzpf, Dzmf;
		  double Dxma, Dymb, Dzmc;
		  
		  //if (!PyArg_ParseTuple(args, "O(ddd)", &f, &hx, &hy, &hz))
		    //return NULL;
		  hx=1;hy=1;hz=1;
		  hx2 = 2*hx;  hy2 = 2*hy;  hz2 = 2*hz;
		  
		  /*if (!PyArray_Check(f))
		    {
		      PyErr_SetString(PyExc_TypeError,"first argument must be array");
		      return NULL;
		    }
		  if (PyArray_NDIM(f) != 3)
		    {
		      PyErr_SetString(PyExc_TypeError,"array argument must have rank 3");
		      return NULL;
		    }*/
		  
		  //Nx = PyArray_DIM(f, 0);
		  //Ny = PyArray_DIM(f, 1);
		  //Nz = PyArray_DIM(f, 2);
		  Nx = (int)estimate.dimension(0);
		  Ny = (int)estimate.dimension(1);
		  Nz = (int)estimate.dimension(2);
		  
		  //r = (PyArrayObject*)PyArray_SimpleNew(3, PyArray_DIMS(f), PyArray_TYPE(f));
		  long[] dimensions=new long[]{estimate.dimension(0), estimate.dimension(1), estimate.dimension(2)};
		  Img<T> out=estimate.factory().create(dimensions , estimate.firstElement());
		  
		 // f_data_sp = (npy_float32*)PyArray_DATA(f);
	     // r_data_sp = (npy_float32*)PyArray_DATA(r);
	     
		 RandomAccess<T> random = estimate.randomAccess();
		 RandomAccess<T> outRandom = out.randomAccess();
		 
		 for (i=0; i<Nx; i++)
	     {
	    	 im1 = (i>0?i-1:0);
	    	 im2 = (im1>0?im1-1:0);
	      	 ip1 = (i+1==Nx?i:i+1);
	      	 
	      	 for (j=0; j<Ny; j++)
	      	 {
	      		 jm1 = (j>0?j-1:0);
	      		 jm2 = (jm1>0?jm1-1:0);
	      		 jp1 = (j+1==Ny?j:j+1);
	      		 for (k=0; k<Nz; k++)
	      		 {
	      			 km1 = (k>0?k-1:0);
	      			 km2 = (km1>0?km1-1:0);
	      			 kp1 = (k+1==Nz?k:k+1);

	      			 try
	      			 {
	      			 //fimjm = *((npy_float32*)PyArray_GETPTR3(f, im1, jm1, k));      			 
	      		     random.setPosition(new int[]{im1, jm1, k});
	      			 fimjm = random.get().getRealFloat();
	      			 //fim = *((npy_float32*)PyArray_GETPTR3(f, im1, j, k));
	      			 random.setPosition(new int[]{im1, j, k});
	      			 fim = random.get().getRealDouble(); 
	      			 //fimkm = *((npy_float32*)PyArray_GETPTR3(f, im1, j, km1));
	      			 random.setPosition(new int[]{im1, j, km1});
	      			 fimkm= random.get().getRealDouble();
	      			 //fimkp = *((npy_float32*)PyArray_GETPTR3(f, im1, j, kp1));
	      			 random.setPosition(new int[]{im1, j, kp1});
	      			 fimkp= random.get().getRealDouble();
	      			 //fimjp = *((npy_float32*)PyArray_GETPTR3(f, im1, jp1, k));
	      			 random.setPosition(new int[]{im1, jp1, k});
	      			 fimjp= random.get().getRealDouble();
	      			 
	      			 //fjmkm = *((npy_float32*)PyArray_GETPTR3(f, i, jm1, km1));
	      			 random.setPosition(new int[]{i, jm1, km1});
	      			 fjmkm= random.get().getRealDouble();
	      			 //fjm = *((npy_float32*)PyArray_GETPTR3(f, i, jm1, k));
	      			 random.setPosition(new int[]{i, jm1, k});
	      			 fjm= random.get().getRealDouble();
	      			 //fjmkp = *((npy_float32*)PyArray_GETPTR3(f, i, jm1, kp1));
	      			 random.setPosition(new int[]{i, jm1, kp1});
	      			 fjmkp= random.get().getRealDouble();
	      			 
	      			 //fkm = *((npy_float32*)PyArray_GETPTR3(f, i, j, km1));
	      			 random.setPosition(new int[]{i, j, km1});
	      			 fkm= random.get().getRealDouble();
	      			 //fijk = *((npy_float32*)PyArray_GETPTR3(f, i, j, k));
	      			 random.setPosition(new int[]{i, j, k});
	      			 fijk= random.get().getRealDouble();
	      			 //fkp = *((npy_float32*)PyArray_GETPTR3(f, i, j, kp1));
	      			 random.setPosition(new int[]{i, j, kp1});
	      			 fkp= random.get().getRealDouble();

	      			 //fjpkm = *((npy_float32*)PyArray_GETPTR3(f, i, jp1, km1));
	      			 random.setPosition(new int[]{i, jp1, km1});
	      			 fjpkm= random.get().getRealDouble();
	      			 //fjp = *((npy_float32*)PyArray_GETPTR3(f, i, jp1, k));
	      			 random.setPosition(new int[]{i, jp1, k});
	      			 fjp= random.get().getRealDouble();

	      			 //fipjm = *((npy_float32*)PyArray_GETPTR3(f, ip1, jm1, k));
	      			 random.setPosition(new int[]{ip1, jm1, k});
	      			 fipjm= random.get().getRealDouble();
	      			 //fipkm = *((npy_float32*)PyArray_GETPTR3(f, ip1, j, km1));
	      			 random.setPosition(new int[]{ip1, j, km1});
	      			 fipkm= random.get().getRealDouble();
	      			 //fip = *((npy_float32*)PyArray_GETPTR3(f, ip1, j, k));
	      			 random.setPosition(new int[]{ip1, j, k});
	      			 fip= random.get().getRealDouble();
	      			 
	      			
	      			 Dxpf = (fip - fijk) / hx;
	      			 Dxmf = (fijk - fim) / hx;
	      			 Dypf = (fjp - fijk) / hy;
	      			 Dymf = (fijk - fjm) / hy;
	      			 Dzpf = (fkp - fijk) / hz;
	      			 Dzmf = (fijk - fkm) / hz;
	      			 aijk = hypot3(Dxpf, m(Dypf, Dymf), m(Dzpf, Dzmf));
	      			 bijk = hypot3(Dypf, m(Dxpf, Dxmf), m(Dzpf, Dzmf));
	      			 cijk = hypot3(Dzpf, m(Dypf, Dymf), m(Dxpf, Dxmf));

	      			 aijk = (aijk>FLOAT32_EPS?Dxpf / aijk:0.0);
	      			 bijk = (bijk>FLOAT32_EPS?Dypf / bijk: 0.0);
	      			 cijk = (cijk>FLOAT32_EPS?Dzpf / cijk:0.0); 
			  

	      			 Dxpf = (fijk - fim) / hx;
	      			 Dypf = (fimjp - fim) / hy;
	      			 Dymf = (fim - fimjm) / hy;
	      			 Dzpf = (fimkp - fim) / hz;
	      			 Dzmf = (fim - fimkm) / hz;
	      			 aim = hypot3(Dxpf, m(Dypf, Dymf), m(Dzpf, Dzmf));

	      			 aim = (aim>FLOAT32_EPS?Dxpf/aim:0.0); 


	      			 Dxpf = (fipjm - fjm) / hx;
	      			 Dxmf = (fjm - fimjm) / hx;
	      			 Dypf = (fijk - fjm) / hy;
	      			 Dzpf = (fjmkp - fjm) / hz;
	      			 Dzmf = (fjm - fjmkm) / hz;
	      			 bjm = hypot3(Dypf, m(Dxpf, Dxmf), m(Dzpf, Dzmf));

	      			 bjm = (bjm>FLOAT32_EPS?Dypf/bjm:0.0);
			  

	      			 Dxpf = (fipkm - fkm) / hx;
	      			 Dxmf = (fjm - fimkm) / hx;
	      			 Dypf = (fjpkm - fkm) / hy;
	      			 Dymf = (fkm - fjmkm) / hy;
	      			 Dzpf = (fijk - fkm) / hz;
	      			 ckm = hypot3(Dzpf, m(Dypf, Dymf), m(Dxpf, Dxmf));

	      			 ckm = (ckm>FLOAT32_EPS?Dzpf/ckm:0.0); 

	      			 Dxma = (aijk - aim) / hx;
	      			 Dymb = (bijk - bjm) / hy;
	      			 Dzmc = (cijk - ckm) / hz;
			  
	      			 //*((npy_float32*)PyArray_GETPTR3(r, i, j, k)) = Dxma/hx + Dymb/hy + Dzmc/hz;
	      			 //*((npy_float32*)PyArray_GETPTR3(r, i, j, k)) = Dxma + Dymb + Dzmc;
	      			 outRandom.setPosition(new int[]{i,j,k});
	      			 outRandom.get().setReal(Dxma+Dymb+Dzmc);
	      			 }
	      			 catch (java.lang.ArrayIndexOutOfBoundsException ex)
	      			 {
	      				 int stop=5;
	      			 }

	      		 }
		    }
		}

		return out;
	}
	
	public void setRegularizationFactor(float regularizationFactor)
	{
		this.regularizationFactor=regularizationFactor;
	}
}
