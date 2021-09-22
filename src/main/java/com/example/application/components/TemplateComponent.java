package com.example.application.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

@Tag("template-component")
@JsModule("./components/template-component.js")
public class TemplateComponent extends Component implements HasComponents {

	private final TextField textField;

	public TemplateComponent() {
		textField = new TextField();
		textField.setId("textField");
		textField.setSizeFull();
		textField.setValueChangeMode(ValueChangeMode.ON_CHANGE);
		textField.getElement().setAttribute("slot", "textField");
		add(textField);
	}

	public TemplateComponent(String label) {
		this();
		setLabel(label);
	}

	public TemplateComponent setLabel(String label) {
		textField.setLabel(label);
		return this;
	}

	public TemplateComponent setValue(String value) {
		textField.setValue(value);
		return this;
	}

	public TemplateComponent setReadOnly(boolean readOnly) {
		textField.setReadOnly(readOnly);
		return this;
	}
}
