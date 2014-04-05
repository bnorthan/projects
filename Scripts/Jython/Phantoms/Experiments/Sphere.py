from Experiment import Experiment
import Phantoms

class Sphere(Experiment):
	
	def __init__(self, measurementSizeX, measurementSizeY, measurementSizeZ, psfSizeX, psfSizeY, psfSizeZ, homeDirectory):	
		Experiment.__init__(self,measurementSizeX, measurementSizeY, measurementSizeZ, psfSizeX, psfSizeY, psfSizeZ, homeDirectory)
	
		# parameters of the top sphere
		self.spherePositionX=self.objectSizeX / 2
		self.spherePositionY=self.objectSizeY / 2
		self.spherePositionZ=self.objectSizeZ / 2
		self.sphereRadius=5
		self.sphereIntensity=100

		self.background=0.000001

		self.directory=homeDirectory+"/Sphere/"

	def CreatePhantom(self, command):
		module=command.run("com.truenorth.commands.phantom.CreatePhantomCommand", True, "xSize", self.objectSizeX, "ySize", self.objectSizeY, "zSize", self.objectSizeZ, "background", self.background).get();

		module=Phantoms.AddSphere(command, self.spherePositionX, self.spherePositionY, self.spherePositionZ, self.sphereRadius, self.sphereIntensity);
		
		return module.getOutputs().get("output")
