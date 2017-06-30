package com.uib.web.peptideshaker;

import com.compomics.util.experiment.identification.identification_parameters.SearchParameters;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.uib.web.peptideshaker.galaxy.dataobjects.SystemDataSet;
import com.uib.web.peptideshaker.galaxy.dataobjects.GalaxyFile;
import com.uib.web.peptideshaker.galaxy.GalaxyLayer;
import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.presenter.GalaxyFileSystemPresenter;
import com.uib.web.peptideshaker.presenter.PeptideShakerViewPresenter;
import com.uib.web.peptideshaker.presenter.ToolPresenter;
import com.uib.web.peptideshaker.presenter.WelcomePage;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.VerticalLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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
     * The tools view component.
     */
    private final ToolPresenter toolsView;

    private final GalaxyFileSystemPresenter fileSystemView;

    private final PeptideShakerViewPresenter peptideShakerView;

    private final PresenterManager presentationManager;

    /**
     * Constructor to initialize the application.
     */
    public WebPeptideShakerApp() {
        WebPeptideShakerApp.this.setSizeFull();
        WebPeptideShakerApp.this.setMargin(new MarginInfo(true, true, true, true));
//        WebPeptideShakerApp.this.addStyleName("autooverflow");
        WebPeptideShakerApp.this.addStyleName("frame");
        presentationManager = new PresenterManager();
        WebPeptideShakerApp.this.addComponent(presentationManager);

        peptideShakerView = new PeptideShakerViewPresenter();
        Galaxy_Layer = new GalaxyLayer() {
            @Override
            public void systemConnected() {
                presentationManager.setSideButtonsVisible(true);
                connectGalaxy();
            }

            @Override
            public void systemDisconnected() {
                presentationManager.setSideButtonsVisible(false);
            }

            @Override
            public void jobsInProgress(boolean inprogress, Map<String, SystemDataSet> historyFilesMap) {
                fileSystemView.setBusy(inprogress, historyFilesMap);
                presentationManager.viewLayout(fileSystemView.getViewId());
//                if (peptideShakerView != null) {
//                    Map<String, PeptideShakerVisualizationDataset> peptideShakerVisualizationMap = new LinkedHashMap<>();
//                    for (String key : historyFilesMap.keySet()) {
//                        SystemDataSet ds = historyFilesMap.get(key);
//                        System.out.println("at key "+key+"  "+ds.getName()+"   "+ds.getType() );
//                        if (ds.getType().equalsIgnoreCase("Web Peptide Shaker Dataset")) {
//                            peptideShakerVisualizationMap.put(key, (PeptideShakerVisualizationDataset) ds);
//                        }
//
//                    }
//                    peptideShakerView.updateData(peptideShakerVisualizationMap);
//                }

            }

        };

        WelcomePage welcomePage = new WelcomePage(Galaxy_Layer.getGalaxyConnectionPanel());
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
//         
        fileSystemView = new GalaxyFileSystemPresenter() {
            @Override
            public void deleteDataset(SystemDataSet ds) {
                Galaxy_Layer.deleteDataset(ds);
            }

            @Override
            public void viewDataset(PeptideShakerVisualizationDataset ds) {
                peptideShakerView.setSelectedDataset(ds);
                presentationManager.viewLayout(peptideShakerView.getViewId());
            }

        };
        presentationManager.registerView(fileSystemView);

        presentationManager.registerView(peptideShakerView);
    }

    private void connectGalaxy() {
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
//        fileSystemView.updatePresenter(Galaxy_Layer.getHistoryFilesMap());
//        peptideShakerView.updateData(Galaxy_Layer.getPeptideShakerVisualizationMap());

    }
    public void reConnectToGalaxy(String APIKEy, String galaxyUrl ){
        Galaxy_Layer.reConnectToGalaxy(APIKEy, galaxyUrl );
    
    }

}
