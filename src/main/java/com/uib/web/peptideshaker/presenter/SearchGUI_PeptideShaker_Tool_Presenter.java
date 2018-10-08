package com.uib.web.peptideshaker.presenter;

import com.uib.web.peptideshaker.presenter.core.ViewableFrame;
import com.compomics.util.experiment.identification.identification_parameters.SearchParameters;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyTransferableFile;
import com.uib.web.peptideshaker.presenter.layouts.SearchGUIPeptideShakerWorkFlowInputLayout;
import com.uib.web.peptideshaker.presenter.core.BigSideBtn;
import com.uib.web.peptideshaker.presenter.core.ButtonWithLabel;
import com.uib.web.peptideshaker.presenter.core.SmallSideBtn;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.LayoutEvents.LayoutClickNotifier;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.VerticalLayout;
import java.util.Map;
import java.util.Set;

/**
 * This class represent web tool presenter which is responsible for managing the
 * view and interactivity of the tool
 *
 * @author Yehia Farag
 */
public abstract class SearchGUI_PeptideShaker_Tool_Presenter extends VerticalLayout implements ViewableFrame, LayoutEvents.LayoutClickListener {

    /**
     * The tools layout side button.
     */
    private final ButtonWithLabel controlButton;
    /**
     * The work-flow input form layout container.
     */
    private SearchGUIPeptideShakerWorkFlowInputLayout peptideshakerToolInputForm;
    /**
     * The work-flow side button container (left side button container).
     */
    private VerticalLayout btnContainer;

    /**
     * Initialise the web tool main attributes
     *
     */
    public SearchGUI_PeptideShaker_Tool_Presenter() {
        SearchGUI_PeptideShaker_Tool_Presenter.this.setSizeFull();
        SearchGUI_PeptideShaker_Tool_Presenter.this.setStyleName("activelayout");
        SearchGUI_PeptideShaker_Tool_Presenter.this.addStyleName("integratedframe");

        this.controlButton = new ButtonWithLabel("SearchGUI & PeptideShaker",1);//spectra2.png
        this.controlButton.setData(SearchGUI_PeptideShaker_Tool_Presenter.this.getViewId());
        this.controlButton.updateIconResource(new ThemeResource("img/searchgui-medium-shadow-2.png"));//img/workflow3.png

        this.initLayout();
        SearchGUI_PeptideShaker_Tool_Presenter.this.minimizeView();
        this.controlButton.setDescription("Run SearchGUI & PeptideShaker tool");
//         this.controlButton.addStyleName("hidetopbtn");

    }

    /**
     * Update the work-flow input files
     *
     *
     * @param searchSettingsMap search settings .par files map
     * @param fastaFilesMap The main FASTA File Map (ID to Name).
     * @param mgfFilesMap The main MGF File Map (ID to Name).
     */
    public void updatePeptideShakerToolInputForm(Map<String, GalaxyTransferableFile> searchSettingsMap, Map<String, GalaxyFileObject> fastaFilesMap, Map<String, GalaxyFileObject> mgfFilesMap) {
        peptideshakerToolInputForm.updateForm(searchSettingsMap, fastaFilesMap, mgfFilesMap);
    }

    /**
     * Initialise the main forms for user data input that is required for
     * performing search.
     */
    private void initLayout() {

        btnContainer = new VerticalLayout();
        btnContainer.setWidth(100, Unit.PERCENTAGE);
        btnContainer.setHeightUndefined();
        btnContainer.setSpacing(true);
        btnContainer.setMargin(new MarginInfo(false, false, true, false));
        btnContainer.addStyleName("singlebtn");

        peptideshakerToolInputForm = new SearchGUIPeptideShakerWorkFlowInputLayout() {
            @Override
            public void executeWorkFlow(String projectName, String fastaFileId, Set<String> mgfIdsList, Set<String> searchEnginesList, SearchParameters searchParam) {
                SearchGUI_PeptideShaker_Tool_Presenter.this.execute_SearchGUI_PeptideShaker_WorkFlow(projectName, fastaFileId, mgfIdsList, searchEnginesList, searchParam);
            }

            @Override
            public Map<String, GalaxyTransferableFile> saveSearchGUIParameters(SearchParameters searchParameters, boolean isNEw) {
                return SearchGUI_PeptideShaker_Tool_Presenter.this.saveSearchGUIParameters(searchParameters, isNEw);
            }

        };

        BigSideBtn workFlowBtn = new BigSideBtn("Work-Flow", 2);
        workFlowBtn.updateIconResource(new ThemeResource("img/workflow3.png"));
        workFlowBtn.setData("workflow");
        workFlowBtn.addStyleName("zeropadding");
        btnContainer.addComponent(workFlowBtn);
        btnContainer.setComponentAlignment(workFlowBtn, Alignment.TOP_CENTER);
        workFlowBtn.addLayoutClickListener(SearchGUI_PeptideShaker_Tool_Presenter.this);
        workFlowBtn.setSelected(true);

        VerticalLayout toolViewFrame = new VerticalLayout();
        toolViewFrame.setSizeFull();
        toolViewFrame.setStyleName("viewframe");

        this.addComponent(toolViewFrame);
        this.setExpandRatio(toolViewFrame, 100);

        AbsoluteLayout toolViewFrameContent = new AbsoluteLayout();
        toolViewFrameContent.addStyleName("viewframecontent");
        toolViewFrameContent.setSizeFull();
        toolViewFrame.addComponent(toolViewFrameContent);
        toolViewFrameContent.addComponent(peptideshakerToolInputForm);

    }

    /**
     *
     * @return
     */
    @Override
    public VerticalLayout getMainView() {
        return this;
    }

    /**
     *
     * @return
     */
    @Override
    public BigSideBtn getPresenterControlInframeButton() {
        return null;
    }
     @Override
    public ButtonWithLabel getPresenterControlButton() {
        return controlButton;
    }

    /**
     *
     * @return
     */
    @Override
    public String getViewId() {
        return SearchGUI_PeptideShaker_Tool_Presenter.class.getName();
    }

    /**
     *
     */
    @Override
    public void minimizeView() {
        controlButton.setSelected(false);
        this.addStyleName("hidepanel");
        this.btnContainer.removeStyleName("visible");

    }

    /**
     *
     */
    @Override
    public void maximizeView() {
        controlButton.setSelected(true);
        this.btnContainer.addStyleName("visible");
        this.removeStyleName("hidepanel");
    }

    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {

    }

    /**
     *
     * @return
     */
    @Override
    public VerticalLayout getSubViewButtonsActionContainerLayout() {
        return btnContainer;
    }



    /**
     * Run Online Peptide-Shaker work-flow
     *
     * @param fastaFileId FASTA file dataset id
     * @param mgfIdsList list of MGF file dataset ids
     * @param searchEnginesList List of selected search engine names
     * @param historyId galaxy history id that will store the results
     */
    public abstract void execute_SearchGUI_PeptideShaker_WorkFlow(String projectName, String fastaFileId, Set<String> mgfIdsList, Set<String> searchEnginesList, SearchParameters searchParam);

    /**
     * Save search settings file into galaxy
     *
     *
     * @param searchParameters searchParameters .par file
     * @param isNew is new search parameter file
     * @return updated search parameters file list
     */
    public abstract Map<String, GalaxyTransferableFile> saveSearchGUIParameters(SearchParameters searchParameters, boolean isNew);

}
