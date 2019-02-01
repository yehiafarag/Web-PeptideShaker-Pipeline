/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components;

import com.ejt.vaadin.sizereporter.ComponentResizeEvent;
import com.ejt.vaadin.sizereporter.ComponentResizeListener;
import com.ejt.vaadin.sizereporter.SizeReporter;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PSMObject;
import com.uib.web.peptideshaker.presenter.core.ResizeTableControlButtons;
import com.uib.web.peptideshaker.presenter.core.SparkLineLabel;
import com.uib.web.peptideshaker.presenter.core.ValidationLabel;
import com.uib.web.peptideshaker.presenter.pscomponents.SecondarySpectraChartsGenerator;
import com.uib.web.peptideshaker.presenter.pscomponents.SpectrumInformation;
import com.uib.web.peptideshaker.presenter.pscomponents.SpectrumPlot;
import com.vaadin.data.Property;
import com.vaadin.event.LayoutEvents;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Image;
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
    private final AbsoluteLayout psmTableWrapper;
    private final Property.ValueChangeListener psmlistener;
    private final VerticalLayout chartContainer;
    private final SpectrumPlot spectrumPlot;
    private int index = 0;
    private final DecimalFormat df = new DecimalFormat("0.00E00");//new DecimalFormat("#.##");
    private final DecimalFormat df1 = new DecimalFormat("#.##");

    public SpectrumPlot getSpectrumPlot() {
        return spectrumPlot;
    }
    private final LayoutEvents.LayoutClickListener listener;
    private Map<Object, SpectrumInformation> spectrumInformationMap;

    public void viewSpectraPlot(boolean view) {
        chartContainer.setVisible(view);
        if (!view) {
            psmTableWrapper.addStyleName("nomargintop");
        } else {
            psmTableWrapper.removeStyleName("nomargintop");
        }
    }

    public void viewPSMTable(boolean view) {
        psmTableWrapper.setVisible(view);
        psmTableWrapper.setSizeFull();
        psmOverviewTable.setSizeFull();

    }

    public PSMViewComponent() {
        PSMViewComponent.this.setSizeFull();
        PSMViewComponent.this.setSpacing(true);

        final VerticalLayout splitpanel = new VerticalLayout();
        splitpanel.setSizeFull();

        PSMViewComponent.this.addComponent(splitpanel);

        psmTableWrapper = new AbsoluteLayout();
        psmTableWrapper.setSizeFull();

        psmOverviewTable = new Table() {
            DecimalFormat df = new DecimalFormat("0.00E00");// new DecimalFormat("#.##");
            DecimalFormat df1 = new DecimalFormat("#.##");

            @Override
            protected String formatPropertyValue(Object rowId, Object colId, Property property) {
                Object v = property.getValue();
                if (v instanceof Double) {
                    if ((double) v == 1) {
                        return "1.00";
                    }
                    if ((double) v > 100) {
                        return df.format(v);
                    } else {
                        return df1.format(v);
                    }
                }
                return super.formatPropertyValue(rowId, colId, property);
            }
        };
        psmTableWrapper.addComponent(psmOverviewTable, "left:0px;bottom:0px");
        psmOverviewTable.setStyleName(ValoTheme.TABLE_COMPACT);
        psmOverviewTable.addStyleName(ValoTheme.TABLE_SMALL);
        psmOverviewTable.addStyleName("framedpanel");
        psmOverviewTable.addStyleName("inframetable");
        psmOverviewTable.addStyleName("psmtable");
        psmOverviewTable.addStyleName(ValoTheme.TABLE_NO_STRIPES);
        psmOverviewTable.setSelectable(true);
        psmOverviewTable.setNullSelectionAllowed(false);
        psmOverviewTable.setMultiSelect(false);
        psmOverviewTable.setWidth(100, Unit.PERCENTAGE);
        psmOverviewTable.setHeight(100, Unit.PERCENTAGE);
        psmOverviewTable.addContainerProperty("index", Integer.class, null, "", null, Table.Align.RIGHT);
//        psmOverviewTable.addContainerProperty("id", ColorLabel.class, null, "ID", null, Table.Align.CENTER);
        psmOverviewTable.addContainerProperty("sequenceFrag", VerticalLayout.class, null, generateCaptionWithTooltio("Fragmentation", "Sequence Fragmentation"), null, Table.Align.LEFT);
        psmOverviewTable.addContainerProperty("massErrorPlot", VerticalLayout.class, null, generateCaptionWithTooltio("Mass Error Plot", "Mass Error Plot"), null, Table.Align.LEFT);
        psmOverviewTable.addContainerProperty("charge", SparkLineLabel.class, null, generateCaptionWithTooltio("Charge", "Charge"), null, Table.Align.LEFT);
        psmOverviewTable.addContainerProperty("mzError", SparkLineLabel.class, null, generateCaptionWithTooltio("Mass Error", "Mass Error"), null, Table.Align.LEFT);

        psmOverviewTable.addContainerProperty("confidence", SparkLineLabel.class, null, generateCaptionWithTooltio("Confidence", "Confidence level"), null, Table.Align.LEFT);
        psmOverviewTable.addContainerProperty("validation", ValidationLabel.class, null, generateCaptionWithTooltio("", "Protein validation"), null, Table.Align.CENTER);
        psmOverviewTable.setColumnWidth("index", 50);
//        psmOverviewTable.setColumnWidth("id", 37);
        psmOverviewTable.setColumnWidth("validation", 32);
        psmOverviewTable.setColumnWidth("confidence", 170);
        psmOverviewTable.setColumnWidth("charge", 130);
        psmOverviewTable.setColumnWidth("mzError", 180);
        psmOverviewTable.setColumnWidth("massErrorPlot", 300);
        psmOverviewTable.setSortContainerPropertyId("charge");

        splitpanel.setStyleName("nonscrollsplitpanel");
        chartContainer = new VerticalLayout();
        chartContainer.setSizeFull();
        this.chartContainer.addStyleName("inframetable");
        splitpanel.addComponent(chartContainer);
        splitpanel.addComponent(psmTableWrapper);

        psmlistener = (Property.ValueChangeEvent event) -> {
            SpectrumInformation spectrumInformation = spectrumInformationMap.get(psmOverviewTable.getValue());
            this.getSpectrumPlot().selectedSpectrum(spectrumInformation.getSpectrum(), spectrumInformation.getCharge(), spectrumInformation.getFragmentIonAccuracy(), spectrumInformation.getIdentificationParameters(), spectrumInformation.getSpectrumMatch());
        };
        psmOverviewTable.addValueChangeListener(psmlistener);

        this.spectrumPlot = new SpectrumPlot();
        this.spectrumPlot.setSizeFull();
        this.chartContainer.addComponent(spectrumPlot);

        this.listener = (LayoutEvents.LayoutClickEvent event) -> {
            psmOverviewTable.setValue(((VerticalLayout) (event.getComponent())).getData());
        };

        Table.ColumnResizeListener columnResizeListener = ((Table.ColumnResizeEvent event) -> {
            psmOverviewTable.setColumnWidth(event.getPropertyId(), event.getPreviousWidth());
        });

        psmOverviewTable.addColumnResizeListener(columnResizeListener);
        SizeReporter tableResporter = new SizeReporter(psmOverviewTable);
        tableResporter.addResizeListener((ComponentResizeEvent event) -> {
            int height = event.getHeight();
            int actH = 37 + (74 * psmOverviewTable.getItemIds().size());
            int fragWidth = event.getWidth() - (862) - 9;
            if ((actH > height)) {
                fragWidth -= 20;
            }
            if (fragWidth < 1) {
                return;
            }
            psmOverviewTable.removeColumnResizeListener(columnResizeListener);
            psmOverviewTable.setColumnWidth("index", 50);
            psmOverviewTable.setColumnWidth("sequenceFrag", fragWidth);
            psmOverviewTable.setColumnWidth("validation", 32);
            psmOverviewTable.setColumnWidth("confidence", 170);
            psmOverviewTable.setColumnWidth("charge", 130);
            psmOverviewTable.setColumnWidth("mzError", 180);
            psmOverviewTable.setColumnWidth("massErrorPlot", 300);
            psmOverviewTable.addColumnResizeListener(columnResizeListener);
        });

//       
//        resizeControlBtn.resize(2);
    }

    public void updateView(List<PSMObject> psms, String tooltip, int peptideLength) {
        this.psmOverviewTable.removeValueChangeListener(psmlistener);
        this.psmOverviewTable.removeAllItems();
        index = 1;
        spectrumInformationMap = getSpectrumData(psms);
        if (spectrumInformationMap == null) {
            return;
        }
        psms.stream().map((psm) -> {
            return psm;
        }).forEachOrdered((psm) -> {
            SecondarySpectraChartsGenerator chartGenerator = new SecondarySpectraChartsGenerator(psm.getModifiedSequence(), tooltip, psm.getIndex(), spectrumInformationMap.get(psm.getIndex()));
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

            double mzError = Math.abs(psm.getPrecursorMZError_PPM());
            Map<String, Number> mzErrorValues = new LinkedHashMap<>();
            mzErrorValues.put("greenlayout", (float) mzError / ((float) spectrumInformationMap.get(psm.getIndex()).getMzError() * 2.0f));

            SparkLineLabel mzErrorLabel = new SparkLineLabel(df1.format(mzError) + "", mzErrorValues, psm.getIndex()) {
                @Override
                public void selected(Object itemId) {
                    psmOverviewTable.setValue(itemId);
                }
            };

            Map<String, Number> confidentValues = new LinkedHashMap<>();
            confidentValues.put("greenlayout", (float) psm.getConfidence() / 100f);
            SparkLineLabel confidentLabel = new SparkLineLabel(df1.format(psm.getConfidence()), confidentValues, psm.getIndex()) {
                @Override
                public void selected(Object itemId) {
                    psmOverviewTable.setValue(itemId);
                }
            };
            ValidationLabel validation = new ValidationLabel(psm.getValidation());
            this.psmOverviewTable.addItem(new Object[]{index++, chartGenerator.getSequenceFragmentationChart(), chartGenerator.getMassErrorPlot(), chargeLabel, mzErrorLabel, confidentLabel, validation}, psm.getIndex());
        });
        this.psmOverviewTable.setSortContainerPropertyId("confidence");
        this.psmOverviewTable.setSortAscending(false);
        this.psmOverviewTable.sort();
        index = 1;
        psmOverviewTable.getItemIds().forEach((id) -> {
            this.psmOverviewTable.getItem(id).getItemProperty("index").setValue(index++);
        });
        this.psmOverviewTable.addValueChangeListener(psmlistener);
        spectrumPlot.setDisableSizeReporter(false);
        psmOverviewTable.setValue(psmOverviewTable.firstItemId());
        psmOverviewTable.commit();
//        

    }

    public abstract Map<Object, SpectrumInformation> getSpectrumData(List<PSMObject> psms);

    public void setThumbImage(Image thumbImage) {
        spectrumPlot.setPlotThumbImage(thumbImage);
    }

    private String generateCaptionWithTooltio(String caption, String tooltip) {

        return "<div class='tooltip'>" + caption + "<span class='tooltiptext'>" + tooltip + "</span></div>";

    }
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
