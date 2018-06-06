package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.coverage;

import com.compomics.util.experiment.biology.PTMFactory;
import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideObject;
import com.uib.web.peptideshaker.model.core.ModificationMatrix;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author yfa041
 */
public class PeptideLayout extends AbsoluteLayout implements Comparable<PeptideLayout> {

    private final int startIndex;
    private final float x;
    private final PeptideObject peptide;
    private final String validationStatuesStyle;
    private final String proteinEvidenceStyle;
    private boolean selected;
    /**
     * The post translational modifications factory.
     */
    private final PTMFactory PTM = PTMFactory.getInstance();

    public PeptideLayout(PeptideObject peptide, float width, int startIndex, float x, String validationStatuesStyle, String proteinEvidenceStyle, boolean enzymatic) {

        PeptideLayout.this.setHeight(15, Unit.PIXELS);
        PeptideLayout.this.setWidth(width, Unit.PERCENTAGE);
        PeptideLayout.this.addStyleName("lightbluelayout");
        PeptideLayout.this.addStyleName("peptidelayout");
        PeptideLayout.this.addStyleName("transparent");
        if (enzymatic) {
            PeptideLayout.this.addStyleName("blackborder");
        } else {
            PeptideLayout.this.addStyleName("dottedborder");
        }

        this.startIndex = startIndex;
        this.x = x;
        this.peptide = peptide;

        this.validationStatuesStyle = validationStatuesStyle;
        this.proteinEvidenceStyle = proteinEvidenceStyle;
//        System.out.println("inside peptide modifications " + peptide.getModifiedSequence() + "  fixed " + peptide.getFixedModifications() + "  " + peptide.getVariableModifications());
//        for (String mod : peptide.getFixedModifications().split(",")) {
//            VerticalLayout modification = new VerticalLayout();
//            modification.setStyleName("basicpeptidemodification");
//            PeptideLayout.this.addComponent(modification);
//            modification.setDescription(mod);
//            System.out.println("at modification found " + mod + peptide.getProteinGroups());
//
//        }

        String modifiedSequence = peptide.getModifiedSequence();

        for (String mod : peptide.getVariableModifications().split(",")) {
            if (mod.trim().equalsIgnoreCase("") || mod.contains("Pyrolidone")) {
                continue;
            }
            String tmod = mod.replace("(", "__").split("__")[0].trim();
            Label modification = new Label("<center style='color:rgb("+PTMFactory.getDefaultColor(tmod).getRed()+","+PTMFactory.getDefaultColor(tmod).getGreen()+","+PTMFactory.getDefaultColor(tmod).getBlue()+")'>"+VaadinIcons.CIRCLE.getHtml()+"</center>", ContentMode.HTML);           
            modification.setStyleName("basicpeptidemodification");
            modification.setSizeFull();
            PeptideLayout.this.addComponent(modification);
            modification.setDescription(mod);

           

        }

        PeptideLayout.this.setDescription(peptide.getModifiedSequence().replace("<", "&lt;").replace(">", "&gt;"));

    }

    public Object getPeptideId() {
        return peptide.getModifiedSequence();
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return startIndex + peptide.getSequence().length();
    }

    public float getX() {
        return x;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
//            PeptideLayout.this.removeStyleName("lightgraylayout");
            PeptideLayout.this.addStyleName("selectedpeptide");
        } else {
            PeptideLayout.this.removeStyleName("selectedpeptide");
//            PeptideLayout.this.addStyleName("lightgraylayout");
        }
    }

    @Override
    public int compareTo(PeptideLayout o) {
        if (this.getWidth() > o.getWidth()) {
            return 1;
        } else {
            return -1;
        }
    }

    public void updateStylingMode(String statues) {
//        this.resetStyle();
        resetStyle();
        if (statues.equalsIgnoreCase("Validation Status")) {
            this.addStyleName(validationStatuesStyle);
//            this.removeStyleName(proteinEvidenceStyle);
        } else if (statues.equalsIgnoreCase("Protein Evidence")) {
//            this.removeStyleName(validationStatuesStyle);
            this.addStyleName(proteinEvidenceStyle);
        }

    }

    private void resetStyle() {
        if (this.getStyleName().contains(proteinEvidenceStyle)) {
            this.removeStyleName(proteinEvidenceStyle);
        }
        if (this.getStyleName().contains(validationStatuesStyle)) {
            this.removeStyleName(validationStatuesStyle);
        }

    }

}
