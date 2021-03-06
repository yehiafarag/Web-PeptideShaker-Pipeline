package com.uib.web.peptideshaker.presenter.core.filtercharts.updatedfilters;

import com.uib.web.peptideshaker.presenter.core.filtercharts.charts.*;
import com.ejt.vaadin.sizereporter.ComponentResizeEvent;
import com.ejt.vaadin.sizereporter.SizeReporter;
import com.google.common.collect.Sets;
import com.itextpdf.text.pdf.codec.Base64;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.SelectionManager;
import com.uib.web.peptideshaker.presenter.core.FilterButton;
import com.vaadin.data.Property;
import com.vaadin.event.LayoutEvents;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Slider;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeMap;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

/**
 * This class represents range component with lower and upper range
 *
 * @author Yehia Farag
 */
public abstract class DivaRangeFilter extends VerticalLayout implements Property.ValueChangeListener, RegistrableFilter {

    private final String title;
    private final String filterId;
    private final SelectionManager Selection_Manager;

    private final VerticalLayout chartContainer;
    private final Image chartImage;
    private final Slider upperRangeSlider;
    private final Slider lowerRangeSlider;
    private final AbsoluteLayout slidersContainer;
    private TreeMap<Comparable, Set<Comparable>> data;
    private final TreeMap<Comparable, Set<Comparable>> activeData;
    private final Label upperLabelValueComponent;
    private final Label lowerLableValueComponent;
    private int chartWidth;
    private int chartHeight;
    private final JFreeChart mainChart;
    private final GridLayout filterGridContainer;
    private final FilterButton removeFilterIcon;
    private int imageRepaintCounter = 0;

    public DivaRangeFilter(String title, String filterId, SelectionManager Selection_Manager) {

        this.title = title;
        this.filterId = filterId;
        this.Selection_Manager = Selection_Manager;
        this.activeData = new TreeMap<>();
        DivaRangeFilter.this.setSizeFull();
        DivaRangeFilter.this.setStyleName("thumbfilterframe");
        DivaRangeFilter.this.setSpacing(true);
        DivaRangeFilter.this.setMargin(new MarginInfo(false, false, false, false));

        Label chartTitle = new Label("<font style='padding-top: 10px;position: absolute;'>" + title + "</font>", ContentMode.HTML);

        chartTitle.setWidth(100, Unit.PERCENTAGE);
        chartTitle.setHeight(30, Unit.PIXELS);
        chartTitle.setStyleName(ValoTheme.LABEL_BOLD);
        chartTitle.addStyleName("resizeabletext");
        DivaRangeFilter.this.addComponent(chartTitle);
        DivaRangeFilter.this.setExpandRatio(chartTitle, 10);

        filterGridContainer = new GridLayout(3, 2);
        filterGridContainer.setSizeFull();
        filterGridContainer.setSpacing(false);
        filterGridContainer.setRowExpandRatio(0, 1.5f);
        filterGridContainer.setRowExpandRatio(1, 0.5f);
        filterGridContainer.setColumnExpandRatio(0, 10);
        filterGridContainer.setColumnExpandRatio(1, 80);
        filterGridContainer.setColumnExpandRatio(2, 10);

        DivaRangeFilter.this.addComponent(filterGridContainer);
        DivaRangeFilter.this.setExpandRatio(filterGridContainer, 90);
        this.Selection_Manager.RegistrDatasetsFilter(DivaRangeFilter.this);

        chartContainer = new VerticalLayout();
        chartContainer.setWidth(100, Unit.PERCENTAGE);
        chartContainer.setHeight(100, Unit.PERCENTAGE);
        chartContainer.setMargin(new MarginInfo(false, false, false, false));
        chartImage = new Image();
        chartImage.setSizeFull();
        chartContainer.addComponent(chartImage);
        SizeReporter reporter = new SizeReporter(chartContainer);
        mainChart = initChart();
        reporter.addResizeListener((ComponentResizeEvent event) -> {
            int tChartWidth = event.getWidth();
            int tChartHeight = event.getHeight();
            if (tChartWidth <= 0 || tChartHeight <= 0) {
                return;
            }
            if ((tChartWidth == chartWidth || Math.abs(tChartWidth - chartWidth) < 10) && (chartHeight == tChartHeight || Math.abs(tChartHeight - chartHeight) < 10)) {
                return;
            }
//            if (imageRepaintCounter < 4) {
//                imageRepaintCounter++;
//                return;
//            }
//            imageRepaintCounter = 3;
            chartWidth = tChartWidth;
            chartHeight = tChartHeight;
            chartImage.setSource(new ExternalResource(saveToFile(mainChart, chartWidth, chartHeight)));
        });

        filterGridContainer.addComponent(chartContainer, 1, 0);
        filterGridContainer.setComponentAlignment(chartContainer, Alignment.TOP_LEFT);
        lowerLableValueComponent = initLabel("");
        filterGridContainer.addComponent(lowerLableValueComponent, 0, 1);
        filterGridContainer.setComponentAlignment(lowerLableValueComponent, Alignment.MIDDLE_CENTER);

        slidersContainer = new AbsoluteLayout();
        slidersContainer.setStyleName("maxhight20");
        slidersContainer.setWidth(100, Unit.PERCENTAGE);
        slidersContainer.setHeight(100, Unit.PERCENTAGE);
        filterGridContainer.addComponent(slidersContainer, 1, 1);
        filterGridContainer.setComponentAlignment(slidersContainer, Alignment.MIDDLE_CENTER);
        lowerRangeSlider = new Slider();
        lowerRangeSlider.setWidth(100, Unit.PERCENTAGE);
        lowerRangeSlider.setStyleName("rangeslider");
        lowerRangeSlider.addStyleName("lower");
        lowerRangeSlider.addValueChangeListener(DivaRangeFilter.this);
        slidersContainer.addComponent(lowerRangeSlider, "left:0px; top:50%;");

        upperRangeSlider = new Slider();
        upperRangeSlider.setStyleName("rangeslider");
        upperRangeSlider.addStyleName("upper");
        upperRangeSlider.setWidth(100, Unit.PERCENTAGE);
        upperRangeSlider.addValueChangeListener(DivaRangeFilter.this);
        slidersContainer.addComponent(upperRangeSlider, "left:0px; top:50%;");

        upperLabelValueComponent = initLabel("");
        filterGridContainer.addComponent(upperLabelValueComponent, 2, 1);
        filterGridContainer.setComponentAlignment(upperLabelValueComponent, Alignment.MIDDLE_CENTER);

        removeFilterIcon = new FilterButton() {
            @Override
            public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                applyFilter(lowerRangeSlider.getMin(), lowerRangeSlider.getMax());

            }
        };
        removeFilterIcon.setWidth(25, Unit.PIXELS);
        removeFilterIcon.setHeight(25, Unit.PIXELS);
        removeFilterIcon.setVisible(false);
//        removeFilterIcon.setActiveBtn(true);
        removeFilterIcon.addStyleName("btninframe");

//        topLeftContainer.addComponent(removeFilterIcon);
//        topLeftContainer.setComponentAlignment(removeFilterIcon, Alignment.TOP_CENTER);
        DivaRangeFilter.this.addComponent(removeFilterIcon);
        DivaRangeFilter.this.setComponentAlignment(removeFilterIcon, Alignment.TOP_RIGHT);
        DivaRangeFilter.this.setExpandRatio(removeFilterIcon, 0.1f);

    }

    public void initializeFilterData(TreeMap<Comparable, Set<Comparable>> data) {
        activeData.clear();
        if (data.isEmpty()) {
            upperRangeSlider.setEnabled(false);
            lowerRangeSlider.setEnabled(false);
            chartContainer.removeAllComponents();
            return;
        }

        upperRangeSlider.setEnabled(true);
        lowerRangeSlider.setEnabled(true);
        double min = Double.valueOf(data.firstKey() + "");
        double max = Double.valueOf(data.lastKey() + "");
        upperRangeSlider.removeValueChangeListener(DivaRangeFilter.this);
        lowerRangeSlider.removeValueChangeListener(DivaRangeFilter.this);
        lowerRangeSlider.setMin(min);
        lowerRangeSlider.setMax(max);
        lowerRangeSlider.setValue(min);
        upperRangeSlider.setMin(min);
        upperRangeSlider.setMax(max);
        upperRangeSlider.setValue(max);

        upperRangeSlider.addValueChangeListener(DivaRangeFilter.this);
        lowerRangeSlider.addValueChangeListener(DivaRangeFilter.this);
        upperLabelValueComponent.setValue("<center>" + (int) max + "</center>");
        lowerLableValueComponent.setValue("<center>" + (int) min + "</center>");
        activeData.putAll(data);
        updateChartDataset();
        if (this.data == null) {
            this.data = data;
//            redrawRangeOnChart(min, max);
        }

    }

    private void updateChartDataset() {
        XYPlot plot = ((XYPlot) mainChart.getPlot());
        XYSeriesCollection dataset = (XYSeriesCollection) plot.getDataset();
        dataset.removeAllSeries();
        final XYSeries series1 = new XYSeries("rangeData");
//        for (Comparable value : activeData.keySet()) {
//            System.out.println("at value "+value);
//            series1.add((int) value, scaleValues(activeData.get(value).size(), 100, 5));
//
//        }
        for (int index = (int) lowerRangeSlider.getMin(); index <= (int) lowerRangeSlider.getMax(); index++) {
            if (activeData.containsKey(index)) {
                series1.add(index, scaleValues(activeData.get(index).size(), 100, 10));
            } else {
                series1.add(index, 0);
            }

        }
        dataset.addSeries(series1);
        XYAreaRenderer renderer = (XYAreaRenderer) ((XYPlot) mainChart.getPlot()).getRenderer();
        renderer.setSeriesPaint(0, new Color(211, 211, 211), true);
        if (lowerRangeSlider.getMin() > 0) {
            ((NumberAxis) plot.getDomainAxis()).setAutoRangeMinimumSize(lowerRangeSlider.getMin());
            
        }
         ((NumberAxis) plot.getDomainAxis()).setFixedAutoRange(lowerRangeSlider.getMax()-lowerRangeSlider.getMin());

    }

    private JFreeChart initChart() {

        XYSeriesCollection dataset = new XYSeriesCollection();
        final NumberAxis domainAxis = new NumberAxis();
        domainAxis.setVisible(false);
        domainAxis.setAutoRange(true);        
        domainAxis.setAutoRangeIncludesZero(false);

        final NumberAxis rangeAxis = new NumberAxis() {

        };

        rangeAxis.setVisible(false);
        XYAreaRenderer renderer = new XYAreaRenderer();

        renderer.setSeriesPaint(0, new Color(211, 211, 211), true);

        XYPlot plot = new XYPlot(dataset, domainAxis, rangeAxis, renderer);
        plot.setOutlineVisible(false);
        plot.setRangeAxisLocation(AxisLocation.TOP_OR_RIGHT);

        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinesVisible(false);
        plot.getDomainAxis().setLowerMargin(0.0D);
        plot.getDomainAxis().setUpperMargin(0.0D);
        plot.setBackgroundPaint(Color.WHITE);
        final JFreeChart chart = new JFreeChart(plot);
        chart.setPadding(new RectangleInsets(0.0, 0.0, 0.0, 0.0));
        plot.setAxisOffset(new RectangleInsets(0, 0, 0, 0));
        chart.setBorderPaint(null);
        chart.setBackgroundPaint(null);
        return chart;

    }

    /**
     * Convert JFree chart into image and encode it as base64 string to be used
     * as image link.
     *
     * @param chart JFree chart instance
     * @param width Image width
     * @param height Image height.
     */
    private String saveToFile(final JFreeChart chart, int width, int height) {
        byte imageData[];
        try {
            chart.getLegend().setVisible(false);
            imageData = ChartUtilities.encodeAsPNG(chart.createBufferedImage(width, height));
            String base64 = Base64.encodeBytes(imageData);
            base64 = "data:image/png;base64," + base64;
            return base64;
        } catch (IOException e) {
            System.err.println("at error " + e.getMessage());
        }
        return "";
    }

    public void valueChange() {
        upperRangeSlider.removeStyleName("lower");
        upperRangeSlider.removeStyleName("upper");
        lowerRangeSlider.removeStyleName("lower");
        lowerRangeSlider.removeStyleName("upper");
        AbsoluteLayout.ComponentPosition lowerPos = slidersContainer.getPosition(lowerRangeSlider);
        AbsoluteLayout.ComponentPosition upperPos = slidersContainer.getPosition(upperRangeSlider);
        slidersContainer.removeAllComponents();
        double min;
        double max;

        if (lowerRangeSlider.getValue() <= upperRangeSlider.getValue() && slidersContainer.getComponentCount() == 0) {
            slidersContainer.addComponent(lowerRangeSlider, lowerPos.getCSSString());
            slidersContainer.addComponent(upperRangeSlider, upperPos.getCSSString());
            upperRangeSlider.addStyleName("upper");
            lowerRangeSlider.addStyleName("lower");
            min = lowerRangeSlider.getValue();
            max = upperRangeSlider.getValue();

        } else {
            slidersContainer.addComponent(upperRangeSlider, upperPos.getCSSString());
            slidersContainer.addComponent(lowerRangeSlider, lowerPos.getCSSString());
            lowerRangeSlider.addStyleName("upper");
            upperRangeSlider.addStyleName("lower");
            min = upperRangeSlider.getValue();
            max = lowerRangeSlider.getValue();

        }

        applyFilter(min, max);

    }

    private void applyFilter(double min, double max) {
        LinkedHashSet filter = new LinkedHashSet<>();
        if (min != lowerRangeSlider.getMin() || max != lowerRangeSlider.getMax()) {
            filter.addAll(Arrays.asList(new Comparable[]{min, max}));
        }
        Selection_Manager.setSelection("dataset_filter_selection", filter, null, filterId);
    }

    @Override
    public void updateFilterSelection(Set<Comparable> selectedItems, Set<Comparable> selectedCategories, boolean topFilter, boolean singleProteinsFilter, boolean selfAction) {

        if (!selfAction) {
            if (selectedItems == null || selectedItems.isEmpty()) {
                filterGridContainer.setVisible(false);
                System.out.println("its empty selection ");
                return;
            } else {
                filterGridContainer.setVisible(true);
            }

//            redrawRangeOnChart(min, max);
            if (singleProteinsFilter && !selfAction && !selectedCategories.isEmpty()) {
                //reset filter value to oreginal 
                initializeFilterData(this.data);
            } else {
                TreeMap<Comparable, Set<Comparable>> tPieChartValues = new TreeMap<>();
                for (Comparable key : data.keySet()) {
                    LinkedHashSet<Comparable> tSet = new LinkedHashSet<>(Sets.intersection(data.get(key), selectedItems));
                    if (!tSet.isEmpty()) {
                        tPieChartValues.put(key, new LinkedHashSet<>(Sets.intersection(data.get(key), selectedItems)));
                    }
                }

                initializeFilterData(tPieChartValues);
            }
        }
        if (selectedCategories == null || selectedCategories.isEmpty()) {
            if (chartWidth <= 0 || chartHeight <= 0) {
                return;
            }
            XYSeriesCollection dataset = (XYSeriesCollection) ((XYPlot) mainChart.getPlot()).getDataset();
            if (dataset.getSeriesCount() == 2) {
                dataset.removeSeries(0);
            }
            XYAreaRenderer renderer = (XYAreaRenderer) ((XYPlot) mainChart.getPlot()).getRenderer();

            renderer.setSeriesPaint(0, new Color(211, 211, 211), true);
            chartImage.setSource(new ExternalResource(saveToFile(mainChart, chartWidth, chartHeight)));
            setMainAppliedFilter(false);
            return;
        }
        double min = Math.min(lowerRangeSlider.getValue(), upperRangeSlider.getValue());
        double max = Math.max(lowerRangeSlider.getValue(), upperRangeSlider.getValue());

        double tmin = (double) selectedCategories.toArray()[0];
        double tmax;
        if (selectedCategories.size() == 1) {
            tmax = min;
        } else {
            tmax = (double) selectedCategories.toArray()[1];
        }
        min = Math.max(min, tmin);
        max = Math.min(max, tmax);
        redrawRangeOnChart(min, max);
        setMainAppliedFilter(topFilter);
//        selectSlice(selectedCategories);
    }

    private Label initLabel(String labelValue) {
        Label label = new Label("<center>" + labelValue + "</center>", ContentMode.HTML);
        label.setStyleName(ValoTheme.LABEL_TINY);
        label.addStyleName(ValoTheme.LABEL_SMALL);
        label.setWidth(100, Unit.PERCENTAGE);
        label.addStyleName("maxhight20");
        return label;

    }

    private void redrawRangeOnChart(double start, double end) {
//         = (double) selectedCategories.toArray()[0];
//        = (double) selectedCategories.toArray()[1];

        if (start != -1 && end != -1) {
            upperRangeSlider.removeValueChangeListener(DivaRangeFilter.this);
            lowerRangeSlider.removeValueChangeListener(DivaRangeFilter.this);
            if (lowerRangeSlider.getStyleName().contains("lower")) {
                lowerRangeSlider.setValue(start);
                upperRangeSlider.setValue(end);
            } else {
                upperRangeSlider.setValue(start);
                lowerRangeSlider.setValue(end);
            }
            upperRangeSlider.addValueChangeListener(DivaRangeFilter.this);
            lowerRangeSlider.addValueChangeListener(DivaRangeFilter.this);

        }

        XYSeriesCollection dataset = (XYSeriesCollection) ((XYPlot) mainChart.getPlot()).getDataset();
        if (dataset.getSeriesCount() == 2) {
            dataset.removeSeries(0);
        }

        final XYSeries series1 = new XYSeries("highlightedData");
        final XYSeries series2 = dataset.getSeries("rangeData");
        int counter = 0;
//        for (Comparable value : activeData.keySet()) {
//            int key = (int) value;
//            if (key >= start && key <= end) {
//                series1.add(series2.getDataItem(counter).getX(), series2.getDataItem(counter).getYValue());
//
//            }
//            counter++;
//
//        }
        for (int index = 0; index < series2.getItemCount(); index++) {
            double value = series2.getDataItem(index).getXValue();
            if (value >= start && value <= end) {
                series1.add(value, series2.getDataItem(index).getYValue());
            }

        }

        dataset.removeAllSeries();
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        XYAreaRenderer renderer = (XYAreaRenderer) ((XYPlot) mainChart.getPlot()).getRenderer();
        renderer.setSeriesPaint(1, new Color(211, 211, 211), true);
        renderer.setSeriesPaint(0, new Color(186, 213, 242), true);

//        if (upperRangeSlider.getMax() == upperRangeSlider.getMin()) {
//            ((XYPlot) mainChart.getPlot()).getDomainAxis().setVisible(false);
//           
////            ((CategoryPlot) mainChart.getPlot()).getRangeAxis().setVisible(true);
//        } else {
//            ((XYPlot) mainChart.getPlot()).getDomainAxis().setVisible(false);
////            ((CategoryPlot) mainChart.getPlot()).getRangeAxis().setVisible(false);
////            ((NumberAxis) ((CategoryPlot) mainChart.getPlot()).getRangeAxis()).setTickUnit(new NumberTickUnit(1));
//        }
        if (chartWidth <= 0 || chartHeight <= 0) {
            return;
        }
        chartImage.setVisible(true);
        chartImage.setSource(new ExternalResource(saveToFile(mainChart, chartWidth, chartHeight)));
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event
    ) {
        valueChange();
    }

    @Override
    public String getFilterId() {
        return filterId;
    }

    @Override
    public void redrawChart() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Converts the value from linear scale to log scale. The log scale numbers
     * are limited by the range of the type float. The linear scale numbers can
     * be any double value.
     *
     * @param linearValue the value to be converted to log scale
     * @param max The upper limit number for the input numbers
     * @param lowerLimit the lower limit for the input numbers
     * @return the value in log scale
     */
    private double scaleValues(double linearValue, double max, double lowerLimit) {
        double logMax = (Math.log(max) / Math.log(2));
        double logValue = (Math.log(linearValue) / Math.log(2));
        logValue = ((max / logMax) * logValue) + lowerLimit;//(max/Math.log(max))*Math.log(linearValue)+10;
        return logValue;

//        return Math.min(logValue, max);
    }

    @Override
    public void suspendFilter(boolean suspend) {
    }

    private void setMainAppliedFilter(boolean mainAppliedFilter) {
        removeFilterIcon.setVisible(mainAppliedFilter);
        if (mainAppliedFilter) {
            this.addStyleName("highlightfilter");
        } else {
            this.removeStyleName("highlightfilter");

            upperRangeSlider.removeValueChangeListener(DivaRangeFilter.this);
            lowerRangeSlider.removeValueChangeListener(DivaRangeFilter.this);
            lowerRangeSlider.setValue(lowerRangeSlider.getMin());
            upperRangeSlider.setValue(lowerRangeSlider.getMax());

            upperRangeSlider.addValueChangeListener(DivaRangeFilter.this);
            lowerRangeSlider.addValueChangeListener(DivaRangeFilter.this);
        }

    }

}
