package com.uib.web.peptideshaker;

import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.presenter.PresenterManager;
import com.uib.web.peptideshaker.galaxy.GalaxyInteractiveLayer;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyTransferableFile;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.model.UploadedProjectUtility;
import com.uib.web.peptideshaker.presenter.FileSystemPresenter;
import com.uib.web.peptideshaker.presenter.InteractivePSPRojectResultsPresenter;
import com.uib.web.peptideshaker.presenter.SearchGUIPeptideShakerToolPresenter;
import com.uib.web.peptideshaker.presenter.WelcomePagePresenter;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.HttpsURLConnection;
import pl.exsio.plupload.PluploadFile;

/**
 * This class represents the main landing Online PeptideShaker application
 *
 * @author Yehia Farag
 */
public class WebPeptideShakerApp extends VerticalLayout {

    /**
     * PeptideShaker visualisation layer - Coordinator to organise the different
     * views (home, analysis, data, or results visualisation).
     */
    private final PresenterManager presentationManager;
    /**
     * The Main Galaxy server layer that interact with Galaxy server.
     */
    private final GalaxyInteractiveLayer Galaxy_Interactive_Layer;

    /**
     * Container to view selected PeptideShaker projects.
     */
    private InteractivePSPRojectResultsPresenter interactivePSPRojectResultsPresenter;

    /**
     * The SearchGUI & PeptideShaker tools view component (frame to start
     * analysis).
     */
    private SearchGUIPeptideShakerToolPresenter searchGUIPeptideShakerToolPresenter;

    /**
     * The SearchGUI & PeptideShaker & PathwayMatcher tools view component
     * (frame to start analysis).
     */
    //private final SearchGUI_PeptideShaker_PathwayMatcher_Tool_Presenter SearchGUI_PeptideShaker_PathwayMatcher_Tool_Presenter;
    /**
     * Container to view the main available datasets and files on galaxy server
     * or in other databases.
     */
    private FileSystemPresenter fileSystemPresenter;

    private final UploadedProjectUtility uploadedProjectUtility;

    /**
     * Constructor to initialise the application.
     *
     * @param galaxyUrl web link to galaxy server
     */
    public WebPeptideShakerApp(String galaxyUrl) {
        WebPeptideShakerApp.this.setSizeFull();
        WebPeptideShakerApp.this.setMargin(new MarginInfo(true, true, true, true));
        WebPeptideShakerApp.this.addStyleName("mainapplicationframe");
        WebPeptideShakerApp.this.addStyleName("frame");
        this.uploadedProjectUtility = new UploadedProjectUtility() {
            @Override
            public void viewUploadedProjectDataset(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {
                ((LinkedHashMap<String, GalaxyFileObject>) VaadinSession.getCurrent().getAttribute("uploaded_projects_" + Galaxy_Interactive_Layer.getAPIKey())).put(peptideShakerVisualizationDataset.getProjectName(), peptideShakerVisualizationDataset);
                Map<String, GalaxyFileObject> historyFilesMap = new LinkedHashMap<>();
                historyFilesMap.put(peptideShakerVisualizationDataset.getProjectName(), peptideShakerVisualizationDataset);
                historyFilesMap.putAll(fileSystemPresenter.getHistoryFilesMap());
                fileSystemPresenter.updateSystemData(historyFilesMap, fileSystemPresenter.isJobInProgress());
                interactivePSPRojectResultsPresenter.setSelectedDataset(peptideShakerVisualizationDataset);
                presentationManager.viewLayout(interactivePSPRojectResultsPresenter.getViewId());
            }

        };
        /**
         * check galaxy available.
         */
        boolean availableGalaxyServer = checkConnectionToGalaxy(galaxyUrl);
        if (availableGalaxyServer) {
            this.Galaxy_Interactive_Layer = new GalaxyInteractiveLayer() {
                @Override
                public void synchronizeDataWithGalaxyServer(Map<String, GalaxyFileObject> historyFilesMap, boolean jobsInProgress, boolean updatePresenterView) {
                    if (historyFilesMap.size() == 1 && historyFilesMap.keySet().iterator().next().contains("_ExternalDS")) {
                        fileSystemPresenter.viewDataset((PeptideShakerVisualizationDataset) historyFilesMap.values().iterator().next());
                        for (String btn : presentationManager.getPresenterBtnsMap().keySet()) {
                            presentationManager.getPresenterBtnsMap().get(btn).setEnabled(false);
                        }
                    } else {
                        Map<String, GalaxyFileObject> tempHistoryFilesMap = new LinkedHashMap<>();
                        tempHistoryFilesMap.putAll(((LinkedHashMap<String, GalaxyFileObject>) VaadinSession.getCurrent().getAttribute("uploaded_projects_" + this.getAPIKey())));
                        tempHistoryFilesMap.putAll(historyFilesMap);
                        fileSystemPresenter.updateSystemData(tempHistoryFilesMap, jobsInProgress);
                        searchGUIPeptideShakerToolPresenter.updateSystemData(historyFilesMap);

                    }
                }
            };
        } else {
            this.Galaxy_Interactive_Layer = null;
        }
        presentationManager = new PresenterManager();
        presentationManager.addStyleName("mainapplicationframe");
        WebPeptideShakerApp.this.addComponent(presentationManager);
        WebPeptideShakerApp.this.setComponentAlignment(presentationManager, Alignment.TOP_CENTER);

        /**
         * landing page initialisation.
         *
         */
        WelcomePagePresenter welcomePage = new WelcomePagePresenter(availableGalaxyServer) {
            @Override
            public List<String> connectToGalaxy(String userAPI, String viewId) {
                String galaxyServerUrl = VaadinSession.getCurrent().getAttribute("galaxyServerUrl").toString();
                String userDataFolderUrl = VaadinSession.getCurrent().getAttribute("userDataFolderUrl").toString();

                if (userAPI.equalsIgnoreCase("test_User_Login")) {
                    userAPI = VaadinSession.getCurrent().getAttribute("testUserAPIKey").toString();
                }
                if (VaadinSession.getCurrent().getAttribute("uploaded_projects_" + userAPI) == null) {
                    VaadinSession.getCurrent().setAttribute("uploaded_projects_" + userAPI, new LinkedHashMap<>());
                }
                Galaxy_Interactive_Layer.connectToGalaxyServer(galaxyServerUrl, userAPI, userDataFolderUrl);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                }
                return Galaxy_Interactive_Layer.getUserOverViewList();

            }

            @Override
            public void maximizeView() {
                if (Galaxy_Interactive_Layer != null) {
                    super.updateUserOverviewPanel(Galaxy_Interactive_Layer.getUserOverViewList());
                }
                super.maximizeView(); //To change body of generated methods, choose Tools | Templates.
            }

        };
        presentationManager.setSideButtonsVisible(true);
        welcomePage.setPresenterControlButtonContainer(presentationManager.getPresenterButtonsContainerLayout());
        searchGUIPeptideShakerToolPresenter = new SearchGUIPeptideShakerToolPresenter() {
            @Override
            public void execute_SearchGUI_PeptideShaker_WorkFlow(String projectName, String fastaFileId, String searchParameterFileId, Set<String> inputFilesIdsList, Set<String> searchEnginesList, IdentificationParameters searchParameters, boolean quant) {
                Galaxy_Interactive_Layer.execute_SearchGUI_PeptideShaker_WorkFlow(projectName, fastaFileId, searchParameterFileId, inputFilesIdsList, searchEnginesList, searchParameters, quant);
                presentationManager.viewLayout(fileSystemPresenter.getViewId());
            }

            @Override
            public Map<String, GalaxyTransferableFile> saveSearchGUIParameters(IdentificationParameters searchParameters, boolean isNew) {
                return Galaxy_Interactive_Layer.saveSearchGUIParameters(searchParameters, isNew);
            }

            @Override
            public void maximizeView() {
//                updatePeptideShakerToolInputForm(Galaxy_Interactive_Layer.getSearchSettingsFilesMap(), Galaxy_Interactive_Layer.getFastaFilesMap(), Galaxy_Interactive_Layer.getMgfFilesMap(), Galaxy_Interactive_Layer.getRawFilesMap());
                super.maximizeView(); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean uploadToGalaxy(PluploadFile[] toUploadFiles) {
                boolean check = Galaxy_Interactive_Layer.uploadToGalaxy(toUploadFiles);
                fileSystemPresenter.updateSystemData(null, check);
                searchGUIPeptideShakerToolPresenter.updateSystemData(null);
                return check;

            }

        };
        presentationManager.registerView(searchGUIPeptideShakerToolPresenter);
        searchGUIPeptideShakerToolPresenter.getLargePresenterControlButton().setEnabled(availableGalaxyServer);
        searchGUIPeptideShakerToolPresenter.getSmallPresenterControlButton().setEnabled(availableGalaxyServer);
        if (!availableGalaxyServer) {
            Notification.show("Galaxy server is not available", Notification.Type.TRAY_NOTIFICATION);
            searchGUIPeptideShakerToolPresenter.getLargePresenterControlButton().setDescription("Galaxy server is not available");
            searchGUIPeptideShakerToolPresenter.getSmallPresenterControlButton().setDescription("Galaxy server is not available");
        }

        fileSystemPresenter = new FileSystemPresenter() {
            @Override
            public void deleteDataset(GalaxyFileObject ds) {

                if (!ds.getType().equalsIgnoreCase("User uploaded Project")) {
                    Galaxy_Interactive_Layer.deleteDataset(ds);
                } else {
                    ((LinkedHashMap<String, GalaxyFileObject>) VaadinSession.getCurrent().getAttribute("uploaded_projects_" + Galaxy_Interactive_Layer.getAPIKey())).remove(ds.getName());
                    Map<String, GalaxyFileObject> historyFilesMap = new LinkedHashMap<>();
                    fileSystemPresenter.getHistoryFilesMap().remove(ds.getName());
                    historyFilesMap.putAll(fileSystemPresenter.getHistoryFilesMap());
                    fileSystemPresenter.updateSystemData(historyFilesMap, fileSystemPresenter.isJobInProgress());
                }
            }

            @Override
            public void viewDataset(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {
                if (peptideShakerVisualizationDataset != null) {
                    if ((this.getData() + "").equalsIgnoreCase(peptideShakerVisualizationDataset.getProjectName())) {
                        presentationManager.viewLayout(interactivePSPRojectResultsPresenter.getViewId());
                        return;
                    }
                    this.setData(peptideShakerVisualizationDataset.getProjectName());
                }
                interactivePSPRojectResultsPresenter = new InteractivePSPRojectResultsPresenter() {
                    @Override
                    public boolean[] processVisualizationDataset(String projectName, Map<String, PluploadFile> uploadedFileMap) {

                        return uploadedProjectUtility.processVisualizationDataset(projectName, uploadedFileMap, Galaxy_Interactive_Layer.getCsf_pr_Accession_List());
                    }

                };
                presentationManager.registerView(interactivePSPRojectResultsPresenter);
                interactivePSPRojectResultsPresenter.setSelectedDataset(peptideShakerVisualizationDataset);
                presentationManager.viewLayout(interactivePSPRojectResultsPresenter.getViewId());

            }

            @Override
            public boolean sendToNeLS(GalaxyFileObject ds) {
                if (ds.getType().equalsIgnoreCase("Web Peptide Shaker Dataset")) {
                    boolean check;
                    check = false;
                    if (!check) {
                        return check;
                    }
                    check = false;
                    return check;

                }

                return false;
            }

            @Override
            public boolean getFromNels(GalaxyFileObject ds) {
                return false;
            }
        };
        presentationManager.registerView(welcomePage);
        presentationManager.viewLayout(welcomePage.getViewId());
        presentationManager.registerView(fileSystemPresenter);

        interactivePSPRojectResultsPresenter = new InteractivePSPRojectResultsPresenter() {
            @Override
            public boolean[] processVisualizationDataset(String projectName, Map<String, PluploadFile> uploadedFileMap) {
                return uploadedProjectUtility.processVisualizationDataset(projectName, uploadedFileMap, Galaxy_Interactive_Layer.getCsf_pr_Accession_List());
            }

        };

        presentationManager.registerView(interactivePSPRojectResultsPresenter);

    }

    /**
     * Check Galaxy server is available.
     *
     * @param urlAddress Galaxy server url
     * @return is galaxy server available online
     */
    private boolean checkConnectionToGalaxy(String urlAddress) {
        try {
            URL url = new URL(urlAddress);
            if (urlAddress.contains("https")) {
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                int code = connection.getResponseCode();
                if (code == 404) {
                    return false;
                }
            } else {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                int code = connection.getResponseCode();
                if (code == 404) {
                    return false;
                }
            }

        } catch (MalformedURLException ex) {
            return false;
        } catch (IOException ex) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
