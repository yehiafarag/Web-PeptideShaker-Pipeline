package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.coverage;

import com.compomics.util.experiment.biology.PTMFactory;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideObject;
import com.uib.web.peptideshaker.presenter.core.graph.Node;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import java.awt.Color;
import java.util.HashMap;
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
    private VerticalLayout modificationLayout;
    private VerticalLayout psmNumberLayout;

    private final String modificationStyleName = "nodemodificationbackground";
    /**
     * The post translational modifications factory.
     */
    private final PTMFactory PTM = PTMFactory.getInstance();

    public PeptideLayout(PeptideObject peptide, float width, int startIndex, float x, String validationStatuesStyle, String proteinEvidenceStyle, boolean enzymatic, String PSMNumberColor) {

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

        modificationLayout = new VerticalLayout();
        modificationLayout.setSizeFull();
        PeptideLayout.this.addComponent(modificationLayout);
        modificationLayout.setData(peptide.getModifiedSequence());
        modificationLayout.setStyleName("basicpeptidemodification");
        String subTooltip = "";
        Map<String, String> modificationsTooltip = new HashMap<>();

        for (String mod : peptide.getVariableModifications().split("\\),")) {
            if (mod.trim().equalsIgnoreCase("") || mod.contains("Pyrolidone") || mod.contains("Acetylation of protein N-term")) {
                continue;
            }
            String[] tmod = mod.split("\\(");
            Color c = PTMFactory.getDefaultColor(tmod[0].trim());
            Label modification = new Label("<div  style='background:rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ");;width: 100%;height: 100%;'></div>", ContentMode.HTML);
            modification.setSizeFull();
            modification.setData(peptide.getModifiedSequence());
            modificationLayout.addComponent(modification);
            String[] indexArr = tmod[1].replace(")", "").replace(" ", "").split(",");
            for (String indexStr : indexArr) {
                int i = Integer.valueOf(indexStr) - 1;
                modificationsTooltip.put(peptide.getSequence().charAt(i) + "<" + PTM.getPTM(tmod[0].trim()).getShortName() + ">", "<font style='background-color:rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")'>" + peptide.getSequence().charAt(i) + "</font>");
                if (!subTooltip.contains("</br><span style='width:20px;height:10px;background-color:rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")'>" + peptide.getSequence().charAt(i) + "</span> - " + mod)) {
                    subTooltip += "</br><span style='width:20px;height:10px;background-color:rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")'>" + peptide.getSequence().charAt(i) + "</span> - " + mod;
                }
            }
//            int i = Integer.valueOf(tmod[1].trim().replace(")", "")) - 1;
//            modificationsTooltip.put(peptide.getSequence().charAt(i) + "<" + PTM.getPTM(tmod[0].trim()).getShortName() + ">", "<font style='background-color:rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")'>" + peptide.getSequence().charAt(i) + "</font>");
//            subTooltip += "</br><span style='width:20px;height:10px;background-color:rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")'>" + peptide.getSequence().charAt(i) + "</span> - " + mod;
        }

        String tooltip = peptide.getModifiedSequence();
        for (String key : modificationsTooltip.keySet()) {
            tooltip = tooltip.replace(key, modificationsTooltip.get(key));
        }
        tooltip += subTooltip;

        PeptideLayout.this.setDescription(tooltip);//peptide.getModifiedSequence().replace("<", "&lt;").replace(">", "&gt;")
        if (modificationLayout.getComponentCount() > 1) {
            modificationLayout.removeAllComponents();
            Label modification = new Label("<div style='background:orange; width:100%;height:100%;'></div>", ContentMode.HTML);
            modification.setSizeFull();
            modification.setData(peptide.getModifiedSequence());
            modificationLayout.addComponent(modification);
            modification.setData(peptide.getModifiedSequence());
        }
        this.modificationLayout.setVisible(false);

        psmNumberLayout = new VerticalLayout();
        psmNumberLayout.setSizeFull();
        PeptideLayout.this.addComponent(psmNumberLayout);
        psmNumberLayout.setData(peptide.getModifiedSequence());
//        psmNumberLayout.setStyleName("basicpeptidemodification");

        tooltip += "</br>#PSM: " + peptide.getPSMsNumber() + "";

        Label psmsColorLabel = new Label("<div style='background:" + PSMNumberColor + "; width:100%;height:100%;'></div>", ContentMode.HTML);
        psmsColorLabel.setSizeFull();
        psmsColorLabel.setData(peptide.getModifiedSequence());
        psmNumberLayout.addComponent(psmsColorLabel);
        psmNumberLayout.setVisible(false);
        peptide.setTooltip(tooltip);
        PeptideLayout.this.setDescription(tooltip);

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
        resetStyle();
        if (statues.equalsIgnoreCase("Validation Status")) {
            this.addStyleName(validationStatuesStyle);
        } else if (statues.equalsIgnoreCase("Protein Evidence")) {
            this.addStyleName(proteinEvidenceStyle);
        } else if (statues.equalsIgnoreCase("Modification Status")) {
            this.modificationLayout.setVisible(true);
            this.addStyleName(modificationStyleName);
        } else if (statues.equalsIgnoreCase("PSMNumber")) {
            this.psmNumberLayout.setVisible(true);
            this.addStyleName(modificationStyleName);
        }

    }

    private void resetStyle() {
        if (this.getStyleName().contains(proteinEvidenceStyle)) {
            this.removeStyleName(proteinEvidenceStyle);
        }
        if (this.getStyleName().contains(validationStatuesStyle)) {
            this.removeStyleName(validationStatuesStyle);
        }
        if (this.getStyleName().contains(modificationStyleName)) {
            this.removeStyleName(modificationStyleName);
        }
        this.modificationLayout.setVisible(false);
        this.psmNumberLayout.setVisible(false);

    }

}
