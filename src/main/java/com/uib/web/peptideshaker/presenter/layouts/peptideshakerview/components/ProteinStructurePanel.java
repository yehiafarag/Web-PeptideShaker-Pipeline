package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components;

import com.compomics.util.experiment.biology.PTMFactory;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideObject;
import com.uib.web.peptideshaker.model.core.pdb.PDBMatch;
import com.uib.web.peptideshaker.model.core.pdb.PdbHandler;
import com.uib.web.peptideshaker.model.core.pdb.ChainBlock;
import com.vaadin.data.Property;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class represents 3D protein structure panel using JSMOL web service
 *
 * @author Yehia Farag
 */
public class ProteinStructurePanel extends AbsoluteLayout {

    private final VerticalLayout LiteMolPanel;
    private AbsoluteLayout chainCoverageLayout;

    private final LiteMOL3DComponent liteMOL3DComponent;
    private final Map<String, List<HashMap<String, Object>>> peptidesQueryMap;

    private int moleculeMode = 1;
    private final ComboBox pdbMatchesSelect;
    private final ComboBox pdbChainsSelect;
    private final Property.ValueChangeListener pdbMatchSelectlistener;
    private final Property.ValueChangeListener pdbChainsSelectlistener;
//    private final Button playBtn;
    private ChainCoverageComponent lastSelectedChainCoverage;
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
    private final int[] basicColor = new int[]{226, 226, 226};
    private String lastSelectedPeptideKey;
    private Collection<PeptideObject> proteinPeptides;
    private final Map<String, PeptideObject> proteinPeptidesMap;
    private final Map<String, List<ChainBlock>> pdbBlockMap;
    private int proteinSequenceLength;
    private final Label uniprotLabel;
    private final PdbHandler pdbHandler = new PdbHandler();
    private PDBMatch lastSelectedMatch;

    public ProteinStructurePanel() {
        ProteinStructurePanel.this.setSizeFull();
        ProteinStructurePanel.this.setStyleName("proteinStructiorpanel");

        this.pdbBlockMap = new HashMap<>();
        this.peptidesQueryMap = new LinkedHashMap<>();
        this.proteinPeptidesMap = new LinkedHashMap<>();

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
//        playBtn = new Button("Enable 3D", VaadinIcons.POWER_OFF);
//        playBtn.setStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
//        playBtn.addStyleName(ValoTheme.BUTTON_LINK);
//        ProteinStructurePanel.this.addComponent(playBtn, "left: 10px; top:-10px");
//
//        playBtn.setDescription("Click to start protein 3D structure");
//        playBtn.addClickListener((Button.ClickEvent event) -> {
//            if (playBtn.getCaption().contains("Enable 3D")) {
//                playBtn.setCaption("Disable 3D");
//                loadData(lastSelectedAccession, lastSelectedProteinSequence);
//            } else {
//                playBtn.setCaption("Enable 3D");
//                reset();
//                playBtn.setEnabled(true);
//            }
//        });

        uniprotLabel = new Label("UniProt:");
        uniprotLabel.addStyleName("selectchain3dMenue");
        uniprotLabel.setVisible(false);

        pdbChainsSelect = new ComboBox("Chains:");
        pdbChainsSelect.setTextInputAllowed(false);
        pdbChainsSelect.addStyleName("select3dMenue");
        pdbChainsSelect.addStyleName("selectchain3dMenue");
        pdbChainsSelect.setNullSelectionAllowed(false);
        pdbChainsSelect.setVisible(false);
        HorizontalLayout dropdownMenuesContainer = new HorizontalLayout();
        dropdownMenuesContainer.setWidth(321, Unit.PIXELS);
        dropdownMenuesContainer.setHeight(25, Unit.PIXELS);
        dropdownMenuesContainer.setStyleName("select3dMenue");

        ProteinStructurePanel.this.addComponent(dropdownMenuesContainer, "left: 10px; bottom:-12.5px");
        this.pdbChainsSelectlistener = ((Property.ValueChangeEvent event) -> {
            LiteMolPanel.setVisible(pdbChainsSelect.getValue() != null);
            if (LiteMolPanel.isVisible()) {
                lastSelectedChainCoverage.selectChain(pdbChainsSelect.getValue() + "");
                chainCoverageLayout.removeAllComponents();
                chainCoverageLayout.addComponent(lastSelectedChainCoverage.getChainCoverageWebComponent());
                contsructQueries(pdbBlockMap.get(pdbChainsSelect.getValue() + ""));
                selectPeptides(lastSelectedPeptideKey);

            }
        });
        pdbChainsSelect.addValueChangeListener(pdbChainsSelectlistener);
        pdbMatchesSelect = new ComboBox("PDB:") {
            @Override
            public void setVisible(boolean visible) {
                uniprotLabel.setVisible(visible);
                super.setVisible(visible);
            }

        };
        pdbMatchesSelect.setTextInputAllowed(false);
        pdbMatchesSelect.addStyleName("select3dMenue");
        pdbMatchesSelect.setNullSelectionAllowed(false);
        pdbMatchesSelect.setCaptionAsHtml(true);
        pdbMatchesSelect.setVisible(false);
        dropdownMenuesContainer.addComponent(uniprotLabel);
        dropdownMenuesContainer.setExpandRatio(uniprotLabel, 115);
        dropdownMenuesContainer.addComponent(pdbMatchesSelect);
        dropdownMenuesContainer.setExpandRatio(pdbMatchesSelect, 108);
        dropdownMenuesContainer.addComponent(pdbChainsSelect);
        dropdownMenuesContainer.setExpandRatio(pdbChainsSelect, 98);

        this.pdbMatchSelectlistener = ((Property.ValueChangeEvent event) -> {
            //time to active d3
            pdbChainsSelect.removeValueChangeListener(pdbChainsSelectlistener);
            pdbChainsSelect.removeAllItems();
            LiteMolPanel.setVisible(pdbMatchesSelect.getValue() != null);//            
            lastSelectedMatch = pdbHandler.updatePdbInformation(pdbMatchesSelect.getValue().toString(), lastSelectedProteinSequence, lastSelectedAccession);
            pdbBlockMap.clear();
            lastSelectedChainCoverage = reCalculateChainRange(lastSelectedMatch.getChains(), proteinSequenceLength);
            pdbChainsSelect.addItem("All");
            pdbChainsSelect.setItemCaption("All", "All");
            pdbChainsSelect.setItemIcon("All", new ExternalResource(lastSelectedChainCoverage.selectChain("All")));
            pdbBlockMap.put("All", new ArrayList<>());

            lastSelectedMatch.getChains().stream().map((chain) -> {
                if (!pdbBlockMap.containsKey(chain.getChain_id())) {
                    List<ChainBlock> blocks = new ArrayList<>();
                    pdbBlockMap.put(chain.getChain_id(), blocks);
                }
                return chain;
            }).map((chain) -> {
                pdbChainsSelect.addItem(chain.getChain_id());
                return chain;
            }).map((chain) -> {
                pdbChainsSelect.setItemCaption(chain.getChain_id(), chain.getChain_id());
                return chain;
            }).map((chain) -> {
                pdbChainsSelect.setItemIcon(chain.getChain_id(), new ExternalResource(lastSelectedChainCoverage.selectChain(chain.getChain_id())));
                return chain;
            }).map((chain) -> {
                pdbBlockMap.get(chain.getChain_id()).add(chain);
                return chain;
            }).forEachOrdered((chain) -> {
                pdbBlockMap.get("All").add(chain);
            });
            if (pdbChainsSelect.getItemIds().size() == 2) {
                pdbBlockMap.remove("All");
                pdbChainsSelect.removeItem("All");
            }
            pdbMatchesSelect.setDescription(pdbMatchesSelect.getItemCaption(pdbMatchesSelect.getValue()));
            pdbChainsSelect.addValueChangeListener(pdbChainsSelectlistener);
            pdbChainsSelect.setValue(pdbChainsSelect.getItemIds().iterator().next());

        });
        pdbMatchesSelect.addValueChangeListener(pdbMatchSelectlistener);

    }

    public void reset() {
//        playBtn.setEnabled(false);
        liteMOL3DComponent.reset3DView();
        pdbMatchesSelect.setVisible(false);
        pdbChainsSelect.setVisible(false);
        chainCoverageLayout.setVisible(false);
        uniprotLabel.setVisible(false);
        
//        liteMOL3DComponent.setVisible(false);
        
        lastSelectedPeptideKey = null;
//        playBtn.setCaption("Enable 3D");
    }

    public void activate3DProteinView() {
        boolean activate = liteMOL3DComponent.activate3DProteinView();
        if (!activate) {
            reset();
            return;
        }
//        if (pdbMatchesSelect.isVisible()) {
//            return;
//        }
        pdbMatchesSelect.setVisible(true);
        pdbChainsSelect.setVisible(true);
        chainCoverageLayout.setVisible(true);
        uniprotLabel.setVisible(true);
//        playBtn.setCaption("Disable 3D");
        loadData(lastSelectedAccession, lastSelectedProteinSequence);
    }

    public void updatePanel(Object accession, String proteinSequence, Collection<PeptideObject> proteinPeptides) {
//        playBtn.setEnabled(true);
        this.lastSelectedAccession = accession;
        this.lastSelectedProteinSequence = proteinSequence;
        this.uniprotLabel.setValue("UniProt: " + lastSelectedAccession);
        this.proteinPeptides = proteinPeptides;
//        if (playBtn.getCaption().equalsIgnoreCase("Enable 3D")) {
//            return;
//        }
        loadData(lastSelectedAccession, lastSelectedProteinSequence);

    }

    private void loadData(Object accessionObject, String proteinSequence) {
        if (accessionObject == null) {
            return;
        }
        pdbMatchesSelect.removeValueChangeListener(pdbMatchSelectlistener);
        pdbMatchesSelect.removeAllItems();
        pdbBlockMap.clear();
        String accession = accessionObject.toString();
        proteinSequenceLength = proteinSequence.length();
        Map<String, PDBMatch> pdbMachSet = pdbHandler.getData(accession);
        if (pdbMachSet != null && !pdbMachSet.isEmpty()) {
            pdbMachSet.keySet().forEach((str) -> {
                pdbMatchesSelect.addItem(str);
                pdbMatchesSelect.setItemCaption(str, str.toUpperCase() + " " + pdbMachSet.get(str).getDescription());

            });
            if (pdbMatchesSelect.getItemIds() == null) {
                Notification.show("No visulization available ", Notification.Type.TRAY_NOTIFICATION);
                reset();
                return;
            }
            pdbMatchesSelect.setVisible(true);
            pdbChainsSelect.setVisible(pdbMatchesSelect.isVisible());
            chainCoverageLayout.setVisible(pdbMatchesSelect.isVisible());
            pdbMatchesSelect.addValueChangeListener(pdbMatchSelectlistener);
            String pdbMachSelectValue = pdbMatchesSelect.getItemIds().toArray()[0] + "";
            pdbMatchesSelect.setValue(pdbMachSelectValue);
//            playBtn.addStyleName("poweron");

        } else {
            Notification.show("No visulization available ", Notification.Type.TRAY_NOTIFICATION);
            reset();
        }

    }

    private ChainCoverageComponent reCalculateChainRange(Collection<ChainBlock> chainBlocks, int proteinSequenceLength) {
        ChainCoverageComponent chainCoverage = new ChainCoverageComponent(proteinSequenceLength);
        chainBlocks.forEach((chain) -> {
            chainCoverage.addChainRange(chain.getChain_id(), chain.getStart_author_residue_number(), chain.getEnd_author_residue_number());
        });
        return chainCoverage;

    }

    /**
     * The post translational modifications factory.
     */
    private final PTMFactory PTM = PTMFactory.getInstance();

    private void selectPeptides(String peptideKey) {
        lastSelectedPeptideKey = peptideKey;
        String chainId = (pdbChainsSelect.getValue() + "");
        LinkedHashSet<HashMap> entriesSet = new LinkedHashSet<>();
        //color chains
        if (!chainId.equalsIgnoreCase("All")) {
            List<ChainBlock> chains = pdbBlockMap.get(pdbChainsSelect.getValue() + "");
            if (chains == null) {
                return;
            }
            chains.stream().map((selectedBlock) -> {
                int start = selectedBlock.getStart_residue_number();
                int end = selectedBlock.getEnd_residue_number();//we need color               
                HashMap chainSeq = initSequenceMap(selectedBlock.getChain_id(), lastSelectedMatch.getEntity_id(selectedBlock.getChain_id()), start, end, "valid", false, null, selectedBlock.getUniprot_chain_sequence());
                return chainSeq;
            }).map((chainSeq) -> {
                chainSeq.put("color", initColorMap(Color.WHITE));//selectedChainColor
                return chainSeq;
            }).forEachOrdered((chainSeq) -> {
                entriesSet.add(chainSeq);
            });
        }

        //set color based on protein overview radio btn
        switch (moleculeMode) {
            case 1:
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
                break;
            case 2: {
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
                break;
            }
            case 3: {

                Map<String, Color> peptideOverlappingMap = new HashMap<>();
                peptidesQueryMap.keySet().forEach((key) -> {
                    peptidesQueryMap.get(key).stream().filter((peptide) -> (chainId.contains("All") || chainId.equalsIgnoreCase(peptide.get("struct_asym_id").toString()))).map((peptide) -> {
                        if (!(boolean) peptide.get("modified")) {
                            if (peptideKey == null) { //select all
                                peptide.put("color", initColorMap(new int[]{211, 211, 211}));
                                peptide.put("color", initColorMap(Color.GRAY.darker()));
                            } else {
                                peptide.put("color", initColorMap(new int[]{245, 245, 245}));
                                peptide.put("color", initColorMap(Color.LIGHT_GRAY));
                            }
                        } else {
                            Color c = Color.ORANGE;
                            if (peptide.get("modifications").toString().split(",").length == 1) {
                                c = PTM.getColor(peptide.get("modifications").toString().trim());
                            }

                            if (peptideKey == null) {
                                peptide.put("color", initColorMap(c));
                            } else {

                                if (c.brighter().toString().equalsIgnoreCase(c.toString())) {
                                    c = new Color(Math.max(c.getRed(), 100), Math.max(c.getGreen(), 100), Math.max(c.getBlue(), 100));
                                    peptide.put("color", initColorMap(c));
                                } else {
                                    c = c.brighter().brighter();
                                    peptide.put("color", initColorMap(c));
                                }
                            }
                            if (!peptideOverlappingMap.containsKey(peptide.get("sequence_key").toString())) {
                                peptideOverlappingMap.put(peptide.get("sequence_key").toString(), c);
                            } else {
                                if (!peptideOverlappingMap.get(peptide.get("sequence_key").toString()).toString().equalsIgnoreCase(c.toString())) {
                                    if (peptideKey == null) {
                                        peptideOverlappingMap.put(peptide.get("sequence_key").toString(), Color.ORANGE);
                                    } else {
                                        peptideOverlappingMap.put(peptide.get("sequence_key").toString(), Color.ORANGE.brighter().brighter());
                                    }
                                }
                            }
                        }
                        return peptide;
                    }).forEachOrdered((peptide) -> {
                        entriesSet.add(peptide);
                    });
                });
                entriesSet.stream().filter((pep) -> (peptideOverlappingMap.containsKey(pep.get("sequence_key").toString()))).forEachOrdered((pep) -> {
                    pep.put("color", initColorMap(peptideOverlappingMap.get(pep.get("sequence_key").toString())));
                });
                if (peptideKey != null && peptidesQueryMap.containsKey(peptideKey)) {
                    peptidesQueryMap.get(peptideKey).stream().filter((peptide) -> (chainId.contains("All") || chainId.equalsIgnoreCase(peptide.get("struct_asym_id").toString()))).map((peptide) -> {
                        if (!(boolean) peptide.get("modified")) {
                            peptide.put("color", initColorMap(Color.GRAY.darker()));
                        } else {
                            Color c = Color.ORANGE;
                            if (peptide.get("modifications").toString().split(",").length == 1) {
                                c = PTM.getColor(peptide.get("modifications").toString().trim());
                            }
                            peptide.put("color", initColorMap(c));
                        }
                        return peptide;
                    }).forEachOrdered((peptide) -> {
                        entriesSet.add(peptide);
                    });
                }
                break;
            }
        }
        String pdbAccession = (pdbMatchesSelect.getValue() + "").toLowerCase();        
        liteMOL3DComponent.excuteQuery(pdbAccession, lastSelectedMatch.getEntity_id(chainId), chainId.toUpperCase(), initColorMap(basicColor), entriesSet);

    }

    public void selectPeptide(String peptideKey) {
        if (pdbMatchesSelect.isVisible()) {
            selectPeptides(peptideKey);
        }
    }

    private void contsructQueries(List<ChainBlock> selectedBlocks) {
        peptidesQueryMap.clear();
        this.proteinPeptidesMap.clear();
        this.proteinPeptides.forEach((peptide) -> {
            this.proteinPeptidesMap.put(peptide.getModifiedSequence(), peptide);
            this.peptidesQueryMap.put(peptide.getModifiedSequence(), new ArrayList<>());
            int start;
            Set<String> selectedChains = new LinkedHashSet<>();
            selectedBlocks.forEach((chain) -> {
                selectedChains.add(chain.getChain_id());
            });
            for (String chainId : selectedChains) {
                int current = 0;
                for (String seq : lastSelectedMatch.getSequence(chainId)) {
                    if (seq.toLowerCase().replace("i", "l").contains(peptide.getSequence().toLowerCase().replaceAll("i", "l"))) {
                        for (ChainBlock c : lastSelectedMatch.getChainBlocks(chainId)) {
                            start = seq.toLowerCase().replaceAll("i", "l").indexOf(peptide.getSequence().toLowerCase().replaceAll("i", "l"), current) + c.getStart_residue_number();
                            if (start == 0) {
                                continue;
                            }
                            int end = start + peptide.getSequence().length() - 1;
                            current = end;
                            String varMod = null;
                            if (!peptide.getVariableModifications().trim().equalsIgnoreCase("")) {
                                varMod = peptide.getVariableModifications();
                            }
                            peptidesQueryMap.get(peptide.getModifiedSequence()).add(initSequenceMap(chainId, lastSelectedMatch.getEntity_id(chainId), start, end, peptide.getValidation(), peptide.getModifiedSequence().contains("<"), varMod, peptide.getSequence()));
                            break;
                        }

                    }
                }
            }
        });

    }

    private HashMap<String, Object> initSequenceMap(String chainId, int entity_id, int start, int end, String validation, boolean modified, String modifications, String sequence) {
        HashMap<String, Object> sequenceMap = new HashMap<>();
        sequenceMap.put("entity_id", entity_id + "");
        sequenceMap.put("struct_asym_id", chainId.toUpperCase());
        sequenceMap.put("start_residue_number", start);
        sequenceMap.put("end_residue_number", end);
        sequenceMap.put("sequence_key", start + "_" + end);
        sequenceMap.put("validation", validation);
        sequenceMap.put("modified", modified);
        sequenceMap.put("modifications", modifications);
        sequenceMap.put("sequence", sequence);
        return sequenceMap;

    }

    private HashMap<String, Integer> initColorMap(int[] color) {
        HashMap<String, Integer> colorMap = new HashMap<>();
        colorMap.put("r", color[0]);
        colorMap.put("g", color[1]);
        colorMap.put("b", color[2]);
        return colorMap;
    }

    private HashMap<String, Integer> initColorMap(Color color) {
        HashMap<String, Integer> colorMap = new HashMap<>();
        colorMap.put("r", color.getRed());
        colorMap.put("g", color.getGreen());
        colorMap.put("b", color.getBlue());
        return colorMap;
    }

    public void setMode(int moleculeMode) {
        this.moleculeMode = moleculeMode;
        selectPeptide(lastSelectedPeptideKey);
    }

    public AbsoluteLayout getChainCoverageLayout() {
        return chainCoverageLayout;
    }

}
