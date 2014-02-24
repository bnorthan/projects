# @StatusService status
# @DatasetService data
# @CommandService command
# @DisplayService display
# @IOService io

xSize=256
ySize=256
zSize=128
background=0.0

outname="/home/bnorthan/Brian2014/Images/General/Deconvolution/Phantoms/ScriptTest/phantom3.ome.tif"

# create a blank image and get the modul
module=command.run("com.truenorth.commands.phantom.CreatePhantomCommand", True, "xSize", xSize, "ySize", ySize, "zSize", zSize, "background", background).get();

# add a sphere
command.run("com.truenorth.commands.phantom.AddSphereCommand", True, "xCenter", xSize/2, "yCenter", ySize/2, "zCenter", zSize/2, "radius", 20, "intensity", 500).get();

# get the output and save it
#output=module.getOutputs().get("output");
output = module.getOutputs().values().toArray()[0];
io.save(output, outname);


