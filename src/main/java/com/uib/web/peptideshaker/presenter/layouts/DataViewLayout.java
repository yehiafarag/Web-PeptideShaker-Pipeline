package com.uib.web.peptideshaker.presenter.layouts;

import com.compomics.util.experiment.identification.identification_parameters.SearchParameters;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.uib.web.peptideshaker.model.core.ClipboardUtil;
import com.uib.web.peptideshaker.model.core.LinkUtil;
import com.uib.web.peptideshaker.presenter.core.ActionLabel;
import com.uib.web.peptideshaker.presenter.core.FileOverviewLayout;
import com.uib.web.peptideshaker.presenter.core.PopupWindow;
import com.uib.web.peptideshaker.presenter.core.StatusLabel;
import com.uib.web.peptideshaker.presenter.core.Uploader;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
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

    private final Panel topPanelLayout;
    private final VerticalLayout topDataTable;

    private final Panel bottomPanelLayout;
    private final VerticalLayout bottomDataTable;
    private final LinkUtil linkUtil;
    private final float[] expandingRatio = new float[]{5f, 31f, 8f, 8f, 8f, 8f, 8f, 8f, 8f, 8f};
    private final AbsoluteLayout panelsContainers;

    /**
     * Constructor to initialise the main layout and attributes.
     */
    public DataViewLayout() {
        DataViewLayout.this.setWidth(100, Unit.PERCENTAGE);
        DataViewLayout.this.setHeight(100, Unit.PERCENTAGE);
        DataViewLayout.this.setStyleName("integratedframe");
        this.linkUtil = new LinkUtil();
        panelsContainers = new AbsoluteLayout();
//        panelsContainers.setMargin(new MarginInfo(true, true, true, true));
        panelsContainers.setWidth(100, Unit.PERCENTAGE);
        panelsContainers.setHeight(100, Unit.PERCENTAGE);
//        panelsContainers.setSpacing(true);
        DataViewLayout.this.setContent(panelsContainers);

        Uploader uploader = new Uploader() {
            @Override
            public void filesUploaded(PluploadFile[] uploadedFiles) {
                uploadToGalaxy(uploadedFiles);
            }

        };

        Label psProjectsLabel = new Label("<font style='color: rgb(71, 71, 71); font-size: 14px;font-weight: 400; line-height:40px !important;'>PeptideShaker Projects</font>", ContentMode.HTML);
        panelsContainers.addComponent(psProjectsLabel, "top:0px;left:10px;right:10px;bottom:50%;");
        psProjectsLabel.setHeight(20, Unit.PIXELS);
        topPanelLayout = new Panel();
        topPanelLayout.setStyleName(ValoTheme.PANEL_BORDERLESS);
        topPanelLayout.setWidth(100, Unit.PERCENTAGE);
        topPanelLayout.setHeight(100, Unit.PERCENTAGE);
        topPanelLayout.addStyleName("maxheight50per");
        panelsContainers.addComponent(topPanelLayout, "top:0px;left:10px;right:10px;bottom:50%;");
//        panelsContainers.setExpandRatio(topPanelLayout,0.5f);

        topDataTable = new VerticalLayout();
        topDataTable.setWidth(100, Unit.PERCENTAGE);
        topDataTable.setHeightUndefined();
        topDataTable.setSpacing(true);
        topPanelLayout.setContent(topDataTable);

        VerticalLayout spacer = new VerticalLayout();
        spacer.setHeight(10, Unit.PIXELS);
        spacer.setWidth(100, Unit.PERCENTAGE);
//        panelsContainers.addComponent(spacer);

        Label inputFilesLabel = new Label("<font style='color: rgb(71, 71, 71); font-size: 14px;font-weight: 400; line-height:40px !important;'>Input Files</font>", ContentMode.HTML);
        panelsContainers.addComponent(inputFilesLabel, "top:50%;left:10px;right:10px;bottom:50%;");
        bottomPanelLayout = new Panel();
        bottomPanelLayout.setStyleName(ValoTheme.PANEL_BORDERLESS);
        bottomPanelLayout.setWidth(100, Unit.PERCENTAGE);
        bottomPanelLayout.setHeight(100, Unit.PERCENTAGE);
//        bottomPanelLayout.setSpacing(true);
        bottomPanelLayout.addStyleName("maxheight50per");
//        bottomPanelLayout.setMargin(new MarginInfo(false, false, true, false));
        panelsContainers.addComponent(bottomPanelLayout, "top: 50%;left: 10px;right: 10px;bottom: 10px;");
        panelsContainers.addComponent(uploader, "top: 50%;right: 10px;");
        bottomDataTable = new VerticalLayout();
        bottomDataTable.setWidth(100, Unit.PERCENTAGE);
        bottomDataTable.setHeightUndefined();
        bottomDataTable.setSpacing(true);
        bottomPanelLayout.setContent(bottomDataTable);
        initHeaders();

    }
    private Component nameLabel;
    private boolean nelsSupported;

    private void initHeaders() {
        Label headerName = new Label("Name");
        Label headerType = new Label("Type");
        Label headerStatus = new Label("Valid");
        headerStatus.addStyleName("textalignmiddle");

        Label headerView = new Label("Information");
        headerView.addStyleName("textalignmiddle");

        Label headerShare = new Label("Share");
        headerShare.addStyleName("textalignmiddle");

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
        HorizontalLayout headerRow = initializeRowData(new Component[]{new Label(""), headerName, headerType, headerView, headerShare, headerGalaxy, headerNeLS, headerDownload, headerDelete, headerStatus}, true);
        headerRow.addStyleName("panelTableHeaders");
        panelsContainers.addComponent(headerRow, "top:0px;left:10px;right:10px;bottom:50%;");
        headerName = new Label("Name");
        headerType = new Label("Type");
        headerStatus = new Label("Valid");
        headerStatus.addStyleName("textalignmiddle");
        headerView = new Label("Information");
        headerView.addStyleName("textalignmiddle");
        headerShare = new Label("Share");
        headerShare.addStyleName("textalignmiddle");
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

        HorizontalLayout headerRow2 = initializeRowData(new Component[]{new Label(""), headerName, headerType, headerView, headerShare, headerGalaxy, headerNeLS, headerDownload, headerDelete, headerStatus}, true);
        headerRow2.addStyleName("panelTableHeaders");
        panelsContainers.addComponent(headerRow2, "top:50%;left:10px;right:10px;bottom:50%;");

    }

    public void updateDatasetsTable(Map<String, GalaxyFileObject> historyFilesMap) {

        nelsSupported = (boolean) VaadinSession.getCurrent().getAttribute("nelsgalaxy");
        topDataTable.removeAllComponents();
        bottomDataTable.removeAllComponents();

        int i = 1;
        for (GalaxyFileObject ds : historyFilesMap.values()) {
            if (ds.getName() == null || ds.getType().equalsIgnoreCase("FASTA File")) {
                continue;
            }
            Component infoLabel;
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

                nameLabel = new ActionLabel(VaadinIcons.CLUSTER, ds.getName().split("___")[0], "PeptideShaker results ") {
                    @Override
                    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                        DataViewLayout.this.setEnabled(false);
                        viewDataset((PeptideShakerVisualizationDataset) ds);
                    }

                };
                nameLabel.addStyleName("bluecolor");
                nameLabel.addStyleName("orangecolor");

                infoLabel = new PopupWindow("   ") {
                    @Override
                    public void onClosePopup() {
                    }

                };
                infoLabel.setIcon(VaadinIcons.INFO_CIRCLE_O);

//                ActionLabel shareLabel = new ActionLabel(VaadinIcons.LINK, "Share as link") {
//                    @Override
//                    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
//                        String link = ((PeptideShakerVisualizationDataset) ds).getLinkToShare();
//                        link = "http://localhost:8084/web-peptide-shaker/toShare_-_" + linkUtil.encrypt(link);
//                        clipboardUtil.CopyToClipboard(link);
//                    }
//
//                };
                String link = ((PeptideShakerVisualizationDataset) ds).getLinkToShare();
                if (link != null) {
                    link = "http://localhost:8084/web-peptide-shaker/toShare_-_" + linkUtil.encrypt(link);

                }
                ClipboardUtil shareLabel = new ClipboardUtil(link);

//                DatasetOverviewLayout dsOverview = new DatasetOverviewLayout((PeptideShakerVisualizationDataset) ds) {
//                    private final PopupWindow tDsOverview = (PopupWindow) infoLabel;
//
//                    @Override
//                    public void close() {
//                        ((PopupWindow) tDsOverview).setPopupVisible(false);
//                    }
//
//                };
                SearchSettingsLayout dsOverview = new SearchSettingsLayout((PeptideShakerVisualizationDataset) ds, false) {
                    private final PopupWindow tDsOverview = (PopupWindow) infoLabel;

                    @Override
                    public void saveSearchingFile(SearchParameters searchParameters, boolean isNew) {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public void cancel() {
                        ((PopupWindow) tDsOverview).setPopupVisible(false);
                    }

                };

                ((PeptideShakerVisualizationDataset) ds).setEnzyme(dsOverview.getEnzyme());
                ((PopupWindow) infoLabel).setContent(dsOverview);
                ((PopupWindow) infoLabel).setDescription("View search settings ");

                if (statusLabel.getStatus() == 2) {
                    statusLabel.setStatus("Some files are missings or corrupted please re-run SearchGUI-PeptideShaker-WorkFlow");
                }

//                viewLabel = new ActionLabel(VaadinIcons.COG, "View PeptideShaker results ") {
//                    @Override
//                    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
//                        viewDataset((PeptideShakerVisualizationDataset) ds);
//                    }
//
//                };
                infoLabel.addStyleName("centeredicon");

                //0psiconHRNS
                String quant = null;
                String quantTooltip="";
                if (((PeptideShakerVisualizationDataset) ds).isQuantDataset()) {
                    quant = "Quant";
                }
                Label type = new Label(quant);
                type.setIcon(new ThemeResource("img/psiconHRNS.png"));
                type.setDescription(ds.getType()+" "+quantTooltip);
                type.setStyleName("smalliconlabel");

                rowLayout = initializeRowData(new Component[]{new Label(i + ""), nameLabel, type, infoLabel, shareLabel, getToGalaxyLabel, nelsLabel, downloadLabel, deleteLabel, statusLabel}, false);
                topDataTable.addComponent(rowLayout);
            } else {
                infoLabel = new PopupWindow("   ") {
                    @Override
                    public void onClosePopup() {
                    }

                };
                infoLabel.setIcon(VaadinIcons.INFO_CIRCLE_O);
                infoLabel.addStyleName("centeredicon");
                FileOverviewLayout fileOverview = new FileOverviewLayout(ds) {
                    private final PopupWindow tFileOverview = (PopupWindow) infoLabel;

                    @Override
                    public void close() {
                        ((PopupWindow) tFileOverview).setPopupVisible(false);
                    }

                };
                ((PopupWindow) infoLabel).setContent(fileOverview);
                ((PopupWindow) infoLabel).setDescription("Search settings ");

                nameLabel = new Label(ds.getName());

                Label type = new Label();
                if (ds.getType().contains("JSON")) {
                    type.setValue("<b>JSON</b>");
                } else {
                    type.setValue("<b>" + ds.getType() + "</b>");
                }
                type.setContentMode(ContentMode.HTML);
                type.setDescription(ds.getType());
                ActionLabel shareLabel = new ActionLabel(VaadinIcons.LINK, "Share as link") {
                    @Override
                    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                        System.out.println("copy as link ?? ");
                    }

                };
                shareLabel.setEnabled(false);
//                ((Label) nameLabel).setDescription(ds.getName());
                rowLayout = initializeRowData(new Component[]{new Label(i + ""), nameLabel, type, infoLabel, shareLabel, getToGalaxyLabel, nelsLabel, downloadLabel, deleteLabel, statusLabel}, false);
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
