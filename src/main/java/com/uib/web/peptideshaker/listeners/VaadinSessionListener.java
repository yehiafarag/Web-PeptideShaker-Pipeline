package com.uib.web.peptideshaker.listeners;

import java.io.File;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * This class responsible for cleaning folders after user session expire
 *
 * @author Yehia Farag
 */
public class VaadinSessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent hse) {
        System.out.println("at *****welcome user*******");

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

  

}
