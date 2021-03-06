package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.event.LayoutEvents;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;

/**
 * This class represent the top right small button
 *
 * @author Yehia Farag
 */
public class BigSideBtn extends HorizontalLayout {

    private Image btnThumbIconImage;
    private final Label iconLabel;
    private final MobileSideBtn mobileModeBtn;
    private final int btnId;

    public BigSideBtn(String text, int btnId) {
        mobileModeBtn = new MobileSideBtn(text);
        btnThumbIconImage = new Image() {
            @Override
            public void setSource(Resource source) {
                BigSideBtn.this.setVisible((source != null));
                if (source == null || btnThumbIconImage.getSource() == source) {
                    return;
                }
                btnThumbIconImage.setVisible(true);
                iconLabel.setVisible(false);
                super.setSource(source);
                mobileModeBtn.updateIconResource(source);
//                if (this.getStyleName().contains("reshake")) {
//                    this.removeStyleName("reshake");
//                    this.addStyleName("shake");
//                    mobileModeBtn.removeStyleName("reshake");
//                    mobileModeBtn.addStyleName("shake");
//                } else {
//                    this.removeStyleName("shake");
//                    this.addStyleName("reshake");
//                    mobileModeBtn.removeStyleName("shake");
//                    mobileModeBtn.addStyleName("reshake");
//                }
            }

        };
        btnThumbIconImage.setSizeFull();
        this.iconLabel = new Label();
        this.iconLabel.setSizeFull();
        this.iconLabel.setContentMode(ContentMode.HTML);
        this.btnId = btnId;
        BigSideBtn.this.addComponent(btnThumbIconImage);
        BigSideBtn.this.setComponentAlignment(btnThumbIconImage, Alignment.MIDDLE_CENTER);
        BigSideBtn.this.addComponent(iconLabel);
        BigSideBtn.this.setComponentAlignment(iconLabel, Alignment.MIDDLE_CENTER);
        BigSideBtn.this.setSizeFull();
        BigSideBtn.this.setStyleName("bigmenubtn");

        

    }

    public Image getBtnThumbIconImage() {
        return btnThumbIconImage;
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

    public void updateIconResource(Resource imageURL) {
        btnThumbIconImage.setSource(imageURL);
    }

    public void updateIcon(String HTML) {
        this.setVisible((HTML != null));
        if (HTML == null) {
            return;
        }
        btnThumbIconImage.setVisible(false);
        iconLabel.setVisible(true);
        iconLabel.setValue(HTML);
        mobileModeBtn.updateIconHTML(HTML);
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
