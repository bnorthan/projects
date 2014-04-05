class PSFModel(object):
	def __init__(self, scopeType, psfModel, xySpace, zSpace, emissionWavelength, numericalAperture, designImmersionOilRefractiveIndex, \
					designSpecimenLayerRefractiveIndex, actualImmersionOilRefractiveIndex, \
					actualSpecimenLayerRefractiveIndex, actualPointSourceDepthInSpecimenLayer, homeDirectory):
		self.scopeType=scopeType 
		self.psfModel=psfModel 
		self.xySpace=xySpace
		self.zSpace=zSpace 
		self.emissionWavelength=emissionWavelength 
		self.numericalAperture=numericalAperture
		self.designImmersionOilRefractiveIndex=designImmersionOilRefractiveIndex 
		self.designSpecimenLayerRefractiveIndex=designSpecimenLayerRefractiveIndex 
		self.actualImmersionOilRefractiveIndex=actualImmersionOilRefractiveIndex 
		self.actualSpecimenLayerRefractiveIndex=actualSpecimenLayerRefractiveIndex
		self.actualPointSourceDepthInSpecimenLayer=actualPointSourceDepthInSpecimenLayer

	def CreatePsf(self, command, psfCommandName, xSize, ySize, zSize):
		module=command.run(psfCommandName, True, \
		"xSize", xSize, \
		"ySize", ySize, \
		"zSize", zSize, \
		"fftType", "none", \
		"scopeType", self.scopeType, \
		"psfModel", self.psfModel, \
		"xySpace", self.xySpace, \
		"zSpace", self.zSpace, \
		"emissionWavelength", self.emissionWavelength, \
		"numericalAperture", self.numericalAperture, \
		"designImmersionOilRefractiveIndex", self.designImmersionOilRefractiveIndex, \
		"designSpecimenLayerRefractiveIndex", self.designSpecimenLayerRefractiveIndex, \
		"actualImmersionOilRefractiveIndex", self.actualImmersionOilRefractiveIndex, \
		"actualSpecimenLayerRefractiveIndex", self.actualSpecimenLayerRefractiveIndex, \
		"actualPointSourceDepthInSpecimenLayer", self.actualPointSourceDepthInSpecimenLayer, \
		"centerPsf", True).get()

		return module.getOutputs().get("output");

