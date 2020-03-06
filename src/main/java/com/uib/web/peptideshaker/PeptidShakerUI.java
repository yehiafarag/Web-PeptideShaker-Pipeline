package com.uib.web.peptideshaker;

import com.uib.web.peptideshaker.galaxy.nelsgalaxy.NeLSStorageInteractiveLayer;
import com.uib.web.peptideshaker.presenter.core.BasicUploader;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
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
    private boolean mobileScreenComp;
    private boolean verticalScreenMode;

    /**
     * The entry point for the application .
     *
     * @param vaadinRequest represents the incoming request
     */
    @Override
    protected void init(VaadinRequest vaadinRequest) {
        String localFileSystemFolderPath;
        String tempFolder;
        try {
            PeptidShakerUI.this.addStyleName("uicontainer");
            PeptidShakerUI.this.setSizeFull();
            notification = new Notification("Use the device in landscape mode <center><i>(recommended)</i></center>", Notification.Type.ERROR_MESSAGE);
            notification.setDelayMsec(10000);
            notification.setHtmlContentAllowed(true);
            notification.setStyleName("mobilealertnotification");
            Path path;
            try {
                path = Files.createTempDirectory("userTempFolder");
                path.toFile().deleteOnExit();
                localFileSystemFolderPath = path.toFile().getAbsolutePath();
                tempFolder = path.toFile().getParentFile().getAbsolutePath();
            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
            /**
             * Initialise the context parameters and store them in
             * VaadinSession.
             */
            ServletContext scx = VaadinServlet.getCurrent().getServletContext();
            scx.setAttribute("tempFolder", tempFolder);

            VaadinSession.getCurrent().setAttribute("userDataFolderUrl", localFileSystemFolderPath);
            VaadinSession.getCurrent().getSession().setAttribute("userDataFolderUrl", localFileSystemFolderPath);
            VaadinSession.getCurrent().setAttribute("ctxPath", vaadinRequest.getContextPath());
            String testUserAPIKey = (scx.getInitParameter("testUserAPIKey"));
            VaadinSession.getCurrent().setAttribute("testUserAPIKey", testUserAPIKey);
            String galaxyServerUrl = (scx.getInitParameter("galaxyServerUrl"));
            VaadinSession.getCurrent().setAttribute("galaxyServerUrl", galaxyServerUrl);
            String csfprLink = (scx.getInitParameter("csfprservice"));
            VaadinSession.getCurrent().setAttribute("csfprLink", csfprLink);

            String psVersion = (scx.getInitParameter("psvirsion"));
            String searchGUIversion = (scx.getInitParameter("searchguivirsion"));
            VaadinSession.getCurrent().setAttribute("psVersion", psVersion);
            VaadinSession.getCurrent().setAttribute("searchGUIversion", searchGUIversion);

            mobileScreenComp = false;//(Page.getCurrent().getWebBrowser().getBrowserApplication().contains("Mobile")) || (screenW < 1349 || screenH < 1000);
            verticalScreenMode = false;//(Page.getCurrent().getBrowserWindowWidth() < Page.getCurrent().getBrowserWindowHeight());

//            if (mobileScreenComp) {
            VaadinSession.getCurrent().setAttribute("smallscreenstyle", false);
            VaadinSession.getCurrent().setAttribute("mobilescreenstyle", mobileScreenComp);
//            }

            WebPeptideShakerApp webPeptideShakerApp = new WebPeptideShakerApp(galaxyServerUrl);
            PeptidShakerUI.this.setContent(webPeptideShakerApp);

            /**
             * Check the visualisation mode based on screen size small screen
             * for mobile browser or tablet.
             *
             * @todo: we need better optimisation to detect the window ratio.
             */
//        if ((Page.getCurrent().getBrowserWindowWidth() < Page.getCurrent().getBrowserWindowHeight()) || (Page.getCurrent().getBrowserWindowWidth() < 650) || (Page.getCurrent().getBrowserWindowHeight() < 600)) {
            if (mobileScreenComp) {
                webPeptideShakerApp.addStyleName("mobilestyle");
            }
            if (mobileScreenComp && !(Page.getCurrent().getWebBrowser().getBrowserApplication().contains("Mobile"))) {
                webPeptideShakerApp.addStyleName("smallpcscreen");
            }
//            if (smallscreen && verticalScreenMode) {
//                webPeptideShakerApp.addStyleName("verticalmode");
//            }
            /**
             * On resize the browser re-arrange all the created pop-up windows
             * to the page center.
             */
            Page.getCurrent().addBrowserWindowResizeListener((Page.BrowserWindowResizeEvent event) -> {

                if (mobileScreenComp && (Page.getCurrent().getBrowserWindowWidth() < Page.getCurrent().getBrowserWindowHeight())) {
                    if (Page.getCurrent().getWebBrowser().getBrowserApplication().contains("Mobile")) {
                        notification.show(Page.getCurrent());
                    }
                    webPeptideShakerApp.addStyleName("verticalmode");
//                    webPeptideShakerApp.addStyleName("hidemode");
                } else if (Page.getCurrent().getWebBrowser().getBrowserApplication().contains("Mobile") && (Page.getCurrent().getBrowserWindowWidth() >= Page.getCurrent().getBrowserWindowHeight())) {
//                    webPeptideShakerApp.removeStyleName("hidemode");
                    webPeptideShakerApp.removeStyleName("verticalmode");

                }
                UI.getCurrent().getWindows().forEach((w) -> {
                    w.center();
                });
            });

            if (mobileScreenComp && verticalScreenMode) {
                if (Page.getCurrent().getWebBrowser().getBrowserApplication().contains("Mobile")) {
                    notification.show(Page.getCurrent());
                }
                webPeptideShakerApp.addStyleName("verticalmode");
//                    webPeptideShakerApp.addStyleName("hidemode");
            } else if (mobileScreenComp && !verticalScreenMode) {
//                    webPeptideShakerApp.removeStyleName("hidemode");
                webPeptideShakerApp.removeStyleName("verticalmode");

            }

            /**
             * for future use to integrate with NeLS.
             *
             * @todo:re implement all concept in future
             */
            NeLS_Galaxy = new NeLSStorageInteractiveLayer();
            boolean isNelsGalaxyConnection = NeLS_Galaxy.isNelsGalaxyConnection(vaadinRequest);
            VaadinSession.getCurrent().setAttribute("nelsgalaxy", isNelsGalaxyConnection);
            Page.getCurrent().setTitle("PeptideShaker");
        } catch (Exception e) {
            e.printStackTrace();

        }

//        getSpectrum();
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
