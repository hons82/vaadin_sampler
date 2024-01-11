package com.example.application.views;

import com.example.application.MainLayout;
import com.example.application.model.Person;
import com.example.application.services.PersonService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;

import java.util.List;

@PageTitle("Grid")
@Route(value = "grid", layout = MainLayout.class)
public class GridView extends Composite<VerticalLayout> {

	private Grid<Person> grid;
	private Div focusedCell;
	private Registration focusRegistration;

	public GridView() {
	}

	private static List<Person> getItems() {
		PersonService personService = new PersonService(11, 111111);
		return personService.fetchAll();
	}

	@Override
	protected VerticalLayout initContent() {
		grid = new Grid<>();
		grid.setSizeFull();
		grid.setItems(getItems());
		grid.addColumn(Person::getId)
			.setHeader("Id");
		grid.addColumn(Person::getFirstName)
			.setHeader("First Name");
		grid.addColumn(Person::getLastName)
			.setHeader("Last Name");
		grid.addColumn(Person::getAge)
			.setHeader("Age");
		grid.addColumn(Person::getPhoneNumber)
			.setHeader("Phone number");

		focusedCell = new Div();
		focusedCell.setId("status");

		var layout = new VerticalLayout(focusedCell, grid);
		layout.setSizeFull();
		return layout;
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);
		focusRegistration = grid.addCellFocusListener(e -> e.getItem()
			.ifPresent(p -> focusedCell.setText("Last Focused Row: " + p))
		);
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
		super.onDetach(detachEvent);
		if (focusRegistration != null)
			focusRegistration.remove();
		focusRegistration = null;
	}
}
