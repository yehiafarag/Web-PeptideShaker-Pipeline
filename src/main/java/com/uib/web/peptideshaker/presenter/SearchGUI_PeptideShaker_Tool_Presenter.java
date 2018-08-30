package com.uib.web.peptideshaker.presenter;

import com.uib.web.peptideshaker.presenter.core.ViewableFrame;
import com.compomics.util.experiment.identification.identification_parameters.SearchParameters;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyTransferableFile;
import com.uib.web.peptideshaker.presenter.layouts.SearchGUIPeptideShakerWorkFlowInputLayout;
import com.uib.web.peptideshaker.presenter.core.BigSideBtn;
import com.uib.web.peptideshaker.presenter.core.SmallSideBtn;
import com.vaadin.event.LayoutEvents;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import java.util.HashMap;
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
    private final SmallSideBtn SearchGUI_PeptideShaker_Tool_side_Btn;
    /**
     * The tools layout top button.
     */
    private final SmallSideBtn SearchGUI_PeptideShaker_Tool_top_Btn;
    /**
     * The work-flow input form layout container.
     */
    private SearchGUIPeptideShakerWorkFlowInputLayout peptideshakerToolInputForm;
    /**
     * The work-flow side button container (left side button container).
     */
    private VerticalLayout btnContainer;
     /**
     * The work-flow bottom button container (bottom buttons container for small screen support).
     */
    private HorizontalLayout mobilebtnContainer;

    /**
     * Initialise the web tool main attributes
     *
     */
    public SearchGUI_PeptideShaker_Tool_Presenter() {
        SearchGUI_PeptideShaker_Tool_Presenter.this.setSizeFull();
        SearchGUI_PeptideShaker_Tool_Presenter.this.setStyleName("activelayout");
        SearchGUI_PeptideShaker_Tool_Presenter.this.addStyleName("integratedframe");

        this.SearchGUI_PeptideShaker_Tool_side_Btn = new SmallSideBtn("img/searchgui-medium-shadow-2.png");//spectra2.png
        this.SearchGUI_PeptideShaker_Tool_side_Btn.setData(SearchGUI_PeptideShaker_Tool_Presenter.this.getViewId());
        this.SearchGUI_PeptideShaker_Tool_top_Btn = new SmallSideBtn("img/searchgui-medium-shadow-2.png");//spectra2.png
        this.SearchGUI_PeptideShaker_Tool_top_Btn.setData(SearchGUI_PeptideShaker_Tool_Presenter.this.getViewId());

        this.initLayout();
        SearchGUI_PeptideShaker_Tool_Presenter.this.minimizeView();

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

        peptideshakerToolInputForm = new SearchGUIPeptideShakerWorkFlowInputLayout() {
            @Override
            public void executeWorkFlow(String projectName, String fastaFileId, Set<String> mgfIdsList, Set<String> searchEnginesList, SearchParameters searchParam) {
                SearchGUI_PeptideShaker_Tool_Presenter.this.execute_SearchGUI_PeptideShaker_WorkFlow(projectName, fastaFileId, mgfIdsList, searchEnginesList, searchParam);
            }

            @Override
            public Map<String, GalaxyTransferableFile> saveSearchGUIParameters(SearchParameters searchParameters, boolean editMode) {
                return SearchGUI_PeptideShaker_Tool_Presenter.this.saveSearchGUIParameters(searchParameters, editMode);
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

        mobilebtnContainer = new HorizontalLayout();
        mobilebtnContainer.setHeightUndefined();
        mobilebtnContainer.setWidthUndefined();
        mobilebtnContainer.setSpacing(true);
        mobilebtnContainer.setStyleName("bottomsidebtncontainer");

//        mobilebtnContainer.addComponent(workFlowBtn.getMobileModeBtn());
//        mobilebtnContainer.setComponentAlignment(workFlowBtn.getMobileModeBtn(), Alignment.TOP_CENTER);

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
    public SmallSideBtn getPresenterControlButton() {
        return SearchGUI_PeptideShaker_Tool_side_Btn;
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
        SearchGUI_PeptideShaker_Tool_side_Btn.setSelected(false);
        SearchGUI_PeptideShaker_Tool_top_Btn.setSelected(false);
        this.addStyleName("hidepanel");
        this.btnContainer.removeStyleName("visible");
        this.mobilebtnContainer.addStyleName("hidepanel");

    }

    /**
     *
     */
    @Override
    public void maximizeView() {
        SearchGUI_PeptideShaker_Tool_side_Btn.setSelected(true);
        SearchGUI_PeptideShaker_Tool_top_Btn.setSelected(true);
        this.btnContainer.addStyleName("visible");
        this.mobilebtnContainer.removeStyleName("hidepanel");
        this.removeStyleName("hidepanel");
    }

    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
//        BigSideBtn comp = (BigSideBtn) event.getComponent();
//        for (BigSideBtn bbt : btnsLayoutMap.keySet()) {
//            if (comp.getData().toString().equalsIgnoreCase(bbt.getData().toString())) {
//                bbt.setSelected(true);
//                btnsLayoutMap.get(bbt).removeStyleName("hidepanel");
//            } else {
//                bbt.setSelected(false);
//                btnsLayoutMap.get(bbt).addStyleName("hidepanel");
//            }
//        }
//
//        if (comp.getData().toString().equalsIgnoreCase("nels")) {
//        }
    }

    /**
     *
     * @return
     */
    @Override
    public VerticalLayout getLeftView() {
        return btnContainer;
    }

    /**
     *
     * @return
     */
    @Override
    public HorizontalLayout getBottomView() {
        return mobilebtnContainer;
    }

    /**
     *
     * @return
     */
//    @Override
//    public SmallSideBtn getTopView() {
//        return SearchGUI_PeptideShaker_Tool_top_Btn;
//    }

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
     * @param fileName search parameters file name
     * @param searchParameters searchParameters .par file
     */
    public abstract Map<String, GalaxyTransferableFile> saveSearchGUIParameters(SearchParameters searchParameters, boolean editMode);

}
