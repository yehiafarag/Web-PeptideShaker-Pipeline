package com.uib.web.peptideshaker.presenter.core.filtercharts.charts;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.ScatterChartConfig;
import com.byteowls.vaadin.chartjs.data.Data;
import com.byteowls.vaadin.chartjs.data.ScatterData;
import com.byteowls.vaadin.chartjs.data.ScatterDataset;
import com.byteowls.vaadin.chartjs.options.InteractionMode;
import com.byteowls.vaadin.chartjs.options.scale.Axis;
import com.byteowls.vaadin.chartjs.options.scale.LinearScale;
import com.vaadin.data.Property;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Slider;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents range component with lower and upper range
 *
 * @author Yehia Farag
 */
public abstract class RangeFilter extends VerticalLayout implements Property.ValueChangeListener {

    private final VerticalLayout chartContainer;
    private final Slider upperRangeSlider;
    private final Slider lowerRangeSlider;
    private final AbsoluteLayout slidersContainer;
    private double[][] data;
    private final Double splitDataPoint;
    private final String[] colors;
    private final Label upperLabelValueComponent;

    private OptionGroup addExtraRange;
    private Label secoundUpperLabelValueComponent;
    private Slider secoundUpperRangeSlider;
    private Slider secoundLowerRangeSlider;
    private AbsoluteLayout secoundSlidersContainer;
    private final HorizontalLayout secoundBottopmLayout;

    private final boolean supportMultiRange;

    public RangeFilter(String lowerLabel, double lowerValue, String upperLabel, double upperValue, String[] colors, Double splitDataPoint, boolean supportMultiRange) {
        this.splitDataPoint = splitDataPoint;
        this.colors = colors;
        this.supportMultiRange = supportMultiRange;
        RangeFilter.this.setSizeFull();
        RangeFilter.this.setStyleName("fixedFilterFrame");
        RangeFilter.this.setSpacing(true);
        RangeFilter.this.setMargin(new MarginInfo(true, false, false, false));

        Label thumbTitle = new Label("Kokowawa");
        thumbTitle.setContentMode(ContentMode.HTML);
        thumbTitle.setStyleName(ValoTheme.LABEL_BOLD);
        thumbTitle.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        thumbTitle.setWidth(100, Unit.PERCENTAGE);
        thumbTitle.setHeight(30, Unit.PIXELS);
        thumbTitle.addStyleName("centeredtext");
        RangeFilter.this.addComponent(thumbTitle);
        RangeFilter.this.setExpandRatio(thumbTitle, 1);

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setSizeFull();
        RangeFilter.this.addComponent(topLayout);
         RangeFilter.this.setExpandRatio(topLayout, 9);
        Label loweLableComponent = initLabel(lowerLabel);
        topLayout.addComponent(loweLableComponent);
        topLayout.setComponentAlignment(loweLableComponent, Alignment.MIDDLE_CENTER);
        topLayout.setExpandRatio(loweLableComponent, 25);

        chartContainer = new VerticalLayout();
        chartContainer.setWidth(100, Unit.PERCENTAGE);
        chartContainer.setHeight(100, Unit.PERCENTAGE);
        chartContainer.setMargin(new MarginInfo(false, false, false, false));
        chartContainer.setStyleName("scatterchartcontainer");
//        chartContainer.setStyleName("blacklayout");
        topLayout.addComponent(chartContainer);
        topLayout.setComponentAlignment(chartContainer, Alignment.TOP_LEFT);
        topLayout.setExpandRatio(chartContainer, 50);

        Label upperLabelComponent = initLabel(upperLabel);
        topLayout.addComponent(upperLabelComponent);
        topLayout.setComponentAlignment(upperLabelComponent, Alignment.MIDDLE_RIGHT);
        topLayout.setExpandRatio(upperLabelComponent, 25);

        HorizontalLayout bottopmLayout = new HorizontalLayout();
        bottopmLayout.setSizeFull();
        RangeFilter.this.addComponent(bottopmLayout);
         RangeFilter.this.setExpandRatio(bottopmLayout, 9);

        Label loweLableValueComponent = initLabel((int) lowerValue + "");
        bottopmLayout.addComponent(loweLableValueComponent);
        bottopmLayout.setComponentAlignment(loweLableValueComponent, Alignment.MIDDLE_CENTER);
        bottopmLayout.setExpandRatio(loweLableValueComponent, 25);

        slidersContainer = new AbsoluteLayout();
        slidersContainer.setWidth(150, Unit.PIXELS);
        slidersContainer.setHeight(50, Unit.PIXELS);
        bottopmLayout.addComponent(slidersContainer);
        bottopmLayout.setComponentAlignment(slidersContainer, Alignment.MIDDLE_CENTER);
        bottopmLayout.setExpandRatio(slidersContainer, 50);

        lowerRangeSlider = new Slider();
        lowerRangeSlider.setWidth(130, Unit.PIXELS);
        lowerRangeSlider.setMin(lowerValue);
        lowerRangeSlider.setMax(upperValue);
        lowerRangeSlider.setValue(lowerValue);
        lowerRangeSlider.setStyleName("rangeslider");
        lowerRangeSlider.addStyleName("lower");
        lowerRangeSlider.addValueChangeListener(RangeFilter.this);
        slidersContainer.addComponent(lowerRangeSlider, "left:0px; top:50%;");

        upperRangeSlider = new Slider();
        upperRangeSlider.setStyleName("rangeslider");
        upperRangeSlider.addStyleName("upper");
        upperRangeSlider.setWidth(130, Unit.PIXELS);
        upperRangeSlider.setMin((lowerValue));
        upperRangeSlider.setMax(upperValue);
        upperRangeSlider.setValue(upperValue);
        upperRangeSlider.addValueChangeListener(RangeFilter.this);
        slidersContainer.addComponent(upperRangeSlider, "left:0px; top:50%;");

        upperLabelValueComponent = initLabel((int) upperValue + "");
        bottopmLayout.addComponent(upperLabelValueComponent);
        bottopmLayout.setComponentAlignment(upperLabelValueComponent, Alignment.MIDDLE_RIGHT);
        bottopmLayout.setExpandRatio(upperLabelValueComponent, 25);
        secoundBottopmLayout = new HorizontalLayout();
        if (!supportMultiRange) {
            return;
        }

        secoundBottopmLayout.setSizeFull();
        RangeFilter.this.addComponent(secoundBottopmLayout);
         RangeFilter.this.setExpandRatio(secoundBottopmLayout, 9);
        secoundBottopmLayout.setVisible(false);

        Label sloweLableValueComponent = initLabel((int) lowerValue + "");
        secoundBottopmLayout.addComponent(sloweLableValueComponent);
        secoundBottopmLayout.setComponentAlignment(sloweLableValueComponent, Alignment.MIDDLE_CENTER);
        secoundBottopmLayout.setExpandRatio(sloweLableValueComponent, 25);

        secoundSlidersContainer = new AbsoluteLayout();
        secoundSlidersContainer.setWidth(150, Unit.PIXELS);
        secoundSlidersContainer.setHeight(50, Unit.PIXELS);
        secoundBottopmLayout.addComponent(secoundSlidersContainer);
        secoundBottopmLayout.setComponentAlignment(secoundSlidersContainer, Alignment.MIDDLE_CENTER);
        secoundBottopmLayout.setExpandRatio(secoundSlidersContainer, 50);

        secoundLowerRangeSlider = new Slider();
        secoundLowerRangeSlider.setWidth(130, Unit.PIXELS);
        secoundLowerRangeSlider.setMin(lowerValue);
        secoundLowerRangeSlider.setMax(upperValue);
        secoundLowerRangeSlider.setValue(lowerValue);
        secoundLowerRangeSlider.setStyleName("rangeslider");
        secoundLowerRangeSlider.addStyleName("lower");
        secoundLowerRangeSlider.addValueChangeListener(RangeFilter.this);
        secoundSlidersContainer.addComponent(secoundLowerRangeSlider, "left:0px; top:50%;");

        secoundUpperRangeSlider = new Slider();
        secoundUpperRangeSlider.setStyleName("rangeslider");
        secoundUpperRangeSlider.addStyleName("upper");
        secoundUpperRangeSlider.setWidth(130, Unit.PIXELS);
        secoundUpperRangeSlider.setMin((lowerValue));
        secoundUpperRangeSlider.setMax(upperValue);
        secoundUpperRangeSlider.setValue(upperValue);
        secoundUpperRangeSlider.addValueChangeListener(RangeFilter.this);
        secoundSlidersContainer.addComponent(secoundUpperRangeSlider, "left:0px; top:50%;");

        secoundUpperLabelValueComponent = initLabel((int) upperValue + "");
        secoundBottopmLayout.addComponent(secoundUpperLabelValueComponent);
        secoundBottopmLayout.setComponentAlignment(secoundUpperLabelValueComponent, Alignment.MIDDLE_RIGHT);
        secoundBottopmLayout.setExpandRatio(secoundUpperLabelValueComponent, 25);

        addExtraRange = new OptionGroup();
        addExtraRange.setStyleName(ValoTheme.OPTIONGROUP_SMALL);
        addExtraRange.setMultiSelect(true);
        addExtraRange.setCaptionAsHtml(true);
        addExtraRange.addItem("addRrange");
        addExtraRange.setItemCaption("addRrange", "Add range");
        addExtraRange.setDescription("Add extra selection range bar");
        RangeFilter.this.addComponent(addExtraRange);
        RangeFilter.this.setExpandRatio(addExtraRange, 9);
        

        addExtraRange.addValueChangeListener((Property.ValueChangeEvent event) -> {
            secoundBottopmLayout.setVisible(addExtraRange.getValue().toString().equalsIgnoreCase("[addRrange]"));
        });

    }

    public void updateData(double[][] data) {
        this.data = data;
    }

    public void resetRange() {
        upperRangeSlider.removeValueChangeListener(RangeFilter.this);
        lowerRangeSlider.removeValueChangeListener(RangeFilter.this);
        upperRangeSlider.setValue(upperRangeSlider.getMax());
        lowerRangeSlider.setValue(lowerRangeSlider.getMin());

        if (supportMultiRange) {
            secoundUpperRangeSlider.removeValueChangeListener(RangeFilter.this);
            secoundLowerRangeSlider.removeValueChangeListener(RangeFilter.this);
            secoundUpperRangeSlider.setValue(upperRangeSlider.getMax());
            secoundLowerRangeSlider.setValue(lowerRangeSlider.getMin());
        }

        updateFilter();
        upperRangeSlider.addValueChangeListener(RangeFilter.this);
        lowerRangeSlider.addValueChangeListener(RangeFilter.this);
        if (supportMultiRange) {
            secoundUpperRangeSlider.addValueChangeListener(RangeFilter.this);
            secoundLowerRangeSlider.addValueChangeListener(RangeFilter.this);
        }

    }

    public void updateFilter() {
        int min = -100;
        int max = 100;
        if (data.length == 0) {
            upperRangeSlider.setEnabled(false);
            lowerRangeSlider.setEnabled(false);
            chartContainer.removeAllComponents();
            if (supportMultiRange) {
                secoundUpperRangeSlider.setEnabled(false);
                secoundLowerRangeSlider.setEnabled(false);
            }
            return;
        }
        upperRangeSlider.setEnabled(true);
        lowerRangeSlider.setEnabled(true);
        if (supportMultiRange) {
            secoundUpperRangeSlider.setEnabled(true);
            secoundLowerRangeSlider.setEnabled(true);
        }

        if (scatterChartConfig == null) {
            List<ScatterData> scatterdataList = new ArrayList<>();
            List<ScatterData> lowerDataList = new ArrayList<>();
            List<ScatterData> splitDataList = new ArrayList<>();
            List<ScatterData> upperDataList = new ArrayList<>();
            List<ScatterDataset> datasetsList = new ArrayList<>();
            boolean splitDataExist = false;
            if (splitDataPoint != null) {
                for (double[] dArr : data) {
                    ScatterData sd = new ScatterData();
                    sd.x(dArr[0]);
                    sd.y(dArr[1]);
                    scatterdataList.add(sd);
                    splitDataList.add(sd);
                    if (dArr[0] > splitDataPoint) {
                        upperDataList.add(sd);
                    } else if (dArr[0] < splitDataPoint) {
                        lowerDataList.add(sd);
                    } else {
                        splitDataExist = true;
                    }
                }
                if (!splitDataExist) {
                    splitDataList.clear();
                }
//                if (scatterdataList.size() == 1) {
//                    ScatterData sd = new ScatterData();
//                    String[] arr = scatterdataList.get(0).toString().replace("\\[", "").replace("]", "").replace(" ", "").split(",");
//                    sd.x(Double.valueOf(arr[0].split("=")[1]) + 3.0);
//                    sd.y(Double.valueOf(arr[1].split("=")[1]));
////                    scatterdataList.add(sd);
//                }
                if (lowerDataList.size() == 1) {
                    ScatterData sd = new ScatterData();
                    String[] arr = lowerDataList.get(0).toString().replace("\\[", "").replace("]", "").replace(" ", "").split(",");
                    sd.x(Double.valueOf(arr[0].split("=")[1]) + 6.0);
                    sd.y(Double.valueOf(arr[1].split("=")[1]));
                    lowerDataList.add(sd);
                }
                if (splitDataList.size() == 1) {
                    ScatterData sd = new ScatterData();
                    String[] arr = splitDataList.get(0).toString().replace("\\[", "").replace("]", "").replace(" ", "").split(",");
                    sd.x(Double.valueOf(arr[0].split("=")[1]) + 3.0);
                    sd.y(Double.valueOf(arr[1].split("=")[1]));
                    splitDataList.add(sd);

                    ScatterData sd1 = new ScatterData();
                    sd1.x(Double.valueOf(arr[0].split("=")[1]) - 3.0);
                    sd1.y(Double.valueOf(arr[1].split("=")[1]));
                    splitDataList.add(sd1);
                }
                if (upperDataList.size() == 1) {
                    ScatterData sd = new ScatterData();
                    String[] arr = upperDataList.get(0).toString().replace("\\[", "").replace("]", "").replace(" ", "").split(",");
                    sd.x(Double.valueOf(arr[0].split("=")[1]) - 6.0);
                    sd.y(Double.valueOf(arr[1].split("=")[1]));
                    upperDataList.add(sd);
                }

                ScatterDataset ds = (new ScatterDataset().label("1").fill(false).dataAsList(scatterdataList).backgroundColor("lightgray"));
                ScatterDataset lowerHightLightDs = (new ScatterDataset().label("2").fill(true).dataAsList(lowerDataList).backgroundColor(colors[0]));
                ScatterDataset splitHightLightDs = (new ScatterDataset().label("3").fill(true).dataAsList(splitDataList).backgroundColor("rgb(154, 208, 248)"));
                ScatterDataset upperHightLightDs = (new ScatterDataset().label("4").fill(true).dataAsList(upperDataList).backgroundColor(colors[1]));

                datasetsList.add(ds);
                datasetsList.add(lowerHightLightDs);
                datasetsList.add(upperHightLightDs);
                datasetsList.add(splitHightLightDs);

            } else {
                for (double[] dArr : data) {
                    ScatterData sd = new ScatterData();
                    sd.x(dArr[0]);
                    sd.y(dArr[1]);
                    scatterdataList.add(sd);
                }
                min = 0;
                max = (int) data[data.length - 1][0];
                if (scatterdataList.size() == 1) {
                    ScatterData sd = new ScatterData();
                    sd.x((double) min);
                    sd.y(0.0);
                    scatterdataList.add(sd);
                }

                ScatterDataset ds = (new ScatterDataset().label("1").fill(false).dataAsList(scatterdataList).backgroundColor("lightgray"));
                ScatterDataset hightLightDs = (new ScatterDataset().label("2").fill(true).dataAsList(scatterdataList).backgroundColor("#197de1"));
                datasetsList.add(ds);
                datasetsList.add(hightLightDs);
            }
            upperLabelValueComponent.setValue("<center>" + max + "</center>");

            upperRangeSlider.removeValueChangeListener(RangeFilter.this);
            lowerRangeSlider.removeValueChangeListener(RangeFilter.this);
            lowerRangeSlider.setMin(min);
            lowerRangeSlider.setMax(max);
            upperRangeSlider.setMin(min);
            upperRangeSlider.setMax(max);
            upperRangeSlider.addValueChangeListener(RangeFilter.this);
            lowerRangeSlider.addValueChangeListener(RangeFilter.this);
            chartContainer.removeAllComponents();
            ChartJs chart = updateChart(datasetsList, min, max);
            chartContainer.addComponent(chart);
            chartContainer.setComponentAlignment(chart, Alignment.TOP_LEFT);
        } else {
            redrawRangeOnChart(lowerRangeSlider.getValue(), upperRangeSlider.getValue());
        }
    }

    private Label initLabel(String labelValue) {
        Label label = new Label("<center>" + labelValue + "</center>");
        label.setStyleName(ValoTheme.LABEL_SMALL);
        label.setStyleName(ValoTheme.LABEL_TINY);
        label.setContentMode(ContentMode.HTML);
        label.setWidth(100, Unit.PERCENTAGE);
        return label;

    }

    private ScatterChartConfig scatterChartConfig;

    private ChartJs updateChart(List<ScatterDataset> datasetsList, int min, int max) {
        scatterChartConfig = new ScatterChartConfig();
        Data<ScatterChartConfig> linechartData = scatterChartConfig.data();
        for (ScatterDataset ds : datasetsList) {
            linechartData.addDataset(ds);
        }

        linechartData.and()
                .options().animation().duration(1).and()
                .legend().display(false).and()
                .responsive(false)
                .title()
                .display(false)
                .and()
                .tooltips()
                .enabled(false)
                .and()
                .hover()
                .mode(InteractionMode.NEAREST)
                .intersect(true)
                .and()
                .scales()
                .add(Axis.X, new LinearScale()
                        .display(false)
                        .scaleLabel()
                        .display(false)
                        .and().ticks().min(min).max(max).fixedStepSize(1.0).display(false).and().gridLines().drawBorder(false).drawTicks(false).and())
                .add(Axis.Y, new LinearScale()
                        .display(false)
                        .scaleLabel()
                        .display(false)
                        .and()
                        .ticks().beginAtZero(Boolean.TRUE).display(false)
                        .and().gridLines().drawBorder(false).drawTicks(false).and())
                .and().elements().point().radius(0).and().and()
                .done();

        ChartJs chart = new ChartJs(scatterChartConfig);
        chart.setWidth(100, Unit.PERCENTAGE);
        chart.setHeight(50, Unit.PERCENTAGE);
        return chart;

    }

    public void valueChange() {
        upperRangeSlider.removeStyleName("lower");
        upperRangeSlider.removeStyleName("upper");
        lowerRangeSlider.removeStyleName("lower");
        lowerRangeSlider.removeStyleName("upper");
        if (supportMultiRange) {
            secoundUpperRangeSlider.removeStyleName("lower");
            secoundUpperRangeSlider.removeStyleName("upper");
            secoundLowerRangeSlider.removeStyleName("lower");
            secoundLowerRangeSlider.removeStyleName("upper");;
        }

        AbsoluteLayout.ComponentPosition lowerPos = slidersContainer.getPosition(lowerRangeSlider);
        AbsoluteLayout.ComponentPosition upperPos = slidersContainer.getPosition(upperRangeSlider);
        slidersContainer.removeAllComponents();
        double min;
        double max;

        if (lowerRangeSlider.getValue() <= upperRangeSlider.getValue() && slidersContainer.getComponentCount() == 0) {
            slidersContainer.addComponent(lowerRangeSlider, lowerPos.getCSSString());
            slidersContainer.addComponent(upperRangeSlider, upperPos.getCSSString());
            upperRangeSlider.addStyleName("upper");
            lowerRangeSlider.addStyleName("lower");
            min = lowerRangeSlider.getValue();
            max = upperRangeSlider.getValue();

        } else {
            slidersContainer.addComponent(upperRangeSlider, upperPos.getCSSString());
            slidersContainer.addComponent(lowerRangeSlider, lowerPos.getCSSString());
            lowerRangeSlider.addStyleName("upper");
            upperRangeSlider.addStyleName("lower");
            min = upperRangeSlider.getValue();
            max = lowerRangeSlider.getValue();

        }
        if (!supportMultiRange || (supportMultiRange && !secoundBottopmLayout.isVisible())) {
            redrawRangeOnChart(min, max);
            selectedRange(min, max, !(max == lowerRangeSlider.getMax() && min == lowerRangeSlider.getMin()));
        } else {

            AbsoluteLayout.ComponentPosition secLowerPos = secoundSlidersContainer.getPosition(secoundLowerRangeSlider);
            AbsoluteLayout.ComponentPosition secUpperPos = secoundSlidersContainer.getPosition(secoundUpperRangeSlider);
            secoundSlidersContainer.removeAllComponents();
            double secMin;
            double secMax;

            if (secoundLowerRangeSlider.getValue() <= secoundUpperRangeSlider.getValue() && secoundSlidersContainer.getComponentCount() == 0) {
                secoundSlidersContainer.addComponent(secoundLowerRangeSlider, secLowerPos.getCSSString());
                secoundSlidersContainer.addComponent(secoundUpperRangeSlider, secUpperPos.getCSSString());
                secoundUpperRangeSlider.addStyleName("upper");
                secoundLowerRangeSlider.addStyleName("lower");
                secMin = secoundLowerRangeSlider.getValue();
                secMax = secoundUpperRangeSlider.getValue();

            } else {
                secoundSlidersContainer.addComponent(secoundUpperRangeSlider, secUpperPos.getCSSString());
                secoundSlidersContainer.addComponent(secoundLowerRangeSlider, secLowerPos.getCSSString());
                secoundLowerRangeSlider.addStyleName("upper");
                secoundUpperRangeSlider.addStyleName("lower");
                secMin = secoundUpperRangeSlider.getValue();
                secMax = secoundLowerRangeSlider.getValue();

            }

            Rectangle r1 = new Rectangle((int) min, 0, (int) max + 100, 10);
            Rectangle r2 = new Rectangle((int) secMin, 0, (int) secMax + 100, 10);
            if (((min == lowerRangeSlider.getMin() || secMin == lowerRangeSlider.getMin()) && (max == upperRangeSlider.getMax() || secMax == upperRangeSlider.getMax())) && r1.intersects(r2)) {
                redrawRangeOnChart(lowerRangeSlider.getMin(), upperRangeSlider.getMax());
                selectedRange(lowerRangeSlider.getMin(), upperRangeSlider.getMax(), false);
            } else if (r1.intersects(r2)) {
                min = Math.min(r1.x, r2.x);
                max = Math.max(r1.width - 100, r2.width - 100);
                redrawRangeOnChart(min, max);
                selectedRange(min, max, true);
            } else {
                redrawRangeOnChart(min, max, secMin, secMax);
                selectedRange(min, max, secMin, secMax, true);
            }

        }

    }

    private void redrawRangeOnChart(double min, double max) {
        if (scatterChartConfig.data().getDatasets().size() < 3) {
            List<ScatterData> updatedList = new ArrayList<>();
            for (double[] dArr : data) {
                ScatterData sd = new ScatterData();
                if (dArr[0] >= min && dArr[0] <= max) {
                    sd.y(dArr[1]);
                    sd.x(dArr[0]);
                    updatedList.add(sd);
                }
            }
            if (updatedList.size() == 1) {
                ScatterData sd = new ScatterData();
                sd.x(min);
                sd.y(0.0);
                updatedList.add(sd);
            }
            ScatterDataset hightLightDs = (new ScatterDataset().label("2").fill(true).dataAsList(updatedList).backgroundColor("#197de1"));
            scatterChartConfig.data().getDatasets().remove(1);
            scatterChartConfig.data().addDataset(hightLightDs);
        } else {
            boolean splitDataExist = false;
            List<ScatterData> scatterdataList = new ArrayList<>();
            List<ScatterData> lowerDataList = new ArrayList<>();
            List<ScatterData> splitDataList = new ArrayList<>();
            List<ScatterData> upperDataList = new ArrayList<>();
            for (double[] dArr : data) {
                ScatterData sd = new ScatterData();
                sd.x(dArr[0]);
                sd.y(dArr[1]);
                scatterdataList.add(sd);
                if (dArr[0] >= min && dArr[0] <= max) {
                    splitDataList.add(sd);
                    if (dArr[0] > splitDataPoint) {
                        upperDataList.add(sd);
                    } else if (dArr[0] < splitDataPoint) {
                        lowerDataList.add(sd);
                    } else {
                        splitDataExist = true;
                    }
                }
            }

            if (!splitDataExist) {
                splitDataList.clear();
            }
            if (lowerDataList.size() == 1) {
                ScatterData sd = new ScatterData();
                String[] arr = lowerDataList.get(0).toString().replace("\\[", "").replace("]", "").replace(" ", "").split(",");
                sd.x(Double.valueOf(arr[0].split("=")[1]) + 6.0);
                sd.y(Double.valueOf(arr[1].split("=")[1]));
                lowerDataList.add(sd);
            }
            if (splitDataList.size() == 1) {
                ScatterData sd = new ScatterData();
                String[] arr = splitDataList.get(0).toString().replace("\\[", "").replace("]", "").replace(" ", "").split(",");
                sd.x(Double.valueOf(arr[0].split("=")[1]) + 3.0);
                sd.y(Double.valueOf(arr[1].split("=")[1]));
                splitDataList.add(sd);
                ScatterData sd2 = new ScatterData();
                sd2.x(Double.valueOf(arr[0].split("=")[1]) - 3.0);
                sd2.y(Double.valueOf(arr[1].split("=")[1]));
                splitDataList.add(sd2);

            }
            if (upperDataList.size() == 1) {
                ScatterData sd = new ScatterData();
                String[] arr = upperDataList.get(0).toString().replace("\\[", "").replace("]", "").replace(" ", "").split(",");
                sd.x(Double.valueOf(arr[0].split("=")[1]) - 6.0);
                sd.y(Double.valueOf(arr[1].split("=")[1]));
                upperDataList.add(sd);
            }

            ScatterDataset ds = (new ScatterDataset().label("1").fill(false).dataAsList(scatterdataList).backgroundColor("lightgray"));
            ScatterDataset lowerHightLightDs = (new ScatterDataset().label("2").fill(true).dataAsList(lowerDataList).backgroundColor(colors[0]));
            ScatterDataset splitHightLightDs = (new ScatterDataset().label("3").fill(true).dataAsList(splitDataList).backgroundColor("rgb(154, 208, 248)"));
            ScatterDataset upperHightLightDs = (new ScatterDataset().label("4").fill(true).dataAsList(upperDataList).backgroundColor(colors[1]));

            scatterChartConfig.data().getDatasets().clear();
            scatterChartConfig.data().addDataset(ds);
            scatterChartConfig.data().addDataset(lowerHightLightDs);
            scatterChartConfig.data().addDataset(upperHightLightDs);
            scatterChartConfig.data().addDataset(splitHightLightDs);

        }
        chartContainer.removeAllComponents();
        ChartJs chart = new ChartJs(scatterChartConfig);
        chart.setWidth(100, Unit.PERCENTAGE);
        chart.setHeight(50, Unit.PERCENTAGE);
        chartContainer.addComponent(chart);
        chartContainer.setComponentAlignment(chart, Alignment.TOP_LEFT);
    }

    private void redrawRangeOnChart(double min, double max, double secMin, double secMax) {
        if (scatterChartConfig.data().getDatasets().size() < 3) {
            //need to implement
//            List<ScatterData> updatedList = new ArrayList<>();
//            for (double[] dArr : data) {
//                ScatterData sd = new ScatterData();
//                if (dArr[0] >= min && dArr[0] <= max) {
//                    sd.y(dArr[1]);
//                    sd.x(dArr[0]);
//                    updatedList.add(sd);
//                }
//            }
//            if (updatedList.size() == 1) {
//                ScatterData sd = new ScatterData();
//                sd.x(min);
//                sd.y(0.0);
//                updatedList.add(sd);
//            }
//            ScatterDataset hightLightDs = (new ScatterDataset().label("2").fill(true).dataAsList(updatedList).backgroundColor("#197de1"));
//            scatterChartConfig.data().getDatasets().remove(1);
//            scatterChartConfig.data().addDataset(hightLightDs);
        } else {
            boolean splitDataExist = false;
            List<ScatterData> scatterdataList = new ArrayList<>();
            List<ScatterData> lowerDataList = new ArrayList<>();
            List<ScatterData> splitDataList = new ArrayList<>();
            List<ScatterData> upperDataList = new ArrayList<>();
            for (double[] dArr : data) {
                ScatterData sd = new ScatterData();
                sd.x(dArr[0]);
                sd.y(dArr[1]);
                scatterdataList.add(sd);
                if ((dArr[0] >= min && dArr[0] <= max) || (dArr[0] >= secMin && dArr[0] <= secMax)) {
                    splitDataList.add(sd);
                    if (dArr[0] > splitDataPoint) {
                        upperDataList.add(sd);
                    } else if (dArr[0] < splitDataPoint) {
                        lowerDataList.add(sd);
                    } else {
                        splitDataExist = true;
                    }
                }
            }

            if (!splitDataExist) {
                splitDataList.clear();
            }
            if (lowerDataList.size() == 1) {
                ScatterData sd = new ScatterData();
                String[] arr = lowerDataList.get(0).toString().replace("\\[", "").replace("]", "").replace(" ", "").split(",");
                sd.x(Double.valueOf(arr[0].split("=")[1]) + 6.0);
                sd.y(Double.valueOf(arr[1].split("=")[1]));
                lowerDataList.add(sd);
            }
            if (splitDataList.size() == 1) {
                ScatterData sd = new ScatterData();
                String[] arr = splitDataList.get(0).toString().replace("\\[", "").replace("]", "").replace(" ", "").split(",");
                sd.x(Double.valueOf(arr[0].split("=")[1]) + 3.0);
                sd.y(Double.valueOf(arr[1].split("=")[1]));
                splitDataList.add(sd);
                ScatterData sd2 = new ScatterData();
                sd2.x(Double.valueOf(arr[0].split("=")[1]) - 3.0);
                sd2.y(Double.valueOf(arr[1].split("=")[1]));
                splitDataList.add(sd2);

            }
            if (upperDataList.size() == 1) {
                ScatterData sd = new ScatterData();
                String[] arr = upperDataList.get(0).toString().replace("\\[", "").replace("]", "").replace(" ", "").split(",");
                sd.x(Double.valueOf(arr[0].split("=")[1]) - 6.0);
                sd.y(Double.valueOf(arr[1].split("=")[1]));
                upperDataList.add(sd);
            }

            ScatterDataset ds = (new ScatterDataset().label("1").fill(false).dataAsList(scatterdataList).backgroundColor("lightgray"));
            ScatterDataset lowerHightLightDs = (new ScatterDataset().label("2").fill(true).dataAsList(lowerDataList).backgroundColor(colors[0]));
            ScatterDataset splitHightLightDs = (new ScatterDataset().label("3").fill(true).dataAsList(splitDataList).backgroundColor("rgb(154, 208, 248)"));
            ScatterDataset upperHightLightDs = (new ScatterDataset().label("4").fill(true).dataAsList(upperDataList).backgroundColor(colors[1]));

            scatterChartConfig.data().getDatasets().clear();
            scatterChartConfig.data().addDataset(ds);
            scatterChartConfig.data().addDataset(lowerHightLightDs);
            scatterChartConfig.data().addDataset(upperHightLightDs);
            scatterChartConfig.data().addDataset(splitHightLightDs);

        }
        chartContainer.removeAllComponents();
        ChartJs chart = new ChartJs(scatterChartConfig);
        chart.setWidth(100, Unit.PERCENTAGE);
        chart.setHeight(50, Unit.PERCENTAGE);
        chartContainer.addComponent(chart);
        chartContainer.setComponentAlignment(chart, Alignment.TOP_LEFT);
    }

    public abstract void selectedRange(double min, double max, boolean filterApplied);

    public abstract void selectedRange(double min, double max, double secMin, double secMax, boolean filterApplied);

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        valueChange();
    }

}
