package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components;

import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.ProteinGroupObject;
import com.uib.web.peptideshaker.presenter.core.filtercharts.components.RangeColorGenerator;
import com.uib.web.peptideshaker.presenter.core.graph.GraphComponent;
import com.vaadin.ui.VerticalLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * this class represents interactive graph layout
 *
 * @author Yehia Farag
 */
public abstract class ProteinsPeptidesGraphComponent extends VerticalLayout {

    private final GraphComponent graphComponent;
    private final Map<String, ProteinGroupObject> proteinNodes;
    private final Map<String, PeptideObject> peptidesNodes;
    private final Set<PeptideObject> peptides;
    private final Map<String, ProteinGroupObject> unrelatedProt = new LinkedHashMap<>();
    private final Map<String, PeptideObject> unrelatedPeptides = new LinkedHashMap<>();
    private final HashMap<String, ArrayList<String>> edges;
    private PeptideShakerVisualizationDataset peptideShakerVisualizationDataset;
    private String thumbURL;
    private int maxPsms;
    private RangeColorGenerator colorScale;

    public RangeColorGenerator getColorScale() {
        return colorScale;
    }

    public ProteinsPeptidesGraphComponent() {

        ProteinsPeptidesGraphComponent.this.setSizeFull();
        graphComponent = new GraphComponent() {
            @Override
            public void selectedItem(Set<Object> selectedItems, Set<Object> selectedChildsItems) {
                ProteinsPeptidesGraphComponent.this.selectedItem(selectedItems, selectedChildsItems);
            }

            @Override
            public void updateProteinsMode(String modeType) {
                ProteinsPeptidesGraphComponent.this.updateProteinsMode(modeType);
            }

        };
        ProteinsPeptidesGraphComponent.this.addComponent(graphComponent);
        proteinNodes = new LinkedHashMap<>();
        peptidesNodes = new LinkedHashMap<>();
        peptides = new LinkedHashSet<>();
        edges = new HashMap<>();

    }

    public void selectDataset(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {
        this.peptideShakerVisualizationDataset = peptideShakerVisualizationDataset;
    }

    public Map<String, ProteinGroupObject> getProteinNodes() {
        return proteinNodes;
    }

    public Map<String, PeptideObject> getPeptidesNodes() {
        return peptidesNodes;
    }

    public void selectPeptide(Object proteinId, Object peptideId) {
        if (peptideId == null) {

            graphComponent.selectParentItem(proteinId);
        } else {
            graphComponent.selectChildItem(proteinId, peptideId);
        }
    }

    public String updateGraphData(String selectedProteinId) {

        proteinNodes.clear();
        peptidesNodes.clear();
        peptides.clear();
        unrelatedProt.clear();
        unrelatedPeptides.clear();
        edges.clear();
        if (peptideShakerVisualizationDataset == null || selectedProteinId == null || selectedProteinId.trim().equalsIgnoreCase("null") || peptideShakerVisualizationDataset.getProtein(selectedProteinId) == null) {
            graphComponent.updateGraphData(null, null, null, null, null);
            thumbURL = null;
            return thumbURL;
        }
        ProteinGroupObject protein = peptideShakerVisualizationDataset.getProtein(selectedProteinId);
        peptides.addAll(peptideShakerVisualizationDataset.getPeptides(selectedProteinId));

        Set<String> tunrelatedProt = new LinkedHashSet<>();
        maxPsms = 0;
        peptides.stream().map((peptide) -> {
            peptidesNodes.put(peptide.getModifiedSequence(), peptide);
            maxPsms = Math.max(maxPsms, peptide.getPSMsNumber());
            return peptide;
        }).forEachOrdered((peptide) -> {
            ArrayList<String> tEd = new ArrayList<>();
//            for (String protGroupKey : peptide.getProteinGroupKey().split(";")) {
//                protGroupKey = protGroupKey.trim();
//                System.out.println("peptide.getModifiedSequence() "+peptide.getModifiedSequence()+" "+protGroupKey);
//                tEd.add(protGroupKey);
//                if (!proteinNodes.containsKey(protGroupKey)) {
//                    tunrelatedProt.add(protGroupKey);
//                }
//            }
            peptide.getProteinsSet().stream().map((acc) -> {
                tEd.add(acc);
                return acc;
            }).filter((acc) -> (!proteinNodes.containsKey(acc))).forEachOrdered((acc) -> {
                tunrelatedProt.add(acc);
            });
            edges.put(peptide.getModifiedSequence(), tEd);
        });

        tunrelatedProt.forEach((unrelated) -> {            
            fillUnrelatedProteinsAndPeptides(unrelated, peptideShakerVisualizationDataset.getProtein(unrelated));
        });
        proteinNodes.putAll(unrelatedProt);
        peptidesNodes.putAll(unrelatedPeptides);

        Map<String, ProteinGroupObject> tempProteinNodes = new LinkedHashMap<>(proteinNodes);
        tempProteinNodes.keySet().forEach((accession) -> {
           
            proteinNodes.replace(accession, peptideShakerVisualizationDataset.updateProteinInformation(tempProteinNodes.get(accession), accession));
        });
        colorScale = new RangeColorGenerator(maxPsms);
        graphComponent.updateGraphData(protein, proteinNodes, peptidesNodes, edges, colorScale);
        thumbURL = graphComponent.getThumbImgeUrl();
        return thumbURL;

    }

    private void fillUnrelatedProteinsAndPeptides(String proteinAccession, ProteinGroupObject protein) {
        if (unrelatedProt.containsKey(proteinAccession)) {
            return;
        }
        unrelatedProt.put(proteinAccession, protein);
        Set<PeptideObject> tpeptides = peptideShakerVisualizationDataset.getPeptides(proteinAccession);
        if (tpeptides != null) {
            tpeptides.stream().map((pep) -> {
                if (!edges.containsKey(pep.getModifiedSequence())) {
                    ArrayList<String> tEd = new ArrayList<>();
                    edges.put(pep.getModifiedSequence(), tEd);
                }
                return pep;
            }).map((pep) -> {
                edges.get(pep.getModifiedSequence()).add(proteinAccession);
                return pep;
            }).filter((pep) -> (!peptidesNodes.containsKey(pep.getModifiedSequence()))).map((pep) -> {
                unrelatedPeptides.put(pep.getModifiedSequence(), pep);
                return pep;
            }).forEachOrdered((pep) -> {
//               for(String proteinGroupsKey:pep.getProteinGroupKey().split(";")) {
//                    fillUnrelatedProteinsAndPeptides(proteinGroupsKey, peptideShakerVisualizationDataset.getProtein(proteinGroupsKey));
//                };
                pep.getProteinsSet().forEach((newAcc) -> {
                    ProteinGroupObject tprot = peptideShakerVisualizationDataset.getProtein(newAcc,pep.getModifiedSequence());
                    fillUnrelatedProteinsAndPeptides(newAcc, tprot);
                });
            });
        }

    }

    public Set<Object> getSelectedProteins() {
        return graphComponent.getSelectedProteins();
    }

    public Set<Object> getSelectedPeptides() {
        return graphComponent.getSelectedPeptides();
    }

    public abstract void selectedItem(Set<Object> selectedItems, Set<Object> selectedChildsItems);

    public abstract void updateProteinsMode(String modeType);

}
