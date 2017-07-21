package com.uib.web.peptideshaker;

import com.uib.web.peptideshaker.presenter.core.filtercharts.ChartFiltersContainer;
import com.uib.web.peptideshaker.presenter.core.filtercharts.MatrixLayoutChartFilter;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.Extension;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of a html page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@Theme("webpeptideshakertheme")
public class PeptidShakerUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        ServletContext scx = VaadinServlet.getCurrent().getServletContext();
        String userDataFolderUrl = (scx.getInitParameter("filesURL"));
        VaadinSession.getCurrent().setAttribute("userDataFolderUrl", userDataFolderUrl);

//        if (VaadinSession.getCurrent().getAttribute("presessionid") != null) {
//            UI.getCurrent().getSession().close();
//            VaadinSession.getCurrent().getSession().invalidate();
//            Page.getCurrent().reload();
//        }
        VaadinSession.getCurrent().setAttribute("presessionid", "it is exist session");
        this.setSizeFull();
        WebPeptideShakerApp webPeptideShakerApp = new WebPeptideShakerApp();
        if ((Page.getCurrent().getBrowserWindowWidth() < Page.getCurrent().getBrowserWindowHeight()) || (Page.getCurrent().getBrowserWindowWidth() < 650) || (Page.getCurrent().getBrowserWindowHeight() < 600)) {
            webPeptideShakerApp.addStyleName("horizontalcss");
        } else {
            webPeptideShakerApp.removeStyleName("horizontalcss");
        }
        Page.getCurrent().addBrowserWindowResizeListener((Page.BrowserWindowResizeEvent event) -> {
            if ((Page.getCurrent().getBrowserWindowWidth() < Page.getCurrent().getBrowserWindowHeight()) || (Page.getCurrent().getBrowserWindowWidth() < 650) || (Page.getCurrent().getBrowserWindowHeight() < 600)) {
                webPeptideShakerApp.addStyleName("horizontalcss");
            } else {
                webPeptideShakerApp.removeStyleName("horizontalcss");
            }
            for (Window w : UI.getCurrent().getWindows()) {
                w.center();
            }
        });

//        setContent(webPeptideShakerApp);
        mimicNelsLogin();
        if (VaadinSession.getCurrent().getAttribute("ApiKey") != null && VaadinSession.getCurrent().getAttribute("galaxyUrl") != null) {
            webPeptideShakerApp.reConnectToGalaxy(VaadinSession.getCurrent().getAttribute("ApiKey") + "", VaadinSession.getCurrent().getAttribute("galaxyUrl") + "");

        }
        
        ///for testing 
        VerticalLayout ChartFiltersContainer = new VerticalLayout();
        ChartFiltersContainer.setWidth(100,Unit.PERCENTAGE);
        ChartFiltersContainer.setHeight(100,Unit.PERCENTAGE);
          MatrixLayoutChartFilter filter3 = new MatrixLayoutChartFilter("Validation");
        ChartFiltersContainer.addComponent(filter3);
        ChartFiltersContainer.setComponentAlignment(filter3, Alignment.MIDDLE_CENTER);
        setContent(ChartFiltersContainer);
        
            String[] labels = new String[22];
        for (int i = 0; i < 22; i++) {
            labels[i] = ("" + (i + 1));
        }
        List<Double> data = new ArrayList<>();
        for (String label : labels) {
            data.add((Math.random() * 9.0));
        }
        filter3.updateChartData(data, labels);
        
        
       
        
    }

    private void mimicNelsLogin() {
        // Create a new cookie
        initCookie("SimpleSAMLAuthToken", "_b2a5dc90062df4afe96340aabcaa83cb63aba0088d");
        initCookie("PHPSESSID", "c96b2f39f0e7e6ffd44ae3339192f3a8");
        initCookie("AuthMemCookie", "_ca646df0314db57f7217cb8a449e9715bf93a034c1");
    }

    private void initCookie(String name, String value) {
        // Create a new cookie
        Cookie myCookie = new Cookie(name, value);
// Make cookie expire in 2 minutes
        myCookie.setMaxAge(120);
// Set the cookie path.
        myCookie.setPath(VaadinService.getCurrentRequest().getContextPath());
// Save cookie
        VaadinService.getCurrentResponse().addCookie(myCookie);
    }

    @Override
    public void addExtension(Extension extension) {
        super.addExtension(extension); //To change body of generated methods, choose Tools | Templates.
    }

    @WebServlet(urlPatterns = "/*", name = "PeptidShakerUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = PeptidShakerUI.class, productionMode = false)
    public static class PeptidShakerUIServlet extends VaadinServlet {
    }
}
