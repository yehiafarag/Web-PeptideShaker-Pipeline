package com.uib.web.peptideshaker.listeners;

import com.vaadin.server.VaadinService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Set;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * This class responsible for cleaning folders after user session expire
 *
 * @author Yehia Farag
 */
public class VaadinSessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent hse) {
        System.out.println("at *****welcome user*******");
        updateCSFPRProteinsList();

    }

    @Override
    public void sessionDestroyed(HttpSessionEvent hse) {
        String ApiKey = hse.getSession().getAttribute("ApiKey") + "";
        if (ApiKey == null || ApiKey.equalsIgnoreCase(hse.getSession().getAttribute("testUserAPIKey") + "")) {
            return;
        }
        String userDataFolderUrl = hse.getSession().getAttribute("userDataFolderUrl").toString();
        File user_folder = new File(userDataFolderUrl, ApiKey);
        if (user_folder.exists()) {
            for (File tFile : user_folder.listFiles()) {
                tFile.delete();
            }
        }
        boolean cleaned = user_folder.delete();
        System.out.println("at session is ready to distroy ..Good bye...folder (" + user_folder.getName() + " are cleaned (" + cleaned + ") and folder exist (" + user_folder.exists() + ")");
        System.out.println("no more cookies exist");
    }

    /**
     * Get the name list of the files exist in the compressed folder
     *
     * @return the sub files list of files contained in folder (case of
     * compressed folder).
     *
     */
    public void updateCSFPRProteinsList() {
        String updateFileName = "";
        try {
            Document doc = Jsoup.connect("http://129.177.231.63/csf-pr/VAADIN/").get();
            Elements elements = doc.getElementsByTag("body");
            for (Element elemet : elements) {
                if ((elemet.text() + "").contains("prot-")) {
                    for (String fileName : elemet.text().split(" ")) {
                        if (fileName.contains("prot-") && fileName.trim().endsWith(".txt")) {
                            updateFileName = fileName.trim();
                            break;
                        }
                    }
                    break;
                }
            }
            System.out.println("at lozzaa " + updateFileName);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
        File file = new File(basepath + "/VAADIN/" + updateFileName);
        if (file.exists()) {
            return;
        }
        FileOutputStream fos = null;
        try {
            file.createNewFile();
            URL downloadableFile = new URL("http://129.177.231.63/csf-pr/VAADIN/" + updateFileName);
            URLConnection conn = downloadableFile.openConnection();
            conn.addRequestProperty("Connection", "keep-alive");
            conn.setDoInput(true);
            InputStream in = conn.getInputStream();
            try (ReadableByteChannel rbc = Channels.newChannel(in)) {
                fos = new FileOutputStream(file);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                fos.close();
                rbc.close();
                in.close();
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

            }

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            System.out.println("proteins file transfered fine :-) ");

        }

    }

}
