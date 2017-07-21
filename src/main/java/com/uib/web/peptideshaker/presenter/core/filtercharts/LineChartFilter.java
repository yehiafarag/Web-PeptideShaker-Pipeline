package com.uib.web.peptideshaker.presenter.core.filtercharts;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.LineChartConfig;
import com.byteowls.vaadin.chartjs.data.Data;
import com.byteowls.vaadin.chartjs.data.LineDataset;
import com.byteowls.vaadin.chartjs.options.elements.Line;
import com.byteowls.vaadin.chartjs.options.scale.Axis;
import com.byteowls.vaadin.chartjs.options.scale.BaseScale;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents pie-chart filter component
 *
 * @author YEhia Farag
 */
public class LineChartFilter extends AbsoluteLayout {

    private ChartJs chart;
    private final ChartJs.DataPointClickListener clickListener;
    private String notAvailableColor = "#d3d3d3";
    private Map<String, String> colorsMap;
    private final LineChartConfig config;

    public LineChartFilter(String chartTitle) {
        LineChartFilter.this.setSizeFull();
        VerticalLayout container = new VerticalLayout();
        container.setSizeFull();
        LineChartFilter.this.addComponent(container);
        colorsMap = new HashMap<>();
        colorsMap.put("#fcb6b7", "#930608");
        colorsMap.put("#46BFBD", "#287171");
        colorsMap.put("#c6ebeb", "#975402");
        colorsMap.put("#c3cbd5", "#4b5568");
        colorsMap.put("#99ccff", "#495869");
        colorsMap.put("#ccccff", "#0000b3");
        colorsMap.put("#ffffcc", "#666600");
        colorsMap.put("#ffcc66", "#b37700");
        colorsMap.put("#66ff33", "#1a6600");
        colorsMap.put("#d9e6f2", "#274e72");
        ArrayList<String> colorList = new ArrayList<>(colorsMap.values());

        
      
        config = new LineChartConfig();
//        config
//                .data()
//                .labels("Red", "Green", "Yellow", "Grey", "Dark Grey")
//                .addDataset(new PieDataset().label("Dataset 1"))
//                .addDataset(new PieDataset().label("Dataset 2"))
//                .addDataset(new PieDataset().label("Dataset 3"))
////                .addDataset(new PieDataset().label("Dataset 4"))
////                .addDataset(new PieDataset().label("Dataset 5"))
////                .addDataset(new PieDataset().label("Dataset 6"))
//                .and();

        config.
                options().animation().duration(0).and()
                .responsive(true)
                .title()
                .display(true)
                .text(chartTitle).fontStyle("normal").fontSize(12).fontFamily("Open Sans")
                .and()
                //                .animation()
                //                .and()
                .elements().line().fill(Line.FillMode.BOTTOM)
                .and()
                .and()
                .legend().display(false)
                //                .position(Position.BOTTOM)
                .and()
                .done();

        clickListener = new ChartJs.DataPointClickListener() {
            @Override
            public void onDataPointClick(int datasetIndex, int dataIndex) {

//                ((PieDataset)config.data().getDatasets().get(dataIndex)).backgroundColor(colorList.get(datasetIndex));
                if (pointsSize[dataIndex] == 3) {
                    pointsSize[dataIndex] = 6;
                } else {
                    pointsSize[dataIndex] = 3;
                }
                if (pointColors[dataIndex].equals("lightgray")) {
                    pointColors[dataIndex] = "gray";
                } else {
                    pointColors[dataIndex] = "lightgray";
                }
                LineChartFilter.this.removeComponent(chart);
                ((LineDataset) config.data().getDatasetAtIndex(datasetIndex)).pointRadius(pointsSize).pointBackgroundColor(pointColors).pointHoverRadius(pointsSize);
                TestUpdates = !TestUpdates;
//                chart.configure(config);
//                chart.refreshData();
                chart = new ChartJs(config);

                chart.setWidth(100, Unit.PERCENTAGE);
                chart.setHeight(100, Unit.PERCENTAGE);
                LineChartFilter.this.addComponent(chart);
                chart.addClickListener(this);

            }
        };

//        chart.addClickListener(clickListener);
//        DonutChartFilter.this.addComponent(chart);
    }
    private Integer[] pointsSize;
    private String[] pointColors;

    public void updateChartData(Map<String, Double> dataMap) {
        Data<LineChartConfig> dataConfig = config.data().labels(dataMap.keySet().toArray(new String[dataMap.size()]));
//        for (int x = 0; x < dataMap.size();x++) {
        LineDataset ds = new LineDataset().label("Dataset " + 1).borderColor("#d3d3d3")
                .backgroundColor("whitesmoke");
        pointsSize = new Integer[dataMap.size()];
        pointColors = new String[dataMap.size()];
        int x = 0;
        for (double d : dataMap.values()) {
            ds.addData(d);
            pointsSize[x] = 3;
            pointColors[x] = "lightgray";
            x++;
        }
        ds.pointRadius(pointsSize);
        ds.pointBackgroundColor(pointColors);
        ds.pointHoverRadius(pointsSize);
        dataConfig.addDataset(ds);
//            x += 10;
//        }
        dataConfig.and();

        String[] colors = new String[]{"#F7464A", "#46BFBD", "#FDB45C", "#949FB1", "#4D5360"};

        List<String> labels = dataConfig.getLabels();
        int counter = 0;
//        for (Dataset<?, ?> ds : dataConfig.getDatasets()) {
//            PieDataset lds = (PieDataset) ds;
////            lds.borderColor(colorList.get(counter++));
//            lds.backgroundColor(colorsMap.values().toArray(new String[colorsMap.size()]));
//
//            lds.randomBackgroundColors(true);
//            List<Double> data = new ArrayList<>();
//            for (String label : labels) {
//                data.add(dataMap.get(label));
//            }
//            lds.dataAsList(data);
//        }

        chart = new ChartJs(config);
        chart.setWidth(100, Unit.PERCENTAGE);
        chart.setHeight(100, Unit.PERCENTAGE);
        chart.setJsLoggingEnabled(true);
        LineChartFilter.this.addComponent(chart);
        chart.addClickListener(clickListener);

    }
    private boolean TestUpdates = false;

}
