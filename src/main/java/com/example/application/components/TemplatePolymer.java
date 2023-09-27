//package com.example.application.components;
//
//import com.vaadin.flow.component.HasComponents;
//import com.vaadin.flow.component.Tag;
//import com.vaadin.flow.component.dependency.JsModule;
//import com.vaadin.flow.component.polymertemplate.Id;
//import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
//import com.vaadin.flow.component.textfield.TextField;
//import com.vaadin.flow.data.value.ValueChangeMode;
//import com.vaadin.flow.templatemodel.TemplateModel;
//
//@Tag("template-polymer")
//@JsModule("./components/template-polymer.js")
//public class TemplatePolymer extends PolymerTemplate<TemplatePolymer.TemplatePolymerTemplateModel> implements HasComponents {
//
//	public interface TemplatePolymerTemplateModel extends TemplateModel {
//
//	}
//
//	@Id
//	private TextField textField;
//
//	public TemplatePolymer() {
//		textField.setSizeFull();
//		textField.setValueChangeMode(ValueChangeMode.ON_CHANGE);
//	}
//
//	public TemplatePolymer(String label) {
//		this();
//		setLabel(label);
//	}
//
//	public TemplatePolymer setLabel(String label) {
//		textField.setLabel(label);
//		return this;
//	}
//
//	public TemplatePolymer setValue(String value) {
//		textField.setValue(value);
//		return this;
//	}
//
//	public TemplatePolymer setReadOnly(boolean readOnly) {
//		textField.setReadOnly(readOnly);
//		return this;
//	}
//}
