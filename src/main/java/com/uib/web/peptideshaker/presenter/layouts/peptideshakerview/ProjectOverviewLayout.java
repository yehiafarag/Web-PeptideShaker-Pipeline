/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview;

import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.model.core.ClipboardUtil;
import com.uib.web.peptideshaker.model.core.LinkUtil;
import com.uib.web.peptideshaker.presenter.core.ActionLabel;
import com.uib.web.peptideshaker.presenter.core.CloseButton;
import com.uib.web.peptideshaker.presenter.core.PopupWindow;
import com.uib.web.peptideshaker.presenter.core.StatusLabel;
import com.uib.web.peptideshaker.presenter.core.form.Horizontal2Label;
import com.uib.web.peptideshaker.presenter.layouts.SearchSettingsLayout;
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
import com.vaadin.ui.PopupView;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.Map;
import sun.rmi.server.UnicastRef2;

/**
 *
 * @author y-mok
 */
public abstract class ProjectOverviewLayout extends AbsoluteLayout {
    
    private final Panel panelLayout;
    private final VerticalLayout peptideShakerVisualizationDatasetTable;
    private Component nameLabel;
    private boolean nelsSupported;
    private final LinkUtil linkUtil;
    private final float[] expandingRatio = new float[]{5f, 31f, 8f, 8f, 8f, 8f, 8f, 8f, 8f, 8f};
    private final PopupView addProject;

    public ProjectOverviewLayout() {
        ProjectOverviewLayout.this.setWidth(100, Unit.PERCENTAGE);
        ProjectOverviewLayout.this.setHeight(100, Unit.PERCENTAGE);
        
        panelLayout = new Panel();
        panelLayout.setStyleName(ValoTheme.PANEL_BORDERLESS);
        panelLayout.setWidth(100, Unit.PERCENTAGE);
        panelLayout.setHeight(100, Unit.PERCENTAGE);
        ProjectOverviewLayout.this.addComponent(panelLayout, "top:50px;left:10px;right:10px;bottom:0%;");
        this.linkUtil = new LinkUtil();
        
        addProject = new PopupView("Add Project", initUploadFilesLayout());
        ProjectOverviewLayout.this.addComponent(addProject, "right:10px;");
        addProject.setStyleName("addprojectbtn");
        addProject.setHideOnMouseOut(false);
        
        peptideShakerVisualizationDatasetTable = new VerticalLayout();
        peptideShakerVisualizationDatasetTable.setWidth(100, Unit.PERCENTAGE);
        peptideShakerVisualizationDatasetTable.setHeightUndefined();
        peptideShakerVisualizationDatasetTable.setSpacing(true);
        panelLayout.setContent(peptideShakerVisualizationDatasetTable);
        initHeaders();
    }
    
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
        ProjectOverviewLayout.this.addComponent(headerRow, "top:-30px;left:10px;right:10px");
        
    }
    
    private AbsoluteLayout initUploadFilesLayout() {
        AbsoluteLayout container = new AbsoluteLayout();
        container.setWidth(300, Unit.PIXELS);
        container.setHeight(300, Unit.PIXELS);
        container.setStyleName("uploadfilescontainer");
        Label titleLabel = new Label("<h1>Upload Files <i>Click " + VaadinIcons.INFO_CIRCLE_O.getHtml() + " for file formats</i></h1>", ContentMode.HTML);
        container.addComponent(titleLabel, "left:10px;top:10px");
        HorizontalLayout fastaFileRow = initUploadFileRow("Fasta File", "fasta", "File have to be in FASTA format");
        
        CloseButton closeBtn = new CloseButton() {
            @Override
            public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                addProject.setPopupVisible(false);
                
            }
        };
        container.addComponent(closeBtn, "right:10px;top:10px");    
        container.addComponent(fastaFileRow, "left:40px;top:10px");
        return container;
    }
    
    private HorizontalLayout initUploadFileRow(String title, String fileType, String infoImgSrc) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidth(100, Unit.PERCENTAGE);
        row.setHeight(50, Unit.PIXELS);
        row.setSpacing(true);        
        Label fileUploadName = new Label("<h2>" + title + "</h2>");
        row.addComponent(fileUploadName);
        return row;
        
    }

    public void updateDatasetsTable(Map<String, PeptideShakerVisualizationDataset> peptideShakerVisualizationDatasetMap) {
        
        nelsSupported = false;//(boolean) VaadinSession.getCurrent().getAttribute("nelsgalaxy");
        peptideShakerVisualizationDatasetTable.removeAllComponents();
        if (peptideShakerVisualizationDatasetMap == null || peptideShakerVisualizationDatasetMap.isEmpty()) {
            return;
        }
        int i = 1;
        for (PeptideShakerVisualizationDataset ds : peptideShakerVisualizationDatasetMap.values()) {
            
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
            getToGalaxyLabel.setVisible(nelsSupported);
            
            HorizontalLayout rowLayout;
            nameLabel = new ActionLabel(VaadinIcons.CLUSTER, ds.getName().split("___")[0], "PeptideShaker results ") {
                @Override
                public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                    ProjectOverviewLayout.this.setEnabled(false);
                    viewDataset(ds);
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
            
            String link = ((PeptideShakerVisualizationDataset) ds).getLinkToShare();
            if (link != null) {
                link = VaadinSession.getCurrent().getAttribute("galaxyServerUrl") + "toShare_-_" + linkUtil.encrypt(link);
                System.out.println("at link to galkaxy " + link);
                
            }
            ClipboardUtil shareLabel = new ClipboardUtil(link);
            
            SearchSettingsLayout dsOverview = new SearchSettingsLayout((PeptideShakerVisualizationDataset) ds, false) {
                private final PopupWindow tDsOverview = (PopupWindow) infoLabel;
                
                @Override
                public void saveSearchingFile(IdentificationParameters searchParameters, boolean isNew) {
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
            
            infoLabel.addStyleName("centeredicon");

            //0psiconHRNS
            String quant = null;
            String quantTooltip = "";
            if (((PeptideShakerVisualizationDataset) ds).isQuantDataset()) {
                quant = "Quant";
            }
            Label type = new Label(quant);
            type.setIcon(new ThemeResource("img/psiconHRNS.png"));
            type.setDescription(ds.getType() + " " + quantTooltip);
            type.setStyleName("smalliconlabel");
            
            rowLayout = initializeRowData(new Component[]{new Label(i + ""), nameLabel, type, infoLabel, shareLabel, getToGalaxyLabel, nelsLabel, downloadLabel, deleteLabel, statusLabel}, false);
            peptideShakerVisualizationDatasetTable.addComponent(rowLayout);
            
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
        
    }
    
    private HorizontalLayout initializeRowData(Component[] data, boolean header) {
        HorizontalLayout row = new HorizontalLayout();
        row.setSpacing(true);
        int i = 0;
        for (Component component : data) {
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
    
    public abstract void deleteDataset(PeptideShakerVisualizationDataset ds);
    
    public abstract boolean sendToNeLS(PeptideShakerVisualizationDataset ds);
    
    public abstract boolean getFromNels(PeptideShakerVisualizationDataset ds);
    
    public abstract void viewDataset(PeptideShakerVisualizationDataset ds);
}
