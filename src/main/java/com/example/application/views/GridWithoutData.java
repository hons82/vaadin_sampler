package com.example.application.views;

import com.example.application.MainLayout;
import com.example.application.model.Person;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.UUID;

@PageTitle("Grid without Data")
@Route(value = "grid_empty", layout = MainLayout.class)
public class GridWithoutData extends Composite<VerticalLayout> {

	@Override
	protected VerticalLayout initContent() {
		var grid = new Grid<>(Person.class, false);
		grid.setMultiSort(true);
		grid.setColumnReorderingAllowed(true);
		grid.setSizeFull();
		grid.setItems();

		Grid.Column<Person> firstNameColumn = grid.addColumn(Person::getFirstName)
			.setKey(UUID.randomUUID().toString())
			.setHeader("First name")
			.setSortable(true);
		grid.addColumn(Person::getLastName)
			.setKey(UUID.randomUUID().toString())
			.setHeader("Last name")
			.setSortable(true);
		grid.addColumn(Person::getEmail)
			.setKey(UUID.randomUUID().toString())
			.setHeader("Email")
			.setSortable(true);
		grid.addColumn(Person::getPhoneNumber)
			.setKey(UUID.randomUUID().toString())
			.setHeader("Phone number")
			.setSortable(true);

		ArrayList<GridSortOrder<Person>> sortOrder = new ArrayList<>();
		sortOrder.add(new GridSortOrder<>(firstNameColumn, SortDirection.ASCENDING));
		grid.sort(sortOrder);

		var layout = new VerticalLayout(grid);
		layout.setSizeFull();
		return layout;
	}
}
