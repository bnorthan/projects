class Experiment(object):
	def __init__(self, measurementSizeX, measurementSizeY, measurementSizeZ, psfSizeX, psfSizeY, psfSizeZ, homeDirectory):
		self.homeDirectory=homeDirectory
		
		# size of the measurement 
		self.measurementSizeX=measurementSizeX
		self.measurementSizeY=measurementSizeY
		self.measurementSizeZ=measurementSizeZ

		# size of the psf
		self.psfSizeX=psfSizeX
		self.psfSizeY=psfSizeY
		self.psfSizeZ=psfSizeZ

		# size of the object space
		self.objectSizeX=measurementSizeX+psfSizeX-1
		self.objectSizeY=measurementSizeY+psfSizeY-1
		self.objectSizeZ=measurementSizeZ+psfSizeZ-1

