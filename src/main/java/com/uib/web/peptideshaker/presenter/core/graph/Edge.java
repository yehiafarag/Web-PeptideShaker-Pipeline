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

    public Edge(Node n1, Node n2) {
        this.n1 = n1;
        this.n2 = n2;
    }

    public boolean isSelected() {
        return (n1.isSelected() && n2.isSelected());
    }

    public void select(Node n) {
        if (n == n1 || n == n2) {
            n1.setSelected(true);
            n2.setSelected(true);
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

}
