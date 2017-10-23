package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;

/**
 * This class represent the top right small button
 *
 * @author Yehia Farag
 */
public class MobileSideBtn extends HorizontalLayout {

    private final Image icon;

    public MobileSideBtn(String text) {
        icon = new Image();
        icon.setSizeFull();
        MobileSideBtn.this.addComponent(icon);
        MobileSideBtn.this.setComponentAlignment(icon, Alignment.MIDDLE_CENTER);
        MobileSideBtn.this.setSizeFull();
        MobileSideBtn.this.setStyleName("bigmenubtn");
          MobileSideBtn.this.addStyleName("zeropadding");

    }

    public void updateIcon(Resource imageURL) {
        this.setVisible((imageURL == null));
        if (imageURL == null) {
            return;
        }

        icon.setSource(imageURL);

    }

    public void setSelected(boolean selected) {
        if (selected) {
            MobileSideBtn.this.addStyleName("selectedbiglbtn");
        } else {
            MobileSideBtn.this.removeStyleName("selectedbiglbtn");
        }
    }
    

}
