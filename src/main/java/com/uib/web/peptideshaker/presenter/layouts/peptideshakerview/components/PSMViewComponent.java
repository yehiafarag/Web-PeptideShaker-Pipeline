/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components;

import com.uib.web.peptideshaker.galaxy.dataobjects.PSMObject;
import com.uib.web.peptideshaker.presenter.core.ColorLabel;
import com.uib.web.peptideshaker.presenter.core.SparkLineLabel;
import com.uib.web.peptideshaker.presenter.core.ValidationLabel;
import com.uib.web.peptideshaker.presenter.pscomponents.SecondarySpectraChartsGenerator;
import com.uib.web.peptideshaker.presenter.pscomponents.SpectrumInformation;
import com.uib.web.peptideshaker.presenter.pscomponents.SpectrumPlot;
import com.vaadin.data.Property;
import com.vaadin.event.LayoutEvents;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Yehia Farag
 */
public abstract class PSMViewComponent extends VerticalLayout {

    private final Table psmOverviewTable;
    private final VerticalLayout chartContainer;
    private final SpectrumPlot SpectrumPlot;
    private int index = 0;
    private final DecimalFormat df = new DecimalFormat("#.##");

    public SpectrumPlot getSpectrumPlot() {
        return SpectrumPlot;
    }
    private final LayoutEvents.LayoutClickListener listener;
    private Map<Object, SpectrumInformation> spectrumInformationMap;

    public PSMViewComponent() {
        PSMViewComponent.this.setSizeFull();
        PSMViewComponent.this.setSpacing(true);
        psmOverviewTable = new Table() {
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
        psmOverviewTable.addContainerProperty("id", ColorLabel.class, null, "ID", null, Table.Align.CENTER);
//        psmOverviewTable.addContainerProperty("sequence", String.class, null, "Sequence", null, Table.Align.LEFT);
        psmOverviewTable.addContainerProperty("sequenceFrag", VerticalLayout.class, null, "Sequence Fragmentation", null, Table.Align.LEFT);
        psmOverviewTable.addContainerProperty("massErrorPlot", VerticalLayout.class, null, "Mass Error Plot", null, Table.Align.LEFT);
        psmOverviewTable.addContainerProperty("charge", SparkLineLabel.class, null, "Charge", null, Table.Align.LEFT);
        psmOverviewTable.addContainerProperty("mzError", SparkLineLabel.class, null, "m/z Error", null, Table.Align.LEFT);

        psmOverviewTable.addContainerProperty("confidence", SparkLineLabel.class, null, "Confidence", null, Table.Align.LEFT);
        psmOverviewTable.addContainerProperty("validation", ValidationLabel.class, null, "", null, Table.Align.CENTER);
        psmOverviewTable.setColumnWidth("index", 50);
        psmOverviewTable.setColumnWidth("id", 37);
        psmOverviewTable.setColumnWidth("validation", 32);
        psmOverviewTable.setColumnWidth("confidence", 170);
        psmOverviewTable.setColumnWidth("charge", 150);
        psmOverviewTable.setColumnWidth("mzError", 170);
        psmOverviewTable.setColumnWidth("massErrorPlot", 274);
        psmOverviewTable.setSortContainerPropertyId("charge");
        PSMViewComponent.this.addComponent(psmOverviewTable);
        PSMViewComponent.this.setExpandRatio(psmOverviewTable, 0.25f);
        chartContainer = new VerticalLayout();
        chartContainer.setSizeFull();
        this.chartContainer.addStyleName("inframetable");
        PSMViewComponent.this.addComponent(chartContainer);
        PSMViewComponent.this.setExpandRatio(chartContainer, 0.75f);

        psmOverviewTable.addValueChangeListener((Property.ValueChangeEvent event) -> {
            SpectrumInformation spectrumInformation = spectrumInformationMap.get(psmOverviewTable.getValue());
            this.getSpectrumPlot().selectedSpectrum(spectrumInformation.getSpectrum(), spectrumInformation.getCharge(), spectrumInformation.getFragmentIonAccuracy(), spectrumInformation.getIdentificationParameters(), spectrumInformation.getSpectrumMatch());

        });

        this.SpectrumPlot = new SpectrumPlot();
        this.SpectrumPlot.setSizeFull();
        this.chartContainer.addComponent(SpectrumPlot);

        this.listener = (LayoutEvents.LayoutClickEvent event) -> {
            psmOverviewTable.setValue(((VerticalLayout) (event.getComponent())).getData());
        };
        psmOverviewTable.addColumnResizeListener((Table.ColumnResizeEvent event) -> {
            psmOverviewTable.setColumnWidth(event.getPropertyId(), event.getPreviousWidth());
        });

    }

    public void updateView(List<PSMObject> psms, int peptideLength) {
        this.psmOverviewTable.removeAllItems();
//        psmOverviewTable.setColumnWidth("sequenceFrag", peptideLength * 15);
        index = 1;
        spectrumInformationMap = getSpectrumData(psms);
        psms.stream().map((psm) -> {
            return psm;
        }).forEachOrdered((psm) -> {
            SecondarySpectraChartsGenerator chartGenerator = new SecondarySpectraChartsGenerator(psm.getModifiedSequence(), psm.getIndex(), spectrumInformationMap.get(psm.getIndex()));
            chartGenerator.getSequenceFragmentationChart().addLayoutClickListener(listener);
            chartGenerator.getMassErrorPlot().addLayoutClickListener(listener);
            int charge = Integer.parseInt(psm.getIdentificationCharge().replace("+", ""));
            Map<String, Number> values = new LinkedHashMap<>();
            values.put("greenlayout", (float) charge / (float) spectrumInformationMap.get(psm.getIndex()).getMaxCharge());
            SparkLineLabel chargeLabel = new SparkLineLabel(charge + "", values, psm.getIndex()) {
                @Override
                public void selected(Object itemId) {
                    psmOverviewTable.setValue(itemId);
                }
            };

            double mzError = psm.getPrecursorMZError_PPM();
            Map<String, Number> mzErrorValues = new LinkedHashMap<>();
            mzErrorValues.put("greenlayout", (float) mzError / ((float) spectrumInformationMap.get(psm.getIndex()).getMzError() * 2.0f));

            SparkLineLabel mzErrorLabel = new SparkLineLabel(df.format(mzError) + "", mzErrorValues, psm.getIndex()) {
                @Override
                public void selected(Object itemId) {
                    psmOverviewTable.setValue(itemId);
                }
            };

            Map<String, Number> confidentValues = new LinkedHashMap<>();
            confidentValues.put("greenlayout", (float) psm.getConfidence() / 100f);
            SparkLineLabel confidentLabel = new SparkLineLabel(df.format(psm.getConfidence()), confidentValues, psm.getIndex()) {
                @Override
                public void selected(Object itemId) {
                    psmOverviewTable.setValue(itemId);
                }
            };
            ValidationLabel validation = new ValidationLabel(psm.getValidation());
            this.psmOverviewTable.addItem(new Object[]{index++, new ColorLabel(0,"Not Available"), chartGenerator.getSequenceFragmentationChart(), chartGenerator.getMassErrorPlot(), chargeLabel, mzErrorLabel, confidentLabel, validation}, psm.getIndex());
        });
        this.psmOverviewTable.setSortContainerPropertyId("charge");
        this.psmOverviewTable.sort();
        index = 1;
        psmOverviewTable.getItemIds().forEach((id) -> {
            this.psmOverviewTable.getItem(id).getItemProperty("index").setValue(index++);
        });

    }

    public abstract Map<Object, SpectrumInformation> getSpectrumData(List<PSMObject> psms);
}
/**
 * + "Spectrum & Fragment Ions (" +
 * Util.roundDouble(currentSpectrum.getPrecursor().getMz(), 2) + " m/z)"
 *
 *
 *
 *
 *  // the index column psmTable.getColumn(" ").setMaxWidth(50);
 * psmTable.getColumn(" ").setMinWidth(50);
 *
 * try { psmTable.getColumn("Confidence").setMaxWidth(90);
 * psmTable.getColumn("Confidence").setMinWidth(90); } catch
 * (IllegalArgumentException w) { psmTable.getColumn("Score").setMaxWidth(90);
 * psmTable.getColumn("Score").setMinWidth(90); }
 *
 * // the validated column psmTable.getColumn("").setMaxWidth(30);
 * psmTable.getColumn("").setMinWidth(30);
 *
 * // the selected columns psmTable.getColumn(" ").setMaxWidth(30);
 * psmTable.getColumn(" ").setMinWidth(30);
 *
 * // the protein inference column psmTable.getColumn("ID").setMaxWidth(37);
 * psmTable.getColumn("ID").setMinWidth(37);
 *
 * // set up the psm color map HashMap<Integer, java.awt.Color> psmColorMap =
 * new HashMap<Integer, java.awt.Color>();
 * psmColorMap.put(SpectrumIdentificationPanel.AGREEMENT_WITH_MODS,
 * peptideShakerGUI.getSparklineColor()); // id software agree with PTM
 * certainty psmColorMap.put(SpectrumIdentificationPanel.AGREEMENT,
 * java.awt.Color.CYAN); // id software agree on peptide but not ptm certainty
 * psmColorMap.put(SpectrumIdentificationPanel.CONFLICT, java.awt.Color.YELLOW);
 * // id software don't agree
 * psmColorMap.put(SpectrumIdentificationPanel.PARTIALLY_MISSING,
 * java.awt.Color.ORANGE); // some id software id'ed some didn't
 *
 * // set up the psm tooltip map HashMap<Integer, String> psmTooltipMap = new
 * HashMap<Integer, String>();
 * psmTooltipMap.put(SpectrumIdentificationPanel.AGREEMENT_WITH_MODS, "ID
 * Software Agree"); psmTooltipMap.put(SpectrumIdentificationPanel.AGREEMENT,
 * "ID Software Agree - PTM Certainty Issues");
 * psmTooltipMap.put(SpectrumIdentificationPanel.CONFLICT, "ID Software
 * Disagree"); psmTooltipMap.put(SpectrumIdentificationPanel.PARTIALLY_MISSING,
 * "First Hit(s) Missing");
 *
 *
 *
 */
