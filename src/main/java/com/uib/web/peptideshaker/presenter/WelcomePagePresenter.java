package com.uib.web.peptideshaker.presenter;

//import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
//import com.uib.onlinepeptideshaker.managers.RegistrableView;
//import com.uib.onlinepeptideshaker.presenter.view.SmallSideBtn;
import com.uib.web.peptideshaker.presenter.core.ButtonWithLabel;
import com.uib.web.peptideshaker.presenter.core.SmallSideBtn;
import com.uib.web.peptideshaker.presenter.core.ViewableFrame;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
//import sun.security.provider.certpath.Vertex;

/**
 * This class represents the welcome page for Online PeptideShaker
 *
 * @author Yehia Farag
 */
public abstract class WelcomePagePresenter extends VerticalLayout implements ViewableFrame {

    /**
     * The header layout panel.
     */
    private final HorizontalLayout mainHeaderPanel;
    /**
     * The header layout container layout.
     */
    private final HorizontalLayout headerPanelContentLayout;

    /**
     * The body layout panel.
     */
    private final VerticalLayout userConnectionPanel;
    private final Label connectingLabel;
    private final ButtonWithLabel connectionBtnLabel;
    /**
     * The connection button to NeLS login page
     */
    private final Link nelsGalaxyConnectionBtn;
    /**
     * Button to direct connection using testing account
     *
     */
//    private final Button loadExampleUserAccount;
    /**
     * Button to connect to user account.
     *
     */
    private final Button galaxyConnectionBtn;
    /**
     * Test user Login account.
     *
     */
    private final String testUserLogin = "test_User_Login";
    /**
     * Not valid API error message .
     *
     */
    private final String apiErrorMessage = "Wrong API please try again";
    /**
     * Connection to galaxy statues label.
     *
     */
//    private final Label loginLabel;
    private final ButtonWithLabel galaxyLloginBtn;
    /**
     * Galaxy login controls layout.
     *
     */
    private final VerticalLayout galaxyLoginLayout;
    /**
     * User API login field.
     *
     */
    private final TextField userAPIFeald;

    /**
     * The side home button .
     */
    private final SmallSideBtn viewControlButton;
    /**
     * Busy connecting window
     */
    private final Window busyConnectinWindow;
    /**
     * Executor service to execute connection task to galaxy server.
     */
    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    private final VerticalLayout presenteControlButtonsLayout;
    private final VerticalLayout userOverviewLayout;
    /**
     *
     */
    private final Window connectinoWindow;
    private String viewId;

    /**
     * Constructor to initialise the layout.
     */
    public WelcomePagePresenter() {
        WelcomePagePresenter.this.setSizeFull();
        WelcomePagePresenter.this.addStyleName("welcomepagestyle");

        AbsoluteLayout container = new AbsoluteLayout();
        container.setSizeFull();
//        container.setSpacing(true);
        container.setStyleName("welcomepagecontainer");

        WelcomePagePresenter.this.addComponent(container);
        WelcomePagePresenter.this.setComponentAlignment(container, Alignment.TOP_CENTER);

        mainHeaderPanel = new HorizontalLayout();
        mainHeaderPanel.setHeight(50, Unit.PIXELS);
        mainHeaderPanel.setWidth(100, Unit.PERCENTAGE);
        container.addComponent(mainHeaderPanel, "left:10px;top:20px;");
//        container.setExpandRatio(mainHeaderPanel, 10);
        headerPanelContentLayout = initializeHeaderPanel();
        mainHeaderPanel.addComponent(headerPanelContentLayout);
        mainHeaderPanel.setMargin(new MarginInfo(false, false, false, false));

//        VerticalLayout connectionCommentLayout = new VerticalLayout();
//        connectionCommentLayout.setSizeFull();
//        mainHeaderPanel.addComponent(connectionCommentLayout);
//        mainHeaderPanel.setComponentAlignment(connectionCommentLayout, Alignment.TOP_RIGHT);
        galaxyLloginBtn = new ButtonWithLabel("Galaxy Login<br/><font>Login using API key</font>", 0);
        galaxyLloginBtn.updateIconResource(new ThemeResource("img/galaxyLogo.png"));//galaxyLogo_2.png
        galaxyLloginBtn.addStyleName("galaxylabel");
        galaxyLloginBtn.setDescription("Login to Galaxy - API key required");

//        loginLabel = new Label("<img src='VAADIN/themes/webpeptideshakertheme/img/galaxyLogo_2.png' alt style='width: 25px;height: 25px;border: 1px solid lightgray;border-radius: 5px;background-color: whitesmoke;cursor:pointer !important;'><font color='#cd6e1d' style='line-height: 0px !important; vertical-align:text-top;cursor:pointer !important;'> Galaxy Login</font>");
//        loginLabel.setContentMode(ContentMode.HTML);
//        loginLabel.setHeight(25, Sizeable.Unit.PIXELS);
//        loginLabel.setWidth(160, Sizeable.Unit.PIXELS);
//        loginLabel.setStyleName(ValoTheme.LABEL_SMALL);
//        loginLabel.addStyleName(ValoTheme.LABEL_BOLD);
//        loginLabel.addStyleName(ValoTheme.LABEL_TINY);
//        loginLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
//        loginLabel.setVisible(true);
//        connectionCommentLayout.addComponent(loginLabel);
//
//        connectionCommentLayout.setComponentAlignment(loginLabel, Alignment.TOP_LEFT);
//        connectionCommentLayout.setStyleName("clickableimg");
        HorizontalLayout mainMiddlePanel = new HorizontalLayout();
        mainMiddlePanel.setSizeFull();
        mainMiddlePanel.setSpacing(true);
        container.addComponent(mainMiddlePanel, "left:10px;top:100px;");
//        container.setExpandRatio(mainMiddlePanel, 70);

        /**
         * The left panel (user data overview/connect-disconnect)
         */
        VerticalLayout userOverviewPanel = new VerticalLayout();
        userOverviewPanel.setWidth(100, Unit.PERCENTAGE);
        userOverviewPanel.setHeightUndefined();
        userOverviewPanel.setSpacing(true);
        userOverviewPanel.setStyleName("useroverviewpanelstyle");
        mainMiddlePanel.addComponent(userOverviewPanel);
        mainMiddlePanel.setExpandRatio(userOverviewPanel, 20);

        /**
         * *user overview layout*
         */
        userOverviewLayout = new VerticalLayout();
        userOverviewLayout.setSizeFull();
        userOverviewPanel.addComponent(userOverviewLayout);
        userOverviewPanel.setExpandRatio(userOverviewLayout, 0.7f);
        userOverviewPanel.setComponentAlignment(userOverviewLayout, Alignment.TOP_LEFT);
        Label overviewLabel = initLeftSideInfoLabel("<b></b>", "");
        overviewLabel.addStyleName(ValoTheme.LABEL_H2);
        userOverviewLayout.addComponent(overviewLabel);

        Label userLabel = initLeftSideInfoLabel(VaadinIcons.USER.getHtml() + " User ", "<i>Offline</i>");
        userOverviewLayout.addComponent(userLabel);

        Label dsNumberLabel = initLeftSideInfoLabel(VaadinIcons.CLUSTER.getHtml() + " Results ", "<i></i>");
        userOverviewLayout.addComponent(dsNumberLabel);

        Label filesNumberLabel = initLeftSideInfoLabel(VaadinIcons.FILE_TEXT_O.getHtml() + " Files ", "<i></i>");
        userOverviewLayout.addComponent(filesNumberLabel);

        Label usedMemory = initLeftSideInfoLabel(VaadinIcons.CLOUD_O.getHtml() + " Storage ", "<i></i>");
        userOverviewLayout.addComponent(usedMemory);

        Label searchGUI = initLeftSideInfoLabel("<img src='VAADIN/themes/webpeptideshakertheme/img/sgiconHRNSgray21.png' alt style='width: auto;height:15px;margin-left:-2px;    margin-right: 4px;'>" + " SearchGUI ", "<i></i>");
        userOverviewLayout.addComponent(searchGUI);

        Label peptideShaker = initLeftSideInfoLabel("<img src='VAADIN/themes/webpeptideshakertheme/img/psiconHRNSgray21.png' alt style='width: auto;height:15px;margin-left:-2px;    margin-right: 4px;'>" + " PeptideShaker ", "<i></i>");
        userOverviewLayout.addComponent(peptideShaker);

        /**
         * The right panel (welcome message / presenter control buttons)
         */
        VerticalLayout presenterControlButtonsPanel = new VerticalLayout();
        presenterControlButtonsPanel.setWidth(100, Unit.PERCENTAGE);
        presenterControlButtonsPanel.setHeight(360, Unit.PIXELS);
        presenterControlButtonsPanel.setSpacing(true);
        mainMiddlePanel.addComponent(presenterControlButtonsPanel);
        mainMiddlePanel.setExpandRatio(presenterControlButtonsPanel, 80);
        presenterControlButtonsPanel.setMargin(new MarginInfo(false, false, false, true));

        VerticalLayout welcomeTextContainerLayout = new VerticalLayout();
        welcomeTextContainerLayout.setSizeFull();
        welcomeTextContainerLayout.setData("ignoreclick");
        welcomeTextContainerLayout.addStyleName("mainbodystyle");
        welcomeTextContainerLayout.addStyleName("connectionpanelstyle");
        welcomeTextContainerLayout.setSpacing(true);
        welcomeTextContainerLayout.setWidth(100, Unit.PERCENTAGE);
        welcomeTextContainerLayout.setHeight(100, Unit.PIXELS);
        presenterControlButtonsPanel.addComponent(welcomeTextContainerLayout);
        presenterControlButtonsPanel.setComponentAlignment(welcomeTextContainerLayout, Alignment.TOP_LEFT);
        presenterControlButtonsPanel.setExpandRatio(welcomeTextContainerLayout, 0.4f);

        Label welcomeText = new Label();
        welcomeText.setSizeFull();
        welcomeText.setContentMode(ContentMode.HTML);
        welcomeText.setStyleName(ValoTheme.LABEL_NO_MARGIN);
        welcomeText.setValue("<font style='font-weight: bold; font-size:23px'>Welcome to PeptideShaker </font>");// <br/><br/>To start using the system connect to your Galaxy Server");
        welcomeText.setData("ignoreclick");
        welcomeTextContainerLayout.addComponent(welcomeText);
        welcomeTextContainerLayout.setExpandRatio(welcomeText, 0.05f);
        welcomeTextContainerLayout.setComponentAlignment(welcomeText, Alignment.TOP_LEFT);

        Label subWelcomeText = new Label();
        subWelcomeText.setSizeFull();
        subWelcomeText.setData("ignoreclick");
        subWelcomeText.setContentMode(ContentMode.HTML);
        subWelcomeText.setStyleName(ValoTheme.LABEL_NO_MARGIN);
        subWelcomeText.setValue("<font>A web interactive visual analysis tool that attempts to give the best possible understanding of proteomics data on web!</font>");// <br/><br/>To start using the system connect to your Galaxy Server");

        welcomeTextContainerLayout.addComponent(subWelcomeText);
        welcomeTextContainerLayout.setExpandRatio(subWelcomeText, 0.05f);
        welcomeTextContainerLayout.setComponentAlignment(subWelcomeText, Alignment.TOP_LEFT);

        /**
         * *Presenter control buttons container layout*
         */
        presenteControlButtonsLayout = new VerticalLayout();
        presenteControlButtonsLayout.setHeight(180, Unit.PIXELS);
        presenteControlButtonsLayout.setWidth(100, Unit.PERCENTAGE);
        presenteControlButtonsLayout.setSpacing(true);
        presenterControlButtonsPanel.addComponent(presenteControlButtonsLayout);
        presenterControlButtonsPanel.setExpandRatio(presenteControlButtonsLayout, 0.6f);
        presenteControlButtonsLayout.setEnabled(false);
        presenteControlButtonsLayout.addStyleName("disableasenable");

        /**
         * Pop-up window layout to connect to Galaxy Server.
         */
        userConnectionPanel = new VerticalLayout();
        userConnectionPanel.setWidth(500, Unit.PIXELS);
        userConnectionPanel.setHeightUndefined();
        userConnectionPanel.setMargin(new MarginInfo(true, true, true, true));
        userConnectionPanel.setSpacing(true);

        connectingLabel = new Label("<h1 class='animation'>Connecting to galaxy, Please wait...</h1>");
        connectingLabel.setVisible(false);
        connectingLabel.setCaptionAsHtml(true);
        connectingLabel.setContentMode(ContentMode.HTML);
        connectingLabel.setHeight(25, Sizeable.Unit.PIXELS);
        connectingLabel.setWidth(200, Sizeable.Unit.PIXELS);
        connectingLabel.setStyleName(ValoTheme.LABEL_SMALL);
        connectingLabel.addStyleName(ValoTheme.LABEL_BOLD);
        connectingLabel.addStyleName(ValoTheme.LABEL_TINY);
        connectingLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);

        userConnectionPanel.addComponent(connectingLabel);
        userConnectionPanel.setComponentAlignment(connectingLabel, Alignment.MIDDLE_CENTER);

        connectinoWindow = new Window(null, userConnectionPanel) {
            @Override
            public void close() {
                this.setVisible(false);
            }

            @Override
            public void setVisible(boolean visible) {
                super.setVisible(visible);
                if (visible) {
                    this.center();
                }
            }

        };
        connectinoWindow.setModal(true);
        connectinoWindow.setDraggable(false);
        connectinoWindow.setClosable(true);

        connectinoWindow.setResizable(false);
        connectinoWindow.setStyleName("connectionwindow");
        connectinoWindow.center();
        connectinoWindow.setWindowMode(WindowMode.NORMAL);
        UI.getCurrent().addWindow(connectinoWindow);
        connectinoWindow.setVisible(false);

        presenterControlButtonsPanel.addLayoutClickListener((LayoutEvents.LayoutClickEvent event) -> {
            AbstractComponent comp = (AbstractComponent) event.getClickedComponent();
            if (presenteControlButtonsLayout.isEnabled() || comp == null || (comp.getData() != null && comp.getData().toString().equalsIgnoreCase("ignoreclick"))) {
                return;
            }

            if (galaxyLloginBtn.getData() == null) {
                viewId = comp.getData().toString();
                connectinoWindow.setVisible(true);
            }
        });
        galaxyLloginBtn.addLayoutClickListener((LayoutEvents.LayoutClickEvent event) -> {
            viewId = this.getViewId();
            if (galaxyLloginBtn.getData() == null) {
                connectinoWindow.removeStyleName("connectionwindow");
                connectinoWindow.setStyleName("windowcontainer");
                connectinoWindow.setVisible(true);
            } else {
                VaadinSession.getCurrent().getSession().invalidate();
                Page.getCurrent().reload();
            }
        });

        HorizontalLayout serviceButtonContainer = new HorizontalLayout();
        serviceButtonContainer.setWidth(100, Unit.PERCENTAGE);
        serviceButtonContainer.setHeight(40, Unit.PIXELS);
        serviceButtonContainer.setMargin(false);
        serviceButtonContainer.setSpacing(true);
        userConnectionPanel.addComponent(serviceButtonContainer);
        userConnectionPanel.setExpandRatio(serviceButtonContainer, 0.1f);
        userConnectionPanel.setComponentAlignment(serviceButtonContainer, Alignment.TOP_LEFT);
//
//        loadExampleUserAccount = new Button("Continue as guest", new ThemeResource("img/peptideshaker_example_dataset_1.png"));
//        loadExampleUserAccount.setSizeFull();
//        
//        loadExampleUserAccount.setDescription("Continue as guest user");
//        loadExampleUserAccount.setDisableOnClick(true);
//        loadExampleUserAccount.setStyleName(ValoTheme.BUTTON_LINK);
//        loadExampleUserAccount.addStyleName("smallest");
////         userConnectionPanel.addComponent(loadExampleUserAccount);
////         userConnectionPanel.setExpandRatio(loadExampleUserAccount, 0.1f);
////        userConnectionPanel.setComponentAlignment(loadExampleUserAccount, Alignment.TOP_LEFT);

        galaxyConnectionBtn = new Button("Galaxy", new ThemeResource("img/galaxyLogo.png"));
        galaxyConnectionBtn.setSizeFull();
        galaxyConnectionBtn.setEnabled(false);
        galaxyConnectionBtn.setCaptionAsHtml(true);
//        serviceButtonContainer.addComponent(galaxyConnectionBtn);
//        serviceButtonContainer.setComponentAlignment(galaxyConnectionBtn, Alignment.TOP_LEFT);
//        serviceButtonContainer.setExpandRatio(galaxyConnectionBtn, 0.35f);

//        Label galaxyConnectionText = new Label("<font style='width: 100%;height: 100%;font-size: 14px;font-weight: 600;text-align: justify;margin-top: 5px;'>Login to Galaxy Server using your API Key or as a guest</font>", ContentMode.HTML);
//        galaxyConnectionText.setStyleName(ValoTheme.LABEL_BOLD);
//        galaxyConnectionText.setSizeFull();
//        serviceButtonContainer.addComponent(galaxyConnectionText);
//        serviceButtonContainer.setComponentAlignment(galaxyConnectionText, Alignment.TOP_LEFT);
//        serviceButtonContainer.setExpandRatio(galaxyConnectionText, 0.65f);
        connectionBtnLabel = new ButtonWithLabel("<font style='width: 100%;height: 100%;font-size: 14px;font-weight: 600;text-align: justify;line-height: 70px;'>Login to Galaxy Server using your API Key</font>", 1);
        connectionBtnLabel.updateIconResource(new ThemeResource("img/galaxyLogo.png"));

        connectionBtnLabel.addStyleName("smaller");
        serviceButtonContainer.addComponent(connectionBtnLabel);

        nelsGalaxyConnectionBtn = new Link("NeLS-Galaxy", new ExternalResource("http://localhost:8084/NelsGalaxyRedirectForm/"));
        nelsGalaxyConnectionBtn.setStyleName("nelslogo");
        /**
         * @todo: to be remove in NeLS enable Online PeptideShaker
         *
         */
        nelsGalaxyConnectionBtn.setVisible(false);
//        final VerticalLayout spacerLayout = new VerticalLayout();
        galaxyLoginLayout = new VerticalLayout();
        galaxyLoginLayout.setSizeFull();
        galaxyLoginLayout.setSpacing(true);
        galaxyLoginLayout.setVisible(true);
        galaxyLoginLayout.setMargin(false);
        userConnectionPanel.addComponent(galaxyLoginLayout);
        userConnectionPanel.setExpandRatio(galaxyLoginLayout, 0.1f);
        userConnectionPanel.setComponentAlignment(galaxyLoginLayout, Alignment.TOP_LEFT);

        userAPIFeald = new TextField("User API");
        userAPIFeald.addFocusListener((FieldEvents.FocusEvent event) -> {
            if (userAPIFeald.getValue().equals(apiErrorMessage)) {
                userAPIFeald.clear();
                userAPIFeald.removeStyleName("redfont");
            }
        });
        userAPIFeald.setSizeFull();
        userAPIFeald.setStyleName(ValoTheme.TEXTFIELD_TINY);
        userAPIFeald.setRequired(true);
        userAPIFeald.setRequiredError("API key is required");
        userAPIFeald.setInputPrompt("Enter Galaxy API Key");
        galaxyLoginLayout.addComponent(userAPIFeald);

        HorizontalLayout galaxyServiceBtns = new HorizontalLayout();
        galaxyServiceBtns.setSizeFull();
        galaxyServiceBtns.setSpacing(true);
        galaxyLoginLayout.addComponent(galaxyServiceBtns);
        Link regLink = new Link("Register", new ExternalResource("http://129.177.231.63:8081/galaxy/user/create"));
        regLink.setStyleName("newlink");
        regLink.setTargetName("_blank");
        galaxyServiceBtns.addComponent(regLink);
        galaxyServiceBtns.setExpandRatio(regLink, 0.1f);

        Link userAPI = new Link("Get User API", new ExternalResource("http://129.177.231.63:8081/galaxy/user/api_key"));
        userAPI.setStyleName("newlink");
        userAPI.setTargetName("_blank");
        galaxyServiceBtns.addComponent(userAPI);
        galaxyServiceBtns.setExpandRatio(userAPI, 0.4f);
//
//        galaxyServiceBtns.addComponent(loadExampleUserAccount);
//        galaxyServiceBtns.setExpandRatio(loadExampleUserAccount, 0.1f);
//        galaxyServiceBtns.setComponentAlignment(loadExampleUserAccount, Alignment.TOP_RIGHT);

        Button loginButton = new Button("Login");
        loginButton.setStyleName(ValoTheme.BUTTON_TINY);
        galaxyServiceBtns.addComponent(loginButton);
        galaxyServiceBtns.setExpandRatio(loginButton, 0.4f);
        galaxyServiceBtns.setComponentAlignment(loginButton, Alignment.TOP_RIGHT);

//        galaxyConnectionBtn.addClickListener((Button.ClickEvent event) -> {
//            if (galaxyConnectionBtn.getCaption().equalsIgnoreCase("<b>Disconnect</b>")) {
//                VaadinSession.getCurrent().getSession().invalidate();
//                Page.getCurrent().reload();
//            } else if (galaxyLoginLayout.isVisible()) {
//                galaxyLoginLayout.setVisible(false);
//            } else {
//                galaxyLoginLayout.setVisible(true);
//            }
//
//        });
        loginButton.addClickListener((Button.ClickEvent event) -> {
            userAPIFeald.commit();
            if (userAPIFeald.isValid() && !userAPIFeald.getValue().equalsIgnoreCase(testUserLogin)) {
                connectingLabel.setVisible(true);
                connectinoWindow.setClosable(false);
                connectionBtnLabel.setVisible(false);
                galaxyLoginLayout.setVisible(false);
                Runnable task = () -> {
                    connectinoWindow.removeStyleName("windowcontainer");
                    connectinoWindow.setStyleName("connectionwindow");

                    List<String> userOverviewData = connectToGalaxy(userAPIFeald.getValue(), viewId);
                    connectedToGalaxy(userOverviewData);
                };
                if (executorService.isShutdown()) {
                    executorService = Executors.newFixedThreadPool(2);
                }

                executorService.submit(task);
                executorService.shutdown();

            }
        });
//        loadExampleUserAccount.addClickListener((Button.ClickEvent event) -> {
//            galaxyLoginLayout.setVisible(false);
//            connectinoWindow.setClosable(false);
//            connectingLabel.setVisible(true);
//            connectionBtnLabel.setVisible(false);
//            Runnable task = () -> {
//                List<Object> userOverviewData = connectToGalaxy(testUserLogin, viewId);
//                connectedToGalaxy(userOverviewData);
//            };
//            if (executorService.isShutdown()) {
//                executorService = Executors.newFixedThreadPool(2);
//            }
//            executorService.submit(task);
//            executorService.shutdown();
//
//        });

//
        viewControlButton = new SmallSideBtn(VaadinIcons.HOME_O);
        viewControlButton.addStyleName("homepagepresenterbtn");
        viewControlButton.setData(WelcomePagePresenter.this.getViewId());

        this.viewControlButton.setDescription("Home page");

        VerticalLayout mainBottomPanel = new VerticalLayout();
        mainBottomPanel.setStyleName("bluelayout");
        mainBottomPanel.setHeight(65, Unit.PIXELS);
        mainBottomPanel.setWidth(100, Unit.PERCENTAGE);
        container.addComponent(mainBottomPanel, "left:0px;bottom:10px");

        HorizontalLayout sponserContainer = new HorizontalLayout();
        sponserContainer.setHeight(100, Unit.PERCENTAGE);
        sponserContainer.setSpacing(true);
        sponserContainer.setWidthUndefined();
        mainBottomPanel.addComponent(sponserContainer);
        mainBottomPanel.setComponentAlignment(sponserContainer, Alignment.TOP_CENTER);

        Label developmentText = new Label("<font>The web version of PeptideShaker is being developed by <a href='http://www.cbu.uib.no/barsnes/' target='_blank'>Barsnes Group</a> at the Computational Biology Unit (CBU) at the University of Bergen, Norway, in close collaboration with the Proteomics Unit at the University of Bergen (PROBE), Bergen, Norway.</font>", ContentMode.HTML);
        developmentText.setStyleName("refrencetext");
        sponserContainer.addComponent(developmentText);

        Link probeLink = new Link("<img src='VAADIN/themes/webpeptideshakertheme/img/probe-updated.png' alt style='height: 65px;margin-top: -20px;cursor:pointer !important;'>", new ExternalResource("https://www.uib.no/rg/probe"));
        probeLink.setCaptionAsHtml(true);
        probeLink.setHeight(100, Unit.PERCENTAGE);
        probeLink.setWidth(200, Unit.PIXELS);
        probeLink.setTargetName("_blank");
        sponserContainer.addComponent(probeLink);
        Link uibLink = new Link("<img src='VAADIN/themes/webpeptideshakertheme/img/uib-logo.svg' alt style='height: 85px;margin-top: -30px;margin-left: -20px;cursor:pointer !important;'>", new ExternalResource("https://www.uib.no/"));
        uibLink.setCaptionAsHtml(true);
        uibLink.setHeight(100, Unit.PERCENTAGE);
        uibLink.setWidth(65, Unit.PIXELS);
        uibLink.setTargetName("_blank");
        sponserContainer.addComponent(uibLink);
        Link cbuLink = new Link("<img src='VAADIN/themes/webpeptideshakertheme/img/cbu_logo_lightBlue_transparent.png' alt style='height: 55px;margin-top: -10px;cursor:pointer !important;'>", new ExternalResource("http://www.cbu.uib.no/"));
        cbuLink.setCaptionAsHtml(true);
        cbuLink.setHeight(100, Unit.PERCENTAGE);
        cbuLink.setWidth(112, Unit.PIXELS);
        cbuLink.setTargetName("_blank");
        sponserContainer.addComponent(cbuLink);

//        container.setExpandRatio(mainBottomPanel, 20);
        VerticalLayout windowContent = new VerticalLayout();
        windowContent.setWidth(500, Unit.PIXELS);
        this.busyConnectinWindow = new Window(null, windowContent);
        this.busyConnectinWindow.setSizeFull();
        this.busyConnectinWindow.setStyleName("busyconnectingwindow");
        this.busyConnectinWindow.setModal(false);
        this.busyConnectinWindow.setDraggable(false);
        this.busyConnectinWindow.setClosable(false);
        this.busyConnectinWindow.setResizable(false);
        busyConnectinWindow.center();
        this.busyConnectinWindow.setWindowMode(WindowMode.NORMAL);
        busyConnectinWindow.addStyleName("hidewindow");
        this.loginAsGuest();

//        presenterControlButtonsPanel.addComponent(new IconDesign());
    }

    private void loginAsGuest() {
        connectionBtnLabel.setVisible(false);
        connectinoWindow.setVisible(true);
        galaxyLoginLayout.setVisible(false);
        connectinoWindow.setClosable(false);
        connectingLabel.setCaption("<b style=\"color:#cd6e1d !important\">Guest User <i>(public data)</i></b>");
        connectingLabel.setVisible(true);
        Runnable task = () -> {
            connectinoWindow.removeStyleName("windowcontainer");
            connectinoWindow.setStyleName("connectionwindow");
            List<String> userOverviewData = connectToGalaxy(testUserLogin, viewId);
            connectedToGalaxy(userOverviewData);
        };
        if (executorService.isShutdown()) {
            executorService = Executors.newFixedThreadPool(2);
        }
        executorService.submit(task);
        executorService.shutdown();

    }

    /**
     * update the layout based on connection to galaxy.
     *
     * @param connected connected to galaxy server
     */
    private void connectedToGalaxy(List<String> userOverviewData) {
        connectinoWindow.setClosable(true);
        connectionBtnLabel.setVisible(true);
        galaxyLoginLayout.setEnabled(true);
        galaxyLoginLayout.setVisible(true);
        galaxyLloginBtn.setData(null);
        connectingLabel.setCaption(null);

        if (userOverviewData != null && userOverviewData.get(0).contains("Guest User")) {
            connectinoWindow.setVisible(false);
            presenteControlButtonsLayout.setEnabled(true);
        } else if (userOverviewData != null && !userOverviewData.get(0).contains("Guest User")) {
            connectinoWindow.setVisible(false);
            galaxyLloginBtn.updateText("Galaxy Logout");
            galaxyLloginBtn.setData("connected");
            galaxyLoginLayout.setEnabled(false);
            presenteControlButtonsLayout.setEnabled(true);

        } else {
            userAPIFeald.setValue(apiErrorMessage);
            userAPIFeald.addStyleName("redfont");
        }
        
        updateUserOverviewPanel(userOverviewData);        
        connectingLabel.setVisible(false);
        

    }

    public void updateUserOverviewPanel(List<String> userOverviewData) {
        if (userOverviewData != null && !userOverviewData.isEmpty()) {
            Label l1 = initLeftSideInfoLabel(VaadinIcons.USER.getHtml() + " <b style='color:#cd6e1d !important'>" + userOverviewData.get(0) + "</b>", "");
            userOverviewLayout.replaceComponent(userOverviewLayout.getComponent(1), l1);
            for (int i = 2; i < userOverviewLayout.getComponentCount(); i++) {
                Label l = (Label) userOverviewLayout.getComponent(i);
                updateLeftSideInfoLabel(l, userOverviewData.get(i - 1));
            }
        }
    }

    /**
     * Initialise the header layout.
     */
    private HorizontalLayout initializeHeaderPanel() {

        HorizontalLayout headerLayoutContainer = new HorizontalLayout();
        headerLayoutContainer.setSpacing(true);
        headerLayoutContainer.addStyleName("logocontainer");
        Image peptideShakerLogoIcon = new Image();
        peptideShakerLogoIcon.setSource(new ThemeResource("img/peptideshakericon.png"));
        peptideShakerLogoIcon.setHeight(100, Unit.PIXELS);
        headerLayoutContainer.addComponent(peptideShakerLogoIcon);
        headerLayoutContainer.setComponentAlignment(peptideShakerLogoIcon, Alignment.MIDDLE_LEFT);

        Link headerLogoLabel = new Link("PeptideShaker", new ExternalResource(""));//PeptideShaker <font>(Online Version)</font>
        headerLayoutContainer.addComponent(headerLogoLabel);
        headerLogoLabel.setCaptionAsHtml(true);
        headerLayoutContainer.setComponentAlignment(headerLogoLabel, Alignment.MIDDLE_LEFT);
        headerLogoLabel.setStyleName("headerlogo");
        headerLayoutContainer.addLayoutClickListener((LayoutEvents.LayoutClickEvent event) -> {
            Page.getCurrent().open("", "", false);
        });
        return headerLayoutContainer;
    }

    @Override
    public String getViewId() {
        return WelcomePagePresenter.class.getName();
    }

    @Override
    public void minimizeView() {
        viewControlButton.setSelected(false);
        this.addStyleName("hidepanel");
    }

    @Override
    public void maximizeView() {
        viewControlButton.setSelected(true);
        this.removeStyleName("hidepanel");
    }

    @Override
    public SmallSideBtn getSmallPresenterControlButton() {
        return viewControlButton;
    }

    @Override
    public VerticalLayout getMainView() {
        return this;
    }

    @Override
    public VerticalLayout getSubViewButtonsActionContainerLayout() {
        return new VerticalLayout();
    }

    /**
     * Start the Online PeptideShaker - Galaxy server connection.
     *
     * @param presenterId button used to login
     * @param userAPI user API key that is required to connect to galaxy
     * @return list of overview data for the user / null indicate failed to
     * connect to Galaxy Server
     */
    public abstract List<String> connectToGalaxy(String userAPI, String presenterId);

    private Label initLeftSideInfoLabel(String title, String value) {
        Label label = new Label("<font style='width:120px;font-size: 14px;'>" + title + "</font><font style='width:60px; text-align: right !important;font-size: 14px; float: right !important;  color:#cd6e1d'>  " + value + " </font>");
        label.setContentMode(ContentMode.HTML);
        label.setHeight(30, Sizeable.Unit.PIXELS);
        label.setWidth(180, Sizeable.Unit.PIXELS);
        label.setStyleName(ValoTheme.LABEL_SMALL);
        return label;

    }

    private void updateLeftSideInfoLabel(Label label, String value) {
        String org = label.getValue().split("</font>")[0];
        label.setValue(org + "</font><font style='width:60px; text-align: right !important;font-size: 14px;float: right !important;  color:#cd6e1d; margin-top:2px'>  " + value + " </font>");

    }

    @Override
    public ButtonWithLabel getLargePresenterControlButton() {
        return null;
    }

    public void setPresenterControlButtonContainer(GridLayout presenterBtnsContainer) {
        presenterBtnsContainer.addComponent(galaxyLloginBtn, 1, 1);
        presenteControlButtonsLayout.addComponent(presenterBtnsContainer);
    }

}
