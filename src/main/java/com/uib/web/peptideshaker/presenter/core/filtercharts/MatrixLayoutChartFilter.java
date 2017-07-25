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
import com.uib.web.peptideshaker.presenter.core.form.SparkLine;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
//   private final  VerticalLayout graphLabelsContainer ;

    public MatrixLayoutChartFilter(String title) {
        MatrixLayoutChartFilter.this.setWidth(80, Unit.PERCENTAGE);
        MatrixLayoutChartFilter.this.setHeight(80, Unit.PERCENTAGE);
        HorizontalLayout barChartPanel = new HorizontalLayout();
        barChartPanel.setSizeFull();
        MatrixLayoutChartFilter.this.addComponent(barChartPanel);
        VerticalLayout spacer = new VerticalLayout();
        spacer.setSizeFull();
//        spacer.setStyleName("lightgraylayout");
        barChartPanel.addComponent(spacer);
        barChartPanel.setExpandRatio(spacer, 10);
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

//        graphLabelsContainer = new VerticalLayout();
//        graphLabelsContainer.setWidth(100,Unit.PERCENTAGE);
//        graphLabelsContainer.setStyleName("blacklayout");
//        graphChartContainerPanel.addComponent(graphLabelsContainer);
//        graphChartContainerPanel.setExpandRatio(graphLabelsContainer, 10);
    }

    public void updateChartData(List<Double> data, String[] labels) {
//        barChartContainerPanel.setContent(initBarChartFilter(data, labels));
//        TreeSet<Double> ts = new TreeSet<>(data);
//        HorizontalLayout graphLayout = initGraph(ts.last(), labels.length);
//        graphChartContainerPanel.setContent(graphLayout);
    }
    private final Map<String, Set<String>> columns = new LinkedHashMap<>();
    private Map<String, Integer> rows = new LinkedHashMap<>();
    private final Set<String> keySorter = new TreeSet<>();

    public void updateChartData(Map<String, Set<String>> data) {
        calculateMatrix(data);
        List<Double> barChartData = new ArrayList<>();
        for (Set<String> set : columns.values()) {
            barChartData.add((double) set.size());
        }
        barChartContainerPanel.setContent(initBarChartFilter(barChartData, columns.keySet().toArray(new String[columns.size()])));

        TreeSet<Double> ts = new TreeSet<>(barChartData);
        HorizontalLayout graphLayout = initGraph(ts.last(), columns, rows);
        graphChartContainerPanel.setContent(graphLayout);
    }

    private void calculateMatrix(Map<String, Set<String>> data) {
        //calculate matrix
        columns.clear();
        rows.clear();
        for (String str : data.keySet()) {
            System.out.println("at data " + str + "    " + data.get(str));
        }

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

//        columns.putAll((rows));
//        loop(rows);
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

    private ChartJs initBarChartFilter(List<Double> data, String[] labels) {
        BarChartConfig barConfig = new BarChartConfig();
        barConfig.
                data().labels(labels)
                .addDataset(
                        new BarDataset().backgroundColor("gray").borderColor("#ffffff").label("Dataset 1").yAxisID("y-axis-1"))
                .and();
        barConfig.
                options().maintainAspectRatio(false)
                .responsive(true)
                .hover()
                .mode(InteractionMode.INDEX)
                .intersect(true)
                .animationDuration(400)
                .and()
                .title()
                .display(true)
                .text("Chart.js Bar Chart - Multi Axis")
                .and()
                .scales()
                .add(Axis.Y, new LinearScale().display(true).position(Position.LEFT).id("y-axis-1").scaleLabel().display(true).labelString("#Proteins").and().ticks().beginAtZero(Boolean.TRUE).and())
                .add(Axis.X, new CategoryScale().display(false).categoryPercentage(1.05))
                .and()
                .done();

        for (Dataset<?, ?> ds : barConfig.data().getDatasets()) {
            BarDataset lds = (BarDataset) ds;
            lds.dataAsList(data);
            lds.fill(false);

        }

        ChartJs chart = new ChartJs(barConfig);

        chart.setJsLoggingEnabled(true);

        chart.addClickListener((int datasetIndex, int dataIndex) -> {
            System.out.println("at dataset " + datasetIndex + "  ,  data " + dataIndex);
        });
        chart.setWidth(100, Unit.PERCENTAGE);
        chart.setHeight(100, Unit.PERCENTAGE);
        return chart;
    }

    private HorizontalLayout initGraph(double protNumber, Map<String, Set<String>> columns, Map<String, Integer> rows) {

        HorizontalLayout graphLabelsContainer = new HorizontalLayout();
        graphLabelsContainer.setSizeFull();

        VerticalLayout labelsContainer = new VerticalLayout();
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
        GridLayout nodeContainer = new GridLayout(columns.size(), rowNum);
        nodeContainer.setSizeFull();
        wrapper.addComponent(nodeContainer);
        int max = 0;
        for (int i : rows.values()) {
            max += i;
        }
        int x = 0;

        for (String rowKey : rows.keySet()) {
            SparkLine sl = new SparkLine(rowKey, rows.get(rowKey), 0, max);
            sl.setWidth(100, Unit.PERCENTAGE);
            sl.setHeight(50, Unit.PERCENTAGE);
            labelsContainer.addComponent(sl);
            labelsContainer.setComponentAlignment(sl, Alignment.MIDDLE_CENTER);
            int y = 0;
            for (String columnKey : columns.keySet()) {
                SelectableNode node = new SelectableNode(rowKey, columns.get(columnKey).isEmpty());
                nodeContainer.addComponent(node, y, x);
                nodeContainer.setComponentAlignment(node, Alignment.MIDDLE_CENTER);
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

}
