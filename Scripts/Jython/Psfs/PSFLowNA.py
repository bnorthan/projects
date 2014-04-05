from PSFModel import PSFModel

class PSFLowNA(PSFModel):
	def __init__(self, homeDirectory):
		PSFModel.__init__(self, \
			"Widefield", \
			"GibsonLanni", \
			100, \
			300, \
			50.0, \
			0.2, \
			1.515, \
			1.515, \
			1.515, \
			1.51, \
			10, \
			homeDirectory)
		self.directory=homeDirectory+"/PSF_LowNA/"

