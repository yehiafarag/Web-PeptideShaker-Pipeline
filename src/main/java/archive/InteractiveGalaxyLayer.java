package archive;

import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyTransferableFile;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.compomics.util.experiment.identification.identification_parameters.SearchParameters;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import archive.GalaxyConnectionPanelLayout;
import com.uib.web.peptideshaker.galaxy.utilities.GalaxyHistoryHandler;
import com.uib.web.peptideshaker.galaxy.utilities.GalaxyToolsHandler;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import pl.exsio.plupload.PluploadFile;

/**
 * This class represents Galaxy layer that work as middle layer between the
 * application and the galaxy server
 *
 * @author Yehia Farag
 */
public abstract class InteractiveGalaxyLayer {

//    private final HorizontalLayout galaxyConnectionPanel;
//    private final PopupView connectionSettingsPanel;

    /**
     * Galaxy server history management system
     *
     */
    private GalaxyHistoryHandler historyHandler;

    /**
     * Galaxy server tools management handler
     *
     */
    private GalaxyToolsHandler toolsHandler;

    private File user_folder;

    private String galaxyURL;

    private final GalaxyConnectionPanelLayout galaxyConnectionSettingsPanel;

//    private final Button connectionBtn;

    /**
     * Constructor to initialise Galaxy layer.
     */
    public InteractiveGalaxyLayer() {
        String userDataFolderUrl = VaadinSession.getCurrent().getAttribute("userDataFolderUrl") + "";
        galaxyConnectionSettingsPanel = new GalaxyConnectionPanelLayout() {
            @Override
            public void connectedToGalaxy(GalaxyInstance Galaxy_Instance) {
                try {
                    if (Galaxy_Instance != null) {
                        if (VaadinSession.getCurrent().getAttribute("ApiKey") != null) {
                            String APIKey = VaadinSession.getCurrent().getAttribute("ApiKey").toString();

                            if (!APIKey.equalsIgnoreCase(Galaxy_Instance.getApiKey())) {
                                //clean history and create new folder
                                user_folder = new File(userDataFolderUrl, APIKey);
                                if (user_folder.exists()) {
                                    for (File tFile : user_folder.listFiles()) {
                                        tFile.delete();
                                    }
                                }
                                user_folder.delete();
                            }
                        }
                        user_folder = new File(userDataFolderUrl, Galaxy_Instance.getApiKey() + "");
                        user_folder.mkdir();
                        VaadinSession.getCurrent().setAttribute("ApiKey", Galaxy_Instance.getApiKey() + "");
                        VaadinSession.getCurrent().setAttribute("galaxyUrl", Galaxy_Instance.getGalaxyUrl());
                        System.out.println("at galaxy 0");
                        galaxyURL = Galaxy_Instance.getGalaxyUrl();
                        if (!checkConn(galaxyURL)) {
                            systemDisconnected();
                            galaxyConnectionSettingsPanel.disconnectGalaxy();
//                            connectionBtn.setEnabled(true);
                            Notification.show("Error in connection check the web address", Notification.Type.ERROR_MESSAGE);
                            return;
                        }
                        System.out.println("at galaxy I ");
                        toolsHandler = new GalaxyToolsHandler(Galaxy_Instance.getToolsClient(), Galaxy_Instance.getWorkflowsClient(), Galaxy_Instance.getHistoriesClient()) {
                            @Override
                            public void synchronizeDataWithGalaxyServer() {
                                historyHandler.updateHistory();
                            }

                        };
                        
                        System.out.println("at galaxy II ");
                        historyHandler = new GalaxyHistoryHandler() {
                            @Override
                            public void synchronizeDataWithGalaxyServer(boolean busy, Map<String, GalaxyFileObject> historyFilesMap) {
                                //update history in the system 
                                jobsInProgress(busy, historyFilesMap);
                            }

                        };//  
                        historyHandler.connectToGalaxy(Galaxy_Instance, user_folder);
                        System.out.println("at galaxy III");
//                        connectionBtn.setCaption("Disconnect");
//                        connectionBtn.addStyleName("disconnect");
//                        connectionStatuesLabel.setValue("Galaxy is <font color='green'>connected </font><font size='3' color='green'> " + FontAwesome.SMILE_O.getHtml() + "</font>");
                        systemConnected();
                    } else {
                        System.out.println("at null galaxy");
//                        connectionSettingsPanel.setPopupVisible(true);
                        historyHandler = null;
                        toolsHandler = null;
                        systemDisconnected();
                    }
//                    connectionBtn.setEnabled(true);
                } catch (Exception exp) {
                    Thread t = new Thread(() -> {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    });
                    t.start();
                    historyHandler = null;
                    toolsHandler = null;
                    systemDisconnected();
                }
            }

            @Override
            public void hideGalaxyPanel() {
//                connectionSettingsPanel.setPopupVisible(false);
            }

        };
//        connectionSettingsPanel = new PopupView(null, galaxyConnectionSettingsPanel);
//        connectionSettingsPanel.setSizeFull();
//        connectionSettingsPanel.setHideOnMouseOut(false);
//        settingBtn.addComponent(connectionSettingsPanel);

//        connectionBtn.addClickListener((Button.ClickEvent event) -> {
//
//            if (connectionBtn.getCaption().equalsIgnoreCase("Disconnect")) {
//                //disconnect from galaxy
//                connectionBtn.setCaption("Connect");
//                connectionBtn.removeStyleName("disconnect");
//                connectionStatuesLabel.setValue("Galaxy is<font color='red'>  not connected </font><font size='3' color='red'> " + FontAwesome.FROWN_O.getHtml() + "</font>");
//                galaxyConnectionSettingsPanel.disconnectGalaxy();
//                historyHandler = null;
//                toolsHandler = null;
//                connectionBtn.setEnabled(true);
//                UI.getCurrent().getSession().close();
//                VaadinSession.getCurrent().getSession().invalidate();
//                Page.getCurrent().reload();
//
//            } else {
                //connect to galaxy
                galaxyConnectionSettingsPanel.validateAndConnect();
//            }
//
//        });

    }

//    public Layout getGalaxyConnectionPanel() {
//        return galaxyConnectionPanel;
//
//    }

//    /**
//     * Connect to Galaxy server
//     * @param username username. 
//     * @param password  user password. 
//     **/
//    public boolean connectToGalaxy(String username,String password){
//    
//    return true;
//    }
//    /**
//     * Connect to Galaxy server
//     * @param galaxyAPIKey personal API key. 
//     **/
//    public boolean connectToGalaxy(String galaxyAPIKey){
//    
//    return true;
//    }
    public abstract void systemConnected();

    public abstract void systemDisconnected();

    /**
     * Get the main search settings .par files Map
     *
     * @return searchSettingsFilesMap
     */
    public Map<String, GalaxyTransferableFile> getSearchSettingsFilesMap() {
        if (historyHandler != null) {
            return historyHandler.getSearchSettingsFilesMap();
        } else {
            return new HashMap<>();
        }
    }

    /**
     * Get the main search settings .par files Map
     *
     * @return searchSettingsFilesMap
     */
    public Map<String, PeptideShakerVisualizationDataset> getPeptideShakerVisualizationMap() {
        if (historyHandler != null) {
            return historyHandler.getPeptideShakerVisualizationMap();
        } else {
            return new HashMap<>();
        }
    }

    /**
     * Get the main FASTA files Map
     *
     * @return fastaFilesMap
     */
    public Map<String, GalaxyFileObject> getFastaFilesMap() {
        if (historyHandler != null) {
            return historyHandler.getFastaFilesMap();
        } else {
            return new HashMap<>();
        }
    }

    /**
     * Get the main FASTA files Map
     *
     * @return fastaFilesMap
     */
    public Map<String, GalaxyFileObject> getHistoryFilesMap() {
        if (historyHandler != null) {
            return historyHandler.getHistoryFilesMap();
        } else {
            return new HashMap<>();
        }
    }

    public boolean checkToolsAvailable() {

        return toolsHandler.isToolsAvailable();
    }

    /**
     * Get the main MGF files Map
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
     * Re-Index the files (FASTA or MGF files)
     *
     * @param id file id on galaxy server
     * @param historyId the history id that the file belong to
     * @param workHistoryId the history id that the new re-indexed file will be
     * stored in
     *
     * @return new re-indexed file id on galaxy
     *
     */
//    public String reIndexFile(String id, String historyId, String workHistoryId) {
//
//        if (toolsHandler != null) {
//            return toolsHandler.reIndexFile(id, historyId, workHistoryId);
//        }
//
//        return null;
//    }
    /**
     * Save search settings file into galaxy
     *
     * @param fileName search parameters file name
     * @param searchParameters searchParameters .par file
     */
    public Map<String, GalaxyTransferableFile> saveSearchGUIParameters(SearchParameters searchParameters, boolean editMode) {

        if (toolsHandler != null) {
            return toolsHandler.saveSearchGUIParameters(galaxyURL, user_folder, historyHandler.getSearchSettingsFilesMap(), historyHandler.getWorkingHistoryId(), searchParameters, editMode);
        }
        return null;

    }

    /**
     * Run Online Peptide-Shaker work-flow
     *
     * @param projectName
     * @param fastaFileId FASTA file dataset id
     * @param mgfIdsList list of MGF file dataset ids
     * @param searchEnginesList List of selected search engine names
     * @param searchParameters
     * @param otherSearchParameters
     */
    public void executeWorkFlow(String projectName, String fastaFileId, Set<String> mgfIdsList, Set<String> searchEnginesList, SearchParameters searchParameters, Map<String, Boolean> otherSearchParameters) {
        Map<String, String> mgfMap = new LinkedHashMap<>();
        mgfIdsList.forEach((mgfId) -> {
            mgfMap.put(mgfId, historyHandler.getMgfFilesMap().get(mgfId).getName());
        });
        PeptideShakerVisualizationDataset tempWorkflowOutput = toolsHandler.executeWorkFlow(projectName, fastaFileId, mgfMap, searchEnginesList, historyHandler.getWorkingHistoryId(), searchParameters);
        toolsHandler.synchronizeDataWithGalaxyServer();
    }

    public void deleteDataset(GalaxyFileObject ds) {
        if (ds.getType().equalsIgnoreCase("Web Peptide Shaker Dataset")) {
            PeptideShakerVisualizationDataset vDs = (PeptideShakerVisualizationDataset) ds;
//            toolsHandler.deleteDataset(galaxyURL, vDs.getHistoryId(), vDs.getCpsId());
//            toolsHandler.deleteDataset(galaxyURL, vDs.getHistoryId(), vDs.getProteinFileId());
//            toolsHandler.deleteDataset(galaxyURL, vDs.getHistoryId(), vDs.getPeptideFileId());
//            toolsHandler.deleteDataset(galaxyURL, vDs.getHistoryId(), vDs.getPsmFileId());
            toolsHandler.deleteDataset(galaxyURL, vDs.getHistoryId(), vDs.getSearchGUIFile().getGalaxyId());

        } else {
            toolsHandler.deleteDataset(galaxyURL, ds.getHistoryId(), ds.getGalaxyId());
        }

        historyHandler.updateHistory();
    }

    public boolean sendDataToNels(String historyId, String datasetGalaxyId) {
        boolean check = toolsHandler.sendToNels(historyId, datasetGalaxyId, galaxyURL);
        toolsHandler.synchronizeDataWithGalaxyServer();
        return check;

    }

    public boolean getFromNels(String historyId, String datasetGalaxyId) {
        return toolsHandler.getFromNels(historyId, datasetGalaxyId);

    }

    public boolean uploadToGalaxy(PluploadFile[] toUploadFiles) {
        return toolsHandler.uploadToGalaxy(historyHandler.getWorkingHistoryId(), toUploadFiles);

    }
    public abstract void jobsInProgress(boolean inprogress, Map<String, GalaxyFileObject> historyFilesMap);

    public void reConnectToGalaxy(String APIKEy, String galaxyUrl) {
        galaxyConnectionSettingsPanel.reConnectToGalaxy(APIKEy, galaxyUrl);
//        connectionBtn.click();

    }

    private boolean checkConn(String urlAddress) {
        try {
            URL url = new URL(urlAddress);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int code = connection.getResponseCode();
            if (code == 404) {
                return false;
            }

        } catch (MalformedURLException ex) {
            return false;
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

}
