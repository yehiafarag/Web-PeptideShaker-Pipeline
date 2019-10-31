package com.uib.web.peptideshaker.model.core;
//
//;
//import com.compomics.util.experiment.biology.Enzyme;
//import com.compomics.util.experiment.biology.EnzymeFactory;
//import com.compomics.util.experiment.biology.Ion;
//import com.compomics.util.experiment.biology.IonFactory;
//import com.compomics.util.experiment.biology.NeutralLoss;
//import com.compomics.util.experiment.biology.PTMFactory;
//import com.compomics.util.experiment.biology.ions.ReporterIon;
//import com.compomics.util.experiment.identification.identification_parameters.PtmSettings;
//import com.compomics.util.experiment.identification.identification_parameters.SearchParameters;
//import com.compomics.util.experiment.identification.identification_parameters.SearchParameters.MassAccuracyType;
//import com.compomics.util.experiment.identification.protein_inference.PeptideMapperType;
//import com.compomics.util.experiment.identification.ptm.PtmScore;
//import com.compomics.util.experiment.identification.spectrum_annotation.AnnotationSettings;
//import com.compomics.util.experiment.massspectrometry.Charge;
//import com.compomics.util.preferences.DigestionPreferences;
//import com.compomics.util.preferences.IdentificationParameters;
//import com.compomics.util.preferences.PTMScoringPreferences;
//import com.compomics.util.preferences.SequenceMatchingPreferences;
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import web.com.uib.probe.searchparameterwrlight.UpdatedSearchParameterFileUtility;
//
///**
// * This class represents parameters file reader that support old and new
// * searching parameter files
// *
// * @author Yehia Farag
// */


public class WebSearchParametersTempRemove {
//
//    private IdentificationParameters identificationParameters;
//    private final UpdatedSearchParameterFileUtility updatedIdentificationParameters = new UpdatedSearchParameterFileUtility();
//    private boolean newVersionParFile;
//    private boolean create_decoy;
//    private String galaxyId;
//
//    public String getGalaxyId() {
//        return galaxyId;
//    }
//
//    public void setGalaxyId(String galaxyId) {
//        this.galaxyId = galaxyId;
//    }
//
//    public boolean isCreate_decoy() {
//        return create_decoy;
//    }
//
//    public void setCreate_decoy(boolean create_decoy) {
//        this.create_decoy = create_decoy;
//    }
//
//    public String getParamFileName() {
//        return paramFileName;
//    }
//
//    public void setParamFileName(String paramFileName) {
//        this.paramFileName = paramFileName;
//    }
//    private String paramFileName;
//
//    public boolean isNewVersionParFile() {
//        return newVersionParFile;
//    }
//
//    public IdentificationParameters getIdentificationParameters() {
//        return identificationParameters;
//    }
//
//    public web.com.compomics.util.parameters.identification.IdentificationParameters getUpdatedIdentificationParameters() {
//        return updatedIdentificationParameters.getIdentificationParameters();
//    }
//
//    public WebSearchParameters(File paramFile) {
//        try {
//            initFromNewSearchingParameterFile(paramFile);
//        } catch (Exception ex) {
//            initFromOldSearchingParameterFile(paramFile);
//        }
//    }
//
//    public web.com.compomics.util.parameters.identification.search.SearchParameters getUpdatedSearchParameter() {
//
//        return updatedIdentificationParameters.getSearchParameters();
//    }
//
//    private void initFromNewSearchingParameterFile(File paramFile) throws Exception {
//
//        updatedIdentificationParameters.initIdentificationParameters(paramFile);
//        newVersionParFile = true;
//    }
//
//    private void initFromOldSearchingParameterFile(File paramFile) {
//        try {
//
//            SearchParameters searchParam = SearchParameters.getIdentificationParameters(paramFile);
//            searchParam.setFastaFile(null);
//            identificationParameters = new IdentificationParameters(searchParam);
//            newVersionParFile = false;
//        } catch (IOException | ClassNotFoundException ex) {
//            ex.printStackTrace();
//        }
//
//    }
//
//    public ArrayList<String> getFixedModifications() {
//        if (newVersionParFile) {
//            return updatedIdentificationParameters.getFixedModifications();
//        } else {
//            return identificationParameters.getSearchParameters().getPtmSettings().getFixedModifications();
//        }
//
//    }
//
//    public ArrayList<String> getVariableModifications() {
//        if (newVersionParFile) {
//            return updatedIdentificationParameters.geteVariableModifications();
//        } else {
//            return identificationParameters.getSearchParameters().getPtmSettings().getVariableModifications();
//        }
//    }
//
//    public double getFragmentIonAccuracy() {
//        if (newVersionParFile) {
//            return updatedIdentificationParameters.getFragmentIonAccuracy();
//        } else {
//            return identificationParameters.getSearchParameters().getFragmentIonAccuracy();
//        }
//
//    }
//
//    public int getCleavagePreferenceOrdinal() {
//        if (newVersionParFile) {
//            return updatedIdentificationParameters.getCleavagePreferenceOrdinal();
//        } else {
//            return identificationParameters.getSearchParameters().getDigestionPreferences().getCleavagePreference().ordinal();
//        }
//
//    }
//
//    public String getCleavagePreferenceName() {
//        if (newVersionParFile) {
//            return updatedIdentificationParameters.getCleavagePreferenceName();
//        } else {
//            return identificationParameters.getSearchParameters().getDigestionPreferences().getCleavagePreference().name();
//        }
//        //
//
//    }
//
//    public String getEnzymeName() {
//        if (newVersionParFile) {
//            return updatedIdentificationParameters.getEnzymes().get(0);
//        } else {
//            return identificationParameters.getSearchParameters().getDigestionPreferences().getEnzymes().get(0).getName();
//        }
//
//    }
//
//    public int getnMissedCleavages(String enzymeName) {
//        if (newVersionParFile) {
//            return updatedIdentificationParameters.getMissedCleavages(enzymeName);
//        } else {
//            return identificationParameters.getSearchParameters().getDigestionPreferences().getnMissedCleavages(enzymeName);
//        }
//
//    }
//
//    public List<Integer> getForwardIons() {
//        if (newVersionParFile) {
//            return updatedIdentificationParameters.getForwardIons();
//        } else {
//            return identificationParameters.getSearchParameters().getForwardIons();
//        }
//
//    }
//
//    public List<Integer> getRewindIons() {
//        if (newVersionParFile) {
//            return updatedIdentificationParameters.getRewindIons();
//        } else {
//            return identificationParameters.getSearchParameters().getRewindIons();
//        }
//
//    }
//
//    public String getPrecursorIon() {
//        if (newVersionParFile) {
//            return updatedIdentificationParameters.getPrecursorIon();
//        } else {
//            return identificationParameters.getSearchParameters().getPrecursorAccuracy() + " " + (identificationParameters.getSearchParameters().getPrecursorAccuracyType().ordinal() + "").replace("0", "ppm").replace("1", "Da");
//        }
//
//    }
//
//    public String getFragmentIonAccuracyInDaltons() {
//        if (newVersionParFile) {
//            return updatedIdentificationParameters.getFragmentAccuracyType();
//        } else {
//            return identificationParameters.getSearchParameters().getFragmentIonAccuracyInDaltons() + " Da";
//        }
//
//    }
//
//    public int getMinChargeSearched() {
//        if (newVersionParFile) {
//            return updatedIdentificationParameters.getMinChargeSearched();
//        } else {
//            return identificationParameters.getSearchParameters().getMinChargeSearched().value;
//
//        }
//    }
//
//    public int getMaxChargeSearched() {
//        if (newVersionParFile) {
//            return updatedIdentificationParameters.getMaxChargeSearched();
//        } else {
//            return identificationParameters.getSearchParameters().getMaxChargeSearched().value;
//        }
//
//    }
//
//    public int getMinIsotopicCorrection() {
//        if (newVersionParFile) {
//            return updatedIdentificationParameters.getMinIsotopicCorrection();
//        } else {
//            return identificationParameters.getSearchParameters().getMinIsotopicCorrection();
//
//        }
//    }
//
//    public int getMaxIsotopicCorrection() {
//        if (newVersionParFile) {
//            return updatedIdentificationParameters.getMaxIsotopicCorrection();
//        } else {
//            return identificationParameters.getSearchParameters().getMaxIsotopicCorrection();
//
//        }
//    }
//
//    public Object getDigestionPreferences() {
//        if (newVersionParFile) {
//            return updatedIdentificationParameters.getDigestionPreferences();
//        } else {
//            return identificationParameters.getSearchParameters().getDigestionPreferences();
//        }
//
//    }
//
//    public String getCleavagePreferenceAsString() {
//        if (newVersionParFile) {
//            return updatedIdentificationParameters.getCleavagePreference().toString();
//        } else {
//            return identificationParameters.getSearchParameters().getDigestionPreferences().getCleavagePreference().toString();
//        }
//    }
//
//    public String getSpecificity(String enzymeName) {
//        if (newVersionParFile) {
//            return updatedIdentificationParameters.getSpecificity(enzymeName);
//        } else {
//            return identificationParameters.getSearchParameters().getDigestionPreferences().getSpecificity(enzymeName).name();
//        }
//
//    }
//
//    public String getPrecursorAccuracy() {
//        if (newVersionParFile) {
//            return updatedIdentificationParameters.getPrecursorAccuracy().toString();
//        } else {
//            return identificationParameters.getSearchParameters().getPrecursorAccuracy().toString();
//        }
//
//    }
//
//    public MassAccuracyType getFragmentAccuracyType() {
//        if (newVersionParFile) {
//            return SearchParameters.MassAccuracyType.valueOf(updatedIdentificationParameters.getFragmentAccuracyType());
//        } else {
//            return identificationParameters.getSearchParameters().getFragmentAccuracyType();
//
//        }
//    }
//
//    public String getPrecursorAccuracyType() {
//        if (newVersionParFile) {
//            return "";
//        } else {
//            return identificationParameters.getSearchParameters().getPrecursorAccuracyType().toString();
//
//        }
//    }
//    PtmSettings ptmSettings = new PtmSettings();
//    PTMFactory PTM = PTMFactory.getInstance();
//    EnzymeFactory enzymeFactory = EnzymeFactory.getInstance();
//    DigestionPreferences digPref = new DigestionPreferences();
//
//    public void addFixedModification(String modificationId) {
//        if (newVersionParFile) {
//            updatedIdentificationParameters.addFixedModification(modificationId);
//        } else {
//            ptmSettings.addFixedModification(PTM.getPTM(modificationId));
//            identificationParameters.getSearchParameters().setPtmSettings(ptmSettings);
//
//        }
//
//    }
//
//    public void addVariableModification(String modificationId) {
//        if (newVersionParFile) {
//            updatedIdentificationParameters.addVariableModification(modificationId);
//
//        } else {
//            ptmSettings.addVariableModification(PTM.getPTM(modificationId));
//            identificationParameters.getSearchParameters().setPtmSettings(ptmSettings);
//        }
//
//    }
//
//    public void setEnzymes(ArrayList<String> enzymesName) {
//        if (newVersionParFile) {
//            updatedIdentificationParameters.setEnzymes(enzymesName);
//        } else {
//            ArrayList<Enzyme> enzymes = new ArrayList<>();
//            enzymesName.forEach((str) -> {
//                enzymes.add(enzymeFactory.getEnzyme(str));
//            });
//            digPref.setEnzymes(enzymes);
//            identificationParameters.getSearchParameters().setDigestionPreferences(digPref);
//        }
//
//    }
//
//    public void setSpecificity(String enzymeName, String enzymeSpecificityName) {
//        if (newVersionParFile) {
//            updatedIdentificationParameters.setSpecificity(enzymeName, enzymeSpecificityName);
//        } else {
//            digPref.setSpecificity(enzymeName, DigestionPreferences.Specificity.valueOf(enzymeSpecificityName.toLowerCase()));
//            identificationParameters.getSearchParameters().setDigestionPreferences(digPref);
//        }
//
//    }
//
//    public void setnMissedCleavages(String enzymeName, int enzymeMissedCleavages) {
//        if (newVersionParFile) {
//            updatedIdentificationParameters.setMissedCleavages(enzymeName, enzymeMissedCleavages);
//        } else {
//            digPref.setnMissedCleavages(enzymeName, enzymeMissedCleavages);
//            identificationParameters.getSearchParameters().setDigestionPreferences(digPref);
//
//        }
//    }
//
//    /**
//     * Sets the cleavage preferences.
//     *
//     * @param cleavagePreferenceString the cleavage preferences
//     */
//    public void setCleavagePreference(String cleavagePreferenceString) {
//        if (newVersionParFile) {
//            updatedIdentificationParameters.setCleavagePreference(cleavagePreferenceString);
//        } else {
//            digPref.setCleavagePreference(DigestionPreferences.CleavagePreference.valueOf(cleavagePreferenceString.toLowerCase().replace("uns", "unS").replace("le p", "leP")));
//            identificationParameters.getSearchParameters().setDigestionPreferences(digPref);
//        }
//    }
//
//    public void setRewindIons(ArrayList<Integer> rewindIonsv) {
//        if (newVersionParFile) {
//            updatedIdentificationParameters.setRewindIons(rewindIonsv);
//        } else {
//            identificationParameters.getSearchParameters().setRewindIons(rewindIonsv);
//        }
//    }
//
//    public void setForwardIons(ArrayList<Integer> forwardIonsv) {
//        if (newVersionParFile) {
//            updatedIdentificationParameters.setForwardIons(forwardIonsv);
//        } else {
//            identificationParameters.getSearchParameters().setForwardIons(forwardIonsv);
//        }
//    }
//
//    public void setPrecursorAccuracy(double precursorToleranceValue) {
//        if (newVersionParFile) {
//            updatedIdentificationParameters.setPrecursorAccuracy(precursorToleranceValue);
//        } else {
//            identificationParameters.getSearchParameters().setPrecursorAccuracy(precursorToleranceValue);
//        }
//    }
//
//    public void setPrecursorAccuracyType(String precursorToleranceType) {
//        if (newVersionParFile) {
//            updatedIdentificationParameters.setPrecursorAccuracyType(precursorToleranceType);
//        } else {
//            identificationParameters.getSearchParameters().setPrecursorAccuracyType(SearchParameters.MassAccuracyType.valueOf(precursorToleranceType.toUpperCase()));
//        }
//    }
//
//    public void setFragmentIonAccuracy(double fragmentIonAccuracyValue) {
//        if (newVersionParFile) {
//            updatedIdentificationParameters.setFragmentIonAccuracy(fragmentIonAccuracyValue);
//        } else {
//            identificationParameters.getSearchParameters().setFragmentIonAccuracy((fragmentIonAccuracyValue));
//        }
//    }
//
//    public void setFragmentAccuracyType(String fragmentAccuracyType) {
//        if (newVersionParFile) {
//            updatedIdentificationParameters.setFragmentAccuracyType(fragmentAccuracyType);
//        } else {
//            identificationParameters.getSearchParameters().setFragmentAccuracyType(SearchParameters.MassAccuracyType.valueOf(fragmentAccuracyType.toUpperCase()));
//        }
//    }
//
//    public void setMinChargeSearched(int charge) {
//        if (newVersionParFile) {
//            updatedIdentificationParameters.setMinChargeSearched(charge);
//        } else {
//            identificationParameters.getSearchParameters().setMinChargeSearched(new Charge(Charge.PLUS, charge));
//        }
//
//    }
//
//    public void setMaxChargeSearched(int charge) {
//        if (newVersionParFile) {
//            updatedIdentificationParameters.setMaxChargeSearched(charge);
//        } else {
//            identificationParameters.getSearchParameters().setMaxChargeSearched(new Charge(Charge.PLUS, charge));
//        }
//
//    }
//
//    public void setMinIsotopicCorrection(int correction) {
//        if (newVersionParFile) {
//            updatedIdentificationParameters.setMinIsotopicCorrection(correction);
//        } else {
//            identificationParameters.getSearchParameters().setMinIsotopicCorrection(correction);
//        }
//
//    }
//
//    public void setMaxIsotopicCorrection(int correction) {
//        if (newVersionParFile) {
//            updatedIdentificationParameters.setMaxIsotopicCorrection(correction);
//        } else {
//            identificationParameters.getSearchParameters().setMaxIsotopicCorrection(correction);
//        }
//    }
//
//    public String getShortDescription() {
//        if (newVersionParFile) {
//            return updatedIdentificationParameters.getShortDescription();
//        } else {
//            return identificationParameters.getSearchParameters().getShortDescription();
//        }
//    }
//
//    public void saveIdentificationParameters(WebSearchParameters searchParameters, File paramFile) {
//        if (newVersionParFile) {
//            updatedIdentificationParameters.saveIdentificationParameters(paramFile);
//        } else {
//            try {
//                SearchParameters.saveIdentificationParameters(searchParameters.getIdentificationParameters().getSearchParameters(), paramFile);
//            } catch (IOException ex) {
//                Logger.getLogger(WebSearchParameters.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//    }
//
//    /**
//     * Updates the annotation parameters based on search parameters.
//     *
//     * @param searchParameters the search parameters where to take the
//     * information from
//     * @return
//     */
//    public AnnotationSettings getPreferencesFromSearchParameters(UpdatedSearchParameterFileUtility searchParameters) {
//        AnnotationSettings oldAnnotationSettings = new AnnotationSettings();
//        oldAnnotationSettings.clearIonTypes();
//        searchParameters.getForwardIons().stream().map((ion) -> {
//            oldAnnotationSettings.addIonType(Ion.IonType.PEPTIDE_FRAGMENT_ION, ion);
//            return ion;
//        }).forEachOrdered((ion) -> {
//            oldAnnotationSettings.addIonType(Ion.IonType.TAG_FRAGMENT_ION, ion);
//        });
//        searchParameters.getRewindIons().stream().map((ion) -> {
//            oldAnnotationSettings.addIonType(Ion.IonType.PEPTIDE_FRAGMENT_ION, ion);
//            return ion;
//        }).forEachOrdered((ion) -> {
//            oldAnnotationSettings.addIonType(Ion.IonType.TAG_FRAGMENT_ION, ion);
//        });
//        oldAnnotationSettings.addIonType(Ion.IonType.PRECURSOR_ION);
//        oldAnnotationSettings.addIonType(Ion.IonType.IMMONIUM_ION);
//        oldAnnotationSettings.addIonType(Ion.IonType.REPORTER_ION);
//        oldAnnotationSettings.addIonType(Ion.IonType.RELATED_ION);
//        oldAnnotationSettings.setFragmentIonAccuracy(searchParameters.getFragmentIonAccuracy());
//        oldAnnotationSettings.setFragmentIonPpm(SearchParameters.MassAccuracyType.valueOf(searchParameters.getFragmentAccuracyType()) == SearchParameters.MassAccuracyType.PPM);
//        ptmSettings = new PtmSettings();
//        searchParameters.getFixedModifications().forEach((fixedMod) -> {
//            ptmSettings.addFixedModification(PTM.getPTM(fixedMod));
//        });
//        searchParameters.geteVariableModifications().forEach((varMod) -> {
//            ptmSettings.addVariableModification(PTM.getPTM(varMod));
//        });
//        if (oldAnnotationSettings.getReporterIons()) {
//            HashSet<Integer> ptmReporterIons = IonFactory.getReporterIons(ptmSettings);
//            oldAnnotationSettings.getIonTypes().put(ReporterIon.IonType.REPORTER_ION, ptmReporterIons);
//        }
//        if (oldAnnotationSettings.isAutomaticAnnotation() || oldAnnotationSettings.areNeutralLossesSequenceAuto()) {
//            ArrayList<NeutralLoss> neutralLosses = IonFactory.getNeutralLosses(ptmSettings);
//            neutralLosses.forEach((neutralLoss) -> {
//                oldAnnotationSettings.addNeutralLoss(neutralLoss);
//            });
//        }
//        return oldAnnotationSettings;
//    }
//
//    private AnnotationSettings annotationSettings;
//
//    public AnnotationSettings getAnnotationPreferences() {
//        if (newVersionParFile) {
//            annotationSettings = getPreferencesFromSearchParameters(updatedIdentificationParameters);
//            return annotationSettings;
//        } else {
//            return identificationParameters.getAnnotationPreferences();
//        }
//    }
//
//    public void setAnnotationSettings(AnnotationSettings annotationPreferences) {
//        if (newVersionParFile) {
//            annotationSettings = annotationPreferences;
//        } else {
//            identificationParameters.setAnnotationSettings(annotationPreferences);
//        }
//
//    }
//    private SequenceMatchingPreferences sequenceMatchingPreferences;
//
//    public SequenceMatchingPreferences getSequenceMatchingPreferences() {
//        if (newVersionParFile) {
//            if (sequenceMatchingPreferences == null) {
//                sequenceMatchingPreferences = new SequenceMatchingPreferences();
//                sequenceMatchingPreferences.setLimitX(updatedIdentificationParameters.getSequenceMatchingPreferences().getLimitX());
//                sequenceMatchingPreferences.setSequenceMatchingType(SequenceMatchingPreferences.MatchingType.valueOf(updatedIdentificationParameters.getSequenceMatchingPreferences().getSequenceMatchingType().name()));
//                System.out.println("at updated matching type ---- " + updatedIdentificationParameters.getSequenceMatchingPreferences().getShortDescription());
//                sequenceMatchingPreferences.setPeptideMapperType(PeptideMapperType.tree);
//            }
//            return sequenceMatchingPreferences;
//        } else {
//            return identificationParameters.getSequenceMatchingPreferences();
//        }
//    }
//
//    private PTMScoringPreferences ptmScoringPreferences;
//
//    public SequenceMatchingPreferences getPtmScoringPreferencesSequenceMatchingPreferences() {
//        if (newVersionParFile) {
//            if (ptmScoringPreferences == null) {
//                ptmScoringPreferences = new PTMScoringPreferences();
//                ptmScoringPreferences.setAlignNonConfidentPTMs(updatedIdentificationParameters.getPtmScoringPreferences().getAlignNonConfidentModifications());
//                ptmScoringPreferences.setProbabilisticScoreNeutralLosses(updatedIdentificationParameters.getPtmScoringPreferences().isProbabilisticScoreNeutralLosses());
//                ptmScoringPreferences.setProbabilisticScoreThreshold(updatedIdentificationParameters.getPtmScoringPreferences().getProbabilisticScoreThreshold());
//                ptmScoringPreferences.setProbabilitsticScoreCalculation(updatedIdentificationParameters.getPtmScoringPreferences().isProbabilisticScoreCalculation());
//                ptmScoringPreferences.setSelectedProbabilisticScore(PtmScore.valueOf(updatedIdentificationParameters.getPtmScoringPreferences().getSelectedProbabilisticScore().getName()));
//
//                SequenceMatchingPreferences PTMSequenceMatchingPreferences = new SequenceMatchingPreferences();
//                PTMSequenceMatchingPreferences.setLimitX(updatedIdentificationParameters.getPtmScoringPreferences().getSequenceMatchingParameters().getLimitX());
//                PTMSequenceMatchingPreferences.setSequenceMatchingType(SequenceMatchingPreferences.MatchingType.valueOf(updatedIdentificationParameters.getPtmScoringPreferences().getSequenceMatchingParameters().getSequenceMatchingType().name()));
//                PTMSequenceMatchingPreferences.setPeptideMapperType(PeptideMapperType.tree);
//                ptmScoringPreferences.setSequenceMatchingPreferences(PTMSequenceMatchingPreferences);
//
//            }
//
//            return ptmScoringPreferences.getSequenceMatchingPreferences();
//        } else {
//            return identificationParameters.getPtmScoringPreferences().getSequenceMatchingPreferences();
//        }
//
//    }
//
//    public PtmSettings getPtmSettings() {
//        if (newVersionParFile) {
//            if (ptmSettings == null) {
//                annotationSettings = getPreferencesFromSearchParameters(updatedIdentificationParameters);
//            }
//            return ptmSettings;
//        } else {
//            return identificationParameters.getSearchParameters().getPtmSettings();
//        }
//
//    }
//
//    public void resetSearchingParameters() {
//        if (updatedIdentificationParameters != null) {
//            updatedIdentificationParameters.resetSearchingParameters();
//        };
//
//    }

}
