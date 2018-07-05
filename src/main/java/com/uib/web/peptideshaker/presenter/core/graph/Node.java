/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.presenter.core.graph;

import com.compomics.util.experiment.biology.PTMFactory;
import com.vaadin.event.LayoutEvents;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents Node for graph layout
 *
 * @author Yehia Farag
 */
public abstract class Node extends VerticalLayout implements LayoutEvents.LayoutClickListener {

    private final String nodeId;
    private final VerticalLayout modificationLayout;
     private boolean selected;
    private String defaultStyleName;
    private String validationStatuesStyle;
    private String proteinEvidenceStyle;
    private final String modificationStyleName ="nodemodificationbackground";
    private int edgesNumber;

    /**
     * The post translational modifications factory.
     */
    private final PTMFactory PTM = PTMFactory.getInstance();

    public Node(String id, String modifications, String sequence) {
        Node.this.setWidth(20, Unit.PIXELS);
        Node.this.setHeight(20, Unit.PIXELS);
        Node.this.setStyleName("node");
        Node.this.addLayoutClickListener(Node.this);
        this.nodeId = id;
        this.modificationLayout = new VerticalLayout();
        this.modificationLayout.setSizeFull();
        Node.this.addComponent(modificationLayout);
        String subTooltip = "";
        Map<String, String> modificationsTooltip = new HashMap<>();
        for (String mod : modifications.split(",")) {
            if (mod.trim().equalsIgnoreCase("") || mod.contains("Pyrolidone") || mod.contains("Acetylation of protein N-term")) {
                continue;
            }
            String[] tmod = mod.replace("(", "__").split("__");
            Color c = PTMFactory.getDefaultColor(tmod[0].trim());
            Label modification = new Label("<div  style='background:rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ");border-radius:100%;width: 100%;height: 100%;opacity:0.2;'></div>", ContentMode.HTML);
            modification.setSizeFull();
            modificationLayout.addComponent(modification);
            int i = Integer.valueOf(tmod[1].trim().replace(")", "")) - 1;
            modificationsTooltip.put(sequence.charAt(i) + "<" + PTM.getPTM(tmod[0].trim()).getShortName() + ">", "<font style='background-color:rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")'>" + sequence.charAt(i) + "</font>");
            subTooltip += "</br><span style='width:20px;height:10px;background-color:rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")'>" + sequence.charAt(i) + "</span> - " + mod;

        }
        if (modificationLayout.getComponentCount() > 1) {
            modificationLayout.removeAllComponents();
            Label modification = new Label("<div style='background:orange; width:100%;height:100%;border-radius:100%;opacity:0.2;'></div>", ContentMode.HTML);
            modification.setSizeFull();
            modificationLayout.addComponent(modification);
        }
        String tooltip = nodeId;
        for (String key : modificationsTooltip.keySet()) {
            tooltip = tooltip.replace(key, modificationsTooltip.get(key));
        }
        tooltip += subTooltip;
        modificationLayout.setVisible(false);
        Node.this.setDescription(tooltip);
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getProteinEvidenceStyle() {
        return proteinEvidenceStyle;
    }

    public void setProteinEvidenceStyle(String proteinEvidenceStyle) {
        this.proteinEvidenceStyle = proteinEvidenceStyle;
    }

    public int getEdgesNumber() {
        return edgesNumber;
    }

    public void addEdge() {
        this.edgesNumber++;
    }
   
    public int getType() {
        return type;
    }

    public void setNodeStatues(String statues) {
        this.resetStyle();
        if (statues.equalsIgnoreCase("Molecule Type")) {
            this.addStyleName(defaultStyleName);
        } else if (statues.equalsIgnoreCase("Validation Status")) {
            this.addStyleName(validationStatuesStyle);
        } else if (statues.equalsIgnoreCase("Protein Evidence")) {
            this.addStyleName(proteinEvidenceStyle);
        } else if (statues.equalsIgnoreCase("Modification  Status")) {
            modificationLayout.setVisible(true);
            this.addStyleName(modificationStyleName);
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
        this.modificationLayout.setVisible(false);
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

    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        selected(nodeId);
    }

    public abstract void selected(String id);

 

}
