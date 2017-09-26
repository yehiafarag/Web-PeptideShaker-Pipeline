package com.uib.web.peptideshaker.presenter.core.filtercharts.charts;

import com.ejt.vaadin.sizereporter.ComponentResizeEvent;
import com.ejt.vaadin.sizereporter.SizeReporter;
import com.google.common.collect.Sets;
import com.itextpdf.text.pdf.codec.Base64;
import com.uib.web.peptideshaker.presenter.core.filtercharts.components.PieChartSlice;
import com.vaadin.event.LayoutEvents;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.block.ColumnArrangement;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;

/**
 * This class represents the dataset interactive pie chart filter
 *
 * @author Yehia Farag
 */
public abstract class DivaPieChart extends HorizontalLayout implements LayoutEvents.LayoutClickListener {

    /**
     * The highlight selection color (required by JFree chart).
     */
    private final Color selectedColor = Color.decode("#197de1");
    /**
     * Map of each category in the chart and its default color (for JFree chart
     * reset coloring).
     */
    private final Map<Comparable, Color> defaultKeyColorMap = new HashMap<>();
    /**
     * Map of category and selected slice color (required by JFree chart).
     */
    private final Map<Comparable, Color> selectedKeyColorMap = new HashMap<>();
    /**
     * Map of category and value (number of datasets).
     */
    private final Map<Comparable, String> valuesMap = new HashMap<>();
    /**
     * The width of the chart.
     */
    private int width;
    /**
     * The height of the chart.
     */
    private int height;
    /**
     * The map of category and current updated dataset indexes.
     */
    private final Map<Comparable, List<Comparable>> inuseDsIndexesMap;
    /**
     * The set of selected dataset indexes.
     */
    private final HashSet<Comparable> selectedItemIds = new HashSet<>();
    /**
     * The set of selected categories.
     */
    private final HashSet<Comparable> selectedCategories = new HashSet<>();
    /**
     * The set of all dataset indexes.
     */
    private final HashSet<Comparable> fullDsIds = new HashSet<>();
    /**
     * The map of category and each pie-chart slice component.
     */
    private final Map<Comparable, PieChartSlice> chartData;
    /**
     * Array of default slice colors (required by JFree chart).
     */
    private Color[] defaultColors = new Color[]{new Color(110, 177, 206), new Color(219, 169, 1), new Color(213, 8, 8), new Color(4, 180, 95), new Color(174, 180, 4), new Color(10, 255, 14), new Color(244, 250, 88), new Color(255, 0, 64), new Color(246, 216, 206), new Color(189, 189, 189), new Color(255, 128, 0), Color.WHITE};
    /**
     * The main pie-chart JFree plot(required by JFree chart).
     */
    private PiePlot plot;
    /**
     * The main pie-chart JFree chart (required by JFree chart).
     */
    private JFreeChart chart;
    /**
     * The main pie-chart JFree chart rendering information generated by JFree
     * chart and contain all the chart information.
     */
    private final ChartRenderingInfo chartRenderingInfo = new ChartRenderingInfo();
    /**
     * The main chart background image (to be updated using JFreechart).
     */
    private final Image chartBackgroundImg;
    /**
     * A wite layout that has the label and turn pie-chart into donut chart.
     */
    private final VerticalLayout middleDountLayout;

    /**
     * The chart label contain the total number of datasets.
     */
    private final Label selectAllLabel;
    private final boolean thumbMode;
    private final AbsoluteLayout mainContainer;
    private final Image legendImg;
    private final ChartRenderingInfo legendRenderingInfo = new ChartRenderingInfo();

    /**
     * Constructor to initialize the main attributes
     *
     * @param filterTitle The filter title
     * @param filterId The filter ID
     */
    public DivaPieChart(String filterTitle, String filterId, boolean thumbMode) {
        this.width = 300;
        this.height = 300;
        this.thumbMode = thumbMode;
        DivaPieChart.this.setWidth(100, Unit.PERCENTAGE);
        DivaPieChart.this.setHeight(100, Unit.PERCENTAGE);

        mainContainer = new AbsoluteLayout();
        mainContainer.setWidth(100, Unit.PERCENTAGE);
        mainContainer.setHeight(100, Unit.PERCENTAGE);
        mainContainer.addLayoutClickListener(DivaPieChart.this);
        DivaPieChart.this.addComponent(mainContainer);

        SizeReporter sizeReporter = new SizeReporter(mainContainer);
        sizeReporter.addResizeListener((ComponentResizeEvent event) -> {
            int tWidth = event.getWidth();
            int tHeight = event.getHeight();
            if (width == tWidth && height == tHeight) {
                return;
            }
            width = tWidth;
            height = tHeight;
            redrawChart();
        });
        this.chartBackgroundImg = new Image();
        DivaPieChart.this.addStyleName("pointer");
        chartBackgroundImg.setWidth(100, Unit.PERCENTAGE);
        chartBackgroundImg.setHeight(100, Unit.PERCENTAGE);
        mainContainer.addComponent(chartBackgroundImg, "left: 0px; top: 0px");

        middleDountLayout = new VerticalLayout();
        middleDountLayout.setStyleName("middledountchart");

        selectAllLabel = new Label();
        selectAllLabel.addStyleName("middledountchart");
        selectAllLabel.setContentMode(ContentMode.HTML);
        selectAllLabel.addStyleName(ValoTheme.LABEL_TINY);
        selectAllLabel.addStyleName(ValoTheme.LABEL_SMALL);
        this.initPieChart();
        this.inuseDsIndexesMap = new LinkedHashMap<>();
        this.chartData = new LinkedHashMap<>();
        legendImg = new Image();
        if (thumbMode) {
            legendImg.setSizeFull();
            legendImg.setStyleName("legendimg");
            DivaPieChart.this.addComponent(legendImg);
        }

    }

    /**
     * Initialize the main JFree chart component.
     */
    private void initPieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        plot = new PiePlot(dataset);
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
        if (thumbMode) {
            plot.setInteriorGap(0.0);
            plot.setLabelGenerator(null);
            chart.getLegend().setItemFont(new Font("\"Open Sans\", sans-serif", Font.PLAIN, 11));
            selectAllLabel.addStyleName("invisibletext");
            plot.setBaseSectionOutlineStroke(new BasicStroke(2));
            plot.setInteriorGap(0);
            chart.getLegend().setPosition(RectangleEdge.RIGHT);
            chart.setPadding(new RectangleInsets(0, 0, 0, 0));
            chart.setBorderVisible(false);
            plot.setOutlinePaint(null);
            plot.setOutlineVisible(false);
            plot.setShadowYOffset(0);
            plot.setShadowXOffset(0);

        } else {
            plot.setLabelFont(new Font("\"Open Sans\", sans-serif", Font.PLAIN, 13));
            plot.setLabelGenerator(new PieSectionLabelGenerator() {

                @Override
                public String generateSectionLabel(PieDataset pd, Comparable cmprbl) {
                    return valuesMap.get(cmprbl);
                }

                @Override
                public AttributedString generateAttributedSectionLabel(PieDataset pd, Comparable cmprbl) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            });
            plot.setSimpleLabels(false);
            plot.setLabelBackgroundPaint(null);
            plot.setLabelShadowPaint(null);
            plot.setLabelPaint(Color.GRAY);
            plot.setLabelOutlinePaint(null);
            chart.getLegend().setItemFont(new Font("\"Open Sans\", sans-serif", Font.PLAIN, 12));
            plot.setInteriorGap(0.05);
            plot.setBaseSectionOutlineStroke(new BasicStroke(1.2f));

        }
        chart.setBorderPaint(null);
        chart.setBackgroundPaint(null);
        chart.getLegend().setFrame(BlockBorder.NONE);

    }

    /**
     * Convert JFree chart into image and encode it as base64 string to be used
     * as image link.
     *
     * @param chart JFree chart instance
     * @param width Image width
     * @param height Image height.
     */
    private String saveToFile(final JFreeChart chart, double width, double height) {
        byte imageData[];
        try {
            if (thumbMode) {
                chart.getLegend().setVisible(false);
            }
            imageData = ChartUtilities.encodeAsPNG(chart.createBufferedImage((int) width, (int) height, chartRenderingInfo));
            String base64 = Base64.encodeBytes(imageData);
            base64 = "data:image/png;base64," + base64;
            if (thumbMode) {
                LegendTitle legend = new LegendTitle(chart.getPlot(),
                        new ColumnArrangement(HorizontalAlignment.LEFT, VerticalAlignment.CENTER, 0, 0),
                        new ColumnArrangement(HorizontalAlignment.LEFT, VerticalAlignment.CENTER, 0, 0));
                chart.addLegend(legend);
                legend.setPadding(RectangleInsets.ZERO_INSETS);
                legend.setPosition(RectangleEdge.LEFT);

                int tW = (int) width;
                int tH = (int) height;
                BufferedImage blegendImg = chart.createBufferedImage(tW, tH, legendRenderingInfo);
                double plotWidth = legendRenderingInfo.getPlotInfo().getPlotArea().getWidth();
                if (plotWidth < 0) {
                    plotWidth = 0.0;
                }
                int nTW = (int) Math.min((int) (tW - plotWidth - 10), tW);
                blegendImg = blegendImg.getSubimage(0, 0, nTW, tH);
                byte legendImgData[] = ChartUtilities.encodeAsPNG(blegendImg);
                String legendUrl = Base64.encodeBytes(legendImgData);
                legendUrl = "data:image/png;base64," + legendUrl;
                legendImg.setSource(new ExternalResource(legendUrl));
                legendImg.setWidth(nTW, Unit.PIXELS);
                chart.removeLegend();
            }

            return base64;
        } catch (IOException e) {
            System.err.println("at error " + e.getMessage());
        }
        return "";
    }

    /**
     * This method is responsible for updating pie-chart data.
     *
     * @param chartData information required to update chart data.
     */
    public void initializeFilterData(Map<Comparable, PieChartSlice> chartData, Color[] defaultColors) {
        this.chartData.clear();
        this.chartData.putAll(chartData);
        this.defaultColors = defaultColors;
        fullDsIds.clear();
        int coundDs = 0;
        coundDs = chartData.values().stream().map((slice) -> slice.getTotalValue()).reduce(coundDs, Integer::sum);
        this.selectAllLabel.setValue("<center>" + coundDs + "</center>");
        reset();
    }

    /**
     * This method responsible for invoking the selection action the method to
     * be implemented in the container to maintain pie-chart interactivity
     *
     * @param noselection nothing is selected
     */
    public abstract void selectDatasets(boolean noselection);

    /**
     * Update the chart generated image based on user selection.
     */
    public void redrawChart() {
        if (width < 1 || height < 1) {
            return;
        }
        String imgUrl = saveToFile(chart, width, height);
        this.chartBackgroundImg.setSource(new ExternalResource(imgUrl));
        float dountW = (float) Math.min(chartRenderingInfo.getPlotInfo().getDataArea().getWidth(), chartRenderingInfo.getPlotInfo().getDataArea().getHeight());
        if (thumbMode) {
            dountW = dountW * 0.55f;
        } else {
            dountW = dountW * 0.4f;
        }
        selectAllLabel.setWidth(dountW, Unit.PIXELS);
        selectAllLabel.setHeight(dountW, Unit.PIXELS);
        selectAllLabel.setVisible(dountW >= 30);
        mainContainer.addComponent(selectAllLabel, "left: " + (chartRenderingInfo.getPlotInfo().getDataArea().getCenterX() - dountW * 0.5) + "px; top:" + (chartRenderingInfo.getPlotInfo().getDataArea().getCenterY() - dountW * 0.5) + "px");
    }

    /**
     * On chart click (selection on the pie-chart layout).
     *
     * @param event user click action
     */
    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        if (event.getClickedComponent() instanceof VerticalLayout || event.getClickedComponent() instanceof Label) {
            return;
        }

        ChartEntity entity = chartRenderingInfo.getEntityCollection().getEntity(event.getRelativeX(), event.getRelativeY());

        if (entity != null && entity instanceof PieSectionEntity) {
            PieSectionEntity pieEnt = (PieSectionEntity) entity;
            selectSlice(pieEnt.getSectionKey());
            selectDatasets(selectedCategories.isEmpty());
        }
    }

    /**
     * Select slice action.
     *
     * @param sliceKey selected slice key.
     */
    private void selectSlice(Comparable sliceKey) {
        if (plot.getSectionOutlinePaint(sliceKey) == selectedColor) {
            plot.setSectionOutlinePaint(sliceKey, null);
            plot.setSectionPaint(sliceKey, defaultKeyColorMap.get(sliceKey));
            selectedItemIds.removeAll(chartData.get(sliceKey).getItemsIds());
            selectedCategories.remove(sliceKey);
        } else {
            plot.setSectionOutlinePaint(sliceKey, selectedColor);
            plot.setSectionPaint(sliceKey, selectedKeyColorMap.get(sliceKey));
            selectedItemIds.addAll(chartData.get(sliceKey).getItemsIds());
            selectedCategories.add(sliceKey);

        }

        redrawChart();

    }

    /**
     * Select slice action.
     *
     * @param sliceKey selected slice key.
     */
    public void updateSliceSelection(Set<Comparable> sliceKeys) {
        reset();
        for (Comparable sliceKey : sliceKeys) {
            if (plot.getSectionOutlinePaint(sliceKey) == selectedColor) {
                plot.setSectionOutlinePaint(sliceKey, null);
                plot.setSectionPaint(sliceKey, defaultKeyColorMap.get(sliceKey));
                selectedItemIds.removeAll(chartData.get(sliceKey).getItemsIds());
                selectedCategories.remove(sliceKey);
            } else {
                plot.setSectionOutlinePaint(sliceKey, selectedColor);
                plot.setSectionPaint(sliceKey, selectedKeyColorMap.get(sliceKey));
                selectedItemIds.addAll(chartData.get(sliceKey).getItemsIds());
                selectedCategories.add(sliceKey);

            }
        }

        redrawChart();

    }

    /**
     * Check if filter has selected data or empty
     *
     * @return no data selected
     */
    public boolean isActiveFilter() {

        return !selectedItemIds.isEmpty();
    }

    /**
     * Synchronize the pie-chart in response to other pie-charts filter
     *
     * @param selectedItems Set of quant dataset indexes
     * @param single only one filter is applied
     */
    public void localUpdate(Set<Comparable> selectedItems, boolean single) {

        if (single && !selectedItemIds.isEmpty()) {
            selectedItems.clear();
            selectedItems.addAll(fullDsIds);

        }
        List<Double> barChartData = new ArrayList<>();
        for (PieChartSlice slice : chartData.values()) {
            barChartData.add((double) Sets.intersection(slice.getItemsIds(), selectedItems).size());
        }
        TreeSet<Double> barChartset = new TreeSet<>(barChartData);

        this.selectAllLabel.setValue("<center>" + selectedItems.size() + "</center>");
        valuesMap.clear();
        DefaultPieDataset dataset = (DefaultPieDataset) plot.getDataset();
        dataset.clear();
        chartData.values().stream().forEach((slice) -> {
            int value = 0;
            List<Comparable> idList = new ArrayList<>();
            value = selectedItems.stream().filter((id) -> (slice.getItemsIds().contains(id))).map((id) -> {
                idList.add(id);
                return id;
            }).map((_item) -> 1).reduce(value, Integer::sum);
            inuseDsIndexesMap.put(slice.getLabel(), idList);
            dataset.setValue(slice.getLabel(), scaleValues((double) Sets.intersection(slice.getItemsIds(), selectedItems).size(), barChartset.last(), barChartset.first()));
            valuesMap.put(slice.getLabel(), value + "");
        });

        redrawChart();
    }

    /**
     * Get selected dataset indexes
     *
     * @return selectedItemIds set of selected dataset indexes
     */
    public HashSet<Comparable> getSelectedItemIds() {
        if (selectedItemIds.isEmpty()) {
            return fullDsIds;
        }
        return selectedItemIds;
    }

    /**
     * Reset the chart ti initial state.
     */
    public void reset() {
        DefaultPieDataset dataset = (DefaultPieDataset) plot.getDataset();
        dataset.clear();
        defaultKeyColorMap.clear();
        selectedKeyColorMap.clear();
        valuesMap.clear();
        inuseDsIndexesMap.clear();
        selectedItemIds.clear();
        selectedCategories.clear();

        List<Double> barChartData = new ArrayList<>();
        for (PieChartSlice slice : chartData.values()) {
            barChartData.add((double) slice.getTotalValue());
        }
        TreeSet<Double> barChartset = new TreeSet<>(barChartData);

        Map<Comparable, PieChartSlice> tchartData = new LinkedHashMap<>();
        int counter = 0;
        int coundDs = 0;
        for (PieChartSlice slice : chartData.values()) {
            if (slice.getLabel().toString().trim().equals("")) {
                slice.setLabel("Not Available");
                slice.setColor(Color.LIGHT_GRAY);
            } else {
                slice.setColor(defaultColors[counter++]);
            }
            dataset.setValue(slice.getLabel(), scaleValues(slice.getTotalValue(), barChartset.last(), barChartset.first()));//slice.getTotalValue());
            fullDsIds.addAll(slice.getItemsIds());
            plot.setSectionPaint(slice.getLabel(), slice.getColor());
            plot.setSectionOutlinePaint(slice.getLabel(), null);
            valuesMap.put(slice.getLabel(), slice.getTotalValue() + "");
            defaultKeyColorMap.put(slice.getLabel(), slice.getColor());
            selectedKeyColorMap.put(slice.getLabel(), slice.getColor().darker());
            coundDs += slice.getTotalValue();
            inuseDsIndexesMap.put(slice.getLabel(), new ArrayList<>(slice.getItemsIds()));
            tchartData.put(slice.getLabel(), slice);
            plot.setExplodePercent(slice.getLabel(), 0);

        }
        this.selectAllLabel.setValue("<center>" + coundDs + "</center>");
        this.chartData.clear();
        this.chartData.putAll(tchartData);
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
        if (linearValue == 0.0) {
            return linearValue;
        }
        double logMax = (Math.log(max) / Math.log(2));
        double logValue = (Math.log(linearValue + 1) / Math.log(2));
        logValue = (logValue * 2 / logMax) + lowerLimit;
        return Math.min(logValue, max);
    }

    public HashSet<Comparable> getSelectedCategories() {
        return selectedCategories;
    }

}
