package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components;

import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.ProteinObject;
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
    private final Map<String, ProteinObject> proteinNodes;
    private final Map<String, PeptideObject> peptidesNodes;
    private final Set<PeptideObject> peptides;
    private final Map<String, ProteinObject> unrelatedProt = new LinkedHashMap<>();
    private final Map<String, PeptideObject> unrelatedPeptides = new LinkedHashMap<>();
    private final HashMap<String, ArrayList<String>> edges;
    private PeptideShakerVisualizationDataset peptideShakerVisualizationDataset;
    private String thumbURL;

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

    public Map<String, ProteinObject> getProteinNodes() {
        return proteinNodes;
    }

    public Map<String, PeptideObject> getPeptidesNodes() {
        return peptidesNodes;
    }

    public void selectPeptide(Object proteinId, Object peptideId) {
        graphComponent.selectChildItem(proteinId, peptideId);
    }

    public String updateGraphData(String selectedProteinId) {
        proteinNodes.clear();
        peptidesNodes.clear();
        peptides.clear();
        unrelatedProt.clear();
        unrelatedPeptides.clear();
        edges.clear();
        if (peptideShakerVisualizationDataset == null || selectedProteinId == null || selectedProteinId.trim().equalsIgnoreCase("null") || peptideShakerVisualizationDataset.getProtein(selectedProteinId) == null) {
//            selectedProtiensLabel.setCaption("");
            graphComponent.updateGraphData(null, null, null, null);
            thumbURL = null;
            return thumbURL;
        }
        ProteinObject protein = peptideShakerVisualizationDataset.getProtein(selectedProteinId);
//        if (protein.getSequence() == null) {
//            peptideShakerVisualizationDataset.setProteinInformation(protein);
//        }
        protein.getProteinGroupSet().stream().map((acc) -> {
            proteinNodes.put(acc, peptideShakerVisualizationDataset.getProtein(acc));
            return acc;
        }).forEachOrdered((acc) -> {
            peptides.addAll(peptideShakerVisualizationDataset.getPeptides(acc));
        });

        Set<String> tunrelatedProt = new LinkedHashSet<>();
        peptides.stream().map((peptide) -> {
            peptidesNodes.put(peptide.getModifiedSequence(), peptide);
            return peptide;
        }).forEachOrdered((peptide) -> {
            ArrayList<String> tEd = new ArrayList<>();
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

        Map<String, ProteinObject> tempProteinNodes = new LinkedHashMap<>(proteinNodes);
        tempProteinNodes.keySet().forEach((accession) -> {
            proteinNodes.replace(accession, peptideShakerVisualizationDataset.updateProteinInformation(tempProteinNodes.get(accession), accession));
        });

        graphComponent.updateGraphData(protein, proteinNodes, peptidesNodes, edges);
        thumbURL = graphComponent.getThumbImgeUrl();
        return thumbURL;

    }

    private void fillUnrelatedProteinsAndPeptides(String proteinAccession, ProteinObject protein) {
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
                pep.getProteinsSet().forEach((newAcc) -> {
                    fillUnrelatedProteinsAndPeptides(newAcc, peptideShakerVisualizationDataset.getProtein(newAcc));
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
