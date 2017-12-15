package com.uib.web.peptideshaker;

import com.uib.web.peptideshaker.galaxy.nelsgalaxy.NeLSGalaxy;
import com.uib.web.peptideshaker.presenter.components.peptideshakerview.components.LiteMOLComponent;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.event.LayoutEvents;
import com.vaadin.server.Extension;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import elemental.json.JsonArray;
import javax.servlet.ServletContext;

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

    /**
     * The connection layer to NeLS-Galaxy server.
     */
    private NeLSGalaxy NeLS_Galaxy;

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        PeptidShakerUI.this.setSizeFull();
        ServletContext scx = VaadinServlet.getCurrent().getServletContext();

        String fullPath = scx.getRealPath("/VAADIN/jsmol/data/");
        System.out.println("get real data folder path " + fullPath);

        String localFileSystemFolderPath = (scx.getInitParameter("filesURL"));
        VaadinSession.getCurrent().setAttribute("userDataFolderUrl", localFileSystemFolderPath);
        VaadinSession.getCurrent().getSession().setAttribute("userDataFolderUrl", localFileSystemFolderPath);
        VaadinSession.getCurrent().setAttribute("ctxPath", vaadinRequest.getContextPath());
        /**
         * Check the visualization mode based on screen size small screen for
         * mobile browser or tablet.
         *
         * @todo: we need better optimization to detect the window ratio.
         */
        WebPeptideShakerApp webPeptideShakerApp = new WebPeptideShakerApp();
        if ((Page.getCurrent().getBrowserWindowWidth() < Page.getCurrent().getBrowserWindowHeight()) || (Page.getCurrent().getBrowserWindowWidth() < 650) || (Page.getCurrent().getBrowserWindowHeight() < 600)) {
            webPeptideShakerApp.addStyleName("horizontalcss");
        } else {
            webPeptideShakerApp.removeStyleName("horizontalcss");
        }
        /**
         * On resize the browser re-arrange all the created windows to center.
         */
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

        NeLS_Galaxy = new NeLSGalaxy();
        boolean isNelsGalaxyConnection = NeLS_Galaxy.isNelsGalaxyConnection(vaadinRequest);
        VaadinSession.getCurrent().setAttribute("nelsgalaxy", isNelsGalaxyConnection);
        setContent(webPeptideShakerApp);
        if (isNelsGalaxyConnection || (VaadinSession.getCurrent().getAttribute("ApiKey") != null && VaadinSession.getCurrent().getAttribute("galaxyUrl") != null)) {
            webPeptideShakerApp.reConnectToGalaxyServer(VaadinSession.getCurrent().getAttribute("ApiKey") + "", VaadinSession.getCurrent().getAttribute("galaxyUrl") + "");

        }
        System.out.println("at request address " + vaadinRequest.getHeaderNames() + "  " + vaadinRequest.getContextPath());

        VerticalLayout container = new VerticalLayout();
        container.setSizeFull();
        container.setSpacing(true);
        
        LiteMOLComponent comp = new LiteMOLComponent();
       comp.setSizeFull();
       comp.setMargin(true);
       comp.loadProtein("1cbs");
        container.addComponent(comp);
        
        container.addLayoutClickListener(new LayoutEvents.LayoutClickListener() {
            @Override
            public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                 comp.loadProtein("5exw");
                 Notification.show("clicked working");
            
            }
        });
        

//        Label proteinStructurePanel = new Label("<iframe id='jsmolframe' src='jsmol'  style='width:100%; height:100%'></iframe>", ContentMode.HTML);        
//        proteinStructurePanel.setSizeFull();
//        container.addComponent(proteinStructurePanel);
//         container.setExpandRatio(proteinStructurePanel,0.9f);
//        TextArea textArea = new TextArea();
//        textArea.setValue("document.getElementById('jsmolframe').contentWindow.loadNewProtein('1BLU');");
//        container.addComponent(textArea);
//         container.setExpandRatio(textArea,0.5f);
//        textArea.setSizeFull();
//        final Button executeButton = new Button("Execute", event -> JavaScript.getCurrent().execute(textArea.getValue()));
//        container.addComponent(executeButton);
//        container.setExpandRatio(executeButton,0.5f);
//    
//       
//
        Window testWindow = new Window("Test JSMOL");
        testWindow.setWidth(90, Unit.PERCENTAGE);
        testWindow.setHeight(90, Unit.PERCENTAGE);
        testWindow.center();
        testWindow.setContent(container);
        UI.getCurrent().addWindow(testWindow);
        testWindow.setVisible(true);

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
