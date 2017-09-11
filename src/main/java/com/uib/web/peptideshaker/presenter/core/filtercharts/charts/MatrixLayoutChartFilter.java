package com.uib.web.peptideshaker.presenter.core.filtercharts.charts;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.BarChartConfig;
import com.byteowls.vaadin.chartjs.data.BarDataset;
import com.byteowls.vaadin.chartjs.data.Dataset;
import com.byteowls.vaadin.chartjs.options.InteractionMode;
import com.byteowls.vaadin.chartjs.options.Position;
import com.byteowls.vaadin.chartjs.options.scale.Axis;
import com.byteowls.vaadin.chartjs.options.scale.CategoryScale;
import com.byteowls.vaadin.chartjs.options.scale.LinearScale;
import com.google.common.collect.Sets;

import com.uib.web.peptideshaker.model.AlphanumComparator;
import com.uib.web.peptideshaker.presenter.components.peptideshakerview.components.SelectionManager;
import com.uib.web.peptideshaker.presenter.core.filtercharts.components.SelectableNode;
import com.uib.web.peptideshaker.presenter.core.form.SparkLine;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * This class represents matrix layout filter
 *
 * @author Yehia Farag
 */
public abstract class MatrixLayoutChartFilter extends AbsoluteLayout implements RegistrableFilter {

    private final Panel barChartContainerPanel;
    private final Panel graphChartContainerPanel;
    private Map<String, Set<Comparable>> columns;
    private final Map<String, Integer> rows = new LinkedHashMap<>();
    private final Map<String, Set<Comparable>> calculatedMatrix = new LinkedHashMap<>();

    private final Set<String> keySorter = new TreeSet<>();
    private final Label setSizeLabel;
    private final String title;
    private final String filterId;
    private BarChartConfig barConfig;
    private ChartJs chart;
    private String[] colors;
    private VerticalLayout chartContainer;
    private final VerticalLayout thumbFilterContainer;
    private final VerticalLayout thumbChartContainer;
    private final GridLayout thumblegendContainer;
    private final Label thumbTitle;
    private final String unselectedColor = "lightgray";
    private final String selectedColor = "#bad5f2";//"#1780E9";
    private GridLayout nodeContainer;
    private VerticalLayout rowsLabelsLayoutContainer;
    private final Label chartTitle;
    private final Set<Comparable> selectedDataSet;
    private Map<String, Color> dataColors;
    private int appliedFilters;
    private int totalItemsNumber;
    private final SelectionManager Selection_Manager;
    private final GridLayout thumbNodeContainer;
    private int selectedColumns;
    private LinearScale yAxisScale;
//    private final HorizontalLayout labelContainer;
    private final Map<String, SparkLine> rowLabelsMap;

    public MatrixLayoutChartFilter(String title, String filterId, SelectionManager Selection_Manager) {
        this.title = title;
        this.filterId = filterId;
        this.Selection_Manager = Selection_Manager;
        this.appliedFilters = -1;
        this.rowLabelsMap = new LinkedHashMap<>();

//        this.labelContainer = new HorizontalLayout();
//        this.labelContainer.setSpacing(true);
        MatrixLayoutChartFilter.this.setWidth(100, Unit.PERCENTAGE);
        MatrixLayoutChartFilter.this.setHeight(100, Unit.PERCENTAGE);
        VerticalLayout filterContainer = new VerticalLayout();
        filterContainer.setSizeFull();
        filterContainer.setMargin(new MarginInfo(true, true, false, true));
        MatrixLayoutChartFilter.this.addComponent(filterContainer);

        chartTitle = new Label("" + title + " (100000/1000000)");
        chartTitle.setContentMode(ContentMode.HTML);
        chartTitle.setStyleName(ValoTheme.LABEL_BOLD);
        chartTitle.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        chartTitle.setWidth(100, Unit.PERCENTAGE);
        chartTitle.setHeight(30, Unit.PIXELS);
        MatrixLayoutChartFilter.this.addComponent(chartTitle, "left: " + 20 + "px; top: " + 10 + "px");
        Button cancelSelectionButton = new Button(VaadinIcons.CLOSE);
        cancelSelectionButton.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        cancelSelectionButton.addStyleName(ValoTheme.BUTTON_TINY);
        cancelSelectionButton.setWidth(25, Unit.PIXELS);
        cancelSelectionButton.setHeight(25, Unit.PIXELS);
        MatrixLayoutChartFilter.this.addComponent(cancelSelectionButton, "right: " + 20 + "px; top: " + 15 + "px");
        cancelSelectionButton.addClickListener((Button.ClickEvent event) -> {
            if (appliedFilters == -1) {
                unselectAll();
            } else {
                colors[appliedFilters] = unselectedColor;
                selectColumn(appliedFilters);

            }
            close();
        });

        Button applySelectionButton = new Button(VaadinIcons.CHECK_SQUARE);
        applySelectionButton.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        applySelectionButton.addStyleName(ValoTheme.BUTTON_TINY);
        applySelectionButton.setWidth(25, Unit.PIXELS);
        applySelectionButton.setHeight(25, Unit.PIXELS);
        applySelectionButton.setDescription("Apply filter selection");
        MatrixLayoutChartFilter.this.addComponent(applySelectionButton, "right: " + 50 + "px; top: " + 15 + "px");
        applySelectionButton.addClickListener((Button.ClickEvent event) -> {
            applyFilter(getSelectedDataSet());
            close();
//            unselectAll();
        });

        Button resetSelectionButton = new Button(VaadinIcons.REFRESH);//"Unselect All",
        resetSelectionButton.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        resetSelectionButton.addStyleName(ValoTheme.BUTTON_TINY);
        resetSelectionButton.setWidth(25, Unit.PIXELS);//110
        resetSelectionButton.setHeight(25, Unit.PIXELS);
        MatrixLayoutChartFilter.this.addComponent(resetSelectionButton, "right: " + 80 + "px; top: " + 15 + "px");
        resetSelectionButton.addClickListener((Button.ClickEvent event) -> {
            unselectAll();
        });

        this.selectedDataSet = new LinkedHashSet<>();
        HorizontalLayout barChartPanel = new HorizontalLayout();
        barChartPanel.setSizeFull();
        filterContainer.addComponent(barChartPanel);
        VerticalLayout spacer = new VerticalLayout();
        spacer.setSizeFull();
        barChartPanel.setMargin(new MarginInfo(true, false, false, false));
        barChartPanel.addComponent(spacer);
        barChartPanel.setExpandRatio(spacer, 20);
        HorizontalLayout setSizeLabelContainer = new HorizontalLayout();
        setSizeLabelContainer.setSizeFull();
        setSizeLabelContainer.setMargin(new MarginInfo(false, false, false, false));
        setSizeLabelContainer.setSpacing(true);

        spacer.addComponent(setSizeLabelContainer);
//        spacer.setExpandRatio(setSizeLabelContainer, 0.1f);

        setSizeLabel = new Label("0  <font>0</font>");
        setSizeLabel.setContentMode(ContentMode.HTML);
        setSizeLabel.setWidth(100, Unit.PERCENTAGE);
        setSizeLabel.setStyleName(ValoTheme.LABEL_NO_MARGIN);
        setSizeLabel.addStyleName("sizelabel");
        setSizeLabelContainer.addComponent(setSizeLabel);
        setSizeLabelContainer.setComponentAlignment(setSizeLabel, Alignment.BOTTOM_RIGHT);
        setSizeLabelContainer.setExpandRatio(setSizeLabel, 50);
        VerticalLayout v1 = new VerticalLayout();
        setSizeLabelContainer.addComponent(v1);
        setSizeLabelContainer.setExpandRatio(v1, 50);
        VerticalLayout v2 = new VerticalLayout();
        setSizeLabelContainer.addComponent(v2);
        setSizeLabelContainer.setExpandRatio(v2, 1);

        barChartContainerPanel = new Panel();
        barChartContainerPanel.setStyleName(ValoTheme.PANEL_BORDERLESS);

        barChartContainerPanel.setSizeFull();
        barChartPanel.addComponent(barChartContainerPanel);
        barChartPanel.setExpandRatio(barChartContainerPanel, 80);
        barChartContainerPanel.setWidth(100, Unit.PERCENTAGE);
        barChartContainerPanel.setHeight(100, Unit.PERCENTAGE);
        barChartContainerPanel.addStyleName("scrolspacer");

        graphChartContainerPanel = new Panel();
        graphChartContainerPanel.setSizeFull();
        graphChartContainerPanel.setStyleName(ValoTheme.PANEL_BORDERLESS);
        graphChartContainerPanel.addStyleName("scrolspacer");
        filterContainer.addComponent(graphChartContainerPanel);

        this.Selection_Manager.RegistrFilter(MatrixLayoutChartFilter.this);

        thumbFilterContainer = new VerticalLayout();
        thumbFilterContainer.setStyleName("thumbfilterframe");
        thumbFilterContainer.setWidth(100, Unit.PERCENTAGE);
        thumbFilterContainer.setHeight(100, Unit.PERCENTAGE);
        thumbFilterContainer.setSpacing(true);
        thumbFilterContainer.setMargin(new MarginInfo(false, false, false, false));
//        thumbFilterContainer.setIcon(VaadinIcons.EXPAND_FULL);

        thumbTitle = new Label();
        thumbTitle.setContentMode(ContentMode.HTML);
        thumbTitle.setStyleName(ValoTheme.LABEL_BOLD);
        thumbTitle.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        thumbTitle.setWidth(100, Unit.PERCENTAGE);
        thumbTitle.setHeight(30, Unit.PIXELS);
        thumbTitle.addStyleName("centeredtext");
        thumbFilterContainer.addComponent(thumbTitle);
        thumbFilterContainer.setExpandRatio(thumbTitle, 1);

        thumbChartContainer = new VerticalLayout();
        thumbChartContainer.setStyleName("thumbchart");
        thumbChartContainer.setWidth(100, Unit.PERCENTAGE);
        thumbChartContainer.setHeight(95, Unit.PERCENTAGE);
        thumbFilterContainer.addComponent(thumbChartContainer);
        thumbFilterContainer.setExpandRatio(thumbChartContainer, 5);

        Panel thumbFilterPanel = new Panel();
        thumbFilterPanel.setHeight(100, Unit.PERCENTAGE);
        thumbFilterPanel.setWidth(95, Unit.PERCENTAGE);
        thumbFilterPanel.setStyleName(ValoTheme.PANEL_BORDERLESS);
        thumbFilterContainer.addComponent(thumbFilterPanel);
        thumbFilterContainer.setComponentAlignment(thumbFilterPanel, Alignment.TOP_CENTER);
        thumbFilterContainer.setExpandRatio(thumbFilterPanel, 69);

        thumbNodeContainer = new GridLayout();
        thumbNodeContainer.setWidth(100, Unit.PERCENTAGE);
        thumbFilterPanel.setContent(thumbNodeContainer);

        thumblegendContainer = new GridLayout();
        thumblegendContainer.setWidth(95, Unit.PERCENTAGE);
        thumblegendContainer.setHeight(100, Unit.PERCENTAGE);
        thumblegendContainer.setMargin(new MarginInfo(false, false, true, false));
        thumblegendContainer.setColumns(2);
        thumblegendContainer.setRows(10);
        thumblegendContainer.setSpacing(true);
        thumblegendContainer.setHideEmptyRowsAndColumns(true);
//        thumblegendContainer.setMargin(new MarginInfo(false, true, false , true));
        thumbFilterContainer.addComponent(thumblegendContainer);
        thumbFilterContainer.setComponentAlignment(thumblegendContainer, Alignment.TOP_CENTER);
        thumbFilterContainer.setExpandRatio(thumblegendContainer, 25);

    }

    public void calculateChartData(Map<String, Set<Comparable>> data, Map<String, Color> dataColors, Set<Object> selectedCategories, int totalNumber) {
        selectedDataSet.clear();
        selectedColumns = -1;
        rows.clear();
        rowLabelsMap.clear();
        appliedFilters = -1;
        this.dataColors = dataColors;
        columns = calculateMatrix(data);
        calculatedMatrix.clear();
        calculatedMatrix.putAll(columns);
        totalItemsNumber = totalNumber;

        List<Double> barChartData = new ArrayList<>();
        for (Set<Comparable> set : columns.values()) {
            barChartData.add((double) set.size());
        }
        TreeSet<Double> ts = new TreeSet<>(barChartData);
        barChartContainerPanel.setContent(initBarChartFilter(barChartData));
        HorizontalLayout graphLayout = initGraph(ts.last(), columns, rows);
        graphChartContainerPanel.setContent(graphLayout);
        unselectAll();
        int index = 0;
        for (String col : columns.keySet()) {
            if (selectedCategories.contains(col)) {
                selectColumn(index);
            }
            index++;
        }
        this.updateLabels();

    }

    private Map<String, Set<Comparable>> calculateMatrix(Map<String, Set<Comparable>> data) {

//        Map<TreeSet<String>, Set<String>> temMatrixData = new LinkedHashMap<>();
//        for (String key : data.keySet()) {
//            TreeSet<String> keySet = new TreeSet<>();
//            keySet.add(key);
//            temMatrixData.put(keySet, data.get(key));
//
//        }
//        Map<TreeSet<String>, Set<String>> finalMatrixData = new LinkedHashMap<>(temMatrixData);
//        for (String key : data.keySet()) {
//            for (TreeSet<String> key2 : temMatrixData.keySet()) {
//                Set<String> intersection = new LinkedHashSet<>();
//                intersection.addAll(com.google.common.collect.Sets.intersection(temMatrixData.get(key2), data.get(key)));
//                if (!key2.contains(key) && !intersection.isEmpty()) {
//                    System.out.println("at ----- there is intersection " + key2 + "    " + key);
//                    TreeSet<String> newkeySet = new TreeSet<>();
//                    newkeySet.add(key);
//                    newkeySet.addAll(key2);
//                    finalMatrixData.put(newkeySet, intersection);
//                }
//
//            }
//        }
//calculate matrix
        Map<String, Set<Comparable>> matrixData = new LinkedHashMap<>();
        TreeMap<AlphanumComparator, String> sortingMap = new TreeMap<>(Collections.reverseOrder());
        for (String key : data.keySet()) {
            AlphanumComparator sortingKey = new AlphanumComparator(data.get(key).size() + "_" + key);
            sortingMap.put(sortingKey, key);
        }
        Map<String, Set<Comparable>> sortedData = new LinkedHashMap<>();
        for (String key : sortingMap.values()) {
            int size = data.get(key).size();
            this.rows.put(key, size);
            sortedData.put(key, data.get(key));
        }
        Map<String, Set<Comparable>> rowsII = new LinkedHashMap<>(sortedData);
        Map<String, Set<Comparable>> tempColumns = new LinkedHashMap<>();
        tempColumns.putAll(sortedData);
        Map<String, Set<Comparable>> trows = new LinkedHashMap<>(sortedData);
        for (String keyI : sortedData.keySet()) {
            for (String keyII : rowsII.keySet()) {
                if (keyI.equals(keyII) || keyII.contains(keyI)) {
                    continue;
                }
                String key = (keyII + "," + keyI).replace("[", "").replace("]", "");//.replace(" ", "");
                keySorter.addAll(Arrays.asList(key.split(",")));
                key = keySorter.toString();
                keySorter.clear();
                if (trows.containsKey(key)) {
                    Set<Comparable> union = new LinkedHashSet<>();
                    union.addAll(com.google.common.collect.Sets.union(trows.get(key), com.google.common.collect.Sets.intersection(rowsII.get(keyII), sortedData.get(keyI))));
                    trows.put(key, union);
                } else {
                    Set<Comparable> intersection = new LinkedHashSet<>();
                    intersection.addAll(com.google.common.collect.Sets.intersection(rowsII.get(keyII), sortedData.get(keyI)));
                    trows.put(key, intersection);
                    Set<Comparable> tempSetI = new LinkedHashSet<>();
                    tempSetI.addAll(rowsII.get(keyII));
                    tempSetI.removeAll(intersection);
                    rowsII.replace(keyII, tempSetI);
                    Set<Comparable> tempSetII = new LinkedHashSet<>();
                    tempSetII.addAll(sortedData.get(keyI));
                    tempSetII.removeAll(intersection);
                    sortedData.replace(keyI, tempSetII);
                }
            }
            rowsII.clear();
            rowsII.putAll(trows);
            tempColumns.putAll(trows);
        }
        Map<AlphanumComparator, String> sortingMap2 = new TreeMap<>(Collections.reverseOrder());
        for (String key : tempColumns.keySet()) {
            AlphanumComparator sortingKey = new AlphanumComparator(tempColumns.get(key).size() + "_" + key);
            sortingMap2.put(sortingKey, key);
        }
        List<String> sortingKeysList = new ArrayList<>(rows.keySet());
        Map<Integer, String> sortingKysMap = new TreeMap<>();
        for (String key : sortingMap2.values()) {
            String[] arr = key.split(",");
            String updatedKey = key;
            if (arr.length > 1) {
                sortingKysMap.clear();
                for (String sub : arr) {
                    sub = sub.replace("]", "").replace("[", "").trim();
                    sortingKysMap.put(sortingKeysList.indexOf(sub), sub);
                }
                updatedKey = sortingKysMap.values().toString();
            }
            if (!tempColumns.get(key).isEmpty()) {
                matrixData.put(updatedKey, tempColumns.get(key));
            }
        }

        for (String key1 : matrixData.keySet()) {
            for (String key2 : matrixData.keySet()) {
                HashSet<Comparable> intersction = new HashSet<>();
                intersction.addAll(Sets.intersection(matrixData.get(key2), matrixData.get(key1)));
                if (!intersction.isEmpty() && !key2.equalsIgnoreCase(key1)) {
                    if (key1.split(",").length > key2.split(",").length) {
                        matrixData.get(key2).removeAll(intersction);
                    } else if (key1.split(",").length < key2.split(",").length) {
                        matrixData.get(key1).removeAll(intersction);
                    } else {
                        matrixData.get(key1).removeAll(intersction);
                        matrixData.get(key2).removeAll(intersction);
                    }
                }
            }
        }

        Map<String, Set<Comparable>> tempMatrixData = new LinkedHashMap<>(matrixData);
        for (String key1 : tempMatrixData.keySet()) {
            if (matrixData.get(key1).isEmpty()) {
                matrixData.remove(key1);
            }

        }

        return matrixData;
    }

    private AbsoluteLayout initBarChartFilter(List<Double> data) {
        TreeSet<Double> maxSet = new TreeSet<>(data);
        int step = maxSet.last().intValue();
        step = Math.max((step / 10), 5);
        while (step % 10 != 0) {
            step++;
        }
        //init color array
        int i = 0;
        List<String> dataLabels = new ArrayList<>();
        List<Double> thumbData = new ArrayList<>();
        for (double d : data) {
            thumbData.add(0.0);
            dataLabels.add("" + (int) d);
        }
        colors = new String[data.size()];
        for (int x = 0; x < colors.length; x++) {
            colors[x] = unselectedColor;
        }
        yAxisScale = new LinearScale().display(true).position(Position.LEFT).id("y-axis-1").scaleLabel().display(true).labelString("#Proteins").and().ticks().fixedStepSize(step).beginAtZero(Boolean.TRUE).and();
        barConfig = new BarChartConfig();
        barConfig.
                data().labels(dataLabels.toArray(new String[dataLabels.size()]))
                .addDataset(
                        new BarDataset().backgroundColor(colors).borderColor("gray").label("#Proteins").yAxisID("y-axis-1"))
                .and();
        barConfig.
                options().maintainAspectRatio(false)
                .responsive(true)
                .hover()
                .mode(InteractionMode.INDEX)
                .intersect(true)
                .animationDuration(1)
                .and()
                .title()
                .display(false)
                .text(this.title)
                .and()
                .scales()
                .add(Axis.Y, yAxisScale)
                .add(Axis.X, new CategoryScale().display(true).gridLines().display(false).and())
                .and().legend().display(false).and()
                .done();
        for (Dataset<?, ?> ds : barConfig.data().getDatasets()) {
            BarDataset lds = (BarDataset) ds;
            lds.dataAsList(data);
            lds.fill(false);
        }
        chartContainer = new VerticalLayout();
        chartContainer.setSizeFull();
        AbsoluteLayout container = new AbsoluteLayout();
        container.setWidth(100, Unit.PERCENTAGE);
        container.setHeight(100, Unit.PERCENTAGE);

        thumbChartContainer.removeAllComponents();
        BarChartConfig tcon = new BarChartConfig();
        tcon.data().labels(dataLabels.toArray(new String[dataLabels.size()])).addDataset(
                new BarDataset().dataAsList(thumbData).fill(false).backgroundColor(colors).borderColor("gray"));
        tcon.options().scales().add(Axis.Y, new LinearScale().display(false)).add(Axis.X, new CategoryScale().display(true).gridLines().display(false).and())
                .and().legend().display(false).and().done();
        ChartJs thumbChart = new ChartJs(tcon);
        thumbChart.setJsLoggingEnabled(true);

        thumbChart.setWidth(100, Unit.PERCENTAGE);
        thumbChart.setHeight(20, Unit.PIXELS);
        thumbChartContainer.addComponent(thumbChart);

//        labelContainer.removeAllComponents();
//        labelContainer.setVisible(false);
//        labelContainer.setStyleName("barchartlabelcontainer");
//        int x0;
//        if (protNumber < 100) {
//            x0 = 40;
//        } else {
//            x0 = 50;
//        }
//        labelContainer.addLayoutClickListener((LayoutEvents.LayoutClickEvent event) -> {
//            Component c = event.getClickedComponent();
//            if (c != null) {
//                selectColumn(labelContainer.getComponentIndex(c));
//            }
//        });
//        labelContainer.addStyleName("padding" + x0);
//        labelContainer.setWidth(100, Unit.PERCENTAGE);
//        labelContainer.setHeight(40, Unit.PIXELS);
//        labelContainer.setMargin(new MarginInfo(false, false, true, false));
//        Iterator<String> itr = columns.keySet().iterator();
//        for (double labelSize : data) {
//            VerticalLayout labelLayout = new VerticalLayout();
//            labelLayout.setSizeFull();
//
//            String label = "" + (int) labelSize;
//            Label l = new Label(label);
//            labelLayout.addComponent(l);
//            labelLayout.setExpandRatio(l, 1);
//            labelLayout.setComponentAlignment(l, Alignment.BOTTOM_CENTER);
//            l.setContentMode(ContentMode.HTML);
//            l.setData((int) 0);
//
//            String columnTitle = itr.next().replace("[", "").replace("]", "").trim();
//            GridLayout colorlabelsContainer = new GridLayout(3, 3);
//            colorlabelsContainer.setHideEmptyRowsAndColumns(true);
//            int r = 0;
//            int c = 0;
//            int counter = 0;
//            for (String value : columnTitle.split(",")) {
//                if (c == 3) {
//                    c = 0;
//                    r++;
//                }
//
//                Label color = new Label("<center><div style='width: 10px;height: 10px;border: 1px solid lightgray;background: rgb(" + Color.RED.getRed() + "," + Color.RED.getGreen() + "," + Color.RED.getBlue() + ");'></div></center>");
//                colorlabelsContainer.addComponent(color, c++, r);
//                color.setWidth(100, Unit.PERCENTAGE);
//                color.setHeight(100, Unit.PERCENTAGE);
//                color.setContentMode(ContentMode.HTML);
//
//                if (counter < 3) {
//                    counter++;
//                }
//            }
//
//            colorlabelsContainer.setWidth(counter * 20, Unit.PIXELS);
//            colorlabelsContainer.setHeight(100, Unit.PERCENTAGE);
//            labelLayout.addComponent(colorlabelsContainer);
//            labelLayout.setComponentAlignment(colorlabelsContainer, Alignment.BOTTOM_CENTER);
//            labelLayout.setExpandRatio(colorlabelsContainer, r);
//            labelContainer.addComponent(labelLayout);
//            labelContainer.setComponentAlignment(labelLayout, Alignment.BOTTOM_CENTER);
//        }
//        container.addComponent(labelContainer, "left: " + 0 + "px; bottom: " + 10 + "%");
        container.addComponent(chartContainer);
        redrawChart();

        return container;
    }

    private HorizontalLayout initGraph(double protNumber, Map<String, Set<Comparable>> columns, Map<String, Integer> rows) {

        while (protNumber % 10 != 0) {
            protNumber++;
        }
        HorizontalLayout graphLabelsContainer = new HorizontalLayout();
        graphLabelsContainer.setMargin(new MarginInfo(true, false, false, false));
        graphLabelsContainer.setSizeFull();
        rowsLabelsLayoutContainer = new VerticalLayout();
        rowsLabelsLayoutContainer.setSizeFull();
        rowsLabelsLayoutContainer.setMargin(new MarginInfo(false, false, false, false));
        graphLabelsContainer.addComponent(rowsLabelsLayoutContainer);
        graphLabelsContainer.setExpandRatio(rowsLabelsLayoutContainer, 20);
        VerticalLayout graphContainer = new VerticalLayout();
        graphContainer.setSizeFull();
        graphLabelsContainer.addComponent(graphContainer);
        graphLabelsContainer.setExpandRatio(graphContainer, 80);
        int x0;
        if (protNumber < 100) {
            x0 = 40;
        } else if ((protNumber >= 100 && protNumber < 1000)) {
            x0 = 50;
        } else {
            x0 = 55;
        }

        graphContainer.addStyleName("padding" + x0);
        AbsoluteLayout wrapper = new AbsoluteLayout();
        graphContainer.addComponent(wrapper);
        wrapper.setWidth(99, Unit.PERCENTAGE);
        int rowNum = rows.size();

        graphLabelsContainer.setHeight(rowNum * 35, Unit.PIXELS);
        wrapper.setHeight(100, Unit.PERCENTAGE);
        nodeContainer = new GridLayout(columns.size(), rowNum);
        nodeContainer.setSizeFull();
        wrapper.addComponent(nodeContainer);
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        for (int i : rows.values()) {
            if (max < i) {
                max = i;
            }
            if (min > i) {
                min = i;
            }
        }
        setSizeLabel.setValue(max + "  <font>" + min + "</font>");

//        LayoutEvents.LayoutClickListener listener = (LayoutEvents.LayoutClickEvent event) -> {
//            Component c = event.getClickedComponent();
//            if (c instanceof SparkLine) {
//                setSelectRow((Integer) ((SparkLine) c).getData(), !((SparkLine) c).isSelected());
//                ((SparkLine) c).setSelected(!((SparkLine) c).isSelected());
//            } else if (c instanceof ColorLabel || c instanceof Label) {
//                setSelectRow((Integer) ((SparkLine) c.getParent()).getData(), !((SparkLine) c.getParent()).isSelected());
//                ((SparkLine) c.getParent()).setSelected(!((SparkLine) c.getParent()).isSelected());
//
//            } else {
//                unselectAll();
//            }
//
//        };
//        labelsContainer.addLayoutClickListener(listener);
        rowLabelsMap.clear();
        int x = 0;
        thumblegendContainer.removeAllComponents();

        int r = 0;
        int c = 0;
        for (String rowKey : rows.keySet()) {
            SparkLine sl = new SparkLine(rowKey, rows.get(rowKey), 0, max, dataColors.get(rowKey));
            sl.setWidth(100, Unit.PERCENTAGE);
            sl.setHeight(50, Unit.PERCENTAGE);
//            sl.addStyleName("pointer");
            sl.setData(x);
            sl.setDescription(rowKey);

            SparkLine sl2 = new SparkLine(rowKey, 0.0, 0, -100000, dataColors.get(rowKey));
            sl2.setWidth(100, Unit.PERCENTAGE);
            sl2.setHeight(100, Unit.PERCENTAGE);
            thumblegendContainer.addComponent(sl2, c++, r);
            if (c % 2 == 0) {
                thumblegendContainer.setComponentAlignment(sl2, Alignment.TOP_RIGHT);
            } else {
                thumblegendContainer.setComponentAlignment(sl2, Alignment.TOP_LEFT);
            }
            if (c == 2) {
                c = 0;
                r++;
            }

            rowsLabelsLayoutContainer.addComponent(sl);
            rowsLabelsLayoutContainer.setComponentAlignment(sl, Alignment.MIDDLE_CENTER);
            rowLabelsMap.put(rowKey, sl);
            int y = 0;
            for (String columnKey : columns.keySet()) {

                SelectableNode node = new SelectableNode(rowKey, y, columns.get(columnKey).isEmpty(), dataColors.get(rowKey)) {
                    @Override
                    public void selectNode(int columnIndex) {
                        Iterator<Component> itr = rowsLabelsLayoutContainer.iterator();
                        while (itr.hasNext()) {
                            ((SparkLine) itr.next()).setSelected(false);
                        }
                        selectColumn(columnIndex);
                    }

                };
                node.setData(columns.get(columnKey).size());
                node.setDescription(columnKey);
                nodeContainer.addComponent(node, y, x);
                nodeContainer.setComponentAlignment(node, Alignment.MIDDLE_CENTER);
                if (y % 2 == 0) {
                    node.addStyleName("hilightcolumn");
                }
                y++;
            }
            x++;

        }
        int z = 0;
        List<String> sortingKeysList = new ArrayList<>(rows.keySet());
        for (String columnKey : columns.keySet()) {
            String[] subArr = columnKey.replace("]", "").replace("[", "").trim().split(",");
            int startLineRange = sortingKeysList.indexOf(subArr[0]);
            int endLineRange = sortingKeysList.indexOf(subArr[subArr.length - 1].trim());
            for (int i = 0; i < nodeContainer.getRows(); i++) {
                SelectableNode node = (SelectableNode) nodeContainer.getComponent(z, i);

                if (columnKey.contains(node.getNodeId())) {
                    node.setSelecatble(true);
                    node.setUpperSelected(true);
                    node.setLowerSelected(true);

                } else {
                    node.setSelecatble(false);
                }
                if (columnKey.split(",").length == 1) {
                    node.setUpperSelected(false);
                    node.setLowerSelected(false);
                } else {
                    int nodeIndex = sortingKeysList.indexOf(node.getNodeId());
                    if (nodeIndex == startLineRange) {
                        node.setUpperSelected(false);
                        node.setLowerSelected(true);
                    } else if (nodeIndex > startLineRange && nodeIndex < endLineRange) {
                        node.setUpperSelected(true);
                        node.setLowerSelected(true);
                    } else if (nodeIndex == endLineRange) {
                        node.setUpperSelected(true);
                        node.setLowerSelected(false);
                    } else {
                        node.setUpperSelected(false);
                        node.setLowerSelected(false);
                    }
                }

            }
            z++;

        }

        thumbNodeContainer.removeAllComponents();
        thumbNodeContainer.setColumns(this.nodeContainer.getColumns());
        thumbNodeContainer.setRows(this.nodeContainer.getRows());
        thumbNodeContainer.setHeight(rowNum * 35, Unit.PIXELS);
        for (int i = 0; i < nodeContainer.getRows(); i++) {
            for (int n = 0; n < nodeContainer.getColumns(); n++) {
                SelectableNode node = (SelectableNode) nodeContainer.getComponent(n, i);
                SelectableNode temNode = new SelectableNode(node.getId(), node.getColumnIndex(), node.isDisables(), node.getNodeColor()) {
                    @Override
                    public void selectNode(int columnIndex) {
                        Iterator<Component> itr = rowsLabelsLayoutContainer.iterator();
                        while (itr.hasNext()) {
                            ((SparkLine) itr.next()).setSelected(false);
                        }
                        selectColumn(columnIndex);
                        applyFilter(getSelectedDataSet());
                        close();
                    }
                };
                temNode.setSelected(node.isSelected());
                temNode.setSelecatble(node.isSelecatble());
                temNode.setUpperSelected(node.isUpperSelected());
                temNode.setLowerSelected(node.isLowerSelected());
                temNode.setDescription(node.getDescription());
                if (n % 2 == 0) {
                    temNode.addStyleName("hilightcolumn");
                }
                thumbNodeContainer.addComponent(temNode, n, i);
                thumbNodeContainer.setComponentAlignment(temNode, Alignment.MIDDLE_CENTER);
            }

        }

        return graphLabelsContainer;
    }

    private void unselectAll() {
        selectedDataSet.clear();
        for (int col = 0; col < nodeContainer.getColumns(); col++) {
            for (int row = 0; row < nodeContainer.getRows(); row++) {
                SelectableNode node = (SelectableNode) nodeContainer.getComponent(col, row);
                node.setSelected(false);
                SelectableNode temnode = (SelectableNode) thumbNodeContainer.getComponent(col, row);
                temnode.setSelected(false);
            }
            colors[col] = unselectedColor;
        }
        ((BarDataset) barConfig.data().getDatasets().get(0)).backgroundColor(colors);
        barConfig.options().animation().duration(1);
        redrawChart();
        Iterator<Component> itr = rowsLabelsLayoutContainer.iterator();
        while (itr.hasNext()) {
            ((SparkLine) itr.next()).setSelected(false);
        }
        this.updateLabels();
    }

    private void updateLabels() {
        int currentSize = 0;
        int subTotalNumber = 0;
        for (int r = 0; r < nodeContainer.getRows(); r++) {
            for (int c = 0; c < nodeContainer.getColumns(); c++) {
                SelectableNode node = (SelectableNode) nodeContainer.getComponent(c, r);
                if (r == 0) {
                    subTotalNumber += (int) node.getData();
                }
                if (node.isSelecatble() && node.isSelected() && !node.isUpperSelected()) {
                    currentSize += (int) node.getData();
                }
            }

        }
        if (currentSize == 0) {
            currentSize = subTotalNumber;
        }
        chartTitle.setValue("" + title + " (" + currentSize + "/" + totalItemsNumber + ")");
        thumbTitle.setValue(chartTitle.getValue());
    }

    private void setSelectRow(Integer rowIndex, boolean selected) {
        if (rowIndex == null) {
            unselectAll();
        } else {
            for (int col = 0; col < nodeContainer.getColumns(); col++) {
                SelectableNode node = (SelectableNode) nodeContainer.getComponent(col, rowIndex);
                if (node.isSelecatble()) {
                    if (selected) {
                        colors[col] = unselectedColor;
                    } else {
                        colors[col] = selectedColor;
                    }
                    selectColumn(col);
                }

            }

        }
    }

    private void selectColumn(int columnIndex) {

        String selectedColumnTitle = columns.keySet().toArray(new String[columns.size()])[columnIndex];
        for (SparkLine rowLabel : rowLabelsMap.values()) {
            rowLabel.removeStyleName("highlightedtext");
        }
        Iterator<Component> itr = nodeContainer.iterator();
        while (itr.hasNext()) {
            SelectableNode node = (SelectableNode) itr.next();
            node.setSelected(false);
        }
        Iterator<Component> thumbItr = thumbNodeContainer.iterator();
        while (thumbItr.hasNext()) {
            SelectableNode node = (SelectableNode) thumbItr.next();
            node.setSelected(false);
        }

        boolean select;
        if (colors[columnIndex].equals(selectedColor)) {
            colors[columnIndex] = unselectedColor;
            select = false;
        } else {
            selectedDataSet.clear();
            for (int col = 0; col < nodeContainer.getColumns(); col++) {
                colors[col] = unselectedColor;
            }
            colors[columnIndex] = selectedColor;
            select = true;
        }
        ((BarDataset) barConfig.data().getDatasets().get(0)).backgroundColor(colors);
        barConfig.options().animation().duration(1).and().done();

        for (int row = 0; row < nodeContainer.getRows(); row++) {
            SelectableNode node = (SelectableNode) nodeContainer.getComponent(columnIndex, row);
            node.setSelected(select);
            SelectableNode temnode = (SelectableNode) thumbNodeContainer.getComponent(columnIndex, row);
            temnode.setSelected(select);

        }
        if (select) {
            String[] columnTitleArr = selectedColumnTitle.replace("[", "").replace("]", "").trim().split(", ");
            for (String rowTitle : columnTitleArr) {
                rowLabelsMap.get(rowTitle).addStyleName("highlightedtext");
            }
            selectedDataSet.addAll(columns.get(selectedColumnTitle));
            selectedColumns = columnIndex;
        } else {
            selectedDataSet.removeAll(columns.get(selectedColumnTitle));
            selectedColumns = -1;
        }

        redrawChart();
        this.updateLabels();
    }

    @Override
    public void redrawChart() {
        yAxisScale.scaleLabel().display(true).and().gridLines().display(true).and().ticks().display(true).and();
        barConfig.options()
                .title()
                .display(false)
                .and()
                .done();
        chartContainer.removeAllComponents();
        chart = new ChartJs(barConfig);
        chart.setJsLoggingEnabled(true);
        chart.addClickListener((int datasetIndex, int dataIndex) -> {
            chart.setData(dataIndex);
            Iterator<Component> itr = rowsLabelsLayoutContainer.iterator();
            while (itr.hasNext()) {
                ((SparkLine) itr.next()).setSelected(false);
            }
            selectColumn(dataIndex);

        });
        chart.setWidth(100, Unit.PERCENTAGE);
        chart.setHeight(100, Unit.PERCENTAGE);
        chartContainer.addComponent(chart);

    }

    @Override
    public String getFilterId() {
        return filterId;
    }

    @Override
    public void resetFilter() {
        selectedDataSet.clear();
        selectedColumns = -1;
        appliedFilters = -1;
        calculatedMatrix.clear();
        calculatedMatrix.putAll(columns);
        List<Double> barChartData = new ArrayList<>();
        for (Set<Comparable> set : columns.values()) {
            barChartData.add((double) set.size());
        }
        TreeSet<Double> ts = new TreeSet<>(barChartData);
        barChartContainerPanel.setContent(initBarChartFilter(barChartData));
        HorizontalLayout graphLayout = initGraph(ts.last(), columns, rows);
        graphChartContainerPanel.setContent(graphLayout);
        unselectAll();
        this.updateLabels();
    }

    @Override
    public void updateFilter(Set<Comparable> selection, Set<Object> selectedCategories, boolean singleFilter) {

        List<Double> newDataValues = new ArrayList<>();
        List<String> newLabels = new ArrayList<>();
        List<Integer> selectedCategoriesIndexes = new ArrayList<>();
        Map<String, Set<Comparable>> updatedColumns = new LinkedHashMap<>();
        int index = 0;
        for (String colId : columns.keySet()) {
            Set<Comparable> intersection = new LinkedHashSet<>();
            if (singleFilter && !selectedCategories.isEmpty()) {
                intersection.addAll(columns.get(colId));
            } else {
                intersection.addAll(Sets.intersection(selection, columns.get(colId)));
            }
            newDataValues.add((double) intersection.size());
            updatedColumns.put(colId, intersection);
            newLabels.add(colId);
            if (selectedCategories.contains(colId)) {
                selectedCategoriesIndexes.add(index);
            }
            index++;
        }
        TreeSet<Double> ts = new TreeSet<>(newDataValues);
        barChartContainerPanel.setContent(initBarChartFilter(newDataValues));
        HorizontalLayout graphLayout = initGraph(ts.last(), updatedColumns, rows);
        graphChartContainerPanel.setContent(graphLayout);
        for (int i : selectedCategoriesIndexes) {
            selectColumn(i);
        }
        appliedFilters = (selectedColumns);
        this.updateLabels();
        if (singleFilter && !selectedCategories.isEmpty()) {
            applyFilter(getSelectedDataSet());
        }

    }

    public Set<Comparable> getSelectedDataSet() {
        return selectedDataSet;
    }

    public abstract void close();

    public void applyFilter(Set<Comparable> selectedDataset) {

        appliedFilters = (selectedColumns);
        Selection_Manager.setSelection("protein_selection", selectedDataset, filterId);
    }

    @Override
    public Component getThumb() {

        return thumbFilterContainer;
    }

    public Map<String, Set<Comparable>> getCalculatedMatrix() {
        return calculatedMatrix;
    }

    @Override
    public boolean isAppliedFilter() {
        return !(appliedFilters == -1);

    }

    @Override
    public Set<Object> getSelectedCategories() {
        Set<Object> s = new HashSet<>();
        if (appliedFilters != -1) {
            s.add(appliedFilters);
        }
        return s;
    }

}
