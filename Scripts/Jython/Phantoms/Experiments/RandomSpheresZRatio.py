from Experiment import Experiment
import Phantoms

# creates a phantom with random spheres for z spacing as big as XY

class RandomSpheresZRatio(Experiment):
	directory="/home/bnorthan/Brian2014/Images/General/Deconvolution/Phantoms/RandomSpheresZRatio/"

	def __init__(self, measurementSizeX, measurementSizeY, measurementSizeZ, psfSizeX, psfSizeY, psfSizeZ, homeDirectory):	
		Experiment.__init__(self,measurementSizeX, measurementSizeY, measurementSizeZ, psfSizeX, psfSizeY, psfSizeZ, homeDirectory)
		self.numpoints=7;
		self.background=100;
		self.directory=homeDirectory+"/RandomSpheresZRatio/"

	def CreatePhantom(self, command):
		print "Create Phantom"

		zStart=self.psfSizeZ/2+self.measurementSizeZ/2
		zWidth=2

		intensity=200000
		minSphereRadius=3
		maxSphereRadius=4

		XYSpaceToZSpaceRatio=0.333

		module=command.run("com.truenorth.commands.phantom.CreatePhantomCommand", True, "xSize", self.objectSizeX, "ySize", self.objectSizeY, "zSize", self.objectSizeZ, "background", self.background).get();
		Phantoms.AddRandomSpheresInROIZRatio(command, self.numpoints, self.psfSizeX/2, self.psfSizeY/2, zStart, self.measurementSizeX, self.measurementSizeY, zWidth, intensity, minSphereRadius, maxSphereRadius, XYSpaceToZSpaceRatio)

		return module.getOutputs().get("output")
