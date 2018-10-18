package com.uib.web.peptideshaker.presenter.core.form;

import com.vaadin.data.Validator;
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
public class HorizontalLabel2TextField extends HorizontalLayout {

    /**
     * First drop-down list.
     */
    private final TextField textField1;
    /**
     * Second drop-down list.
     */
    private final TextField textField2;
    private final String defaultValue1;
    private final String defaultValue2;
    private final Label cap;

    /**
     * Constructor to initialize the main attributes
     *
     * @param caption title
     * @param values the drop-down list values
     */
    public HorizontalLabel2TextField(String title, Object defaultValue1, Object defaultValue2, Validator validator) {

        HorizontalLabel2TextField.this.setSizeFull();
        cap = new Label(title);
        cap.addStyleName(ValoTheme.LABEL_TINY);
        cap.addStyleName(ValoTheme.LABEL_SMALL);
        cap.addStyleName("smallundecorated");
        HorizontalLabel2TextField.this.addComponent(cap);
        HorizontalLabel2TextField.this.setExpandRatio(cap, 45);

        if (defaultValue1 == null) {
            this.defaultValue1 = "0";
        } else {
            this.defaultValue1 = defaultValue1.toString();
        }

        textField1 = new TextField();
        textField1.setValidationVisible(true);
        textField1.setConverter(Integer.class);

        textField1.addValidator(validator);
        textField1.addStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
        textField1.setWidth(100, Unit.PERCENTAGE);
        textField1.addStyleName(ValoTheme.TEXTFIELD_TINY);
        textField1.setNullRepresentation(this.defaultValue1);
        textField1.setWidth(100, Unit.PERCENTAGE);
        textField1.setHeight(25, Unit.PIXELS);
        HorizontalLabel2TextField.this.addComponent(textField1);
        HorizontalLabel2TextField.this.setExpandRatio(textField1, 27.5f);

        if (defaultValue2 == null) {
            this.defaultValue2 = "0";
        } else {
            this.defaultValue2 = defaultValue2.toString();
        }

        textField2 = new TextField();
        textField2.setValidationVisible(true);
        textField2.setConverter(Integer.class);

        textField2.addValidator(validator);
        textField2.addStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
        textField2.setWidth(100, Unit.PERCENTAGE);
        textField2.addStyleName(ValoTheme.TEXTFIELD_TINY);
        textField2.setNullRepresentation(this.defaultValue2);
        textField2.setWidth(100, Unit.PERCENTAGE);
        textField2.setHeight(25, Unit.PIXELS);
        HorizontalLabel2TextField.this.addComponent(textField2);
        HorizontalLabel2TextField.this.setExpandRatio(textField2, 27.5f);

    }

    public String getFirstSelectedValue() {
        if (textField1.getValue() == null) {
            return defaultValue1;
        }
        return textField1.getValue();

    }

    public String getSecondSelectedValue() {
        if (textField2.getValue() == null) {
            return defaultValue2;
        }
        return textField2.getValue();

    }

    public void setFirstSelectedValue(Object value) {
        textField1.setValue(value + "");
        textField1.setData(value);

    }

    public void setSecondSelectedValue(Object value) {
        textField2.setValue(value + "");
        textField2.setData(value);

    }

    public boolean isValid() {
        boolean check1 = textField1.isValid();
        boolean check2 = textField2.isValid();
        boolean check3 = Integer.valueOf(this.getFirstSelectedValue()) <= Integer.valueOf(this.getSecondSelectedValue());

        if (!check3) {
            textField1.setValue("Error");
            textField2.setValue("Error");
        }
        return check1 && check2 && check3;
    }

    public boolean isModified() {
        return (!textField1.getValue().equalsIgnoreCase(textField1.getData() + "")) || (!textField2.getValue().equalsIgnoreCase(textField2.getData() + ""));

    }
    public String fullLabelValue(){
        return "<b>"+cap.getValue()+": </b>"+textField1.getValue()+"_-_"+textField2.getValue();
    
    }

}
