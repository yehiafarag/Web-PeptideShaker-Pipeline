package com.uib.web.peptideshaker.galaxy.dataobjects;

import com.uib.web.peptideshaker.galaxy.dataobjects.SystemDataSet;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import uk.ac.ebi.pride.tools.braf.BufferedRandomAccessFile;

/**
 * This class represents dataset visualization that store data for viewing files
 * on web
 *
 * @author Yehia Farag
 */
public class PeptideShakerVisualizationDataset extends SystemDataSet {

    private Map<Object, ProteinObject> proteinsMap;
    private Map<String, Set<PeptideObject>> protein_peptide_Map;
    private Map<String, Set<ProteinObject>> protein_relatedProteins_Map;
    private Map<Object, PeptideObject> peptidesMap;

    public ProteinObject getProtein(Object proteinKey) {
        if (proteinsMap.containsKey(proteinKey)) {
            return proteinsMap.get(proteinKey);
        } else {
            return null;
        }

    }

    public Map<Object, ProteinObject> getProteinsMap() {
        if (proteinsMap != null) {
            return proteinsMap;
        }
        protein_relatedProteins_Map = new HashMap<>();
        proteinsMap = new LinkedHashMap<>();
        BufferedRandomAccessFile bufferedRandomAccessFile = null;
        try {//           
            bufferedRandomAccessFile = new BufferedRandomAccessFile(proteinFile.getFile(), "r", 1024 * 100);
            String line;
            /**
             * escape header
             */
            bufferedRandomAccessFile.getNextLine();
            int index = 0;
            while ((line = bufferedRandomAccessFile.getNextLine()) != null) {
                String[] arr = line.split("\\t");
//                Object[] obj = new Object[]{Integer.valueOf(arr[0]), arr[1], arr[2], arr[3], Double.valueOf(arr[5]), Double.valueOf(arr[6]), Integer.valueOf(arr[16])};
                ProteinObject protein = new ProteinObject();
                protein.setIndex(Integer.valueOf(arr[0]));
                protein.setAccession(arr[1]);
                protein.setDescription(arr[2]);
                protein.setGeneName(arr[3]);
                protein.setChromosome(arr[4]);

                protein.setMW(Double.valueOf(arr[5]));
                protein.setPossibleCoverage(Double.valueOf(arr[6]));
                protein.setCoverage(Double.valueOf(arr[7]));
                protein.setSpectrumCounting(Double.valueOf(arr[8]));
                protein.setConfidentlyLocalizedModificationSites(arr[9]);
                protein.setConfidentlyLocalizedModificationSitesNumber(arr[10]);
                protein.setAmbiguouslyLocalizedModificationSites(arr[11]);
                protein.setAmbiguouslyLocalizedModificationSitesNumber(arr[12]);
                protein.setProteinInference(arr[13]);
                protein.setSecondaryAccessions(arr[14]);

                protein.setProteinGroup(arr[15]);
                protein.setValidatedPeptidesNumber(Integer.parseInt(arr[16]));
                protein.setPeptidesNumber(Integer.parseInt(arr[17]));
                protein.setUniqueNumber(Integer.parseInt(arr[18]));
                protein.setValidatedUniqueNumber(Integer.parseInt(arr[19]));
                protein.setUniqueToGroupNumber(Integer.parseInt(arr[20]));
                protein.setValidatedUniqueToGroupNumber(Integer.valueOf(arr[21]));
                protein.setValidatedPSMsNumber(Integer.valueOf(arr[22]));
                protein.setConfidence(Double.valueOf(arr[23]));
                protein.setValidation(arr[24]);
                proteinsMap.put(protein.getAccession(), protein);
                for (String acc : protein.getProteinGroupSet()) {
                    if (!protein_relatedProteins_Map.containsKey(acc)) {
                        Set<ProteinObject> protenHashSet = new LinkedHashSet<>();
                        protein_relatedProteins_Map.put(acc, protenHashSet);
                    }
                    protein_relatedProteins_Map.get(acc).add(protein);
                }

//                if (!protein_relatedProteins_Map.containsKey(arr[1])) {
//                    Set<String> relatedProteins = new HashSet<>();
//                    protein_relatedProteins_Map.put(arr[1], relatedProteins);
//                }
//                Set<String> relatedProteins = protein_relatedProteins_Map.get(arr[1]);
//                if (arr[13].equalsIgnoreCase("Related Proteins")) {
//                    relatedProteins.addAll(Arrays.asList(arr[14].replace(" ", "").split(","))); 
//                    
//                }
//                relatedProteins.addAll(Arrays.asList(arr[15].replace(" ", "").split(",")));
//                relatedProteins.add(arr[1]);
//               
//                protein_relatedProteins_Map.put(arr[1], relatedProteins);
                index++;
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

                peptide.setConfidence(Double.parseDouble(arr[14]));
                peptide.setValidation(arr[6]);

//                Object[] obj = new Object[]{Integer.valueOf(arr[0]), arr[1], arr[2], Integer.valueOf(arr[3]), Integer.valueOf(arr[4]), (arr[5]), (arr[10]), (arr[11]), Integer.valueOf(arr[14]), Double.valueOf(arr[15]), arr[16]};
//                String key = arr[0] + "_-_" + arr[1].replace(";", "_") + "_-_" + arr[2].replace(";", "_");
                for (String prot : peptide.getProteinsSet()) {
                    if (!protein_peptide_Map.containsKey(prot)) {
                        protein_peptide_Map.put(prot, new LinkedHashSet<>());
                    }
                    protein_peptide_Map.get(prot).add(peptide);
                }

                peptidesMap.put(peptide.getSequence(), peptide);

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

    private GalaxyFile proteinFile;
    private GalaxyFile peptideFile;

    private String proteinFileId;
    private String peptideFileId;
    private String cpsId;
    private String psmFileId;
    private final String jobId;
    private String searchGUIFileId;
    private final Map<String, String> mgfFiles;
    private String fastaFileId;
    private String fastaFileIndex;
    private final Map<String, String> mgfFilesIndexes;
    private String fastaFileName;
    private int proteinsNumber;
    private int psmNumber;

    public int getPsmNumber() {
        return psmNumber;
    }

    public void setPsmNumber(int psmNumber) {
        this.psmNumber = psmNumber;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
    private int peptidesNumber;
    private Map<String, Object> parameters;

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

    public String getFastaFileIndex() {
        return fastaFileIndex;
    }

    public String getFastaFileName() {
        return fastaFileName;
    }

    public void setFastaFileIndex(String fastaFileIndex) {
        this.fastaFileIndex = fastaFileIndex;
    }

    public PeptideShakerVisualizationDataset(String jobId) {
        this.jobId = jobId;
        this.mgfFiles = new LinkedHashMap<>();
        mgfFilesIndexes = new LinkedHashMap<>();
    }

    public String getJobId() {
        return jobId;
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
        return !(proteinFileId == null || peptideFileId == null || searchGUIFileId == null || psmFileId == null || mgfFiles.isEmpty() || fastaFileId == null || fastaFileIndex == null || mgfFilesIndexes.isEmpty());
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

    public Map<String, String> getMgfFiles() {
        return mgfFiles;
    }

    public void addMgfFiles(String mgfFileID, String mgfFileName) {
        this.mgfFiles.put(mgfFileID, mgfFileName);
    }

    public String getFastaFileId() {
        return fastaFileId;
    }

    public void setFastaFile(String fastaFileId, String fastaFileName) {
        this.fastaFileId = fastaFileId;
        this.fastaFileName = fastaFileName;
    }

    public void addMGFFileIndex(String mgfFileId, String mgfFileIndex) {
        mgfFilesIndexes.put(mgfFileId, mgfFileIndex);
    }

}
