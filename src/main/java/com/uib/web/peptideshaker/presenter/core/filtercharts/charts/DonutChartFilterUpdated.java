package com.uib.web.peptideshaker.presenter.core.filtercharts.charts;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.google.common.collect.Sets;
import com.uib.web.peptideshaker.presenter.components.peptideshakerview.components.SelectionManager;
import com.uib.web.peptideshaker.presenter.core.CloseButton;
import com.uib.web.peptideshaker.presenter.core.form.ColorLabel;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

/**
 * This class represents matrix layout filter
 *
 * @author Yehia Farag
 */
public abstract class DonutChartFilterUpdated extends AbsoluteLayout implements RegistrableFilter {

    private final Panel donutChartContainerPanel;
    private final String title;
    private final String filterId;
//    private  LineConfig<String, Integer>  donutConfig;

    private ChartJs chart;
    private VerticalLayout chartContainer;
    private final VerticalLayout thumbFilterContainer;
    private final Label thumbTitle;
//    private ChartConfiguration pieConfiguration;

    private final Label chartTitle;
    private final Set<Object> selectedCategories;
    private final Set<Object> appliedFilters;
    private Map<String, Set<String>> fullData;
    private final Map<String, Set<String>> filteredData;

    private int totalItemsNumber;
    private final SelectionManager Selection_Manager;
    private final VerticalLayout legendContainer;
    private final VerticalLayout thumbLegendContainer;
    private final Map<String, Color> colorsMap;
    private final Map<String, ColorLabel> colorsLabelMap;
    private final Set<String> selectedData;
    private final Map<String, String> chartColors;
    private final VerticalLayout thumbChartContainer;

    @Override
    public boolean isAppliedFilter() {
        return !(selectedData.isEmpty() || selectedData.size() == totalItemsNumber);
    }

    public DonutChartFilterUpdated(String title, String filterId, SelectionManager Selection_Manager) {

        this.title = title;
        this.filterId = filterId;
        this.Selection_Manager = Selection_Manager;
        selectedData = new LinkedHashSet<>();
        this.appliedFilters = new LinkedHashSet<>();
        DonutChartFilterUpdated.this.setWidth(100, Unit.PERCENTAGE);
        DonutChartFilterUpdated.this.setHeight(100, Unit.PERCENTAGE);
        VerticalLayout filterContainer = new VerticalLayout();
        filterContainer.setSizeFull();
        filterContainer.setMargin(new MarginInfo(true, true, false, true));
        DonutChartFilterUpdated.this.addComponent(filterContainer);

        chartTitle = new Label("" + title + " (100000/1000000)");
        chartTitle.setContentMode(ContentMode.HTML);
        chartTitle.setStyleName(ValoTheme.LABEL_BOLD);
        chartTitle.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        chartTitle.setWidth(100, Unit.PERCENTAGE);
        chartTitle.setHeight(30, Unit.PIXELS);
        DonutChartFilterUpdated.this.addComponent(chartTitle, "left: " + 20 + "px; top: " + 10 + "px");
        Button cancelSelectionButton = new Button(VaadinIcons.CLOSE);
        cancelSelectionButton.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        cancelSelectionButton.addStyleName(ValoTheme.BUTTON_TINY);
        cancelSelectionButton.setWidth(25, Unit.PIXELS);
        cancelSelectionButton.setHeight(25, Unit.PIXELS);
        DonutChartFilterUpdated.this.addComponent(cancelSelectionButton, "right: " + 20 + "px; top: " + 15 + "px");
        cancelSelectionButton.addClickListener((Button.ClickEvent event) -> {
            if (this.appliedFilters.isEmpty()) {
                unselectAll();
            } else {
                for (Object i : appliedFilters) {
                    selectCategory((String) i);
                }
            }
            close();
        });

        Button applySelectionButton = new Button(VaadinIcons.CHECK_SQUARE);
        applySelectionButton.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        applySelectionButton.addStyleName(ValoTheme.BUTTON_TINY);
        applySelectionButton.setWidth(25, Unit.PIXELS);
        applySelectionButton.setHeight(25, Unit.PIXELS);
        applySelectionButton.setDescription("Apply filter selection");
        DonutChartFilterUpdated.this.addComponent(applySelectionButton, "right: " + 50 + "px; top: " + 15 + "px");
        applySelectionButton.addClickListener((Button.ClickEvent event) -> {
            applyFilter(selectedData);
            close();
        });

        Button resetSelectionButton = new Button(VaadinIcons.REFRESH);//"Unselect All",
        resetSelectionButton.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        resetSelectionButton.addStyleName(ValoTheme.BUTTON_TINY);
        resetSelectionButton.setWidth(25, Unit.PIXELS);//110
        resetSelectionButton.setHeight(25, Unit.PIXELS);
        DonutChartFilterUpdated.this.addComponent(resetSelectionButton, "right: " + 80 + "px; top: " + 15 + "px");
        resetSelectionButton.addClickListener((Button.ClickEvent event) -> {
            unselectAll();
        });

        this.selectedCategories = new LinkedHashSet<>();
        VerticalLayout donutChartPanel = new VerticalLayout();
        donutChartPanel.setSizeFull();
        filterContainer.addComponent(donutChartPanel);

        donutChartContainerPanel = new Panel();
        donutChartContainerPanel.setStyleName(ValoTheme.PANEL_BORDERLESS);

        donutChartContainerPanel.setSizeFull();
        donutChartPanel.addComponent(donutChartContainerPanel);
        donutChartPanel.setExpandRatio(donutChartContainerPanel, 90);
        donutChartContainerPanel.setWidth(100, Unit.PERCENTAGE);
        donutChartContainerPanel.setHeight(100, Unit.PERCENTAGE);
        donutChartContainerPanel.addStyleName("scrolspacer");

        this.Selection_Manager.RegistrFilter(DonutChartFilterUpdated.this);
        thumbFilterContainer = new VerticalLayout();
        thumbFilterContainer.setStyleName("thumbfilterframe");
        thumbFilterContainer.setSizeFull();
        thumbFilterContainer.setSpacing(true);
        thumbFilterContainer.setMargin(new MarginInfo(false, false, false, false));
        thumbFilterContainer.setIcon(VaadinIcons.EXPAND_FULL);

        thumbTitle = new Label();
        thumbTitle.setContentMode(ContentMode.HTML);
        thumbTitle.setStyleName(ValoTheme.LABEL_BOLD);
        thumbTitle.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        thumbTitle.setWidth(100, Unit.PERCENTAGE);
        thumbTitle.setHeight(30, Unit.PIXELS);
        thumbTitle.addStyleName("centeredtext");
        thumbFilterContainer.addComponent(thumbTitle);
        thumbFilterContainer.setExpandRatio(thumbTitle, 1);

        Panel thumbFilterPanel = new Panel();
        thumbFilterPanel.setHeight(100, Unit.PERCENTAGE);
        thumbFilterPanel.setWidth(95, Unit.PERCENTAGE);
        thumbFilterPanel.setStyleName(ValoTheme.PANEL_BORDERLESS);
        thumbFilterContainer.addComponent(thumbFilterPanel);
        thumbFilterContainer.setComponentAlignment(thumbFilterPanel, Alignment.TOP_CENTER);
        thumbFilterContainer.setExpandRatio(thumbFilterPanel, 99);

        thumbChartContainer = new VerticalLayout();
        thumbChartContainer.setSizeFull();
        thumbFilterPanel.setContent(thumbChartContainer);

        legendContainer = new VerticalLayout();
        legendContainer.setSizeFull();
        donutChartPanel.setMargin(new MarginInfo(true, false, false, false));
        donutChartPanel.addComponent(legendContainer);
        donutChartPanel.setExpandRatio(legendContainer, 10);
        legendContainer.setVisible(false);

        thumbLegendContainer = new VerticalLayout();
        thumbLegendContainer.setSizeFull();
        colorsMap = new LinkedHashMap<>();
        chartColors = new LinkedHashMap<>();
        colorsLabelMap = new LinkedHashMap<>();
        this.filteredData = new LinkedHashMap<>();

    }

    public void updateChartData(Map<String, Set<String>> data, Color[] colorsArr) {
        this.fullData = data;
        filteredData.clear();
        filteredData.putAll(data);
        appliedFilters.clear();

        selectedCategories.clear();
        colorsMap.clear();
        List<Double> barChartData = new ArrayList<>();
        totalItemsNumber = 0;

        int index = 0;
        for (String key : data.keySet()) {
            int size = data.get(key).size();
            totalItemsNumber += size;
            barChartData.add((double) size);
            Color c = colorsArr[index++];
            colorsMap.put(key, c);
            chartColors.put(key, String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue()));
        }
        TreeSet<Double> barChartset = new TreeSet<>(barChartData);
        double counter = 0;
        List<Double> updatedBarChartData = new ArrayList<>();
        for (double linar : barChartData) {
            double sv = 0;
            if (linar > 0.0) {
                sv = scaleValues(linar, barChartset.last(), barChartset.first());
            }
            counter += sv;
            updatedBarChartData.add(sv);
        }
        barChartData.clear();
        for (double log : updatedBarChartData) {
            barChartData.add((double) ((int) (log * 100.0 / counter)));

        }

        legendContainer.setVisible(true);
        thumbLegendContainer.setVisible(true);
        PopupView legend = initLegend(data.keySet());
        legendContainer.removeAllComponents();
        legendContainer.addComponent(legend);
        legendContainer.setComponentAlignment(legend, Alignment.MIDDLE_CENTER);

        PopupView thumbLegend = initLegend(data.keySet());
        thumbLegendContainer.removeAllComponents();
        thumbLegendContainer.addComponent(thumbLegend);
        thumbLegendContainer.setComponentAlignment(thumbLegend, Alignment.MIDDLE_CENTER);

        donutChartContainerPanel.setContent(initDonutChartFilter(barChartData));
        updateLabels();

    }

    private void updateLabels() {
        int count = selectedData.size();
        if (count == 0) {
            for (Set<String> set : filteredData.values()) {
                count += set.size();
            }
        }

        chartTitle.setValue("" + title + " (" + count + "/" + totalItemsNumber + ")");
        thumbTitle.setValue(chartTitle.getValue());
    }

    private PopupView initLegend(Set<String> categories) {
        colorsLabelMap.clear();
        HorizontalLayout legendContainerLayoutFrame = new HorizontalLayout();
        legendContainerLayoutFrame.setStyleName("popupframe");
        legendContainerLayoutFrame.setWidthUndefined();
        VerticalLayout legendContainerLayout = new VerticalLayout();
        legendContainerLayoutFrame.addComponent(legendContainerLayout);
        LayoutEvents.LayoutClickListener listener = (LayoutEvents.LayoutClickEvent event) -> {
            HorizontalLayout labelContainer = (HorizontalLayout) event.getComponent();
            if (labelContainer != null) {
                selectCategory(labelContainer.getData() + "");
            }

        };

        for (String cat : categories) {
            HorizontalLayout labelContainer = new HorizontalLayout();
            labelContainer.setStyleName("pointer");
            labelContainer.setSpacing(true);
            labelContainer.setWidthUndefined();
            labelContainer.setData(cat);
            labelContainer.addLayoutClickListener(listener);
            Color awtC = colorsMap.get(cat);
            ColorLabel c = new ColorLabel(awtC.getRed(), awtC.getGreen(), awtC.getBlue());
            labelContainer.addComponent(c);
            Label labelTitle = new Label(cat);
            labelTitle.setWidthUndefined();
            labelTitle.setStyleName(ValoTheme.LABEL_TINY);
            labelTitle.addStyleName(ValoTheme.LABEL_SMALL);
            labelContainer.addComponent(labelTitle);
            legendContainerLayout.addComponent(labelContainer);
            colorsLabelMap.put(cat, c);

        }

        PopupView legend = new PopupView("<font style='font-size:12px;'>Legend (Click to view)</font>", legendContainerLayoutFrame);
        legend.setHideOnMouseOut(false);
        legend.setCaptionAsHtml(true);
        legend.setStyleName(ValoTheme.LABEL_SMALL);
        legend.addStyleName(ValoTheme.LABEL_TINY);
        CloseButton closebtn = new CloseButton() {
            @Override
            public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                legend.setPopupVisible(false);
            }
        };
        legendContainerLayoutFrame.addComponent(closebtn);
        return legend;

    }

    private void selectCategory(String categoryId) {
        selectedData.clear();
        if (selectedCategories.contains(categoryId)) {
            selectedCategories.remove(categoryId);
        } else {
            selectedCategories.add(categoryId);
        }
        if (selectedCategories.isEmpty() || selectedCategories.size() == colorsMap.size()) {
            selectedCategories.clear();
            for (String cat : colorsMap.keySet()) {
                Color c = colorsMap.get(cat);
                chartColors.put(cat, String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue()));
                selectedData.addAll(filteredData.get(cat));
                colorsLabelMap.get(cat).removeStyleName("blackborder");
            }
            redrawChart();
            updateLabels();
            return;
        }
        for (String cat : colorsMap.keySet()) {
            Color c = colorsMap.get(cat);
            if (selectedCategories.contains(cat)) {
                chartColors.put(cat, "black");
                colorsLabelMap.get(cat).addStyleName("blackborder");
                selectedData.addAll(filteredData.get(cat));
            } else {
                chartColors.put(cat, String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue()));
                colorsLabelMap.get(cat).removeStyleName("blackborder");
            }

        }
        redrawChart();
        updateLabels();

    }

    private AbsoluteLayout initDonutChartFilter(List<Double> data) {

        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("One", new Double(43.2));
        dataset.setValue("Two", new Double(10.0));
        dataset.setValue("Three", new Double(27.5));
        dataset.setValue("Four", new Double(17.5));
        dataset.setValue("Five", new Double(11.0));
        dataset.setValue("Six", new Double(19.4));
        PiePlot plot = new PiePlot(dataset);
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        plot.setNoDataMessage("No data available");
        plot.setCircular(false);
        plot.setLabelGap(0.02);
        JFreeChart chartToBeWrapped = new JFreeChart(plot);
        
        
        JFreeChart chart2 = ChartFactory.createPieChart(
            "Pie Chart Demo 1",  // chart title
            dataset,             // data
            true,               // include legend
            true,
            false
        );
        
//        donutConfig = new DonutChartConfig();
//        donutConfig.data()
//                .labels(colorsMap.keySet().toArray(new String[colorsMap.size()]))
//                .addDataset(
//                        new PieDataset().label("Dataset 1").dataAsList(data).backgroundColor(chartColors.values().toArray(new String[colorsMap.size()])).borderColor("rgba(255, 255, 255,0.1)").borderWidth(1))
//                .and();
//
//        donutConfig.
//                options().maintainAspectRatio(false)
//                .responsive(true)
//                .hover()
//                .intersect(true)
//                .animationDuration(1)
//                .and()
//                .title()
//                .display(false)
//                .and().legend().display(!legendContainer.isVisible()).position(Position.BOTTOM)
//                .and().tooltips().titleFontSize(0).mode(InteractionMode.INDEX).and()
//                .done();
//
        chartContainer = new VerticalLayout();
        chartContainer.setSizeFull();
        chart.setSizeFull();
        chartContainer.addComponent(chart);
        AbsoluteLayout container = new AbsoluteLayout();
        container.setWidth(100, Unit.PERCENTAGE);
        container.setHeight(100, Unit.PERCENTAGE);
        container.addComponent(chartContainer);
        
//
//        pieConfiguration = new ChartConfiguration();

//        pieConfiguration.setTitle("TestPie");
//        pieConfiguration.setColors(new ArrayList<Color>(colorsMap.values()));
//        pieConfiguration.setChartType(ChartType.PIE);
//        pieConfiguration.setBackgroundColor(Colors.WHITE);
//        PieChartPlotOptions option = new PieChartPlotOptions();
//        option.setDataLabelsEnabled(true);
//        option.setShowCheckBox(false);
//        option.setAnimated(false);
//        option.setAllowPointSelect(true);
//
//        pieConfiguration.setPlotOptions(option);
//
//        pieConfiguration.setCreditsEnabled(false);
//
//        PieChartSeries pieChartSeries = new PieChartSeries("");
//        int index = 0;
//        for (String key : colorsLabelMap.keySet()) {
//            PieChartData pieChartData = new PieChartData(key, data.get(index++));
//
//            pieChartSeries.getData().add(pieChartData);
//        }
//        pieConfiguration.getSeriesList().add(pieChartSeries);

        redrawChart();

        return container;
    }

    private void unselectAll() {
        selectedData.clear();
        selectedCategories.clear();
        for (String cat : colorsMap.keySet()) {
            Color c = colorsMap.get(cat);
            chartColors.put(cat, String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue()));
            selectedData.addAll(filteredData.get(cat));
            colorsLabelMap.get(cat).removeStyleName("blackborder");

        }
//        donutConfig.options().animation().duration(1);
        redrawChart();
        updateLabels();

    }

    @Override
    public void redrawChart() {

//        String[] colorArr = chartColors.values().toArray(new String[chartColors.size()]);
//        ((PieDataset) donutConfig.data().getDatasetAtIndex(0)).borderColor("white").backgroundColor(colorArr).borderWidth(1);
//        donutConfig.options().legend().display(!legendContainer.isVisible()).and().animation().duration(1).and().done();
//        chartContainer.removeAllComponents();
//        chart = new ChartJs(donutConfig);
//        chart.addClickListener((int datasetIndex, int dataIndex) -> {
//            selectCategory(colorsMap.keySet().toArray()[dataIndex] + "");
//        });
//        chart.setWidth(100, Unit.PERCENTAGE);
//        chart.setHeight(100, Unit.PERCENTAGE);
//        chartContainer.addComponent(chart);
// Create and configure a chart.
//        chartContainer.removeAllComponents();
//        try {
//
//            HighChart pieChart = HighChartFactory.renderChart(pieConfiguration);
//            pieChart.setHeight(100, Unit.PERCENTAGE);
//            pieChart.setWidth(100, Unit.PERCENTAGE);
//
//            System.out.println("PieChart Script : " + pieConfiguration.getHighChartValue());
//            chartContainer.addComponent(pieChart);
//            chartContainer.setComponentAlignment(pieChart, Alignment.MIDDLE_CENTER);
//        } catch (NoChartTypeException e) {
//            e.printStackTrace();
//        } catch (HighChartsException ex) {
//            ex.printStackTrace();
//        }

        thumbChartContainer.removeAllComponents();
//        donutConfig.options().legend().display(false).and().done();
//        ChartJs thumbChart = new ChartJs(donutConfig);
//        thumbChart.addClickListener((int datasetIndex, int dataIndex) -> {
//            selectCategory(colorsMap.keySet().toArray()[dataIndex] + "");
//            applyFilter(selectedData);
//        });
//        thumbChart.setWidth(100, Unit.PERCENTAGE);
//        thumbChart.setHeight(100, Unit.PERCENTAGE);
//        thumbChartContainer.addComponent(thumbChart);
//        thumbChartContainer.setExpandRatio(thumbChart, 80);
        thumbChartContainer.addComponent(thumbLegendContainer);
        thumbChartContainer.setExpandRatio(thumbLegendContainer, 20);

    }

    @Override
    public String getFilterId() {
        return filterId;
    }

    @Override
    public void resetFilter() {
        filteredData.clear();
        filteredData.putAll(fullData);
        selectedCategories.clear();
        appliedFilters.clear();
        List<Double> barChartData = new ArrayList<>();
        for (String key : fullData.keySet()) {
            int size = fullData.get(key).size();
            barChartData.add((double) size);
        }
        TreeSet<Double> barChartset = new TreeSet<>(barChartData);
        double counter = 0;
        List<Double> updatedBarChartData = new ArrayList<>();
        for (double linar : barChartData) {
            double sv = 0;
            if (linar > 0.0) {
                sv = scaleValues(linar, barChartset.last(), barChartset.first());
            }
            counter += sv;
            updatedBarChartData.add(sv);
        }
        barChartData.clear();
        for (double log : updatedBarChartData) {
            barChartData.add((double) ((int) (log * 100.0 / counter)));

        }
        legendContainer.setVisible(true);

        PopupView legend = initLegend(fullData.keySet());
        legendContainer.removeAllComponents();
        legendContainer.addComponent(legend);
        legendContainer.setComponentAlignment(legend, Alignment.MIDDLE_CENTER);

        PopupView thumbLegend = initLegend(fullData.keySet());
        thumbLegendContainer.removeAllComponents();
        thumbLegendContainer.addComponent(thumbLegend);
        thumbLegendContainer.setComponentAlignment(thumbLegend, Alignment.MIDDLE_CENTER);

        donutChartContainerPanel.setContent(initDonutChartFilter(barChartData));
        unselectAll();
        updateLabels();

    }

    @Override
    public void updateFilter(Set<String> selection, Set<Object> selectedCategories, boolean singleFilter) {
        this.selectedCategories.clear();
        filteredData.clear();
        selectedData.clear();

        List<Double> barChartData = new ArrayList<>();
        for (String key : fullData.keySet()) {
            Set<String> intersection = new LinkedHashSet<>();
            if (singleFilter && !selectedCategories.isEmpty()) {
                intersection.addAll(fullData.get(key));
            } else {
                intersection.addAll(Sets.intersection(selection, fullData.get(key)));
            }
            filteredData.put(key, intersection);
            barChartData.add((double) intersection.size());
        }
        TreeSet<Double> barChartset = new TreeSet<>(barChartData);
        double counter = 0;
        List<Double> updatedBarChartData = new ArrayList<>();
        for (double linar : barChartData) {
            double sv = 0;
            if (linar > 0.0) {
                sv = scaleValues(linar, barChartset.last(), barChartset.first());
            }
            counter += sv;
            updatedBarChartData.add(sv);
        }
        barChartData.clear();
        for (double log : updatedBarChartData) {
            barChartData.add((double) ((int) (log * 100.0 / counter)));
        }

        donutChartContainerPanel.setContent(initDonutChartFilter(barChartData));
        if (selection.size() == totalItemsNumber) {
            unselectAll();
//            applyFilter(selectedData);
        } else if (singleFilter && !selectedCategories.isEmpty()) {
            applyFilter(selectedData);
        } else {
            for (Object cat : selectedCategories) {
                selectCategory(cat + "");
            }
        }
        updateLabels();

    }

    @Override
    public Set<Object> getSelectedCategories() {
        return appliedFilters;
    }

    public abstract void close();

    public void applyFilter(Set<String> selectedDataset) {
        appliedFilters.clear();
        appliedFilters.addAll(selectedCategories);
        Selection_Manager.setSelection("protein_selection", selectedDataset, filterId);
    }

    @Override
    public Component getThumb() {

        return thumbFilterContainer;
    }

    public Set<String> getSelectedData() {
        if (selectedData.isEmpty()) {
            selectedData.addAll(filteredData.keySet());
        }
        return selectedData;
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
        double logValue = (Math.log(linearValue + 1) / Math.log(2));
        logValue = (logValue * 2 / logMax) + lowerLimit;
        return Math.min(logValue, max);
    }

}
