package com.example.application.components;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

@Tag("template-lit-slotted")
@JsModule("./components/template-lit-slotted.js")
public class TemplateLitSlotted extends LitTemplate implements HasComponents {

	private final TextField textField;

	public TemplateLitSlotted() {
		textField = new TextField();
		textField.setId("textField");
		textField.setSizeFull();
		textField.setValueChangeMode(ValueChangeMode.ON_CHANGE);
		textField.getElement().setAttribute("slot", "textField");
		add(textField);
	}

	public TemplateLitSlotted(String label) {
		this();
		setLabel(label);
	}

	public TemplateLitSlotted setLabel(String label) {
		textField.setLabel(label);
		return this;
	}

	public TemplateLitSlotted setValue(String value) {
		textField.setValue(value);
		return this;
	}

	public TemplateLitSlotted setReadOnly(boolean readOnly) {
		textField.setReadOnly(readOnly);
		return this;
	}
}
