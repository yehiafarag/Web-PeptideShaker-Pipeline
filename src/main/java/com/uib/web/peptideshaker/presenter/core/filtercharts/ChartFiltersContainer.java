package com.uib.web.peptideshaker.presenter.core.filtercharts;

import com.uib.web.peptideshaker.presenter.core.filtercharts.charts.DonutChartFilter;
import com.uib.web.peptideshaker.presenter.core.filtercharts.charts.MatrixLayoutChartFilter;
import com.uib.web.peptideshaker.presenter.components.peptideshakerview.components.SelectionManager;
import com.uib.web.peptideshaker.presenter.core.filtercharts.charts.RangeFilter;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * This class represents main container for pie-chart filters the class
 * responsible for maintaining pie-charts interactivity
 *
 * @author Yehia Farag
 */
public class ChartFiltersContainer extends HorizontalLayout {

    private final PopUpFilterContainer validation_filterContainer;
    private final DonutChartFilter validationFilter;

    private final PopUpFilterContainer PI_filterContainer;
    private final DonutChartFilter PIFilter;

    private final PopUpFilterContainer chromosome_filterContainer;
    private final DonutChartFilter chromosomeFilter;

    private final PopUpFilterContainer modificationFilterContainer;
    private final MatrixLayoutChartFilter updatedModificationFilter;
    private final List<Color> colorList;
    /**
     * Array of default slice colors.
     */
    private final Color[] defaultColors = new Color[]{ new Color(219, 169, 1),new Color(110, 177, 206), new Color(213, 8, 8), new Color(4, 180, 95), new Color(174, 180, 4), new Color(10, 255, 14), new Color(244, 250, 88), new Color(255, 0, 64), new Color(246, 216, 206), new Color(189, 189, 189), new Color(255, 128, 0), Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.PINK};
    /**
     * Array of extra colors.
     */
    private String[] extraColourValues = new String[]{
        "C00000", "00C000", "0000C0", "C0C000", "C000C0", "00C0C0", "C0C0C0",
        "400000", "004000", "000040", "404000", "400040", "004040", "404040",
        "200000", "002000", "000020", "202000", "200020", "002020", "202020",
        "600000", "006000", "000060", "606000", "600060", "006060", "606060",
        "A00000", "00A000", "0000A0", "A0A000", "A000A0", "00A0A0", "A0A0A0",
        "E00000", "00E000", "0000E0", "E0E000", "E000E0", "00E0E0", "E0E0E0",};
    private final SelectionManager Selection_Manager;

    public ChartFiltersContainer(SelectionManager Selection_Manager) {
        this.Selection_Manager = Selection_Manager;
        colorList = new ArrayList<>(Arrays.asList(defaultColors));
        for (String str : extraColourValues) {
            Color c = hex2Rgb("#" + str);
            if (c.getRGB() == Color.WHITE.getRGB() || c.getRGB() == Color.BLACK.getRGB() || Arrays.asList(defaultColors).contains(c)) {
                continue;
            }
            colorList.add(c);
        }
        ChartFiltersContainer.this.setSizeFull();
        ChartFiltersContainer.this.setSpacing(true);
        GridLayout leftFiltersContainer = new GridLayout(3, 2);
        leftFiltersContainer.setSizeFull();
        leftFiltersContainer.setSpacing(true);
        leftFiltersContainer.setRowExpandRatio(0, 50);
         leftFiltersContainer.setRowExpandRatio(1, 50);
        ChartFiltersContainer.this.addComponent(leftFiltersContainer);     
        ChartFiltersContainer.this.setExpandRatio(leftFiltersContainer, 3);

        PIFilter = new DonutChartFilter("Protein Inference", "pi_filter", Selection_Manager) {
            @Override
            public void close() {
                PI_filterContainer.setPopupVisible(false);
            }

            @Override
            public void selectionChange(String type) {
                if (type.equalsIgnoreCase("protein_selection")) {
                    updateFilter(Selection_Manager.getProteinSelectionValue(), Selection_Manager.getAppliedFilterCategories("pi_filter"), Selection_Manager.isSingleProteinsFilter());

                }
            }

        };
        PI_filterContainer = new PopUpFilterContainer("Protein Inference", PIFilter, "med");
        leftFiltersContainer.addComponent(PI_filterContainer, 0, 0);
        leftFiltersContainer.setComponentAlignment(PI_filterContainer, Alignment.TOP_CENTER);

        validationFilter = new DonutChartFilter("Protein Validation", "validation_filter", Selection_Manager) {
            @Override
            public void close() {
                validation_filterContainer.setPopupVisible(false);
            }

            @Override
            public void selectionChange(String type) {
                if (type.equalsIgnoreCase("protein_selection")) {
                    updateFilter(Selection_Manager.getProteinSelectionValue(), Selection_Manager.getAppliedFilterCategories("validation_filter"), Selection_Manager.isSingleProteinsFilter());

                }
            }

        };
        validation_filterContainer = new PopUpFilterContainer("Protein Validation", validationFilter, "med");
        leftFiltersContainer.addComponent(validation_filterContainer, 1, 0);
        leftFiltersContainer.setComponentAlignment(validation_filterContainer, Alignment.TOP_CENTER);

        chromosomeFilter = new DonutChartFilter("Chromosome", "chromosome_filter", Selection_Manager) {
            @Override
            public void close() {
                chromosome_filterContainer.setPopupVisible(false);
            }

            @Override
            public void selectionChange(String type) {
                if (type.equalsIgnoreCase("protein_selection")) {
                    updateFilter(Selection_Manager.getProteinSelectionValue(), Selection_Manager.getAppliedFilterCategories("chromosome_filter"), Selection_Manager.isSingleProteinsFilter());

                }
            }

        };
        chromosome_filterContainer = new PopUpFilterContainer("Chromosome", chromosomeFilter, "med");
        leftFiltersContainer.addComponent(chromosome_filterContainer, 2, 0);
        leftFiltersContainer.setComponentAlignment(chromosome_filterContainer, Alignment.TOP_CENTER);
        chromosome_filterContainer.setSizeFull();
        chromosomeFilter.setSizeFull();

//add range filter
      
        
        String[] colors = new String[]{"green", "red"};
        RangeFilter test = new RangeFilter("Decreased<br/>%", -100.0, "Increased<br/>%", 100.0, colors, 0.0, true) {
            @Override
            public void selectedRange(double min, double max, boolean filterApplied) {

            }

            @Override
            public void selectedRange(double min, double max, double secMin, double secMax, boolean filterApplied) {

            }

        };
       
        leftFiltersContainer.addComponent(test, 0, 1);
        leftFiltersContainer.setComponentAlignment(test, Alignment.BOTTOM_CENTER); 
        test.updateData(initRangeData(null));
        test.updateFilter();
         RangeFilter test2 = new RangeFilter("Decreased<br/>%", -100.0, "Increased<br/>%", 100.0, colors, 0.0, true) {
            @Override
            public void selectedRange(double min, double max, boolean filterApplied) {

            }

            @Override
            public void selectedRange(double min, double max, double secMin, double secMax, boolean filterApplied) {

            }

        };
        test2.updateData(initRangeData(null));
        test2.updateFilter();
        leftFiltersContainer.addComponent(test2, 1, 1);
        leftFiltersContainer.setComponentAlignment(test2, Alignment.BOTTOM_CENTER);
         RangeFilter test3 = new RangeFilter("Decreased<br/>%", -100.0, "Increased<br/>%", 100.0, colors, 0.0, true) {
            @Override
            public void selectedRange(double min, double max, boolean filterApplied) {

            }

            @Override
            public void selectedRange(double min, double max, double secMin, double secMax, boolean filterApplied) {

            }

        };
        test3.updateData(initRangeData(null));
        test3.updateFilter();
        leftFiltersContainer.addComponent(test3, 2, 1);
        leftFiltersContainer.setComponentAlignment(test3, Alignment.BOTTOM_CENTER);

        updatedModificationFilter = new MatrixLayoutChartFilter("Modifications", "modifications_filter", Selection_Manager) {
            @Override
            public void close() {
                modificationFilterContainer.setPopupVisible(false);
            }

            @Override
            public void selectionChange(String type) {
                if (type.equalsIgnoreCase("protein_selection")) {
                    updateFilter(Selection_Manager.getProteinSelectionValue(), Selection_Manager.getAppliedFilterCategories("modifications_filter"), Selection_Manager.isSingleProteinsFilter());
                }
            }

        };
        modificationFilterContainer = new PopUpFilterContainer("Modifications", updatedModificationFilter, "large");
        ChartFiltersContainer.this.addComponent(modificationFilterContainer);
        ChartFiltersContainer.this.setComponentAlignment(modificationFilterContainer, Alignment.TOP_CENTER);
        ChartFiltersContainer.this.setExpandRatio(modificationFilterContainer, 1);
        modificationFilterContainer.setSizeFull();
        updatedModificationFilter.setSizeFull();
    }

    public void updateFiltersData(Map<String, Set<String>> modificationsMap,Map<String, Color> modificationsColorMap, Map<String, Set<String>> chromosomeMap, Map<String, Set<String>> piMap,Map<String, Set<String>> proteinValidationMap) {
        Selection_Manager.reset();
        if (modificationsMap.size() < 10) {
            Selection_Manager.setModificationsMap(modificationsMap);
            updatedModificationFilter.calculateChartData(modificationsMap,modificationsColorMap, new HashSet<>(), Selection_Manager.getFullProteinSet().size());

        } else {
            Selection_Manager.setModificationsMap(new HashMap<>());
        }
        chromosomeFilter.updateChartData(chromosomeMap, colorList.subList(0, chromosomeMap.size()).toArray(new Color[chromosomeMap.size()]));
        Selection_Manager.setChromosomeMap(chromosomeMap);
        PIFilter.updateChartData(piMap, colorList.subList(0, piMap.size()).toArray(new Color[piMap.size()]));
        Selection_Manager.setPiMap(piMap);
        Selection_Manager.setProteinValidationMap(proteinValidationMap);
        validationFilter.updateChartData(proteinValidationMap, colorList.subList(0, proteinValidationMap.size()).toArray(new Color[proteinValidationMap.size()]));

    }

    private double[][] initRangeData(Set<String> filteredData) {
        Map<Double, Integer> dataMap = new TreeMap<>();
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        for (int x = 0; x < 100; x++) {
            double value = Math.random() * 100;
            if (!dataMap.containsKey(value)) {
                dataMap.put(value, 0);
            }
            dataMap.put(value, (dataMap.get(value) + 1));
            if (dataMap.get(value) >= max) {
                max = dataMap.get(value);
            }
            if (dataMap.get(value) <= min) {
                min = dataMap.get(value);
            }
        }
        double[][] data = new double[dataMap.size()][2];
        int i = 0;
        for (double key : dataMap.keySet()) {
            data[i] = new double[]{key, scaleValues((double) dataMap.get(key), max, min)};
            i++;
        }
        return data;
    }

    /**
     *
     * @param colorStr e.g. "#FFFFFF"
     * @return
     */
    private Color hex2Rgb(String colorStr) {
        return new Color(
                Integer.valueOf(colorStr.substring(1, 3), 16),
                Integer.valueOf(colorStr.substring(3, 5), 16),
                Integer.valueOf(colorStr.substring(5, 7), 16));
    }

    /**
     * Converts the value from linear scale to log scale. The log scale numbers
     * are limited by the range of the type float. The linear scale numbers can
     * be any double value.
     *
     * @param linearValue the value to be converted to log scale
     * @param max The upper limit number for the input numbers
     * @param lowerLimit the lower limit for the input numbers
     * @return the value in log scale
     */
    private double scaleValues(double linearValue, double max, double lowerLimit) {
        double logMax = (Math.log(max) / Math.log(2));
        double logValue = (Math.log(linearValue + 1) / Math.log(2));
        logValue = (logValue * 2 / logMax) + lowerLimit;
        return Math.min(logValue, max);
    }

}
