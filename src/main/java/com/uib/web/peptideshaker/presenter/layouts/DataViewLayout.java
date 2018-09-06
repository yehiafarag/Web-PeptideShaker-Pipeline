package com.uib.web.peptideshaker.presenter.layouts;

import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.uib.web.peptideshaker.presenter.core.DatasetOverviewLayout;
import com.uib.web.peptideshaker.presenter.core.ActionLabel;
import com.uib.web.peptideshaker.presenter.core.PopupWindow;
import com.uib.web.peptideshaker.presenter.core.StatusLabel;
import com.uib.web.peptideshaker.presenter.core.Uploader;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.Map;
import pl.exsio.plupload.PluploadFile;

/**
 * This class represents the data view layout (equal to history in galaxy) the
 * class allows users to get an over view of their files on galaxy and allow
 * users to delete the files and datasets.
 *
 * @author Yehia Farag
 */
public abstract class DataViewLayout extends Panel {

    private final VerticalLayout topPanelLayout;
    private final VerticalLayout topDataTable;

    private final VerticalLayout bottomPanelLayout;
    private final VerticalLayout bottomDataTable;

    private final float[] expandingRatio = new float[]{5f, 32f, 9f, 9f, 9f, 9f, 9f, 9f, 9f};

    /**
     * Constructor to initialise the main layout and attributes.
     */
    public DataViewLayout() {
        DataViewLayout.this.setWidth(100, Unit.PERCENTAGE);
        DataViewLayout.this.setHeight(100, Unit.PERCENTAGE);
        DataViewLayout.this.setStyleName("integratedframe");
        VerticalLayout panelsContainers = new VerticalLayout();
        panelsContainers.setMargin(new MarginInfo(true, true, true, true));
        panelsContainers.setWidth(100, Unit.PERCENTAGE);
        panelsContainers.setHeightUndefined();
        panelsContainers.setSpacing(true);
        DataViewLayout.this.setContent(panelsContainers);

        Uploader uploader = new Uploader() {
            @Override
            public void filesUploaded(PluploadFile[] uploadedFiles) {               
                uploadToGalaxy(uploadedFiles);
            }

        };

        topPanelLayout = new VerticalLayout();
        topPanelLayout.setWidth(100, Unit.PERCENTAGE);
        topPanelLayout.setHeightUndefined();
        topPanelLayout.setSpacing(true);
        topPanelLayout.setCaption("PeptideShaker Projects");
        panelsContainers.addComponent(topPanelLayout);
        topPanelLayout.addComponent(uploader);
        
        topDataTable = new VerticalLayout();
        topDataTable.setWidth(100, Unit.PERCENTAGE);
        topDataTable.setHeightUndefined();
        topDataTable.setSpacing(true);
        topDataTable.setSpacing(true);
        topPanelLayout.addComponent(topDataTable);

        VerticalLayout spacer = new VerticalLayout();
        spacer.setHeight(10, Unit.PIXELS);
        spacer.setWidth(100, Unit.PERCENTAGE);
        panelsContainers.addComponent(spacer);

        bottomPanelLayout = new VerticalLayout();
        bottomPanelLayout.setWidth(100, Unit.PERCENTAGE);
        bottomPanelLayout.setHeightUndefined();
        bottomPanelLayout.setSpacing(true);
        bottomPanelLayout.setCaption("Input Files");
        bottomPanelLayout.setMargin(new MarginInfo(false, false, true, false));
        panelsContainers.addComponent(bottomPanelLayout);

        bottomDataTable = new VerticalLayout();
        bottomDataTable.setWidth(100, Unit.PERCENTAGE);
        bottomDataTable.setHeightUndefined();
        bottomDataTable.setSpacing(true);
        bottomPanelLayout.addComponent(bottomDataTable);
    }
    private Component nameLabel;
    private boolean nelsSupported;

    public void updateDatasetsTable(Map<String, GalaxyFileObject> historyFilesMap) {

        nelsSupported = (boolean) VaadinSession.getCurrent().getAttribute("nelsgalaxy");
        topDataTable.removeAllComponents();
        bottomDataTable.removeAllComponents();
        Label headerName = new Label("Name");
        Label headerType = new Label("Type");
        Label headerStatus = new Label("Valid");
        headerStatus.addStyleName("textalignmiddle");

        Label headerView = new Label("View");
        headerView.addStyleName("textalignmiddle");

        Label headerNeLS = new Label("Backup");
        headerNeLS.addStyleName("textalignmiddle");
        headerNeLS.addStyleName("nelslogo");
        headerNeLS.setVisible(nelsSupported);

        Label headerGalaxy = new Label("Status");
        headerGalaxy.addStyleName("textalignmiddle");
        headerGalaxy.addStyleName("nelslogo");
        headerGalaxy.setVisible(nelsSupported);

        Label headerDownload = new Label("Download");
        headerDownload.addStyleName("textalignmiddle");
        Label headerDelete = new Label("Delete");
        headerDelete.addStyleName("textalignmiddle");
        headerDelete.setVisible(!nelsSupported);
        HorizontalLayout headerRow = initializeRowData(new Component[]{new Label(""), headerName, headerType, headerView, headerGalaxy, headerNeLS, headerDownload, headerDelete, headerStatus}, true);

        headerName = new Label("Name");
        headerType = new Label("Type");
        headerStatus = new Label("Valid");
        headerStatus.addStyleName("textalignmiddle");
        headerView = new Label("View");
        headerView.addStyleName("textalignmiddle");
        headerNeLS = new Label("Backup");
        headerNeLS.addStyleName("textalignmiddle");
        headerNeLS.addStyleName("nelslogo");
        headerNeLS.setVisible(nelsSupported);

        headerGalaxy = new Label("Status");
        headerGalaxy.addStyleName("textalignmiddle");
        headerGalaxy.addStyleName("nelslogo");
        headerGalaxy.setVisible(nelsSupported);

        headerDownload = new Label("Download");
        headerDownload.addStyleName("textalignmiddle");
        headerDelete = new Label("Delete");
        headerDelete.addStyleName("textalignmiddle");
        headerDelete.setVisible(!nelsSupported);

        HorizontalLayout headerRow2 = initializeRowData(new Component[]{new Label(""), headerName, headerType, headerView, headerGalaxy, headerNeLS, headerDownload, headerDelete, headerStatus}, true);

        bottomDataTable.addComponent(headerRow2);
        topDataTable.addComponent(headerRow);
        int i = 1;
        for (GalaxyFileObject ds : historyFilesMap.values()) {
            if (ds.getName() == null || ds.getType().equalsIgnoreCase("FASTA File")) {
                continue;
            }
            Component viewLabel;
            StatusLabel statusLabel = new StatusLabel();
            statusLabel.setStatus(ds.getStatus());
            ActionLabel downloadLabel = new ActionLabel(VaadinIcons.DOWNLOAD_ALT, "Download File") {
                @Override
                public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                    Page.getCurrent().open(ds.getDownloadUrl(), "", false);
                }

            };
            ActionLabel deleteLabel = new ActionLabel(VaadinIcons.TRASH, "Delete File") {
                @Override
                public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                    deleteDataset(ds);
                }

            };
            deleteLabel.setVisible(!nelsSupported);

            ActionLabel nelsLabel = new ActionLabel("NeLS", "Backup in NeLS") {
                @Override
                public void layoutClick(LayoutEvents.LayoutClickEvent event) {

                    String style = this.getStyleName();
                    this.removeStyleName("activate");
                    this.removeStyleName("deactivate");

                    if (style.contains("deactivate")) {
                        if (sendToNeLS(ds)) {
                            this.removeStyleName("deactivate");
                            this.addStyleName("activatenels");
                        }
                    } else {
                        Notification.show("remove from nels");
                        this.removeStyleName("activatenels");
                        this.addStyleName("deactivate");
                    }
                }

            };
            if (ds.isAvailableOnNels()) {
                nelsLabel.addStyleName("activatenels");
            } else {
                nelsLabel.addStyleName("deactivate");
            }
            nelsLabel.setVisible(nelsSupported);

            ActionLabel getToGalaxyLabel = new ActionLabel("Active", "Load from NeLS") {
                @Override
                public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                    String style = this.getStyleName();
                    this.removeStyleName("activate");
                    this.removeStyleName("deactivate");
                    if (style.contains("deactivate")) {
                        getFromNels(ds);
                        this.addStyleName("activate");
                    } else if (style.contains("activate")) {
                        if (ds.isAvailableOnNels()) {
                            Notification not = new Notification("WARNING", "You are going to delete this dataset and no backup available, are you sure you want to delete it", Notification.Type.ASSISTIVE_NOTIFICATION) {

                            };
                            deleteDataset(ds);
                        } else {
                            Notification not = new Notification("WARNING", "You are going to delete this dataset and no backup available, are you sure you want to delete it", Notification.Type.ASSISTIVE_NOTIFICATION) {

                            };
                        }
                        this.addStyleName("deactivate");
                        Notification.show("Delete from Galaxy");
                    }

                }
            };
            getToGalaxyLabel.setDescription("Activate/Deactivate the  dataset");
            if (ds.isAvailableOnGalaxy()) {
                getToGalaxyLabel.addStyleName("activate");
            } else {
                getToGalaxyLabel.addStyleName("deactivate");

            }
//            getToGalaxyLabel.setEnabled(!ds.isAvailableOnGalaxy());
            getToGalaxyLabel.setVisible(nelsSupported);

            HorizontalLayout rowLayout;

            if (ds.getType().equalsIgnoreCase("Web Peptide Shaker Dataset")) {
                nameLabel = new PopupWindow(ds.getName());
                DatasetOverviewLayout dsOverview = new DatasetOverviewLayout((PeptideShakerVisualizationDataset) ds) {
                    private final PopupWindow tDsOverview = (PopupWindow) nameLabel;

                    @Override
                    public void close() {
                        ((PopupWindow) tDsOverview).setPopupVisible(false);
                    }

                };
                ((PeptideShakerVisualizationDataset) ds).setEnzyme(dsOverview.getEnzyme());
                ((PopupWindow) nameLabel).setContent(dsOverview);
                nameLabel.addStyleName("bluecolor");
                if (statusLabel.getStatus() == 2) {
                    statusLabel.setStatus("Some files are missings or corrupted please re-run SearchGUI-PeptideShaker-WorkFlow");
                }

                viewLabel = new ActionLabel(VaadinIcons.CLUSTER, "View PeptideShaker results ") {
                    @Override
                    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                        viewDataset((PeptideShakerVisualizationDataset) ds);
                    }

                };
                viewLabel.addStyleName("orangecolor");
                rowLayout = initializeRowData(new Component[]{new Label(i + ""), nameLabel, new Label(ds.getType()), viewLabel, getToGalaxyLabel, nelsLabel, downloadLabel, deleteLabel, statusLabel}, false);
                topDataTable.addComponent(rowLayout);
            } else {
                nameLabel = new Label(ds.getName());
                ((Label) nameLabel).setDescription(ds.getName());
                rowLayout = initializeRowData(new Component[]{new Label(i + ""), nameLabel, new Label(ds.getType()), new Label(), getToGalaxyLabel, nelsLabel, downloadLabel, deleteLabel, statusLabel}, false);
                bottomDataTable.addComponent(rowLayout);
            }

            if (statusLabel.getStatus() == 1) {
                rowLayout.setEnabled(false);
            } else if (statusLabel.getStatus() == 2) {
                rowLayout.getComponent(0).setEnabled(false);
                rowLayout.getComponent(1).setEnabled(false);
                rowLayout.getComponent(2).setEnabled(false);
                rowLayout.getComponent(3).setEnabled(false);
                rowLayout.getComponent(4).setEnabled(true);
                rowLayout.getComponent(5).setEnabled(false);
                rowLayout.getComponent(6).setEnabled(false);
                rowLayout.getComponent(7).setEnabled(true);
            }

            rowLayout.setData(ds.getGalaxyId());

            i++;
        }
//        topPanelLayout.setVisible(topDataTable.getComponentCount() > 1);

    }

    private HorizontalLayout initializeRowData(Component[] data, boolean header) {
        HorizontalLayout row = new HorizontalLayout();
        row.setSpacing(true);
        int i = 0;
        for (Component component : data) {
//            component.setSizeFull();
//            component.setSizeUndefined();
            component.addStyleName(ValoTheme.LABEL_NO_MARGIN);
            row.addComponent(component);
            row.setComponentAlignment(component, Alignment.MIDDLE_CENTER);
            row.setExpandRatio(component, expandingRatio[i]);
            i++;
        }
        row.setWidth(100, Unit.PERCENTAGE);
        row.setHeight(30, Unit.PIXELS);
        row.setStyleName("row");
        if (header) {
            row.addStyleName("header");
        }

        return row;
    }

    public abstract boolean uploadToGalaxy(PluploadFile[] toUploadFiles);

    public abstract void deleteDataset(GalaxyFileObject ds);

    public abstract boolean sendToNeLS(GalaxyFileObject ds);

    public abstract boolean getFromNels(GalaxyFileObject ds);

    public abstract void viewDataset(PeptideShakerVisualizationDataset ds);
}
