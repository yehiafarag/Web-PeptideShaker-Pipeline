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
import com.vaadin.ui.UI;
import javax.servlet.ServletContext;

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
        this.setSizeFull();
       

    }

    @Override
    public void addExtension(Extension extension) {
        super.addExtension(extension); //To change body of generated methods, choose Tools | Templates.

    }

    @WebServlet(urlPatterns = "/*", name = "PeptidShakerUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = PeptidShakerUI.class, productionMode = true, resourceCacheTime = 0)//, resourceCacheTime = 1
    public static class PeptidShakerUIServlet extends VaadinServlet {
    }

}
