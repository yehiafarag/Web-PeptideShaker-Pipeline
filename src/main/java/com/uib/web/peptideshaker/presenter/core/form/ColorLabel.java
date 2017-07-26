package com.uib.web.peptideshaker.presenter.core.form;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import java.awt.Color;

/**
 * This class represents color label
 *
 * @author Yehia Farag
 */
public class ColorLabel extends Label {

    /**
     * Constructor to initialize the label color
     */
    public ColorLabel(Color color) {
        ColorLabel.this.setContentMode(ContentMode.HTML);
        ColorLabel.this.setWidth(100, Unit.PERCENTAGE);
        ColorLabel.this.setHeight(15, Unit.PIXELS);
        ColorLabel.this.setValue("<div style='background:rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ");width: 100% !important;height: 100%!important;'></div>");
    }

    public void updateColor(Color newColor) {
        ColorLabel.this.setValue("<div style='background:rgb(" + newColor.getRed() + "," + newColor.getGreen() + "," + newColor.getBlue() + ");width: 100% !important;height: 100%!important;'></div>");

    }

}
