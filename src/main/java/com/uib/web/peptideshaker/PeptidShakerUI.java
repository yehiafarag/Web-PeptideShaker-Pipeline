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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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
        String localFileSystemFolderPath = "";//(scx.getInitParameter("filesURL"));   
        String tempFolder = "";
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
                System.out.println(path);
                boolean b = Files.isDirectory(path);
                path.toFile().deleteOnExit();

                localFileSystemFolderPath = path.toFile().getAbsolutePath();
                tempFolder = path.toFile().getParentFile().getAbsolutePath();
//         userUploadFolder = n
            } catch (IOException ex) {

                Logger.getLogger(BasicUploader.class.getName()).log(Level.SEVERE, null, ex);
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
//            String dbURL = (scx.getInitParameter("url"));
//            String dbDriver = (scx.getInitParameter("driver"));
//            String dbUserName = (scx.getInitParameter("userName"));
//            String dbPassword = (scx.getInitParameter("password"));
//            VaadinSession.getCurrent().setAttribute("dbURL", dbURL);
//            VaadinSession.getCurrent().setAttribute("dbDriver", dbDriver);
//            VaadinSession.getCurrent().setAttribute("dbUserName", dbUserName);
//            VaadinSession.getCurrent().setAttribute("dbPassword", dbPassword);
            VaadinSession.getCurrent().setAttribute("csfprLink", csfprLink);

            String psVersion = (scx.getInitParameter("psvirsion"));
            String searchGUIversion = (scx.getInitParameter("searchguivirsion"));
            VaadinSession.getCurrent().setAttribute("psVersion", psVersion);
            VaadinSession.getCurrent().setAttribute("searchGUIversion", searchGUIversion);

            if (testUserAPIKey == null || galaxyServerUrl == null || !checkConnectionToGalaxy(galaxyServerUrl)) {
//              updateCSFPRProteinsList(csfProteinsListURL);
                notification = new Notification("<center style=' color: black;>'><font style='font-size: 14px;font-weight: 600;line-height: 31px;word-spacing: 4px; letter-spacing: 1px;'>Contact administrator !</font><br>Galaxy server is not available</center>", Notification.Type.WARNING_MESSAGE);
                notification.setHtmlContentAllowed(true);
                notification.setDelayMsec(-1);
                notification.show(Page.getCurrent());
                return;
            }
            int screenH = Page.getCurrent().getWebBrowser().getScreenHeight();
            int screenW = Page.getCurrent().getWebBrowser().getScreenWidth();
            mobileScreenComp = false;//(Page.getCurrent().getWebBrowser().getBrowserApplication().contains("Mobile")) || (screenW < 1349 || screenH < 1000);
            verticalScreenMode = false;//(Page.getCurrent().getBrowserWindowWidth() < Page.getCurrent().getBrowserWindowHeight());

//            if (mobileScreenComp) {
            VaadinSession.getCurrent().setAttribute("smallscreenstyle", false);
            VaadinSession.getCurrent().setAttribute("mobilescreenstyle", mobileScreenComp);
//            }

            WebPeptideShakerApp webPeptideShakerApp = new WebPeptideShakerApp();
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

    /**
     * Check Galaxy server is available.
     *
     * @param urlAddress Galaxy server url
     * @return is galaxy server available online
     */
    private boolean checkConnectionToGalaxy(String urlAddress) {
        try {
            URL url = new URL(urlAddress);
            if (urlAddress.contains("https")) {
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                int code = connection.getResponseCode();
                if (code == 404) {
                    return false;
                }
            } else {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                int code = connection.getResponseCode();
                if (code == 404) {
                    return false;
                }
            }

        } catch (MalformedURLException ex) {
            return false;
        } catch (IOException ex) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void addExtension(Extension extension) {
        super.addExtension(extension);

    }

    @Override
    public void removeExtension(Extension extension) {
//        super.removeExtension(extension);

    }

    /**
     * Main application servlet.
     */
    @WebServlet(urlPatterns = "/*", name = "PeptidShakerUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = PeptidShakerUI.class, productionMode = true, resourceCacheTime = 0)//, resourceCacheTime = 1
    public static class PeptidShakerUIServlet extends VaadinServlet {
    }

    /**
     * Get MSn spectrum object using HTML request to Galaxy server (byte serving
     * support).
     *
     * @param startIndex the spectra index on the MGF file
     * @param historyId the Galaxy Server History ID that contain the MGF file
     * @param MGFGalaxyID The ID of the MGF file on Galaxy Server
     * @param MGFFileName The MGF file name
     * @return MSnSpectrum spectrum object
     */
    private void getSpectrum() {

        try {
            StringBuilder locationBuilder = new StringBuilder("https://galaxy-uib.bioinfo.no/api/histories/d52bb4af7fa2c539/contents/6ee1d49667a686b6/display?key=042f8dd459607d2d56c8d41cfb6eb5b5");//"/api/histories/" + historyId + "/contents/" + MGFGalaxyID + "/display?"

            String location = locationBuilder.toString();
            URL website = new URL(location);
            URLConnection conn = website.openConnection();
            conn.addRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
            conn.addRequestProperty("Accept-Encoding", "gzip, deflate, sdch, br");
            conn.addRequestProperty("Accept-Language", "ar,en-US;q=0.8,en;q=0.6,en-GB;q=0.4");
            conn.addRequestProperty("Cache-Control", "no-cache");
            conn.addRequestProperty("Connection", "keep-alive");
            conn.addRequestProperty("Range", "bytes=" + 65544 + "-" + Long.MAX_VALUE);
            conn.addRequestProperty("offset", "65544");
            conn.addRequestProperty("DNT", "1");
            conn.addRequestProperty("X-Requested-With", "XMLHttpRequest");
            conn.addRequestProperty("Pragma", "no-cache");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36");
            conn.setDoInput(true);
//https://galaxy-uib.bioinfo.no/api/histories/d52bb4af7fa2c539/contents/6ee1d49667a686b6/display?offset=10000&key=042f8dd459607d2d56c8d41cfb6eb5b5
            String line;
            int counter = 0;
            try (BufferedReader bin = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {

                while ((line = bin.readLine()) != null && counter < 30) {

                    System.out.println("at line " + line);
                    counter++;
                }

            }

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("we will return null spectrum");
    }

}
