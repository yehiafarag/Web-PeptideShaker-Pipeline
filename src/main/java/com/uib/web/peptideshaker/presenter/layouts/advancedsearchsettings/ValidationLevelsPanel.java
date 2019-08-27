package com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings;

import com.uib.web.peptideshaker.model.core.WebSearchParameters;
import com.uib.web.peptideshaker.presenter.core.PopupWindow;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabelTextField;
import com.vaadin.data.validator.DoubleRangeValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import web.com.compomics.util.parameters.identification.advanced.IdMatchValidationParameters;

/**
 *
 * @author y-mok
 */
public class ValidationLevelsPanel extends PopupWindow {

    private final HorizontalLabelTextField proteinFdr;
    private final HorizontalLabelTextField peptideFdr;
    private final HorizontalLabelTextField psmFdr;
    private WebSearchParameters webSearchParameters;

    public ValidationLevelsPanel() {
        super(VaadinIcons.COG.getHtml() + " Validation Level");
        Label title = new Label("Filters");
        AbsoluteLayout container = new AbsoluteLayout();
        container.setStyleName("popuppanelmaincontainer");
        container.setWidth(345, Unit.PIXELS);
        container.setHeight(184, Unit.PIXELS);

        VerticalLayout subContainer = new VerticalLayout();
        subContainer.setSizeFull();
        subContainer.setStyleName("importfiltersubcontainer");

        container.addComponent(title, "left:10px;top:10px");
        container.addComponent(subContainer, "left:10px;top:40px;right:10px;bottom:40px");
        proteinFdr = new HorizontalLabelTextField("Protein FDR (%)", 0.0, new DoubleRangeValidator("0 to 100 % range", 0.0, 100.0));
        peptideFdr = new HorizontalLabelTextField("Peptide FDR (%)", 0.0, new DoubleRangeValidator("0 to 100 % range", 0.0, 100.0));
        psmFdr = new HorizontalLabelTextField("PSM FDR (%)", 0.0, new DoubleRangeValidator("0 to 100 % range", 0.0, 100.0));
        subContainer.addComponent(proteinFdr);
        subContainer.addComponent(peptideFdr);
        subContainer.addComponent(psmFdr);

        ValidationLevelsPanel.this.setContent(container);
        ValidationLevelsPanel.this.setClosable(true);

        Button okBtn = new Button("OK");
        okBtn.setWidth(76, Unit.PIXELS);
        okBtn.setHeight(20, Unit.PIXELS);
        okBtn.setStyleName(ValoTheme.BUTTON_TINY);
        okBtn.addClickListener((Button.ClickEvent event) -> {
            if (proteinFdr.isValid() && peptideFdr.isValid() && psmFdr.isValid()) {
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
            ValidationLevelsPanel.this.setPopupVisible(false);
        });

    }

    public void updateGUI(WebSearchParameters webSearchParameters) {
        this.webSearchParameters = webSearchParameters;
        IdMatchValidationParameters idMatchValidationParameters = webSearchParameters.getUpdatedIdentificationParameters().getIdValidationParameters();

        proteinFdr.setSelectedValue(idMatchValidationParameters.getDefaultProteinFDR());
        peptideFdr.setSelectedValue(idMatchValidationParameters.getDefaultPeptideFDR());
        psmFdr.setSelectedValue(idMatchValidationParameters.getDefaultPsmFDR());
        super.setLabelValue(VaadinIcons.COG.getHtml() + " Validation Levels" + "<center>" + webSearchParameters.getUpdatedIdentificationParameters().getIdValidationParameters().getShortDescription() + "</center>");

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
            super.setLabelValue(VaadinIcons.COG.getHtml() + " Validation Levels" + "<center>" + webSearchParameters.getUpdatedIdentificationParameters().getIdValidationParameters().getShortDescription() + "</center>");
        }
        super.setPopupVisible(visible); //To change body of generated methods, choose Tools | Templates.
    }

    private void updateParameters() {
        IdMatchValidationParameters idMatchValidationParameters = webSearchParameters.getUpdatedIdentificationParameters().getIdValidationParameters();
        idMatchValidationParameters.setDefaultProteinFDR(idMatchValidationParameters.getDefaultProteinFDR());
        idMatchValidationParameters.setDefaultPeptideFDR(idMatchValidationParameters.getDefaultPeptideFDR());
        idMatchValidationParameters.setDefaultPsmFDR(idMatchValidationParameters.getDefaultPsmFDR());

    }

}
