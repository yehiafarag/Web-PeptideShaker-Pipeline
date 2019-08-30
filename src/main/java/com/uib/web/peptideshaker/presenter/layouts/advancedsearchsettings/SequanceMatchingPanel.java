
package com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings;

import com.compomics.util.preferences.SequenceMatchingPreferences;
import com.uib.web.peptideshaker.model.core.WebSearchParameters;
import com.uib.web.peptideshaker.presenter.core.PopupWindow;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabelDropDounList;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabelTextField;
import com.vaadin.data.validator.DoubleRangeValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author y-mok
 */
public class SequanceMatchingPanel extends PopupWindow {

    private final HorizontalLabelTextField shareOfX;
    private final HorizontalLabelDropDounList matchingMethodList;
    private WebSearchParameters webSearchParameters;

    public SequanceMatchingPanel() {
        super(VaadinIcons.COG.getHtml() + " Sequance Matching");
        Label title = new Label("Settings");

        matchingMethodList = new HorizontalLabelDropDounList("Matching Method");
        Set<String> values = new LinkedHashSet<>();
        values.add(SequenceMatchingPreferences.MatchingType.string.name());

        values.add(SequenceMatchingPreferences.MatchingType.aminoAcid.name());
        values.add(SequenceMatchingPreferences.MatchingType.indistiguishableAminoAcids.name());
        matchingMethodList.updateData(values);
        matchingMethodList.setItemCaption(SequenceMatchingPreferences.MatchingType.string.name(), "Character Sequence");
        matchingMethodList.setItemCaption(SequenceMatchingPreferences.MatchingType.aminoAcid.name(), "Amino Acids");
        matchingMethodList.setItemCaption(SequenceMatchingPreferences.MatchingType.indistiguishableAminoAcids.name(), "Indistinguishable Amino Acids");
        shareOfX = new HorizontalLabelTextField("Maximum Share ofX's", 0.30, new DoubleRangeValidator("Range allowed between 0.05 and 0.95", 0.05, 0.95));
        AbsoluteLayout container = new AbsoluteLayout();
        container.setStyleName("popuppanelmaincontainer");
        container.setWidth(474, Unit.PIXELS);
        container.setHeight(160, Unit.PIXELS);

        VerticalLayout subContainer = new VerticalLayout();
        subContainer.setSizeFull();
        subContainer.setStyleName("subcontainer");
        subContainer.addComponent(matchingMethodList);
        subContainer.addComponent(shareOfX);
        container.addComponent(title, "left:10px;top:10px");
        container.addComponent(subContainer, "left:10px;top:40px;right:10px;bottom:40px");
        SequanceMatchingPanel.this.setContent(container);
        SequanceMatchingPanel.this.setClosable(true);

        Button okBtn = new Button("OK");
        okBtn.setWidth(76, Unit.PIXELS);
        okBtn.setHeight(20, Unit.PIXELS);
        okBtn.setStyleName(ValoTheme.BUTTON_TINY);
        okBtn.addClickListener((Button.ClickEvent event) -> {
            if (shareOfX.isValid()) {
                setPopupVisible(false);
            }
        });
        container.addComponent(okBtn, "bottom:10px;right:10px");
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyleName(ValoTheme.BUTTON_TINY);
        cancelBtn.setWidth(76, Unit.PIXELS);
        cancelBtn.setHeight(20, Unit.PIXELS);
        container.addComponent(cancelBtn, "bottom:10px;right:96px");
        cancelBtn.addClickListener((Button.ClickEvent event) -> {
            SequanceMatchingPanel.this.setPopupVisible(false);
        });

    }

    public void updateGUI(WebSearchParameters webSearchParameters) {
        this.webSearchParameters = webSearchParameters;
        shareOfX.setSelectedValue(webSearchParameters.getSequenceMatchingPreferences().getLimitX());
        matchingMethodList.setSelected(webSearchParameters.getSequenceMatchingPreferences().getSequenceMatchingType().name());
        super.setLabelValue(VaadinIcons.COG.getHtml() + " Sequance Matching" + "<center>" + webSearchParameters.getSequenceMatchingPreferences().getShortDescription() + "</center>");

    }

    @Override
    public void onClosePopup() {
    }

    @Override
    public void setPopupVisible(boolean visible) {
        if (visible && webSearchParameters != null) {
            updateGUI(webSearchParameters);
        } else if (webSearchParameters != null) {
            updateParameters();
            super.setLabelValue(VaadinIcons.COG.getHtml() + " Sequance Matching" + "<center>" + webSearchParameters.getSequenceMatchingPreferences().getShortDescription() + "</center>");
        }
        super.setPopupVisible(visible); //To change body of generated methods, choose Tools | Templates.
    }

    private void updateParameters() {
        if (shareOfX.isValid()) {
            webSearchParameters.getSequenceMatchingPreferences().setLimitX(Double.valueOf(shareOfX.getSelectedValue()));
        }
        webSearchParameters.getSequenceMatchingPreferences().setSequenceMatchingType(SequenceMatchingPreferences.MatchingType.valueOf(matchingMethodList.getSelectedValue()));

    }

}
