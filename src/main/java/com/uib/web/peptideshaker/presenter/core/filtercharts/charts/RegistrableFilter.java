package com.uib.web.peptideshaker.presenter.core.filtercharts.charts;

import com.vaadin.ui.Component;
import com.vaadin.ui.Layout;
import java.util.Set;

/**
 * This interface include all the abstracted methods required to register the
 * filter into the selection manager
 *
 * @author Yehia Farag
 */
public interface RegistrableFilter extends Layout {

    public String getFilterId();

    public void resetFilter();

    public void updateFilter(Set<Comparable> selection,Set<Object>selectedCategories,boolean singleFilter);

    public void selectionChange(String type);

    public Component getThumb();

    public void redrawChart();

    public boolean isAppliedFilter();

    public Set<Object> getSelectedCategories();

}
