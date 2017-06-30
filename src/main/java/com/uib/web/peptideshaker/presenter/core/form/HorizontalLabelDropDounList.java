package com.uib.web.peptideshaker.presenter.core.form;

import com.vaadin.data.Property;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represent form component (drop down list with caption on the left
 * side)
 *
 * @author Yehia Farag
 */
public class HorizontalLabelDropDounList extends HorizontalLayout {

    /**
     * Main drop-down list.
     */
    private final ComboBox list;

    /**
     * Constructor to initialize the main attributes
     *
     * @param caption title
     * @param values the drop-down list values
     */
    public HorizontalLabelDropDounList(String caption) {
        HorizontalLabelDropDounList.this.setSizeFull();
        Label cap = new Label(caption);
        cap.addStyleName(ValoTheme.LABEL_TINY);
        cap.addStyleName(ValoTheme.LABEL_SMALL);
        cap.addStyleName("smallundecorated");
        HorizontalLabelDropDounList.this.addComponent(cap);
        HorizontalLabelDropDounList.this.setExpandRatio(cap, 45);

        list = new ComboBox();
        list.setWidth(100, Unit.PERCENTAGE);
        list.setHeight(25, Unit.PIXELS);
        list.setStyleName(ValoTheme.COMBOBOX_SMALL);
        list.addStyleName(ValoTheme.COMBOBOX_TINY);
        list.addStyleName(ValoTheme.COMBOBOX_ALIGN_CENTER);
        list.setNullSelectionAllowed(false);
        list.setImmediate(true);

        HorizontalLabelDropDounList.this.addComponent(list);
        HorizontalLabelDropDounList.this.setExpandRatio(list, 55);
    }

    public void updateData(Set<String> values) {
        if (values == null) {
            values = new HashSet<>();
        }
        if (values.isEmpty()) {
            values.add("N/A");
        }
        for (String str : values) {
            list.addItem(str);
        }
        list.setValue(values.toArray()[0]);
         list.commit();
        }

    public String getSelectedValue() {
        return list.getValue().toString();

    }

    public void addValueChangeListener(Property.ValueChangeListener listener) {
        this.list.addValueChangeListener(listener);
    }

    public void setSelected(Object objectId) {
        list.select(objectId);
        list.setData(objectId);

    }
    public boolean isValid(){
        list.setRequired(true);        
        boolean check = list.isValid();
        list.setRequired(!check);
        return check;
    }
    public boolean isModified() {    
        return !list.getValue().toString().equalsIgnoreCase(list.getData()+"");
    }

}
