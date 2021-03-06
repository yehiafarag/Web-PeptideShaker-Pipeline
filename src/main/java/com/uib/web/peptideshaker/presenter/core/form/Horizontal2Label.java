package com.uib.web.peptideshaker.presenter.core.form;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represent form component (drop down list with caption on the left
 * side)
 *
 * @author Yehia Farag
 */
public class Horizontal2Label extends HorizontalLayout {


    /**
     * Constructor to initialize the main attributes
     *
     * @param caption title
     * @param values the drop-down list values
     */
    public Horizontal2Label(String caption, Object defaultValue) {
        Horizontal2Label.this.setSpacing(true);
        Label cap = new Label(caption);
        cap.setContentMode(ContentMode.HTML);
        cap.addStyleName(ValoTheme.LABEL_TINY);
        cap.addStyleName(ValoTheme.LABEL_SMALL);
        cap.addStyleName(ValoTheme.LABEL_BOLD);
        cap.addStyleName("smallundecorated");
        Horizontal2Label.this.addComponent(cap);  
        if(defaultValue==null)
        {
            Horizontal2Label.this.setEnabled(false);
            return;
            
        }

        Label valueLabel = new Label();       
        valueLabel.addStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
        valueLabel.setWidth(100, Unit.PERCENTAGE);
        valueLabel.addStyleName(ValoTheme.LABEL_TINY);
        valueLabel.setValue(defaultValue+"");

        valueLabel.setWidth(100, Unit.PERCENTAGE);
        valueLabel.setHeight(25, Unit.PIXELS);
        valueLabel.addStyleName("smallundecorated");
        Horizontal2Label.this.addComponent(valueLabel);
    }

    

}
