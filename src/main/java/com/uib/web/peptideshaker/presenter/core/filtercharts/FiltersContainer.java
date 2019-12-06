package com.uib.web.peptideshaker.presenter.core.filtercharts;

import com.uib.web.peptideshaker.model.core.ModificationMatrix;
import com.uib.web.peptideshaker.presenter.core.filtercharts.filters.ChromosomesFilter;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.SelectionManager;
import com.uib.web.peptideshaker.presenter.core.filtercharts.filters.DivaPieChartFilter;
import com.uib.web.peptideshaker.presenter.core.filtercharts.filters.DivaRangeFilter;
import com.uib.web.peptideshaker.presenter.core.filtercharts.filters.ModificationsFilter;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
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

    private final DivaRangeFilter peptidesNumberFilter;
    private final DivaRangeFilter coverageFilter;
    private final DivaRangeFilter psmNumberFilter;

    private final AbsoluteLayout intinsityContainer;
    private final DivaRangeFilter intensityAllPeptidesRange;
    private final DivaRangeFilter intensityUniquePeptidesRange;

    private final VerticalLayout filterRightPanelContainer;
    private final VerticalLayout filterLeftPanelContainer;

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
              @Override
            public void filterSizeChanged(int w, int h) {
            }

        };
        ProteinInferenceFilter.addStyleName("pifilter");
        filterLeftPanelContainer = new VerticalLayout();
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

            @Override
            public void filterSizeChanged(int w, int h) {
               ProteinInferenceFilter.sizeChanged(w, h);
            }
        };
        validationFilter.addStyleName("validationfilter");
        filterLeftPanelContainer.addComponent(validationFilter);
        filterLeftPanelContainer.setComponentAlignment(validationFilter, Alignment.TOP_CENTER);
        chromosomeFilter = new ChromosomesFilter("Chromosome", "chromosome_filter", Selection_Manager) {
            @Override
            public void selectionChange(String type) {
                if (type.equalsIgnoreCase("dataset_filter_selection")) {
                }
            }
        };
        chromosomeFilter.addStyleName("chromfilter");
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

        filterRightPanelContainer = new VerticalLayout();
        filterRightPanelContainer.setHeight(100, Unit.PERCENTAGE);
        filterRightPanelContainer.setWidth(100, Unit.PERCENTAGE);
        filterRightPanelContainer.setSpacing(false);
        filterRightPanelContainer.addStyleName("cornerfiltercontainerstyle");
        filterRightPanelContainer.addStyleName("rightpanelfilter");
        filterRightPanelContainer.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
        FiltersContainer.this.addComponent(filterRightPanelContainer);
        FiltersContainer.this.setComponentAlignment(filterRightPanelContainer, Alignment.TOP_LEFT);
        FiltersContainer.this.setExpandRatio(filterRightPanelContainer, 1);

        //add range filter
        intensityAllPeptidesRange = new DivaRangeFilter("Intensity (% - All peptides)", "intensityAllPep_filter", this.Selection_Manager) {
            @Override
            public void selectionChange(String type) {

            }

            @Override
            public void setVisible(boolean visible) {
                intensityUniquePeptidesRange.setVisible(!visible);
                super.setVisible(visible);
            }

        };
        intensityAllPeptidesRange.addStyleName("intallpepfilter");
        intensityUniquePeptidesRange = new DivaRangeFilter("Intensity (% - Unique peptides)", "intensityUniquePep_filter", this.Selection_Manager) {
            @Override
            public void selectionChange(String type) {

            }
        };
        intensityUniquePeptidesRange.addStyleName("intuniquepepfilter");
        intinsityContainer = new AbsoluteLayout();
        intinsityContainer.setSizeFull();
        intinsityContainer.addComponent(intensityAllPeptidesRange, "left:0px;top:5px;bottom:0px !important;right:0px;");
        intensityAllPeptidesRange.setVisible(true);
        intinsityContainer.addComponent(intensityUniquePeptidesRange, "left:0px;top:5px;bottom:0px;right:0px;");
        intinsityContainer.addStyleName("intinsfilter");
        peptidesNumberFilter = new DivaRangeFilter("#Peptides", "peptidesNum_filter", this.Selection_Manager) {
            @Override
            public void selectionChange(String type) {
            }

        };
        peptidesNumberFilter.addStyleName("pepNumfilter");
        filterRightPanelContainer.addComponent(peptidesNumberFilter);
        filterRightPanelContainer.setComponentAlignment(peptidesNumberFilter, Alignment.TOP_LEFT);

        psmNumberFilter = new DivaRangeFilter("#PSMs", "psmNum_filter", this.Selection_Manager) {
            @Override
            public void selectionChange(String type) {
            }

        };
        psmNumberFilter.addStyleName("psmnumfilter");
        filterRightPanelContainer.addComponent(psmNumberFilter);
        filterRightPanelContainer.setComponentAlignment(psmNumberFilter, Alignment.TOP_CENTER);
//        TabSheet tabFilterContainer = new TabSheet();
//        tabFilterContainer.setHeight(100.0f, Unit.PERCENTAGE);
//        tabFilterContainer.addStyleName(ValoTheme.TABSHEET_COMPACT_TABBAR);
//        tabFilterContainer.addStyleName(ValoTheme.TABSHEET_FRAMED);
//        tabFilterContainer.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
//        tabFilterContainer.addStyleName("tabsheetinfilter");
//        tabFilterContainer.setTabCaptionsAsHtml(true);
//        tabFilterContainer.addTab(psmNumberFilter,psmNumberFilter.externalTitle());
//        filterRightPanelContainer.addComponent(tabFilterContainer);
//        filterRightPanelContainer.setComponentAlignment(tabFilterContainer, Alignment.TOP_CENTER);
        coverageFilter = new DivaRangeFilter("Coverage (%)", "possibleCoverage_filter", this.Selection_Manager) {
            @Override
            public void selectionChange(String type) {
            }

        };
        coverageFilter.addStyleName("coveragefilter");
        filterRightPanelContainer.addComponent(coverageFilter);
        coverageFilter.addStyleName("bottomfilter");
        coverageFilter.addStyleName("correctresetbtn");
        filterRightPanelContainer.setComponentAlignment(coverageFilter, Alignment.TOP_CENTER);

        filterRightPanelContainer.addComponent(intinsityContainer);
        filterRightPanelContainer.setComponentAlignment(intinsityContainer, Alignment.BOTTOM_RIGHT);

    }
    private final Color[] colorsArrII = new Color[]{Color.DARK_GRAY, new Color(4, 180, 95), Color.ORANGE, new Color(213, 8, 8)};

    public void updateFiltersData(ModificationMatrix modificationMatrix, Map<String, Color> modificationsColorMap, Map<Integer, Set<Comparable>> chromosomeMap, Map<String, Set<Comparable>> piMap, Map<String, Set<Comparable>> proteinValidationMap, TreeMap<Comparable, Set<Comparable>> proteinPeptidesNumberMap, TreeMap<Comparable, Set<Comparable>> proteinPSMNumberMap, TreeMap<Comparable, Set<Comparable>> proteinCoverageMap, TreeMap<Comparable, Set<Comparable>> proteinIntinsityAllPepMap, TreeMap<Comparable, Set<Comparable>> proteinIntinsityUniquePepMap) {
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
        Selection_Manager.setProteinIntinsityAllPepMap(proteinIntinsityAllPepMap);
        Selection_Manager.setProteinIntinsityUniquePepMap(proteinIntinsityUniquePepMap);
        Selection_Manager.setProteinPSMNumberMap(proteinPSMNumberMap);
        Selection_Manager.setProteinPeptidesNumberMap(proteinPeptidesNumberMap);
        peptidesNumberFilter.initializeFilterData(proteinPeptidesNumberMap);
        psmNumberFilter.initializeFilterData(proteinPSMNumberMap);
        coverageFilter.initializeFilterData(proteinCoverageMap);
        if (proteinIntinsityAllPepMap.isEmpty()) {
            Label noquant = new Label("<center> No quant data available </center>", ContentMode.HTML);
            noquant.setSizeFull();
            noquant.setStyleName("noquantlabel");
            intensityAllPeptidesRange.addComponent(noquant);
            intensityUniquePeptidesRange.suspendFilter(true);
            intensityAllPeptidesRange.suspendFilter(true);

        } else {
            intensityAllPeptidesRange.initializeFilterData(proteinIntinsityAllPepMap);
            intensityUniquePeptidesRange.initializeFilterData(proteinIntinsityUniquePepMap);
        }

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

    public void updateQuantFilter(boolean allPeptideInteinsity) {
        this.intensityAllPeptidesRange.setVisible(allPeptideInteinsity);

    }

    public DivaPieChartFilter getValidationFilter() {
        return validationFilter;
    }

    public DivaPieChartFilter getProteinInferenceFilter() {
        return ProteinInferenceFilter;
    }

    public ChromosomesFilter getChromosomeFilter() {
        return chromosomeFilter;
    }

    public ModificationsFilter getModificationFilter() {
        return modificationFilter;
    }

    public VerticalLayout getFilterRightPanelContainer() {
        return filterRightPanelContainer;
    }

    public VerticalLayout getFilterLeftPanelContainer() {
        return filterLeftPanelContainer;
    }

    public DivaRangeFilter getPeptidesNumberFilter() {
        return peptidesNumberFilter;
    }

    public DivaRangeFilter getPsmNumberFilter() {
        return psmNumberFilter;
    }

    public DivaRangeFilter getIntensityAllPeptidesRange() {
        return intensityAllPeptidesRange;
    }

    public DivaRangeFilter getIntensityUniquePeptidesRange() {
        return intensityUniquePeptidesRange;
    }

}
