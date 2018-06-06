package com.uib.web.peptideshaker.galaxy;

import com.uib.web.peptideshaker.galaxy.dataobjects.GalaxyFile;
import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.galaxy.dataobjects.SystemDataSet;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContents;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContentsProvenance;
import com.github.jmchilton.blend4j.galaxy.beans.JobDetails;
import com.github.wolfie.refresher.Refresher;
import com.uib.web.peptideshaker.PeptidShakerUI;
import com.uib.web.peptideshaker.galaxy.dataobjects.GalaxyFastaFileReader;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Notification;

import com.vaadin.ui.UI;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * This class represents Galaxy history (galaxy file system) in Peptide-Shaker
 * web application the class is responsible for handling files in galaxy history
 * and shares it cross the application
 *
 * @author Yehia Farag
 */
public abstract class HistoryHandler {

    /**
     * The main Galaxy instance in the system.
     */
    private final GalaxyInstance Galaxy_Instance;
    /**
     * The main Search settings .par File Map.
     */
    private final Map<String, GalaxyFile> searchSettingsFilesMap;
    /**
     * The main FASTA File Map.
     */
    private final Map<String, SystemDataSet> fastaFilesMap;
    /**
     * The main MGF Files Map.
     */
    private final Map<String, SystemDataSet> mgfFilesMap;
    /**
     * The main MGF and FASTA index Files Map.
     */
    private final Map<String, GalaxyFile> indexFilesMap;
    /**
     * The Full historyFiles Map.
     */
    private final Map<String, SystemDataSet> historyFilesMap;

    /**
     * The main SearchGUI Files Map.
     */
    private final Map<String, HistoryContentsProvenance> searchGUIFilesMap;
    /**
     * The main PeptideShaker Visualisation Map.
     */
    private final Map<String, PeptideShakerVisualizationDataset> peptideShakerVisualizationMap;
    /**
     * The Working galaxy history.
     */
    private History workingHistory;
    private final LinkedHashMap<String, String> NeLSFilesMap;
    ;
    private final Set<String> historiesIds;
    private final File userFolder;
    private final GalaxyDatasetServingUtil galaxyDatasetServingUtil;
    /**
     * History progress icon
     *
     */
//    private final Window progressWindow;
    /**
     * Refresher to keep tracking history state in galaxy
     *
     */
    private final Refresher REFRESHER;

    public Map<String, PeptideShakerVisualizationDataset> getPeptideShakerVisualizationMap() {
        return peptideShakerVisualizationMap;
    }

    /**
     * Constructor to initialise the main data structure and other variables.
     *
     * @param Galaxy_Instance the main Galaxy instance in the system
     * @param userFolder user folder to store users file temporarily
     *
     */
    public HistoryHandler(GalaxyInstance Galaxy_Instance, File userFolder) {
        this.Galaxy_Instance = Galaxy_Instance;
        this.searchSettingsFilesMap = new LinkedHashMap<>();
        this.fastaFilesMap = new LinkedHashMap<>();
        this.mgfFilesMap = new LinkedHashMap<>();
        this.indexFilesMap = new LinkedHashMap<>();
        this.peptideShakerVisualizationMap = new LinkedHashMap<>();
        this.searchGUIFilesMap = new LinkedHashMap<>();
        this.historyFilesMap = new LinkedHashMap<>();
        this.historiesIds = new HashSet<>();
        this.NeLSFilesMap = new LinkedHashMap<>();
        this.galaxyDatasetServingUtil = new GalaxyDatasetServingUtil(Galaxy_Instance.getGalaxyUrl(), Galaxy_Instance.getApiKey());

        REFRESHER = new Refresher();
        ((PeptidShakerUI) UI.getCurrent()).addExtension(REFRESHER);
        this.userFolder = userFolder;
        HistoryHandler.this.updateHistory();

    }

    /**
     * Get the main Search settings par files Map
     *
     * @return searchSetiingsFilesMap
     */
    public Map<String, GalaxyFile> getSearchSettingsFilesMap() {
        return searchSettingsFilesMap;
    }

    /**
     * Get the main FASTA files Map
     *
     * @return fastaFilesMap
     */
    public Map<String, SystemDataSet> getFastaFilesMap() {
        return fastaFilesMap;
    }

    /**
     * Get the main MGF files Map
     *
     * @return mgfFilesMap
     */
    public Map<String, SystemDataSet> getMgfFilesMap() {
        return mgfFilesMap;
    }

    public Map<String, SystemDataSet> getHistoryFilesMap() {
        return historyFilesMap;
    }

    public void updateHistory() {
        this.updateHistoryDatastructure();
        systemIsBusy(true, historyFilesMap);
        int mSec = 20000;
        if (!isReadyHistory(mSec)) {
            return;
        }
        this.updateHistoryDatastructure();

    }

    /**
     * Update the FASTA and MGF and peptide Shaker Visualisation maps
     *
     * @return mgfFilesMap
     */
    private void updateHistoryDatastructure() {

        try {
            NeLSFilesMap.clear();
            NeLSFilesMap.putAll((LinkedHashMap<String, String>) VaadinSession.getCurrent().getAttribute("nelsFilesMap"));
            fastaFilesMap.clear();
            mgfFilesMap.clear();
            searchGUIFilesMap.clear();
            historyFilesMap.clear();
            peptideShakerVisualizationMap.clear();
            searchSettingsFilesMap.clear();
            historiesIds.clear();
            indexFilesMap.clear();
            HistoriesClient galaxyHistoriesClient = Galaxy_Instance.getHistoriesClient();
            List<History> historiesList = galaxyHistoriesClient.getHistories();
            if (historiesList.isEmpty()) {
                galaxyHistoriesClient.create(new History("Online-PeptideShaker-History"));
                workingHistory = galaxyHistoriesClient.create(new History("Online-PeptideShaker-Job-History"));
            } else {
                for (History h : historiesList) {
                    if (h.getName().equalsIgnoreCase("Online-PeptideShaker-Job-History")) {
                        workingHistory = h;
                    }
                }
                if (workingHistory == null) {
                    workingHistory = Galaxy_Instance.getHistoriesClient().create(new History("Online-PeptideShaker-Job-History"));
                }
            }
            historiesList = galaxyHistoriesClient.getHistories();
            Map<String, SystemDataSet> tabMgfFilesMap = new HashMap<>();
            for (History history : historiesList) {
                historiesIds.add(history.getId());
                final String query = "select * from hda where history_id= '" + history.getId() + "'";
                List<Map<String, Object>> results = Galaxy_Instance.getSearchClient().search(query).getResults();
                results.stream().filter((map) -> !(map.get("purged").toString().equalsIgnoreCase("true") || map.get("deleted").toString().equalsIgnoreCase("true"))).forEachOrdered(new Consumer<Map<String, Object>>() {
                    @Override
                    public void accept(Map<String, Object> map) {
                        if (map.get("data_type").toString().equalsIgnoreCase("galaxy.datatypes.binary.SearchGuiArchive")) {
                            HistoryContentsProvenance prov = galaxyHistoriesClient.showProvenance(workingHistory.getId(), map.get("id").toString());
                            searchGUIFilesMap.put(map.get("name").toString().replace("-SearchGUI Results", ""), prov);

                        } else if (map.get("data_type").toString().equalsIgnoreCase("galaxy.datatypes.text.Json") && map.get("name").toString().endsWith(".par")) {
                            SystemDataSet ds = new SystemDataSet();
                            ds.setName(map.get("name").toString());
                            ds.setType("Search Paramerters File (JSON)");
                            ds.setHistoryId(history.getId());
                            ds.setGalaxyId(map.get("id").toString());
                            ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + "/datasets/" + ds.getGalaxyId() + "/display");
                            GalaxyFile file = new GalaxyFile(userFolder, ds, false);
                            file.setDownloadUrl("to_ext=" + map.get("file_ext").toString());
                            file.setNelsKey(map.get("name") + "", map.get("file_ext") + "");
                            file.setAvailableOnNels(NeLSFilesMap.containsKey(file.getNelsKey()));
                            HistoryHandler.this.searchSettingsFilesMap.put(ds.getGalaxyId(), file);
                            ds.setNelsKey(map.get("name") + "", map.get("file_ext") + "");
                            ds.setAvailableOnNels(NeLSFilesMap.containsKey(ds.getNelsKey()));
                            NeLSFilesMap.remove(ds.getNelsKey());
                        } else if (map.get("data_type").toString().equalsIgnoreCase("galaxy.datatypes.proteomics.Mgf")) {
                            SystemDataSet ds = new SystemDataSet();
                            ds.setName(map.get("name").toString());
                            ds.setType("MGF");
                            ds.setDownloadUrl("to_ext=" + map.get("file_ext").toString());
                            ds.setHistoryId(history.getId());
                            ds.setGalaxyId(map.get("id").toString());
                            ds.setNelsKey(map.get("name") + "", map.get("file_ext") + "");
                            ds.setAvailableOnNels(NeLSFilesMap.containsKey(ds.getNelsKey()));
                            HistoryHandler.this.mgfFilesMap.put(ds.getGalaxyId(), ds);
                            NeLSFilesMap.remove(ds.getNelsKey());
                        } else if (map.get("data_type").toString().equalsIgnoreCase("galaxy.datatypes.sequence.Fasta")) {
                            SystemDataSet ds = new SystemDataSet();
                            ds.setName(map.get("name").toString());
                            ds.setType("Fasta");
                            ds.setDownloadUrl("to_ext=" + map.get("file_ext").toString());
                            ds.setHistoryId(history.getId());
                            ds.setGalaxyId(map.get("id").toString());
                            ds.setNelsKey(map.get("name") + "", map.get("file_ext") + "");
                            ds.setAvailableOnNels(NeLSFilesMap.containsKey(ds.getNelsKey()));
                            HistoryHandler.this.fastaFilesMap.put(ds.getGalaxyId(), ds);
                            NeLSFilesMap.remove(ds.getNelsKey());
                        } else if ((map.get("name").toString().endsWith("-ZIP")) && (map.get("data_type").toString().equalsIgnoreCase("abc.CompressedArchive") || map.get("data_type").toString().equalsIgnoreCase("galaxy.datatypes.binary.CompressedZipArchive"))) {
                            String projectId = map.get("name").toString().split("-")[0];
                            PeptideShakerVisualizationDataset vDs = new PeptideShakerVisualizationDataset(projectId, userFolder, Galaxy_Instance.getGalaxyUrl(), Galaxy_Instance.getApiKey(), galaxyDatasetServingUtil);
                            peptideShakerVisualizationMap.put(projectId, vDs);
                            HistoryContentsProvenance prov = galaxyHistoriesClient.showProvenance(workingHistory.getId(), map.get("id").toString());
                            vDs.setJobId("_PS_" + prov.getJobId());
                            vDs.setGalaxyId("_PS_" + prov.getJobId());
                            vDs.setHistoryId(history.getId());
                            vDs.setType("Web Peptide Shaker Dataset");
                            vDs.setName(projectId);
                            vDs.setFile_ext(map.get("file_ext") + "");
                            vDs.setZipFileId(map.get("id").toString());

                        } else if (map.get("data_type").toString().equalsIgnoreCase("galaxy.datatypes.tabular.Tabular") && map.get("name").toString().endsWith("-MGFFile")) {
                            SystemDataSet ds = new SystemDataSet();
                            ds.setName(map.get("name").toString().split("-")[1]);
                            ds.setType("MGF");
                            ds.setHistoryId(history.getId());
                            ds.setGalaxyId(map.get("id").toString());
                            ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + "/datasets/" + ds.getGalaxyId() + "/display");
                            ds.setNelsKey(map.get("name") + "", map.get("file_ext") + "");
                            ds.setAvailableOnNels(NeLSFilesMap.containsKey(ds.getNelsKey()));
                            HistoryContentsProvenance prov = galaxyHistoriesClient.showProvenance(workingHistory.getId(), map.get("id").toString());
                            Map<String, Object> params = prov.getParameters();
                            ds.setInputDsId(((Map<String, Object>) params.get("input")).get("id").toString());
                            NeLSFilesMap.remove(ds.getNelsKey());
                            tabMgfFilesMap.put(ds.getInputDsId(), ds);

                        }
                    }
                });

            }

            for (String key : peptideShakerVisualizationMap.keySet()) {
                PeptideShakerVisualizationDataset vDs = peptideShakerVisualizationMap.get(key);

                if (!searchGUIFilesMap.containsKey(vDs.getProjectName())) {
                    continue;
                }
                JobDetails job = Galaxy_Instance.getJobsClient().showJob(searchGUIFilesMap.get(vDs.getProjectName()).getJobId());
                if (!job.getState().equalsIgnoreCase("running")) {
                    vDs.setSearchGUIFileId(searchGUIFilesMap.get(vDs.getProjectName()).getId());
                }
                vDs.setJobDate(job.getCreated());
                Map<String, Object> parameters = searchGUIFilesMap.get(vDs.getProjectName()).getParameters();
                vDs.setSearchingParameters(jsonToMap(new JSONObject(parameters)));
                parameters.keySet().stream().filter((str) -> (str.contains("peak_lists"))).forEachOrdered((str) -> {
                    String id = ((Map<String, Object>) parameters.get(str)).get("id").toString();
                    vDs.addMgfFiles(id, tabMgfFilesMap.get(id));
//                   
                });
                if (fastaFilesMap.containsKey(vDs.getOrgFastaFileId())) {
                    vDs.setOrgFastaFileName(fastaFilesMap.get(vDs.getOrgFastaFileId()).getName());
                }

            }
//            if (tempWorkflowOutput != null) {
//                System.out.println("-------------lozzaaa---- "+tempWorkflowOutput.getOutputsIds());
////                JobDetails job = Galaxy_Instance.getJobsClient().showJob(tempWorkflowOutput.getOutputsIds().get(0));
////                tempWorkflowOutput.setJobDate(job.getCreated());
////                peptideShakerVisualizationMap.put(tempWorkflowOutput.getProjectName(), tempWorkflowOutput);
//            }
            List<PeptideShakerVisualizationDataset> collection = new ArrayList<>(peptideShakerVisualizationMap.values());
            Collections.sort(collection);
            Collections.reverse(collection);
            peptideShakerVisualizationMap.clear();
            collection.forEach((ps) -> {
                peptideShakerVisualizationMap.put(ps.getProjectName(), ps);
            });

            historyFilesMap.putAll(peptideShakerVisualizationMap);
            historyFilesMap.putAll(mgfFilesMap);
            historyFilesMap.putAll(fastaFilesMap);
            historyFilesMap.putAll(searchSettingsFilesMap);
            historyFilesMap.putAll(indexFilesMap);
//            if (tempWorkflowOutput != null) {
//                historyFilesMap.put(tempWorkflowOutput.getJobId(), tempWorkflowOutput);
//            }//
            updateDSStatus(false);
            systemIsBusy(false, historyFilesMap);

//
        } catch (JSONException e) {
            if (e.toString().contains("Service Temporarily Unavailable")) {
                Notification.show("Service Temporarily Unavailable", Notification.Type.ERROR_MESSAGE);
            } else {
                e.printStackTrace();
                System.out.println("at history are not available");
            }
        }

    }

    public String getWorkingHistoryId() {
        return workingHistory.getId();
    }
    int count = 0;

    private boolean isReadyHistory(int mSecound) {
        HistoriesClient loopGalaxyHistoriesClient = Galaxy_Instance.getHistoriesClient();
        List<History> historiesList = Galaxy_Instance.getHistoriesClient().getHistories();
        boolean ready = false;
        for (History history : historiesList) {
            ready = (loopGalaxyHistoriesClient.showHistory(history.getId()).isReady());
            if (!ready) {
                System.out.println("not ready history " + count++);
                notReadyHistory(history.getId(), mSecound);
                break;
            }
        }
        return ready;
    }

    private void notReadyHistory(String name, int mSecound) {
        HistoriesClient loopGalaxyHistoriesClient = Galaxy_Instance.getHistoriesClient();
        if (name == null || (loopGalaxyHistoriesClient.showHistory(name).isReady())) {
//            updateDSStatus(true);
//            systemIsBusy(false, historyFilesMap);
            return;
        }
        REFRESHER.setRefreshInterval(mSecound);
        Refresher.RefreshListener listener = new Refresher.RefreshListener() {
            @Override
            public void refresh(Refresher source) {
                boolean ready = (loopGalaxyHistoriesClient.showHistory(name).isReady());
                if (ready) {
                    REFRESHER.removeListener(this);
                    updateHistory();
                    systemIsBusy(false, historyFilesMap);
                } else {
                    System.out.println("--------------------- at the history not ready --------------------- " + name + "   ");
                }
            }
        };
        REFRESHER.addListener(listener);

    }

    @SuppressWarnings("CallToPrintStackTrace")
    private void updateDSStatus(boolean readyHistory) {

        if (historyFilesMap.containsKey("tempID") && readyHistory) {
//            updateHistoryDatastructure(userFolder, null);
        }
        HistoriesClient galaxyHistoriesClient = Galaxy_Instance.getHistoriesClient();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        for (String historyId : historiesIds) {
            List<HistoryContents> hcList;
            while (true) {
                try {
                    hcList = galaxyHistoriesClient.showHistoryContents(historyId);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("at error ");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }

                }
            }
            for (HistoryContents hc : hcList) {
                if (!historyFilesMap.containsKey(hc.getId())) {
                    continue;
                }
                if (historyFilesMap.get(hc.getId()).getName() == null) {
                    historyFilesMap.remove(hc.getId());
                } else {
                    SystemDataSet ds = historyFilesMap.get(hc.getId());
                    if (hc.getHistoryContentType().equalsIgnoreCase("dataset_collection")) {
                        ds.setStatus("ok");
                        ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + "/api/histories/" + historyId + "/contents/" + ds.getGalaxyId() + "/display?key=" + Galaxy_Instance.getApiKey() + "&" + ds.getDownloadUrl());

                    } else {
                        ds.setStatus(hc.getState());
                        ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + "/api/histories/" + historyId + "/contents/" + ds.getGalaxyId() + "/display?key=" + Galaxy_Instance.getApiKey() + "&" + ds.getDownloadUrl());
                    }
                    historyFilesMap.replace(ds.getGalaxyId(), ds);
                }

            }
//to be checked if needed
            peptideShakerVisualizationMap.keySet().stream().map((key) -> peptideShakerVisualizationMap.get(key)).map((ds) -> {
                ds.setStatus(Galaxy_Instance.getJobsClient().showJob(ds.getJobId()).getState());
                return ds;
            }).map((ds) -> {
//                ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + "/api/histories/" + historyId + "/contents/" + ds.getCpsId() + "/display?key=" + Galaxy_Instance.getApiKey() + "&" + ds.getDownloadUrl());
                return ds;
            }).forEachOrdered((ds) -> {
                historyFilesMap.replace(ds.getGalaxyId(), ds);
            });

        }
        NeLSFilesMap.keySet().stream().map((fileOnNels) -> {
            String[] fileInfoArr = NeLSFilesMap.get(fileOnNels).split("__");
            SystemDataSet ds = new SystemDataSet();
            ds.setAvailableOnGalaxy(false);
            ds.setAvailableOnNels(true);
            ds.setDownloadUrl(fileInfoArr[1]);
            ds.setHistoryId(workingHistory.getId());
            ds.setGalaxyId(fileOnNels);
            ds.setName(fileOnNels);
            ds.setType(fileInfoArr[2]);
            return ds;
        }).map((ds) -> {
            ds.setStatus("OK");
            return ds;
        }).forEachOrdered((ds) -> {
            historyFilesMap.put(ds.getName(), ds);
        });

    }

    public Map<String, Object> jsonToMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = jsonToMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = jsonToMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    public abstract void systemIsBusy(boolean busy, Map<String, SystemDataSet> historyFilesMap);
}
