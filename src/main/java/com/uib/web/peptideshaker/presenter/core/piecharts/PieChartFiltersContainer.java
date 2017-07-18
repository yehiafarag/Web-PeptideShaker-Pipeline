package com.uib.web.peptideshaker.presenter.core.piecharts;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

/**
 * This class represents main container for pie-chart filters the class
 * responsible for maintaining pie-charts interactivity
 *
 * @author Yehia Farag
 */
public class PieChartFiltersContainer extends HorizontalLayout {

    public PieChartFiltersContainer() {
        PieChartFiltersContainer.this.setSizeFull();
        PieChartFilter filter = new PieChartFilter();
        PieChartFiltersContainer.this.addComponent(filter);
        PieChartFiltersContainer.this.setComponentAlignment(filter, Alignment.MIDDLE_CENTER);
        PieChartFilter filter1 = new PieChartFilter();
        PieChartFiltersContainer.this.addComponent(filter1);
        PieChartFiltersContainer.this.setComponentAlignment(filter1, Alignment.MIDDLE_CENTER);
        PieChartFilter filter2 = new PieChartFilter();
        PieChartFiltersContainer.this.addComponent(filter2);
        PieChartFiltersContainer.this.setComponentAlignment(filter2, Alignment.MIDDLE_CENTER);
        PieChartFilter filter3 = new PieChartFilter();
        PieChartFiltersContainer.this.addComponent(filter3);
        PieChartFiltersContainer.this.setComponentAlignment(filter3, Alignment.MIDDLE_CENTER);
    }

}
