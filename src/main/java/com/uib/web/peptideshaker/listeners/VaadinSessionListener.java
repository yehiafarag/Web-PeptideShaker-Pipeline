package com.uib.web.peptideshaker.listeners;

import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import java.io.File;
import javax.servlet.http.Cookie;
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
        System.out.println("at welcome to new session");
    }
    
    @Override
    public void sessionDestroyed(HttpSessionEvent hse) {
        String ApiKey = hse.getSession().getAttribute("ApiKey") + "";
        String userDataFolderUrl = hse.getSession().getAttribute("userDataFolderUrl").toString();        
        File userFolder = new File(userDataFolderUrl, ApiKey);
        if (userFolder.exists()) {
            for (File tFile : userFolder.listFiles()) {
                tFile.delete();
            }
        }
        boolean cleaned = userFolder.delete();
        System.out.println("at session is ready to distroy ..Good bye...folder (" + userFolder.getName() + " are cleaned (" + cleaned + ") and folder exist (" + userFolder.exists() + ")");
        System.out.println("no more cookies exist");
    }
    
}
