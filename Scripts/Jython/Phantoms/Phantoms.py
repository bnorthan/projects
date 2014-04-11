import random

def AddSphere(command, spherePositionX, spherePositionY, spherePositionZ, sphereRadius, sphereIntensity):
		return command.run("com.truenorth.commands.phantom.AddSphereCommand", True, "xCenter", spherePositionX, "yCenter", spherePositionY, "zCenter", spherePositionZ, "radius", sphereRadius, "intensity", sphereIntensity).get();

def AddPoint(command, x, y, z, intensity):
	return command.run("com.truenorth.commands.phantom.AddPointCommand", True, "xCenter", x, "yCenter", y, "zCenter", z, "intensity", intensity).get();

def AddRandomPointsInROI(command, num, xStart, yStart, zStart, xWidth, yWidth, zWidth, intensity):
	for n in range(0, num-1):
		random_x=random.randrange(xStart, xStart+xWidth)
		random_y=random.randrange(yStart, yStart+yWidth)
		random_z=random.randrange(zStart, zStart+zWidth)	
		print str(random_x)+" "+str(random_y)+" "+str(random_z)
		command.run("com.truenorth.commands.phantom.AddPointCommand", True, "xCenter", random_x, "yCenter", random_y, "zCenter", random_z, "intensity", intensity)

def AddRandomSpheresInROIZRatio(command, num, xStart, yStart, zStart, xWidth, yWidth, zWidth, intensity, minRadius, maxRadius, zRatio):
	for n in range(0, num-1):
		print n
		random_x=random.randrange(xStart, xStart+xWidth)
		random_y=random.randrange(yStart, yStart+yWidth)
		random_z=random.randrange(zStart, zStart+zWidth)	
		random_radius=random.randrange(minRadius, maxRadius)

		z_radius=random_radius*zRatio
		print z_radius

		if z_radius<1:
			z_radius=1	

		print str(random_x)+" "+str(random_y)+" "+str(random_z)
		command.run("com.truenorth.commands.phantom.AddAsymetricSphereCommand", True, "xCenter", random_x, "yCenter", random_y, "zCenter", random_z, "xRadius", random_radius, "yRadius", random_radius, "zRadius", z_radius, "intensity", intensity)

def AddRandomSpheresInROI(command, num, xStart, yStart, zStart, xWidth, yWidth, zWidth, intensity, minRadius, maxRadius):
	for n in range(0, num-1):
		random_x=random.randrange(xStart, xStart+xWidth)
		random_y=random.randrange(yStart, yStart+yWidth)
		random_z=random.randrange(zStart, zStart+zWidth)	
		random_radius=random.randrange(minRadius, maxRadius)

		print str(random_x)+" "+str(random_y)+" "+str(random_z)
		command.run("com.truenorth.commands.phantom.AddSphereCommand", True, "xCenter", random_x, "yCenter", random_y, "zCenter", random_z, "radius", random_radius, "intensity", intensity)

def AddShell():
	# parameters of the shell
	shellPositionX=experiment.objectSizeX / 2
	shellPositionY=experiment.objectSizeY / 2
	shellPositionZ=experiment.objectSizeZ / 2 -25
	shellOuterRadius=58
	shellInnerRadius=55
	shellIntensity=1000
	innerIntensity=100

