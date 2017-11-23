package com.uib.web.peptideshaker.presenter.components.peptideshakerview.components;

import com.compomics.util.pdbfinder.FindPdbForUniprotAccessions;
import com.compomics.util.pdbfinder.pdb.PdbBlock;
import com.compomics.util.pdbfinder.pdb.PdbParameter;
import com.ejt.vaadin.sizereporter.ComponentResizeEvent;
import com.ejt.vaadin.sizereporter.SizeReporter;
import com.vaadin.data.Property;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.ArrayList;
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
//    private BrowserFrame JSmolFrame;
    private final String ctxPath;
    private final Map<String, FindPdbForUniprotAccessions> accessionToPDBMap;
    private final Table pdbMatchesTable;
    private final Table pdbChainsTable;
    private final Property.ValueChangeListener pdbMatchTablelistener;
    private final Property.ValueChangeListener pdbChainsTablelistener;
    private final Map<Object, List<Object[]>> pdbMachesTableData;
    private final Button playBtn;
    private Object lastSelectedAccession;
    private String lastSelectedProteinSequence;
    private final Image backgroundImg;
    private final VerticalLayout captionContainer;

    public ProteinStructurePanel() {
        ProteinStructurePanel.this.setSizeFull();
        ProteinStructurePanel.this.setStyleName("proteinStructiorpanel");

        backgroundImg = new Image();
        backgroundImg.setSizeFull();
        backgroundImg.setSource(new ThemeResource("img/protein_structure_template.png"));
        ProteinStructurePanel.this.addComponent(backgroundImg);

        this.pdbMachesTableData = new LinkedHashMap<>();
        jSMolPanel = new VerticalLayout();
        jSMolPanel.setSizeFull();
        ProteinStructurePanel.this.addComponent(jSMolPanel);
        ctxPath = VaadinSession.getCurrent().getAttribute("ctxPath") + "";

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
        captionContainer.setWidth(150, Unit.PIXELS);
        ProteinStructurePanel.this.addComponent(captionContainer, "left: 10px; bottom:52px");
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
         * Initialize PDB Chains table
         *
         */
        pdbChainsTable = new Table("PDB Chains");
        pdbChainsTable.setSelectable(true);
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

        this.pdbMatchTablelistener = ((Property.ValueChangeEvent event) -> {
            pdbChainsTable.removeAllItems();
            jSMolPanel.setVisible(pdbMatchesTable.getValue() != null);
            if (pdbMatchesTable.getValue() == null) {
                pdbChainsTable.setCaption("PDB Chains");
                captionLabel.setValue(("PDB Match: " + pdbMatchesTable.getValue() + " <br/>PDB Chain: " + pdbChainsTable.getValue()).replace("null", "<font style='color:red'>Select from table</font>"));
                lastQuerey = null;
                return;
            }
            List<Object[]> tableData = pdbMachesTableData.get(pdbMatchesTable.getValue());
            for (Object[] row : tableData) {
                pdbChainsTable.addItem(row, row[0] + " " + row[1]);
            }
            pdbChainsTable.setCaption("PDB Chains (" + pdbChainsTable.getItemIds().size() + ")");
            pdbChainsTable.setValue(pdbChainsTable.getItemIds().iterator().next());

//        );
        });
        pdbMatchesTable.addValueChangeListener(pdbMatchTablelistener);

        playBtn = new Button("Enable 3D", VaadinIcons.POWER_OFF);
        playBtn.setStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
        ProteinStructurePanel.this.addComponent(playBtn, "left: 20px; top:-10px");
        SizeReporter reporter = new SizeReporter(jSMolPanel);
        reporter.addResizeListener((ComponentResizeEvent event) -> {
            if (lastQuerey != null && (playBtn.getStyleName().contains("poweron"))) {
                loadProteinStructure(lastQuerey, event.getWidth(), event.getHeight());
            }
        });
        playBtn.setDescription("Click to start protein 3D structure");
        playBtn.addClickListener((Button.ClickEvent event) -> {
            if (playBtn.getStyleName().contains("poweron")) {
                reset();
            } else {
                loadData(lastSelectedAccession);
                playBtn.addStyleName("poweron");
            }
        });
        this.pdbChainsTablelistener = ((Property.ValueChangeEvent event) -> {
            jSMolPanel.setVisible(pdbChainsTable.getValue() != null);
            if (jSMolPanel.isVisible()) {
//            "select resno >=" + (peptideTempStart - chains[selectedChainIndex - 1].getDifference())
//                        + " and resno <=" + (peptideTempEnd - chains[selectedChainIndex - 1].getDifference())
//                        + " and chain = " + currentChain + "; color green")
                lastQuerey = ctxPath + "/VAADIN/jsmol/index.html?pdb=" + pdbMatchesTable.getValue() +/*"&start="+start+"&end="+end+*/ "&chain=" + pdbChainsTable.getValue().toString().split(" ")[1] + "&color=" + "green";
                loadProteinStructure(lastQuerey, reporter.getWidth(), reporter.getHeight());
                captionLabel.setValue(("PDB Match: " + pdbMatchesTable.getValue() + " <br/>PDB Chain: " + pdbChainsTable.getValue()).replace("null", "<font style='color:red'>Select from table</font>"));
            } else {
                lastQuerey = null;
            }

        });
        pdbChainsTable.addValueChangeListener(pdbChainsTablelistener);

//        
//        updatePanel(new LinkedHashSet<>(Arrays.asList(new String[]{"P11021"})));
    }

    private void reset() {
        playBtn.removeStyleName("poweron");
        lastQuerey = null;
        captionContainer.setVisible(false);

        jSMolPanel.removeAllComponents();

    }

    public void updatePanel(Object accession, String proteinSequence, Set<Object> peptides) {
        this.lastSelectedAccession = accession;
        this.lastSelectedProteinSequence = proteinSequence;
        if (playBtn.getStyleName().contains("poweron")) {
            loadData(lastSelectedAccession);
        }

    }
    private String lastQuerey;

    private void loadData(Object accessionObject) {
        pdbMatchesTable.removeValueChangeListener(pdbMatchTablelistener);
        pdbMatchesTable.removeAllItems();
        pdbMachesTableData.clear();
        Thread t = new Thread(() -> {
            String accession = accessionObject.toString();
            if (!accessionToPDBMap.containsKey(accession)) {
                accessionToPDBMap.put(accession, new FindPdbForUniprotAccessions(accession, null));
            }
            int proteinSequenceLength = 100000;
            int index = 1;
            if (accessionToPDBMap.get(accession) != null) {
                Vector<PdbParameter> resultsVictor = accessionToPDBMap.get(accession).getPdbs();
                for (PdbParameter param : resultsVictor) {
                    pdbMatchesTable.addItem(new Object[]{index++, param.getPdbaccession(), param.getTitle(), param.getExperiment_type(), param.getBlocks().length}, param.getPdbaccession());
                    List<Object[]> pdbChainTableData = new ArrayList<>();
                    PdbBlock[] pdbChains = param.getBlocks();
                    int count = 1;
                    for (PdbBlock chain : pdbChains) {
                        pdbChainTableData.add(new Object[]{count++, chain.getBlock(), ("   [" + (chain.getStartProtein() + "," + chain.getEndProtein()) + "]   "), ((((double) chain.getEndProtein() - chain.getStartProtein()) / proteinSequenceLength) * 100)});
                    }
                    pdbMachesTableData.put(param.getPdbaccession(), pdbChainTableData);
                }
            }

            pdbMatchesTable.setVisible(!pdbMatchesTable.getItemIds().isEmpty());
            pdbChainsTable.setVisible(pdbMatchesTable.isVisible());
            pdbMatchesTable.setCaption("PDB Matches (" + pdbMatchesTable.getItemIds().size() + ")");
            pdbMatchesTable.addValueChangeListener(pdbMatchTablelistener);
            if (!pdbMatchesTable.getItemIds().isEmpty()) {
                pdbMatchesTable.setValue(pdbMatchesTable.getItemIds().iterator().next());
            } else {
                Notification.show("No visulization available ", Notification.Type.TRAY_NOTIFICATION);
            }
        });
        t.start();

    }

    private void loadProteinStructure(String querey, int w, int h) {
        if (w == -1 || h == -1) {
            return;
        }
//        w = w - 40;
//        h = h - 30;
        Thread t = new Thread(() -> {
            jSMolPanel.removeAllComponents();
            BrowserFrame JSmolFrame = new BrowserFrame();
            JSmolFrame.setSizeFull();
            jSMolPanel.addComponent(JSmolFrame);
            JSmolFrame.setSource(new ExternalResource(querey + "&w=" + w + "&h=" + h));
            JSmolFrame.setVisible(true);
            captionContainer.setVisible(true);
        });
        t.start();

    }

    public void mapPeptide(String peptideSequence) {

    }
}
