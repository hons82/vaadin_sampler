package com.example.application.components;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

@Tag("template-lit")
@JsModule("./components/template-lit.js")
public class TemplateLit extends LitTemplate implements HasComponents {

	@Id
	private TextField textField;

	public TemplateLit() {
		textField.setSizeFull();
		textField.setValueChangeMode(ValueChangeMode.ON_CHANGE);
	}

	public TemplateLit(String label) {
		this();
		setLabel(label);
	}

	public TemplateLit setLabel(String label) {
		textField.setLabel(label);
		return this;
	}

	public TemplateLit setValue(String value) {
		textField.setValue(value);
		return this;
	}

	public TemplateLit setReadOnly(boolean readOnly) {
		textField.setReadOnly(readOnly);
		return this;
	}
}
