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
public class ValidationLabel extends VerticalLayout {
private final Label icon;
    public ValidationLabel(String validation) {
        ValidationLabel.this.setSizeFull();
        this.icon = new Label();
        this.icon.setWidth(16, Unit.PIXELS);
        this.icon.setHeight(16,Unit.PIXELS);
        this.icon.setContentMode(ContentMode.HTML);
        ValidationLabel.this.addComponent(icon);
        ValidationLabel.this.setComponentAlignment(icon, Alignment.TOP_CENTER);
        if (validation.equalsIgnoreCase("confident")) {
            ValidationLabel.this.setStyleName("validlabel");
            this.icon.setValue(VaadinIcons.CHECK_CIRCLE.getHtml());

        } else if (validation.equalsIgnoreCase("doubtful")) {
            ValidationLabel.this.setStyleName("doubtfullabel");
            this.icon.setValue(VaadinIcons.EXCLAMATION_CIRCLE.getHtml());

        } else {
            ValidationLabel.this.setStyleName("notvalidlabel");
            this.icon.setValue(VaadinIcons.CLOSE_CIRCLE.getHtml());
        }
        ValidationLabel.this.setDescription(validation);

    }

}
