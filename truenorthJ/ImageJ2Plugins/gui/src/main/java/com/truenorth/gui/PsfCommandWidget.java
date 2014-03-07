package com.truenorth.gui;

import imagej.widget.InputWidget;
import imagej.widget.WidgetModel;
import org.scijava.plugin.Plugin;
import com.truenorth.commandmodels.PsfCommandModel;

@Plugin(type = InputWidget.class)
public class PsfCommandWidget extends ModuleModelWidget
{
	@Override
	void createModel()
	{
		moduleModel=new PsfCommandModel();
	}
	
	@Override
	public boolean supports(final WidgetModel model) 
	{
		return model.isType(PsfCommandModel.class);
	}
}
