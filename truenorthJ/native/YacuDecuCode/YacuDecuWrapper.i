
%module YacuDecu_swig
%include carrays.i
%array_functions(float, float_array)
%include arrays_java.i
%apply float[] {float *};
%{
extern int deconv_device(unsigned int iter, size_t N1, size_t N2, size_t N3,
                  float *h_image, float *h_psf, float *h_object);
%}

extern int deconv_device(unsigned int iter, size_t N1, size_t N2, size_t N3,
                  float *h_image, float *h_psf, float *h_object);


