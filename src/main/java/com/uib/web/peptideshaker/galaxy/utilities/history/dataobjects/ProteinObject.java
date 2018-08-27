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
    /**
     * Protein short name.
     */
    private String description;
    /**
     * Gene name.
     */
    private String geneName;
    /**
     * Chromosome name.
     */
    private String chromosome;
    /**
     * Chromosome index.
     */
    private int chromosomeIndex;
    /**
     * Molecular weight.
     */
    private double MW;
    /**
     * Possible protein coverage.
     */
    private double possibleCoverage;
    /**
     * Protein coverage.
     */
    private double coverage;
    /**
     * Number of spectrum.
     */
    private double spectrumCounting;
    /**
     * Confidently localised modification sites.
     */
    private String confidentlyLocalizedModificationSites;
    /**
     * Number of confidently localised modification sites.
     */
    private String ConfidentlyLocalizedModificationSitesNumber;
    /**
     * Ambiguous localised modification sites.
     */
    private String ambiguouslyLocalizedModificationSites;
    /**
     * Number of ambiguous localised modification sites.
     */
    private String ambiguouslyLocalizedModificationSitesNumber;
    /**
     * Protein inference type.
     */
    private String proteinInference;
    /**
     * Secondary accessions related to the main accession protein.
     */
    private String secondaryAccessions;
    /**
     * Set of secondary accessions related to the main accession protein.
     */
    private final Set<String> secondaryAccessionSet;
    /**
     * Protein group that is related to the main protein.
     */
    private String proteinGroup;
    /**
     * Protein group accessions set.
     */
    private final Set<String> proteinGroupSet;
    /**
     * Number of validated peptides.
     */
    private int validatedPeptidesNumber;
    /**
     * Total number of peptides.
     */
    private int peptidesNumber;
    /**
     * Unique number of peptides to the protein.
     */
    private int uniqueNumber;
    /**
     * Unique number of validated peptides to the protein.
     */
    private int validatedUniqueNumber;
    /**
     * Unique number of peptides to the protein group.
     */
    private int uniqueToGroupNumber;
    /**
     * Unique number of validated peptides to the protein group.
     */
    private int validatedUniqueToGroupNumber;
    /**
     * Number of validated PSMs.
     */
    private int validatedPSMsNumber;
    /**
     * Number of PSMs.
     */
    private int PSMsNumber;
    /**
     * Protein confident value in percentage.
     */
    private double confidence;
    /**
     * Validation value.
     */
    private String validation;
    /**
     * Protein index from the exported PeptideShaker file.
     */
    private int index;
    /**
     * Protein evidence value.
     */
    private String proteinEvidence;
    /**
     * Protein sequence.
     */
    private String sequence;
    /**
     * List of peptides related to protein group.
     */
    private final Map<String, Boolean> relatedPeptidesList;

    /**
     * Get chromosome index
     *
     * @return chromosome index
     */
    public int getChromosomeIndex() {
        return chromosomeIndex;
    }

    /**
     * Set chromosome index
     *
     * @param chromosomeIndex chromosome number
     */
    public void setChromosomeIndex(int chromosomeIndex) {
        this.chromosomeIndex = chromosomeIndex;
    }

    /**
     * Get protein evidence value.
     *
     * @return Protein evidence value.
     */
    public String getProteinEvidence() {
        return proteinEvidence;
    }

    /**
     * Set protein evidence value.
     *
     * @param proteinEvidence Protein evidence value.
     */
    public void setProteinEvidence(String proteinEvidence) {
        this.proteinEvidence = proteinEvidence;
    }

    @Override
    public String getSequence() {
        return sequence;
    }

    /**
     * Set protein sequence.
     *
     * @param sequence Standard protein sequence.
     */
    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    /**
     * Constructor to initialise the main data structure.
     */
    public ProteinObject() {
        this.secondaryAccessionSet = new LinkedHashSet<>();
        this.proteinGroupSet = new LinkedHashSet<>();
        this.relatedPeptidesList = new HashMap<>();
    }

    /**
     * Get set of peptides related to protein group.
     *
     * @return set of peptides keys (modified sequence)
     */
    public Set<String> getRelatedPeptidesList() {
        return relatedPeptidesList.keySet();
    }

    /**
     * Add peptide
     *
     * @param peptideKey peptides keys (modified sequence)
     */
    public void addPeptideSequence(String peptideKey) {
        relatedPeptidesList.put(peptideKey, true);
    }

    /**
     * Update peptide type
     *
     * @param peptideKey peptides keys (modified sequence)
     * @param enzymatic enzymatic peptide
     */
    public void updatePeptideType(String peptideKey, boolean enzymatic) {
        relatedPeptidesList.put(peptideKey, enzymatic);
    }

    /**
     * Check if the peptide is enzymatic
     *
     * @param peptideKey peptide key (modified sequence)
     * @return is enzymatic peptide
     */
    public boolean isEnymaticPeptide(String peptideKey) {
        if (relatedPeptidesList.containsKey(peptideKey)) {
            return relatedPeptidesList.get(peptideKey);
        } else {
            return true;
        }

    }

    /**
     * Check if the peptide is related to protein
     *
     * @param peptideKey peptide key (modified sequence)
     * @return is related peptide
     */
    public boolean isRelatedPeptide(String peptideKey) {
        return relatedPeptidesList.containsKey(peptideKey);
    }

    /**
     * Get the main protein accession
     *
     * @return accession UniProt protein accession
     */
    @Override
    public String getAccession() {
        return accession;
    }

    /**
     * Set the main protein accession
     *
     * @param accession UniProt protein accession
     */
    public void setAccession(String accession) {
        this.accession = accession;
    }

    /**
     * Get protein short name.
     *
     * @return protein name
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set protein short name.
     *
     * @param description protein short name.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get gene name
     *
     * @return gene name
     */
    public String getGeneName() {
        return geneName;
    }

    /**
     * Set gene name
     *
     * @param geneName gene name
     */
    public void setGeneName(String geneName) {
        this.geneName = geneName;
    }

    /**
     * Get chromosome name
     *
     * @return chromosome name
     */
    public String getChromosome() {
        return chromosome;
    }

    /**
     * set chromosome name
     *
     * @param chromosome chromosome name
     */
    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    /**
     * Get molecular weight
     *
     * @return double value
     */
    public double getMW() {
        return MW;
    }

    /**
     * Set molecular weight
     *
     * @param MW double value
     */
    public void setMW(double MW) {
        this.MW = MW;
    }

    /**
     * Get possible protein coverage
     *
     * @return possible protein coverage
     */
    public double getPossibleCoverage() {
        return possibleCoverage;
    }

    /**
     * Set possible protein coverage
     *
     * @param possibleCoverage possible protein coverage
     */
    public void setPossibleCoverage(double possibleCoverage) {
        this.possibleCoverage = possibleCoverage;
    }

    /**
     * Get protein coverage
     *
     * @return protein coverage
     */
    public double getCoverage() {
        return coverage;
    }

    /**
     * Set protein coverage
     *
     * @param coverage protein coverage
     */
    public void setCoverage(double coverage) {
        this.coverage = coverage;
    }

    /**
     * Get number of spectrum
     *
     * @return Number of spectrum
     */
    public double getSpectrumCounting() {
        return spectrumCounting;
    }

    /**
     * Set number of spectrum
     *
     * @param spectrumCounting number of spectrum
     */
    public void setSpectrumCounting(double spectrumCounting) {
        this.spectrumCounting = spectrumCounting;
    }

    /**
     * Get confidently localised modification sites
     *
     * @return Confidently localised modification sites
     */
    public String getConfidentlyLocalizedModificationSites() {
        return confidentlyLocalizedModificationSites;
    }

    /**
     * Set confidently localised modification sites
     *
     * @param confidentlyLocalizedModificationSites confidently localised
     * modification sites
     */
    public void setConfidentlyLocalizedModificationSites(String confidentlyLocalizedModificationSites) {
        this.confidentlyLocalizedModificationSites = confidentlyLocalizedModificationSites;
    }

    /**
     * Get number of confidently localised modification sites.
     *
     * @return Number of confidently localised modification sites.
     */
    public String getConfidentlyLocalizedModificationSitesNumber() {
        return ConfidentlyLocalizedModificationSitesNumber;
    }

    /**
     * Set number of confidently localised modification sites.
     *
     * @param ConfidentlyLocalizedModificationSitesNumber number of confidently
     * localised modification sites.
     */
    public void setConfidentlyLocalizedModificationSitesNumber(String ConfidentlyLocalizedModificationSitesNumber) {
        this.ConfidentlyLocalizedModificationSitesNumber = ConfidentlyLocalizedModificationSitesNumber;
    }

    /**
     * Get ambiguous localised modification sites
     *
     * @return Ambiguous localised modification sites
     */
    public String getAmbiguouslyLocalizedModificationSites() {
        return ambiguouslyLocalizedModificationSites;
    }

    /**
     * Set ambiguous localised modification sites
     *
     * @param ambiguouslyLocalizedModificationSites Ambiguous localised
     * modification sites
     */
    public void setAmbiguouslyLocalizedModificationSites(String ambiguouslyLocalizedModificationSites) {
        this.ambiguouslyLocalizedModificationSites = ambiguouslyLocalizedModificationSites;
    }

    /**
     * Get number of ambiguous localised modification sites.
     *
     * @return Number of ambiguous localised modification sites.
     */
    public String getAmbiguouslyLocalizedModificationSitesNumber() {
        return ambiguouslyLocalizedModificationSitesNumber;
    }

    /**
     * Set number of ambiguous localised modification sites.
     *
     * @param ambiguouslyLocalizedModificationSitesNumber Number of ambiguous
     * localised modification sites.
     */
    public void setAmbiguouslyLocalizedModificationSitesNumber(String ambiguouslyLocalizedModificationSitesNumber) {
        this.ambiguouslyLocalizedModificationSitesNumber = ambiguouslyLocalizedModificationSitesNumber;
    }

    /**
     * Get protein inference type.
     *
     * @return protein inference type.
     */
    public String getProteinInference() {
        return proteinInference;
    }

    /**
     * Set protein inference type.
     *
     * @param proteinInference protein inference type.
     */
    public void setProteinInference(String proteinInference) {
        this.proteinInference = proteinInference;
    }

    /**
     * Get secondary accessions related to the main accession protein
     *
     * @return Secondary accessions related to the main accession protein
     */
    public String getSecondaryAccessions() {
        return secondaryAccessions;
    }

    /**
     * Set secondary accessions related to the main accession protein
     *
     * @param secondaryAccessions Secondary accessions related to the main
     * accession protein
     */
    public void setSecondaryAccessions(String secondaryAccessions) {
        this.secondaryAccessions = secondaryAccessions;
        secondaryAccessionSet.addAll(Arrays.asList(secondaryAccessions.replace(" ", "").split(",")));
    }

    /**
     * Get protein group that is related to the main protein.
     *
     * @return protein accessions
     */
    public String getProteinGroup() {
        return proteinGroup;
    }

    /**
     * Set protein group that is related to the main protein.
     *
     * @param proteinGroup protein accessions
     */
    public void setProteinGroup(String proteinGroup) {
        this.proteinGroup = proteinGroup;
        proteinGroupSet.addAll(Arrays.asList(proteinGroup.split(", ")));
    }

    /**
     * Get number of the validated peptides
     *
     * @return validated peptides number
     */
    public int getValidatedPeptidesNumber() {
        return validatedPeptidesNumber;
    }

    /**
     * Set number of the validated peptides
     *
     * @param validatedPeptidesNumber number of the validated peptides
     */
    public void setValidatedPeptidesNumber(int validatedPeptidesNumber) {
        this.validatedPeptidesNumber = validatedPeptidesNumber;
    }

    /**
     * Get number of the peptides
     *
     * @return total number of the peptides
     */
    public int getPeptidesNumber() {
        return peptidesNumber;
    }

    /**
     * Get set of secondary accessions related to the main accession protein.
     *
     * @return set of protein accessions.
     */
    public Set<String> getSecondaryAccessionSet() {
        return secondaryAccessionSet;
    }

    /**
     * Get set of accessions in the protein group.
     *
     * @return set of protein accessions.
     */
    public Set<String> getProteinGroupSet() {
        return proteinGroupSet;
    }

    /**
     * Set number of peptides
     *
     * @param peptidesNumber number of peptides
     */
    public void setPeptidesNumber(int peptidesNumber) {
        this.peptidesNumber = peptidesNumber;
    }

    /**
     * Get unique number of peptides to the protein.
     *
     * @return Unique number of peptides to the protein.
     */
    public int getUniqueNumber() {
        return uniqueNumber;
    }

    /**
     * Set unique number of peptides to the protein.
     *
     * @param uniqueNumber Unique number of peptides to the protein.
     */
    public void setUniqueNumber(int uniqueNumber) {
        this.uniqueNumber = uniqueNumber;
    }

    /**
     * Get unique number of validated peptides to the protein.
     *
     * @return Unique number of validated peptides to the protein.
     */
    public int getValidatedUniqueNumber() {
        return validatedUniqueNumber;
    }

    /**
     * Set unique number of validated peptides to the protein.
     *
     * @param validatedUniqueNumber Unique number of validated peptides to the
     * protein.
     */
    public void setValidatedUniqueNumber(int validatedUniqueNumber) {
        this.validatedUniqueNumber = validatedUniqueNumber;
    }

    /**
     * Get unique number of peptides to the protein group.
     *
     * @return Unique number of peptides to the protein group.
     */
    public int getUniqueToGroupNumber() {
        return uniqueToGroupNumber;
    }

    /**
     * Set unique number of peptides to the protein group.
     *
     * @param uniqueToGroupNumber Unique number of peptides to the protein
     * group.
     */
    public void setUniqueToGroupNumber(int uniqueToGroupNumber) {
        this.uniqueToGroupNumber = uniqueToGroupNumber;
    }

    /**
     * Get unique number of validated peptides to the protein group.
     *
     * @return Unique number of validated peptides to the protein group.
     */
    public int getValidatedUniqueToGroupNumber() {
        return validatedUniqueToGroupNumber;
    }

    /**
     * Set unique number of validated peptides to the protein group.
     *
     * @param validatedUniqueToGroupNumber Unique number of validated peptides
     * to the protein group.
     */
    public void setValidatedUniqueToGroupNumber(int validatedUniqueToGroupNumber) {
        this.validatedUniqueToGroupNumber = validatedUniqueToGroupNumber;
    }

    /**
     * Get number of validated PSMs.
     *
     * @return Number of validated PSMs.
     */
    public int getValidatedPSMsNumber() {
        return validatedPSMsNumber;
    }

    /**
     * Set number of validated PSMs.
     *
     * @param validatedPSMsNumber Number of validated PSMs.
     */
    public void setValidatedPSMsNumber(int validatedPSMsNumber) {
        this.validatedPSMsNumber = validatedPSMsNumber;
    }

    /**
     * Get Number of PSMs.
     *
     * @return Number of PSMs.
     */
    public int getPSMsNumber() {
        return PSMsNumber;
    }

    /**
     * Set number of PSMs.
     *
     * @param PSMsNumber Number of PSMs.
     */
    public void setPSMsNumber(int PSMsNumber) {
        this.PSMsNumber = PSMsNumber;
    }

    /**
     * Get confidence as % value
     *
     * @return value of confidence
     */
    public double getConfidence() {
        return confidence;
    }

    /**
     * Set confidence as % value
     *
     * @param confidence value of confidence
     */
    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    /**
     * Get validation of peptide
     *
     * @return validation value
     */
    public String getValidation() {
        return validation;
    }

    /**
     * Set validation of peptide
     *
     * @param validation validation value
     */
    public void setValidation(String validation) {
        this.validation = validation;
    }

    /**
     * Get peptide index
     *
     * @return peptide index in the exported file
     */
    public int getIndex() {
        return index;
    }

    /**
     * Set peptide index
     *
     * @param index peptide index in the exported file
     */
    public void setIndex(int index) {
        this.index = index;
    }

}
