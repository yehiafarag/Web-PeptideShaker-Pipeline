package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components;

import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.ProteinGroupObject;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.coverage.ProteinCoverageComponent;
import com.uib.web.peptideshaker.presenter.core.ActionLabel;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Link;
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
    private final AbsoluteLayout chainCoverageLayout;

    public ProteinCoverageContainer(AbsoluteLayout chainCoverageLayout) {
        ProteinCoverageContainer.this.setSizeFull();
        this.chainCoverageLayout = chainCoverageLayout;
        tableData = new LinkedMap<>();
        proteinCoverageTable = new Table(" ");
        proteinCoverageTable.setStyleName(ValoTheme.TABLE_COMPACT);
        proteinCoverageTable.addStyleName(ValoTheme.TABLE_SMALL);
        proteinCoverageTable.addStyleName("inframetable");
        proteinCoverageTable.addStyleName("framedpanel");
        proteinCoverageTable.addStyleName(ValoTheme.TABLE_NO_STRIPES);
        proteinCoverageTable.setSelectable(false);
        proteinCoverageTable.setWidth(100, Unit.PERCENTAGE);
        proteinCoverageTable.setHeight(95, Unit.PERCENTAGE);
        proteinCoverageTable.addContainerProperty("info", ActionLabel.class, null, "", null, Table.Align.LEFT);
        proteinCoverageTable.addContainerProperty("acc", Link.class, null, "Accession", null, Table.Align.CENTER);
        proteinCoverageTable.addContainerProperty("name", ActionLabel.class, null, "Name", null, Table.Align.LEFT);
        proteinCoverageTable.addContainerProperty("coverage", ProteinCoverageComponent.class, null, "Coverage", null, Table.Align.LEFT);
        proteinCoverageTable.setColumnWidth("info", 37);
        proteinCoverageTable.setColumnWidth("acc", 100);
        proteinCoverageTable.setColumnWidth("name", 300);
        ProteinCoverageContainer.this.addComponent(proteinCoverageTable);
        proteinCoverageTable.addColumnResizeListener((Table.ColumnResizeEvent event) -> {
            proteinCoverageTable.setColumnWidth(event.getPropertyId(), event.getPreviousWidth());
        });

    }

    public void setSelectedItems(Set<Object> selectedProteinsItems, Set<Object> selectedPeptidesItems) {
        if (tableData.isEmpty() || !tableData.keySet().containsAll(selectedProteinsItems)) {
            return;
        }
        this.proteinCoverageTable.removeAllItems();
        selectedProteinsItems.stream().map((id) -> {
            this.proteinCoverageTable.addItem(tableData.get(id), id);
            return id;
        }).map((id) -> ((ProteinCoverageComponent) tableData.get(id)[3])).map((pcov) -> {
            pcov.selectPeptides(selectedPeptidesItems);
            return pcov;
        }).filter((pcov) -> (selectedProteinsItems.size() == 1)).map((pcov) -> {
            if (chainCoverageLayout != null && chainCoverageLayout.isAttached()) {
                chainCoverageLayout.setSizeUndefined();
                chainCoverageLayout.detach();
            }
            return pcov;
        }).map((pcov) -> {
            pcov.enable3D(chainCoverageLayout);
            return pcov;
        }).forEachOrdered((_item) -> {
        });

    }

    public void updateProteinsMode(String mode) {
        tableData.keySet().forEach((id) -> {
            ((ProteinCoverageComponent) tableData.get(id)[3]).updateStylingMode(mode);
        });

    }

    public Map<Object, Object[]> getTableData() {
        return tableData;
    }

    public void selectDataset(Map<String, ProteinGroupObject> proteinNodes, Map<String, PeptideObject> peptidesNodes, Set<Object> defaultSelectedProteinsItems, Set<Object> defaultSelectedPeptidesItems) {
        tableData.clear();
        proteinNodes.values().forEach((protein) -> {
            
            ProteinCoverageComponent proteinLayout = new ProteinCoverageComponent(protein, peptidesNodes) {
                @Override
                public void selectPeptide(Object proteinId, Object peptideId) {
                    ProteinCoverageContainer.this.selectPeptide(proteinId, peptideId);
                    tableData.keySet().forEach((id) -> {
                        if (id.equals(proteinId)) {
                            ((ProteinCoverageComponent) tableData.get(id)[3]).selectPeptides(peptideId);
                        } else {
                            ((ProteinCoverageComponent) tableData.get(id)[3]).selectPeptides("");
                        }
                    });
                }

            };
            ActionLabel info = new ActionLabel(VaadinIcons.INFO, "Click to view protein information") {
                @Override
                public void layoutClick(LayoutEvents.LayoutClickEvent event) {

                }
            };
            Link proteinAccLink = new Link(protein.getAccession(), new ExternalResource("http://www.uniprot.org/uniprot/" + protein.getAccession()));
            proteinAccLink.setTargetName("_blank");
            proteinAccLink.setStyleName("tablelink");

            ActionLabel proteinDescription = new ActionLabel(protein.getDescription()) {
                @Override
                public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                }
            };
            proteinDescription.setData(protein.getAccession());
            tableData.put(protein.getAccession(), new Object[]{info, proteinAccLink, proteinDescription, proteinLayout});
        });
        setSelectedItems(defaultSelectedProteinsItems, defaultSelectedPeptidesItems);
    }

    public abstract void selectPeptide(Object proteinId, Object peptideId);

}


