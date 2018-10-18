/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Label;

/**
 *
 * @author y-mok
 */
public class IconDesign extends AbsoluteLayout{

    public IconDesign() {
        this.setWidth(120,Unit.PIXELS);
        this.setHeight(120,Unit.PIXELS);
        this.addStyleName("designicon");
        
        Label l1  = new Label(VaadinIcons.SPLINE_AREA_CHART.getHtml(), ContentMode.HTML);
        l1.setSizeFull();
        this.addComponent(l1);
        
        Label l2  = new Label(VaadinIcons.LIFEBUOY.getHtml(), ContentMode.HTML);
        l2.setSizeFull();
        l2.addStyleName("lb");
        this.addComponent(l2);
    }
    
    
}
