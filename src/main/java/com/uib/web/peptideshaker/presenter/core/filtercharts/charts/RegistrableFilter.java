package com.uib.web.peptideshaker.presenter.core.filtercharts.charts;

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

    public void updateFilterSelection(Set<Comparable> selection,Set<Comparable>selectedCategories,boolean topFilter,boolean singleFilter,boolean selfAction);

    public void selectionChange(String type);

    public void redrawChart();

}
