package com.advocacia.gestao.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {

    public MainLayout() {
        H1 title = new H1("Advocacia System");
        addToNavbar(title);

        VerticalLayout menu = new VerticalLayout();
        RouterLink dashboardLink = new RouterLink("Dashboard", com.advocacia.gestao.views.dashboards.DashboardView.class);
        RouterLink clientesLink = new RouterLink("Clientes", com.advocacia.gestao.views.clientes.ClienteView.class);
        RouterLink processosLink = new RouterLink("Processos", com.advocacia.gestao.views.processos.ProcessoView.class);
        menu.add(dashboardLink, clientesLink, processosLink);

        addToDrawer(menu);
    }
}