package com.uib.web.peptideshaker.presenter.core.filtercharts.charts;

//import com.byteowls.vaadin.chartjs.ChartJs;
//import com.byteowls.vaadin.chartjs.config.DonutChartConfig;
//import com.byteowls.vaadin.chartjs.config.PieChartConfig;
//import com.byteowls.vaadin.chartjs.data.PieDataset;
//import com.byteowls.vaadin.chartjs.options.InteractionMode;
//import com.byteowls.vaadin.chartjs.options.Position;
import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.DonutChartConfig;
import com.byteowls.vaadin.chartjs.config.PieChartConfig;
import com.byteowls.vaadin.chartjs.data.PieDataset;
import com.byteowls.vaadin.chartjs.options.InteractionMode;
import com.byteowls.vaadin.chartjs.options.Position;
import com.byteowls.vaadin.chartjs.options.Tooltips;
import com.google.common.collect.Sets;
import com.uib.web.peptideshaker.presenter.components.peptideshakerview.components.SelectionManager;
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
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class represents matrix layout filter
 *
 * @author Yehia Farag
 */
public abstract class DonutChartFilter extends AbsoluteLayout implements RegistrableFilter {

    private final Panel donutChartContainerPanel;
    private final String title;
    private final String filterId;
    private DonutChartConfig donutConfig;
    private DonutChartConfig highLightDonutConfig;
    private PieChartConfig basicChartConfig;

    private ChartJs chart;
    private VerticalLayout transparentChartContainer;
    private VerticalLayout chartContainer;
    private VerticalLayout highLightchartContainer;
    private final VerticalLayout thumbFilterContainer;
    private final Label thumbTitle;

    private final Label chartTitle;
    private final Set<Object> selectedCategories;
    private final Set<Object> appliedFilters;
    private Map<String, Set<Comparable>> fullData;
    private final Map<String, Set<Comparable>> filteredData;

    private int totalItemsNumber;
    private final SelectionManager Selection_Manager;
    private final VerticalLayout legendContainer;
    private final VerticalLayout thumbLegendContainer;
    private final Map<String, Color> colorsMap;
    private final Map<String, ColorLabel> colorsLabelMap;
    private final Set<Comparable> selectedData;
    private final Map<String, String> chartColors;
    private final Map<String, String> highLightedChartColors;
    private final Map<String, String> chartBorderColors;
    private final VerticalLayout thumbChartContainer;

//    @Override
    public boolean isAppliedFilter() {
        return !(selectedData.isEmpty() || selectedData.size() == totalItemsNumber);
    }

    public DonutChartFilter(String title, String filterId, SelectionManager Selection_Manager) {

        this.title = title;
        this.filterId = filterId;
        this.Selection_Manager = Selection_Manager;
        selectedData = new LinkedHashSet<>();
        this.appliedFilters = new LinkedHashSet<>();
        DonutChartFilter.this.setWidth(100, Unit.PERCENTAGE);
        DonutChartFilter.this.setHeight(100, Unit.PERCENTAGE);
        DonutChartFilter.this.setStyleName("filterframe");
        VerticalLayout filterContainer = new VerticalLayout();
        filterContainer.setSizeFull();
        filterContainer.setMargin(new MarginInfo(true, true, false, true));
        DonutChartFilter.this.addComponent(filterContainer);

        chartTitle = new Label("" + title + " (100000/1000000)");
        chartTitle.setContentMode(ContentMode.HTML);
        chartTitle.setStyleName(ValoTheme.LABEL_BOLD);
        chartTitle.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        chartTitle.setWidth(100, Unit.PERCENTAGE);
        chartTitle.setHeight(30, Unit.PIXELS);
        DonutChartFilter.this.addComponent(chartTitle, "left: " + 20 + "px; top: " + 10 + "px");
        Button cancelSelectionButton = new Button(VaadinIcons.CLOSE);
        cancelSelectionButton.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        cancelSelectionButton.addStyleName(ValoTheme.BUTTON_TINY);
        cancelSelectionButton.setWidth(25, Unit.PIXELS);
        cancelSelectionButton.setHeight(25, Unit.PIXELS);
        DonutChartFilter.this.addComponent(cancelSelectionButton, "right: " + 20 + "px; top: " + 15 + "px");
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
        DonutChartFilter.this.addComponent(applySelectionButton, "right: " + 50 + "px; top: " + 15 + "px");
        applySelectionButton.addClickListener((Button.ClickEvent event) -> {
            applyFilter(selectedData);
            close();
        });

        Button resetSelectionButton = new Button(VaadinIcons.REFRESH);//"Unselect All",
        resetSelectionButton.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        resetSelectionButton.addStyleName(ValoTheme.BUTTON_TINY);
        resetSelectionButton.setWidth(25, Unit.PIXELS);//110
        resetSelectionButton.setHeight(25, Unit.PIXELS);
        DonutChartFilter.this.addComponent(resetSelectionButton, "right: " + 80 + "px; top: " + 15 + "px");
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

        this.Selection_Manager.RegistrFilter(DonutChartFilter.this);
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
        highLightedChartColors = new LinkedHashMap<>();
        colorsLabelMap = new LinkedHashMap<>();
        this.filteredData = new LinkedHashMap<>();
        this.chartBorderColors = new LinkedHashMap<>();

    }

    public void updateChartData(Map<String, Set<Comparable>> data, Color[] colorsArr) {
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
            chartBorderColors.put(key, "white");
            chartColors.put(key, String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue()));
            highLightedChartColors.put(key, "rgba(0,0,0,0)");
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
        HorizontalLayout legend = initLegend(data.keySet());
        legendContainer.removeAllComponents();
        legendContainer.addComponent(legend);
        legendContainer.setComponentAlignment(legend, Alignment.MIDDLE_CENTER);

        HorizontalLayout thumbLegend = initLegend(data.keySet());
        thumbLegendContainer.removeAllComponents();
        thumbLegendContainer.addComponent(thumbLegend);
        thumbLegendContainer.setComponentAlignment(thumbLegend, Alignment.MIDDLE_CENTER);

        donutChartContainerPanel.setContent(initDonutChartFilter(barChartData));
        updateLabels();

    }

    private void updateLabels() {
        int count = selectedData.size();
        if (count == 0) {
            for (Set<Comparable> set : filteredData.values()) {
                count += set.size();
            }
        }

        chartTitle.setValue("" + title + " (" + count + "/" + totalItemsNumber + ")");
        thumbTitle.setValue(chartTitle.getValue());
    }

    private HorizontalLayout initLegend(Set<String> categories) {
        colorsLabelMap.clear();
        HorizontalLayout legendContainerLayoutFrame = new HorizontalLayout();
        legendContainerLayoutFrame.setStyleName("popupframe");
        legendContainerLayoutFrame.addStyleName("stackedlegend");
        legendContainerLayoutFrame.setWidthUndefined();
        VerticalLayout legendContainerLayout = new VerticalLayout();
        legendContainerLayoutFrame.addComponent(legendContainerLayout);
        legendContainerLayoutFrame.setComponentAlignment(legendContainerLayout, Alignment.MIDDLE_CENTER);
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
        legendContainerLayout.setHeight(categories.size() * 27, Unit.PIXELS);

//        PopupView legend = new PopupView("<font style='font-size:12px;'>Legend (Click to view)</font>", legendContainerLayoutFrame);
//        legend.setHideOnMouseOut(false);
//        legend.setCaptionAsHtml(true);
//        legend.setStyleName(ValoTheme.LABEL_SMALL);
//        legend.addStyleName(ValoTheme.LABEL_TINY);
//        legend.addStyleName("stackedlegend");
//        CloseButton closebtn = new CloseButton() {
//            @Override
//            public void layoutClick(LayoutEvents.LayoutClickEvent event) {
////                legend.setPopupVisible(false);
//            }
//        };
//        legendContainerLayoutFrame.addComponent(closebtn);
        return legendContainerLayoutFrame;
//        return legend;

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
                chartBorderColors.put(cat, "white");
                highLightedChartColors.put(cat, "rgba(0,0,0,0)");
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
                highLightedChartColors.put(cat, String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue()));
                chartColors.put(cat, "rgba(0,0,0,0)");
                chartBorderColors.put(cat, "rgba(0,0,0,0)");
                colorsLabelMap.get(cat).addStyleName("blackborder");
                selectedData.addAll(filteredData.get(cat));
            } else {
                chartColors.put(cat, String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue()));
                chartBorderColors.put(cat, "white");
                highLightedChartColors.put(cat, "rgba(0,0,0,0)");
                colorsLabelMap.get(cat).removeStyleName("blackborder");
            }

        }
        redrawChart();
        updateLabels();

    }

    private AbsoluteLayout initDonutChartFilter(List<Double> data) {

        donutConfig = new DonutChartConfig();
        donutConfig.data()
                //                .labelsAsList(new ArrayList<>(colorsMap.keySet()))//.toArray(new String[colorsMap.size()]))
                .addDataset(
                        new PieDataset().label("Dataset 1").dataAsList(data).backgroundColor(chartColors.values().toArray(new String[colorsMap.size()])).borderColor(chartBorderColors.values().toArray(new String[colorsMap.size()])).borderWidth(5))
                .and();

        donutConfig.
                options().maintainAspectRatio(false)
                .responsive(true)
                .hover()
                .intersect(true)
                .animationDuration(1)
                .and()
                .title()
                .display(false)
                .and().legend().display(!legendContainer.isVisible()).position(Position.BOTTOM)
                .and().tooltips().displayColors(false).enabled(false).titleFontSize(0).mode(InteractionMode.INDEX).and()
                .done();

        highLightDonutConfig = new DonutChartConfig();
        highLightDonutConfig.data()
                //                .labels(colorsMap.keySet().toArray(new String[colorsMap.size()]))
                .addDataset(
                        new PieDataset().label("Dataset 1").dataAsList(data).backgroundColor("rgba(255, 255, 255,0.0)").borderColor("white").borderWidth(10))
                .and();

        highLightDonutConfig.
                options().maintainAspectRatio(false)
                .responsive(true)
                .hover()
                .intersect(true)
                .animationDuration(1)
                .and()
                .title()
                .display(false)
                .and().legend().display(!legendContainer.isVisible()).position(Position.BOTTOM)
                .and().tooltips().displayColors(false).enabled(false).titleFontSize(0).mode(InteractionMode.INDEX).and()
                .done();

        highLightchartContainer = new VerticalLayout();
        highLightchartContainer.setSizeFull();
        highLightchartContainer.addStyleName("highlightchartcontainer");

        chartContainer = new VerticalLayout();
        chartContainer.addStyleName("chartcontainer");
        chartContainer.setSizeFull();

        basicChartConfig = new PieChartConfig();
        List<String> labelList = new ArrayList<>();
        for (String str : colorsMap.keySet()) {
            labelList.add(str + " #:1000 | %");

        }

        basicChartConfig.data()
                .labelsAsList(labelList)
                .addDataset(
                        new PieDataset().label("Dataset 1").dataAsList(data).backgroundColor("rgba(255, 255, 255,0.0)").hoverBackgroundColor("rgba(255, 255, 255,0.0)").hoverBorderColor("rgba(255, 255, 255,0.0)").borderColor("rgba(255, 255, 255,0.0)").borderWidth(10))
                //                new PieDataset().label("Dataset 1").dataAsList(data).backgroundColor(chartColors.values().toArray(new String[colorsMap.size()])).borderColor(chartBorderColors.values().toArray(new String[colorsMap.size()])).borderWidth(5))

                .and();

        basicChartConfig.
                options().maintainAspectRatio(false)
                .responsive(true)
                .hover()
                .intersect(true)
                .animationDuration(1)
                .and()
                .title()
                .display(false)
                .and().legend().display(!legendContainer.isVisible()).position(Position.BOTTOM)
                .and().tooltips().titleFontSize(0).bodyFontSize(12).mode(InteractionMode.INDEX).position(Tooltips.PositionMode.NEAREST).displayColors(false).enabled(true).titleFontSize(0).and()
                .done();

        transparentChartContainer = new VerticalLayout();
        transparentChartContainer.setSizeFull();
        transparentChartContainer.addStyleName("basicchartcontainer");

        AbsoluteLayout container = new AbsoluteLayout();

        container.setWidth(100, Unit.PERCENTAGE);
        container.setHeight(100, Unit.PERCENTAGE);
        container.addComponent(chartContainer);
        container.addComponent(highLightchartContainer);
        container.addComponent(transparentChartContainer);

//                ((PieDataset) basicChartConfig.data().getDatasetAtIndex(0)).borderColor("rgba(0,0,0,0)").hoverBorderColor("rgba(0,0,0,0)").backgroundColor("rgba(0,0,0,0)").hoverBackgroundColor("rgba(0,0,0,0)").borderWidth(10);
//        basicChartConfig.options().legend().display(!legendContainer.isVisible()).and().animation().duration(1).and().done();
//
//        
        ChartJs chart3 = new ChartJs(basicChartConfig);
        chart3.addClickListener((int datasetIndex, int dataIndex) -> {
            selectCategory(colorsMap.keySet().toArray()[dataIndex] + "");
        });
        chart3.setWidth(100, Unit.PERCENTAGE);
        chart3.setHeight(100, Unit.PERCENTAGE);
        transparentChartContainer.addComponent(chart3);

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
            highLightedChartColors.put(cat, "rgba(0,0,0,0)");
            colorsLabelMap.get(cat).removeStyleName("blackborder");

        }
        donutConfig.options().animation().duration(1);
        redrawChart();
        updateLabels();

    }

    @Override
    public void redrawChart() {

        String[] highLightedColorArr = highLightedChartColors.values().toArray(new String[chartColors.size()]);
        String[] colorArr = chartColors.values().toArray(new String[chartColors.size()]);
        String[] borderColorArr = chartBorderColors.values().toArray(new String[colorsMap.size()]);
        ((PieDataset) donutConfig.data().getDatasetAtIndex(0)).borderColor(borderColorArr).hoverBorderColor(borderColorArr).backgroundColor(colorArr).hoverBackgroundColor(colorArr).borderWidth(5);
        donutConfig.options().legend().display(!legendContainer.isVisible()).and().animation().duration(1).and().done();
        chartContainer.removeAllComponents();
        chart = new ChartJs(donutConfig);
        chart.addClickListener((int datasetIndex, int dataIndex) -> {
            selectCategory(colorsMap.keySet().toArray()[dataIndex] + "");
        });
        chart.setWidth(80, Unit.PERCENTAGE);
        chart.setHeight(80, Unit.PERCENTAGE);

        chartContainer.addComponent(chart);
        chartContainer.setComponentAlignment(chart, Alignment.MIDDLE_CENTER);
        ((PieDataset) highLightDonutConfig.data().getDatasetAtIndex(0)).borderColor("white").hoverBorderColor("white").backgroundColor(highLightedColorArr).hoverBackgroundColor(highLightedColorArr).borderWidth(10);
        highLightDonutConfig.options().legend().display(!legendContainer.isVisible()).and().animation().duration(1).and().done();

        highLightchartContainer.removeAllComponents();
        ChartJs chart2 = new ChartJs(highLightDonutConfig);
        chart2.addClickListener((int datasetIndex, int dataIndex) -> {
            selectCategory(colorsMap.keySet().toArray()[dataIndex] + "");
        });
        chart2.setWidth(100, Unit.PERCENTAGE);
        chart2.setHeight(100, Unit.PERCENTAGE);
        highLightchartContainer.addComponent(chart2);

        thumbChartContainer.removeAllComponents();
        donutConfig.options().legend().display(false).and().done();
        ChartJs thumbChart = new ChartJs(donutConfig);
        thumbChart.addClickListener((int datasetIndex, int dataIndex) -> {
            selectCategory(colorsMap.keySet().toArray()[dataIndex] + "");
            applyFilter(selectedData);
        });
        thumbChart.setWidth(100, Unit.PERCENTAGE);
        thumbChart.setHeight(100, Unit.PERCENTAGE);
        thumbChartContainer.addComponent(thumbChart);
        thumbChartContainer.setExpandRatio(thumbChart, 80);
        thumbChartContainer.addComponent(thumbLegendContainer);
        thumbChartContainer.setComponentAlignment(thumbLegendContainer, Alignment.MIDDLE_CENTER);
        thumbChartContainer.setExpandRatio(thumbLegendContainer, 20);

    }

    @Override
    public String getFilterId() {
        return filterId;
    }

//    @Override
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

        HorizontalLayout legend = initLegend(fullData.keySet());
        legend.addStyleName("stackedlegend");
        legendContainer.removeAllComponents();
        legendContainer.addComponent(legend);
        legendContainer.setComponentAlignment(legend, Alignment.MIDDLE_CENTER);

        HorizontalLayout thumbLegend = initLegend(fullData.keySet());
        thumbLegend.addStyleName("stackedlegend");
        thumbLegendContainer.removeAllComponents();
        thumbLegendContainer.addComponent(thumbLegend);
        thumbLegendContainer.setComponentAlignment(thumbLegend, Alignment.MIDDLE_CENTER);

        donutChartContainerPanel.setContent(initDonutChartFilter(barChartData));
        unselectAll();
        updateLabels();

    }

//    @Override
    public void updateFilter(Set<Comparable> selection, Set<Comparable> selectedCategories, boolean singleFilter) {
        this.selectedCategories.clear();
        filteredData.clear();
        selectedData.clear();

        List<Double> barChartData = new ArrayList<>();
        for (String key : fullData.keySet()) {
            Set<Comparable> intersection = new LinkedHashSet<>();
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

//    @Override
    public Set<Object> getSelectedCategories() {
        return appliedFilters;
    }

    public abstract void close();

    public void applyFilter(Set<Comparable> selectedDataset) {
        appliedFilters.clear();
        appliedFilters.addAll(selectedCategories);
        Selection_Manager.setSelection("protein_selection", selectedDataset,null, filterId);
    }

//    @Override
    public Component getThumb() {

        return thumbFilterContainer;
    }

    public Set<Comparable> getSelectedData() {
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
