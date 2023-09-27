package com.example.application.views;

import com.example.application.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("DialogScroll")
@Route(value = "dialogscroll", layout = MainLayout.class)
public class DialogScrollView extends Composite<VerticalLayout> {

	@Override
	protected VerticalLayout initContent() {
		var button1 = dialog();

		var layout = new VerticalLayout(button1);
		layout.setSizeFull();
		return layout;
	}
	private static Button dialog() {
		var toolbar = new HorizontalLayout(new H2("Title"));

		var scrollable = new VerticalLayout(DialogView.getComponents(300));
		scrollable.setMaxHeight("1000px");
		scrollable.getStyle().set("overflow", "auto");

		var dLayout = new VerticalLayout(toolbar, scrollable);
		dLayout.setSizeFull();
		dLayout.getStyle().set("overflow", "hidden");
		dLayout.setFlexGrow(1, scrollable);

		var dialog = new Dialog();
		dialog.getHeader().add(new Label("Header"));
		dialog.getFooter().add(new Label("Footer"));
		dialog.setMinWidth("600px");
		dialog.add(dLayout);
		dialog.setResizable(true);
		dialog.setDraggable(true);
		dialog.setCloseOnOutsideClick(true);

		var button = new Button("Open Dialog, with close on outside click");
		button.addClickListener(event -> dialog.open());
		return button;
	}
}
