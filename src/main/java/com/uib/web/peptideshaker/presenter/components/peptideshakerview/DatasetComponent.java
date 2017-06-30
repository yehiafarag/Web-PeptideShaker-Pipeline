package com.uib.web.peptideshaker.presenter.components.peptideshakerview;

import com.uib.web.peptideshaker.galaxy.PeptideShakerVisualizationDataset;
import com.vaadin.event.LayoutEvents;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 *
 * @author Yehia Farag
 */
public  abstract class DatasetComponent extends VerticalLayout implements LayoutEvents.LayoutClickListener{

    private final String datasetId;   
    private VerticalLayout proteinLayout;
    public DatasetComponent(PeptideShakerVisualizationDataset dataset) {
        this.setSizeFull();
        this.setSpacing(true);
        this.setStyleName("datasetcomponent");
        this.datasetId= dataset.getJobId();
        Label name = new Label(dataset.getName());
        this.addComponent(name);
        this.setComponentAlignment(name, Alignment.BOTTOM_CENTER);
        Label prot = new Label("#Proteins " + dataset.getProteinsNumber());
        prot.addStyleName(ValoTheme.LABEL_SMALL);
         prot.addStyleName(ValoTheme.LABEL_TINY);
        this.addComponent(prot);
        this.setComponentAlignment(prot, Alignment.TOP_CENTER);
        this.initProteinsLayout();

    }

    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        select(datasetId);
    }
    
    private void initProteinsLayout(){
    proteinLayout = new VerticalLayout();
    proteinLayout.setSizeFull();
    proteinLayout.setStyleName("lightgraylayout");
    }

    public VerticalLayout getProteinLayout() {
        return proteinLayout;
    }
    
    public abstract void select(String datasetId);

}
