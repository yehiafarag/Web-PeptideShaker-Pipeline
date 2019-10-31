package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.coverage;

import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.ProteinGroupObject;
import com.uib.web.peptideshaker.presenter.core.filtercharts.components.RangeColorGenerator;
import com.vaadin.event.LayoutEvents;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
public abstract class ProteinCoverageComponent extends AbsoluteLayout {

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
    private final float resizeFactor;

    public ProteinCoverageComponent(ProteinGroupObject protein, Map<String, PeptideObject> peptidesNodes, RangeColorGenerator colorScale) {
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
                    ProteinCoverageComponent.this.removeAllComponents();
                    ProteinCoverageComponent.this.addComponent(chainCoverage3dLayout, "left:0; top:0px;");
                    ProteinCoverageComponent.this.addComponent(proteinCoverageLayout, "left:0; top:30px;");
                    ProteinCoverageComponent.this.addComponent(peptideDistributionLayout, "left:0; top:45px;");
                    ProteinCoverageComponent.this.setHeight(ProteinCoverageComponent.this.getHeight() + 20, Unit.PIXELS);
                    expanded = true;

                } else if (!v && expanded) {
                    ProteinCoverageComponent.this.removeAllComponents();
                    ProteinCoverageComponent.this.addComponent(chainCoverage3dLayout, "left:0; top:0px;");
                    ProteinCoverageComponent.this.addComponent(proteinCoverageLayout, "left:0; top:10px;");
                    ProteinCoverageComponent.this.addComponent(peptideDistributionLayout, "left:0; top:25px;");
                    ProteinCoverageComponent.this.setHeight(ProteinCoverageComponent.this.getHeight() - 20, Unit.PIXELS);
                    expanded = false;
                } else if (!v) {
                    ProteinCoverageComponent.this.removeAllComponents();
                    ProteinCoverageComponent.this.addComponent(chainCoverage3dLayout, "left:0; top:0px;");
                    ProteinCoverageComponent.this.addComponent(proteinCoverageLayout, "left:0; top:10px;");
                    ProteinCoverageComponent.this.addComponent(peptideDistributionLayout, "left:0; top:25px;");
                }
                if (v) {
                    proteinCoverageLayout.removeStyleName("hideinvisible");
                } else {
                    proteinCoverageLayout.addStyleName("hideinvisible");
                }
                super.setVisible(v);
            }
        };

        proteinCoverageLayout = new ProteinCoverageLayout(styles.get(protein.getValidation()), styles.get(protein.getProteinEvidence()));
        peptideDistMap = new TreeSet<>(Collections.reverseOrder());
        proteinCoverageLayout.addLayoutClickListener((LayoutEvents.LayoutClickEvent event) -> {
            resetHeighlightedProtoforms();
            selectPeptide(protein.getAccession(), null);
        });

        peptideDistributionLayout = new AbsoluteLayout();
        peptideDistributionLayout.setWidth(100, Unit.PERCENTAGE);
        peptideDistributionLayout.setHeight(100, Unit.PERCENTAGE);
        peptideDistributionLayout.addStyleName("peptidecoverage");

        LayoutEvents.LayoutClickListener peptidesListener = (LayoutEvents.LayoutClickEvent event) -> {
            Component clickedComp = event.getClickedComponent();
            if ((clickedComp != null) && (clickedComp.getStyleName().contains("protoformcoverage") || clickedComp.getStyleName().contains("protoformmodstyle") || (clickedComp.getStyleName().contains("peptidelayout") && (clickedComp instanceof VerticalLayout)))) {

            } else if (clickedComp != null && clickedComp.getStyleName().contains("peptidelayout")) {
                PeptideLayout genPeptid = (PeptideLayout) clickedComp;
                selectPeptide(protein.getAccession(), genPeptid.getPeptideId());
                resetHeighlightedProtoforms();
            } else if (clickedComp instanceof Label) {
                Object data = ((Label) clickedComp).getData();
                if (data != null) {
                    selectPeptide(protein.getAccession(), ((Label) clickedComp).getData());
                    resetHeighlightedProtoforms();
                }
            } else if (clickedComp instanceof VerticalLayout) {
                selectPeptide(protein.getAccession(), ((VerticalLayout) clickedComp).getData());
                resetHeighlightedProtoforms();

            }
        };

        peptideDistributionLayout.addLayoutClickListener(peptidesListener);
        resizeFactor = 100f / Float.valueOf(protein.getSequence().length());
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
                    float left = (index) * resizeFactor;
                    float width = (peptide.getSequence().length() * resizeFactor);
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
                    for (String mod : peptide.getVariableModificationsAsString().split(",")) {
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
                    float left = (index) * resizeFactor;
                    float width = (tempPeptSeq.length() * resizeFactor);
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
                float left = index * resizeFactor;
                int start = index;
                float w = 0;
                for (; index < distArr.length && distArr[index] > 0; index++) {
                    w++;
                }
                String desc = protein.getSequence().substring(start, (start + (int) w));
                w = w * resizeFactor;
                w = (w / (100f - left) * 100);
                HighlightPeptide highlight = new HighlightPeptide(w, desc);
                proteinCoverageLayout.addComponent(highlight, "left:" + left + "%; bottom:0px;");
            }

        }
        proteinSequenceLength = protein.getSequence().length();
        int levelNum = initPeptideCoverageLayout();
        levelNum++;
        layoutHeight = 25 + (levelNum * 20) + 5;
        chainCoverage3dLayout.setVisible(false);
        ProteinCoverageComponent.this.setHeight(layoutHeight, Unit.PIXELS);
        layoutHeightProtoform = layoutHeight;

        protoformCoverage = new AbsoluteLayout();
        protoformCoverage.setWidth(100, Unit.PERCENTAGE);
        protoformCoverage.setHeight(100, Unit.PERCENTAGE);
        protoformCoverage.setVisible(false);
        protoformCoverage.addStyleName("protoformcoverage");

    }

    public void selectSubComponents(Set<Object> peptidesId) {
        peptideDistMap.forEach((peptide) -> {
            peptide.setSelected(peptidesId.contains(peptide.getPeptideId()));
        });
        if (!peptidesId.isEmpty() && peptidesId.iterator().next().toString().contains(";")) {
            Iterator<Component> itr = protoformCoverage.iterator();
            while (itr.hasNext()) {
                ProteoformLayout proteform = ((ProteoformLayout) itr.next());
                if (!peptidesId.contains(proteform.getData())) {
                    proteform.addStyleName("inactivatelayout");
                    proteform.setEnabled(false);
                    proteform.getIncludedModifications().stream().map((mod) -> {
                        mod.addStyleName("inactivatelayout");
                        return mod;
                    }).forEachOrdered((mod) -> {
                        mod.setEnabled(false);
                    });
                } else {
                    proteform.removeStyleName("inactivatelayout");
                    proteform.setEnabled(true);
                    proteform.getIncludedModifications().stream().map((mod) -> {
                        mod.removeStyleName("inactivatelayout");
                        return mod;
                    }).forEachOrdered((mod) -> {
                        mod.setEnabled(true);
                    });
                }
            }
        }
    }

    public Set<PeptideObject> getPeptideObjectsSet() {
        return peptideObjectsSet;
    }

    public void selectPeptides(Object peptideId) {
        final boolean selectAll = peptideId == null;
        peptideDistMap.forEach((PeptideLayout peptide) -> {
            peptide.setSelected(selectAll || peptide.getPeptideId().equals(peptideId));
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
            if (!protoformInit && mainProteinObject.getProtoformsNodes() != null && !mainProteinObject.getProtoformsNodes().isEmpty()) {
                protoformInit = true;
                int topCorrector = layoutHeight - 25;
                peptideDistributionLayout.addComponent(protoformCoverage, "left:0px;top:" + topCorrector + "px;");
                int protoformNum = 0;
                int top = 0;
                for (String protoformKey : mainProteinObject.getProtoformsNodes().keySet()) {
                    Map<String, Integer> modificationMap = mainProteinObject.getProtoformsNodes().get(protoformKey).getModificationsLocationsMap();
                    ProteoformLayout protolayout = new ProteoformLayout(++protoformNum, top, mainProteinObject.getProtoformsNodes().get(protoformKey).getFinalColor(), protoformKey, (mainProteinObject.getProtoformsNodes().get(protoformKey).getEdgesNumber() > 1)) {
                        @Override
                        public void selectProtoform(ProteoformLayout protoform) {
                            selectProteinProteoform(protoform);
                        }

                    };
                    protolayout.setData(protoformKey);
                    protolayout.setDescription(mainProteinObject.getProtoformsNodes().get(protoformKey).getDescription());
                    protoformCoverage.addComponent(protolayout, "left:0px;top:" + top + "px;");
                    protolayout.updateHighlightedComponents(proteinCoverageLayout);
                    for (String mod : modificationMap.keySet()) {
                        ProtoformModificationLayout modLayout = new ProtoformModificationLayout(mod, modificationMap.get(mod)) {
                            @Override
                            public void selected(ProtoformModificationLayout protoformModificationLayout) {
                                selectedProteoformModification(protoformModificationLayout);
                            }

                        };
                        protolayout.addModificationLayout(modLayout);
                        float left = (float) modificationMap.get(mod) * resizeFactor;
                        peptideDistributionLayout.addComponent(modLayout, "left:" + left + "%; top:" + (top + topCorrector) + "px;");

                        if (modificationPeptideMap.containsKey(mod)) {
                            Set<PeptideLayout> peptides = modificationPeptideMap.get(mod);
                            peptides.forEach((peptide) -> {
                                String poStr = peptide.getPeptide().getPostion();
                                for (String st : poStr.split(";")) {
                                    int peptideStart = Integer.valueOf(st.trim());
                                    int peptideLocation = peptideStart + peptide.getPeptide().getSequence().length();
                                    int modLocation = modificationMap.get(mod);
                                    if (modLocation >= peptideStart && modLocation < peptideLocation) {
                                        modLayout.addCorrespondingPeptide(peptide);
                                    }
                                }
                            });
                        }
                    }
                    top += 20;
                    layoutHeightProtoform = (layoutHeight) + (protoformNum * 20) + 25;

                }
            }
            ProteinCoverageComponent.this.setHeight(layoutHeightProtoform, Unit.PIXELS);
            protoformCoverage.setVisible(true);
        } else if (style.equalsIgnoreCase("Protein-Peptide")) {
            resetHeighlightedProtoforms();
            ProteinCoverageComponent.this.setHeight(layoutHeight, Unit.PIXELS);
            return;
        } else {
            updateStyle = style;
            ProteinCoverageComponent.this.setHeight(layoutHeight, Unit.PIXELS);
            protoformCoverage.setVisible(false);
        }
        peptideDistMap.forEach((peptide) -> {
            peptide.updateStylingMode(updateStyle);
        });

    }

    private int initPeptideCoverageLayout() {
        int levelNum = 1;
        int[] usedDistArr = new int[proteinSequenceLength];
        int level;
        int topLevel;
        for (PeptideLayout pep : peptideDistMap) {
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
            peptideDistributionLayout.addComponent(pep, "left:" + pep.getX() + "%; top:" + level + "px;");
            pep.addLocation(level);
            if (pep.getPeptide().isInvisibleOn3d()) {

                AbsoluteLayout unmapped3dPeptide = new AbsoluteLayout();
                unmapped3dPeptide.setWidth(pep.getWidth(), pep.getWidthUnits());
                unmapped3dPeptide.setStyleName("invisiblepeptideon3d");
                unmapped3dPeptide.setHeight(15, Unit.PIXELS);
                VerticalLayout blackLine = new VerticalLayout();
                blackLine.setStyleName("graymiddleline");
                blackLine.setWidth(100, Unit.PERCENTAGE);
                blackLine.setHeight(2, Unit.PIXELS);
                unmapped3dPeptide.addComponent(blackLine, "left:0px;top:50%;");

                this.proteinCoverageLayout.addComponent(unmapped3dPeptide, "left:" + pep.getX() + "%; top:-24px;");
            }

        }
        return levelNum;

    }

    private void selectedProteoformModification(ProtoformModificationLayout proteoformModification) {
        if (!proteoformModification.isEnabled()) {
            return;
        }
        resetHeighlightedProtoforms();
        PeptideLayout genPeptid = proteoformModification.select();
        if (genPeptid != null) {
            selectPeptide(mainProteinObject.getAccession(), genPeptid.getPeptideId());
        }
    }

    private void resetHeighlightedProtoforms() {
        Iterator<Component> itr = peptideDistributionLayout.iterator();
        while (itr.hasNext()) {
            itr.next().removeStyleName("heighlightcorrespondingpeptide");
        }
        itr = protoformCoverage.iterator();
        while (itr.hasNext()) {
            itr.next().removeStyleName("selectedprotoform");
        }

    }

    private void selectProteinProteoform(ProteoformLayout protoform) {
        Iterator<Component> itr = protoformCoverage.iterator();
        while (itr.hasNext()) {
            itr.next().removeStyleName("selectedprotoform");
        }
        protoform.addStyleName("selectedprotoform");
    }

}
