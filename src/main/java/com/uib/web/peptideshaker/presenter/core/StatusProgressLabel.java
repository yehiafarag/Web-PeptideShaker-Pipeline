/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Image;

/**
 * This class represent status label
 *
 * @author Yehia Farag
 */
public class StatusProgressLabel extends Image {

    private final Resource notActive =new ThemeResource("img/check-button.png");
    private final Resource ok = new ThemeResource("img/check-circle.png");
    private final Resource notValid = new ThemeResource("img/close-circle.png");
    private final Resource processing = new ThemeResource("img/loading.gif");
    private int status;

    public StatusProgressLabel() {
        StatusProgressLabel.this.setSource(notActive);
        StatusProgressLabel.this.setWidth(20, Unit.PIXELS);
        StatusProgressLabel.this.setHeight(20, Unit.PIXELS);
//         StatusLabel.this.setStyleName("actionlabel");
StatusProgressLabel.this.addStyleName("inactive");
    }

    public void setStatus(String status) {
        StatusProgressLabel.this.removeStyleName("inactive");
        StatusProgressLabel.this.removeStyleName("active");
        status=status+"";
//        StatusProgressLabel.this.setWidth(16, Unit.PIXELS);
         StatusProgressLabel.this.setDescription((status+"").toUpperCase());
        if (status.equalsIgnoreCase("ok")) {
            StatusProgressLabel.this.setSource(notActive);
             StatusProgressLabel.this.addStyleName("active");
            this.status = 0;
        } else if (status.equalsIgnoreCase("running")) {
            StatusProgressLabel.this.setSource(processing);
//            StatusProgressLabel.this.setWidth(100, Unit.PIXELS);
            this.status = 1;
        } else {
            StatusProgressLabel.this.setSource(notValid);
            this.status = 2;
        }

    }

    public int getStatus() {
        return status;
    }

}
