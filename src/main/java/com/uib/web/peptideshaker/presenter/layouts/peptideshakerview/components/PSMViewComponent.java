/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components;

import com.uib.web.peptideshaker.galaxy.dataobjects.PSMObject;
import com.uib.web.peptideshaker.presenter.core.ActionLabel;
import com.uib.web.peptideshaker.presenter.core.PopupLabel;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.coverage.ProteinCoverageComponent;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.List;

/**
 *
 * @author Yehia Farag
 */
public class PSMViewComponent extends VerticalLayout {

    private final Table psmOverviewTable;
    private final VerticalLayout chartContainer;

    public PSMViewComponent() {
        PSMViewComponent.this.setSizeFull();
        PSMViewComponent.this.setSpacing(true);
        psmOverviewTable = new Table();
        psmOverviewTable.setStyleName(ValoTheme.TABLE_COMPACT);
        psmOverviewTable.addStyleName(ValoTheme.TABLE_SMALL);
        psmOverviewTable.addStyleName("inframetable");
        psmOverviewTable.addStyleName(ValoTheme.TABLE_NO_STRIPES);
        psmOverviewTable.setSelectable(false);
        psmOverviewTable.setWidth(100, Unit.PERCENTAGE);
        psmOverviewTable.setHeight(100, Unit.PERCENTAGE);
        psmOverviewTable.addContainerProperty("index", Integer.class, null, "", null, Table.Align.RIGHT);
        psmOverviewTable.addContainerProperty("sequence", String.class, null, "Sequence", null, Table.Align.LEFT);
        psmOverviewTable.addContainerProperty("charge", String.class, null, "Charge", null, Table.Align.LEFT);
        psmOverviewTable.addContainerProperty("mzError", String.class, null, "m/z Error", null, Table.Align.LEFT);
        psmOverviewTable.addContainerProperty("confidence", Double.class, null, "Confidence", null, Table.Align.LEFT);
        psmOverviewTable.addContainerProperty("validation", String.class, null, "", null, Table.Align.LEFT);
        psmOverviewTable.setColumnWidth("index", 30);
        PSMViewComponent.this.addComponent(psmOverviewTable);

        chartContainer = new VerticalLayout();
        chartContainer.setSizeFull();
        PSMViewComponent.this.addComponent(chartContainer);

    }
    int index = 0;

    public void updateView(List<PSMObject> psms) {
        this.psmOverviewTable.removeAllItems();
        index = 0;
        psms.stream().map((psm) -> {
            return psm;
        }).forEachOrdered((psm) -> {
            this.psmOverviewTable.addItem(new Object[]{index++, psm.getModifiedSequence(), psm.getIdentificationCharge(), psm.getPrecursorMZError_PPM(), psm.getConfidence(), psm.getValidation()}, psm.getIndex());
        });
        ;

    }
}
