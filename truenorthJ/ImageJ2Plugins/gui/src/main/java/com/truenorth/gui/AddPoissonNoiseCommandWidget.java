package com.truenorth.gui;

import org.scijava.plugin.Plugin;

import imagej.widget.InputWidget;
import imagej.widget.WidgetModel;

import com.truenorth.commandmodels.PoissonNoiseModel;

@Plugin(type = InputWidget.class)
public class AddPoissonNoiseCommandWidget extends ModuleModelWidget
{
	@Override
	void createModel()
	{
		moduleModel=new PoissonNoiseModel();
	}
	
	@Override
	public boolean supports(final WidgetModel model) 
	{
		return model.isType(PoissonNoiseModel.class);
	}
}
