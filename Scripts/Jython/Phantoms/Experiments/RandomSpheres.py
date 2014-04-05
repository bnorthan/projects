from Experiment import Experiment
import Phantoms

class RandomSpheres(Experiment):
	directory="/home/bnorthan/Brian2014/Images/General/Deconvolution/Phantoms/RandomSpheres2/"

	def __init__(self, measurementSizeX, measurementSizeY, measurementSizeZ, psfSizeX, psfSizeY, psfSizeZ, homeDirectory):	
		Experiment.__init__(self,measurementSizeX, measurementSizeY, measurementSizeZ, psfSizeX, psfSizeY, psfSizeZ, homeDirectory)
		self.numpoints=100;
		self.background=0.000001;
		self.directory=homeDirectory+"/RandomSpheres/"

	def CreatePhantom(self, command):
		module=command.run("com.truenorth.commands.phantom.CreatePhantomCommand", True, "xSize", self.objectSizeX, "ySize", self.objectSizeY, "zSize", self.objectSizeZ, "background", self.background).get();
		Phantoms.AddRandomSpheresInROI(command, self.numpoints, self.psfSizeX/2, self.psfSizeY/2, self.psfSizeZ/2, self.measurementSizeX, self.measurementSizeY, self.measurementSizeZ, 100, 1, 3)

		return module.getOutputs().get("output")
