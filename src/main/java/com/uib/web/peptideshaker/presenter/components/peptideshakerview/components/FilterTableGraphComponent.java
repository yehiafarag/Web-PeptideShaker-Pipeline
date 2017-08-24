package com.uib.web.peptideshaker.presenter.components.peptideshakerview.components;

import com.compomics.util.experiment.biology.PTMFactory;
import com.uib.web.peptideshaker.presenter.core.graph.GraphComponent;
import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideObject;
import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.galaxy.dataobjects.ProteinObject;
import com.uib.web.peptideshaker.presenter.core.SearchableTable;
import com.uib.web.peptideshaker.presenter.core.TableColumnHeader;
import com.uib.web.peptideshaker.presenter.core.filtercharts.ChartFiltersContainer;
import com.uib.web.peptideshaker.presenter.core.filtercharts.charts.RegistrableFilter;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import java.awt.Color;
import java.util.ArrayList;
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
public class FilterTableGraphComponent extends VerticalLayout implements RegistrableFilter {
    
    private final GraphComponent graphLayout;
    private final Map<String, ProteinObject> proteinNodes;
    private final Map<String, PeptideObject> peptidesNodes;
    private final Set<PeptideObject> peptides;
    
    private final HashMap<String, ArrayList<String>> edges;
    private Label searchResultsLabel;
    
    private final SearchableTable proteinTableContainer;
    private final ChartFiltersContainer chartFiltersContainer;
    private final SelectionManager Selection_Manager;
    /**
     * The post translational modifications factory.
     */
    private final PTMFactory PTM = PTMFactory.getInstance();
    
    public FilterTableGraphComponent(SelectionManager Selection_Manager) {
        this.Selection_Manager = Selection_Manager;
        FilterTableGraphComponent.this.setSizeFull();
        FilterTableGraphComponent.this.setSpacing(true);
        FilterTableGraphComponent.this.addStyleName("scrollinsideframe");
        
        this.chartFiltersContainer = new ChartFiltersContainer(Selection_Manager);
        FilterTableGraphComponent.this.addComponent(chartFiltersContainer);
        
        TableColumnHeader headerI = new TableColumnHeader("Index", Integer.class, null, "", null, Table.Align.RIGHT);
        TableColumnHeader headerII = new TableColumnHeader("Accession", String.class, null, "Accession", null, Table.Align.CENTER);
        TableColumnHeader headerIII = new TableColumnHeader("Name", String.class, null, "Name", null, Table.Align.LEFT);
        TableColumnHeader headerIV = new TableColumnHeader("geneName", String.class, null, "Gene Name", null, Table.Align.CENTER);
        TableColumnHeader headerV = new TableColumnHeader("proteinInference", String.class, null, "Protein Inference", null, Table.Align.CENTER);
        TableColumnHeader headerVI = new TableColumnHeader("mwkDa", Double.class, null, "MW (kDa)", null, Table.Align.RIGHT);
        TableColumnHeader headerVII = new TableColumnHeader("possibleCoverage", Double.class, null, "Possible Coverage", null, Table.Align.RIGHT);
        TableColumnHeader headerVIII = new TableColumnHeader("peptides_number", Integer.class, null, "#Peptides", null, Table.Align.RIGHT);
        TableColumnHeader headerIVV = new TableColumnHeader("protein_group", String.class, null, "Protein Group", null, Table.Align.LEFT);
        
        TableColumnHeader[] tableHeaders = new TableColumnHeader[]{headerI, headerII, headerIII, headerIV, headerV, headerVI, headerVII, headerVIII, headerIVV};
        this.proteinTableContainer = new SearchableTable("proteins", "Accssion or protein name", tableHeaders);
        FilterTableGraphComponent.this.addComponent(proteinTableContainer);
        
        graphLayout = new GraphComponent();
//        FilterTableGraphComponent.this.addComponent(graphLayout);
        proteinNodes = new LinkedHashMap<>();
        peptidesNodes = new LinkedHashMap<>();
        peptides = new LinkedHashSet<>();
        edges = new HashMap<>();
        Selection_Manager.RegistrFilter(FilterTableGraphComponent.this);
    }
    
    public void updateData(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {
        Map<String, ProteinObject> proteinTableMap = peptideShakerVisualizationDataset.getProteinsMap();
        Map<String, Object[]> proteinTableData = new LinkedHashMap<>();
        for (ProteinObject protein : proteinTableMap.values()) {
            proteinTableData.put(protein.getAccession(), new Object[]{protein.getIndex(), protein.getAccession(), protein.getDescription(), protein.getGeneName(), protein.getProteinInference(), protein.getMW(), protein.getPossibleCoverage(), protein.getValidatedPeptidesNumber(), protein.getProteinGroup()});
        }
        int[] searchingIndexes = {2, 8};
        int keyIndex = 1;
        this.proteinTableContainer.upateTableData(proteinTableData, searchingIndexes, keyIndex);
        peptideShakerVisualizationDataset.getPeptidesMap();
//        Map<String, Set<String>> rows = new LinkedHashMap<>();
//        Random r = new Random();
//        String alphabet = "abcdefghijklmnopqrstuvwxyz";
//        String alphabet2 = "ABCDEF";
//        for (int i = 0; i < alphabet2.length(); i++) {
//            String key = "" + alphabet2.charAt(i);
//            rows.put(key, new HashSet<>());
//            int protNum = r.nextInt(alphabet2.length());
//            for (int y = 0; y < protNum; y++) {
//                rows.get(key).add("" + alphabet.charAt(r.nextInt(alphabet.length())));
//                rows.get(key).add("ZzZ");
//            }
//        }
////        this.chartFiltersContainer.updateFiltersData(rows);
        Map<String, Color> ModificationColorMap = new LinkedHashMap<>();
        Map<String, Set<String>> modificationsMap = peptideShakerVisualizationDataset.getModificationMap();
        for (String mod : modificationsMap.keySet()) {
            if (PTM.containsPTM(mod)) {
                ModificationColorMap.put(mod, PTM.getColor(mod));
            }else{
                 ModificationColorMap.put(mod, Color.BLACK);
            }
        }
        this.chartFiltersContainer.updateFiltersData(modificationsMap, ModificationColorMap, peptideShakerVisualizationDataset.getChromosomeMap(), peptideShakerVisualizationDataset.getPiMap(), peptideShakerVisualizationDataset.getProteinValidationMap());
        // peptideShakerVisualizationDataset.getModificationMap()

    }
    
    Map<String, ProteinObject> unrelatedProt = new LinkedHashMap<>();
    Map<String, PeptideObject> unrelatedPeptides = new LinkedHashMap<>();
    
    @Override
    public String getFilterId() {
        return "proteins_filter";
    }
    
    @Override
    public void resetFilter() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void updateFilter(Set<String> selection, Set<Object> selectedCategories, boolean singleFilter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void selectionChange(String type) {
        if (type.equalsIgnoreCase("protein_selection")) {
            this.proteinTableContainer.filterTable(Selection_Manager.getProteinSelectionValue());
        }
    }
    
    @Override
    public Component getThumb() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void redrawChart() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public boolean isAppliedFilter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public Set<Object> getSelectedCategories() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
