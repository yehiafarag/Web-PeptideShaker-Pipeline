package com.uib.web.peptideshaker.presenter.core.filtercharts;

import com.uib.web.peptideshaker.presenter.core.filtercharts.charts.RegistrableFilter;
import com.uib.web.peptideshaker.presenter.core.PopupWindow;
import com.vaadin.event.LayoutEvents;
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

    public PopUpFilterContainer(String title, RegistrableFilter filter, String filterSize) {

        PopUpFilterContainer.this.setWidth(100, Unit.PERCENTAGE);
        PopUpFilterContainer.this.setHeight(100, Unit.PERCENTAGE);

        this.filter = filter;
        PopUpFilterContainer.this.addLayoutClickListener(PopUpFilterContainer.this);
        window = new PopupWindow("");
        window.setContent(title, filter);
        window.setWindowSize(95, 95);
        PopUpFilterContainer.this.addComponent(filter.getThumb());
        if (filterSize.equalsIgnoreCase("med")) {
            window.addWindowStyle("medpopupwindow");
        }
        if (filterSize.equalsIgnoreCase("small")) {
            window.addWindowStyle("smallpopupwindow");
        }

    }

    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        filter.redrawChart();
        window.setPopupVisible(true);
    }

    public void setPopupVisible(boolean visible) {
        window.setPopupVisible(visible);
    }

    @Override
    public void setHeight(float height, Unit unit) {
        super.setHeight(height, unit); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSizeFull() {

        super.setSizeFull(); //To change body of generated methods, choose Tools | Templates.
        filter.getThumb().setSizeFull();
    }

}
