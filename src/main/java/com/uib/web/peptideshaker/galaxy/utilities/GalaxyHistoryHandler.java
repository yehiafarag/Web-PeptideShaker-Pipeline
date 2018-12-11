package com.uib.web.peptideshaker.galaxy.utilities;

import com.uib.web.peptideshaker.galaxy.utilities.history.GalaxyDatasetServingUtil;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyTransferableFile;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.wolfie.refresher.Refresher;
import com.uib.web.peptideshaker.PeptidShakerUI;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Notification;

import com.vaadin.ui.UI;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
public abstract class GalaxyHistoryHandler {

    /**
     * The main Galaxy instance in the system.
     */
    private GalaxyInstance Galaxy_Instance;
    /**
     * User data files folder.
     */
    private File user_folder;
    /**
     * Helping class to initialise and update dataset information.
     */
    private GalaxyDatasetServingUtil galaxyDatasetServingUtil;
    /**
     * Is system in progress.
     */
    private boolean jobsInProgress = false;
    /**
     * Inner class responsible for create Task to tracking and update the galaxy
     * history.
     */
    private UpdateDatasetructureTask updateDatasetructureTask;
    /**
     * Future results from executing tasks to tracking and update the galaxy
     * history.
     */
    private Future updateDatasetructureFuture;
    /**
     * Refresher to keep tracking history state in galaxy.
     */
    private final Refresher REFRESHER;
    /**
     * Refresher listener allow action on adding different actions for the
     * application refresher.
     */
    private Refresher.RefreshListener refreshlistener;
    /**
     * Date formatter to allow reading the creation date of the datasets from
     * the dataset json file.
     */
    private final DateFormat df6 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
private Set<String> csf_pr_Accssion_List;
    /**
     * Constructor to initialise the the galaxy history handler, and Connection
     * refresher to keep tracking running jobs on Galaxy Server.
     *
     * @param csf_pr_Accssion_List
     */
    public GalaxyHistoryHandler(Set<String> csf_pr_Accssion_List ) {
        REFRESHER = new Refresher();
        ((PeptidShakerUI) UI.getCurrent()).addExtension(REFRESHER);
        this.csf_pr_Accssion_List=csf_pr_Accssion_List;

    }

    /**
     * System connected to Galaxy Server
     *
     * @param Galaxy_Instance the main Galaxy instance in the system
     * @param user_folder user folder to store users file temporarily
     */
    public void connectToGalaxy(GalaxyInstance Galaxy_Instance, File user_folder) {
        this.Galaxy_Instance = Galaxy_Instance;
        this.galaxyDatasetServingUtil = new GalaxyDatasetServingUtil(Galaxy_Instance.getGalaxyUrl(), Galaxy_Instance.getApiKey());
        this.user_folder = user_folder;
        GalaxyHistoryHandler.this.updateHistory(true);
    }

    /**
     * Update galaxy layer file system datasets from Galaxy server.
     *
     * @param updatePresenterView open file system view after updating the file
     * system
     */
    public void updateHistory(boolean updatePresenterView) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        updateDatasetructureTask = new UpdateDatasetructureTask(updatePresenterView);
        updateDatasetructureFuture = executorService.submit(updateDatasetructureTask);
        executorService.shutdown();
    }

    /**
     * Get map of available PeptideShaker visualisation datasets.
     *
     * @return map of available PS datasets
     */
    public Map<String, PeptideShakerVisualizationDataset> getPeptideShakerVisualizationMap() {
        while (!updateDatasetructureFuture.isDone()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        return updateDatasetructureTask.getPeptideShakerVisualizationMap();
    }

    /**
     * Get the main Search settings .par files Map
     *
     * @return Search parameters files .par map
     */
    public Map<String, GalaxyTransferableFile> getSearchSettingsFilesMap() {
        while (!updateDatasetructureFuture.isDone()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        return updateDatasetructureTask.getSearchSettingsFilesMap();
    }

    /**
     * Get the main FASTA files Map
     *
     * @return FASTA Files Map
     */
    public Map<String, GalaxyFileObject> getFastaFilesMap() {
        while (!updateDatasetructureFuture.isDone()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        return updateDatasetructureTask.getFastaFilesMap();
    }

    /**
     * Get the main MGF files Map
     *
     * @return Map of available MGF files (file galaxy id mapped to datasets)
     */
    public Map<String, GalaxyFileObject> getMgfFilesMap() {
        while (!updateDatasetructureFuture.isDone()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        try {
            return updateDatasetructureTask.call();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Get map all available files (datasets)
     *
     * @return Map of all available files (file galaxy id mapped to datasets)
     */
    public Map<String, GalaxyFileObject> getHistoryFilesMap() {
        while (!updateDatasetructureFuture.isDone()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        return updateDatasetructureTask.getHistoryFilesMap();
    }

    /**
     * Get the total memory usage on galaxy server in GB
     *
     * @return double value of user memory used in GB (deleted files not
     * included)
     */
    public double getMemoryUsage() {
        while (!updateDatasetructureFuture.isDone()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        return updateDatasetructureTask.getMemoryUsed();
    }

    /**
     * Get the number of viewable files available for the user
     *
     * @return integer number of available files
     */
    public int getFilesNumber() {
        while (!updateDatasetructureFuture.isDone()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        return updateDatasetructureTask.getFilesNumber();
    }

    /**
     * Get the number of viewable datasets available for the user
     *
     * @return integer number of available datasets
     */
    public int getDatasetsNumber() {
        while (!updateDatasetructureFuture.isDone()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        return updateDatasetructureTask.getDsNumbers();
    }

    /**
     * Get the main working history id on galaxy server where all new datasets
     * will be stored
     *
     * @return Working history id in Galaxy server
     */
    public String getWorkingHistoryId() {
        while (!updateDatasetructureFuture.isDone()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        return updateDatasetructureTask.getWorkingHistoryId();
    }

    /**
     * Convert JSON object to Java readable map
     *
     * @param object JSON object to be converted
     * @return Java Hash map has all the data
     * @throws JSONException in case of error in reading JSON file
     */
    private Map<String, Object> jsonToMap(JSONObject object) throws JSONException {
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

    /**
     * Convert JSON object to Java readable list
     *
     * @param object JSON object to be converted
     * @return Java List has all the data
     * @throws JSONException in case of error in reading JSON file
     */
    private List<Object> toList(JSONArray array) throws JSONException {
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

    /**
     * Abstract method allows performing actions based on the jobs states on
     * Galaxy server
     *
     * @param historyFilesMap list of available files (datasets) on Galaxy
     * server
     * @param jobsInProgress the system currently in progress (jobs still
     * running)
     * @param updatePresenterView open file system view after updating the file
     * system
     */
    public abstract void synchronizeDataWithGalaxyServer(Map<String, GalaxyFileObject> historyFilesMap, boolean jobsInProgress, boolean updatePresenterView);

    /**
     * Following jobs statues on Galaxy server until all jobs are done.
     *
     * @param updatePresenterView open file system view after updating the file
     * system
     */
    private void invokeRecheckDataProcessing(boolean updatePresenterView) {
        int mSecound = 20000;
        if (refreshlistener != null) {
            REFRESHER.removeListener(refreshlistener);

        }
        REFRESHER.setRefreshInterval(mSecound);
        
        
        refreshlistener = (Refresher source) -> {
            HistoriesClient loopGalaxyHistoriesClient = Galaxy_Instance.getHistoriesClient();
            List<History> historiesList = Galaxy_Instance.getHistoriesClient().getHistories();
            boolean ready = false;
            for (History history : historiesList) {
                ready = (loopGalaxyHistoriesClient.showHistory(history.getId()).isReady());
                if (!ready) {
                    break;
                }
            }
            if (ready) {
                REFRESHER.removeListener(refreshlistener);
                updateHistory(updatePresenterView);
            }
        };
        REFRESHER.addListener(refreshlistener);

    }

    /**
     * This class responsible for generating tasks that track the different jobs
     * process on Galaxy server
     *
     */
    private class UpdateDatasetructureTask implements Callable<Map<String, GalaxyFileObject>> {

        /**
         * The main MGF Files Map.
         */
        private final Map<String, GalaxyFileObject> mgfFilesMap;
        /**
         * The Full historyFiles Map.
         */
        private final Map<String, GalaxyFileObject> historyFilesMap;
        /**
         * The main Search settings .par File Map.
         */
        private final Map<String, GalaxyTransferableFile> searchSettingsFilesMap;
        /**
         * The main FASTA File Map.
         */
        private final Map<String, GalaxyFileObject> fastaFilesMap;
        /**
         * The main MGF and FASTA index Files Map.
         */
        private final Map<String, GalaxyTransferableFile> indexFilesMap;

        /**
         * The main SearchGUI Files Map.
         */
        private final Map<String, GalaxyFileObject> searchGUIFilesMap;
        /**
         * The main PeptideShaker Visualisation Map.
         */
        private final Map<String, PeptideShakerVisualizationDataset> peptideShakerVisualizationMap;
        /**
         * List of available galaxy histories IDs.
         */
        private final Set<String> historiesIds;
        /**
         * List of available files on NeLS account
         *
         * @todo:for suture use.
         */
        private final LinkedHashMap<String, String> NeLSFilesMap;
        /**
         * List of converted MGF files into TAB format to allow byte serving in
         * the interaction with server.
         */
        private final Map<String, GalaxyFileObject> tabMgfFilesMap;
        /**
         * The Working galaxy history.
         */
        private History workingHistory;

        public Map<String, GalaxyTransferableFile> getSearchSettingsFilesMap() {
            return searchSettingsFilesMap;
        }

        public Map<String, GalaxyFileObject> getMgfFilesMap() {
            return mgfFilesMap;
        }

        public Map<String, GalaxyFileObject> getHistoryFilesMap() {
            return historyFilesMap;
        }

        public Map<String, GalaxyFileObject> getFastaFilesMap() {
            return fastaFilesMap;
        }

        public Map<String, GalaxyTransferableFile> getIndexFilesMap() {
            return indexFilesMap;
        }

        public Map<String, GalaxyFileObject> getSearchGUIFilesMap() {
            return searchGUIFilesMap;
        }

        public Map<String, PeptideShakerVisualizationDataset> getPeptideShakerVisualizationMap() {
            return peptideShakerVisualizationMap;
        }

        public Set<String> getHistoriesIds() {
            return historiesIds;
        }

        public LinkedHashMap<String, String> getNeLSFilesMap() {
            return NeLSFilesMap;
        }

        public String getWorkingHistoryId() {
            return workingHistory.getId();
        }

        private double memoryUsed = 0;
        private int filesNumber;
        private int dsNumbers;

        public UpdateDatasetructureTask(boolean updatePresenterView) {
            jobsInProgress = false;
            this.mgfFilesMap = new LinkedHashMap<>();
            this.historyFilesMap = new LinkedHashMap<>();
            this.fastaFilesMap = new LinkedHashMap<>();

            this.indexFilesMap = new LinkedHashMap<>();
            this.peptideShakerVisualizationMap = new LinkedHashMap<>();
            this.searchGUIFilesMap = new LinkedHashMap<>();
            this.historiesIds = new HashSet<>();
            this.NeLSFilesMap = new LinkedHashMap<>();
            this.tabMgfFilesMap = new HashMap<>();

            this.searchSettingsFilesMap = new LinkedHashMap<>();
            try {
                NeLSFilesMap.putAll((LinkedHashMap<String, String>) VaadinSession.getCurrent().getAttribute("nelsFilesMap"));
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

                for (History history : historiesList) {
                    historiesIds.add(history.getId());
                }

                final String query = "select * from hda ";//where history_id= '" + history.getId() + "'"; 
                List<Map<String, Object>> results = Galaxy_Instance.getSearchClient().search(query).getResults();

                memoryUsed = 0;
                results.stream().filter((map) -> map != null && (!((map.get("purged") + "").equalsIgnoreCase("true") || (!historiesIds.contains(map.get("history_id") + "")) || (map.get("deleted") + "").equalsIgnoreCase("true")))).forEachOrdered((Map<String, Object> map) -> {
                    int size = (int) map.get("file_size");
                    double sizeinMB = (double) size / 1000000.0;
                    memoryUsed = memoryUsed + size;
                    if ((map.get("data_type") + "").equalsIgnoreCase("galaxy.datatypes.binary.SearchGuiArchive")) {
                        GalaxyFileObject ds = new GalaxyFileObject();
                        ds.setStatus(map.get("state") + "");
                        if (ds.getStatus().equalsIgnoreCase("new") || ds.getStatus().equalsIgnoreCase("running") || ds.getStatus().equalsIgnoreCase("queued")) {
                            jobsInProgress = true;
                        }
                        try {
                            ds.setCreate_time(df6.parse((map.get("create_time") + "")));
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                        ds.setSize(sizeinMB);
                        ds.setName(map.get("name").toString());
                        ds.setType("SearchGUI Output File");
                        ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + "/datasets/" + map.get("id").toString() + "/display?to_ext=" + map.get("file_ext").toString());
                        ds.setHistoryId(map.get("history_id") + "");
                        ds.setGalaxyId(map.get("id").toString());
                        ds.setNelsKey(map.get("name") + "", map.get("file_ext") + "");
                        ds.setAvailableOnNels(NeLSFilesMap.containsKey(ds.getNelsKey()));
                        ds.setOverview(map.get("misc_info") + "");
                        searchGUIFilesMap.put(map.get("name").toString().replace("-SearchGUI Results", ""), ds);
                        try {
                            ds.setCreate_time(df6.parse((map.get("create_time") + "")));
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                    } else if (map.get("data_type").toString().equalsIgnoreCase("galaxy.datatypes.text.Json") && map.get("name").toString().endsWith(".par")) {
                        GalaxyFileObject ds = new GalaxyFileObject();
                        ds.setName(map.get("name").toString());
                        ds.setType("Search Parameters File (JSON)");
                        ds.setHistoryId(map.get("history_id") + "");
                        ds.setGalaxyId(map.get("id").toString());
                        ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + "/datasets/" + map.get("id").toString() + "/display?to_ext=" + map.get("file_ext").toString());
                        ds.setStatus(map.get("state") + "");
                        ds.setSize(sizeinMB);
                        try {
                            ds.setCreate_time(df6.parse((map.get("create_time") + "")));
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                        GalaxyTransferableFile file = new GalaxyTransferableFile(user_folder, ds, false);
                        file.setDownloadUrl(ds.getDownloadUrl());
                        file.setNelsKey(map.get("name") + "", map.get("file_ext") + "");
                        file.setAvailableOnNels(NeLSFilesMap.containsKey(file.getNelsKey()));
//                        try {
//                            file.getFile();
//                        } catch (IOException ex) {
//                            ex.printStackTrace();
//                        }
                        this.searchSettingsFilesMap.put(ds.getGalaxyId(), file);
                        file.setSize(sizeinMB);
                        file.setCreate_time(ds.getCreate_time());
                        ds.setNelsKey(map.get("name") + "", map.get("file_ext") + "");
                        ds.setAvailableOnNels(NeLSFilesMap.containsKey(ds.getNelsKey()));
                        NeLSFilesMap.remove(ds.getNelsKey());
                    } else if (map.get("data_type").toString().equalsIgnoreCase("galaxy.datatypes.proteomics.Mgf")) {
                        GalaxyFileObject ds = new GalaxyFileObject();
                        ds.setName(map.get("name").toString());
                        ds.setType("MGF");
                        ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + "/datasets/" + map.get("id").toString() + "/display?to_ext=" + map.get("file_ext").toString());
                        ds.setHistoryId(map.get("history_id") + "");
                        ds.setGalaxyId(map.get("id").toString());
                        ds.setNelsKey(map.get("name") + "", map.get("file_ext") + "");
                        ds.setAvailableOnNels(NeLSFilesMap.containsKey(ds.getNelsKey()));
                        ds.setStatus(map.get("state") + "");
                        ds.setSize(sizeinMB);
                        try {
                            ds.setCreate_time(df6.parse((map.get("create_time") + "")));
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                        this.mgfFilesMap.put(ds.getGalaxyId(), ds);
                        NeLSFilesMap.remove(ds.getNelsKey());
                    } else if (map.get("data_type").toString().equalsIgnoreCase("galaxy.datatypes.sequence.Fasta")) {
                        GalaxyFileObject ds = new GalaxyFileObject();
                        ds.setName(map.get("name").toString());
                        ds.setType("FASTA");
                        ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + "/datasets/" + map.get("id").toString() + "/display?to_ext=" + map.get("file_ext").toString());
                        ds.setHistoryId(map.get("history_id") + "");
                        ds.setGalaxyId(map.get("id").toString());
                        ds.setNelsKey(map.get("name") + "", map.get("file_ext") + "");
                        ds.setAvailableOnNels(NeLSFilesMap.containsKey(ds.getNelsKey()));
                        this.fastaFilesMap.put(ds.getGalaxyId(), ds);
                        ds.setStatus(map.get("state") + "");
                        ds.setSize(sizeinMB);
                        try {
                            ds.setCreate_time(df6.parse((map.get("create_time") + "")));
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                        NeLSFilesMap.remove(ds.getNelsKey());
                    } else if (map.get("data_type").toString().equalsIgnoreCase("galaxy.datatypes.data.Data") && map.get("name").toString().endsWith(".fasta")) {
                        GalaxyFileObject ds = new GalaxyFileObject();
                        ds.setName(map.get("name").toString());
                        ds.setType("FASTA");
                        ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + "/datasets/" + map.get("id").toString() + "/display?to_ext=" + map.get("file_ext").toString());
                        ds.setHistoryId(map.get("history_id") + "");
                        ds.setGalaxyId(map.get("id").toString());
                        ds.setNelsKey(map.get("name") + "", map.get("file_ext") + "");
                        ds.setAvailableOnNels(NeLSFilesMap.containsKey(ds.getNelsKey()));
                        this.fastaFilesMap.put(ds.getGalaxyId(), ds);
                        ds.setSize(sizeinMB);
                        try {
                            ds.setCreate_time(df6.parse((map.get("create_time") + "")));
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                        ds.setStatus(map.get("state") + "");
                        NeLSFilesMap.remove(ds.getNelsKey());
                        if (map.get("state").toString().equalsIgnoreCase("new") || map.get("state").toString().equalsIgnoreCase("running") || map.get("state").toString().equalsIgnoreCase("queued")) {
                            jobsInProgress = true;
                        }
                    } else if (map.get("data_type").toString().equalsIgnoreCase("galaxy.datatypes.data.Data") && map.get("name").toString().endsWith(".mgf")) {
                        GalaxyFileObject ds = new GalaxyFileObject();
                        ds.setName(map.get("name").toString());
                        ds.setType("MGF");
                        ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + "/datasets/" + map.get("id").toString() + "/display?to_ext=" + map.get("file_ext").toString());
                        ds.setHistoryId(map.get("history_id") + "");
                        ds.setGalaxyId(map.get("id").toString());
                        ds.setNelsKey(map.get("name") + "", map.get("file_ext") + "");
                        ds.setAvailableOnNels(NeLSFilesMap.containsKey(ds.getNelsKey()));
                        ds.setStatus(map.get("state") + "");
                        ds.setSize(sizeinMB);
                        this.mgfFilesMap.put(ds.getGalaxyId(), ds);
                        try {
                            ds.setCreate_time(df6.parse((map.get("create_time") + "")));
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                        NeLSFilesMap.remove(ds.getNelsKey());
                        if (map.get("state").toString().equalsIgnoreCase("new") || map.get("state").toString().equalsIgnoreCase("running") || map.get("state").toString().equalsIgnoreCase("queued")) {
                            jobsInProgress = true;
                        }
                    } else if ((map.get("name").toString().endsWith("-ZIP")) && (map.get("data_type").toString().equalsIgnoreCase("abc.CompressedArchive") || map.get("data_type").toString().equalsIgnoreCase("galaxy.datatypes.binary.CompressedZipArchive"))) {
                        String projectId = map.get("name").toString().split("-")[0];
                        PeptideShakerVisualizationDataset vDs = new PeptideShakerVisualizationDataset(projectId, user_folder, Galaxy_Instance.getGalaxyUrl(), Galaxy_Instance.getApiKey(), galaxyDatasetServingUtil,csf_pr_Accssion_List);
                        peptideShakerVisualizationMap.put(projectId, vDs);
                        vDs.setHistoryId(map.get("history_id") + "");
                        vDs.setType("Web Peptide Shaker Dataset");
                        vDs.setName(projectId);
                        vDs.setFile_ext(map.get("file_ext") + "");
                        vDs.setPeptideShakerResultsFileId(map.get("id").toString());
                        vDs.setStatus(map.get("state") + "");
                        vDs.setGalaxyId(map.get("id").toString());
                        vDs.setSize(sizeinMB);
                        vDs.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + "/datasets/" + map.get("id").toString() + "/display?to_ext=" + map.get("file_ext").toString());

                        if (map.get("state").toString().equalsIgnoreCase("new") || map.get("state").toString().equalsIgnoreCase("running") || map.get("state").toString().equalsIgnoreCase("queued")) {
                            jobsInProgress = true;
                        }
                        try {
                            vDs.setCreateTime(df6.parse((map.get("create_time") + "")));
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                    } else if (map.get("data_type").toString().equalsIgnoreCase("galaxy.datatypes.tabular.Tabular") && map.get("name").toString().endsWith("-MGFFile")) {
                        GalaxyFileObject ds = new GalaxyFileObject();
                        ds.setName(map.get("name").toString());
                        ds.setType("MGF");
                        ds.setHistoryId(map.get("history_id") + "");
                        ds.setGalaxyId(map.get("id").toString());
                        ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + "/datasets/" + map.get("id").toString() + "/display");
                        ds.setNelsKey(map.get("name") + "", map.get("file_ext") + "");
                        ds.setAvailableOnNels(NeLSFilesMap.containsKey(ds.getNelsKey()));
                        ds.setStatus(map.get("state") + "");
                        ds.setSize(sizeinMB);
                        if (ds.getStatus().equalsIgnoreCase("new") || ds.getStatus().equalsIgnoreCase("running") || ds.getStatus().equalsIgnoreCase("queued")) {
                            jobsInProgress = true;
                        }
                        NeLSFilesMap.remove(ds.getNelsKey());
                        tabMgfFilesMap.put(ds.getName(), ds);
                        try {
                            ds.setCreate_time(df6.parse((map.get("create_time") + "")));
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                    }
                });

                peptideShakerVisualizationMap.keySet().stream().map((key) -> peptideShakerVisualizationMap.get(key)).filter((vDs) -> !(!searchGUIFilesMap.containsKey(vDs.getProjectName()))).forEachOrdered((vDs) -> {
                    GalaxyFileObject ds = searchGUIFilesMap.get(vDs.getProjectName());
                    vDs.setCreateTime(ds.getCreate_time());
                    vDs.setSearchGUIResultFile(ds);
                    tabMgfFilesMap.keySet().stream().filter((key) -> (key.contains(vDs.getProjectName()))).forEachOrdered((key) -> {
                        vDs.addMgfFiles(key, tabMgfFilesMap.get(key));
                    });
                });

                List<PeptideShakerVisualizationDataset> collection = new ArrayList<>(peptideShakerVisualizationMap.values());
                Collections.sort(collection);
                Collections.reverse(collection);
                peptideShakerVisualizationMap.clear();
                collection.forEach((ps) -> {
                    peptideShakerVisualizationMap.put(ps.getProjectName(), ps);
                });
                dsNumbers = peptideShakerVisualizationMap.size();
                filesNumber = mgfFilesMap.size() + fastaFilesMap.size() + searchSettingsFilesMap.size() + indexFilesMap.size();
                historyFilesMap.putAll(peptideShakerVisualizationMap);
                historyFilesMap.putAll(mgfFilesMap);
                historyFilesMap.putAll(fastaFilesMap);
                historyFilesMap.putAll(searchSettingsFilesMap);
                historyFilesMap.putAll(indexFilesMap);
                if (jobsInProgress) {
                    
                        invokeRecheckDataProcessing(updatePresenterView);
                  
                   
                }
                synchronizeDataWithGalaxyServer(historyFilesMap, jobsInProgress, updatePresenterView);
            } catch (Exception e) {
                if (e.toString().contains("Service Temporarily Unavailable")) {
                    Notification.show("Service Temporarily Unavailable", Notification.Type.ERROR_MESSAGE);
                } else {
                    e.printStackTrace();
                    System.out.println("at history are not available"); 
                    Page.getCurrent().reload();
                    if (VaadinSession.getCurrent()!=null && VaadinSession.getCurrent().getSession() != null) {
                        VaadinSession.getCurrent().getSession().invalidate();
                    }
                   
                }
            }
            memoryUsed = memoryUsed / 1000000000.0;

        }

        public Map<String, GalaxyFileObject> getTabMgfFilesMap() {
            return tabMgfFilesMap;
        }

        public History getWorkingHistory() {
            return workingHistory;
        }

        public double getMemoryUsed() {
            return memoryUsed;
        }

        public int getFilesNumber() {
            return filesNumber;
        }

        public int getDsNumbers() {
            return dsNumbers;
        }

        @Override
        public Map<String, GalaxyFileObject> call() throws Exception {
            return mgfFilesMap;
        }

    }
}
