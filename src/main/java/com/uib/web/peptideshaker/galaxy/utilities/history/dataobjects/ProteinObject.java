package com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects;

import com.compomics.util.experiment.biology.Protein;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class represents protein for Online PeptideShaker system the class
 * contains all the required information for visualising the proteins data
 *
 * @author Yehia Farag
 */
public class ProteinObject extends Protein {

    /**
     * UniProt accession number.
     */
    private String accession;
    private String description;
    private String geneName;
    private String chromosome;
    private int chromosomeIndex;

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
    /**
     * Protein group the is related to the main protein.
     */
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

    /**
     *
     * @return
     */
    public int getChromosomeIndex() {
        return chromosomeIndex;
    }

    /**
     *
     * @param chromosomeIndex
     */
    public void setChromosomeIndex(int chromosomeIndex) {
        this.chromosomeIndex = chromosomeIndex;
    }

    /**
     *
     * @return
     */
    public String getProteinEvidence() {
        return proteinEvidence;
    }

    /**
     *
     * @param proteinEvidence
     */
    public void setProteinEvidence(String proteinEvidence) {
        this.proteinEvidence = proteinEvidence;
    }

    @Override
    public String getSequence() {
        return sequence;
    }

    /**
     *
     * @param sequence
     */
    public void setSequence(String sequence) {
        this.sequence = sequence;
    }
    private String validation;
    private int index;
    private String proteinEvidence;
    private String sequence;
    private final Map<String, Boolean> relatedPeptidesList;

    /**
     *
     */
    public ProteinObject() {
        this.secondaryAccessionSet = new LinkedHashSet<>();
        this.proteinGroupSet = new LinkedHashSet<>();
        this.relatedPeptidesList = new HashMap<>();
    }

    /**
     *
     * @return
     */
    public Set<String> getRelatedPeptidesList() {
        return relatedPeptidesList.keySet();
    }

    /**
     *
     * @param peptideSequence
     */
    public void addPeptideSequence(String peptideSequence) {
        relatedPeptidesList.put(peptideSequence, true);
    }

    /**
     *
     * @param peptideSequence
     * @param enzymatic
     */
    public void updatePeptideType(String peptideSequence, boolean enzymatic) {
        relatedPeptidesList.put(peptideSequence, enzymatic);
    }

    /**
     *
     * @param peptideSequence
     * @return
     */
    public boolean isEnymaticPeptide(String peptideSequence) {
        if (relatedPeptidesList.containsKey(peptideSequence)) {
            return relatedPeptidesList.get(peptideSequence);
        } else {
            return true;
        }

    }

    /**
     *
     * @param peptideSequence
     * @return
     */
    public boolean isRelatedPeptide(String peptideSequence) {
        return relatedPeptidesList.containsKey(peptideSequence);
    }

    @Override
    public String getAccession() {
        return accession;
    }

    /**
     *
     * @param accession
     */
    public void setAccession(String accession) {
        this.accession = accession;
    }

    /**
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     */
    public String getGeneName() {
        return geneName;
    }

    /**
     *
     * @param geneName
     */
    public void setGeneName(String geneName) {
        this.geneName = geneName;
    }

    /**
     *
     * @return
     */
    public String getChromosome() {
        return chromosome;
    }

    /**
     *
     * @param chromosome
     */
    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    /**
     *
     * @return
     */
    public double getMW() {
        return MW;
    }

    /**
     *
     * @param MW
     */
    public void setMW(double MW) {
        this.MW = MW;
    }

    /**
     *
     * @return
     */
    public double getPossibleCoverage() {
        return possibleCoverage;
    }

    /**
     *
     * @param possibleCoverage
     */
    public void setPossibleCoverage(double possibleCoverage) {
        this.possibleCoverage = possibleCoverage;
    }

    /**
     *
     * @return
     */
    public double getCoverage() {
        return coverage;
    }

    /**
     *
     * @param coverage
     */
    public void setCoverage(double coverage) {
        this.coverage = coverage;
    }

    /**
     *
     * @return
     */
    public double getSpectrumCounting() {
        return spectrumCounting;
    }

    /**
     *
     * @param spectrumCounting
     */
    public void setSpectrumCounting(double spectrumCounting) {
        this.spectrumCounting = spectrumCounting;
    }

    /**
     *
     * @return
     */
    public String getConfidentlyLocalizedModificationSites() {
        return confidentlyLocalizedModificationSites;
    }

    /**
     *
     * @param confidentlyLocalizedModificationSites
     */
    public void setConfidentlyLocalizedModificationSites(String confidentlyLocalizedModificationSites) {
        this.confidentlyLocalizedModificationSites = confidentlyLocalizedModificationSites;
    }

    /**
     *
     * @return
     */
    public String getConfidentlyLocalizedModificationSitesNumber() {
        return ConfidentlyLocalizedModificationSitesNumber;
    }

    /**
     *
     * @param ConfidentlyLocalizedModificationSitesNumber
     */
    public void setConfidentlyLocalizedModificationSitesNumber(String ConfidentlyLocalizedModificationSitesNumber) {
        this.ConfidentlyLocalizedModificationSitesNumber = ConfidentlyLocalizedModificationSitesNumber;
    }

    /**
     *
     * @return
     */
    public String getAmbiguouslyLocalizedModificationSites() {
        return ambiguouslyLocalizedModificationSites;
    }

    /**
     *
     * @param ambiguouslyLocalizedModificationSites
     */
    public void setAmbiguouslyLocalizedModificationSites(String ambiguouslyLocalizedModificationSites) {
        this.ambiguouslyLocalizedModificationSites = ambiguouslyLocalizedModificationSites;
    }

    /**
     *
     * @return
     */
    public String getAmbiguouslyLocalizedModificationSitesNumber() {
        return ambiguouslyLocalizedModificationSitesNumber;
    }

    /**
     *
     * @param ambiguouslyLocalizedModificationSitesNumber
     */
    public void setAmbiguouslyLocalizedModificationSitesNumber(String ambiguouslyLocalizedModificationSitesNumber) {
        this.ambiguouslyLocalizedModificationSitesNumber = ambiguouslyLocalizedModificationSitesNumber;
    }

    /**
     *
     * @return
     */
    public String getProteinInference() {
        return proteinInference;
    }

    /**
     *
     * @param proteinInference
     */
    public void setProteinInference(String proteinInference) {
        this.proteinInference = proteinInference;
    }

    /**
     *
     * @return
     */
    public String getSecondaryAccessions() {
        return secondaryAccessions;
    }

    /**
     *
     * @param secondaryAccessions
     */
    public void setSecondaryAccessions(String secondaryAccessions) {
        this.secondaryAccessions = secondaryAccessions;
        secondaryAccessionSet.addAll(Arrays.asList(secondaryAccessions.replace(" ", "").split(",")));
    }

    /**
     *
     * @return
     */
    public String getProteinGroup() {
        return proteinGroup;
    }

    /**
     *
     * @param proteinGroup
     */
    public void setProteinGroup(String proteinGroup) {
        this.proteinGroup = proteinGroup;
        proteinGroupSet.addAll(Arrays.asList(proteinGroup.split(", ")));
    }

    /**
     *
     * @return
     */
    public int getValidatedPeptidesNumber() {
        return validatedPeptidesNumber;
    }

    /**
     *
     * @param validatedPeptidesNumber
     */
    public void setValidatedPeptidesNumber(int validatedPeptidesNumber) {
        this.validatedPeptidesNumber = validatedPeptidesNumber;
    }

    /**
     *
     * @return
     */
    public int getPeptidesNumber() {
        return peptidesNumber;
    }

    /**
     *
     * @return
     */
    public Set<String> getSecondaryAccessionSet() {
        return secondaryAccessionSet;
    }

    /**
     *
     * @return
     */
    public Set<String> getProteinGroupSet() {
        return proteinGroupSet;
    }

    /**
     *
     * @param peptidesNumber
     */
    public void setPeptidesNumber(int peptidesNumber) {
        this.peptidesNumber = peptidesNumber;
    }

    /**
     *
     * @return
     */
    public int getUniqueNumber() {
        return uniqueNumber;
    }

    /**
     *
     * @param uniqueNumber
     */
    public void setUniqueNumber(int uniqueNumber) {
        this.uniqueNumber = uniqueNumber;
    }

    /**
     *
     * @return
     */
    public int getValidatedUniqueNumber() {
        return validatedUniqueNumber;
    }

    /**
     *
     * @param validatedUniqueNumber
     */
    public void setValidatedUniqueNumber(int validatedUniqueNumber) {
        this.validatedUniqueNumber = validatedUniqueNumber;
    }

    /**
     *
     * @return
     */
    public int getUniqueToGroupNumber() {
        return uniqueToGroupNumber;
    }

    /**
     *
     * @param uniqueToGroupNumber
     */
    public void setUniqueToGroupNumber(int uniqueToGroupNumber) {
        this.uniqueToGroupNumber = uniqueToGroupNumber;
    }

    /**
     *
     * @return
     */
    public int getValidatedUniqueToGroupNumber() {
        return validatedUniqueToGroupNumber;
    }

    /**
     *
     * @param validatedUniqueToGroupNumber
     */
    public void setValidatedUniqueToGroupNumber(int validatedUniqueToGroupNumber) {
        this.validatedUniqueToGroupNumber = validatedUniqueToGroupNumber;
    }

    /**
     *
     * @return
     */
    public int getValidatedPSMsNumber() {
        return validatedPSMsNumber;
    }

    /**
     *
     * @param validatedPSMsNumber
     */
    public void setValidatedPSMsNumber(int validatedPSMsNumber) {
        this.validatedPSMsNumber = validatedPSMsNumber;
    }

    /**
     *
     * @return
     */
    public int getPSMsNumber() {
        return PSMsNumber;
    }

    /**
     *
     * @param PSMsNumber
     */
    public void setPSMsNumber(int PSMsNumber) {
        this.PSMsNumber = PSMsNumber;
    }

    /**
     *
     * @return
     */
    public double getConfidence() {
        return confidence;
    }

    /**
     *
     * @param confidence
     */
    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    /**
     *
     * @return
     */
    public String getValidation() {
        return validation;
    }

    /**
     *
     * @param validation
     */
    public void setValidation(String validation) {
        this.validation = validation;
    }

    /**
     *
     * @return
     */
    public int getIndex() {
        return index;
    }

    /**
     *
     * @param index
     */
    public void setIndex(int index) {
        this.index = index;
    }

}
