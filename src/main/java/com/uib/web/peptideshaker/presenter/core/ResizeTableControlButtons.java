package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.event.LayoutEvents;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.VerticalLayout;

/**
 *
 * @author Yehia Farag
 */
public abstract class ResizeTableControlButtons extends AbsoluteLayout implements LayoutEvents.LayoutClickListener {

    public ResizeTableControlButtons() {
        ResizeTableControlButtons.this.setWidth(58, Unit.PIXELS);
        ResizeTableControlButtons.this.setHeight(28, Unit.PIXELS);
        ResizeTableControlButtons.this.setStyleName("resizebtn");
        ResizeTableControlButtons.this.addLayoutClickListener(ResizeTableControlButtons.this);

        VerticalLayout largeBtn = new VerticalLayout();
        largeBtn.setHeight(16, Unit.PIXELS);
        largeBtn.setWidth(16, Unit.PIXELS);
        largeBtn.setDescription("Resize Table (Large)");
        largeBtn.setData(1);

        ResizeTableControlButtons.this.addComponent(largeBtn, "left:5px; top:5px;");
        VerticalLayout medBtn = new VerticalLayout();
        medBtn.setHeight(12, Unit.PIXELS);
        medBtn.setWidth(12, Unit.PIXELS);
        ResizeTableControlButtons.this.addComponent(medBtn, "left:26px; top:7px;");
        medBtn.setDescription("Resize Table (Medium)");
        medBtn.setData(2);

        VerticalLayout smallBtn = new VerticalLayout();
        smallBtn.setHeight(8, Unit.PIXELS);
        smallBtn.setWidth(8, Unit.PIXELS);
        ResizeTableControlButtons.this.addComponent(smallBtn, "left:43px; top:9px;");
        smallBtn.setDescription("Resize Table (Small)");
        smallBtn.setData(3);

    }

    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        if (event.getClickedComponent() instanceof VerticalLayout) {
            int i = (int) ((VerticalLayout) event.getClickedComponent()).getData();
            resize(i);
        }
        

    }

    public abstract void resize(int btnIndex);

}
