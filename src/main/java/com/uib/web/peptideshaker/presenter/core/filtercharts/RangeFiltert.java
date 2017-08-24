package com.uib.web.peptideshaker.presenter.core.filtercharts;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.ScatterChartConfig;
import com.byteowls.vaadin.chartjs.data.Data;
import com.byteowls.vaadin.chartjs.data.LineDataset;
import com.byteowls.vaadin.chartjs.data.ScatterDataset;
import com.byteowls.vaadin.chartjs.options.InteractionMode;
import com.byteowls.vaadin.chartjs.options.Position;
import com.byteowls.vaadin.chartjs.options.scale.Axis;
import com.byteowls.vaadin.chartjs.options.scale.LinearScale;
import com.uib.web.peptideshaker.presenter.core.graph.GraphComponent;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.DragAndDropWrapper;
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
public class RangeFiltert extends AbsoluteLayout {

    private ChartJs chart;
    private final ChartJs.DataPointClickListener clickListener;
    private String notAvailableColor = "#d3d3d3";
    private Map<String, String> colorsMap;
    private final ScatterChartConfig config;
    private final VerticalLayout container;

    public RangeFiltert(String chartTitle, double lower, double upper) {
        RangeFiltert.this.setWidth(100, Unit.PERCENTAGE);
        RangeFiltert.this.setHeight(100, Unit.PIXELS);
        container = new VerticalLayout();
        container.setSizeFull();
        RangeFiltert.this.addComponent(container);
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

        config = new ScatterChartConfig();
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
                .and().maintainAspectRatio(false)
                //                .animation()
                //                .and()
                //                .elements().line().fill(Line.FillMode.BOTTOM)
                //                .and()
                //                .and()
                .legend().display(false)
                //                .position(Position.BOTTOM)
                .and()
                //                .responsive(true)
                .hover()
                .mode(InteractionMode.INDEX)
                .intersect(false)
                .and()
                //                .title()
                //                    .display(true)
                //                    .text("Chart.js Scatter Chart - Multi Axis")
                //                    .and()
                .scales()
                .add(Axis.X, new LinearScale().position(Position.BOTTOM).gridLines().display(true).drawOnChartArea(false).and())
                .add(Axis.Y, new LinearScale().position(Position.LEFT).gridLines().display(false).drawTicks(false).and().ticks().display(false).and())
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
                RangeFiltert.this.removeComponent(chart);
                ((ScatterDataset) config.data().getDatasetAtIndex(datasetIndex)).pointRadius(pointsSize).pointBackgroundColor(pointColors).pointHoverRadius(pointsSize);
                TestUpdates = !TestUpdates;
//                chart.configure(config);
//                chart.refreshData();
                chart = new ChartJs(config);

                chart.setWidth(100, Unit.PERCENTAGE);
                chart.setHeight(100, Unit.PERCENTAGE);
                RangeFiltert.this.addComponent(chart);
                chart.addClickListener(this);

            }
        };
        
        
        
        mainContainer= new AbsoluteLayout();
        mainContainer.setSizeFull();
        
        
        // Wrap the layout to allow handling drops
        DragAndDropWrapper layoutWrapper
                = new DragAndDropWrapper(mainContainer);
        layoutWrapper.addStyleName("subframe");
        RangeFiltert.this.addComponent(layoutWrapper);
        layoutWrapper.setSizeFull();
// Handle moving components within the AbsoluteLayout

        dropHandler = new DropHandler() {
            @Override
            public AcceptCriterion getAcceptCriterion() {

                return AcceptAll.get();
            }

            @Override
            public void drop(DragAndDropEvent event) {

                Component component = event.getTransferable().getSourceComponent();
                if (component instanceof RangeFiltert.WrappedComponent) {
//                    GraphComponent.WrappedComponent node = (GraphComponent.WrappedComponent) component;
//                    DragAndDropWrapper.WrapperTransferable t = (DragAndDropWrapper.WrapperTransferable) event.getTransferable();
//                    DragAndDropWrapper.WrapperTargetDetails details = (DragAndDropWrapper.WrapperTargetDetails) event.getTargetDetails();
//                    // Calculate the drag coordinate difference
//                    int xChange = details.getMouseEvent().getClientX() - t.getMouseDownEvent().getClientX();
//                    int yChange = details.getMouseEvent().getClientY() - t.getMouseDownEvent().getClientY();
//                    // Move the component in the absolute layout
//                    ComponentPosition pos = mainContainer.getPosition(t.getSourceComponent());
//                    pos.setLeftValue(pos.getLeftValue() + xChange);
//                    pos.setTopValue(pos.getTopValue() + yChange);
//
//                    double x = pos.getLeftValue();
//                    double y = pos.getTopValue();
//                    nodesMap.get(node.getData() + "").setX(x);
//                    nodesMap.get(node.getData() + "").setY(y);
//                    drawEdges();
                }

            }
        };
        layoutWrapper.setDropHandler(dropHandler);
        
        VerticalLayout lowerLine = new VerticalLayout();
        lowerLine.setWidth(5,Unit.PIXELS);
        lowerLine.setHeight(50,Unit.PERCENTAGE);
        lowerLine.setStyleName("blacklayout");
        
         final WrappedComponent wrapper = new WrappedComponent(lowerLine,
                    dropHandler);
            wrapper.setSizeUndefined();
            wrapper.setData("lowerLine");
            wrapper.setDescription("Lower limit");
            mainContainer.addComponent(wrapper, "left: " + 0 + "px; top: " + 30 + "px");
        

//        chart.addClickListener(clickListener);
//        DonutChartFilter.this.addComponent(chart);
    }
    private final AbsoluteLayout mainContainer;
    private final DropHandler dropHandler;
    private Integer[] pointsSize;
    private String[] pointColors;

    public void updateChartData(Map<Double, Double> dataMap) {
        Data<ScatterChartConfig> dataConfig = config.data();//.labels(dataMap.keySet().toArray(new String[dataMap.size()]));
//        for (int x = 0; x < dataMap.size();x++) {
        ScatterDataset ds = new ScatterDataset().label("Dataset " + 1).borderColor("#d3d3d3")
                .backgroundColor("whitesmoke");
        pointsSize = new Integer[dataMap.size()];
        pointColors = new String[dataMap.size()];
        int x = 0;
        for (double d : dataMap.keySet()) {
            ds.addData(d, dataMap.get(d));
            pointsSize[x] = 3;
            pointColors[x] = "lightgray";
            x++;
        }
        ds.pointRadius(0);
        ds.pointBackgroundColor("lightgray");
        ds.pointHoverRadius(0);
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
        RangeFiltert.this.addComponent(chart);
        chart.addClickListener(clickListener);

    }
    private boolean TestUpdates = false;
    
     /**
     * This class is a wrapper for the dropped component that is used in the
     * Drag-Drop layout.
     *
     * @author Yehia Farag
     */
    class WrappedComponent extends DragAndDropWrapper {

        /**
         * The layout drop handler.
         */
        private final DropHandler dropHandler;

        /**
         * Constructor to initialize the main attributes.
         *
         * @param content the dropped component (the label layout)
         * @param dropHandler The layout drop handler.
         */
        public WrappedComponent(final Component content, final DropHandler dropHandler) {
            super(content);
            this.dropHandler = dropHandler;
            WrappedComponent.this.setDragStartMode(DragAndDropWrapper.DragStartMode.WRAPPER);
        }

        @Override
        public DropHandler getDropHandler() {
            return dropHandler;
        }

    }

}
