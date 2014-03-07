package com.truenorth.gui;

import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;

import java.awt.Color;

import imagej.plugins.uis.swing.widget.SwingInputWidget;
import imagej.plugins.uis.swing.widget.SwingInputHarvester;

import imagej.widget.InputPanel;
import imagej.widget.WidgetModel;

import org.scijava.plugin.PluginInfo;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.PluginService;

import imagej.module.ModuleInfo;
import imagej.module.ModuleService;

import imagej.module.process.PreprocessorPlugin;
import imagej.command.Command;
import imagej.command.CommandService;

import java.util.List;

import com.truenorth.commandmodels.ModuleModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public abstract class ModuleModelWidget extends SwingInputWidget<ModuleModel> 	
		implements ActionListener
{
	@Parameter
	CommandService commandService;
	
	@Parameter
	ModuleService moduleService;
	
	@Parameter
	PluginService pluginService;
	
	InputPanel<JPanel, JPanel> panel;
	
	ModuleModel moduleModel;
	
	List<PluginInfo<Command>> commands;
	
	JComboBox<String> commandList;
	
	@Override
	public ModuleModel getValue() 
	{	
		return moduleModel;
	}
	
	abstract void createModel();
	
	@Override
	public void actionPerformed(final ActionEvent e) 
	{
		// get the new class name
		String newCommandName=commandList.getSelectedItem().toString();

		moduleModel.setCommandClassName(newCommandName);
		
		// create the new module
		ModuleInfo info=commandService.getCommand(newCommandName);
		moduleModel.setModule(moduleService.createModule(info));
		
		updateJPanel();
		
		updateModel();
	}
	
	@Override
	public void set(final WidgetModel model) 
	{
		super.set(model);
		
		if (model.getValue()!=null)
		{
			moduleModel=(ModuleModel)model.getValue();
		}
		else
		{
			createModel();
		}
		
		commandList=new JComboBox<String>();
		
		Class baseClass=moduleModel.getBaseClass();
		
		if (baseClass!=null)
		{
			// get a list of all plugins derived from the base class
			commands=pluginService.getPluginsOfType(baseClass);
			
			// place the list into the combo box
			for (PluginInfo<Command> c:commands)
			{
				System.out.println(c.getClassName());
				System.out.println(c.getName());
				
				commandList.addItem(c.getClassName());
			}
		}
		
		// get the current plugin name
		String commandClassName=moduleModel.getCommandClassName();
		
		// if no current plugin name is set and we have a list of commands...
		if ( ( (commandClassName==null) || (commandClassName.equals(""))) && (commands!=null) )
		{
			if ( (commands.size()>0))
			{
				// set the current name equal to the first plugin that was found
				commandClassName=commands.get(0).getClassName();
			}
		}
		
		// use the command name to create the module
		if (commandClassName!=null)
		{
			commandList.setSelectedItem(commandClassName);
			ModuleInfo info=commandService.getCommand(commandClassName);
			moduleModel.setCommandClassName(commandClassName);
			moduleModel.setModule(moduleService.createModule(info));
		}
		
		// get the preprocessor plugins
		List<? extends PreprocessorPlugin> pre=pluginService.createInstancesOfType(PreprocessorPlugin.class);
		
		// apply all preprocessors except for the SwingInputHavester.  The SwingInputHavester will be called in the updateJPanel function
		// and the resulting JPanel inserted recursively (instead of in it's own dialog). 
		for (PreprocessorPlugin p:pre)
		{
			if (p.getClass()!=SwingInputHarvester.class)
			{
				p.process(moduleModel.getModule());
			}
		}
		
		updateModel();
		
		updateJPanel();
		
		commandList.addActionListener(this);
		
		this.getComponent().setLayout(new BoxLayout(this.getComponent(), BoxLayout.PAGE_AXIS) );
		
		commandList.setAlignmentX(0.0f);
		panel.getComponent().setAlignmentX(0.0f);
		this.getComponent().add(commandList);
		this.getComponent().add(panel.getComponent());
	}
	
	private void updateJPanel()
	{
		SwingChainedInputHarvester harvester=new SwingChainedInputHarvester();
		getContext().inject(harvester);
	
		if (panel==null)
		{
			panel=harvester.createInputPanel();
			panel.getComponent().setBorder(BorderFactory.createLineBorder(Color.black));
		}
		
		// remove any previous widgets from the JPanel
		panel.getComponent().removeAll();
		
		// harvest the inputs
		try
		{
			harvester.buildPanel(panel, moduleModel.getModule());
		}
		catch (Exception e)
		{
			// Todo: Handle error
		}
		
		this.getComponent().revalidate();
	}
	
	@Override
	public boolean supports(final WidgetModel model) 
	{
		return false;
	}
	
	@Override
	public void doRefresh() 
	{
		
	}

}

