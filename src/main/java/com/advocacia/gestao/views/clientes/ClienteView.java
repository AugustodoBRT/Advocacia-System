package com.advocacia.gestao.views.clientes;

import com.advocacia.gestao.views.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "clientes", layout = MainLayout.class)
public class ClienteView extends VerticalLayout {

    public ClienteView() {
        add(new H2("Clientes - Tela de teste"));
    }
}