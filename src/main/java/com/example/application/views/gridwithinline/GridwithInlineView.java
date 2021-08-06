package com.example.application.views.gridwithinline;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.example.application.views.MainLayout;
import com.vaadin.flow.router.RouteAlias;

@PageTitle("Grid with Inline")
@Route(value = "grid_inline", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class GridwithInlineView extends Div {

    public GridwithInlineView() {
        addClassName("gridwith-inline-view");
        add(new Text("Content placeholder"));
    }

}
