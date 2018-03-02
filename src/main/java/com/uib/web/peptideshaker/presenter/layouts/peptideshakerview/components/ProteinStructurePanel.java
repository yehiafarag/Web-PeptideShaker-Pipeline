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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents 3D protein structure panel using JSMOL web service
 *
 * @author Yehia Farag
 */
public class ProteinStructurePanel extends AbsoluteLayout {

    private final VerticalLayout LiteMolPanel;
    private AbsoluteLayout chainCoverageLayout;

    private final LiteMOL3DComponent liteMOL3DComponent;
    private final ConcurrentHashMap<String, WebFindPdbForUniprotAccessions> accessionToPDBMap;
    private final Map<String, List<HashMap<String, Object>>> peptidesQueryMap;

    private boolean moleculeMode = true;
    private final ComboBox pdbMatchesSelect;
    private final ComboBox pdbChainsSelect;
    private final Property.ValueChangeListener pdbMatchSelectlistener;
    private final Property.ValueChangeListener pdbChainsSelectlistener;
    private final Map<Object, List<Object[]>> pdbMachesTableData;
    private final Button playBtn;
    private ChainCoverageComponent lastSelectedChainCoverage; //    private final Button refreshBtn;
    private Object lastSelectedAccession;
    private String lastSelectedProteinSequence;

    private final int[] defaultSelectedBlueColor = new int[]{25, 125, 255};
    private final int[] defaultUnSelectedBlueColor = new int[]{132, 191, 249};
    private final int[] defaultSelectedConfidentColor = new int[]{0, 128, 0};
    private final int[] defaultUnselectedConfidenColor = new int[]{159, 242, 159};
    private final int[] defaultSelectedDoubtfulColor = new int[]{253, 167, 8};
    private final int[] defaultUnselectedDoubtfulColor = new int[]{255, 224, 192};
    private final int[] defaultSelectedNotValidatedColor = new int[]{233, 0, 0};
    private final int[] defaultUnselectedNotValidatedColor = new int[]{233, 146, 146};
    private final int[] defaultSelectedNotAvailableColor = new int[]{211, 211, 211};
    private final int[] defaultUnselectedNotAvailableColor = new int[]{246, 246, 246};
    private final int[] selectedChainColor = new int[]{163, 163, 163};
    private final int[] basicColor = new int[]{226, 226, 226};
    private String lastSelectedPeptideKey;
    private Set<PeptideObject> proteinPeptides;
    private final Map<String, List<PdbBlock>> pdbBlockMap;
    private final Map<String, PdbParameter> pdbToParamMap = new LinkedHashMap<>();
    private int proteinSequenceLength;

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

        chainCoverageLayout = new AbsoluteLayout() {
            @Override
            public void setVisible(boolean v) {
                if (this.getParent() != null) {
                    this.getParent().setVisible(v);
                }
                super.setVisible(v);
            }
        };
        chainCoverageLayout.setSizeFull();
        LiteMolPanel.addComponent(liteMOL3DComponent);
        ProteinStructurePanel.this.addComponent(LiteMolPanel);
        this.accessionToPDBMap = new ConcurrentHashMap<>();

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
        pdbChainsSelect.addStyleName("selectchain3dMenue");
        pdbChainsSelect.setNullSelectionAllowed(false);
        ProteinStructurePanel.this.addComponent(pdbChainsSelect, "left: 132px; bottom:0px");
        this.pdbChainsSelectlistener = ((Property.ValueChangeEvent event) -> {
            LiteMolPanel.setVisible(pdbChainsSelect.getValue() != null);
            if (LiteMolPanel.isVisible()) {
                lastSelectedChainCoverage.selectChain(pdbChainsSelect.getValue() + "");
                chainCoverageLayout.removeAllComponents();
                chainCoverageLayout.addComponent(lastSelectedChainCoverage.getChainCoverageWebComponent());
                contsructQueries(pdbBlockMap.get(pdbChainsSelect.getValue() + ""));
                selectPeptides(null);
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

            lastSelectedChainCoverage = reCalculateChainRange(pdbChains, proteinSequenceLength);
            pdbChainsSelect.addItem("All");
            pdbChainsSelect.setItemCaption("All", "All");
            pdbChainsSelect.setItemIcon("All", new ExternalResource(lastSelectedChainCoverage.selectChain("All")));
            pdbBlockMap.put("All", new ArrayList<>());

            for (PdbBlock chain : pdbChains) {

                if (!pdbBlockMap.containsKey(chain.getBlock())) {
                    List<PdbBlock> blocks = new ArrayList<>();
                    pdbBlockMap.put(chain.getBlock(), blocks);
                }
                pdbChainsSelect.addItem(chain.getBlock());
                pdbChainsSelect.setItemCaption(chain.getBlock(), chain.getBlock());
                pdbChainsSelect.setItemIcon(chain.getBlock(), new ExternalResource(lastSelectedChainCoverage.selectChain(chain.getBlock())));
                pdbBlockMap.get(chain.getBlock()).add(chain);
                pdbBlockMap.get("All").add(chain);

            }
            if (pdbChainsSelect.getItemIds().size() == 2) {
                pdbBlockMap.remove("All");
                pdbChainsSelect.removeItem("All");
            }
            pdbChainsSelect.setValue(pdbChainsSelect.getItemIds().iterator().next());

        });
        pdbMatchesSelect.addValueChangeListener(pdbMatchSelectlistener);
    }

    public void reset() {
        liteMOL3DComponent.reset3DView();
        pdbMatchesSelect.setVisible(false);
        pdbChainsSelect.setVisible(false);
        chainCoverageLayout.setVisible(false);
        playBtn.setCaption("Enable 3D");
        lastSelectedPeptideKey = "";
    }

    public void activate3DProteinView() {

        pdbMatchesSelect.setVisible(true);
        pdbChainsSelect.setVisible(true);
        chainCoverageLayout.setVisible(true);
        playBtn.setCaption("Disable 3D");
        loadData(lastSelectedAccession, lastSelectedProteinSequence);
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
    private  ExecutorService executor;

    public void updatePdbMap(Set<String> accessionList) {
        List<Callable<String>> tasks = new ArrayList<>();
        accessionList.stream().filter((accession) -> (!accessionToPDBMap.containsKey(accession))).forEachOrdered((accession) -> {
            WebFindPdbForUniprotAccessions temp = new WebFindPdbForUniprotAccessions(accession);
            accessionToPDBMap.put(accession, temp);
            Callable<String> task = temp.reProcessInformation();
            tasks.add(task);
        });

        try {
            executor = Executors.newSingleThreadExecutor();
            executor.invokeAll(tasks, Math.min(tasks.size() * 2, 10), TimeUnit.SECONDS); // Timeout of 10 minutes.
            executor.shutdown();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private void loadData(Object accessionObject, String proteinSequence) {

        pdbMatchesSelect.removeValueChangeListener(pdbMatchSelectlistener);
        pdbMatchesSelect.removeAllItems();
        pdbBlockMap.clear();
        pdbMachesTableData.clear();
        String accession = accessionObject.toString();
        if (!accessionToPDBMap.containsKey(accession)) {
            WebFindPdbForUniprotAccessions temp = new WebFindPdbForUniprotAccessions(accession);
            accessionToPDBMap.put(accession, temp);
        }
        proteinSequenceLength = proteinSequence.length();
        WebFindPdbForUniprotAccessions data = accessionToPDBMap.get(accession);
        while (!data.isValid()) {
            try {
                executor = Executors.newSingleThreadExecutor();
                executor.invokeAll(Arrays.asList(data.reProcessInformation()), 2, TimeUnit.SECONDS); // Timeout of 10 minutes.
                executor.shutdown();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

        }

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
            pdbToParamMap.put(param.getPdbaccession(), param);//         
        });

        pdbMatchesSelect.setVisible(!pdbMatchesSelect.getItemIds().isEmpty());
        pdbChainsSelect.setVisible(pdbMatchesSelect.isVisible());
        chainCoverageLayout.setVisible(pdbMatchesSelect.isVisible());
        pdbMatchesSelect.addValueChangeListener(pdbMatchSelectlistener);
        if (!pdbMatchesSelect.getItemIds().isEmpty()) {
            pdbMatchesSelect.setValue(pdbMatchesSelect.getItemIds().toArray()[0]);
            playBtn.addStyleName("poweron");
        } else {
            Notification.show("No visulization available ", Notification.Type.TRAY_NOTIFICATION);
            reset();
        }

    }

    private ChainCoverageComponent reCalculateChainRange(PdbBlock[] chainBlocks, int proteinSequenceLength) {
        ChainCoverageComponent chainCoverage = new ChainCoverageComponent(proteinSequenceLength);
        for (PdbBlock chain : chainBlocks) {
            chainCoverage.addChainRange(chain.getBlock(), chain.getStartProtein(), chain.getEndProtein());

        }
        return chainCoverage;

    }

    private void selectPeptides(String peptideKey) {
        lastSelectedPeptideKey = peptideKey;
        String chainId = (pdbChainsSelect.getValue() + "");
        LinkedHashSet<HashMap> entriesSet = new LinkedHashSet<>();

        //color chains
        if (!chainId.equalsIgnoreCase("All")) {
            List<PdbBlock> chains = pdbBlockMap.get(pdbChainsSelect.getValue() + "");
            chains.stream().map((selectedBlock) -> {
                int start = selectedBlock.getStartBlock();
                int end = selectedBlock.getEndBlock();//we need color               
                HashMap chainSeq = initSequenceMap(selectedBlock.getBlock(), start, end, "valid");
                return chainSeq;
            }).map((chainSeq) -> {
                chainSeq.put("color", initColorMap(selectedChainColor));
                return chainSeq;
            }).forEachOrdered((chainSeq) -> {
                entriesSet.add(chainSeq);
            });
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
        liteMOL3DComponent.excuteQuery(pdbAccession, chainId.toUpperCase(), initColorMap(basicColor), entriesSet);

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
            TreeMap<String, Integer> chainCalMap = new TreeMap<>();
            selectedBlocks.stream().map((selectedBlock) -> {
                if (!chainCalMap.containsKey(selectedBlock.getBlock())) {
                    chainCalMap.put(selectedBlock.getBlock(), Integer.MAX_VALUE);
                }
                return selectedBlock;
            }).forEachOrdered((selectedBlock) -> {
                chainCalMap.put(selectedBlock.getBlock(), Math.min(chainCalMap.get(selectedBlock.getBlock()), selectedBlock.getStartBlock()));
            });
            selectedBlocks.forEach((selectedBlock) -> {
                int updatedstart = start - (selectedBlock.getDifference() + chainCalMap.get(selectedBlock.getBlock()) - 2);
                int updatedend = updatedstart + (end - start + selectedBlock.getDifference());//end - selectedBlock.getDifference();
                if ((updatedstart >= selectedBlock.getStartBlock() - 1 && updatedend <= selectedBlock.getEndBlock())) {
                    peptidesQueryMap.get(peptide.getModifiedSequence()).add(initSequenceMap(selectedBlock.getBlock(), updatedstart, updatedend, peptide.getValidation()));
                }
            });

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

    public AbsoluteLayout getChainCoverageLayout() {
        return chainCoverageLayout;
    }
}
