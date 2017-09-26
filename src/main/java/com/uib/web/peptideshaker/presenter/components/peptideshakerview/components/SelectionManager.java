package com.uib.web.peptideshaker.presenter.components.peptideshakerview.components;

import com.google.common.collect.Sets;
import com.uib.web.peptideshaker.model.core.ModificationMatrix;
import com.uib.web.peptideshaker.presenter.core.BigSideBtn;
import com.uib.web.peptideshaker.presenter.core.filtercharts.updatedfilters.DivaMatrixLayoutChartFilter;
import com.uib.web.peptideshaker.presenter.core.filtercharts.charts.RegistrableFilter;
import com.vaadin.ui.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class represents peptideShaker presenter selection manager
 *
 * @author Yehia Farag
 */
public class SelectionManager {

    private final Map<BigSideBtn, Component> btnsLayoutMap;
    private final Map<String, RegistrableFilter> registeredFiltersMap;
    private final Set<Comparable> fullProteinSet;
    private Set<Comparable> filteredProteinsSet;
    private final List<String> filterOrderList;

    private boolean singleProteinsFilter = false;

    public Set<Comparable> getFullProteinSet() {
        return fullProteinSet;
    }
    private ModificationMatrix modificationMatrix;
    private Map<String, Set<Comparable>> chromosomeMap;

    private final Map<String, Set<Comparable>> selectedModificationsMap;
    private final Map<String, Set<Comparable>> selectedChromosomeMap;
    private Map<String, Set<Comparable>> piMap;
    private final Map<String, Set<Comparable>> selectedPIMap;

    private Map<String, Set<Comparable>> proteinValidationMap;
    private final Map<String, Set<Comparable>> selectedProteinValidationMap;

    private final Map<String, Set<Comparable>> registeredAppliedFiltersMap;

    public void setChromosomeMap(Map<String, Set<Comparable>> chromosomeMap) {
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

    public Map<String, Set<Comparable>> getChromosomeMap() {
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
        for (String filterId : registeredAppliedFiltersMap.keySet()) {
            registeredAppliedFiltersMap.get(filterId).clear();
        }

    }

    public void setModificationsMap(ModificationMatrix modificationMatrix) {
        selectedModificationsMap.clear();
        this.modificationMatrix = modificationMatrix;
        for (Set<Comparable> set : modificationMatrix.getCalculatedMatrix().values()) {
            fullProteinSet.addAll(set);
        }
    }

    public SelectionManager() {
        this.btnsLayoutMap = new LinkedHashMap<>();
        this.registeredFiltersMap = new LinkedHashMap<>();
        this.selectedModificationsMap = new LinkedHashMap<>();
        this.selectedChromosomeMap = new LinkedHashMap<>();
        this.selectedPIMap = new LinkedHashMap<>();
        this.selectedProteinValidationMap = new LinkedHashMap<>();
        this.fullProteinSet = new LinkedHashSet<>();
        this.registeredAppliedFiltersMap = new LinkedHashMap<>();
        this.filterOrderList = new ArrayList<>();
    }

    public void addBtnLayout(BigSideBtn btn, Component layout) {
        btnsLayoutMap.put(btn, layout);
    }

    public void selectBtn(BigSideBtn btn) {
        for (BigSideBtn bbt : btnsLayoutMap.keySet()) {
            if (btn.getData().toString().equalsIgnoreCase(bbt.getData().toString())) {
                bbt.setSelected(true);
                btnsLayoutMap.get(bbt).removeStyleName("hidepanel");
            } else {
                bbt.setSelected(false);
                btnsLayoutMap.get(bbt).addStyleName("hidepanel");
            }
        }

    }

    public void selectBtn(int index) {
        BigSideBtn btn = (BigSideBtn) btnsLayoutMap.keySet().toArray()[index];
        selectBtn(btn);

    }

    public void RegistrFilter(RegistrableFilter filter) {
        this.registeredFiltersMap.put(filter.getFilterId(), filter);
        this.registeredAppliedFiltersMap.put(filter.getFilterId(), new LinkedHashSet<>());
        this.filterOrderList.add(filter.getFilterId());
    }

    /**
     * Set Selection in the system to update other registered listeners
     *
     * @param selectionType the type of the event
     * @param filteringValue set of selected value ids
     * @param filterId filter that create the event
     */
    public void setSelection(String selectionType, Set<Comparable> filteringValue, Set<Comparable> filteredItemsSet, String filterId) {

        if (selectionType.equalsIgnoreCase("protein_selection")) {
            filterOrderList.remove(filterId);
            if (filteringValue.isEmpty()) {
                filterOrderList.add(filterId);//               
            } else {
                filterOrderList.add(0, filterId);
            }

            registeredAppliedFiltersMap.put(filterId, filteringValue);
            filteredProteinsSet = filterProteinData();
////                filteredProteinsSet = filteredItemsSet;            } 

//            if (filterId.equalsIgnoreCase("modifications_filter")) {
//                registeredAppliedFiltersMap.get(filterId).clear();
//                DivaMatrixLayoutChartFilter filter = (DivaMatrixLayoutChartFilter) registeredFiltersMap.get(filterId);
//                for (Object obj : filter.getSelectedCategories()) {                    
//                    registeredAppliedFiltersMap.get(filterId).add(filter.getCalculatedMatrix().keySet().toArray()[(int) obj]);
//                }
//            } else {
//              registeredAppliedFiltersMap.put(filterId, registeredFiltersMap.get(filterId).getSelectedCategories());
//            }
//
//            for (String id : registeredAppliedFiltersMap.keySet()) {
//                Set<Object> categories = registeredAppliedFiltersMap.get(id);
//                if (!categories.isEmpty()) {
//                    usedFilter++;
//                }
//            }
//            singleProteinsFilter = (usedFilter < 2);
//              
//            if (usedFilter > 0 && registeredAppliedFiltersMap.get(filterId).isEmpty()) {
//                filterId = "";
//            }
//                
//            filteredProteinsSet = filterProteinData();
//            selectedChromosomeMap.clear();
//            for (String key : chromosomeMap.keySet()) {
//                Set<Comparable> inter = new LinkedHashSet<>(Sets.intersection(filteredProteinsSet, chromosomeMap.get(key)));
//                selectedChromosomeMap.put(key, inter);
//            }
//            selectedPIMap.clear();
//            for (String key : piMap.keySet()) {
//                Set<Comparable> inter = new LinkedHashSet<>(Sets.intersection(filteredProteinsSet, piMap.get(key)));
//                selectedPIMap.put(key, inter);
//            }
//              selectedProteinValidationMap.clear();
//            for (String key : proteinValidationMap.keySet()) {
//                Set<Comparable> inter = new LinkedHashSet<>(Sets.intersection(filteredProteinsSet,proteinValidationMap.get(key)));
//                selectedProteinValidationMap.put(key, inter);
//            }
//            selectedModificationsMap.clear();
//            for (String key : modificationsMap.keySet()) {
//                Set<Comparable> inter = new LinkedHashSet<>();
//                inter.addAll(Sets.intersection(filteredProteinsSet, modificationsMap.get(key)));
//                if (!inter.isEmpty()) {
//                    selectedModificationsMap.put(key, inter);
//                }
//            }
        } else {

        }
        SelectionChanged(selectionType, filterId);

    }

    private Set<Comparable> filterProteinData() {

        Set<Comparable> filteredProtenSet = new LinkedHashSet<>(fullProteinSet);
        Set<Comparable> tempProtenSet = new LinkedHashSet<>();
        int onlyFilter = 0;
        for (String filterId : registeredAppliedFiltersMap.keySet()) {
            Set<Comparable> selectedCategories = registeredAppliedFiltersMap.get(filterId);
            if (!selectedCategories.isEmpty()) {
                onlyFilter++;
            }
            if (filterId.equalsIgnoreCase("modifications_filter") && !selectedCategories.isEmpty()) {
                for (Comparable str : selectedCategories) {
                    tempProtenSet.addAll(Sets.difference(filteredProtenSet, this.modificationMatrix.getCalculatedMatrix().get(str.toString())));
                    filteredProtenSet.removeAll(tempProtenSet);
                    tempProtenSet.clear();
                }
            } else if (filterId.equalsIgnoreCase("chromosome_filter") && !selectedCategories.isEmpty()) {
                Set<Comparable> selectedData = new LinkedHashSet<>();
                for (Comparable cat : selectedCategories) {
                    if (cat == null) {
                        continue;
                    }
                    selectedData.addAll(Sets.intersection(chromosomeMap.get(cat.toString()), filteredProtenSet));
                }
                tempProtenSet.addAll(Sets.difference(filteredProtenSet, selectedData));
                filteredProtenSet.removeAll(tempProtenSet);
                tempProtenSet.clear();

            } else if (filterId.equalsIgnoreCase("PI_filter") && !selectedCategories.isEmpty()) {
                Set<Comparable> selectedData = new LinkedHashSet<>();
                for (Comparable cat : selectedCategories) {
                    if (cat == null) {
                        continue;
                    }
                    selectedData.addAll(Sets.intersection(piMap.get(cat.toString()), filteredProtenSet));
                }
                tempProtenSet.addAll(Sets.difference(filteredProtenSet, selectedData));
                filteredProtenSet.removeAll(tempProtenSet);
                tempProtenSet.clear();

            } else if (filterId.equalsIgnoreCase("validation_filter") && !selectedCategories.isEmpty()) {
                Set<Comparable> selectedData = new LinkedHashSet<>();
                for (Comparable cat : selectedCategories) {
                    if (cat == null) {
                        continue;
                    }
                    selectedData.addAll(Sets.intersection(proteinValidationMap.get(cat.toString()), filteredProtenSet));
                }
                tempProtenSet.addAll(Sets.difference(filteredProtenSet, selectedData));
                filteredProtenSet.removeAll(tempProtenSet);
                tempProtenSet.clear();

            }
        }
        singleProteinsFilter = onlyFilter == 1;
        return filteredProtenSet;

    }

    public boolean isSingleProteinsFilter() {
        return singleProteinsFilter;
    }

    /**
     * Loop responsible for updating all registered listeners
     *
     * @param type selection type
     * @param filterId filter that create the event
     */
    private void SelectionChanged(String selectionType, String actionFilterId) {

        if (selectionType.equalsIgnoreCase("protein_selection")) {
            for (int index = 0; index < filterOrderList.size(); index++) {
                String filterId = filterOrderList.get(index);
                registeredFiltersMap.get(filterId).updateFilterSelection(filteredProteinsSet, registeredAppliedFiltersMap.get(filterId), (index == 0), singleProteinsFilter, (filterId.equalsIgnoreCase(actionFilterId)));
            }

        }
    }

    public Set<Comparable> getAppliedFilterCategories(String filterId) {
        return registeredAppliedFiltersMap.get(filterId);

    }

    public Set<Comparable> getFilteredProteinsSet() {
        if (filteredProteinsSet == null) {
            System.out.println("there is a null selected proted");
        }
        return filteredProteinsSet;
    }

}
