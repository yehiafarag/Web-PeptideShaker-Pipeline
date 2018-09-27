package com.uib.web.peptideshaker.presenter;

//import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
//import com.uib.onlinepeptideshaker.managers.RegistrableView;
//import com.uib.onlinepeptideshaker.presenter.view.SmallSideBtn;
import com.uib.web.peptideshaker.presenter.core.BigSideBtn;
import com.uib.web.peptideshaker.presenter.core.ViewableFrame;
import com.uib.web.peptideshaker.presenter.core.SmallSideBtn;
import com.vaadin.event.FieldEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
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
    private final VerticalLayout mainHeaderPanel;
    /**
     * The header layout container layout.
     */
    private final HorizontalLayout headerPanelContentLayout;

    /**
     * The body layout panel.
     */
    private final HorizontalLayout bodyPanel;
    /**
     * The connection button to NeLS login page
     */
    private final Link nelsGalaxyConnectionBtn;
    /**
     * Button to direct connection using testing account
     *
     */
    private final Button loadExampleUserAccount;
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
    private final Label connectionStatuesLabel;
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
    private final BigSideBtn viewControlButton;
    /**
     * Busy connecting window
     */
    private final Window busyConnectinWindow;
     /**
     * Executor service to execute connection task to galaxy server.
     */
    private  ExecutorService executorService = Executors.newFixedThreadPool(2);;

    /**
     * Constructor to initialise the layout.
     */
    public WelcomePagePresenter() {
        WelcomePagePresenter.this.setSizeFull();
        WelcomePagePresenter.this.addStyleName("welcomepagestyle");

        VerticalLayout windowContent = new VerticalLayout();
        windowContent.setSizeFull();

       

        this.busyConnectinWindow = new Window(null, windowContent);
        this.busyConnectinWindow.setSizeFull();
        this.busyConnectinWindow.setStyleName("busyconnectingwindow");
        this.busyConnectinWindow.setModal(false);
        this.busyConnectinWindow.setDraggable(false);
        this.busyConnectinWindow.setClosable(false);
        this.busyConnectinWindow.setResizable(false);
        busyConnectinWindow.center();
        this.busyConnectinWindow.setWindowMode(WindowMode.NORMAL);

        mainHeaderPanel = new VerticalLayout();
        mainHeaderPanel.setHeight(50, Unit.PIXELS);
        WelcomePagePresenter.this.addComponent(mainHeaderPanel);
        WelcomePagePresenter.this.setComponentAlignment(mainHeaderPanel, Alignment.MIDDLE_LEFT);
        WelcomePagePresenter.this.setExpandRatio(mainHeaderPanel, 10);
        headerPanelContentLayout = initializeHeaderPanel();
        mainHeaderPanel.addComponent(headerPanelContentLayout);
        mainHeaderPanel.setMargin(new MarginInfo(false, false, false, false));

        bodyPanel = new HorizontalLayout();
        bodyPanel.setSizeFull();
        bodyPanel.setMargin(new MarginInfo(true, false, false, false));
        WelcomePagePresenter.this.addComponent(bodyPanel);
        WelcomePagePresenter.this.setComponentAlignment(bodyPanel, Alignment.TOP_CENTER);
        WelcomePagePresenter.this.setExpandRatio(bodyPanel, 90);

        VerticalLayout bodyContent = new VerticalLayout();
        bodyContent.setSizeFull();
        bodyContent.addStyleName("mainbodystyle");
        bodyContent.addStyleName("connectionpanelstyle");
        bodyContent.setSpacing(true);
        bodyContent.setWidth(405, Unit.PIXELS);
        bodyContent.setHeight(400, Unit.PIXELS);
        bodyPanel.addComponent(bodyContent);
        bodyPanel.setComponentAlignment(bodyContent, Alignment.TOP_CENTER);

        Label welcomeText = new Label();
        welcomeText.setSizeFull();
        welcomeText.setContentMode(ContentMode.HTML);
        welcomeText.setStyleName(ValoTheme.LABEL_NO_MARGIN);
        welcomeText.setValue("<font style='font-weight: bold; font-size:23px'>Welcome to PeptideShaker <font size='2'><i>(online version)</i></font></font>");// <br/><br/>To start using the system connect to your Galaxy Server");

        bodyContent.addComponent(welcomeText);
        bodyContent.setExpandRatio(welcomeText, 0.05f);
        bodyContent.setComponentAlignment(welcomeText, Alignment.TOP_LEFT);

        HorizontalLayout serviceButtonContainer = new HorizontalLayout();
        serviceButtonContainer.setWidth(100, Unit.PERCENTAGE);
        serviceButtonContainer.setHeight(100, Unit.PERCENTAGE);
        serviceButtonContainer.setSpacing(true);
        bodyContent.addComponent(serviceButtonContainer);
        bodyContent.setExpandRatio(serviceButtonContainer, 0.1f);
        bodyContent.setComponentAlignment(serviceButtonContainer, Alignment.TOP_LEFT);

        loadExampleUserAccount = new Button("Example", new ThemeResource("img/peptideshaker_example_dataset_1.png"));
        loadExampleUserAccount.setSizeFull();
        serviceButtonContainer.addComponent(loadExampleUserAccount);
        serviceButtonContainer.setComponentAlignment(loadExampleUserAccount, Alignment.TOP_LEFT);
        loadExampleUserAccount.setDisableOnClick(true);

        galaxyConnectionBtn = new Button("Galaxy", new ThemeResource("img/galaxyLogo_2.png"));
        galaxyConnectionBtn.setSizeFull();
        galaxyConnectionBtn.setCaptionAsHtml(true);

        serviceButtonContainer.addComponent(galaxyConnectionBtn);
        serviceButtonContainer.setComponentAlignment(galaxyConnectionBtn, Alignment.TOP_LEFT);

        nelsGalaxyConnectionBtn = new Link("NeLS-Galaxy", new ExternalResource("http://localhost:8084/NelsGalaxyRedirectForm/"));
        nelsGalaxyConnectionBtn.setStyleName("nelslogo");
        /**
         * @todo: to be remove in NeLS enable Online PeptideShaker
         *
         */
        nelsGalaxyConnectionBtn.setVisible(false);
        final VerticalLayout spacerLayout = new VerticalLayout();
        galaxyLoginLayout = new VerticalLayout() {
            @Override
            public void setVisible(boolean visible) {
                super.setVisible(visible);
                spacerLayout.setVisible(!visible);
            }

        };
        galaxyLoginLayout.setSizeFull();
        galaxyLoginLayout.setSpacing(true);
        galaxyLoginLayout.setVisible(false);
        bodyContent.addComponent(galaxyLoginLayout);
        bodyContent.setExpandRatio(galaxyLoginLayout, 0.1f);
        bodyContent.setComponentAlignment(galaxyLoginLayout, Alignment.TOP_LEFT);

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
        galaxyServiceBtns.setExpandRatio(regLink, 0.15f);

        Link userAPI = new Link("Get User API", new ExternalResource("http://129.177.231.63:8081/galaxy/user/api_key"));
        userAPI.setStyleName("newlink");
        userAPI.setTargetName("_blank");
        galaxyServiceBtns.addComponent(userAPI);
        galaxyServiceBtns.setExpandRatio(userAPI, 0.25f);
        Button loginButton = new Button("Login");
        loginButton.setStyleName(ValoTheme.BUTTON_TINY);
        galaxyServiceBtns.addComponent(loginButton);
        galaxyServiceBtns.setExpandRatio(loginButton, 0.6f);
        galaxyServiceBtns.setComponentAlignment(loginButton, Alignment.TOP_RIGHT);

        VerticalLayout connectionCommentLayout = new VerticalLayout();
        connectionCommentLayout.setSizeFull();
        bodyContent.addComponent(connectionCommentLayout);
        bodyContent.setExpandRatio(connectionCommentLayout, 0.1f);
        bodyContent.setComponentAlignment(connectionCommentLayout, Alignment.TOP_LEFT);

        connectionStatuesLabel = new Label("Galaxy is<font color='red'>  not connected </font><font size='3' color='red'> " + FontAwesome.FROWN_O.getHtml() + "</font>");
        connectionStatuesLabel.setContentMode(ContentMode.HTML);
        connectionStatuesLabel.setHeight(25, Sizeable.Unit.PIXELS);
        connectionStatuesLabel.setWidth(160, Sizeable.Unit.PIXELS);
        connectionStatuesLabel.setStyleName(ValoTheme.LABEL_SMALL);
        connectionStatuesLabel.addStyleName(ValoTheme.LABEL_BOLD);
        connectionStatuesLabel.addStyleName(ValoTheme.LABEL_TINY);
        connectionStatuesLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        connectionCommentLayout.addComponent(connectionStatuesLabel);
        connectionCommentLayout.setComponentAlignment(connectionStatuesLabel, Alignment.TOP_CENTER);

        spacerLayout.setSizeFull();
        spacerLayout.setSpacing(true);
        spacerLayout.setVisible(true);
        bodyContent.addComponent(spacerLayout);
        bodyContent.setExpandRatio(spacerLayout, 0.1f);
        bodyContent.setComponentAlignment(spacerLayout, Alignment.TOP_LEFT);

        galaxyConnectionBtn.addClickListener((Button.ClickEvent event) -> {
            if (galaxyConnectionBtn.getCaption().equalsIgnoreCase("<b>Disconnect</b>")) {
                VaadinSession.getCurrent().getSession().invalidate();
                Page.getCurrent().reload();
            } else if (galaxyLoginLayout.isVisible()) {
                galaxyLoginLayout.setVisible(false);
            } else {
                galaxyLoginLayout.setVisible(true);
            }

        });
        loginButton.addClickListener((Button.ClickEvent event) -> {
            userAPIFeald.commit();
            connectionStatuesLabel.setValue("<h1 class='animation'>Connecting to galaxy....</h1>");
            if (userAPIFeald.isValid() && !userAPIFeald.getValue().equalsIgnoreCase(testUserLogin)) {
                galaxyLoginLayout.setVisible(false);
                Runnable task = () -> {
                    boolean connected = connectToGalaxy(userAPIFeald.getValue());
                    connectedToGalaxy(connected);
                };
                if (executorService.isShutdown()) {
                    executorService = Executors.newFixedThreadPool(2);
                }

                executorService.submit(task);
                executorService.shutdown();

            }
        });
        loadExampleUserAccount.addClickListener((Button.ClickEvent event) -> {
            galaxyLoginLayout.setVisible(false);
            Runnable task = () -> {
                boolean connected = connectToGalaxy(testUserLogin);
                connectedToGalaxy(connected);
            };
            connectionStatuesLabel.setValue("<h1 class='animation'>Connecting to galaxy....</h1>");
            if (executorService.isShutdown()) {
                    executorService = Executors.newFixedThreadPool(2);
                }
            executorService.submit(task);
            executorService.shutdown();

        });

//
        viewControlButton = new BigSideBtn("Home Page",1);
        viewControlButton.updateIcon(VaadinIcons.HOME_O.getHtml());
        viewControlButton.addStyleName("homepagepresenterbtn");
        viewControlButton.setData(WelcomePagePresenter.this.getViewId());
        busyConnectinWindow.addStyleName("hidewindow");        
        this.viewControlButton.setDescription("View home page");

    }

    /**
     * update the layout based on connection to galaxy.
     *
     * @param connected connected to galaxy server
     */
    private void connectedToGalaxy(boolean connected) {
        if (connected) {
            connectionStatuesLabel.setValue("Galaxy is <font color='green'>connected </font><font size='3' color='green'> " + FontAwesome.SMILE_O.getHtml() + "</font>");
            galaxyConnectionBtn.setCaption("<b>Disconnect</b>");
            galaxyLoginLayout.setEnabled(false);
            loadExampleUserAccount.setEnabled(false);

        } else {
            userAPIFeald.setValue(apiErrorMessage);
            userAPIFeald.addStyleName("redfont");
            connectionStatuesLabel.setValue("Galaxy is<font color='red'>  not connected </font><font size='3' color='red'> " + FontAwesome.FROWN_O.getHtml() + "</font>");
            galaxyLoginLayout.setEnabled(true);
            loadExampleUserAccount.setEnabled(true);
            galaxyLoginLayout.setVisible(true);
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

        Link headerLogoLabel = new Link("PeptideShaker <font>(Online Version)</font>", new ExternalResource(""));
        headerLayoutContainer.addComponent(headerLogoLabel);
        headerLogoLabel.setCaptionAsHtml(true);
        headerLayoutContainer.setComponentAlignment(headerLogoLabel, Alignment.MIDDLE_LEFT);
        headerLogoLabel.setStyleName("headerlogo");
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
    public BigSideBtn getPresenterControlButton() {
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
     * @param userAPI user API key that is required to connect to galaxy
     * @return boolean successful connection to galaxy
     */
    public abstract boolean connectToGalaxy(String userAPI);

}
