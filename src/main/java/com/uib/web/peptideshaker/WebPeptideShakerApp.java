package com.uib.web.peptideshaker;

import com.compomics.util.parameters.identification.IdentificationParameters;
import com.compomics.util.parameters.identification.search.SearchParameters;
import com.uib.web.peptideshaker.presenter.PresenterManager;
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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.VerticalLayout;
import java.util.List;
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
     * The SearchGUI & PeptideShaker tools view component (frame to start
     * analysis).
     */
    private SearchGUI_PeptideShaker_Tool_Presenter SearchGUI_PeptideShaker_Tool_Presenter;

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

    /**
     * Constructor to initialise the application.
     */
    public WebPeptideShakerApp() {
        WebPeptideShakerApp.this.setSizeFull();
        WebPeptideShakerApp.this.setMargin(new MarginInfo(true, true, true, true));
        WebPeptideShakerApp.this.addStyleName("mainapplicationframe");
        WebPeptideShakerApp.this.addStyleName("frame");

        this.Galaxy_Interactive_Layer = new GalaxyInteractiveLayer() {

            @Override
            public void synchronizeDataWithGalaxyServer(Map<String, GalaxyFileObject> historyFilesMap, boolean jobsInProgress, boolean updatePresenterView) {
                if (historyFilesMap.size() == 1 && historyFilesMap.keySet().iterator().next().contains("_ExternalDS")) {
                    fileSystemPresenter.viewDataset((PeptideShakerVisualizationDataset) historyFilesMap.values().iterator().next());
                    for (String btn : presentationManager.getPresenterBtnsMap().keySet()) {
                        presentationManager.getPresenterBtnsMap().get(btn).setEnabled(false);
                    }
                } else {
                    fileSystemPresenter.updateSystemData(historyFilesMap, jobsInProgress);
                }
            }
        };
        presentationManager = new PresenterManager();
        presentationManager.addStyleName("mainapplicationframe");
        WebPeptideShakerApp.this.addComponent(presentationManager);
        WebPeptideShakerApp.this.setComponentAlignment(presentationManager, Alignment.TOP_CENTER);

        /**
         * landing page initialisation.
         *
         */
        WelcomePagePresenter welcomePage = new WelcomePagePresenter() {
            @Override
            public List<String> connectToGalaxy(String userAPI, String viewId) {
                String galaxyServerUrl = VaadinSession.getCurrent().getAttribute("galaxyServerUrl").toString();
                String userDataFolderUrl = VaadinSession.getCurrent().getAttribute("userDataFolderUrl").toString();
                if (userAPI.equalsIgnoreCase("test_User_Login")) {
                    userAPI = VaadinSession.getCurrent().getAttribute("testUserAPIKey").toString();
                }
                boolean connected = Galaxy_Interactive_Layer.connectToGalaxyServer(galaxyServerUrl, userAPI, userDataFolderUrl);
                presentationManager.setSideButtonsVisible(connected);
                return Galaxy_Interactive_Layer.getUserOverViewList();

            }

            @Override
            public void maximizeView() {
                super.updateUserOverviewPanel(Galaxy_Interactive_Layer.getUserOverViewList());
                super.maximizeView(); //To change body of generated methods, choose Tools | Templates.
            }

        };
        welcomePage.setPresenterControlButtonContainer(presentationManager.getPresenterButtonsContainerLayout());

        SearchGUI_PeptideShaker_Tool_Presenter = new SearchGUI_PeptideShaker_Tool_Presenter() {
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
                updatePeptideShakerToolInputForm(Galaxy_Interactive_Layer.getSearchSettingsFilesMap(), Galaxy_Interactive_Layer.getFastaFilesMap(), Galaxy_Interactive_Layer.getMgfFilesMap(), Galaxy_Interactive_Layer.getRawFilesMap());
                super.maximizeView(); //To change body of generated methods, choose Tools | Templates.
            }

        };
        SearchGUI_PeptideShaker_Tool_Presenter.initLayout();

        fileSystemPresenter = new FileSystemPresenter() {
            @Override
            public void deleteDataset(GalaxyFileObject ds) {
                Galaxy_Interactive_Layer.deleteDataset(ds);
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
                interactivePSPRojectResultsPresenter = new InteractivePSPRojectResultsPresenter();
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

            @Override
            public boolean uploadToGalaxy(PluploadFile[] toUploadFiles) {
                boolean check = Galaxy_Interactive_Layer.uploadToGalaxy(toUploadFiles);
                fileSystemPresenter.updateSystemData(null, check);
                return check;
            }

        };
        presentationManager.registerView(welcomePage);
        presentationManager.viewLayout(welcomePage.getViewId());
        presentationManager.registerView(fileSystemPresenter);
        presentationManager.registerView(SearchGUI_PeptideShaker_Tool_Presenter);
        interactivePSPRojectResultsPresenter = new InteractivePSPRojectResultsPresenter();
        presentationManager.registerView(interactivePSPRojectResultsPresenter);

    }

}
