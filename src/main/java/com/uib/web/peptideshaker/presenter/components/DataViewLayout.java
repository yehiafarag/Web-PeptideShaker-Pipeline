package com.uib.web.peptideshaker.presenter.components;

import com.compomics.util.experiment.identification.identification_parameters.SearchParameters;
import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.galaxy.dataobjects.SystemDataSet;
import com.uib.web.peptideshaker.presenter.core.DatasetOverviewLayout;
import com.uib.web.peptideshaker.presenter.core.ActionLabel;
import com.uib.web.peptideshaker.presenter.core.PopupWindow;
import com.uib.web.peptideshaker.presenter.core.StatusLabel;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
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

    private final float[] expandingRatio = new float[]{5f, 35f, 12, 12, 12, 12, 12f};

    /**
     * Constructor to initialize the main layout and attributes.
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

        topPanelLayout = new VerticalLayout();
        topPanelLayout.setWidth(100, Unit.PERCENTAGE);
        topPanelLayout.setHeightUndefined();
        topPanelLayout.setSpacing(true);
        topPanelLayout.setCaption("Input Files");
        panelsContainers.addComponent(topPanelLayout);

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
        bottomPanelLayout.setCaption("PeptideShaker Projects");
        bottomPanelLayout.setMargin(new MarginInfo(false, false, true, false));
        panelsContainers.addComponent(bottomPanelLayout);

        bottomDataTable = new VerticalLayout();
        bottomDataTable.setWidth(100, Unit.PERCENTAGE);
        bottomDataTable.setHeightUndefined();
        bottomDataTable.setSpacing(true);
        bottomPanelLayout.addComponent(bottomDataTable);
    }
    private Component nameLabel;

    public void updateDatasetsTable(Map<String, SystemDataSet> historyFilesMap) {

        topDataTable.removeAllComponents();
        bottomDataTable.removeAllComponents();
        Label headerName = new Label("Name");
        Label headerType = new Label("Type");
        Label headerStatus = new Label("Status");
        headerStatus.addStyleName("textalignmiddle");

        Label headerView = new Label("View");
        headerView.addStyleName("textalignmiddle");

        Label headerDownload = new Label("Download");
        headerDownload.addStyleName("textalignmiddle");
        Label headerDelete = new Label("Delete");
        headerDelete.addStyleName("textalignmiddle");

        HorizontalLayout headerRow = initializeRowData(new Component[]{new Label(""), headerName, headerType, headerStatus, headerView, headerDownload, headerDelete}, true);
        topDataTable.addComponent(headerRow);
        headerName = new Label("Name");
        headerType = new Label("Type");
        headerStatus = new Label("Status");
        headerStatus.addStyleName("textalignmiddle");
        headerView = new Label("View");
        headerView.addStyleName("textalignmiddle");
        headerDownload = new Label("Download");
        headerDownload.addStyleName("textalignmiddle");
        headerDelete = new Label("Delete");
        headerDelete.addStyleName("textalignmiddle");
        HorizontalLayout headerRow2 = initializeRowData(new Component[]{new Label(""), headerName, headerType, headerStatus, headerView, headerDownload, headerDelete}, true);

        bottomDataTable.addComponent(headerRow2);
        int i = 1;
        for (SystemDataSet ds : historyFilesMap.values()) {
           
            if (ds.getName() == null || ds.getType().equalsIgnoreCase("FASTA File")) {
                continue;
            }
            System.out.println("at history "+ ds.getType());
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
                if (statusLabel.getStatus() == 0 && !((PeptideShakerVisualizationDataset) ds).isValidFile()) {
                    statusLabel.setStatus("Some files are missings or corrupted please re-run SearchGUI-PeptideShaker-WorkFlow");
                }

                viewLabel = new ActionLabel(VaadinIcons.CLUSTER, "View PeptideShaker results ") {
                    @Override
                    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                        Notification.show("View PeptideShaker Results");
                        viewDataset((PeptideShakerVisualizationDataset) ds);
                    }

                };
                viewLabel.addStyleName("orangecolor");
                rowLayout = initializeRowData(new Component[]{new Label(i + ""), nameLabel, new Label(ds.getType()), statusLabel, viewLabel, downloadLabel, deleteLabel}, false);
                bottomDataTable.addComponent(rowLayout);
            } else {
                nameLabel = new Label(ds.getName());
                ((Label) nameLabel).setDescription(ds.getName());
                rowLayout = initializeRowData(new Component[]{new Label(i + ""), nameLabel, new Label(ds.getType()), statusLabel, new Label(), downloadLabel, deleteLabel}, false);
                topDataTable.addComponent(rowLayout);
            }

            if (statusLabel.getStatus() == 1) {
                rowLayout.setEnabled(false);
            } else if (statusLabel.getStatus() == 2) {
                rowLayout.getComponent(0).setEnabled(false);
                rowLayout.getComponent(1).setEnabled(false);
                rowLayout.getComponent(4).setEnabled(false);
            }

            rowLayout.setData(ds.getGalaxyId());

            i++;
        }

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

    public abstract void deleteDataset(SystemDataSet ds);

    public abstract void viewDataset(PeptideShakerVisualizationDataset ds);
}
