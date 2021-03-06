package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.event.LayoutEvents;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * This Class represents pop-up window that support modal mode
 *
 * @author Yehia Farag
 */
public class PopupWindow extends VerticalLayout implements LayoutEvents.LayoutClickListener {

    private final Window window;
    private Layout windoContent;
    private final Label titleLabel;

    /**
     * Constructor to initialize the main layout.
     */
    public PopupWindow(String title) {
        titleLabel = new Label(title);
        titleLabel.setStyleName("windowtitle");
        PopupWindow.this.addComponent(titleLabel);
        PopupWindow.this.addLayoutClickListener(PopupWindow.this);
       
        window = new Window() {
            @Override
            public void close() {
                this.setVisible(false);
            }

            @Override
            public void setVisible(boolean visible) {
                if (windoContent != null) {
                    super.setVisible(visible);
                } else {
                    super.setVisible(false);
                }
            }
        }; 
        
        window.center();
        window.setWindowMode(WindowMode.NORMAL);
        window.setStyleName("popupwindow");
        window.setClosable(false);
        window.setModal(true);
        window.setResizable(false);
        window.setDraggable(false);
        window.setVisible(false);
        UI.getCurrent().addWindow(window);
    }
    public void setClosable(boolean closable){
        window.setClosable(closable);
    }

    public void setLabelValue(String value) {
        titleLabel.setValue(value);
    }

    public void setContent(Layout popup) {
        windoContent = popup;
        window.setContent(popup);
    }
     public void setContent(String title,Layout popup) {
        windoContent = popup;
        window.setContent(popup);
//        window.setCaption(title);
//        window.setClosable(false);
    }

    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        window.setVisible(true);
    }

    public void setPopupVisible(boolean visible) {
        window.setVisible(visible);
    }
    public void setWindowSize(int width,int height){
    window.setWidth(width,Unit.PERCENTAGE);
        window.setHeight(height,Unit.PERCENTAGE);
    }
    public void addWindowStyle(String style){
    window.addStyleName(style);
    }

}
