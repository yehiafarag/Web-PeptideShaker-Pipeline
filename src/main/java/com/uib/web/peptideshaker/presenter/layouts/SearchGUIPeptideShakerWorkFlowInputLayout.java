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
 * This class represents SearchGUI-Peptide-Shaker work-flow which include input
 * form
 *
 * @author Yehia Farag
 */
public abstract class SearchGUIPeptideShakerWorkFlowInputLayout extends Panel {

    /**
     * Search settings .par file drop-down list .
     */
    private final DropDownList searchSettingsFileList;
    /**
     * MGF file list available for user to select from.
     */
    private final MultiSelectOptionGroup mgfFileList;
    /**
     * layout contain project name field and execute button.
     */
    private final TextField projectNameField;
    /**
     * Pop-up layout content that has search input available options.
     */
    private final SearchSettingsLayout searchSettingsLayout;
    /**
     * Pop-up layout container for edit user search input.
     */
    private final PopupWindow editSearchOption;
    /**
     * Available pre-saved search parameters files .par from previous searching.
     */
    private Map<String, GalaxyTransferableFile> searchSettingsMap;
    /**
     * selected search parameters to perform the search at galaxy server.
     */
    private SearchParameters searchParameters;

    /**
     * Constructor to initialise the main attributes.
     */
    @SuppressWarnings("Convert2Lambda")
    public SearchGUIPeptideShakerWorkFlowInputLayout() {
        
        SearchGUIPeptideShakerWorkFlowInputLayout.this.setWidth(100, Unit.PERCENTAGE);
        SearchGUIPeptideShakerWorkFlowInputLayout.this.setHeight(100, Unit.PERCENTAGE);
        
        VerticalLayout content = new VerticalLayout();
        content.setHeightUndefined();
        content.setWidth(100, Unit.PERCENTAGE);
        SearchGUIPeptideShakerWorkFlowInputLayout.this.setContent(content);
        SearchGUIPeptideShakerWorkFlowInputLayout.this.setStyleName("subframe");
        SearchGUIPeptideShakerWorkFlowInputLayout.this.addStyleName("floatrightinyscreen");
        content.setSpacing(true);
        
        Label titleLabel = new Label("SearchGUI-PeptideShaker");
        titleLabel.setStyleName("frametitle");
        content.addComponent(titleLabel);
        
        projectNameField = new TextField("<b>Project Name</b>");
        projectNameField.setInputPrompt("New Project Name");
        projectNameField.setCaptionAsHtml(true);
        projectNameField.setWidth(100, Unit.PERCENTAGE);
        projectNameField.setRequired(false);
        projectNameField.addStyleName("psprojectname");
        content.addComponent(projectNameField);
        
        searchSettingsFileList = new DropDownList("Search Settings");
        searchSettingsFileList.setWidth(450, Unit.PIXELS);
        searchSettingsFileList.addStyleName("nomargintop");
        searchSettingsFileList.setReadOnly(true);
        content.addComponent(searchSettingsFileList);
        searchSettingsFileList.setFocous();
        
        searchSettingsLayout = new SearchSettingsLayout(false) {
            @Override
            public void saveSearchingFile(SearchParameters searchParameters, boolean isNew) {
                checkAndSaveSearchSettingsFile(searchParameters, isNew);
                editSearchOption.setPopupVisible(false);
            }
            
            @Override
            public void cancel() {
                editSearchOption.setPopupVisible(false);
                searchSettingsFileList.defultSelect();
            }
            
        };
        editSearchOption = new PopupWindow("Edit") {
            @Override
            public void onClosePopup() {
            }
            
        };
        editSearchOption.setContent(searchSettingsLayout);
        editSearchOption.setSizeFull();
        editSearchOption.addStyleName("centerwindow");
        
        Label searchSettingInfo = new Label();
        searchSettingInfo.setWidth(450, Unit.PIXELS);
        searchSettingInfo.setHeight(90, Unit.PIXELS);
        searchSettingInfo.setContentMode(ContentMode.HTML);
        searchSettingInfo.setStyleName("subpanelframe");
        searchSettingInfo.addStyleName("bottomformlayout");
        searchSettingInfo.addStyleName("smallfontlongtext");
        content.addComponent(searchSettingInfo);
        
        mgfFileList = new MultiSelectOptionGroup("Spectrum File(s)", false);
        mgfFileList.setWidth(450, Unit.PIXELS);
        content.addComponent(mgfFileList);
        mgfFileList.setRequired(false, "Select at least 1 MGF file");
        mgfFileList.setViewList(true);
        mgfFileList.addStyleName("smallscreenfloatright");
        mgfFileList.addStyleName("top220");
        
        MultiSelectOptionGroup searchEngines = new MultiSelectOptionGroup("Search Engines", false);
        searchEngines.addStyleName("smallscreenfloatright");
        content.addComponent(searchEngines);
        searchEngines.setWidth(450, Unit.PIXELS);
        searchEngines.setRequired(false, "Select at least 1 search engine");
        searchEngines.setViewList(true);
        
        Map<String, String> searchEngienList = new LinkedHashMap<>();
        searchEngienList.put("X!Tandem", "X! Tandem");
        searchEngienList.put("MS-GF+", "MS-GF+");
        searchEngienList.put("OMSSA", "OMSSA");
        searchEngienList.put("Comet", "Comet");
        searchEngienList.put("Tide", "Tide");
        searchEngienList.put("MyriMatch", "MyriMatch");
        searchEngienList.put("MS_Amanda", "MS Amanda");
        searchEngienList.put("DirecTag", "DirecTag");
        searchEngienList.put("Novor (Select for non-commercial use only)", "Novor");
        searchEngines.updateList(searchEngienList);
        searchEngines.setSelectedValue("X!Tandem");
        searchEngines.setSelectedValue("MS-GF+");
        searchEngines.setSelectedValue("OMSSA");
        HorizontalLayout bottomLayout = new HorizontalLayout();
        bottomLayout.setStyleName("bottomformlayout");
        bottomLayout.setWidth(450, Unit.PIXELS);
        bottomLayout.setSpacing(true);
        content.addComponent(bottomLayout);
        
        Button executeWorkFlowBtn = new Button("Execute");
        executeWorkFlowBtn.setStyleName(ValoTheme.BUTTON_SMALL);
        executeWorkFlowBtn.addStyleName(ValoTheme.BUTTON_TINY);
        bottomLayout.addComponent(executeWorkFlowBtn);
        bottomLayout.setComponentAlignment(executeWorkFlowBtn, Alignment.TOP_CENTER);
        
        executeWorkFlowBtn.addClickListener((Button.ClickEvent event) -> {
            projectNameField.setRequired(true);
            mgfFileList.setRequired(true, "Select at least 1 MGF file");
            searchEngines.setRequired(true, "Select at least 1 search engine");
            
            String fastFileId = searchSettingsLayout.getFastaFileId();
            Set<String> spectrumIds = mgfFileList.getSelectedValue();
            Set<String> searchEnginesIds = searchEngines.getSelectedValue();
            String projectName = projectNameField.getValue().replace(" ", "_").replace("-", "_") + "___" + (new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Timestamp(System.currentTimeMillis())));
            
            if (!projectNameField.isValid()) {
                mgfFileList.setRequired(false, "Select at least 1 MGF file");
                searchEngines.setRequired(false, "Select at least 1 search engine");
                return;
            }
            if (spectrumIds == null) {                
                projectNameField.setRequired(false);
                searchEngines.setRequired(false, "Select at least 1 search engine");
                return;
            }
            if (searchEnginesIds == null) {
                
                projectNameField.setRequired(false);
                mgfFileList.setRequired(false, "Select at least 1 MGF file");
                return;
            }
            if (fastFileId == null) {
                Notification.show("FASTA file not available", Notification.Type.ERROR_MESSAGE);
                return;
            }
            projectNameField.setRequired(false);
            mgfFileList.setRequired(false, "Select at least 1 MGF file");
            searchEngines.setRequired(false, "Select at least 1 search engine");
            Map<String, Boolean> selectedSearchEngines = new HashMap<>();
            searchEngienList.keySet().forEach((paramId) -> {
                selectedSearchEngines.put(paramId, searchEngines.getSelectedValue().contains(paramId));
            });;
            executeWorkFlow(projectName, fastFileId, spectrumIds, searchEnginesIds, searchSettingsLayout.getSearchParameters());
            projectNameField.clear();
            
        }
        );
        mgfFileList.setEnabled(
                false);
        searchEngines.setEnabled(
                false);
        projectNameField.setEnabled(
                false);

//        editSearchSettings.setEnabled(false);
        executeWorkFlowBtn.setEnabled(
                false);
        searchSettingsFileList.addValueChangeListener(
                (Property.ValueChangeEvent event) -> {
                    if (searchSettingsFileList.getSelectedValue() != null) {
                        if (searchSettingsFileList.getSelectedValue().equalsIgnoreCase("Add new")) {
                            String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
                            File file = new File(basepath + "/VAADIN/default_searching.par");
                            SearchParameters tSearchParameters;
                            try {
                                tSearchParameters = SearchParameters.getIdentificationParameters(file);
                                tSearchParameters.setFastaFile(null);
                            } catch (IOException | ClassNotFoundException ex) {
                                
                                ex.printStackTrace();
                                return;
                            }
                            tSearchParameters.setDefaultAdvancedSettings();
                            searchSettingsLayout.updateForms(tSearchParameters, null);
                            editSearchOption.setPopupVisible(true);
                            searchSettingInfo.setValue("Add new searching parameters");
                            
                            return;
                        }
                        
                        mgfFileList.setEnabled(true);
                        searchEngines.setEnabled(true);
                        projectNameField.setEnabled(true);
//                editSearchSettings.setEnabled(true);
                        executeWorkFlowBtn.setEnabled(true);
                        try {
                            File file = searchSettingsMap.get(searchSettingsFileList.getSelectedValue()).getFile();
                            searchParameters = SearchParameters.getIdentificationParameters(file);
                            String descrip = searchParameters.getShortDescription();
                            for (String mod : searchSettingsLayout.getUpdatedModiList().keySet()) {
                                if (descrip.contains(mod)) {
                                    descrip = descrip.replace(mod, searchSettingsLayout.getUpdatedModiList().get(mod));
                                }
                                
                            }
                            
                            descrip = descrip.replace(searchParameters.getFastaFile().getName(), searchSettingsLayout.getFastaFileName(searchParameters.getFastaFile().getName().split("__")[0]));
                            searchSettingInfo.setValue(descrip);
                        } catch (IOException | ClassNotFoundException ex) {
                            ex.printStackTrace();
                            return;
                        }
                        searchSettingsLayout.updateForms(searchParameters, searchSettingsFileList.getSelectedValue());
                        
                    }
                }
        );
    }

    /**
     * Update the tools input forms
     *
     * @param searchSettingsMap search settings .par files map
     * @param fastaFilesMap FASTA files map
     * @param mgfFilesMap MGF file map
     */
    public void updateForm(Map<String, GalaxyTransferableFile> searchSettingsMap, Map<String, GalaxyFileObject> fastaFilesMap, Map<String, GalaxyFileObject> mgfFilesMap) {
        
        this.searchSettingsMap = searchSettingsMap;
        searchSettingsLayout.updateFastaFileList(fastaFilesMap);
        Map<String, String> searchSettingsFileIdToNameMap = new LinkedHashMap<>();
        Object selectedId = "";
        for (String id : searchSettingsMap.keySet()) {
            searchSettingsFileIdToNameMap.put(id, searchSettingsMap.get(id).getGalaxyFileObject().getName().replace(".par", ""));
            selectedId = id;
        }
        searchSettingsFileIdToNameMap.put("Add new", "Add new");
        
        searchSettingsFileList.updateList(searchSettingsFileIdToNameMap);
        searchSettingsFileList.setSelected(selectedId);
        searchSettingsFileList.setItemIcon("Add new", VaadinIcons.FILE_ADD);
        Map<String, String> mgfFileIdToNameMap = new LinkedHashMap<>();
        mgfFilesMap.keySet().forEach((id) -> {
            mgfFileIdToNameMap.put(id, mgfFilesMap.get(id).getName());
        });
        
        mgfFileList.updateList(mgfFileIdToNameMap);
        if (mgfFileIdToNameMap.size() == 1) {
            mgfFileList.setSelectedValue(mgfFileIdToNameMap.keySet());
        }
    }

    /**
     * Run Online Peptide-Shaker work-flow
     *
     * @param projectName name of the project to store
     * @param fastaFileId FASTA file dataset id
     * @param mgfIdsList list of MGF file dataset ids
     * @param searchEnginesList List of selected search engine names
     * @param searchParameters searching parameters // * @param searchEngines
     * search engines
     */
    public abstract void executeWorkFlow(String projectName, String fastaFileId, Set<String> mgfIdsList, Set<String> searchEnginesList, SearchParameters searchParameters);

    /**
     * Validate and save search setting .par file on galaxy server for future
     * use.
     *
     * @param searchParameters selected search parameters
     * @param isNew create new file or edit exist file
     */
    private void checkAndSaveSearchSettingsFile(SearchParameters searchParameters, boolean isNew) {
        this.searchParameters = searchParameters;
        searchSettingsMap = saveSearchGUIParameters(searchParameters, isNew);
        Map<String, String> searchSettingsFileIdToNameMap = new LinkedHashMap<>();
        String objectId = "";
        for (String id : searchSettingsMap.keySet()) {
            searchSettingsFileIdToNameMap.put(id, searchSettingsMap.get(id).getGalaxyFileObject().getName().replace(".par", ""));
            objectId = id;
        }
        searchSettingsFileIdToNameMap.put("Add new", "Add new");
        searchSettingsFileList.setItemIcon("Add new", VaadinIcons.FILE_ADD);
        searchSettingsFileList.updateList(searchSettingsFileIdToNameMap);
        searchSettingsFileList.setSelected(objectId);
        
    }

    /**
     * Save search settings file into galaxy
     *
     *
     * @param searchParameters searchParameters .par file
     * @param isNew create new file or edit exist file
     * @return updated search parameters files map
     */
    public abstract Map<String, GalaxyTransferableFile> saveSearchGUIParameters(SearchParameters searchParameters, boolean isNew);
}
