/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.presenter.core.graph;

/**
 * This class represents edge in the developed graph
 *
 * @author Yehia Farag
 */
public class Edge {

    private final Node n1;
    private final Node n2;
    private final boolean dotted;
    private boolean hide;

    public Edge(Node n1, Node n2, boolean dotted) {
        this.n1 = n1;
        this.n2 = n2;
        this.dotted = dotted;
    }

    public boolean isSelected() {
        return (n1.isSelected() && n2.isSelected());
    }

    public void select(Node n, boolean uniqueOnly) {
        if (n == n2) {
            n2.setSelected(true);
            if (uniqueOnly && n1.getType() == 1 && n1.getEdgesNumber() == 1 || !uniqueOnly) {
                n1.setSelected(true);
            } else if (n1.getType() == 0) {
                n1.setSelected(true);
            }
        } else if (n == n1) {
            n1.setSelected(true);
            if (uniqueOnly && n2.getEdgesNumber() == 1 && n2.getType() == 1 || !uniqueOnly) {
                n2.setSelected(true);
            } else if (n2.getType() == 0) {
                n2.setSelected(true);
            }
        }
    }

    public double getStartX() {
        return n1.getX() + 8;
    }

    public double getStartY() {
        return n1.getY() + 8;
    }

    public double getEndX() {
        return n2.getX() + 15;
    }

    public double getEndY() {
        return n2.getY() + 15;
    }

    public Node getN1() {
        return n1;
    }

    public Node getN2() {
        return n2;
    }

    public boolean isDotted() {
        return dotted;
    }

    public boolean isHide() {
        return (n1.getStyleName().contains("nodedisabled") || n2.getStyleName().contains("nodedisabled"));
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public boolean isBelongToNode(String nodeId) {

        return n1.getNodeId().equalsIgnoreCase(nodeId) || n2.getNodeId().equalsIgnoreCase(nodeId);
    }

}
