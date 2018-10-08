package com.uib.web.peptideshaker.presenter;

import com.uib.web.peptideshaker.presenter.core.ViewableFrame;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.DatasetVisulizationLevelContainer;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.ProteinVisulizationLevelContainer;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.SelectionManager;
import com.uib.web.peptideshaker.presenter.core.BigSideBtn;
import com.uib.web.peptideshaker.presenter.core.ButtonWithLabel;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.PeptideVisulizationLevelContainer;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.VerticalLayout;

/**
 * This class represent PeptideShaker view presenter which is responsible for
 * viewing the peptide shaker results on web
 *
 * @author Yehia Farag
 */
public class InteractivePSPRojectResultsPresenter extends VerticalLayout implements ViewableFrame, LayoutEvents.LayoutClickListener {

    /**
     * The small side button (normal size screen).
     */
    private final ButtonWithLabel controlButton;
    /**
     * The main left side buttons container in big screen mode.
     */
    private VerticalLayout viewControlButtonContainer;
    /**
     * The main bottom side buttons container in small/mobile screen mode.
     */
//    private HorizontalLayout bottomSideButtonsContainer;
    /**
     * The first presenter layout (Dataset-protein level visualisation) .
     */
    private DatasetVisulizationLevelContainer datasetVisulizationLevelContainer;
    /**
     * The second presenter layout (Protein-peptides level visualisation) .
     */
    private ProteinVisulizationLevelContainer proteinsVisulizationLevelContainer;
    /**
     * The third presenter layout (Peptide-PSM level visualisation) .
     */
    private PeptideVisulizationLevelContainer peptideVisulizationLevelContainer;
    /**
     * The central selection manager .
     */
    private final SelectionManager Selection_Manager;
    /**
     * Reference index to the last selected sup-view.
     */
    private int lastSelectedBtn = 1;
     /**
     * The view is in maximised mode.
     */
    private boolean maximisedMode;

    /**
     * Constructor to initialise the main layout and attributes.
     */
    public InteractivePSPRojectResultsPresenter() {
        InteractivePSPRojectResultsPresenter.this.setSizeFull();
        InteractivePSPRojectResultsPresenter.this.setStyleName("activelayout");
        this.controlButton = new ButtonWithLabel("Datasets",1);
        this.controlButton.setData(InteractivePSPRojectResultsPresenter.this.getViewId());
        this.controlButton.updateIcon(VaadinIcons.CLUSTER.getHtml());
         this.controlButton.setDescription("View selected dataset");
         this.controlButton.setEnabled(false);
         this.controlButton.addStyleName("orangeiconcolor");
         this.Selection_Manager = new SelectionManager();
        this.initLayout();
        InteractivePSPRojectResultsPresenter.this.minimizeView();
       
//         this.controlButton.addStyleName("hidetopbtn");

    }

    /**
     * Initialise the container layout.
     */
    private void initLayout() {
        this.addStyleName("integratedframe");
        viewControlButtonContainer = new VerticalLayout();
        viewControlButtonContainer.setWidth(100, Unit.PERCENTAGE);
        viewControlButtonContainer.setHeightUndefined();
        viewControlButtonContainer.setSpacing(false);
        viewControlButtonContainer.setMargin(new MarginInfo(false, false, true, false));

        BigSideBtn datasetsOverviewBtn = new BigSideBtn("Dataset overview", 1);
        datasetsOverviewBtn.setData("datasetoverview");
        viewControlButtonContainer.addComponent(datasetsOverviewBtn);
        viewControlButtonContainer.setComponentAlignment(datasetsOverviewBtn, Alignment.MIDDLE_CENTER);
        datasetsOverviewBtn.addLayoutClickListener(InteractivePSPRojectResultsPresenter.this);
        Selection_Manager.addBtnLayout(datasetsOverviewBtn, datasetVisulizationLevelContainer);
        datasetVisulizationLevelContainer = new DatasetVisulizationLevelContainer(Selection_Manager, datasetsOverviewBtn);
        datasetVisulizationLevelContainer.setSizeFull();
        Selection_Manager.addBtnLayout(datasetsOverviewBtn, datasetVisulizationLevelContainer);

        BigSideBtn proteinoverviewBtn = new BigSideBtn("Protein Overview", 2);
        proteinoverviewBtn.updateIconResource(null);
        proteinoverviewBtn.setData("proteinoverview");
        viewControlButtonContainer.addComponent(proteinoverviewBtn);
        viewControlButtonContainer.setComponentAlignment(proteinoverviewBtn, Alignment.MIDDLE_CENTER);
        proteinoverviewBtn.addLayoutClickListener(InteractivePSPRojectResultsPresenter.this);

        proteinsVisulizationLevelContainer = new ProteinVisulizationLevelContainer(Selection_Manager, proteinoverviewBtn);
        Selection_Manager.addBtnLayout(proteinoverviewBtn, proteinsVisulizationLevelContainer);

        BigSideBtn psmoverviewBtn = new BigSideBtn("PSM Overview", 3);
        psmoverviewBtn.updateIconResource(null);
        psmoverviewBtn.setData("psmoverview");
        viewControlButtonContainer.addComponent(psmoverviewBtn);
        viewControlButtonContainer.setComponentAlignment(psmoverviewBtn, Alignment.MIDDLE_CENTER);
        psmoverviewBtn.addLayoutClickListener(InteractivePSPRojectResultsPresenter.this);

        peptideVisulizationLevelContainer = new PeptideVisulizationLevelContainer(Selection_Manager, psmoverviewBtn);
        Selection_Manager.addBtnLayout(psmoverviewBtn, peptideVisulizationLevelContainer);

        VerticalLayout toolViewFrame = new VerticalLayout();
        toolViewFrame.setSizeFull();
        toolViewFrame.setStyleName("viewframe");

        this.addComponent(toolViewFrame);
        this.setExpandRatio(toolViewFrame, 100);

        AbsoluteLayout toolViewFrameContent = new AbsoluteLayout();
        toolViewFrameContent.addStyleName("viewframecontent");
        toolViewFrameContent.setSizeFull();
        toolViewFrame.addComponent(toolViewFrameContent);

        toolViewFrameContent.addComponent(datasetVisulizationLevelContainer);
        toolViewFrameContent.addComponent(proteinsVisulizationLevelContainer);
        toolViewFrameContent.addComponent(peptideVisulizationLevelContainer);
//
//        bottomSideButtonsContainer = new HorizontalLayout();
//        bottomSideButtonsContainer.setHeight(100, Unit.PERCENTAGE);
//        bottomSideButtonsContainer.setWidthUndefined();
//        bottomSideButtonsContainer.setSpacing(true);
//        bottomSideButtonsContainer.setStyleName("bottomsidebtncontainer");
//
//        bottomSideButtonsContainer.addComponent(datasetsOverviewBtn.getMobileModeBtn());
//        bottomSideButtonsContainer.setComponentAlignment(datasetsOverviewBtn.getMobileModeBtn(), Alignment.TOP_CENTER);
        datasetsOverviewBtn.setSelected(true);
//        bottomSideButtonsContainer.addComponent(proteinoverviewBtn.getMobileModeBtn());
//        bottomSideButtonsContainer.setComponentAlignment(proteinoverviewBtn.getMobileModeBtn(), Alignment.TOP_CENTER);
//        bottomSideButtonsContainer.addComponent(psmoverviewBtn.getMobileModeBtn());
//        bottomSideButtonsContainer.setComponentAlignment(psmoverviewBtn.getMobileModeBtn(), Alignment.TOP_CENTER);

    }

    /**
     * Get the main frame layout
     *
     * @return PeptideShaker results view presenter layout
     */
    @Override
    public VerticalLayout getMainView() {
        return this;
    }

    /**
     * Get the small right side button component (represent view control button
     * in large screen mode)
     *
     * @return right view control button
     */
    @Override
    public BigSideBtn getPresenterControlInframeButton() {
        return null;
    }

    /**
     * Get the current view ID
     *
     * @return view id
     */
    @Override
    public String getViewId() {
        return InteractivePSPRojectResultsPresenter.class.getName();
    }

    /**
     * Hide the main view for the current component.
     */
    @Override
    public void minimizeView() {
        controlButton.setSelected(false);
        this.addStyleName("hidepanel");
        this.viewControlButtonContainer.removeStyleName("visible");
        this.maximisedMode=false;

    }

    /**
     * Show the main view for the current component.
     */
    @Override
    public void maximizeView() {
        if (maximisedMode) {
            System.out.println("is selected??");
            return;
        }
        controlButton.setSelected(true);
        this.viewControlButtonContainer.addStyleName("visible");
        this.removeStyleName("hidepanel");
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(500);
                datasetVisulizationLevelContainer.setMargin(false);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });
        t.start();
        datasetVisulizationLevelContainer.setMargin(true);
        this.maximisedMode=true;

    }

    /**
     * Layout click method that is used to coordinate view inside the layout (in
     * case of multiple view under the same presenter).
     *
     * @param event left side button clicked action
     */
    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        BigSideBtn comp = (BigSideBtn) event.getComponent();
        Selection_Manager.selectBtn(comp);
        if (proteinsVisulizationLevelContainer != null) {
            if (comp.getBtnId() == 2 && lastSelectedBtn == 1) {
                proteinsVisulizationLevelContainer.activate3DProteinView();
            } else if (lastSelectedBtn == 1) {
                proteinsVisulizationLevelContainer.reset3DProteinView();
            } else {
                proteinsVisulizationLevelContainer.reset3DProteinView();
            }
        }
        lastSelectedBtn = comp.getBtnId();

    }

    /**
     * Get the left side container for left side big buttons (to be used in case
     * of large screen mode)
     *
     * @return left side buttons container
     */
    @Override
    public VerticalLayout getSubViewButtonsActionContainerLayout() {
        return viewControlButtonContainer;
    }

    @Override
    public ButtonWithLabel getPresenterControlButton() {
        return controlButton;
    }

    /**
     * Activate PeptideShaker dataset visualisation upon user selection
     *
     * @param peptideShakerVisualizationDataset PeptideShaker visualisation
     * dataset
     */
    public void setSelectedDataset(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {
        this.controlButton.setEnabled(peptideShakerVisualizationDataset != null);
        Selection_Manager.reset();
        Selection_Manager.selectBtn(0);
        this.datasetVisulizationLevelContainer.selectDataset(peptideShakerVisualizationDataset);
        this.proteinsVisulizationLevelContainer.selectDataset(peptideShakerVisualizationDataset);
        this.peptideVisulizationLevelContainer.selectDataset(peptideShakerVisualizationDataset);

    }
}
