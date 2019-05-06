package com.uib.web.peptideshaker.presenter.core.filtercharts;

import com.uib.web.peptideshaker.model.core.ModificationMatrix;
import com.uib.web.peptideshaker.presenter.core.filtercharts.filters.ChromosomesFilter;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.SelectionManager;
import com.uib.web.peptideshaker.presenter.core.filtercharts.filters.DivaPieChartFilter;
import com.uib.web.peptideshaker.presenter.core.filtercharts.filters.DivaRangeFilter;
import com.uib.web.peptideshaker.presenter.core.filtercharts.filters.ModificationsFilter;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
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
public class FiltersContainer extends HorizontalLayout {

    private final ModificationsFilter modificationFilter;

    private final DivaPieChartFilter validationFilter;

    private final DivaPieChartFilter ProteinInferenceFilter;

    private final ChromosomesFilter chromosomeFilter;

    private final DivaRangeFilter prptidesNumberFilter;
    private final DivaRangeFilter coverageFilter;
    private final DivaRangeFilter psmNumberFilter;
    private final Map<String, Color> PIColorMap;// new Color[]{Color.DARK_GRAY, new Color(4, 180, 95), new Color(245, 226, 80), new Color(213, 8, 8), Color.ORANGE};
//    private final SelectionGraph proteinsPathwayNewtorkGraph;
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
        FiltersContainer.this.setStyleName("datasetfilterstyle");
        PIColorMap = new HashMap<>();// new Color[]{Color.DARK_GRAY, new Color(4, 180, 95), new Color(245, 226, 80), new Color(213, 8, 8), Color.ORANGE};

        PIColorMap.put("Single", new Color(4, 180, 95));
        PIColorMap.put("Related", new Color(245, 226, 80));
        PIColorMap.put("Related & Unrelated", Color.ORANGE);
        PIColorMap.put("Unrelated", new Color(213, 8, 8));
        this.Selection_Manager = Selection_Manager;
        colorList = new ArrayList<>(Arrays.asList(defaultColors));
        for (String str : extraColourValues) {
            Color c = hex2Rgb("#" + str);
            if (c.getRGB() == Color.WHITE.getRGB() || c.getRGB() == Color.BLACK.getRGB() || Arrays.asList(defaultColors).contains(c)) {
                continue;
            }
            colorList.add(c);
        }
        ProteinInferenceFilter = new DivaPieChartFilter("Protein Inference", "pi_filter", Selection_Manager) {
            @Override
            public void selectionChange(String type) {
                if (type.equalsIgnoreCase("dataset_filter_selection")) {

                }
            }

        };

        VerticalLayout filterLeftPanelContainer = new VerticalLayout();
        filterLeftPanelContainer.setHeight(100, Unit.PERCENTAGE);
        filterLeftPanelContainer.setWidth(100, Unit.PERCENTAGE);
        filterLeftPanelContainer.setSpacing(false);
        filterLeftPanelContainer.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
        filterLeftPanelContainer.addStyleName("cornerfiltercontainerstyle");
        FiltersContainer.this.addComponent(filterLeftPanelContainer);
        FiltersContainer.this.setExpandRatio(filterLeftPanelContainer, 1);
        filterLeftPanelContainer.addComponent(ProteinInferenceFilter);
        filterLeftPanelContainer.setComponentAlignment(ProteinInferenceFilter, Alignment.TOP_LEFT);

        validationFilter = new DivaPieChartFilter("Protein Validation", "validation_filter", Selection_Manager) {
            @Override
            public void selectionChange(String type) {
                if (type.equalsIgnoreCase("dataset_filter_selection")) {

                }
            }
        };
        filterLeftPanelContainer.addComponent(validationFilter);
        filterLeftPanelContainer.setComponentAlignment(validationFilter, Alignment.TOP_CENTER);
        chromosomeFilter = new ChromosomesFilter("Chromosome", "chromosome_filter", Selection_Manager) {
            @Override
            public void selectionChange(String type) {
                if (type.equalsIgnoreCase("dataset_filter_selection")) {
                }
            }
        };
        filterLeftPanelContainer.addComponent(chromosomeFilter);
        filterLeftPanelContainer.setComponentAlignment(chromosomeFilter, Alignment.TOP_RIGHT);
        chromosomeFilter.addStyleName("bottomfilter");

        VerticalLayout filterMiddlePanelContainer = new VerticalLayout();
        filterMiddlePanelContainer.setHeight(100, Unit.PERCENTAGE);
        filterMiddlePanelContainer.setWidth(100, Unit.PERCENTAGE);
        filterMiddlePanelContainer.setSpacing(true);
        filterMiddlePanelContainer.addStyleName("cornerfiltercontainerstyle");
        FiltersContainer.this.addComponent(filterMiddlePanelContainer);
        FiltersContainer.this.setComponentAlignment(filterMiddlePanelContainer, Alignment.TOP_LEFT);
        FiltersContainer.this.setExpandRatio(filterMiddlePanelContainer, 2);

        modificationFilter = new ModificationsFilter("Modifications", "modifications_filter", Selection_Manager) {

            @Override
            public void selectionChange(String type) {
                if (type.equalsIgnoreCase("dataset_filter_selection")) {
//                    updateFilterSelection(Selection_Manager.getActiveProteinsSet(), Selection_Manager.getAppliedFilterCategories("modifications_filter"), Selection_Manager.isSingleProteinsFilter());
                }
            }

        };
        modificationFilter.addStyleName("middlefiltercontainerstyle");
        filterMiddlePanelContainer.addComponent(modificationFilter);
        modificationFilter.setSizeFull();

        VerticalLayout filterRightPanelContainer = new VerticalLayout();
        filterRightPanelContainer.setHeight(100, Unit.PERCENTAGE);
        filterRightPanelContainer.setWidth(100, Unit.PERCENTAGE);
        filterRightPanelContainer.setSpacing(false);
        filterRightPanelContainer.addStyleName("cornerfiltercontainerstyle");
        filterRightPanelContainer.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
        FiltersContainer.this.addComponent(filterRightPanelContainer);
        FiltersContainer.this.setComponentAlignment(filterRightPanelContainer, Alignment.TOP_LEFT);
        FiltersContainer.this.setExpandRatio(filterRightPanelContainer, 1);

//add range filter
        prptidesNumberFilter = new DivaRangeFilter("#Peptides", "peptidesNum_filter", this.Selection_Manager) {
            @Override
            public void selectionChange(String type) {
            }

        };
        filterRightPanelContainer.addComponent(prptidesNumberFilter);
        filterRightPanelContainer.setComponentAlignment(prptidesNumberFilter, Alignment.TOP_LEFT);

        psmNumberFilter = new DivaRangeFilter("#PSMs", "psmNum_filter", this.Selection_Manager) {
            @Override
            public void selectionChange(String type) {
            }

        };

        coverageFilter = new DivaRangeFilter("Coverage (%)", "possibleCoverage_filter", this.Selection_Manager) {
            @Override
            public void selectionChange(String type) {
            }

        };
        filterRightPanelContainer.addComponent(psmNumberFilter);
        filterRightPanelContainer.setComponentAlignment(psmNumberFilter, Alignment.TOP_CENTER);

        filterRightPanelContainer.addComponent(coverageFilter);
        coverageFilter.addStyleName("bottomfilter");
        coverageFilter.addStyleName("correctresetbtn");
        filterRightPanelContainer.setComponentAlignment(coverageFilter, Alignment.TOP_RIGHT);

//        proteinsPathwayNewtorkGraph = new SelectionGraph();
//        proteinsPathwayNewtorkGraph.setSizeFull();
//        filterMiddlePanelContainer.addComponent(proteinsPathwayNewtorkGraph);
    }
    private final Color[] colorsArrII = new Color[]{Color.DARK_GRAY, new Color(4, 180, 95), Color.ORANGE, new Color(213, 8, 8)};

    public void updateFiltersData(ModificationMatrix modificationMatrix, Map<String, Color> modificationsColorMap, Map<Integer, Set<Comparable>> chromosomeMap, Map<String, Set<Comparable>> piMap, Map<String, Set<Comparable>> proteinValidationMap, TreeMap<Comparable, Set<Comparable>> proteinPeptidesNumberMap, TreeMap<Comparable, Set<Comparable>> proteinPSMNumberMap, TreeMap<Comparable, Set<Comparable>> proteinCoverageMap) {
        Selection_Manager.reset();
        Selection_Manager.setModificationsMap(modificationMatrix);
        modificationFilter.initializeFilterData(modificationMatrix, modificationsColorMap, new HashSet<>(), Selection_Manager.getFullProteinSet().size());

        chromosomeFilter.initializeFilterData(chromosomeMap);
        Selection_Manager.setChromosomeMap(chromosomeMap);

        ProteinInferenceFilter.initializeFilterData(piMap, PIColorMap);//colorList.subList(0, piMap.size()).toArray(new Color[piMap.size()])
        Selection_Manager.setPiMap(piMap);
        Selection_Manager.setProteinValidationMap(proteinValidationMap);

        validationFilter.initializeFilterData(proteinValidationMap, new ArrayList<>(Arrays.asList(colorsArrII)));//colorList.subList(0, proteinValidationMap.size()).toArray(new Color[proteinValidationMap.size()])
        Selection_Manager.setProteinCoverageMap(proteinCoverageMap);
        Selection_Manager.setProteinPSMNumberMap(proteinPSMNumberMap);
        Selection_Manager.setProteinPeptidesNumberMap(proteinPeptidesNumberMap);
        prptidesNumberFilter.initializeFilterData(proteinPeptidesNumberMap);
        psmNumberFilter.initializeFilterData(proteinPSMNumberMap);
        coverageFilter.initializeFilterData(proteinCoverageMap);
        
//         Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                proteinsPathwayNewtorkGraph.updateGraph();
//            }
//        }
//        );
//        t.start();
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
