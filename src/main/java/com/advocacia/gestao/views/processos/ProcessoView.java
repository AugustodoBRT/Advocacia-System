package com.advocacia.gestao.views.processos;

import com.advocacia.gestao.views.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "processos", layout = MainLayout.class)
public class ProcessoView extends VerticalLayout {

    public ProcessoView() {
        add(new H2("Processos - Tela de teste"));
    }
}