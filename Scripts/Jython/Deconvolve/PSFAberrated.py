from PSFModel import PSFModel

class PSFAberrated(PSFModel):
	def __init__(self, homeDirectory):
		PSFModel.__init__(self, \
			"Widefield", \
			"GibsonLanni", \
			100, \
			300, \
			500.0, \
			1.3, \
			1.515, \
			1.515, \
			1.515, \
			1.33, \
			10, \
			homeDirectory)
		self.directory=homeDirectory+"/PSF_Aberrated/"
