# go to root directory of class files
cd ../truenorthJ/ImageJ2Plugins/functions/target/classes

# generate jni file
javah com.truenorth.functions.psf.PsfGenerator

# copy to COSM wrapper directory
cp com_truenorth_functions_psf_PsfGenerator.h ../../../../native/CosmPsfWrapper/


