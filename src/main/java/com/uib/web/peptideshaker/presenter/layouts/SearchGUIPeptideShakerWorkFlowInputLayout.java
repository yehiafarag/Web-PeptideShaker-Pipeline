package com.uib.web.peptideshaker.presenter.layouts;

import com.compomics.util.experiment.identification.identification_parameters.SearchParameters;
import web.com.uib.probe.searchparameterwrlight.UpdatedSearchParameterFileUtility;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyTransferableFile;
import com.uib.web.peptideshaker.presenter.core.DropDownList;
import com.uib.web.peptideshaker.presenter.core.MultiSelectOptionGroup;
import com.uib.web.peptideshaker.presenter.core.PopupWindow;
import com.vaadin.data.Property;
import com.vaadin.event.FieldEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
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
import static umontreal.iro.lecuyer.util.PrintfFormat.d;

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
     * FASTA file drop-down list .
     */
    protected DropDownList _fastaFileInputLayout;
    /**
     * MGF file list available for user to select from.
     */
    protected MultiSelectOptionGroup _mgfFileList;
    /**
     * Raw file list available for user to select from.
     */
    protected MultiSelectOptionGroup _rawFileList;
    /**
     * layout contain project name field and execute button.
     */
    protected TextField _projectNameField;
    /**
     * Pop-up layout content that has search input available options.
     */
    protected SearchSettingsLayout _searchSettingsLayout;
    /**
     * Input files type (MGF or raw files).
     */
    private OptionGroup _inputFileList;
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
     * updated search parameters to perform the search at galaxy server.
     */
//    protected SearchParameters _updatedSearchParameters;  
    /**
     * Map Galaxy if for FASTA file to FAST files name.
     */
    private Map<String, String> fastaFileIdToNameMap;
    /**
     * Search engines file list available for user to select from.
     */
    private MultiSelectOptionGroup _searchEngines;
    /**
     * Execution button for the work-flow
     */
    private Button _executeWorkFlowBtn;

    /**
     * Create decoy database file list.
     */
    /**
     * Constructor to initialise the main attributes.
     */
    @SuppressWarnings("Convert2Lambda")
    public SearchGUIPeptideShakerWorkFlowInputLayout() {
        fastaFileIdToNameMap = new LinkedHashMap<>();
//        fastaFileIdToNameMap.put(dataset.getFastaFileName(), dataset.getFastaFileName());
//        fastaFileList.updateList(fastaFileIdToNameMap);
//        fastaFileList.setSelected(dataset.getFastaFileName());
//        fastaFileList.setEnabled(false);

    }

    public void initLayout() {

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
        // Search settings layout
        _searchSettingsLayout = setAndGetLayoutSearchSettingsLayout();

        // Edit search option
        _editSearchOption = setAndGetLayoutEditSearchOption(_searchSettingsLayout);

        // Search settings info
        Label searchSettingInfo = setAndGetLayoutSearchSettingInfo();
        content.addComponent(searchSettingInfo);

        //Fasta file dropdown list (opon select popup option)
        _fastaFileInputLayout = setAndGetLayoutFastaFileList();
        content.addComponent(_fastaFileInputLayout);

        _inputFileList = new OptionGroup();
        _inputFileList.setStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        _inputFileList.addItem("rawFile");
        _inputFileList.addItem("spectrumFile");
        _inputFileList.setItemCaption("rawFile", "Raw File(s)");
        _inputFileList.setItemCaption("spectrumFile", "Spectrum File(s) - Protein Identification Only!");
        _inputFileList.addStyleName("smallcombobox");
        content.addComponent(_inputFileList);

        // Raw file list
        _rawFileList = setAndGetLayoutInputFileList("Raw File (s)");
        content.addComponent(_rawFileList);

        // MGF file list
        _mgfFileList = setAndGetLayoutInputFileList("Spectrum File (s)");
        content.addComponent(_mgfFileList);

        _inputFileList.addValueChangeListener((Property.ValueChangeEvent event) -> {
            _rawFileList.setVisible(_inputFileList.getValue().toString().equalsIgnoreCase("rawFile"));
            _mgfFileList.setVisible(!_rawFileList.isVisible());
            _rawFileList.setSelectedValue("");
            _mgfFileList.setSelectedValue("");
        });
        _rawFileList.setVisible(false);
        _mgfFileList.setVisible(false);

        // Search engines list
        Map<String, String> searchEnginesList = getSearchEnginesList();
        _searchEngines = setAndGetLayoutMultiSelectOptionGroup(searchEnginesList);
        content.addComponent(_searchEngines);
        _searchEngines.addValueChangeListener((Property.ValueChangeEvent event) -> {
            if (_searchEngines.getSelectedValue() == null) {
                _searchEngines.selectAll();
            }
        });

        // BOTTOM LAYOUT
        HorizontalLayout bottomLayout = new HorizontalLayout();
        bottomLayout.setStyleName("bottomformlayout");
        bottomLayout.setWidth(450, Unit.PIXELS);
        bottomLayout.setSpacing(true);
        content.addComponent(bottomLayout);

        _executeWorkFlowBtn = setAndGetLayoutExecuteWorkFlowBtn(bottomLayout, _searchEngines, searchEnginesList);
        _fastaFileInputLayout.setEnabled(false);
        _mgfFileList.setEnabled(false);
        _searchEngines.setEnabled(false);
        _projectNameField.setEnabled(true);

//        editSearchSettings.setEnabled(false);
        _executeWorkFlowBtn.setEnabled(false);

        setLayoutSearchSettingsFileListListeners(_searchSettingsFileList, searchSettingInfo, _searchEngines, _executeWorkFlowBtn);

        _projectNameField.addTextChangeListener((FieldEvents.TextChangeEvent event) -> {

            if (event.getText() != null && !event.getText().trim().equals("")) {
                _projectNameField.removeStyleName("focos");
                activateTilStep(1);
                _searchSettingsFileList.addStyleName("focos");
            } else {
                _projectNameField.addStyleName("focos");
                _searchSettingsFileList.removeStyleName("focos");

                activateTilStep(0);
            }
        });

        _mgfFileList.addValueChangeListener((Property.ValueChangeEvent event) -> {
            if (_mgfFileList.getSelectedValue() == null) {
                _mgfFileList.setRequired(true, "Select at least one file");
                activateTilStep(2);
            } else {
                _mgfFileList.setRequired(false, "Select at least one file");
                activateTilStep(3);

            }
            System.out.println("at value of mgf changes " + _mgfFileList.getSelectedValue());
        });
         _rawFileList.addValueChangeListener((Property.ValueChangeEvent event) -> {
            if (_rawFileList.getSelectedValue() == null) {
                _rawFileList.setRequired(true, "Select at least one file");
                activateTilStep(2);
            } else {
                _rawFileList.setRequired(false, "Select at least one file");
                activateTilStep(3);

            }
            System.out.println("at value of raw changes " + _rawFileList.getSelectedValue());
        });
        _projectNameField.setTextChangeTimeout(3000);
        _projectNameField.addStyleName("focos");
        activateTilStep(0);
    }

    protected TextField setAndGetLayoutProjectNameField() {
        TextField projectNameField = new TextField("<b>Project Name</b>");
        projectNameField.setInputPrompt("New Project Name");
        projectNameField.setCaptionAsHtml(true);
        projectNameField.setWidth(100, Unit.PERCENTAGE);
        projectNameField.setRequired(false);
        projectNameField.addStyleName("psprojectname");
        return projectNameField;
    }

    protected DropDownList setAndGetLayoutSearchSettingsFileList() {
        DropDownList searchSettingsFileList = new DropDownList("Search Settings") {
            @Override
            public void setEnabled(boolean enable) {

                if (!enable) {
                    this.removeStyleName("focos");
                }
                super.setEnabled(enable);
            }
        };
        searchSettingsFileList.setWidth(450, Unit.PIXELS);
        searchSettingsFileList.addStyleName("nomargintop");
        searchSettingsFileList.setReadOnly(true);
        return searchSettingsFileList;
    }

    protected DropDownList setAndGetLayoutFastaFileList() {
        final DropDownList fastaFileList = new DropDownList("Protein Database (FASTA)") {
            @Override
            public boolean isValid() {
                this.setRequired(true, "No FASTA FILE AVAILABLE");
                boolean check = super.isValid();
                this.setRequired(!check, "No FASTA FILE AVAILABLE");
                return check;

            }

            @Override
            public void setEnabled(boolean enable) {

                if (!enable) {
                    this.removeStyleName("focos");
                }
                super.setEnabled(enable);
            }
        };

        fastaFileList.addStyleName("v-caption-on-left");;
        fastaFileList.setWidth(450, Unit.PIXELS);
        fastaFileList.addStyleName("nomargintop");
        fastaFileList.setReadOnly(true);

        return fastaFileList;
    }

    protected SearchSettingsLayout setAndGetLayoutSearchSettingsLayout() {
        SearchSettingsLayout searchSettingsLayout = new SearchSettingsLayout(false) {
            @Override
            public void saveSearchingFile(SearchParameters searchParameters, boolean isNew) {
//                checkAndSaveSearchSettingsFile(searchParameters, isNew);
                _editSearchOption.setPopupVisible(false);
                if (!_fastaFileInputLayout.getSelectedValue().trim().equalsIgnoreCase("")) {
                    activateTilStep(2);
                    _mgfFileList.setRequired(true, "Select at least one file");
                }
                _searchSettingsFileList.removeStyleName("focos");
            }

            @Override
            public void cancel() {
                _editSearchOption.setPopupVisible(false);
                _searchSettingsFileList.defultSelect();
            }

            @Override
            public void setEnabled(boolean enable) {

                if (!enable) {
                    this.removeStyleName("focos");
                }
                super.setEnabled(enable);
            }

        };
        return searchSettingsLayout;
    }

    protected PopupWindow setAndGetLayoutEditSearchOption(VerticalLayout layoutContent) {
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

    protected Label setAndGetLayoutSearchSettingInfo() {
        Label searchSettingInfo = new Label();
        searchSettingInfo.setWidth(450, Unit.PIXELS);
        searchSettingInfo.setHeight(90, Unit.PIXELS);
        searchSettingInfo.setContentMode(ContentMode.HTML);
        searchSettingInfo.setStyleName("subpanelframe");
        searchSettingInfo.addStyleName("bottomformlayout");
        searchSettingInfo.addStyleName("smallfontlongtext");
        return searchSettingInfo;
    }

    protected MultiSelectOptionGroup setAndGetLayoutInputFileList(String title ) {
        MultiSelectOptionGroup inputFileList = new MultiSelectOptionGroup(title, false) {
            @Override
            public void setEnabled(boolean enable) {

                if (!enable) {
                    this.removeStyleName("focos");
                }
                super.setEnabled(enable);
            }
        };
        inputFileList.setWidth(450, Unit.PIXELS);
        inputFileList.setRequired(false, "Select at least 1  file");
        inputFileList.setViewList(true);
        inputFileList.addStyleName("smallscreenfloatright");
        inputFileList.addStyleName("top220");
        inputFileList.addStyleName("mgfliststyle");

        return inputFileList;
    }

    protected MultiSelectOptionGroup setAndGetLayoutMultiSelectOptionGroup(Map<String, String> searchEnginesList) {
        MultiSelectOptionGroup searchEngines = new MultiSelectOptionGroup("Search Engines", false) {
            @Override
            public void setEnabled(boolean enable) {

                if (!enable) {
                    this.removeStyleName("focos");
                }
                super.setEnabled(enable);
            }
        };
        searchEngines.addStyleName("smallscreenfloatright");
        searchEngines.setRequired(false, "Select at least 1 search engine");
        searchEngines.setViewList(true);
        searchEngines.addStyleName("searchenginesstyle");

        searchEngines.updateList(searchEnginesList);
        searchEngines.setSelectedValue("X!Tandem");
        searchEngines.setSelectedValue("MS-GF+");
        searchEngines.setSelectedValue("OMSSA");
        return searchEngines;
    }

    protected Button setAndGetLayoutExecuteWorkFlowBtn(AbstractOrderedLayout layout, MultiSelectOptionGroup searchEngines, Map<String, String> searchEnginesList) {

        Button executeWorkFlowBtn = new Button("Execute") {
            @Override
            public void setEnabled(boolean enable) {

                if (!enable) {
                    this.removeStyleName("focos");
                }
                super.setEnabled(enable);
            }
        };
        executeWorkFlowBtn.setStyleName(ValoTheme.BUTTON_SMALL);
        executeWorkFlowBtn.addStyleName(ValoTheme.BUTTON_TINY);
        layout.addComponent(executeWorkFlowBtn);
        layout.setComponentAlignment(executeWorkFlowBtn, Alignment.TOP_CENTER);

        executeWorkFlowBtn.addClickListener((Button.ClickEvent event) -> {
            _mgfFileList.setRequired(true, "Select at least 1 MGF file");
            searchEngines.setRequired(true, "Select at least 1 search engine");

            String fastFileId = this.getFastaFileId();
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
//private final    WebSearchParameters tempFileReader = new WebSearchParameters();;

    protected void setLayoutSearchSettingsFileListListeners(DropDownList searchSettingsFileList, Label searchSettingInfo, MultiSelectOptionGroup searchEngines, Button executeWorkFlowBtn) {

        _searchSettingsFileList.addValueChangeListener(
                (Property.ValueChangeEvent event) -> {
                    if (_searchSettingsFileList.getSelectedValue() != null) {
                        if (_searchSettingsFileList.getSelectedValue().equalsIgnoreCase("Add new")) {
                            String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
                            File file = new File(basepath + "/VAADIN/default_searching.par");//    /VAADIN/default_searching.par SEARCHGUI_IdentificationParameters.par
                            try {
                                UpdatedSearchParameterFileUtility searchPAramUtil = new UpdatedSearchParameterFileUtility();
                                searchPAramUtil.initIdentificationParameters(new File(basepath + "/VAADIN/SEARCHGUI_IdentificationParameters.par"));
                                System.out.println("at updated modification file is readed successfulyy  yeeeey " + searchPAramUtil.geteVariableModifications());
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            SearchParameters searchParameters;
                            try {

                                searchParameters = SearchParameters.getIdentificationParameters(file);
//                                searchParameters.setFastaFile(null);
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

                        try {
                            File file = _searchSettingsMap.get(_searchSettingsFileList.getSelectedValue()).getFile();
                            _searchParameters = SearchParameters.getIdentificationParameters(file);
                            String descrip = _searchParameters.getShortDescription();
                            for (String mod : _searchSettingsLayout.getUpdatedModiList().keySet()) {
                                if (descrip.contains(mod)) {
                                    descrip = descrip.replace(mod, _searchSettingsLayout.getUpdatedModiList().get(mod));
                                }

                            }

//                            descrip = descrip.replace(_searchParameters.getFastaFile().getName(), _searchSettingsLayout.getFastaFileName(_searchParameters.getFastaFile().getName().split("__")[0]));
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

    protected Map<String, String> getSearchEnginesList() {
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
     * @param rawFilesMap Raw file map
     */
    public void updateForm(Map<String, GalaxyTransferableFile> searchSettingsMap, Map<String, GalaxyFileObject> fastaFilesMap, Map<String, GalaxyFileObject> mgfFilesMap, Map<String, GalaxyFileObject> rawFilesMap) {

        this._searchSettingsMap = searchSettingsMap;
        this.updateFastaFileList(fastaFilesMap);
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

        Map<String, String> rawFileIdToNameMap = new LinkedHashMap<>();
        rawFilesMap.keySet().forEach((id) -> {
            rawFileIdToNameMap.put(id, rawFilesMap.get(id).getName());
        });
        _rawFileList.updateList(rawFileIdToNameMap);
        _inputFileList.setItemEnabled("spectrumFile", !mgfFileIdToNameMap.isEmpty());
        _inputFileList.setItemEnabled("rawFile", !rawFileIdToNameMap.isEmpty());

        if (!rawFileIdToNameMap.isEmpty()) {
            _inputFileList.setValue("rawFile");
        } else if (!mgfFileIdToNameMap.isEmpty()) {
            _inputFileList.setValue("spectrumFile");
        } else {
            _inputFileList.setValue(null);

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
     * Get selected FASTA file galaxy id
     *
     * @return FASTA file Galaxy id
     */
    public String getFastaFileId() {
        return this._fastaFileInputLayout.getSelectedValue();
    }

    /**
     * Update selection list for FASTA files
     *
     * @param fastaFilesMap Map of FASTA files galaxy ID and FASTA Files dataset
     */
    public void updateFastaFileList(Map<String, GalaxyFileObject> fastaFilesMap) {
        fastaFileIdToNameMap = new LinkedHashMap<>();
        String selectedId = "";
        for (String id : fastaFilesMap.keySet()) {
            fastaFileIdToNameMap.put(id, fastaFilesMap.get(id).getName());
            selectedId = id;
        }
        this._fastaFileInputLayout.updateList(fastaFileIdToNameMap);
        this._fastaFileInputLayout.setSelected(selectedId);

    }

    /**
     * Get Selected FASTA file name
     *
     * @param fastaFileID FASTA file Galaxy ID
     * @return FASTA file name
     */
    public String getFastaFileName(String fastaFileID) {
        if (fastaFileIdToNameMap != null && fastaFileIdToNameMap.containsKey(fastaFileID)) {
            return fastaFileIdToNameMap.get(fastaFileID);
        } else {
            return "";
        }
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

    private void activateTilStep(int step) {

        _searchSettingsFileList.setEnabled(step > 0);
        _fastaFileInputLayout.setEnabled(step > 1);
        _inputFileList.setEnabled(step > 1);
        _mgfFileList.setEnabled(step > 1);
         _rawFileList.setEnabled(step > 1);
        _searchEngines.setEnabled(step > 1);
        _executeWorkFlowBtn.setEnabled(step > 2);

    }
}
