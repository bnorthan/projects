package com.truenorth.gui;

import imagej.module.Module;
import imagej.module.process.PreprocessorPlugin;
import imagej.plugins.uis.swing.sdi.SwingUI;
import imagej.ui.AbstractInputHarvesterPlugin;
import imagej.util.swing.SwingDialog;
import imagej.widget.InputHarvester;
import imagej.widget.InputPanel;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.scijava.Priority;
import org.scijava.plugin.Plugin;

import imagej.plugins.uis.swing.widget.SwingInputPanel;

/**
 * SwingInputHarvester is an {@link InputHarvester} that collects input
 * parameter values from the user using a {@link SwingInputPanel} dialog box.
 * 
 * @author Curtis Rueden
 * @author Barry DeZonia
 */
public class SwingChainedInputHarvester extends
	AbstractChainedInputHarvester<JPanel, JPanel>
{

	// -- InputHarvester methods --

	@Override
	public SwingInputPanel createInputPanel() {
		return new SwingInputPanel();
	}

	@Override
	public boolean harvestInputs(final InputPanel<JPanel, JPanel> inputPanel,
		final Module module)
	{
		final JPanel pane = inputPanel.getComponent();

		// display input panel in a dialog
		final String title = module.getInfo().getTitle();
		final boolean modal = !module.getInfo().isInteractive();
		final boolean allowCancel = module.getInfo().canCancel();
		final int optionType, messageType;
		if (allowCancel) optionType = JOptionPane.OK_CANCEL_OPTION;
		else optionType = JOptionPane.DEFAULT_OPTION;
		if (inputPanel.isMessageOnly()) {
			if (allowCancel) messageType = JOptionPane.QUESTION_MESSAGE;
			else messageType = JOptionPane.INFORMATION_MESSAGE;
		}
		else messageType = JOptionPane.PLAIN_MESSAGE;
		final boolean doScrollBars = messageType == JOptionPane.PLAIN_MESSAGE;
		final SwingDialog dialog =
			new SwingDialog(pane, optionType, messageType, doScrollBars);
		dialog.setTitle(title);
		dialog.setModal(modal);
		final int rval = dialog.show();

		// verify return value of dialog
		return rval == JOptionPane.OK_OPTION;
	}

}
