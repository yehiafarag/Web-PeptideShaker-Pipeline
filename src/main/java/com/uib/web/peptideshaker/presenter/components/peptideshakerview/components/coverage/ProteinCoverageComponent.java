package com.uib.web.peptideshaker.presenter.components.peptideshakerview.components.coverage;

import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideObject;
import com.uib.web.peptideshaker.galaxy.dataobjects.ProteinObject;
import com.vaadin.event.LayoutEvents;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.VerticalLayout;
import java.util.Collections;
import java.util.HashMap;
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
    private final Set<PeptideLayout> peptideDistMap;
    
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
        proteinCoverageLayout = new ProteinCoverageLayout(styles.get(protein.getValidation()), styles.get(protein.getProteinEvidence()));
        ProteinCoverageComponent.this.addComponent(proteinCoverageLayout, "left:0; bottom:5px;");
        
        peptideDistributionLayout = new AbsoluteLayout();
        peptideDistributionLayout.setWidth(100, Unit.PERCENTAGE);
        peptideDistributionLayout.setHeight(100, Unit.PERCENTAGE);
        peptideDistributionLayout.addStyleName("peptidecoverage");
        ProteinCoverageComponent.this.addComponent(peptideDistributionLayout, "left:0; bottom:25px;");
        
        LayoutEvents.LayoutClickListener peptidesListener = (LayoutEvents.LayoutClickEvent event) -> {
            PeptideLayout genPeptid = (PeptideLayout) event.getComponent();
            selectPeptide(protein.getAccession(), genPeptid.getPeptideId());
        };
        
        float factor = 100f / protein.getSequence().length();
        int[] distArr = new int[protein.getSequence().length()];
        peptideDistMap = new TreeSet<>(Collections.reverseOrder());
        for (PeptideObject peptide : peptidesNodes.values()) {
            if (protein.getSequence().contains(peptide.getSequence())) {
                int index = protein.getSequence().indexOf(peptide.getSequence());
                float left = index * factor;
                float width = peptide.getSequence().length() * factor;
                for (char c : peptide.getSequence().toCharArray()) {
                    distArr[index] = distArr[index] + 1;
                    index++;
                }
                PeptideLayout genPeptide = new PeptideLayout(peptide, width, index, left, styles.get(peptide.getValidation()), styles.get("Not Applicable"),protein.isEnymaticPeptide(peptide.getModifiedSequence()));
                genPeptide.addLayoutClickListener(peptidesListener);
                peptideDistMap.add(genPeptide);
                
            }
            
        }
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
                highlight.setWidth(w, Unit.PERCENTAGE);
                proteinCoverageLayout.addComponent(highlight, "left:" + left + "%; bottom:0px;");
                
            }
            
        }
        int[] usedDistArr = new int[protein.getSequence().length()];
        for (PeptideLayout pep : peptideDistMap) {
            if (usedDistArr[pep.getStartIndex()] == 0) {
                peptideDistributionLayout.addComponent(pep, "left:" + pep.getX() + "%; bottom:5px;");
                usedDistArr[pep.getStartIndex()] = usedDistArr[pep.getStartIndex()] + 1;
            } else {
                int level = usedDistArr[pep.getStartIndex()] * 25;
                peptideDistributionLayout.addComponent(pep, "left:" + pep.getX() + "%; bottom:" + level + "px;");
                usedDistArr[pep.getStartIndex()] = usedDistArr[pep.getStartIndex()] + 1;
                if (levelNum < usedDistArr[pep.getStartIndex()]) {
                    levelNum = usedDistArr[pep.getStartIndex()];
                }
            }
            
        }
        
        levelNum = 25 + (levelNum * 25) + 5;
        ProteinCoverageComponent.this.setHeight(levelNum, Unit.PIXELS);
    }
    
    public void selectPeptides(Set<Object> peptidesId) {
        for (PeptideLayout peptide : peptideDistMap) {
            peptide.setSelected(peptidesId.contains(peptide.getPeptideId()));
        }
    }
    
    public void selectPeptides(Object peptideId) {
        for (PeptideLayout peptide : peptideDistMap) {
            peptide.setSelected(peptideId.equals(peptide.getPeptideId()));
        }
    }
    
    public abstract void selectPeptide(Object proteinId, Object peptideId);
    
    public void updateStylingMode(String style) {
        proteinCoverageLayout.updateStylingMode(style);
        for (PeptideLayout peptide : peptideDistMap) {
            peptide.updateStylingMode(style);
        }
    }
    
}