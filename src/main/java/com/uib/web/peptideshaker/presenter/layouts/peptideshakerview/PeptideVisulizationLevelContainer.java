package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview;

import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PSMObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.presenter.core.BigSideBtn;
import com.uib.web.peptideshaker.presenter.core.filtercharts.charts.RegistrableFilter;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.PSMViewComponent;
import com.uib.web.peptideshaker.presenter.pscomponents.SpectrumInformation;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class represents the layout that contains PeptideShaker datasets
 * overview
 *
 * @author Yehia Farag
 *
 */
public class PeptideVisulizationLevelContainer extends HorizontalLayout implements RegistrableFilter {

    private final VerticalLayout container;
    private final Label headerLabel;
    private final SelectionManager Selection_Manager;
    private final BigSideBtn psmViewBtn;
    private PeptideShakerVisualizationDataset peptideShakerVisualizationDataset;
    private final PSMViewComponent psmViewComponent;

    /**
     * Constructor to initialise the main layout and variables.
     *
     * @param Selection_Manager
     * @param psmViewBtn
     */
    public PeptideVisulizationLevelContainer(SelectionManager Selection_Manager, BigSideBtn psmViewBtn) {
        PeptideVisulizationLevelContainer.this.setSizeFull();
        PeptideVisulizationLevelContainer.this.setSpacing(true);
        PeptideVisulizationLevelContainer.this.setMargin(false);
        PeptideVisulizationLevelContainer.this.setStyleName("psmView");

        this.Selection_Manager = Selection_Manager;
        this.psmViewBtn = psmViewBtn;

        container = new VerticalLayout();
        container.setSizeFull();
        container.setSpacing(false);
        PeptideVisulizationLevelContainer.this.addComponent(container);

        HorizontalLayout topLabelContainer = new HorizontalLayout();
        topLabelContainer.setSizeFull();
        topLabelContainer.addStyleName("minhight30");
        container.addComponent(topLabelContainer);
        container.setExpandRatio(topLabelContainer, 0.01f);

        HorizontalLayout topLeftLabelContainer = new HorizontalLayout();
        topLeftLabelContainer.setWidthUndefined();
        topLeftLabelContainer.setHeight(100, Unit.PERCENTAGE);
        topLabelContainer.addComponent(topLeftLabelContainer);
        headerLabel = new Label();
        headerLabel.setValue("Peptide Spectrum Matches");
        headerLabel.addStyleName("largetitle");
        headerLabel.setWidthUndefined();
        topLeftLabelContainer.setSpacing(true);
        topLeftLabelContainer.addComponent(headerLabel);
        HorizontalLayout middleContainer = new HorizontalLayout();
        middleContainer.setSizeFull();
        middleContainer.setSpacing(true);
        container.addComponent(middleContainer);
        container.setExpandRatio(middleContainer, 1f);

        psmViewComponent = new PSMViewComponent() {
            @Override
            public Map<Object, SpectrumInformation> getSpectrumData(List<PSMObject> psms) {
                return peptideShakerVisualizationDataset.getSelectedSpectrumData(psms, Selection_Manager.getSelectedPeptide());
            }

        };
        psmViewComponent.setThumbImage(this.psmViewBtn.getBtnThumbIconImage());
        middleContainer.addComponent(psmViewComponent);
        Selection_Manager.RegistrProteinInformationComponent(PeptideVisulizationLevelContainer.this);
    }

    public void selectDataset(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {
        this.peptideShakerVisualizationDataset = peptideShakerVisualizationDataset;
    }

    @Override
    public String getFilterId() {
        return "PSM";
    }

    @Override
    public void updateFilterSelection(Set<Comparable> selection, Set<Comparable> selectedCategories, boolean topFilter, boolean singleFilter, boolean selfAction) {

    }

    @Override
    public void selectionChange(String type) {
        if (type.equalsIgnoreCase("peptide_selection")) {
            if (Selection_Manager.getSelectedPeptide() != null) {
                headerLabel.setValue("Peptide Spectrum Matches ( " + Selection_Manager.getSelectedPeptide().getModifiedSequence() + " )");
                this.psmViewComponent.updateView(peptideShakerVisualizationDataset.getPSM(Selection_Manager.getSelectedPeptide().getModifiedSequence()), Selection_Manager.getSelectedPeptide().getTooltip(), Selection_Manager.getSelectedPeptide().getModifiedSequence().length());
//                this.psmViewBtn.updateIconResource(new ThemeResource("img/spectra_1.png"));
            } else {
                headerLabel.setValue("Peptide Spectrum Matches");
                this.psmViewBtn.updateIconResource(null);
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

//    private void peptideSelection(Object peptideId) {
//        System.out.println("at -------- PSM -------- next------- " + peptideId);
//
//    }
//
//    public void reset3DProteinView() {
//        proteinStructurePanel.reset();
//    }
//
//    public void activate3DProteinView() {
//        
//        proteinStructurePanel.updatePdbMap(selectedProteinGraph.getProteinNodes().keySet());
//        proteinStructurePanel.activate3DProteinView();
//    }
}
