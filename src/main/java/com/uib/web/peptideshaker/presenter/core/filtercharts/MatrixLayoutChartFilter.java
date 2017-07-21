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
import com.uib.web.peptideshaker.presenter.core.filtercharts.components.SelectableNode;
import com.uib.web.peptideshaker.presenter.core.form.Horizontal2Label;
import com.uib.web.peptideshaker.presenter.core.form.SparkLine;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.List;
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
        barChartContainerPanel.setContent(initBarChartFilter(data, labels));
        TreeSet<Double> ts = new TreeSet<>(data);
        HorizontalLayout graphLayout = initGraph(ts.last(), labels.length);
        graphChartContainerPanel.setContent(graphLayout);
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
                .add(Axis.Y, new LinearScale().display(true).position(Position.LEFT).id("y-axis-1").scaleLabel().display(true).labelString("#Proteins").and())
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

    private HorizontalLayout initGraph(double protNumber, int labelNumbers) {
        
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
        graphLabelsContainer.setExpandRatio(graphContainer,90);
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
        int rowNum = 6; 
        
        graphLabelsContainer.setHeight(rowNum * 40, Unit.PIXELS);
        wrapper.setHeight(100, Unit.PERCENTAGE);
        GridLayout nodeContainer = new GridLayout(labelNumbers, rowNum);
        nodeContainer.setSizeFull();
        wrapper.addComponent(nodeContainer);
        for (int x = 0; x < nodeContainer.getRows(); x++) {
           
            SparkLine sl = new SparkLine((x+1)*10, 0, 60);
            sl.setWidth(100,Unit.PERCENTAGE);
            sl.setHeight(50,Unit.PERCENTAGE);
            labelsContainer.addComponent(sl);
             labelsContainer.setComponentAlignment(sl,Alignment.MIDDLE_CENTER);
            for (int i = 0; i < nodeContainer.getColumns(); i++) {
                SelectableNode node = new SelectableNode((i == 0 || i % 2 == 0), (x != 0), (x != nodeContainer.getRows() - 1));
                nodeContainer.addComponent(node, i, x);
                nodeContainer.setComponentAlignment(node, Alignment.MIDDLE_CENTER);
            }

        }
        return graphLabelsContainer;
    }

}
