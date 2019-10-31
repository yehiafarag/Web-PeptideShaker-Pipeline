package com.uib.web.peptideshaker.galaxy;

import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.galaxy.utilities.GalaxyHistoryHandler;
import com.uib.web.peptideshaker.galaxy.utilities.GalaxyToolsHandler;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstanceFactory;
import com.github.jmchilton.blend4j.galaxy.UsersClient;
import com.github.jmchilton.blend4j.galaxy.beans.User;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyTransferableFile;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
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
     * User overview information list for welcome page left panel
     */
    private final List<String> userOverViewList;
    /**
     * Decimal Format for memory usage
     */
    private final DecimalFormat dsFormater = new DecimalFormat("#.##");

//    private Set<String> csf_pr_Accession_List;
//    public Set<String> getCsf_pr_Accession_List() {
//        return csf_pr_Accession_List;
//    }
    /**
     * Constructor to initialise the main Galaxy history handler.
     */
    public GalaxyInteractiveLayer() {
        this.userOverViewList = new ArrayList<>();
//        if (csf_pr_Accession_List == null) {
//            csf_pr_Accession_List = initialiseCSFList();
//        }

        this.historyHandler = new GalaxyHistoryHandler() {
            @Override
            public void synchronizeDataWithGalaxyServer(Map<String, GalaxyFileObject> historyFilesMap, boolean jobsInProgress, boolean updatePresenterView, Set<String> toDeleteMap) {
                //update history in the system                 
                GalaxyInteractiveLayer.this.synchronizeDataWithGalaxyServer(historyFilesMap, jobsInProgress, updatePresenterView);
                if (!jobsInProgress && toDeleteMap != null && toolsHandler != null) {
                    toDeleteMap.forEach((galaxyId) -> {
                        toolsHandler.deleteDataset(Galaxy_Instance.getGalaxyUrl(), galaxyId.split(";")[0], galaxyId.split(";")[1], true, false);
                    });
                    System.out.println("done deleting files");
                    toDeleteMap.clear();
                }
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
            userOverViewList.clear();
            Galaxy_Instance = GalaxyInstanceFactory.get(galaxyServerUrl, userAPI);
            Galaxy_Instance.getHistoriesClient().getHistories();
            user_folder = new File(userDataFolderUrl, Galaxy_Instance.getApiKey() + "");
            user_folder.mkdir();
            historyHandler.connectToGalaxy(Galaxy_Instance, user_folder);
            toolsHandler = new GalaxyToolsHandler(Galaxy_Instance.getToolsClient(), Galaxy_Instance.getWorkflowsClient(), Galaxy_Instance.getHistoriesClient()) {
                @Override
                public void synchronizeDataWithGalaxyServer(boolean updatePresenterView) {
                    historyHandler.updateHistory(updatePresenterView);
                    userOverViewList.set(1, historyHandler.getDatasetsNumber() + "");
                    userOverViewList.set(2, historyHandler.getFilesNumber() + "");
                    userOverViewList.set(3, dsFormater.format(historyHandler.getMemoryUsage()) + " GB");
                }
            };
            VaadinSession.getCurrent().setAttribute("ApiKey", Galaxy_Instance.getApiKey());

            UsersClient userClient = Galaxy_Instance.getUsersClient();
            User user = userClient.getUsers().get(0);
            userOverViewList.add(user.getUsername().replace("public_user", "Guest User <i style='font-size: 10px;position: relative;top: -23px;left: 101px;'>(public data)</i>"));
            userOverViewList.add(historyHandler.getDatasetsNumber() + "");
            userOverViewList.add(historyHandler.getFilesNumber() + "");
            userOverViewList.add(historyHandler.getMemoryUsage());
            userOverViewList.add(toolsHandler.getSearch_GUI_Tool_version());
            userOverViewList.add(toolsHandler.getPeptideShaker_Tool_Version());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("at ---->>>>exception in galaxy connection cought " + e.getMessage());
            if (VaadinSession.getCurrent().getSession() != null) {
                VaadinSession.getCurrent().getSession().invalidate();
            }
            Page.getCurrent().reload();
            return false;
        }
        return true;
    }

    /**
     * Get user overview information list for welcome page left panel
     *
     * @return list of user information username/#datasets/#files/memory used on
     * Galaxy Server
     */
    public List<String> getUserOverViewList() {
        return userOverViewList;
    }

    /**
     * Run Online Peptide-Shaker search and analysis work-flow
     *
     * @param projectName new project name
     * @param fastaFileId FASTA file dataset id
     * @param searchParameterFileId .par file id
     * @param inputFilesIdsList list of MGF or Raw file dataset ids
     * @param searchEnginesList List of selected search engine names
     * @param searchParameters User input search parameters
     */
    public void execute_SearchGUI_PeptideShaker_WorkFlow(String projectName, String fastaFileId, String searchParameterFileId, Set<String> inputFilesIdsList, Set<String> searchEnginesList, IdentificationParameters searchParameters, boolean quant) {
        Map<String, String> inputFilesMap = new LinkedHashMap<>();
        inputFilesIdsList.forEach((inputFileId) -> {
            if (quant) {
                inputFilesMap.put(inputFileId, historyHandler.getRawFilesMap().get(inputFileId).getName());
            } else {
                inputFilesMap.put(inputFileId, historyHandler.getMgfFilesMap().get(inputFileId).getName());
            }
        });
        toolsHandler.execute_SearchGUI_PeptideShaker_WorkFlow(projectName, fastaFileId, searchParameterFileId, inputFilesMap, searchEnginesList, historyHandler.getWorkingHistoryId(), searchParameters, quant);
        toolsHandler.synchronizeDataWithGalaxyServer(true);
    }

    /**
     * Run Online Peptide-Shaker & PathwayMatcher search and analysis work-flow
     *
     * @param projectName new project name
     * @param fastaFileId FASTA file dataset id
     * @param mgfIdsList list of MGF file dataset ids
     * @param searchEnginesList List of selected search engine names
     * @param searchParameters User input search parameters
     */
    public void execute_SearchGUI_PeptideShaker_PathwayMatcher_WorkFlow(String projectName, String fastaFileId, Set<String> mgfIdsList, Set<String> searchEnginesList, IdentificationParameters searchParameters) {
        Map<String, String> mgfMap = new LinkedHashMap<>();
        mgfIdsList.forEach((mgfId) -> {
            mgfMap.put(mgfId, historyHandler.getMgfFilesMap().get(mgfId).getName());
        });
        toolsHandler.execute_SearchGUI_PeptideShaker_PathwayMatcher_WorkFlow(projectName, fastaFileId, mgfMap, searchEnginesList, historyHandler.getWorkingHistoryId(), searchParameters);
        toolsHandler.synchronizeDataWithGalaxyServer(true);
    }

    /**
     * Save user input search parameters file into galaxy to be reused in future
     *
     * @param searchParameters search parameters file.
     * @param newFile is new or just edited file
     * @return updated get user Search Settings Files Map
     */
    public Map<String, GalaxyTransferableFile> saveSearchGUIParameters(IdentificationParameters searchParameters, boolean newFile) {
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
     * Get the available Raw files Map on Galaxy Server
     *
     * @return rawfFilesMap
     *
     */
    public Map<String, GalaxyFileObject> getRawFilesMap() {
        if (historyHandler != null) {
            return historyHandler.getRawFilesMap();
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
                toolsHandler.deleteDataset(Galaxy_Instance.getGalaxyUrl(), vDs.getHistoryId(), galaxyFile.getGalaxyId(), true, true);
            });
            toolsHandler.deleteDataset(Galaxy_Instance.getGalaxyUrl(), vDs.getHistoryId(), vDs.getGalaxyId(), true, true);
            toolsHandler.deleteDataset(Galaxy_Instance.getGalaxyUrl(), vDs.getHistoryId(), vDs.getSearchGUIFile().getGalaxyId(), true, true);

        } else {
            toolsHandler.deleteDataset(Galaxy_Instance.getGalaxyUrl(), fileObject.getHistoryId(), fileObject.getGalaxyId(), true, true);
        }
    }

    private Set<String> initialiseCSFList() {
        Set<String> csfprAccList = new HashSet<>();
        String csfprfilepath = (String) VaadinSession.getCurrent().getAttribute("csfprfile");
        if (csfprfilepath == null) {
            return null;
        }
        try {
            File file = new File(csfprfilepath);//Galaxy-Workflow-Web-Peptide-Shaker-Multi-MGF-2018.ga
            FileReader fileReader = new FileReader(file);
            String line;
            try ( // Always wrap FileReader in BufferedReader.
                    BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                while ((line = bufferedReader.readLine()) != null) {
                    csfprAccList.add(line.split("\\(")[0].trim());
                }
                // Always close files.
            }
        } catch (FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" + "'");
        } catch (IOException ex) {
             ex.printStackTrace();
            System.out.println("Error reading file '" + "'");
        }
        return csfprAccList;

    }

    /**
     * Update and synchronise the data on Galaxy Server with the local file
     * system on Online Peptide Shaker
     *
     * @param historyFilesMap List of available files(datasets) available on
     * galaxy
     * @param jobsInProgress there is currently jobs running on Galaxy Server
     * @param updatePresenterView open file system view after updating the file
     * system
     */
    public abstract void synchronizeDataWithGalaxyServer(Map<String, GalaxyFileObject> historyFilesMap, boolean jobsInProgress, boolean updatePresenterView);

}
