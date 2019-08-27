package com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects;

import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class represents peptide object for Online PeptideShaker system the
 * class contains all the required information for visualising the peptide data
 *
 * @author Yehia Farag
 */
public class PeptideObject extends Peptide {

    /**
     * Peptide index from the exported PeptideShaker file.
     */
    private int index;
    /**
     * Standard peptide sequence.
     */
    private String sequence;
    /**
     * Peptide modified sequence.
     */
    private String modifiedSequence;
    /**
     * Peptide position.
     */
    private String postion;
    /**
     * Amino acid score before.
     */
    private String aasBefore;
    /**
     * Amino acid score after.
     */
    private String aasAfter;
    /**
     * Protein variable modifications included in the peptide sequence.
     */
    private String variableModifications;
    /**
     * Protein fixed modifications included in the peptide sequence.
     */
    private String fixedModifications;
    /**
     * Localisation confidence.
     */
    private String localizationConfidence;
    /**
     * Number of valid protein groups that include the peptide.
     */
    private int validatedProteinGroupsNumber;
    /**
     * Number of unique databases.
     */
    private int uniqueDatabase;
    /**
     * Number of PSMs that include the peptide.
     */
    private int PSMsNumber;
    /**
     * Number of validated PSMs that include the peptide.
     */
    private int validatedPSMsNumber;
    /**
     * Confidence value.
     */
    private double confidence;
    /**
     * Validation value.
     */
    private String validation;
    /**
     * Peptide tool-tip text value.
     */
    private String tooltip;
    /**
     * Set of main protein accessions for the peptide.
     */
    private final Set<String> proteinsSet;
    /**
     * UniProt protein group key.
     */
    private String proteinGroupKey;
    /**
     * Set of main protein group accessions for the peptide.
     */
    private final Set<String> proteinGroupsSet;
    /**
     * Number of validated unique peptides to protein group.
     */
    private int validatedUniqueToGroupNumber;
    /**
     * Instance of the PTM factory that is loading PTM from an XML file and
     * provide them on demand as standard class.
     */
    private final PTMFactory ptmFactory = PTMFactory.getInstance();
    /**
     * The modifications carried by the peptide.
     */
    private ArrayList<ModificationMatch> modificationMatches = null;
    /**
     * Intensity value of the quantification.
     */
    private double intensity = 0;
    /**
     * Intensity hash-code colour of the quantification.
     */
    private String intensityColor;
    /**
     * Intensity hash-code colour of the psmNumber.
     */
    private String psmColor = "RGB(120,120,120)";
    /**
     * Peptide not mapped to 3dView.
     */
    private boolean invisibleOn3d;

    public boolean isInvisibleOn3d() {
        return invisibleOn3d;
    }

    public void setInvisibleOn3d(boolean invisibleOn3d) {
        this.invisibleOn3d = invisibleOn3d;
    }

    public String getPsmColor() {
        return psmColor;
    }

    public void setPsmColor(String psmColor) {
        this.psmColor = psmColor;
    }

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    /**
     * Constructor to initialise the main data structure.
     */
    public PeptideObject() {
        this.proteinsSet = new LinkedHashSet<>();
        this.proteinGroupsSet = new LinkedHashSet<>();

    }

    /**
     * Set peptide tool-tip text value.
     *
     * @param tooltip tool tip text
     */
    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    /**
     * Get peptide tool-tip text value.
     *
     * @return tool tip text
     */
    public String getTooltip() {
        return this.tooltip;
    }

    /**
     * Set main protein accessions for the peptide.
     *
     * @param proteins ';'separated strings of accessions
     */
    public void setProteins(String proteins) {
        for (String acc : proteins.split(";")) {
            proteinsSet.add(acc.replace(" ", ""));
        }
    }

    /**
     * Get Standard peptide sequence.
     *
     * @return Standard peptide sequence.
     */
    @Override
    public String getSequence() {
        return sequence;
    }

    /**
     * Set standard peptide sequence.
     *
     * @param sequence Standard peptide sequence.
     */
    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    /**
     * Get peptide modified sequence
     *
     * @return peptide modified sequence
     */
    public String getModifiedSequence() {
        return modifiedSequence;
    }

    /**
     * Set peptide modified sequence
     *
     * @param modifiedSequence peptide modified sequence
     */
    public void setModifiedSequence(String modifiedSequence) {
        this.modifiedSequence = modifiedSequence;
    }

    /**
     * Get peptide positions.
     *
     * @return peptide position.
     */
    public String getPostion() {
        return postion;
    }

    /**
     * Set peptide position.
     *
     * @param postion peptide position.
     */
    public void setPostion(String postion) {
        this.postion = postion;
    }

    /**
     * Get Amino acid score before
     *
     * @return Amino acid score before
     */
    public String getAasBefore() {
        return aasBefore;
    }

    /**
     * Set Amino acid score before
     *
     * @param aasBefore Amino acid score before
     */
    public void setAasBefore(String aasBefore) {
        this.aasBefore = aasBefore;
    }

    /**
     * Get Amino acid score after
     *
     * @return Amino acid score before
     */
    public String getAasAfter() {
        return aasAfter;
    }

    /**
     * Set Amino acid score after
     *
     * @param aasAfter Amino acid score after
     */
    public void setAasAfter(String aasAfter) {
        this.aasAfter = aasAfter;
    }

    /**
     * Get protein variable modifications list included in the peptide sequence.
     *
     * @return modifications list.
     */
    public String getVariableModifications() {
        return variableModifications;
    }

    /**
     * Set protein variable modifications list included in the peptide sequence.
     *
     * @param variableModifications protein variable modifications list(as
     * string) included in the peptide sequence.
     */
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
                ModificationMatch mod = new ModificationMatch(ptm.getName(), true, Integer.parseInt(modIndex.trim().split(";")[0]));
                modificationMatches.add(mod);
            }

        }
    }

    /**
     * Get protein fixed modifications list included in the peptide sequence.
     *
     * @return set of modifications name.
     */
    public String getFixedModifications() {
        return fixedModifications;
    }

    /**
     * Set protein fixed modifications list included in the peptide sequence.
     *
     * @param fixedModifications protein fixed modifications list(as string)
     * included in the peptide sequence.
     */
    public void setFixedModifications(String fixedModifications) {
        this.fixedModifications = fixedModifications;
    }

    /**
     * Get localisation confidence
     *
     * @return localisation confidence
     */
    public String getLocalizationConfidence() {
        return localizationConfidence;
    }

    /**
     * Set localisation confidence
     *
     * @param localizationConfidence localisation confidence
     */
    public void setLocalizationConfidence(String localizationConfidence) {
        this.localizationConfidence = localizationConfidence;
    }

    /**
     * Set main protein groups accessions for the peptide.
     *
     * @param proteinGroups ';'separated strings of accessions
     */
    public void setProteinGroups(String proteinGroups) {
        proteinGroupKey = proteinGroups.replace("(Confident)", "").replace("(Doubtful)", "");
//        proteinGroupKey = proteinGroupKey.replace("Not Validated", "").replace("(","").replace(")", "");
        proteinGroupKey = proteinGroupKey.replace(" ", "").replace(",", "-_-");
        for (String protGroup : proteinGroups.split(",")) {
            proteinGroupsSet.add(protGroup.replace(" ", ""));
        }
    }

    public String getProteinGroupKey() {
        return proteinGroupKey;
    }

    /**
     * Get main protein accessions for the peptide.
     *
     * @return set of main protein accessions for the peptide
     */
    public Set<String> getProteinsSet() {
        return proteinsSet;
    }

    /**
     * Get main protein groups accessions for the peptide.
     *
     * @return set of main protein groups accessions for the peptide
     */
    public Set<String> getProteinGroupsSet() {
        return proteinGroupsSet;
    }

    /**
     * Get number of valid protein groups that include the peptide
     *
     * @return number of valid protein groups
     */
    public int getValidatedProteinGroupsNumber() {
        return validatedProteinGroupsNumber;
    }

    /**
     * Set number of valid protein groups that include the peptide
     *
     * @param validatedProteinGroupsNumber number of valid protein groups
     */
    public void setValidatedProteinGroupsNumber(int validatedProteinGroupsNumber) {
        this.validatedProteinGroupsNumber = validatedProteinGroupsNumber;
    }

    /**
     * Get number of unique database
     *
     * @return unique database number
     */
    public int getUniqueDatabase() {
        return uniqueDatabase;
    }

    /**
     * Set number of unique database
     *
     * @param uniqueDatabase unique database number
     */
    public void setUniqueDatabase(int uniqueDatabase) {
        this.uniqueDatabase = uniqueDatabase;
    }

    /**
     * Get number of validated unique peptides to protein group
     *
     * @return validated unique peptides number
     */
    public int getValidatedUniqueToGroupNumber() {
        return validatedUniqueToGroupNumber;
    }

    /**
     * Set number of validated unique peptides to protein group
     *
     * @param validatedUniqueToGroupNumber validated unique peptides number
     */
    public void setValidatedUniqueToGroupNumber(int validatedUniqueToGroupNumber) {
        this.validatedUniqueToGroupNumber = validatedUniqueToGroupNumber;
    }

    /**
     * Get number of validated PSMs that include the peptide.
     *
     * @return valid PSMs number
     */
    public int getValidatedPSMsNumber() {
        return validatedPSMsNumber;
    }

    /**
     * Set number of validated PSMs that include the peptide.
     *
     * @param validatedPSMsNumber valid PSMs number
     */
    public void setValidatedPSMsNumber(int validatedPSMsNumber) {
        this.validatedPSMsNumber = validatedPSMsNumber;
    }

    /**
     * Get number of PSMs that include the peptide.
     *
     * @return PSMs number
     */
    public int getPSMsNumber() {
        return PSMsNumber;
    }

    /**
     * Set number of PSMs that include the peptide.
     *
     * @param PSMsNumber PSMs number
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

    @Override
    public boolean isModified() {
        return !this.variableModifications.isEmpty();

    }

    @Override
    public ArrayList<ModificationMatch> getModificationMatches() {
        return modificationMatches;
    }

    public String getIntensityColor() {
        return intensityColor;
    }

    public void setIntensityColor(String intensityColor) {
        this.intensityColor = intensityColor;
    }

}
