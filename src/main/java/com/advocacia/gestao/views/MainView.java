package com.advocacia.gestao.views;

import com.advocacia.gestao.views.MainLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
public class MainView extends VerticalLayout {

    public MainView() {
        add(new H1("Bem-vindo ao Sistema de Gestão Jurídica"));
        add(new Paragraph("Escolha uma opção no menu lateral para começar."));
    }
}
