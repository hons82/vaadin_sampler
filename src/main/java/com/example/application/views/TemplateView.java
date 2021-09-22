package com.example.application.views;

import com.example.application.MainLayout;
import com.example.application.components.TemplateComponent;
import com.example.application.components.TemplateLit;
import com.example.application.components.TemplateLitSlotted;
import com.example.application.components.TemplatePolymer;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@PageTitle("Template")
@Route(value = "template", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class TemplateView extends Composite<VerticalLayout> {

	@Override
	protected VerticalLayout initContent() {

		var layout = new VerticalLayout(
			getTemplateComponent(),
			getTemplateLit(),
			getTemplatePolymer(),
			getTemplateLitSlotted()
		);
//		for (int i = 0; i < 500; i++) layout.add(getTemplateLit());

		layout.setSizeFull();
		return layout;
	}

	private static Component getTemplateComponent() {
		long startTime = System.currentTimeMillis();

		var template = new TemplateComponent("Template extends Component");

		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		template
			.setReadOnly(true)
			.setValue("ExecutionTime: " + elapsedTime + " ms");
		return template;
	}

	private static Component getTemplateLit() {
		long startTime = System.currentTimeMillis();

		var template = new TemplateLit("Template extends LitTemplate");

		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		template
			.setReadOnly(true)
			.setValue("ExecutionTime: " + elapsedTime + " ms");
		return template;
	}

	private static Component getTemplatePolymer() {
		long startTime = System.currentTimeMillis();

		var template = new TemplatePolymer("Template extends PolymerTemplate");

		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		template
			.setReadOnly(true)
			.setValue("ExecutionTime: " + elapsedTime + " ms");
		return template;
	}

	private static Component getTemplateLitSlotted() {
		long startTime = System.currentTimeMillis();

		var template = new TemplateLitSlotted("Template extends LitTemplate (Slotted)");

		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		template
			.setReadOnly(true)
			.setValue("ExecutionTime: " + elapsedTime + " ms");
		return template;
	}
}
