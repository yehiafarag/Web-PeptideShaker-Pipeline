
package com.uib.web.peptideshaker.presenter.core.filtercharts;

import com.vaadin.ui.Layout;

/**
 *This interface include all the abstracted methods required to register the filter into the selection manager
 * @author Yehia Farag
 */
public interface RegistrableFilter  extends Layout{
    public String getFilterId();
    public void selectData();
    public void updateFilter();
    
}
