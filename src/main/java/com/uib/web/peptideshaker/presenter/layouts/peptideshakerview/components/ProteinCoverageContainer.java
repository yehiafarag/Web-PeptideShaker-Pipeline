package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components;

import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideObject;
import com.uib.web.peptideshaker.galaxy.dataobjects.ProteinObject;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.coverage.ProteinCoverageComponent;
import com.uib.web.peptideshaker.presenter.core.ActionLabel;
import com.uib.web.peptideshaker.presenter.core.PopupLabel;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections15.map.LinkedMap;

/**
 * This class represents proteins coverage container component
 *
 * @author Yehia Farag
 */
public abstract class ProteinCoverageContainer extends VerticalLayout {

    private final Table proteinCoverageTable;
    private final Map<Object, Object[]> tableData;

    public ProteinCoverageContainer() {
        ProteinCoverageContainer.this.setSizeFull();
        tableData = new LinkedMap<>();
        proteinCoverageTable = new Table(" ");
        proteinCoverageTable.setStyleName(ValoTheme.TABLE_COMPACT);
        proteinCoverageTable.addStyleName(ValoTheme.TABLE_SMALL);
        proteinCoverageTable.addStyleName("inframetable");
        proteinCoverageTable.addStyleName(ValoTheme.TABLE_NO_STRIPES);
        proteinCoverageTable.setSelectable(false);
        proteinCoverageTable.setWidth(100, Unit.PERCENTAGE);
        proteinCoverageTable.setHeight(95, Unit.PERCENTAGE);
        proteinCoverageTable.addContainerProperty("info", ActionLabel.class, null, "", null, Table.Align.LEFT);
        proteinCoverageTable.addContainerProperty("acc", ActionLabel.class, null, "Accession", null, Table.Align.LEFT);
        proteinCoverageTable.addContainerProperty("name", PopupLabel.class, null, "Name", null, Table.Align.LEFT);
        proteinCoverageTable.addContainerProperty("coverage", ProteinCoverageComponent.class, null, "Coverage", null, Table.Align.LEFT);
        proteinCoverageTable.setColumnWidth("info", 30);
        proteinCoverageTable.setColumnWidth("acc", 100);
        proteinCoverageTable.setColumnWidth("name", 300);
        ProteinCoverageContainer.this.addComponent(proteinCoverageTable);

    }

    public void setSelectedItems(Set<Object> selectedProteinsItems, Set<Object> selectedPeptidesItems) {
        if (tableData.isEmpty() || !tableData.keySet().containsAll(selectedProteinsItems)) {
            return;
        }
        this.proteinCoverageTable.removeAllItems();
        for (Object id : selectedProteinsItems) {
            this.proteinCoverageTable.addItem(tableData.get(id), id);
            ((ProteinCoverageComponent) tableData.get(id)[3]).selectPeptides(selectedPeptidesItems);
        }

    }

    public void updateProteinsMode(String mode) {
        for (Object id : tableData.keySet()) {
            ((ProteinCoverageComponent) tableData.get(id)[3]).updateStylingMode(mode);
        }

    }

    public Map<Object, Object[]> getTableData() {
        return tableData;
    }

    public void selectDataset(Map<String, ProteinObject> proteinNodes, Map<String, PeptideObject> peptidesNodes, Set<Object> defaultSelectedProteinsItems, Set<Object> defaultSelectedPeptidesItems) {
        tableData.clear();
//        this.proteinCoverageTable.removeAllItems();

        for (ProteinObject protein : proteinNodes.values()) {
            ProteinCoverageComponent proteinLayout = new ProteinCoverageComponent(protein, peptidesNodes) {
                @Override
                public void selectPeptide(Object proteinId, Object peptideId) {
                    ProteinCoverageContainer.this.selectPeptide(proteinId, peptideId);
                    for (Object id : tableData.keySet()) {
                        if (id.equals(proteinId)) {
                            ((ProteinCoverageComponent) tableData.get(id)[3]).selectPeptides(peptideId);
                        } else {
                            ((ProteinCoverageComponent) tableData.get(id)[3]).selectPeptides("");
                        }
                    }
                }

            };
            ActionLabel info = new ActionLabel(VaadinIcons.INFO_CIRCLE, "Click to view protein information") {
                @Override
                public void layoutClick(LayoutEvents.LayoutClickEvent event) {

                }
            };
            ActionLabel acc = new ActionLabel(protein.getAccession(), new ExternalResource("http://www.uniprot.org/uniprot/" + protein.getAccession())) {
                @Override
                public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                }
            };
            PopupLabel proteinDescription = new PopupLabel(protein.getDescription());
            tableData.put(protein.getAccession(), new Object[]{info, acc, proteinDescription, proteinLayout});
//            this.proteinCoverageTable.addItem(tableData.get(protein.getAccession()), protein.getAccession());

        }
        setSelectedItems(defaultSelectedProteinsItems, defaultSelectedPeptidesItems);
    }

    public abstract void selectPeptide(Object proteinId, Object peptideId);

}
