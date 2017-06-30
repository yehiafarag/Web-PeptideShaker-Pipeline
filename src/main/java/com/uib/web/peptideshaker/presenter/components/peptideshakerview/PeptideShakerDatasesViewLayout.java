package com.uib.web.peptideshaker.presenter.components.peptideshakerview;

import com.uib.web.peptideshaker.galaxy.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.presenter.core.PopupWindow;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import java.util.Map;

/**
 * This class represents the layout that contains PeptideShaker datasets
 * overview
 *
 * @author Yehia Farag
 */
public class PeptideShakerDatasesViewLayout extends HorizontalLayout {

    private final VerticalLayout container;
    private final PopupWindow headerLabel;
    private final FilterTableGraphComponent proteinsTableComponents;

    /**
     * Constructor to initialize the main layout and variables.
     */
    public PeptideShakerDatasesViewLayout() {
        PeptideShakerDatasesViewLayout.this.setSizeFull();
        PeptideShakerDatasesViewLayout.this.setSpacing(true);
        PeptideShakerDatasesViewLayout.this.setMargin(false);

        container = new VerticalLayout();
        container.setSizeFull();
        container.setSpacing(true);
        PeptideShakerDatasesViewLayout.this.addComponent(container);

        headerLabel = new PopupWindow("Dataset name");
        headerLabel.addStyleName("largetitle");
        container.addComponent(headerLabel);
        container.setExpandRatio(headerLabel, 1);

        proteinsTableComponents = new FilterTableGraphComponent();
        container.addComponent(proteinsTableComponents);
        container.setExpandRatio(proteinsTableComponents, 99);

    }

    /**
     * Update the dataset overview layout.
     *
     * @param peptideShakerVisualizationMap map of visualization datasets
     */
    public void updateData(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {
//        this.removeAllComponents();
// #of datasets

        //mode of view
        if (peptideShakerVisualizationDataset.getStatus().equalsIgnoreCase("ok") && peptideShakerVisualizationDataset.isValidFile()) {
            System.out.println("at --- --- " + peptideShakerVisualizationDataset.getName());
//                DatasetComponent dsComponent = new DatasetComponent(vDs) {
//                    @Override
//                    public void select(String datasetId) {
//                        PeptideShakerDatasesViewLayout.this.selectDataset(peptideShakerVisualizationMap.get(datasetId));
//                    }
//
//                };
//                this.addComponent(dsComponent);
//                this.setComponentAlignment(dsComponent, Alignment.TOP_CENTER);

//                 DatasetComponent ds29 = new DatasetComponent(vDs);
//                this.addComponent(ds29);
//                this.setComponentAlignment(ds29, Alignment.MIDDLE_CENTER);
//                
//                 DatasetComponent dsComponent1 = new DatasetComponent(vDs);
//                this.addComponent(dsComponent1);
//                this.setComponentAlignment(dsComponent1, Alignment.MIDDLE_CENTER);
//                
//                 DatasetComponent ds2 = new DatasetComponent(vDs);
//                this.addComponent(ds2);
//                this.setComponentAlignment(ds2, Alignment.MIDDLE_CENTER);
//                
//                 DatasetComponent ds3 = new DatasetComponent(vDs);
//                this.addComponent(ds3);
//                this.setComponentAlignment(ds3, Alignment.MIDDLE_CENTER);
//                
//                 DatasetComponent ds24 = new DatasetComponent(vDs);
//                this.addComponent(ds24);
//                this.setComponentAlignment(ds24, Alignment.MIDDLE_CENTER);
//                
//                 DatasetComponent ds25 = new DatasetComponent(vDs);
//                this.addComponent(ds25);
//                this.setComponentAlignment(ds25, Alignment.MIDDLE_CENTER);
//                
//                 DatasetComponent ds26 = new DatasetComponent(vDs);
//                this.addComponent(ds26);
//                this.setComponentAlignment(ds26, Alignment.MIDDLE_CENTER);
//                
//                 DatasetComponent ds27 = new DatasetComponent(vDs);
//                this.addComponent(ds27);
//                this.setComponentAlignment(ds27, Alignment.MIDDLE_CENTER);
//            }
        }
    }

    public void selectDataset(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {
        headerLabel.setLabelValue("Project: " + peptideShakerVisualizationDataset.getName());
        DatasetOverviewLayout dsOverview = new DatasetOverviewLayout((PeptideShakerVisualizationDataset) peptideShakerVisualizationDataset) {
            @Override
            public void close() {
                headerLabel.setPopupVisible(false);
            }

        };
        headerLabel.setContent(dsOverview);
        proteinsTableComponents.upateTableData(peptideShakerVisualizationDataset);

    }

  

}
