package com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings;

import com.compomics.util.gui.parameters.identification_parameters.PTMLocalizationParametersDialog;
import com.compomics.util.preferences.PTMScoringPreferences;
import com.uib.web.peptideshaker.model.core.WebSearchParameters;
import com.uib.web.peptideshaker.presenter.core.PopupWindow;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabelDropDounList;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabelTextField;
import com.vaadin.data.Property;
import com.vaadin.data.validator.DoubleRangeValidator;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.LinkedHashSet;
import java.util.Set;
import web.com.compomics.util.experiment.identification.modification.ModificationLocalizationScore;

/**
 *
 * @author y-mok
 */
public class PTMLocalizationPanel extends PopupWindow {

    private final HorizontalLabelDropDounList probabilisticScore;
    private final HorizontalLabelDropDounList accountNeutralLosses;
    private final HorizontalLabelDropDounList confidentSites;
    private final HorizontalLabelTextField threshold;

    private WebSearchParameters webSearchParameters;

    public PTMLocalizationPanel() {
        super(VaadinIcons.COG.getHtml() + " PTM Localization");
        Label title = new Label("Modification Scoring");
        AbsoluteLayout container = new AbsoluteLayout();
        container.setStyleName("popuppanelmaincontainer");
        container.setWidth(600, Unit.PIXELS);
        container.setHeight(260, Unit.PIXELS);

        VerticalLayout subContainer = new VerticalLayout();
        subContainer.setSizeFull();
        subContainer.setStyleName("importfiltersubcontainer");
        Set<String> values = new LinkedHashSet<>();
        values.add(ModificationLocalizationScore.PhosphoRS.getName());
        values.add(ModificationLocalizationScore.None.getName());

        probabilisticScore = new HorizontalLabelDropDounList("Probabilistic Score");
        probabilisticScore.updateData(values);
        values.clear();

        values.add("Yes");
        values.add("No");

        accountNeutralLosses = new HorizontalLabelDropDounList("Account Neutral Losses");
        accountNeutralLosses.updateData(values);
        threshold = new HorizontalLabelTextField("Threshold", 0.0, new DoubleRangeValidator("Only double values allowd", Double.MIN_VALUE, Double.MAX_VALUE));

        confidentSites = new HorizontalLabelDropDounList("Confident Sites");
        confidentSites.updateData(values);

        container.addComponent(title, "left:10px;top:10px");
        container.addComponent(subContainer, "left:10px;top:40px;right:10px;bottom:110px");
        subContainer.addComponent(probabilisticScore);
        subContainer.addComponent(accountNeutralLosses);
        subContainer.addComponent(threshold);

        Label title2 = new Label("Site Alignment");
        VerticalLayout subContainer2 = new VerticalLayout();
        subContainer2.setSizeFull();
        subContainer2.setStyleName("importfiltersubcontainer");
        container.addComponent(title2, "left:10px;top:150px");
        container.addComponent(subContainer2, "left:10px;top:180px;right:10px;bottom:40px");

        subContainer2.addComponent(confidentSites);

        PTMLocalizationPanel.this.setContent(container);
        PTMLocalizationPanel.this.setClosable(true);

        Button okBtn = new Button("OK");
        okBtn.setWidth(76, Unit.PIXELS);
        okBtn.setHeight(20, Unit.PIXELS);
        okBtn.setStyleName(ValoTheme.BUTTON_TINY);
        okBtn.addClickListener((Button.ClickEvent event) -> {
            setPopupVisible(false);

        });
        container.addComponent(okBtn, "bottom:10px;right:10px");
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyleName(ValoTheme.BUTTON_TINY);
        cancelBtn.setWidth(76, Unit.PIXELS);
        cancelBtn.setHeight(20, Unit.PIXELS);
        container.addComponent(cancelBtn, "bottom:10px;right:96px");
        cancelBtn.addClickListener((Button.ClickEvent event) -> {
            PTMLocalizationPanel.this.setPopupVisible(false);
        });

    }

    public void updateGUI(WebSearchParameters webSearchParameters) {
        this.webSearchParameters = webSearchParameters;
        probabilisticScore.setSelected(webSearchParameters.getUpdatedIdentificationParameters().getModificationLocalizationParameters().getSelectedProbabilisticScore().getName());

        if (webSearchParameters.getUpdatedIdentificationParameters().getModificationLocalizationParameters().isProbabilisticScoreNeutralLosses()) {
            accountNeutralLosses.setSelected("Yes");
        } else {
            accountNeutralLosses.setSelected("No");
        }

        if (webSearchParameters.getUpdatedIdentificationParameters().getModificationLocalizationParameters().getAlignNonConfidentModifications()) {
            confidentSites.setSelected("Yes");
        } else {
            confidentSites.setSelected("No");
        }

        threshold.setSelectedValue(webSearchParameters.getUpdatedIdentificationParameters().getModificationLocalizationParameters().getProbabilisticScoreThreshold());

        super.setLabelValue(VaadinIcons.COG.getHtml() + " PTM Localization" + "<center>" + webSearchParameters.getUpdatedIdentificationParameters().getModificationLocalizationParameters().getShortDescription() + "</center>");

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
            super.setLabelValue(VaadinIcons.COG.getHtml() + " PTM Localization" + "<center>" + webSearchParameters.getUpdatedIdentificationParameters().getModificationLocalizationParameters().getShortDescription() + "</center>");
        }
        super.setPopupVisible(visible); //To change body of generated methods, choose Tools | Templates.
    }

    private void updateParameters() {
        webSearchParameters.getUpdatedIdentificationParameters().getModificationLocalizationParameters().setSelectedProbabilisticScore(ModificationLocalizationScore.getScore(probabilisticScore.getSelectedValue()));
        webSearchParameters.getUpdatedIdentificationParameters().getModificationLocalizationParameters().setProbabilisticScoreNeutralLosses(accountNeutralLosses.getSelectedValue().equalsIgnoreCase("Yes"));
        webSearchParameters.getUpdatedIdentificationParameters().getModificationLocalizationParameters().setAlignNonConfidentModifications(confidentSites.getSelectedValue().equalsIgnoreCase("Yes"));
        webSearchParameters.getUpdatedIdentificationParameters().getModificationLocalizationParameters().setProbabilisticScoreThreshold(Double.valueOf(threshold.getSelectedValue()));
    }

}
