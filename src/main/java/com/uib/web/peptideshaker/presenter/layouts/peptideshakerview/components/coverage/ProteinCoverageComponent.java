package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.coverage;

import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideObject;
import com.uib.web.peptideshaker.galaxy.dataobjects.ProteinObject;
import com.vaadin.event.LayoutEvents;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class represents protein coverage layout that contain the distribution
 * of peptides on the protein
 *
 * @author Yehia Farag
 */
public abstract class ProteinCoverageComponent extends AbsoluteLayout {

    private final ProteinCoverageLayout proteinCoverageLayout;
    private final AbsoluteLayout peptideDistributionLayout;
    private final AbsoluteLayout chainCoverage3dLayout;
    private final Set<PeptideLayout> peptideDistMap;
    private final Set<PeptideObject> peptideObjectsSet;

    public ProteinCoverageComponent(ProteinObject protein, Map<String, PeptideObject> peptidesNodes) {
        ProteinCoverageComponent.this.setWidth(100, Unit.PERCENTAGE);
        HashMap<String, String> styles = new HashMap<>();
        styles.put("Confident", "greenbackground");
        styles.put("Doubtful", "orangebackground");
        styles.put("Not Validated", "redbackground");
        styles.put("Not Available", "graybackground");
        styles.put("Protein", "greenbackground");
        styles.put("Transcript", "orangebackground");
        styles.put("Homology", "seabluebackground");
        styles.put("Predicted", "purplebackground");
        styles.put("Uncertain", "redbackground");
        styles.put("Not Applicable", "lightgraybackground");

        if (protein.getValidation() == null) {
            protein.setValidation("Not Available");
        }
        this.peptideObjectsSet = new LinkedHashSet<>();
        chainCoverage3dLayout = new AbsoluteLayout() {
            private boolean expanded = false;

            @Override
            public void setVisible(boolean v) 
            { 
                if (v && !expanded) {
                    ProteinCoverageComponent.this.removeAllComponents();
                    ProteinCoverageComponent.this.addComponent(chainCoverage3dLayout, "left:0; top:0px;");
                    ProteinCoverageComponent.this.addComponent(proteinCoverageLayout, "left:0; top:30px;");
                    ProteinCoverageComponent.this.addComponent(peptideDistributionLayout, "left:0; top:45px;");
                    ProteinCoverageComponent.this.setHeight(ProteinCoverageComponent.this.getHeight() + 30, Unit.PIXELS);
                    expanded = true;
                } else if (!v && expanded) {
                    ProteinCoverageComponent.this.removeAllComponents();
                    ProteinCoverageComponent.this.addComponent(chainCoverage3dLayout, "left:0; top:0px;");
                    ProteinCoverageComponent.this.addComponent(proteinCoverageLayout, "left:0; top:10px;");
                    ProteinCoverageComponent.this.addComponent(peptideDistributionLayout, "left:0; top:25px;");
                    ProteinCoverageComponent.this.setHeight(ProteinCoverageComponent.this.getHeight() - 30, Unit.PIXELS);
                    expanded = false;
                } else if (!v) {
                    ProteinCoverageComponent.this.removeAllComponents();
                    ProteinCoverageComponent.this.addComponent(chainCoverage3dLayout, "left:0; top:0px;");
                    ProteinCoverageComponent.this.addComponent(proteinCoverageLayout, "left:0; top:10px;");
                    ProteinCoverageComponent.this.addComponent(peptideDistributionLayout, "left:0; top:25px;");
                }
                super.setVisible(v);

            }
        ;

        };
        

        proteinCoverageLayout = new ProteinCoverageLayout(styles.get(protein.getValidation()), styles.get(protein.getProteinEvidence()));
//        ProteinCoverageComponent.this.addComponent(proteinCoverageLayout, "left:0; top:10px;");

        peptideDistributionLayout = new AbsoluteLayout();
        peptideDistributionLayout.setWidth(100, Unit.PERCENTAGE);
        peptideDistributionLayout.setHeight(100, Unit.PERCENTAGE);
        peptideDistributionLayout.addStyleName("peptidecoverage");
//        ProteinCoverageComponent.this.addComponent(peptideDistributionLayout, "left:0; top:25px;");

        LayoutEvents.LayoutClickListener peptidesListener = (LayoutEvents.LayoutClickEvent event) -> {
            PeptideLayout genPeptid = (PeptideLayout) event.getComponent();
            selectPeptide(protein.getAccession(), genPeptid.getPeptideId());
        };

        float factor = 100f / Float.valueOf(protein.getSequence().length());
        int[] distArr = new int[protein.getSequence().length()];
        peptideDistMap = new TreeSet<>(Collections.reverseOrder());
        peptidesNodes.values().forEach((peptide) -> {
            if (protein.getSequence().contains(peptide.getSequence())) {
                int current = 0;
                int index;
                while (true) {
                    index = protein.getSequence().indexOf(peptide.getSequence(), current);
                    current = index + peptide.getSequence().length();
                    if (index == -1) {
                        break;
                    }
                    int startIndex = index;
                    float left = (index) * factor;
                    float width = (peptide.getSequence().length() * factor);
                    width = (width / (100f - left) * 100);
                    for (char c : peptide.getSequence().toCharArray()) {
                        distArr[index] = distArr[index] + 1;
                        index++;
                    }

                    PeptideLayout genPeptide = new PeptideLayout(peptide, width, startIndex, left, styles.get(peptide.getValidation()), styles.get("Not Applicable"), protein.isEnymaticPeptide(peptide.getModifiedSequence()));
                    genPeptide.addLayoutClickListener(peptidesListener);
                    peptideDistMap.add(genPeptide);
                    peptideObjectsSet.add(peptide);

                }

            } else if (protein.getSequence().toLowerCase().replaceAll("i", "l").contains(peptide.getSequence().toLowerCase().replaceAll("i", "l"))) {
                String tempProtSeq = protein.getSequence().toLowerCase().replaceAll("i", "l");
                String tempPeptSeq = peptide.getSequence().toLowerCase().replaceAll("i", "l");
                 int current = 0;
                int index;
                while (true) {
                    index = tempProtSeq.indexOf(tempPeptSeq, current);
                    current = index + tempPeptSeq.length();
                    if (index == -1) {
                        break;
                    }
                    int startIndex = index;
                    float left = (index) * factor;
                    float width = (tempPeptSeq.length() * factor);
                    width = (width / (100f - left) * 100);
                    for (char c : tempPeptSeq.toCharArray()) {
                        distArr[index] = distArr[index] + 1;
                        index++;
                    }

                    PeptideLayout genPeptide = new PeptideLayout(peptide, width, startIndex, left, styles.get(peptide.getValidation()), styles.get("Not Applicable"), protein.isEnymaticPeptide(peptide.getModifiedSequence()));
                    genPeptide.addLayoutClickListener(peptidesListener);
                    peptideDistMap.add(genPeptide);
                    peptideObjectsSet.add(peptide);

                }
            }
        });

        int levelNum = 1;
        for (int index = 0; index < distArr.length; index++) {
            if (distArr[index] > 0) {
                VerticalLayout highlight = new VerticalLayout();
                highlight.setHeight(100, Unit.PERCENTAGE);
                highlight.setStyleName("lightgraylayout");
                highlight.addStyleName("peptidelayout");
                float left = index * factor;
                int start = index;
                float w = 0;
                for (; index < distArr.length && distArr[index] > 0; index++) {
                    w++;
                }
                highlight.setDescription(protein.getSequence().substring(start, (start + (int) w)));
                w = w * factor;
                w = (w / (100f - left) * 100);
                highlight.setWidth(w, Unit.PERCENTAGE);
                proteinCoverageLayout.addComponent(highlight, "left:" + left + "%; bottom:0px;");

            }

        }
        int[] usedDistArr = new int[protein.getSequence().length()];
        for (PeptideLayout pep : peptideDistMap) {
            int level = 0;
            for (int i = pep.getStartIndex(); i < pep.getEndIndex(); i++) {
                level = Math.max(usedDistArr[i], level);
                usedDistArr[i] = usedDistArr[i] + 1;
                levelNum = Math.max(levelNum, usedDistArr[i]);
            }

            level = level * 20;
            peptideDistributionLayout.addComponent(pep, "left:" + pep.getX() + "%; top:" + level + "px;");

        }
        levelNum = 25 + (levelNum * 20) + 5;
        ProteinCoverageComponent.this.setHeight(levelNum, Unit.PIXELS);
        chainCoverage3dLayout.setVisible(false);

    }

    public void selectPeptides(Set<Object> peptidesId) {
        peptideDistMap.forEach((peptide) -> {
            peptide.setSelected(peptidesId.contains(peptide.getPeptideId()));
        });
    }

    public Set<PeptideObject> getPeptideObjectsSet() {
        return peptideObjectsSet;
    }

    public void selectPeptides(Object peptideId) {
        peptideDistMap.forEach((peptide) -> {
            peptide.setSelected(peptideId.equals(peptide.getPeptideId()));
        });
    }

    public void enable3D(Component view) {
        this.chainCoverage3dLayout.removeAllComponents();
        this.chainCoverage3dLayout.addComponent(view);
        view.setSizeFull();
        this.chainCoverage3dLayout.setHeight(30, Unit.PIXELS);
        this.chainCoverage3dLayout.setWidth(100, Unit.PERCENTAGE);
        this.chainCoverage3dLayout.setVisible(view.isVisible());

    }

    public abstract void selectPeptide(Object proteinId, Object peptideId);

    public void updateStylingMode(String style) {
        proteinCoverageLayout.updateStylingMode(style);
        peptideDistMap.forEach((peptide) -> {
            peptide.updateStylingMode(style);
        });
    }

}
