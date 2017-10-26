package com.uib.web.peptideshaker.presenter.components.peptideshakerview;

import com.uib.web.peptideshaker.presenter.components.peptideshakerview.components.DatasetVisulizationLevelComponent;
import com.uib.web.peptideshaker.presenter.core.DatasetOverviewLayout;
import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.presenter.core.BigSideBtn;
import com.uib.web.peptideshaker.presenter.core.FilterButton;
import com.uib.web.peptideshaker.presenter.core.PopupWindow;
import com.vaadin.event.LayoutEvents;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import java.util.Set;

/**
 * This class represents the layout that contains PeptideShaker datasets
 * overview
 *
 * @author Yehia Farag
 */
public class DatasetVisulizationLevelContainer extends HorizontalLayout {

    private final VerticalLayout container;
    private final PopupWindow headerLabel;
    private final DatasetVisulizationLevelComponent datasetVisulizationLevelComponent;
    private final ThemeResource defaultThemeIcon;
    private final ThemeResource activeThemeIcon;

    /**
     * Constructor to initialize the main layout and variables.
     */
    public DatasetVisulizationLevelContainer(SelectionManager Selection_Manager, BigSideBtn datasetsOverviewBtn) {
        DatasetVisulizationLevelContainer.this.setSizeFull();
        DatasetVisulizationLevelContainer.this.setSpacing(true);
        DatasetVisulizationLevelContainer.this.setMargin(false);

        defaultThemeIcon = new ThemeResource("img/ds_filters_icon.png");
        activeThemeIcon = new ThemeResource("img/ds_filters_icon_color.png");
        datasetsOverviewBtn.updateIcon(defaultThemeIcon);
        container = new VerticalLayout();
        container.setSizeFull();
        container.setSpacing(true);
        DatasetVisulizationLevelContainer.this.addComponent(container);

        HorizontalLayout topLabelContainer = new HorizontalLayout();
        topLabelContainer.setSizeFull();
        topLabelContainer.addStyleName("minhight30");
        container.addComponent(topLabelContainer);
        container.setExpandRatio(topLabelContainer, 1);

        HorizontalLayout topLeftLabelContainer = new HorizontalLayout();
        topLeftLabelContainer.setWidthUndefined();
        topLeftLabelContainer.setHeight(100, Unit.PERCENTAGE);
        topLabelContainer.addComponent(topLeftLabelContainer);
        headerLabel = new PopupWindow("Dataset name");
        headerLabel.addStyleName("largetitle");
        headerLabel.setWidthUndefined();
        topLeftLabelContainer.setSpacing(true);
        topLeftLabelContainer.addComponent(headerLabel);

        FilterButton removeFilterIcon = new FilterButton() {
            @Override
            public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                Selection_Manager.resetDatasetSelection();

            }
        };
        topLeftLabelContainer.addComponent(removeFilterIcon);

        Label commentLabel = new Label("<i style='padding-right: 50px;'>* Click in the charts to select and filter data</i>", ContentMode.HTML);
        commentLabel.setWidthUndefined();
        commentLabel.setStyleName("resizeabletext");
        commentLabel.addStyleName("margintop10");
        topLabelContainer.addComponent(commentLabel);
        topLabelContainer.setComponentAlignment(commentLabel, Alignment.TOP_RIGHT);

        datasetVisulizationLevelComponent = new DatasetVisulizationLevelComponent(Selection_Manager) {
            @Override
            public void updateFilterSelection(Set<Comparable> selection, Set<Comparable> selectedCategories, boolean topFilter, boolean selectOnly, boolean selfAction) {
                removeFilterIcon.setVisible(Selection_Manager.isDatasetFilterApplied());
                super.updateFilterSelection(selection, selectedCategories, topFilter, selectOnly, selfAction);
                if (Selection_Manager.isDatasetFilterApplied()) {
                    datasetsOverviewBtn.updateIcon(activeThemeIcon);
                } else {
                    datasetsOverviewBtn.updateIcon(defaultThemeIcon);
                }
            }
        };
        container.addComponent(datasetVisulizationLevelComponent);
        container.setExpandRatio(datasetVisulizationLevelComponent, 99);

    }

    public void selectDataset(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {
        headerLabel.setLabelValue("Dataset: " + peptideShakerVisualizationDataset.getName());
        DatasetOverviewLayout dsOverview = new DatasetOverviewLayout((PeptideShakerVisualizationDataset) peptideShakerVisualizationDataset) {
            @Override
            public void close() {
                headerLabel.setPopupVisible(false);
            }

        };
        headerLabel.setContent(dsOverview);
        datasetVisulizationLevelComponent.updateData(peptideShakerVisualizationDataset);

    }

}
