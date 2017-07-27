package com.uib.web.peptideshaker.presenter.components.peptideshakerview.components;

import com.uib.web.peptideshaker.presenter.core.graph.GraphComponent;
import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideObject;
import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.galaxy.dataobjects.ProteinObject;
import com.uib.web.peptideshaker.presenter.core.SearchableTable;
import com.uib.web.peptideshaker.presenter.core.TableColumnHeader;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabelTextField;
import com.uib.web.peptideshaker.presenter.core.filtercharts.ChartFiltersContainer;
import com.vaadin.data.Property;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.collections15.map.LinkedMap;

/**
 * This class represents filtered Table with graph view
 *
 * @author Yehia Farag
 */
public class FilterTableGraphComponent extends VerticalLayout {

   

    private final GraphComponent graphLayout;
    private final Map<String, ProteinObject> proteinNodes;
    private final Map<String, PeptideObject> peptidesNodes;
    private final Set<PeptideObject> peptides;

    private final HashMap<String, ArrayList<String>> edges;
    private Label searchResultsLabel;

    private final SearchableTable proteinTableContainer; 
    private final ChartFiltersContainer chartFiltersContainer;

    public FilterTableGraphComponent(SelectionManager Selection_Manager) {
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
        this.proteinTableContainer = new SearchableTable("proteins","proteins_filter","Accssion or protein name", tableHeaders);
        FilterTableGraphComponent.this.addComponent(proteinTableContainer);
        Selection_Manager.RegistrFilter(proteinTableContainer);
        graphLayout = new GraphComponent();
//        FilterTableGraphComponent.this.addComponent(graphLayout);
        proteinNodes = new LinkedHashMap<>();
        peptidesNodes = new LinkedHashMap<>();
        peptides = new LinkedHashSet<>();
        edges = new HashMap<>();

    }

    public void updateData(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {
        Map<Object, ProteinObject> proteinTableMap = peptideShakerVisualizationDataset.getProteinsMap();
        Object[][] proteinTableData = new Object[proteinTableMap.size()][];
        int x = 0;
        for (ProteinObject protein : proteinTableMap.values()) {
            proteinTableData[x++] = new Object[]{protein.getIndex(), protein.getAccession(), protein.getDescription(), protein.getGeneName(), protein.getProteinInference(), protein.getMW(), protein.getPossibleCoverage(), protein.getValidatedPeptidesNumber(), protein.getProteinGroup()};
        }
        int[] searchingIndexes = {2, 8};
        int keyIndex = 1;
        this.proteinTableContainer.upateTableData(proteinTableData, searchingIndexes, keyIndex);
        peptideShakerVisualizationDataset.getPeptidesMap();
         Map<String, Set<String>> rows = new LinkedHashMap<>();
        Random r = new Random();
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        String alphabet2 = "ABCDEF";
        for (int i = 0; i < alphabet2.length(); i++) {
            String key = "" + alphabet2.charAt(i);
            rows.put(key, new HashSet<>());
            int protNum = r.nextInt(alphabet2.length());
            for (int y = 0; y < protNum; y++) {
                rows.get(key).add(""+alphabet.charAt(r.nextInt(alphabet.length())));
                rows.get(key).add("ZzZ");
            }
        }
//        this.chartFiltersContainer.updateFiltersData(rows);
          this.chartFiltersContainer.updateFiltersData(peptideShakerVisualizationDataset.getModificationMap());
       // peptideShakerVisualizationDataset.getModificationMap()

    }

    Map<String, ProteinObject> unrelatedProt = new LinkedHashMap<>();
    Map<String, PeptideObject> unrelatedPeptides = new LinkedHashMap<>();

    private void fillUnrelatedProteinsAndPeptides(String proteinAccession, ProteinObject protein) {
//        if (unrelatedProt.containsKey(proteinAccession)) {
//            return;
//        }
//        unrelatedProt.put(proteinAccession, protein);
//        Set<PeptideObject> tpeptides = peptideShakerVisualizationDataset.getPeptides(proteinAccession);
//        if (tpeptides != null) {
//            for (PeptideObject pep : tpeptides) {
//                if (!edges.containsKey(pep.getModifiedSequence())) {
//                    ArrayList<String> tEd = new ArrayList<>();
//                    edges.put(pep.getModifiedSequence(), tEd);
//                }
//                edges.get(pep.getModifiedSequence()).add(proteinAccession);
//                if (!peptidesNodes.containsKey(pep.getModifiedSequence())) {
//                    unrelatedPeptides.put(pep.getModifiedSequence(), pep);
//                    for (String newAcc : pep.getProteinsSet()) {
//                        fillUnrelatedProteinsAndPeptides(newAcc, proteinTableData.get(newAcc));
//                    }
//                }
//
//            }
//        }

    }

}
