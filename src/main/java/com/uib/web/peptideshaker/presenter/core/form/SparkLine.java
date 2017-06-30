package com.uib.web.peptideshaker.presenter.core.form;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import java.awt.Color;
import java.text.DecimalFormat;

/**
 * This class represent sparkLine
 *
 * @author Yehia Farag
 */
public class SparkLine extends HorizontalLayout {

    private final DecimalFormat df = new DecimalFormat("#.##");

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

        ColorLabel spark = new ColorLabel(selectedColor);
        spark.setHeight(60, Unit.PERCENTAGE);
        spark.setWidth(Math.min(Math.max(Math.round(value),5),100), Unit.PERCENTAGE);
        SparkLine.this.addComponent(spark);
        SparkLine.this.setComponentAlignment(spark, Alignment.BOTTOM_LEFT);

    }

}
