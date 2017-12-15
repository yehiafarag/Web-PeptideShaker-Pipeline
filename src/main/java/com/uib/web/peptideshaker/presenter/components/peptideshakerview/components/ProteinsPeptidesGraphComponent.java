package com.uib.web.peptideshaker.presenter.components.peptideshakerview.components;

import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideObject;
import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.galaxy.dataobjects.ProteinObject;
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
    public void selectPeptide(Object proteinId, Object peptideId){      
    graphComponent.selectChildItem( proteinId,  peptideId);
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
//        selectedProtiensLabel.setCaption(protein.getDescription());
//        selectedProtiensLabel.setEnabled(true);
//        selectedProtiensLabel.setResource(new ExternalResource("http://www.uniprot.org/uniprot/" + selectedProteinId));
        for (String acc : protein.getProteinGroupSet()) {
            proteinNodes.put(acc, peptideShakerVisualizationDataset.getProtein(acc));
            peptides.addAll(peptideShakerVisualizationDataset.getPeptides(acc));
        }

        Set<String> tunrelatedProt = new LinkedHashSet<>();
        for (PeptideObject peptide : peptides) {
            peptidesNodes.put(peptide.getModifiedSequence(), peptide);
            ArrayList<String> tEd = new ArrayList<>();
            for (String acc : peptide.getProteinsSet()) {
                tEd.add(acc);
                if (!proteinNodes.containsKey(acc)) {
                    tunrelatedProt.add(acc);
                }
            }
            edges.put(peptide.getModifiedSequence(), tEd);

        }
        for (String unrelated : tunrelatedProt) {
            fillUnrelatedProteinsAndPeptides(unrelated, peptideShakerVisualizationDataset.getProtein(unrelated));
        }
        proteinNodes.putAll(unrelatedProt);
        peptidesNodes.putAll(unrelatedPeptides);

        Map<String, ProteinObject> tempProteinNodes = new LinkedHashMap<>(proteinNodes);
        for (String accession : tempProteinNodes.keySet()) {
            proteinNodes.replace(accession, peptideShakerVisualizationDataset.updateProteinInformation(tempProteinNodes.get(accession), accession));
//            proteinTableData.put(accession, proteinNodes.get(accession));
        }

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
            for (PeptideObject pep : tpeptides) {
                if (!edges.containsKey(pep.getModifiedSequence())) {
                    ArrayList<String> tEd = new ArrayList<>();
                    edges.put(pep.getModifiedSequence(), tEd);
                }
                edges.get(pep.getModifiedSequence()).add(proteinAccession);
                if (!peptidesNodes.containsKey(pep.getModifiedSequence())) {
                    unrelatedPeptides.put(pep.getModifiedSequence(), pep);
                    for (String newAcc : pep.getProteinsSet()) {
                        fillUnrelatedProteinsAndPeptides(newAcc, peptideShakerVisualizationDataset.getProtein(newAcc));
                    }
                }

            }
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
