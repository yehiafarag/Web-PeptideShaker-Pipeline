
package com.uib.web.peptideshaker.presenter.core;

import com.uib.web.peptideshaker.presenter.core.SmallSideBtn;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

/**
 *This interface represents the minimum requirement for presenter components to be used as a view in web PeptideShaker application
 * @author Yehia Farag
 */
public interface ViewableFrame {
    
    VerticalLayout getLeftView();
    VerticalLayout getMainView();
     HorizontalLayout getBottomView();
      SmallSideBtn getTopView(); 
    SmallSideBtn getRightView();
    String getViewId();
    void minimizeView();
    void maximizeView();
    
}
