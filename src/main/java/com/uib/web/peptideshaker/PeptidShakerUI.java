package com.uib.web.peptideshaker;

import com.uib.web.peptideshaker.galaxy.nelsgalaxy.NeLSStorageInteractiveLayer;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.Extension;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.ServletContext;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of a html page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialised using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialise non-component functionality.
 *
 * @author Yehia Farag
 */
@Theme("webpeptideshakertheme")
public class PeptidShakerUI extends UI {

    /**
     * The connection layer to NeLS-Galaxy server.
     */
    private NeLSStorageInteractiveLayer NeLS_Galaxy;
    private Notification notification;

    /**
     * The entry point for the application .
     *
     * @param vaadinRequest represents the incoming request
     */
    @Override
    protected void init(VaadinRequest vaadinRequest) {

        PeptidShakerUI.this.setSizeFull();
        notification = new Notification("Use the device in landscape mode :-)", Notification.Type.ERROR_MESSAGE);
        notification.setDelayMsec(10000);
        notification.setStyleName("mobilealertnotification");
        /**
         * Initialise the context parameters and store them in Vaadin session.
         *
         */
        ServletContext scx = VaadinServlet.getCurrent().getServletContext();
        String localFileSystemFolderPath = (scx.getInitParameter("filesURL"));
        VaadinSession.getCurrent().setAttribute("userDataFolderUrl", localFileSystemFolderPath);
        VaadinSession.getCurrent().getSession().setAttribute("userDataFolderUrl", localFileSystemFolderPath);
        VaadinSession.getCurrent().setAttribute("ctxPath", vaadinRequest.getContextPath());
        String testUserAPIKey = (scx.getInitParameter("testUserAPIKey"));
        VaadinSession.getCurrent().setAttribute("testUserAPIKey", testUserAPIKey);
        String galaxyServerUrl = (scx.getInitParameter("galaxyServerUrl"));
        VaadinSession.getCurrent().setAttribute("galaxyServerUrl", galaxyServerUrl);

        if (testUserAPIKey == null || galaxyServerUrl == null) {
            notification = new Notification("Error in Galaxy server address, Contact administrator :-(", Notification.Type.ERROR_MESSAGE);
            notification.setDelayMsec(-1);
            notification.show(Page.getCurrent());
            return;
        }
        if (!checkConnectionToGalaxy(galaxyServerUrl)) {
            notification = new Notification("Error, Galaxy server is not available , Contact administrator :-(", Notification.Type.ERROR_MESSAGE);
            notification.setDelayMsec(-1);
            notification.show(Page.getCurrent());
            return;
        }

        WebPeptideShakerApp webPeptideShakerApp = new WebPeptideShakerApp();
        PeptidShakerUI.this.setContent(webPeptideShakerApp);

        /**
         * Check the visualisation mode based on screen size small screen for
         * mobile browser or tablet.
         *
         * @todo: we need better optimisation to detect the window ratio.
         */
//        if ((Page.getCurrent().getBrowserWindowWidth() < Page.getCurrent().getBrowserWindowHeight()) || (Page.getCurrent().getBrowserWindowWidth() < 650) || (Page.getCurrent().getBrowserWindowHeight() < 600)) {
        if (Page.getCurrent().getWebBrowser().getBrowserApplication().contains("Mobile")) {
            webPeptideShakerApp.addStyleName("mobilestyle");
            webPeptideShakerApp.addStyleName("smallscreenstyle");
            VaadinSession.getCurrent().setAttribute("smallscreenstyle", true);
        } else {
            webPeptideShakerApp.removeStyleName("mobilestyle");
            VaadinSession.getCurrent().setAttribute("smallscreenstyle", false);
        }

        /**
         * On resize the browser re-arrange all the created pop-up windows to
         * the page center.
         */
        Page.getCurrent().addBrowserWindowResizeListener((Page.BrowserWindowResizeEvent event) -> {
            if (Page.getCurrent().getWebBrowser().getBrowserApplication().contains("Mobile") && (Page.getCurrent().getBrowserWindowWidth() < Page.getCurrent().getBrowserWindowHeight())) {
                notification.show(Page.getCurrent());
                webPeptideShakerApp.addStyleName("hidemode");
            } else if (Page.getCurrent().getWebBrowser().getBrowserApplication().contains("Mobile") && (Page.getCurrent().getBrowserWindowWidth() >= Page.getCurrent().getBrowserWindowHeight())) {
                webPeptideShakerApp.removeStyleName("hidemode");

            } else if ((Page.getCurrent().getBrowserWindowWidth() < Page.getCurrent().getBrowserWindowHeight()) || (Page.getCurrent().getBrowserWindowWidth() < 650) || (Page.getCurrent().getBrowserWindowHeight() < 600)) {
                webPeptideShakerApp.addStyleName("smallscreenstyle");
                VaadinSession.getCurrent().setAttribute("smallscreenstyle", true);
            } else {
                webPeptideShakerApp.removeStyleName("smallscreenstyle");
                VaadinSession.getCurrent().setAttribute("smallscreenstyle", false);
            }
            UI.getCurrent().getWindows().forEach((w) -> {
                w.center();
            });
        });

        /**
         * for future use to integrate with NeLS.
         *
         * @todo:re implement all concept in future
         */
        NeLS_Galaxy = new NeLSStorageInteractiveLayer();
        boolean isNelsGalaxyConnection = NeLS_Galaxy.isNelsGalaxyConnection(vaadinRequest);
        VaadinSession.getCurrent().setAttribute("nelsgalaxy", isNelsGalaxyConnection);

        /**
         * Auto-reconnect to galaxy server if the session is still valid.
         */
        if (isNelsGalaxyConnection || (VaadinSession.getCurrent().getAttribute("ApiKey") != null && VaadinSession.getCurrent().getAttribute("galaxyUrl") != null)) {
            webPeptideShakerApp.reConnectToGalaxyServer(VaadinSession.getCurrent().getAttribute("ApiKey") + "", VaadinSession.getCurrent().getAttribute("galaxyUrl") + "");
        }

    }

    /**
     * Check Galaxy server is available.
     *
     * @param urlAddress Galaxy server url
     * @return is galaxy server available online
     */
    private boolean checkConnectionToGalaxy(String urlAddress) {
        try {
            URL url = new URL(urlAddress);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int code = connection.getResponseCode();
            if (code == 404) {
                return false;
            }

        } catch (MalformedURLException ex) {
            return false;
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

    @Override
    public void addExtension(Extension extension) {
        super.addExtension(extension);

    }

    /**
     * Main application servlet.
     */
    @WebServlet(urlPatterns = "/*", name = "PeptidShakerUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = PeptidShakerUI.class, productionMode = true, resourceCacheTime = 0)//, resourceCacheTime = 1
    public static class PeptidShakerUIServlet extends VaadinServlet {
    }

}
