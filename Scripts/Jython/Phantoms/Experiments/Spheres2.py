from Experiment import Experiment
import Phantoms

class Spheres2(Experiment):
	
	def __init__(self, measurementSizeX, measurementSizeY, measurementSizeZ, psfSizeX, psfSizeY, psfSizeZ, homeDirectory):	
		Experiment.__init__(self,measurementSizeX, measurementSizeY, measurementSizeZ, psfSizeX, psfSizeY, psfSizeZ, homeDirectory)
	
		# parameters of the top sphere
		self.spherePositionX=self.objectSizeX / 2
		self.spherePositionY=self.objectSizeY / 2
		self.spherePositionZ=self.objectSizeZ / 2 - 27
		self.sphereRadius=5
		self.sphereIntensity=10000

		# parameters of the bottom sphere
		self.spherePosition2X=self.objectSizeX / 2
		self.spherePosition2Y=self.objectSizeY / 2
		self.spherePosition2Z=self.objectSizeZ / 2 
		self.sphereRadius2=5
		self.sphereIntensity2=10000

		self.background=0.000001

		self.directory=homeDirectory+"/SpheresHighIntensity/"

	def CreatePhantom(self, command):
		module=command.run("com.truenorth.commands.phantom.CreatePhantomCommand", True, "xSize", self.objectSizeX, "ySize", self.objectSizeY, "zSize", self.objectSizeZ, "background", self.background).get();

		Phantoms.AddSphere(command, self.spherePositionX, self.spherePositionY, self.spherePositionZ, self.sphereRadius, self.sphereIntensity);
		
		module=Phantoms.AddSphere(command, self.spherePosition2X, self.spherePosition2Y, self.spherePosition2Z, self.sphereRadius, self.sphereIntensity2);
		
		return module.getOutputs().get("output")
