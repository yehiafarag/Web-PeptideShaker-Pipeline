package com.uib.web.peptideshaker.presenter.pscomponents;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

/**
 *
 * @author Yehia Farag this class represents spectra chart in relation with
 * peptide sequence
 */
public  class PSMSequenceChart extends AbsoluteLayout {

    private final Image plotImage;
    private final Label sequenceLabel;
    private final Object objectId;

    public Object getObjectId() {
        return objectId;
    }

    public PSMSequenceChart(String sequence, Object objectId) {
        PSMSequenceChart.this.setSizeFull();
        this.plotImage = new Image();
        this.plotImage.setSizeFull();
        PSMSequenceChart.this.addComponent(this.plotImage);
        this.sequenceLabel = new Label(sequence, ContentMode.HTML);
        this.sequenceLabel.setStyleName(ValoTheme.LABEL_SMALL);
        this.sequenceLabel.setSizeFull();
        PSMSequenceChart.this.addComponent(sequenceLabel);
        PSMSequenceChart.this.reset();
        this.objectId=objectId;

       
    }

    public void reset() {
        this.plotImage.setVisible(false);
        this.sequenceLabel.setVisible(true);
        this.setStyleName("textintablecell");
    }


}
