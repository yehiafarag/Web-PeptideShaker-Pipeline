package com.uib.web.peptideshaker.presenter.components.peptideshakerview;

import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.presenter.components.peptideshakerview.components.ProteinCoverageContainer;
import com.uib.web.peptideshaker.presenter.components.peptideshakerview.components.ProteinsPeptidesGraphComponent;
import com.uib.web.peptideshaker.presenter.core.BigSideBtn;
import com.uib.web.peptideshaker.presenter.core.filtercharts.charts.RegistrableFilter;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
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
    private final ProteinCoverageContainer proteinCoverageComponent;

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

        selectedProteinGraph = new ProteinsPeptidesGraphComponent() {
            @Override
            public void selectedItem(Set<Object> selectedItems, Set<Object> selectedChildsItems) {
                proteinCoverageComponent.setSelectedItems(selectedItems, selectedChildsItems);
                if (selectedChildsItems.size() == 1) {
                    peptideSelection(selectedChildsItems.iterator().next());
                }
            }

            @Override
            public void updateProteinsMode(String modeType) {
                proteinCoverageComponent.updateProteinsMode(modeType);
            }

        };
        container.addComponent(selectedProteinGraph);
        container.setExpandRatio(selectedProteinGraph, 49);
        Selection_Manager.RegistrProteinInformationComponent(ProteinVisulizationLevelContainer.this);

        proteinCoverageComponent = new ProteinCoverageContainer() {
            @Override
            public void selectPeptide(Object proteinId, Object peptideId) {
                selectedProteinGraph.selectPeptide(proteinId, peptideId);
                peptideSelection(peptideId);
            }

        };
        proteinCoverageComponent.setSizeFull();
        container.addComponent(proteinCoverageComponent);
        container.setExpandRatio(proteinCoverageComponent, 49);

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
        proteinCoverageComponent.selectDataset(selectedProteinGraph.getProteinNodes(), selectedProteinGraph.getPeptidesNodes(), selectedProteinGraph.getSelectedProteins(), selectedProteinGraph.getSelectedPeptides());
        if (imgUrl != null) {
            this.proteinoverviewBtn.updateIcon(new ExternalResource(imgUrl));
        } else {
            this.proteinoverviewBtn.updateIcon(null);
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
        System.out.println("at -------- PSM -------- next------- " + peptideId);

    }

}
