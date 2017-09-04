package com.uib.web.peptideshaker.presenter.core.filtercharts.components;

import com.vaadin.event.LayoutEvents;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import java.awt.Color;

/**
 * This class represents selectable node that used mainly in
 * MatrixLayoutChartFilter
 *
 * @author Yehia Farag
 */
public abstract class SelectableNode extends AbsoluteLayout implements LayoutEvents.LayoutClickListener {

    private boolean upperSelected;
    private boolean lowerSelected;
    private boolean selecatble;
    private boolean disables;
    private final VerticalLayout upperLine;
    private final VerticalLayout lowerLine;
    private final String nodeId;
    private boolean selected;
    private final int columnIndex;
    private final VerticalLayout nodeContainer;
    private final Color nodeColor;

    public String getNodeId() {
        return nodeId;
    }

    public Color getNodeColor() {
        return nodeColor;
    }

    public boolean isDisables() {
        return disables;
    }

    public SelectableNode(String nodeId, int columnIndex, boolean disables, Color nodeColor) {
        this.nodeId = nodeId;
        this.columnIndex = columnIndex;
        this.nodeColor = nodeColor;
        SelectableNode.this.setWidth(100, Unit.PERCENTAGE);
        SelectableNode.this.setHeight(100, Unit.PERCENTAGE);
        VerticalLayout lineContainers = new VerticalLayout();
        lineContainers.setWidth(100, Unit.PERCENTAGE);
        lineContainers.setHeight(100, Unit.PERCENTAGE);
        SelectableNode.this.addComponent(lineContainers);

        nodeContainer = new VerticalLayout();
        nodeContainer.setWidth(100, Unit.PERCENTAGE);
        nodeContainer.setHeight(100, Unit.PERCENTAGE);
        SelectableNode.this.addComponent(nodeContainer);

//        
        this.disables = disables;
        if (disables) {
            Label nodeComp = new Label("<center><div style='width: 16px;height: 16px;border-radius: 100%;border:2px solid lightgray;background-color:whitesmoke;'></div><center>");
            nodeComp.setContentMode(ContentMode.HTML);
            nodeContainer.addComponent(nodeComp);
            nodeContainer.setComponentAlignment(nodeComp, Alignment.MIDDLE_CENTER);
//            SelectableNode.this.setStyleName("lightgraybubble");
        } else {
            SelectableNode.this.addLayoutClickListener(SelectableNode.this);
        }
        if (nodeColor != null && !disables) {
            Label nodeComp = new Label("<center><div style='width: 16px;height: 16px;border-radius: 100%;border:2px solid gray;background-color:rgb(" + nodeColor.getRed() + "," + nodeColor.getGreen() + "," + nodeColor.getBlue() + ");'></div><center>");
            nodeComp.setContentMode(ContentMode.HTML);
            nodeContainer.addComponent(nodeComp);
            nodeContainer.setComponentAlignment(nodeComp, Alignment.MIDDLE_CENTER);
        }
        upperLine = new VerticalLayout();
        upperLine.setHeight(105, Unit.PERCENTAGE);
        upperLine.setWidth(10, Unit.PERCENTAGE);

        lineContainers.addComponent(upperLine);
        lineContainers.setComponentAlignment(upperLine, Alignment.TOP_CENTER);

        lowerLine = new VerticalLayout();
        lowerLine.setHeight(105, Unit.PERCENTAGE);
        lowerLine.setWidth(10, Unit.PERCENTAGE);

        lineContainers.addComponent(lowerLine);
        lineContainers.setComponentAlignment(lowerLine, Alignment.TOP_CENTER);

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
//             nodeContainer.setVisible(true);
            SelectableNode.this.addStyleName("selectablebubble");
        } else {
//             nodeContainer.setVisible(false);
            nodeContainer.removeAllComponents();
            Label nodeComp = new Label("<center><div style='width: 16px;height: 16px;border-radius: 100%;border:2px solid lightgray;background-color:whitesmoke;'></div><center>");
            nodeComp.setContentMode(ContentMode.HTML);
            nodeContainer.addComponent(nodeComp);
            nodeContainer.setComponentAlignment(nodeComp, Alignment.MIDDLE_CENTER);
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
