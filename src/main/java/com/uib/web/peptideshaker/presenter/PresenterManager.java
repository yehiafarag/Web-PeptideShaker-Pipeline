package com.uib.web.peptideshaker.presenter;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import com.uib.web.peptideshaker.presenter.core.ViewableFrame;
import com.vaadin.event.LayoutEvents;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import org.vaadin.hezamu.canvas.Canvas;

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
    private final HorizontalLayout topLayoutContainer;
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
    private final GridLayout presenterButtonsContainerLayout;
    /**
     * Top layout container is the right side buttons layout container that
     * contain the small control buttons.
     */
    private final HorizontalLayout topLayoutBtnsContainer;
    /**
     * Presenter buttons layout container contains the presenter control buttons
     * layout.
     */
    private final AbsoluteLayout presenterButtonsContainer;
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

        topLayoutContainer = new HorizontalLayout();
        topLayoutContainer.setSizeFull();
        topLayoutContainer.setStyleName("topviewcontainer");
        middleLayoutContainer.addComponent(topLayoutContainer);
        middleLayoutContainer.setExpandRatio(topLayoutContainer, 0);

        this.topLayoutBtnsContainer = new HorizontalLayout();
        topLayoutBtnsContainer.setSizeFull();
        topLayoutBtnsContainer.setSpacing(false);
        topLayoutContainer.addComponent(this.topLayoutBtnsContainer);
        topLayoutContainer.setComponentAlignment(this.topLayoutBtnsContainer, Alignment.TOP_RIGHT);

        topMiddleLayoutContainer = new AbsoluteLayout();
        topMiddleLayoutContainer.setSizeFull();
        middleLayoutContainer.addComponent(topMiddleLayoutContainer);
        middleLayoutContainer.setExpandRatio(topMiddleLayoutContainer, 100);
        topLayoutContainer.addStyleName("hide");

        presenterButtonsContainer = new AbsoluteLayout();
        presenterButtonsContainer.setSizeFull();
        presenterButtonsContainer.setData("controlBtnsAction");
        presenterButtonsContainer.setStyleName("presentercontainer");
        presenterButtonsContainer.addStyleName("bigmenubtn");
        presenterButtonsContainer.addStyleName("selectedbiglbtn");
        presenterButtonsContainer.addStyleName("hide");
        presenterButtonsContainer.addStyleName("welcomepagstyle");
        PresenterManager.this.addComponent(presenterButtonsContainer);
        PresenterManager.this.setExpandRatio(presenterButtonsContainer, 0);

        this.presenterButtonsContainerLayout = new GridLayout(2, 2);
        presenterButtonsContainerLayout.setSizeFull();
        presenterButtonsContainer.addComponent(this.presenterButtonsContainerLayout);
        presenterButtonsContainerLayout.addStyleName("actionButtonContainer");
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
    }

    /**
     * Hide / show side buttons .
     *
     * @param showSideButtons boolean show the buttons.
     */
    public void setSideButtonsVisible(boolean showSideButtons) {
        if (showSideButtons) {
            presenterButtonsContainer.removeStyleName("hide");
            topLayoutContainer.removeStyleName("hide");
        } else {
            presenterButtonsContainer.addStyleName("hide");
            topLayoutContainer.addStyleName("hide");
        }

    }

    /**
     * Register view into the view management system.
     *
     * @param view visualisation layout.
     */
    public void registerView(ViewableFrame view) {
        if (visualizationMap.containsKey(view.getViewId())) {
            ViewableFrame tview = visualizationMap.get(view.getViewId());
            tview.getPresenterControlButton().removeLayoutClickListener(PresenterManager.this);
//            tview.getTopView().removeLayoutClickListener(PresenterManager.this);
//            topLayoutBtnsContainer.removeComponent(tview.getTopView());
            subViewButtonsActionContainer.removeComponent(tview.getSubViewButtonsActionContainerLayout());
            subViewButtonsActionContainer.removeComponent(tview.getSubViewButtonsActionContainerLayout());

            topMiddleLayoutContainer.removeComponent(tview.getMainView());
            GridLayout.Area postion = presenterButtonsContainerLayout.getComponentArea(tview.getPresenterControlButton());
            column = postion.getColumn1();
            rows = postion.getRow1();
            presenterButtonsContainerLayout.removeComponent(tview.getPresenterControlButton());

        }
        view.getPresenterControlButton().addLayoutClickListener(PresenterManager.this);
        visualizationMap.put(view.getViewId(), view);
        subViewButtonsActionContainer.addComponent(view.getSubViewButtonsActionContainerLayout());
        topMiddleLayoutContainer.addComponent(view.getMainView());
        presenterButtonsContainerLayout.addComponent(view.getPresenterControlButton(), column++, rows);
        presenterButtonsContainerLayout.setComponentAlignment(view.getPresenterControlButton(), Alignment.MIDDLE_CENTER);
        if (column == 2) {
            column = 0;
            rows++;
        }
        if (rows == 2) {
            column = 0;
            rows = 0;
        }

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
            presenterButtonsContainer.addStyleName("welcomepagstyle");
        } else {
            presenterButtonsContainer.removeStyleName("welcomepagstyle");
        }

    }

    /**
     * On click on the side button view the selected layout
     *
     * @param event action on side buttons
     */
    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        String selectedBtnData = ((AbsoluteLayout) event.getComponent()).getData().toString();
        if (selectedBtnData.equalsIgnoreCase("controlBtnsAction")) {
            return;
        }
        this.viewLayout(selectedBtnData);
    }

}
