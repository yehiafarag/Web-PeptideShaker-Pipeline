package com.uib.web.peptideshaker.presenter.core.filtercharts.components;

import com.vaadin.event.LayoutEvents;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.VerticalLayout;

/**
 * This class represents selectable node that used mainly in
 * MatrixLayoutChartFilter
 *
 * @author Yehia Farag
 */
public abstract class SelectableNode extends VerticalLayout implements LayoutEvents.LayoutClickListener {

    private boolean upperSelected;
    private boolean lowerSelected;
    private boolean selecatble;
    private boolean disables;
    private final VerticalLayout upperLine;
    private final VerticalLayout lowerLine;
    private final String nodeId;
    private boolean selected;
    private final int columnIndex;

    public String getNodeId() {
        return nodeId;
    }

    public SelectableNode(String nodeId, int columnIndex, boolean disables) {
        this.nodeId = nodeId;
        this.columnIndex = columnIndex;
        SelectableNode.this.setWidth(100, Unit.PERCENTAGE);
        SelectableNode.this.setHeight(100, Unit.PERCENTAGE);
//        
        this.disables = disables;
        if (disables) {
            SelectableNode.this.setStyleName("lightgraybubble");
        } else {
            SelectableNode.this.addLayoutClickListener(SelectableNode.this);
        }
        upperLine = new VerticalLayout();
        upperLine.setHeight(105, Unit.PERCENTAGE);
        upperLine.setWidth(10, Unit.PERCENTAGE);

        SelectableNode.this.addComponent(upperLine);
        SelectableNode.this.setComponentAlignment(upperLine, Alignment.TOP_CENTER);

        lowerLine = new VerticalLayout();
        lowerLine.setHeight(105, Unit.PERCENTAGE);
        lowerLine.setWidth(10, Unit.PERCENTAGE);

        SelectableNode.this.addComponent(lowerLine);
        SelectableNode.this.setComponentAlignment(lowerLine, Alignment.TOP_CENTER);

    }

    public boolean isUpperSelected() {
        return upperSelected;
    }

    public void setUpperSelected(boolean upperSelected) {
        if (disables) {
            return;
        }
        this.upperSelected = upperSelected;
        if (upperSelected) {
            upperLine.setStyleName("selectednodeline");
        } else {
            upperLine.setStyleName("unselectednodeline");
        }
    }

    public boolean isLowerSelected() {
        return lowerSelected;

    }

    public void setLowerSelected(boolean lowerSelected) {
        if (disables) {
            return;
        }
        this.lowerSelected = lowerSelected;
        if (lowerSelected) {
            lowerLine.addStyleName("selectednodeline");
        } else {
            lowerLine.addStyleName("unselectednodeline");
        }
    }

    public boolean isSelecatble() {
        if (disables) {
            return !disables;
        }
        return selecatble;
    }

    public void setSelecatble(boolean selecatble) {
        if (disables) {
            return;
        }
        this.selecatble = selecatble;
        if (selecatble) {
            SelectableNode.this.addStyleName("selectablebubble");
        } else {
            SelectableNode.this.addStyleName("lightgraybubble");
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            SelectableNode.this.addStyleName("selectedbubble");
            lowerLine.addStyleName("selectedbubble");
        } else {
            SelectableNode.this.removeStyleName("selectedbubble");
            lowerLine.removeStyleName("selectedbubble");
        }

    }

    public int getColumnIndex() {
        return columnIndex;
    }

    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        selectNode(columnIndex);
    }

    public abstract void selectNode(int columnIndex);

}
