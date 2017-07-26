package com.uib.web.peptideshaker.presenter.core.filtercharts;

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
import com.uib.web.peptideshaker.presenter.core.filtercharts.components.SelectableNode;
import com.uib.web.peptideshaker.presenter.core.form.ColorLabel;
import com.uib.web.peptideshaker.presenter.core.form.SparkLine;
import com.vaadin.event.LayoutEvents;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
public class MatrixLayoutChartFilter extends VerticalLayout {

    private final Panel barChartContainerPanel;
    private final Panel graphChartContainerPanel;
    private final Map<String, Set<String>> columns = new LinkedHashMap<>();
    private final Map<String, Integer> rows = new LinkedHashMap<>();
    private final Set<String> keySorter = new TreeSet<>();
    private final Label setSizeLabel;
    private final String title;

    public MatrixLayoutChartFilter(String title) {
        this.title = title;
        MatrixLayoutChartFilter.this.setWidth(80, Unit.PERCENTAGE);
        MatrixLayoutChartFilter.this.setHeight(80, Unit.PERCENTAGE);
        HorizontalLayout barChartPanel = new HorizontalLayout();
        barChartPanel.setSizeFull();
        MatrixLayoutChartFilter.this.addComponent(barChartPanel);
        VerticalLayout spacer = new VerticalLayout();
        spacer.setSizeFull();
        barChartPanel.addComponent(spacer);
        barChartPanel.setExpandRatio(spacer, 10);
        HorizontalLayout setSizeLabelContainer = new HorizontalLayout();
        setSizeLabelContainer.setSizeFull();
        setSizeLabelContainer.setMargin(new MarginInfo(false, false, false, true));
        setSizeLabelContainer.setSpacing(true);
        spacer.addComponent(setSizeLabelContainer);

        setSizeLabel = new Label("0  <font>0</font>");
        setSizeLabel.setContentMode(ContentMode.HTML);
        setSizeLabel.setWidth(100, Unit.PERCENTAGE);
        setSizeLabel.setStyleName(ValoTheme.LABEL_NO_MARGIN);
        setSizeLabel.addStyleName("sizelabel");
        setSizeLabelContainer.addComponent(setSizeLabel);
        setSizeLabelContainer.setComponentAlignment(setSizeLabel, Alignment.BOTTOM_RIGHT);
//         setSizeLabelContainer.setExpandRatio(setSizeLabel, 0.5f);
        setSizeLabelContainer.addComponent(new VerticalLayout());

        barChartContainerPanel = new Panel();
        barChartContainerPanel.setStyleName(ValoTheme.PANEL_BORDERLESS);

        barChartContainerPanel.setSizeFull();
        barChartPanel.addComponent(barChartContainerPanel);
        barChartPanel.setExpandRatio(barChartContainerPanel, 90);
        barChartContainerPanel.setWidth(100, Unit.PERCENTAGE);
        barChartContainerPanel.setHeight(100, Unit.PERCENTAGE);
        barChartContainerPanel.addStyleName("scrolspacer");

        graphChartContainerPanel = new Panel();
        graphChartContainerPanel.setSizeFull();
        graphChartContainerPanel.setStyleName(ValoTheme.PANEL_BORDERLESS);
        graphChartContainerPanel.addStyleName("scrolspacer");
        MatrixLayoutChartFilter.this.addComponent(graphChartContainerPanel);

    }

    public void updateChartData(Map<String, Set<String>> data) {
        calculateMatrix(data);
        List<Double> barChartData = new ArrayList<>();
        for (Set<String> set : columns.values()) {
            barChartData.add((double) set.size());
        }
        TreeSet<Double> ts = new TreeSet<>(barChartData);
        barChartContainerPanel.setContent(initBarChartFilter(ts.last(), barChartData, columns.keySet().toArray(new String[columns.size()])));

        HorizontalLayout graphLayout = initGraph(ts.last(), columns, rows);
        graphChartContainerPanel.setContent(graphLayout);
    }

    private void calculateMatrix(Map<String, Set<String>> data) {
        //calculate matrix
        columns.clear();
        rows.clear();
        TreeMap<AlphanumComparator, String> sortingMap = new TreeMap<>();
        for (String key : data.keySet()) {
            AlphanumComparator sortingKey = new AlphanumComparator(data.get(key).size() + "_" + key);
            sortingMap.put(sortingKey, key);
        }
        Map<String, Set<String>> sortedData = new LinkedHashMap<>();

        for (String key : sortingMap.values()) {
            this.rows.put(key, data.get(key).size());
            sortedData.put(key, data.get(key));
        }

        Map<String, Set<String>> rowsII = new LinkedHashMap<>(sortedData);
        Map<String, Set<String>> tempColumns = new LinkedHashMap<>();
        tempColumns.putAll(sortedData);
        Map<String, Set<String>> trows = new LinkedHashMap<>(sortedData);
        for (String keyI : sortedData.keySet()) {
            for (String keyII : rowsII.keySet()) {
                if (keyI.equals(keyII) || keyII.contains(keyI)) {
                    continue;
                }
                String key = (keyII + "," + keyI).replace("[", "").replace("]", "").replace(" ", "");

                keySorter.addAll(Arrays.asList(key.split(",")));
                key = keySorter.toString();
                keySorter.clear();

                if (trows.containsKey(key)) {
                    Set<String> union = new LinkedHashSet<>(Sets.union(trows.get(key), Sets.intersection(rowsII.get(keyII), sortedData.get(keyI))));
                    trows.put(key, union);
                } else {
                    Set<String> intersection = new LinkedHashSet<>(Sets.intersection(rowsII.get(keyII), sortedData.get(keyI)));
                    trows.put(key, intersection);
                    rowsII.get(keyII).removeAll(intersection);
                    sortedData.get(keyI).removeAll(intersection);
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
                this.columns.put(updatedKey, tempColumns.get(key));
            }
        }

    }

    private AbsoluteLayout initBarChartFilter(double protNumber, List<Double> data, String[] labels) {

        TreeSet<Double> maxSet = new TreeSet<>(data);
        int step = maxSet.last().intValue();
        step = Math.max((step / 10), 1);

        //init color array
        colors = new String[labels.length];
        for (int x = 0; x < colors.length; x++) {
            colors[x] = unselectedColor;
        }

        barConfig = new BarChartConfig();
        barConfig.
                data().labels(labels)
                .addDataset(
                        new BarDataset().backgroundColor(colors).borderColor("gray").label("Dataset 1").yAxisID("y-axis-1"))
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
                .display(true)
                .text(this.title)
                .and()
                .scales()
                .add(Axis.Y, new LinearScale().display(true).position(Position.LEFT).id("y-axis-1").scaleLabel().display(true).labelString("#Proteins").and().ticks().fixedStepSize(step).beginAtZero(Boolean.TRUE).and())
                .add(Axis.X, new CategoryScale().display(false).gridLines().display(false).and())
                .and().legend().display(false).and()
                .done();

        for (Dataset<?, ?> ds : barConfig.data().getDatasets()) {
            BarDataset lds = (BarDataset) ds;
            lds.dataAsList(data);
            lds.fill(false);

        }

//        chart = new ChartJs(barConfig);
//        chart.setJsLoggingEnabled(true);
//        chart.addClickListener((int datasetIndex, int dataIndex) -> {
//            selectedNode(dataIndex);
//        });
//        chart.setWidth(100, Unit.PERCENTAGE);
//        chart.setHeight(100, Unit.PERCENTAGE);
        chartContainer = new VerticalLayout();
        chartContainer.setSizeFull();
//        chartContainer.addComponent(chart);

        AbsoluteLayout container = new AbsoluteLayout();
        container.setWidth(100, Unit.PERCENTAGE);
        container.setHeight(100, Unit.PERCENTAGE);

        HorizontalLayout labelContainer = new HorizontalLayout();
        labelContainer.setStyleName("barchartlabelcontainer");
        int x0;
        if (protNumber < 100) {
            x0 = 40;
        } else {
            x0 = 50;
        }
        labelContainer.addStyleName("padding" + x0);
        labelContainer.setWidth(100, Unit.PERCENTAGE);
        labelContainer.setHeight(20, Unit.PIXELS);
        labelContainer.setMargin(new MarginInfo(false, false, true, false));
        for (double labelSize : data) {
            Label l = new Label("" + (int) labelSize);
            l.setWidth(20, Unit.PIXELS);
            l.setHeight(20, Unit.PIXELS);
            labelContainer.addComponent(l);
            labelContainer.setComponentAlignment(l, Alignment.BOTTOM_CENTER);
        }
        container.addComponent(labelContainer, "left: " + 0 + "px; bottom: " + 10 + "%");
        container.addComponent(chartContainer);
        redrawBarChart();
        return container;
    }

    private HorizontalLayout initGraph(double protNumber, Map<String, Set<String>> columns, Map<String, Integer> rows) {

        HorizontalLayout graphLabelsContainer = new HorizontalLayout();
        graphLabelsContainer.setSizeFull();

        labelsContainer = new VerticalLayout();
        labelsContainer.setSizeFull();
        labelsContainer.setMargin(new MarginInfo(false, false, false, true));
        graphLabelsContainer.addComponent(labelsContainer);
        graphLabelsContainer.setExpandRatio(labelsContainer, 10);

        VerticalLayout graphContainer = new VerticalLayout();
        graphContainer.setSizeFull();
        graphLabelsContainer.addComponent(graphContainer);
        graphLabelsContainer.setExpandRatio(graphContainer, 90);
        int x0;
        if (protNumber < 100) {
            x0 = 40;
        } else {
            x0 = 50;
        }
        graphContainer.addStyleName("padding" + x0);
        AbsoluteLayout wrapper = new AbsoluteLayout();
        graphContainer.addComponent(wrapper);
        wrapper.setWidth(100, Unit.PERCENTAGE);
        int rowNum = rows.size();

        graphLabelsContainer.setHeight(rowNum * 40, Unit.PIXELS);
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

        LayoutEvents.LayoutClickListener listener = (LayoutEvents.LayoutClickEvent event) -> {
            Component c = event.getClickedComponent();
            System.out.println("at componmet " + c);
            if (c instanceof SparkLine) {
                setSelectRow((Integer) ((SparkLine) c).getData(), !((SparkLine) c).isSelected());
                ((SparkLine) c).setSelected(!((SparkLine) c).isSelected());
            } else if (c instanceof ColorLabel || c instanceof Label) {
                setSelectRow((Integer) ((SparkLine) c.getParent()).getData(), !((SparkLine) c.getParent()).isSelected());
                ((SparkLine) c.getParent()).setSelected(!((SparkLine) c.getParent()).isSelected());

            } else {
                unselectAll();
            }

        };
        labelsContainer.addLayoutClickListener(listener);

        int x = 0;
        for (String rowKey : rows.keySet()) {
            SparkLine sl = new SparkLine(rowKey, rows.get(rowKey), 0, max);
            sl.setWidth(100, Unit.PERCENTAGE);
            sl.setHeight(50, Unit.PERCENTAGE);
            sl.addStyleName("pointer");
            labelsContainer.addComponent(sl);
            sl.setData(x);
//            sl.addLayoutClickListener(listener);
            labelsContainer.setComponentAlignment(sl, Alignment.MIDDLE_CENTER);

            int y = 0;
            for (String columnKey : columns.keySet()) {

                SelectableNode node = new SelectableNode(rowKey, y, columns.get(columnKey).isEmpty()) {
                    @Override
                    public void selectNode(int columnIndex) {
                        Iterator<Component> itr = labelsContainer.iterator();
                        while (itr.hasNext()) {
                            ((SparkLine) itr.next()).setSelected(false);
                        }
                        selectColumn(columnIndex);
                    }

                };
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
                if (columnKey.length() == 1) {
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

        return graphLabelsContainer;
    }

    private BarChartConfig barConfig;
    private ChartJs chart;
    private String[] colors;
    private VerticalLayout chartContainer;
    private final String unselectedColor = "lightgray";
    private final String selectedColor = "#1780E9";
    private GridLayout nodeContainer;
    private VerticalLayout labelsContainer;

    private void unselectAll() {
        for (int col = 0; col < nodeContainer.getColumns(); col++) {
            for (int row = 0; row < nodeContainer.getRows(); row++) {
                SelectableNode node = (SelectableNode) nodeContainer.getComponent(col, row);
                node.setSelected(false);
            }
            colors[col] = unselectedColor;
        }
        ((BarDataset) barConfig.data().getDatasets().get(0)).backgroundColor(colors);
        barConfig.options().animation().duration(1);
        redrawBarChart();
        Iterator<Component> itr = labelsContainer.iterator();
        while (itr.hasNext()) {
            ((SparkLine) itr.next()).setSelected(false);
        }
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
        boolean select;
        if (colors[columnIndex].equals(selectedColor)) {
            colors[columnIndex] = unselectedColor;
            select = false;
        } else {
            colors[columnIndex] = selectedColor;
            select = true;
        }
        ((BarDataset) barConfig.data().getDatasets().get(0)).backgroundColor(colors);
        barConfig.options().animation().duration(1).and().done();
        redrawBarChart();

        for (int row = 0; row < nodeContainer.getRows(); row++) {
            SelectableNode node = (SelectableNode) nodeContainer.getComponent(columnIndex, row);
            node.setSelected(select);

        }

    }

    private void redrawBarChart() {
        chartContainer.removeAllComponents();
        chart = new ChartJs(barConfig);
        chart.setJsLoggingEnabled(true);
        chart.addClickListener((int datasetIndex, int dataIndex) -> {
            Iterator<Component> itr = labelsContainer.iterator();
            while (itr.hasNext()) {
                ((SparkLine) itr.next()).setSelected(false);
            }
            selectColumn(dataIndex);
        });
        chart.setWidth(100, Unit.PERCENTAGE);
        chart.setHeight(100, Unit.PERCENTAGE);
        chartContainer.addComponent(chart);
    }
}
