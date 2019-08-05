package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.coverage;

import com.compomics.util.experiment.biology.PTMFactory;
import com.vaadin.event.LayoutEvents;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents coloured modification on the Protein-Protoforms layout
 *
 * @author Yehia Farag
 */
public abstract class ProtoformModificationLayout extends VerticalLayout implements LayoutEvents.LayoutClickListener {

    private final Set<PeptideLayout> correspondingPeptidesSet;

    public ProtoformModificationLayout(String modificationName, int location) {
        ProtoformModificationLayout.this.setWidth(5, Unit.PIXELS);
        ProtoformModificationLayout.this.setHeight(15, Unit.PIXELS);
        ProtoformModificationLayout.this.setStyleName("protoformmodstyle");
        ProtoformModificationLayout.this.setDescription(modificationName + " (" + location + ")");
        Color c = PTMFactory.getDefaultColor(modificationName);
        Label modification = new Label("<div  style='background:rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ");;width: 100%;height: 100%;'></div>", ContentMode.HTML);
        modification.setSizeFull();
        ProtoformModificationLayout.this.addComponent(modification);
        this.correspondingPeptidesSet = new HashSet<>();
        ProtoformModificationLayout.this.addLayoutClickListener(ProtoformModificationLayout.this);

    }

    public void addCorrespondingPeptide(PeptideLayout peptide) {
        correspondingPeptidesSet.add(peptide);

    }

    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        selected(this);

    }

    public abstract void selected(ProtoformModificationLayout protoformModificationLayout);

    public PeptideLayout select() {
        for (PeptideLayout peptide : correspondingPeptidesSet) {
            peptide.addStyleName("heighlightcorrespondingpeptide");
        }
        this.addStyleName("heighlightcorrespondingpeptide");
        if (correspondingPeptidesSet.size() == 1) {
            return correspondingPeptidesSet.iterator().next();
        }
        return null;
    }

    }
