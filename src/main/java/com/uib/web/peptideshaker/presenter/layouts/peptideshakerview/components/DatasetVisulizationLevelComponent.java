package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components;

import com.compomics.util.experiment.biology.PTMFactory;
import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.galaxy.dataobjects.ProteinObject;
import com.uib.web.peptideshaker.model.core.ModificationMatrix;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.SelectionManager;
import com.uib.web.peptideshaker.presenter.core.SearchableTable;
import com.uib.web.peptideshaker.presenter.core.TableColumnHeader;
import com.uib.web.peptideshaker.presenter.core.filtercharts.FiltersContainer;
import com.uib.web.peptideshaker.presenter.core.filtercharts.charts.RegistrableFilter;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import java.awt.Color;
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

        TableColumnHeader headerI = new TableColumnHeader("Index", Integer.class, null, "", null, Table.Align.RIGHT);
        TableColumnHeader headerII = new TableColumnHeader("Accession", String.class, null, "Accession", null, Table.Align.CENTER);
        TableColumnHeader headerIII = new TableColumnHeader("Name", String.class, null, "Name", null, Table.Align.LEFT);
        TableColumnHeader headerIV = new TableColumnHeader("geneName", String.class, null, "Gene Name", null, Table.Align.CENTER);
        TableColumnHeader headerV = new TableColumnHeader("proteinInference", String.class, null, "Protein Inference", null, Table.Align.CENTER);
        TableColumnHeader headerVI = new TableColumnHeader("mwkDa", Double.class, null, "MW (kDa)", null, Table.Align.RIGHT);
        TableColumnHeader headerVII = new TableColumnHeader("possibleCoverage", Double.class, null, "Possible Coverage", null, Table.Align.RIGHT);
        TableColumnHeader headerVIII = new TableColumnHeader("peptides_number", Integer.class, null, "#Peptides", null, Table.Align.RIGHT);
        TableColumnHeader headerIVV = new TableColumnHeader("protein_group", String.class, null, "Protein Group", null, Table.Align.LEFT);
        TableColumnHeader headerIIVV = new TableColumnHeader("psm_number", Integer.class, null, "#PSM", null, Table.Align.RIGHT);

        TableColumnHeader[] tableHeaders = new TableColumnHeader[]{headerI, headerII, headerIII, headerIV, headerV, headerVI, headerVII, headerVIII, headerIVV,headerIIVV};
        this.proteinTableContainer = new SearchableTable("Proteins", "Accssion or protein name", tableHeaders){
           private final Set<Comparable>selectedIds = new LinkedHashSet<>();
           @Override
            public void itemSelected(Object itemId) {
                selectedIds.clear();
                selectedIds.add(itemId+"");
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
        proteinTableMap.values().forEach((protein) -> {
            proteinTableData.put(protein.getAccession(), new Object[]{protein.getIndex(), protein.getAccession(), protein.getDescription(), protein.getGeneName(), protein.getProteinInference(), protein.getMW(), protein.getPossibleCoverage(), protein.getValidatedPeptidesNumber(), protein.getProteinGroup(),protein.getPSMsNumber()});
        });
        int[] searchingIndexes = {1, 2, 8};
        int keyIndex = 1;
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
