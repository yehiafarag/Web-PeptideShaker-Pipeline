package com.uib.web.peptideshaker;

import com.compomics.util.experiment.identification.identification_parameters.SearchParameters;
import com.uib.web.peptideshaker.galaxy.dataobjects.SystemDataSet;
import com.uib.web.peptideshaker.galaxy.dataobjects.GalaxyFile;
import com.uib.web.peptideshaker.galaxy.GalaxyLayer;
import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.presenter.GalaxyFileSystemPresenter;
import com.uib.web.peptideshaker.presenter.PeptideShakerViewPresenter;
import com.uib.web.peptideshaker.presenter.ToolPresenter;
import com.uib.web.peptideshaker.presenter.WelcomePage;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents the main Web PeptideShaker application
 *
 * @author Yehia Farag
 */
public class WebPeptideShakerApp extends VerticalLayout {

    /**
     * The Galaxy server layer.
     */
    private final GalaxyLayer Galaxy_Layer;
    /**
     * The tools view component (frame to start analysis).
     */
    private final ToolPresenter toolsView;
    /**
     * Container to view the main available datasets and files on galaxy server
     * or in other databases.
     */
    private final GalaxyFileSystemPresenter fileSystemView;
    /**
     * Container to view selected PeptideShaker projects.
     */
    private PeptideShakerViewPresenter peptideShakerView;
    /**
     * Coordinator to organize the different views (home, analysis, database, or
     * PeptideShaker visualization layer).
     */
    private final PresenterManager presentationManager;

    private final Link nelsGalaxyConnectionBtn;

    /**
     * Constructor to initialize the application.
     */
    public WebPeptideShakerApp() {
        WebPeptideShakerApp.this.setSizeFull();
        WebPeptideShakerApp.this.setMargin(new MarginInfo(true, true, true, true));
        WebPeptideShakerApp.this.addStyleName("frame");
        presentationManager = new PresenterManager();
        WebPeptideShakerApp.this.addComponent(presentationManager);
        peptideShakerView = new PeptideShakerViewPresenter();
        Galaxy_Layer = new GalaxyLayer() {
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
                toolsView.updateHistoryHandler(Galaxy_Layer.getSearchSettingsFilesMap(), Galaxy_Layer.getFastaFilesMap(), Galaxy_Layer.getMgfFilesMap());

            }
        };

        nelsGalaxyConnectionBtn = new Link("NeLS-Galaxy", new ExternalResource("http://localhost:8084/NelsGalaxyRedirectForm/"));
        nelsGalaxyConnectionBtn.setStyleName("nelslogo");
        WelcomePage welcomePage = new WelcomePage(Galaxy_Layer.getGalaxyConnectionPanel(), nelsGalaxyConnectionBtn);
        presentationManager.registerView(welcomePage);
        presentationManager.viewLayout(welcomePage.getViewId());
        toolsView = new ToolPresenter() {
            @Override
            public void executeWorkFlow(String projectName, String fastaFileId, Set<String> mgfIdsList, Set<String> searchEnginesList, SearchParameters searchParameters, Map<String, Boolean> otherSearchParameters) {
                Galaxy_Layer.executeWorkFlow(projectName, fastaFileId, mgfIdsList, searchEnginesList, searchParameters, otherSearchParameters);
            }

            @Override
            public Map<String, GalaxyFile> saveSearchGUIParameters(SearchParameters searchParameters, boolean editMode) {
                return Galaxy_Layer.saveSearchGUIParameters(searchParameters, editMode);
            }

        };
        presentationManager.registerView(toolsView);
        fileSystemView = new GalaxyFileSystemPresenter() {
            @Override
            public void deleteDataset(SystemDataSet ds) {
                Galaxy_Layer.deleteDataset(ds);
            }

            @Override
            public void viewDataset(PeptideShakerVisualizationDataset ds) {
                peptideShakerView = new PeptideShakerViewPresenter();
                presentationManager.registerView(peptideShakerView);
                peptideShakerView.setSelectedDataset(ds);
                presentationManager.viewLayout(peptideShakerView.getViewId());
            }

            @Override
            public boolean sendToNeLS(SystemDataSet ds) {
                if (ds.getType().equalsIgnoreCase("Web Peptide Shaker Dataset")) {

                    PeptideShakerVisualizationDataset vDs = (PeptideShakerVisualizationDataset) ds;

                    boolean check = Galaxy_Layer.sendDataToNels(vDs.getHistoryId(), vDs.getProteinFileId());
                    if (!check) {
                        return check;
                    }
                    check = Galaxy_Layer.sendDataToNels(vDs.getHistoryId(), vDs.getPeptideFileId());
                    if (!check) {
                        return check;
                    }
                    check = Galaxy_Layer.sendDataToNels(vDs.getHistoryId(), vDs.getPsmFileId());
                    if (!check) {
                        return check;
                    }
                    check = Galaxy_Layer.sendDataToNels(vDs.getHistoryId(), vDs.getSearchGUIFileId());
                    if (!check) {
                        return check;
                    }
                    check = Galaxy_Layer.sendDataToNels(vDs.getHistoryId(), vDs.getZipFileId());
                    return check;

                }

                return Galaxy_Layer.sendDataToNels(ds.getHistoryId(), ds.getGalaxyId());
            }

            @Override
            public boolean getFromNels(SystemDataSet ds) {
                return Galaxy_Layer.getFromNels(ds.getHistoryId(), ds.getGalaxyId());
            }

        };

        presentationManager.registerView(fileSystemView);
        presentationManager.registerView(peptideShakerView);
    }

    private void connectGalaxyServer() {
        if (Galaxy_Layer.checkToolsAvailable()) {
            toolsView.getRightView().setEnabled(true);
            toolsView.getTopView().setEnabled(true);
            toolsView.getRightView().setDescription("Click to view the tools layout");
            toolsView.getTopView().setDescription("Click to view the tools layout");
            toolsView.updateHistoryHandler(Galaxy_Layer.getSearchSettingsFilesMap(), Galaxy_Layer.getFastaFilesMap(), Galaxy_Layer.getMgfFilesMap());

        } else {
            toolsView.getRightView().setDescription("Tools are not available");
            toolsView.getTopView().setDescription("Tools are not available");
            toolsView.getRightView().setEnabled(false);
            toolsView.getTopView().setEnabled(false);
        }
        if ((boolean) VaadinSession.getCurrent().getAttribute("nelsgalaxy")) {
            nelsGalaxyConnectionBtn.addStyleName("disconnect");
        } else {
            nelsGalaxyConnectionBtn.removeStyleName("disconnect");
        }
    }

    public void reConnectToGalaxyServer(String APIKEy, String galaxyUrl) {
        Galaxy_Layer.reConnectToGalaxy(APIKEy, galaxyUrl);
        if ((boolean) VaadinSession.getCurrent().getAttribute("nelsgalaxy")) {
            nelsGalaxyConnectionBtn.addStyleName("disconnect");
        } else {
            nelsGalaxyConnectionBtn.removeStyleName("disconnect");

        }

    }

}
