package com.example.application.views;

import com.example.application.MainLayout;
import com.example.application.model.Person;
import com.example.application.services.PersonService;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@PageTitle("Grid with Variable Columns")
@Route(value = "grid_columns", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class GridWithVariableColumns extends Composite<VerticalLayout> {

	private final ExecutorService executor;

	public GridWithVariableColumns() {
		executor = Executors.newSingleThreadExecutor();
	}

	private static List<Person> getItems() {
		PersonService personService = new PersonService();
		return personService.fetchAll();
	}

	@Override
	protected VerticalLayout initContent() {
		Grid<Person> grid = new Grid<>(Person.class, false);
		grid.setSizeFull();
		grid.setMultiSort(true);
		grid.setItems(getItems());
		addColumns(grid);

		Button resetColumns = new Button("Reset columns", e -> {
			var oldColumns = grid.getColumns();
			var newColumns = addColumns(grid);

			grid.setColumnOrder(Stream.concat(newColumns.stream(), oldColumns.stream()).collect(Collectors.toList()));
		});

		Button removeColumn = new Button("Remove first column", e -> {
			List<Grid.Column<Person>> columns = grid.getColumns();
			if (!columns.isEmpty()) {
				grid.removeColumn(columns.get(0));
				var collsList = new ArrayList<>(grid.getColumns());
				Collections.shuffle(collsList);
				grid.setColumnOrder(collsList);

			}
			columns = grid.getColumns();
			if (!columns.isEmpty()) {
				grid.sort(
					columns.stream().map(c -> new GridSortOrder<>(c, SortDirection.ASCENDING)).collect(Collectors.toList())
				);
			}
		});

		var layout = new VerticalLayout(resetColumns, removeColumn, grid);
		layout.setSizeFull();
		return layout;
	}

	private static List<Grid.Column<Person>> addColumns(Grid<Person> grid) {
		return List.of(
		grid.addColumn(Person::getFirstName)
			.setKey(UUID.randomUUID().toString())
			.setHeader("First name")
			.setSortable(true),
		grid.addColumn(Person::getLastName)
			.setKey(UUID.randomUUID().toString())
			.setHeader("Last name")
			.setSortable(true),
		grid.addColumn(Person::getEmail)
			.setKey(UUID.randomUUID().toString())
			.setHeader("Email")
			.setSortable(true),
		grid.addColumn(Person::getPhoneNumber)
			.setKey(UUID.randomUUID().toString())
			.setHeader("Phone number")
			.setSortable(true)
		);
	}
}
