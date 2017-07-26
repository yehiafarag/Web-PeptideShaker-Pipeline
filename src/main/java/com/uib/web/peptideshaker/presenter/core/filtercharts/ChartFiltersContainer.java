package com.uib.web.peptideshaker.presenter.core.filtercharts;

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

    public ChartFiltersContainer() {
        ChartFiltersContainer.this.setSizeFull();
        Map<String, Double> modificationValues = new LinkedHashMap<>();
//        modificationValues.put("Variable", 100.0);
//        modificationValues.put("Fixed", 100.0);
//        modificationValues.put("No Modifications", 300.0);
        
        for(int x=1;x<31;x++){
         modificationValues.put("Chr "+x, Math.random()*x);
        }
        DonutChartFilter filter = new DonutChartFilter("Modification");
        ChartFiltersContainer.this.addComponent(filter);
        filter.updateChartData(modificationValues);
        ChartFiltersContainer.this.setComponentAlignment(filter, Alignment.MIDDLE_CENTER);
        LineChartFilter filter1 = new LineChartFilter("chromosome");
//        ChartFiltersContainer.this.addComponent(filter1);
//         filter1.updateChartData(modificationValues);
//        ChartFiltersContainer.this.setComponentAlignment(filter1, Alignment.MIDDLE_CENTER);
        RangeFilter filter2 = new RangeFilter("Protein Inference",0,100);
        
         Map<Double, Double> rangeValues = new LinkedHashMap<>();
         for(int x=0;x<100;x++){
         rangeValues.put((double)x, Math.random()*100);
         }
        ChartFiltersContainer.this.addComponent(filter2);
        filter2.updateChartData(rangeValues);
        ChartFiltersContainer.this.setComponentAlignment(filter2, Alignment.MIDDLE_CENTER);
        MatrixLayoutChartFilter filter3 = new MatrixLayoutChartFilter("Validation");
        ChartFiltersContainer.this.addComponent(filter3);
        ChartFiltersContainer.this.setComponentAlignment(filter3, Alignment.MIDDLE_CENTER);
        
         ///for testing 
        VerticalLayout ChartFiltersContainer = new VerticalLayout();
        ChartFiltersContainer.setWidth(100, Unit.PERCENTAGE);
        ChartFiltersContainer.setHeight(100, Unit.PERCENTAGE);    

        Map<String, Set<String>> rows = new LinkedHashMap<>();
        Random r = new Random();
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        String alphabet2 = "ABCDEF";
        for (int i = 0; i < alphabet2.length(); i++) {
            String key = "" + alphabet2.charAt(i);
            rows.put(key, new HashSet<>());
            int protNum = r.nextInt(alphabet2.length());
            for (int y = 0; y < protNum; y++) {
                rows.get(key).add(""+alphabet.charAt(r.nextInt(alphabet.length())));
                rows.get(key).add("ZzZ");
            }
        }
        filter3.updateChartData(rows);
        
    }

}
