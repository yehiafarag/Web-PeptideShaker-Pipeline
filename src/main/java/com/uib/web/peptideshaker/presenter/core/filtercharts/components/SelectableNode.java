package com.uib.web.peptideshaker.presenter.core.filtercharts.components;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.VerticalLayout;

/**
 * This class represents selectable node that used mainly in
 * MatrixLayoutChartFilter
 *
 * @author Yehia Farag
 */
public class SelectableNode extends VerticalLayout {

    public SelectableNode(boolean selectable, boolean upperSelected, boolean lowerSelected) {
        SelectableNode.this.setWidth(100, Unit.PERCENTAGE);
        SelectableNode.this.setHeight(100, Unit.PERCENTAGE);
        if (selectable) {
            SelectableNode.this.setStyleName("selectablebubble");
        } else {
            SelectableNode.this.setStyleName("lightgraybubble");   
            return;        
        }

        VerticalLayout upperLine = new VerticalLayout();
        upperLine.setHeight(105, Unit.PERCENTAGE);
        upperLine.setWidth(10, Unit.PERCENTAGE);
        if (upperSelected) {
            upperLine.setStyleName("selectednodeline");
        } else {
            upperLine.setStyleName("unselectednodeline");
         
        }
        SelectableNode.this.addComponent(upperLine);
        SelectableNode.this.setComponentAlignment(upperLine, Alignment.TOP_CENTER);

        VerticalLayout lowerLine = new VerticalLayout();
        lowerLine.setHeight(105, Unit.PERCENTAGE);
        lowerLine.setWidth(10, Unit.PERCENTAGE);
        if (lowerSelected) {
            lowerLine.setStyleName("selectednodeline");
        } else {
            lowerLine.setStyleName("unselectednodeline");
        }
        SelectableNode.this.addComponent(lowerLine);
        SelectableNode.this.setComponentAlignment(lowerLine, Alignment.TOP_CENTER);
    }

}
