package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.event.LayoutEvents;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;
import java.util.Map;
import java.util.Set;

/**
 * This class represents drop-down list component
 *
 * @author Yehia Farag
 */
public class MultiSelectOptionGroup extends VerticalLayout implements LayoutEvents.LayoutClickListener {

    private final OptionGroup list;
    private final boolean expandable;

    /**
     * Constructor to initialize the main attributes.
     */
    public MultiSelectOptionGroup(String title, boolean expandable) {
        this.expandable=expandable;
        MultiSelectOptionGroup.this.setSizeUndefined();
        MultiSelectOptionGroup.this.setSpacing(true);
        if (expandable) {
            MultiSelectOptionGroup.this.addLayoutClickListener(MultiSelectOptionGroup.this);
        }
        MultiSelectOptionGroup.this.setStyleName("optiongroupframe");
        if (title != null) {
            list = new OptionGroup(title);
        } else {
            list = new OptionGroup();
        }

        list.setCaptionAsHtml(true);
        list.setSizeUndefined();
        list.setMultiSelect(true);
        list.setStyleName("optiongroup");

        MultiSelectOptionGroup.this.addComponent(list);

    }

    /**
     * Update the list
     *
     * @param idToCaptionMap list of ids and names
     */
    public void updateList(Map<String, String> idToCaptionMap) {
        list.removeAllItems();
        list.clear();
        for (String id : idToCaptionMap.keySet()) {
            list.addItem(id);
            list.setItemCaption(id, idToCaptionMap.get(id));
        }

    }

    /**
     * Get selection value
     *
     * @return String id of the selected item
     */
    public Set<String> getSelectedValue() {
        list.removeStyleName("error");
        if (list.isValid()) {
            return (Set<String>) list.getValue();
        }
        list.addStyleName("error");
        return null;
    }

    /**
     * Set selection value
     *
     * @return String id of the selected item
     */
    public void setSelectedValue(Set<String> values) {
        list.setValue(values);
        list.setData(list.getValue());
    }

    /**
     * Set selection value
     *
     * @param String id of the selected item
     */
    public void setSelectedValue(String valueId) {
        list.select(valueId);
        list.setData(list.getValue());
    }

    /**
     * Set the list is required to have a value.
     *
     * @param required the selection is required
     * @param requiredMessage the error appear if no data selected
     */
    public void setRequired(boolean required, String requiredMessage) {
        list.setRequired(required);
        list.setRequiredError(requiredMessage);

    }

    public void setViewList(boolean view) {
        if (view || !expandable) {
            list.removeStyleName("hidelist");
            list.addStyleName("showlist");
        } else {
            list.addStyleName("hidelist");
            list.removeStyleName("showlist");
        }

    }

    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        if (event.getClickedComponent() != null) {
            if (list.getStyleName().contains("hidelist")) {
                list.removeStyleName("hidelist");
                list.addStyleName("showlist");
            } else if (list.getStyleName().contains("hidelist")) {
                list.addStyleName("hidelist");
                list.removeStyleName("showlist");
            }
        } else if (list.getStyleName().contains("hidelist")) {
            list.removeStyleName("hidelist");
            list.addStyleName("showlist");
        } else {
            list.addStyleName("hidelist");
            list.removeStyleName("showlist");

        }
    }
    
        public boolean isValid() {
        list.setRequired(true);
        boolean check = list.isValid();
        list.setRequired(!check);
        return check;
    }

    public boolean isModified() {
        return !list.getValue().toString().equalsIgnoreCase(list.getData() + "");
    }

}
