package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components;

import com.compomics.util.experiment.biology.PTMFactory;
import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.galaxy.dataobjects.ProteinObject;
import com.uib.web.peptideshaker.model.core.ModificationMatrix;
import com.uib.web.peptideshaker.presenter.core.ColorLabel;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.SelectionManager;
import com.uib.web.peptideshaker.presenter.core.SearchableTable;
import com.uib.web.peptideshaker.presenter.core.SparkLineLabel;
import com.uib.web.peptideshaker.presenter.core.TableColumnHeader;
import com.uib.web.peptideshaker.presenter.core.ValidationLabel;
import com.uib.web.peptideshaker.presenter.core.filtercharts.FiltersContainer;
import com.uib.web.peptideshaker.presenter.core.filtercharts.charts.RegistrableFilter;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class represents filtered Table with graph view
 *
 * @author Yehia Farag
 */
public class DatasetVisulizationLevelComponent extends VerticalLayout implements RegistrableFilter {

    private final SearchableTable proteinTableContainer;
    private final FiltersContainer chartFiltersContainer;
    private final Map<String, Integer> inferenceMap;

    private final DecimalFormat df = new DecimalFormat("#.##");

    /**
     * The post translational modifications factory.
     */
    private final PTMFactory PTM = PTMFactory.getInstance();

    public DatasetVisulizationLevelComponent(SelectionManager Selection_Manager) {
        DatasetVisulizationLevelComponent.this.setSizeFull();
        DatasetVisulizationLevelComponent.this.setSpacing(false);
        DatasetVisulizationLevelComponent.this.addStyleName("scrollinsideframe");

        this.chartFiltersContainer = new FiltersContainer(Selection_Manager);
        DatasetVisulizationLevelComponent.this.addComponent(chartFiltersContainer);
        DatasetVisulizationLevelComponent.this.setExpandRatio(chartFiltersContainer, 100f);

        this.inferenceMap = new HashMap<>();
        this.inferenceMap.put("Single Protein", 1);
        this.inferenceMap.put("Related ", 2);
        TableColumnHeader header1 = new TableColumnHeader("index", Integer.class, null, "", null, Table.Align.RIGHT);
        TableColumnHeader header2 = new TableColumnHeader("proteinInference", ColorLabel.class, null, "PI", null, Table.Align.CENTER);
        TableColumnHeader header3 = new TableColumnHeader("Accession", String.class, null, "Accession", null, Table.Align.CENTER);
        TableColumnHeader header4 = new TableColumnHeader("Name", String.class, null, "Description", null, Table.Align.LEFT);
        TableColumnHeader header5 = new TableColumnHeader("protein_group", String.class, null, "Protein Group", null, Table.Align.LEFT);
        TableColumnHeader header6 = new TableColumnHeader("geneName", String.class, null, "Gene Name", null, Table.Align.CENTER);
        TableColumnHeader header7 = new TableColumnHeader("chromosom", String.class, null, "Chr", null, Table.Align.CENTER);
        TableColumnHeader header8 = new TableColumnHeader("possibleCoverage", SparkLineLabel.class, null, "Possible Coverage", null, Table.Align.LEFT);
        TableColumnHeader header9 = new TableColumnHeader("peptides_number", SparkLineLabel.class, null, "#Peptides", null, Table.Align.LEFT);
        TableColumnHeader header10 = new TableColumnHeader("psm_number", SparkLineLabel.class, null, "#PSM", null, Table.Align.LEFT);
        TableColumnHeader header11 = new TableColumnHeader("ms2Quant", SparkLineLabel.class, null, "MS2 Quant", null, Table.Align.LEFT);
        TableColumnHeader header12 = new TableColumnHeader("mwkDa", SparkLineLabel.class, null, "MW (kDa)", null, Table.Align.LEFT);
        TableColumnHeader header13 = new TableColumnHeader("confidence", SparkLineLabel.class, null, "Confidence", null, Table.Align.LEFT);
        TableColumnHeader header14 = new TableColumnHeader("validation", ValidationLabel.class, null, "", null, Table.Align.CENTER);

        TableColumnHeader[] tableHeaders = new TableColumnHeader[]{header1, header2, header3, header4, header5, header6, header7, header8, header9, header10, header11, header12, header13, header14};
        this.proteinTableContainer = new SearchableTable("Proteins", "Accssion or protein name", tableHeaders) {
            private final Set<Comparable> selectedIds = new LinkedHashSet<>();

            @Override
            public void itemSelected(Object itemId) {
                selectedIds.clear();
                selectedIds.add(itemId + "");
                Selection_Manager.setSelection("protein_selection", selectedIds, null, getFilterId());

            }

        };
        DatasetVisulizationLevelComponent.this.addComponent(proteinTableContainer);
        DatasetVisulizationLevelComponent.this.setExpandRatio(proteinTableContainer, 100f);

        Selection_Manager.RegistrDatasetsFilter(DatasetVisulizationLevelComponent.this);
    }

    public void updateData(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {
        Map<String, ProteinObject> proteinTableMap = peptideShakerVisualizationDataset.getProteinsMap();
        Map<Comparable, Object[]> proteinTableData = new LinkedHashMap<>();
        final Table mainTable = proteinTableContainer.getMainTable();
        mainTable.setColumnWidth("index", 50);
        mainTable.setColumnWidth("proteinInference", 37);
        
        mainTable.setColumnWidth("possibleCoverage", 150);
        mainTable.setColumnWidth("peptides_number", 150);
        mainTable.setColumnWidth("psm_number", 150);
        mainTable.setColumnWidth("ms2Quant", 150);
        mainTable.setColumnWidth("mwkDa", 150);
         mainTable.setColumnWidth("chromosom", 50);
        
        mainTable.setColumnWidth("validation", 32);
        mainTable.setColumnWidth("confidence", 150);
        mainTable.setColumnCollapsible("protein_group", true);
        mainTable.setColumnCollapsed("protein_group", true);
        mainTable.setColumnCollapsible("geneName", true);
        mainTable.setColumnCollapsed("geneName", true);
        proteinTableMap.values().forEach((protein) -> {
            ColorLabel piLabel = new ColorLabel(inferenceMap.get(protein.getProteinInference()), protein.getProteinInference());

            Map<String, Number> coverageValues = new LinkedHashMap<>();
            coverageValues.put("greenlayout", (float) protein.getPossibleCoverage() / 100f);
            SparkLineLabel coverageLabel = new SparkLineLabel(df.format(protein.getPossibleCoverage()), coverageValues, protein.getAccession()) {
                @Override
                public void selected(Object itemId) {
                    mainTable.setValue(itemId);
                }
            };
            
            
             Map<String, Number> peptidesNumValues = new LinkedHashMap<>();
            peptidesNumValues.put("greenlayout", (float) protein.getValidatedPeptidesNumber()/ (float)peptideShakerVisualizationDataset.getMaxPeptideNumber());
            SparkLineLabel peptidesNumberLabelLabel = new SparkLineLabel(df.format(protein.getValidatedPeptidesNumber()), peptidesNumValues, protein.getAccession()) {
                @Override
                public void selected(Object itemId) {
                    mainTable.setValue(itemId);
                }
            };
            
            
            Map<String, Number> psmNumValues = new LinkedHashMap<>();
            psmNumValues.put("greenlayout", (float) protein.getValidatedPSMsNumber()/ (float)peptideShakerVisualizationDataset.getMaxPsmNumber());
            SparkLineLabel psmNumberLabelLabel = new SparkLineLabel(df.format(protein.getValidatedPSMsNumber()), psmNumValues, protein.getAccession()) {
                @Override
                public void selected(Object itemId) {
                    mainTable.setValue(itemId);
                }
            };
             Map<String, Number> ms2QuantValues = new LinkedHashMap<>();
            ms2QuantValues.put("greenlayout", (float) protein.getSpectrumCounting()/ (float)peptideShakerVisualizationDataset.getMaxMS2Quant());
            SparkLineLabel ms2QuantLabelLabel = new SparkLineLabel(df.format(protein.getSpectrumCounting()), ms2QuantValues, protein.getAccession()) {
                @Override
                public void selected(Object itemId) {
                    mainTable.setValue(itemId);
                }
            };
             Map<String, Number> mwValues = new LinkedHashMap<>();
            mwValues.put("greenlayout", (float) protein.getMW()/ (float)peptideShakerVisualizationDataset.getMaxMW());
            SparkLineLabel mwLabel = new SparkLineLabel(df.format(protein.getMW()), mwValues, protein.getAccession()) {
                @Override
                public void selected(Object itemId) {
                    mainTable.setValue(itemId);
                }
            };
            

            Map<String, Number> confidentValues = new LinkedHashMap<>();
            confidentValues.put("greenlayout", (float) protein.getConfidence() / 100f);
            SparkLineLabel confidentLabel = new SparkLineLabel(df.format(protein.getConfidence()), confidentValues, protein.getAccession()) {
                @Override
                public void selected(Object itemId) {
                    mainTable.setValue(itemId);
                }
            };
            ValidationLabel validation = new ValidationLabel(protein.getValidation());

            proteinTableData.put(protein.getAccession(), new Object[]{protein.getIndex(), piLabel, protein.getAccession(), protein.getDescription(), protein.getProteinGroup(), protein.getGeneName(), protein.getChromosome(), coverageLabel, peptidesNumberLabelLabel, psmNumberLabelLabel, ms2QuantLabelLabel, mwLabel, confidentLabel, validation});
        });
        int[] searchingIndexes = {2, 3, 4};
        int keyIndex = 2;
        this.proteinTableContainer.upateTableData(proteinTableData, searchingIndexes, keyIndex);
        peptideShakerVisualizationDataset.getPeptidesMap();
        Map<String, Color> ModificationColorMap = new LinkedHashMap<>();
        ModificationMatrix modificationMatrix = peptideShakerVisualizationDataset.getModificationMatrix();
        modificationMatrix.getRows().keySet().forEach((mod) -> {
            if (PTM.containsPTM(mod)) {
                ModificationColorMap.put(mod, PTMFactory.getDefaultColor(mod));
            } else {
                ModificationColorMap.put(mod, Color.LIGHT_GRAY);
            }
        });
        this.chartFiltersContainer.updateFiltersData(modificationMatrix, ModificationColorMap, peptideShakerVisualizationDataset.getChromosomeMap(), peptideShakerVisualizationDataset.getPiMap(), peptideShakerVisualizationDataset.getProteinValidationMap(), peptideShakerVisualizationDataset.getProteinPeptidesNumberMap(), peptideShakerVisualizationDataset.getProteinPSMNumberMap(), peptideShakerVisualizationDataset.getProteinCoverageMap());

        mainTable.sort(new Object[]{"peptides_number", "psm_number"}, new boolean[]{false, false});
        int index = 1;
        for (Object key : mainTable.getItemIds()) {
            mainTable.getItem(key).getItemProperty("index").setValue(index++);
        }
        mainTable.setSortEnabled(false);
    }

    @Override
    public void selectionChange(String type) {
        if (type.equalsIgnoreCase("dataset_filter_selection")) {
//            
        }
    }

    @Override
    public String getFilterId() {
        return "proteins_table_filter";
    }

    @Override
    public void updateFilterSelection(Set<Comparable> selection, Set<Comparable> selectedCategories, boolean topFilter, boolean selectOnly, boolean selfAction) {
        this.proteinTableContainer.filterTable(selection);
    }

    @Override
    public void redrawChart() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void suspendFilter(boolean suspend) {
    }

}
