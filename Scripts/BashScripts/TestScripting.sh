# script to test the scripting

# directory where the imagej projects are
projectsDir=~/Brian2014/Projects/deconware/code/projects/truenorthJ/ImageJ2Plugins/
scriptDir=~/Brian2014/Projects/deconware/code/projects/Scripts/Jython/

cd $projectsDir
mvn

#scriptName=$scriptDir/Grand_Challenge/DeconvolveChallengeImages_reflection.py
#scriptName=$scriptDir/Deconvolve/Deconvolve_noncirculant.py
#scriptName=$scriptDir/Deconvolve/Deconvolve_reflection.py
#scriptName=$scriptDir/Phantoms/CreatePhantom_noncirculant.py

cd $projectsDir/ScriptTest

mvn exec:java -Dexec.mainClass=com.truenorth.ScriptTest -Dexec.args="$scriptName"
