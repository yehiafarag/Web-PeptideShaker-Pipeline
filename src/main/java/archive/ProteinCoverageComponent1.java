package archive;

import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.ProteinGroupObject;
import com.uib.web.peptideshaker.presenter.core.filtercharts.components.RangeColorGenerator;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.coverage.HighlightPeptide;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.coverage.PeptideLayout;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.coverage.ProteinCoverageLayout;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.coverage.ProteoformLayout;
import com.vaadin.event.LayoutEvents;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
public abstract class ProteinCoverageComponent1 extends AbsoluteLayout {
    
    private final ProteinCoverageLayout proteinCoverageLayout;
    private final AbsoluteLayout peptideDistributionLayout;
    private final AbsoluteLayout chainCoverage3dLayout;
    private final AbsoluteLayout protoformCoverage;
    private final Set<PeptideLayout> peptideDistMap;
    private final Set<PeptideObject> peptideObjectsSet;
    private final Map<String, Set<PeptideLayout>> modificationPeptideMap;
    private final int layoutHeight;
    private int layoutHeightProtoform;
    private final int proteinSequenceLength;
    private boolean protoformInit;
    private final ProteinGroupObject mainProteinObject;
    
    public ProteinCoverageComponent1(ProteinGroupObject protein, Map<String, PeptideObject> peptidesNodes, RangeColorGenerator colorScale) {
        ProteinCoverageComponent1.this.setWidth(100, Unit.PERCENTAGE);
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
        this.mainProteinObject = protein;
        modificationPeptideMap = new LinkedHashMap<>();
        if (protein.getValidation() == null) {
            protein.setValidation("Not Available");
        }
        this.peptideObjectsSet = new LinkedHashSet<>();
        chainCoverage3dLayout = new AbsoluteLayout() {
            private boolean expanded = false;
            
            @Override
            public void setVisible(boolean v) {
                if (v && !expanded) {
                    ProteinCoverageComponent1.this.removeAllComponents();
                    ProteinCoverageComponent1.this.addComponent(chainCoverage3dLayout, "left:0; top:0px;");
                    ProteinCoverageComponent1.this.addComponent(proteinCoverageLayout, "left:0; top:30px;");
                    ProteinCoverageComponent1.this.addComponent(peptideDistributionLayout, "left:0; top:45px;");
                    ProteinCoverageComponent1.this.setHeight(ProteinCoverageComponent1.this.getHeight() + 30, Unit.PIXELS);
                    expanded = true;
                } else if (!v && expanded) {
                    ProteinCoverageComponent1.this.removeAllComponents();
                    ProteinCoverageComponent1.this.addComponent(chainCoverage3dLayout, "left:0; top:0px;");
                    ProteinCoverageComponent1.this.addComponent(proteinCoverageLayout, "left:0; top:10px;");
                    ProteinCoverageComponent1.this.addComponent(peptideDistributionLayout, "left:0; top:25px;");
                    ProteinCoverageComponent1.this.setHeight(ProteinCoverageComponent1.this.getHeight() - 30, Unit.PIXELS);
                    expanded = false;
                } else if (!v) {
                    ProteinCoverageComponent1.this.removeAllComponents();
                    ProteinCoverageComponent1.this.addComponent(chainCoverage3dLayout, "left:0; top:0px;");
                    ProteinCoverageComponent1.this.addComponent(proteinCoverageLayout, "left:0; top:10px;");
                    ProteinCoverageComponent1.this.addComponent(peptideDistributionLayout, "left:0; top:25px;");
                }
                super.setVisible(v);
            }
        };
        
        proteinCoverageLayout = new ProteinCoverageLayout(styles.get(protein.getValidation()), styles.get(protein.getProteinEvidence()));
        peptideDistMap = new TreeSet<>(Collections.reverseOrder());
        proteinCoverageLayout.addLayoutClickListener((LayoutEvents.LayoutClickEvent event) -> {
            selectPeptide(protein.getAccession(), null);
        });
        
        peptideDistributionLayout = new AbsoluteLayout();
        peptideDistributionLayout.setWidth(100, Unit.PERCENTAGE);
        peptideDistributionLayout.setHeight(100, Unit.PERCENTAGE);
        peptideDistributionLayout.addStyleName("peptidecoverage");
        
        LayoutEvents.LayoutClickListener peptidesListener = (LayoutEvents.LayoutClickEvent event) -> {
            Component clickedComp = event.getClickedComponent();
            if (clickedComp != null && clickedComp.getStyleName().contains("peptidelayout")) {
                PeptideLayout genPeptid = (PeptideLayout) clickedComp;
                selectPeptide(protein.getAccession(), genPeptid.getPeptideId());
            } else if (clickedComp instanceof Label) {
                selectPeptide(protein.getAccession(), ((Label) clickedComp).getData());
                
            } else if (clickedComp instanceof VerticalLayout) {
                selectPeptide(protein.getAccession(), ((VerticalLayout) clickedComp).getData());
                
            }
        };
        
        peptideDistributionLayout.addLayoutClickListener(peptidesListener);
        float factor = 100f / Float.valueOf(protein.getSequence().length());
        int[] distArr = new int[protein.getSequence().length()];
        
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
                    int topLevel = 0;
                    for (char c : peptide.getSequence().toCharArray()) {
                        distArr[index] = distArr[index] + 1;
                        topLevel = Math.max(topLevel, distArr[index]);
                        index++;
                    }
                    PeptideLayout genPeptide = new PeptideLayout(peptide, width, startIndex, left, styles.get(peptide.getValidation()), styles.get("Not Applicable"), protein.isEnymaticPeptide(peptide.getModifiedSequence()), colorScale.getColor(peptide.getPSMsNumber()));
                    peptideDistMap.add(genPeptide);
                    peptideObjectsSet.add(peptide);
                    for (String mod : peptide.getVariableModifications().split(",")) {
                        mod = mod.split("\\(")[0].trim();
                        if (mod.equalsIgnoreCase("")) {
                            continue;
                        }
                        if (!modificationPeptideMap.containsKey(mod)) {
                            modificationPeptideMap.put(mod, new LinkedHashSet<>());
                        }
                        modificationPeptideMap.get(mod).add(genPeptide);
                    }
                    
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
                    int topLevel = 0;
                    for (char c : tempPeptSeq.toCharArray()) {
                        distArr[index] = distArr[index] + 1;
                        topLevel = Math.max(topLevel, distArr[index]);
                        index++;
                    }
                    PeptideLayout genPeptide = new PeptideLayout(peptide, width, startIndex, left, styles.get(peptide.getValidation()), styles.get("Not Applicable"), protein.isEnymaticPeptide(peptide.getModifiedSequence()), colorScale.getColor(peptide.getPSMsNumber()));
                    peptideDistMap.add(genPeptide);
                    peptideObjectsSet.add(peptide);
                    
                }
            }
        });
        
        for (int index = 0; index < distArr.length; index++) {
            if (distArr[index] > 0) {
                float left = index * factor;
                int start = index;
                float w = 0;
                for (; index < distArr.length && distArr[index] > 0; index++) {
                    w++;
                }
                String desc = protein.getSequence().substring(start, (start + (int) w));
                w = w * factor;
                w = (w / (100f - left) * 100);
                HighlightPeptide highlight = new HighlightPeptide(w, desc);
                proteinCoverageLayout.addComponent(highlight, "left:" + left + "%; bottom:0px;");
            }
            
        }
        proteinSequenceLength = protein.getSequence().length();
        int levelNum = initPeptideCoverageLayout(false);
        layoutHeight = 25 + (levelNum * 20) + 5;
        chainCoverage3dLayout.setVisible(false);
        ProteinCoverageComponent1.this.setHeight(layoutHeight, Unit.PIXELS);
        
        protoformCoverage = new AbsoluteLayout();
        protoformCoverage.setWidth(100, Unit.PERCENTAGE);
        protoformCoverage.setHeight(100, Unit.PERCENTAGE);
        protoformCoverage.setVisible(false);
        
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
        final boolean selectAll = peptideId == null;
        peptideDistMap.forEach((peptide) -> {
            peptide.setSelected(selectAll || peptideId.equals(peptide.getPeptideId()));
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
        final String updateStyle;
        if (style.equalsIgnoreCase("Proteoform")) {
            updateStyle = "Modification";
            if (!protoformInit) {
                protoformInit = true;
                int levelNum = initPeptideCoverageLayout(true);
                int topCorrector = ((levelNum * 20) + 5);
                peptideDistributionLayout.addComponent(protoformCoverage, "left:0px;top:" + topCorrector + "px;");
                int protoformNum = 0;
                int top = 0;
                for (String protoformKey : mainProteinObject.getProtoformsNodes().keySet()) {
                    if (protoformKey.contains(":") || protoformKey.contains("-")) {
                        ProteoformLayout protolayout = new ProteoformLayout(++protoformNum, top,Color.GRAY,"",true){
                            @Override
                            public void selectProtoform(ProteoformLayout protoform) {
                                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                            }
                        
                        
                        };
                        protoformCoverage.addComponent(protolayout, "left:0px;top:" + top + "px;");
                        protolayout.updateHighlightedComponents(proteinCoverageLayout);
                        
                        Map<String, Integer> modificationMap = mainProteinObject.getProtoformsNodes().get(protoformKey).getModificationsLocationsMap();
                        for (String mod : modificationMap.keySet()) {
                            if (modificationPeptideMap.containsKey(mod)) {
                                Set<PeptideLayout> peptides = modificationPeptideMap.get(mod);
                                PeptideLayout refPeptide = null;
                                for (PeptideLayout peptide : peptides) {
                                    int peptideStart = Integer.valueOf(peptide.getPeptide().getPostion());
                                    int peptideLocation = peptideStart + peptide.getPeptide().getSequence().length();
                                    int modLocation = modificationMap.get(mod);
                                    if (modLocation >= peptideStart && modLocation < peptideLocation) {
                                        peptide.addLocation(topCorrector + top);
                                        refPeptide = peptide;
                                        break;
                                    }
                                }
                                if (refPeptide != null) {
                                    peptides.remove(refPeptide);
                                    if (modificationPeptideMap.get(mod).isEmpty()) {
                                        modificationPeptideMap.remove(mod);
                                    }
                                }
                            }
                        }
                        top += 20;
                    }
                    
                }
                
                layoutHeightProtoform = (layoutHeight) + (protoformNum * 20);
                for (String key : modificationPeptideMap.keySet()) {
                    for (PeptideLayout pep : modificationPeptideMap.get(key)) {
                        pep.addLocation(pep.getPostionsList().get(0));
                    }
                }
            }
            
            ProteinCoverageComponent1.this.setHeight(layoutHeightProtoform, Unit.PIXELS);
            protoformCoverage.setVisible(true);
            reDistributePeptides(true);
//            int levelNum = reOrganizePeptideCoverageLayout(true);
//            peptideDistributionLayout.addComponent(protoformCoverage, "left:0px;top:" + ((levelNum * 20) + 5) + "px;");
//            levelNum = ((levelNum * 20) + 5) + (protoformNum * 20)+50;
//            ProteinCoverageComponent.this.setHeight(levelNum, Unit.PIXELS);
//            protoformCoverage.setVisible(true);

        } else {
            updateStyle = style;
            ProteinCoverageComponent1.this.setHeight(layoutHeight, Unit.PIXELS);
            protoformCoverage.setVisible(false);
            reDistributePeptides(false);

//            System.out.println("at  update " + coverageLayoutHeightWithOutProtoform);
        }
        peptideDistMap.forEach((peptide) -> {
            peptide.updateStylingMode(updateStyle);
        });
        
    }
    
    private void reDistributePeptides(boolean protoform) {
        int yIndex = 0;
        if (protoform) {
            yIndex = 1;
        }
        for (PeptideLayout pep : peptideDistMap) {
            peptideDistributionLayout.getPosition(pep).setTop(pep.getPostionsList().get(yIndex), Unit.PIXELS);
            
        }
        
    }
    
    private int initPeptideCoverageLayout(boolean protoform) {
        int levelNum = 1;
        int[] usedDistArr = new int[proteinSequenceLength];
        int level = 0;
        int topLevel = 0;
        for (PeptideLayout pep : peptideDistMap) {
            if (pep.isModifiedPeptide() && protoform) {
                continue;
            }
            level = 0;
            topLevel = 0;
            for (int i = pep.getStartIndex(); i < pep.getEndIndex(); i++) {
                level = Math.max(usedDistArr[i], level);
                usedDistArr[i] = usedDistArr[i] + 1;
                topLevel = Math.max(topLevel, usedDistArr[i]);
            }
            for (int i = pep.getStartIndex(); i < pep.getEndIndex(); i++) {
                usedDistArr[i] = topLevel;
            }
            levelNum = Math.max(levelNum, topLevel);
            level = level * 20;
            if (!protoform) {
                peptideDistributionLayout.addComponent(pep, "left:" + pep.getX() + "%; top:" + level + "px;");
            }
            pep.addLocation(level);
            
        }
        return levelNum;
        
    }
    
}
