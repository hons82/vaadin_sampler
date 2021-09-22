package com.example.application.views;

import com.example.application.MainLayout;
import com.example.application.components.Autosuggest;
import com.example.application.model.Person;
import com.example.application.services.PersonService;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

@PageTitle("Autosuggest")
@Route(value = "autosuggest", layout = MainLayout.class)
@NpmPackage(value = "@polymer/iron-icons", version = "3.0.1")
@JsModule("@polymer/iron-icons/iron-icons.js")
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
		autosuggest1.setDropdownEndActions(List.of(
			new Autosuggest.Action("Action 1", "search", "", key -> System.out.println("Action " + key + " clicked!!!")),
			new Autosuggest.Action("Action 2", null, "", key -> System.out.println("Action " + key + "  executed!!!")))
		);

		var layout = new VerticalLayout(autosuggest1);
		layout.setSizeFull();
		return layout;
	}
}

