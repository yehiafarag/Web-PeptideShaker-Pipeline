package com.uib.web.peptideshaker.presenter.core.filtercharts.components;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import java.awt.Color;
import java.awt.GradientPaint;

/**
 * This class is responsible for generating Color Gradient as HTML hashed color
 * code for the heat map
 *
 * @author Yehia Farag
 */
public class RangeColorGenerator {
    
    private final double max;
    private final Color lowerColor = new Color(13, 99, 196);
    private final Color upperColor = new Color(207, 227, 249);
    
     private final Color lowerColor2 = new Color(0, 77, 122);
    private final Color upperColor2 = new Color(201, 236, 255);
    private final HorizontalLayout colorScale;

    /**
     * Constructor to initialize the main attributes
     *
     * @param max the maximum value
     */
    public RangeColorGenerator(double max) {
        
        this.max = max;
        colorScale = new HorizontalLayout();
        
        colorScale.setHeight(20, Unit.PIXELS);
        colorScale.setWidth(100, Unit.PERCENTAGE);
        colorScale.setStyleName("stacked");
        colorScale.addStyleName("colorscale");
        Label l = new Label("<center style= 'margin-left:-10px;font-size: 1vmin;width:15px !important; height:15px !important;'>0</center>", ContentMode.HTML);
        l.setSizeFull();
        colorScale.addComponent(l);
        for (double x = 0; x < 50; x++) {
            l = new Label("<center style= ' margin-top: 5px;background-color: " + RangeColorGenerator.this.getColor(x * 0.02 * max) + ";width:100% !important; height:5px !important;'></center>", ContentMode.HTML);
            l.setWidth(100, Unit.PERCENTAGE);
            l.setHeight(5, Unit.PIXELS);
            l.setStyleName(ValoTheme.LABEL_NO_MARGIN);
            colorScale.addComponent(l);
            colorScale.setComponentAlignment(l, Alignment.TOP_CENTER);
            
        }
        l = new Label("<center style= 'font-size: 1vmin;width:25px !important; height:15px !important;'>" + (int) max + "</center>", ContentMode.HTML);
        l.setSizeFull();
        colorScale.addComponent(l);
        
    }
    
    public HorizontalLayout getColorScale() {
        return colorScale;
    }

    /**
     * Get the color for the input value
     *
     * @param value double value to be converted to color
     * @return HTML hashed color for the input value
     */
    public String getColor(double value) {
        if (value < 1) {
            return "RGB(" + 245 + "," + 245 + "," + 245 + ")";
        }
        double n = (value) / max;
        
        double R1 = lowerColor.getRed() * n + upperColor.getRed() * (1 - n);
        double G1 = lowerColor.getGreen() * n + upperColor.getGreen() * (1 - n);
        double B1 = lowerColor.getBlue() * n + upperColor.getBlue() * (1 - n);
        
        String rgb = "RGB(" + (int) R1 + "," + (int) G1 + "," + (int) B1 + ")";
        return rgb;
    }
    
    public String getGradeColor(double value, double max, double min) {
        if (value==0.0) {
            return "RGB(" + 255 + "," + 255 + "," + 255 + ")";
        }
        double n = (value) / max;        
        double R1 = lowerColor2.getRed() * n + upperColor2.getRed() * (1 - n);
        double G1 = lowerColor2.getGreen() * n + upperColor2.getGreen() * (1 - n);
        double B1 = lowerColor2.getBlue() * n + upperColor2.getBlue() * (1 - n);
        
        String rgb = "RGB(" + (int) R1 + "," + (int) G1 + "," + (int) B1 + ")";
        return rgb;
    }

    private double scaleValues(double linearValue, double max, double lowerLimit) {
        double logMax = (Math.log(max) / Math.log(2));
        double logValue = (Math.log(linearValue) / Math.log(2));
        logValue = ((max / logMax) * logValue) + lowerLimit;//(max/Math.log(max))*Math.log(linearValue)+10;
        return logValue;
    }
}
