package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview;

import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.ProteinGroupObject;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.ProteinCoverageTable;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.Protein3DStructurePanel;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.GraphsContainerComponent;
import com.uib.web.peptideshaker.presenter.core.BigSideBtn;
import com.uib.web.peptideshaker.presenter.core.filtercharts.charts.RegistrableFilter;
import com.uib.web.peptideshaker.presenter.core.filtercharts.components.RangeColorGenerator;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
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
 * This class represents the layout that contains selected proteins overview
 *
 * @author Yehia Farag
 */
public class ProteinVisulizationLevelContainer extends HorizontalLayout implements RegistrableFilter {

    private final AbsoluteLayout container;
    private final Label headerLabel;
    private final GraphsContainerComponent graphsContainerComponent;
    private final SelectionManager Selection_Manager;
    private final BigSideBtn proteinoverviewBtn;
    private final ProteinCoverageTable proteinCoverageContainer;
    private final Protein3DStructurePanel protein3DStructurePanel;
    private Map<String, PeptideObject> proteinPeptides;
    private RangeColorGenerator colorScale;
    private boolean specificPeptideSelection;

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
        ProteinVisulizationLevelContainer.this.setStyleName("transitionallayout");
        this.Selection_Manager = Selection_Manager;
        this.proteinoverviewBtn = proteinoverviewBtn;

        container = new AbsoluteLayout();
        container.setSizeFull();
        ProteinVisulizationLevelContainer.this.addComponent(container);

        HorizontalLayout topLabelContainer = new HorizontalLayout();
        topLabelContainer.setHeight(30, Unit.PIXELS);
        topLabelContainer.setWidth(100, Unit.PERCENTAGE);
        container.addComponent(topLabelContainer);

        HorizontalLayout topLeftLabelContainer = new HorizontalLayout();
        topLeftLabelContainer.setWidthUndefined();
        topLeftLabelContainer.setHeight(100, Unit.PERCENTAGE);
        topLabelContainer.addComponent(topLeftLabelContainer);
        headerLabel = new Label();
        headerLabel.setValue("Protein overview");
        headerLabel.addStyleName("largetitle");
        headerLabel.setWidthUndefined();
        topLeftLabelContainer.setSpacing(true);
        topLeftLabelContainer.addComponent(headerLabel);

        Label commentLabel = new Label("<i style='padding-right: 50px;top: 3px !important;position: relative;'>* Click in the graph or table to select proteins and peptides</i>", ContentMode.HTML);
        commentLabel.setWidthUndefined();
        commentLabel.setStyleName("resizeabletext");
        commentLabel.addStyleName("margintop10");
        topLabelContainer.addComponent(commentLabel);
        topLabelContainer.setComponentAlignment(commentLabel, Alignment.TOP_RIGHT);

        VerticalLayout subContainer = new VerticalLayout();
        subContainer.setSizeFull();
        subContainer.setSpacing(true);
        container.addComponent(subContainer, "left:0px; top:30px;");

        HorizontalLayout middleContainer = new HorizontalLayout();
        middleContainer.addStyleName("extendwidthstyle");
        middleContainer.setHeight(100, Unit.PERCENTAGE);
        middleContainer.setWidth(100, Unit.PERCENTAGE);

        middleContainer.setSpacing(true);
        subContainer.addComponent(middleContainer);

        graphsContainerComponent = new GraphsContainerComponent() {
            @Override
            public void selectedItem(Set<Object> selectedItems, Set<Object> selectedChildsItems, boolean isProteform) {
                if (selectedItems.size() == 1) {
                    headerLabel.setValue("Protein overview (" + graphsContainerComponent.getProteinName(selectedItems.iterator().next().toString())+")");
                } else {
                    headerLabel.setValue("Protein overview ");
                }

                if (specificPeptideSelection) {
                    return;
                }
                proteinCoverageContainer.setSelectedItems(selectedItems, selectedChildsItems);
                if (isProteform) {
                    return;
                }
                if (selectedItems.size() == 1) {
                    ProteinGroupObject protein = this.getProteinNodes().get((String) selectedItems.iterator().next());
                    proteinPeptides = new LinkedHashMap<>();
                    this.getPeptidesNodes().values().stream().filter((peptide) -> (peptide.getProteinsSet().contains(protein.getAccession()))).forEachOrdered((peptide) -> {
                        proteinPeptides.put(peptide.getModifiedSequence(), peptide);
                    });

                    protein3DStructurePanel.updatePanel(protein.getAccession(), protein.getSequence(), proteinPeptides.values());
                } else {
                    protein3DStructurePanel.reset();
                }

                if (selectedChildsItems.size() == 1) {
                    Object peptideId = selectedChildsItems.iterator().next();
                    Object proteinId = selectedItems.iterator().next();
                    peptideSelection(peptideId, proteinId);
                    protein3DStructurePanel.selectPeptide(peptideId + "");
                } else {
                    peptideSelection(null, null);
                }

            }

            @Override
            public void updateProteinsMode(String modeType) {
                proteinCoverageContainer.updateProteinsMode(modeType);
                int mode = 1;
                switch (modeType) {
                    case "Validation":
                        mode = 2;
                        break;
                    case "Modification":
                        mode = 3;
                        break;
                    case "Proteoform":
                        mode = 3;
                        break;
                    case "Intensity":
                        mode = 5;
                        break;
                    case "PSMNumber":
                        mode = 4;
                        break;
                }
                protein3DStructurePanel.setMode(mode);
            }

        };
        middleContainer.addComponent(graphsContainerComponent);
        middleContainer.setExpandRatio(graphsContainerComponent, 60);

        protein3DStructurePanel = new Protein3DStructurePanel();
        middleContainer.addComponent(protein3DStructurePanel);
        middleContainer.setExpandRatio(protein3DStructurePanel, 40);

        Selection_Manager.RegistrProteinInformationComponent(ProteinVisulizationLevelContainer.this);

        proteinCoverageContainer = new ProteinCoverageTable(protein3DStructurePanel.getChainCoverageLayout()) {
            @Override
            public void selectPeptide(Object proteinId, Object peptideId) {
                specificPeptideSelection = true;
                peptideSelection(peptideId, proteinId);
                protein3DStructurePanel.selectPeptide(peptideId + "");
                graphsContainerComponent.selectPeptide(proteinId, peptideId);
            }
        };
        proteinCoverageContainer.setSizeFull();
        subContainer.addComponent(proteinCoverageContainer);

    }

    public void selectDataset(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {
        graphsContainerComponent.selectDataset(peptideShakerVisualizationDataset);
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
            String imgUrl = graphsContainerComponent.updateGraphData(proteinsId);
            this.colorScale = graphsContainerComponent.getColorScale();
            proteinCoverageContainer.selectDataset(graphsContainerComponent.getProteinNodes(), graphsContainerComponent.getPeptidesNodes(), graphsContainerComponent.getSelectedProteins(), graphsContainerComponent.getSelectedPeptides(), colorScale, graphsContainerComponent.isQuantDataSet());
            if (imgUrl != null) {
                this.proteinoverviewBtn.updateIconResource(new ExternalResource(imgUrl));
            } else {
                this.proteinoverviewBtn.updateIconResource(null);
            }
        }
        specificPeptideSelection = false;
    }

    @Override
    public void redrawChart() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void suspendFilter(boolean suspend) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void peptideSelection(Object peptideId, Object proteinId) {

        if (peptideId == null) {
            Selection_Manager.setSelectedPeptide(null);
            Selection_Manager.setSelection("peptide_selection", new HashSet<>(Arrays.asList(new Comparable[]{null})), null, getFilterId());
        } else {
            if (proteinPeptides == null || proteinPeptides.get(peptideId.toString()) == null) {
                proteinPeptides = new LinkedHashMap<>();
                ProteinGroupObject protein = graphsContainerComponent.getProteinNodes().get((String) proteinId);
                graphsContainerComponent.getPeptidesNodes().values().stream().filter((peptide) -> (peptide.getProteinsSet().contains(protein.getAccession()))).forEachOrdered((peptide) -> {
                    proteinPeptides.put(peptide.getModifiedSequence(), peptide);
                });
            }
            Selection_Manager.setSelectedPeptide(proteinPeptides.get(peptideId.toString()));
            Selection_Manager.setSelection("peptide_selection", new HashSet<>(Arrays.asList(new Comparable[]{peptideId + ""})), null, getFilterId());
        }

    }

    public void reset3DProteinView() {
    }

    public void activate3DProteinView() {
        protein3DStructurePanel.activate3DProteinView();
        graphsContainerComponent.updateMode();
    }

}
