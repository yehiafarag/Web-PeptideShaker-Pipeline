package com.uib.web.peptideshaker.presenter.components.peptideshakerview;

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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class represents filtered Table with graph view
 *
 * @author Yehia Farag
 */
public class FilterTableGraphComponent extends VerticalLayout implements Property.ValueChangeListener {

    private Table proteinsTable;
    private PeptideShakerVisualizationDataset peptideShakerVisualizationDataset;
    private final GraphComponent graphLayout;

    public FilterTableGraphComponent() {
        FilterTableGraphComponent.this.setSizeFull();
        FilterTableGraphComponent.this.setSpacing(true);
        FilterTableGraphComponent.this.addStyleName("scrollinsideframe");
        initProteinTable();
        FilterTableGraphComponent.this.addComponent(proteinsTable);
        graphLayout = new GraphComponent();
        FilterTableGraphComponent.this.addComponent(graphLayout);

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
        Map<Object, ProteinObject> proteinTableData = peptideShakerVisualizationDataset.getProteinsMap();
        for (ProteinObject protein : proteinTableData.values()) {
            this.proteinsTable.addItem(new Object[]{protein.getIndex(), protein.getAccession(), protein.getDescription(), protein.getGeneName(), protein.getProteinInference(), protein.getMW(), protein.getPossibleCoverage(), protein.getValidatedPeptidesNumber()}, protein.getAccession());
        }
        proteinsTable.setCaption("<b>Proteins</b> ( " + proteinsTable.getItemIds().size() + " )");
        proteinsTable.addValueChangeListener(FilterTableGraphComponent.this);
        peptideShakerVisualizationDataset.getPeptidesMap();
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        Object objcetId = proteinsTable.getValue();
        if (objcetId == null) {
            graphLayout.updateGraphData(null, null, null, null);
            return;
        }
        ProteinObject protein = peptideShakerVisualizationDataset.getProtein(objcetId);

        Set<String> proteinNodes = new LinkedHashSet<>();
        Set<PeptideObject> peptides = new LinkedHashSet<>();
        for (String acc : protein.getProteinGroupSet()) {
            proteinNodes.add(acc);
            System.out.println("acction list " + acc);
            peptides.addAll(peptideShakerVisualizationDataset.getPeptides(acc));
        }

        Set<String> peptidesNodes = new LinkedHashSet<>();
        HashMap<String, ArrayList<String>> edges = new HashMap<>();
        for (PeptideObject peptide : peptides) {
            peptidesNodes.add(peptide.getModifiedSequence());
            ArrayList<String> tEd = new ArrayList<>();
            for (String acc : peptide.getProteinsSet()) {
               tEd.add(acc);
            } 
            edges.put(peptide.getModifiedSequence(), tEd);

        }

//        Set<ProteinObject> relatedProteinsSet = new HashSet<>();
//        for (String acc : protein.getProteinGroupSet()) {
////            relatedProteinsSet.addAll(peptideShakerVisualizationDataset.getRelatedProteinsSet(acc));
////            System.out.println("at other proteins [" + relatedProteinsSet.size() + "] [" + protein.getProteinGroup() + "]");
//        }
//        Set<ProteinObject> tempRelatedProteinsSet = new HashSet<>(relatedProteinsSet);
////        Set<ProteinObject> mainProteinGroup = peptideShakerVisualizationDataset.getRelatedProteinsSet(protAcc);
////        relatedProteinsSet.addAll(mainProteinGroup);
////        tempRelatedProteinsSet.addAll(mainProteinGroup);
//        boolean addNewValue = true;
//        while (addNewValue) {
//            for (ProteinObject tempProtein : relatedProteinsSet) {
//                tempRelatedProteinsSet.addAll(peptideShakerVisualizationDataset.getRelatedProteinsSet(tempProtein.getAccession()));
//            }
//            if (tempRelatedProteinsSet.size() != relatedProteinsSet.size()) {
//                relatedProteinsSet.addAll(tempRelatedProteinsSet);
//                addNewValue = true;
//
//            } else {
//                addNewValue = false;
//            }
//        }
//        Set<String> proteinNodes = new LinkedHashSet<>();
//        HashMap<String, ArrayList<String>> edges = new HashMap<>();
////        for (ProteinObject tempProtein : relatedProteinsSet) {
////        for (int x = 0; x < 100; x++) {
//            for (String acc : protein.getProteinGroupSet()) {
//                proteinNodes.add(acc);
//            }
//            nodes.add("A1 "+x);
////            System.out.println("at protein name " + prot);
//            ArrayList<String> tEd = new ArrayList<>();
//            tEd.add("A1"+x);
//            tEd.add("A1"+(x+1));
////        tEd.add("D");
////        tEd.add("E");
////        tEd.add("F");
//            edges.put("A1"+x, tEd);
//        }
        graphLayout.updateGraphData(protein, proteinNodes, peptidesNodes, edges);

    }

}
