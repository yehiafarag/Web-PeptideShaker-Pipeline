package com.uib.web.peptideshaker.presenter.core.piecharts;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.DonutChartConfig;
import com.byteowls.vaadin.chartjs.data.Dataset;
import com.byteowls.vaadin.chartjs.data.PieDataset;
import com.byteowls.vaadin.chartjs.options.Position;
import com.vaadin.ui.VerticalLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents pie-chart filter component
 *
 * @author YEhia Farag
 */
public class PieChartFilter extends VerticalLayout {

    public PieChartFilter() {
//        this.setSizeFull();
        DonutChartConfig config = new DonutChartConfig();
        config
                .data()
                .labels("Red", "Green", "Yellow", "Grey", "Dark Grey")
                .addDataset(new PieDataset().label("Dataset 1"))
                .addDataset(new PieDataset().label("Dataset 2"))
                .addDataset(new PieDataset().label("Dataset 3"))
                .addDataset(new PieDataset().label("Dataset 4"))
                .addDataset(new PieDataset().label("Dataset 5"))
                .addDataset(new PieDataset().label("Dataset 6"))
                .and();

        config.
                options()
                .responsive(true)
                .title()
                .display(true)
                .text("Chart.js Doughnut Chart").fontSize(12).fontColor("#F7464A")
                .and()
                .animation()
                .animateScale(true)
                .animateRotate(true)
                .and().legend()
                .position(Position.BOTTOM)
                .and()
                .done();

        String[] colors = new String[]{"#F7464A", "#46BFBD", "#FDB45C", "#949FB1", "#4D5360"};

        List<String> labels = config.data().getLabels();
        for (Dataset<?, ?> ds : config.data().getDatasets()) {
            PieDataset lds = (PieDataset) ds;
            lds.backgroundColor(colors);

            List<Double> data = new ArrayList<>();
            for (int i = 0; i < labels.size(); i++) {
                data.add((double) (Math.round(Math.random() * 100)));
            }
            lds.dataAsList(data);
        }

        ChartJs chart = new ChartJs(config);
        chart.setWidth(100, Unit.PERCENTAGE);
        chart.setHeight(100, Unit.PERCENTAGE);

        chart.setJsLoggingEnabled(true);
        chart.addClickListener((int datasetIndex, int dataIndex) -> {
            System.out.println("at data index " + dataIndex + "    dataset index " + datasetIndex);

        });
        this.addComponent(chart);

    }

}
