package com.uib.web.peptideshaker.presenter.layouts;

import com.uib.web.peptideshaker.model.core.WebSearchParameters;
import com.uib.web.peptideshaker.presenter.core.PopupWindow;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchenginessettings.MSAmandaAdvancedSettingsPanel;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchenginessettings.MyriMatchAdvancedSettingsPanel;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchenginessettings.XTandemAdvancedSettingsPanel;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings.DatabaseProcessingPanel;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings.FractionAnalysisPanel;

import com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings.GeneAnnotationPanel;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings.ImportFiltersPanel;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings.PSMScoringPanel;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings.PTMLocalizationPanel;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings.PeptideVariantsPanel;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings.ProteinInferencePanel;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings.QualityControlPanel;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings.SequanceMatchingPanel;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings.ValidationLevelsPanel;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * This class represents advanced search settings layout that allow users to
 * modify the search input parameters
 *
 * @author Yehia Farag
 */
public class AdvancedSearchEnginesSettings extends VerticalLayout {

    private final PopupWindow popupAdvancedSettingLayout;
    private WebSearchParameters webSearchParam;
    private XTandemAdvancedSettingsPanel xTandemAdvancedSettingsPanel;
    private MyriMatchAdvancedSettingsPanel myriMatchAdvancedSettingsPanel;
    private MSAmandaAdvancedSettingsPanel mSAmandaAdvancedSettingsPanel;
    private ValidationLevelsPanel validationLevel;
    private FractionAnalysisPanel fractionAnalysis;
    private ProteinInferencePanel proteinInference;
    private GeneAnnotationPanel geneAnnotation;
    private PTMLocalizationPanel ptmLocalization;
    private DatabaseProcessingPanel databaseProcessing;
    private PeptideVariantsPanel peptideVariants;
    private PSMScoringPanel psmScoring;
    private QualityControlPanel qualityControl;

    public AdvancedSearchEnginesSettings() {
        AdvancedSearchEnginesSettings.this.setSizeFull();
        AdvancedSearchEnginesSettings.this.setStyleName("advancedsearchenglayout");
//        Label title = new Label("( Advanced Settings )");
//        AdvancedSearchEnginesSettings.this.addComponent(title);
//        AdvancedSearchEnginesSettings.this.addLayoutClickListener(AdvancedSearchEnginesSettings.this);
        popupAdvancedSettingLayout = new PopupWindow(" Search Engines Settings )") {
            @Override
            public void onClosePopup() {

            }
        };
        AdvancedSearchEnginesSettings.this.addComponent(popupAdvancedSettingLayout);
        AbsoluteLayout vlo = initLayout();
        vlo.setWidth(590, Unit.PIXELS);
        vlo.setHeight(590, Unit.PIXELS);
        popupAdvancedSettingLayout.setClosable(true);
        popupAdvancedSettingLayout.setContent(vlo);

    }

    private AbsoluteLayout initLayout() {
        AbsoluteLayout container = new AbsoluteLayout();
        container.setSizeFull();
        container.setStyleName("advsettingcontainer");
        Label title = new Label("<font style='font-size:15px;'> Search Engines Settings</font>", ContentMode.HTML);
        container.addComponent(title, "left:10px;top:10px");

        VerticalLayout subContainer = new VerticalLayout();
        subContainer.setWidth(100, Unit.PERCENTAGE);
        subContainer.setHeightUndefined();
        container.addComponent(subContainer, "left:10px;top:37px;right:10px;");
        subContainer.setMargin(false);
        subContainer.setStyleName("advsettingssubcontainer");

        xTandemAdvancedSettingsPanel = new XTandemAdvancedSettingsPanel();
        subContainer.addComponent(xTandemAdvancedSettingsPanel);

        myriMatchAdvancedSettingsPanel = new MyriMatchAdvancedSettingsPanel();
        subContainer.addComponent(myriMatchAdvancedSettingsPanel);
        peptideVariants = new PeptideVariantsPanel();
        subContainer.addComponent(peptideVariants);

        mSAmandaAdvancedSettingsPanel = new MSAmandaAdvancedSettingsPanel();
        subContainer.addComponent(mSAmandaAdvancedSettingsPanel);
        psmScoring = new PSMScoringPanel();
        subContainer.addComponent(psmScoring);
        ptmLocalization = new PTMLocalizationPanel();
        subContainer.addComponent(ptmLocalization);
        geneAnnotation = new GeneAnnotationPanel();
        subContainer.addComponent(geneAnnotation);
        proteinInference = new ProteinInferencePanel();
        subContainer.addComponent(proteinInference);
        validationLevel = new ValidationLevelsPanel();
        subContainer.addComponent(validationLevel);
        fractionAnalysis = new FractionAnalysisPanel();
        subContainer.addComponent(fractionAnalysis);
        qualityControl = new QualityControlPanel();
        subContainer.addComponent(qualityControl);

        databaseProcessing = new DatabaseProcessingPanel();
        subContainer.addComponent(databaseProcessing);

        return container;
    }

    /**
     * Update search input forms based on user selection (add/edit) from search
     * files drop-down list
     *
     * @param searchParameters search parameter object from selected parameter
     */
    public void updateAdvancedSearchParamForms(WebSearchParameters searchParameters) {
        this.webSearchParam = searchParameters;
        if (webSearchParam.isNewVersionParFile()) {
            web.com.compomics.util.parameters.identification.IdentificationParameters idSearchParameter = webSearchParam.getUpdatedIdentificationParameters();
            if (idSearchParameter != null) {
                xTandemAdvancedSettingsPanel.updateGUI(searchParameters);
                myriMatchAdvancedSettingsPanel.updateGUI(searchParameters);
                mSAmandaAdvancedSettingsPanel.updateGUI(searchParameters);
                validationLevel.updateGUI(searchParameters);
                fractionAnalysis.updateGUI(searchParameters);
                proteinInference.updateGUI(searchParameters);
                geneAnnotation.updateGUI(searchParameters);
                ptmLocalization.updateGUI(searchParameters);
                databaseProcessing.updateGUI(searchParameters);
                peptideVariants.updateGUI(searchParameters);
                psmScoring.updateGUI(searchParameters);
                qualityControl.updateGUI(searchParameters);
            }
        }

    }

}
