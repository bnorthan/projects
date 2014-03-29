# parameters of the shell
shellPositionX=objectSizeX / 2
shellPositionY=objectSizeY / 2
shellPositionZ=objectSizeZ / 2
shellOuterRadius=42
shellInnerRadius=40
shellIntensity=1000
innerIntensity=100

command.run("com.truenorth.commands.phantom.AddShellCommand", True, "xCenter", shellPositionX, "yCenter", shellPositionY, "zCenter", shellPositionZ, "radius", shellOuterRadius, "innerRadius", shellInnerRadius, "intensity", shellIntensity, "innerIntensity", innerIntensity).get();

