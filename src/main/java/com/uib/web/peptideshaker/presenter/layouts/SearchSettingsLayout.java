package com.uib.web.peptideshaker.presenter.layouts;

import com.compomics.util.experiment.biology.Enzyme;
import com.compomics.util.experiment.biology.EnzymeFactory;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.identification.identification_parameters.PtmSettings;
import com.compomics.util.experiment.identification.identification_parameters.SearchParameters;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.preferences.DigestionPreferences;
import com.uib.web.peptideshaker.galaxy.dataobjects.SystemDataSet;
import com.uib.web.peptideshaker.presenter.core.DropDownList;
import com.uib.web.peptideshaker.presenter.core.MultiSelectOptionGroup;
import com.uib.web.peptideshaker.presenter.core.PopupWindow;
import com.uib.web.peptideshaker.presenter.core.form.ColorLabel;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabel2DropdownList;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabel2TextField;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabelDropDounList;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabelTextField;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabelTextFieldDropdownList;
import com.uib.web.peptideshaker.presenter.core.form.SparkLine;
import com.vaadin.data.Property;
import com.vaadin.data.validator.DoubleRangeValidator;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class represents the search settings form layout
 *
 * @author Yehia Farag
 */
public abstract class SearchSettingsLayout extends VerticalLayout {

//    private final SearchParameters searchParameters;
    /**
     * The sequence factory.
     */
//    private final SequenceFactory sequenceFactory = SequenceFactory.getInstance();
    /**
     * The enzyme factory.
     */
    private final EnzymeFactory enzymeFactory = EnzymeFactory.getInstance();
  
    
    /**
     * Convenience array for ion type selection.
     */
    private final List<String> ions = new ArrayList(Arrays.asList(new String[]{"a", "b", "c","x", "y", "z"}));
    /**
     * The post translational modifications factory.
     */
    private final PTMFactory PTM = PTMFactory.getInstance();

    private final Set<String> commonModificationIds;
    private Table fixedModificationTable;
    private Table variableModificationTable;
    private Table allModificationsTable;
    private Table mostUsedModificationsTable;
    private Button toFixedModBtn;
    private Button toVariableModBtn;
    private Button fromVariableModBtn;
    private Button fromFixedModBtn;
    private final Map<Object, Object[]> completeModificationItems = new LinkedHashMap<>();
    /**
     * Select FASTA file drop-down list .
     */
    private final DropDownList fastaFileList;
    private Map<String, String> fastaFileIdToNameMap;
    /**
     * Create decoy database file list.
     */
    private final MultiSelectOptionGroup databaseOptionList;

    private HorizontalLabelTextField searchSettingsName;
    private String paramFileId;

    /**
     * Constructor to initialize the main setting parameters
     */
    public SearchSettingsLayout() {
        SearchSettingsLayout.this.setMargin(true);
        SearchSettingsLayout.this.setSizeUndefined();
        SearchSettingsLayout.this.setSpacing(true);
        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setSizeFull();
        titleLayout.addStyleName("subpanelframe");

        this.commonModificationIds = new HashSet<>();
        String mod = "Acetylation of K//Acetylation of protein N-term//Carbamidomethylation of C//Oxidation of M//Phosphorylation of S//Phosphorylation of T//Phosphorylation of Y//Arginine 13C6//Lysine 13C6//iTRAQ 4-plex of peptide N-term//iTRAQ 4-plex of K//iTRAQ 4-plex of Y//iTRAQ 8-plex of peptide N-term//iTRAQ 8-plex of K//iTRAQ 8-plex of Y//TMT 6-plex of peptide N-term//TMT 6-plex of K//TMT 10-plex of peptide N-term//TMT 10-plex of K//Pyrolidone from E//Pyrolidone from Q//Pyrolidone from carbamidomethylated C//Deamidation of N//Deamidation of Q";
        commonModificationIds.addAll(Arrays.asList(mod.split("//")));

        SearchSettingsLayout.this.addComponent(titleLayout);
        Label setteingsLabel = new Label("Search Settings");
        setteingsLabel.addStyleName(ValoTheme.LABEL_BOLD);
        titleLayout.addComponent(setteingsLabel);
        titleLayout.setExpandRatio(setteingsLabel, 20);

        PopupWindow advancedSearchOption = new PopupWindow("(Advanced Settings)");
        advancedSearchOption.setContent( initAdvancedSearchOption());
        advancedSearchOption.setSizeFull();
        advancedSearchOption.addStyleName("centerwindow");
        titleLayout.addComponent(advancedSearchOption);
        titleLayout.setComponentAlignment(advancedSearchOption, Alignment.MIDDLE_LEFT);
        titleLayout.setExpandRatio(advancedSearchOption, 80);

        VerticalLayout upperPanel = new VerticalLayout();
        upperPanel.setWidth(100, Unit.PERCENTAGE);
        upperPanel.addStyleName("subpanelframe");
        SearchSettingsLayout.this.addComponent(upperPanel);

        searchSettingsName = new HorizontalLabelTextField("<b>Search Settings Name</b>", "New Searching Settings Name", null);
        searchSettingsName.setWidth(60, Unit.PERCENTAGE);
        searchSettingsName.setRequired(true);
        upperPanel.addComponent(searchSettingsName);

        HorizontalLayout protDatabaseContainer = new HorizontalLayout();
        protDatabaseContainer.setWidthUndefined();
        protDatabaseContainer.setHeight(60, Unit.PIXELS);
        upperPanel.addComponent(protDatabaseContainer);
        fastaFileList = new DropDownList("Protein Database (FASTA)");
        protDatabaseContainer.addComponent(fastaFileList);
        fastaFileList.setRequired(true, "Select FASTA file");
        fastaFileList.addStyleName("paddingleft-20");
        databaseOptionList = new MultiSelectOptionGroup(null, false);

        protDatabaseContainer.addComponent(databaseOptionList);
        protDatabaseContainer.setComponentAlignment(databaseOptionList, Alignment.BOTTOM_LEFT);
        Map<String, String> paramMap = new LinkedHashMap<>();
        paramMap.put("create_decoy", "Add Decoy Sequences");
//        paramMap.put("use_gene_mapping", "Use Gene Mappings (UniProt databases only)");
//        paramMap.put("update_gene_mapping", "Update gene mappings automatically from Ensembl (UniProt databases only)");

        databaseOptionList.updateList(paramMap);
        databaseOptionList.setSelectedValue("create_decoy");
        databaseOptionList.setViewList(true);

        HorizontalLayout modificationContainer = inititModificationLayout();
        modificationContainer.addStyleName("subpanelframe");
        SearchSettingsLayout.this.addComponent(modificationContainer);

        GridLayout proteaseFragmentationContainer = inititProteaseFragmentationLayout();
        proteaseFragmentationContainer.addStyleName("subpanelframe");
        SearchSettingsLayout.this.addComponent(proteaseFragmentationContainer);
    }

    public String getFastaFileId() {
        return fastaFileList.getSelectedValue();
    }

    public void updateFastaFileList(Map<String, SystemDataSet> fastaFilesMap) {
        fastaFileIdToNameMap = new LinkedHashMap<>();
        String selectedId = "";
        for (String id : fastaFilesMap.keySet()) {
            fastaFileIdToNameMap.put(id, fastaFilesMap.get(id).getName());
            selectedId = id;
        }
        fastaFileList.updateList(fastaFileIdToNameMap);
        fastaFileList.setSelected(selectedId);

    }

    private HorizontalLayout inititModificationLayout() {
        HorizontalLayout modificationContainer = new HorizontalLayout();
        modificationContainer.setStyleName("panelframe");
        modificationContainer.setSizeFull();
        modificationContainer.setMargin(new MarginInfo(false, false, false, false));
        modificationContainer.setWidth(700, Unit.PIXELS);
        modificationContainer.setHeight(360, Unit.PIXELS);

        VerticalLayout leftSideLayout = new VerticalLayout();
        leftSideLayout.setSizeFull();
        leftSideLayout.setSpacing(true);
        leftSideLayout.setMargin(new MarginInfo(false, false, false, false));
        modificationContainer.addComponent(leftSideLayout);
        modificationContainer.setExpandRatio(leftSideLayout, 45);

        Label modificationLabel = new Label("Modifications");
        modificationLabel.setSizeFull();
        leftSideLayout.addComponent(modificationLabel);
        leftSideLayout.setExpandRatio(modificationLabel, 4);

        HorizontalLayout leftTopLayout = new HorizontalLayout();
        leftTopLayout.setSizeFull();
        leftTopLayout.setSpacing(true);
        leftSideLayout.addComponent(leftTopLayout);
        leftSideLayout.setExpandRatio(leftTopLayout, 48);

        fixedModificationTable = initModificationTable("Fixed Modifications");
        leftTopLayout.addComponent(fixedModificationTable);
        HorizontalLayout leftBottomLayout = new HorizontalLayout();
        leftBottomLayout.setSizeFull();
        leftBottomLayout.setSpacing(true);
        leftSideLayout.addComponent(leftBottomLayout);
        leftSideLayout.setExpandRatio(leftBottomLayout, 48);
        variableModificationTable = initModificationTable("Variable Modifications");
        leftBottomLayout.addComponent(variableModificationTable);
        leftBottomLayout.setExpandRatio(variableModificationTable, 80);

        VerticalLayout middleSideLayout = new VerticalLayout();
        middleSideLayout.setSizeFull();
        modificationContainer.addComponent(middleSideLayout);
        modificationContainer.setExpandRatio(middleSideLayout, 10);

        VerticalLayout spacer = new VerticalLayout();
        spacer.setSizeFull();
        middleSideLayout.addComponent(spacer);
        middleSideLayout.setExpandRatio(spacer, 4);

        VerticalLayout sideTopButtons = new VerticalLayout();
        sideTopButtons.setSizeUndefined();
        middleSideLayout.addComponent(sideTopButtons);
        middleSideLayout.setComponentAlignment(sideTopButtons, Alignment.BOTTOM_CENTER);
        middleSideLayout.setExpandRatio(sideTopButtons, 48);

        toFixedModBtn = new Button(VaadinIcons.ARROW_LEFT);
        toFixedModBtn.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        sideTopButtons.addComponent(toFixedModBtn);
        sideTopButtons.setComponentAlignment(toFixedModBtn, Alignment.BOTTOM_CENTER);

        fromFixedModBtn = new Button(VaadinIcons.ARROW_RIGHT);
        fromFixedModBtn.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        sideTopButtons.addComponent(fromFixedModBtn);
        sideTopButtons.setComponentAlignment(fromFixedModBtn, Alignment.MIDDLE_CENTER);

        VerticalLayout sideBottomButtons = new VerticalLayout();
        middleSideLayout.addComponent(sideBottomButtons);
        middleSideLayout.setComponentAlignment(sideBottomButtons, Alignment.BOTTOM_CENTER);
        middleSideLayout.setExpandRatio(sideBottomButtons, 48);

        toVariableModBtn = new Button(VaadinIcons.ARROW_LEFT);
        toVariableModBtn.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        sideBottomButtons.addComponent(toVariableModBtn);
        sideBottomButtons.setComponentAlignment(toVariableModBtn, Alignment.MIDDLE_CENTER);

        fromVariableModBtn = new Button(VaadinIcons.ARROW_RIGHT);
        fromVariableModBtn.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        sideBottomButtons.addComponent(fromVariableModBtn);
        sideBottomButtons.setComponentAlignment(fromVariableModBtn, Alignment.MIDDLE_CENTER);

        VerticalLayout rightSideLayout = new VerticalLayout();
        rightSideLayout.setSizeFull();
        rightSideLayout.setSpacing(true);
        modificationContainer.addComponent(rightSideLayout);
        modificationContainer.setExpandRatio(rightSideLayout, 45);

        ComboBox modificationListControl = new ComboBox();
        modificationListControl.setWidth(100, Unit.PERCENTAGE);
        modificationListControl.setHeight(30, Unit.PIXELS);
        modificationListControl.setStyleName(ValoTheme.COMBOBOX_SMALL);
        modificationListControl.addStyleName(ValoTheme.COMBOBOX_TINY);
        modificationListControl.setNullSelectionAllowed(false);
        modificationListControl.addItem("Most Used Modifications");
        modificationListControl.addItem("All Modifications");
        modificationListControl.setValue("Most Used Modifications");
        rightSideLayout.addComponent(modificationListControl);
        rightSideLayout.setExpandRatio(modificationListControl, 4);
        rightSideLayout.setComponentAlignment(modificationListControl, Alignment.MIDDLE_CENTER);

        mostUsedModificationsTable = initModificationTable("");

        List<String> allModiList = PTM.getDefaultModifications();
        // get the min and max values for the mass sparklines
        double maxMass = Double.MIN_VALUE;
        double minMass = Double.MAX_VALUE;

        for (String ptm : PTM.getPTMs()) {
            if (PTM.getPTM(ptm).getMass() > maxMass) {
                maxMass = PTM.getPTM(ptm).getMass();
            }
            if (PTM.getPTM(ptm).getMass() < minMass) {
                minMass = PTM.getPTM(ptm).getMass();
            }
        }

        for (int x = 0; x < allModiList.size(); x++) {
            ColorLabel color = new ColorLabel(PTM.getColor(allModiList.get(x)));
            SparkLine sLine = new SparkLine(PTM.getPTM(allModiList.get(x)).getMass(), minMass, maxMass);
            Object[] modificationArr = new Object[]{color, allModiList.get(x), sLine};
            completeModificationItems.put(allModiList.get(x), modificationArr);
        }
        rightSideLayout.addComponent(mostUsedModificationsTable);
        for (Object id : completeModificationItems.keySet()) {
            if (commonModificationIds.contains(id.toString())) {
                mostUsedModificationsTable.addItem(completeModificationItems.get(id), id);
            }
        }
        mostUsedModificationsTable.setCaption("(" + mostUsedModificationsTable.getItemIds().size() + ")");
        mostUsedModificationsTable.setVisible(false);
        rightSideLayout.setExpandRatio(mostUsedModificationsTable, 96);
        allModificationsTable = initModificationTable("");
        rightSideLayout.addComponent(allModificationsTable);
        rightSideLayout.setExpandRatio(allModificationsTable, 96);
        allModificationsTable.setVisible(false);

        modificationListControl.addValueChangeListener((Property.ValueChangeEvent event) -> {
            allModificationsTable.removeAllItems();
            mostUsedModificationsTable.removeAllItems();
            if (modificationListControl.getValue().toString().equalsIgnoreCase("All Modifications")) {
                for (Object id : completeModificationItems.keySet()) {
                    if (fixedModificationTable.containsId(id) || variableModificationTable.containsId(id)) {
                        continue;
                    }
                    allModificationsTable.addItem(completeModificationItems.get(id), id);
                }
                allModificationsTable.setVisible(true);
                mostUsedModificationsTable.setVisible(false);

            } else {
                for (Object id : completeModificationItems.keySet()) {
                    if (fixedModificationTable.containsId(id) || variableModificationTable.containsId(id)) {
                        continue;
                    }
                    if (commonModificationIds.contains(id.toString())) {
                        mostUsedModificationsTable.addItem(completeModificationItems.get(id), id);
                    }
                }

                allModificationsTable.setVisible(false);
                mostUsedModificationsTable.setVisible(true);

            }
            allModificationsTable.sort(new Object[]{"name"}, new boolean[]{true});
            mostUsedModificationsTable.sort(new Object[]{"name"}, new boolean[]{true});
            mostUsedModificationsTable.setCaption("(" + mostUsedModificationsTable.getItemIds().size() + ")");
            allModificationsTable.setCaption("(" + allModificationsTable.getItemIds().size() + ")");
        });
        toFixedModBtn.addClickListener((Button.ClickEvent event) -> {
            Table selectionTable;
            if (allModificationsTable.isVisible()) {
                selectionTable = allModificationsTable;
            } else {
                selectionTable = mostUsedModificationsTable;
            }
            Set<Object> selection = ((Set<Object>) selectionTable.getValue());
            for (Object id : selection) {
                selectionTable.removeItem(id);
                fixedModificationTable.addItem(completeModificationItems.get(id), id);

            }
            fixedModificationTable.sort(new Object[]{"name"}, new boolean[]{true});
            fixedModificationTable.setCaption("Fixed Modifications (" + fixedModificationTable.getItemIds().size() + ")");
            selectionTable.setCaption("(" + selectionTable.getItemIds().size() + ")");

        });
        fromFixedModBtn.addClickListener((Button.ClickEvent event) -> {
            Table selectionTable;
            if (allModificationsTable.isVisible()) {
                selectionTable = allModificationsTable;
            } else {
                selectionTable = mostUsedModificationsTable;
            }
            Set<Object> selection = ((Set<Object>) fixedModificationTable.getValue());
            for (Object id : selection) {
                fixedModificationTable.removeItem(id);
                selectionTable.addItem(completeModificationItems.get(id), id);

            }
            selectionTable.sort(new Object[]{"name"}, new boolean[]{true});
            fixedModificationTable.setCaption("Fixed Modifications (" + fixedModificationTable.getItemIds().size() + ")");
            selectionTable.setCaption("(" + selectionTable.getItemIds().size() + ")");

        });
        toVariableModBtn.addClickListener((Button.ClickEvent event) -> {
            Table selectionTable;
            if (allModificationsTable.isVisible()) {
                selectionTable = allModificationsTable;
            } else {
                selectionTable = mostUsedModificationsTable;
            }
            Set<Object> selection = ((Set<Object>) selectionTable.getValue());
            for (Object id : selection) {
                selectionTable.removeItem(id);
                variableModificationTable.addItem(completeModificationItems.get(id), id);

            }
            variableModificationTable.sort(new Object[]{"name"}, new boolean[]{true});
            variableModificationTable.setCaption("Variable Modifications (" + variableModificationTable.getItemIds().size() + ")");
            selectionTable.setCaption("(" + selectionTable.getItemIds().size() + ")");

        });
        fromVariableModBtn.addClickListener((Button.ClickEvent event) -> {
            Table selectionTable;
            if (allModificationsTable.isVisible()) {
                selectionTable = allModificationsTable;
            } else {
                selectionTable = mostUsedModificationsTable;
            }
            Set<Object> selection = ((Set<Object>) variableModificationTable.getValue());
            for (Object id : selection) {
                variableModificationTable.removeItem(id);
                selectionTable.addItem(completeModificationItems.get(id), id);

            }
            selectionTable.sort(new Object[]{"name"}, new boolean[]{true});
            variableModificationTable.setCaption("Variable Modifications (" + variableModificationTable.getItemIds().size() + ")");
            selectionTable.setCaption("(" + selectionTable.getItemIds().size() + ")");

        });
        mostUsedModificationsTable.setVisible(true);

        return modificationContainer;

    }

    private VerticalLayout initAdvancedSearchOption() {
        return new VerticalLayout();

    }

    private Table initModificationTable(String cap) {
        Table modificationsTable = new Table(cap) {
            DecimalFormat df = new DecimalFormat("#.##");

            @Override
            protected String formatPropertyValue(Object rowId, Object colId, Property property) {
                Object v = property.getValue();
                if (v instanceof Double) {
                    return df.format(v);
                }
                return super.formatPropertyValue(rowId, colId, property);
            }

        };
        Set<Object> idSet = new HashSet<>();
        modificationsTable.setData(idSet);
        modificationsTable.setSizeFull();
        modificationsTable.setStyleName(ValoTheme.TABLE_SMALL);
        modificationsTable.addStyleName(ValoTheme.TABLE_COMPACT);
        modificationsTable.addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
        modificationsTable.addStyleName("smalltable");
        modificationsTable.setMultiSelect(true);
        modificationsTable.setSelectable(true);
        modificationsTable.addContainerProperty("color", ColorLabel.class, null, "", null, Table.Align.CENTER);
        modificationsTable.addContainerProperty("name", String.class, null, "Name", null, Table.Align.LEFT);
        modificationsTable.addContainerProperty("mass", SparkLine.class, null, "Mass", null, Table.Align.LEFT);
        modificationsTable.setColumnExpandRatio("color", 10);
        modificationsTable.setColumnExpandRatio("name", 55);
        modificationsTable.setColumnExpandRatio("mass", 35);
        modificationsTable.sort(new Object[]{"name"}, new boolean[]{true});
        modificationsTable.setSortEnabled(false);
        modificationsTable.setItemDescriptionGenerator((Component source, Object itemId, Object propertyId) -> PTM.getPTM(itemId.toString()).getHtmlTooltip());
        return modificationsTable;
    }

    private HorizontalLabelDropDounList digestionList;
    private HorizontalLabelDropDounList enzymeList;
    private HorizontalLabelDropDounList specificityList;
    private HorizontalLabelTextField maxMissCleav;
    private HorizontalLabel2DropdownList fragmentIonTypes;
    private HorizontalLabelTextFieldDropdownList precursorTolerance;
    private HorizontalLabelTextFieldDropdownList fragmentTolerance;
    private HorizontalLabel2TextField precursorCharge;
    private HorizontalLabel2TextField isotopes;
    private SearchParameters searchParameters;
    private boolean editMode = false;

    private GridLayout inititProteaseFragmentationLayout() {
        GridLayout proteaseFragmentationContainer = new GridLayout(2, 6);
        proteaseFragmentationContainer.setStyleName("panelframe");
        proteaseFragmentationContainer.setColumnExpandRatio(0, 55);
        proteaseFragmentationContainer.setColumnExpandRatio(1, 45);
        proteaseFragmentationContainer.setMargin(new MarginInfo(false, false, false, false));
        proteaseFragmentationContainer.setWidth(700, Unit.PIXELS);
        proteaseFragmentationContainer.setHeight(190, Unit.PIXELS);
        proteaseFragmentationContainer.setSpacing(true);

        Label label = new Label("Protease & Fragmentation");
        label.setSizeFull();
        proteaseFragmentationContainer.addComponent(label, 0, 0);

        Set<String> digestionOptionList = new LinkedHashSet<>();
        digestionOptionList.add("Enzyme");
        digestionOptionList.add("Unspecific");
        digestionOptionList.add("Whole Protein");

        digestionList = new HorizontalLabelDropDounList("Digestion");
        digestionList.updateData(digestionOptionList);
        digestionList.addValueChangeListener((Property.ValueChangeEvent event) -> {
            if (digestionList.getSelectedValue().equalsIgnoreCase("Enzyme")) {
                enzymeList.setEnabled(true);
                specificityList.setEnabled(true);
                maxMissCleav.setEnabled(true);
            } else {
                maxMissCleav.setEnabled(false);
                enzymeList.setEnabled(false);
                specificityList.setEnabled(false);
            } 

        });

        enzymeList = new HorizontalLabelDropDounList("Enzyme");
        Set<String> enzList = new LinkedHashSet<>();
        List<Enzyme> enzObjList = enzymeFactory.getEnzymes();
        for (Enzyme enz : enzObjList) {
            enzList.add(enz.getName());
        }
        enzymeList.updateData(enzList);
        Set<String> specificityOptionList = new LinkedHashSet<>();
        specificityOptionList.add("Specific");
        specificityOptionList.add("Semi-Specific");
        specificityOptionList.add("N-term Specific");
        specificityOptionList.add("C-term Specific");

        specificityList = new HorizontalLabelDropDounList("Specificity");
        specificityList.updateData(specificityOptionList);
        maxMissCleav = new HorizontalLabelTextField("Max Missed Cleavages", 2, new IntegerRangeValidator("Error", Integer.MIN_VALUE, Integer.MAX_VALUE));

        Set<String> ionListI = new LinkedHashSet<>();
        ionListI.add("a");
        ionListI.add("b");
        ionListI.add("c");
        Set<String> ionListII = new LinkedHashSet<>();
        ionListII.add("x");
        ionListII.add("y");
        ionListII.add("z");
        fragmentIonTypes = new HorizontalLabel2DropdownList("Fragment Ion Types", ionListI, ionListII);

        proteaseFragmentationContainer.addComponent(digestionList, 0, 1);
        proteaseFragmentationContainer.addComponent(enzymeList, 0, 2);
        proteaseFragmentationContainer.addComponent(specificityList, 0, 3);

        proteaseFragmentationContainer.addComponent(maxMissCleav, 0, 4);
        proteaseFragmentationContainer.addComponent(fragmentIonTypes, 0, 5);

        Set<String> mzToleranceList = new LinkedHashSet<>();
        mzToleranceList.add("ppm");
        mzToleranceList.add("Da");
        precursorTolerance = new HorizontalLabelTextFieldDropdownList("Precursor m/z Tolerance", 10.0, mzToleranceList, new DoubleRangeValidator("Error ", Double.MIN_VALUE, Double.MAX_VALUE));
        fragmentTolerance = new HorizontalLabelTextFieldDropdownList("Fragment m/z Tolerance", 0.5, mzToleranceList, new DoubleRangeValidator("Error ", Double.MIN_VALUE, Double.MAX_VALUE));
        precursorTolerance.setSelected("ppm");
        precursorTolerance.setTextValue(10);

        fragmentTolerance.setSelected("Da");
        fragmentTolerance.setTextValue(0.5);
        proteaseFragmentationContainer.addComponent(precursorTolerance, 1, 1);
        proteaseFragmentationContainer.addComponent(fragmentTolerance, 1, 2);
        precursorCharge = new HorizontalLabel2TextField("Precursor Charge", 2, 4, new IntegerRangeValidator("Error ", Integer.MIN_VALUE, Integer.MAX_VALUE));
        proteaseFragmentationContainer.addComponent(precursorCharge, 1, 3);
        isotopes = new HorizontalLabel2TextField("Isotopes", 0, 1, new IntegerRangeValidator("Error", Integer.MIN_VALUE, Integer.MAX_VALUE));
        proteaseFragmentationContainer.addComponent(isotopes, 1, 4);

        HorizontalLayout btnsContainer = new HorizontalLayout();
        btnsContainer.setHeight(25, Unit.PIXELS);
        btnsContainer.setWidth(55, Unit.PERCENTAGE);
        proteaseFragmentationContainer.addComponent(btnsContainer, 1, 5);
        proteaseFragmentationContainer.setComponentAlignment(btnsContainer, Alignment.MIDDLE_RIGHT);

        Button saveBtn = new Button("Save");
        saveBtn.setStyleName(ValoTheme.BUTTON_SMALL);
        saveBtn.addStyleName(ValoTheme.BUTTON_TINY);
        saveBtn.setHeight(25, Unit.PIXELS);
        saveBtn.setWidth(100, Unit.PERCENTAGE);
        saveBtn.addClickListener((Button.ClickEvent event) -> {
            if (this.isValidForm()) {
                if (this.isModifiedForm()) {
                    saveSearchingFile(updateSearchingFile(), editMode);
                } else {
                    cancel();
                }
            }
        });
        btnsContainer.addComponent(saveBtn);
        Button closeBtn = new Button("Close");
        closeBtn.setStyleName(ValoTheme.BUTTON_SMALL);
        closeBtn.addStyleName(ValoTheme.BUTTON_TINY);
        closeBtn.setHeight(25, Unit.PIXELS);
        closeBtn.setWidth(100, Unit.PERCENTAGE);
        closeBtn.addClickListener((Button.ClickEvent event) -> {
            cancel();
        });
        btnsContainer.addComponent(closeBtn);

        return proteaseFragmentationContainer;

    }

    public void updateForms(SearchParameters searchParameters, String paramFileId) {
        this.searchParameters = searchParameters;

        if (searchParameters.getFastaFile() != null) {
            editMode = true;
            this.paramFileId = paramFileId;
            String[] fileInfo = searchParameters.getFastaFile().getName().split("__");
            if (fileInfo.length == 4) {
                searchSettingsName.setSelectedValue(fileInfo[1]);
                if (Boolean.valueOf(fileInfo[2])) {
                    databaseOptionList.setSelectedValue("create_decoy");
                } else {
                    databaseOptionList.setSelectedValue(new HashSet<>());
                }
            }
        } else {
            editMode = false;
            this.paramFileId = "New_File";
            searchSettingsName.setSelectedValue(null);
            databaseOptionList.setSelectedValue("create_decoy");

        }

        if (searchParameters.getDigestionPreferences() != null) {
            digestionList.setSelected(searchParameters.getDigestionPreferences().getCleavagePreference().toString());
            enzymeList.setSelected(searchParameters.getDigestionPreferences().getEnzymes().get(0).getName());
            specificityList.setSelected(searchParameters.getDigestionPreferences().getSpecificity(searchParameters.getDigestionPreferences().getEnzymes().get(0).getName()));
            maxMissCleav.setSelectedValue(searchParameters.getDigestionPreferences().getnMissedCleavages(searchParameters.getDigestionPreferences().getEnzymes().get(0).getName()));
            fragmentIonTypes.setSelectedI(ions.get(searchParameters.getForwardIons().get(0)));
            fragmentIonTypes.setSelectedII(ions.get(searchParameters.getRewindIons().get(0)));
            precursorTolerance.setTextValue(searchParameters.getPrecursorAccuracy());
            precursorTolerance.setSelected(searchParameters.getPrecursorAccuracyType().toString());
            fragmentTolerance.setTextValue(searchParameters.getFragmentIonAccuracy());
            fragmentTolerance.setSelected(searchParameters.getFragmentAccuracyType().toString());
            precursorCharge.setFirstSelectedValue(searchParameters.getMinChargeSearched().value);
            precursorCharge.setSecondSelectedValue(searchParameters.getMaxChargeSearched().value);
            isotopes.setFirstSelectedValue(searchParameters.getMinIsotopicCorrection());
            isotopes.setSecondSelectedValue(searchParameters.getMaxIsotopicCorrection());

            mostUsedModificationsTable.removeAllItems();
            variableModificationTable.removeAllItems();
            fixedModificationTable.removeAllItems();
            for (Object id : completeModificationItems.keySet()) {
                if (commonModificationIds.contains(id.toString())) {
                    mostUsedModificationsTable.addItem(completeModificationItems.get(id), id);
                }
            }

            ArrayList<String> vm = searchParameters.getPtmSettings().getVariableModifications();
            mostUsedModificationsTable.setValue(vm);
            toVariableModBtn.click();
            ArrayList<String> fm = searchParameters.getPtmSettings().getFixedModifications();
            mostUsedModificationsTable.setValue(fm);
            toFixedModBtn.click();

        } else {
            variableModificationTable.removeAllItems();
            fixedModificationTable.removeAllItems();
            mostUsedModificationsTable.removeAllItems();
            for (Object id : completeModificationItems.keySet()) {
                if (commonModificationIds.contains(id.toString())) {
                    mostUsedModificationsTable.addItem(completeModificationItems.get(id), id);
                }
            }
            enzymeList.setSelected("Trypsin");
            digestionList.setSelected("Enzyme");
            specificityList.setSelected("Specific");
            fragmentIonTypes.setSelectedI("b");
            fragmentIonTypes.setSelectedII("y");
            precursorTolerance.setSelected("ppm");
            fragmentTolerance.setSelected("Da");

        }

        Set<Object> idSet = (Set<Object>) variableModificationTable.getData();
        idSet.addAll(variableModificationTable.getItemIds());
        variableModificationTable.setData(idSet);

        Set<Object> idSet2 = (Set<Object>) fixedModificationTable.getData();
        idSet2.addAll(fixedModificationTable.getItemIds());
        fixedModificationTable.setData(idSet2);

        mostUsedModificationsTable.setCaption("(" + mostUsedModificationsTable.getItemIds().size() + ")");
        allModificationsTable.setCaption("(" + allModificationsTable.getItemIds().size() + ")");

    }

    public boolean isValidForm() {
        return (digestionList.isValid())
                && enzymeList.isValid()
                && specificityList.isValid()
                && maxMissCleav.isValid()
                && fragmentIonTypes.isValid()
                && precursorTolerance.isValid()
                && fragmentTolerance.isValid()
                && precursorCharge.isValid() && isotopes.isValid() && searchSettingsName.isValid() && fastaFileList.isValid();
    }

    public boolean isModifiedForm() {

        Set<Object> idSet = (Set<Object>) variableModificationTable.getData();
        boolean testVarMod = false;
        if (variableModificationTable.getItemIds().size() != idSet.size() || !variableModificationTable.getItemIds().containsAll(idSet)) {
            testVarMod = true;
        }

        Set<Object> idSet2 = (Set<Object>) fixedModificationTable.getData();
        boolean testFixedMod = false;
        if (fixedModificationTable.getItemIds().size() != idSet2.size() || !fixedModificationTable.getItemIds().containsAll(idSet2)) {
            testFixedMod = true;
        }

        return (testFixedMod || testVarMod || digestionList.isModified())
                || enzymeList.isModified()
                || specificityList.isModified()
                || maxMissCleav.isModified()
                || fragmentIonTypes.isModified()
                || precursorTolerance.isModified()
                || fragmentTolerance.isModified()
                || precursorCharge.isModified()
                || isotopes.isModified()
                || searchSettingsName.isModified()
                || fastaFileList.isModified()
                || databaseOptionList.isModified();

    }

    private SearchParameters updateSearchingFile() {

        PtmSettings ptmSettings = new PtmSettings();
        for (Object modificationId : fixedModificationTable.getItemIds()) {
            ptmSettings.addFixedModification(PTM.getPTM(modificationId.toString()));
        }
        for (Object modificationId : variableModificationTable.getItemIds()) {
            ptmSettings.addVariableModification(PTM.getPTM(modificationId.toString()));
        }
        searchParameters.setPtmSettings(ptmSettings);
        DigestionPreferences digPref = new DigestionPreferences();
        ArrayList<Enzyme> enzymes = new ArrayList<>();
        enzymes.add(enzymeFactory.getEnzyme(enzymeList.getSelectedValue()));
        digPref.setEnzymes(enzymes);
        digPref.setSpecificity(enzymeList.getSelectedValue(), DigestionPreferences.Specificity.valueOf(specificityList.getSelectedValue().toLowerCase()));
        digPref.setnMissedCleavages(enzymeList.getSelectedValue(), Integer.valueOf(maxMissCleav.getSelectedValue()));
        digPref.setCleavagePreference(DigestionPreferences.CleavagePreference.valueOf(digestionList.getSelectedValue().toLowerCase().replace("uns", "unS").replace("le p", "leP")));
        searchParameters.setDigestionPreferences(digPref);
        ArrayList<Integer> forwardIonsv = new ArrayList<>();
        forwardIonsv.add(ions.indexOf(fragmentIonTypes.getFirstSelectedValue()));
        searchParameters.setForwardIons(forwardIonsv);
        ArrayList<Integer> rewindIonsv = new ArrayList<>();
        rewindIonsv.add(ions.indexOf(fragmentIonTypes.getSecondSelectedValue()));
        searchParameters.setRewindIons(rewindIonsv);
        searchParameters.setPrecursorAccuracy(Double.valueOf(precursorTolerance.getFirstSelectedValue()));
        searchParameters.setPrecursorAccuracyType(SearchParameters.MassAccuracyType.valueOf(precursorTolerance.getSecondSelectedValue().toUpperCase()));
        searchParameters.setFragmentIonAccuracy(Double.valueOf(fragmentTolerance.getFirstSelectedValue()));
        searchParameters.setFragmentAccuracyType(SearchParameters.MassAccuracyType.valueOf(fragmentTolerance.getSecondSelectedValue().toUpperCase()));

        searchParameters.setMinChargeSearched(new Charge(Charge.PLUS, Integer.valueOf(precursorCharge.getFirstSelectedValue())));
        searchParameters.setMaxChargeSearched(new Charge(Charge.PLUS, Integer.valueOf(precursorCharge.getSecondSelectedValue())));

        searchParameters.setMinIsotopicCorrection(Integer.valueOf(isotopes.getFirstSelectedValue()));
        searchParameters.setMaxIsotopicCorrection(Integer.valueOf(isotopes.getSecondSelectedValue()));
        searchParameters.setFastaFile(new File(fastaFileList.getSelectedValue() + "__" + searchSettingsName.getSelectedValue() + "__" + ("[create_decoy]".equalsIgnoreCase(databaseOptionList.getSelectedValue() + "")) + "__" + paramFileId));
        return searchParameters;

    }

    public String getSettingsName() {
        return searchSettingsName.getSelectedValue();
    }

    public SearchParameters getSearchParameters() {
        return searchParameters;
    }

    public String getFastaFileName(String fastaFileID) {
        if (fastaFileIdToNameMap != null && fastaFileIdToNameMap.containsKey(fastaFileID)) {
            return fastaFileIdToNameMap.get(fastaFileID);
        } else {
            return "";
        }
    }

    public abstract void saveSearchingFile(SearchParameters searchParameters, boolean edit);

    public abstract void cancel();

}
