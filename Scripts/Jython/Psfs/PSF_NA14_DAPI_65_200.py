from PSFModel import PSFModel

class PSF_NA14_DAPI_65_200(PSFModel):
	def __init__(self, homeDirectory):
		PSFModel.__init__(self, \
			"Widefield", \
			"GibsonLanni", \
			65, \
			200, \
			500.0, \
			1.4, \
			1.515, \
			1.515, \
			1.515, \
			1.4, \
			5, \
			homeDirectory)
		self.directory=homeDirectory+"/PSF_NA14_DAPI_65_200/"

