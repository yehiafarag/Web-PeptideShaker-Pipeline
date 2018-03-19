package com.uib.web.peptideshaker.galaxy.dataobjects;

import com.compomics.util.experiment.biology.AminoAcidPattern;
import com.compomics.util.experiment.biology.Enzyme;
import com.compomics.util.experiment.biology.EnzymeFactory;
import com.compomics.util.preferences.SequenceMatchingPreferences;
import com.google.common.collect.Sets;
import com.uib.web.peptideshaker.model.core.ModificationMatrix;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import uk.ac.ebi.pride.tools.braf.BufferedRandomAccessFile;

/**
 * This class represents dataset visualisation that store data for viewing files
 * on web
 *
 * @author Yehia Farag
 */
public class PeptideShakerVisualizationDataset1 extends SystemDataSet {

    private Map<String, ProteinObject> proteinsMap;
    private final Map<Object, ProteinObject> fastaProteinsMap;
    private Map<String, Set<PeptideObject>> protein_peptide_Map;
    private Map<String, Set<ProteinObject>> protein_relatedProteins_Map;
    private Map<Object, PeptideObject> peptidesMap;
    private final Map<String, Set<Comparable>> modificationMap;
    private final Map<String, Set<Comparable>> chromosomeMap;
    private final Map<String, Set<Comparable>> piMap;
    private final Map<String, Set<Comparable>> proteinValidationMap;
    private final TreeMap<Comparable, Set<Comparable>> proteinPeptidesNumberMap;
    private final TreeMap<Comparable, Set<Comparable>> proteinPSMNumberMap;
    private final TreeMap<Comparable, Set<Comparable>> proteinCoverageMap;


    private GalaxyFile peptideFile;
    private GalaxyFile fastaFile;
    private GalaxyFile proteinFile;
    private GalaxyFile psmFile;

    private String proteinFileId;
    private String peptideFileId;
    private String cpsId;
    private String psmFileId;
    private String jobId;
    private String searchGUIFileId;
    private final Map<String, SystemDataSet> mgfFiles;
    private final Map<String, String> mgfFilesIndexes;
    private String fastaFileName;
    private String fastaFileId;
    private String zipFileId;

    public String getZipFileId() {
        return zipFileId;
    }

    public GalaxyFile getPsmFile() {
        return psmFile;
    }

    public void setPsmFile(GalaxyFile psmFile) {
        this.psmFile = psmFile;
    }

    public void setZipFileId(String zipFileId) {
        this.zipFileId = zipFileId;
    }
    private GalaxyFastaFileReader fastaFileReader;
    private int proteinsNumber;
    private int psmNumber;
    private final SequenceMatchingPreferences sequenceMatchingPreferences;
    private final EnzymeFactory enzymeFactory;
    private ModificationMatrix modificationMatrix;

    private int peptidesNumber;
    private Map<String, Object> searchingParameters;
    private String fixedModification;
    private String variableModification;

    public void setEnzyme(String enzyme) {
        this.enzyme = enzyme;
    }
    private String enzyme;

    public ProteinObject getProtein(String proteinKey) {
        if (proteinsMap.containsKey(proteinKey)) {
            return proteinsMap.get(proteinKey);
        } else {
            ProteinObject newRelatedProt = updateProteinInformation(null, proteinKey);
            return newRelatedProt;
        }

    }

    public Map<String, Set<Comparable>> getChromosomeMap() {
        return chromosomeMap;
    }

    public Map<String, ProteinObject> getProteinsMap() {
        if (proteinsMap != null) {
            return proteinsMap;
        }
        protein_relatedProteins_Map = new HashMap<>();
        proteinsMap = new LinkedHashMap<>();
        readFastaFile();
        BufferedRandomAccessFile bufferedRandomAccessFile = null;
        //for testing
        Random r = new Random();
//        int[] chrom = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24};

        try {//           
            bufferedRandomAccessFile = new BufferedRandomAccessFile(proteinFile.getFile(), "r", 1024 * 100);
            String line;
            /**
             * escape header
             */
            bufferedRandomAccessFile.getNextLine();
            while ((line = bufferedRandomAccessFile.getNextLine()) != null) {
                String[] arr = line.split("\\t");
//                Object[] obj = new Object[]{Integer.valueOf(arr[0]), arr[1], arr[2], arr[3], Double.valueOf(arr[5]), Double.valueOf(arr[6]), Integer.valueOf(arr[16])};
                ProteinObject protein;
                if (fastaProteinsMap.containsKey(arr[1])) {
                    protein = fastaProteinsMap.get(arr[1]);
                } else {
                    protein = new ProteinObject();
                    System.out.println("not exist protein need sequence " + arr[1]);
                }
                protein.setAccession(arr[1]);
                protein.setIndex(Integer.valueOf(arr[0]));
                protein.setDescription(arr[2]);
                protein.setGeneName(arr[3]);
                protein.setChromosome(arr[4]);
//                protein.setChromosome(chrom[r.nextInt(10)] + "");
                if (protein.getChromosome().trim().isEmpty()) {
                    protein.setChromosome("No Information");
                    chromosomeMap.get(protein.getChromosome()).add(protein.getAccession());
                } else {
                    if (!chromosomeMap.containsKey(protein.getChromosome())) {
                        chromosomeMap.put(protein.getChromosome(), new LinkedHashSet<>());
                    }
                    chromosomeMap.get(protein.getChromosome()).add(protein.getAccession());
                }

                protein.setMW(Double.valueOf(arr[5]));
                protein.setPossibleCoverage(Double.valueOf(arr[6]));
                protein.setCoverage(Double.valueOf(arr[7]));
                int pc = (int) Math.round(protein.getPossibleCoverage());
                if (!proteinCoverageMap.containsKey(pc)) {
                    proteinCoverageMap.put(pc, new LinkedHashSet<>());
                }
                proteinCoverageMap.get(pc).add(protein.getAccession());
                protein.setSpectrumCounting(Double.valueOf(arr[8]));
                protein.setConfidentlyLocalizedModificationSites(arr[9]);
                protein.setConfidentlyLocalizedModificationSitesNumber(arr[10]);
                protein.setAmbiguouslyLocalizedModificationSites(arr[11]);
                protein.setAmbiguouslyLocalizedModificationSitesNumber(arr[12]);
                protein.setProteinInference(arr[13].replace("Proteins", "").replace("and", "&"));

                if (protein.getProteinInference().trim().isEmpty()) {
                    protein.setProteinInference("No Information");
                    piMap.get(protein.getProteinInference()).add(protein.getAccession());
                } else {
                    if (!piMap.containsKey(protein.getProteinInference())) {
                        piMap.put(protein.getProteinInference(), new LinkedHashSet<>());
                    }
                    piMap.get(protein.getProteinInference()).add(protein.getAccession());
                }

                protein.setSecondaryAccessions(arr[14]);

                protein.setProteinGroup(arr[15]);
                protein.setValidatedPeptidesNumber(Integer.parseInt(arr[16]));
                protein.setPeptidesNumber(Integer.parseInt(arr[17]));
                if (!proteinPeptidesNumberMap.containsKey(protein.getValidatedPeptidesNumber())) {
                    proteinPeptidesNumberMap.put(protein.getValidatedPeptidesNumber(), new LinkedHashSet<>());
                }
                proteinPeptidesNumberMap.get(protein.getValidatedPeptidesNumber()).add(protein.getAccession());
                protein.setUniqueNumber(Integer.parseInt(arr[18]));
                protein.setValidatedUniqueNumber(Integer.parseInt(arr[19]));
                protein.setUniqueToGroupNumber(Integer.parseInt(arr[20]));
                protein.setValidatedUniqueToGroupNumber(Integer.valueOf(arr[21]));
                protein.setValidatedPSMsNumber(Integer.valueOf(arr[22]));
                protein.setPSMsNumber(Integer.valueOf(arr[23]));
                if (!proteinPSMNumberMap.containsKey(protein.getValidatedPSMsNumber())) {
                    proteinPSMNumberMap.put(protein.getValidatedPSMsNumber(), new LinkedHashSet<>());
                }
                proteinPSMNumberMap.get(protein.getValidatedPSMsNumber()).add(protein.getAccession());
                protein.setConfidence(Double.valueOf(arr[24]));
                protein.setValidation(arr[25]);
                if (protein.getValidation().trim().isEmpty()) {
                    protein.setValidation("No Information");
                    proteinValidationMap.get(protein.getValidation()).add(protein.getAccession());
                } else {
                    if (!proteinValidationMap.containsKey(protein.getValidation())) {
                        proteinValidationMap.put(protein.getValidation(), new LinkedHashSet<>());
                    }
                    proteinValidationMap.get(protein.getValidation()).add(protein.getAccession());
                }

//                boolean addModification = false;
//                for (String modification : modificationMap.keySet()) {
//                    if (protein.getConfidentlyLocalizedModificationSites().contains(modification) || protein.getAmbiguouslyLocalizedModificationSites().contains(modification)) {
//                        modificationMap.get(modification).add(protein.getAccession());
//                        addModification = true;
//                    }
//
//                }
//                if (!addModification) {
//                    modificationMap.get("No Modifications").add(protein.getAccession());
//                }
//                protein = fastaFileReader.updateProteinInformation(protein, protein.getAccession());
                proteinsMap.put(protein.getAccession(), protein);
                for (String acc : protein.getProteinGroupSet()) {
                    if (!protein_relatedProteins_Map.containsKey(acc)) {
                        Set<ProteinObject> protenHashSet = new LinkedHashSet<>();
                        protein_relatedProteins_Map.put(acc, protenHashSet);
                    }
                    protein_relatedProteins_Map.get(acc).add(protein);
                }
            }
            bufferedRandomAccessFile.close();
        } catch (IOException | NumberFormatException ex) {
            if (bufferedRandomAccessFile != null) {
                try {
                    bufferedRandomAccessFile.close();
                } catch (IOException ex1) {
                }
            }
        }
        return proteinsMap;
    }

    private final String[] proteinEvedence = new String[]{"Not Available", "Protein", "Transcript", "Homology", "Predicted", "Uncertain"};

    private void readFastaFile() {
        fastaProteinsMap.clear();
        BufferedRandomAccessFile bufferedRandomAccessFile = null;
        try {//           
            bufferedRandomAccessFile = new BufferedRandomAccessFile(fastaFile.getFile(), "r", 1024 * 100);
            String line;
            /**
             * escape header
             */
            String fastaHeader = "";
            String sequence = "";
            while ((line = bufferedRandomAccessFile.getNextLine()) != null) {
                if (line.startsWith(">")) {
                    if (!fastaHeader.equalsIgnoreCase("")) {
                        ProteinObject protein = new ProteinObject();
                        String accss = fastaHeader.split("\\|")[1];
                        String desc = fastaHeader.split("\\|")[2].split("OS=")[0];
                        desc = desc.replace(desc.split(" ")[0], "").trim();
                        protein.setDescription(desc);
                        protein.setAccession(accss);
                        protein.setSequence(sequence);
                        protein.setProteinEvidence(proteinEvedence[Integer.parseInt(fastaHeader.split("PE=")[1].split(" ")[0])]);
                        fastaProteinsMap.put(protein.getAccession(), protein);
                    }
                    fastaHeader = line;
                    sequence = "";
                    continue;
                }
                sequence += line;
            }
            bufferedRandomAccessFile.close();
        } catch (IOException | NumberFormatException ex) {
            if (bufferedRandomAccessFile != null) {
                try {
                    bufferedRandomAccessFile.close();
                } catch (IOException ex1) {
                }
            }
        }

    }

    public Map<Object, PeptideObject> getPeptidesMap() {
        if (peptidesMap != null) {
            return peptidesMap;
        }
        peptidesMap = new LinkedHashMap<>();
        protein_peptide_Map = new HashMap<>();
        BufferedRandomAccessFile bufferedRandomAccessFile = null;
        try {//           
            bufferedRandomAccessFile = new BufferedRandomAccessFile(peptideFile.getFile(), "r", 1024 * 100);
            String line;
            /**
             * escape header
             */
            bufferedRandomAccessFile.getNextLine();
            while ((line = bufferedRandomAccessFile.getNextLine()) != null) {
                String[] arr = line.split("\\t");
                //	Position	AAs Before	AAs After	Variable Modifications	Fixed Modifications	Localization Confidence	#Validated PSMs	#PSMs	Confidence [%]	Validation
                PeptideObject peptide = new PeptideObject();
                peptide.setIndex(Integer.parseInt(arr[0]));
                peptide.setProteins(arr[1]);
                peptide.setProteinGroups(arr[2]);
                peptide.setValidatedProteinGroupsNumber(Integer.parseInt(arr[3]));
                peptide.setUniqueDatabase(Integer.parseInt(arr[4]));
                peptide.setSequence(arr[5]);
                peptide.setModifiedSequence(arr[6]);

                peptide.setPostion(arr[7]);
                peptide.setAasBefore(arr[8]);
                peptide.setAasAfter(arr[9]);
                peptide.setVariableModifications(arr[10]);
                peptide.setFixedModifications(arr[11]);
                peptide.setLocalizationConfidence(arr[12]);
                peptide.setValidatedPSMsNumber(Integer.parseInt(arr[13]));
                peptide.setPSMsNumber(Integer.parseInt(arr[14]));

                peptide.setConfidence(Double.parseDouble(arr[15]));
                peptide.setValidation(arr[16]);

//                Object[] obj = new Object[]{Integer.valueOf(arr[0]), arr[1], arr[2], Integer.valueOf(arr[3]), Integer.valueOf(arr[4]), (arr[5]), (arr[10]), (arr[11]), Integer.valueOf(arr[14]), Double.valueOf(arr[15]), arr[16]};
//                String key = arr[0] + "_-_" + arr[1].replace(";", "_") + "_-_" + arr[2].replace(";", "_");
                for (String prot : peptide.getProteinsSet()) {
                    if (!protein_peptide_Map.containsKey(prot)) {
                        protein_peptide_Map.put(prot, new LinkedHashSet<>());
                    }
                    protein_peptide_Map.get(prot).add(peptide);
                    if (fastaProteinsMap.containsKey(prot)) {
                        fastaProteinsMap.get(prot).addPeptideSequence(peptide.getModifiedSequence());
                    }
                }

                for (String modification : modificationMap.keySet()) {
                    if (peptide.getVariableModifications().contains(modification) || peptide.getFixedModifications().contains(modification)) {
                        Set<String> intersectSet = new LinkedHashSet<>();
                        intersectSet.addAll(Sets.intersection(peptide.getProteinsSet(), proteinsMap.keySet()));
                        modificationMap.get(modification).addAll(intersectSet);
                    }
                }

                peptidesMap.put(peptide.getModifiedSequence(), peptide);

            }
            for (Object protAcc : proteinsMap.keySet()) {
                String proteinAcc = protAcc + "";
                boolean addModification = false;
                for (Set<Comparable> accSet : modificationMap.values()) {
                    if (accSet.contains(proteinAcc)) {
                        addModification = true;
                        break;
                    }

                }
                if (!addModification) {
                    modificationMap.get("No Modification").add(proteinAcc);
                }
            }
            bufferedRandomAccessFile.close();
        } catch (IOException | NumberFormatException ex) {
            if (bufferedRandomAccessFile != null) {
                try {
                    bufferedRandomAccessFile.close();
                } catch (IOException ex1) {
                }
            }
        }
        return null;
    }

    public Set<ProteinObject> getRelatedProteinsSet(String proteinKey) {
        if (protein_relatedProteins_Map.containsKey(proteinKey)) {
            return protein_relatedProteins_Map.get(proteinKey);
        }
        return new HashSet<>();

    }

    public Set<PeptideObject> getPeptides(String proteinKey) {
        if (protein_peptide_Map.containsKey(proteinKey)) {
            return protein_peptide_Map.get(proteinKey);
        }
        return null;

    }

    public void setProteinFile(GalaxyFile proteinFile) {
        this.proteinFile = proteinFile;
    }

    public GalaxyFile getPeptideFile() {
        return peptideFile;
    }

    public void setPeptideFile(GalaxyFile peptideFile) {
        this.peptideFile = peptideFile;
    }

    public GalaxyFile getFastaFile() {
        return fastaFile;
    }

    public void setFastaFile(GalaxyFile fastaFile) {
        this.fastaFile = fastaFile;
    }

    public int getPsmNumber() {
        return psmNumber;
    }

    public GalaxyFastaFileReader getFastaFileReader() {
        return fastaFileReader;
    }

    public void setFastaFileReader(GalaxyFastaFileReader fastaFileReader) {
        this.fastaFileReader = fastaFileReader;
    }

    public void setPsmNumber(int psmNumber) {
        this.psmNumber = psmNumber;
    }

    public Map<String, Object> getSearchingParameters() {
        return searchingParameters;
    }

    public Map<String, Set<Comparable>> getPiMap() {
        return piMap;
    }

    public Map<String, Set<Comparable>> getProteinValidationMap() {
        return proteinValidationMap;
    }

    public String getVariableModification() {
        if (variableModification == null) {
            variableModification = (jsonToMap(searchingParameters.get("protein_modification_options").toString())).get("variable_modifications").replace("[", "").replace("]", "");
            if (variableModification != null && !variableModification.equalsIgnoreCase("null")) {
                variableModification = variableModification.replace("\"", "");
                String[] modArr = variableModification.split(",");
                for (String mod : modArr) {
                    modificationMap.put(mod, new LinkedHashSet<>());
                }
            } else {
                variableModification = "";
            }

        }
        return variableModification;
    }

    public String getFixedModification() {
        if (fixedModification == null) {
            fixedModification = (jsonToMap(searchingParameters.get("protein_modification_options").toString())).get("fixed_modifications").replace("[", "").replace("]", "").replace("\"", "").replace("\n", "");
            if (fixedModification != null && !fixedModification.equalsIgnoreCase("null")) {
                fixedModification = fixedModification.replace("\"", "");
                String[] modArr = fixedModification.split(",");
                for (String mod : modArr) {
                    modificationMap.put(mod, new LinkedHashSet<>());
                }
            } else {
                fixedModification = "";
            }

        }
        return fixedModification;
    }

    public void setSearchingParameters(Map<String, Object> searchingParameters) {
        this.searchingParameters = searchingParameters;

    }

    public int getPeptidesNumber() {
        return peptidesNumber;
    }

    public void setPeptidesNumber(int peptidesNumber) {
        this.peptidesNumber = peptidesNumber;
    }

    public int getProteinsNumber() {
        return proteinsNumber;
    }

    public void setProteinsNumber(int proteinsNumber) {
        this.proteinsNumber = proteinsNumber;
    }

//    public String getFastaFileIndex() {
//        return  fastaFileName.getIndexedFastafile();
//    }
//
    public String getFastaFileName() {
        return fastaFileName;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
    
    private final String projectName;

    public String getProjectName() {
        return projectName;
    }
    

    public PeptideShakerVisualizationDataset1(String projectName) {
        this.projectName = projectName;
        this.mgfFiles = new LinkedHashMap<>();
        mgfFilesIndexes = new LinkedHashMap<>();
        sequenceMatchingPreferences = SequenceMatchingPreferences.getDefaultSequenceMatching();
        enzymeFactory = EnzymeFactory.getInstance();
        fastaProteinsMap = new LinkedHashMap<>();
        modificationMap = new LinkedHashMap<>();
        modificationMap.put("No Modification", new LinkedHashSet<>());
        chromosomeMap = new LinkedHashMap<>();
        chromosomeMap.put("No Information", new LinkedHashSet<>());
        proteinPeptidesNumberMap = new TreeMap<>();
        proteinPSMNumberMap = new TreeMap<>();
        proteinCoverageMap = new TreeMap<>();
        piMap = new LinkedHashMap<>();
        piMap.put("No Information", new LinkedHashSet<>());
        proteinValidationMap = new LinkedHashMap<>();
        proteinValidationMap.put("No Information", new LinkedHashSet<>());
        proteinValidationMap.put("Confident", new LinkedHashSet<>());
        proteinValidationMap.put("Doubtful", new LinkedHashSet<>());
    }

    public TreeMap<Comparable, Set<Comparable>> getProteinPeptidesNumberMap() {
        return proteinPeptidesNumberMap;
    }

    public TreeMap<Comparable, Set<Comparable>> getProteinPSMNumberMap() {
        return proteinPSMNumberMap;
    }

    public TreeMap<Comparable, Set<Comparable>> getProteinCoverageMap() {
        return proteinCoverageMap;
    }

    public String getJobId() {
        return jobId.replace("_PS_", "");
    }

    public String getProteinFileId() {
        return proteinFileId;
    }

    public void setProteinFileId(String proteinFileId) {
        this.proteinFileId = proteinFileId;
    }

    public String getPeptideFileId() {
        return peptideFileId;
    }

    public void setPeptideFileId(String peptideFileId) {
        this.peptideFileId = peptideFileId;
    }

    public String getCpsId() {
        return cpsId;
    }

    public void setCpsId(String cpsId) {
        this.cpsId = cpsId;
    }

    public boolean isValidFile() {
        return true;
//        System.out.println("at files "+(proteinFileId == null )+"  "+(peptideFileId == null )+"||"+(searchGUIFileId == null )+"||"+( psmFileId == null )+"||"+( mgfFiles.isEmpty() )+"||"+( fastaFileName == null )+"||"+(mgfFilesIndexes.isEmpty()));
//        return !(proteinFileId == null || peptideFileId == null || fastaFileName == null/*|| searchGUIFileId == null || psmFileId == null || mgfFiles.isEmpty()  || mgfFilesIndexes.isEmpty()*/);
    }

    public String getPsmFileId() {
        return psmFileId;
    }

    public void setPsmFileId(String psmFileId) {
        this.psmFileId = psmFileId;
    }

    public String getSearchGUIFileId() {
        return searchGUIFileId;
    }

    public void setSearchGUIFileId(String searchGUIFileId) {
        this.searchGUIFileId = searchGUIFileId;
    }

    public Map<String, SystemDataSet> getMgfFiles() {
        return mgfFiles;
    }

    public void addMgfFiles(String mgfFileID, SystemDataSet mgfDs) {
        this.mgfFiles.put(mgfFileID, mgfDs);
    }

    public void setFastaFileId(String fastaFileId) {
        this.fastaFileId = fastaFileId;
    }

    public String getFastaFileId() {
        return fastaFileId;
    }

    public void setFastaFileName(String fastaFileName) {
        this.fastaFileName = fastaFileName;
    }

    public void addMGFFileIndex(String mgfFileId, String mgfFileIndex) {
        mgfFilesIndexes.put(mgfFileId, mgfFileIndex);
    }

    public ProteinObject updateProteinInformation(ProteinObject protein, String accession) {
        if (protein == null) {
            if (fastaProteinsMap.containsKey(accession)) {
                protein = fastaProteinsMap.get(accession);
            } else {
                protein = fastaFileReader.updateProteinInformation(protein, accession);
            }
        }
        if (enzyme != null) {
            for (String str : protein.getRelatedPeptidesList()) {
                protein.updatePeptideType(str, isEnzymaticPeptide(protein.getSequence(), peptidesMap.get(str).getSequence(), enzymeFactory.getEnzyme(enzyme), sequenceMatchingPreferences));
            }
        }

        return protein;

    }

    /**
     * Returns true of the peptide is non-enzymatic, i.e., has one or more end
     * points that cannot be caused by the enzyme alone. False means that both
     * the endpoints of the peptides could be caused by the selected enzyme, or
     * that it is a terminal peptide (where one end point is most likely not
     * enzymatic). Note that if a peptide maps to multiple locations in the
     * protein sequence this method returns true if one or more of these
     * peptides are enzymatic, even if not all mappings are enzymatic.
     *
     * @param peptideSequence the peptide sequence to check
     * @param enzyme the enzyme to use
     * @param sequenceMatchingPreferences the sequence matching preferences
     *
     * @return true of the peptide is non-enzymatic
     *
     * @throws IOException if an IOException occurs
     */
    public boolean isEnzymaticPeptide(String sequence, String peptideSequence, Enzyme enzyme, SequenceMatchingPreferences sequenceMatchingPreferences) {

        // get the surrounding amino acids
        HashMap<Integer, String[]> surroundingAminoAcids = getSurroundingAA(sequence, peptideSequence, 1, sequenceMatchingPreferences);

        String firstAA = peptideSequence.charAt(0) + "";
        String lastAA = peptideSequence.charAt(peptideSequence.length() - 1) + "";

        // iterate the possible extended peptide sequences
        for (int index : surroundingAminoAcids.keySet()) {

            String before = surroundingAminoAcids.get(index)[0];
            String after = surroundingAminoAcids.get(index)[1];

            // @TODO: how to handle semi-specific enzymes??
            if ((enzyme.isCleavageSite(before, firstAA) && enzyme.isCleavageSite(lastAA, after)
                    || (before.length() == 0 && enzyme.isCleavageSite(lastAA, after)
                    || (enzyme.isCleavageSite(before, firstAA) && after.length() == 0)))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the amino acids surrounding a peptide in the sequence of the
     * given protein in a map: peptide start index &gt; (amino acids before,
     * amino acids after).
     *
     * @param peptide the sequence of the peptide of interest
     * @param nAA the number of amino acids to include
     * @param sequenceMatchingPreferences the sequence matching preferences
     *
     * @return the amino acids surrounding a peptide in the protein sequence
     *
     * @throws IOException Exception thrown whenever an error occurred while
     * parsing the protein sequence
     */
    private HashMap<Integer, String[]> getSurroundingAA(String sequence, String peptide, int nAA, SequenceMatchingPreferences sequenceMatchingPreferences) {

        ArrayList<Integer> startIndexes = getPeptideStart(sequence, peptide, sequenceMatchingPreferences);
        HashMap<Integer, String[]> result = new HashMap<>();

        for (int startIndex : startIndexes) {

            result.put(startIndex, new String[2]);
            String subsequence = "";

            int stringIndex = startIndex - 1;
            for (int aa = stringIndex - nAA; aa < stringIndex; aa++) {
                if (aa >= 0 && aa < sequence.length()) {
                    subsequence += sequence.charAt(aa);
                }
            }

            result.get(startIndex)[0] = subsequence;
            subsequence = "";

            for (int aa = stringIndex + peptide.length(); aa < stringIndex + peptide.length() + nAA; aa++) {
                if (aa >= 0 && aa < sequence.length()) {
                    subsequence += sequence.charAt(aa);
                }
            }

            result.get(startIndex)[1] = subsequence;
        }

        return result;
    }

    /**
     * Returns the list of indexes where a peptide can be found in the protein
     * sequence. 1 is the first amino acid.
     *
     * @param peptideSequence the sequence of the peptide of interest
     * @param sequenceMatchingPreferences the sequence matching preferences
     *
     * @return the list of indexes where a peptide can be found in a protein
     * sequence
     */
    private ArrayList<Integer> getPeptideStart(String sequence, String peptideSequence, SequenceMatchingPreferences sequenceMatchingPreferences) {
        AminoAcidPattern aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString(peptideSequence);
        return aminoAcidPattern.getIndexes(sequence, sequenceMatchingPreferences);
    }

    public ModificationMatrix getModificationMatrix() {
        if (modificationMatrix != null) {
            return modificationMatrix;
        }
        modificationMatrix = new ModificationMatrix(modificationMap);
        return modificationMatrix;
    }

    private HashMap<String, String> jsonToMap(String t) {

        try {
            HashMap<String, String> map = new HashMap<>();
            JSONObject jObject = new JSONObject(t);
            Iterator<?> keys = jObject.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                String value = jObject.getString(key);
                map.put(key, value);

            }

            return map;
        } catch (JSONException ex) {
            Logger.getLogger(PeptideShakerVisualizationDataset1.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void setAvailableOnNels(boolean availableOnNels) {
        super.setAvailableOnNels(availableOnNels); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isAvailableOnNels() {
        if (proteinFile != null && peptideFile != null && psmFile != null) {
            return (proteinFile.isAvailableOnNels() && peptideFile.isAvailableOnNels() && psmFile.isAvailableOnNels()); //To change body of generated methods, choose Tools | Templates.
        }
        return false;

    }

    @Override
    public void setAvailableOnGalaxy(boolean availableOnGalaxy) {
        super.setAvailableOnGalaxy(availableOnGalaxy); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isAvailableOnGalaxy() {
        return super.isAvailableOnGalaxy(); //To change body of generated methods, choose Tools | Templates.
    }

}
