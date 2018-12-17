package com.uib.web.peptideshaker.presenter;

import com.uib.web.peptideshaker.presenter.core.ViewableFrame;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.uib.web.peptideshaker.presenter.core.BigSideBtn;
import com.uib.web.peptideshaker.presenter.layouts.DataViewLayout;
import com.uib.web.peptideshaker.presenter.core.ButtonWithLabel;
import com.uib.web.peptideshaker.presenter.core.SmallSideBtn;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
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
 * viewing the PeptideShaker results on web
 *
 * @author Yehia Farag
 */
public abstract class FileSystemPresenter extends VerticalLayout implements ViewableFrame, LayoutEvents.LayoutClickListener {

    /**
     * The small side button (normal size screen).
     */
    private final ButtonWithLabel controlButton;
    /**
     * The main left side buttons container in big screen mode.
     */
    /**
     * The small side button (normal size screen).
     */
    private final SmallSideBtn smallControlButton;
    private VerticalLayout leftSideButtonsContainer;
    /**
     * Map of layouts to coordinate left side buttons actions.
     */
    private final Map<BigSideBtn, Component> btnsLayoutMap;
    /**
     * Main layout that contains the files and datasets table.
     */
    private DataViewLayout dataLayout;
    /**
     * The main left side button (only support in a big screen).
     */
    private BigSideBtn viewDataBtn;

    /**
     * Constructor to initialise the web tool main attributes.
     */
    public FileSystemPresenter() {
        FileSystemPresenter.this.setSizeFull();
        FileSystemPresenter.this.setStyleName("activelayout");
        FileSystemPresenter.this.addStyleName("hidelowerpanel");

        this.smallControlButton = new SmallSideBtn(VaadinIcons.GLOBE);
        this.smallControlButton.setData(FileSystemPresenter.this.getViewId());
        smallControlButton.setDescription("View available datastes and files");
        this.controlButton = new ButtonWithLabel("Data Overview</br><font>Available files and projects</font>", 1);
        this.controlButton.updateIcon(VaadinIcons.GLOBE.getHtml());
        this.controlButton.setData(FileSystemPresenter.this.getViewId());
        this.controlButton.setDescription("View available datastes and files");
//         this.controlButton.addStyleName("hidetopbtn");
        this.btnsLayoutMap = new LinkedHashMap<>();
        this.initLayout();
        FileSystemPresenter.this.minimizeView();
    }

    /**
     * Initialise the container layout.
     */
    private void initLayout() {
        this.addStyleName("integratedframe");
        leftSideButtonsContainer = new VerticalLayout();
        leftSideButtonsContainer.setWidth(100, Unit.PERCENTAGE);
        leftSideButtonsContainer.setHeightUndefined();
        leftSideButtonsContainer.setSpacing(true);
        leftSideButtonsContainer.setMargin(new MarginInfo(false, false, true, false));
        leftSideButtonsContainer.addStyleName("singlebtn");
        viewDataBtn = new BigSideBtn("Data Overview", 1);
        viewDataBtn.setDescription("Available datasets and files");
        viewDataBtn.updateIcon(VaadinIcons.GLOBE.getHtml());
        viewDataBtn.setData("datasetoverview");
        leftSideButtonsContainer.addComponent(viewDataBtn);
        leftSideButtonsContainer.setComponentAlignment(viewDataBtn, Alignment.TOP_CENTER);
        viewDataBtn.addLayoutClickListener(FileSystemPresenter.this);

        VerticalLayout dataContainerLayout = initDataViewTableLayout();
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
        dataViewFrameContent.addComponent(dataContainerLayout);
        viewDataBtn.setSelected(true);

    }

    /**
     * Initialise the data view table layout.
     */
    private VerticalLayout initDataViewTableLayout() {
        VerticalLayout container = new VerticalLayout();
        container.setWidth(100, Unit.PERCENTAGE);
        container.setHeight(100, Unit.PERCENTAGE);
        container.setSpacing(true);
        container.setStyleName("subframe");
        container.addStyleName("padding25");
        dataLayout = new DataViewLayout() {
            @Override
            public void deleteDataset(GalaxyFileObject ds) {
                System.out.println("at user "+(VaadinSession.getCurrent().getAttribute("ApiKey").toString().equals(VaadinSession.getCurrent().getAttribute("testUserAPIKey"))));
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
                boolean check = FileSystemPresenter.this.uploadToGalaxy(toUploadFiles);
                return check;
            }

        };
        container.addComponent(dataLayout);
        container.setComponentAlignment(dataLayout, Alignment.MIDDLE_CENTER);

        return container;
    }

    /**
     * Update Online PeptideShaker files from Galaxy Server
     *
     * @param historyFilesMap List of available files on Galaxy Server
     * @param jobInProgress Jobs are running
     */
    public void updateSystemData(Map<String, GalaxyFileObject> historyFilesMap, boolean jobInProgress) {
        if (jobInProgress) {
            smallControlButton.updateIconURL("img/globeearthanimation1.gif");
            controlButton.updateIconResource(new ThemeResource("img/globeearthanimation1.gif"));
            viewDataBtn.updateIconResource(new ThemeResource("img/globeearthanimation1.gif"));
        } else {
            smallControlButton.updateIconURL(VaadinIcons.GLOBE);
            controlButton.updateIcon(VaadinIcons.GLOBE.getHtml());
            viewDataBtn.updateIcon(VaadinIcons.GLOBE.getHtml());
        }
        if (historyFilesMap != null) {
            this.dataLayout.updateDatasetsTable(historyFilesMap);
        }
    }

    /**
     * Get the main frame layout
     *
     * @return File system presenter layout
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

    @Override
    public ButtonWithLabel getLargePresenterControlButton() {
        return controlButton;
    }

    /**
     * Get the current view ID
     *
     * @return view id
     */
    @Override
    public String getViewId() {
        return FileSystemPresenter.class.getName();
    }

    /**
     * Hide the main view for the current component.
     */
    @Override
    public void minimizeView() {
        smallControlButton.setSelected(false);
        controlButton.setSelected(false);
        this.addStyleName("hidepanel");
        this.leftSideButtonsContainer.removeStyleName("visible");

    }

    /**
     * Show the main view for the current component.
     */
    @Override
    public void maximizeView() {
        controlButton.setSelected(true);
        dataLayout.setEnabled(true);
        smallControlButton.setSelected(true);
        this.leftSideButtonsContainer.addStyleName("visible");
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

    /**
     * Get the left side container for left side big buttons (to be used in case
     * of large screen mode)
     *
     * @return left side buttons container
     */
    @Override
    public VerticalLayout getSubViewButtonsActionContainerLayout() {
        return leftSideButtonsContainer;
    }

    /**
     * Abstract method to allow customised delete action for files from Galaxy
     * Server
     *
     * @param fileObject the file to be removed from Galaxy Server
     */
    public abstract void deleteDataset(GalaxyFileObject fileObject);

    /**
     * Abstract method to allow customised view action for PeptideShaker dataset
     *
     * @param PeptideShakerDataset selected Peptide Shaker dataset to view
     */
    public abstract void viewDataset(PeptideShakerVisualizationDataset PeptideShakerDataset);

    /**
     *
     * Abstract method to allow customised saving/moving action for files from
     * Galaxy Server to NeLS storage system
     *
     * @param fileObject the file to be saved on NeLS storage system
     * @return file successfully saved on NeLS storage system
     */
    public abstract boolean sendToNeLS(GalaxyFileObject fileObject);

    /**
     * Abstract method to allow customised retrieve/moving action for files from
     * NeLS storage system to Galaxy Server
     *
     * @param fileObject the file to be saved on NeLS storage system
     * @return file successfully moved to Galaxy Server
     */
    public abstract boolean getFromNels(GalaxyFileObject fileObject);

    /**
     * Abstract method to allow customised uploading action for files from user
     * local computer to Galaxy Server
     *
     * @param toUploadFiles array of files to be uploaded
     * @return successfully uploaded files
     */
    public abstract boolean uploadToGalaxy(PluploadFile[] toUploadFiles);

}
