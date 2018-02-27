package com.uib.web.peptideshaker.presenter.core.filtercharts;

import com.uib.web.peptideshaker.model.core.ModificationMatrix;
import com.uib.web.peptideshaker.presenter.core.filtercharts.updatedfilters.ChromosomesFilter;
import com.uib.web.peptideshaker.presenter.core.filtercharts.updatedfilters.DivaMatrixLayoutChartFilter;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.SelectionManager;
import com.uib.web.peptideshaker.presenter.core.filtercharts.charts.RangeFilter;
import com.uib.web.peptideshaker.presenter.core.filtercharts.updatedfilters.DivaPieChartFilter;
import com.uib.web.peptideshaker.presenter.core.filtercharts.updatedfilters.DivaRangeFilter;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
public class FiltersContainer extends HorizontalLayout {

    private final DivaMatrixLayoutChartFilter modificationFilter;

    private final DivaPieChartFilter validationFilter;

    private final DivaPieChartFilter PIFilter;

    private final ChromosomesFilter chromosomeFilter;

    private final DivaRangeFilter prptidesNumberFilter;
    private final DivaRangeFilter possibleCoverageFilter;
    private final DivaRangeFilter psmNumberFilter;

    private final List<Color> colorList;
    /**
     * Array of default slice colors.
     */
    private final Color[] defaultColors = new Color[]{new Color(219, 169, 1), new Color(110, 177, 206), new Color(213, 8, 8), new Color(4, 180, 95), new Color(174, 180, 4), new Color(10, 255, 14), new Color(244, 250, 88), new Color(255, 0, 64), new Color(246, 216, 206), new Color(189, 189, 189), new Color(255, 128, 0), Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.PINK};
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

    public FiltersContainer(SelectionManager Selection_Manager) {
        FiltersContainer.this.setSizeFull();
        FiltersContainer.this.setSpacing(true);

        this.Selection_Manager = Selection_Manager;
        colorList = new ArrayList<>(Arrays.asList(defaultColors));
        for (String str : extraColourValues) {
            Color c = hex2Rgb("#" + str);
            if (c.getRGB() == Color.WHITE.getRGB() || c.getRGB() == Color.BLACK.getRGB() || Arrays.asList(defaultColors).contains(c)) {
                continue;
            }
            colorList.add(c);
        }

        HorizontalLayout filtersContainer = new HorizontalLayout();
        filtersContainer.setSizeFull();
        filtersContainer.setSpacing(true);
        FiltersContainer.this.addComponent(filtersContainer);

        PIFilter = new DivaPieChartFilter("Protein Inference", "pi_filter", Selection_Manager) {
            @Override
            public void selectionChange(String type) {
                if (type.equalsIgnoreCase("dataset_filter_selection")) {
//                    updateFilterSelection(Selection_Manager.getFilteredProteinsSet(), Selection_Manager.getAppliedFilterCategories("pi_filter"), false, false, false);

                }
            }

        };

        VerticalLayout leftThumbContainer = new VerticalLayout();
        leftThumbContainer.setSizeFull();
        leftThumbContainer.setSpacing(true);
        filtersContainer.addComponent(leftThumbContainer);
        filtersContainer.setExpandRatio(leftThumbContainer, 1);

        leftThumbContainer.addComponent(PIFilter);
        leftThumbContainer.setComponentAlignment(PIFilter, Alignment.TOP_CENTER);

        validationFilter = new DivaPieChartFilter("Protein Validation", "validation_filter", Selection_Manager) {

            @Override
            public void selectionChange(String type) {
                if (type.equalsIgnoreCase("dataset_filter_selection")) {
//                    updateFilterSelection(Selection_Manager.getFilteredProteinsSet(), Selection_Manager.getAppliedFilterCategories("validation_filter"),false, false,false);

                }
            }

        };

        leftThumbContainer.addComponent(validationFilter);
        leftThumbContainer.setComponentAlignment(validationFilter, Alignment.TOP_CENTER);

        chromosomeFilter = new ChromosomesFilter("Chromosome", "chromosome_filter", Selection_Manager) {

            @Override
            public void selectionChange(String type) {
                if (type.equalsIgnoreCase("dataset_filter_selection")) {
//                    updateFilterSelection(Selection_Manager.getFilteredProteinsSet(), Selection_Manager.getAppliedFilterCategories("chromosome_filter"),false,false,false);

                }
            }
//
//            @Override
//            public void updateFilterSelection(Set<Comparable> selection, Set<Comparable> selectedCategories,boolean topFilter, boolean selectOnly,boolean selfAction) {
////                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//            }

        };

//        chromosome_filterContainer = new PopUpFilterContainer("Chromosome", chromosomeFilter, "med");
        leftThumbContainer.addComponent(chromosomeFilter);
        leftThumbContainer.setComponentAlignment(chromosomeFilter, Alignment.TOP_CENTER);

        modificationFilter = new DivaMatrixLayoutChartFilter("Modifications", "modifications_filter", Selection_Manager) {

            @Override
            public void selectionChange(String type) {
                if (type.equalsIgnoreCase("dataset_filter_selection")) {
//                    updateFilterSelection(Selection_Manager.getActiveProteinsSet(), Selection_Manager.getAppliedFilterCategories("modifications_filter"), Selection_Manager.isSingleProteinsFilter());
                }
            }

        };
        filtersContainer.addComponent(modificationFilter);
        filtersContainer.setComponentAlignment(modificationFilter, Alignment.TOP_CENTER);
        filtersContainer.setExpandRatio(modificationFilter, 2);
        modificationFilter.setSizeFull();

        VerticalLayout rightThumbContainer = new VerticalLayout();
        rightThumbContainer.setSizeFull();
        rightThumbContainer.setSpacing(true);
        filtersContainer.addComponent(rightThumbContainer);
        filtersContainer.setComponentAlignment(rightThumbContainer, Alignment.TOP_CENTER);
        filtersContainer.setExpandRatio(rightThumbContainer, 1);

//add range filter
        prptidesNumberFilter = new DivaRangeFilter("#Peptides", "peptidesNum_filter", this.Selection_Manager) {
            @Override
            public void selectionChange(String type) {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        };
//        prptidesNumberFilter.initializeFilterData(initRangeData(null));
//        prptidesNumberFilter.updateFilter();
        rightThumbContainer.addComponent(prptidesNumberFilter);

        psmNumberFilter = new DivaRangeFilter("#PSM", "psmNum_filter", this.Selection_Manager) {
            @Override
            public void selectionChange(String type) {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        };

        possibleCoverageFilter = new DivaRangeFilter("Possible Coverage (%)", "possibleCoverage_filter", this.Selection_Manager) {
            @Override
            public void selectionChange(String type) {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        };

//        test2.updateData(initRangeData(null));
//        test2.updateFilter();
        rightThumbContainer.addComponent(psmNumberFilter);

        rightThumbContainer.addComponent(possibleCoverageFilter);
    }

    public void updateFiltersData(ModificationMatrix modificationMatrix, Map<String, Color> modificationsColorMap, Map<String, Set<Comparable>> chromosomeMap, Map<String, Set<Comparable>> piMap, Map<String, Set<Comparable>> proteinValidationMap, TreeMap<Comparable, Set<Comparable>> proteinPeptidesNumberMap, TreeMap<Comparable, Set<Comparable>> proteinPSMNumberMap, TreeMap<Comparable, Set<Comparable>> proteinCoverageMap) {
        Selection_Manager.reset();
//        if (modificationMatrix.size() < 10) {
        Selection_Manager.setModificationsMap(modificationMatrix);
        modificationFilter.initializeFilterData(modificationMatrix, modificationsColorMap, new HashSet<>(), Selection_Manager.getFullProteinSet().size());

//        } else {
//            Selection_Manager.setModificationsMap(new HashMap<>());
//        }
        chromosomeFilter.initializeFilterData(chromosomeMap);
        Selection_Manager.setChromosomeMap(chromosomeMap);

        Color[] colorsArr = new Color[]{Color.DARK_GRAY, new Color(4, 180, 95), Color.YELLOW, new Color(213, 8, 8), Color.ORANGE};
        PIFilter.initializeFilterData(piMap, new ArrayList<>(Arrays.asList(colorsArr)));//colorList.subList(0, piMap.size()).toArray(new Color[piMap.size()])
        Selection_Manager.setPiMap(piMap);
        Selection_Manager.setProteinValidationMap(proteinValidationMap);

        colorsArr = new Color[]{Color.DARK_GRAY, new Color(4, 180, 95), new Color(213, 8, 8)};

        validationFilter.initializeFilterData(proteinValidationMap, new ArrayList<>(Arrays.asList(colorsArr)));//colorList.subList(0, proteinValidationMap.size()).toArray(new Color[proteinValidationMap.size()])
        Selection_Manager.setProteinCoverageMap(proteinCoverageMap);
        Selection_Manager.setProteinPSMNumberMap(proteinPSMNumberMap);
        Selection_Manager.setProteinPeptidesNumberMap(proteinPeptidesNumberMap);
        prptidesNumberFilter.initializeFilterData(proteinPeptidesNumberMap);
        psmNumberFilter.initializeFilterData(proteinPSMNumberMap);
        possibleCoverageFilter.initializeFilterData(proteinCoverageMap);
    }

    private Integer[] initRangeData(Set<String> filteredData) {

        Integer[] data = new Integer[10];
        data[0] = 30;
        data[1] = 50;
        data[2] = 30;
        data[3] = 10;
        data[4] = 5;
        data[5] = 30;
        data[6] = 100;
        data[7] = 11;
        data[8] = 1;
        data[9] = 70;

//        for (int x = 0; x < data.length; x++) {
//            int value = (int) (Math.random() * 1000);            
//            data[value] = data[value] + 1;
//
//        }
//        for (int x = 0; x < 100; x++) {
//            double value = Math.random() * 100;
//            if (!dataMap.containsKey(value)) {
//                dataMap.put(value, 0);
//            }
//            dataMap.put(value, (dataMap.get(value) + 1));
//            if (dataMap.get(value) >= max) {
//                max = dataMap.get(value);
//            }
//            if (dataMap.get(value) <= min) {
//                min = dataMap.get(value);
//            }
//        }
//        double[][] data = new double[dataMap.size()][2];
//        int i = 0;
//        for (double key : dataMap.keySet()) {
//            data[i] = new double[]{key, scaleValues((double) dataMap.get(key), max, min)};
//            i++;
//        }
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
