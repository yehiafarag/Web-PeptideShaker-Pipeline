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
 * This class represents SearchGUI-Peptide-Shaker work-flow which include input
 * form
 *
 * @author Yehia Farag
 */
public abstract class SearchGUIPeptideShakerWorkFlowInputLayout extends Panel {

    /**
     * Search settings .par file drop-down list .
     */
    protected DropDownList _searchSettingsFileList;
    /**
     * MGF file list available for user to select from.
     */
    protected MultiSelectOptionGroup _mgfFileList;
    /**
     * layout contain project name field and execute button.
     */
    protected TextField _projectNameField;
    /**
     * Pop-up layout content that has search input available options.
     */
    protected SearchSettingsLayout _searchSettingsLayout;
    /**
     * Pop-up layout container for edit user search input.
     */
    protected PopupWindow _editSearchOption;
    /**
     * Available pre-saved search parameters files .par from previous searching.
     */
    protected Map<String, GalaxyTransferableFile> _searchSettingsMap;
    /**
     * selected search parameters to perform the search at galaxy server.
     */
    protected SearchParameters _searchParameters;

    /**
     * Constructor to initialise the main attributes.
     */
    @SuppressWarnings("Convert2Lambda")
    public SearchGUIPeptideShakerWorkFlowInputLayout() {

    }

    public void initLayout(){

        SearchGUIPeptideShakerWorkFlowInputLayout.this.setWidth(100, Unit.PERCENTAGE);
        SearchGUIPeptideShakerWorkFlowInputLayout.this.setHeight(100, Unit.PERCENTAGE);

        // MAIN LAYOUT CONFIGURATION
        VerticalLayout content = new VerticalLayout();
        content.setHeightUndefined();
        content.setWidth(100, Unit.PERCENTAGE);
        SearchGUIPeptideShakerWorkFlowInputLayout.this.setContent(content);
        SearchGUIPeptideShakerWorkFlowInputLayout.this.setStyleName("subframe");
        SearchGUIPeptideShakerWorkFlowInputLayout.this.addStyleName("floatrightinyscreen");
        content.setSpacing(true);

        // CONFIGURING GRAPHICAL COMPONENTS...

        // Title
        Label titleLabel = new Label("SearchGUI-PeptideShaker");
        titleLabel.setStyleName("frametitle");
        content.addComponent(titleLabel);

        // Project name field
        _projectNameField = setAndGetLayoutProjectNameField();
        content.addComponent(_projectNameField);

        // Search settings
        _searchSettingsFileList = setAndGetLayoutSearchSettingsFileList();
        content.addComponent(_searchSettingsFileList);
        _searchSettingsFileList.setFocous();
        // Search settings layout
        _searchSettingsLayout = setAndGetLayoutSearchSettingsLayout();

        // Edit search option
        _editSearchOption = setAndGetLayoutEditSearchOption(_searchSettingsLayout);

        // Search settings info
        Label searchSettingInfo = setAndGetLayoutSearchSettingInfo();
        content.addComponent(searchSettingInfo);

        // MGF file list
        _mgfFileList = setAndGetLayoutMgfFileList();
        content.addComponent(_mgfFileList);

        // Search engines list
        Map<String, String> searchEnginesList = getSearchEnginesList();
        MultiSelectOptionGroup searchEngines = setAndGetLayoutMultiSelectOptionGroup(searchEnginesList);
        content.addComponent(searchEngines);


        // BOTTOM LAYOUT
        HorizontalLayout bottomLayout = new HorizontalLayout();
        bottomLayout.setStyleName("bottomformlayout");
        bottomLayout.setWidth(450, Unit.PIXELS);
        bottomLayout.setSpacing(true);
        content.addComponent(bottomLayout);

        Button executeWorkFlowBtn = setAndGetLayoutExecuteWorkFlowBtn(bottomLayout, searchEngines, searchEnginesList);

        _mgfFileList.setEnabled(false);
        searchEngines.setEnabled(false);
        _projectNameField.setEnabled(false);
//        editSearchSettings.setEnabled(false);
        executeWorkFlowBtn.setEnabled(false);

        setLayoutSearchSettingsFileListListeners(_searchSettingsFileList, searchSettingInfo, searchEngines, executeWorkFlowBtn);
    }


    protected TextField setAndGetLayoutProjectNameField(){
        TextField projectNameField = new TextField("<b>Project Name</b>");
        projectNameField.setInputPrompt("New Project Name");
        projectNameField.setCaptionAsHtml(true);
        projectNameField.setWidth(100, Unit.PERCENTAGE);
        projectNameField.setRequired(false);
        projectNameField.addStyleName("psprojectname");
        return projectNameField;
    }

    protected DropDownList setAndGetLayoutSearchSettingsFileList(){
        DropDownList searchSettingsFileList = new DropDownList("Search Settings");
        searchSettingsFileList.setWidth(450, Unit.PIXELS);
        searchSettingsFileList.addStyleName("nomargintop");
        searchSettingsFileList.setReadOnly(true);
        return searchSettingsFileList;
    }


    protected SearchSettingsLayout setAndGetLayoutSearchSettingsLayout(){
        SearchSettingsLayout searchSettingsLayout = new SearchSettingsLayout(false) {
            @Override
            public void saveSearchingFile(SearchParameters searchParameters, boolean isNew) {
                checkAndSaveSearchSettingsFile(searchParameters, isNew);
                _editSearchOption.setPopupVisible(false);
            }

            @Override
            public void cancel() {
                _editSearchOption.setPopupVisible(false);
                _searchSettingsFileList.defultSelect();
            }

        };
        return searchSettingsLayout;
    }

    protected PopupWindow setAndGetLayoutEditSearchOption(VerticalLayout layoutContent){
        PopupWindow editSearchOption = new PopupWindow("Edit") {
            @Override
            public void onClosePopup() {
            }

        };
        editSearchOption.setContent(layoutContent);
        editSearchOption.setSizeFull();
        editSearchOption.addStyleName("centerwindow");
        return editSearchOption;
    }

    protected Label setAndGetLayoutSearchSettingInfo(){
        Label searchSettingInfo = new Label();
        searchSettingInfo.setWidth(450, Unit.PIXELS);
        searchSettingInfo.setHeight(90, Unit.PIXELS);
        searchSettingInfo.setContentMode(ContentMode.HTML);
        searchSettingInfo.setStyleName("subpanelframe");
        searchSettingInfo.addStyleName("bottomformlayout");
        searchSettingInfo.addStyleName("smallfontlongtext");
        return searchSettingInfo;
    }

    protected MultiSelectOptionGroup setAndGetLayoutMgfFileList(){
        MultiSelectOptionGroup mgfFileList = new MultiSelectOptionGroup("Spectrum File(s)", false);
        mgfFileList.setWidth(450, Unit.PIXELS);
        mgfFileList.setRequired(false, "Select at least 1 MGF file");
        mgfFileList.setViewList(true);
        mgfFileList.addStyleName("smallscreenfloatright");
        mgfFileList.addStyleName("top220");
        return mgfFileList;
    }

    protected MultiSelectOptionGroup setAndGetLayoutMultiSelectOptionGroup(Map<String, String> searchEnginesList){
        MultiSelectOptionGroup searchEngines = new MultiSelectOptionGroup("Search Engines", false);
        searchEngines.addStyleName("smallscreenfloatright");

        searchEngines.setRequired(false, "Select at least 1 search engine");
        searchEngines.setViewList(true);


        searchEngines.updateList(searchEnginesList);
        searchEngines.setSelectedValue("X!Tandem");
        searchEngines.setSelectedValue("MS-GF+");
        searchEngines.setSelectedValue("OMSSA");
        return searchEngines;
    }

    protected Button setAndGetLayoutExecuteWorkFlowBtn(AbstractOrderedLayout layout, MultiSelectOptionGroup searchEngines, Map<String, String> searchEnginesList){

        Button executeWorkFlowBtn = new Button("Execute");
        executeWorkFlowBtn.setStyleName(ValoTheme.BUTTON_SMALL);
        executeWorkFlowBtn.addStyleName(ValoTheme.BUTTON_TINY);
        layout.addComponent(executeWorkFlowBtn);
        layout.setComponentAlignment(executeWorkFlowBtn, Alignment.TOP_CENTER);

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


    protected void setLayoutSearchSettingsFileListListeners(DropDownList searchSettingsFileList, Label searchSettingInfo, MultiSelectOptionGroup searchEngines, Button executeWorkFlowBtn ){
        _searchSettingsFileList.addValueChangeListener(
            (Property.ValueChangeEvent event) -> {
                if (_searchSettingsFileList.getSelectedValue() != null) {
                    if (_searchSettingsFileList.getSelectedValue().equalsIgnoreCase("Add new")) {
                        String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
                        File file = new File(basepath + "/VAADIN/default_searching.par");
                        SearchParameters searchParameters;
                        try {
                            searchParameters = SearchParameters.getIdentificationParameters(file);
                            searchParameters.setFastaFile(null);
                        } catch (IOException | ClassNotFoundException ex) {

                            ex.printStackTrace();
                            return;
                        }
                        searchParameters.setDefaultAdvancedSettings();
                        _searchSettingsLayout.updateForms(searchParameters, null);
                        _editSearchOption.setPopupVisible(true);
                        searchSettingInfo.setValue("Add new searching parameters");

                        return;
                    }

                    _mgfFileList.setEnabled(true);
                    searchEngines.setEnabled(true);
                    _projectNameField.setEnabled(true);
    //                editSearchSettings.setEnabled(true);
                    executeWorkFlowBtn.setEnabled(true);
                    try {
                        File file = _searchSettingsMap.get(_searchSettingsFileList.getSelectedValue()).getFile();
                        _searchParameters = SearchParameters.getIdentificationParameters(file);
                        String descrip = _searchParameters.getShortDescription();
                        for (String mod : _searchSettingsLayout.getUpdatedModiList().keySet()) {
                            if (descrip.contains(mod)) {
                                descrip = descrip.replace(mod, _searchSettingsLayout.getUpdatedModiList().get(mod));
                            }

                        }

                        descrip = descrip.replace(_searchParameters.getFastaFile().getName(), _searchSettingsLayout.getFastaFileName(_searchParameters.getFastaFile().getName().split("__")[0]));
                        searchSettingInfo.setValue(descrip);
                    } catch (IOException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                        return;
                    }
                    _searchSettingsLayout.updateForms(_searchParameters, _searchSettingsFileList.getSelectedValue());

                }
            }
        );
    }

    protected Map<String, String> getSearchEnginesList(){
        Map<String, String> searchEnginesList = new LinkedHashMap<>();
        searchEnginesList.put("X!Tandem", "X! Tandem");
        searchEnginesList.put("MS-GF+", "MS-GF+");
        searchEnginesList.put("OMSSA", "OMSSA");
        searchEnginesList.put("Comet", "Comet");
        searchEnginesList.put("Tide", "Tide");
        searchEnginesList.put("MyriMatch", "MyriMatch");
        searchEnginesList.put("MS_Amanda", "MS Amanda");
        searchEnginesList.put("DirecTag", "DirecTag");
        searchEnginesList.put("Novor (Select for non-commercial use only)", "Novor");
        return searchEnginesList;
    }

    /**
     * Update the tools input forms
     *
     * @param searchSettingsMap search settings .par files map
     * @param fastaFilesMap FASTA files map
     * @param mgfFilesMap MGF file map
     */
    public void updateForm(Map<String, GalaxyTransferableFile> searchSettingsMap, Map<String, GalaxyFileObject> fastaFilesMap, Map<String, GalaxyFileObject> mgfFilesMap) {

        this._searchSettingsMap = searchSettingsMap;
        _searchSettingsLayout.updateFastaFileList(fastaFilesMap);
        Map<String, String> searchSettingsFileIdToNameMap = new LinkedHashMap<>();
        Object selectedId = "";
        for (String id : searchSettingsMap.keySet()) {
            searchSettingsFileIdToNameMap.put(id, searchSettingsMap.get(id).getGalaxyFileObject().getName().replace(".par", ""));
            selectedId = id;
        }
        searchSettingsFileIdToNameMap.put("Add new", "Add new");

        _searchSettingsFileList.updateList(searchSettingsFileIdToNameMap);
        _searchSettingsFileList.setSelected(selectedId);
        _searchSettingsFileList.setItemIcon("Add new", VaadinIcons.FILE_ADD);
        Map<String, String> mgfFileIdToNameMap = new LinkedHashMap<>();
        mgfFilesMap.keySet().forEach((id) -> {
            mgfFileIdToNameMap.put(id, mgfFilesMap.get(id).getName());
        });
        _mgfFileList.updateList(mgfFileIdToNameMap);
        if (mgfFileIdToNameMap.size() == 1) {
            _mgfFileList.setSelectedValue(mgfFileIdToNameMap.keySet());
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
        this._searchParameters = searchParameters;
        _searchSettingsMap = saveSearchGUIParameters(searchParameters, isNew);
        Map<String, String> searchSettingsFileIdToNameMap = new LinkedHashMap<>();
        String objectId = "";
        for (String id : _searchSettingsMap.keySet()) {
            searchSettingsFileIdToNameMap.put(id, _searchSettingsMap.get(id).getGalaxyFileObject().getName().replace(".par", ""));
            objectId = id;
        }
        searchSettingsFileIdToNameMap.put("Add new", "Add new");
        _searchSettingsFileList.setItemIcon("Add new", VaadinIcons.FILE_ADD);
        _searchSettingsFileList.updateList(searchSettingsFileIdToNameMap);
        _searchSettingsFileList.setSelected(objectId);

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
