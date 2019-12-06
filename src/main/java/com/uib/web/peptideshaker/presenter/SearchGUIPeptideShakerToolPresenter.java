package com.uib.web.peptideshaker.presenter;

import com.compomics.util.parameters.identification.IdentificationParameters;
import com.google.common.collect.HashBiMap;
import com.uib.web.peptideshaker.presenter.core.ViewableFrame;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyTransferableFile;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;

import com.uib.web.peptideshaker.presenter.layouts.SearchGUIPeptideShakerWorkFlowInputLayout;
import com.uib.web.peptideshaker.presenter.core.BigSideBtn;
import com.uib.web.peptideshaker.presenter.core.ButtonWithLabel;
import com.uib.web.peptideshaker.presenter.core.SmallSideBtn;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.VerticalLayout;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import pl.exsio.plupload.PluploadFile;

/**
 * This class represent web tool presenter which is responsible for managing the
 * view and interactivity of the tool
 *
 * @author Yehia Farag
 */
public abstract class SearchGUIPeptideShakerToolPresenter extends VerticalLayout implements ViewableFrame, LayoutEvents.LayoutClickListener {

    /**
     * The tools layout side button.
     */
    protected SmallSideBtn smallControlButton;
    /**
     * The tools layout side button.
     */
    protected ButtonWithLabel controlButton;
    /**
     * The work-flow input form layout container.
     */
    protected SearchGUIPeptideShakerWorkFlowInputLayout peptideshakerToolInputForm;
    /**
     * The work-flow side button container (left side button container).
     */
    protected VerticalLayout btnContainer;

    /**
     * Initialise the web tool main attributes
     *
     */
    public SearchGUIPeptideShakerToolPresenter() {
        SearchGUIPeptideShakerToolPresenter.this.setSizeFull();
        SearchGUIPeptideShakerToolPresenter.this.setStyleName("activelayout");
        SearchGUIPeptideShakerToolPresenter.this.addStyleName("integratedframe");
    }

    /**
     * Update the work-flow input files
     *
     *
     * @param searchSettingFilesMap Search settings .par files map
     * @param fastaFilesMap The main FASTA File Map (ID to Name).
     * @param mgfFilesMap The main MGF File Map (ID to Name).
     * @param rawFilesMap The main Raw file map (ID to Name).
     */
    public void updatePeptideShakerToolInputForm(Map<String, GalaxyTransferableFile> searchSettingFilesMap, Map<String, GalaxyFileObject> fastaFilesMap, Map<String, GalaxyFileObject> mgfFilesMap, Map<String, GalaxyFileObject> rawFilesMap) {
//        peptideshakerToolInputForm.updateForm(searchSettingFilesMap, fastaFilesMap, mgfFilesMap, rawFilesMap, new HashMap<>());
    }

    /**
     * Initialise the main forms for user data input that is required for
     * performing search.
     */
    public void initLayout() {
        smallControlButton = new SmallSideBtn("img/searchguiblue.png");//spectra2.pngimg/searchgui-medium-shadow-2.png
        smallControlButton.setData(SearchGUIPeptideShakerToolPresenter.this.getViewId());
        smallControlButton.setDescription("Search and process data (SearchGUI and PeptideShaker)");
        smallControlButton.addStyleName("smalltoolsbtn");
         smallControlButton.addStyleName("searchguiicon");
        controlButton = new ButtonWithLabel("Analyze Data</br><font>Search and process data</font>", 1);//spectra2.png
        controlButton.setData(SearchGUIPeptideShakerToolPresenter.this.getViewId());
        controlButton.updateIconResource(new ThemeResource("img/searchguiblue.png"));//img/workflow3.png
        controlButton.addStyleName("searchguiicon");
        btnContainer = new VerticalLayout();
        btnContainer.setWidth(100, Unit.PERCENTAGE);
        btnContainer.setHeightUndefined();
        btnContainer.setSpacing(true);
        btnContainer.setMargin(new MarginInfo(false, false, true, false));
        btnContainer.addStyleName("singlebtn");

        peptideshakerToolInputForm = new SearchGUIPeptideShakerWorkFlowInputLayout() {
            @Override
            public void executeWorkFlow(String projectName, String fastaFileId, String searchParameterFileId, Set<String> inputFilesIdsList, Set<String> searchEnginesList, IdentificationParameters searchParam, boolean quant) {
                SearchGUIPeptideShakerToolPresenter.this.execute_SearchGUI_PeptideShaker_WorkFlow(projectName, fastaFileId, searchParameterFileId, inputFilesIdsList, searchEnginesList, searchParam, quant);

            }

            @Override
            public boolean uploadToGalaxy(PluploadFile[] toUploadFiles) {
                return SearchGUIPeptideShakerToolPresenter.this.uploadToGalaxy(toUploadFiles);
            }

            @Override
            public Map<String, GalaxyTransferableFile> saveSearchGUIParameters(IdentificationParameters searchParameters, boolean isNEw) {
                return SearchGUIPeptideShakerToolPresenter.this.saveSearchGUIParameters(searchParameters, isNEw);
            }

        };
        peptideshakerToolInputForm.initLayout();

        BigSideBtn workFlowBtn = new BigSideBtn("Work-Flow", 2);
        workFlowBtn.updateIconResource(new ThemeResource("img/searchguiblue.png"));
        workFlowBtn.addStyleName("searchguiicon");
        workFlowBtn.addStyleName("padding20");
        workFlowBtn.setData("workflow");
        btnContainer.addComponent(workFlowBtn);
        btnContainer.setComponentAlignment(workFlowBtn, Alignment.TOP_CENTER);
        workFlowBtn.addLayoutClickListener(SearchGUIPeptideShakerToolPresenter.this);
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

        SearchGUIPeptideShakerToolPresenter.this.minimizeView();
        this.controlButton.setDescription("Search and process data (SearchGUI and PeptideShaker)");
//         this.controlButton.addStyleName("hidetopbtn");
    }

    /**
     * Update Online PeptideShaker files from Galaxy Server
     *
     * @param historyFilesMap List of available files on Galaxy Server
     * @param jobInProgress Jobs are running
     */
    public void updateSystemData(Map<String, GalaxyFileObject> historyFilesMap) {

        if (historyFilesMap != null) {
            Map<String, GalaxyTransferableFile> searchSettingFilesMap = new LinkedHashMap<>();
            Map<String, GalaxyFileObject> fastaFilesMap = new LinkedHashMap<>();
            Map<String, GalaxyFileObject> mgfFilesMap = new LinkedHashMap<>();
            Map<String, GalaxyFileObject> rawFilesMap = new LinkedHashMap<>();
//            Map<String, PeptideShakerVisualizationDataset> psDatasetFilesMap = new LinkedHashMap<>();
            for (String fileKey : historyFilesMap.keySet()) {
                GalaxyFileObject fileObject = historyFilesMap.get(fileKey);
                String type = fileObject.getType();
                switch (type) {
                    case "MGF":
                        mgfFilesMap.put(fileKey, fileObject);
                        break;
                    case "FASTA":
                        fastaFilesMap.put(fileKey, fileObject);
                        break;

                    case "Parameters File (JSON)":
                        searchSettingFilesMap.put(fileKey, (GalaxyTransferableFile) fileObject);
                        break;

                    case "Thermo.raw":
                        rawFilesMap.put(fileKey, fileObject);
                        break;
//                    case "Web Peptide Shaker Dataset":
//                        psDatasetFilesMap.put(fileKey, (PeptideShakerVisualizationDataset) fileObject);
//                        break;
                }

            }
            peptideshakerToolInputForm.updateForm(searchSettingFilesMap, fastaFilesMap, mgfFilesMap, rawFilesMap);
        }
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
    public SmallSideBtn getSmallPresenterControlButton() {
        return smallControlButton;
    }

    @Override
    public ButtonWithLabel getLargePresenterControlButton() {
        return controlButton;
    }

    /**
     *
     * @return
     */
    @Override
    public String getViewId() {
        return SearchGUIPeptideShakerToolPresenter.class.getName();
    }

    /**
     *
     */
    @Override
    public void minimizeView() {
        smallControlButton.setSelected(false);
        controlButton.setSelected(false);
        this.addStyleName("hidepanel");
        this.btnContainer.removeStyleName("visible");

    }

    /**
     *
     */
    @Override
    public void maximizeView() {

        smallControlButton.setSelected(true);
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
     * @param projectName The project name
     * @param fastaFileId FASTA file dataset id
     * @param searchParameterFileId .par file id
     * @param mgfIdsList list of MGF file dataset ids
     * @param searchEnginesList List of selected search engine names
     */
    public abstract void execute_SearchGUI_PeptideShaker_WorkFlow(String projectName, String fastaFileId, String searchParameterFileId, Set<String> mgfIdsList, Set<String> searchEnginesList, IdentificationParameters searchParam, boolean quant);

    /**
     * Save search settings file into galaxy
     *
     *
     * @param searchParameters searchParameters .par file
     * @param isNew is new search parameter file
     * @return updated search parameters file list
     */
    public abstract Map<String, GalaxyTransferableFile> saveSearchGUIParameters(IdentificationParameters searchParameters, boolean isNew);

    /**
     * upload file into galaxy
     *
     *
     * @param toUploadFiles files to be uploaded to galaxy
     * @return updated files map
     */
    public abstract boolean uploadToGalaxy(PluploadFile[] toUploadFiles);

}
