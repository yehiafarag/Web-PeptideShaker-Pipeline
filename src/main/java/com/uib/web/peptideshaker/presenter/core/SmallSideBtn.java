package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Image;

/**
 * This class represent the top right small button
 *
 * @author Yehia Farag
 */
public class SmallSideBtn extends AbsoluteLayout {

    private final Image icon;

    public SmallSideBtn(String iconUrl) {
        icon = new Image();
        icon.setSource(new ThemeResource(iconUrl));
        icon.setSizeFull();
        SmallSideBtn.this.addComponent(icon);
        SmallSideBtn.this.setStyleName("smallmenubtn");
    }

    public void updateIconURL(String iconUrl) {
        icon.setSource(new ThemeResource(iconUrl));
    }

    public void setSelected(boolean selected) {
        if (selected) {
            SmallSideBtn.this.addStyleName("selectedsmallbtn");
        } else {
            SmallSideBtn.this.removeStyleName("selectedsmallbtn");
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
