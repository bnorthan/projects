package com.truenorth.gui;

import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Plugin;

import imagej.module.ModuleInfo;
import imagej.module.Module;
import imagej.widget.InputWidget;
import imagej.widget.WidgetModel;

import com.truenorth.commandmodels.ExtendCommandModel;
import com.truenorth.commandmodels.ModuleModel;
import com.truenorth.commands.dim.ExtendCommandDimension;

@Plugin(type = InputWidget.class)
public class ExtendCommandWidget<T extends RealType<T> & NativeType<T>> extends ModuleModelWidget<T>
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
