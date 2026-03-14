package com.advocacia.gestao.views.dashboards;

import com.advocacia.gestao.views.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "dashboard", layout = MainLayout.class)
public class DashboardView extends VerticalLayout {

    public DashboardView() {
        add(new H2("Dashboard - Tela de teste"));
    }
}