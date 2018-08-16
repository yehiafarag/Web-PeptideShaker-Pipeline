package com.uib.web.peptideshaker;

import com.compomics.util.experiment.identification.identification_parameters.SearchParameters;
import com.uib.web.peptideshaker.galaxy.dataobjects.SystemDataSet;
import com.uib.web.peptideshaker.galaxy.dataobjects.GalaxyFile;
import com.uib.web.peptideshaker.galaxy.InteractiveGalaxyLayer;
import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.presenter.FileSystemPresenter;
import com.uib.web.peptideshaker.presenter.InteractivePSPRojectResultsPresenter;
import com.uib.web.peptideshaker.presenter.ToolPresenter;
import com.uib.web.peptideshaker.presenter.WelcomePagePresenter;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Link;
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
    private final InteractiveGalaxyLayer Interactive_Galaxy_Layer;

    /**
     * Container to view selected PeptideShaker projects.
     */
    private InteractivePSPRojectResultsPresenter interactivePSPRojectResultsPresenter;

    /**
     * The tools view component (frame to start analysis).
     */
    private final ToolPresenter toolsView;
    /**
     * Container to view the main available datasets and files on galaxy server
     * or in other databases.
     */
    private final FileSystemPresenter fileSystemView;

    

    /**
     * Constructor to initialise the application.
     */
    public WebPeptideShakerApp() {
        WebPeptideShakerApp.this.setSizeFull();
        WebPeptideShakerApp.this.setMargin(new MarginInfo(true, true, true, true));
        WebPeptideShakerApp.this.addStyleName("frame");

        presentationManager = new PresenterManager();
        WebPeptideShakerApp.this.addComponent(presentationManager);

        Interactive_Galaxy_Layer = new InteractiveGalaxyLayer() {
            @Override
            public void systemConnected() {
                presentationManager.setSideButtonsVisible(true);
                connectGalaxyServer();
            }

            @Override
            public void systemDisconnected() {
                presentationManager.setSideButtonsVisible(false);
            }

            @Override
            public void jobsInProgress(boolean inprogress, Map<String, SystemDataSet> historyFilesMap) {
                fileSystemView.setBusy(inprogress, historyFilesMap);
                presentationManager.viewLayout(fileSystemView.getViewId());
                toolsView.updatePeptideShakerToolInputForm(Interactive_Galaxy_Layer.getSearchSettingsFilesMap(), Interactive_Galaxy_Layer.getFastaFilesMap(), Interactive_Galaxy_Layer.getMgfFilesMap());

            }
        };
        
         
        
        /**
         * landing page initialisation.
        *
         */
        WelcomePagePresenter welcomePage = new WelcomePagePresenter(Interactive_Galaxy_Layer.getGalaxyConnectionPanel());
        presentationManager.registerView(welcomePage);
        presentationManager.viewLayout(welcomePage.getViewId());

        

        interactivePSPRojectResultsPresenter = new InteractivePSPRojectResultsPresenter();

    

        toolsView = new ToolPresenter() {
            @Override
            public void executeWorkFlow(String projectName, String fastaFileId, Set<String> mgfIdsList, Set<String> searchEnginesList, SearchParameters searchParameters, Map<String, Boolean> otherSearchParameters) {
                Interactive_Galaxy_Layer.executeWorkFlow(projectName, fastaFileId, mgfIdsList, searchEnginesList, searchParameters, otherSearchParameters);
            }

            @Override
            public Map<String, GalaxyFile> saveSearchGUIParameters(SearchParameters searchParameters, boolean editMode) {
                return Interactive_Galaxy_Layer.saveSearchGUIParameters(searchParameters, editMode);
            }

        };
        presentationManager.registerView(toolsView);
        fileSystemView = new FileSystemPresenter() {
            @Override
            public void deleteDataset(SystemDataSet ds) {
                Interactive_Galaxy_Layer.deleteDataset(ds);
            }

            @Override
            public void viewDataset(PeptideShakerVisualizationDataset ds) {
                interactivePSPRojectResultsPresenter = new InteractivePSPRojectResultsPresenter();
                presentationManager.registerView(interactivePSPRojectResultsPresenter);
                interactivePSPRojectResultsPresenter.setSelectedDataset(ds);
                presentationManager.viewLayout(interactivePSPRojectResultsPresenter.getViewId());
            }

            @Override
            public boolean sendToNeLS(SystemDataSet ds) {
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
                    check = Interactive_Galaxy_Layer.sendDataToNels(vDs.getHistoryId(), vDs.getSearchGUIFile().getGalaxyId());
                    if (!check) {
                        return check;
                    }
                    check = Interactive_Galaxy_Layer.sendDataToNels(vDs.getHistoryId(), vDs.getZipFileId());
                    return check;

                }

                return Interactive_Galaxy_Layer.sendDataToNels(ds.getHistoryId(), ds.getGalaxyId());
            }

            @Override
            public boolean getFromNels(SystemDataSet ds) {
                return Interactive_Galaxy_Layer.getFromNels(ds.getHistoryId(), ds.getGalaxyId());
            }

            @Override
            public boolean uploadToGalaxy(PluploadFile[] toUploadFiles) {
                return Interactive_Galaxy_Layer.uploadToGalaxy(toUploadFiles);
            }

        };

        presentationManager.registerView(fileSystemView);
        presentationManager.registerView(interactivePSPRojectResultsPresenter);
    }

    private void connectGalaxyServer() {
        if (Interactive_Galaxy_Layer.checkToolsAvailable()) {
            toolsView.getRightView().setEnabled(true);
            toolsView.getTopView().setEnabled(true);
            toolsView.getRightView().setDescription("Click to view the tools layout");
            toolsView.getTopView().setDescription("Click to view the tools layout");
            toolsView.updatePeptideShakerToolInputForm(Interactive_Galaxy_Layer.getSearchSettingsFilesMap(), Interactive_Galaxy_Layer.getFastaFilesMap(), Interactive_Galaxy_Layer.getMgfFilesMap());

        } else {
            toolsView.getRightView().setDescription("Tools are not available");
            toolsView.getTopView().setDescription("Tools are not available");
            toolsView.getRightView().setEnabled(false);
            toolsView.getTopView().setEnabled(false);
        }
        if ((boolean) VaadinSession.getCurrent().getAttribute("nelsgalaxy")) {
//            nelsGalaxyConnectionBtn.addStyleName("disconnect");
        } else {
//            nelsGalaxyConnectionBtn.removeStyleName("disconnect");
        }
    }

    public void reConnectToGalaxyServer(String APIKEy, String galaxyUrl) {
        Interactive_Galaxy_Layer.reConnectToGalaxy(APIKEy, galaxyUrl);
        if ((boolean) VaadinSession.getCurrent().getAttribute("nelsgalaxy")) {
//            nelsGalaxyConnectionBtn.addStyleName("disconnect");
        } else {
//            nelsGalaxyConnectionBtn.removeStyleName("disconnect");

        }

    }

}
