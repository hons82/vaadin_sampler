/*
 * DialogView  2021-10-04
 *
 * Copyright (c) Pro Data GmbH & ASA KG. All rights reserved.
 */

package com.example.application.views;

import com.example.application.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Dialog")
@Route(value = "dialog", layout = MainLayout.class)
@CssImport(value = "./components/dialog-layout.css", themeFor = "vaadin-dialog-overlay")
public class DialogView  extends Composite<VerticalLayout> {

	private Dialog hideDialog;

	public DialogView() {
	}

	private Component[] getComponents(int size) {
		var result = new Component[size];
		for (int i = 0; i < size; i++) {
			var c = new TextField("Component" + i, "Enter value for " + i);
			c.setWidthFull();
			result[i] = c;
		}
		return result;
	}

	@Override
	protected VerticalLayout initContent() {
		var button1 = simpleDialog();

		var button2 = hideDialog();

		var layout = new VerticalLayout(button1, button2);
		layout.setSizeFull();
		return layout;
	}

	private Button hideDialog() {

		System.out.println("Init hide dialog");

		var dLayout = new VerticalLayout(getComponents(1000));
		dLayout.setSizeFull();
		dLayout.getStyle()
			.set("padding", "8px");

		var div = new Div(dLayout);
		div.setSizeFull();
		div.getStyle()
			.set("box-sizing", "border-box")
			.set("overflow", "auto")
			.set("flex-grow", "1");

		hideDialog = new Dialog();
		hideDialog.setMinWidth("600px");
		hideDialog.setCloseOnOutsideClick(false);

		var close = new Button("Close");
		close.addClickListener(event -> {
			hideDialog.setModal(false);
			hideDialog.getElement().executeJs("return window.getComputedStyle(this.$.overlay).display;").then(handler -> {
				System.out.println(handler.toJson());
			});
		});

		var layout = new VerticalLayout(
			new Label("I'm a title :-)"),
			div,
			new HorizontalLayout(close)
		);
		layout.setSizeFull();


		hideDialog.add(layout);

		var button = new Button("Open Dialog, hide on close");
		button.addClickListener(event -> {
			System.out.println("Opened: " + hideDialog.isOpened());
			if (hideDialog.isOpened()) {
				hideDialog.setModal(true);
				hideDialog.getElement().executeJs("this.$.overlay.style.display = \"\";");
			} else {
				hideDialog.open();
			}
		});
		return button;
	}

	private Button simpleDialog() {

		System.out.println("Init simple dialog");

		var dLayout = new VerticalLayout(getComponents(3000));
		dLayout.setSizeFull();

		var dialog = new Dialog();
		dialog.setMinWidth("600px");
		dialog.add(dLayout);
		dialog.setCloseOnOutsideClick(true);

		var button = new Button("Open Dialog, with close on outside click");
		button.addClickListener(event -> dialog.open());
		return button;
	}
}
