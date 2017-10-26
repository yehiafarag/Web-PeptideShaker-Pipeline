package com.uib.web.peptideshaker;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.Extension;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import java.util.Set;
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

//        Component grid = new BasicPlot().getGrid();
//        setContent(grid);
        setContent(webPeptideShakerApp);
        mimicNelsLogin();
        if (VaadinSession.getCurrent().getAttribute("ApiKey") != null && VaadinSession.getCurrent().getAttribute("galaxyUrl") != null) {
            webPeptideShakerApp.reConnectToGalaxy(VaadinSession.getCurrent().getAttribute("ApiKey") + "", VaadinSession.getCurrent().getAttribute("galaxyUrl") + "");

        }

        ///for testing 
//        VerticalLayout ChartFiltersContainer = new VerticalLayout();
//        ChartFiltersContainer.setWidth(100, Unit.PERCENTAGE);
//        ChartFiltersContainer.setHeight(100, Unit.PERCENTAGE);
//        MatrixLayoutChartFilter filter3 = new MatrixLayoutChartFilter("Validation");
//        ChartFiltersContainer.addComponent(filter3);
//        ChartFiltersContainer.setComponentAlignment(filter3, Alignment.MIDDLE_CENTER);
//        setContent(ChartFiltersContainer);
//
//        Map<String, Set<String>> rows = new LinkedHashMap<>();
//        Random r = new Random();
//        String alphabet = "abcdefghijklmnopqrstuvwxyz";
//        String alphabet2 = "ABCDEF";
//        for (int i = 0; i < alphabet2.length(); i++) {
//            String key = "" + alphabet2.charAt(i);
//            rows.put(key, new HashSet<>());
//            int protNum = r.nextInt(alphabet2.length());
//            for (int y = 0; y < protNum; y++) {
//                rows.get(key).add(""+alphabet.charAt(r.nextInt(alphabet.length())));
//                rows.get(key).add("ZzZ");
//            }
//        }
//        filter3.updateChartData(rows);
    }

    private void mimicNelsLogin() {
        // Create a new cookie
        initCookie("SimpleSAMLAuthToken", "_bdf887a4276b6eaf728bdc9234cba2c370b28d87cb");
//        initCookie("PHPSESSID", "5ff92840ca235709497f8ba4fcc1a0e7");
        initCookie("AuthMemCookie", "_cba251b42b45e328de95da43bd84bb3634da532e94");
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

    /**
     * Given a Range and an group of other Ranges, identify the set of ranges in
     * the group which overlap with the first range. Note this returns a
     * Set<Range>
     * not a RangeSet, because we don't want to collapse connected ranges
     * together.
     */
    public <T extends Comparable<?>> Set<Range<T>>
            getIntersectingRanges(Range<T> intersects, Iterable<Range<T>> ranges) {
        ImmutableSet.Builder<Range<T>> builder = ImmutableSet.builder();
        for (Range<T> r : ranges) {
            if (r.isConnected(intersects) && !r.intersection(intersects).isEmpty()) {
                builder.add(r);
            }
        }
        return builder.build();
    }

}
