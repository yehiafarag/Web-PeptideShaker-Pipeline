package com.uib.web.peptideshaker.presenter.components.peptideshakerview.components;

import com.compomics.util.pdbfinder.pdb.PdbBlock;
import com.compomics.util.pdbfinder.pdb.PdbParameter;
import com.ejt.vaadin.sizereporter.SizeReporter;
import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideObject;
import com.uib.web.peptideshaker.model.core.WebFindPdbForUniprotAccessions;
import com.vaadin.data.Property;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * This class represents 3D protein structure panel using JSMOL web service
 *
 * @author Yehia Farag
 */
public class ProteinStructurePanel extends AbsoluteLayout {
    
    private final VerticalLayout jSMolPanel;
//    private JSMOLComponent jSmolcomponent;
    private LiteMOLComponent lightmolcomponent;
    private final Map<String, WebFindPdbForUniprotAccessions> accessionToPDBMap;
    private final Map<String, String> peptidesQueryMap;
    private final Table pdbMatchesTable;
    private final Table pdbChainsTable;
    private final Property.ValueChangeListener pdbMatchTablelistener;
    private final Property.ValueChangeListener pdbChainsTablelistener;
    private final Map<Object, List<Object[]>> pdbMachesTableData;
    private final Button playBtn;
//    private final Button refreshBtn;
    private Object lastSelectedAccession;
    private String lastSelectedProteinSequence;
//    private String lastQuerey;
    private final Image backgroundImg;
    private final VerticalLayout captionContainer;
    private Set<PeptideObject> proteinPeptides;
    private final Map<String, PdbBlock> pdbBlockMap;
    private final SizeReporter jSmolSizeReporter;
    
    public ProteinStructurePanel() {
        ProteinStructurePanel.this.setSizeFull();
        ProteinStructurePanel.this.setStyleName("proteinStructiorpanel");
        
        this.pdbBlockMap = new HashMap<>();
        this.peptidesQueryMap = new LinkedHashMap<>();
        backgroundImg = new Image();
        backgroundImg.setSizeFull();
        backgroundImg.setSource(new ThemeResource("img/protein_structure_template.png"));
        ProteinStructurePanel.this.addComponent(backgroundImg);
        
        this.pdbMachesTableData = new LinkedHashMap<>();
        jSMolPanel = new VerticalLayout();
        jSMolPanel.setSizeFull();
//
//        jSmolcomponent = new JSMOLComponent();
//        jSmolcomponent.setSizeFull();
//        jSmolcomponent.setVisible(false);
//        jSMolPanel.addComponent(jSmolcomponent);

        lightmolcomponent = new LiteMOLComponent();
        lightmolcomponent.setSizeFull();
        lightmolcomponent.setVisible(false);
        jSMolPanel.addComponent(lightmolcomponent);
        
        ProteinStructurePanel.this.addComponent(jSMolPanel);
        this.accessionToPDBMap = new LinkedHashMap<>();
        VerticalLayout container = new VerticalLayout();
        PopupView popupContainer = new PopupView("", container) {
            @Override
            public void setPopupVisible(boolean visible) {
                setVisible(visible);
                super.setPopupVisible(visible); //To change body of generated methods, choose Tools | Templates.
            }
            
        };
        
        popupContainer.setHideOnMouseOut(false);
        popupContainer.setVisible(false);
        
        container.setSizeFull();
        container.setStyleName("popuptablecontainer");
        
        captionContainer = new VerticalLayout();
        captionContainer.setStyleName("clickablelabel");
        captionContainer.setWidth(160, Unit.PIXELS);
        ProteinStructurePanel.this.addComponent(captionContainer, "left: 10px; bottom:43px");
        captionContainer.addLayoutClickListener((LayoutEvents.LayoutClickEvent event) -> {
            popupContainer.setPopupVisible(true);
        });
        captionContainer.addComponent(popupContainer);
        Label captionLabel = new Label("", ContentMode.HTML);
        captionContainer.addComponent(captionLabel);

        /**
         * Initialize PDB Matches table
         *
         */
        pdbMatchesTable = new Table("PDB Matches");
        pdbMatchesTable.setSelectable(true);
        pdbMatchesTable.setNullSelectionAllowed(false);
        pdbMatchesTable.setWidth(100, Unit.PERCENTAGE);
        pdbMatchesTable.setHeight(100, Unit.PERCENTAGE);
        pdbMatchesTable.setStyleName(ValoTheme.TABLE_COMPACT);
        pdbMatchesTable.addStyleName("singlerowtable");
        pdbMatchesTable.addContainerProperty("index", Integer.class, null, "", null, Table.Align.RIGHT);
        pdbMatchesTable.addContainerProperty("PDB", String.class, null, "PDB", null, Table.Align.LEFT);
        pdbMatchesTable.addContainerProperty("Title", String.class, null, "Title", null, Table.Align.LEFT);
        pdbMatchesTable.addContainerProperty("Type", String.class, null, "Type", null, Table.Align.LEFT);
        pdbMatchesTable.addContainerProperty("chains", Integer.class, null, "Chains", null, Table.Align.LEFT);
        pdbMatchesTable.setColumnExpandRatio("index", 0.05f);
        pdbMatchesTable.setColumnExpandRatio("PDB", 0.05f);
        pdbMatchesTable.setColumnExpandRatio("Title", 0.45f);
        pdbMatchesTable.setColumnExpandRatio("Type", 0.225f);
        pdbMatchesTable.setColumnExpandRatio("chains", 0.225f);
        container.addComponent(pdbMatchesTable);

        /**
         * Initialize PDB Chains table.
         *
         */
        pdbChainsTable = new Table("PDB Chains");
        pdbChainsTable.setSelectable(true);
        pdbChainsTable.setNullSelectionAllowed(false);
        pdbChainsTable.setWidth(100, Unit.PERCENTAGE);
        pdbChainsTable.setHeight(100, Unit.PERCENTAGE);
        pdbChainsTable.setStyleName(ValoTheme.TABLE_COMPACT);
        pdbChainsTable.addStyleName("singlerowtable");
        pdbChainsTable.addContainerProperty("index", Integer.class, null, "", null, Table.Align.RIGHT);
        pdbChainsTable.addContainerProperty("Chain", String.class, null, "Chain", null, Table.Align.LEFT);
        pdbChainsTable.addContainerProperty("PDB-Protein", String.class, null, "PDB-Protein", null, Table.Align.LEFT);
        pdbChainsTable.addContainerProperty("Coverage", Double.class, null, "Coverage", null, Table.Align.LEFT);
        pdbChainsTable.setColumnExpandRatio("index", 0.05f);
        pdbChainsTable.setColumnExpandRatio("Chain", 0.05f);
        pdbChainsTable.setColumnExpandRatio("PDB-Protein", 0.45f);
        pdbChainsTable.setColumnExpandRatio("Coverage", 0.45f);
        container.addComponent(pdbChainsTable);
        
        playBtn = new Button("Enable 3D", VaadinIcons.POWER_OFF);
        playBtn.setStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
        ProteinStructurePanel.this.addComponent(playBtn, "left: 20px; top:-10px");
        
//        refreshBtn = new Button("Refresh", VaadinIcons.REFRESH);
//        refreshBtn.setWidth(80, Unit.PIXELS);
//        refreshBtn.setStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
//        refreshBtn.addStyleName("plainbtn");
//        refreshBtn.setEnabled(false);
//        ProteinStructurePanel.this.addComponent(refreshBtn, "right: 100px; top:-10px");
        
        jSmolSizeReporter = new SizeReporter(jSMolPanel);
        playBtn.setDescription("Click to start protein 3D structure");
        playBtn.addClickListener((Button.ClickEvent event) -> {
            if (playBtn.getStyleName().contains("poweron")) {
                reset();
            } else {
                loadData(lastSelectedAccession, lastSelectedProteinSequence);
            }
        });
        this.pdbChainsTablelistener = ((Property.ValueChangeEvent event) -> {
            jSMolPanel.setVisible(pdbChainsTable.getValue() != null);
            if (jSMolPanel.isVisible()) {
                contsructQueries(pdbBlockMap.get(pdbChainsTable.getValue() + ""));
                captionLabel.setValue(("PDB Match: " + pdbMatchesTable.getValue() + " <br/>PDB Chain: " + pdbChainsTable.getValue()).replace("null", "<font style='color:red'>Select from table</font>"));
                selectPeptides(null);
            } else {
//                lastQuerey = null;
            }
            
        });
//        refreshBtn.addClickListener((Button.ClickEvent event) -> {
////            if (lastQuerey != null) {
//////                loadProteinStructure(lastQuerey, jSmolSizeReporter.getWidth(), jSmolSizeReporter.getHeight());
////            }
//        });
        pdbChainsTable.addValueChangeListener(pdbChainsTablelistener);
        this.pdbMatchTablelistener = ((Property.ValueChangeEvent event) -> {
            pdbChainsTable.removeAllItems();
            jSMolPanel.setVisible(pdbMatchesTable.getValue() != null);
            if (pdbMatchesTable.getValue() == null) {
                pdbChainsTable.setCaption("PDB Chains");
                captionLabel.setValue(("PDB Match: " + pdbMatchesTable.getValue() + " <br/>PDB Chain: " + pdbChainsTable.getValue()).replace("null", "<font style='color:red'>Select from table</font>"));
//                lastQuerey = null;
                return;
            }
            loadProteinStructure(pdbMatchesTable.getValue() + "");
            List<Object[]> tableData = pdbMachesTableData.get(pdbMatchesTable.getValue());
            for (Object[] row : tableData) {
                pdbChainsTable.addItem(row, row[0] + " " + row[1]);
            }
            pdbChainsTable.setCaption("PDB Chains (" + pdbChainsTable.getItemIds().size() + ")");
            captionLabel.setValue(("PDB Match: " + pdbMatchesTable.getValue() + " <br/>PDB Chain: " + pdbChainsTable.getValue()).replace("null", "<font style='color:red'>select from table</font>"));
            pdbChainsTable.setValue(pdbChainsTable.getItemIds().iterator().next());

//        );
        });
        pdbMatchesTable.addValueChangeListener(pdbMatchTablelistener);
        
    }
    
    private void reset() {
        playBtn.removeStyleName("poweron");
//        refreshBtn.setEnabled(false);
//        lastQuerey = null;
        captionContainer.setVisible(false);
//        jSMolPanel.removeAllComponents();

    }
    
    public void updatePanel(Object accession, String proteinSequence, Set<PeptideObject> proteinPeptides) {
        this.lastSelectedAccession = accession;
        this.lastSelectedProteinSequence = proteinSequence;
        this.proteinPeptides = proteinPeptides;
        if (playBtn.getStyleName().contains("poweron")) {
            loadData(lastSelectedAccession, lastSelectedProteinSequence);
        }
        
    }
    
    private void loadData(Object accessionObject, String proteinSequence) {
        pdbMatchesTable.removeValueChangeListener(pdbMatchTablelistener);
        pdbMatchesTable.removeAllItems();
        pdbBlockMap.clear();
        pdbMachesTableData.clear();
        String accession = accessionObject.toString();
        if (!accessionToPDBMap.containsKey(accession)) {
            accessionToPDBMap.put(accession, new WebFindPdbForUniprotAccessions(accession));
        }
        int proteinSequenceLength = proteinSequence.length();
        int index = 1;
        WebFindPdbForUniprotAccessions data = accessionToPDBMap.get(accession);
        Vector<PdbParameter> resultsVictor = data.getPdbs();
        for (PdbParameter param : resultsVictor) {
            pdbMatchesTable.addItem(new Object[]{index++, param.getPdbaccession(), param.getTitle(), param.getExperiment_type(), param.getBlocks().length}, param.getPdbaccession());
            List<Object[]> pdbChainTableData = new ArrayList<>();
            PdbBlock[] pdbChains = param.getBlocks();
            int count = 1;
            for (PdbBlock chain : pdbChains) {
                pdbChainTableData.add(new Object[]{count, chain.getBlock(), ("   [" + (chain.getStartProtein() + "," + chain.getEndProtein()) + "]   "), ((((double) chain.getEndProtein() - chain.getStartProtein()) / proteinSequenceLength) * 100)});
                pdbBlockMap.put((count++) + " " + chain.getBlock(), chain);
            }
            pdbMachesTableData.put(param.getPdbaccession(), pdbChainTableData);
        }
        
        pdbMatchesTable.setVisible(!pdbMatchesTable.getItemIds().isEmpty());
        pdbChainsTable.setVisible(pdbMatchesTable.isVisible());
        pdbMatchesTable.setCaption("PDB Matches (" + pdbMatchesTable.getItemIds().size() + ")");
        pdbMatchesTable.addValueChangeListener(pdbMatchTablelistener);
        if (!pdbMatchesTable.getItemIds().isEmpty()) {
            pdbMatchesTable.setValue(pdbMatchesTable.getItemIds().iterator().next());
            playBtn.addStyleName("poweron");
//            refreshBtn.setEnabled(true);
        } else {
            Notification.show("No visulization available ", Notification.Type.TRAY_NOTIFICATION);
            reset();
        }
    }
    
    private void loadProteinStructure(String pdbAccession) {
//        jSmolcomponent.setVisible(true);
//        jSmolcomponent.loadProtein(pdbAccession);
        lightmolcomponent.setVisible(true);
        lightmolcomponent.loadProtein(pdbAccession);
        
    }
    
    private void selectPeptides(String peptideKey) {
        
        for (String key : peptidesQueryMap.keySet()) {
            String updatedQuery = peptidesQueryMap.get(key).split(";")[0] + ";" + "color black;";
            peptidesQueryMap.put(key, updatedQuery);
        }
        if (peptideKey != null && peptidesQueryMap.containsKey(peptideKey)) {
            String updatedQuery = peptidesQueryMap.get(peptideKey).split(";")[0] + ";" + "color red;";
            peptidesQueryMap.put(peptideKey, updatedQuery);
        }
        String query = "select all; color whitesmoke;";
        for (String peptideQuery : peptidesQueryMap.values()) {
            query += peptideQuery + "";
        }
//        jSmolcomponent.excuteQuery(query);
        lightmolcomponent.excuteQuery(query);

//        System.out.println("to update protein structure");
//        Thread t = new Thread(() -> {
//            jSMolPanel.removeAllComponents();
//            JSmolFrame = new BrowserFrame();
//            JSmolFrame.setSizeFull();
//            jSMolPanel.addComponent(JSmolFrame);
//            JSmolFrame.setVisible(true);
//            captionContainer.setVisible(true);
//        });
//        t.start();
    }
    
    public void selectPeptide(String peptideKey) {
        if (jSMolPanel.isVisible()) {
            selectPeptides(peptideKey);
        }
    }
    
    private void contsructQueries(PdbBlock selectedBlock) {
        peptidesQueryMap.clear();
        for (PeptideObject peptide : this.proteinPeptides) {
            String color = "black";
            int start = lastSelectedProteinSequence.indexOf(peptide.getSequence());
            int end = lastSelectedProteinSequence.indexOf(peptide.getSequence()) + peptide.getSequence().length();
            String query = "select resno >=" + (start - selectedBlock.getDifference())
                    + " and resno <=" + (end - selectedBlock.getDifference())
                    + " and chain = " + selectedBlock.getBlock() + "; color " + color + ";";
            
            System.out.println("the siffrent " + peptide.getSequence() + "   " + lastSelectedProteinSequence.contains(peptide.getSequence()) + "  start  " + start + "   end  " + end);
            System.out.println("the       to " + peptide.getModifiedSequence());
            System.out.println("at ----------------------------------------------");
            peptidesQueryMap.put(peptide.getModifiedSequence(), query);
        }
        
    }
}

/**
 * $(document).ready(function() { $("#btnOutside").click(function () {
 * $('iframe[src="other.html"]').contents().find("#btnInside").click(); }); });*
 */
