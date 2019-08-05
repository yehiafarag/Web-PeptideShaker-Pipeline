package com.uib.web.peptideshaker.presenter;

import com.uib.web.peptideshaker.presenter.core.ButtonWithLabel;
import com.uib.web.peptideshaker.presenter.core.SmallSideBtn;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import com.uib.web.peptideshaker.presenter.core.ViewableFrame;
import com.vaadin.event.LayoutEvents;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;

/**
 * This class represents the main layout of the application and main view
 * controller manager
 *
 * @author Yehia Farag
 */
public class PresenterManager extends HorizontalLayout implements LayoutEvents.LayoutClickListener {

    /**
     * Top layout container.
     */
//    private final HorizontalLayout topLayoutContainer;
    /**
     * Left layout container is the main layout container that contain the main
     * views.
     */
    private final AbsoluteLayout subViewButtonsActionContainer;
    /**
     * Left layout container is the main layout container that contain the main
     * views.
     */
    private final AbsoluteLayout topMiddleLayoutContainer;
    /**
     * Left layout container is the main layout container that contain the main
     * views.
     */
//    private final AbsoluteLayout bottomLayoutContainer;
    /**
     * Left layout container is the main layout container that contain the main
     * views.
     */
    private final VerticalLayout middleLayoutContainer;

    /**
     * Presenter buttons container layout container is layout container that
     * contain the small presenter control buttons.
     */
    private final AbsoluteLayout presenterButtonsContainerLayout;

    private final Button signOutBtn;

    public AbsoluteLayout getPresenterButtonsContainerLayout() {
        return presenterButtonsContainerLayout;
    }
    /**
     * Top layout container is the right side buttons layout container that
     * contain the small control buttons.
     */
//    private final HorizontalLayout topLayoutBtnsContainer;
    /**
     * Presenter buttons layout container contains the presenter control buttons
     * layout.
     */
    private final AbsoluteLayout subPresenterButtonsContainer;
    /**
     * Map of current registered views.
     */
    private final Map<String, ViewableFrame> visualizationMap = new LinkedHashMap<>();
    /**
     * The column index number for the presenter buttons container
     */
    private int column = 0;
    /**
     * The rows index number for the presenter buttons container
     */
    private int rows = 0;
    private boolean startDrag = false;
    private long startX = 0;
    private long endX = 0;
    private final Map<String, SmallSideBtn> presenterBtnsMap = new LinkedHashMap<>();

    public AbsoluteLayout getSubViewButtonsActionContainer() {
        return subViewButtonsActionContainer;
    }

    /**
     * Constructor to initialise the layout.
     */
    public PresenterManager() {
        PresenterManager.this.setSizeFull();
        PresenterManager.this.setStyleName("mainlayout");

        subViewButtonsActionContainer = new AbsoluteLayout();
        subViewButtonsActionContainer.setSizeFull();
        subViewButtonsActionContainer.setStyleName("subviewbuttonsactioncontainer");
        PresenterManager.this.addComponent(subViewButtonsActionContainer);
        PresenterManager.this.setExpandRatio(subViewButtonsActionContainer, 0);

        middleLayoutContainer = new VerticalLayout();
        middleLayoutContainer.setSizeFull();
        middleLayoutContainer.setStyleName("middleviewcontainer");
        PresenterManager.this.addComponent(middleLayoutContainer);
        PresenterManager.this.setExpandRatio(middleLayoutContainer, 100);

//        topLayoutContainer = new HorizontalLayout();
//        topLayoutContainer.setSizeFull();
//        topLayoutContainer.setStyleName("topviewcontainer");
//        middleLayoutContainer.addComponent(topLayoutContainer);
//        middleLayoutContainer.setExpandRatio(topLayoutContainer, 0);
//        this.topLayoutBtnsContainer = new HorizontalLayout();
//        topLayoutBtnsContainer.setSizeFull();
//        topLayoutBtnsContainer.setSpacing(false);
//        topLayoutContainer.addComponent(this.topLayoutBtnsContainer);
//        topLayoutContainer.setComponentAlignment(this.topLayoutBtnsContainer, Alignment.TOP_RIGHT);
        topMiddleLayoutContainer = new AbsoluteLayout();
        topMiddleLayoutContainer.setSizeFull();
        middleLayoutContainer.addComponent(topMiddleLayoutContainer);
        middleLayoutContainer.setExpandRatio(topMiddleLayoutContainer, 100);
//        topLayoutContainer.addStyleName("hide");

        subPresenterButtonsContainer = new AbsoluteLayout();
        subPresenterButtonsContainer.setSizeFull();
        subPresenterButtonsContainer.setData("controlBtnsAction");
        subPresenterButtonsContainer.setStyleName("presentercontainer");
        subPresenterButtonsContainer.addStyleName("bigmenubtn");
        subPresenterButtonsContainer.addStyleName("selectedbiglbtn");
        subPresenterButtonsContainer.addStyleName("hide");
        subPresenterButtonsContainer.addStyleName("welcomepagstyle");
        PresenterManager.this.addComponent(subPresenterButtonsContainer);
        PresenterManager.this.setExpandRatio(subPresenterButtonsContainer, 0);

        this.presenterButtonsContainerLayout = new AbsoluteLayout();
        presenterButtonsContainerLayout.setSizeFull();
//        presenterButtonsContainer.addComponent(this.presenterButtonsContainerLayout);
//        presenterButtonsContainerLayout.addStyleName("actionButtonContainer");
//        presenterButtonsContainerLayout.addStyleName("hideactionbutton");
//        Canvas canvas = new Canvas();
//        presenterButtonsContainer.addComponent(canvas);
//        canvas.setSizeFull();
//        canvas.setStyleName("sweepeventcanvas");
//        canvas.addMouseMoveListener((MouseEventDetails mouseDetails) -> {
//            if (startDrag) {
//                if (startX == 1000000000) {
//                    startX = mouseDetails.getClientX();
//                }
//                endX = mouseDetails.getClientX();
//            }
//        });
//        canvas.addMouseDownListener(() -> {
//            startX = 1000000000;
//            startDrag = true;
//        });
//        middleLayoutContainer.addLayoutClickListener((LayoutEvents.LayoutClickEvent event) -> {
//            if (event.getClickedComponent().getStyleName().contains("home")) {
//                presenterButtonsContainerLayout.addStyleName("hideactionbutton");
//            }
//        });
//
//        canvas.addMouseUpListener(() -> {
//            startDrag = false;
//            if (endX < startX && presenterButtonsContainerLayout.getStyleName().contains("hideactionbutton")) {
//                presenterButtonsContainerLayout.removeStyleName("hideactionbutton");
//            } else {
//                presenterButtonsContainerLayout.addStyleName("hideactionbutton");
//            }
//        });
//        leftLayoutContainer.addComponent(presenterButtonsContainerLayout);

        signOutBtn = new Button("Signout");
        signOutBtn.addClickListener((Button.ClickEvent event) -> {
            VaadinSession.getCurrent().getSession().invalidate();
            Page.getCurrent().reload();
        });
    }

    /**
     * Hide / show side buttons .
     *
     * @param showSideButtons boolean show the buttons.
     */
    public void setSideButtonsVisible(boolean showSideButtons) {
        if (showSideButtons) {
            subPresenterButtonsContainer.removeStyleName("hide");
//            topLayoutContainer.removeStyleName("hide");
        } else {
            subPresenterButtonsContainer.addStyleName("hide");
//            topLayoutContainer.addStyleName("hide");
        }

    }

    int x = 0;
    int y = 0;

    /**
     * Register view into the view management system.
     *
     * @param view visualisation layout.
     */
    public void registerView(ViewableFrame view) {
        if (visualizationMap.containsKey(view.getViewId())) {
            presenterBtnsMap.remove(view.getViewId());
            ViewableFrame tview = visualizationMap.get(view.getViewId());
            AbstractOrderedLayout cBtn = tview.getLargePresenterControlButton();
            cBtn.removeLayoutClickListener(PresenterManager.this);
            subViewButtonsActionContainer.removeComponent(tview.getSubViewButtonsActionContainerLayout());
            topMiddleLayoutContainer.removeComponent(tview.getMainView());
//            column = presenterButtonsContainerLayout.getComponentArea(cBtn).getColumn1();
//            rows = presenterButtonsContainerLayout.getComponentArea(cBtn).getRow1();
//            presenterButtonsContainerLayout.removeComponent(cBtn);
            y = presenterButtonsContainerLayout.getPosition(cBtn).getTopValue().intValue();
            x = presenterButtonsContainerLayout.getPosition(cBtn).getLeftValue().intValue();
            presenterButtonsContainerLayout.removeComponent(cBtn);

        }

        visualizationMap.put(view.getViewId(), view);
        presenterBtnsMap.put(view.getViewId(), view.getSmallPresenterControlButton());

        subViewButtonsActionContainer.addComponent(view.getSubViewButtonsActionContainerLayout());
        topMiddleLayoutContainer.addComponent(view.getMainView());
        view.getSmallPresenterControlButton().addLayoutClickListener(PresenterManager.this);
        if (!view.getViewId().equalsIgnoreCase("com.uib.web.peptideshaker.presenter.WelcomePagePresenter")) {
            view.getLargePresenterControlButton().addLayoutClickListener(PresenterManager.this);
            presenterButtonsContainerLayout.addComponent(view.getLargePresenterControlButton(), "left:" + x + "%;top:" + y + "%;");
            y += 50;
            if (y == 100) {
                y = 0;
                x += 50;
            }
//            presenterButtonsContainerLayout.addComponent(view.getLargePresenterControlButton(), column, rows++);
//            if (rows == 2) {
//                column++;
//                rows = 0;
//            }

        } else {
//            view.getSubViewButtonsActionContainerLayout().addLayoutClickListener(PresenterManager.this);
            view.getSmallPresenterControlButton().addLayoutClickListener(PresenterManager.this);
//             view.getLargePresenterControlButton().addLayoutClickListener(PresenterManager.this);
        }
        reOrganizePresenterButtons();
    }

    /**
     * View only selected view and hide the rest of registered layout
     *
     * @param viewId selected view id
     */
    public void viewLayout(String viewId) {
        visualizationMap.values().forEach((view) -> {
            if (viewId.equalsIgnoreCase(view.getViewId())) {
                view.maximizeView();
            } else {
                view.minimizeView();
            }
        });
        if (viewId.equalsIgnoreCase("com.uib.web.peptideshaker.presenter.WelcomePagePresenter")) {
            subPresenterButtonsContainer.addStyleName("welcomepagstyle");
        } else {
            subPresenterButtonsContainer.removeStyleName("welcomepagstyle");
        }
        if (viewId.equalsIgnoreCase("com.uib.web.peptideshaker.presenter.InteractivePSPRojectResultsPresenter")) {
            subViewButtonsActionContainer.addStyleName("displayvisible");
        } else {
            subViewButtonsActionContainer.removeStyleName("displayvisible");
        }

    }

    /**
     * On click on the side button view the selected layout
     *
     * @param event action on side buttons
     */
    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        Component com = event.getComponent();
        String selectedBtnData;
        if (com instanceof SmallSideBtn) {
            selectedBtnData = ((SmallSideBtn) com).getData().toString();
        } else {
            selectedBtnData = ((ButtonWithLabel) com).getData().toString();
        }
        if (selectedBtnData.equalsIgnoreCase("controlBtnsAction")) {
            return;
        }
        this.viewLayout(selectedBtnData);
    }

    public Map<String, SmallSideBtn> getPresenterBtnsMap() {
        return presenterBtnsMap;
    }

    private void reOrganizePresenterButtons() {
        if (presenterBtnsMap.size() < 4) {
            return;
        }
        int l = 0;
        int t = 0;
        subPresenterButtonsContainer.removeAllComponents();
        for (SmallSideBtn btn : presenterBtnsMap.values()) {
            subPresenterButtonsContainer.addComponent(btn, "left:" + l + "px; top:" + t + "px;");
            l = l + 45;
            if (l > 50) {
                l = 0;
                t = 45;
            }
        }
        //subPresenterButtonsContainer.addComponent(view.getSmallPresenterControlButton());
    }

}
