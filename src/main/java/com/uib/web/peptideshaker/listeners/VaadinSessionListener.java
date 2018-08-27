package com.uib.web.peptideshaker.listeners;

import java.io.File;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * This class responsible for cleaning folders after user session expire
 *
 * @author Yehia Farag
 */
public class VaadinSessionListener implements HttpSessionListener, ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("at welcome back to life :-) ");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        String userDataFolderUrl = sce.getServletContext().getInitParameter("filesURL");
        File usersFolder = new File(userDataFolderUrl);
        if (usersFolder.exists()) {
            for (File tFile : usersFolder.listFiles()) {
                if (tFile.isDirectory()) {
                    for (File subtFile : tFile.listFiles()) {
                        subtFile.delete();
                    }
                }
                System.out.println("at folder here  " + tFile.getName() + "  deleted " + tFile.delete());
            }
        }
        System.out.println("at context now cleaned is ready to distroy ..Good bye...folder (" + usersFolder.getName() + ") now it is  cleaned "+usersFolder.listFiles().length);
       
    }

    @Override
    public void sessionCreated(HttpSessionEvent hse) {
        System.out.println("at welcome to new session");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent hse) {
        String ApiKey = hse.getSession().getAttribute("ApiKey") + "";
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
