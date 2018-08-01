package com.uib.web.peptideshaker.presenter;

//import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
//import com.uib.onlinepeptideshaker.managers.RegistrableView;
//import com.uib.onlinepeptideshaker.presenter.view.SmallSideBtn;
import com.uib.web.peptideshaker.presenter.core.SmallSideBtn;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Link;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
//import sun.security.provider.certpath.Vertex;

/**
 * This class represents the welcome page for Online PeptideShaker
 *
 * @author Yehia Farag
 */
public class WelcomePagePresenter extends VerticalLayout implements ViewableFrame {

    /**
     * The body layout panel.
     */
    private final HorizontalLayout bodyPanel;
    /**
     * The header layout panel.
     */
    private final VerticalLayout headerPanel;
    /**
     * The header layout container.
     */
    private final HorizontalLayout headerPanelLayout;
    /**
     * Galaxy connection setting popup.
     */
    private PopupView connectionSettingsPanel;

    /**
     * The galaxy server connection panel.
     */
//    private GalaxyConnectionPanel galaxyInputPanel;
    /**
     * The side home button .
     */
    private SmallSideBtn homeBtn;
    /**
     * The top home button .
     */
    private SmallSideBtn topHomeBtn;

    /**
     * Constructor to initialize the layout.
     *
     * @param galaxyConnectionLayout The Galaxy server connection layout
     */
    public WelcomePagePresenter(Layout galaxyConnectionLayout, Link nelsGalaxyConnectionBtn) {
        WelcomePagePresenter.this.setSizeFull();
        WelcomePagePresenter.this.setStyleName("activelayout");
        headerPanel = new VerticalLayout();
        headerPanel.setHeight(50, Unit.PIXELS);
        WelcomePagePresenter.this.addComponent(headerPanel);
        WelcomePagePresenter.this.setComponentAlignment(headerPanel, Alignment.MIDDLE_LEFT);
        WelcomePagePresenter.this.setExpandRatio(headerPanel, 10);
        headerPanelLayout = initializeHeaderPanel();
        headerPanel.addComponent(headerPanelLayout);
        headerPanel.setMargin(new MarginInfo(false, false, false, false));

        bodyPanel = new HorizontalLayout();
        bodyPanel.setSizeFull();
        bodyPanel.setMargin(new MarginInfo(true, false, false, false));
        WelcomePagePresenter.this.addComponent(bodyPanel);
        WelcomePagePresenter.this.setComponentAlignment(bodyPanel, Alignment.TOP_CENTER);
        WelcomePagePresenter.this.setExpandRatio(bodyPanel, 70);

        VerticalLayout bodyContent = new VerticalLayout();
        bodyContent.setSizeFull();
        bodyContent.setSpacing(true);
        bodyContent.setWidth(405, Unit.PIXELS);
        bodyContent.setHeight(200, Unit.PIXELS);
        bodyPanel.addComponent(bodyContent);
        bodyPanel.setComponentAlignment(bodyContent, Alignment.TOP_CENTER);

        Label welcomeText = new Label();
        welcomeText.setSizeFull();
        welcomeText.setContentMode(ContentMode.HTML);
        welcomeText.setStyleName(ValoTheme.LABEL_NO_MARGIN);
        welcomeText.setValue("<font style='font-weight: bold; font-size:23px'>Welcome to PeptideShaker <font size='2'><i>(online version)</i></font></font> <br/><br/>To start using the system connect to your Galaxy Server");

        bodyContent.addComponent(welcomeText);
        bodyContent.setComponentAlignment(welcomeText, Alignment.TOP_LEFT);

        bodyContent.addComponent(galaxyConnectionLayout);
        bodyContent.setComponentAlignment(galaxyConnectionLayout, Alignment.TOP_LEFT);

        bodyContent.addComponent(nelsGalaxyConnectionBtn);
        bodyContent.setComponentAlignment(nelsGalaxyConnectionBtn, Alignment.TOP_RIGHT);
//
        homeBtn = new SmallSideBtn("img/home-o.svg");
        homeBtn.setData(WelcomePagePresenter.this.getViewId());

        topHomeBtn = new SmallSideBtn("img/home-o.svg");
        topHomeBtn.setData(WelcomePagePresenter.this.getViewId());

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
        homeBtn.setSelected(false);
        topHomeBtn.setSelected(false);
        this.addStyleName("hidepanel");
    }

    @Override
    public void maximizeView() {
        homeBtn.setSelected(true);
        topHomeBtn.setSelected(true);
        this.removeStyleName("hidepanel");
    }

    @Override
    public SmallSideBtn getRightView() {
        return homeBtn;
    }

    @Override
    public VerticalLayout getMainView() {
        return this;
    }

    @Override
    public VerticalLayout getLeftView() {
        return new VerticalLayout();
    }

    @Override
    public HorizontalLayout getBottomView() {
        return new HorizontalLayout();
    }

    @Override
    public SmallSideBtn getTopView() {
        return topHomeBtn;
    }

}
