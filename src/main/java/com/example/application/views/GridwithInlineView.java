package com.example.application.views;

import com.example.application.MainLayout;
import com.example.application.components.Autosuggest;
import com.example.application.model.Person;
import com.example.application.model.Person.MaritalStatus;
import com.example.application.services.PersonService;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;

@PageTitle("Grid with Inline")
@Route(value = "grid_inline", layout = MainLayout.class)
public class GridwithInlineView extends Composite<VerticalLayout> {

	public GridwithInlineView() {
	}

	private static List<Person> getItems() {
		PersonService personService = new PersonService();
		return personService.fetchAll();
	}

	@Override
	protected VerticalLayout initContent() {
		Grid<Person> grid = new Grid<>();
		grid.setSizeFull();
		List<Person> persons = getItems();
		grid.setItems(persons);
		Grid.Column<Person> firstNameColumn = grid.addColumn(Person::getFirstName)
			.setHeader("First Name");
		Grid.Column<Person> ageColumn = grid
			.addColumn(Person::getAge).setHeader("Age");
		Grid.Column<Person> maritalStatusColumn = grid
			.addColumn(Person::getMaritalStatus).setHeader("Status");

		Binder<Person> binder = new Binder<>(Person.class);
		Editor<Person> editor = grid.getEditor();
		editor.setBinder(binder);
		editor.setBuffered(true);

		Div validationStatus = new Div();
		validationStatus.setId("validation");

		TextField firstNameField = new TextField();
		binder.forField(firstNameField)
			.withValidator(new StringLengthValidator("First name length must be between 3 and 50.", 3, 50))
			.withStatusLabel(validationStatus).bind("firstName");
		firstNameColumn.setEditorComponent(firstNameField);

		TextField ageField = new TextField();
		binder.forField(ageField)
			.withConverter(
				new StringToIntegerConverter("Age must be a number."))
			.withStatusLabel(validationStatus).bind("age");
		ageColumn.setEditorComponent(ageField);

		Autosuggest<MaritalStatus> maritalStatusField = new Autosuggest<>();
		maritalStatusField.setPlaceholder("Marital Status...");
		maritalStatusField.setOpenDropdownOnClick(true);
		maritalStatusField.setItems(Arrays.asList(MaritalStatus.values()));
		maritalStatusField.setKeyGenerator(i -> Integer.toString(i.ordinal()));
		maritalStatusField.setLabelGenerator(GridwithInlineView::getLabel);

		binder.forField(maritalStatusField.getTextField()).withConverter(
				isMarried -> isMarried.equalsIgnoreCase("married") ? MaritalStatus.MARRIED : MaritalStatus.SINGLE,
				GridwithInlineView::getLabel)
			.bind(Person::getMaritalStatus,
				Person::setMaritalStatus);

		maritalStatusColumn.setEditorComponent(maritalStatusField);

		Collection<Button> editButtons = Collections
			.newSetFromMap(new WeakHashMap<>());

		Grid.Column<Person> editorColumn = grid.addComponentColumn(person -> {
			Button edit = new Button("Edit");
			edit.addClassName("edit");
			edit.addClickListener(e -> {
				editor.editItem(person);
				firstNameField.focus();
			});
			edit.setEnabled(!editor.isOpen());
			editButtons.add(edit);
			return edit;
		});
		editor.addOpenListener(e -> editButtons
			.forEach(button -> button.setEnabled(!editor.isOpen())));
		editor.addCloseListener(e -> editButtons
			.forEach(button -> button.setEnabled(!editor.isOpen())));

		Button save = new Button("Save", e -> editor.save());
		save.addClassName("save");

		Button cancel = new Button("Cancel", e -> editor.cancel());
		cancel.addClassName("cancel");

// Add a keypress listener that listens for an escape key up event.
// Note! some browsers return key as Escape and some as Esc
		grid.getElement().addEventListener("keyup", event -> editor.cancel())
			.setFilter("event.key === 'Escape' || event.key === 'Esc'");

		Div buttons = new Div(save, cancel);
		editorColumn.setEditorComponent(buttons);

		editor.addSaveListener(
			event -> Notification.show(event.getItem().getFirstName() + ", "
				+ event.getItem().getAge()));

		var layout = new VerticalLayout(validationStatus, grid);
		layout.setSizeFull();
		return layout;
	}

	private static String getLabel(MaritalStatus maritalStatus) {
		switch (maritalStatus) {
		case SINGLE:
			return "Single";
		default:
			return "Married";
		}
	}
}
