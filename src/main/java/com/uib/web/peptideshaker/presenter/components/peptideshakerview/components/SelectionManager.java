package com.uib.web.peptideshaker.presenter.components.peptideshakerview.components;

import com.google.common.collect.Sets;
import com.uib.web.peptideshaker.presenter.core.BigSideBtn;
import com.uib.web.peptideshaker.presenter.core.filtercharts.charts.MatrixLayoutChartFilter;
import com.uib.web.peptideshaker.presenter.core.filtercharts.charts.RegistrableFilter;
import com.vaadin.ui.Component;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
    private final Set<String> fullProteinSet;
    private Set<String> proteinSelectionValue;

    private boolean singleProteinsFilter = false;

    public Set<String> getFullProteinSet() {
        return fullProteinSet;
    }
    private Map<String, Set<String>> modificationsMap;
    private Map<String, Set<String>> chromosomeMap;
    
    private final Map<String, Set<String>> selectedModificationsMap;
    private final Map<String, Set<String>> selectedChromosomeMap;
    private Map<String, Set<String>> piMap;
    private final Map<String, Set<String>> selectedPIMap;
    
    
    private Map<String, Set<String>> proteinValidationMap;
    private final Map<String, Set<String>> selectedProteinValidationMap;
    
    private final Map<String, Set<Object>> registeredAppliedFiltersMap;

    public void setChromosomeMap(Map<String, Set<String>> chromosomeMap) {
        this.selectedChromosomeMap.clear();
        this.chromosomeMap = chromosomeMap;
    }

    public void setPiMap(Map<String, Set<String>> piMap) {
        this.selectedPIMap.clear();
        this.piMap = piMap;
    }

    public Map<String, Set<String>> getPiMap() {
        if (selectedPIMap.isEmpty()) {
            return piMap;
        } else {
            return selectedPIMap;
        }
    }

    public Map<String, Set<String>> getProteinValidationMap() {
        if (selectedProteinValidationMap.isEmpty()) {
            return proteinValidationMap;
        } else {
            return selectedProteinValidationMap;
        }
    }

    public void setProteinValidationMap(Map<String, Set<String>> proteinValidationMap) {
        this.selectedProteinValidationMap.clear();
        this.proteinValidationMap = proteinValidationMap;
    }
    
    

    public Map<String, Set<String>> getChromosomeMap() {
        if (selectedChromosomeMap.isEmpty()) {
            return chromosomeMap;
        } else {
            return selectedChromosomeMap;
        }
    }

    public Map<String, Set<String>> getModificationsMap() {
        if (selectedModificationsMap.isEmpty()) {
            return modificationsMap;
        } else {
            return selectedModificationsMap;
        }
    }

    public void reset() {
        fullProteinSet.clear();
        selectedChromosomeMap.clear();
        selectedModificationsMap.clear();
        selectedPIMap.clear();
        selectedProteinValidationMap.clear();
        modificationsMap = null;
        piMap = null;
        chromosomeMap = null;
        proteinValidationMap=null;
        for (String filterId : registeredAppliedFiltersMap.keySet()) {
            registeredAppliedFiltersMap.get(filterId).clear();
        }

    }

    public void setModificationsMap(Map<String, Set<String>> modificationsMap) {
        selectedModificationsMap.clear();
        this.modificationsMap = modificationsMap;
        for (Set<String> set : modificationsMap.values()) {
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
        registeredAppliedFiltersMap = new LinkedHashMap<>();
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
    }

    /**
     * Set Selection in the system to update other registered listeners
     *
     * @param selectionType the type of the event
     * @param selectionValue set of selected value ids
     * @param filterId filter that create the event
     */
    public void setSelection(String selectionType, Set<String> selectionValue, String filterId) {
        int usedFilter = 0;
        if (selectionType.equalsIgnoreCase("protein_selection")) {
            if (filterId.equalsIgnoreCase("modifications_filter")) {
                registeredAppliedFiltersMap.get(filterId).clear();
                MatrixLayoutChartFilter filter = (MatrixLayoutChartFilter) registeredFiltersMap.get(filterId);
                for (Object obj : filter.getSelectedCategories()) {                    
                    registeredAppliedFiltersMap.get(filterId).add(filter.getCalculatedMatrix().keySet().toArray()[(int) obj]);
                }
            } else {
              registeredAppliedFiltersMap.put(filterId, registeredFiltersMap.get(filterId).getSelectedCategories());
            }

            for (String id : registeredAppliedFiltersMap.keySet()) {
                Set<Object> categories = registeredAppliedFiltersMap.get(id);
                if (!categories.isEmpty()) {
                    usedFilter++;
                }
            }
            singleProteinsFilter = (usedFilter < 2);
              
            if (usedFilter > 0 && registeredAppliedFiltersMap.get(filterId).isEmpty()) {
                filterId = "";
            }
                
            proteinSelectionValue = filterProteinData();
            selectedChromosomeMap.clear();
            for (String key : chromosomeMap.keySet()) {
                Set<String> inter = new LinkedHashSet<>(Sets.intersection(proteinSelectionValue, chromosomeMap.get(key)));
                selectedChromosomeMap.put(key, inter);
            }
            selectedPIMap.clear();
            for (String key : piMap.keySet()) {
                Set<String> inter = new LinkedHashSet<>(Sets.intersection(proteinSelectionValue, piMap.get(key)));
                selectedPIMap.put(key, inter);
            }
              selectedProteinValidationMap.clear();
            for (String key : proteinValidationMap.keySet()) {
                Set<String> inter = new LinkedHashSet<>(Sets.intersection(proteinSelectionValue,proteinValidationMap.get(key)));
                selectedProteinValidationMap.put(key, inter);
            }
            selectedModificationsMap.clear();
            for (String key : modificationsMap.keySet()) {
                Set<String> inter = new LinkedHashSet<>();
                inter.addAll(Sets.intersection(proteinSelectionValue, modificationsMap.get(key)));
                if (!inter.isEmpty()) {
                    selectedModificationsMap.put(key, inter);
                }
            }

        } else {

        }
        SelectionChanged(selectionType, filterId);

    }

    private Set<String> filterProteinData() {
        

        Set<String> filteredProtenSet = new LinkedHashSet<>(fullProteinSet);
        for (String filterId : registeredAppliedFiltersMap.keySet()) {
            Set<Object> selectedCategories = registeredAppliedFiltersMap.get(filterId);
            if (filterId.equalsIgnoreCase("modifications_filter") && !selectedCategories.isEmpty()) {
                Map<String, Set<String>> filteredMatrix = ((MatrixLayoutChartFilter) registeredFiltersMap.get(filterId)).getCalculatedMatrix();
                for (String key : filteredMatrix.keySet()) {
                   if (!selectedCategories.contains(key)) {
                        filteredProtenSet.removeAll(filteredMatrix.get(key));
                    }
                }

            } else if (filterId.equalsIgnoreCase("chromosome_filter") && !selectedCategories.isEmpty()) {
                Set<String> selectedData = new LinkedHashSet<>();
                for (Object cat : selectedCategories) {
                    selectedData.addAll(Sets.intersection(chromosomeMap.get(cat.toString()), filteredProtenSet));
                }
                filteredProtenSet.clear();
                filteredProtenSet.addAll(selectedData);
            } else if (filterId.equalsIgnoreCase("PI_filter") && !selectedCategories.isEmpty()) {
                Set<String> selectedData = new LinkedHashSet<>();
                for (Object cat : selectedCategories) {
                    selectedData.addAll(Sets.intersection(piMap.get(cat.toString()), filteredProtenSet));
                }
                filteredProtenSet.clear();
                filteredProtenSet.addAll(selectedData);
            }else if (filterId.equalsIgnoreCase("validation_filter") && !selectedCategories.isEmpty()) {
                Set<String> selectedData = new LinkedHashSet<>();
                for (Object cat : selectedCategories) {
                    selectedData.addAll(Sets.intersection(proteinValidationMap.get(cat.toString()), filteredProtenSet));
                }
                filteredProtenSet.clear();
                filteredProtenSet.addAll(selectedData);
            }
        }

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
    private void SelectionChanged(String type, String filterId) {

        if (proteinSelectionValue.size() == fullProteinSet.size()) {
            registeredFiltersMap.keySet().stream().forEach((filter) -> {
                registeredFiltersMap.get(filter).resetFilter();

            });

        } else {
            registeredFiltersMap.keySet().stream().forEach((filter) -> {
                if (!filter.equalsIgnoreCase(filterId)) {
                    registeredFiltersMap.get(filter).selectionChange(type);
                }
            });
        }

    }

    public Set<Object> getAppliedFilterCategories(String filterId) {
        return registeredAppliedFiltersMap.get(filterId);

    }

    public Set<String> getProteinSelectionValue() {
        if (proteinSelectionValue == null) {
            System.out.println("there is a null selected proted");
        }
        return proteinSelectionValue;
    }

}
