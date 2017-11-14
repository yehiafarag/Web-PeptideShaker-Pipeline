package com.uib.web.peptideshaker.presenter.core.form;

import com.vaadin.data.Validator;
import com.vaadin.event.FieldEvents;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

/**
 * This class represent form component (drop down list with caption on the left
 * side)
 *
 * @author Yehia Farag
 */
public class HorizontalLabelTextField extends HorizontalLayout {

    /**
     * Main drop-down list.
     */
    private final TextField textField;
    private String defaultValue;

    /**
     * Constructor to initialize the main attributes
     *
     * @param caption title
     * @param values the drop-down list values
     */
    public HorizontalLabelTextField(String caption, Object defaultValue, Validator validator) {
        HorizontalLabelTextField.this.setSizeFull();
        Label cap = new Label(caption);
        cap.setContentMode(ContentMode.HTML);
        cap.addStyleName(ValoTheme.LABEL_TINY);
        cap.addStyleName(ValoTheme.LABEL_SMALL);
        cap.addStyleName("smallundecorated");
        HorizontalLabelTextField.this.addComponent(cap);
        HorizontalLabelTextField.this.setExpandRatio(cap, 45);

        if (defaultValue == null) {
            this.defaultValue = "0";
        } else {
            this.defaultValue = defaultValue.toString();
        }

        textField = new TextField();
        textField.setValidationVisible(true);

        if (validator != null) {
            textField.setConverter(Integer.class);
            textField.addValidator(validator);
        }
        textField.addStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
        textField.setWidth(100, Unit.PERCENTAGE);
        textField.addStyleName(ValoTheme.TEXTFIELD_TINY);
        textField.setNullRepresentation(this.defaultValue);
        textField.setInputPrompt(this.defaultValue);
        textField.setValue(this.defaultValue);

        textField.setWidth(100, Unit.PERCENTAGE);
        textField.setHeight(25, Unit.PIXELS);
        HorizontalLabelTextField.this.addComponent(textField);
        HorizontalLabelTextField.this.setExpandRatio(textField, 55);
       
    }
    public void addTextChangeListener(FieldEvents.TextChangeListener listener){
        textField.addTextChangeListener(listener);
        textField.setTextChangeTimeout(2000);
    
    }

    public void setRequired(boolean required) {
        textField.setRequired(required);
        textField.setRequiredError("Can not be empty");
    }

    public boolean isValid() {
        boolean check = textField.isValid();
        return check;

    }

    public boolean isModified() {
        return !textField.getValue().equalsIgnoreCase(textField.getData() + "");
    }

    public void setSelectedValue(Object value) {
        if (value == null) {
            textField.clear();
            return;
        }
        textField.setValue(value + "");
        textField.setData(value);

    }

    public String getSelectedValue() {
        if (textField.getValue() == null) {
            return defaultValue.replace(" ","_");

        }
        return textField.getValue().replace(" ","_");

    }

}
