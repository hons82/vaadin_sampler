package com.example.application.views.autosuggest;

import com.example.application.model.Person;
import com.example.application.service.PersonService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import java.util.List;

@PageTitle("Autosuggest")
@Route(value = "autosuggest", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class AutosuggestView extends Composite<VerticalLayout> {

	private final PersonService personService;

	public AutosuggestView() {
		personService = new PersonService();
	}

	private List<Person> getItems() {
		return personService.fetchAll();
	}

	@Override
	protected VerticalLayout initContent() {

		long startTime = System.currentTimeMillis();

		var autosuggest1 = new Autosuggest<Person>(5);

		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println("ExecutionTime: " + elapsedTime + " ms for CONSTRUCTOR");

		autosuggest1.setOpenDropdownOnClick(true);
		autosuggest1.setDefaultOption("", "Default!", "Default! + uselessSearchStr");
		autosuggest1.setItems(getItems());

		Div testElement = new Div(new Span("IT WORKED !!!!"));
		testElement.setId("IT-WORKED");
		autosuggest1.setComponentToDropdownEndSlot(testElement);

		var layout = new VerticalLayout(autosuggest1);
		layout.setSizeFull();
		return layout;
	}

}

