
package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.ui.VerticalLayout;

/**
 *This class represent coloured based Inference label
 * @author Yehia Farag 
 */
public class ColorLabel extends VerticalLayout{
    private final String[] colorStyles=new String[]{"whitecolor","greenincolor","yellowcolor","redincolor"};

    public ColorLabel(int colorIndex,String description) {
        ColorLabel.this.setSizeFull();
        ColorLabel.this.setStyleName("colorlabelfortablecell");
        ColorLabel.this.addStyleName(colorStyles[colorIndex]);
        ColorLabel.this.setDescription(description);
    }
    
    
}
