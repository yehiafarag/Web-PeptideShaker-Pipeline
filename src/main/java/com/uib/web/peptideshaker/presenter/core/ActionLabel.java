/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.event.LayoutEvents;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.themes.ValoTheme;

/**
 * This class represents Label with link that support action
 *
 * @author Yehia Farag
 */
public abstract class ActionLabel extends HorizontalLayout implements LayoutEvents.LayoutClickListener {

    public ActionLabel(String value) {
        ActionLabel.this.setSizeFull();
        Label label = new Label(value);
        ActionLabel.this.setStyleName("actionlabel");
//        label.setDescription(value);
        ActionLabel.this.setDescription(value);
        label.setSizeFull();
        label.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        ActionLabel.this.addComponent(label);
        ActionLabel.this.addLayoutClickListener(ActionLabel.this);

    }

    public ActionLabel(Resource icon, String description, Resource url) {
        ActionLabel.this.setSizeUndefined();
        Link tLabel = new Link();
        tLabel.setResource(url);
        ActionLabel.this.setStyleName("actionlabel");
        ActionLabel.this.setDescription(description);
//        label.setSizeFull();
        tLabel.setIcon(icon);
        tLabel.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        tLabel.setDescription(description);
        tLabel.setTargetName("_blank");
        tLabel.setStyleName(ValoTheme.LINK_SMALL);
        ActionLabel.this.addComponent(tLabel);
        ActionLabel.this.setComponentAlignment(tLabel, Alignment.TOP_CENTER);
//        ActionLabel.this.addLayoutClickListener(ActionLabel.this);

    }

    public ActionLabel(String description, Resource url) {
        ActionLabel.this.setSizeUndefined();
        Link label = new Link();
        label.setResource(url);
        label.setCaption(description);
        ActionLabel.this.setStyleName("actionlabel");
        ActionLabel.this.setDescription(description);
        label.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        label.setDescription(description);
        label.setTargetName("_blank");
        label.setStyleName(ValoTheme.LINK_SMALL);
        ActionLabel.this.addComponent(label);
        ActionLabel.this.setComponentAlignment(label, Alignment.TOP_CENTER);

    }
    private Label label;

    public ActionLabel(Resource icon, String description) {
        ActionLabel.this.setSizeUndefined();
        label = new Label();
        ActionLabel.this.setStyleName("actionlabel");
        ActionLabel.this.setDescription(description);
//        label.setSizeFull();
        label.setIcon(icon);
        label.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        label.setDescription(description);
        ActionLabel.this.addComponent(label);
        ActionLabel.this.setComponentAlignment(label, Alignment.TOP_CENTER);
        ActionLabel.this.addLayoutClickListener(ActionLabel.this);

    }

    public ActionLabel(FontAwesome icon, String description) {
        ActionLabel.this.setSizeUndefined();
        label = new Label();
        ActionLabel.this.setStyleName("actionlabel");
        ActionLabel.this.setDescription(description);
//        label.setSizeFull();
        label.setIcon(icon);
        label.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        label.setDescription(description);
        ActionLabel.this.addComponent(label);
        ActionLabel.this.setComponentAlignment(label, Alignment.TOP_CENTER);
        ActionLabel.this.addLayoutClickListener(ActionLabel.this);

    }

    public ActionLabel(FontAwesome icon, Resource icon_2, String description) {
        ActionLabel.this.setSizeUndefined();
        label = new Label(icon.getHtml(), ContentMode.HTML);
        ActionLabel.this.setStyleName("actionlabel");
        ActionLabel.this.setDescription(description);
//        label.setSizeFull();
        label.setIcon(icon_2);
        label.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        label.setDescription(description);
        ActionLabel.this.addComponent(label);
        ActionLabel.this.setComponentAlignment(label, Alignment.TOP_CENTER);
        ActionLabel.this.addLayoutClickListener(ActionLabel.this);

    }
    public ActionLabel(String text, String description) {
        ActionLabel.this.setSizeUndefined();
        label = new Label(text);
        ActionLabel.this.setStyleName("actionlabel");
        ActionLabel.this.setDescription(description);
//        label.setSizeFull();
        label.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        label.setDescription(description);
        ActionLabel.this.addComponent(label);
        ActionLabel.this.setComponentAlignment(label, Alignment.TOP_CENTER);
        ActionLabel.this.addLayoutClickListener(ActionLabel.this);

    }

    public ActionLabel(Resource icon, String name, String description) {
        ActionLabel.this.setSizeFull();
        ActionLabel.this.setDescription(description);
        HorizontalLayout wrapper = new HorizontalLayout();
        wrapper.setWidthUndefined();
        wrapper.setHeight(100, Unit.PERCENTAGE);
        wrapper.setStyleName("actionlabel");
        wrapper.setSpacing(true);
        ActionLabel.this.addComponent(wrapper);
        Label iconLabel = new Label();
        iconLabel.setSizeFull();
        iconLabel.setIcon(icon);
        wrapper.addComponent(iconLabel);
        wrapper.setComponentAlignment(iconLabel, Alignment.MIDDLE_LEFT);
        label = new Label(name);
        label.setSizeFull();
        wrapper.addComponent(label);       
        ActionLabel.this.addLayoutClickListener(ActionLabel.this);

    }

}
