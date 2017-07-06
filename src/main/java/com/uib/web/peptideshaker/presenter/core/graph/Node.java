/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.presenter.core.graph;

import com.vaadin.event.LayoutEvents;
import com.vaadin.ui.VerticalLayout;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents Node for graph layout
 *
 * @author Yehia Farag
 */
public abstract class Node extends VerticalLayout implements LayoutEvents.LayoutClickListener {

    private final String id;
    private boolean selected;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    private int type;

    public double getX() {
        return x;
    }

   

    
    

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
    private double x;
    private double y;

    public boolean isSelected() {

        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            this.addStyleName("selectednode");
        } else {
            this.removeStyleName("selectednode");
        }

    }

    public Node(String id) {
        Node.this.setWidth(20, Unit.PIXELS);
        Node.this.setHeight(20, Unit.PIXELS);
        Node.this.setStyleName("node");
        Node.this.addLayoutClickListener(Node.this);
        this.id = id;
    }

    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        selected(id);
    }

    public abstract void selected(String id);

}
