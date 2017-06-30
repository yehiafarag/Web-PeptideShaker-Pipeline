
package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * This class represent the top right small button
 *
 * @author Yehia Farag
 */
public class BigSideBtn extends HorizontalLayout {

    public BigSideBtn(String iconUrl,String text) {
        Image icon = new Image();
        icon.setSource(new ThemeResource(iconUrl));
        icon.setSizeFull();
        BigSideBtn.this.addComponent(icon);
        BigSideBtn.this.setComponentAlignment(icon,Alignment.MIDDLE_CENTER);
//         BigSideBtn.this.setExpandRatio(icon,1);
        BigSideBtn.this.setSizeFull();
        BigSideBtn.this.setStyleName("bigmenubtn");
        
        
//        
//        Label textLabel = new Label(text);
//        textLabel.setStyleName("bigbtnText");
//         BigSideBtn.this.addComponent(textLabel);
//          BigSideBtn.this.setExpandRatio(textLabel,2);
//         BigSideBtn.this.setComponentAlignment(textLabel,Alignment.MIDDLE_LEFT);
        
        
        
        
    }

    public void setSelected(boolean selected) {
        if (selected) {
            BigSideBtn.this.addStyleName("selectedbiglbtn");
        } else {
            BigSideBtn.this.removeStyleName("selectedbiglbtn");
        }
    }

}
