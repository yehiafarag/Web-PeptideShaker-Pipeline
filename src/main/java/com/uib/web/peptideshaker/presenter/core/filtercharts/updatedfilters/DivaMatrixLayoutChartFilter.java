package com.uib.web.peptideshaker.presenter.core.filtercharts.updatedfilters;

import com.ejt.vaadin.sizereporter.ComponentResizeEvent;
import com.ejt.vaadin.sizereporter.SizeReporter;
import com.google.common.collect.Sets;
import com.itextpdf.text.pdf.codec.Base64;

import com.uib.web.peptideshaker.model.core.ModificationMatrix;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.SelectionManager;
import com.uib.web.peptideshaker.presenter.core.FilterButton;
import com.uib.web.peptideshaker.presenter.core.filtercharts.charts.RegistrableFilter;
import com.uib.web.peptideshaker.presenter.core.filtercharts.components.SelectableNode;
import com.uib.web.peptideshaker.presenter.core.form.SparkLine;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleInsets;

/**
 * This class represents matrix layout filter
 *
 * @author Yehia Farag
 */
public abstract class DivaMatrixLayoutChartFilter extends VerticalLayout implements RegistrableFilter {

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
    private Map<String, Set<Comparable>> columns;
    private final Map<String, Integer> rows = new LinkedHashMap<>();
    private final Set<Comparable> fullItemsSet;
    private final SelectionManager Selection_Manager;
    private final Map<Integer, Double> barChartValues;
    private Label chartTitle;
    private FilterButton removeFilterIcon;
    private Image mainChartImg;
    private VerticalLayout leftBottomCorner;
    private AbsoluteLayout bottomLayoutContainer;
    private final Map<Comparable, List<SelectableNode>> nodesTable;
    private AbsoluteLayout topLayoutPanel;
    private AbsoluteLayout bottomLayoutPanel;
    private boolean allowDrawing = false;
    private int index = 0;

    /**
     * The bar-chart JFree chart (required by JFree chart).
     */
    private JFreeChart chart;
    /**
     * The bar-chart rendering information generated by JFree chart and contain
     * all the chart information.
     */
    private final ChartRenderingInfo mainChartRenderingInfo = new ChartRenderingInfo();
    private AbsoluteLayout mainChartContainer;
    private final LayoutEvents.LayoutClickListener barListener;
    private AbsoluteLayout chartBarsContainer;
    private final Map<Comparable, VerticalLayout> chartBarsList;
//    private final Set<Comparable> selectedDataSet;
    private AbsoluteLayout columnsContainer;
    private final Map<String, SparkLine> rowLabelsMap;
    private Map<String, Color> dataColors;

    public DivaMatrixLayoutChartFilter(String title, String filterId, SelectionManager Selection_Manager) {
        this.mainWidth = 300;
        this.mainHeight = 300;
        this.title = title;
        this.filterId = filterId;
        this.Selection_Manager = Selection_Manager;
        this.rowLabelsMap = new LinkedHashMap<>();
//        this.selectedDataSet = new LinkedHashSet<>();
        this.Selection_Manager.RegistrDatasetsFilter(DivaMatrixLayoutChartFilter.this);
        this.nodesTable = new LinkedHashMap<>();
        this.chartBarsList = new HashMap<>();
        this.barChartValues = new LinkedHashMap<>();
        this.fullItemsSet = new LinkedHashSet<>();
        this.barListener = (LayoutEvents.LayoutClickEvent event) -> {
            Component clickedComponent = event.getClickedComponent();
            if (clickedComponent == null) {
                return;
            }
            if (clickedComponent instanceof VerticalLayout && !clickedComponent.getStyleName().contains("selectedbarlayout")) {
                int columnIndx = (int) ((VerticalLayout) clickedComponent).getData();
                applyFilter(columnIndx);
            } else {
                applyFilter(-1);
            }
        };
        this.initlayout();

    }

    private void initlayout() {
        DivaMatrixLayoutChartFilter.this.setSizeFull();
        DivaMatrixLayoutChartFilter.this.setStyleName("thumbfilterframe");
        DivaMatrixLayoutChartFilter.this.setSpacing(true);
        VerticalLayout topLeftCornerLayout = new VerticalLayout();
        topLeftCornerLayout.setWidth(28, Unit.PERCENTAGE);
        topLeftCornerLayout.setHeight(100, Unit.PERCENTAGE);
        topLeftCornerLayout.setMargin(new MarginInfo(false, false, false, false));
        topLeftCornerLayout.addStyleName("toppanel");
        DivaMatrixLayoutChartFilter.this.addComponent(topLeftCornerLayout);
        DivaMatrixLayoutChartFilter.this.setExpandRatio(topLeftCornerLayout, 5);
        topLayoutPanel = new AbsoluteLayout();
        topLayoutPanel.setSizeFull();

        DivaMatrixLayoutChartFilter.this.addComponent(topLayoutPanel);
        DivaMatrixLayoutChartFilter.this.setComponentAlignment(topLayoutPanel, Alignment.TOP_LEFT);
        DivaMatrixLayoutChartFilter.this.setExpandRatio(topLayoutPanel, 30);

        chartTitle = new Label("<font style='padding-top: 10px;position: absolute;'>" + title + "</font>", ContentMode.HTML);
        chartTitle.setStyleName(ValoTheme.LABEL_BOLD);
        chartTitle.setWidth(100, Unit.PERCENTAGE);
        chartTitle.setHeight(300, Unit.PIXELS);
        chartTitle.addStyleName("resizeabletext");
        topLeftCornerLayout.addComponent(chartTitle);
        topLeftCornerLayout.setComponentAlignment(chartTitle, Alignment.TOP_LEFT);

        mainChartContainer = new AbsoluteLayout();
        mainChartContainer.setWidth(100, Unit.PERCENTAGE);
        mainChartContainer.setHeight(100, Unit.PERCENTAGE);
        topLayoutPanel.addComponent(mainChartContainer, "left:28%;top:0px;");

        mainChartImg = new Image();
        mainChartImg.setHeight(100, Unit.PERCENTAGE);
        mainChartImg.setWidth(100, Unit.PERCENTAGE);

        mainChartContainer.addComponent(mainChartImg);
        mainChartImg.addStyleName("hide");
        chartBarsContainer = new AbsoluteLayout();
        chartBarsContainer.setHeight(100, Unit.PERCENTAGE);
        chartBarsContainer.setWidth(100, Unit.PERCENTAGE);
        mainChartContainer.addComponent(chartBarsContainer);
        chartBarsContainer.addStyleName("pointer");
        chartBarsContainer.addLayoutClickListener(barListener);

        SizeReporter mainSizeReporter = new SizeReporter(mainChartContainer);
        mainSizeReporter.addResizeListener((ComponentResizeEvent event) -> {

//           
            int tChartWidth = event.getWidth();
            int tChartHeight = event.getHeight();
            if (tChartWidth <= 0 || tChartHeight <= 0) {
                return;
            }
            if ((tChartWidth == mainWidth || Math.abs(tChartWidth - mainWidth) < 10) && (mainHeight == tChartHeight || Math.abs(tChartHeight - mainHeight) < 10)) {
                return;
            }

            mainWidth = tChartWidth;
            mainHeight = tChartHeight;
            redrawChart();
        });
        initChart();
        /**
         * ******************lower panel*********************
         */
        bottomLayoutPanel = new AbsoluteLayout();
        bottomLayoutPanel.setSizeFull();
        DivaMatrixLayoutChartFilter.this.addComponent(bottomLayoutPanel);
        DivaMatrixLayoutChartFilter.this.setComponentAlignment(bottomLayoutPanel, Alignment.TOP_LEFT);
        DivaMatrixLayoutChartFilter.this.setExpandRatio(bottomLayoutPanel, 65);
        bottomLayoutPanel.addStyleName("ignorscrollspace");
        bottomLayoutContainer = new AbsoluteLayout();
        bottomLayoutContainer.setWidth(100, Unit.PERCENTAGE);
        bottomLayoutPanel.addComponent(bottomLayoutContainer);
        leftBottomCorner = new VerticalLayout();
        leftBottomCorner.setStyleName("leftbottommatrexcorner");
        leftBottomCorner.setWidth(28, Unit.PERCENTAGE);
        leftBottomCorner.setHeight(100, Unit.PERCENTAGE);
        leftBottomCorner.setSpacing(false);
        columnsContainer = new AbsoluteLayout();
        columnsContainer.setHeight(100, Unit.PERCENTAGE);
        columnsContainer.setWidth(100, Unit.PERCENTAGE);
        columnsContainer.addStyleName("pointer");

        removeFilterIcon = new FilterButton() {
            @Override
            public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                System.out.println("at clicked reset");
                applyFilter(-1);

            }
        };
        removeFilterIcon.setWidth(25, Unit.PIXELS);
        removeFilterIcon.setHeight(25, Unit.PIXELS);
        removeFilterIcon.setVisible(false);
//        removeFilterIcon.setActiveBtn(true);
        removeFilterIcon.addStyleName("btninframe");

//        topLeftContainer.addComponent(removeFilterIcon);
//        topLeftContainer.setComponentAlignment(removeFilterIcon, Alignment.TOP_CENTER);
        DivaMatrixLayoutChartFilter.this.addComponent(removeFilterIcon);
        DivaMatrixLayoutChartFilter.this.setComponentAlignment(removeFilterIcon, Alignment.TOP_RIGHT);
        DivaMatrixLayoutChartFilter.this.setExpandRatio(removeFilterIcon, 0);
    }

    public void initializeFilterData(ModificationMatrix modificationMatrix, Map<String, Color> dataColors, Set<Object> selectedCategories, int totalNumber) {
        index = 0;
        allowDrawing = false;
        rows.clear();
        rowLabelsMap.clear();
        this.dataColors = dataColors;
        rows.putAll(modificationMatrix.getRows());
        columns = modificationMatrix.getCalculatedColumns();
        barChartValues.clear();
        fullItemsSet.clear();
        int coulmnIndx = 0;
        for (String key : columns.keySet()) {
            fullItemsSet.addAll(columns.get(key));
            barChartValues.put(coulmnIndx++, (double) columns.get(key).size());
        }
        updateChartDataset(barChartValues);
        allowDrawing = true;
    }

    private void reDrawLayout(int width, ChartRenderingInfo chartRenderingInfo) {
        //80% == width then 20% ==??
        int x = 28 * width / 72;
        columnsContainer.removeAllComponents();
        chartBarsContainer.removeAllComponents();
        bottomLayoutContainer.removeAllComponents();
        int finalRowNumbers = 0;
        finalRowNumbers = rows.keySet().stream().filter((rowKey) -> !(rows.get(rowKey) == 0)).map((_item) -> 1).reduce(finalRowNumbers, Integer::sum);

        bottomLayoutContainer.setHeight(((finalRowNumbers + 1) * 30), Unit.PIXELS);
        bottomLayoutContainer.addComponent(leftBottomCorner, "left:0px;top:0px;");
        bottomLayoutContainer.addComponent(columnsContainer, "left:" + x + "px;top:0px;");
        leftBottomCorner.removeAllComponents();
        nodesTable.clear();
        chartBarsList.clear();
        int columnPreIndex = 0;
        Integer[] reIndexing = barChartValues.keySet().toArray(new Integer[barChartValues.size()]);
        for (int i = 0; i < chartRenderingInfo.getEntityCollection().getEntityCount(); i++) {
            ChartEntity ent = chartRenderingInfo.getEntityCollection().getEntity(i);
            if (ent instanceof CategoryItemEntity) {
                CategoryItemEntity catEnt = (CategoryItemEntity) ent;
                Rectangle rect = catEnt.getArea().getBounds();
                VerticalLayout column = new VerticalLayout();
                column.setWidth(rect.width, Unit.PIXELS);
                column.setHeight(100, Unit.PERCENTAGE);
                column.addStyleName("selectablenode");
                column.addStyleName("bordermarker");
                column.setSpacing(false);
                columnsContainer.addComponent(column, "left:" + rect.x + "px; top:" + 0 + "px;");
                //init basic nodes
                int columnIndex = reIndexing[columnPreIndex++];
                String columnKey = columns.keySet().toArray()[columnIndex] + "";
                if (!nodesTable.containsKey(columnKey)) {
                    nodesTable.put(columnKey, new ArrayList<>());
                }
                for (String rowKey : rows.keySet()) {
                    if (rows.get(rowKey) == 0) {
                        continue;
                    }
                    SelectableNode node = new SelectableNode(rowKey, columnIndex, columns.get(columnKey).isEmpty(), dataColors.get(rowKey)) {
                        @Override
                        public void selectNode(int columnIndex) {
                            applyFilter(columnIndex);
                        }
                    };
                    node.setData(columns.get(columnKey).size());
                    node.setDescription(columnKey);
                    nodesTable.get(columnKey).add(node);
                    column.addComponent(node);
                    column.setComponentAlignment(node, Alignment.TOP_LEFT);

                }

                VerticalLayout bar = new VerticalLayout();
                bar.setWidth(rect.width, Unit.PIXELS);
                bar.setHeight(rect.height, Unit.PIXELS);
                bar.setStyleName("barlayout");
                bar.setData(columnIndex);
                String mod = columns.keySet().toArray()[columnIndex++].toString().replace("[", "").replace("]", "<br/>").replace(",", "<br/>") +  "<font style='font-size:10px !important;margin-right:5px'> " + VaadinIcons.HASH.getHtml() + "</font>Proteins" +"" + ((int) (double) barChartValues.get(columnIndex - 1)) + "";
                for (String key : dataColors.keySet()) {
                    if (mod.contains(key)) {
                        Color c = dataColors.get(key);
                        mod = mod.replace(key, "<font style='color:rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ");font-size:10px !important;margin-right:5px'> " + VaadinIcons.CIRCLE.getHtml() + "</font>" + key);
                    }

                }
                bar.setDescription(mod);
                chartBarsContainer.addComponent(bar, "left:" + rect.x + "px; top:" + rect.y + "px;");
                chartBarsList.put(columnKey, bar);

            }

        }
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

        int rowIndex = 0;
        for (String rowKey : rows.keySet()) {
            if (rows.get(rowKey) == 0) {
                continue;
            }
            SparkLine sl = new SparkLine(rowKey, rows.get(rowKey), 0, max, dataColors.get(rowKey));
            sl.setData(rowIndex);
            sl.setDescription(rowKey);
            leftBottomCorner.addComponent(sl);
            leftBottomCorner.setComponentAlignment(sl, Alignment.TOP_LEFT);
            rowLabelsMap.put(rowKey, sl);
            List<String> sortedKeysList = new ArrayList<>(rows.keySet());
            for (int columnIndex : barChartValues.keySet()) {
                String columnKey = columns.keySet().toArray()[columnIndex] + "";
                String[] subArr = columnKey.replace("]", "").replace("[", "").trim().split(",");
                int startLineRange = sortedKeysList.indexOf(subArr[0]);
                int endLineRange = sortedKeysList.indexOf(subArr[subArr.length - 1].trim());
//                System.out.println("at node selection "  + "  coulmn key  " + columnKey + "  " + rowIndex + "  startLineRange " + startLineRange+"  end "+subArr[subArr.length - 1].trim()+"  sortedKeysList: "+sortedKeysList);
                if (!nodesTable.containsKey(columnKey) || nodesTable.get(columnKey).get(rowIndex) == null) {
                    columnPreIndex++;
                    continue;
                }
                SelectableNode node = nodesTable.get(columnKey).get(rowIndex);
                if (columnIndex == 4) {

                }
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
                    int nodeIndex = sortedKeysList.indexOf(node.getNodeId());
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
                columnPreIndex++;
            }
            rowIndex++;
        }

    }

    private void selectColumn(Set<Comparable> columnIds) {
        unselectAll();
        if (columnIds.isEmpty()) {
            return;
        }
        String columnId = columnIds.iterator().next() + "";
        if (nodesTable.get(columnId).get(0).isSelected()) {
            return;
        }
        for (SparkLine sL : rowLabelsMap.values()) {
            sL.setSelected(columnId.contains(sL.getDescription()));
        }
        for (SelectableNode sN : nodesTable.get(columnId)) {
            sN.setSelected(true);
        }
        chartBarsList.get(columnId).addStyleName("selectedbarlayout");

    }

    private void unselectAll() {
        for (SparkLine sL : rowLabelsMap.values()) {
            sL.setSelected(false);
        }
        for (VerticalLayout bar : chartBarsList.values()) {
            bar.removeStyleName("selectedbarlayout");
        }
        for (List<SelectableNode> lSN : nodesTable.values()) {
            for (SelectableNode sN : lSN) {
                sN.setSelected(false);

            }
        }
    }

    @Override
    public void suspendFilter(boolean suspend) {
    }

    @Override
    public void redrawChart() {

        if (allowDrawing && index++ > 0) {
            mainChartImg.setSource(new ExternalResource(saveToFile(chart, mainChartRenderingInfo, mainWidth, mainHeight)));
            mainChartImg.removeStyleName("hide");
            reDrawLayout(mainWidth, mainChartRenderingInfo);

        }
    }

    @Override
    public String getFilterId() {
        return filterId;
    }

    @Override
    public void updateFilterSelection(Set<Comparable> selectedItems, Set<Comparable> selectedCategories, boolean topFilter, boolean singleProteinsFilter, boolean selfAction) {

        //case 1 self selection (select coulmn or unselect all)
        if (!selfAction) {
            barChartValues.clear();
            if (singleProteinsFilter && !selfAction && !selectedCategories.isEmpty()) {
                int coulmnIndex = 0;
                for (String key : columns.keySet()) {
                    double d = (double) columns.get(key).size();
                    barChartValues.put(coulmnIndex, d);
                    coulmnIndex++;
                }
//                updateChartDataset(barChartValues);
//                redrawChart();

            } else {
                Map<Integer, Double> tbarChartValues = new LinkedHashMap<>();
                int coulmnIndex = 0;
                for (String key : columns.keySet()) {
                    double d = (double) Sets.intersection(columns.get(key), selectedItems).size();
                    if (d > 0) {
                        tbarChartValues.put(coulmnIndex, d);
                    }
                    coulmnIndex++;
                }
//                barChartValues.clear();
                barChartValues.putAll(tbarChartValues);

            }
            updateChartDataset(barChartValues);
            redrawChart();
//            unselectAll();
        }
        setMainAppliedFilter(topFilter && !selectedCategories.isEmpty());
        selectColumn(selectedCategories);
//       

    }

    public void applyFilter(int columnIndex) {
        if (chartBarsList.size() == 1) {
            return;
        }

        Set<Comparable> appliedFilter = new LinkedHashSet<>();
        if (columnIndex == -1) {
            Selection_Manager.setSelection("dataset_filter_selection", appliedFilter, null, filterId);
            return;
        }
        appliedFilter.add((columns.keySet().toArray()[columnIndex] + ""));
        Selection_Manager.setSelection("dataset_filter_selection", appliedFilter, null, filterId);
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
        if (width < 1 || height < 1) {
            width = 1;
            height = 1;
        }
        byte imageData[];
        try {
//            BarRenderer renderer = ((BarRenderer) ((CategoryPlot) chart.getPlot()).getRenderer());
            double barArea = (double) width / (double) ((CategoryPlot) chart.getPlot()).getDataset().getColumnCount();
            double barWidth = (10.0 / barArea) / 2.0;
//System.out.println("at total width "+width+"   "+((CategoryPlot) chart.getPlot()).getDataset().getColumnCount()+" barWidth "+barWidth);
//            if (((CategoryPlot) chart.getPlot()).getDataset().getColumnCount() == 1) {
            ((BarRenderer) ((CategoryPlot) chart.getPlot()).getRenderer()).setMaximumBarWidth(barWidth);//(50.0 / (double) width));
//            } else {
//                ((BarRenderer) ((CategoryPlot) chart.getPlot()).getRenderer()).setMaximumBarWidth(barWidth);//1.0);
//            }
//            ((CategoryPlot) chart.getPlot()).setRenderer(renderer);
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
     * Initialise the main JFree chart component.
     */
    private void initChart() {
        // create the dataset...
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        CategoryAxis domainAxis = new CategoryAxis();
        Font labelFont = new Font("\"Open Sans\", sans-serif", Font.PLAIN, 11);

        domainAxis.setTickLabelFont(labelFont);
        domainAxis.setTickLabelPaint(Color.GRAY);
        domainAxis.setTickLabelsVisible(false);
        domainAxis.setTickMarksVisible(false);

        NumberAxis rangeAxis = new NumberAxis();
        rangeAxis.setTickLabelFont(labelFont);
        rangeAxis.setTickLabelPaint(Color.GRAY);
        rangeAxis.setUpperMargin(0.17);
        rangeAxis.setTickLabelsVisible(false);
        rangeAxis.setTickMarksVisible(false);
        rangeAxis.setVisible(false);
        rangeAxis.setAxisLineVisible(false);

        BarRenderer renderer = new BarRenderer();
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator() {
            @Override
            public String generateLabel(CategoryDataset dataset, int row, int column) {

                int key = barChartValues.keySet().toArray(new Integer[barChartValues.size()])[column];
                return ((int) ((double) barChartValues.get(key))) + "";//super.generateLabel(dataset, row, column); //To change body of generated methods, choose Tools | Templates.
            }
        });

        renderer.setBaseItemLabelsVisible(true);
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setSeriesPaint(0, Color.WHITE);
        renderer.setShadowVisible(false);
        renderer.setBaseItemLabelFont(labelFont);
        renderer.setBaseItemLabelPaint(Color.GRAY);
        renderer.setMaximumBarWidth(0.075);

        CategoryPlot plot = new CategoryPlot(dataset, domainAxis, rangeAxis, renderer);
        plot.setNoDataMessage("No data available");
        plot.setOutlineVisible(false);
        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinesVisible(false);

        plot.getDomainAxis().setLowerMargin(0.0D);
        plot.getDomainAxis().setUpperMargin(0.0D);

        plot.setBackgroundPaint(Color.WHITE);
        chart = new JFreeChart(plot);
        chart.setPadding(new RectangleInsets(0.0, 0.0, 0.0, 5));
        chart.setBorderPaint(null);
        chart.setBackgroundPaint(null);
        chart.getLegend().setFrame(BlockBorder.NONE);

    }

    private void updateChartDataset(Map<Integer, Double> barChartData) {
        // column keys...    
        int counter = 0;
        // update the dataset...
        DefaultCategoryDataset dataset = (DefaultCategoryDataset) ((CategoryPlot) chart.getPlot()).getDataset();
        dataset.clear();
        for (double d : barChartData.values()) {
            dataset.addValue(Math.log(d), "protData", (counter++) + "");

        }
//        redrawChart();

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
        logValue = ((max / logMax) * logValue) + lowerLimit;//(max/Math.log(max))*Math.log(linearValue)+10;
        return logValue;

//        return Math.min(logValue, max);
    }

    public double lin2log(double z, double y, double x) {
       
        double b = Math.log(y / x) / (y - x);
        double a = y / Math.exp(b * y);
        double finalAnswer = a * Math.exp(b * z);
        System.out.println("final answer "+Math.log(z));
//        double finalAnswer = Math.max(Math.round(tempAnswer) - 1, 0);
        return Math.log(z);

    }

    private void setMainAppliedFilter(boolean mainAppliedFilter) {
        removeFilterIcon.setVisible(mainAppliedFilter);
        if (mainAppliedFilter) {
            this.addStyleName("highlightfilter");
        } else {
            this.removeStyleName("highlightfilter");
        }

    }

}
