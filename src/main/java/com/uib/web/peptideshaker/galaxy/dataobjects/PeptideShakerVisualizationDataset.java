package com.uib.web.peptideshaker.galaxy.dataobjects;

import com.compomics.util.experiment.biology.AminoAcidPattern;
import com.compomics.util.experiment.biology.Enzyme;
import com.compomics.util.experiment.biology.EnzymeFactory;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.identification_parameters.SearchParameters;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.experiment.identification.spectrum_assumptions.PeptideAssumption;
import com.compomics.util.experiment.io.massspectrometry.MgfIndex;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.io.SerializationUtils;
import com.compomics.util.preferences.IdentificationParameters;
import com.compomics.util.preferences.SequenceMatchingPreferences;
import com.google.common.collect.Sets;
import com.uib.web.peptideshaker.galaxy.GalaxyDatasetServingUtil;
import com.uib.web.peptideshaker.model.core.ModificationMatrix;
import com.uib.web.peptideshaker.presenter.pscomponents.SpectrumInformation;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import uk.ac.ebi.pride.tools.braf.BufferedRandomAccessFile;

/**
 * This class represents dataset visualisation that store data for viewing files
 * on web
 *
 * @author Yehia Farag
 */
public class PeptideShakerVisualizationDataset extends SystemDataSet implements Comparable<PeptideShakerVisualizationDataset> {

    private final String projectName;
    private final File user_folder;
    private final String galaxyUrl;
    private final String apiKey;
    private String jobId;
    private String searchGUIFileId;
    private Map<String, Object> searchingParameters;
    private String orgFastaFileId;
    private String orgFastaFileName;
    private final Map<String, SystemDataSet> mgfFiles;
    private String zipFileId;
    private String file_ext;
    private GalaxyFile fasta_file;
    private GalaxyFile proteins_file;
    private GalaxyFile peptides_file;
    private GalaxyFile psm_file;
    private ModificationMatrix modificationMatrix;
    private String enzyme;
    private GalaxyFastaFileReader fastaFileReader;
    private final Map<String, GalaxyFile> mgfFilesIndexers;
    private final Map<String, MgfIndex> importedMgfFilesIndexers;
    private Map<String, ProteinObject> proteinsMap;
    private Map<String, Set<ProteinObject>> protein_relatedProteins_Map;
    private final Map<Object, ProteinObject> fastaProteinsMap;
    private final String[] proteinEvedence = new String[]{"Not Available", "Protein", "Transcript", "Homology", "Predicted", "Uncertain"};
    private final Map<Integer, Set<Comparable>> chromosomeMap;
    private final TreeMap<Comparable, Set<Comparable>> proteinCoverageMap;
    private final Map<String, Set<Comparable>> PIMap;
    private final TreeMap<Comparable, Set<Comparable>> proteinPeptidesNumberMap;
    private final TreeMap<Comparable, Set<Comparable>> proteinPSMNumberMap;
    private final Map<String, Set<Comparable>> proteinValidationMap;
    private Map<Object, PeptideObject> peptidesMap;
    private Map<String, Set<PeptideObject>> protein_peptide_Map;
    private final Map<String, Set<Comparable>> modificationMap;

    private final SequenceMatchingPreferences sequenceMatchingPreferences;
    private final EnzymeFactory enzymeFactory;

    private Map<String, List<PSMObject>> psmMap;
    private Map<Object, PSMObject> psmIndexMap;
    private final GalaxyDatasetServingUtil galaxyDatasetServingUtil;

    private IdentificationParameters identificationParameters;
    private List<String> outputsIds;

    private Date jobDate;

    public List<String> getOutputsIds() {
        return outputsIds;
    }

    public void setOutputsIds(List<String> outputsIds) {
        this.outputsIds = outputsIds;
    }

//
//
//    private GalaxyFile peptideFile;
//    private GalaxyFile fastaFile;
//    private GalaxyFile proteinFile;
//    private GalaxyFile psmFile;
//
//    private String proteinFileId;
//    private String peptideFileId;
//    private String cpsId;
//    private String psmFileId;
//    
//    
//   
//    private final Map<String, String> mgfFilesIndexes;
//    private String fastaFileName;
//    private String fastaFileId;
//    
//
    public PeptideShakerVisualizationDataset(String projectName, File user_folder, String galaxyUrl, String apiKey, GalaxyDatasetServingUtil galaxyDatasetServingUtil) {
        this.projectName = projectName;
        this.user_folder = user_folder;
        this.galaxyUrl = galaxyUrl;
        this.apiKey = apiKey;
        this.mgfFiles = new LinkedHashMap<>();
        this.mgfFilesIndexers = new LinkedHashMap<>();
        this.importedMgfFilesIndexers = new LinkedHashMap<>();
        this.fastaProteinsMap = new LinkedHashMap<>();
        this.chromosomeMap = new LinkedHashMap<>();
        this.chromosomeMap.put(-2, new LinkedHashSet<>());
        this.proteinCoverageMap = new TreeMap<>();
        this.PIMap = new LinkedHashMap<>();
        this.PIMap.put("No Information", new LinkedHashSet<>());
        this.proteinPeptidesNumberMap = new TreeMap<>();
        this.proteinPSMNumberMap = new TreeMap<>();

        this.proteinValidationMap = new LinkedHashMap<>();
        this.proteinValidationMap.put("No Information", new LinkedHashSet<>());
        this.proteinValidationMap.put("Confident", new LinkedHashSet<>());
        this.proteinValidationMap.put("Doubtful", new LinkedHashSet<>());
        this.modificationMap = new LinkedHashMap<>();
        this.modificationMap.put("No Modification", new LinkedHashSet<>());
        this.sequenceMatchingPreferences = SequenceMatchingPreferences.getDefaultSequenceMatching();
        this.enzymeFactory = EnzymeFactory.getInstance();

        this.galaxyDatasetServingUtil = galaxyDatasetServingUtil;
//        
//        
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobId() {
        return jobId.replace("_PS_", "");
    }

    public String getProjectName() {
        return projectName;
    }

    public String getSearchGUIFileId() {
        return searchGUIFileId;
    }

    public void setSearchGUIFileId(String searchGUIFileId) {
        this.searchGUIFileId = searchGUIFileId;
        //init fasta file 
        SystemDataSet ds = new SystemDataSet();
        ds.setName(this.projectName + "-Param");
        ds.setType("Param File");
        ds.setGalaxyId(searchGUIFileId + "__SEARCHGUI_IdentificationParameters.par");
        ds.setDownloadUrl(galaxyUrl + "/api/histories/" + this.getHistoryId() + "/contents/" + searchGUIFileId + "/display?key=" + apiKey);
        GalaxyFile file = new GalaxyFile(user_folder, ds, true);
        file.setDownloadUrl("to_ext=" + file_ext);
        SearchParameters searchParameters;
        try {
            searchParameters = SearchParameters.getIdentificationParameters(file.getFile());
            searchParameters.setFastaFile(null);
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
            return;
        }

        this.identificationParameters = new IdentificationParameters(searchParameters);
    }

    public void setSearchingParameters(Map<String, Object> searchingParameters) {
        this.searchingParameters = searchingParameters;
        this.orgFastaFileId = ((Map<String, Object>) searchingParameters.get("input_database")).get("id").toString();

    }

    public String getOrgFastaFileId() {
        return orgFastaFileId;
    }

    public Map<String, Object> getSearchingParameters() {
        return searchingParameters;
    }

    public Map<String, SystemDataSet> getMgfFiles() {
        return mgfFiles;
    }

    public void addMgfFiles(String mgfFileID, SystemDataSet mgfDs) {
        this.mgfFiles.put(mgfFileID, mgfDs);
    }

    public String getFile_ext() {
        return file_ext;
    }

    public void setFile_ext(String file_ext) {
        this.file_ext = file_ext;
    }

    public String getZipFileId() {
        return zipFileId;
    }

    public void setZipFileId(String zipFileId) {
        if (this.zipFileId == null) {
            this.initialiseDataFiles(zipFileId);
        }
        this.zipFileId = zipFileId;

    }

    private void initialiseDataFiles(String zipFileId) {

        //validate zipFile
        SystemDataSet ds = new SystemDataSet();
        ds.setName(this.projectName + "-ZIP");
        ds.setType("ZIP File");
        ds.setGalaxyId(zipFileId);
        ds.setDownloadUrl(galaxyUrl + "/api/histories/" + this.getHistoryId() + "/contents/" + zipFileId + "/display?key=" + apiKey);
        GalaxyFile zip_file = new GalaxyFile(user_folder, ds, true);
        zip_file.setDownloadUrl("to_ext=" + file_ext);
        Set<String> filesList = zip_file.getFileInformation();

        boolean valid = false;
        if (!filesList.contains("data/") || !filesList.contains("reports/") || !filesList.contains("data/input_database.fasta")) {
            valid = false;
            return;
        }
        filesList.remove("data/");
        filesList.remove("reports/");
        filesList.remove("data/input_database.fasta");
        boolean prot = false;
        boolean pep = false;
        boolean psm = false;
        for (String fileName : filesList) {
            if (fileName.endsWith("Default_Protein_Report.txt")) {
                prot = true;
            } else if (fileName.endsWith("Default_Peptide_Report.txt")) {
                pep = true;
            } else if (fileName.endsWith("Default_PSM_Report.txt")) {
                psm = true;
            }

        }
        if (!prot || !pep || !psm) {
            valid = false;
            return;
        }
        //init fasta file 
        ds = new SystemDataSet();
        ds.setName(this.projectName + "-FASTA");
        ds.setType("FASTA File");
        ds.setGalaxyId(zipFileId + "__data/input_database.fasta");
        ds.setDownloadUrl(galaxyUrl + "/api/histories/" + this.getHistoryId() + "/contents/" + zipFileId + "/display?key=" + apiKey);
        fasta_file = new GalaxyFile(user_folder, ds, true);
        fasta_file.setDownloadUrl("to_ext=" + file_ext);
//        fasta_file.getFile();
        //init protein file

        ds = new SystemDataSet();
        ds.setName(this.projectName + "-PROTEINS");
        ds.setType("Protein File");
        ds.setGalaxyId(zipFileId + "__reports/Default_Protein_Report.txt");
        ds.setDownloadUrl(galaxyUrl + "/api/histories/" + this.getHistoryId() + "/contents/" + zipFileId + "/display?key=" + apiKey);
        proteins_file = new GalaxyFile(user_folder, ds, true);
        proteins_file.setDownloadUrl("to_ext=" + file_ext);
//        proteins_file.getFile();

        //init peptides file
        ds = new SystemDataSet();
        ds.setName(this.projectName + "-PEPTIDES");
        ds.setType("Peptides File");
        ds.setGalaxyId(zipFileId + "__reports/Default_Peptide_Report.txt");
        ds.setDownloadUrl(galaxyUrl + "/api/histories/" + this.getHistoryId() + "/contents/" + zipFileId + "/display?key=" + apiKey);
        peptides_file = new GalaxyFile(user_folder, ds, true);
        peptides_file.setDownloadUrl("to_ext=" + file_ext);
//        peptides_file.getFile();

        ds = new SystemDataSet();
        ds.setName(this.projectName + "-PSM");
        ds.setType("PSM File");
        ds.setGalaxyId(zipFileId + "__reports/Default_PSM_Report.txt");
        ds.setDownloadUrl(galaxyUrl + "/api/histories/" + this.getHistoryId() + "/contents/" + zipFileId + "/display?key=" + apiKey);
        psm_file = new GalaxyFile(user_folder, ds, true);
        psm_file.setDownloadUrl("to_ext=" + file_ext);
//        psm_file.getFile();
        for (String key : filesList) {
            if (key.endsWith("mgf.cui")) {
                ds = new SystemDataSet();
                ds.setName(this.projectName + "-CUI");
                ds.setType("MGF Index File");
                ds.setGalaxyId(zipFileId + "__" + key);
                ds.setDownloadUrl(galaxyUrl + "/api/histories/" + this.getHistoryId() + "/contents/" + zipFileId + "/display?key=" + apiKey);
                GalaxyFile index_file = new GalaxyFile(user_folder, ds, true);
                index_file.setDownloadUrl("to_ext=" + file_ext);
                mgfFilesIndexers.put(key, index_file);
//                index_file.getFile();
            }

        }

    }

    public void setOrgFastaFileName(String orgFastaFileName) {
        this.orgFastaFileName = orgFastaFileName;
    }

    public GalaxyFile getFastaFile() {
        return fasta_file;
    }
//

    public GalaxyFile getPsmFile() {
        return psm_file;
    }

    public GalaxyFile getPeptideFile() {
        return peptides_file;
    }

    public String getFastaFileName() {
        if (orgFastaFileName != null) {
            return orgFastaFileName;
        }

        return fasta_file.getName();
    }

    public String getVariableModification() {

        String variableModification = "";
        try {
            variableModification = jsonToMap(new JSONObject(searchingParameters.get("protein_modification_options").toString())).get("variable_modifications").toString().replace("[", "").replace("]", "");
            for (String mod : variableModification.split(",")) {
                modificationMap.put(mod.trim(), new LinkedHashSet<>());
            }
        } catch (JSONException ex) {

        }
        return variableModification;
    }

    public String getFixedModification() {
        String fixedModification = "";
        try {
            fixedModification = jsonToMap(new JSONObject(searchingParameters.get("protein_modification_options").toString())).get("fixed_modifications").toString().replace("[", "").replace("]", "");
            for (String mod : fixedModification.split(",")) {
                modificationMap.put(mod.trim(), new LinkedHashSet<>());
            }
        } catch (JSONException ex) {

        }
        return fixedModification;

    }

    private Map<String, Object> jsonToMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = jsonToMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    private List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = jsonToMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    public void setEnzyme(String enzyme) {
        this.enzyme = enzyme;
    }

    public boolean isValidFile() {
        return (searchingParameters != null && zipFileId != null && mgfFilesIndexers.size() == mgfFiles.size());
//        System.out.println("at files "+(proteinFileId == null )+"  "+(peptideFileId == null )+"||"+(searchGUIFileId == null )+"||"+( psmFileId == null )+"||"+( mgfFiles.isEmpty() )+"||"+( fastaFileName == null )+"||"+(mgfFilesIndexes.isEmpty()));
//        return !(proteinFileId == null || peptideFileId == null || fastaFileName == null/*|| searchGUIFileId == null || psmFileId == null || mgfFiles.isEmpty()  || mgfFilesIndexes.isEmpty()*/);
    }
    private double maxMW = Double.MIN_VALUE;
    private double maxMS2Quant = Double.MIN_VALUE;
    private int maxPeptideNumber = Integer.MIN_VALUE;
    private int maxPsmNumber = Integer.MIN_VALUE;

    public Map<String, ProteinObject> getProteinsMap() {
        if (proteinsMap != null) {
            return proteinsMap;
        }
        protein_relatedProteins_Map = new HashMap<>();
        proteinsMap = new LinkedHashMap<>();
        readFastaFile();
        BufferedRandomAccessFile bufferedRandomAccessFile = null;
        //for testing
//        Random r = new Random();
//        int[] chrom = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24};

        try {//           
            bufferedRandomAccessFile = new BufferedRandomAccessFile(proteins_file.getFile(), "r", 1024 * 100);
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
                int chrIndex = -1;
                try {
                    chrIndex = Integer.parseInt(protein.getChromosome());
                } catch (NumberFormatException ex) {
                    if (protein.getChromosome().contains("HSCHR")) {
                        chrIndex = Integer.parseInt(protein.getChromosome().split("HSCHR")[1].split("_")[0].replaceAll("[\\D]", ""));
                    } else if (protein.getChromosome().equalsIgnoreCase("X")) {
                        chrIndex = 23;
                    } else if (protein.getChromosome().equalsIgnoreCase("Y")) {
                        chrIndex = 24;
                    }

                }
                protein.setChromosomeIndex(chrIndex);

//                protein.setChromosome(chrom[r.nextInt(10)] + "");
                if (protein.getChromosome().trim().isEmpty()) {
                    protein.setChromosome("No Information");
                    protein.setChromosomeIndex(-2);
                    chromosomeMap.get(protein.getChromosomeIndex()).add(protein.getAccession());
                } else {
                    if (!chromosomeMap.containsKey(protein.getChromosomeIndex())) {
                        chromosomeMap.put(protein.getChromosomeIndex(), new LinkedHashSet<>());
                    }
                    chromosomeMap.get(protein.getChromosomeIndex()).add(protein.getAccession());
                }

                protein.setMW(Double.valueOf(arr[5]));
                if (protein.getMW() > maxMW) {
                    maxMW = protein.getMW();
                }
                protein.setPossibleCoverage(Double.valueOf(arr[6]));
                protein.setCoverage(Double.valueOf(arr[7]));
                int pc = (int) Math.round(protein.getPossibleCoverage());
                if (!proteinCoverageMap.containsKey(pc)) {
                    proteinCoverageMap.put(pc, new LinkedHashSet<>());
                }
                proteinCoverageMap.get(pc).add(protein.getAccession());
                protein.setSpectrumCounting(Double.valueOf(arr[8]));
                if (protein.getSpectrumCounting() > maxMS2Quant) {
                    maxMS2Quant = protein.getSpectrumCounting();
                }
                protein.setConfidentlyLocalizedModificationSites(arr[9]);
                protein.setConfidentlyLocalizedModificationSitesNumber(arr[10]);
                protein.setAmbiguouslyLocalizedModificationSites(arr[11]);
                protein.setAmbiguouslyLocalizedModificationSitesNumber(arr[12]);
                protein.setProteinInference(arr[13].replace("Proteins", "").replace("and", "&"));

                if (protein.getProteinInference().trim().isEmpty()) {
                    protein.setProteinInference("No Information");
                    PIMap.get(protein.getProteinInference()).add(protein.getAccession());
                } else {
                    if (!PIMap.containsKey(protein.getProteinInference())) {
                        PIMap.put(protein.getProteinInference(), new LinkedHashSet<>());
                    }
                    PIMap.get(protein.getProteinInference()).add(protein.getAccession());
                }

                protein.setSecondaryAccessions(arr[14]);

                protein.setProteinGroup(arr[15]);
                protein.setValidatedPeptidesNumber(Integer.parseInt(arr[16]));
                if (protein.getValidatedPeptidesNumber() > maxPeptideNumber) {
                    maxPeptideNumber = protein.getValidatedPeptidesNumber();
                }
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
                if (protein.getValidatedPSMsNumber() > maxPsmNumber) {
                    maxPsmNumber = protein.getValidatedPSMsNumber();
                }
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
                protein.getProteinGroupSet().stream().map((acc) -> {
                    if (!protein_relatedProteins_Map.containsKey(acc)) {
                        Set<ProteinObject> protenHashSet = new LinkedHashSet<>();
                        protein_relatedProteins_Map.put(acc, protenHashSet);
                    }
                    return acc;
                }).forEachOrdered((acc) -> {
                    protein_relatedProteins_Map.get(acc).add(protein);
                });
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

    public double getMaxMW() {
        return maxMW;
    }

    public double getMaxMS2Quant() {
        return maxMS2Quant;
    }

    public int getMaxPeptideNumber() {
        return maxPeptideNumber;
    }

    public int getMaxPsmNumber() {
        return maxPsmNumber;
    }

    private void readFastaFile() {
        fastaProteinsMap.clear();
        BufferedRandomAccessFile bufferedRandomAccessFile = null;
        try {//           
            bufferedRandomAccessFile = new BufferedRandomAccessFile(fasta_file.getFile(), "r", 1024 * 100);
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
        this.psmMap = new LinkedHashMap<>();
        this.psmIndexMap = new LinkedHashMap<>();
        BufferedRandomAccessFile bufferedRandomAccessFile = null;
        try {//           
            bufferedRandomAccessFile = new BufferedRandomAccessFile(peptides_file.getFile(), "r", 1024 * 100);
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
                psmMap.put(peptide.getModifiedSequence(), new ArrayList<>());

//                Object[] obj = new Object[]{Integer.valueOf(arr[0]), arr[1], arr[2], Integer.valueOf(arr[3]), Integer.valueOf(arr[4]), (arr[5]), (arr[10]), (arr[11]), Integer.valueOf(arr[14]), Double.valueOf(arr[15]), arr[16]};
//                String key = arr[0] + "_-_" + arr[1].replace(";", "_") + "_-_" + arr[2].replace(";", "_");
                peptide.getProteinsSet().stream().map((prot) -> {
                    if (!protein_peptide_Map.containsKey(prot)) {
                        protein_peptide_Map.put(prot, new LinkedHashSet<>());
                    }
                    return prot;
                }).map((prot) -> {
                    protein_peptide_Map.get(prot).add(peptide);
                    return prot;
                }).filter((prot) -> (fastaProteinsMap.containsKey(prot))).forEachOrdered((prot) -> {
                    fastaProteinsMap.get(prot).addPeptideSequence(peptide.getModifiedSequence());
                });

                modificationMap.keySet().stream().filter((modification) -> (peptide.getVariableModifications().contains(modification) || peptide.getFixedModifications().contains(modification))).forEachOrdered((modification) -> {
                    Set<String> intersectSet = new LinkedHashSet<>();
                    intersectSet.addAll(Sets.intersection(peptide.getProteinsSet(), proteinsMap.keySet()));
                    modificationMap.get(modification).addAll(intersectSet);
                });

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
            this.initPsmMap();
        } catch (IOException | NumberFormatException ex) {
            ex.printStackTrace();
            if (bufferedRandomAccessFile != null) {
                try {
                    bufferedRandomAccessFile.close();
                } catch (IOException ex1) {
                }
            }
        }

        return null;
    }

    private void initPsmMap() {
//        if (psmMap != null) {
//            return psmMap;
//        }
        BufferedRandomAccessFile bufferedRandomAccessFile = null;
        try {//           
            bufferedRandomAccessFile = new BufferedRandomAccessFile(psm_file.getFile(), "r", 1024 * 100);
            String line;
            /**
             * escape header
             */
            bufferedRandomAccessFile.getNextLine();
            while ((line = bufferedRandomAccessFile.getNextLine()) != null) {
                String[] arr = line.split("\\t");
                PSMObject psm = new PSMObject();
                psm.setIndex(Integer.parseInt(arr[0]));
                for (String acc : arr[1].split(",")) {
                    psm.addProtein(acc);
                }
                psm.setSequence(arr[2]);
                psm.setAasBefore(arr[3]);
                psm.setAasAfter((arr[4]));
                psm.setPostions(arr[5]);
                psm.setModifiedSequence(arr[6]);
                for (String mod : arr[7].split(",")) {
                    psm.addVariableModification(mod);
                }
                for (String mod : arr[8].split(",")) {
                    psm.addFixedModification(mod);
                }
                psm.setSpectrumFile(arr[9]);
                psm.setSpectrumTitle(arr[10]);
                psm.setSpectrumScanNumber(arr[11]);
                psm.setRT(arr[12]);
                psm.setMZ(arr[13]);
                psm.setMeasuredCharge((arr[14]));
                psm.setIdentificationCharge((arr[15]));
                if (!arr[16].equalsIgnoreCase("")) {
                    psm.setTheoriticalMass(Double.parseDouble(arr[16]));
                }
                if (!arr[17].equalsIgnoreCase("")) {
                    psm.setIsotopNumber(Integer.parseInt(arr[17]));
                }
                if (!arr[18].equalsIgnoreCase("")) {
                    psm.setPrecursorMZError_PPM(Double.parseDouble(arr[18]));
                }
                psm.setLocalizationConfidence(arr[19]);

                psm.setProbabilisticPTMScore((arr[20]));

                psm.setD_Score((arr[21]));

                if (!arr[22].equalsIgnoreCase("")) {
                    psm.setConfidence(Double.parseDouble(arr[22]));
                }
                psm.setValidation(arr[23]);

                if (psmMap.containsKey(psm.getModifiedSequence())) {
                    psmMap.get(psm.getModifiedSequence()).add(psm);
                } else if (psmMap.containsKey(psm.getModifiedSequence().replace("L", "I"))) {
                    System.out.println("at Error for psm mapping...not exist peptide need to replace L" + psm.getModifiedSequence());
                } else if (psmMap.containsKey(psm.getModifiedSequence().replace("I", "L"))) {
                    System.out.println("at Error for psm mapping...not exist peptide need to replace I" + psm.getModifiedSequence());
                } else {
                    System.out.println("at Error for psm mapping...not exist peptide " + psm.getModifiedSequence());
                }
                psmIndexMap.put(psm.getIndex(), psm);

            }
            bufferedRandomAccessFile.close();

        } catch (IOException | NumberFormatException ex) {
            ex.printStackTrace();
            if (bufferedRandomAccessFile != null) {
                try {
                    bufferedRandomAccessFile.close();
                } catch (IOException ex1) {
                }
            }
        }
    }

    public ModificationMatrix getModificationMatrix() {
        if (modificationMatrix != null) {
            return modificationMatrix;
        }
        modificationMatrix = new ModificationMatrix(modificationMap);
        return modificationMatrix;
    }

    public Map<Integer, Set<Comparable>> getChromosomeMap() {
        return chromosomeMap;
    }

    public Map<String, Set<Comparable>> getPiMap() {
        return PIMap;
    }

    public Map<String, Set<Comparable>> getProteinValidationMap() {
        return proteinValidationMap;
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

    public ProteinObject getProtein(String proteinKey) {
        if (proteinsMap.containsKey(proteinKey)) {
            return proteinsMap.get(proteinKey);
        } else {
            ProteinObject newRelatedProt = updateProteinInformation(null, proteinKey);
            return newRelatedProt;
        }

    }

    public ProteinObject updateProteinInformation(ProteinObject protein, String accession) {
        if (protein == null) {
            if (fastaProteinsMap.containsKey(accession)) {
                protein = fastaProteinsMap.get(accession);
            } else {
                if (fastaFileReader == null) {
                    fastaFileReader = new GalaxyFastaFileReader();
                }
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
     * @param sequence
     * @param peptideSequence the peptide sequence to check
     * @param enzyme the enzyme to use
     * @param sequenceMatchingPreferences the sequence matching preferences
     *
     * @return true of the peptide is non-enzymatic
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

        startIndexes.stream().map((startIndex) -> {
            result.put(startIndex, new String[2]);
            return startIndex;
        }).forEachOrdered((startIndex) -> {
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
        });

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

    public Set<PeptideObject> getPeptides(String proteinKey) {
        if (protein_peptide_Map.containsKey(proteinKey)) {
            return protein_peptide_Map.get(proteinKey);
        }
        return null;

    }

    public List<PSMObject> getPSM(String peptideModifiedSequence) {
        if (psmMap.containsKey(peptideModifiedSequence)) {
            return psmMap.get(peptideModifiedSequence);
        } else {
            return new ArrayList<>();
        }

    }

    public Map<Object, SpectrumInformation> getSelectedPsmData(List<PSMObject> psms, PeptideObject peptideObject) {//SpectrumPlot plot
        Map<Object, SpectrumInformation> spectrumInformationMap = new LinkedHashMap<>();
        int maxCharge = Integer.MIN_VALUE;
        double maxError = Double.MIN_VALUE;;
        for (PSMObject selectedPsm : psms) {
            try {
                if (!importedMgfFilesIndexers.containsKey(selectedPsm.getSpectrumFile())) {
                    importedMgfFilesIndexers.put(selectedPsm.getSpectrumFile(), (MgfIndex) SerializationUtils.readObject(mgfFilesIndexers.get("data/" + selectedPsm.getSpectrumFile() + ".cui").getFile()));
                }

            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
            MgfIndex mgfIndex = importedMgfFilesIndexers.get(selectedPsm.getSpectrumFile());
            String galaxyFileId = "";
            String galaxyHistoryId = "";
            for (SystemDataSet ds : mgfFiles.values()) {
                if (ds.getName().equalsIgnoreCase(selectedPsm.getSpectrumFile())) {
                    galaxyFileId = ds.getGalaxyId();
                    galaxyHistoryId = ds.getHistoryId();
                    break;
                }
            }
            MSnSpectrum spectrum = galaxyDatasetServingUtil.getSpectrum(mgfIndex.getIndex(selectedPsm.getSpectrumTitle()), galaxyHistoryId, galaxyFileId, selectedPsm.getSpectrumFile());
            int tCharge = Integer.parseInt(selectedPsm.getMeasuredCharge().replace("+", ""));
            if (tCharge > maxCharge) {
                maxCharge = tCharge;
            }
            if (selectedPsm.getPrecursorMZError_PPM() > maxError) {
                maxError = selectedPsm.getPrecursorMZError_PPM();
            }
            
            ArrayList<ModificationMatch> psModificationMatches = null;
            if (peptideObject.isModified()) {
                psModificationMatches = new ArrayList<>(peptideObject.getNModifications());
                for (ModificationMatch seModMatch : peptideObject.getModificationMatches()) {
                    psModificationMatches.add(new ModificationMatch(seModMatch.getTheoreticPtm(), seModMatch.isVariable(), seModMatch.getModificationSite()));
                }
            }

            Peptide psPeptide = new Peptide(peptideObject.getSequence(), psModificationMatches);//modifiedPeptideSequence.replace("NH2-", "").replace("-COOH", "")
            psPeptide.setParentProteins(new ArrayList<>(selectedPsm.getProteins()));
            PeptideAssumption psAssumption = new PeptideAssumption(psPeptide, new Charge(+2, 2));
            SpectrumMatch spectrumMatch = new SpectrumMatch(spectrum.getSpectrumKey());
            spectrumMatch.setBestPeptideAssumption(psAssumption);
            SpectrumInformation spectrumInformation = new SpectrumInformation();
            spectrumInformation.setCharge("2");
            spectrumInformation.setFragmentIonAccuracy(0.02);
            spectrumInformation.setIdentificationParameters(identificationParameters);
            spectrumInformation.setSpectrumMatch(spectrumMatch);
            spectrumInformation.setSpectrumId(selectedPsm.getIndex());
            spectrumInformation.setSpectrum(spectrum);
            spectrumInformationMap.put(selectedPsm.getIndex(), spectrumInformation);

        }
        for (SpectrumInformation spectrumInformation : spectrumInformationMap.values()) {
            spectrumInformation.setMaxCharge(maxCharge);
            spectrumInformation.setMzError(maxError);
        }
        return spectrumInformationMap;

    }
//
//    public void setPsmFile(GalaxyFile psmFile) {
//        this.psmFile = psmFile;
//    }
//
//    private int proteinsNumber;
//    private int psmNumber;
//
//    private int peptidesNumber;
//   
//    private String fixedModification;
//    private String variableModification;
//
//    
//
//
//
//
//    
//
    //
    //
//    public Set<ProteinObject> getRelatedProteinsSet(String proteinKey) {
//        if (protein_relatedProteins_Map.containsKey(proteinKey)) {
//            return protein_relatedProteins_Map.get(proteinKey);
//        }
//        return new HashSet<>();
//
//    }
//

//
//    public void setProteinFile(GalaxyFile proteinFile) {
//        this.proteinFile = proteinFile;
//    }
//
//    
//
//    public void setPeptideFile(GalaxyFile peptideFile) {
//        this.peptideFile = peptideFile;
//    }
//
//   
//
//    public void setFastaFile(GalaxyFile fastaFile) {
//        this.fastaFile = fastaFile;
//    }
//
//    public int getPsmNumber() {
//        return psmNumber;
//    }
//
//    public GalaxyFastaFileReader getFastaFileReader() {
//        return fastaFileReader;
//    }
//
//    public void setFastaFileReader(GalaxyFastaFileReader fastaFileReader) {
//        this.fastaFileReader = fastaFileReader;
//    }
//
//    public void setPsmNumber(int psmNumber) {
//        this.psmNumber = psmNumber;
//    }
//
//
//    public int getPeptidesNumber() {
//        return peptidesNumber;
//    }
//
//    public void setPeptidesNumber(int peptidesNumber) {
//        this.peptidesNumber = peptidesNumber;
//    }
//
//    public int getProteinsNumber() {
//        return proteinsNumber;
//    }
//
//    public void setProteinsNumber(int proteinsNumber) {
//        this.proteinsNumber = proteinsNumber;
//    }
//
////    public String getFastaFileIndex() {
////        return  fastaFileName.getIndexedFastafile();
////    }
////
//
//   
//    
//
//
//
//   
//
//    public String getProteinFileId() {
//        return proteinFileId;
//    }
//
//    public void setProteinFileId(String proteinFileId) {
//        this.proteinFileId = proteinFileId;
//    }
//
//    public String getPeptideFileId() {
//        return peptideFileId;
//    }
//
//    public void setPeptideFileId(String peptideFileId) {
//        this.peptideFileId = peptideFileId;
//    }
//
//    public String getCpsId() {
//        return cpsId;
//    }
//
//    public void setCpsId(String cpsId) {
//        this.cpsId = cpsId;
//    }
//
//    
//
//    public String getPsmFileId() {
//        return psmFileId;
//    }
//
//    public void setPsmFileId(String psmFileId) {
//        this.psmFileId = psmFileId;
//    }
//
//
//
//    public void setFastaFileId(String fastaFileId) {
//        this.fastaFileId = fastaFileId;
//    }
//
//    public String getFastaFileId() {
//        return fastaFileId;
//    }
//
//    public void setFastaFileName(String fastaFileName) {
//        this.fastaFileName = fastaFileName;
//    }
//
//    public void addMGFFileIndex(String mgfFileId, String mgfFileIndex) {
//        mgfFilesIndexes.put(mgfFileId, mgfFileIndex);
//    }
//
//
//    @Override
//    public void setAvailableOnNels(boolean availableOnNels) {
//        super.setAvailableOnNels(availableOnNels); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public boolean isAvailableOnNels() {
//        if (proteinFile != null && peptideFile != null && psmFile != null) {
//            return (proteinFile.isAvailableOnNels() && peptideFile.isAvailableOnNels() && psmFile.isAvailableOnNels()); //To change body of generated methods, choose Tools | Templates.
//        }
//        return false;
//
//    }
//
//    @Override
//    public void setAvailableOnGalaxy(boolean availableOnGalaxy) {
//        super.setAvailableOnGalaxy(availableOnGalaxy); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public boolean isAvailableOnGalaxy() {
//        return super.isAvailableOnGalaxy(); //To change body of generated methods, choose Tools | Templates.
//    }
    public Date getJobDate() {
        return jobDate;
    }

    public void setJobDate(Date jobDate) {
        this.jobDate = jobDate;
    }

    @Override
    public int compareTo(PeptideShakerVisualizationDataset t) {
        return this.jobDate.compareTo(t.jobDate);
    }

}
