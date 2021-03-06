package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview;

import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideObject;
import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.galaxy.dataobjects.ProteinObject;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.ProteinCoverageContainer;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.ProteinStructurePanel;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.ProteinsPeptidesGraphComponent;
import com.uib.web.peptideshaker.presenter.core.BigSideBtn;
import com.uib.web.peptideshaker.presenter.core.filtercharts.charts.RegistrableFilter;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class represents the layout that contains PeptideShaker datasets
 * overview
 *
 * @author Yehia Farag
 */
public class ProteinVisulizationLevelContainer extends HorizontalLayout implements RegistrableFilter {

    private final VerticalLayout container;
    private final Label headerLabel;
    private final ProteinsPeptidesGraphComponent selectedProteinGraph;
    private final SelectionManager Selection_Manager;
    private final BigSideBtn proteinoverviewBtn;
    private final ProteinCoverageContainer proteinCoverageContainer;
    private final ProteinStructurePanel proteinStructurePanel;
    private Map<String, PeptideObject> proteinPeptides;

    /**
     * Constructor to initialise the main layout and variables.
     *
     * @param Selection_Manager
     * @param proteinoverviewBtn
     */
    public ProteinVisulizationLevelContainer(SelectionManager Selection_Manager, BigSideBtn proteinoverviewBtn) {
        ProteinVisulizationLevelContainer.this.setSizeFull();
        ProteinVisulizationLevelContainer.this.setSpacing(true);
        ProteinVisulizationLevelContainer.this.setMargin(false);
        this.Selection_Manager = Selection_Manager;
        this.proteinoverviewBtn = proteinoverviewBtn;

        container = new VerticalLayout();
        container.setSizeFull();
        container.setSpacing(false);
        ProteinVisulizationLevelContainer.this.addComponent(container);

        HorizontalLayout topLabelContainer = new HorizontalLayout();
        topLabelContainer.setSizeFull();
        topLabelContainer.addStyleName("minhight30");
        container.addComponent(topLabelContainer);
        container.setExpandRatio(topLabelContainer, 5);

        HorizontalLayout topLeftLabelContainer = new HorizontalLayout();
        topLeftLabelContainer.setWidthUndefined();
        topLeftLabelContainer.setHeight(100, Unit.PERCENTAGE);
        topLabelContainer.addComponent(topLeftLabelContainer);
        headerLabel = new Label();
        headerLabel.setValue("Proteins overview");
        headerLabel.addStyleName("largetitle");
        headerLabel.setWidthUndefined();
        topLeftLabelContainer.setSpacing(true);
        topLeftLabelContainer.addComponent(headerLabel);

        Label commentLabel = new Label("<i style='padding-right: 50px;'>* Click in the graph to select proteins and peptides</i>", ContentMode.HTML);
        commentLabel.setWidthUndefined();
        commentLabel.setStyleName("resizeabletext");
        commentLabel.addStyleName("margintop10");
        topLabelContainer.addComponent(commentLabel);
        topLabelContainer.setComponentAlignment(commentLabel, Alignment.TOP_RIGHT);

        HorizontalLayout middleContainer = new HorizontalLayout();
        middleContainer.setSizeFull();
        middleContainer.setSpacing(true);
        container.addComponent(middleContainer);
        container.setExpandRatio(middleContainer, 49);

        selectedProteinGraph = new ProteinsPeptidesGraphComponent() {
            @Override
            public void selectedItem(Set<Object> selectedItems, Set<Object> selectedChildsItems) {
                proteinCoverageContainer.setSelectedItems(selectedItems, selectedChildsItems);
                if (selectedItems.size() == 1) {
                    ProteinObject protein = this.getProteinNodes().get((String) selectedItems.iterator().next());
                    proteinPeptides = new LinkedHashMap<>();
                    this.getPeptidesNodes().values().stream().filter((peptide) -> (peptide.getProteinsSet().contains(protein.getAccession()))).forEachOrdered((peptide) -> {
                        proteinPeptides.put(peptide.getModifiedSequence(), peptide);
                    });
                    proteinStructurePanel.updatePanel(protein.getAccession(), protein.getSequence(), proteinPeptides.values());
                } else {
                    proteinStructurePanel.reset();
                }

                if (selectedChildsItems.size() == 1) {
                    Object peptideId = selectedChildsItems.iterator().next();
                    peptideSelection(peptideId);
                    proteinStructurePanel.selectPeptide(peptideId + "");
                } else {
                    peptideSelection(null);
                }

            }

            @Override
            public void updateProteinsMode(String modeType) {
                proteinCoverageContainer.updateProteinsMode(modeType);
                int mode = 1;
                switch (modeType) {
                    case "Validation Status":
                        mode = 2;
                        break;
                    case "Modification  Status":
                        mode = 3;
                        break;
                }
                proteinStructurePanel.setMode(mode);
            }

        };
        middleContainer.addComponent(selectedProteinGraph);
        middleContainer.setExpandRatio(selectedProteinGraph, 60);

        proteinStructurePanel = new ProteinStructurePanel();
        middleContainer.addComponent(proteinStructurePanel);
        middleContainer.setExpandRatio(proteinStructurePanel, 40);

        Selection_Manager.RegistrProteinInformationComponent(ProteinVisulizationLevelContainer.this);

        proteinCoverageContainer = new ProteinCoverageContainer(proteinStructurePanel.getChainCoverageLayout()) {
            @Override
            public void selectPeptide(Object proteinId, Object peptideId) {
                selectedProteinGraph.selectPeptide(proteinId, peptideId);
                peptideSelection(peptideId);
                proteinStructurePanel.selectPeptide(peptideId + "");
            }

        };
        proteinCoverageContainer.setSizeFull();
        container.addComponent(proteinCoverageContainer);
        container.setExpandRatio(proteinCoverageContainer, 49);

    }

    public void selectDataset(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {
        selectedProteinGraph.selectDataset(peptideShakerVisualizationDataset);
    }

    @Override
    public String getFilterId() {
        return "ProteinsPeptide";
    }

    @Override
    public void updateFilterSelection(Set<Comparable> selection, Set<Comparable> selectedCategories, boolean topFilter, boolean singleFilter, boolean selfAction) {

    }

    @Override
    public void selectionChange(String type) {
        if (type.equalsIgnoreCase("protein_selection")) {
            String proteinsId = Selection_Manager.getSelectedProteinId();
            String imgUrl = selectedProteinGraph.updateGraphData(proteinsId);

            proteinCoverageContainer.selectDataset(selectedProteinGraph.getProteinNodes(), selectedProteinGraph.getPeptidesNodes(), selectedProteinGraph.getSelectedProteins(), selectedProteinGraph.getSelectedPeptides());
            if (imgUrl != null) {
                this.proteinoverviewBtn.updateIconResource(new ExternalResource(imgUrl));
            } else {
                this.proteinoverviewBtn.updateIconResource(null);
            }
        }
    }

    @Override
    public void redrawChart() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void suspendFilter(boolean suspend) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void peptideSelection(Object peptideId) {
        if (peptideId == null) {
            Selection_Manager.setSelectedPeptide(null);
            Selection_Manager.setSelection("peptide_selection", new HashSet<>(Arrays.asList(new Comparable[]{null})), null, getFilterId());
        } else {
            System.out.println("it is a selected peptide with id " + peptideId + "   " + proteinPeptides);
            Selection_Manager.setSelectedPeptide(proteinPeptides.get(peptideId.toString()));
            Selection_Manager.setSelection("peptide_selection", new HashSet<>(Arrays.asList(new Comparable[]{peptideId + ""})), null, getFilterId());
        }

    }

    public void reset3DProteinView() {
        proteinStructurePanel.reset();
    }

    public void activate3DProteinView() {
//        proteinStructurePanel.updatePdbMap(selectedProteinGraph.getProteinNodes().keySet());
        proteinStructurePanel.activate3DProteinView();
    }

}
