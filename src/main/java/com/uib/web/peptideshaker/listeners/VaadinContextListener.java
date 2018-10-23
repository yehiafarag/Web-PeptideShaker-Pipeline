/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.listeners;

import java.io.File;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author y-mok
 */
public class VaadinContextListener implements ServletContextListener{
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
            }
        }
        System.out.println("at context now cleaned is ready to distroy ..Good bye...folder (" + usersFolder.getName() + ") now it is  cleaned " + usersFolder.listFiles().length);

    }
}
