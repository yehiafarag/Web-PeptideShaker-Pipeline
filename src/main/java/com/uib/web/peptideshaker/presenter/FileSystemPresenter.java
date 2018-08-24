package com.uib.web.peptideshaker.presenter;

import com.uib.web.peptideshaker.presenter.core.ViewableFrame;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.uib.web.peptideshaker.presenter.layouts.DataViewLayout;
import com.uib.web.peptideshaker.presenter.core.BigSideBtn;
import com.uib.web.peptideshaker.presenter.core.SmallSideBtn;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import pl.exsio.plupload.PluploadFile;

/**
 * This class represent PeptideShaker view presenter which is responsible for
 * viewing the peptide shaker results on web
 *
 * @author Yehia Farag
 */
public abstract class FileSystemPresenter extends VerticalLayout implements ViewableFrame, LayoutEvents.LayoutClickListener {

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
    private final Map<BigSideBtn, Component> btnsLayoutMap;
    private DataViewLayout dataLayout;
    private BigSideBtn viewDataBtn;

    /**
     * Initialise the web tool main attributes.
     *
     */
    public FileSystemPresenter() {
        FileSystemPresenter.this.setSizeFull();
        FileSystemPresenter.this.setStyleName("activelayout");
//        this.toolsBtn = new SmallSideBtn("img/jobs2.png");
        this.toolsBtn = new SmallSideBtn(VaadinIcons.GLOBE);
        this.toolsBtn.setData(FileSystemPresenter.this.getViewId());
//  this.topToolsBtn = new SmallSideBtn("img/jobs2.png");
        this.topToolsBtn = new SmallSideBtn(VaadinIcons.GLOBE);
        this.topToolsBtn.setData(FileSystemPresenter.this.getViewId());

        this.btnsLayoutMap = new LinkedHashMap<>();
        this.initLayout();
        FileSystemPresenter.this.minimizeView();

    }

    public void setBusy(boolean busy, Map<String, GalaxyFileObject> historyFilesMap) {
        if (busy) {
            toolsBtn.updateIconURL("img/globe-earth-animation-26.gif");//loading.gif
            topToolsBtn.updateIconURL("img/globe-earth-animation-26.gif");//loading.gif
            viewDataBtn.updateIconResource(new ThemeResource("img/globe-earth-animation-26.gif"));
//            return;
        } else {
//            toolsBtn.updateIconURL("img/jobs2.png");
//            topToolsBtn.updateIconURL("img/jobs2.png");
            toolsBtn.updateIconURL(VaadinIcons.GLOBE);
            topToolsBtn.updateIconURL(VaadinIcons.GLOBE);
            viewDataBtn.updateIcon(VaadinIcons.GLOBE.getHtml());
        }
        this.dataLayout.updateDatasetsTable(historyFilesMap);
    }

    private void initLayout() {
        this.addStyleName("integratedframe");
        btnContainer = new VerticalLayout();
        btnContainer.setWidth(100, Unit.PERCENTAGE);
        btnContainer.setHeightUndefined();
        btnContainer.setSpacing(true);
        btnContainer.setMargin(new MarginInfo(false, false, true, false));
//
        viewDataBtn = new BigSideBtn("Show Data", 1);
//        viewDataBtn.updateIcon(new ThemeResource("img/jobs2.png"));
        viewDataBtn.updateIcon(VaadinIcons.GLOBE.getHtml());
        viewDataBtn.setData("datasetoverview");
        btnContainer.addComponent(viewDataBtn);
        btnContainer.setComponentAlignment(viewDataBtn, Alignment.TOP_CENTER);
        viewDataBtn.addLayoutClickListener(FileSystemPresenter.this);

        VerticalLayout dataContainerLayout = initDataViewLayout();
        btnsLayoutMap.put(viewDataBtn, dataContainerLayout);

        VerticalLayout dataViewFrame = new VerticalLayout();
        dataViewFrame.setSizeFull();
        dataViewFrame.setStyleName("viewframe");

        this.addComponent(dataViewFrame);
        this.setExpandRatio(dataViewFrame, 100);
        AbsoluteLayout dataViewFrameContent = new AbsoluteLayout();
        dataViewFrameContent.addStyleName("viewframecontent");
        dataViewFrameContent.setSizeFull();
        dataViewFrame.addComponent(dataViewFrameContent);
//
        dataViewFrameContent.addComponent(dataContainerLayout);

        mobilebtnContainer = new HorizontalLayout();
        mobilebtnContainer.setHeight(100, Unit.PERCENTAGE);
        mobilebtnContainer.setWidthUndefined();
        mobilebtnContainer.setSpacing(true);
        mobilebtnContainer.setStyleName("bottomsidebtncontainer");

        mobilebtnContainer.addComponent(viewDataBtn.getMobileModeBtn());
        mobilebtnContainer.setComponentAlignment(viewDataBtn.getMobileModeBtn(), Alignment.TOP_CENTER);

        viewDataBtn.setSelected(true);

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
        return FileSystemPresenter.class.getName();
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
        btnsLayoutMap.keySet().forEach((bbt) -> {
            if (comp.getData().toString().equalsIgnoreCase(bbt.getData().toString())) {
                bbt.setSelected(true);
                btnsLayoutMap.get(bbt).removeStyleName("hidepanel");
            } else {
                bbt.setSelected(false);
                btnsLayoutMap.get(bbt).addStyleName("hidepanel");
            }
        });
        if (comp.getData().toString().equalsIgnoreCase("datasetoverview")) {

        }
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

    private VerticalLayout initDataViewLayout() {
        VerticalLayout container = new VerticalLayout();
        container.setWidth(100, Unit.PERCENTAGE);
        container.setHeight(100, Unit.PERCENTAGE);
        container.setSpacing(true);
        container.setStyleName("subframe");
        container.addStyleName("padding25");
//        container.setMargin(new MarginInfo(true, true, true, true));
        dataLayout = new DataViewLayout() {
            @Override
            public void deleteDataset(GalaxyFileObject ds) {
                FileSystemPresenter.this.deleteDataset(ds);
            }

            @Override
            public void viewDataset(PeptideShakerVisualizationDataset ds) {
                FileSystemPresenter.this.viewDataset(ds);
            }

            @Override
            public boolean sendToNeLS(GalaxyFileObject ds) {
                return FileSystemPresenter.this.sendToNeLS(ds);
            }

            @Override
            public boolean getFromNels(GalaxyFileObject ds) {
                return FileSystemPresenter.this.getFromNels(ds);
            }

            @Override
            public boolean uploadToGalaxy(PluploadFile[] toUploadFiles) {
                return FileSystemPresenter.this.uploadToGalaxy(toUploadFiles);
            }
            

        };
        container.addComponent(dataLayout);
        container.setComponentAlignment(dataLayout, Alignment.MIDDLE_CENTER);

        return container;
    }

    public abstract void deleteDataset(GalaxyFileObject ds);

    public abstract void viewDataset(PeptideShakerVisualizationDataset ds);

    public abstract boolean sendToNeLS(GalaxyFileObject ds);

    public abstract boolean getFromNels(GalaxyFileObject ds);
    
     public abstract boolean uploadToGalaxy(PluploadFile[] toUploadFiles);

}