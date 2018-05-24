package com.uib.web.peptideshaker;

import com.uib.web.peptideshaker.galaxy.nelsgalaxy.NeLSGalaxy;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.Extension;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import javax.servlet.ServletContext;
import selectioncanvas.SelectioncanvasComponent;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of a html page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialised using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialise non-component functionality.
 */
@Theme("webpeptideshakertheme")
public class PeptidShakerUI extends UI {

    /**
     * The connection layer to NeLS-Galaxy server.
     */
    private NeLSGalaxy NeLS_Galaxy;

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        PeptidShakerUI.this.setSizeFull();
        ServletContext scx = VaadinServlet.getCurrent().getServletContext();

//        String fullPath = scx.getRealPath("/VAADIN/jsmol/data/");
        String localFileSystemFolderPath = (scx.getInitParameter("filesURL"));
        VaadinSession.getCurrent().setAttribute("userDataFolderUrl", localFileSystemFolderPath);
        VaadinSession.getCurrent().getSession().setAttribute("userDataFolderUrl", localFileSystemFolderPath);
        VaadinSession.getCurrent().setAttribute("ctxPath", vaadinRequest.getContextPath());
        /**
         * Check the visualisation mode based on screen size small screen for
         * mobile browser or tablet.
         *
         * @todo: we need better optimisation to detect the window ratio.
         */
        WebPeptideShakerApp webPeptideShakerApp = new WebPeptideShakerApp();
        if ((Page.getCurrent().getBrowserWindowWidth() < Page.getCurrent().getBrowserWindowHeight()) || (Page.getCurrent().getBrowserWindowWidth() < 650) || (Page.getCurrent().getBrowserWindowHeight() < 600)) {
            webPeptideShakerApp.addStyleName("horizontalcss");
        } else {
            webPeptideShakerApp.removeStyleName("horizontalcss");
        }
        /**
         * On resize the browser re-arrange all the created windows to center.
         */
        Page.getCurrent().addBrowserWindowResizeListener((Page.BrowserWindowResizeEvent event) -> {
            if ((Page.getCurrent().getBrowserWindowWidth() < Page.getCurrent().getBrowserWindowHeight()) || (Page.getCurrent().getBrowserWindowWidth() < 650) || (Page.getCurrent().getBrowserWindowHeight() < 600)) {
                webPeptideShakerApp.addStyleName("horizontalcss");
            } else {
                webPeptideShakerApp.removeStyleName("horizontalcss");
            }
            UI.getCurrent().getWindows().forEach((w) -> {
                w.center();
            });
        });

        NeLS_Galaxy = new NeLSGalaxy();
        boolean isNelsGalaxyConnection = NeLS_Galaxy.isNelsGalaxyConnection(vaadinRequest);
        VaadinSession.getCurrent().setAttribute("nelsgalaxy", isNelsGalaxyConnection);
        setContent(webPeptideShakerApp);
        if (isNelsGalaxyConnection || (VaadinSession.getCurrent().getAttribute("ApiKey") != null && VaadinSession.getCurrent().getAttribute("galaxyUrl") != null)) {
            webPeptideShakerApp.reConnectToGalaxyServer(VaadinSession.getCurrent().getAttribute("ApiKey") + "", VaadinSession.getCurrent().getAttribute("galaxyUrl") + "");
        }

//        UnusedSelectionCanvas sc = new UnusedSelectionCanvas() {
//            @Override
//            public void dragSelectionIsPerformed(double startX, double startY, double endX, double endY) {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//            }
//
//            @Override
//            public void rightSelectionIsPerformed(double startX, double startY) {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//            }
//
//            @Override
//            public void leftSelectionIsPerformed(double startX, double startY) {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//            }
//        };
//        sc.setSize(1000, 1000);
        this.setSizeFull();

        VerticalLayout layoutContent = new VerticalLayout();
        layoutContent.setSizeFull();
//        setContent(layoutContent);

        final SelectioncanvasComponent mycomponent = new SelectioncanvasComponent() {
            @Override
            public void dragSelectionIsPerformed(double startX, double startY, double endX, double endY) {
                Notification.show("Drag and select");
                System.out.println("Drag and select");
            }

            @Override
            public void rightSelectionIsPerformed(double startX, double startY) {
                Notification.show("Right click select");
                System.out.println("Right click select");
            }

            @Override
            public void leftSelectionIsPerformed(double startX, double startY) {
                Notification.show("left and select");
                System.out.println("left and select");
            }

        };
        mycomponent.setHeight(300, Unit.PIXELS);
        mycomponent.setWidth(500, Unit.PIXELS);
//// Set the value from server-side
////        mycomponent.setValue("Server-side value");
//
// Process a value input by the user from the client-side
        mycomponent.addValueChangeListener(new SelectioncanvasComponent.ValueChangeListener() {
            @Override
            public void valueChange() {
                Notification.show("Value: " + mycomponent.getValue());
            }
        });
        layoutContent.addComponent(mycomponent);
        Button l = new Button("Kokowawa ");
        layoutContent.addComponent(l);
        layoutContent.markAsDirty();
        l.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                mycomponent.setHeight(600, Unit.PIXELS);
                mycomponent.setWidth(600, Unit.PIXELS);
                mycomponent.setValue("600,600");
            }
        });

    }

    @Override
    public void addExtension(Extension extension) {
        super.addExtension(extension); //To change body of generated methods, choose Tools | Templates.

    }

    @WebServlet(urlPatterns = "/*", name = "PeptidShakerUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = PeptidShakerUI.class, productionMode = false, resourceCacheTime = 1)
    public static class PeptidShakerUIServlet extends VaadinServlet {
    }

}
