package com.uib.web.peptideshaker.presenter.core.form;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import java.awt.Color;
import java.text.DecimalFormat;
import org.jfree.chart.ChartColor;

/**
 * This class represent sparkLine
 *
 * @author Yehia Farag
 */
public class SparkLine extends HorizontalLayout {

    private final DecimalFormat df = new DecimalFormat("#.##");
    private ColorLabel spark;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (spark == null) {
            return;
        }
        if (selected) {
            spark.updateColor(new Color(25, 125, 225));
        } else {
            spark.updateColor(Color.lightGray);
        }

    }
    private boolean selected;

    /**
     * Constructor to initialize the spark-line class
     */
    public SparkLine(double value, double min, double max) {

        SparkLine.this.setWidth(100, Unit.PERCENTAGE);
        SparkLine.this.setHeight(10, Unit.PIXELS);
        Label label = new Label(df.format(value));
        label.setStyleName("smalltable");
        SparkLine.this.setSpacing(true);
        SparkLine.this.addComponent(label);
        SparkLine.this.setComponentAlignment(label, Alignment.TOP_LEFT);
        Color selectedColor;

        double factor = Math.max(max, Math.abs(min));
        factor = Math.log10((Double) factor);
        if (value > 0) {
            value = Math.log10((Double) value);

            value = value * 100 / factor;
            selectedColor = Color.RED;
        } else if (value < 0) {
            value = Math.log10((Double) value * -1.0);
            value = value * 100 / factor;
            selectedColor = Color.BLUE;
        } else {
            return;
        }

        spark = new ColorLabel(selectedColor);
        spark.setHeight(60, Unit.PERCENTAGE);
        spark.setWidth(Math.min(Math.max(Math.round(value), 5), 100), Unit.PERCENTAGE);
        SparkLine.this.addComponent(spark);
        SparkLine.this.setComponentAlignment(spark, Alignment.TOP_LEFT);

    }

    /**
     * Constructor to initialize the spark-line class
     */
    public SparkLine(String textLabel, double value, double min, double max, Color color) {

        SparkLine.this.setWidth(100, Unit.PERCENTAGE);
        SparkLine.this.setHeight(10, Unit.PIXELS);
        Label label = new Label(textLabel);
        label.setWidth(100, Unit.PERCENTAGE);
        label.setStyleName("smalltable");
        SparkLine.this.setSpacing(true);

        Color selectedColor;

        double factor = Math.max(max, Math.abs(min));
        factor = Math.log10((Double) factor);
        if (value > 0) {
            value = Math.log10((Double) value);
            value = value * 100 / factor;
            selectedColor = Color.RED;
        } else if (value < 0) {
            value = Math.log10((Double) value * -1.0);
            value = value * 100 / factor;
            selectedColor = Color.BLUE;
        } else {
            SparkLine.this.addComponent(new Label());
            SparkLine.this.addComponent(label);
            SparkLine.this.setComponentAlignment(label, Alignment.TOP_LEFT);
            return;
        }

        spark = new ColorLabel(selectedColor);
        spark.setHeight(60, Unit.PERCENTAGE);
        spark.addStyleName("margintop5");
        spark.setWidth(Math.min(Math.max(Math.round(value), 5), 100), Unit.PERCENTAGE);
        SparkLine.this.addComponent(spark);
        SparkLine.this.setComponentAlignment(spark, Alignment.TOP_RIGHT);
        SparkLine.this.setExpandRatio(spark, 50);
        SparkLine.this.addComponent(label);
        SparkLine.this.setComponentAlignment(label, Alignment.TOP_LEFT);
        SparkLine.this.setExpandRatio(label, 50);
        Label colorLegend = new Label("<div style='width: 13px;height: 13px;margin-top: 3px;border: 1px solid lightgray;background: rgb("+color.getRed()+","+color.getGreen()+","+color.getBlue()+");'></div>");
        colorLegend.setContentMode(ContentMode.HTML);
        colorLegend.setWidthUndefined();
        colorLegend.setHeightUndefined();
        
        SparkLine.this.addComponent(colorLegend);
        SparkLine.this.setComponentAlignment(colorLegend, Alignment.TOP_LEFT);
        SparkLine.this.setExpandRatio(colorLegend, 1);
    }

}
