package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.coverage;

import com.uib.web.peptideshaker.presenter.core.graph.Node;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class represents legend layout for graph and protein coverage
 *
 * @author Yehia Farag
 */
public abstract class Legend extends VerticalLayout {

    private final Map<String, Component> layoutMap;

    public Legend() {
        Legend.this.setSpacing(true);
        Legend.this.setStyleName("viewframecontent");
        Legend.this.setWidth(500, Unit.PIXELS);
        this.layoutMap = new LinkedHashMap<>();

        Button closeBtn = new Button("Close");
        closeBtn.setIcon(VaadinIcons.CLOSE_SMALL, "Close window");
        closeBtn.setStyleName(ValoTheme.BUTTON_SMALL);
        closeBtn.addStyleName(ValoTheme.BUTTON_TINY);
        closeBtn.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        closeBtn.addStyleName("centerbackground");
        closeBtn.setHeight(25, Unit.PIXELS);
        closeBtn.setWidth(25, Unit.PIXELS);

        closeBtn.addClickListener((Button.ClickEvent event) -> {
            close();
        });
        Legend.this.addComponent(closeBtn);
        Legend.this.setExpandRatio(closeBtn, 0.01f);

//        Legend.this.addComponent(title);
        HorizontalLayout container = new HorizontalLayout();
        container.setCaption("<b>Proteins & Peptides</b>");
        container.setCaptionAsHtml(true);
        container.setSpacing(true);
        Legend.this.addComponent(container);
        Legend.this.setExpandRatio(container, 0.5f);
        container.setHeight(90, Unit.PIXELS);
        container.setWidth(100, Unit.PERCENTAGE);

        VerticalLayout mTContainer = new VerticalLayout();
        mTContainer.setSizeFull();
        container.addComponent(mTContainer);
        container.setExpandRatio(mTContainer, 30);

        Node protreinMT = generateNode("proteinnode");
        mTContainer.addComponent(generateLabel(protreinMT, "Protein"));
        Node peptideMT = generateNode("peptidenode");
        mTContainer.addComponent(generateLabel(peptideMT, "Peptide"));

        VerticalLayout enzymaticContainer = new VerticalLayout();
        enzymaticContainer.setSizeFull();
        container.addComponent(enzymaticContainer);
        container.setExpandRatio(enzymaticContainer, 70);
        Node proteinNodeEnzimatic = generateNode("proteinnode");
        Node peptideNodeEnzimatic = generateNode("peptidenode");
        HorizontalLayout enzymaticEdge = generateEdgeLabel(proteinNodeEnzimatic, peptideNodeEnzimatic, "Enzymatic");
        enzymaticContainer.addComponent(enzymaticEdge);

        Node proteinNodeNonEnzimatic = generateNode("proteinnode");
        Node peptideNodeNonEnzimatic = generateNode("peptidenode");
        enzymaticContainer.addComponent(generateEdgeLabel(proteinNodeNonEnzimatic, peptideNodeNonEnzimatic, "Non Enzymatic"));

        HorizontalLayout lowerContainer = new HorizontalLayout();
        lowerContainer.setWidth(100, Unit.PERCENTAGE);
        Legend.this.addComponent(lowerContainer);
        Legend.this.setExpandRatio(lowerContainer, 0.5f);
        VerticalLayout proteinEvidenceContainer = new VerticalLayout();
        proteinEvidenceContainer.setHeight(240, Unit.PIXELS);
        proteinEvidenceContainer.setCaption("<b>Protein Evidence</b>");
        proteinEvidenceContainer.setCaptionAsHtml(true);
        lowerContainer.addComponent(proteinEvidenceContainer);
        Node protein = generateNode("proteinnode");
        protein.addStyleName("greenbackground");
        proteinEvidenceContainer.addComponent(generateLabel(protein, "Protein"));
        Node transcript = generateNode("proteinnode");
        transcript.addStyleName("orangebackground");
        proteinEvidenceContainer.addComponent(generateLabel(transcript, "Transcript"));
        Node homology = generateNode("proteinnode");
        homology.addStyleName("seabluebackground");
        proteinEvidenceContainer.addComponent(generateLabel(homology, "Homology"));
        Node predect = generateNode("proteinnode");
        predect.addStyleName("purplebackground");
        proteinEvidenceContainer.addComponent(generateLabel(predect, "Predicted"));
        Node uncertain = generateNode("proteinnode");
        uncertain.addStyleName("redbackground");
        proteinEvidenceContainer.addComponent(generateLabel(uncertain, "Uncertain"));
        Node notAvailable = generateNode("proteinnode");
        notAvailable.addStyleName("graybackground");
        proteinEvidenceContainer.addComponent(generateLabel(notAvailable, "Not Available"));

        VerticalLayout proteinValidationContainer = new VerticalLayout();
        proteinValidationContainer.setHeight(170, Unit.PIXELS);
        proteinValidationContainer.setCaption("<b>Validation Status</b>");
        proteinValidationContainer.setCaptionAsHtml(true);
        lowerContainer.addComponent(proteinValidationContainer);
        Node confedent = generateNode("proteinnode");
        confedent.addStyleName("greenbackground");
        proteinValidationContainer.addComponent(generateLabel(confedent, "Confident"));
        Node doubtful = generateNode("proteinnode");
        doubtful.addStyleName("orangebackground");
        proteinValidationContainer.addComponent(generateLabel(doubtful, "Doubtful"));

        Node notValidated = generateNode("proteinnode");
        notValidated.addStyleName("redbackground");
        proteinValidationContainer.addComponent(generateLabel(notValidated, "Not Validated"));
        Node notAvailable2 = generateNode("proteinnode");
        notAvailable2.addStyleName("graybackground");
        proteinValidationContainer.addComponent(generateLabel(notAvailable2, "Not Available"));

    }

    private Node generateNode(String defaultStyleName) {
        Node node = new Node("prot") {
            @Override
            public void selected(String id) {

            }
        };
        node.setDefaultStyleName(defaultStyleName);
        node.setSelected(true);
        node.addStyleName("defaultcursor");
        return node;
    }

    public void updateLegend(String mode) {

    }

    private HorizontalLayout generateLabel(Component small, String title) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidth(100, Unit.PERCENTAGE);
        row.setHeight(100, Unit.PERCENTAGE);
        row.setSpacing(true);
        row.addComponent(small);
        row.setExpandRatio(small, 25);
        row.setComponentAlignment(small, Alignment.TOP_LEFT);

        Label l = new Label(title);
        l.setStyleName(ValoTheme.LABEL_TINY);
        row.addComponent(l);
        row.setExpandRatio(l, 75);
        row.setComponentAlignment(l, Alignment.TOP_LEFT);
        return row;

    }

    private HorizontalLayout generateEdgeLabel(Component small, Component small2, String text) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidth(100, Unit.PERCENTAGE);
        row.setHeight(30, Unit.PIXELS);
        row.setSpacing(true);
        row.addComponent(small);
        row.setExpandRatio(small, 20);
        row.setComponentAlignment(small, Alignment.TOP_LEFT);

        VerticalLayout edge = new VerticalLayout();
        edge.setHeight(2, Unit.PIXELS);
        if (text.contains("Non")) {
            edge.addStyleName("dottedborder");
        } else {
            edge.addStyleName("solidborder");
        }
        row.addComponent(edge);
        row.setExpandRatio(edge, 20);
        row.setComponentAlignment(edge, Alignment.MIDDLE_LEFT);

        row.addComponent(small2);
        row.setExpandRatio(small2, 10);
        row.setComponentAlignment(small2, Alignment.TOP_LEFT);

        Label l = new Label(text);
        l.setStyleName(ValoTheme.LABEL_TINY);
        row.addComponent(l);
        row.setExpandRatio(l, 50);
        row.setComponentAlignment(l, Alignment.TOP_LEFT);
        return row;

    }
    public abstract void close();

}
