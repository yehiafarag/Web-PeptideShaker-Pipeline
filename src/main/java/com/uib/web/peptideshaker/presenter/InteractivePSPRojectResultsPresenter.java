package com.uib.web.peptideshaker.presenter;

import com.uib.web.peptideshaker.presenter.core.ViewableFrame;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.DatasetVisulizationLevelContainer;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.ProteinVisulizationLevelContainer;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.SelectionManager;
import com.uib.web.peptideshaker.presenter.core.BigSideBtn;
import com.uib.web.peptideshaker.presenter.core.ButtonWithLabel;
import com.uib.web.peptideshaker.presenter.core.SmallSideBtn;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.PeptideVisulizationLevelContainer;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.VerticalLayout;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

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
     * The small side button (normal size screen).
     */
    private final SmallSideBtn smallControlButton;
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

        this.smallControlButton = new SmallSideBtn(VaadinIcons.CLUSTER);
        smallControlButton.updateIconURL("img/venn.png");
        smallControlButton.setDescription("Selected projects");
        this.smallControlButton.setData(InteractivePSPRojectResultsPresenter.this.getViewId());
        this.smallControlButton .addStyleName("resultsmallbtn");

        this.controlButton = new ButtonWithLabel("Results</br><font>Interactive results visualization</font>", 1);
        this.controlButton.setData(InteractivePSPRojectResultsPresenter.this.getViewId());
        this.controlButton.updateIcon(VaadinIcons.CLUSTER.getHtml());
        this.controlButton.updateIconResource(new ThemeResource("img/venn.png"));
        this.controlButton.setDescription("Selected projects");
        this.controlButton.setEnabled(false);
        this.controlButton.addStyleName("orangeiconcolor");
        this.controlButton .addStyleName("resultsbtn");
        this.smallControlButton.setEnabled(false);
        this.Selection_Manager = new SelectionManager();
        this.initLayout();
        InteractivePSPRojectResultsPresenter.this.minimizeView();
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
        datasetsOverviewBtn.addStyleName("dsoverviewbtn");
        datasetsOverviewBtn.setData("datasetoverview");
        datasetsOverviewBtn.setDescription("Dataset Overview");
        viewControlButtonContainer.addComponent(datasetsOverviewBtn);
        viewControlButtonContainer.setComponentAlignment(datasetsOverviewBtn, Alignment.MIDDLE_CENTER);
        datasetsOverviewBtn.addLayoutClickListener(InteractivePSPRojectResultsPresenter.this);
        datasetVisulizationLevelContainer = new DatasetVisulizationLevelContainer(Selection_Manager, datasetsOverviewBtn);
        datasetVisulizationLevelContainer.setSizeFull();
        Selection_Manager.addBtnLayout(datasetsOverviewBtn, datasetVisulizationLevelContainer);

        BigSideBtn proteinoverviewBtn = new BigSideBtn("Protein Overview", 2);
        proteinoverviewBtn.setDescription("Protein Overview");
        proteinoverviewBtn.updateIconResource(null);
        proteinoverviewBtn.setData("proteinoverview");
        proteinoverviewBtn.addStyleName("proteinoverviewbtn");
        viewControlButtonContainer.addComponent(proteinoverviewBtn);
        viewControlButtonContainer.setComponentAlignment(proteinoverviewBtn, Alignment.MIDDLE_CENTER);
        proteinoverviewBtn.addLayoutClickListener(InteractivePSPRojectResultsPresenter.this);

        proteinsVisulizationLevelContainer = new ProteinVisulizationLevelContainer(Selection_Manager, proteinoverviewBtn);
        Selection_Manager.addBtnLayout(proteinoverviewBtn, proteinsVisulizationLevelContainer);

        BigSideBtn psmoverviewBtn = new BigSideBtn("PSM Overview", 3);
        psmoverviewBtn.updateIconResource(null);
        psmoverviewBtn.setDescription("Peptide Spectrum Matches");
        psmoverviewBtn.setData("psmoverview");
        psmoverviewBtn.addStyleName("psmoverviewbtn");
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
    public SmallSideBtn getSmallPresenterControlButton() {
        return smallControlButton;
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
        smallControlButton.setSelected(false);
        this.addStyleName("hidepanel");
        this.viewControlButtonContainer.removeStyleName("visible");
        this.maximisedMode = false;

    }

    /**
     * Show the main view for the current component.
     */
    @Override
    public void maximizeView() {
        if (maximisedMode) {
            return;
        }
        smallControlButton.setSelected(true);
        controlButton.setSelected(true);
        datasetVisulizationLevelContainer.setMargin(new MarginInfo(false, false, false, false));
        this.maximisedMode = true;
        this.viewControlButtonContainer.addStyleName("visible");
        while (!dataprocessFuture.isDone()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {

            }
        }
        this.removeStyleName("hidepanel");

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
            if (comp.getBtnId() == 2 && lastSelectedBtn != 2) {
                proteinsVisulizationLevelContainer.activate3DProteinView();
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
    public ButtonWithLabel getLargePresenterControlButton() {
        return controlButton;
    }
    private Future dataprocessFuture;
    private long start;

    /**
     * Activate PeptideShaker dataset visualisation upon user selection
     *
     * @param peptideShakerVisualizationDataset PeptideShaker visualisation
     * dataset
     */
    public void setSelectedDataset(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {
        start = System.currentTimeMillis();

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Runnable runnableTask = () -> {
            controlButton.setEnabled(peptideShakerVisualizationDataset != null);
            smallControlButton.setEnabled(peptideShakerVisualizationDataset != null);
            Selection_Manager.reset();
            Selection_Manager.selectBtn(0);

        };
        Runnable runnableTask1 = () -> {
            controlButton.setEnabled(peptideShakerVisualizationDataset != null);
            smallControlButton.setEnabled(peptideShakerVisualizationDataset != null);
            Selection_Manager.reset();
            Selection_Manager.selectBtn(0);
                datasetVisulizationLevelContainer.selectDataset(peptideShakerVisualizationDataset);
           
        };
        Runnable runnableTask2 = () -> {

            proteinsVisulizationLevelContainer.selectDataset(peptideShakerVisualizationDataset);

        };
        Runnable runnableTask3 = () -> {
            peptideVisulizationLevelContainer.selectDataset(peptideShakerVisualizationDataset);

        };
        dataprocessFuture = executorService.submit(runnableTask1);
        executorService.submit(runnableTask);
        executorService.submit(runnableTask2);
        executorService.submit(runnableTask3);
        executorService.shutdown();

    }
}
