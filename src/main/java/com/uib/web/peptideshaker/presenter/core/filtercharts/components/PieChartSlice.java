package com.uib.web.peptideshaker.presenter.core.filtercharts.components;

import java.awt.Color;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * This class contains information required for pie-chart slice(working with pie
 * chart interactive pie-chart filter)
 *
 * @author Yehia Farag
 */
public class PieChartSlice implements Serializable {

    /**
     * The slice identification label.
     */
    private Comparable label;
    /**
     * The slice AWT Color required by JFreechart.
     */
    private Color color;
    /**
     * Set of included dataset indexes.
     */
    private final Set<Comparable> itemsIds = new HashSet<>();
    
    private int totalValue; 

    public int getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(int totalValue) {
        this.totalValue = totalValue;
    }

    /**
     * Get the slice identification label
     *
     * @return label for the section
     */
    public Comparable getLabel() {
        return label;
    }

    /**
     * Set the slice identification label
     *
     * @param label Label for the section
     */
    public void setLabel(Comparable label) {
        this.label = label;
    }

    /**
     * Get number of included dataset indexes
     *
     * @return dataset indexes set size
     */
    public int getValue() {
        return itemsIds.size();
    }

    /**
     * Get the slice AWT Color required by JFreechart
     *
     * @return color AWT color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Set the slice AWT Color required by JFreechart
     *
     * @param color AWT color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Get set of included dataset indexes
     *
     * @return set of dataset indexes
     */
    public Set<Comparable> getItemsIds() {
        return itemsIds;
    }

    /**
     * Add dataset indexes to et of included dataset indexes
     *
     * @param datasetId Quant dataset index
     */
    public void addItemId(int datasetId) {
        this.itemsIds.add(datasetId);
    }
}
