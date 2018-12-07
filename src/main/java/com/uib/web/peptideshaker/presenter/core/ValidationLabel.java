package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * Valid or not valid label
 *
 * @author Yehia Farag
 */
public class ValidationLabel extends VerticalLayout implements Comparable<ValidationLabel>{
private final Label icon;
private final Integer sortIndex;
    public ValidationLabel(String validation) {
        ValidationLabel.this.setSizeFull();
        this.icon = new Label();
        this.icon.setWidth(16, Unit.PIXELS);
        this.icon.setHeight(16,Unit.PIXELS);
        this.icon.setContentMode(ContentMode.HTML);
        ValidationLabel.this.addComponent(icon);
        ValidationLabel.this.setComponentAlignment(icon, Alignment.TOP_CENTER);
        if (validation.trim().equalsIgnoreCase("confident")) {
            ValidationLabel.this.setStyleName("validlabel");
            this.icon.setValue(VaadinIcons.CHECK_CIRCLE.getHtml());
            sortIndex=0;

        } else if (validation.trim().equalsIgnoreCase("doubtful")) {
            ValidationLabel.this.setStyleName("doubtfullabel");
            this.icon.setValue(VaadinIcons.WARNING.getHtml());
             sortIndex=1;

        } else {
            ValidationLabel.this.setStyleName("notvalidlabel");
            this.icon.setValue(VaadinIcons.CLOSE_CIRCLE.getHtml());
             sortIndex=2;
        }
        ValidationLabel.this.setDescription(validation);

    }

    @Override
    public int compareTo(ValidationLabel t) {
        return t.sortIndex.compareTo(sortIndex);
    }
    

}
