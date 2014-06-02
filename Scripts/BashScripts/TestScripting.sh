# script to test the scripting

# directory where the imagej projects are
projectsDir=~/Brian2014/Projects/deconware/code/projects/truenorthJ/ImageJ2Plugins/
scriptDir=~/Brian2014/Projects/deconware/code/projects/Scripts/Jython/

#scriptName=$scriptDir/Deconvolve/Deconvolve.py
#scriptName=$scriptDir/Grand_Challenge/DeconvolveChallengeImages_reflection.py
#scriptName=$scriptDir/Deconvolve/Deconvolve_noncirculant.py
#scriptName=$scriptDir/Deconvolve/Deconvolve_seminoncirculant.py
#scriptName=$scriptDir/Deconvolve/Deconvolve_reflection.py
#scriptName=$scriptDir/Phantoms/CreatePhantom_noncirculant.py
#scriptName=$scriptDir/Phantoms/test.py
#scriptName=$scriptDir/TestOps/TestOps.py
#scriptName=$scriptDir/Simple.py

#cd $projectsDir/ScriptTest
#mvn exec:java -Dexec.mainClass=com.truenorth.ScriptTest -Dexec.args="$scriptName"

cd $projectsDir
mvn clean
mvn

cd $projectsDir/ops
#mvn 

cd $projectsDir/ScriptTest
mvn dependency:copy-dependencies

cd $projectsDir/ScriptTest/target/dependency
rm jruby-complete-1.7.11.jar

cd $projectsDir/ScriptTest/target/

java -Xms12000m -Xmx12000m -classpath ScriptTest-1.0.0-SNAPSHOT.jar:dependency/* com.truenorth.ScriptTest $scriptName

