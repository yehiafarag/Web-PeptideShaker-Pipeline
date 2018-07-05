package com.uib.web.peptideshaker.galaxy.dataobjects;

import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This
 *
 * @author Yehia Farag
 */
public class PeptideObject extends Peptide {

    private String proteins;
    private String proteinGroups;
    private int index;
    private int validatedProteinGroupsNumber;
    private int uniqueDatabase;
    private String sequence;
    private String modifiedSequence;
    private String postion;
    private String aasBefore;
    private String aasAfter;
    private String variableModifications;
    private String fixedModifications;
    private String localizationConfidence;
    private int PSMsNumber;
    private int validatedPSMsNumber;
    private double confidence;
    private String validation;
    private String tooltip;

    private final Set<String> proteinsSet;
    private final Set<String> proteinGroupsSet;

    private int validatedUniqueToGroupNumber;
    public void setTooltip(String tooltip){
        this.tooltip=tooltip;
    }
    public String getTooltip(){
        return this.tooltip;
    }

    public PeptideObject() {
        this.proteinsSet = new LinkedHashSet<>();
        this.proteinGroupsSet = new LinkedHashSet<>();

    }

    public String getProteins() {
        return proteins;
    }

    public void setProteins(String proteins) {
        this.proteins = proteins;
        proteinsSet.addAll(Arrays.asList(proteins.split("; ")));
    }

    @Override
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

    public String getPostion() {
        return postion;
    }

    public void setPostion(String postion) {
        this.postion = postion;
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

    public String getVariableModifications() {
        return variableModifications;
    }
    private final PTMFactory ptmFactory = PTMFactory.getInstance();
    /**
     * The modifications carried by the peptide.
     */
    private ArrayList<ModificationMatch> modificationMatches = null;

    public void setVariableModifications(String variableModifications) {
        this.variableModifications = variableModifications;
        if (variableModifications.trim().equalsIgnoreCase("")) {
            return;
        }
        this.modificationMatches = new ArrayList<>();
        for (String modStr : variableModifications.replace("),", "__").split("__")) {
            String[] modStrArr = modStr.trim().replace("(", "__").split("__");
            PTM ptm = ptmFactory.getPTM(modStrArr[0].trim());
            String[] indexArr = modStrArr[1].trim().replace(")", "").trim().split(",");
            for (String modIndex : indexArr) {
                ModificationMatch mod = new ModificationMatch(ptm.getName(), true, Integer.parseInt(modIndex.trim()));
                modificationMatches.add(mod);
            }

        }
    }

    public String getFixedModifications() {
        return fixedModifications;
    }

    public void setFixedModifications(String fixedModifications) {
        this.fixedModifications = fixedModifications;
    }

    public String getLocalizationConfidence() {
        return localizationConfidence;
    }

    public void setLocalizationConfidence(String localizationConfidence) {
        this.localizationConfidence = localizationConfidence;
    }

    public String getProteinGroups() {
        return proteinGroups;
    }

    public void setProteinGroups(String proteinGroups) {
        this.proteinGroups = proteinGroups;
        proteinGroupsSet.addAll(Arrays.asList(proteinGroups.split(", ")));
    }

    public Set<String> getProteinsSet() {
        return proteinsSet;
    }

    public Set<String> getProteinGroupsSet() {
        return proteinGroupsSet;
    }

    public int getValidatedProteinGroupsNumber() {
        return validatedProteinGroupsNumber;
    }

    public void setValidatedProteinGroupsNumber(int validatedProteinGroupsNumber) {
        this.validatedProteinGroupsNumber = validatedProteinGroupsNumber;
    }

    public int getUniqueDatabase() {
        return uniqueDatabase;
    }

    public void setUniqueDatabase(int uniqueDatabase) {
        this.uniqueDatabase = uniqueDatabase;
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

    @Override
    public boolean isModified() {
        return !this.variableModifications.isEmpty();

    }

    @Override
    public ArrayList<ModificationMatch> getModificationMatches() {
        return modificationMatches;
    }

}
