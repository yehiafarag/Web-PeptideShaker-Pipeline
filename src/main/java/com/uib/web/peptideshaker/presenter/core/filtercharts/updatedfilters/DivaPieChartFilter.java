package com.uib.web.peptideshaker.presenter.core.filtercharts.updatedfilters;

import com.ejt.vaadin.sizereporter.ComponentResizeEvent;
import com.ejt.vaadin.sizereporter.SizeReporter;
import com.google.common.collect.Sets;
import com.itextpdf.text.pdf.codec.Base64;

import com.uib.web.peptideshaker.presenter.components.peptideshakerview.components.SelectionManager;
import com.uib.web.peptideshaker.presenter.core.filtercharts.charts.RegistrableFilter;
import com.uib.web.peptideshaker.presenter.core.filtercharts.components.LegendItem;
import com.vaadin.event.LayoutEvents;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;

/**
 * This class represents matrix layout filter
 *
 * @author Yehia Farag
 */
public abstract class DivaPieChartFilter extends HorizontalLayout implements RegistrableFilter {

    /**
     * The width of the chart.
     */
    private int mainWidth;
    /**
     * The height of the chart.
     */
    private int mainHeight;
    private final String title;
    private final String filterId;
    private final Set<Comparable> fullItemsSet;
    private final SelectionManager Selection_Manager;
    private final Set<Comparable> appliedFilter;
    private Label chartTitle;
    private Image mainChartImg;
    private boolean activateFilter = false;
    /**
     * A wite layout that has the label and turn pie-chart into donut chart.
     */
    private VerticalLayout middleDountLayout;
    /**
     * The chart label contain the total number of datasets.
     */
    private Label selectAllLabel;
    /**
     * The highlight selection color (required by JFree chart).
     */
    private final Color selectedColor = Color.decode("#197de1");

    /**
     * The bar-chart JFree chart (required by JFree chart).
     */
    private JFreeChart chart;
    /**
     * The thumb bar-chart rendering information generated by JFree chart and
     * contain all the chart information.
     */
    private final ChartRenderingInfo mainChartRenderingInfo = new ChartRenderingInfo();
    private VerticalLayout rightLayout;
    private int redrawIndex;
    private final List<ComponentResizeEvent> eventList = new ArrayList<>();

    private AbsoluteLayout mainChartContainer;
    private final LayoutEvents.LayoutClickListener dountChartListener;
    private final Set<Comparable> selectedDataSet;
    private Map<String, Set<Comparable>> fullData;
    private Color[] colorsArr;

    public DivaPieChartFilter(String title, String filterId, SelectionManager Selection_Manager) {
        this.mainWidth = -1;
        this.mainHeight = -1;
        this.title = title;
        this.filterId = filterId;
        this.Selection_Manager = Selection_Manager;
        this.selectedDataSet = new LinkedHashSet<>();
        this.Selection_Manager.RegistrFilter(DivaPieChartFilter.this);
        this.fullItemsSet = new LinkedHashSet<>();
        this.appliedFilter = new LinkedHashSet<>();
        this.dountChartListener = (LayoutEvents.LayoutClickEvent event) -> {
            Component clickedComponent = event.getClickedComponent();
            if (clickedComponent instanceof Image) {
                PieSectionEntity ent = (PieSectionEntity) mainChartRenderingInfo.getEntityCollection().getEntity(event.getRelativeX(), event.getRelativeY());
//                int columnIndx = (int) ((VerticalLayout) clickedComponent).getData();
//               
                System.out.println("select pie chart  " + ent.getSectionKey());
                applyFilter(ent.getSectionKey() + "");
            } else {
                applyFilter(null);
                System.out.println("select others " + clickedComponent.getClass());
            }
        };
        this.initlayout();

    }
    Timer timer = new Timer();

    private void initlayout() {
        DivaPieChartFilter.this.setSizeFull();
        DivaPieChartFilter.this.setStyleName("thumbfilterframe");
        DivaPieChartFilter.this.setSpacing(true);

        chartTitle = new Label("<font style='padding-left: 10px;padding-top: 30%;position: absolute !important;'>" + title + "</font>", ContentMode.HTML);
        chartTitle.setStyleName(ValoTheme.LABEL_BOLD);
        chartTitle.setWidth(100, Unit.PERCENTAGE);
        chartTitle.setHeight(100, Unit.PERCENTAGE);
        chartTitle.addStyleName("resizeabletext");
        DivaPieChartFilter.this.addComponent(chartTitle);
        DivaPieChartFilter.this.setComponentAlignment(chartTitle, Alignment.TOP_LEFT);
        DivaPieChartFilter.this.setExpandRatio(chartTitle, 15);

        mainChartContainer = new AbsoluteLayout();

        mainChartContainer.setWidth(100, Unit.PERCENTAGE);
        mainChartContainer.setHeight(100, Unit.PERCENTAGE);
        DivaPieChartFilter.this.addComponent(mainChartContainer);
        DivaPieChartFilter.this.setComponentAlignment(mainChartContainer, Alignment.TOP_LEFT);
        DivaPieChartFilter.this.setExpandRatio(mainChartContainer, 50);
        mainChartContainer.addLayoutClickListener(dountChartListener);

        middleDountLayout = new VerticalLayout();
        middleDountLayout.setSizeFull();
        middleDountLayout.setVisible(false);
        mainChartContainer.addComponent(middleDountLayout, "left:0px; top:0px;");
        selectAllLabel = new Label("<center>1000000</center>", ContentMode.HTML);
        selectAllLabel.addStyleName("middledountchart");
        selectAllLabel.addStyleName(ValoTheme.LABEL_TINY);
        selectAllLabel.addStyleName(ValoTheme.LABEL_SMALL);

        middleDountLayout.addComponent(selectAllLabel);
        middleDountLayout.setComponentAlignment(selectAllLabel, Alignment.MIDDLE_CENTER);

        mainChartImg = new Image();
        mainChartImg.setVisible(false);
        mainChartImg.setStyleName("pointer");
        mainChartImg.setHeight(100, Unit.PERCENTAGE);
        mainChartImg.setWidth(100, Unit.PERCENTAGE);
        mainChartContainer.addComponent(mainChartImg, "left:0px; top:0px;");

        /**
         * ******************right panel*********************
         */
        rightLayout = new VerticalLayout();
        rightLayout.setSizeFull();
        rightLayout.setStyleName("margintop5");
        rightLayout.addStyleName("autooverflow");

        DivaPieChartFilter.this.addComponent(rightLayout);
        DivaPieChartFilter.this.setComponentAlignment(rightLayout, Alignment.TOP_LEFT);
        DivaPieChartFilter.this.setExpandRatio(rightLayout, 35);

        SizeReporter mainSizeReporter = new SizeReporter(mainChartContainer);
        mainSizeReporter.addResizeListener((ComponentResizeEvent event) -> {
            try {
                if (redrawIndex < 4) {
                    redrawIndex++;
                    return;
                }
                mainChartImg.setVisible(false);
                middleDountLayout.setVisible(false);
                eventList.add(event);
                timer.schedule(
                        new java.util.TimerTask() {
                    @Override
                    public void run() {
                        timer.cancel();
                        timer = new Timer();
                        ComponentResizeEvent event = eventList.get(eventList.size() - 1);
                        eventList.clear();
                        mainWidth = event.getWidth();
                        mainHeight = event.getHeight();
                        redrawChart();
                        mainChartImg.setVisible(true);
                        middleDountLayout.setVisible(true);

                    }
                },
                        2000
                );

            } catch (Exception ex) {
                System.out.println("at error " + this.getClass().getName() + "  " + ex);
            }
        });

        initChart();

    }

    public void initializeFilterData(Map<String, Set<Comparable>> fullData, Color[] colorsArr) {
        activateFilter = true;
        this.fullData = fullData;
        selectedDataSet.clear();
        this.colorsArr = colorsArr;
        this.appliedFilter.clear();
        fullItemsSet.clear();
        for (String key : fullData.keySet()) {
            fullItemsSet.addAll(fullData.get(key));
        }
        selectedDataSet.addAll(fullItemsSet);
        selectAllLabel.setValue("<center>" + selectedDataSet.size() + "</center>");
        updateChartDataset();

    }

    private void reDrawLayout() {
        //calc 60% of width
        double w = Math.min(mainWidth, mainHeight);
        w = 60 * w / 100.0;
        selectAllLabel.setWidth((float) w, Unit.PIXELS);
        selectAllLabel.setHeight((float) w, Unit.PIXELS);
        updateLegndItems();
    }

    private void unselectAll() {
        PiePlot plot = ((PiePlot) chart.getPlot());
        for (Comparable sliceKey : fullData.keySet()) {
            plot.setSectionOutlinePaint(sliceKey, null);
            plot.setSectionPaint(sliceKey, colorsArr[plot.getDataset().getIndex(sliceKey)]);
        }

    }

    @Override
    public void redrawChart() {
        if (mainWidth < 1 || mainHeight < 1 || !activateFilter) {
            return;
        }

        mainChartImg.setSource(new ExternalResource(saveToFile(chart, mainChartRenderingInfo, mainWidth, mainHeight)));
        reDrawLayout();
    }

    @Override
    public String getFilterId() {
        return filterId;
    }

    @Override
    public void updateFilterSelection(Set<Comparable> selectedItems, Set<Comparable> selectedCategories,boolean topFilter, boolean singleProteinsFilter, boolean selfAction) {
//        appliedFilter.clear();
//        appliedFilter.addAll(selectedCategories);
        if (selfAction) {
            
            
        } else {
//            barChartValues.clear();
//            int coulmnIndex = 0;
//            for (String key : columns.keySet()) {
////                fullItemsSet.addAll(columns.get(key));
//                double d = (double) Sets.intersection(columns.get(key), selectedItems).size();
//                if (d > 0) {
//                    barChartValues.put(coulmnIndex, d);
//                }
//                coulmnIndex++;
//            }
//            updateChartDataset(barChartValues);
//            redrawChart();
        }
        
        
//           //case 1 self selection (select coulmn or unselect all)
//        if (!selfAction) {
//            if (singleProteinsFilter && !selfAction && !selectedCategories.isEmpty()) {
//                 barChartValues.clear();
//                 int coulmnIndex = 0;
//                for (String key : columns.keySet()) {
//                    double d = (double) columns.get(key).size();
//                    barChartValues.put(coulmnIndex, d);
//                    coulmnIndex++;
//                }
//                updateChartDataset(barChartValues);
//                redrawChart();
//
//            } else {
//                Map<Integer, Double> tbarChartValues = new LinkedHashMap<>();
//                int coulmnIndex = 0;
//                for (String key : columns.keySet()) {
//                    double d = (double) Sets.intersection(columns.get(key), selectedItems).size();
//                    if (d > 0) {
//                        tbarChartValues.put(coulmnIndex, d);
//                    }
//                    coulmnIndex++;
//                }
//                barChartValues.clear();
//                barChartValues.putAll(tbarChartValues);
//                updateChartDataset(barChartValues);
//                redrawChart();//
//            }
//        }
        selectSlice(selectedCategories);

        //case 2 unselectall with re-drawing the chart
//        else if (selectedDataSet.isEmpty() && selectedCategories.isEmpty() && (selectedItems.size() == fullItemsSet.size() && fullItemsSet.containsAll(selectedItems))) {
//            unselectAll();
//        }
        //case 3 re-draw the chart 
//        subset the matrix 
        //new case 4 add selection selection
//                select column 
        selectAllLabel.setValue("<center>" + selectedItems.size() + "</center>");
    }

    /**
     * Select slice action.
     *
     * @param sliceKey selected slice key.
     */
    private void selectSlice(Set<Comparable> sliceKeys) {
        unselectAll();
        if (!sliceKeys.isEmpty()) {
            PiePlot plot = ((PiePlot) chart.getPlot());
            for (Comparable sliceKey : sliceKeys) {
                plot.setSectionOutlinePaint(sliceKey, selectedColor);
                plot.setSectionPaint(sliceKey, colorsArr[plot.getDataset().getIndex(sliceKey)].darker());
            }
        }
    }

    public void applyFilter(String pieSlice) {
        if (appliedFilter.contains(pieSlice)) {
            appliedFilter.remove(pieSlice);
        } else {
            appliedFilter.add(pieSlice);
        }
        
        if (appliedFilter.size() == rightLayout.getComponentCount()) {
            appliedFilter.clear();
        }
        Selection_Manager.setSelection("protein_selection", new LinkedHashSet<>(appliedFilter), null, filterId);
    }

    /**
     * Convert JFree chart into image and encode it as base64 string to be used
     * as image link.
     *
     * @param chart JFree chart instance
     * @param width Image width
     * @param height Image height.
     */
    private String saveToFile(final JFreeChart chart, ChartRenderingInfo chartRenderingInfo, int width, int height) {

        byte imageData[];
        try {
            chart.getLegend().setVisible(false);
            imageData = ChartUtilities.encodeAsPNG(chart.createBufferedImage(width, height, chartRenderingInfo));
            String base64 = Base64.encodeBytes(imageData);
            base64 = "data:image/png;base64," + base64;
            return base64;
        } catch (IOException e) {
            System.err.println("at error " + e.getMessage());
        }
        return "";
    }

    /**
     * Initialize the main JFree chart component.
     */
    private void initChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();

        PiePlot plot = new PiePlot(dataset);
        plot.setNoDataMessage("No data available");
        plot.setCircular(true);
        plot.setLabelGap(0);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setShadowPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setBaseSectionOutlinePaint(Color.WHITE);
        plot.setSectionOutlinesVisible(true);
        plot.setIgnoreZeroValues(true);
        chart = new JFreeChart(plot);
        plot.setInteriorGap(0.0);
        plot.setLabelGenerator(null);
        chart.getLegend().setItemFont(new Font("\"Open Sans\", sans-serif", Font.PLAIN, 11));
        plot.setBaseSectionOutlineStroke(new BasicStroke(2));
        plot.setInteriorGap(0);
        chart.getLegend().setPosition(RectangleEdge.RIGHT);
        chart.setPadding(new RectangleInsets(0, 0, 0, 0));
        chart.setBorderVisible(false);
        plot.setOutlinePaint(null);
        plot.setOutlineVisible(false);
        plot.setShadowYOffset(0);
        plot.setShadowXOffset(0);

        chart.setBorderPaint(null);
        chart.setBackgroundPaint(null);
        chart.getLegend().setFrame(BlockBorder.NONE);

    }

    private void updateChartDataset() {
        // column keys...    
        int counter = 0;
        // update the dataset...
        rightLayout.removeAllComponents();
        DefaultPieDataset dataset = (DefaultPieDataset) ((PiePlot) chart.getPlot()).getDataset();
        dataset.clear();

        for (String key : fullData.keySet()) {

            dataset.setValue(key, scaleValues(fullData.get(key).size(), 100, 20));
            ((PiePlot) chart.getPlot()).setSectionPaint(key, this.colorsArr[counter]);
            if (!fullData.get(key).isEmpty()) {
                LegendItem item = new LegendItem(key + "", this.colorsArr[counter]);
                rightLayout.addComponent(item);
            }

            counter++;
        }
        redrawChart();

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
        double logValue = (Math.log(linearValue) / Math.log(2));
        logValue = ((max / logMax) * logValue) + lowerLimit;
        return logValue;
    }

    private void updateLegndItems() {

    }

}
