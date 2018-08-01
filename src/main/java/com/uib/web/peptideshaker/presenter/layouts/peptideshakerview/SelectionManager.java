package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview;

import com.google.common.collect.Sets;
import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideObject;
import com.uib.web.peptideshaker.model.core.FilteredProteins;
import com.uib.web.peptideshaker.model.core.ModificationMatrix;
import com.uib.web.peptideshaker.presenter.core.BigSideBtn;
import com.uib.web.peptideshaker.presenter.core.filtercharts.charts.RegistrableFilter;
import com.vaadin.ui.Component;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * This class represents peptideShaker presenter selection manager
 *
 * @author Yehia Farag
 */
public class SelectionManager {

    private final Map<BigSideBtn, Component> btnsLayoutMap;
    private final Map<String, RegistrableFilter> registeredDatasetFiltersMap;
    private final Map<String, RegistrableFilter> registeredProteinComponentsMap;
    private final Set<Comparable> fullProteinSet;
    private FilteredProteins filteredProteinsSet;
    private final List<String> datasetFilterOrderList;

    public String getSelectedProteinId() {
        return selectedProteinId;
    }
    private String selectedProteinId;
     private PeptideObject selectedPeptide;

    private boolean singleProteinsFilter = false;

    public Set<Comparable> getFullProteinSet() {
        return fullProteinSet;
    }
    private ModificationMatrix modificationMatrix;
    private Map<Integer, Set<Comparable>> chromosomeMap;

    private final Map<String, Set<Comparable>> selectedModificationsMap;
    private final Map<Integer, Set<Comparable>> selectedChromosomeMap;
    private Map<String, Set<Comparable>> piMap;
    private final Map<String, Set<Comparable>> selectedPIMap;

    private Map<String, Set<Comparable>> proteinValidationMap;
    private final Map<String, Set<Comparable>> selectedProteinValidationMap;

    private final Map<String, Set<Comparable>> registeredDatasetAppliedFiltersMap;

    private TreeMap<Comparable, Set<Comparable>> proteinPeptidesNumberMap;
    private TreeMap<Comparable, Set<Comparable>> proteinPSMNumberMap;
    private TreeMap<Comparable, Set<Comparable>> proteinCoverageMap;

    public void setProteinPeptidesNumberMap(TreeMap<Comparable, Set<Comparable>> proteinPeptidesNumberMap) {
        this.selectedProteinPeptidesNumberMap.clear();
        this.proteinPeptidesNumberMap = proteinPeptidesNumberMap;
    }

    public void setProteinPSMNumberMap(TreeMap<Comparable, Set<Comparable>> proteinPSMNumberMap) {
        this.selectedProteinPSMNumberMap.clear();
        this.proteinPSMNumberMap = proteinPSMNumberMap;
    }

    public void setProteinCoverageMap(TreeMap<Comparable, Set<Comparable>> proteinCoverageMap) {
        this.selectedProteinCoverageMap.clear();
        this.proteinCoverageMap = proteinCoverageMap;
    }

    public TreeMap<Comparable, Set<Comparable>> getProteinPeptidesNumberMap() {
        if (selectedProteinPeptidesNumberMap.isEmpty()) {
            return selectedProteinPeptidesNumberMap;
        } else {
            return proteinPeptidesNumberMap;
        }
    }

    public TreeMap<Comparable, Set<Comparable>> getProteinPSMNumberMap() {
        if (selectedProteinPSMNumberMap.isEmpty()) {
            return selectedProteinPSMNumberMap;
        } else {
            return proteinPSMNumberMap;
        }
    }

    public TreeMap<Comparable, Set<Comparable>> getProteinCoverageMap() {
        if (selectedProteinCoverageMap.isEmpty()) {
            return selectedProteinCoverageMap;
        } else {
            return proteinCoverageMap;
        }
    }

    private final TreeMap<Comparable, Set<Comparable>> selectedProteinPeptidesNumberMap;
    private final TreeMap<Comparable, Set<Comparable>> selectedProteinPSMNumberMap;
    private final TreeMap<Comparable, Set<Comparable>> selectedProteinCoverageMap;

    public void setChromosomeMap(Map<Integer, Set<Comparable>> chromosomeMap) {
        this.selectedChromosomeMap.clear();
        this.chromosomeMap = chromosomeMap;
    }

    public void setPiMap(Map<String, Set<Comparable>> piMap) {
        this.selectedPIMap.clear();
        this.piMap = piMap;
    }

    public Map<String, Set<Comparable>> getPiMap() {
        if (selectedPIMap.isEmpty()) {
            return piMap;
        } else {
            return selectedPIMap;
        }
    }

    public Map<String, Set<Comparable>> getProteinValidationMap() {
        if (selectedProteinValidationMap.isEmpty()) {
            return proteinValidationMap;
        } else {
            return selectedProteinValidationMap;
        }
    }

    public void setProteinValidationMap(Map<String, Set<Comparable>> proteinValidationMap) {
        this.selectedProteinValidationMap.clear();
        this.proteinValidationMap = proteinValidationMap;
    }

    public Map<Integer, Set<Comparable>> getChromosomeMap() {
        if (selectedChromosomeMap.isEmpty()) {
            return chromosomeMap;
        } else {
            return selectedChromosomeMap;
        }
    }

//    public ModificationMatrix getModificationsMap() {
//        if (selectedModificationsMap.isEmpty()) {
//            return modificationMatrix;
//        } else {
//            return selectedModificationsMap;
//        }
//    }
    public void reset() {
        fullProteinSet.clear();
        selectedChromosomeMap.clear();
        selectedModificationsMap.clear();
        selectedPIMap.clear();
        selectedProteinValidationMap.clear();
        modificationMatrix = null;
        piMap = null;
        chromosomeMap = null;
        proteinValidationMap = null;
        registeredDatasetAppliedFiltersMap.keySet().forEach((filterId) -> {
            registeredDatasetAppliedFiltersMap.get(filterId).clear();
        });

    }

    public void resetDatasetSelection() {
        datasetFilterOrderList.forEach((filterId) -> {
            registeredDatasetAppliedFiltersMap.get(filterId).clear();
        });
        filteredProteinsSet = new FilteredProteins();
        filteredProteinsSet.setWithoutTopFilterList(new LinkedHashSet<>(fullProteinSet));
        filteredProteinsSet.setWithTopFilterList(new LinkedHashSet<>(fullProteinSet));
        SelectionChanged("dataset_filter_selection", "reset");

    }

    public void setModificationsMap(ModificationMatrix modificationMatrix) {
        selectedModificationsMap.clear();
        this.modificationMatrix = modificationMatrix;
        modificationMatrix.getCalculatedColumns().values().forEach((set) -> {
            fullProteinSet.addAll(set);
        });
    }

    public SelectionManager() {
        this.btnsLayoutMap = new LinkedHashMap<>();
        this.registeredDatasetFiltersMap = new LinkedHashMap<>();
        this.registeredProteinComponentsMap = new LinkedHashMap<>();
        this.selectedModificationsMap = new LinkedHashMap<>();
        this.selectedChromosomeMap = new LinkedHashMap<>();
        this.selectedPIMap = new LinkedHashMap<>();
        this.selectedProteinValidationMap = new LinkedHashMap<>();
        this.fullProteinSet = new LinkedHashSet<>();
        this.registeredDatasetAppliedFiltersMap = new LinkedHashMap<>();
        this.selectedProteinCoverageMap = new TreeMap<>();
        this.selectedProteinPeptidesNumberMap = new TreeMap<>();
        this.selectedProteinPSMNumberMap = new TreeMap<>();
        this.datasetFilterOrderList = new ArrayList<>();
    }

    public void addBtnLayout(BigSideBtn btn, Component layout) {
        btnsLayoutMap.put(btn, layout);
    }

    public void selectBtn(BigSideBtn btn) {
        btnsLayoutMap.keySet().forEach((bbt) -> {            
            if (btn.getData().toString().equalsIgnoreCase(bbt.getData().toString())) {
                bbt.setSelected(true);
                btnsLayoutMap.get(bbt).removeStyleName("hidepanel");
            } else {
                bbt.setSelected(false);
                btnsLayoutMap.get(bbt).addStyleName("hidepanel");
            }
        });

    }

    public void selectBtn(int index) {
        BigSideBtn btn = (BigSideBtn) btnsLayoutMap.keySet().toArray()[index];
        selectBtn(btn);

    }

    public void RegistrDatasetsFilter(RegistrableFilter filter) {
        this.registeredDatasetFiltersMap.put(filter.getFilterId(), filter);
        this.registeredDatasetAppliedFiltersMap.put(filter.getFilterId(), new LinkedHashSet<>());
        this.datasetFilterOrderList.add(filter.getFilterId());
    }

    public void RegistrProteinInformationComponent(RegistrableFilter filter) {
        registeredProteinComponentsMap.put(filter.getFilterId(), filter);
//        this.registeredDatasetFiltersMap.put(filter.getFilterId(), filter);
//        this.registeredDatasetAppliedFiltersMap.put(filter.getFilterId(), new LinkedHashSet<>());
//        this.datasetFilterOrderList.add(filter.getFilterId());
    }

    /**
     * Set Selection in the system to update other registered listeners  
     * @return 
     */
    public PeptideObject getSelectedPeptide() {

        return selectedPeptide;

    }

    public void setSelectedPeptide(PeptideObject selectedPeptide) {
        this.selectedPeptide = selectedPeptide;
    }
    

    public void setSelection(String selectionType, Set<Comparable> filteringValue, Set<Comparable> filteredItemsSet, String filterId) {
        if (selectionType.equalsIgnoreCase("dataset_filter_selection")) {
            datasetFilterOrderList.remove(filterId);
            if (filteringValue.isEmpty()) {
                datasetFilterOrderList.add(filterId);//               
            } else {
                datasetFilterOrderList.add(0, filterId);
            }

            registeredDatasetAppliedFiltersMap.put(filterId, filteringValue);
            filteredProteinsSet = filterProteinData();
        } else if (selectionType.equalsIgnoreCase("protein_selection")) {
            selectedProteinId = (String)filteringValue.toArray()[0];
        }else if (selectionType.equalsIgnoreCase("peptide_selection")) {
//            selectedPeptide = (String)filteringValue.toArray()[0];

        }
        
        SelectionChanged(selectionType, filterId);
    }

    private FilteredProteins filterProteinData() {

        String topFilterId = datasetFilterOrderList.get(0);
        Set<Comparable> filteredProtenSet = new LinkedHashSet<>(fullProteinSet);
        Set<Comparable> tempProtenSet = new LinkedHashSet<>();
        Integer onlyFilter = 0;
        registeredDatasetAppliedFiltersMap.keySet().stream().filter((filterId) -> !(filterId.equals(topFilterId))).forEachOrdered((filterId) -> {
            proteinFilterUtility(filterId, onlyFilter, tempProtenSet, filteredProtenSet);
        });
        FilteredProteins filteredProteinList = new FilteredProteins();
        filteredProteinList.setWithoutTopFilterList(new LinkedHashSet<>(filteredProtenSet));
        proteinFilterUtility(topFilterId, onlyFilter, tempProtenSet, filteredProtenSet);
        filteredProteinList.setWithTopFilterList(new LinkedHashSet<>(filteredProtenSet));

        singleProteinsFilter = onlyFilter == 1;
        return filteredProteinList;

    }

    private void proteinFilterUtility(String filterId, Integer onlyFilter, Set<Comparable> tempProtenSet, Set<Comparable> filteredProtenSet) {

        Set<Comparable> selectedCategories = registeredDatasetAppliedFiltersMap.get(filterId);
        if (!selectedCategories.isEmpty()) {
            onlyFilter++;
        }
        if (filterId.equalsIgnoreCase("modifications_filter") && !selectedCategories.isEmpty()) {
            selectedCategories.stream().map((str) -> {
                tempProtenSet.addAll(Sets.difference(filteredProtenSet, this.modificationMatrix.getCalculatedColumns().get(str.toString())));
                return str;
            }).map((_item) -> {
                filteredProtenSet.removeAll(tempProtenSet);
                return _item;
            }).forEachOrdered((_item) -> {
                tempProtenSet.clear();
            });
        } else if (filterId.equalsIgnoreCase("chromosome_filter") && !selectedCategories.isEmpty()) {
            Set<Comparable> selectedData = new LinkedHashSet<>();
            selectedCategories.stream().filter((cat) -> !(cat == null)).forEachOrdered((cat) -> {
                selectedData.addAll(Sets.intersection(chromosomeMap.get(Integer.valueOf(cat.toString())), filteredProtenSet));
            });
            tempProtenSet.addAll(Sets.difference(filteredProtenSet, selectedData));
            filteredProtenSet.removeAll(tempProtenSet);
            tempProtenSet.clear();

        } else if (filterId.equalsIgnoreCase("PI_filter") && !selectedCategories.isEmpty()) {
            Set<Comparable> selectedData = new LinkedHashSet<>();
            selectedCategories.stream().filter((cat) -> !(cat == null)).forEachOrdered((cat) -> {
                selectedData.addAll(Sets.intersection(piMap.get(cat.toString()), filteredProtenSet));
            });
            tempProtenSet.addAll(Sets.difference(filteredProtenSet, selectedData));
            filteredProtenSet.removeAll(tempProtenSet);
            tempProtenSet.clear();

        } else if (filterId.equalsIgnoreCase("validation_filter") && !selectedCategories.isEmpty()) {
            Set<Comparable> selectedData = new LinkedHashSet<>();
            selectedCategories.stream().filter((cat) -> !(cat == null)).forEachOrdered((cat) -> {
                selectedData.addAll(Sets.intersection(proteinValidationMap.get(cat.toString()), filteredProtenSet));
            });
            tempProtenSet.addAll(Sets.difference(filteredProtenSet, selectedData));
            filteredProtenSet.removeAll(tempProtenSet);
            tempProtenSet.clear();

        } else if (filterId.equalsIgnoreCase("peptidesNum_filter") && !selectedCategories.isEmpty()) {
            Set<Comparable> selectedData = new LinkedHashSet<>();
            double min = (double) selectedCategories.toArray()[0];
            double max;//= (double) selectedCategories.toArray()[1];
            if (selectedCategories.size() == 1) {
                max = min;
            } else {
                max = (double) selectedCategories.toArray()[1];
            }
            proteinPeptidesNumberMap.keySet().forEach((cat) -> {
                int key = (int) cat;
                if (key >= min && key <= max) {
                    selectedData.addAll(Sets.intersection(proteinPeptidesNumberMap.get(cat), filteredProtenSet));
                }
            });
            tempProtenSet.addAll(Sets.difference(filteredProtenSet, selectedData));
            filteredProtenSet.removeAll(tempProtenSet);
            tempProtenSet.clear();

        } else if (filterId.equalsIgnoreCase("psmNum_filter") && !selectedCategories.isEmpty()) {
            Set<Comparable> selectedData = new LinkedHashSet<>();
            double min = (double) selectedCategories.toArray()[0];
            double max;//= (double) selectedCategories.toArray()[1];
            if (selectedCategories.size() == 1) {
                max = min;
            } else {
                max = (double) selectedCategories.toArray()[1];
            }
            proteinPSMNumberMap.keySet().forEach((cat) -> {
                int key = (int) cat;
                if (key >= min && key <= max) {
                    selectedData.addAll(Sets.intersection(proteinPSMNumberMap.get(cat), filteredProtenSet));
                }
            });
            tempProtenSet.addAll(Sets.difference(filteredProtenSet, selectedData));
            filteredProtenSet.removeAll(tempProtenSet);
            tempProtenSet.clear();

        } else if (filterId.equalsIgnoreCase("possibleCoverage_filter") && !selectedCategories.isEmpty()) {
            Set<Comparable> selectedData = new LinkedHashSet<>();
            double min = (double) selectedCategories.toArray()[0];
            double max;//= (double) selectedCategories.toArray()[1];
            if (selectedCategories.size() == 1) {
                max = min;
            } else {
                max = (double) selectedCategories.toArray()[1];
            }
            proteinCoverageMap.keySet().forEach((cat) -> {
                int key = (int) cat;
                if (key >= min && key <= max) {
                    selectedData.addAll(Sets.intersection(proteinCoverageMap.get(cat), filteredProtenSet));
                }
            });
            tempProtenSet.addAll(Sets.difference(filteredProtenSet, selectedData));
            filteredProtenSet.removeAll(tempProtenSet);
            tempProtenSet.clear();

        }

    }

    public boolean isSingleProteinsFilter() {
        return singleProteinsFilter;
    }

    public boolean isDatasetFilterApplied() {
        return (filteredProteinsSet.getWithTopFilterList().size() != fullProteinSet.size());

    }

    /**
     * Loop responsible for updating all registered listeners
     *
     * @param type selection type
     * @param filterId filter that create the event
     */
    private void SelectionChanged(String selectionType, String actionFilterId) {

        if (selectionType.equalsIgnoreCase("dataset_filter_selection")) {
            for (int index = 0; index < datasetFilterOrderList.size(); index++) {
                String filterId = datasetFilterOrderList.get(index);
                if (index == 0) {
                    registeredDatasetFiltersMap.get(filterId).updateFilterSelection(filteredProteinsSet.getWithoutTopFilterList(), registeredDatasetAppliedFiltersMap.get(filterId), (index == 0), singleProteinsFilter, (filterId.equalsIgnoreCase(actionFilterId)));
                } else {
                    registeredDatasetFiltersMap.get(filterId).updateFilterSelection(filteredProteinsSet.getWithTopFilterList(), registeredDatasetAppliedFiltersMap.get(filterId), (index == 0), singleProteinsFilter, (filterId.equalsIgnoreCase(actionFilterId)));
                }
//                
            }

        } else if (selectionType.equalsIgnoreCase("protein_selection")||selectionType.equalsIgnoreCase("peptide_selection")) {
            registeredProteinComponentsMap.keySet().forEach((filterId) -> {
                registeredProteinComponentsMap.get(filterId).selectionChange(selectionType);
            });
        }
    }

    public Set<Comparable> getAppliedFilterCategories(String filterId) {

        return registeredDatasetAppliedFiltersMap.get(filterId);

    }

//    public Set<Comparable> getFilteredProteinsSet() {
//        if (filteredProteinsSet == null) {
//            System.out.println("there is a null selected proted");
//        }
//        return filteredProteinsSet;
//    }
}
