package com.advocacia.gestao.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;

@CssImport("./styles.css")
public class MainLayout extends AppLayout {

    public MainLayout() {
        // Navbar
        DrawerToggle toggle = new DrawerToggle();

        H1 title = new H1("Advocacia System");
        title.addClassName("navbar-title");

        addToNavbar(toggle, title);

        // Menu lateral
        Span menuLabel = new Span("NAVEGAÇÃO");
        menuLabel.addClassName("menu-label");

        RouterLink dashboardLink = new RouterLink("Dashboard",
                com.advocacia.gestao.views.dashboards.DashboardView.class);
        RouterLink clientesLink = new RouterLink("Clientes",
                com.advocacia.gestao.views.clientes.ClienteView.class);
        RouterLink processosLink = new RouterLink("Processos",
                com.advocacia.gestao.views.processos.ProcessoView.class);

        dashboardLink.addClassName("menu-link");
        clientesLink.addClassName("menu-link");
        processosLink.addClassName("menu-link");

        VerticalLayout menu = new VerticalLayout(menuLabel, dashboardLink, clientesLink, processosLink);
        menu.addClassName("menu-container");
        menu.setSpacing(false);
        menu.setPadding(false);

        addToDrawer(menu);
    }
}