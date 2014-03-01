package com.truenorth.gui;

import imagej.module.Module;
import imagej.module.ModuleCanceledException;
import imagej.module.ModuleException;
import imagej.module.ModuleItem;
import imagej.command.CommandModuleItem;
import imagej.widget.InputHarvester;
import imagej.widget.InputPanel;
import imagej.widget.InputWidget;
import imagej.widget.WidgetModel;
import imagej.widget.WidgetService;

import java.util.ArrayList;
import java.util.List;

import org.scijava.AbstractContextual;
import org.scijava.object.ObjectService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Attr;

/**
 * Abstract superclass for {@link InputHarvester}s.
 * <p>
 * An input harvester obtains a module's unresolved input parameter values from
 * the user. Parameters are collected using an {@link InputPanel} dialog box.
 * </p>
 * 
 * @author Curtis Rueden
 * @param <P> The type of UI component housing the input panel itself.
 * @param <W> The type of UI component housing each input widget.
 */
public abstract class AbstractChainedInputHarvester<P, W> extends AbstractContextual
	implements InputHarvester<P, W>
{

	@Parameter
	private WidgetService widgetService;

	@Parameter
	private ObjectService objectService;

	// -- InputHarvester methods --

	@Override
	public void harvest(final Module module) throws ModuleException {
		final InputPanel<P, W> inputPanel = createInputPanel();
		buildPanel(inputPanel, module);
		if (!inputPanel.hasWidgets()) return; // no inputs left to harvest

		final boolean ok = harvestInputs(inputPanel, module);
		if (!ok) throw new ModuleCanceledException();

		processResults(inputPanel, module);
	}

	@Override
	public void
		buildPanel(final InputPanel<P, W> inputPanel, final Module module)
			throws ModuleException
	{
		final Iterable<ModuleItem<?>> inputs = module.getInfo().inputs();

		final ArrayList<WidgetModel> models = new ArrayList<WidgetModel>();

		for (final ModuleItem<?> item : inputs) {
			String test=item.getDescription();
			CommandModuleItem cItem=(CommandModuleItem)item;
			
			Parameter p=cItem.getField().getAnnotation(Parameter.class);
			String n=cItem.getName();
			Attr[] attrs=p.attrs();
			
			boolean display=true;
			if ( (attrs!=null) && (attrs.length>0) )
			{
				Attr attr=attrs[0];
				
				String name=attr.name();
				String value=attr.value();
				
				if (attr.name().equals("ShowInChainedGUI")&&(attr.value().equals("false")) )
				{
					display=false;
				}
			}
				
			if (display==true)
			{
				final WidgetModel model = addInput(inputPanel, module, item);
				if (model != null) models.add(model);
			}
		}

		// mark all models as initialized
		for (final WidgetModel model : models)
			model.setInitialized(true);

		// compute initial preview
		module.preview();
	}

	@Override
	public void processResults(final InputPanel<P, W> inputPanel,
		final Module module) throws ModuleException
	{
		final Iterable<ModuleItem<?>> inputs = module.getInfo().inputs();

		for (final ModuleItem<?> item : inputs) {
			final String name = item.getName();
			module.setResolved(name, true);
		}
	}

	// -- Helper methods --

	private <T> WidgetModel addInput(final InputPanel<P, W> inputPanel,
		final Module module, final ModuleItem<T> item) throws ModuleException
	{
		final String name = item.getName();
		final boolean resolved = module.isResolved(name);
		if (resolved) return null; // skip resolved inputs

		final Class<T> type = item.getType();
		final WidgetModel model =
			new WidgetModel(getContext(), inputPanel, module, item, getObjects(type));

		final InputWidget<?, ?> widget = widgetService.create(model);
		if (widget != null) {
			// FIXME: This cast is NOT safe! Multiple UIs in the
			// classpath will have clashing widget implementations.
			@SuppressWarnings("unchecked")
			final InputWidget<?, W> typedWidget = (InputWidget<?, W>) widget;
			inputPanel.addWidget(typedWidget);
			return model;
		}

		if (item.isRequired()) {
			throw new ModuleException("A " + type.getSimpleName() +
				" is required but none exist.");
		}

		// item is not required; we can skip it
		return null;
	}

	/** Asks the object service for valid choices */
	private <T> List<T> getObjects(final Class<T> type) {
		return objectService.getObjects(type);
	}

}

