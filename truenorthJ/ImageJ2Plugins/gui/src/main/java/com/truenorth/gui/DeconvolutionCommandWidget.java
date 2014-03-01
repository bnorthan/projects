package com.truenorth.gui;

import org.scijava.plugin.Plugin;

import imagej.module.Module;
import imagej.module.ModuleInfo;
import imagej.widget.InputWidget;
import imagej.widget.WidgetModel;

import com.truenorth.commandmodels.DeconvolutionModel;
import com.truenorth.commands.psf.CreatePsfCommand;

@Plugin(type = InputWidget.class)
public class DeconvolutionCommandWidget extends ModuleModelWidget
{
	@Override
	void createModel()
	{
		moduleModel=new DeconvolutionModel();
	}
	
	@Override
	public boolean supports(final WidgetModel model) 
	{
		return model.isType(DeconvolutionModel.class);
	}
}
