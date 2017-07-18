package com.uib.web.peptideshaker.presenter.components.peptideshakerview;

import com.compomics.util.experiment.biology.PTMFactory;
import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.presenter.core.form.ColorLabel;
import com.uib.web.peptideshaker.presenter.core.form.Horizontal2Label;
import com.uib.web.peptideshaker.presenter.core.form.SparkLine;
import com.vaadin.data.Property;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * This class represents the search settings form layout
 *
 * @author Yehia Farag
 */
public abstract class DatasetOverviewLayout extends VerticalLayout {

    /**
     * Convenience array for forward ion type selection.
     */
    /**
     * The post translational modifications factory.
     */
    private final PTMFactory PTM = PTMFactory.getInstance();

    private Table fixedModificationTable;

    public String getEnzyme() {
        return enzyme;
    }
    private Table variableModificationTable;
    
    private String enzyme = "";

    /**
     * Constructor to initialize the main setting parameters
     */
    public DatasetOverviewLayout(PeptideShakerVisualizationDataset dataset) {
        DatasetOverviewLayout.this.setMargin(true);
        DatasetOverviewLayout.this.setSizeUndefined();
        DatasetOverviewLayout.this.setSpacing(true);
        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setSizeFull();
        titleLayout.addStyleName("subpanelframe");
        if (dataset.getParameters() == null) {
            return;
        }
        DatasetOverviewLayout.this.addComponent(titleLayout);
        Label projectNameLabel = new Label(dataset.getName());
        projectNameLabel.addStyleName(ValoTheme.LABEL_BOLD);
        titleLayout.addComponent(projectNameLabel);
        titleLayout.setComponentAlignment(projectNameLabel, Alignment.TOP_CENTER);

        Button closeBtn = new Button("Close");
        closeBtn.setIcon(VaadinIcons.CLOSE_SMALL, "Close window");
        closeBtn.setStyleName(ValoTheme.BUTTON_SMALL);
        closeBtn.addStyleName(ValoTheme.BUTTON_TINY);
        closeBtn.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        closeBtn.addStyleName("centerbackground");
        closeBtn.setHeight(25, Unit.PIXELS);
        closeBtn.setWidth(25, Unit.PIXELS);

        closeBtn.addClickListener((Button.ClickEvent event) -> {
            close();
        });
        titleLayout.addComponent(closeBtn);
        titleLayout.setComponentAlignment(closeBtn, Alignment.TOP_RIGHT);
        
        HorizontalLayout overviewPanel = new HorizontalLayout();
        overviewPanel.setSizeFull();
        overviewPanel.addStyleName("subpanelframe");
        DatasetOverviewLayout.this.addComponent(overviewPanel);
        HorizontalLayout topUpper = new HorizontalLayout();
        topUpper.setSizeFull();
        topUpper.setSpacing(true);
        overviewPanel.addComponent(topUpper);

        Horizontal2Label proteinsNumber = new Horizontal2Label("#Proteins:", dataset.getProteinsNumber() + "");//new Label("#Proteins: <font style='text-align:right;'>" + dataset.getProteinsNumber() + "</font>");
        topUpper.addComponent(proteinsNumber);
        topUpper.setComponentAlignment(proteinsNumber, Alignment.MIDDLE_CENTER);
        Horizontal2Label peptidesNumber = new Horizontal2Label("#Peptides:", dataset.getPeptidesNumber() + "");//new Label("#Peptides: <font style='text-align:right;'>" + dataset.getPeptidesNumber()+ "</font>");

        topUpper.addComponent(peptidesNumber);
        topUpper.setComponentAlignment(peptidesNumber, Alignment.MIDDLE_CENTER);

        Horizontal2Label PSMNumber = new Horizontal2Label("#PSM:", dataset.getPsmNumber() + "");//new Label("#Peptides: <font style='text-align:right;'>" + dataset.getPeptidesNumber()+ "</font>");

        topUpper.addComponent(PSMNumber);
        topUpper.setComponentAlignment(PSMNumber, Alignment.MIDDLE_CENTER);

        VerticalLayout upperPanel = new VerticalLayout();
        upperPanel.setWidth(100, Unit.PERCENTAGE);
        upperPanel.addStyleName("subpanelframe");
        DatasetOverviewLayout.this.addComponent(upperPanel);

        Horizontal2Label fastaFileLabel = new Horizontal2Label("Protein Database (FASTA) :", dataset.getFastaFileName());
        upperPanel.addComponent(fastaFileLabel);

        if (dataset.getParameters().get("protein_database_options").toString().split("\"create_decoy\": ")[1].startsWith("\"true\"")) {
            Horizontal2Label addDecoy = new Horizontal2Label("Decoy Sequences :", "Added");
            upperPanel.addComponent(addDecoy);

        }
        int index = 1;
        for (String mgf : dataset.getMgfFiles().values()) {
            Horizontal2Label mgfFile = new Horizontal2Label("MGF File " + index + " :", mgf);
            upperPanel.addComponent(mgfFile);
            index++;
        }

        Map<String, String> paramMap = new LinkedHashMap<>();
        paramMap.put("create_decoy", "Add Decoy Sequences");
        try {
            String fixedMod = (jsonToMap(dataset.getParameters().get("protein_modification_options").toString())).get("fixed_modifications").replace("[", "").replace("]", "").replace("\"", "").replace("\n", "");
            String varMod = (jsonToMap(dataset.getParameters().get("protein_modification_options").toString())).get("variable_modifications").replace("[", "").replace("]", "");
            HorizontalLayout modificationContainer = inititModificationLayout(fixedMod.split(","), varMod.split(","));
            modificationContainer.addStyleName("subpanelframe");
            if (!variableModificationTable.getItemIds().isEmpty() || !fixedModificationTable.getItemIds().isEmpty()) {
                DatasetOverviewLayout.this.addComponent(modificationContainer);
            }
            variableModificationTable.setVisible(!variableModificationTable.getItemIds().isEmpty());
            fixedModificationTable.setVisible(!fixedModificationTable.getItemIds().isEmpty());
        } catch (JSONException ex) {

        }

        GridLayout proteaseFragmentationContainer = inititProteaseFragmentationLayout(dataset.getParameters());
        proteaseFragmentationContainer.addStyleName("subpanelframe");
        DatasetOverviewLayout.this.addComponent(proteaseFragmentationContainer);

    }

    private HorizontalLayout inititModificationLayout(String[] fixedMod, String[] varMod) {
        HorizontalLayout modificationContainer = new HorizontalLayout();
        modificationContainer.setSpacing(true);
        modificationContainer.setStyleName("panelframe");
        modificationContainer.setSizeFull();
        modificationContainer.setMargin(new MarginInfo(false, false, false, false));
        modificationContainer.setWidth(650, Unit.PIXELS);
        modificationContainer.setHeight(155, Unit.PIXELS);

        VerticalLayout leftSideLayout = new VerticalLayout();
        leftSideLayout.setSizeFull();
        leftSideLayout.setSpacing(true);
        modificationContainer.addComponent(leftSideLayout);
        fixedModificationTable = initModificationTable("Fixed Modifications");
        leftSideLayout.addComponent(fixedModificationTable);

        VerticalLayout rightSideLayout = new VerticalLayout();
        rightSideLayout.setSizeFull();
        rightSideLayout.setSpacing(true);
        modificationContainer.addComponent(rightSideLayout);
        variableModificationTable = initModificationTable("Variable Modifications");
        rightSideLayout.addComponent(variableModificationTable);

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
        for (String mod : fixedMod) {
            if (mod.equalsIgnoreCase("null")) {
                continue;
            }
            ColorLabel color = new ColorLabel(PTM.getColor(mod));
            SparkLine sLine = new SparkLine(PTM.getPTM(mod).getMass(), minMass, maxMass);
            Object[] modificationArr = new Object[]{color, mod, sLine};
            fixedModificationTable.addItem(modificationArr, mod);
        }
        for (String mod : varMod) {
            if (mod.equalsIgnoreCase("null")) {
                continue;
            }
            mod=mod.replace("\"", "");
            ColorLabel color = new ColorLabel(PTM.getColor(mod));
            SparkLine sLine = new SparkLine(PTM.getPTM(mod).getMass(), minMass, maxMass);
            Object[] modificationArr = new Object[]{color, mod, sLine};
            variableModificationTable.addItem(modificationArr, mod);
        }
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
        modificationsTable.setMultiSelect(false);
        modificationsTable.setSelectable(false);
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

    private GridLayout inititProteaseFragmentationLayout(Map<String, Object> parameters) {
        GridLayout proteaseFragmentationContainer = new GridLayout(2, 6);
        proteaseFragmentationContainer.setWidth(100, Unit.PERCENTAGE);
        proteaseFragmentationContainer.setStyleName("panelframe");
        proteaseFragmentationContainer.setColumnExpandRatio(0, 55);
        proteaseFragmentationContainer.setColumnExpandRatio(1, 45);
        proteaseFragmentationContainer.setMargin(new MarginInfo(false, false, false, false));
        proteaseFragmentationContainer.setWidth(650, Unit.PIXELS);
        proteaseFragmentationContainer.setHeight(190, Unit.PIXELS);
        proteaseFragmentationContainer.setSpacing(true);

//        Set<String> digestionOptionList = new LinkedHashSet<>();
//        digestionOptionList.add("Enzyme");
//        digestionOptionList.add("Unspecific");
//        digestionOptionList.add("Whole Protein");
        String digestion = "";        
        String specificity = "";
        Integer maxMissCleavValue = null;
        String ion = "";
        String precursor_ion = "";
        String fragment_tol = "";
        String _charge = "";
        String iso = "";
        try {
            String[] jsonProtein_digest_optionsObjects = jsonToMap(parameters.get("protein_digest_options").toString()).get("digestion").split(",");
            //cleavage
            if (jsonProtein_digest_optionsObjects[0].contains("\"default\"")) {
                digestion = "Enzyme";
                enzyme = "Trypsin";
                specificity = "Specific";
                maxMissCleavValue = Integer.valueOf(jsonProtein_digest_optionsObjects[1].split(":")[1].replace("\"", ""));

            } else if (jsonProtein_digest_optionsObjects[0].contains("\"0\"")) {
                digestion = "Enzyme";
                enzyme = jsonProtein_digest_optionsObjects[2].split(":")[1].replace("\"", "");
                maxMissCleavValue = Integer.valueOf(jsonProtein_digest_optionsObjects[3].split(":")[1].replace("\"", "").replace("}", "").replace("]", ""));
                specificity = "Specific";

            } else if (jsonProtein_digest_optionsObjects[0].contains("\"1\"")) {
                digestion = "Unspecific";
                enzyme = null;
                specificity = null;

            } else if (jsonProtein_digest_optionsObjects[0].contains("\"2\"")) {
                digestion = "Whole Protein";
                enzyme = null;
                specificity = null;

            }
            
            Map<String, String> json_Precursor_options_Objects = jsonToMap(parameters.get("precursor_options").toString());
            ion = json_Precursor_options_Objects.get("forward_ion") + " , " + json_Precursor_options_Objects.get("reverse_ion");
            precursor_ion = json_Precursor_options_Objects.get("precursor_ion_tol") + " " + json_Precursor_options_Objects.get("precursor_ion_tol_units").replace("1", "ppm").replace("2", "Da");
            fragment_tol = json_Precursor_options_Objects.get("fragment_tol") + " Da";
            _charge = json_Precursor_options_Objects.get("min_charge") + " to " + json_Precursor_options_Objects.get("max_charge");
            iso = json_Precursor_options_Objects.get("min_isotope") + " to " + json_Precursor_options_Objects.get("max_isotope");
        } catch (JSONException | NumberFormatException ex) {
            ex.printStackTrace();
        }
        Horizontal2Label digestionList = new Horizontal2Label("Digestion :", digestion);
        Horizontal2Label enzymeList = new Horizontal2Label("Enzyme :", enzyme);
//        specificityOptionList.add("Specific");
//        specificityOptionList.add("Semi-Specific");
//        specificityOptionList.add("N-term Specific");
//        specificityOptionList.add("C-term Specific");
        Horizontal2Label specificityList = new Horizontal2Label("Specificity :", specificity);
        Horizontal2Label maxMissCleav = new Horizontal2Label("Max Missed Cleavages :", maxMissCleavValue);

//        Set<String> ionListI = new LinkedHashSet<>();
//        ionListI.add("a");
//        ionListI.add("b");
//        ionListI.add("c");
//        Set<String> ionListII = new LinkedHashSet<>();
//        ionListII.add("x");
//        ionListII.add("y");
//        ionListII.add("z");
        Horizontal2Label fragmentIonTypes = new Horizontal2Label("Fragment Ion Types :", ion);

        proteaseFragmentationContainer.addComponent(digestionList, 0, 1);
        proteaseFragmentationContainer.addComponent(enzymeList, 0, 2);
        proteaseFragmentationContainer.addComponent(specificityList, 0, 3);

        proteaseFragmentationContainer.addComponent(maxMissCleav, 0, 4);
        proteaseFragmentationContainer.addComponent(fragmentIonTypes, 0, 5);

//        Set<String> mzToleranceList = new LinkedHashSet<>();
//        mzToleranceList.add("ppm");
//        mzToleranceList.add("Da");
        Horizontal2Label precursorTolerance = new Horizontal2Label("Precursor m/z Tolerance :", precursor_ion);
        Horizontal2Label fragmentTolerance = new Horizontal2Label("Fragment m/z Tolerance :", fragment_tol);

        proteaseFragmentationContainer.addComponent(precursorTolerance, 1, 1);
        proteaseFragmentationContainer.addComponent(fragmentTolerance, 1, 2);
        Horizontal2Label precursorCharge = new Horizontal2Label("Precursor Charge :", _charge);
        proteaseFragmentationContainer.addComponent(precursorCharge, 1, 3);
        Horizontal2Label isotopes = new Horizontal2Label("Isotopes :", iso);
        proteaseFragmentationContainer.addComponent(isotopes, 1, 4);

        return proteaseFragmentationContainer;

    }

    public abstract void close();

    private HashMap<String, String> jsonToMap(String t) throws JSONException {

        HashMap<String, String> map = new HashMap<>();
        JSONObject jObject = new JSONObject(t);
        Iterator<?> keys = jObject.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String value = jObject.getString(key);
            map.put(key, value);

        }

        return map;
    }

}
