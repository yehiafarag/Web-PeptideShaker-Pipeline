package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.event.LayoutEvents;
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
public class BigSideBtn extends HorizontalLayout {

    private final Image icon;
    private final MobileSideBtn mobileModeBtn;
    private final int btnId;

    public BigSideBtn(String text,int btnId) {
        icon = new Image();
        icon.setSizeFull();
        this.btnId = btnId;
        BigSideBtn.this.addComponent(icon);
        BigSideBtn.this.setComponentAlignment(icon, Alignment.MIDDLE_CENTER);
        BigSideBtn.this.setSizeFull();
        BigSideBtn.this.setStyleName("bigmenubtn");

        mobileModeBtn = new MobileSideBtn(text);

    }

    public int getBtnId() {
        return btnId;
    }

    @Override
    public void setData(Object data) {
        mobileModeBtn.setData(data);
        super.setData(data); //To change body of generated methods, choose Tools | Templates.
    }

    public MobileSideBtn getMobileModeBtn() {
        return mobileModeBtn;
    }

    public void updateIcon(Resource imageURL) {
        this.setVisible(!(imageURL == null));
        if (imageURL == null || icon.getSource()==imageURL) {
            return;
        }

        icon.setSource(imageURL);
        mobileModeBtn.updateIcon(imageURL);
        if (this.getStyleName().contains("reshake")) {
            this.removeStyleName("reshake");
            this.addStyleName("shake");
            mobileModeBtn.removeStyleName("reshake");
            mobileModeBtn.addStyleName("shake");
        } else {
            this.removeStyleName("shake");
            this.addStyleName("reshake");
            mobileModeBtn.removeStyleName("shake");
            mobileModeBtn.addStyleName("reshake");

        }
    }

    public void setSelected(boolean selected) {
        if (selected) {
            this.mobileModeBtn.addStyleName("selectedbiglbtn");
            BigSideBtn.this.addStyleName("selectedbiglbtn");
        } else {

            this.mobileModeBtn.removeStyleName("selectedbiglbtn");
            BigSideBtn.this.removeStyleName("selectedbiglbtn");
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            this.mobileModeBtn.removeStyleName("hide");
            this.removeStyleName("hide");
        } else {
            this.mobileModeBtn.addStyleName("hide");
            this.addStyleName("hide");
        }
        //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addLayoutClickListener(LayoutEvents.LayoutClickListener listener) {
        this.mobileModeBtn.addLayoutClickListener(listener);
        super.addLayoutClickListener(listener); //To change body of generated methods, choose Tools | Templates.
    }

}
