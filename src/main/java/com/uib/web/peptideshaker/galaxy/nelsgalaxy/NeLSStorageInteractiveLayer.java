package com.uib.web.peptideshaker.galaxy.nelsgalaxy;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Notification;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URL;
import java.util.LinkedHashMap;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.Cookie;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * This class represents the connection layer to NeLS-Galaxy server the class is
 * used in case of connecting to NeLS-Galaxy in Norway the class will be
 * responsible for managing the initial connection parameters
 *
 * @author Yehia Farag
 */
public class NeLSStorageInteractiveLayer {

    private final LinkedHashMap<String, String> NeLSFilesMap;

    /**
     * Constructor to initialise the main data structure objects.
     */
    public NeLSStorageInteractiveLayer() {
        NeLSFilesMap = new LinkedHashMap<>();
        VaadinSession.getCurrent().setAttribute("nelsFilesMap", NeLSFilesMap);
    }

    /**
     * Reading the initial cookies coming with the request to find if it is
     * redirected from NeLS.
     *
     * @param vaadinRequest the request used to initialise the application UI
     * @return boolean is Nels Storage supported server
     * @todo: will be updated based on final agreement with UiB NeLS-Galaxy
     * administrators on the best authentication method available to access the
     * user date
     */
    public boolean isNelsGalaxyConnection(VaadinRequest vaadinRequest) {
        boolean isNelsGalaxyConnection;
        String NeLSGalaxyDomainURL = "";
        Cookie[] cookies = vaadinRequest.getCookies();
        String cookiesRequestProperty = "";
        String nelsCookiesRequestProperty = "";
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("galaxyUrl")) {
                NeLSGalaxyDomainURL = cookie.getValue();
                VaadinSession.getCurrent().setAttribute("galaxyUrl", NeLSGalaxyDomainURL + "/galaxy");
            }
        }
        isNelsGalaxyConnection = Boolean.FALSE;
        cookiesRequestProperty = cookiesRequestProperty.replaceFirst(";", "");
        nelsCookiesRequestProperty = nelsCookiesRequestProperty.replaceFirst(";", "");
        if (cookiesRequestProperty.contains("SimpleSAMLAuthToken")) {
            VaadinSession.getCurrent().setAttribute("cookies", cookiesRequestProperty);
            isNelsGalaxyConnection = Boolean.TRUE;

        }

        if (isNelsGalaxyConnection) {
            connectToNelsGalaxy(cookiesRequestProperty, NeLSGalaxyDomainURL);
            VaadinSession.getCurrent().setAttribute("nelsCookies", nelsCookiesRequestProperty);//+VaadinSession.getCurrent().getSession().getAttribute("galaxysession"));
            connectToNelsDataStor(nelsCookiesRequestProperty, NeLSGalaxyDomainURL);
        }
        return isNelsGalaxyConnection;

    }

    /**
     * Re-produce the connection to GalaxyNels using the authentication cookies
     * to get the user API key.
     *
     * @param cookiesRequestProperty the authentication cookies used to access
     * NeLS-Galaxy server.
     * @param domainURL path to the server.
     */
    private void connectToNelsGalaxy(String cookiesRequestProperty, String domainURL) {
        try {
            String url = domainURL + "/galaxy/user/api_keys?cntrller=user";
            URL obj = new URL(url);
            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.addRequestProperty("Upgrade-Insecure-Requests", "1");
            con.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            con.addRequestProperty("Cookie", cookiesRequestProperty);
            con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.62 Safari/537.36");
            con.addRequestProperty("Accept-Language", "ar,en-US;q=0.8,en;q=0.6,en-GB;q=0.4");
            con.addRequestProperty("Cache-Control", "max-age=0");
            con.addRequestProperty("Connection", "keep-alive");
            con.addRequestProperty("DNT", "1");
            con.addRequestProperty("Accept-Encoding", "gzip, deflate, br");
            con.addRequestProperty("Host", domainURL);
            con.addRequestProperty("Referer", domainURL + "/idp/module.php/core/loginuserpass.php?");
            StringBuilder response;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {//&& !inputLine.contains("new_api_key_button")) {
                    response.append(inputLine);
                }
            }
            Document doc = Jsoup.parse(response.toString());
            Element e = doc.getElementsByClass("form-row").first();
            String nelsGalaxyAPI = e.text().substring(17); //response.toString().split("<label>Current API key:</label>")[1].split("</div>")[0].trim();
            VaadinSession.getCurrent().setAttribute("ApiKey", nelsGalaxyAPI);
            VaadinSession.getCurrent().getSession().setAttribute("ApiKey", nelsGalaxyAPI);
            VaadinSession.getCurrent().getSession().setAttribute("galaxysession", con.getHeaderField("Set-Cookie").split(";")[0]);
        } catch (IOException e) {
            System.out.println("at Error at line 109 in class " + this.getClass().getName() + "  " + e.getMessage());
        }
    }

    /**
     * Re-produce the connection to NeLS cloud using the authentication cookies
     * to get the user folder path on NeLS and user folder path parameter which
     * will be used to access the user folder on NeLS.
     *
     * @param cookiesRequestProperty the authentication cookies used to access
     * NeLS-Galaxy server.
     * @param domainURL path to the server.
     */
    private void connectToNelsDataStor(String cookiesRequestProperty, String domainURL) {
        try {

            String url = domainURL + "/nels/pages/login.xhtml";
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.addRequestProperty("Upgrade-Insecure-Requests", "1");
            con.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            con.addRequestProperty("Cookie", cookiesRequestProperty);
            con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.62 Safari/537.36");
            con.addRequestProperty("Accept-Language", "ar,en-US;q=0.8,en;q=0.6,en-GB;q=0.4");
            con.addRequestProperty("Cache-Control", "max-age=0");
            con.addRequestProperty("Connection", "keep-alive");
            con.addRequestProperty("DNT", "1");
            con.addRequestProperty("Accept-Encoding", "gzip, deflate, br");
            con.addRequestProperty("Host", domainURL);
            StringBuilder response;
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
            Document doc = Jsoup.parse(response.toString());
            Element e = doc.getElementsByTag("form").first();
            String nelsId = e.text().split("NeLS ID : ")[1].replace(")", "").replace("|", "").trim();// response.toString().split("NeLS ID :")[1].replace(" ", "").replace(")", "___").split("___")[0];
            VaadinSession.getCurrent().setAttribute("nelsUserId", nelsId);
            String openLink = "";
            Elements eList = doc.getElementsByTag("script");
            for (Element et : eList) {
                if (et.html().contains("/nels/pages/file-browse.xhtml")) {
                    openLink = domainURL + "/nels/pages/file-browse.xhtml" + et.html().split("nels/pages/file-browse.xhtml")[1].split("==&isFolder=True'")[0] + "==&isFolder=True";
                    break;
                }
            }

            getNelsFiles(cookiesRequestProperty, openLink, domainURL);

        } catch (IOException e) {

            Notification.show("Could not connect to all service - redirect to NeLsGalaxy login form", Notification.Type.ERROR_MESSAGE);
            Thread t = new Thread(() -> {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            });
            t.start();
            e.printStackTrace();
            System.out.println("at Error at line 1158 in class " + this.getClass().getName() + "  " + e.getMessage());

        }
    }

    /**
     * Re-produce the connection to NeLS cloud using the authentication cookies
     * to get list of files exist on the server
     *
     * @param cookiesRequestProperty the authentication cookies used to access
     * NeLS-Galaxy server.
     * @param nelsFolderURL path to the user folder in NeLS.
     * @param domainURL path to the server.
     */
    private void getNelsFiles(String cookiesRequestProperty, String nelsFolderURL, String domainURL) {
        try {
            NeLSFilesMap.clear();
            URL obj = new URL(nelsFolderURL);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.addRequestProperty("Upgrade-Insecure-Requests", "1");
            con.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            con.addRequestProperty("Cookie", cookiesRequestProperty);
            con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.62 Safari/537.36");
            con.addRequestProperty("Accept-Language", "ar,en-US;q=0.8,en;q=0.6,en-GB;q=0.4");
            con.addRequestProperty("Cache-Control", "max-age=0");
            con.addRequestProperty("Connection", "keep-alive");
            con.addRequestProperty("DNT", "1");
            con.addRequestProperty("Accept-Encoding", "gzip, deflate, br");
            con.addRequestProperty("Host", domainURL);
            StringBuilder response;
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
            Document doc = Jsoup.parse(response.toString());

            Element element = doc.getElementsByTag("tbody").first();
            String folderId = element.getElementsByClass("folder").first().getElementsByTag("ul").text().replace("/", "");
            Elements eList = element.getElementsByClass("ui-widget-content");
            for (Element e : eList) {
                if (!e.tagName().contains("tr") || e.attr("data-rk").startsWith("fldr")) {
                    continue;
                }
                String name = e.getElementsByTag("a").first().text();
                String fileInfo = e.attr("data-rk") + "__" + domainURL + e.getElementsByTag("a").first().attr("href") + "__" + name.replace(".", "_-_").split("_-_")[name.replace(".", "_-_").split("_-_").length - 1];
                NeLSFilesMap.put(name, fileInfo);
            }
            VaadinSession.getCurrent().setAttribute("nelsFolderPath", folderId);
            VaadinSession.getCurrent().setAttribute("nelsFilesMap", NeLSFilesMap);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("at Error at line 215 in class " + this.getClass().getName() + "  " + e.getCause().getMessage());
        }
    }
}
