package com.uib.web.peptideshaker.presenter.layouts;

import com.compomics.util.experiment.identification.identification_parameters.SearchParameters;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyTransferableFile;
import com.uib.web.peptideshaker.presenter.core.DropDownList;
import com.uib.web.peptideshaker.presenter.core.MultiSelectOptionGroup;
import com.uib.web.peptideshaker.presenter.core.PopupWindow;
import com.vaadin.data.Property;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class represents SearchGUI-Peptide-Shaker-PathwayMatcher work-flow which include input
 * form
 *
 * @author Yehia Farag
 */
public abstract class SearchGUIPeptideShakerPathwayMatcherWorkFlowInputLayout extends SearchGUIPeptideShakerWorkFlowInputLayout {

    
    /**
     * Constructor to initialise the main attributes.
     */
    @SuppressWarnings("Convert2Lambda")
    public SearchGUIPeptideShakerPathwayMatcherWorkFlowInputLayout() {

    }

    
    protected Button setAndGetLayoutExecuteWorkFlowBtn(AbstractOrderedLayout layout, MultiSelectOptionGroup searchEngines, Map<String, String> searchEnginesList){

        Button executeWorkFlowBtn = new Button("Execute");
        executeWorkFlowBtn.setStyleName(ValoTheme.BUTTON_SMALL);
        executeWorkFlowBtn.addStyleName(ValoTheme.BUTTON_TINY);
        layout.addComponent(executeWorkFlowBtn);
        layout.setComponentAlignment(executeWorkFlowBtn, Alignment.TOP_CENTER);

        // TEMPORARY, FIXED VALUE
        String proteomFileId = "";
        executeWorkFlowBtn.addClickListener((Button.ClickEvent event) -> {
            _projectNameField.setRequired(true);
            _mgfFileList.setRequired(true, "Select at least 1 MGF file");
            searchEngines.setRequired(true, "Select at least 1 search engine");

            String fastFileId = _searchSettingsLayout.getFastaFileId();
            Set<String> spectrumIds = _mgfFileList.getSelectedValue();
            Set<String> searchEnginesIds = searchEngines.getSelectedValue();
            String projectName = _projectNameField.getValue().replace(" ", "_").replace("-", "_") + "___" + (new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Timestamp(System.currentTimeMillis())));
            
            if (!_projectNameField.isValid()) {
                _mgfFileList.setRequired(false, "Select at least 1 MGF file");
                searchEngines.setRequired(false, "Select at least 1 search engine");
                return;
            }
            if (spectrumIds == null) {                
                _projectNameField.setRequired(false);
                searchEngines.setRequired(false, "Select at least 1 search engine");
                return;
            }
            if (searchEnginesIds == null) {

                _projectNameField.setRequired(false);
                _mgfFileList.setRequired(false, "Select at least 1 MGF file");
                return;
            }
            if (fastFileId == null) {
                Notification.show("FASTA file not available", Notification.Type.ERROR_MESSAGE);
                return;
            }
            
            _projectNameField.setRequired(false);
            _mgfFileList.setRequired(false, "Select at least 1 MGF file");
            searchEngines.setRequired(false, "Select at least 1 search engine");
            Map<String, Boolean> selectedSearchEngines = new HashMap<>();
            searchEnginesList.keySet().forEach((paramId) -> {
                selectedSearchEngines.put(paramId, searchEngines.getSelectedValue().contains(paramId));
            });
            executeWorkFlow(projectName, fastFileId, spectrumIds, searchEnginesIds, _searchSettingsLayout.getSearchParameters());
        });
        return executeWorkFlowBtn;
    }
    

    /**
     * Run Online Peptide-Shaker-PathwayMatcher work-flow
     *
     * @param projectName name of the project to store
     * @param fastaFileId FASTA file dataset id
     * @param mgfIdsList list of MGF file dataset ids
     * @param searchEnginesList List of selected search engine names
     * @param searchParameters searching parameters // * @param searchEngines
     * search engines
     */
    public abstract void executeWorkFlow(String projectName,  String fastaFileId, Set<String> mgfIdsList, Set<String> searchEnginesList, SearchParameters searchParameters);

  
}
