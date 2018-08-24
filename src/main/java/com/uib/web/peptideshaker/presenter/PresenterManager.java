package com.uib.web.peptideshaker.presenter;

import com.vaadin.event.LayoutEvents;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import com.uib.web.peptideshaker.presenter.core.ViewableFrame;

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
    private final AbsoluteLayout leftLayoutContainer;
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
     * Right layout container is the right side buttons layout container that
     * contain the small control buttons.
     */
    private final VerticalLayout rightLayoutBtnsContainer;
    /**
     * Top layout container is the right side buttons layout container that
     * contain the small control buttons.
     */
    private final HorizontalLayout topLayoutBtnsContainer;
    /**
     * Right layout container is the right side buttons layout container that
     * contain the small control buttons.
     */
    private final AbsoluteLayout rightLayoutContainer;
    /**
     * Map of current registered views.
     */
    private final Map<String, ViewableFrame> visualizationMap = new LinkedHashMap<>();

    /**
     * Constructor to initialise the layout
     */
    public PresenterManager() {
        PresenterManager.this.setSizeFull();
        PresenterManager.this.setStyleName("mainlayout");

        leftLayoutContainer = new AbsoluteLayout();
        leftLayoutContainer.setSizeFull();
        leftLayoutContainer.setStyleName("leftsideviewcontainer");
        PresenterManager.this.addComponent(leftLayoutContainer);
        PresenterManager.this.setExpandRatio(leftLayoutContainer, 0);

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

        rightLayoutContainer = new AbsoluteLayout();
        rightLayoutContainer.setSizeFull();
        rightLayoutContainer.setStyleName("rightsidebtncontainer");
        rightLayoutContainer.addStyleName("hide");
        PresenterManager.this.addComponent(rightLayoutContainer);
        PresenterManager.this.setExpandRatio(rightLayoutContainer, 0);

        this.rightLayoutBtnsContainer = new VerticalLayout();
        rightLayoutBtnsContainer.setSizeFull();
        rightLayoutContainer.addComponent(this.rightLayoutBtnsContainer);

    }

    /**
     * Hide / show side buttons .
     *
     * @param showSideButtons boolean show the buttons.
     */
    public void setSideButtonsVisible(boolean showSideButtons) {
        if (showSideButtons) {
            rightLayoutContainer.removeStyleName("hide");
            topLayoutContainer.removeStyleName("hide");
        } else {
            rightLayoutContainer.addStyleName("hide");
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
            tview.getRightView().removeLayoutClickListener(PresenterManager.this);
            tview.getTopView().removeLayoutClickListener(PresenterManager.this);

            topLayoutBtnsContainer.removeComponent(tview.getTopView());
            leftLayoutContainer.removeComponent(tview.getLeftView());
            leftLayoutContainer.removeComponent(tview.getLeftView());
            topMiddleLayoutContainer.removeComponent(tview.getMainView());
            rightLayoutBtnsContainer.removeComponent(tview.getRightView());

        }
        view.getRightView().addLayoutClickListener(PresenterManager.this);
        view.getTopView().addLayoutClickListener(PresenterManager.this);
        topLayoutBtnsContainer.addComponent(view.getTopView());
        topLayoutBtnsContainer.setComponentAlignment(view.getTopView(), Alignment.TOP_CENTER);
        visualizationMap.put(view.getViewId(), view);
        leftLayoutContainer.addComponent(view.getLeftView());
        topMiddleLayoutContainer.addComponent(view.getMainView());
        rightLayoutBtnsContainer.addComponent(view.getRightView());
        rightLayoutBtnsContainer.setComponentAlignment(view.getRightView(), Alignment.MIDDLE_CENTER);

    }

    /**
     * View only selected view and hide the rest of registered layout
     *
     * @param viewId selected view id
     */
    public void viewLayout(String viewId) {
        visualizationMap.values().forEach((view) -> {
            view.minimizeView();
        });
        visualizationMap.get(viewId).maximizeView();

    }

    /**
     * On click on the side button view the selected layout
     *
     * @param event action on side buttons
     */
    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        this.viewLayout(((AbsoluteLayout) event.getComponent()).getData().toString());
    }

}
