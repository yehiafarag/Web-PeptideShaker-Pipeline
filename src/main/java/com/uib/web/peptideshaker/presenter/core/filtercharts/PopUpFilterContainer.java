package com.uib.web.peptideshaker.presenter.core.filtercharts;

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

    public PopUpFilterContainer(RegistrableFilter filter) {
        PopUpFilterContainer.this.setSizeFull();
        PopUpFilterContainer.this.setStyleName("blacklayout");
        PopUpFilterContainer.this.addLayoutClickListener(this);
        window = new PopupWindow("");
        window.setContent(filter);
        window.setWindowSize(95, 95);
        
    }
    
    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        window.setPopupVisible(true);
    }
    
}
