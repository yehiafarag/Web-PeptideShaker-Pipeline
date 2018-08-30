package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;

/**
 * This class represent the top right small button
 *
 * @author Yehia Farag
 */
public class SmallSideBtn extends AbsoluteLayout {

    private final Image iconImage;
      private final Label iconLabel;

    public SmallSideBtn(String iconUrl) {
        iconImage = new Image();
        iconImage.setSource(new ThemeResource(iconUrl));
        iconImage.setSizeFull();         
        SmallSideBtn.this.addComponent(iconImage);
        
        iconLabel = new Label();
        iconLabel.setContentMode(ContentMode.HTML);
        iconLabel.setSizeFull();
        iconLabel.setVisible(false);
        SmallSideBtn.this.addComponent(iconLabel);
        SmallSideBtn.this.setStyleName("presenterbtn");
    }
    public SmallSideBtn(VaadinIcons vicon) {
        iconImage = new Image();
         iconImage.setVisible(false);
        iconImage.setSizeFull();
        SmallSideBtn.this.addComponent(iconImage);
        iconLabel = new Label("<center>"+vicon.getHtml()+"</center>");
        iconLabel.setContentMode(ContentMode.HTML);
        iconLabel.setSizeFull();
        SmallSideBtn.this.addComponent(iconLabel);        
        SmallSideBtn.this.setStyleName("presenterbtn");
    }

    public void updateIconURL(String iconUrl) {
        iconLabel.setVisible(false);
        iconImage.setVisible(true);
        iconImage.setSource(new ThemeResource(iconUrl));
    }
     public void updateIconURL(VaadinIcons vicon) {
        iconLabel.setVisible(true);
        iconImage.setVisible(false);
        iconLabel.setValue("<center>"+vicon.getHtml()+"</center>");
    }

    public void setSelected(boolean selected) {
        if (selected) {
            SmallSideBtn.this.addStyleName("selectedpresenterbtn");
        } else {
            SmallSideBtn.this.removeStyleName("selectedpresenterbtn");
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled) {
            this.removeStyleName("disable");
        } else {
            this.addStyleName("disable");
        }
        super.setEnabled(enabled); //To change body of generated methods, choose Tools | Templates.
    }

}
