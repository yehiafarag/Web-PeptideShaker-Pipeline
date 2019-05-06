/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.model.core;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.jsclipboard.JSClipboard;
import com.vaadin.jsclipboard.JSClipboardButton;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.themes.ValoTheme;

/**
 *
 * @author y-mok
 */
public class ClipboardUtil extends AbsoluteLayout {

    private final TextArea area;
    private JSClipboardButton  clipboard;

    public ClipboardUtil(String text) {
        ClipboardUtil.this.setSizeFull();
        ClipboardUtil.this.addStyleName("clipboardcontainer");
        
//        if (clipboard == null) {
//            clipboard = new JSClipboard();
//            VaadinSession.getCurrent().setAttribute("clipbored", clipboard);
//        }

        area = new TextArea();        
        this.addComponent(area);
        area.setValue(text);
        area.setId("tocopie");
        clipboard = new JSClipboardButton(area, VaadinIcons.LINK);// VaadinSession.getCurrent().getAttribute("clipbored");
        clipboard.setClipboardText(text);
        this.addComponent(clipboard);
        clipboard.addStyleName(ValoTheme.BUTTON_LINK);
        clipboard.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
       
        clipboard.addSuccessListener(() -> {
            Notification.show("Copy to clipboard successful "+text);
        });
        clipboard.addErrorListener(() -> {
            Notification.show("Copy to clipboard unsuccessful", Notification.Type.ERROR_MESSAGE);
        });

    }

   

}
