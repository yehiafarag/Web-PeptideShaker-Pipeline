package com.uib.web.peptideshaker.presenter.components.peptideshakerview.components;

import com.uib.web.peptideshaker.presenter.core.BigSideBtn;
import com.uib.web.peptideshaker.presenter.core.filtercharts.RegistrableFilter;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;
import java.util.LinkedHashMap;
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
    private Set<String> proteinSelectionValue;

    public SelectionManager() {
        this.btnsLayoutMap = new LinkedHashMap<>();
        this.registeredFiltersMap = new LinkedHashMap<>();
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
    }

    /**
     * Set Selection in the system to update other registered listeners
     *
     * @param selectionType the type of the event
     * @param selectionValue set of selected value ids
     * @param filterId filter that create the event
     */
    public void setSelection(String selectionType, Set<String> selectionValue, String filterId) {
        if (selectionType.equalsIgnoreCase("protein_selection")) {
            proteinSelectionValue = selectionValue;
        } else {

        }
        SelectionChanged(selectionType, filterId);

    }

    /**
     * Loop responsible for updating all registered listeners
     *
     * @param type selection type
     * @param filterId filter that create the event
     */
    private void SelectionChanged(String type, String filterId) {

        registeredFiltersMap.keySet().stream().forEach((filter) -> {
            if (!filter.equalsIgnoreCase(filterId)) {
                registeredFiltersMap.get(filter).selectionChange(type);
            }
        });

    }

    public Set<String> getProteinSelectionValue() {
        return proteinSelectionValue;
    }

}
