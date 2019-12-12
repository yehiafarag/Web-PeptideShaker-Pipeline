package com.uib.web.peptideshaker.presenter.layouts;

import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyTransferableFile;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.presenter.core.DropDownList;
import com.uib.web.peptideshaker.presenter.core.MultiSelectOptionGroup;
import com.uib.web.peptideshaker.presenter.core.PopupWindow;
import com.uib.web.peptideshaker.presenter.core.RadioButton;
import com.uib.web.peptideshaker.presenter.core.StatusLabel;
import com.uib.web.peptideshaker.presenter.core.Uploader;
import com.vaadin.data.Property;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import pl.exsio.plupload.PluploadFile;

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
    private final Set<String> _mgfFileList;
    /**
     * Raw file list available for user to select from.
     */
    private final Set<String> _rawFileList;
    /**
     * layout contain project name field and execute button.
     */
    protected TextField _projectNameField;
    /**
     * Pop-up layout content that has search input available options.
     */
    protected SearchSettingsLayout _searchSettingsLayout;
    /**
     * Input files type (MGF or raw files). //
     */
//    private OptionGroup _inputFileList;
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
    protected IdentificationParameters _searchParameters;
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
    private OptionGroup rawMgfController;
    /**
     * Execution button for the work-flow
     */
    private Button _executeWorkFlowBtn;
    private AbsoluteLayout inputDataFilesContainer;
    private Uploader mgf_raw_dataUploader;
    private VerticalLayout rawDataListLayout;
    private VerticalLayout mgfDataListLayout;
    private LayoutClickListener rawClickListener;
    private LayoutClickListener mgfClickListener;
    private Uploader fastaFileUploader;
    private int indexer = 0;
    private final float[] expandingRatio = new float[]{5f, 5f, 58f, 25f, 8f};
//    private OptionGroup projectSupHeader;
//    private final ProjectOverviewLayout existedProjectLayout;

    /**
     * Create decoy database file list.
     */
    /**
     * Constructor to initialise the main attributes.
     */
    @SuppressWarnings("Convert2Lambda")
    public SearchGUIPeptideShakerWorkFlowInputLayout() {
        fastaFileIdToNameMap = new LinkedHashMap<>();
        _mgfFileList = new LinkedHashSet<>();
        _rawFileList = new LinkedHashSet<>();
//        this.existedProjectLayout = new ProjectOverviewLayout() {
//            @Override
//            public void deleteDataset(PeptideShakerVisualizationDataset ds) {
//            }
//
//            @Override
//            public boolean sendToNeLS(PeptideShakerVisualizationDataset ds) {
//                return false;
//            }
//
//            @Override
//            public boolean getFromNels(PeptideShakerVisualizationDataset ds) {
//                return false;
//            }
//
//            @Override
//            public void viewDataset(PeptideShakerVisualizationDataset ds) {
//            }
//        };

    }

    public void initLayout() {

        SearchGUIPeptideShakerWorkFlowInputLayout.this.setWidth(100, Unit.PERCENTAGE);
        SearchGUIPeptideShakerWorkFlowInputLayout.this.setHeight(100, Unit.PERCENTAGE);

        // MAIN LAYOUT CONFIGURATION
        AbsoluteLayout container = new AbsoluteLayout();
        container.setHeight(100, Unit.PERCENTAGE);
        container.setWidth(100, Unit.PERCENTAGE);
        SearchGUIPeptideShakerWorkFlowInputLayout.this.setContent(container);
        SearchGUIPeptideShakerWorkFlowInputLayout.this.setStyleName("subframe");
        SearchGUIPeptideShakerWorkFlowInputLayout.this.addStyleName("floatrightinyscreen");

        // CONFIGURING GRAPHICAL COMPONENTS...
        // Title
        Label titleLabel = new Label("Analyze Data");
        titleLabel.setStyleName("frametitle");
        titleLabel.addStyleName("maintitleheader");
        container.addComponent(titleLabel, "left:40px;top:13px");

        AbsoluteLayout mainContainerLayout = new AbsoluteLayout();
        mainContainerLayout.setSizeFull();
        mainContainerLayout.setStyleName("maincontentcontainer");
        container.addComponent(mainContainerLayout, "left:25px;top:25px;bottom:25px;right:25px");

        AbsoluteLayout newProjectContainer = new AbsoluteLayout();
        newProjectContainer.setWidth(530, Unit.PIXELS);
        newProjectContainer.setHeight(832, Unit.PIXELS);
        newProjectContainer.addStyleName("searchinputcontainer");
        newProjectContainer.addStyleName("datainputsubcontainer");
        mainContainerLayout.addComponent(newProjectContainer, "left:50%;top:50%");

        /**
         * Search settings *
         */
        AbsoluteLayout searchParameterContainer = new AbsoluteLayout();
        searchParameterContainer.setCaption("Search Settings (Search Parameter File)");
        searchParameterContainer.setWidth(480, Unit.PIXELS);
        searchParameterContainer.setHeight(152, Unit.PIXELS);
        searchParameterContainer.setStyleName("titleinborder");
        newProjectContainer.addComponent(searchParameterContainer, "left:25px;top:25px;right:25px");
        _searchSettingsFileList = setAndGetLayoutSearchSettingsFileList();
        searchParameterContainer.addComponent(_searchSettingsFileList, "left:15px;top:15px;right:15px");
        // Search settings layout
        _searchSettingsLayout = setAndGetLayoutSearchSettingsLayout();
        // Edit search option
        _editSearchOption = setAndGetLayoutEditSearchOption(_searchSettingsLayout);
        // Search settings info
        Label searchSettingInfo = setAndGetLayoutSearchSettingInfo();
        searchParameterContainer.addComponent(searchSettingInfo, "left:15px;top:47px;right:15px");

        /**
         * FASTA file drop-down list (open select pop-up option) *
         */
        AbsoluteLayout fastaFileContainer = new AbsoluteLayout();
        fastaFileContainer.setCaption("Protein Database (FASTA File)");
        fastaFileContainer.setWidth(480, Unit.PIXELS);
        fastaFileContainer.setHeight(55, Unit.PIXELS);
        fastaFileContainer.setStyleName("titleinborder");
        newProjectContainer.addComponent(fastaFileContainer, "left:25px;top:202px;right:25px");

        _fastaFileInputLayout = setAndGetLayoutFastaFileList();
        fastaFileContainer.addComponent(_fastaFileInputLayout, "left:15px;top:15px;right:15px");

        /**
         * Initialise Raw/MGF available files controller.
         */
        AbsoluteLayout spectrumFileContainer = new AbsoluteLayout();
        spectrumFileContainer.setCaption("Spectrum Files (Raw/MGF Files)");
        spectrumFileContainer.setWidth(480, Unit.PIXELS);
        spectrumFileContainer.setHeight(232, Unit.PIXELS);
        spectrumFileContainer.setStyleName("titleinborder");
        newProjectContainer.addComponent(spectrumFileContainer, "left:25px;top:282px;right:25px");

        rawMgfController = new OptionGroup();
        rawMgfController.setStyleName("controllercombobox");
        rawMgfController.addItem("rawFiles");
        rawMgfController.addItem("mgfFiles");
        rawMgfController.setHeight(60, Unit.PIXELS);
        rawMgfController.setHtmlContentAllowed(true);
        rawMgfController.setItemCaption("rawFiles", "Raw File(s)");
        rawMgfController.setItemCaption("mgfFiles", "MGF File(s)");
        spectrumFileContainer.addComponent(rawMgfController, "left:15px;top:15px;right:150px");
        rawMgfController.addValueChangeListener((Property.ValueChangeEvent event) -> {
            mgfDataListLayout.setVisible(rawMgfController.getValue().toString().equalsIgnoreCase("mgfFiles"));
            if (mgfDataListLayout.isVisible()) {
                _rawFileList.clear();
            } else {
                _mgfFileList.clear();
            }
            updateMgfFilesTable();
            updateRawFilesTable();
        });
        inputDataFilesContainer = new AbsoluteLayout();
        inputDataFilesContainer.setHeight(135, Unit.PIXELS);
        inputDataFilesContainer.setWidth(450, Unit.PIXELS);
        inputDataFilesContainer.setStyleName("inputdatafilecontainer");
        spectrumFileContainer.addComponent(inputDataFilesContainer, "left:15px;top:80px;right:15px");
        mgf_raw_dataUploader = new Uploader() {
            @Override
            public void filesUploaded(PluploadFile[] uploadedFiles) {
                mgf_raw_dataUploader.setBusy(true);
                uploadToGalaxy(uploadedFiles);
            }
        };
        mgf_raw_dataUploader.addUploaderFilter("mgf");
        mgf_raw_dataUploader.addUploaderFilter("thermo.raw");
        inputDataFilesContainer.addComponent(mgf_raw_dataUploader.getPopupUploaderUnit(), "right:19px;top:-13px");

        rawDataListLayout = new VerticalLayout();
        rawDataListLayout.setSizeFull();
        rawDataListLayout.setSpacing(true);
        rawDataListLayout.setStyleName("astablelayout");
        inputDataFilesContainer.addComponent(rawDataListLayout, "top:0px;");

        mgfDataListLayout = new VerticalLayout() {
            @Override
            public void setVisible(boolean visible) {
                rawDataListLayout.setVisible(!visible);
                super.setVisible(visible); //To change body of generated methods, choose Tools | Templates.
            }

        };
        mgfDataListLayout.setSizeFull();
        mgfDataListLayout.setStyleName("astablelayout");
        inputDataFilesContainer.addComponent(mgfDataListLayout, "top:0px;");
        rawMgfController.select("rawFiles");

        /**
         * Initialise Search Engines container.
         */
        AbsoluteLayout searchEnginesContainer = new AbsoluteLayout();
        searchEnginesContainer.setCaption("Search Engines");
        searchEnginesContainer.setWidth(480, Unit.PIXELS);
        searchEnginesContainer.setHeight(123, Unit.PIXELS);
        searchEnginesContainer.setStyleName("titleinborder");
        newProjectContainer.addComponent(searchEnginesContainer, "left:25px;top:539px;right:25px");
        Map<String, String> searchEnginesList = getSearchEnginesList();
        _searchEngines = setAndGetLayoutMultiSelectOptionGroup(searchEnginesList);
        searchEnginesContainer.addComponent(_searchEngines, "left:15px;top:15px;right:15px;");
        _searchEngines.addValueChangeListener((Property.ValueChangeEvent event) -> {
            if (_searchEngines.getSelectedValue() == null) {
                _searchEngines.selectAll();
            }
        });

        /**
         * Project name field.
         */
        AbsoluteLayout projectNameContainer = new AbsoluteLayout();
        projectNameContainer.setCaption("Project Name");
        projectNameContainer.setWidth(480, Unit.PIXELS);
        projectNameContainer.setHeight(55, Unit.PIXELS);
        projectNameContainer.setStyleName("titleinborder");
        newProjectContainer.addComponent(projectNameContainer, "left:25px;top:687px;right:25px");
        _projectNameField = setAndGetLayoutProjectNameField();
        projectNameContainer.addComponent(_projectNameField, "left:15px;top:15px;right:15px;");

        // BOTTOM LAYOUT
        AbsoluteLayout _executeWorkFlowButtonLayoutContainer = new AbsoluteLayout();
        _executeWorkFlowButtonLayoutContainer.setWidth(100, Unit.PERCENTAGE);
        _executeWorkFlowButtonLayoutContainer.setHeight(40, Unit.PIXELS);
        _executeWorkFlowButtonLayoutContainer.setStyleName("titleinborder");

        HorizontalLayout bottomLayout = new HorizontalLayout();
//        bottomLayout.setStyleName("bottomformlayout");
        bottomLayout.setSizeFull();
        bottomLayout.setSpacing(true);
        _executeWorkFlowButtonLayoutContainer.addComponent(bottomLayout, "top:7.5px;bottom:7.5px");
        newProjectContainer.addComponent(_executeWorkFlowButtonLayoutContainer, "left:25px;top:767px;right:25px");
        _executeWorkFlowBtn = setAndGetLayoutExecuteWorkFlowBtn(bottomLayout, _searchEngines, searchEnginesList);
        _executeWorkFlowBtn.setEnabled(true);

        setLayoutSearchSettingsFileListListeners(_searchSettingsFileList, searchSettingInfo, _searchEngines, _executeWorkFlowBtn);
        _projectNameField.addStyleName("focos");
        rawClickListener = (LayoutEvents.LayoutClickEvent event) -> {
            String id = event.getComponent().getId();

            if (_rawFileList.contains(id)) {
                _rawFileList.remove(id);
            } else {
                _rawFileList.add(id);
            }
            updateRawFilesTable();
        };
        mgfClickListener = (LayoutEvents.LayoutClickEvent event) -> {
            String id = event.getComponent().getId();
            if (_mgfFileList.contains(id)) {
                _mgfFileList.remove(id);
            } else {
                _mgfFileList.add(id);
            }

            updateMgfFilesTable();
        };

    }

    protected TextField setAndGetLayoutProjectNameField() {
        TextField projectNameField = new TextField();
        projectNameField.setInputPrompt("New Project Name");
        projectNameField.setCaptionAsHtml(true);
        projectNameField.setWidth(100, Unit.PERCENTAGE);
        projectNameField.setRequired(false);
        projectNameField.addStyleName("psprojectname");
        return projectNameField;
    }

    protected DropDownList setAndGetLayoutSearchSettingsFileList() {
        DropDownList searchSettingsFileList = new DropDownList(null) {
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
        fastaFileUploader = new Uploader() {
            @Override
            public void filesUploaded(PluploadFile[] uploadedFiles) {
                fastaFileUploader.setBusy(true);
                uploadToGalaxy(uploadedFiles);
                _fastaFileInputLayout.setEnabled(false);
            }

        };
        fastaFileUploader.addUploaderFilter("fasta");
        final DropDownList fastaFileList = new DropDownList(null) {
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
//        fastaFileList.addStyleName("nomargintop");
        fastaFileList.addUploadBtn(fastaFileUploader.getPopupUploaderUnit());
        fastaFileList.setReadOnly(true);

        return fastaFileList;
    }

    protected SearchSettingsLayout setAndGetLayoutSearchSettingsLayout() {
        SearchSettingsLayout searchSettingsLayout = new SearchSettingsLayout(false) {
            @Override
            public void saveSearchingFile(IdentificationParameters searchParameters, boolean isNew) {
                checkAndSaveSearchSettingsFile(searchParameters, isNew);
                _editSearchOption.setPopupVisible(false);
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

    protected MultiSelectOptionGroup setAndGetLayoutInputFileList(String title) {
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
        MultiSelectOptionGroup searchEngines = new MultiSelectOptionGroup(null, false) {
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

        Button executeWorkFlowBtn = new Button("Execute");
        executeWorkFlowBtn.setStyleName(ValoTheme.BUTTON_SMALL);
        executeWorkFlowBtn.addStyleName(ValoTheme.BUTTON_TINY);
        executeWorkFlowBtn.setWidth(100, Unit.PIXELS);
        executeWorkFlowBtn.setHeight(100, Unit.PERCENTAGE);
        layout.addComponent(executeWorkFlowBtn);
        layout.setComponentAlignment(executeWorkFlowBtn, Alignment.TOP_CENTER);

        executeWorkFlowBtn.addClickListener((Button.ClickEvent event) -> {
            if (_searchSettingsFileList.getSelectedValue() == null || _searchSettingsFileList.getSelectedValue().equalsIgnoreCase("null")) {
                _searchSettingsFileList.addStyleName("errorstyle");
                return;
            } else {
                _searchSettingsFileList.removeStyleName("errorstyle");
            }
            if (_fastaFileInputLayout.getSelectedValue() == null || _fastaFileInputLayout.getSelectedValue().equalsIgnoreCase("null")) {
                _fastaFileInputLayout.addStyleName("errorstyle");
                return;
            } else {
                _fastaFileInputLayout.removeStyleName("errorstyle");
            }
            if (_mgfFileList.isEmpty() && _rawFileList.isEmpty()) {
                inputDataFilesContainer.addStyleName("errorstyle");
                return;
            } else {
                inputDataFilesContainer.removeStyleName("errorstyle");
            }
            System.out.println("at search settings ?? " + _searchSettingsFileList.getSelectedValue());

            if (searchEngines.getSelectedValue() == null || searchEngines.getSelectedValue().isEmpty()) {
                searchEngines.setRequired(true, "Select at least 1 search engine");
                return;
            } else {
                searchEngines.setRequired(false, "");
            }
            if (_projectNameField.getValue() == null || _projectNameField.getValue().trim().equalsIgnoreCase("")) {
                _projectNameField.addStyleName("errorstyle");
                return;
            } else {
                _projectNameField.removeStyleName("errorstyle");
            }

            String fastFileId = this.getFastaFileId();
            Set<String> spectrumIds = _mgfFileList;
            Set<String> rawIds = _rawFileList;
            Set<String> searchEnginesIds = searchEngines.getSelectedValue();
            String projectName = _projectNameField.getValue().replace(" ", "_").replace("-", "_") + "___" + (new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss").format(new Timestamp(System.currentTimeMillis())));

            searchEngines.setRequired(false, "Select at least 1 search engine");
            Map<String, Boolean> selectedSearchEngines = new HashMap<>();
            searchEnginesList.keySet().forEach((paramId) -> {
                selectedSearchEngines.put(paramId, searchEngines.getSelectedValue().contains(paramId));
            });
            String searchParamerterId = _searchSettingsMap.get(_searchSettingsFileList.getSelectedValue()).getGalaxyId();
            if (_rawFileList.isEmpty()) {
                executeWorkFlow(projectName, fastFileId, searchParamerterId, spectrumIds, searchEnginesIds, _searchSettingsLayout.getSearchParameters(), false);
            } else {
                executeWorkFlow(projectName, fastFileId, searchParamerterId, rawIds, searchEnginesIds, _searchSettingsLayout.getSearchParameters(), true);
            }
//            projectSupHeader.setValue("existProject");
        });
        return executeWorkFlowBtn;
    }

    protected void setLayoutSearchSettingsFileListListeners(DropDownList searchSettingsFileList, Label searchSettingInfo, MultiSelectOptionGroup searchEngines, Button executeWorkFlowBtn) {

        _searchSettingsFileList.addValueChangeListener((Property.ValueChangeEvent event) -> {
            if (_searchSettingsFileList.getSelectedValue() != null) {
                if (_searchSettingsFileList.getSelectedValue().equalsIgnoreCase("Add new")) {
                    String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
                    try {
                        File file = new File(basepath + "/VAADIN/SEARCHGUI_IdentificationParameters.par");
                        IdentificationParameters searchParamUtil = IdentificationParameters.getIdentificationParameters(file);
                        _searchSettingsLayout.updateForms(searchParamUtil);
                        _editSearchOption.setPopupVisible(true);
                    } catch (IOException ex) {
                        System.out.println("exception is handeled ");
                        ex.printStackTrace();
                    }
                    return;
                }

                try {
                    File file = _searchSettingsMap.get(_searchSettingsFileList.getSelectedValue()).getFile();
                    _searchParameters = IdentificationParameters.getIdentificationParameters(file);
//                    _searchParameters.setGalaxyId( _searchSettingsMap.get(_searchSettingsFileList.getSelectedValue()).getGalaxyId());
                    String descrip = _searchParameters.getDescription();
                    for (String mod : _searchSettingsLayout.getUpdatedModiList().keySet()) {
                        if (descrip.contains(mod)) {
                            descrip = descrip.replace(mod, _searchSettingsLayout.getUpdatedModiList().get(mod));
                        }

                    }
                    searchSettingInfo.setValue(descrip);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return;
                }
//                _searchSettingsLayout.updateForms(_searchParameters, _searchSettingsFileList.getSelectedValue());

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
//        existedProjectLayout.updateDatasetsTable(peptideShakerVisualizationDatasetMap);
        mgf_raw_dataUploader.setBusy(false);
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
        mgfDataListLayout.removeAllComponents();
        rawDataListLayout.removeAllComponents();
        indexer = 1;
        mgfFilesMap.keySet().forEach((id) -> {
            GalaxyFileObject ds = mgfFilesMap.get(id);
            mgfFileIdToNameMap.put(id, ds.getName());
            StatusLabel statusLabel = new StatusLabel();
            statusLabel.setStatus(ds.getStatus());

            Label nameLabel = new Label(ds.getName());
            Label type = new Label();
            type.setValue("<b>" + ds.getType() + "</b>");
            type.setContentMode(ContentMode.HTML);
            type.setDescription(ds.getType());
            RadioButton selectionRadioBtn = new RadioButton(id) {
                @Override
                public void selectItem(Object itemId) {

                }
            };

            HorizontalLayout rowLayout = initializeRowData(new Component[]{new Label(indexer + ""), selectionRadioBtn, nameLabel, type, statusLabel}, false);
            rowLayout.setId(id);
            rowLayout.addLayoutClickListener(mgfClickListener);

            indexer++;
            mgfDataListLayout.addComponent(rowLayout);
        });
        indexer = 1;
        rawDataListLayout.removeAllComponents();
        rawFilesMap.keySet().forEach((id) -> {
            GalaxyFileObject ds = rawFilesMap.get(id);
            StatusLabel statusLabel = new StatusLabel();
            statusLabel.setStatus(ds.getStatus());
            Label nameLabel = new Label(ds.getName());
            Label type = new Label();
            type.setValue("<b>" + ds.getType() + "</b>");
            type.setContentMode(ContentMode.HTML);
            type.setDescription(ds.getType());
            RadioButton selectionRadioBtn = new RadioButton(id) {
                @Override
                public void selectItem(Object itemId) {

                }
            };

            HorizontalLayout rowLayout = initializeRowData(new Component[]{new Label(indexer + ""), selectionRadioBtn, nameLabel, type, statusLabel}, false);
            rowLayout.setId(id);
            rowLayout.addLayoutClickListener(rawClickListener);

            rawDataListLayout.addComponent(rowLayout);
            indexer++;
        });
        updateMgfFilesTable();
        updateRawFilesTable();
    }

    /**
     * Run Online Peptide-Shaker work-flow
     *
     * @param projectName name of the project to store
     * @param fastaFileId FASTA file dataset id
     * @param searchParameterFileId .par file id
     * @param inputIdsList list of MGF file dataset ids
     * @param searchEnginesList List of selected search engine names
     * @param searchParameters searching parameters // * @param searchEngines
     * search engines
     */
    public abstract void executeWorkFlow(String projectName, String fastaFileId, String searchParameterFileId, Set<String> inputIdsList, Set<String> searchEnginesList, IdentificationParameters searchParameters, boolean quant);

    /**
     * Validate and save search setting .par file on galaxy server for future
     * use.
     *
     * @param searchParameters selected search parameters
     * @param isNew create new file or edit exist file
     */
    private void checkAndSaveSearchSettingsFile(IdentificationParameters searchParameters, boolean isNew) {
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
        if (fastaFileIdToNameMap != null && fastaFileIdToNameMap.size() != fastaFilesMap.size()) {
            fastaFileUploader.setBusy(false);
            _fastaFileInputLayout.setEnabled(true);
        }
        fastaFileIdToNameMap = new LinkedHashMap<>();
        String selectedId = "";
        for (String id : fastaFilesMap.keySet()) {
            fastaFileIdToNameMap.put(id, fastaFilesMap.get(id).getName());
            if (selectedId.equals("")) {
                selectedId = id;
            }
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
    public abstract Map<String, GalaxyTransferableFile> saveSearchGUIParameters(IdentificationParameters searchParameters, boolean isNew);

    /**
     * upload file into galaxy
     *
     *
     * @param toUploadFiles files to be uploaded to galaxy
     * @return updated files map
     */
    public abstract boolean uploadToGalaxy(PluploadFile[] toUploadFiles);

    private HorizontalLayout initializeRowData(Component[] data, boolean header) {
        HorizontalLayout row = new HorizontalLayout();
        row.setSpacing(true);
        int i = 0;
        for (Component component : data) {
            component.addStyleName(ValoTheme.LABEL_NO_MARGIN);
            row.addComponent(component);
            row.setComponentAlignment(component, Alignment.MIDDLE_CENTER);
            row.setExpandRatio(component, expandingRatio[i]);
            i++;
        }
        row.setWidth(100, Unit.PERCENTAGE);
        row.setHeight(30, Unit.PIXELS);
        row.setStyleName("row");
        if (header) {
            row.addStyleName("header");
        }

        return row;
    }

    private void updateMgfFilesTable() {
        Iterator<Component> itr = mgfDataListLayout.iterator();
        while (itr.hasNext()) {
            HorizontalLayout raw = (HorizontalLayout) itr.next();
            if (_mgfFileList.contains(raw.getId() + "")) {
                raw.addStyleName("selectedraw");
            } else {
                raw.removeStyleName("selectedraw");
            }
        }

    }

    private void updateRawFilesTable() {
        Iterator<Component> itr = rawDataListLayout.iterator();
        while (itr.hasNext()) {
            HorizontalLayout raw = (HorizontalLayout) itr.next();
            if (_rawFileList.contains(raw.getId() + "")) {
                raw.addStyleName("selectedraw");
            } else {
                raw.removeStyleName("selectedraw");
            }
        }
    }

    public void reset() {
        _rawFileList.clear();
        _mgfFileList.clear();
        rawMgfController.select("rawFiles");
        updateRawFilesTable();
        this._projectNameField.clear();
    }
}
