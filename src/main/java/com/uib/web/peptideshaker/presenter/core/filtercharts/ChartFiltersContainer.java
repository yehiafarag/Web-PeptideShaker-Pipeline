package com.uib.web.peptideshaker.presenter.core.filtercharts;

import com.uib.web.peptideshaker.presenter.components.peptideshakerview.components.SelectionManager;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * This class represents main container for pie-chart filters the class
 * responsible for maintaining pie-charts interactivity
 *
 * @author Yehia Farag
 */
public class ChartFiltersContainer extends HorizontalLayout {

    private final SelectionManager Selection_Manager;
    private final MatrixLayoutChartFilter modifications_filter;

    public ChartFiltersContainer(SelectionManager Selection_Manager) {
        ChartFiltersContainer.this.setSizeFull();
        this.Selection_Manager = Selection_Manager;
//        Map<String, Double> modificationValues = new LinkedHashMap<>();
////        modificationValues.put("Variable", 100.0);
////        modificationValues.put("Fixed", 100.0);
////        modificationValues.put("No Modifications", 300.0);
//        
//        for(int x=1;x<31;x++){
//         modificationValues.put("Chr "+x, Math.random()*x);
//        }
//        DonutChartFilter filter = new DonutChartFilter("Modification");
//        ChartFiltersContainer.this.addComponent(filter);
//        filter.updateChartData(modificationValues);
//        ChartFiltersContainer.this.setComponentAlignment(filter, Alignment.MIDDLE_CENTER);
//        LineChartFilter filter1 = new LineChartFilter("chromosome");
////        ChartFiltersContainer.this.addComponent(filter1);
////         filter1.updateChartData(modificationValues);
////        ChartFiltersContainer.this.setComponentAlignment(filter1, Alignment.MIDDLE_CENTER);
//        RangeFilter filter2 = new RangeFilter("Protein Inference",0,100);
//        
//         Map<Double, Double> rangeValues = new LinkedHashMap<>();
//         for(int x=0;x<100;x++){
//         rangeValues.put((double)x, Math.random()*100);
//         }
//        ChartFiltersContainer.this.addComponent(filter2);
//        filter2.updateChartData(rangeValues);
//        ChartFiltersContainer.this.setComponentAlignment(filter2, Alignment.MIDDLE_CENTER);
        modifications_filter = new MatrixLayoutChartFilter("Modifications", "modifications_filter");
        ChartFiltersContainer.this.addComponent(modifications_filter);
        ChartFiltersContainer.this.setComponentAlignment(modifications_filter, Alignment.MIDDLE_CENTER);
        this.Selection_Manager.RegistrFilter(modifications_filter);

        ///for testing 
    }

    public void updateFiltersData(Map<String, Set<String>> rows) {       
        if (rows.size() > 10) {
            return;
        }
        modifications_filter.updateChartData(rows);

    }

}
