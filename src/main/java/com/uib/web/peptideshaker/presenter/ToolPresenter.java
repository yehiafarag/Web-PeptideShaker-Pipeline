package com.uib.web.peptideshaker.presenter;

import com.compomics.util.experiment.identification.identification_parameters.SearchParameters;
import com.uib.web.peptideshaker.galaxy.dataobjects.SystemDataSet;
import com.uib.web.peptideshaker.galaxy.dataobjects.GalaxyFile;
import com.uib.web.peptideshaker.presenter.layouts.WorkFlowLayout;
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
public abstract class ToolPresenter extends VerticalLayout implements ViewableFrame, LayoutEvents.LayoutClickListener {

    /**
     * The tools layout side button.
     */
    private final SmallSideBtn toolsBtn;
    /**
     * The tools layout top button.
     */
    private final SmallSideBtn topToolsBtn;
    /**
     * The Galaxy history handler.
     */
    /**
     * The work-flow layout container.
     */
    private WorkFlowLayout peptideshakerToolInputForm;

    private final Map<BigSideBtn, Component> btnsLayoutMap;
//    private VerticalLayout rightLayoutContainer;
    private VerticalLayout btnContainer;
    private HorizontalLayout mobilebtnContainer;

    /**
     * Initialize the web tool main attributes
     *
     */
    public ToolPresenter() {
        ToolPresenter.this.setSizeFull();
        ToolPresenter.this.setStyleName("activelayout");

        this.toolsBtn = new SmallSideBtn("img/spectra2.png");//spectra2.png
        this.toolsBtn.setData(ToolPresenter.this.getViewId());
        this.toolsBtn.addStyleName("middilesmallbtn");

        this.topToolsBtn = new SmallSideBtn("img/spectra2.png");
        this.topToolsBtn.setData(ToolPresenter.this.getViewId());

        this.btnsLayoutMap = new HashMap<>();
        this.initLayout();
        ToolPresenter.this.minimizeView();

    }

    /**
     * Update the work-flow input files
     *
     *
     * @param searchSettingsMap search settings .par files map
     * @param fastaFilesMap The main FASTA File Map (ID to Name).
     * @param mgfFilesMap The main MGF File Map (ID to Name).
     */
    public void updatePeptideShakerToolInputForm(Map<String, GalaxyFile> searchSettingsMap, Map<String, SystemDataSet> fastaFilesMap, Map<String, SystemDataSet> mgfFilesMap) {
        peptideshakerToolInputForm.updateForm(searchSettingsMap, fastaFilesMap, mgfFilesMap);
    }

    private void initLayout() {

        this.addStyleName("integratedframe");
        btnContainer = new VerticalLayout();
        btnContainer.setWidth(100, Unit.PERCENTAGE);
        btnContainer.setHeightUndefined();
        btnContainer.setSpacing(true);
        btnContainer.setMargin(new MarginInfo(false, false, true, false));

        peptideshakerToolInputForm = new WorkFlowLayout() {
            @Override
            public void executeWorkFlow(String projectName,String fastaFileId, Set<String> mgfIdsList, Set<String> searchEnginesList,SearchParameters searchParam,Map<String,Boolean>searchEngines) {
                ToolPresenter.this.executeWorkFlow(projectName,fastaFileId, mgfIdsList, searchEnginesList,searchParam,searchEngines);
            }

            @Override
            public Map<String, GalaxyFile>  saveSearchGUIParameters(SearchParameters searchParameters,boolean editMode) {
                return ToolPresenter.this.saveSearchGUIParameters(searchParameters,editMode);
            }

        };
        VerticalLayout nelsLayout = new VerticalLayout();

        BigSideBtn nelsBtn = new BigSideBtn("Get Data",1);
        nelsBtn.updateIconResource(new ThemeResource("img/NeLS3.png"));
        nelsBtn.setData("nels");
        btnContainer.addComponent(nelsBtn);
        btnContainer.setComponentAlignment(nelsBtn, Alignment.TOP_CENTER);
        nelsBtn.addLayoutClickListener(ToolPresenter.this);
        btnsLayoutMap.put(nelsBtn, nelsLayout);

        BigSideBtn workFlowBtn = new BigSideBtn("Work-Flow",2);
         workFlowBtn.updateIconResource(new ThemeResource("img/workflow3.png"));
        workFlowBtn.setData("workflow");
        workFlowBtn.addStyleName("zeropadding");
        btnContainer.addComponent(workFlowBtn);
        btnContainer.setComponentAlignment(workFlowBtn, Alignment.TOP_CENTER);
        workFlowBtn.addLayoutClickListener(ToolPresenter.this);
        workFlowBtn.setSelected(true);
        btnsLayoutMap.put(workFlowBtn, peptideshakerToolInputForm);

        VerticalLayout toolViewFrame = new VerticalLayout();
        toolViewFrame.setSizeFull();
        toolViewFrame.setStyleName("viewframe");

        this.addComponent(toolViewFrame);
        this.setExpandRatio(toolViewFrame, 100);

        AbsoluteLayout toolViewFrameContent = new AbsoluteLayout();
        toolViewFrameContent.addStyleName("viewframecontent");
        toolViewFrameContent.setSizeFull();
        toolViewFrame.addComponent(toolViewFrameContent);

        toolViewFrameContent.addComponent(nelsLayout);
        toolViewFrameContent.addComponent(peptideshakerToolInputForm);

        mobilebtnContainer = new HorizontalLayout();
        mobilebtnContainer.setHeight(100, Unit.PERCENTAGE);
        mobilebtnContainer.setWidthUndefined();
        mobilebtnContainer.setSpacing(true);
        mobilebtnContainer.setStyleName("bottomsidebtncontainer");

       
        mobilebtnContainer.addComponent(nelsBtn.getMobileModeBtn());
        mobilebtnContainer.setComponentAlignment(nelsBtn.getMobileModeBtn(), Alignment.TOP_CENTER);

        mobilebtnContainer.addComponent(workFlowBtn.getMobileModeBtn());
        mobilebtnContainer.setComponentAlignment(workFlowBtn.getMobileModeBtn(), Alignment.TOP_CENTER);
     

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
    public SmallSideBtn getRightView() {
        return toolsBtn;
    }

    /**
     *
     * @return
     */
    @Override
    public String getViewId() {
        return ToolPresenter.class.getName();
    }

    /**
     *
     */
    @Override
    public void minimizeView() {
        toolsBtn.setSelected(false);
        topToolsBtn.setSelected(false);
        this.addStyleName("hidepanel");
        this.btnContainer.removeStyleName("visible");
        this.mobilebtnContainer.addStyleName("hidepanel");

    }

    /**
     *
     */
    @Override
    public void maximizeView() {
        toolsBtn.setSelected(true);
        topToolsBtn.setSelected(true);
        this.btnContainer.addStyleName("visible");
        this.mobilebtnContainer.removeStyleName("hidepanel");
        this.removeStyleName("hidepanel");
    }

    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        BigSideBtn comp = (BigSideBtn) event.getComponent();
        for (BigSideBtn bbt : btnsLayoutMap.keySet()) {
            if (comp.getData().toString().equalsIgnoreCase(bbt.getData().toString())) {
                bbt.setSelected(true);
                btnsLayoutMap.get(bbt).removeStyleName("hidepanel");
            } else {
                bbt.setSelected(false);
                btnsLayoutMap.get(bbt).addStyleName("hidepanel");
            }
        }

        if (comp.getData().toString().equalsIgnoreCase("nels")) {
        }
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
    @Override
    public SmallSideBtn getTopView() {
        return topToolsBtn;
    }

    /**
     * Run Online Peptide-Shaker work-flow
     *
     * @param fastaFileId FASTA file dataset id
     * @param mgfIdsList list of MGF file dataset ids
     * @param searchEnginesList List of selected search engine names
     * @param historyId galaxy history id that will store the results
     */
    public abstract void executeWorkFlow(String projectName,String fastaFileId, Set<String> mgfIdsList, Set<String> searchEnginesList,SearchParameters searchParam,Map<String,Boolean>otherSearchParameters);
    
     /**
     * Save search settings file into galaxy
     *
     * @param fileName search parameters file name
     * @param searchParameters searchParameters .par file
     */
    public abstract Map<String, GalaxyFile>  saveSearchGUIParameters(SearchParameters searchParameters,boolean editMode);

}
