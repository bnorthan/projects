from Experiment import Experiment
import Phantoms

# creates a phantom with random spheres, takes into account z ratio (z spacing)

class RandomSpheresZRatio(Experiment):
	directory="/home/bnorthan/Brian2014/Images/General/Deconvolution/Phantoms/RandomSpheresZRatio/"

	def __init__(self, measurementSizeX, measurementSizeY, measurementSizeZ, psfSizeX, psfSizeY, psfSizeZ, homeDirectory):	
		Experiment.__init__(self,measurementSizeX, measurementSizeY, measurementSizeZ, psfSizeX, psfSizeY, psfSizeZ, homeDirectory)
		self.numpoints=7;
		self.background=0.000001;
		self.directory=homeDirectory+"/RandomSpheresZRatio/"

	def CreatePhantom(self, command):
		print "Create Phantom"

		zStart=self.psfSizeZ/2+self.measurementSizeZ/2-5
		zSize=2

		module=command.run("com.truenorth.commands.phantom.CreatePhantomCommand", True, "xSize", self.objectSizeX, "ySize", self.objectSizeY, "zSize", self.objectSizeZ, "background", self.background).get();
		Phantoms.AddRandomSpheresInROIZRatio(command, self.numpoints, self.psfSizeX/2, self.psfSizeY/2, zStart, self.measurementSizeX, self.measurementSizeY, zSize, 100000, 2, 4, 0.333)

		return module.getOutputs().get("output")
