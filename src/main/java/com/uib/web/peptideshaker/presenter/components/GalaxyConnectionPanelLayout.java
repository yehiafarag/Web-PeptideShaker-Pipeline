package com.uib.web.peptideshaker.presenter.components;

import com.github.jmchilton.blend4j.galaxy.DefaultWebResourceFactoryImpl;
import com.github.jmchilton.blend4j.galaxy.GalaxyAuthWebResourceFactoryImpl;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstanceFactory;
import com.vaadin.data.Property;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.Cookie;

/**
 * This class represents the galaxy server connection panel that contains
 * user-input data and link to galaxy
 *
 * @author Yehia Farag
 */
public abstract class GalaxyConnectionPanelLayout extends VerticalLayout implements Button.ClickListener {

    /**
     * Container for galaxy connection input fields.
     */
    private HorizontalLayout galaxyLinkContainer;
    /**
     * Tab-sheet for galaxy connection user input fields.
     */
    private TabSheet inputTabSheet;
    /**
     * Galaxy server is connected.
     */
    private boolean galaxyConnected;
    /**
     * Galaxy connection label.
     */
    private Label connectionLabel;
    /**
     * Galaxy server user email.
     */
    private TextField userEmail;
    /**
     * Galaxy server user password.
     */
    private PasswordField password;
    /**
     * Galaxy server web address.
     */
    private ComboBox galaxyLink;
    /**
     * Galaxy server user API key.
     */
    private TextField APIKey;

    /**
     * Email format validation regex.
     */
    private final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    /**
     * Main galaxy server instance.
     */
    private GalaxyInstance galaxyInstance;
    /**
     * Connect to galaxy button
     */
    private Button connectBtn;
    /**
     * Request cookies to attach to every request for galaxy used mainly for
     * securing sessions.
     */
    private String cookiesRequestProperty;

    /**
     * Constructor to initialize the main variable.
     */
    public GalaxyConnectionPanelLayout() {
        GalaxyConnectionPanelLayout.this.setWidth(500, Unit.PIXELS);
        GalaxyConnectionPanelLayout.this.setHeight(300, Unit.PIXELS);
        GalaxyConnectionPanelLayout.this.addComponent(this.initializeUserInputPanel());

    }

    /**
     * Initialize galaxy input data layout.
     */
    private VerticalLayout initializeUserInputPanel() {
        VerticalLayout userInputPanelLayout = new VerticalLayout();
        userInputPanelLayout.setSizeFull();
        userInputPanelLayout.setMargin(new MarginInfo(true, true, true, true));
        userInputPanelLayout.setSpacing(true);

        galaxyLinkContainer = new HorizontalLayout();
        galaxyLinkContainer.setSpacing(true);
        galaxyLinkContainer.setSizeFull();

        Image galaxyIcon = new Image();
        galaxyIcon.setSource(new ThemeResource("img/galaxyLogo.png"));
        galaxyIcon.setWidth(100, Unit.PERCENTAGE);
        galaxyIcon.setHeight(100, Unit.PERCENTAGE);
        galaxyIcon.setStyleName("galaxyicon");
        galaxyLinkContainer.addComponent(galaxyIcon);
        galaxyLinkContainer.setExpandRatio(galaxyIcon, 5.9f);
        galaxyLinkContainer.setComponentAlignment(galaxyIcon, Alignment.MIDDLE_CENTER);

        Label galaxyLinkCaption = new Label("Link to galaxy server:");
        galaxyLinkCaption.setStyleName(ValoTheme.LABEL_BOLD);
        galaxyLinkCaption.addStyleName(ValoTheme.LABEL_SMALL);
        galaxyLinkCaption.setWidth(100, Unit.PERCENTAGE);
        galaxyLinkCaption.setHeight(100, Unit.PERCENTAGE);
        galaxyLinkCaption.addStyleName("hidetextoverflow");
//        galaxyLinkCaption.addStyleName("v-textfield-textfileldborder");
        galaxyLinkContainer.addComponent(galaxyLinkCaption);
        galaxyLinkContainer.setExpandRatio(galaxyLinkCaption, 36.5f);
        galaxyLinkContainer.setComponentAlignment(galaxyLinkCaption, Alignment.MIDDLE_CENTER);

        galaxyLink = new ComboBox();
        galaxyLink.setNullSelectionAllowed(false);
        galaxyLink.setTextInputAllowed(true);
        galaxyLink.setNewItemsAllowed(true);
        galaxyLink.addItem("http://129.177.123.195:8080/");
        galaxyLink.setItemCaption("http://129.177.123.195:8080/", "My Galaxy installation in Bergen");
        galaxyLink.addItem("https://usegalaxy.org/");
        galaxyLink.setItemCaption("https://usegalaxy.org/", "Use Galaxy Server");
        galaxyLink.addItem("https://test-fe.cbu.uib.no/galaxy");
        galaxyLink.setItemCaption("https://test-fe.cbu.uib.no/galaxy", "NeLS testing Galaxy installation in Bergen");

        galaxyLink.addItem("https://galaxy-uio.bioinfo.no/main/");
        galaxyLink.setItemCaption("https://galaxy-uio.bioinfo.no/main/", "Official UiB Galaxy Server");

        galaxyLink.addItem("https://usegalaxyp.org/");
        galaxyLink.setItemCaption("https://usegalaxyp.org/", "GalaxyP");

        galaxyLink.setNewItemHandler((final String newItemCaption) -> {

            // Adds new option
            if (galaxyLink.addItem(newItemCaption) != null) {
                galaxyLink.setValue(newItemCaption);
            }

        });
//        galaxyLink.setHeight(100, Unit.PERCENTAGE);
        galaxyLink.setWidth(100, Unit.PERCENTAGE);
        galaxyLink.setHeight(100, Unit.PERCENTAGE);
        galaxyLink.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        galaxyLink.addStyleName(ValoTheme.TEXTFIELD_TINY);
        galaxyLinkContainer.addComponent(galaxyLink);
        galaxyLinkContainer.setExpandRatio(galaxyLink, 57.6f);
        galaxyLinkContainer.setComponentAlignment(galaxyLink, Alignment.MIDDLE_LEFT);

        userInputPanelLayout.addComponent(galaxyLinkContainer);
        userInputPanelLayout.setComponentAlignment(galaxyLinkContainer, Alignment.BOTTOM_CENTER);
        userInputPanelLayout.setExpandRatio(galaxyLinkContainer, 12);

        galaxyLink.setValue("https://test-fe.cbu.uib.no/galaxy");//"http://129.177.123.195:8080/");//https://usegalaxyp.org

        inputTabSheet = new TabSheet();
        inputTabSheet.setWidth(100, Unit.PERCENTAGE);
        inputTabSheet.setHeight(90, Unit.PERCENTAGE);
        userInputPanelLayout.addComponent(inputTabSheet);
        userInputPanelLayout.setExpandRatio(inputTabSheet, 73);
        userInputPanelLayout.setComponentAlignment(inputTabSheet, Alignment.BOTTOM_LEFT);

        HorizontalLayout userInputPanel = new HorizontalLayout();
        userInputPanel.setWidth(100, Unit.PERCENTAGE);
        userInputPanel.setHeight(100, Unit.PERCENTAGE);
        userInputPanel.setData("Email & Password");
        inputTabSheet.addTab(userInputPanel, "Email & Password");

        userEmail = new TextField();
        userEmail.setImmediate(true);

        userEmail.setRequiredError("Not vaild e-mail address");
        userEmail.setStyleName(ValoTheme.TEXTFIELD_SMALL);
        userEmail.addStyleName(ValoTheme.TEXTFIELD_TINY);
        userEmail.setCaption("E-mail");
        userEmail.setInputPrompt("Galaxy e-mail");
        userInputPanel.addComponent(userEmail);
        userEmail.setWidth(90, Unit.PERCENTAGE);
        userEmail.setHeight(50, Unit.PERCENTAGE);
        userInputPanel.setComponentAlignment(userEmail, Alignment.TOP_CENTER);

        password = new PasswordField();
        password.setImmediate(true);

        password.setStyleName(ValoTheme.TEXTFIELD_SMALL);
        password.addStyleName(ValoTheme.TEXTFIELD_TINY);
        password.addStyleName("nomargin");
        password.setCaption("Password");
        password.setRequiredError("Password can not be empty");
        password.setWidth(90, Unit.PERCENTAGE);
        password.setHeight(50, Unit.PERCENTAGE);
        userInputPanel.addComponent(password);
        userInputPanel.setComponentAlignment(password, Alignment.TOP_CENTER);

        HorizontalLayout userAPIKeyPanel = new HorizontalLayout();
        userAPIKeyPanel.setData("API Key");
        userAPIKeyPanel.setWidth(100, Unit.PERCENTAGE);
        userAPIKeyPanel.setHeight(100, Unit.PERCENTAGE);
        inputTabSheet.addTab(userAPIKeyPanel, "API Key");
        inputTabSheet.setSelectedTab(userAPIKeyPanel);

        APIKey = new TextField("");//71821f0c14cf63a2609f59d821bc1df3       //  6abed6a0b5021096631350a0b89c5155----61062cd3acb2433c1e1ed66d6560357f
        APIKey.setImmediate(true);
        APIKey.setRequiredError("Not vaild API Key");
        APIKey.setStyleName(ValoTheme.TEXTFIELD_SMALL);
        APIKey.addStyleName(ValoTheme.TEXTFIELD_TINY);
        APIKey.addStyleName("nomargin");
        APIKey.setInputPrompt("Galaxy API Key");
        APIKey.setValue("9228c9cd3eccff77b6fc2e8d6f3c7d48");//"abba9538dfd38d10c71aab67bbd30cfe"  admin 6abed6a0b5021096631350a0b89c5155   --71821f0c14cf63a2609f59d821bc1df3--61062cd3acb2433c1e1ed66d6560357f
        userAPIKeyPanel.addComponent(APIKey);
        APIKey.setWidth(100, Unit.PERCENTAGE);
        APIKey.setHeight(50, Unit.PERCENTAGE);
        userAPIKeyPanel.setComponentAlignment(APIKey, Alignment.TOP_CENTER);

        HorizontalLayout connectionPanel = new HorizontalLayout();
        connectionPanel.setWidth(100, Unit.PERCENTAGE);
        connectionPanel.setHeight(100, Unit.PERCENTAGE);
        userInputPanelLayout.addComponent(connectionPanel);
        userInputPanelLayout.setExpandRatio(connectionPanel, 15);

        connectionLabel = new Label("Galaxy is not connected <font size=\"3\" color=\"red\"> &#128528;</font>");
        connectionLabel.setContentMode(ContentMode.HTML);
        connectionLabel.setHeight(80, Unit.PERCENTAGE);
        connectionLabel.setWidth(100, Unit.PERCENTAGE);
        connectionLabel.setStyleName(ValoTheme.LABEL_SMALL);
        connectionLabel.addStyleName(ValoTheme.LABEL_BOLD);
        connectionLabel.addStyleName(ValoTheme.LABEL_TINY);
        connectionPanel.addComponent(connectionLabel);
        connectionPanel.setComponentAlignment(connectionLabel, Alignment.TOP_LEFT);
        connectionPanel.setExpandRatio(connectionLabel, 0.7f);

        connectBtn = new Button("Close");
        connectBtn.setStyleName(ValoTheme.BUTTON_TINY);
        connectBtn.addStyleName(ValoTheme.BUTTON_SMALL);
        connectBtn.setWidth(100, Unit.PERCENTAGE);
        connectBtn.setHeight(80, Unit.PERCENTAGE);
        connectionPanel.addComponent(connectBtn);
        connectBtn.addClickListener(GalaxyConnectionPanelLayout.this);
        connectionPanel.setComponentAlignment(connectBtn, Alignment.TOP_RIGHT);
        connectionPanel.setExpandRatio(connectBtn, 0.3f);

        galaxyLink.addValueChangeListener((Property.ValueChangeEvent event) -> {
            if (galaxyLink.getValue().toString().equalsIgnoreCase("https://test-fe.cbu.uib.no/galaxy")) {
                APIKey.setValue("9228c9cd3eccff77b6fc2e8d6f3c7d48");
            } else if (galaxyLink.getValue().toString().equalsIgnoreCase("http://129.177.123.195:8080/")) {
                APIKey.setValue("abba9538dfd38d10c71aab67bbd30cfe");//71821f0c14cf63a2609f59d821bc1df3
            } else if (galaxyLink.getValue().toString().equalsIgnoreCase("https://usegalaxy.org/")) {
                APIKey.setValue("75d32d7c17f3bca57f78b725c2fc1565");
            } else if (galaxyLink.getValue().toString().equalsIgnoreCase("https://galaxy-uio.bioinfo.no/main/")) {
                APIKey.setValue("5bd78042c3420ccb77929aa45cfe4434");
            } else if (galaxyLink.getValue().toString().equalsIgnoreCase("https://usegalaxyp.org/")) {
                APIKey.setValue("61062cd3acb2433c1e1ed66d6560357f");
            }
        });
        return userInputPanelLayout;
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        this.hideGalaxyPanel();

    }

    public void disconnectGalaxy() {
        galaxyConnected = false;
        connectionLabel.setValue("Galaxy is not connected <font size=\"3\" color=\"red\"> &#128528;</font>");
        galaxyLinkContainer.setEnabled(!galaxyConnected);
        inputTabSheet.setEnabled(!galaxyConnected);
        galaxyInstance = null;
       
    }

    private boolean tryToConnect() {
//        Cookie[] cookies = ((VaadinServletRequest) VaadinService.getCurrentRequest()).getCookies();
//        for (Cookie cookie : cookies) {
//            cookiesRequestProperty += ";" + cookie.getName() + "=" + cookie.getValue();
//        }
//        cookiesRequestProperty = cookiesRequestProperty.replaceFirst("null;", "");
//        VaadinSession.getCurrent().setAttribute("cookies",cookiesRequestProperty); 

        if (((HorizontalLayout) inputTabSheet.getSelectedTab()).getData().toString().equalsIgnoreCase("Email & Password")) {
            userEmail.setRequired(true);
            password.setRequired(true);
            galaxyLink.setRequired(true);

            try {
                userEmail.validate();
                password.validate();
                galaxyLink.validate();
                if (!galaxyLink.isValid() || !userEmail.isValid() || !password.isValid()) {
                    userEmail.setRequired(!userEmail.isValid());
                    password.setRequired(!password.isValid());
                    galaxyLink.setRequired(!galaxyLink.isValid());
                    galaxyInstance = null;
                    return false;
                } else {
                    Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(userEmail.getValue());
                    if (!matcher.find()) {
                        userEmail.clear();
                        userEmail.setInputPrompt("Invalid email address");
                        userEmail.validate();
                        galaxyInstance = null;
                        return false;
                    }

                }
            } catch (Exception e) {
                userEmail.setRequired(!userEmail.isValid());
                password.setRequired(!password.isValid());
                galaxyLink.setRequired(!galaxyLink.isValid());
                galaxyInstance = null;
                return false;
            }
            galaxyInstance = GalaxyInstanceFactory.getFromCredentials(galaxyLink.getValue().toString(), userEmail.getValue(), password.getValue());
        } else {

            galaxyLink.setRequired(true);
            APIKey.setRequired(true);

            try {
                APIKey.validate();
                galaxyLink.validate();
                if (!galaxyLink.isValid() || !APIKey.isValid()) {
                    APIKey.setRequired(!APIKey.isValid());
                    galaxyLink.setRequired(!galaxyLink.isValid());
                    galaxyInstance = null;
                    System.out.println("at we reach 1");
                    return false;
                }
            } catch (Exception e) {
                APIKey.setRequired(!APIKey.isValid());
                galaxyLink.setRequired(!galaxyLink.isValid());
                galaxyInstance = null;
                System.out.println("at we reach 2");
                return false;

            }

            try {
                galaxyInstance = GalaxyInstanceFactory.get(galaxyLink.getValue().toString(), APIKey.getValue());
                System.out.println("at we reach 4");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        try {

//             ConfigurationClient galaxyConfigClient =galaxyInstance.getConfigurationClient();
//            System.out.println("at we reach 5" + galaxyConfigClient);
            System.out.println("at we reach 6");
//            galaxyInstance.getGalaxyUrl();
            System.out.println("at we reach 7");
//            System.out.println("at galaxy conn " + galaxyInstance.getApiKey());
            System.out.println("at we reach 8");
        } catch (Exception e) {
            e.printStackTrace();
            galaxyInstance = null;
            galaxyConnected = false;
            connectionLabel.setValue("<font color='red'>Galaxy is not connected, check input data <font size='3' color='red'>&#128530;</font></font>");
            System.out.println("at we reach 9");
            return false;
        }
        System.out.println("at we reach 10");
        APIKey.setRequired(false);
        userEmail.setRequired(false);
        password.setRequired(false);
        galaxyLink.setRequired(false);
        return true;
    }

    /**
     * The server connected to galaxy
     *
     * @param galaxyInstant galaxy server instance
     */
    public abstract void connectedToGalaxy(GalaxyInstance galaxyInstant);

    /**
     * The server connected to galaxy
     *
     * @param galaxyInstant galaxy server instance
     */
    public abstract void hideGalaxyPanel();

    public void validateAndConnect() {
        if (galaxyConnected) {
            galaxyConnected = false;
            connectionLabel.setValue("Galaxy is not connected <font size=\"3\" color=\"red\"> &#128528;</font>");
            galaxyLinkContainer.setEnabled(!galaxyConnected);
            inputTabSheet.setEnabled(!galaxyConnected);
            galaxyInstance = null;
            return;

        } else {
            galaxyConnected = tryToConnect();
            if (galaxyConnected) {
                connectionLabel.setValue("Galaxy is connected <font size=\"3\" color=\"green\"> &#128522;</font>");
            }
            galaxyLinkContainer.setEnabled(!galaxyConnected);
            inputTabSheet.setEnabled(!galaxyConnected);
        }

        this.connectedToGalaxy(galaxyInstance);

    }

    public void reConnectToGalaxy(String APIKEy, String galaxyUrl) {
        APIKey.setValue(APIKEy);
        if (!galaxyLink.getItemIds().contains(galaxyUrl)) {
            galaxyLink.addItem(galaxyUrl);
        }
        galaxyLink.setValue(galaxyUrl);

    }

}
