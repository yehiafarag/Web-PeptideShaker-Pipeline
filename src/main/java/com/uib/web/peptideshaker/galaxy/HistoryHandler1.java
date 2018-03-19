package com.uib.web.peptideshaker.galaxy;
//
//import com.uib.web.peptideshaker.galaxy.dataobjects.GalaxyFile;
//import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideShakerVisualizationDataset;
//import com.uib.web.peptideshaker.galaxy.dataobjects.SystemDataSet;
//import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
//import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
//import com.github.jmchilton.blend4j.galaxy.beans.History;
//import com.github.jmchilton.blend4j.galaxy.beans.HistoryContents;
//import com.github.jmchilton.blend4j.galaxy.beans.HistoryContentsProvenance;
//import com.github.wolfie.refresher.Refresher;
//import com.uib.web.peptideshaker.PeptidShakerUI;
//import com.uib.web.peptideshaker.galaxy.dataobjects.GalaxyFastaFileReader;
//import com.vaadin.server.VaadinSession;
//import com.vaadin.ui.Notification;
//
//import com.vaadin.ui.UI;
//import java.io.File;
//import java.util.HashSet;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.function.Consumer;

/**
 * This class represents Galaxy history (galaxy file system) in Peptide-Shaker
 * web application the class is responsible for handling files in galaxy history
 * and shares it cross the application
 *
 * @author Yehia Farag
 */
public abstract class HistoryHandler1 {

//    /**
//     * The main Galaxy instance in the system.
//     */
//    private final GalaxyInstance Galaxy_Instance;
//    /**
//     * The main Search settings .par File Map.
//     */
//    private final Map<String, GalaxyFile> searchSetiingsFilesMap;
//    /**
//     * The main FASTA File Map.
//     */
//    private final Map<String, SystemDataSet> fastaFilesMap;
//    /**
//     * The main MGF Files Map.
//     */
//    private final Map<String, SystemDataSet> mgfFilesMap;
//    /**
//     * The main MGF and FASTA index Files Map.
//     */
//    private final Map<String, GalaxyFile> indexFilesMap;
//    /**
//     * The Full historyFiles Map.
//     */
//    private final Map<String, SystemDataSet> historyFilesMap;
//
//    /**
//     * The main SearchGUI Files Map.
//     */
//    private final Map<String, HistoryContentsProvenance> searchGUIFilesMap;
//    /**
//     * The main PeptideShaker Visualization Map.
//     */
//    private final Map<String, PeptideShakerVisualizationDataset> peptideShakerVisualizationMap;
//    /**
//     * The Working galaxy history.
//     */
//    private History workingHistory;
//    private final LinkedHashMap<String, String> NeLSFilesMap;
//    ;
//    private final Set<String> historiesIds;
//    private final File userFolder;
//    /**
//     * History progress icon
//     *
//     */
////    private final Window progressWindow;
//    /**
//     * Refresher to keep tracking history state in galaxy
//     *
//     */
//    private final Refresher REFRESHER;
//
//    private final GalaxyFastaFileReader fastaFileReader;
//
//    public Map<String, PeptideShakerVisualizationDataset> getPeptideShakerVisualizationMap() {
//        return peptideShakerVisualizationMap;
//    }
//
//    /**
//     * Constructor to initialise the main data structure and other variables.
//     *
//     * @param Galaxy_Instance the main Galaxy instance in the system
//     * @param userFolder user folder to store users file temporarily
//     *
//     */
//    public HistoryHandler1(GalaxyInstance Galaxy_Instance, File userFolder) {
//        this.Galaxy_Instance = Galaxy_Instance;
//        this.searchSetiingsFilesMap = new LinkedHashMap<>();
//        this.fastaFilesMap = new LinkedHashMap<>();
//        this.mgfFilesMap = new LinkedHashMap<>();
//        this.indexFilesMap = new LinkedHashMap<>();
//        this.peptideShakerVisualizationMap = new LinkedHashMap<>();
//        this.searchGUIFilesMap = new LinkedHashMap<>();
//        this.historyFilesMap = new LinkedHashMap<>();
//        this.historiesIds = new HashSet<>();
//        this.NeLSFilesMap = new LinkedHashMap<>();
//        this.fastaFileReader = new GalaxyFastaFileReader();
//
//        REFRESHER = new Refresher();
//        ((PeptidShakerUI) UI.getCurrent()).addExtension(REFRESHER);
//        this.userFolder = userFolder;
//        this.updateHistoryDatastructure(null);
//
//    }
//
//    /**
//     * Get the main Search settings par files Map
//     *
//     * @return searchSetiingsFilesMap
//     */
//    public Map<String, GalaxyFile> getSearchSettingsFilesMap() {
//        return searchSetiingsFilesMap;
//    }
//
//    /**
//     * Get the main FASTA files Map
//     *
//     * @return fastaFilesMap
//     */
//    public Map<String, SystemDataSet> getFastaFilesMap() {
//        return fastaFilesMap;
//    }
//
//    /**
//     * Get the main MGF files Map
//     *
//     * @return mgfFilesMap
//     */
//    public Map<String, SystemDataSet> getMgfFilesMap() {
//        return mgfFilesMap;
//    }
//
//    public Map<String, SystemDataSet> getHistoryFilesMap() {
//        return historyFilesMap;
//    }
//
//    public void updateHistory(PeptideShakerVisualizationDataset tempWorkflowOutput) {
//        systemIsBusy(true, historyFilesMap);
//        int mSec = 4000;
//        if (tempWorkflowOutput != null) {
//            mSec = 30000;
//        }
//        if (!isReadyHistory(mSec)) {
//            return;
//        }
//        this.updateHistoryDatastructure(tempWorkflowOutput);
//
//    }
//
//    /**
//     * Update the FASTA and MGF and peptide Shaker Visualisation maps
//     *
//     * @return mgfFilesMap
//     */
//    private void updateHistoryDatastructure(PeptideShakerVisualizationDataset tempWorkflowOutput) {
//
//        try {
//            NeLSFilesMap.clear();
//            NeLSFilesMap.putAll((LinkedHashMap<String, String>) VaadinSession.getCurrent().getAttribute("nelsFilesMap"));
//            fastaFilesMap.clear();
//            mgfFilesMap.clear();
//            searchGUIFilesMap.clear();
//            historyFilesMap.clear();
//            peptideShakerVisualizationMap.clear();
//            searchSetiingsFilesMap.clear();
//            historiesIds.clear();
//            indexFilesMap.clear();
//            HistoriesClient galaxyHistoriesClient = Galaxy_Instance.getHistoriesClient();
//            List<History> historiesList = galaxyHistoriesClient.getHistories();
//            if (historiesList.isEmpty()) {
//                galaxyHistoriesClient.create(new History("Online-PeptideShaker-History"));
//                workingHistory = galaxyHistoriesClient.create(new History("Online-PeptideShaker-Job-History"));
//            } else {
//                for (History h : historiesList) {
//                    if (h.getName().equalsIgnoreCase("Online-PeptideShaker-Job-History")) {
//                        workingHistory = h;
//                    }
//                }
//                if (workingHistory == null) {
//                    workingHistory = Galaxy_Instance.getHistoriesClient().create(new History("Online-PeptideShaker-Job-History"));
//                }
//            }
//            historiesList = galaxyHistoriesClient.getHistories();
//            Map<String, Map<String, Object>> workHistoryData = new LinkedHashMap<>();
//
//            for (History history : historiesList) {
//                historiesIds.add(history.getId());
//                final String query = "select * from hda where history_id= '" + history.getId() + "'";
//                List<Map<String, Object>> results = Galaxy_Instance.getSearchClient().search(query).getResults();
//                results.stream().filter((map) -> !(map.get("purged").toString().equalsIgnoreCase("true") || map.get("deleted").toString().equalsIgnoreCase("true"))).forEachOrdered(new Consumer<Map<String, Object>>() {
//                    @Override
//                    public void accept(Map<String, Object> map) {
//                        if (map.get("data_type").toString().equalsIgnoreCase("galaxy.datatypes.binary.SearchGuiArchive")) {
//                            HistoryContentsProvenance prov = galaxyHistoriesClient.showProvenance(workingHistory.getId(), map.get("id").toString());
//                            searchGUIFilesMap.put(map.get("name").toString().replace("-SearchGUI Results", ""), prov);
//                        } else if (map.get("data_type").toString().equalsIgnoreCase("abc.CompressedArchive") || map.get("data_type").toString().equalsIgnoreCase("galaxy.datatypes.tabular.Tabular") || map.get("data_type").toString().equalsIgnoreCase("galaxy.datatypes.binary.CompressedZipArchive")) {
//                            String projectId = map.get("name").toString().split("-")[0];
//                            if (!peptideShakerVisualizationMap.containsKey(projectId)) {
//                                PeptideShakerVisualizationDataset vDs = new PeptideShakerVisualizationDataset(projectId);
//                                peptideShakerVisualizationMap.put(projectId, vDs);
//                                HistoryContentsProvenance prov = galaxyHistoriesClient.showProvenance(workingHistory.getId(), map.get("id").toString());
//                                vDs.setJobId("_PS_" + prov.getJobId());
//                                vDs.setGalaxyId("_PS_" + prov.getJobId());
//                                vDs.setHistoryId(history.getId());
//                                vDs.setType("Web Peptide Shaker Dataset");
//
//                            }
//                            PeptideShakerVisualizationDataset vDs = peptideShakerVisualizationMap.get(projectId);
//                            if (map.get("name").toString().endsWith("-Proteins")) {
//                                vDs.setType("Web Peptide Shaker Dataset");
//                                vDs.setName(map.get("name").toString().replace("-Proteins", ""));
//                                if (!map.get("misc_blurb").toString().equalsIgnoreCase("error")) {
//                                    int i;
//                                    try {
//                                        i = Integer.valueOf(map.get("misc_blurb").toString().replace(" lines", "").replace(",", "")) - 1;
//                                    } catch (NumberFormatException exp) {
//                                        i = 0;
//                                    }
//                                    vDs.setProteinsNumber(i);
//                                }
//                                vDs.setProteinFileId(map.get("id").toString());
//                                SystemDataSet ds = new SystemDataSet();
//                                ds.setName(map.get("name").toString());
//                                ds.setType("Proteins file");
//                                ds.setHistoryId(history.getId());
//                                ds.setGalaxyId(map.get("id").toString());
//                                ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + "/datasets/" + ds.getGalaxyId() + "/display");
//                                ds.setNelsKey(map.get("name") + "", map.get("file_ext") + "");
//                                ds.setAvailableOnNels(NeLSFilesMap.containsKey(ds.getNelsKey()));
//                                GalaxyFile file = new GalaxyFile(userFolder, ds, false);
//                                file.setNelsKey(map.get("name") + "", map.get("file_ext") + "");
//                                file.setAvailableOnNels(NeLSFilesMap.containsKey(file.getNelsKey()));
//                                NeLSFilesMap.remove(file.getNelsKey());
//                                file.setDownloadUrl("to_ext=" + map.get("file_ext").toString());
//                                vDs.setProteinFile(file);
//
//                            } else if (map.get("name").toString().endsWith("-Peptides")) {
//                                if (!map.get("misc_blurb").toString().equalsIgnoreCase("error")) {
//                                    int i;
//                                    try {
//                                        i = Integer.valueOf(map.get("misc_blurb").toString().replace(" lines", "").replace(",", "")) - 1;
//                                    } catch (NumberFormatException exp) {
//                                        i = 0;
//                                    }
//                                    vDs.setPeptidesNumber(i);
//                                }
//                                vDs.setPeptideFileId(map.get("id").toString());
////                                vDs.setSearchGUIFileId(prov.getParameters().get("searchgui_input").toString().split(",")[0].split("id=")[1]);
//                                SystemDataSet ds = new SystemDataSet();
//                                ds.setName(map.get("name").toString());
//                                ds.setType("Peptides file");
//                                ds.setHistoryId(history.getId());
//                                ds.setGalaxyId(map.get("id").toString());
//                                ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + "/datasets/" + ds.getGalaxyId() + "/display");
//
//                                GalaxyFile file = new GalaxyFile(userFolder, ds, false);
//                                file.setDownloadUrl("to_ext=" + map.get("file_ext").toString());
//                                file.setNelsKey(map.get("name") + "", map.get("file_ext") + "");
//                                file.setAvailableOnNels(NeLSFilesMap.containsKey(file.getNelsKey()));
//                                vDs.setPeptideFile(file);
//                                ds.setNelsKey(map.get("name") + "", map.get("file_ext") + "");
//                                ds.setAvailableOnNels(NeLSFilesMap.containsKey(ds.getNelsKey()));
//                                NeLSFilesMap.remove(ds.getNelsKey());
//                            } else if (map.get("name").toString().endsWith("-PSM")) {
//                                vDs.setPsmFileId(map.get("id").toString());
//                                if (!map.get("misc_blurb").toString().equalsIgnoreCase("error")) {
//                                    int i;
//                                    try {
//                                        i = Integer.valueOf(map.get("misc_blurb").toString().replace(" lines", "").replace(",", "")) - 1;
//                                    } catch (NumberFormatException exp) {
//                                        i = 0;
//                                    }
//                                    vDs.setPsmNumber(i);
//                                }
//                                SystemDataSet ds = new SystemDataSet();
//                                ds.setName(map.get("name").toString());
//                                ds.setType("PSM file");
//                                ds.setHistoryId(history.getId());
//                                ds.setGalaxyId(map.get("id").toString());
//                                ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + "/datasets/" + ds.getGalaxyId() + "/display");
//
//                                GalaxyFile file = new GalaxyFile(userFolder, ds, false);
//                                file.setDownloadUrl("to_ext=" + map.get("file_ext").toString());
//                                file.setNelsKey(map.get("name") + "", map.get("file_ext") + "");
//                                file.setAvailableOnNels(NeLSFilesMap.containsKey(file.getNelsKey()));
//                                vDs.setPsmFile(file);
//                                ds.setNelsKey(map.get("name") + "", map.get("file_ext") + "");
//                                ds.setAvailableOnNels(NeLSFilesMap.containsKey(ds.getNelsKey()));
//                                NeLSFilesMap.remove(ds.getNelsKey());
//                            } else if (map.get("name").toString().endsWith("(CPS)")) {
//                                vDs.setCpsId(map.get("id").toString());
//                                vDs.setDownloadUrl("to_ext=" + map.get("file_ext").toString());
//                            } else if (map.get("name").toString().endsWith("-MGFFile")) {
//                                SystemDataSet ds = new SystemDataSet();
//                                ds.setName(map.get("name").toString());
//                                ds.setType("MGF");
//                                ds.setHistoryId(history.getId());
//                                ds.setGalaxyId(map.get("id").toString());
//                                ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + "/datasets/" + ds.getGalaxyId() + "/display");
//         //                       vDs.addMgfFiles(ds.getGalaxyId(), map.get("name").toString());
//                                ds.setNelsKey(map.get("name") + "", map.get("file_ext") + "");
//                                ds.setAvailableOnNels(NeLSFilesMap.containsKey(ds.getNelsKey()));
//                                NeLSFilesMap.remove(ds.getNelsKey());
//
//                            } else if (map.get("name").toString().endsWith("-ZIP")) {
//                                vDs.setZipFileId(map.get("id").toString());
//                                SystemDataSet ds = new SystemDataSet();
//                                ds.setName(map.get("name").toString().replace("-ZIP", "- FASTA"));
//                                ds.setType("FASTA File");
//                                ds.setHistoryId(history.getId());
//                                ds.setGalaxyId(map.get("id").toString() + "__data/input_database.fasta");
//                                ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + "/datasets/" + map.get("id").toString() + "/display");
//
//                                GalaxyFile file = new GalaxyFile(userFolder, ds, true);
//                                file.setDownloadUrl("to_ext=" + map.get("file_ext").toString());
//                                file.setNelsKey(map.get("name") + "", map.get("file_ext") + "");
//                                file.setAvailableOnNels(NeLSFilesMap.containsKey(file.getNelsKey()));
////                                this.indexFilesMap.put(ds.getName(), file);
//                                vDs.setFastaFile(file);
//                                ds.setNelsKey(map.get("name") + "", map.get("file_ext") + "");
//                                ds.setAvailableOnNels(NeLSFilesMap.containsKey(ds.getNelsKey()));
//                                NeLSFilesMap.remove(ds.getNelsKey());
//                                System.out.println("starting point to handel compressed MGF");
//
////
//                            } else {
//                                System.out.println("at here we should add the zip folder for index map for mgf " + map.get("name").toString() + "  ");
//                                workHistoryData.put(map.get("name").toString(), map);
//                            }
//                        } else if (map.get("data_type").toString().equalsIgnoreCase("galaxy.datatypes.text.Json") && map.get("name").toString().endsWith(".par")) {
//                            SystemDataSet ds = new SystemDataSet();
//                            ds.setName(map.get("name").toString());
//                            ds.setType("Search Paramerters File (JSON)");
//                            ds.setHistoryId(history.getId());
//                            ds.setGalaxyId(map.get("id").toString());
//                            ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + "/datasets/" + ds.getGalaxyId() + "/display");
//                            GalaxyFile file = new GalaxyFile(userFolder, ds, false);
//                            file.setDownloadUrl("to_ext=" + map.get("file_ext").toString());
//                            file.setNelsKey(map.get("name") + "", map.get("file_ext") + "");
//                            file.setAvailableOnNels(NeLSFilesMap.containsKey(file.getNelsKey()));
//                            HistoryHandler1.this.searchSetiingsFilesMap.put(ds.getGalaxyId(), file);
//                            ds.setNelsKey(map.get("name") + "", map.get("file_ext") + "");
//                            ds.setAvailableOnNels(NeLSFilesMap.containsKey(ds.getNelsKey()));
//                            NeLSFilesMap.remove(ds.getNelsKey());
//                        } else if (map.get("data_type").toString().equalsIgnoreCase("galaxy.datatypes.data.Data") && map.get("name").toString().endsWith(".cui")) {
//                            SystemDataSet ds = new SystemDataSet();
//                            ds.setName(map.get("name").toString());
//                            ds.setType("Index File");
//                            ds.setHistoryId(history.getId());
//                            ds.setGalaxyId(map.get("id").toString());
//                            ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + "/datasets/" + ds.getGalaxyId() + "/display");
//                            GalaxyFile file = new GalaxyFile(userFolder, ds, false);
//                            file.setDownloadUrl("to_ext=" + map.get("file_ext").toString());
//                            file.setNelsKey(map.get("name") + "", map.get("file_ext") + "");
//                            file.setAvailableOnNels(NeLSFilesMap.containsKey(file.getNelsKey()));
//                            HistoryHandler1.this.indexFilesMap.put(ds.getName().replace(".cui", ""), file);
//                            ds.setNelsKey(map.get("name") + "", map.get("file_ext") + "");
//                            ds.setAvailableOnNels(NeLSFilesMap.containsKey(ds.getNelsKey()));
//                            NeLSFilesMap.remove(ds.getNelsKey());
//                        } else {
//                            SystemDataSet ds = new SystemDataSet();
//                            ds.setName(map.get("name").toString());
//                            ds.setHistoryId(history.getId());
//                            ds.setGalaxyId(map.get("id").toString());
//                            ds.setNelsKey(map.get("name") + "", map.get("file_ext") + "");
//                            ds.setAvailableOnNels(NeLSFilesMap.containsKey(ds.getNelsKey()));
//                            if (map.get("data_type").toString().equalsIgnoreCase("galaxy.datatypes.sequence.Fasta")) {
//                                ds.setType("Fasta");
//                                HistoryHandler1.this.fastaFilesMap.put(ds.getGalaxyId(), ds);
//                                ds.setDownloadUrl("to_ext=" + map.get("file_ext").toString());
//                            } else if (map.get("data_type").toString().equalsIgnoreCase("galaxy.datatypes.proteomics.Mgf")) {
//                                ds.setType("MGF");
//                                ds.setDownloadUrl("to_ext=" + map.get("file_ext").toString());
//                                HistoryHandler1.this.mgfFilesMap.put(ds.getGalaxyId(), ds);
//                            } else if (map.get("data_type").toString().equalsIgnoreCase("galaxy.datatypes.text.Json") && map.get("name").toString().endsWith(".par")) {
//                                ds.setType("Search Paramerters File (JSON)");
//                                ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + "/datasets/" + ds.getGalaxyId() + "/display");
//                                GalaxyFile file = new GalaxyFile(userFolder, ds, false);
//                                file.setDownloadUrl("to_ext=" + map.get("file_ext").toString());
//                                file.setNelsKey(map.get("name") + "", map.get("file_ext") + "");
//                                file.setAvailableOnNels(NeLSFilesMap.containsKey(file.getNelsKey()));
//                                HistoryHandler1.this.searchSetiingsFilesMap.put(ds.getGalaxyId(), file);
//                            } else if (map.get("data_type").toString().equalsIgnoreCase("galaxy.datatypes.data.Data") && map.get("name").toString().endsWith(".cui")) {
//                                ds.setType("Index File");
//                                ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + "/datasets/" + ds.getGalaxyId() + "/display");
//                                GalaxyFile file = new GalaxyFile(userFolder, ds, false);
//                                file.setDownloadUrl("to_ext=" + map.get("file_ext").toString());
//                                HistoryHandler1.this.indexFilesMap.put(ds.getName().replace(".cui", ""), file);
//                                file.setNelsKey(map.get("name") + "", map.get("file_ext") + "");
//                                file.setAvailableOnNels(NeLSFilesMap.containsKey(file.getNelsKey()));
//                            } else if (map.get("data_type").toString().equalsIgnoreCase("galaxy.datatypes.binary.CompressedZipArchive") && map.get("name").toString().endsWith("Archive")) {
//
//                            }
//                            NeLSFilesMap.remove(ds.getNelsKey());
//                        }
//                    }
//                });
//
//            }
//
//            for (String key : peptideShakerVisualizationMap.keySet()) {
//                PeptideShakerVisualizationDataset vDs = peptideShakerVisualizationMap.get(key);
//                if (!searchGUIFilesMap.containsKey(vDs.getProjectName())) {
//                    System.out.println("at error happen here " + vDs.getProjectName() + "  searchGUIFilesMap " + searchGUIFilesMap.keySet());
//                    continue;
//
//                }
//                vDs.setSearchGUIFileId(searchGUIFilesMap.get(vDs.getProjectName()).getId());
//                Map<String, Object> parameters = searchGUIFilesMap.get(vDs.getProjectName()).getParameters();
//                String fastaFileId = parameters.get("input_database").toString().split(",")[0].replace("{id=", "");
//                vDs.setFastaFileReader(fastaFileReader);
//                if (fastaFilesMap.containsKey(fastaFileId)) {
//                    vDs.setFastaFileName(fastaFilesMap.get(fastaFileId).getName());
//                } else {
//                    vDs.setFastaFileName("Name not available");
//                }
//                vDs.setFastaFileId(fastaFileId);
//                vDs.setSearchingParameters(parameters);
//            }
//            historyFilesMap.putAll(peptideShakerVisualizationMap);
//            historyFilesMap.putAll(mgfFilesMap);
//            historyFilesMap.putAll(fastaFilesMap);
//            historyFilesMap.putAll(searchSetiingsFilesMap);
//            historyFilesMap.putAll(indexFilesMap);
//            if (tempWorkflowOutput != null) {
//                historyFilesMap.put(tempWorkflowOutput.getJobId(), tempWorkflowOutput);
//            }//
//            updateDSStatus(false);
//            systemIsBusy(false, historyFilesMap);
////            checkNotReadyHistory();
////
//        } catch (Exception e) {
//            if (e.toString().contains("Service Temporarily Unavailable")) {
//                Notification.show("Service Temporarily Unavailable", Notification.Type.ERROR_MESSAGE);
//            } else {
//                System.out.println("at history are not available");
//            }
//        }
//
//    }
//
//    public String getWorkingHistoryId() {
//        return workingHistory.getId();
//    }
//
//    private boolean isReadyHistory(int mSecound) {
//        HistoriesClient loopGalaxyHistoriesClient = Galaxy_Instance.getHistoriesClient();
//        List<History> historiesList = Galaxy_Instance.getHistoriesClient().getHistories();
//        boolean ready = false;
//        for (History history : historiesList) {
//            ready = (loopGalaxyHistoriesClient.showHistory(history.getId()).isReady());
//            if (!ready) {
//                notReadyHistory(history.getId(), mSecound);
//                break;
//            }
//        }
//        return ready;
//    }
//
//    private void notReadyHistory(String name, int mSecound) {
//        HistoriesClient loopGalaxyHistoriesClient = Galaxy_Instance.getHistoriesClient();
//        if (name == null || (loopGalaxyHistoriesClient.showHistory(name).isReady())) {
////            updateDSStatus(true);
////            systemIsBusy(false, historyFilesMap);
//            return;
//        }
//        REFRESHER.setRefreshInterval(mSecound);
//        Refresher.RefreshListener listener = new Refresher.RefreshListener() {
//            @Override
//            public void refresh(Refresher source) {
//                boolean ready = (loopGalaxyHistoriesClient.showHistory(name).isReady());
//                if (ready) {
//                    REFRESHER.removeListener(this);
//                    updateHistory(null);
//                    systemIsBusy(false, historyFilesMap);
//                } else {
//                    System.out.println("--------------------- at the history not ready --------------------- " + name + "   ");
//                }
//            }
//        };
//        REFRESHER.addListener(listener);
//
//    }
//
//    @SuppressWarnings("CallToPrintStackTrace")
//    private void updateDSStatus(boolean readyHistory) {
//
//        if (historyFilesMap.containsKey("tempID") && readyHistory) {
////            updateHistoryDatastructure(userFolder, null);
//        }
//        HistoriesClient galaxyHistoriesClient = Galaxy_Instance.getHistoriesClient();
//
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException ex) {
//            ex.printStackTrace();
//        }
//        for (String historyId : historiesIds) {
//            List<HistoryContents> hcList;
//            while (true) {
//                try {
//                    hcList = galaxyHistoriesClient.showHistoryContents(historyId);
//                    break;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    System.out.println("at error ");
//                    try {
//                        Thread.sleep(3000);
//                    } catch (InterruptedException ex) {
//                        ex.printStackTrace();
//                    }
//
//                }
//            }
//            for (HistoryContents hc : hcList) {
//                if (!historyFilesMap.containsKey(hc.getId())) {
//                    continue;
//                }
//                if (historyFilesMap.get(hc.getId()).getName() == null) {
//                    historyFilesMap.remove(hc.getId());
//                } else {
//                    SystemDataSet ds = historyFilesMap.get(hc.getId());
//                    if (hc.getHistoryContentType().equalsIgnoreCase("dataset_collection")) {
//                        ds.setStatus("ok");
//                        ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + "/api/histories/" + historyId + "/contents/" + ds.getGalaxyId() + "/display?key=" + Galaxy_Instance.getApiKey() + "&" + ds.getDownloadUrl());
//
//                    } else {
//                        ds.setStatus(hc.getState());
//                        ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + "/api/histories/" + historyId + "/contents/" + ds.getGalaxyId() + "/display?key=" + Galaxy_Instance.getApiKey() + "&" + ds.getDownloadUrl());
//
//                    }
//                    historyFilesMap.replace(ds.getGalaxyId(), ds);
//                }
//
//            }
////to be checked if needed
//            peptideShakerVisualizationMap.keySet().stream().map((key) -> peptideShakerVisualizationMap.get(key)).map((ds) -> {
//                ds.setStatus(Galaxy_Instance.getJobsClient().showJob(ds.getJobId()).getState());
//                return ds;
//            }).map((ds) -> {
//                ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + "/api/histories/" + historyId + "/contents/" + ds.getCpsId() + "/display?key=" + Galaxy_Instance.getApiKey() + "&" + ds.getDownloadUrl());
//                return ds;
//            }).forEachOrdered((ds) -> {
//                historyFilesMap.replace(ds.getGalaxyId(), ds);
//            });
//
//        }
//        NeLSFilesMap.keySet().stream().map((fileOnNels) -> {
//            String[] fileInfoArr = NeLSFilesMap.get(fileOnNels).split("__");
//            SystemDataSet ds = new SystemDataSet();
//            ds.setAvailableOnGalaxy(false);
//            ds.setAvailableOnNels(true);
//            ds.setDownloadUrl(fileInfoArr[1]);
//            ds.setHistoryId(workingHistory.getId());
//            ds.setGalaxyId(fileOnNels);
//            ds.setName(fileOnNels);
//            ds.setType(fileInfoArr[2]);
//            return ds;
//        }).map((ds) -> {
//            ds.setStatus("OK");
//            return ds;
//        }).forEachOrdered((ds) -> {
//            historyFilesMap.put(ds.getName(), ds);
//        });
//
//    }
//
//    public abstract void systemIsBusy(boolean busy, Map<String, SystemDataSet> historyFilesMap);
}
