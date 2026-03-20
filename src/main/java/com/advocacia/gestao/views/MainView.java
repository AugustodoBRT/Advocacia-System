package com.advocacia.gestao.views;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@CssImport("./styles.css")
@Route(value = "", layout = MainLayout.class)
public class MainView extends VerticalLayout {

    public MainView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        addClassName("main-view");

        Span icone = new Span("⚖️");
        icone.addClassName("main-icone");

        H1 titulo = new H1("Sistema de Gestão Jurídica");
        titulo.addClassName("main-titulo");
        titulo.getStyle()
                .set("color", "var(--lumo-primary-color)")
                .set("font-size", "var(--lumo-font-size-xxl)")
                .set("margin", "0");

        Paragraph subtitulo = new Paragraph("Escolha uma opção no menu lateral para começar.");
        subtitulo.addClassName("main-subtitulo");
        subtitulo.getStyle()
                .set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin-top", "var(--lumo-space-s)");

        VerticalLayout card = new VerticalLayout(icone, titulo, subtitulo);
        card.setAlignItems(Alignment.CENTER);
        card.addClassName("main-card");
        card.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-xl)")
                .set("box-shadow", "var(--lumo-box-shadow-m)");

        add(card);
    }
}