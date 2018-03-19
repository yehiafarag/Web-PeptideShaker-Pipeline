/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.galaxy.dataobjects;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author Yehia Farag
 */
public class PSMObject {

    private final Set<String> proteins;
    private String sequence;
    private String modifiedSequence;
    private String aasBefore;
    private String aasAfter;
    private String postions;
    private final Set<String> variableModifications;
    private final Set<String> fixedModifications;
    private String spectrumFile;
    private String spectrumTitle;
    private String spectrumScanNumber;
    private String RT;
    private String MZ;
    private String measuredCharge;
    private String identificationCharge;
    private double theoriticalMass;
    private int isotopNumber;
    private double precursorMZError_PPM;
    private String localizationConfidence;
    private String probabilisticPTMScore;
    private String D_Score;
    private double confidence;
    private String validation;
    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public PSMObject() {
        proteins=new LinkedHashSet<>();
        variableModifications=new LinkedHashSet<>();
        fixedModifications=new LinkedHashSet<>();
    }

    public Set<String> getProteins() {
        return proteins;
    }

    public void addProtein(String protein) {
        this.proteins.add(protein);
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getModifiedSequence() {
        return modifiedSequence;
    }

    public void setModifiedSequence(String modifiedSequence) {
        this.modifiedSequence = modifiedSequence;
    }

    public String getAasBefore() {
        return aasBefore;
    }

    public void setAasBefore(String aasBefore) {
        this.aasBefore = aasBefore;
    }

    public String getAasAfter() {
        return aasAfter;
    }

    public void setAasAfter(String aasAfter) {
        this.aasAfter = aasAfter;
    }

    public String getPostions() {
        return postions;
    }

    public void setPostions(String postions) {
        this.postions = postions;
    }

    public Set<String> getVariableModifications() {
        return variableModifications;
    }

    public void addVariableModification(String variableModification) {
        this.variableModifications.add(variableModification);
    }

    public Set<String> getFixedModifications() {
        return fixedModifications;
    }

    public void addFixedModification(String fixedModification) {
        this.fixedModifications.add(fixedModification);
    }

    public String getSpectrumFile() {
        return spectrumFile;
    }

    public void setSpectrumFile(String spectrumFile) {
        this.spectrumFile = spectrumFile;
    }

    public String getSpectrumTitle() {
        return spectrumTitle;
    }

    public void setSpectrumTitle(String spectrumTitle) {
        this.spectrumTitle = spectrumTitle;
    }

    public String getSpectrumScanNumber() {
        return spectrumScanNumber;
    }

    public void setSpectrumScanNumber(String spectrumScanNumber) {
        this.spectrumScanNumber = spectrumScanNumber;
    }

    public String getRT() {
        return RT;
    }

    public void setRT(String RT) {
        this.RT = RT;
    }

    public String getMZ() {
        return MZ;
    }

    public void setMZ(String MZ) {
        this.MZ = MZ;
    }

    public String getMeasuredCharge() {
        return measuredCharge;
    }

    public void setMeasuredCharge(String measuredCharge) {
        this.measuredCharge = measuredCharge;
    }

    public String getIdentificationCharge() {
        return identificationCharge;
    }

    public void setIdentificationCharge(String identificationCharge) {
        this.identificationCharge = identificationCharge;
    }

    public double getTheoriticalMass() {
        return theoriticalMass;
    }

    public void setTheoriticalMass(double theoriticalMass) {
        this.theoriticalMass = theoriticalMass;
    }

    public int getIsotopNumber() {
        return isotopNumber;
    }

    public void setIsotopNumber(int isotopNumber) {
        this.isotopNumber = isotopNumber;
    }

    public double getPrecursorMZError_PPM() {
        return precursorMZError_PPM;
    }

    public void setPrecursorMZError_PPM(double precursorMZError_PPM) {
        this.precursorMZError_PPM = precursorMZError_PPM;
    }

    public String getLocalizationConfidence() {
        return localizationConfidence;
    }

    public void setLocalizationConfidence(String localizationConfidence) {
        this.localizationConfidence = localizationConfidence;
    }

    public String getProbabilisticPTMScore() {
        return probabilisticPTMScore;
    }

    public void setProbabilisticPTMScore(String probabilisticPTMScore) {
        this.probabilisticPTMScore = probabilisticPTMScore;
    }

    public String getD_Score() {
        return D_Score;
    }

    public void setD_Score(String D_Score) {
        this.D_Score = D_Score;
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

}
