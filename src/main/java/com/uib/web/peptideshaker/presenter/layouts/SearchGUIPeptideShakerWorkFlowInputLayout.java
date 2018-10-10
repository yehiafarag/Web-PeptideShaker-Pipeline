package com.uib.web.peptideshaker.presenter.layouts;

import com.compomics.util.experiment.identification.identification_parameters.SearchParameters;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyTransferableFile;
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
    private final HorizontalLabelTextField projectNameField;
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

        projectNameField = new HorizontalLabelTextField("<b>Project Name</b>", "New Project Name", null);
        projectNameField.setWidth(100, Unit.PERCENTAGE);
        projectNameField.setRequired(false);
        projectNameField.addStyleName("psprojectname");
        content.addComponent(projectNameField);
        HorizontalLayout btnsFrame = new HorizontalLayout();
        btnsFrame.setWidthUndefined();
        btnsFrame.setSpacing(true);
        btnsFrame.setStyleName("btnbesidetitle");
        content.addComponent(btnsFrame);

        searchSettingsFileList = new DropDownList("Search Settings");
        content.addComponent(searchSettingsFileList);
        searchSettingsFileList.setFocous();

        Label addNewSearchSettings = new Label("<<Add new>>");
        addNewSearchSettings.addStyleName("windowtitle");
        btnsFrame.addLayoutClickListener(new LayoutEvents.LayoutClickListener() {
            @Override
            public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                Component c = event.getClickedComponent();
                if (c != null && c instanceof Label && ((Label) c).getValue().equalsIgnoreCase("<<Add new>>")) {
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

                    try {
                        File file = searchSettingsMap.get(searchSettingsFileList.getSelectedValue()).getFile();
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
//        Label editSearchSettings = new Label("Edit");
//        editSearchSettings.addStyleName("windowtitle");
//        btnsFrame.addComponent(editSearchSettings);
        searchSettingsLayout = new SearchSettingsLayout() {
            @Override
            public void saveSearchingFile(SearchParameters searchParameters, boolean isNew) {
                checkAndSaveSearchSettingsFile(searchParameters, isNew);
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
        mgfFileList.setRequired(false, "Select at least 1 MGF file");
        mgfFileList.setViewList(true);
        mgfFileList.addStyleName("smallscreenfloatright");
        mgfFileList.addStyleName("top220");

        MultiSelectOptionGroup searchEngines = new MultiSelectOptionGroup("Search Engines", false);
        searchEngines.addStyleName("smallscreenfloatright");
        content.addComponent(searchEngines);
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

        Button executeWorkFlow = new Button("Execute");
        executeWorkFlow.setStyleName(ValoTheme.BUTTON_SMALL);
        executeWorkFlow.addStyleName(ValoTheme.BUTTON_TINY);
        bottomLayout.addComponent(executeWorkFlow);
        bottomLayout.setComponentAlignment(executeWorkFlow, Alignment.TOP_RIGHT);
        bottomLayout.setExpandRatio(executeWorkFlow, 30);

        executeWorkFlow.addClickListener((Button.ClickEvent event) -> {
            projectNameField.setRequired(true);
            mgfFileList.setRequired(true, "Select at least 1 MGF file");
            searchEngines.setRequired(true, "Select at least 1 search engine");
            
            String fastFileId = searchSettingsLayout.getFastaFileId();
            Set<String> spectrumIds = mgfFileList.getSelectedValue();
            Set<String> searchEnginesIds = searchEngines.getSelectedValue();
            String projectName = projectNameField.getSelectedValue().replace(" ", "_").replace("-", "_") + "___" + (new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Timestamp(System.currentTimeMillis())));
            
            if (!projectNameField.isValid() || fastFileId == null || spectrumIds == null || searchEnginesIds == null) {
                return;
            }
            projectNameField.setRequired(false);
            mgfFileList.setRequired(false, "Select at least 1 MGF file");
            searchEngines.setRequired(false, "Select at least 1 search engine");
            Map<String, Boolean> selectedSearchEngines = new HashMap<>();
            searchEngienList.keySet().forEach((paramId) -> {
                selectedSearchEngines.put(paramId, searchEngines.getSelectedValue().contains(paramId));
            });
            executeWorkFlow(projectName, fastFileId, spectrumIds, searchEnginesIds, searchSettingsLayout.getSearchParameters());
        });
        mgfFileList.setEnabled(false);
        searchEngines.setEnabled(false);
        projectNameField.setEnabled(false);
//        editSearchSettings.setEnabled(false);
        executeWorkFlow.setEnabled(false);
        searchSettingsFileList.addValueChangeListener((Property.ValueChangeEvent event) -> {
            if (searchSettingsFileList.getSelectedValue() != null) {
                mgfFileList.setEnabled(true);
                searchEngines.setEnabled(true);
                projectNameField.setEnabled(true);
//                editSearchSettings.setEnabled(true);
                executeWorkFlow.setEnabled(true);
                try {
                    File file = searchSettingsMap.get(searchSettingsFileList.getSelectedValue()).getFile();
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
    public void updateForm(Map<String, GalaxyTransferableFile> searchSettingsMap, Map<String, GalaxyFileObject> fastaFilesMap, Map<String, GalaxyFileObject> mgfFilesMap) {

        this.searchSettingsMap = searchSettingsMap;
        searchSettingsLayout.updateFastaFileList(fastaFilesMap);
        Map<String, String> searchSettingsFileIdToNameMap = new LinkedHashMap<>();
        Object selectedId = "";
        for (String id : searchSettingsMap.keySet()) {
            searchSettingsFileIdToNameMap.put(id, searchSettingsMap.get(id).getGalaxyFileObject().getName().replace(".par", ""));
            selectedId = id;
        }
        searchSettingsFileList.updateList(searchSettingsFileIdToNameMap);
        searchSettingsFileList.setSelected(selectedId);
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
