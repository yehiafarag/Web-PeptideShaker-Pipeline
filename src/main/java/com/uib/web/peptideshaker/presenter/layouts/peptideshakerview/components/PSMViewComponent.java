/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components;

import com.uib.web.peptideshaker.galaxy.dataobjects.PSMObject;
import com.uib.web.peptideshaker.presenter.pscomponents.PSMSequenceChart;
import com.uib.web.peptideshaker.presenter.pscomponents.SpectrumPlot;
import com.vaadin.data.Property;
import com.vaadin.event.LayoutEvents;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.List;

/**
 *
 * @author Yehia Farag
 */
public abstract class PSMViewComponent extends VerticalLayout {

    private final Table psmOverviewTable;
    private final VerticalLayout chartContainer;
    private final SpectrumPlot SpectrumPlot;
    private int index = 0;

    public SpectrumPlot getSpectrumPlot() {
        return SpectrumPlot;
    }
    private final LayoutEvents.LayoutClickListener listener;

    public PSMViewComponent() {
        PSMViewComponent.this.setSizeFull();
        PSMViewComponent.this.setSpacing(true);
        psmOverviewTable = new Table();
        psmOverviewTable.setStyleName(ValoTheme.TABLE_COMPACT);
        psmOverviewTable.addStyleName(ValoTheme.TABLE_SMALL);
        psmOverviewTable.addStyleName("inframetable");
        psmOverviewTable.addStyleName(ValoTheme.TABLE_NO_STRIPES);
        psmOverviewTable.setSelectable(true);
        psmOverviewTable.setNullSelectionAllowed(false);
        psmOverviewTable.setMultiSelect(false);
        psmOverviewTable.setWidth(100, Unit.PERCENTAGE);
        psmOverviewTable.setHeight(100, Unit.PERCENTAGE);
        psmOverviewTable.addContainerProperty("index", Integer.class, null, "", null, Table.Align.RIGHT);
        psmOverviewTable.addContainerProperty("sequence", PSMSequenceChart.class, null, "Sequence", null, Table.Align.LEFT);
        psmOverviewTable.addContainerProperty("charge", String.class, null, "Charge", null, Table.Align.LEFT);
        psmOverviewTable.addContainerProperty("mzError", String.class, null, "m/z Error", null, Table.Align.LEFT);
        psmOverviewTable.addContainerProperty("confidence", Double.class, null, "Confidence", null, Table.Align.LEFT);
        psmOverviewTable.addContainerProperty("validation", String.class, null, "", null, Table.Align.LEFT);
        psmOverviewTable.setColumnWidth("index", 30);
        psmOverviewTable.setColumnWidth("sequence", 350);
        psmOverviewTable.setSortContainerPropertyId("charge");
        PSMViewComponent.this.addComponent(psmOverviewTable);
        PSMViewComponent.this.setExpandRatio(psmOverviewTable, 0.25f);
        chartContainer = new VerticalLayout();
        chartContainer.setSizeFull();
        this.chartContainer.addStyleName("inframetable");
        PSMViewComponent.this.addComponent(chartContainer);
        PSMViewComponent.this.setExpandRatio(chartContainer, 0.75f);

        psmOverviewTable.addValueChangeListener((Property.ValueChangeEvent event) -> {
            getSpectrumData(psmOverviewTable.getValue());
        });

        this.SpectrumPlot = new SpectrumPlot();
        this.SpectrumPlot.setSizeFull();
        this.chartContainer.addComponent(SpectrumPlot);

        this.listener = (LayoutEvents.LayoutClickEvent event) -> {
            psmOverviewTable.setValue(((PSMSequenceChart) event.getComponent()).getObjectId());
        };

    }

    public void updateView(List<PSMObject> psms) {
        this.psmOverviewTable.removeAllItems();
        index = 1;
        psms.stream().map((psm) -> {
            return psm;
        }).forEachOrdered((psm) -> {
            PSMSequenceChart chart = new PSMSequenceChart(psm.getModifiedSequence(), psm.getIndex());
            chart.addLayoutClickListener(listener);
            this.psmOverviewTable.addItem(new Object[]{index++, chart, psm.getIdentificationCharge(), "" + psm.getPrecursorMZError_PPM(), psm.getConfidence(), psm.getValidation()}, psm.getIndex());
        });

    }
 
    public abstract void getSpectrumData(Object psmId);
}
/**+ "Spectrum & Fragment Ions ("
                            + Util.roundDouble(currentSpectrum.getPrecursor().getMz(), 2) + " m/z)"*/


