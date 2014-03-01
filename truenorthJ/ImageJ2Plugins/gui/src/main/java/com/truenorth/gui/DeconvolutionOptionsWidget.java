package com.truenorth.gui;

import imagej.widget.InputWidget;
import imagej.widget.WidgetModel;
import imagej.plugins.uis.swing.widget.SwingInputWidget;
import imagej.plugins.uis.swing.widget.SwingNumberWidget;
import imagej.plugins.uis.swing.widget.SpinnerBigIntegerModel;
import imagej.plugins.uis.swing.widget.SpinnerNumberModelFactory;

import com.truenorth.commands.fft.DeconvolutionOptions;

import org.scijava.plugin.Plugin;

import javax.swing.*;

@Plugin(type = InputWidget.class)
public class DeconvolutionOptionsWidget extends SwingInputWidget<DeconvolutionOptions>
{
	DeconvolutionOptions options=new DeconvolutionOptions();
	
	int iterations=10;
	
	@Override
	public DeconvolutionOptions getValue() 
	{
		return options;
	}
	
	@Override
	public void set(final WidgetModel model) 
	{
		super.set(model);
		options = (DeconvolutionOptions) model.getValue();
		
		this.getComponent().add(new JLabel("hello here!"));
		
	//	SpinnerNumberModel iterationsModel=new SpinnerNumberModelFactory().createModel(iterations, 1, 10000, 1);
		
	//	SwingNumberWidget iterationsWidget=new SwingNumberWidget();
	//	iterationsWidget.set(iterationsModel);
		
	}
	
	@Override
	public boolean supports(final WidgetModel model) 
	{
		return model.isType(DeconvolutionOptions.class);
	}
	
	@Override
	public void doRefresh() 
	{
		
	}
}
