package com.uib.web.peptideshaker.presenter.components;

import com.compomics.util.experiment.identification.identification_parameters.SearchParameters;
import com.uib.web.peptideshaker.galaxy.SystemDataSet;
import com.uib.web.peptideshaker.galaxy.GalaxyFile;
import com.uib.web.peptideshaker.presenter.core.DropDownList;
import com.uib.web.peptideshaker.presenter.core.MultiSelectOptionGroup;
import com.uib.web.peptideshaker.presenter.core.PopupWindow;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabelTextField;
import com.vaadin.data.Property;
import com.vaadin.event.LayoutEvents;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.io.File;
import java.io.IOException;
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
public abstract class WorkFlowLayout extends Panel {

    /**
     * Search settings .par file drop-down list .
     */
    private final DropDownList searchSettingsFileList;

    /**
     * select MGF file list.
     */
    private final MultiSelectOptionGroup mgfFileList;

    private final HorizontalLabelTextField projectNameField;
//    /**
//     * Create decoy database file list.
//     */
//    private final MultiSelectOptionGroup databaseOptionList;

    private final SearchSettingsLayout searchSettingsLayout;
    private final PopupWindow editSearchOption;
    private Map<String, GalaxyFile> searchSettingsMap;
    private SearchParameters searchParameters;

    /**
     * Constructor to initialize the main attributes.
     */
    public WorkFlowLayout() {
        WorkFlowLayout.this.setWidth(100, Unit.PERCENTAGE);
        WorkFlowLayout.this.setHeight(100, Unit.PERCENTAGE);

        VerticalLayout content = new VerticalLayout();
        content.setHeightUndefined();
        content.setWidth(100, Unit.PERCENTAGE);
        WorkFlowLayout.this.setContent(content);
        WorkFlowLayout.this.setStyleName("subframe");

        content.setSpacing(true);

        Label titleLabel = new Label("SearchGUI-PeptideShaker-WorkFlow");
        titleLabel.setStyleName("frametitle");
        content.addComponent(titleLabel);

        searchSettingsFileList = new DropDownList("Search Settings (Select or Enter New Name)");
        content.addComponent(searchSettingsFileList);
        searchSettingsFileList.setFocous();

        HorizontalLayout btnsFrame = new HorizontalLayout();
        btnsFrame.setWidthUndefined();
        btnsFrame.setSpacing(true);
        btnsFrame.setStyleName("bottomformlayout");
        content.addComponent(btnsFrame);

        Label addNewSearchSettings = new Label("Add");
        addNewSearchSettings.addStyleName("windowtitle");
        btnsFrame.addLayoutClickListener(new LayoutEvents.LayoutClickListener() {
            @Override
            public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                Component c = event.getClickedComponent();
                if (c != null && c instanceof Label && ((Label) c).getValue().equalsIgnoreCase("Add")) {
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
                    searchSettingsLayout.updateForms(searchParameters, null);
                    editSearchOption.setPopupVisible(true);
                } else if (c != null && c instanceof Label && ((Label) c).getValue().equalsIgnoreCase("Edit")) {
                    File file = searchSettingsMap.get(searchSettingsFileList.getSelectedValue()).getFile();
                    SearchParameters searchParameters;
                    try {
                        searchParameters = SearchParameters.getIdentificationParameters(file);
                    } catch (IOException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                        return;
                    }
                    searchSettingsLayout.updateForms(searchParameters, searchSettingsFileList.getSelectedValue());
                    editSearchOption.setPopupVisible(true);

                }
            }
        });
        btnsFrame.addComponent(addNewSearchSettings);
        Label editSearchSettings = new Label("Edit");
        editSearchSettings.addStyleName("windowtitle");
        btnsFrame.addComponent(editSearchSettings);
        searchSettingsLayout = new SearchSettingsLayout() {
            @Override
            public void saveSearchingFile(SearchParameters searchParameters, boolean editMode) {
                checkAndSaveSearchSettingsFile(searchParameters, editMode);
                editSearchOption.setPopupVisible(false);
            }

            @Override
            public void cancel() {
                editSearchOption.setPopupVisible(false);
            }

        };
        editSearchOption = new PopupWindow("Edit");
        editSearchOption.setContent(searchSettingsLayout);
        editSearchOption.setSizeFull();
        editSearchOption.addStyleName("centerwindow");

        Label searchSettingInfo = new Label();
        searchSettingInfo.setWidth(400, Unit.PIXELS);
        searchSettingInfo.setHeight(90, Unit.PIXELS);
        searchSettingInfo.setStyleName("subpanelframe");
        searchSettingInfo.addStyleName("bottomformlayout");
        searchSettingInfo.addStyleName("smallfontlongtext");
        content.addComponent(searchSettingInfo);

        mgfFileList = new MultiSelectOptionGroup("Spectrum File(s)", false);
        content.addComponent(mgfFileList);
        mgfFileList.setRequired(true, "Select at least 1 MGF file");
        mgfFileList.setViewList(true);

        MultiSelectOptionGroup searchEngines = new MultiSelectOptionGroup("Search Engines", false);
        content.addComponent(searchEngines);
        searchEngines.setRequired(true, "Select at least 1 search engine");
        searchEngines.setViewList(true);

        Map<String, String> searchEngienList = new LinkedHashMap<>();
        searchEngienList.put("X!Tandem", "X!Tandem");
        searchEngienList.put("MS-GF+", "MS-GF+");
        searchEngienList.put("OMSSA", "OMSSA");
        searchEngienList.put("Comet", "Comet");
        searchEngienList.put("Tide", "Tide");
        searchEngienList.put("MyriMatch", "MyriMatch");
        searchEngienList.put("MS_Amanda", "MS_Amanda");
        searchEngienList.put("DirecTag", "DirecTag");
        searchEngienList.put("Novor (Select for non-commercial use only)", "Novor (Select for non-commercial use only)");
        searchEngines.updateList(searchEngienList);
        searchEngines.setSelectedValue("X!Tandem");
        searchEngines.setSelectedValue("MS-GF+");
        searchEngines.setSelectedValue("OMSSA");
        HorizontalLayout bottomLayout = new HorizontalLayout();
        bottomLayout.setStyleName("bottomformlayout");
        bottomLayout.setWidth(400, Unit.PIXELS);
        bottomLayout.setSpacing(true);
        content.addComponent(bottomLayout);

        projectNameField = new HorizontalLabelTextField("<b>Project Name</b>", "New Project Name", null);
        projectNameField.setWidth(100, Unit.PERCENTAGE);
        projectNameField.setRequired(true);
        bottomLayout.addComponent(projectNameField);
        bottomLayout.setExpandRatio(projectNameField, 70);

        Button executeWorkFlow = new Button("Execute");
        executeWorkFlow.setStyleName(ValoTheme.BUTTON_SMALL);
        executeWorkFlow.addStyleName(ValoTheme.BUTTON_TINY);
        bottomLayout.addComponent(executeWorkFlow);
        bottomLayout.setComponentAlignment(executeWorkFlow, Alignment.TOP_RIGHT);
        bottomLayout.setExpandRatio(executeWorkFlow, 30);

        executeWorkFlow.addClickListener((Button.ClickEvent event) -> {
            String fastFileId = searchSettingsLayout.getFataFileId();
            Set<String> spectrumIds = mgfFileList.getSelectedValue();
            Set<String> searchEnginesIds = searchEngines.getSelectedValue();
            String projectName = projectNameField.getSelectedValue();
            if (!projectNameField.isValid() || fastFileId == null || spectrumIds == null || searchEnginesIds == null) {
                return;
            }
            Map<String, Boolean> otherSearchParameters = new HashMap<>();

            for (String paramId : searchEngienList.keySet()) {
                otherSearchParameters.put(paramId, searchEngines.getSelectedValue().contains(paramId));
            }
            executeWorkFlow(projectName, fastFileId, spectrumIds, searchEnginesIds, searchSettingsLayout.getSearchParameters(), otherSearchParameters);

        });
        mgfFileList.setEnabled(false);
        searchEngines.setEnabled(false);
        projectNameField.setEnabled(false);
        editSearchSettings.setEnabled(false);
        executeWorkFlow.setEnabled(false);
        searchSettingsFileList.addValueChangeListener((Property.ValueChangeEvent event) -> {
            if (searchSettingsFileList.getSelectedValue() != null) {
                mgfFileList.setEnabled(true);
                searchEngines.setEnabled(true);
                projectNameField.setEnabled(true);
                editSearchSettings.setEnabled(true);
                executeWorkFlow.setEnabled(true);
                File file = searchSettingsMap.get(searchSettingsFileList.getSelectedValue()).getFile();
                SearchParameters searchParameters;
                try {
                    searchParameters = SearchParameters.getIdentificationParameters(file);
                    String descrip = searchParameters.getShortDescription();
                    descrip = descrip.replace(searchParameters.getFastaFile().getName(), searchSettingsLayout.getFastaFileName(searchParameters.getFastaFile().getName().split("__")[0]));
                    searchSettingInfo.setValue(descrip);
                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                    return;
                }
                searchSettingsLayout.updateForms(searchParameters, searchSettingsFileList.getSelectedValue());

            }
        });
    }

    /**
     * Update the tools input forms
     *
     * @param searchSettingsMap search settings .par files map
     * @param fastaFilesMap FASTA files map
     * @param mgfFilesMap MGF file map
     */
    public void updateForm(Map<String, GalaxyFile> searchSettingsMap, Map<String, SystemDataSet> fastaFilesMap, Map<String, SystemDataSet> mgfFilesMap) {

        this.searchSettingsMap = searchSettingsMap;
        searchSettingsLayout.updateFastaFileList(fastaFilesMap);
        Map<String, String> searchSettingsFileIdToNameMap = new LinkedHashMap<>();
        Object selectedId = "";
        for (String id : searchSettingsMap.keySet()) {
            searchSettingsFileIdToNameMap.put(id, searchSettingsMap.get(id).getDataset().getName().replace(".par", ""));
            selectedId = id;
        }
        searchSettingsFileList.updateList(searchSettingsFileIdToNameMap);
        searchSettingsFileList.setSelected(selectedId);

        Map<String, String> mgfFileIdToNameMap = new LinkedHashMap<>();
        for (String id : mgfFilesMap.keySet()) {
            mgfFileIdToNameMap.put(id, mgfFilesMap.get(id).getName());
        }
        mgfFileList.updateList(mgfFileIdToNameMap);
        if (mgfFileIdToNameMap.size() == 1) {
            mgfFileList.setSelectedValue(mgfFileIdToNameMap.keySet());
        }

    }

    /**
     * Run Online Peptide-Shaker work-flow
     *
     * @param fastaFileId FASTA file dataset id
     * @param mgfIdsList list of MGF file dataset ids
     * @param searchEnginesList List of selected search engine names
     * @param historyId galaxy history id that will store the results
     */
    public abstract void executeWorkFlow(String projectName, String fastaFileId, Set<String> mgfIdsList, Set<String> searchEnginesList, SearchParameters searchParameters, Map<String, Boolean> otherSearchParameters);

    private void checkAndSaveSearchSettingsFile(SearchParameters searchParameters, boolean editMode) {
        this.searchParameters = searchParameters;
        searchSettingsMap = saveSearchGUIParameters(searchParameters, editMode);
        Map<String, String> searchSettingsFileIdToNameMap = new LinkedHashMap<>();
        String objectId = "";
        for (String id : searchSettingsMap.keySet()) {
            searchSettingsFileIdToNameMap.put(id, searchSettingsMap.get(id).getDataset().getName().replace(".par", ""));
            objectId = id;
        }
        searchSettingsFileList.updateList(searchSettingsFileIdToNameMap);
        searchSettingsFileList.setSelected(objectId);

    }

    /**
     * Save search settings file into galaxy
     *
     * @param fileName search parameters file name
     * @param searchParameters searchParameters .par file
     */
    public abstract Map<String, GalaxyFile> saveSearchGUIParameters(SearchParameters searchParameters, boolean editMode);
}
