from PSFModel import PSFModel

class PSF_NA14_CY3_65_200_coverslip(PSFModel):
	def __init__(self, homeDirectory):
		PSFModel.__init__(self, \
			"Widefield", \
			"GibsonLanni", \
			65, \
			200, \
			570.0, \
			1.4, \
			1.515, \
			1.515, \
			1.515, \
			1.4, \
			1, \
			homeDirectory)
		self.directory=homeDirectory+"/PSF_NA14_CY3_65_200_coverslip/"

