/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.presenter.core.graph;

import com.vaadin.event.LayoutEvents;
import com.vaadin.ui.VerticalLayout;

/**
 * This class represents Node for graph layout
 *
 * @author Yehia Farag
 */
public abstract class Node extends VerticalLayout implements LayoutEvents.LayoutClickListener {

    private final String nodeId;

    public String getNodeId() {
        return nodeId;
    }

    public String getProteinEvidenceStyle() {
        return proteinEvidenceStyle;
    }

    public void setProteinEvidenceStyle(String proteinEvidenceStyle) {
        this.proteinEvidenceStyle = proteinEvidenceStyle;
    }
    private boolean selected;
    private String defaultStyleName;

    public int getEdgesNumber() {
        return edgesNumber;
    }

    public void addEdge() {
        this.edgesNumber++;
    }
    private String validationStatuesStyle;
    private String proteinEvidenceStyle;
    private int edgesNumber;

    public int getType() {
        return type;
    }
    public void setNodeStatues(String statues){
        this.resetStyle();
        if(statues.equalsIgnoreCase("Validation Status")){
        this.addStyleName(validationStatuesStyle);
        }else if(statues.equalsIgnoreCase("Protein Evidence")){
         this.addStyleName(proteinEvidenceStyle);
        }   
    
    }

    public String getValidationStatuesStyle() {
        return validationStatuesStyle;
    }

    public void setValidationStatuesStyle(String validationStatuesStyle) {
        this.validationStatuesStyle = validationStatuesStyle;
    }

    public String getDefaultStyleName() {
        return defaultStyleName;
    }

    public void setDefaultStyleName(String defaultStyleName) {
        this.setStyleName(defaultStyleName);
        this.defaultStyleName = defaultStyleName;
    }

    public void resetStyle() {
        this.removeStyleName(this.getStyleName());
        this.setStyleName(defaultStyleName);
        this.setSelected(selected);
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
        this.nodeId = id;
    }

    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        selected(nodeId);
    }

    public abstract void selected(String id);

}
