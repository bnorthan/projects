
# this script creates a phantom image

echo Running simple create phantom script

export MAVEN_OPTS="-Xmx4096m"

# desired size of phantom
xSize=192
ySize=192
zSize=64

sphere1X=$(expr $xSize / 3)
sphere1Y=$(expr $ySize / 2)
sphere1Z=$(expr $zSize / 2)

# make the ImageJ2 projects
cd ../truenorthJ/ImageJ2Plugins/

phantomName=../../../Images/Phantom.ome.tif

mvn

cd DeconvolutionTest

# create the phantom
mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.phantom.CreatePhantomCommand silent output=$phantomName xSize=$xSize ySize=$ySize zSize=$zSize"

# add the sphere
mvn exec:java -Dexec.mainClass=com.truenorth.DeconvolutionTest -Dexec.args="com.truenorth.commands.phantom.AddSphereCommand silent input=$phantomName output=$phantomName xCenter=$sphere1X yCenter=$sphere1Y zCenter=$sphere1Z radius=10 intensity=12.5"


