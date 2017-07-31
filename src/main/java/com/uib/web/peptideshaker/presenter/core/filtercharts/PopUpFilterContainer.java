package com.uib.web.peptideshaker.presenter.core.filtercharts;

import com.uib.web.peptideshaker.presenter.core.PopupWindow;
import com.vaadin.event.LayoutEvents;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

/**
 * This layout work as container that contain the minimized mode for filters and
 * maximized mode the have all filter details
 *
 * @author Yehia Farag
 */
public class PopUpFilterContainer extends VerticalLayout implements LayoutEvents.LayoutClickListener {

    private final PopupWindow window;
    private final RegistrableFilter filter;

    public PopUpFilterContainer(String title, RegistrableFilter filter) {
        PopUpFilterContainer.this.setSizeFull();
        this.filter=filter;
//        PopUpFilterContainer.this.setStyleName("blacklayout");
        PopUpFilterContainer.this.addLayoutClickListener(PopUpFilterContainer.this);
        window = new PopupWindow("");
        window.setContent(title, filter);
        window.setWindowSize(95, 95);
        PopUpFilterContainer.this.addComponent(filter.getThumb());

    }

    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        filter.redrawChart();
        window.setPopupVisible(true);
    }

    public void setPopupVisible(boolean visible) {
        window.setPopupVisible(visible);
    }

}
