/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.server.Resource;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Image;

/**
 * This class represent status label
 *
 * @author Yehia Farag
 */
public class StatusLabel extends Image {

    private final Resource ok = new ThemeResource("img/check-circle.png");
    private final Resource notValid = new ThemeResource("img/close-circle.png");
    private final Resource processing = new ThemeResource("img/indeterminateprogress.gif");
    private int status;

    public StatusLabel() {
        StatusLabel.this.setSource(processing);
        StatusLabel.this.setWidth(16, Unit.PIXELS);
        StatusLabel.this.setHeight(16, Unit.PIXELS);
//         StatusLabel.this.setStyleName("actionlabel");
    }

    public void setStatus(String status) {
        status=status+"";
        StatusLabel.this.setWidth(16, Unit.PIXELS);
         StatusLabel.this.setDescription((status+"").toUpperCase());
        if (status.equalsIgnoreCase("ok")) {
            StatusLabel.this.setSource(ok);
            this.status = 0;
        } else if (status.equalsIgnoreCase("new") || status.equalsIgnoreCase("running")|| status.equalsIgnoreCase("queued")) {
            StatusLabel.this.setSource(processing);
            StatusLabel.this.setWidth(100, Unit.PIXELS);
            this.status = 1;
        } else {
            StatusLabel.this.setSource(notValid);
            this.status = 2;
        }

    }

    public int getStatus() {
        return status;
    }

}
