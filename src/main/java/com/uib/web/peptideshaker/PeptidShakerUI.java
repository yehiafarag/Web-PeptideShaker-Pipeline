package com.uib.web.peptideshaker;

import com.uib.web.peptideshaker.galaxy.nelsgalaxy.NeLSGalaxy;
import archive.LiteMOLComponent;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.Extension;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
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

        String localFileSystemFolderPath = (scx.getInitParameter("filesURL"));
        VaadinSession.getCurrent().setAttribute("userDataFolderUrl", localFileSystemFolderPath);
        VaadinSession.getCurrent().getSession().setAttribute("userDataFolderUrl", localFileSystemFolderPath);
        VaadinSession.getCurrent().setAttribute("ctxPath", vaadinRequest.getContextPath());
        /**
         * Check the visualisation mode based on screen size small screen for
         * mobile browser or tablet.
         *
         * @todo: we need better optimisation to detect the window ratio.
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
            UI.getCurrent().getWindows().forEach((w) -> {
                w.center();
            });
        });

        NeLS_Galaxy = new NeLSGalaxy();
        boolean isNelsGalaxyConnection = NeLS_Galaxy.isNelsGalaxyConnection(vaadinRequest);
        VaadinSession.getCurrent().setAttribute("nelsgalaxy", isNelsGalaxyConnection);
        setContent(webPeptideShakerApp);
        if (isNelsGalaxyConnection || (VaadinSession.getCurrent().getAttribute("ApiKey") != null && VaadinSession.getCurrent().getAttribute("galaxyUrl") != null)) {
            webPeptideShakerApp.reConnectToGalaxyServer(VaadinSession.getCurrent().getAttribute("ApiKey") + "", VaadinSession.getCurrent().getAttribute("galaxyUrl") + "");
        }
//        Window modalWindow = new Window();
////        modalWindow.setModal(true);
//        modalWindow.setSizeFull();
//        modalWindow.setClosable(false);
//        modalWindow.setResizable(false);
//        modalWindow.setDraggable(false);
//        modalWindow.setPrimaryStyleName("v-loading-indicator");
////        modalWindow.addStyleName("v-window-modalitycurtain");
//           UI.getCurrent().addWindow(modalWindow);

//        VerticalLayout container = new VerticalLayout();
//        container.setSizeFull();
//        container.setSpacing(true);
//
////        CustomLayout clo = new CustomLayout("mylayout");
////        container.addComponent(clo);
//        
//        
////        
////
//        Button b = new Button("test");
//        container.addComponent(b);
//        container.setExpandRatio(b, 0.01f);
//        LiteMOL3DComponent liteMOLComponent = new LiteMOL3DComponent();
//        liteMOLComponent.setSizeFull();
//        liteMOLComponent.setMargin(true);
////        comp.loadProtein("1cbs");
//        container.addComponent(liteMOLComponent);
//        container.setExpandRatio(liteMOLComponent, 0.99f);
//        b.addClickListener((Button.ClickEvent event) -> {
//            liteMOLComponent.loadProtein("5exw");
//            Notification.show("clicked working");
//        });
//
////        Label proteinStructurePanel = new Label("<iframe id='jsmolframe' src='jsmol'  style='width:100%; height:100%'></iframe>", ContentMode.HTML);        
////        proteinStructurePanel.setSizeFull();
////        container.addComponent(proteinStructurePanel);
////         container.setExpandRatio(proteinStructurePanel,0.9f);
////        TextArea textArea = new TextArea();
////        textArea.setValue("document.getElementById('jsmolframe').contentWindow.loadNewProtein('1BLU');");
////        container.addComponent(textArea);
////         container.setExpandRatio(textArea,0.5f);
////        textArea.setSizeFull();
////        final Button executeButton = new Button("Execute", event -> JavaScript.getCurrent().execute(textArea.getValue()));
////        container.addComponent(executeButton);
////        container.setExpandRatio(executeButton,0.5f);
////    
////       
////
//        VerticalLayout mainLayout = new VerticalLayout();
//        mainLayout.setMargin(true);
//        mainLayout.setSpacing(true);
//        mainLayout.setSizeFull();
//
//        LiteMOLComponent LiteMolInfo;
//        LiteMolInfo = new LiteMOLComponent();
//
//        mainLayout.addComponent(LiteMolInfo);
////
//        
        
        
//              try {
//
//            VerticalLayout mainLayout = new VerticalLayout();
//            mainLayout.setMargin(true);
//            mainLayout.setSpacing(true);
//            mainLayout.setSizeFull();
//            Accordion accordion = new Accordion();
//            accordion.setSizeFull();
//         
//          
//            
//            LiteMolInfo = new LiteMOLComponent();
//            accordion.addTab(LiteMolInfo, "LiteMol-Demo");
//            mainLayout.addComponent(accordion);
//
//     
//
//        setContent(container);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("some stupid error occured!", e);
//        }

    }
    protected LiteMOLComponent LiteMolInfo;
    protected Button javaSend;

    @Override
    public void addExtension(Extension extension) {
        super.addExtension(extension); //To change body of generated methods, choose Tools | Templates.

    }

    @WebServlet(urlPatterns = "/*", name = "PeptidShakerUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = PeptidShakerUI.class, productionMode = false)
    public static class PeptidShakerUIServlet extends VaadinServlet {
    }

}
