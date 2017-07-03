package com.uib.web.peptideshaker.presenter.components.peptideshakerview;

import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideShakerVisualizationDataset;
import com.vaadin.data.Property;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.vaadin.hezamu.canvas.Canvas;

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
        this.proteinsTable.setMultiSelect(true);
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
        this.proteinsTable.addContainerProperty("mwkDa", Double.class, null, "MW (kDa)", null, Table.Align.RIGHT);
        this.proteinsTable.addContainerProperty("possibleCoverage", Double.class, null, "Possible Coverage", null, Table.Align.RIGHT);
        this.proteinsTable.addContainerProperty("peptides_number", Integer.class, null, "#Peptides", null, Table.Align.RIGHT);

    }

    public void upateTableData(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {
        this.peptideShakerVisualizationDataset = peptideShakerVisualizationDataset;
        proteinsTable.removeValueChangeListener(FilterTableGraphComponent.this);
        proteinsTable.removeAllItems();
        Map<Object, Object[]> proteinTableData = peptideShakerVisualizationDataset.getProteinTableData();
        for (Object x : proteinTableData.keySet()) {
            this.proteinsTable.addItem(proteinTableData.get(x), x);
        }
        proteinsTable.setCaption("<b>Proteins</b> ( " + proteinsTable.getItemIds().size() + " )");
        proteinsTable.addValueChangeListener(FilterTableGraphComponent.this);
        peptideShakerVisualizationDataset.getPeptideTableData();
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        Set objectIds = (Set) proteinsTable.getValue();
        for (Object objcetId : objectIds) {
            String protAcc = peptideShakerVisualizationDataset.getProteinTableData().get(objcetId)[1].toString();
            Set<Object[]> peptidesSet = peptideShakerVisualizationDataset.getPeptideInfo(protAcc);
            int index = 1;
            for (Object[] peptide : peptidesSet) {
                System.out.println("at selected value " + index++ + "     " + peptide[1] + "  key " + protAcc);
            }

        }
        ArrayList<String> nodes = new ArrayList<>();
//        nodes.add("A");
//        nodes.add("B");
//        nodes.add("C");
//        nodes.add("D");
//        nodes.add("E");
//        nodes.add("F");
//        nodes.add("G");
//        nodes.add("H");
//        nodes.add("I");
        for (int x = 0; x < 10; x++) {
            nodes.add("A" + x);
        }

        HashMap<String, ArrayList<String>> edges = new HashMap<>();
        ArrayList<String> tEd = new ArrayList<>();
        
        tEd.add("A1");
        tEd.add("A100");
//        tEd.add("D");
//        tEd.add("E");
//        tEd.add("F");
        edges.put("A1", tEd);

        graphLayout.initGraph(nodes, edges);

    }

}
