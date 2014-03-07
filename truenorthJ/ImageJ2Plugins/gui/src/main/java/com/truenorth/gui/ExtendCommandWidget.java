package com.truenorth.gui;

import org.scijava.plugin.Plugin;

import imagej.widget.InputWidget;
import imagej.widget.WidgetModel;

import com.truenorth.commandmodels.ExtendCommandModel;

@Plugin(type = InputWidget.class)
public class ExtendCommandWidget extends ModuleModelWidget
{
	@Override
	void createModel()
	{
		moduleModel=new ExtendCommandModel();
	}
	
	@Override
	public boolean supports(final WidgetModel model) 
	{
		return model.isType(ExtendCommandModel.class);
	}

}
