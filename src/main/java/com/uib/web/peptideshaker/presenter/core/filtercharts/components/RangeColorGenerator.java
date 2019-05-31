package com.uib.web.peptideshaker.presenter.core.filtercharts.components;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

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

    private final Color lowerColor2 = new Color(0, 25, 51);
    private final Color upperColor2 = new Color(230, 242, 255);
    private final HorizontalLayout colorScale;
    private final Map<Double, String> colorCategories;

    /**
     * Constructor to initialise the main attributes
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

        colorCategories = new HashMap<>();
        colorCategories.put(20.0, "rgb(230,242,255)");
        colorCategories.put(40.0, "rgb(153,197,255)");
        colorCategories.put(60.0, "rgb(77,154,255)");
        colorCategories.put(80.0, "rgb(0,110,255)");
        colorCategories.put(100.0, "rgb(0,77,179)");
        colorCategories.put(1000.0, "rgb(0,44,102)");

    }

    /**
     * Get vertical grade scale indicator.
     *
     * @param gradeScale grade scale or colour scale
     * @return grade scale layout
     */
    public VerticalLayout getVerticalScale(boolean gradeScale) {

        VerticalLayout scal = new VerticalLayout();
        scal.setSizeFull();
        scal.setStyleName("verticalgradescal");
        Label l = new Label("<div>Max</div>", ContentMode.HTML);
        l.setSizeFull();
        scal.addComponent(l);
        for (double x = 70; x > 0; x--) {
            if (gradeScale) {
                l = new Label("<center style= ' background-color: " + RangeColorGenerator.this.getGradeColor(x * 0.02 * max, max, 0) + ";'></center>", ContentMode.HTML);
            } else {
                l = new Label("<center style= ' background-color: " + RangeColorGenerator.this.getColor(x * 0.02 * max) + ";'></center>", ContentMode.HTML);
            }
            l.setSizeFull();
            l.setStyleName(ValoTheme.LABEL_NO_MARGIN);
            scal.addComponent(l);
            scal.setComponentAlignment(l, Alignment.TOP_CENTER);

        }
        l = new Label("<div >Min</div>", ContentMode.HTML);
        l.setSizeFull();
        scal.addComponent(l);
        return scal;

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

        if (value == 0.0) {
            return "RGB(" + 245 + "," + 245 + "," + 245 + ")";
        }
        value = value / max;
//        int counter
        if (value <= 0.2) {
            return colorCategories.get(20.0);
        }
        if (value / max <= 0.40) {
            return colorCategories.get(40.0);
        }
        if (value / max <= 0.60) {
            return colorCategories.get(60.0);
        }
        if (value / max <= 0.8) {

            return colorCategories.get(80.0);
        }
        if (value / max <= 1) {

            return colorCategories.get(100.0);
        }
        if (value / max > 1) {

            return colorCategories.get(1000.0);
        }

        double n = (value) / max;
        double R1 = lowerColor2.getRed() * n + upperColor2.getRed() * (1 - n);
        double G1 = lowerColor2.getGreen() * n + upperColor2.getGreen() * (1 - n);
        double B1 = lowerColor2.getBlue() * n + upperColor2.getBlue() * (1 - n);
        String rgb = "RGB(" + (int) R1 + "," + (int) G1 + "," + (int) B1 + ")";
        return rgb;

//        if (value == 0.0) {
//            return "RGB(" + 245 + "," + 245 + "," + 245 + ")";
//        }
//        double n = (value) / max;
//        double R1 = lowerColor2.getRed() * n + upperColor2.getRed() * (1 - n);
//        double G1 = lowerColor2.getGreen() * n + upperColor2.getGreen() * (1 - n);
//        double B1 = lowerColor2.getBlue() * n + upperColor2.getBlue() * (1 - n);
//        String rgb = "RGB(" + (int) R1 + "," + (int) G1 + "," + (int) B1 + ")";
//        return rgb;
    }

}
