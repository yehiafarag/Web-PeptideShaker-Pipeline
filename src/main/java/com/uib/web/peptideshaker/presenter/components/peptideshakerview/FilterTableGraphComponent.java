package com.uib.web.peptideshaker.presenter.components.peptideshakerview;

import com.uib.web.peptideshaker.presenter.core.graph.GraphComponent;
import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideObject;
import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.galaxy.dataobjects.ProteinObject;
import com.vaadin.data.Property;
import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections15.map.LinkedMap;

/**
 * This class represents filtered Table with graph view
 *
 * @author Yehia Farag
 */
public class FilterTableGraphComponent extends VerticalLayout implements Property.ValueChangeListener {

    private Table proteinsTable;
    private PeptideShakerVisualizationDataset peptideShakerVisualizationDataset;
    private final GraphComponent graphLayout;
    private final Map<String, ProteinObject> proteinNodes;
    private final Map<String, PeptideObject> peptidesNodes;
    private final Set<PeptideObject> peptides;
    private Map<Object, ProteinObject> proteinTableData;
    private final HashMap<String, ArrayList<String>> edges;

    public FilterTableGraphComponent() {
        FilterTableGraphComponent.this.setSizeFull();
        FilterTableGraphComponent.this.setSpacing(true);
        FilterTableGraphComponent.this.addStyleName("scrollinsideframe");
        initProteinTable();
        FilterTableGraphComponent.this.addComponent(proteinsTable);
        graphLayout = new GraphComponent();
        FilterTableGraphComponent.this.addComponent(graphLayout);
        proteinNodes = new LinkedHashMap<>();
        peptidesNodes = new LinkedHashMap<>();
        peptides = new LinkedHashSet<>();
        edges = new HashMap<>();
    }

    /**
     * Initialize the proteins table.
     */
    private void initProteinTable() {
        this.proteinsTable = new Table() {
            @Override
            protected String formatPropertyValue(Object rowId, Object colId, Property property) {
                Object v = property.getValue();
                if (v instanceof Double) {
                    DecimalFormat df = new DecimalFormat("#.##");
                    return df.format(v);
                }
                return super.formatPropertyValue(rowId, colId, property);
            }
        };
        this.proteinsTable.setCaption("<b>Proteins</b>");
        this.proteinsTable.setCaptionAsHtml(true);
        this.proteinsTable.setStyleName("framedpanel");
        this.proteinsTable.addStyleName(ValoTheme.TABLE_BORDERLESS);
        this.proteinsTable.addStyleName(ValoTheme.TABLE_SMALL);
        this.proteinsTable.addStyleName(ValoTheme.TABLE_COMPACT);
        this.proteinsTable.setHeight(100, Unit.PERCENTAGE);
        this.proteinsTable.setWidth(100, Unit.PERCENTAGE);
        this.proteinsTable.setCacheRate(1);
        this.proteinsTable.setMultiSelect(false);
        this.proteinsTable.setMultiSelectMode(MultiSelectMode.DEFAULT);
        this.proteinsTable.setSelectable(true);
        this.proteinsTable.setSortEnabled(true);
        this.proteinsTable.setColumnReorderingAllowed(false);

        this.proteinsTable.setColumnCollapsingAllowed(false);
        this.proteinsTable.addColumnResizeListener((Table.ColumnResizeEvent event) -> {
            proteinsTable.setColumnWidth(event.getPropertyId(), event.getPreviousWidth());
        });
        this.proteinsTable.setImmediate(true);

        this.proteinsTable.addContainerProperty("Index", Integer.class, null, "", null, Table.Align.RIGHT);
        this.proteinsTable.addContainerProperty("Accession", String.class, null, "Accession", null, Table.Align.CENTER);
        this.proteinsTable.addContainerProperty("Name", String.class, null, "Name", null, Table.Align.LEFT);
        this.proteinsTable.addContainerProperty("geneName", String.class, null, "Gene Name", null, Table.Align.CENTER);
        this.proteinsTable.addContainerProperty("proteinInference", String.class, null, "Protein Inference", null, Table.Align.CENTER);
        this.proteinsTable.addContainerProperty("mwkDa", Double.class, null, "MW (kDa)", null, Table.Align.RIGHT);
        this.proteinsTable.addContainerProperty("possibleCoverage", Double.class, null, "Possible Coverage", null, Table.Align.RIGHT);
        this.proteinsTable.addContainerProperty("peptides_number", Integer.class, null, "#Peptides", null, Table.Align.RIGHT);

    }

    public void upateTableData(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {
        this.peptideShakerVisualizationDataset = peptideShakerVisualizationDataset;
        proteinsTable.removeValueChangeListener(FilterTableGraphComponent.this);
        proteinsTable.removeAllItems();
        proteinTableData = peptideShakerVisualizationDataset.getProteinsMap();
        for (ProteinObject protein : proteinTableData.values()) {
            this.proteinsTable.addItem(new Object[]{protein.getIndex(), protein.getAccession(), protein.getDescription(), protein.getGeneName(), protein.getProteinInference(), protein.getMW(), protein.getPossibleCoverage(), protein.getValidatedPeptidesNumber()}, protein.getAccession());
        }
        proteinsTable.setCaption("<b>Proteins</b> ( " + proteinsTable.getItemIds().size() + " )");
        proteinsTable.addValueChangeListener(FilterTableGraphComponent.this);
        peptideShakerVisualizationDataset.getPeptidesMap();
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        Object objcetId = proteinsTable.getValue();//"P01889";
        proteinNodes.clear();
        peptidesNodes.clear();
        peptides.clear();
        unrelatedProt.clear();
        unrelatedPeptides.clear();
        edges.clear();
        if (objcetId == null) {
            graphLayout.updateGraphData(null, null, null, null);
            return;
        }
        ProteinObject protein = peptideShakerVisualizationDataset.getProtein(objcetId);
        for (String acc : protein.getProteinGroupSet()) {
            proteinNodes.put(acc, proteinTableData.get(acc));
            peptides.addAll(peptideShakerVisualizationDataset.getPeptides(acc));
        }

        Set<String> tunrelatedProt = new LinkedHashSet<>();
        for (PeptideObject peptide : peptides) {
            peptidesNodes.put(peptide.getModifiedSequence(), peptide);            
//            protein.addPeptideSequence(peptide.getModifiedSequence());
            ArrayList<String> tEd = new ArrayList<>();
            for (String acc : peptide.getProteinsSet()) {
                tEd.add(acc);
                if (!proteinNodes.containsKey(acc)) {
                    tunrelatedProt.add(acc);
                }
            }
            edges.put(peptide.getModifiedSequence(), tEd);

        }
        for (String unrelated : tunrelatedProt) {
            fillUnrelatedProteinsAndPeptides(unrelated, proteinTableData.get(unrelated));
        }
        proteinNodes.putAll(unrelatedProt);
        peptidesNodes.putAll(unrelatedPeptides);

        Map<String, ProteinObject> tempProteinNodes = new LinkedMap<>(proteinNodes);
        for (String accession : tempProteinNodes.keySet()) {
            proteinNodes.replace(accession, peptideShakerVisualizationDataset.updateProteinInformation(tempProteinNodes.get(accession),accession));
            proteinTableData.put(accession,proteinNodes.get(accession));
        }
        

        graphLayout.updateGraphData(protein, proteinNodes, peptidesNodes, edges);

    }
    Map<String, ProteinObject> unrelatedProt = new LinkedHashMap<>();
    Map<String, PeptideObject> unrelatedPeptides = new LinkedHashMap<>();

    private void fillUnrelatedProteinsAndPeptides(String proteinAccession, ProteinObject protein) {
        if (unrelatedProt.containsKey(proteinAccession)) {
            return;
        }
        unrelatedProt.put(proteinAccession, protein);
        Set<PeptideObject> tpeptides = peptideShakerVisualizationDataset.getPeptides(proteinAccession);
        if (tpeptides != null) {
            for (PeptideObject pep : tpeptides) {
                if (!edges.containsKey(pep.getModifiedSequence())) {
                    ArrayList<String> tEd = new ArrayList<>();
                    edges.put(pep.getModifiedSequence(), tEd);
                }
                edges.get(pep.getModifiedSequence()).add(proteinAccession);
                if (!peptidesNodes.containsKey(pep.getModifiedSequence())) {
                    unrelatedPeptides.put(pep.getModifiedSequence(), pep);
                    for (String newAcc : pep.getProteinsSet()) {
                        fillUnrelatedProteinsAndPeptides(newAcc, proteinTableData.get(newAcc));
                    }
                }

            }
        }

    }

}
