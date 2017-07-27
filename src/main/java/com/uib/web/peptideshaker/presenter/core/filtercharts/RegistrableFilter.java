
package com.uib.web.peptideshaker.presenter.core.filtercharts;

/**
 *This interface include all the abstracted methods required to register the filter into the selection manager
 * @author Yehia Farag
 */
public interface RegistrableFilter {
    public String getFilterId();
    public void selectData();
    public void updateFilter();
    
}
