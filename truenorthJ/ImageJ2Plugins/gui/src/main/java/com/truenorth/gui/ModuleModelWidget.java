package com.truenorth.gui;

import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Box;
import java.awt.Dimension;

import java.awt.Color;

import imagej.plugins.uis.swing.widget.SwingInputWidget;
import imagej.plugins.uis.swing.widget.SwingInputHarvester;

import imagej.widget.InputPanel;
import imagej.widget.WidgetModel;

import org.scijava.plugin.PluginInfo;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.PluginService;

import imagej.module.Module;
import imagej.module.ModuleInfo;
import imagej.module.ModuleService;

import imagej.module.process.PreprocessorPlugin;
import imagej.command.Command;
import imagej.command.CommandService;

import java.util.List;

import com.truenorth.commandmodels.ModuleModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * 
 * 
 * 
 * @author bnorthan
 *
 */
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
		
		// get the base class of the command
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
		
		ModuleInfo info;
		
		// use the command name to make sure we can create the module
		// the command name is a setting and is persisted if the plugin changed then it is possible
		// that the command may no longer exist. 
		try
		{
			if (commandClassName!=null)
			{
				info=commandService.getCommand(commandClassName);
				Module test=moduleService.createModule(info);
				
				// if the module was not created properly set the commandClassName is not valid so set it to null
				if (test==null)
				{
					commandClassName=null;
				}
			}
		}
		catch(Exception e)
		{
			commandClassName=null;
		}
		
		// if no current plugin name is set and we have a list of commands...
		if ( ( (commandClassName==null) || (commandClassName.equals(""))) && (commands!=null) )
		{
			if ( (commands.size()>0))
			{
				// set the current name equal to the first plugin that was found
				commandClassName=commands.get(0).getClassName();
			}
		}
		
		commandList.setSelectedItem(commandClassName);
		info=commandService.getCommand(commandClassName);
		moduleModel.setCommandClassName(commandClassName);
		moduleModel.setModule(moduleService.createModule(info));
		
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
		
		updateJPanel();
		updateModel();
		
		// add a callback to handle changing the command name
		commandList.addActionListener(this);
		
		this.getComponent().setLayout(new BoxLayout(this.getComponent(), BoxLayout.PAGE_AXIS) );
		
		commandList.setAlignmentX(0.0f);
		panel.getComponent().setAlignmentX(0.0f);
		this.getComponent().add(commandList);
		this.getComponent().add(panel.getComponent());
	
		// add in a spacer
		this.getComponent().add(Box.createRigidArea(new Dimension(0,15)));
	}
	
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
	public void doRefresh()
	{
		
	}
	
	@Override
	public boolean supports(final WidgetModel model) 
	{
		return false;
	}
	
}

