package com.uib.web.peptideshaker.galaxy;

import com.uib.web.peptideshaker.galaxy.utilities.GalaxyHistoryHandler;
import com.uib.web.peptideshaker.galaxy.utilities.GalaxyToolsHandler;
import com.compomics.util.experiment.identification.identification_parameters.SearchParameters;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstanceFactory;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyTransferableFile;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.vaadin.server.VaadinSession;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import pl.exsio.plupload.PluploadFile;

/**
 * This class represents main galaxy interactive layer that interact with both
 * Galaxy server and Online PeptideShaker
 *
 * @author Yehia Farag
 */
public abstract class GalaxyInteractiveLayer {

    /**
     * Main Galaxy Server instance.
     */
    private GalaxyInstance Galaxy_Instance;
    /**
     * Galaxy Server history management system
     */
    private final GalaxyHistoryHandler historyHandler;
    /**
     * Galaxy Server tools management handler
     */
    private GalaxyToolsHandler toolsHandler;
    /**
     * User data files folder
     */
    private File user_folder;

    /**
     * Constructor to initialise the main Galaxy history handler.
     */
    public GalaxyInteractiveLayer() {
        this.historyHandler = new GalaxyHistoryHandler() {
            @Override
            public void synchronizeDataWithGalaxyServer(Map<String, GalaxyFileObject> historyFilesMap, boolean jobsInProgress,boolean updatePresenterView) {
                //update history in the system                 
                GalaxyInteractiveLayer.this.synchronizeDataWithGalaxyServer(historyFilesMap, jobsInProgress,updatePresenterView);
            }
        };
    }

    /**
     * Connect the system to Galaxy Server
     *
     * @param galaxyServerUrl the address of Galaxy Server
     * @param userAPI Galaxy user API key
     * @param userDataFolderUrl main folder for storing users data
     * @return System connected to Galaxy server or not
     */
    public boolean connectToGalaxyServer(String galaxyServerUrl, String userAPI, String userDataFolderUrl) {
        try {
            Galaxy_Instance = GalaxyInstanceFactory.get(galaxyServerUrl, userAPI);
            Galaxy_Instance.getHistoriesClient().getHistories();
            user_folder = new File(userDataFolderUrl, Galaxy_Instance.getApiKey() + "");
            user_folder.mkdir();
            historyHandler.connectToGalaxy(Galaxy_Instance, user_folder);
            toolsHandler = new GalaxyToolsHandler(Galaxy_Instance.getToolsClient(), Galaxy_Instance.getWorkflowsClient(), Galaxy_Instance.getHistoriesClient()) {
                @Override
                public void synchronizeDataWithGalaxyServer(boolean updatePresenterView) {
                    historyHandler.updateHistory(updatePresenterView);
                }
            };
            VaadinSession.getCurrent().setAttribute("ApiKey", Galaxy_Instance.getApiKey());

        } catch (Exception e) {
            System.out.println("exception in galaxy connection cought");
            return false;
        }
        return true;
    }

    /**
     * Run Online Peptide-Shaker search and analysis work-flow
     *
     * @param projectName new project name
     * @param fastaFileId FASTA file dataset id
     * @param mgfIdsList list of MGF file dataset ids
     * @param searchEnginesList List of selected search engine names
     * @param searchParameters User input search parameters
     */
    public void execute_SearchGUI_PeptideShaker_WorkFlow(String projectName, String fastaFileId, Set<String> mgfIdsList, Set<String> searchEnginesList, SearchParameters searchParameters) {
        Map<String, String> mgfMap = new LinkedHashMap<>();
        mgfIdsList.forEach((mgfId) -> {
            mgfMap.put(mgfId, historyHandler.getMgfFilesMap().get(mgfId).getName());
        });
        toolsHandler.execute_SearchGUI_PeptideShaker_WorkFlow(projectName, fastaFileId, mgfMap, searchEnginesList, historyHandler.getWorkingHistoryId(), searchParameters);
        toolsHandler.synchronizeDataWithGalaxyServer(true);
    }

    /**
     * Save user input search parameters file into galaxy to be reused in future
     *
     * @param searchParameters search parameters file.
     * @param newFile is new or just edited file
     * @return updated get user Search Settings Files Map
     */
    public Map<String, GalaxyTransferableFile> saveSearchGUIParameters(SearchParameters searchParameters, boolean newFile) {
        if (toolsHandler != null) {
            return toolsHandler.saveSearchGUIParameters(Galaxy_Instance.getGalaxyUrl(), user_folder, historyHandler.getSearchSettingsFilesMap(), historyHandler.getWorkingHistoryId(), searchParameters, newFile);
        }
        return null;

    }

    /**
     * Get the main search settings .par files Map on Galaxy Server
     *
     * @return search Settings Files .par Map(Galaxy dataset id to galaxy file)
     */
    public Map<String, GalaxyTransferableFile> getSearchSettingsFilesMap() {
        if (historyHandler != null) {
            return historyHandler.getSearchSettingsFilesMap();
        } else {
            return new HashMap<>();
        }
    }

    /**
     * Get the available FASTA files Map on Galaxy Server
     *
     * @return FASTA Files Map (Galaxy dataset id to galaxy file)
     */
    public Map<String, GalaxyFileObject> getFastaFilesMap() {
        if (historyHandler != null) {
            return historyHandler.getFastaFilesMap();
        } else {
            return new HashMap<>();
        }
    }

    /**
     * Get the available MGF files Map on Galaxy Server
     *
     * @return mgfFilesMap
     *
     */
    public Map<String, GalaxyFileObject> getMgfFilesMap() {
        if (historyHandler != null) {
            return historyHandler.getMgfFilesMap();
        } else {
            return new HashMap<>();
        }
    }

    /**
     * Upload Files (FASTA and MGF files to Galaxy Server)
     *
     * @param toUploadFiles list of the files to be uploaded to Galaxy Server
     * @return files are successfully uploaded to Galaxy Server
     */
    public boolean uploadToGalaxy(PluploadFile[] toUploadFiles) {
        return toolsHandler.uploadToGalaxy(historyHandler.getWorkingHistoryId(), toUploadFiles);

    }

    /**
     * Delete action for files from Galaxy Server
     *
     * @param fileObject the file to be removed from Galaxy Server
     */
    public void deleteDataset(GalaxyFileObject fileObject) {
        if (fileObject.getType().equalsIgnoreCase("Web Peptide Shaker Dataset")) {
            PeptideShakerVisualizationDataset vDs = (PeptideShakerVisualizationDataset) fileObject;
            vDs.getInputMGFFiles().values().forEach((galaxyFile) -> {
                toolsHandler.deleteDataset(Galaxy_Instance.getGalaxyUrl(), vDs.getHistoryId(), galaxyFile.getGalaxyId(),true);
            });
            toolsHandler.deleteDataset(Galaxy_Instance.getGalaxyUrl(), vDs.getHistoryId(), vDs.getGalaxyId(),true);
            toolsHandler.deleteDataset(Galaxy_Instance.getGalaxyUrl(), vDs.getHistoryId(), vDs.getSearchGUIFile().getGalaxyId(),true);

        } else {
            toolsHandler.deleteDataset(Galaxy_Instance.getGalaxyUrl(), fileObject.getHistoryId(), fileObject.getGalaxyId(),true);
        }
    }

    /**
     * Update and synchronise the data on Galaxy Server with the local file
     * system on Online Peptide Shaker
     *
     * @param historyFilesMap List of available files(datasets) available on
     * galaxy
     * @param jobsInProgress there is currently jobs running on Galaxy Server
     * @param updatePresenterView open file system view after updating the file system
     */
    public abstract void synchronizeDataWithGalaxyServer(Map<String, GalaxyFileObject> historyFilesMap, boolean jobsInProgress,boolean updatePresenterView);

}
