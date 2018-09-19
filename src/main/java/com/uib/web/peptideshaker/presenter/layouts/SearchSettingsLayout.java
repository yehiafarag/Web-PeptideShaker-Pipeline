package com.uib.web.peptideshaker.presenter.layouts;

import com.compomics.util.experiment.biology.Enzyme;
import com.compomics.util.experiment.biology.EnzymeFactory;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.identification.identification_parameters.PtmSettings;
import com.compomics.util.experiment.identification.identification_parameters.SearchParameters;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.preferences.DigestionPreferences;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
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
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
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

//need to be documeneted
/**
 * This class represents the search settings input form layout
 *
 * @author Yehia Farag
 */
public abstract class SearchSettingsLayout extends VerticalLayout {

    /**
     * The enzyme factory.
     */
    private final EnzymeFactory enzymeFactory = EnzymeFactory.getInstance();
    /**
     * Convenience array for ion type selection.
     */
    private final List<String> ions = new ArrayList(Arrays.asList(new String[]{"a", "b", "c", "x", "y", "z"}));
    /**
     * The post translational modifications factory.
     */
    private final PTMFactory PTM = PTMFactory.getInstance();
    /**
     * The common modifications list.
     */
    private final Set<String> commonModificationIds;
    /**
     * The selected fixed modifications table.
     */
    private Table fixedModificationTable;
    /**
     * The selected variable modifications table.
     */
    private Table variableModificationTable;
    /**
     * The full modifications list table.
     */
    private Table allModificationsTable;
    /**
     * The most selected modifications list table.
     */
    private Table mostUsedModificationsTable;
    /**
     * The add to fixed modification table button.
     */
    private Button addToFixedModificationTableBtn;
    /**
     * The add to fixed modification table button.
     */
    private Button addToVariableModificationTableBtn;
    /**
     * The remove from fixed modification table button.
     */
    private Button removeFromVariableModificationTableBtn;
    /**
     * The remove from fixed modification table button.
     */
    private Button removeFromFixedModificationTableBtn;
    /**
     * The modification items that is used for initialise modifications tables.
     */
    private final Map<Object, Object[]> completeModificationItems = new LinkedHashMap<>();
    /**
     * Select FASTA file drop-down list from available FAST files.
     */
    private final DropDownList fastaFileList;
    /**
     * Map Galaxy if for FASTA file to FAST files name.
     */
    private Map<String, String> fastaFileIdToNameMap;
    /**
     * Create decoy database file list.
     */
    private final MultiSelectOptionGroup createDecoyDatabaseOptionList;
    /**
     * Search parameter file name input field.
     */
    private final HorizontalLabelTextField searchParametersFileNameInputField;
    /**
     * Selected parameter file .par file galaxy id.
     */
    private String parameterFileId;
    /**
     * Protein digestion options list.
     */
    private HorizontalLabelDropDounList digestionList;
    /**
     * Protein enzymes options list.
     */
    private HorizontalLabelDropDounList enzymeList;
    /**
     * Protein digestion specificity options list.
     */
    private HorizontalLabelDropDounList specificityList;
    /**
     * Maximum number of miss cleavages input field.
     */
    private HorizontalLabelTextField maxMissCleavages;
    /**
     * Fragment ions types selection (forward and rewind) input selection.
     */
    private HorizontalLabel2DropdownList fragmentIonTypes;
    /**
     * Precursor m/z Tolerance value and type input selection.
     */
    private HorizontalLabelTextFieldDropdownList precursorTolerance;
    /**
     * Fragment m/z Tolerance value and type input selection.
     */
    private HorizontalLabelTextFieldDropdownList fragmentTolerance;
    /**
     * Precursor Charge (minimum to maximum) value input fields.
     */
    private HorizontalLabel2TextField precursorCharge;
    /**
     * Isotopes (minimum to maximum) value input fields.
     */
    private HorizontalLabel2TextField isotopes;
    /**
     * Search Parameters object that is used to initialise parameter file
     * (.par).
     */
    private SearchParameters searchParameters;
    /**
     * Search Parameters file (.par) is new or modified.
     */
    private boolean isNew = true;
    /**
     * Coordinate view of sub panels in mobile and small screen mode.
     */
    private final LayoutEvents.LayoutClickListener viewCoordinatorListener;    
   /**
     * Modification container layout.
     */
    private final HorizontalLayout modificationContainer;
    /**
     *Protease fragmentation container layout.
     */
    private final   GridLayout proteaseFragmentationContainer;

    /**
     * Constructor to initialise the main setting parameters.
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
        advancedSearchOption.setContent(initAdvancedSearchOption());
        advancedSearchOption.setDescription("Not supported yet!");
        advancedSearchOption.setSizeFull();
        advancedSearchOption.addStyleName("centerwindow");
        titleLayout.addComponent(advancedSearchOption);
        titleLayout.setComponentAlignment(advancedSearchOption, Alignment.MIDDLE_LEFT);
        titleLayout.setExpandRatio(advancedSearchOption, 80);
        advancedSearchOption.setEnabled(false);

        Button closeIconBtn = new Button("Close");
        closeIconBtn.setIcon(VaadinIcons.CLOSE_SMALL, "Close window");
        closeIconBtn.setStyleName(ValoTheme.BUTTON_SMALL);
        closeIconBtn.addStyleName(ValoTheme.BUTTON_TINY);
        closeIconBtn.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        closeIconBtn.addStyleName("centerbackground");
        closeIconBtn.setHeight(25, Unit.PIXELS);
        closeIconBtn.setWidth(25, Unit.PIXELS);

        closeIconBtn.addClickListener((Button.ClickEvent event) -> {
            cancel();
        });
        titleLayout.addComponent(closeIconBtn);
        titleLayout.setComponentAlignment(closeIconBtn, Alignment.TOP_RIGHT);

        VerticalLayout upperPanel = new VerticalLayout();
        upperPanel.setWidth(100, Unit.PERCENTAGE);
        upperPanel.addStyleName("subpanelframe");
        SearchSettingsLayout.this.addComponent(upperPanel);

        searchParametersFileNameInputField = new HorizontalLabelTextField("<b>Search Settings Name</b>", "New Searching Settings Name", null);
        searchParametersFileNameInputField.setWidth(60, Unit.PERCENTAGE);
        searchParametersFileNameInputField.setRequired(true);
        upperPanel.addComponent(searchParametersFileNameInputField);

        HorizontalLayout protDatabaseContainer = new HorizontalLayout();
        protDatabaseContainer.setWidthUndefined();
        protDatabaseContainer.setHeight(60, Unit.PIXELS);
        upperPanel.addComponent(protDatabaseContainer);
        fastaFileList = new DropDownList("Protein Database (FASTA)");
        protDatabaseContainer.addComponent(fastaFileList);
        fastaFileList.setRequired(true, "Select FASTA file");
        fastaFileList.addStyleName("paddingleft-20");
        createDecoyDatabaseOptionList = new MultiSelectOptionGroup(null, false);

        protDatabaseContainer.addComponent(createDecoyDatabaseOptionList);
        protDatabaseContainer.setComponentAlignment(createDecoyDatabaseOptionList, Alignment.BOTTOM_LEFT);
        Map<String, String> paramMap = new LinkedHashMap<>();
        paramMap.put("create_decoy", "Add Decoy Sequences");

        createDecoyDatabaseOptionList.updateList(paramMap);
        createDecoyDatabaseOptionList.setSelectedValue("create_decoy");
        createDecoyDatabaseOptionList.setViewList(true);

        modificationContainer = inititModificationLayout();
        modificationContainer.addStyleName("subpanelframe");
        SearchSettingsLayout.this.addComponent(modificationContainer);

        proteaseFragmentationContainer = inititProteaseFragmentationLayout();
        proteaseFragmentationContainer.addStyleName("subpanelframe");
        SearchSettingsLayout.this.addComponent(proteaseFragmentationContainer);

        HorizontalLayout actionButtonsLayout = new HorizontalLayout();
        actionButtonsLayout.setSizeFull();
        actionButtonsLayout.addStyleName("subpanelframe");
        SearchSettingsLayout.this.addComponent(actionButtonsLayout);

        HorizontalLayout btnsContainer = new HorizontalLayout();
        btnsContainer.setHeight(25, Unit.PIXELS);
        btnsContainer.setWidth(55, Unit.PERCENTAGE);
        actionButtonsLayout.addComponent(btnsContainer);
        actionButtonsLayout.setComponentAlignment(btnsContainer, Alignment.MIDDLE_CENTER);

        Button saveBtn = new Button("Save");
        saveBtn.setStyleName(ValoTheme.BUTTON_SMALL);
        saveBtn.addStyleName(ValoTheme.BUTTON_TINY);
        saveBtn.setHeight(25, Unit.PIXELS);
        saveBtn.setWidth(100, Unit.PERCENTAGE);
        saveBtn.addClickListener((Button.ClickEvent event) -> {
            if (this.isValidForm()) {
                if (this.isModifiedForm()) {
                    saveSearchingFile(updateSearchingFile(), isNew);
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

        this.viewCoordinatorListener = (LayoutEvents.LayoutClickEvent event) -> {
            Component comp = event.getClickedComponent();
            if ((comp instanceof Label)) {
                Label modificationLabel = (Label) ((VerticalLayout) modificationContainer.getComponent(0)).getComponent(0);
                Label protFragLabel = (Label) (proteaseFragmentationContainer.getComponent(0, 0));
                Label l = (Label) comp;
                if (l.getData() != null) {
                    Integer i = new Integer(l.getData().toString());
                    if (i == 1) { //action on top label
                        if (l.getValue().contains(VaadinIcons.ANGLE_DOUBLE_DOWN.getHtml())) { //hide top and show bottom
                            modificationLabel.setValue(modificationLabel.getValue().replace(VaadinIcons.ANGLE_DOUBLE_DOWN.getHtml(), VaadinIcons.ANGLE_DOUBLE_RIGHT.getHtml()));
                            protFragLabel.setValue(protFragLabel.getValue().replace(VaadinIcons.ANGLE_DOUBLE_RIGHT.getHtml(), VaadinIcons.ANGLE_DOUBLE_DOWN.getHtml()));
                            modificationContainer.addStyleName("minimizelayout");
                            proteaseFragmentationContainer.removeStyleName("minimizelayout");
                        } else { //show top and hide bottom
                            protFragLabel.setValue(protFragLabel.getValue().replace(VaadinIcons.ANGLE_DOUBLE_DOWN.getHtml(), VaadinIcons.ANGLE_DOUBLE_RIGHT.getHtml()));
                            modificationLabel.setValue(modificationLabel.getValue().replace(VaadinIcons.ANGLE_DOUBLE_RIGHT.getHtml(), VaadinIcons.ANGLE_DOUBLE_DOWN.getHtml()));
                            modificationContainer.removeStyleName("minimizelayout");
                            proteaseFragmentationContainer.addStyleName("minimizelayout");
                        }
                    } else {//action on bottom label                        
                        if (l.getValue().contains(VaadinIcons.ANGLE_DOUBLE_DOWN.getHtml())) { //hide bottom and show top
                            protFragLabel.setValue(protFragLabel.getValue().replace(VaadinIcons.ANGLE_DOUBLE_DOWN.getHtml(), VaadinIcons.ANGLE_DOUBLE_RIGHT.getHtml()));
                            modificationLabel.setValue(modificationLabel.getValue().replace(VaadinIcons.ANGLE_DOUBLE_RIGHT.getHtml(), VaadinIcons.ANGLE_DOUBLE_DOWN.getHtml()));
                            modificationContainer.removeStyleName("minimizelayout");
                            proteaseFragmentationContainer.addStyleName("minimizelayout");
                        } else { //show bottom and hide top
                            modificationLabel.setValue(modificationLabel.getValue().replace(VaadinIcons.ANGLE_DOUBLE_DOWN.getHtml(), VaadinIcons.ANGLE_DOUBLE_RIGHT.getHtml()));
                            protFragLabel.setValue(protFragLabel.getValue().replace(VaadinIcons.ANGLE_DOUBLE_RIGHT.getHtml(), VaadinIcons.ANGLE_DOUBLE_DOWN.getHtml()));
                            modificationContainer.addStyleName("minimizelayout");
                            proteaseFragmentationContainer.removeStyleName("minimizelayout");
                        }
                    }
                }
            }
        };

    }

    /**
     * Initialise the layout for search advanced setting
     *
     * @todo: to be implemented
     * @return initialised advanced search setting layout
     */
    private VerticalLayout initAdvancedSearchOption() {
        return new VerticalLayout();

    }

    /**
     * Initialise the modifications layout
     *
     * @return initialised modification layout
     */
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

        Label modificationLabel = new Label(VaadinIcons.ANGLE_DOUBLE_DOWN.getHtml() + "  Modifications", ContentMode.HTML);
        modificationLabel.setSizeFull();
        leftSideLayout.addComponent(modificationLabel);
        leftSideLayout.setExpandRatio(modificationLabel, 4);
        modificationLabel.setData("1");

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

        addToFixedModificationTableBtn = new Button(VaadinIcons.ARROW_LEFT);
        addToFixedModificationTableBtn.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        sideTopButtons.addComponent(addToFixedModificationTableBtn);
        sideTopButtons.setComponentAlignment(addToFixedModificationTableBtn, Alignment.BOTTOM_CENTER);

        removeFromFixedModificationTableBtn = new Button(VaadinIcons.ARROW_RIGHT);
        removeFromFixedModificationTableBtn.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        sideTopButtons.addComponent(removeFromFixedModificationTableBtn);
        sideTopButtons.setComponentAlignment(removeFromFixedModificationTableBtn, Alignment.MIDDLE_CENTER);

        VerticalLayout sideBottomButtons = new VerticalLayout();
        middleSideLayout.addComponent(sideBottomButtons);
        middleSideLayout.setComponentAlignment(sideBottomButtons, Alignment.BOTTOM_CENTER);
        middleSideLayout.setExpandRatio(sideBottomButtons, 48);

        addToVariableModificationTableBtn = new Button(VaadinIcons.ARROW_LEFT);
        addToVariableModificationTableBtn.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        sideBottomButtons.addComponent(addToVariableModificationTableBtn);
        sideBottomButtons.setComponentAlignment(addToVariableModificationTableBtn, Alignment.MIDDLE_CENTER);

        removeFromVariableModificationTableBtn = new Button(VaadinIcons.ARROW_RIGHT);
        removeFromVariableModificationTableBtn.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        sideBottomButtons.addComponent(removeFromVariableModificationTableBtn);
        sideBottomButtons.setComponentAlignment(removeFromVariableModificationTableBtn, Alignment.MIDDLE_CENTER);

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
        completeModificationItems.keySet().stream().filter((id) -> (commonModificationIds.contains(id.toString()))).forEachOrdered((id) -> {
            mostUsedModificationsTable.addItem(completeModificationItems.get(id), id);
        });
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
                completeModificationItems.keySet().stream().filter((id) -> !(fixedModificationTable.containsId(id) || variableModificationTable.containsId(id))).forEachOrdered((id) -> {
                    allModificationsTable.addItem(completeModificationItems.get(id), id);
                });
                allModificationsTable.setVisible(true);
                mostUsedModificationsTable.setVisible(false);

            } else {
                completeModificationItems.keySet().stream().filter((id) -> !(fixedModificationTable.containsId(id) || variableModificationTable.containsId(id))).filter((id) -> (commonModificationIds.contains(id.toString()))).forEachOrdered((id) -> {
                    mostUsedModificationsTable.addItem(completeModificationItems.get(id), id);
                });

                allModificationsTable.setVisible(false);
                mostUsedModificationsTable.setVisible(true);

            }
            allModificationsTable.sort(new Object[]{"name"}, new boolean[]{true});
            mostUsedModificationsTable.sort(new Object[]{"name"}, new boolean[]{true});
            mostUsedModificationsTable.setCaption("(" + mostUsedModificationsTable.getItemIds().size() + ")");
            allModificationsTable.setCaption("(" + allModificationsTable.getItemIds().size() + ")");
        });
        addToFixedModificationTableBtn.addClickListener((Button.ClickEvent event) -> {
            Table selectionTable;
            if (allModificationsTable.isVisible()) {
                selectionTable = allModificationsTable;
            } else {
                selectionTable = mostUsedModificationsTable;
            }
            Set<Object> selection = ((Set<Object>) selectionTable.getValue());
            selection.stream().map((id) -> {
                selectionTable.removeItem(id);
                return id;
            }).forEachOrdered((id) -> {
                fixedModificationTable.addItem(completeModificationItems.get(id), id);
            });
            fixedModificationTable.sort(new Object[]{"name"}, new boolean[]{true});
            fixedModificationTable.setCaption("Fixed Modifications (" + fixedModificationTable.getItemIds().size() + ")");
            selectionTable.setCaption("(" + selectionTable.getItemIds().size() + ")");

        });
        removeFromFixedModificationTableBtn.addClickListener((Button.ClickEvent event) -> {
            Table selectionTable;
            if (allModificationsTable.isVisible()) {
                selectionTable = allModificationsTable;
            } else {
                selectionTable = mostUsedModificationsTable;
            }
            Set<Object> selection = ((Set<Object>) fixedModificationTable.getValue());
            selection.stream().map((id) -> {
                fixedModificationTable.removeItem(id);
                return id;
            }).forEachOrdered((id) -> {
                selectionTable.addItem(completeModificationItems.get(id), id);
            });
            selectionTable.sort(new Object[]{"name"}, new boolean[]{true});
            fixedModificationTable.setCaption("Fixed Modifications (" + fixedModificationTable.getItemIds().size() + ")");
            selectionTable.setCaption("(" + selectionTable.getItemIds().size() + ")");

        });
        addToVariableModificationTableBtn.addClickListener((Button.ClickEvent event) -> {
            Table selectionTable;
            if (allModificationsTable.isVisible()) {
                selectionTable = allModificationsTable;
            } else {
                selectionTable = mostUsedModificationsTable;
            }
            Set<Object> selection = ((Set<Object>) selectionTable.getValue());
            selection.stream().map((id) -> {
                selectionTable.removeItem(id);
                return id;
            }).forEachOrdered((id) -> {
                variableModificationTable.addItem(completeModificationItems.get(id), id);
            });
            variableModificationTable.sort(new Object[]{"name"}, new boolean[]{true});
            variableModificationTable.setCaption("Variable Modifications (" + variableModificationTable.getItemIds().size() + ")");
            selectionTable.setCaption("(" + selectionTable.getItemIds().size() + ")");

        });
        removeFromVariableModificationTableBtn.addClickListener((Button.ClickEvent event) -> {
            Table selectionTable;
            if (allModificationsTable.isVisible()) {
                selectionTable = allModificationsTable;
            } else {
                selectionTable = mostUsedModificationsTable;
            }
            Set<Object> selection = ((Set<Object>) variableModificationTable.getValue());
            selection.stream().map((id) -> {
                variableModificationTable.removeItem(id);
                return id;
            }).forEachOrdered((id) -> {
                selectionTable.addItem(completeModificationItems.get(id), id);
            });
            selectionTable.sort(new Object[]{"name"}, new boolean[]{true});
            variableModificationTable.setCaption("Variable Modifications (" + variableModificationTable.getItemIds().size() + ")");
            selectionTable.setCaption("(" + selectionTable.getItemIds().size() + ")");

        });
        mostUsedModificationsTable.setVisible(true);

        return modificationContainer;

    }

    /**
     * Initialise the modifications tables
     *
     * @return initialised modification table
     */
    private Table initModificationTable(String cap) {
        Table modificationsTable = new Table(cap) {
            DecimalFormat df =  new DecimalFormat("0.00E00");//new DecimalFormat("#.##");

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
        modificationsTable
                .addContainerProperty("color", ColorLabel.class,
                        null, "", null, Table.Align.CENTER);
        modificationsTable
                .addContainerProperty("name", String.class,
                        null, "Name", null, Table.Align.LEFT);
        modificationsTable
                .addContainerProperty("mass", SparkLine.class,
                        null, "Mass", null, Table.Align.LEFT);
        modificationsTable.setColumnExpandRatio("color", 10);
        modificationsTable.setColumnExpandRatio("name", 55);
        modificationsTable.setColumnExpandRatio("mass", 35);
        modificationsTable.sort(new Object[]{"name"}, new boolean[]{true});
        modificationsTable.setSortEnabled(false);
        modificationsTable.setItemDescriptionGenerator((Component source, Object itemId, Object propertyId) -> PTM.getPTM(itemId.toString()).getHtmlTooltip());
        return modificationsTable;
    }

    /**
     * Initialise the Protease and Fragmentation layout
     *
     * @return initialised layout
     */
    private GridLayout inititProteaseFragmentationLayout() {
        GridLayout proteaseFragmentationContainer = new GridLayout(2, 6);
        proteaseFragmentationContainer.setStyleName("panelframe");
        proteaseFragmentationContainer.setColumnExpandRatio(0, 55);
        proteaseFragmentationContainer.setColumnExpandRatio(1, 45);
        proteaseFragmentationContainer.setMargin(new MarginInfo(false, false, true, false));
        proteaseFragmentationContainer.setWidth(700, Unit.PIXELS);
        proteaseFragmentationContainer.setHeight(205, Unit.PIXELS);
        proteaseFragmentationContainer.setSpacing(true);

        Label label = new Label(VaadinIcons.ANGLE_DOUBLE_DOWN.getHtml() + "  Protease & Fragmentation", ContentMode.HTML);
        label.setSizeFull();
        label.setData(2);
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
                maxMissCleavages.setEnabled(true);
            } else {
                maxMissCleavages.setEnabled(false);
                enzymeList.setEnabled(false);
                specificityList.setEnabled(false);
            }

        });

        enzymeList = new HorizontalLabelDropDounList("Enzyme");
        Set<String> enzList = new LinkedHashSet<>();
        List<Enzyme> enzObjList = enzymeFactory.getEnzymes();
        enzObjList.forEach((enz) -> {
            enzList.add(enz.getName());
        });
        enzymeList.updateData(enzList);
        Set<String> specificityOptionList = new LinkedHashSet<>();
        specificityOptionList.add("Specific");
        specificityOptionList.add("Semi-Specific");
        specificityOptionList.add("N-term Specific");
        specificityOptionList.add("C-term Specific");

        specificityList = new HorizontalLabelDropDounList("Specificity");
        specificityList.updateData(specificityOptionList);
        maxMissCleavages = new HorizontalLabelTextField("Max Missed Cleavages", 2, new IntegerRangeValidator("Error", Integer.MIN_VALUE, Integer.MAX_VALUE));

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

        proteaseFragmentationContainer.addComponent(maxMissCleavages, 0, 4);
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

        return proteaseFragmentationContainer;

    }

    /**
     * Get selected FASTA file galaxy id
     *
     * @return FASTA file Galaxy id
     */
    public String getFastaFileId() {
        return fastaFileList.getSelectedValue();
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
        fastaFileList.updateList(fastaFileIdToNameMap);
        fastaFileList.setSelected(selectedId);

    }

    /**
     * Update search input forms based on user selection (add/edit) from search
     * files drop-down list
     *
     * @param searchParameters search parameter object from selected parameter
     * file
     * @param paramFileId Parameter file galaxy id
     */
    public void updateForms(SearchParameters searchParameters, String paramFileId) {
        this.searchParameters = searchParameters;
        if (searchParameters.getFastaFile() != null) {
            isNew = false;
            this.parameterFileId = paramFileId;
            String[] fileInfo = searchParameters.getFastaFile().getName().split("__");
            if (fileInfo.length == 4) {
                searchParametersFileNameInputField.setSelectedValue(fileInfo[1]);
                if (Boolean.valueOf(fileInfo[2])) {
                    createDecoyDatabaseOptionList.setSelectedValue("create_decoy");
                } else {
                    createDecoyDatabaseOptionList.setSelectedValue(new HashSet<>());
                }
            }
        } else {
            isNew = true;
            this.parameterFileId = "New_File";
            searchParametersFileNameInputField.setSelectedValue(null);
            createDecoyDatabaseOptionList.setSelectedValue("create_decoy");

        }

        if (searchParameters.getDigestionPreferences() != null) {
            digestionList.setSelected(searchParameters.getDigestionPreferences().getCleavagePreference().toString());
            enzymeList.setSelected(searchParameters.getDigestionPreferences().getEnzymes().get(0).getName());
            specificityList.setSelected(searchParameters.getDigestionPreferences().getSpecificity(searchParameters.getDigestionPreferences().getEnzymes().get(0).getName()));
            maxMissCleavages.setSelectedValue(searchParameters.getDigestionPreferences().getnMissedCleavages(searchParameters.getDigestionPreferences().getEnzymes().get(0).getName()));
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
            completeModificationItems.keySet().stream().filter((id) -> (commonModificationIds.contains(id.toString()))).forEachOrdered((id) -> {
                mostUsedModificationsTable.addItem(completeModificationItems.get(id), id);
            });

            ArrayList<String> vm = searchParameters.getPtmSettings().getVariableModifications();
            mostUsedModificationsTable.setValue(vm);
            addToVariableModificationTableBtn.click();
            ArrayList<String> fm = searchParameters.getPtmSettings().getFixedModifications();
            mostUsedModificationsTable.setValue(fm);
            addToFixedModificationTableBtn.click();

        } else {
            variableModificationTable.removeAllItems();
            fixedModificationTable.removeAllItems();
            mostUsedModificationsTable.removeAllItems();
            completeModificationItems.keySet().stream().filter((id) -> (commonModificationIds.contains(id.toString()))).forEachOrdered((id) -> {
                mostUsedModificationsTable.addItem(completeModificationItems.get(id), id);
            });
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
        
        if (((boolean) VaadinSession.getCurrent().getAttribute("smallscreenstyle"))) {
            modificationContainer.setHeight(270, Unit.PIXELS);
            modificationContainer.addLayoutClickListener(viewCoordinatorListener);
            proteaseFragmentationContainer.addLayoutClickListener(viewCoordinatorListener);
            Label modificationLabel = (Label) ((VerticalLayout) modificationContainer.getComponent(0)).getComponent(0);
            Label protFragLabel = (Label) (proteaseFragmentationContainer.getComponent(0, 0));
            protFragLabel.setValue(protFragLabel.getValue().replace(VaadinIcons.ANGLE_DOUBLE_DOWN.getHtml(), VaadinIcons.ANGLE_DOUBLE_RIGHT.getHtml()));
            modificationLabel.setValue(modificationLabel.getValue().replace(VaadinIcons.ANGLE_DOUBLE_RIGHT.getHtml(), VaadinIcons.ANGLE_DOUBLE_DOWN.getHtml()));
            modificationContainer.removeStyleName("minimizelayout");
            proteaseFragmentationContainer.addStyleName("minimizelayout");
        }

    }

    /**
     * Validate input forms before save the results on Galaxy server
     *
     * @return is valid data user inputs
     */
    public boolean isValidForm() {
        return (digestionList.isValid())
                && enzymeList.isValid()
                && specificityList.isValid()
                && maxMissCleavages.isValid()
                && fragmentIonTypes.isValid()
                && precursorTolerance.isValid()
                && fragmentTolerance.isValid()
                && precursorCharge.isValid() && isotopes.isValid() && searchParametersFileNameInputField.isValid() && fastaFileList.isValid();
    }

    /**
     * The search forms is new or just modified forms for pre exist parameter
     * file
     *
     * @return is file new or modified
     */
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
                || maxMissCleavages.isModified()
                || fragmentIonTypes.isModified()
                || precursorTolerance.isModified()
                || fragmentTolerance.isModified()
                || precursorCharge.isModified()
                || isotopes.isModified()
                || searchParametersFileNameInputField.isModified()
                || fastaFileList.isModified()
                || createDecoyDatabaseOptionList.isModified();

    }

    /**
     * Updating search parameters object from user input selection before save
     * it into .par file and store it on Galaxy server
     *
     * @return updates search parameters object that will be use for creating
     * .par file
     */
    private SearchParameters updateSearchingFile() {

        PtmSettings ptmSettings = new PtmSettings();
        fixedModificationTable.getItemIds().forEach((modificationId) -> {
            ptmSettings.addFixedModification(PTM.getPTM(modificationId.toString()));
        });
        variableModificationTable.getItemIds().forEach((modificationId) -> {
            ptmSettings.addVariableModification(PTM.getPTM(modificationId.toString()));
        });
        searchParameters.setPtmSettings(ptmSettings);
        DigestionPreferences digPref = new DigestionPreferences();
        ArrayList<Enzyme> enzymes = new ArrayList<>();
        enzymes.add(enzymeFactory.getEnzyme(enzymeList.getSelectedValue()));
        digPref.setEnzymes(enzymes);
        digPref.setSpecificity(enzymeList.getSelectedValue(), DigestionPreferences.Specificity.valueOf(specificityList.getSelectedValue().toLowerCase()));
        digPref.setnMissedCleavages(enzymeList.getSelectedValue(), Integer.valueOf(maxMissCleavages.getSelectedValue()));
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
        searchParameters.setFastaFile(new File(fastaFileList.getSelectedValue() + "__" + searchParametersFileNameInputField.getSelectedValue() + "__" + ("[create_decoy]".equalsIgnoreCase(createDecoyDatabaseOptionList.getSelectedValue() + "")) + "__" + parameterFileId));
        return searchParameters;

    }

    /**
     * Get updated Search parameters object
     *
     * @return updated search parameters object
     */
    public SearchParameters getSearchParameters() {
        return searchParameters;
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
     * Save user search input parameters into .par file and store it on Galaxy
     * server
     *
     * @param searchParameters updated search parameters object that is used to
     * create .par file
     * @param isNew store the search parameters object in new file or over write
     * existing .par file
     */
    public abstract void saveSearchingFile(SearchParameters searchParameters, boolean isNew);

    /**
     * cancel add/edit search parameters input process*
     */
    public abstract void cancel();

}
