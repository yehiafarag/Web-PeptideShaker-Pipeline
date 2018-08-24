package com.uib.web.peptideshaker;

import com.uib.web.peptideshaker.presenter.PresenterManager;
import com.compomics.util.experiment.identification.identification_parameters.SearchParameters;
import com.uib.web.peptideshaker.galaxy.GalaxyInteractiveLayer;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyTransferableFile;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.presenter.FileSystemPresenter;
import com.uib.web.peptideshaker.presenter.InteractivePSPRojectResultsPresenter;
import com.uib.web.peptideshaker.presenter.SearchGUI_PeptideShaker_Tool_Presenter;
import com.uib.web.peptideshaker.presenter.WelcomePagePresenter;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.VerticalLayout;
import java.util.Map;
import java.util.Set;
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
     * The tools view component (frame to start analysis).
     */
    private final SearchGUI_PeptideShaker_Tool_Presenter SearchGUI_PeptideShaker_Tool_Presenter;
    /**
     * Container to view the main available datasets and files on galaxy server
     * or in other databases.
     */
    private final FileSystemPresenter fileSystemPresenter;
    /**
     * There is jobs run in progress
     */
    private boolean inProgress;

    /**
     * Constructor to initialise the application.
     */
    public WebPeptideShakerApp() {
        WebPeptideShakerApp.this.setSizeFull();
        WebPeptideShakerApp.this.setMargin(new MarginInfo(true, true, true, true));
        WebPeptideShakerApp.this.addStyleName("frame");
        this.Galaxy_Interactive_Layer = new GalaxyInteractiveLayer() {
            @Override
            public void synchronizeDataWithGalaxyServer(Map<String, GalaxyFileObject> historyFilesMap, boolean jobsInProgress) {
                fileSystemPresenter.setBusy(jobsInProgress, historyFilesMap);
                presentationManager.viewLayout(fileSystemPresenter.getViewId());
            }
        };
        presentationManager = new PresenterManager();
        WebPeptideShakerApp.this.addComponent(presentationManager);
        /**
         * landing page initialisation.
         *
         */
        WelcomePagePresenter welcomePage = new WelcomePagePresenter() {
            @Override
            public boolean connectToGalaxy(String userAPI) {
                String galaxyServerUrl = VaadinSession.getCurrent().getAttribute("galaxyServerUrl").toString();
                String userDataFolderUrl = VaadinSession.getCurrent().getAttribute("userDataFolderUrl").toString();
                if (userAPI.equalsIgnoreCase("test_User_Login")) {
                    userAPI = VaadinSession.getCurrent().getAttribute("testUserAPIKey").toString();
                }
                System.out.println(".connectToGalaxy()");
                boolean connected = Galaxy_Interactive_Layer.connectToGalaxyServer(galaxyServerUrl, userAPI, userDataFolderUrl);
                presentationManager.setSideButtonsVisible(connected);
                inProgress = false;
                if (connected) {

                }

                return connected;
            }

        };
        presentationManager.registerView(welcomePage);
        presentationManager.viewLayout(welcomePage.getViewId());
        SearchGUI_PeptideShaker_Tool_Presenter = new SearchGUI_PeptideShaker_Tool_Presenter() {
            @Override
            public void execute_SearchGUI_PeptideShaker_WorkFlow(String projectName, String fastaFileId, Set<String> mgfIdsList, Set<String> searchEnginesList, SearchParameters searchParameters) {
                Galaxy_Interactive_Layer.execute_SearchGUI_PeptideShaker_WorkFlow(projectName, fastaFileId, mgfIdsList, searchEnginesList, searchParameters);
            }

            @Override
            public Map<String, GalaxyTransferableFile> saveSearchGUIParameters(SearchParameters searchParameters, boolean editMode) {
                return Galaxy_Interactive_Layer.saveSearchGUIParameters(searchParameters, editMode);
            }

            @Override
            public void maximizeView() {
                updatePeptideShakerToolInputForm(Galaxy_Interactive_Layer.getSearchSettingsFilesMap(), Galaxy_Interactive_Layer.getFastaFilesMap(), Galaxy_Interactive_Layer.getMgfFilesMap());
                super.maximizeView(); //To change body of generated methods, choose Tools | Templates.
            }

        };

        presentationManager.registerView(SearchGUI_PeptideShaker_Tool_Presenter);
        fileSystemPresenter = new FileSystemPresenter() {
            @Override
            public void deleteDataset(GalaxyFileObject ds) {
                // Interactive_Galaxy_Layer.deleteDataset(ds);
            }

            @Override
            public void viewDataset(PeptideShakerVisualizationDataset ds) {
                interactivePSPRojectResultsPresenter = new InteractivePSPRojectResultsPresenter();
                presentationManager.registerView(interactivePSPRojectResultsPresenter);
                interactivePSPRojectResultsPresenter.setSelectedDataset(ds);
                presentationManager.viewLayout(interactivePSPRojectResultsPresenter.getViewId());
            }

            @Override
            public boolean sendToNeLS(GalaxyFileObject ds) {
                if (ds.getType().equalsIgnoreCase("Web Peptide Shaker Dataset")) {

                    PeptideShakerVisualizationDataset vDs = (PeptideShakerVisualizationDataset) ds;

                    boolean check;// = Galaxy_Layer.sendDataToNels(vDs.getHistoryId(), vDs.getProteinFileId());
//                    if (!check) {
//                        return check;
//                    }
//                    check = Galaxy_Layer.sendDataToNels(vDs.getHistoryId(), vDs.getPeptideFileId());
//                    if (!check) {
//                        return check;
//                    }
//                    check = Galaxy_Layer.sendDataToNels(vDs.getHistoryId(), vDs.getPsmFileId());
//                    if (!check) {
//                        return check;
//                    }
                    check = false;// Interactive_Galaxy_Layer.sendDataToNels(vDs.getHistoryId(), vDs.getSearchGUIFile().getGalaxyId());
                    if (!check) {
                        return check;
                    }
                    check = false;//Interactive_Galaxy_Layer.sendDataToNels(vDs.getHistoryId(), vDs.getZipFileId());
                    return check;

                }

                return false;//Interactive_Galaxy_Layer.sendDataToNels(ds.getHistoryId(), ds.getGalaxyId());
            }

            @Override
            public boolean getFromNels(GalaxyFileObject ds) {
                return false;//Interactive_Galaxy_Layer.getFromNels(ds.getHistoryId(), ds.getGalaxyId());
            }

            @Override
            public boolean uploadToGalaxy(PluploadFile[] toUploadFiles) {
                return false;//Interactive_Galaxy_Layer.uploadToGalaxy(toUploadFiles);
            }

        };

        presentationManager.registerView(fileSystemPresenter);
        interactivePSPRojectResultsPresenter = new InteractivePSPRojectResultsPresenter();
        presentationManager.registerView(interactivePSPRojectResultsPresenter);

//        Interactive_Galaxy_Layer = new InteractiveGalaxyLayer() {
//            @Override
//            public void systemConnected() {
//                presentationManager.setSideButtonsVisible(true);
//                connectGalaxyServer();
//            }
//
//            @Override
//            public void systemDisconnected() {
//                presentationManager.setSideButtonsVisible(false);
//            }
//
//            @Override
//            public void synchronizeDataWithGalaxyServer(boolean inprogress, Map<String, GalaxyFileObject> historyFilesMap) {
//                fileSystemView.setBusy(inprogress, historyFilesMap);
//                presentationManager.viewLayout(fileSystemView.getViewId());
//                toolsView.updatePeptideShakerToolInputForm(Interactive_Galaxy_Layer.getSearchSettingsFilesMap(), Interactive_Galaxy_Layer.getFastaFilesMap(), Interactive_Galaxy_Layer.getMgfFilesMap());
//
//            }
//        };
    }


    /**
     * Reconnect to Galaxy Server incase of reload
     *
     * @param APIKey User Galaxy API key
     * @param galaxyUrl web address for Galaxy Server
     */
    public void reConnectToGalaxyServer(String APIKey, String galaxyUrl) {
//        Interactive_Galaxy_Layer.reConnectToGalaxy(APIKEy, galaxyUrl);
//        if ((boolean) VaadinSession.getCurrent().getAttribute("nelsgalaxy")) {
////            nelsGalaxyConnectionBtn.addStyleName("disconnect");
//        } else {
////            nelsGalaxyConnectionBtn.removeStyleName("disconnect");
//
//        }

    }

}
