package com.uib.web.peptideshaker.galaxy.dataobjects;

import com.compomics.util.experiment.biology.Protein;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * This
 *
 * @author Yehia Farag
 */
public class ProteinObject extends Protein {

    private String accession;
    private String description;
    private String geneName;
    private String chromosome;
    private int chromosomeIndex;

    public int getChromosomeIndex() {
        return chromosomeIndex;
    }

    public void setChromosomeIndex(int chromosomeIndex) {
        this.chromosomeIndex = chromosomeIndex;
    }
    private double MW;
    private double possibleCoverage;
    private double coverage;
    private double spectrumCounting;
    private String confidentlyLocalizedModificationSites;
    private String ConfidentlyLocalizedModificationSitesNumber;
    private String ambiguouslyLocalizedModificationSites;
    private String ambiguouslyLocalizedModificationSitesNumber;
    private String proteinInference;
    private String secondaryAccessions;

    private final Set<String> secondaryAccessionSet;
    private String proteinGroup;
    private final Set<String> proteinGroupSet;
    private int validatedPeptidesNumber;
    private int peptidesNumber;
    private int uniqueNumber;
    private int validatedUniqueNumber;
    private int uniqueToGroupNumber;
    private int validatedUniqueToGroupNumber;
    private int validatedPSMsNumber;
    private int PSMsNumber;
    private double confidence;

    public String getProteinEvidence() {
        return proteinEvidence;
    }

    public void setProteinEvidence(String proteinEvidence) {
        this.proteinEvidence = proteinEvidence;
    }

    @Override
    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }
    private String validation;
    private int index;
    private String proteinEvidence;
    private String sequence;
    private final Map<String, Boolean> relatedPeptidesList;

    public ProteinObject() {
        this.secondaryAccessionSet = new LinkedHashSet<>();
        this.proteinGroupSet = new LinkedHashSet<>();
        this.relatedPeptidesList = new HashMap<>();
    }

    public Set<String> getRelatedPeptidesList() {
        return relatedPeptidesList.keySet();
    }

    public void addPeptideSequence(String peptideSequence) {
        relatedPeptidesList.put(peptideSequence, true);
    }

    public void updatePeptideType(String peptideSequence, boolean enzymatic) {
        relatedPeptidesList.put(peptideSequence, enzymatic);
    }

    public boolean isEnymaticPeptide(String peptideSequence) {
        if (relatedPeptidesList.containsKey(peptideSequence)) {
            return relatedPeptidesList.get(peptideSequence);
        } else {
            return true;
        }

    }

    public boolean isRelatedPeptide(String peptideSequence) {
        return relatedPeptidesList.containsKey(peptideSequence);
    }

    @Override
    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGeneName() {
        return geneName;
    }

    public void setGeneName(String geneName) {
        this.geneName = geneName;
    }

    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public double getMW() {
        return MW;
    }

    public void setMW(double MW) {
        this.MW = MW;
    }

    public double getPossibleCoverage() {
        return possibleCoverage;
    }

    public void setPossibleCoverage(double possibleCoverage) {
        this.possibleCoverage = possibleCoverage;
    }

    public double getCoverage() {
        return coverage;
    }

    public void setCoverage(double coverage) {
        this.coverage = coverage;
    }

    public double getSpectrumCounting() {
        return spectrumCounting;
    }

    public void setSpectrumCounting(double spectrumCounting) {
        this.spectrumCounting = spectrumCounting;
    }

    public String getConfidentlyLocalizedModificationSites() {
        return confidentlyLocalizedModificationSites;
    }

    public void setConfidentlyLocalizedModificationSites(String confidentlyLocalizedModificationSites) {
        this.confidentlyLocalizedModificationSites = confidentlyLocalizedModificationSites;
    }

    public String getConfidentlyLocalizedModificationSitesNumber() {
        return ConfidentlyLocalizedModificationSitesNumber;
    }

    public void setConfidentlyLocalizedModificationSitesNumber(String ConfidentlyLocalizedModificationSitesNumber) {
        this.ConfidentlyLocalizedModificationSitesNumber = ConfidentlyLocalizedModificationSitesNumber;
    }

    public String getAmbiguouslyLocalizedModificationSites() {
        return ambiguouslyLocalizedModificationSites;
    }

    public void setAmbiguouslyLocalizedModificationSites(String ambiguouslyLocalizedModificationSites) {
        this.ambiguouslyLocalizedModificationSites = ambiguouslyLocalizedModificationSites;
    }

    public String getAmbiguouslyLocalizedModificationSitesNumber() {
        return ambiguouslyLocalizedModificationSitesNumber;
    }

    public void setAmbiguouslyLocalizedModificationSitesNumber(String ambiguouslyLocalizedModificationSitesNumber) {
        this.ambiguouslyLocalizedModificationSitesNumber = ambiguouslyLocalizedModificationSitesNumber;
    }

    public String getProteinInference() {
        return proteinInference;
    }

    public void setProteinInference(String proteinInference) {
        this.proteinInference = proteinInference;
    }

    public String getSecondaryAccessions() {
        return secondaryAccessions;
    }

    public void setSecondaryAccessions(String secondaryAccessions) {
        this.secondaryAccessions = secondaryAccessions;
        secondaryAccessionSet.addAll(Arrays.asList(secondaryAccessions.replace(" ", "").split(",")));
    }

    public String getProteinGroup() {
        return proteinGroup;
    }

    public void setProteinGroup(String proteinGroup) {
        this.proteinGroup = proteinGroup;
        proteinGroupSet.addAll(Arrays.asList(proteinGroup.split(", ")));
    }

    public int getValidatedPeptidesNumber() {
        return validatedPeptidesNumber;
    }

    public void setValidatedPeptidesNumber(int validatedPeptidesNumber) {
        this.validatedPeptidesNumber = validatedPeptidesNumber;
    }

    public int getPeptidesNumber() {
        return peptidesNumber;
    }

    public Set<String> getSecondaryAccessionSet() {
        return secondaryAccessionSet;
    }

    public Set<String> getProteinGroupSet() {
        return proteinGroupSet;
    }

    public void setPeptidesNumber(int peptidesNumber) {
        this.peptidesNumber = peptidesNumber;
    }

    public int getUniqueNumber() {
        return uniqueNumber;
    }

    public void setUniqueNumber(int uniqueNumber) {
        this.uniqueNumber = uniqueNumber;
    }

    public int getValidatedUniqueNumber() {
        return validatedUniqueNumber;
    }

    public void setValidatedUniqueNumber(int validatedUniqueNumber) {
        this.validatedUniqueNumber = validatedUniqueNumber;
    }

    public int getUniqueToGroupNumber() {
        return uniqueToGroupNumber;
    }

    public void setUniqueToGroupNumber(int uniqueToGroupNumber) {
        this.uniqueToGroupNumber = uniqueToGroupNumber;
    }

    public int getValidatedUniqueToGroupNumber() {
        return validatedUniqueToGroupNumber;
    }

    public void setValidatedUniqueToGroupNumber(int validatedUniqueToGroupNumber) {
        this.validatedUniqueToGroupNumber = validatedUniqueToGroupNumber;
    }

    public int getValidatedPSMsNumber() {
        return validatedPSMsNumber;
    }

    public void setValidatedPSMsNumber(int validatedPSMsNumber) {
        this.validatedPSMsNumber = validatedPSMsNumber;
    }

    public int getPSMsNumber() {
        return PSMsNumber;
    }

    public void setPSMsNumber(int PSMsNumber) {
        this.PSMsNumber = PSMsNumber;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getValidation() {
        return validation;
    }

    public void setValidation(String validation) {
        this.validation = validation;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
