package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.ui.HorizontalLayout;
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

    SmallSideBtn getPresenterControlButton();

    String getViewId();

    void minimizeView();

    void maximizeView();

}
