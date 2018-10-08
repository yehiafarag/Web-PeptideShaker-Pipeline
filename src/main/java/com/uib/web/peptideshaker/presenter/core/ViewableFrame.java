package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.event.LayoutEvents.LayoutClickNotifier;
import com.vaadin.ui.VerticalLayout;

/**
 * This interface represents the minimum requirement for presenter components to
 * be used as a view in web PeptideShaker application
 *
 * @author Yehia Farag
 */
public interface ViewableFrame {

    VerticalLayout getSubViewButtonsActionContainerLayout();

    VerticalLayout getMainView();

    BigSideBtn getPresenterControlInframeButton();

    ButtonWithLabel getPresenterControlButton();

    String getViewId();

    void minimizeView();

    void maximizeView();

}
