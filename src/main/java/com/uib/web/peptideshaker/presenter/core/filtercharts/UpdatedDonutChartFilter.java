package com.uib.web.peptideshaker.presenter.core.filtercharts;

import com.uib.web.peptideshaker.presenter.core.filtercharts.charts.*;
import com.google.common.collect.Sets;
import com.uib.web.peptideshaker.presenter.components.peptideshakerview.components.SelectionManager;
import com.uib.web.peptideshaker.presenter.core.filtercharts.components.PieChartSlice;
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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class represents matrix layout filter
 *
 * @author Yehia Farag
 */
public abstract class UpdatedDonutChartFilter extends AbsoluteLayout implements RegistrableFilter {

    private final Panel donutChartContainerPanel;
    private final String title;
    private final String filterId;

    private final DivaPieChart chart;
    private final DivaPieChart thumbChart;
    private final HorizontalLayout thumbFilterContainer;
    private final Label thumbTitle;
    private final Label chartTitle;
    private final Set<Comparable> selectedItems;

    private final Set<Object> appliedSelectedCategories;
    private final Map<String, Set<Comparable>> filteredData;

    private int totalItemsNumber;
    private final SelectionManager Selection_Manager;
    private Set<Comparable> selectedCategories;
    private final VerticalLayout thumbChartContainer;

    private Map<String, Set<Comparable>> fullData;

//    @Override
    public boolean isAppliedFilter() {
        return !(selectedCategories.isEmpty() || selectedCategories.size() == totalItemsNumber);
    }

    public UpdatedDonutChartFilter(String title, String filterId, SelectionManager Selection_Manager) {

        this.title = title;
        this.filterId = filterId;
        this.Selection_Manager = Selection_Manager;
        this.appliedSelectedCategories = new LinkedHashSet<>();
        UpdatedDonutChartFilter.this.setWidth(100, Unit.PERCENTAGE);
        UpdatedDonutChartFilter.this.setHeight(100, Unit.PERCENTAGE);
        UpdatedDonutChartFilter.this.setStyleName("filterframe");

        VerticalLayout filterContainer = new VerticalLayout();
        filterContainer.setSizeFull();
        filterContainer.setMargin(new MarginInfo(true, true, true, true));
        UpdatedDonutChartFilter.this.addComponent(filterContainer);

        chartTitle = new Label("" + title + " (100000/1000000)");
        chartTitle.setContentMode(ContentMode.HTML);
        chartTitle.setStyleName(ValoTheme.LABEL_BOLD);
        chartTitle.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        chartTitle.setWidth(100, Unit.PERCENTAGE);
        chartTitle.setHeight(30, Unit.PIXELS);
        UpdatedDonutChartFilter.this.addComponent(chartTitle, "left: " + 20 + "px; top: " + 10 + "px");
        Button cancelSelectionButton = new Button(VaadinIcons.CLOSE);
        cancelSelectionButton.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        cancelSelectionButton.addStyleName(ValoTheme.BUTTON_TINY);
        cancelSelectionButton.setWidth(25, Unit.PIXELS);
        cancelSelectionButton.setHeight(25, Unit.PIXELS);
        UpdatedDonutChartFilter.this.addComponent(cancelSelectionButton, "right: " + 20 + "px; top: " + 15 + "px");
        cancelSelectionButton.addClickListener((Button.ClickEvent event) -> {
//local update appliedSelectedCategories
//chart.localUpdate(selectedDatasetIndexes, true);
//            close();
        });

        Button applySelectionButton = new Button(VaadinIcons.CHECK_SQUARE);
        applySelectionButton.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        applySelectionButton.addStyleName(ValoTheme.BUTTON_TINY);
        applySelectionButton.setWidth(25, Unit.PIXELS);
        applySelectionButton.setHeight(25, Unit.PIXELS);
        applySelectionButton.setDescription("Apply filter selection");
        UpdatedDonutChartFilter.this.addComponent(applySelectionButton, "right: " + 50 + "px; top: " + 15 + "px");
        applySelectionButton.addClickListener((Button.ClickEvent event) -> {
            applyFilter(selectedCategories);
//            close();
        });

        Button resetSelectionButton = new Button(VaadinIcons.REFRESH);//"Unselect All",
        resetSelectionButton.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        resetSelectionButton.addStyleName(ValoTheme.BUTTON_TINY);
        resetSelectionButton.setWidth(25, Unit.PIXELS);//110
        resetSelectionButton.setHeight(25, Unit.PIXELS);
        UpdatedDonutChartFilter.this.addComponent(resetSelectionButton, "right: " + 80 + "px; top: " + 15 + "px");
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

        chart = new DivaPieChart(filterId, filterId, false) {
            @Override
            public void selectDatasets(boolean noselection) {
                selectedCategories = this.getSelectedCategories();
                selectedItems.clear();
                for (Comparable comp : selectedCategories) {
                    selectedItems.addAll(filteredData.get(comp.toString()));
                }
                thumbChart.updateSliceSelection(selectedCategories);
                updateLabels();
            }
        };
        donutChartContainerPanel.setContent(chart);

        this.Selection_Manager.RegistrFilter(UpdatedDonutChartFilter.this);
        thumbFilterContainer = new HorizontalLayout();
        thumbFilterContainer.setStyleName("thumbfilterframe");
        thumbFilterContainer.setSizeFull();
        thumbFilterContainer.setSpacing(true);
        thumbFilterContainer.setMargin(new MarginInfo(false, false, false, false));
        thumbFilterContainer.addStyleName("smallthumb");
//        thumbFilterContainer.setIcon(VaadinIcons.EXPAND_FULL);

        thumbTitle = new Label();
        thumbTitle.setContentMode(ContentMode.HTML);
        thumbTitle.setStyleName(ValoTheme.LABEL_BOLD);
        thumbTitle.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        thumbTitle.setWidth(100, Unit.PERCENTAGE);
        thumbTitle.setHeightUndefined();
        thumbTitle.addStyleName("centeredtext");
        thumbFilterContainer.addComponent(thumbTitle);
        thumbFilterContainer.setExpandRatio(thumbTitle, 20);
        thumbFilterContainer.setComponentAlignment(thumbTitle, Alignment.MIDDLE_CENTER);

        Panel thumbFilterPanel = new Panel();
        thumbFilterPanel.setHeight(100, Unit.PERCENTAGE);
        thumbFilterPanel.setWidth(100, Unit.PERCENTAGE);
        thumbFilterPanel.setStyleName(ValoTheme.PANEL_BORDERLESS);
        thumbFilterContainer.addComponent(thumbFilterPanel);
        thumbFilterContainer.setComponentAlignment(thumbFilterPanel, Alignment.TOP_LEFT);
        thumbFilterContainer.setExpandRatio(thumbFilterPanel, 80);

        thumbChartContainer = new VerticalLayout();
        thumbChartContainer.setSizeFull();
        thumbFilterPanel.setContent(thumbChartContainer);

        thumbChart = new DivaPieChart(filterId, filterId, true) {
            @Override
            public void selectDatasets(boolean noselection) {
                selectedCategories = this.getSelectedCategories();
                selectedItems.clear();
                for (Comparable comp : selectedCategories) {
                    selectedItems.addAll(filteredData.get(comp.toString()));

                }
                chart.updateSliceSelection(selectedCategories);
                updateLabels();
                applyFilter(selectedCategories);
            }
        };
        thumbChartContainer.addComponent(thumbChart);
        this.filteredData = new LinkedHashMap<>();
        this.selectedItems = new LinkedHashSet<>();

    }

    public void updateChartData(Map<String, Set<Comparable>> data, Color[] colorsArr) {
        this.fullData = data;
        filteredData.clear();
        filteredData.putAll(data);
        appliedSelectedCategories.clear();
        selectedCategories.clear();
        totalItemsNumber = 0;
        selectedItems.clear();

        Map<Comparable, PieChartSlice> tempchartData = new LinkedHashMap<>();
        int index = 0;
        for (String key : data.keySet()) {
            int size = data.get(key).size();
//            selectedItems.addAll(data.get(key));
            totalItemsNumber += size;
            PieChartSlice slice = new PieChartSlice();
            slice.setColor(colorsArr[index++]);
            slice.setLabel(key);
            slice.setTotalValue(size);
            slice.getItemsIds().addAll(data.get(key));
            tempchartData.put(key, slice);
        }
        chart.initializeFilterData(tempchartData, colorsArr);
        thumbChart.initializeFilterData(tempchartData, colorsArr);
        updateLabels();

    }

    private void updateLabels() {
        int count = selectedItems.size();
        if (count == 0) {
            for (Set<Comparable> set : filteredData.values()) {
                count += set.size();
            }
        }

        chartTitle.setValue("" + title + " (" + count + "/" + totalItemsNumber + ")");
        thumbTitle.setValue(chartTitle.getValue());
    }

    private void unselectAll() {
//        selectedCategory.clear();
//        selectedCategories.clear();
//        redrawChart();
//        updateLabels();

    }

    @Override
    public void redrawChart() {
        thumbChart.redrawChart();
        chart.redrawChart();
    }

    @Override
    public String getFilterId() {
        return filterId;
    }

//    @Override
    public void resetFilter() {
//        filteredData.clear();
//        filteredData.putAll(fullData);
//        selectedCategories.clear();
//        appliedFilters.clear();
//        List<Double> barChartData = new ArrayList<>();
//        for (String key : fullData.keySet()) {
//            int size = fullData.get(key).size();
//            barChartData.add((double) size);
//        }
//        TreeSet<Double> barChartset = new TreeSet<>(barChartData);
//        double counter = 0;
//        List<Double> updatedBarChartData = new ArrayList<>();
//        for (double linar : barChartData) {
//            double sv = 0;
//            if (linar > 0.0) {
//                sv = scaleValues(linar, barChartset.last(), barChartset.first());
//            }
//            counter += sv;
//            updatedBarChartData.add(sv);
//        }
//        barChartData.clear();
//        for (double log : updatedBarChartData) {
//            barChartData.add((double) ((int) (log * 100.0 / counter)));
//
//        }

        updateLabels();

    }

    @Override
    public void updateFilterSelection(Set<Comparable> selection, Set<Comparable> selectedCategories,boolean topFilter, boolean reset,boolean selfAction) {
        boolean singleFilter = true;

        System.out.println("at selection update " + selection + "   ----   " + selectedCategories + "  -----   " + singleFilter);

        this.selectedCategories.clear();
        filteredData.clear();
        for (String key : fullData.keySet()) {
            Set<Comparable> intersection = new LinkedHashSet<>();
            if (singleFilter && !selectedCategories.isEmpty()) {
                intersection.addAll(fullData.get(key));
            } else {
                intersection.addAll(Sets.intersection(selection, fullData.get(key)));
            }
            filteredData.put(key, intersection);
        }

        chart.localUpdate(selection, singleFilter);
        thumbChart.localUpdate(selection, singleFilter);
        updateLabels();
        updateLabels();

    }

//    @Override
    public Set<Object> getSelectedCategories() {
        return appliedSelectedCategories;
    }

    public void applyFilter(Set<Comparable> selectedCategories) {
        appliedSelectedCategories.clear();
        appliedSelectedCategories.addAll(selectedCategories);
        Selection_Manager.setSelection("protein_selection", selectedCategories, null, filterId);
    }

//    @Override
    public Component getThumb() {

        return thumbFilterContainer;
    }

    public Set<Comparable> getSelectedItems() {
        if (selectedCategories.isEmpty()) {
            selectedCategories.addAll(filteredData.keySet());
        }
        return selectedCategories;
    }

}
