package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components;

import com.compomics.util.pdbfinder.pdb.PdbBlock;
import com.compomics.util.pdbfinder.pdb.PdbParameter;
import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideObject;
import com.uib.web.peptideshaker.model.core.WebFindPdbForUniprotAccessions;
import com.vaadin.data.Property;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import static umontreal.iro.lecuyer.util.PrintfFormat.e;

/**
 * This class represents 3D protein structure panel using JSMOL web service
 *
 * @author Yehia Farag
 */
public class ProteinStructurePanel extends AbsoluteLayout {

    private final VerticalLayout LiteMolPanel;
//    private JSMOLComponent jSmolcomponent;
    private LiteMOL3DComponent liteMOL3DComponent;
    private final ConcurrentHashMap<String, WebFindPdbForUniprotAccessions> accessionToPDBMap;
    private final Map<String, List<HashMap<String, Object>>> peptidesQueryMap;
    private boolean moleculeMode = true;
    private final ComboBox pdbMatchesSelect;
    private final ComboBox pdbChainsSelect;
    private final Property.ValueChangeListener pdbMatchSelectlistener;
    private final Property.ValueChangeListener pdbChainsSelectlistener;
    private final Map<Object, List<Object[]>> pdbMachesTableData;
    private final Button playBtn;
//    private final Button refreshBtn;
    private Object lastSelectedAccession;
    private String lastSelectedProteinSequence;
//    private String lastQuerey;

//    private final VerticalLayout captionContainer;
    private Set<PeptideObject> proteinPeptides;
    private final Map<String, List<PdbBlock>> pdbBlockMap;

    public ProteinStructurePanel() {
        ProteinStructurePanel.this.setSizeFull();
        ProteinStructurePanel.this.setStyleName("proteinStructiorpanel");

        this.pdbBlockMap = new HashMap<>();
        this.peptidesQueryMap = new LinkedHashMap<>();

        this.pdbMachesTableData = new LinkedHashMap<>();
        LiteMolPanel = new VerticalLayout();
        LiteMolPanel.setSizeFull();

        liteMOL3DComponent = new LiteMOL3DComponent();
        liteMOL3DComponent.setSizeFull();
        liteMOL3DComponent.setVisible(true);

        LiteMolPanel.addComponent(liteMOL3DComponent);

        ProteinStructurePanel.this.addComponent(LiteMolPanel);
        this.accessionToPDBMap = new ConcurrentHashMap<>();
//        VerticalLayout container = new VerticalLayout();
//        PopupView popupContainer = new PopupView("", container) {
//            @Override
//            public void setPopupVisible(boolean visible) {
//                setVisible(visible);
//                super.setPopupVisible(visible); //To change body of generated methods, choose Tools | Templates.
//            }
//
//        };
//
//        popupContainer.setHideOnMouseOut(false);
//        popupContainer.setVisible(false);
//
//        container.setSizeFull();
//        container.setStyleName("popuptablecontainer");
//
//        captionContainer = new VerticalLayout();
//        captionContainer.setStyleName("clickablelabel");
//        captionContainer.setWidth(100, Unit.PIXELS);
//        
//        captionContainer.addLayoutClickListener((LayoutEvents.LayoutClickEvent event) -> {
//            popupContainer.setPopupVisible(true);
//        });

        playBtn = new Button("Enable 3D", VaadinIcons.POWER_OFF);
        playBtn.setStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
        playBtn.addStyleName(ValoTheme.BUTTON_LINK);
        ProteinStructurePanel.this.addComponent(playBtn, "left: 10px; top:-10px");

        playBtn.setDescription("Click to start protein 3D structure");
        playBtn.addClickListener((Button.ClickEvent event) -> {
            if (playBtn.getCaption().contains("Enable 3D")) {
                playBtn.setCaption("Disable 3D");
                loadData(lastSelectedAccession, lastSelectedProteinSequence);
            } else {
                reset();
            }
        });

        pdbChainsSelect = new ComboBox("Chains:");
//        pdbChainsSelect.addStyleName("select3dMenue");
        pdbChainsSelect.addStyleName("selectchain3dMenue");
        pdbChainsSelect.setNullSelectionAllowed(false);
//        pdbChainsSelect.setItemCaptionMode(AbstractSelect.ItemCaptionMode.ICON_ONLY);
        ProteinStructurePanel.this.addComponent(pdbChainsSelect, "left: 152px; bottom:0px");
        this.pdbChainsSelectlistener = ((Property.ValueChangeEvent event) -> {
            LiteMolPanel.setVisible(pdbChainsSelect.getValue() != null);
            if (LiteMolPanel.isVisible()) {

                contsructQueries(pdbBlockMap.get(pdbChainsSelect.getValue() + ""));

//                captionLabel.setValue(("PDB Match: " + pdbMatchesTable.getValue() + " <br/>PDB Chain: " + pdbChainsTable.getValue()).replace("null", "<font style='color:red'>Select from table</font>"));
                selectPeptides(null);
            } else {
//                lastQuerey = null;
            }

        });
        pdbChainsSelect.addValueChangeListener(pdbChainsSelectlistener);
        pdbMatchesSelect = new ComboBox("PDB:");
        pdbMatchesSelect.addStyleName("selectchain3dMenue");
        pdbMatchesSelect.setNullSelectionAllowed(false);
        pdbMatchesSelect.setCaptionAsHtml(true);
        pdbMatchesSelect.addStyleName("select3dMenue");

        ProteinStructurePanel.this.addComponent(pdbMatchesSelect, "left: 10px; bottom:0px");

        this.pdbMatchSelectlistener = ((Property.ValueChangeEvent event) -> {
            pdbChainsSelect.removeAllItems();
            LiteMolPanel.setVisible(pdbMatchesSelect.getValue() != null);

            pdbBlockMap.clear();
            PdbParameter param = pdbToParamMap.get(pdbMatchesSelect.getValue().toString());
            PdbBlock[] pdbChains = param.getBlocks();
            ChainCoverageComponent chainCoverage = reCalculateChainRange(pdbChains, proteinSequenceLength);
            pdbChainsSelect.addItem("All");
            pdbChainsSelect.setItemCaption("All", "All");
            pdbChainsSelect.setItemIcon("All", new ExternalResource(chainCoverage.selectChain("All")));
            pdbBlockMap.put("All", new ArrayList<>());

            for (PdbBlock chain : pdbChains) {

                if (!pdbBlockMap.containsKey(chain.getBlock())) {
//                    double range = Math.max(1.0, ((double) chain.getEndProtein() - chain.getStartProtein()));
//                    pdbChainTableData.add(new Object[]{chain.getBlock(), ("   [" + (chain.getStartProtein() + "," + chain.getEndProtein()) + "]   "), (((range) / proteinSequenceLength) * 100), chainCoverage.selectChain(chain.getBlock())});
                    List<PdbBlock> blocks = new ArrayList<>();
                    pdbBlockMap.put(chain.getBlock(), blocks);
                }
                pdbChainsSelect.addItem(chain.getBlock());
                pdbChainsSelect.setItemCaption(chain.getBlock(), chain.getBlock());
                pdbChainsSelect.setItemIcon(chain.getBlock(), new ExternalResource(chainCoverage.selectChain(chain.getBlock())));
                pdbBlockMap.get(chain.getBlock()).add(chain);
                pdbBlockMap.get("All").add(chain);

            }
            if (pdbChainsSelect.getItemIds().size() == 2) {
                pdbBlockMap.remove("All");
                pdbChainsSelect.removeItem("All");
            }

//            List<Object[]> tableData = pdbMachesTableData.get(pdbMatchesSelect.getValue());
//            tableData.forEach((row) -> {
//                pdbChainsSelect.addItem(row[0]);
//                pdbChainsSelect.setItemCaption(row[0], row[0] + "  - " + row[1] + "  " + row[2]);
//                //new ExternalResource(row[3].toString())
//            });
////            pdbChainsTable.setCaption("PDB Chains (" + pdbChainsTable.getItemIds().size() + ")");
////            captionLabel.setValue(("PDB Match: " + pdbMatchesTable.getValue() + " <br/>PDB Chain: " + pdbChainsTable.getValue()).replace("null", "<font style='color:red'>select from table</font>"));
            pdbChainsSelect.setValue(pdbChainsSelect.getItemIds().iterator().next());

//        );
        });
        pdbMatchesSelect.addValueChangeListener(pdbMatchSelectlistener);
//        pdbMatchesTable.addValueChangeListener(pdbMatchTablelistener);

        // Create the selection component
    }

    public void reset() {

        liteMOL3DComponent.reset3DView();
        pdbMatchesSelect.setVisible(false);
        pdbChainsSelect.setVisible(false);
        playBtn.setCaption("Enable 3D");
        lastSelectedPeptideKey = "";

//        playBtn.removeStyleName("poweron");
////        refreshBtn.setEnabled(false);
////        lastQuerey = null;
//        captionContainer.setVisible(false);
//        jSMolPanel.removeAllComponents();
    }

    public void activate3DProteinView() {

        pdbMatchesSelect.setVisible(true);
        pdbChainsSelect.setVisible(true);
        playBtn.setCaption("Disable 3D");
        loadData(lastSelectedAccession, lastSelectedProteinSequence);

//        playBtn.removeStyleName("poweron");
////        refreshBtn.setEnabled(false);
////        lastQuerey = null;
//        captionContainer.setVisible(false);
//        jSMolPanel.removeAllComponents();
    }

    public void updatePanel(Object accession, String proteinSequence, Set<PeptideObject> proteinPeptides) {
        this.lastSelectedAccession = accession;
        this.lastSelectedProteinSequence = proteinSequence;
        this.proteinPeptides = proteinPeptides;
        if (playBtn.getCaption().equalsIgnoreCase("Enable 3D")) {
            return;
        }
        loadData(lastSelectedAccession, lastSelectedProteinSequence);

    }
    private Thread updatePdbMapThread;
    private String currentprocessAcc;

    public void updatePdbMap(String accession) {
        try {
            if (updatePdbMapThread != null && updatePdbMapThread.isAlive()) {
                updatePdbMapThread.interrupt();
                accessionToPDBMap.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
            accessionToPDBMap.remove(currentprocessAcc);
        }
        updatePdbMapThread = new Thread(() -> {
//            accessionList.stream().filter((accession) -> (!accessionToPDBMap.containsKey(accession))).forEachOrdered((accession) -> {
            currentprocessAcc = accession;
            accessionToPDBMap.put(accession, new WebFindPdbForUniprotAccessions(accession));
//            });
        });
        updatePdbMapThread.start();

    }
    private final Map<String, PdbParameter> pdbToParamMap = new LinkedHashMap<>();
    private int proteinSequenceLength;

    private void loadData(Object accessionObject, String proteinSequence) {

        pdbMatchesSelect.removeValueChangeListener(pdbMatchSelectlistener);
        pdbMatchesSelect.removeAllItems();
        pdbBlockMap.clear();
        pdbMachesTableData.clear();
        String accession = accessionObject.toString();
        if (!accessionToPDBMap.containsKey(accession)) {
            accessionToPDBMap.put(accession, new WebFindPdbForUniprotAccessions(accession));
        }
        proteinSequenceLength = proteinSequence.length();

        WebFindPdbForUniprotAccessions data = accessionToPDBMap.get(accession);
        List<PdbParameter> resultsVictor = data.getPdbs();
        Collections.reverse(resultsVictor);

        pdbToParamMap.clear();

        resultsVictor.stream().map((param) -> {
            pdbMatchesSelect.addItem(param.getPdbaccession());
            return param;
        }).map((param) -> {
            pdbMatchesSelect.setItemCaption(param.getPdbaccession(), param.getPdbaccession() + "     (" + param.getTitle() + ") " + param.getExperiment_type() + " , " + param.getBlocks().length);
            return param;
        }).forEachOrdered((param) -> {
            pdbToParamMap.put(param.getPdbaccession(), param);
//            List<Object[]> pdbChainTableData = new ArrayList<>();
//            PdbBlock[] pdbChains = param.getBlocks();
//            ChainCoverageComponent chainCoverage = reCalculateChainRange(pdbChains, proteinSequenceLength);
//            pdbChainTableData.add(new Object[]{"All", "All Chains", chainCoverage.getCoverage() + " %", chainCoverage.selectChain("All")});
//            count = 2;
//            for (PdbBlock chain : pdbChains) {
//
//                if (!pdbBlockMap.containsKey(chain.getBlock())) {
//                    double range = Math.max(1.0, ((double) chain.getEndProtein() - chain.getStartProtein()));
//                    pdbChainTableData.add(new Object[]{chain.getBlock(), ("   [" + (chain.getStartProtein() + "," + chain.getEndProtein()) + "]   "), (((range) / proteinSequenceLength) * 100), chainCoverage.selectChain(chain.getBlock())});
//                    List<PdbBlock> blocks = new ArrayList<>();
//                    pdbBlockMap.put(chain.getBlock(), blocks);
//
//                }
//                pdbBlockMap.get(chain.getBlock()).add(chain);
//
//            }
//            System.out.println("chain blocks for " + param.getPdbaccession() + "  ----- " + pdbChainTableData.size());
//            pdbMachesTableData.put(param.getPdbaccession(), pdbChainTableData);
//            System.out.println("------------------------------------------------------");
        });

        pdbMatchesSelect.setVisible(!pdbMatchesSelect.getItemIds().isEmpty());
        pdbChainsSelect.setVisible(pdbMatchesSelect.isVisible());
//        pdbMatchesTable.setCaption("PDB Matches (" + pdbMatchesTable.getItemIds().size() + ")");
        pdbMatchesSelect.addValueChangeListener(pdbMatchSelectlistener);
        if (!pdbMatchesSelect.getItemIds().isEmpty()) {
            pdbMatchesSelect.setValue(pdbMatchesSelect.getItemIds().iterator().next());
            playBtn.addStyleName("poweron");
//            refreshBtn.setEnabled(true);
        } else {
            Notification.show("No visulization available ", Notification.Type.TRAY_NOTIFICATION);
            reset();
        }

    }

    private ChainCoverageComponent reCalculateChainRange(PdbBlock[] chainBlocks, int proteinSequenceLength) {
        ChainCoverageComponent chainCoverage = new ChainCoverageComponent(proteinSequenceLength);
        for (PdbBlock chain : chainBlocks) {
//            double range = Math.max(1.0, ((double) chain.getEndProtein() - chain.getStartProtein()));
            chainCoverage.addChainRange(chain.getBlock(), chain.getStartProtein(), chain.getEndProtein());

        }
        return chainCoverage;

    }
    private final int[] defaultSelectedBlueColor = new int[]{25, 125, 255};
    private final int[] defaultUnSelectedBlueColor = new int[]{191, 223, 255};
    private final int[] defaultSelectedConfidentColor = new int[]{0, 128, 0};
    private final int[] defaultUnselectedConfidenColor = new int[]{159, 242, 159};
    private final int[] defaultSelectedDoubtfulColor = new int[]{253, 167, 8};
    private final int[] defaultUnselectedDoubtfulColor = new int[]{255, 224, 192};
    private final int[] defaultSelectedNotValidatedColor = new int[]{233, 0, 0};
    private final int[] defaultUnselectedNotValidatedColor = new int[]{233, 146, 146};
    private final int[] defaultSelectedNotAvailableColor = new int[]{211, 211, 211};
    private final int[] defaultUnselectedNotAvailableColor = new int[]{246, 246, 246};
    private String lastSelectedPeptideKey;

    private void selectPeptides(String peptideKey) {
        lastSelectedPeptideKey = peptideKey;
        String chainId = (pdbChainsSelect.getValue() + "");
        LinkedHashSet<HashMap> entriesSet = new LinkedHashSet<>();

        //color chains
        if (!chainId.equalsIgnoreCase("All")) {
            List<PdbBlock> chains = pdbBlockMap.get(pdbChainsSelect.getValue() + "");
            for (PdbBlock selectedBlock : chains) {
                int start = selectedBlock.getStartBlock();
                int end = selectedBlock.getEndBlock();//we need color
                HashMap chainSeq = initSequenceMap(selectedBlock.getBlock(), start, end, "valid");
                 chainSeq.put("color", initColorMap(new int[]{255, 195, 206}));
                entriesSet.add(chainSeq);
            }
        }

        //set color based on protein overview radio btn
        if (moleculeMode) {
            int[] defaultSelectedColor = null;
            int[] defaultUnselectedColor;
            if (peptideKey == null) {
                defaultUnselectedColor = defaultSelectedBlueColor;
            } else {
                defaultUnselectedColor = defaultUnSelectedBlueColor;
                defaultSelectedColor = defaultSelectedBlueColor;
            }
            peptidesQueryMap.keySet().forEach((key) -> {
                peptidesQueryMap.get(key).stream().map((peptide) -> {
                    peptide.put("color", initColorMap(defaultUnselectedColor));
                    return peptide;
                }).forEachOrdered((peptide) -> {
                    entriesSet.add(peptide);
                });
            });

            if (peptideKey != null && peptidesQueryMap.containsKey(peptideKey)) {
                for (HashMap peptide : peptidesQueryMap.get(peptideKey)) {
                    if (chainId.contains("All") || chainId.equalsIgnoreCase(peptide.get("struct_asym_id").toString())) {
                        peptide.put("color", initColorMap(defaultSelectedColor));
                        entriesSet.add(peptide);
                    }
                }
            }
        } else {
            int[] selectedConfidentColor;
            int[] selectedDoubtfulColor;
            int[] selectedNotValidatedColor;
            int[] selectedNotAvailableColor;
            if (peptideKey == null) {
                selectedConfidentColor = defaultSelectedConfidentColor;
                selectedDoubtfulColor = defaultSelectedDoubtfulColor;
                selectedNotValidatedColor = defaultSelectedNotValidatedColor;
                selectedNotAvailableColor = defaultSelectedNotAvailableColor;
            } else {
                selectedConfidentColor = defaultUnselectedConfidenColor;
                selectedDoubtfulColor = defaultUnselectedDoubtfulColor;
                selectedNotValidatedColor = defaultUnselectedNotValidatedColor;
                selectedNotAvailableColor = defaultUnselectedNotAvailableColor;
            }

            peptidesQueryMap.keySet().forEach((key) -> {
                for (HashMap peptide : peptidesQueryMap.get(key)) {
                    switch (peptide.get("validation").toString()) {
                        case "Doubtful":
                            peptide.put("color", initColorMap(selectedDoubtfulColor));
                            break;
                        case "Confident":
                            peptide.put("color", initColorMap(selectedConfidentColor));
                            break;
                        case "Not Validated":
                            peptide.put("color", initColorMap(selectedNotValidatedColor));
                            break;
                        default:
                            peptide.put("color", initColorMap(selectedNotAvailableColor));
                            break;

                    }

                    entriesSet.add(peptide);
                }
            });

            if (peptideKey != null && peptidesQueryMap.containsKey(peptideKey)) {
                for (HashMap peptide : peptidesQueryMap.get(peptideKey)) {
                    if (chainId.contains("All") || chainId.equalsIgnoreCase(peptide.get("struct_asym_id").toString())) {
                        switch (peptide.get("validation").toString()) {
                            case "Doubtful":
                                peptide.put("color", initColorMap(defaultSelectedDoubtfulColor));
                                break;
                            case "Confident":
                                peptide.put("color", initColorMap(defaultSelectedConfidentColor));
                                break;
                            case "Not Validated":
                                peptide.put("color", initColorMap(defaultSelectedNotValidatedColor));
                                break;
                            default:
                                peptide.put("color", initColorMap(defaultSelectedNotAvailableColor));
                                break;

                        }
                        entriesSet.add(peptide);
                    }

                }
            }

        }

        String pdbAccession = (pdbMatchesSelect.getValue() + "").toLowerCase();

        liteMOL3DComponent.excuteQuery(pdbAccession, chainId.toUpperCase(), initColorMap(new int[]{255, 255, 255}), entriesSet);

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
        if (LiteMolPanel.isVisible()) {
            selectPeptides(peptideKey);
        }
    }

    private void contsructQueries(List<PdbBlock> selectedBlocks) {
        peptidesQueryMap.clear();

        this.proteinPeptides.forEach((peptide) -> {
            peptidesQueryMap.put(peptide.getModifiedSequence(), new ArrayList<>());
            int start = lastSelectedProteinSequence.indexOf(peptide.getSequence()) + 1;
            int end = lastSelectedProteinSequence.indexOf(peptide.getSequence()) + peptide.getSequence().length();

            for (PdbBlock selectedBlock : selectedBlocks) {
                start = start - selectedBlock.getDifference();
                end = end - selectedBlock.getDifference();//we need color
                peptidesQueryMap.get(peptide.getModifiedSequence()).add(initSequenceMap(selectedBlock.getBlock(), start, end, peptide.getValidation()));
            }

        });

    }

    private HashMap<String, Object> initSequenceMap(String chainId, int start, int end, String validation) {
        HashMap<String, Object> sequenceMap = new HashMap<>();
        sequenceMap.put("entity_id", "1");
        sequenceMap.put("struct_asym_id", chainId.toUpperCase());
        sequenceMap.put("start_residue_number", start);
        sequenceMap.put("end_residue_number", end);
        sequenceMap.put("validation", validation);
        return sequenceMap;

    }

    private HashMap<String, Integer> initColorMap(int[] color) {
        HashMap<String, Integer> colorMap = new HashMap<>();
        colorMap.put("r", color[0]);
        colorMap.put("g", color[1]);
        colorMap.put("b", color[2]);
        return colorMap;
    }

    public void setMode(boolean moleculeMode) {
        this.moleculeMode = moleculeMode;
        selectPeptide(lastSelectedPeptideKey);
    }
}

/**
 * $(document).ready(function() { $("#btnOutside").click(function () {
 * $('iframe[src="other.html"]').contents().find("#btnInside").click(); }); });*
 */
