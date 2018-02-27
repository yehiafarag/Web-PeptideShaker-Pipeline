package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.coverage;

import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideObject;
import com.vaadin.ui.VerticalLayout;

/**
 *
 * @author yfa041
 */
public class PeptideLayout extends VerticalLayout implements Comparable<PeptideLayout> {

    private final int startIndex;
    private final float x;
    private final PeptideObject peptide;
    private final String validationStatuesStyle;
    private final String proteinEvidenceStyle;
    private boolean selected;

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
        PeptideLayout.this.setDescription(peptide.getModifiedSequence());

        this.validationStatuesStyle = validationStatuesStyle;
        this.proteinEvidenceStyle = proteinEvidenceStyle;

    }

    public Object getPeptideId() {
        return peptide.getModifiedSequence();
    }

    public int getStartIndex() {
        return startIndex;
    }
     public int getEndIndex() {
        return startIndex+peptide.getSequence().length();
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
