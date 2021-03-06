package com.uib.web.peptideshaker.presenter;

import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.DatasetVisulizationLevelContainer;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.ProteinVisulizationLevelContainer;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.SelectionManager;
import com.uib.web.peptideshaker.presenter.core.BigSideBtn;
import com.uib.web.peptideshaker.presenter.core.SmallSideBtn;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.PSMVisulizationLevelContainer;
import com.vaadin.event.LayoutEvents;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

/**
 * This class represent PeptideShaker view presenter which is responsible for
 * viewing the peptide shaker results on web
 *
 * @author Yehia Farag
 */
public class PeptideShakerViewPresenter extends VerticalLayout implements ViewableFrame, LayoutEvents.LayoutClickListener {

    /**
     * The small side button.
     */
    private final SmallSideBtn toolsBtn;
    /**
     * The small top button.
     */
    private final SmallSideBtn topToolsBtn;
    private VerticalLayout btnContainer;
    private HorizontalLayout mobilebtnContainer;

    private DatasetVisulizationLevelContainer datasetVisulizationLevelContainer;
    private SelectionManager Selection_Manager;
    private ProteinVisulizationLevelContainer proteinsVisulizationLevelContainer;

    private PSMVisulizationLevelContainer psmVisulizationLevelContainer;

    /**
     * Initialize the web tool main attributes
     *
     */
    public PeptideShakerViewPresenter() {
        PeptideShakerViewPresenter.this.setSizeFull();
        PeptideShakerViewPresenter.this.setStyleName("activelayout");
        this.toolsBtn = new SmallSideBtn("img/cluster.svg");
        this.toolsBtn.setData(PeptideShakerViewPresenter.this.getViewId());

        this.topToolsBtn = new SmallSideBtn("img/cluster.svg");
        this.topToolsBtn.setData(PeptideShakerViewPresenter.this.getViewId());
        this.Selection_Manager = new SelectionManager();
        this.initLayout();
        PeptideShakerViewPresenter.this.minimizeView();
        this.topToolsBtn.setEnabled(false);
        this.toolsBtn.setEnabled(false);

    }

    private void initLayout() {
        this.addStyleName("integratedframe");
        btnContainer = new VerticalLayout();
        btnContainer.setWidth(100, Unit.PERCENTAGE);
        btnContainer.setHeightUndefined();
        btnContainer.setSpacing(true);
        btnContainer.setMargin(new MarginInfo(false, false, true, false));
//

        BigSideBtn datasetsOverviewBtn = new BigSideBtn("Dataset overview", 1);
        datasetsOverviewBtn.setData("datasetoverview");
        btnContainer.addComponent(datasetsOverviewBtn);
        btnContainer.setComponentAlignment(datasetsOverviewBtn, Alignment.TOP_CENTER);
        datasetsOverviewBtn.addLayoutClickListener(PeptideShakerViewPresenter.this);
        Selection_Manager.addBtnLayout(datasetsOverviewBtn, datasetVisulizationLevelContainer);
        datasetVisulizationLevelContainer = new DatasetVisulizationLevelContainer(Selection_Manager, datasetsOverviewBtn);
        datasetVisulizationLevelContainer.setSizeFull();
        Selection_Manager.addBtnLayout(datasetsOverviewBtn, datasetVisulizationLevelContainer);

//"img/peptides_3.png",
        BigSideBtn proteinoverviewBtn = new BigSideBtn("Protein Overview", 2);
        proteinoverviewBtn.updateIconResource(null);
        proteinoverviewBtn.setData("proteinoverview");
        btnContainer.addComponent(proteinoverviewBtn);
        btnContainer.setComponentAlignment(proteinoverviewBtn, Alignment.TOP_CENTER);
        proteinoverviewBtn.addLayoutClickListener(PeptideShakerViewPresenter.this);

        proteinsVisulizationLevelContainer = new ProteinVisulizationLevelContainer(Selection_Manager, proteinoverviewBtn);
        Selection_Manager.addBtnLayout(proteinoverviewBtn, proteinsVisulizationLevelContainer);

        BigSideBtn psmoverviewBtn = new BigSideBtn("PSM Overview", 3);
        psmoverviewBtn.updateIconResource(null);
        psmoverviewBtn.setData("psmoverview");
        btnContainer.addComponent(psmoverviewBtn);
        btnContainer.setComponentAlignment(psmoverviewBtn, Alignment.TOP_CENTER);
        psmoverviewBtn.addLayoutClickListener(PeptideShakerViewPresenter.this);

        psmVisulizationLevelContainer = new PSMVisulizationLevelContainer(Selection_Manager, psmoverviewBtn);
        Selection_Manager.addBtnLayout(psmoverviewBtn, psmVisulizationLevelContainer);

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
        toolViewFrameContent.addComponent(psmVisulizationLevelContainer);

        mobilebtnContainer = new HorizontalLayout();
        mobilebtnContainer.setHeight(100, Unit.PERCENTAGE);
        mobilebtnContainer.setWidthUndefined();
        mobilebtnContainer.setSpacing(true);
        mobilebtnContainer.setStyleName("bottomsidebtncontainer");

        mobilebtnContainer.addComponent(datasetsOverviewBtn.getMobileModeBtn());
        mobilebtnContainer.setComponentAlignment(datasetsOverviewBtn.getMobileModeBtn(), Alignment.TOP_CENTER);
        datasetsOverviewBtn.setSelected(true);
//        BigSideBtn proteinoverviewBtnM = new BigSideBtn("img/peptides_3.png", "Peptides Overview");       
        mobilebtnContainer.addComponent(proteinoverviewBtn.getMobileModeBtn());
        mobilebtnContainer.setComponentAlignment(proteinoverviewBtn.getMobileModeBtn(), Alignment.TOP_CENTER);
        mobilebtnContainer.addComponent(psmoverviewBtn.getMobileModeBtn());
        mobilebtnContainer.setComponentAlignment(psmoverviewBtn.getMobileModeBtn(), Alignment.TOP_CENTER);

    }

    @Override
    public VerticalLayout getMainView() {
        return this;
    }

    @Override
    public SmallSideBtn getRightView() {
        return toolsBtn;
    }

    @Override
    public String getViewId() {
        return PeptideShakerViewPresenter.class.getName();
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

    }

    private int lastSelectedBtn = 1;

    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        BigSideBtn comp = (BigSideBtn) event.getComponent();
        Selection_Manager.selectBtn(comp);
        if (proteinsVisulizationLevelContainer != null) {
            if (comp.getBtnId() == 2 && lastSelectedBtn == 1) {
                proteinsVisulizationLevelContainer.activate3DProteinView();
            } else if (lastSelectedBtn == 1) {
                proteinsVisulizationLevelContainer.reset3DProteinView();
            }
        }
        lastSelectedBtn = comp.getBtnId();

    }

    @Override
    public VerticalLayout getLeftView() {
        return btnContainer;
    }

    @Override
    public HorizontalLayout getBottomView() {
        return mobilebtnContainer;
    }

    @Override
    public SmallSideBtn getTopView() {
        return topToolsBtn;
    }

    public void setSelectedDataset(PeptideShakerVisualizationDataset ds) {
        this.topToolsBtn.setEnabled(ds != null);
        this.toolsBtn.setEnabled(ds != null);
        Selection_Manager.reset();
        Selection_Manager.selectBtn(0);
        this.datasetVisulizationLevelContainer.selectDataset(ds);
        this.proteinsVisulizationLevelContainer.selectDataset(ds);
        this.psmVisulizationLevelContainer.selectDataset(ds);

    }
}
