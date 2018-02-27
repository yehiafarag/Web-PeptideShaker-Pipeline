package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview;

import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideObject;
import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.galaxy.dataobjects.ProteinObject;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.ProteinCoverageContainer;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.ProteinStructurePanel;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.ProteinsPeptidesGraphComponent;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.coverage.ProteinCoverageComponent;
import com.uib.web.peptideshaker.presenter.core.BigSideBtn;
import com.uib.web.peptideshaker.presenter.core.filtercharts.charts.RegistrableFilter;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import java.util.LinkedHashSet;
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

    /**
     * Constructor to initialize the main layout and variables.
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
                if (selectedChildsItems.size() == 1) {
                    peptideSelection(selectedChildsItems.iterator().next());
                }
                if (selectedItems.size() == 1) {
                    ProteinObject protien = this.getProteinNodes().get((String) selectedItems.iterator().next());
                    Set<PeptideObject> proteinPeptides = new LinkedHashSet<>();
                    this.getPeptidesNodes().values().stream().filter((peptide) -> (peptide.getProteinsSet().contains(protien.getAccession()))).forEachOrdered((peptide) -> {
                        proteinPeptides.add(peptide);
                    });
                    proteinStructurePanel.updatePanel(protien.getAccession(), protien.getSequence(), proteinPeptides);
                } else {
                    proteinStructurePanel.reset();
                }

            }

            @Override
            public void updateProteinsMode(String modeType) {
                proteinCoverageContainer.updateProteinsMode(modeType);
                proteinStructurePanel.setMode(!modeType.equalsIgnoreCase("Validation Status"));
            }

        };
        middleContainer.addComponent(selectedProteinGraph);
        middleContainer.setExpandRatio(selectedProteinGraph, 60);

        proteinStructurePanel = new ProteinStructurePanel();
        middleContainer.addComponent(proteinStructurePanel);
        middleContainer.setExpandRatio(proteinStructurePanel, 40);

        Selection_Manager.RegistrProteinInformationComponent(ProteinVisulizationLevelContainer.this);

        proteinCoverageContainer = new ProteinCoverageContainer() {
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
        String proteinsId = Selection_Manager.getSelectedProteinId();
        String imgUrl = selectedProteinGraph.updateGraphData(proteinsId);
//        proteinStructurePanel.updatePdbMap(selectedProteinGraph.getProteinNodes().keySet());
        proteinCoverageContainer.selectDataset(selectedProteinGraph.getProteinNodes(), selectedProteinGraph.getPeptidesNodes(), selectedProteinGraph.getSelectedProteins(), selectedProteinGraph.getSelectedPeptides());

        if (imgUrl != null) {
            this.proteinoverviewBtn.updateIcon(new ExternalResource(imgUrl));
        } else {
            this.proteinoverviewBtn.updateIcon(null);
        }
//        proteinStructurePanel.updatePanel(selectedProteinGraph.getSelectedProteins());
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
        System.out.println("at -------- PSM -------- next------- " + peptideId);

    }

    public void reset3DProteinView() {
        proteinStructurePanel.reset();
    }

    public void activate3DProteinView() {
        proteinStructurePanel.activate3DProteinView();
    }

}
