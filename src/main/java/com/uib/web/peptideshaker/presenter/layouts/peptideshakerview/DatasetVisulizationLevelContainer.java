package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview;

import com.compomics.util.experiment.identification.identification_parameters.SearchParameters;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.DatasetVisulizationLevelComponent;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.presenter.core.BigSideBtn;
import com.uib.web.peptideshaker.presenter.core.FilterButton;
import com.uib.web.peptideshaker.presenter.core.PopupWindow;
import com.uib.web.peptideshaker.presenter.layouts.SearchSettingsLayout;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import java.util.Set;

/**
 * This class represents the layout that contains PeptideShaker datasets
 * overview
 *
 * @author Yehia Farag
 */
public class DatasetVisulizationLevelContainer extends HorizontalLayout {

    private final AbsoluteLayout container;
    private final PopupWindow headerLabel;
    private final DatasetVisulizationLevelComponent datasetVisulizationLevelComponent;
//    private final ThemeResource defaultThemeIcon;
//    private final ThemeResource activeThemeIcon;

    /**
     * Constructor to initialise the main layout and variables.
     *
     * @param Selection_Manager
     * @param datasetsOverviewBtn
     */
    public DatasetVisulizationLevelContainer(SelectionManager Selection_Manager, BigSideBtn datasetsOverviewBtn) {
        DatasetVisulizationLevelContainer.this.setSizeFull();
        DatasetVisulizationLevelContainer.this.setStyleName("transitionallayout");
        DatasetVisulizationLevelContainer.this.setSpacing(false);
        DatasetVisulizationLevelContainer.this.setMargin(false);
        datasetsOverviewBtn.setDescription("Selected dataset overview and the proteins list");

//        defaultThemeIcon = new ThemeResource("img/cluster.svg");//new ThemeResource("img/ds_filters_icon.png");
//        activeThemeIcon = new ThemeResource("img/cluster.svg");
        datasetsOverviewBtn.updateIcon(VaadinIcons.CLUSTER.getHtml());
        datasetsOverviewBtn.updateIconResource(new ThemeResource("img/venn_color.png"));//img/vizicon.png
        container = new AbsoluteLayout();
        container.setSizeFull();
        DatasetVisulizationLevelContainer.this.addComponent(container);

        HorizontalLayout topLabelContainer = new HorizontalLayout();
        topLabelContainer.setSizeFull();
        topLabelContainer.addStyleName("minhight30");
        container.addComponent(topLabelContainer);

        HorizontalLayout topLeftLabelContainer = new HorizontalLayout();
        topLeftLabelContainer.setWidthUndefined();
        topLeftLabelContainer.setHeight(100, Unit.PERCENTAGE);
        topLabelContainer.addComponent(topLeftLabelContainer);
        headerLabel = new PopupWindow("Dataset name") {
            @Override
            public void onClosePopup() {
            }

        };
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
//                    datasetsOverviewBtn.updateIconResource(activeThemeIcon);
                } else {
//                    datasetsOverviewBtn.updateIconResource(defaultThemeIcon);
                }
            }
        };
        container.addComponent(datasetVisulizationLevelComponent, "left:0px;top:40px;");

    }

    public void selectDataset(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {
        headerLabel.setLabelValue("Dataset: " + peptideShakerVisualizationDataset.getName().split("___")[0]);

        SearchSettingsLayout dsOverview = new SearchSettingsLayout((PeptideShakerVisualizationDataset) peptideShakerVisualizationDataset) {
            @Override
            public void saveSearchingFile(SearchParameters searchParameters, boolean isNew) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            @Override
            public void cancel() {
                headerLabel.setPopupVisible(false);
            }
        };
        headerLabel.setContent(dsOverview);
        datasetVisulizationLevelComponent.updateData(peptideShakerVisualizationDataset);

    }

}
