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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
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
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections15.map.LinkedMap;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet;
import org.biojava.nbio.core.sequence.io.FastaReader;
import org.biojava.nbio.core.sequence.io.GenericFastaHeaderParser;
import org.biojava.nbio.core.sequence.io.ProteinSequenceCreator;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

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
    private SystemDataSet searchGUIFileDs;
    private String orgFastaFileId;
    private final Map<String, SystemDataSet> mgfFiles;
    private String zipFileId;
    private String file_ext;
    private GalaxyFile fasta_file;
    private GalaxyFile proteins_file;
    private GalaxyFile peptides_file;
    private GalaxyFile psm_file;
    private  GalaxyFile zip_file ;
    private String enzyme;
    private GalaxyFastaFileReader fastaFileReader;
    private final Map<String, GalaxyFile> mgfFilesIndexers;
    private final Map<String, MgfIndex> importedMgfFilesIndexers;

    private final String[] proteinEvedence = new String[]{"Not Available", "Protein", "Transcript", "Homology", "Predicted", "Uncertain"};
    private final ConcurrentHashMap<String, Set<Comparable>> modificationMap;

    private final TreeMap<Comparable, Set<Comparable>> proteinPeptidesNumberMap;

//    private Map<Object, PeptideObject> peptidesMap;
//    private Map<String, Set<PeptideObject>> protein_peptide_Map;
    private SequenceMatchingPreferences sequenceMatchingPreferences;
    private EnzymeFactory enzymeFactory;

    private final GalaxyDatasetServingUtil galaxyDatasetServingUtil;

    private IdentificationParameters identificationParameters;
    private List<String> outputsIds;

    private Date jobDate;
    private String status;

    private Thread zipFolderThread;
    private Thread psmFileThread;

    public PeptideShakerVisualizationDataset(String projectName, File user_folder, String galaxyUrl, String apiKey, GalaxyDatasetServingUtil galaxyDatasetServingUtil) {
        this.projectName = projectName;
        this.user_folder = user_folder;
        this.galaxyUrl = galaxyUrl;
        this.apiKey = apiKey;
        this.mgfFiles = new LinkedHashMap<>();
        this.mgfFilesIndexers = new LinkedHashMap<>();
        this.importedMgfFilesIndexers = new LinkedHashMap<>();

//        this.chromosomeMap = new LinkedHashMap<>();
//        this.chromosomeMap.put(-2, new LinkedHashSet<>());
//        this.proteinCoverageMap = new TreeMap<>();
//        this.PIMap = new LinkedHashMap<>();
        this.proteinPeptidesNumberMap = new TreeMap<>();
//        this.proteinPSMNumberMap = new TreeMap<>();
//        this.proteinValidationMap = new LinkedHashMap<>();
//
        this.modificationMap = new ConcurrentHashMap<>();
        this.modificationMap.put("No Modification", new LinkedHashSet<>());
        this.galaxyDatasetServingUtil = galaxyDatasetServingUtil;
    }

    public String getSearchEngines() {
        if (searchGUIFileDs != null) {
            return searchGUIFileDs.getOverview().split("DB:")[0];
        }
        return "";
    }

    public boolean isDecoyDBAdded() {
        if (searchGUIFileDs != null) {
            return searchGUIFileDs.getOverview().contains("Creating decoy database");
        }
        return false;
    }

    @Override
    public String getStatus() {
        if (searchGUIFileDs == null) {
            return "Error";
        }
        if (!searchGUIFileDs.getStatus().equalsIgnoreCase("ok")) {
            return searchGUIFileDs.getStatus();
        }
        return this.status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getOutputsIds() {
        return outputsIds;
    }

    public void setOutputsIds(List<String> outputsIds) {
        this.outputsIds = outputsIds;
    }

    public String getProjectName() {
        return projectName;
    }

    public SystemDataSet getSearchGUIFile() {
        return searchGUIFileDs;
    }

    public void setSearchGUIFile(SystemDataSet searchGUIFileDs) {
        this.searchGUIFileDs = searchGUIFileDs;
        if (zip_file != null) {
            initMgfIndexFiles();
        }
//         
    }

    private void initMgfIndexFiles() {
        mgfFilesIndexers.clear();
        for (String str : searchGUIFileDs.getOverview().split("Spectrums:")) {
            if (str.contains("API:")) {
                String key = "data/" + str.split("\\(API:")[0].trim() + ".cui";
                SystemDataSet ds = new SystemDataSet();
                ds.setName(this.projectName + "-CUI");
                ds.setType("MGF Index File");
                ds.setGalaxyId(zipFileId + "__" + key);
                ds.setHistoryId(zip_file.getHistoryId());
                ds.setDownloadUrl(galaxyUrl + "/api/histories/" + zip_file.getHistoryId()+ "/contents/" + zip_file.getGalaxyId() + "/display?key=" + apiKey);
                GalaxyFile index_file = new GalaxyFile(user_folder, ds, true);
                index_file.setDownloadUrl("to_ext=" + file_ext);
                mgfFilesIndexers.put(key, index_file);
            }
        }

//        for (String key : filesList) {
//            if (key.endsWith("mgf.cui")) {
//                ds = new SystemDataSet();
//                ds.setName(this.projectName + "-CUI");
//                ds.setType("MGF Index File");
//                ds.setGalaxyId(zipFileId + "__" + key);
//                ds.setDownloadUrl(galaxyUrl + "/api/histories/" + this.getHistoryId() + "/contents/" + zipFileId + "/display?key=" + apiKey);
//                GalaxyFile index_file = new GalaxyFile(user_folder, ds, true);
//                index_file.setDownloadUrl("to_ext=" + file_ext);
//                mgfFilesIndexers.put(key, index_file);
////                index_file.getFile();
//            }
//
//        }
    }

    public String getOrgFastaFileId() {
        return orgFastaFileId;
    }

    public IdentificationParameters getSearchingParameters() {

        if (searchGUIFileDs == null || (!getStatus().equalsIgnoreCase("ok"))) {
            return null;
        }
        if (identificationParameters == null) {
            initIdintificationParameters();
        }

        return identificationParameters;
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
        this.zipFileId = zipFileId;
        zipFolderThread = new Thread(() -> {
            initialiseDataFiles(zipFileId);
        });
        zipFolderThread.start();

    }

    private void initialiseDataFiles(String zipFileId) {
        //validate zipFile
        SystemDataSet ds = new SystemDataSet();
        ds.setName(this.projectName + "-ZIP");
        ds.setType("ZIP File");
        ds.setGalaxyId(zipFileId);
        ds.setDownloadUrl(galaxyUrl + "/api/histories/" + this.getHistoryId() + "/contents/" + zipFileId + "/display?key=" + apiKey);
        ds.setStatus(this.status);
        zip_file = new GalaxyFile(user_folder, ds, true);
        zip_file.setDownloadUrl("to_ext=" + file_ext);
        zip_file.setHistoryId(this.getHistoryId());
        //init fasta file 
        ds = new SystemDataSet();
        ds.setName(this.projectName + "-FASTA");
        ds.setType("FASTA File");
        ds.setGalaxyId(zipFileId + "__data/input_database.fasta");
        ds.setDownloadUrl(galaxyUrl + "/api/histories/" + this.getHistoryId() + "/contents/" + zipFileId + "/display?key=" + apiKey);
        fasta_file = new GalaxyFile(user_folder, ds, true);
        fasta_file.setDownloadUrl("to_ext=" + file_ext);
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
        ds = new SystemDataSet();
        ds.setName(this.projectName + "-PSM");
        ds.setType("PSM File");
        ds.setGalaxyId(zipFileId + "__reports/Default_PSM_Report.txt");
        ds.setDownloadUrl(galaxyUrl + "/api/histories/" + this.getHistoryId() + "/contents/" + zipFileId + "/display?key=" + apiKey);
        psm_file = new GalaxyFile(user_folder, ds, true);
        psm_file.setDownloadUrl("to_ext=" + file_ext);
        if (searchGUIFileDs != null) {
            initMgfIndexFiles();
        }

    }

    public GalaxyFile getFastaFile() {
        return fasta_file;
    }

    public GalaxyFile getPsmFile() {
        return psm_file;
    }

    public GalaxyFile getPeptideFile() {
        return peptides_file;
    }

    public String getFastaFileName() {
        if (searchGUIFileDs != null) {
            return searchGUIFileDs.getOverview().split("sequences:")[0].split("DB:")[1].trim();
        }

        return "Input_database.fasta";
    }

    public ArrayList<String> getVariableModification() {

        if (identificationParameters == null) {
            initIdintificationParameters();
        }
        ArrayList<String> variableModifications = identificationParameters.getSearchParameters().getPtmSettings().getVariableModifications();
        variableModifications.forEach((mod) -> {
            modificationMap.put(mod.trim(), new LinkedHashSet<>());
        });

        return variableModifications;
    }

    public ArrayList<String> getFixedModification() {
        if (identificationParameters == null) {
            initIdintificationParameters();
        }
        ArrayList<String> fixedModifications = identificationParameters.getSearchParameters().getPtmSettings().getFixedModifications();
        fixedModifications.forEach((mod) -> {
            modificationMap.put(mod.trim(), new LinkedHashSet<>());
        });
        return fixedModifications;

    }

    private void initFromFastaFile(String proteinkey) {
        ProteinObject protein = new ProteinObject();
        protein.setAccession(proteinkey);
        ProteinSequence entry = processFastaFileTask.getFastaProteinSequenceMap().get(protein.getAccession());
        String[] descArr = entry.getDescription().split("\\s");
        protein.setDescription(descArr[0].replace("OS", "").trim());
        protein.setSequence(entry.getSequenceAsString());
        protein.setProteinEvidence(proteinEvedence[Integer.parseInt(descArr[descArr.length - 2].replace("PE=", "").trim())]);
        processFastaFileTask.getFastaProteinMap().put(proteinkey, protein);

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
        return true;//(searchingParameters != null && zipFileId != null && mgfFilesIndexers.size() == mgfFiles.size());
//        System.out.println("at files "+(proteinFileId == null )+"  "+(peptideFileId == null )+"||"+(searchGUIFileId == null )+"||"+( psmFileId == null )+"||"+( mgfFiles.isEmpty() )+"||"+( fastaFileName == null )+"||"+(mgfFilesIndexes.isEmpty()));
//        return !(proteinFileId == null || peptideFileId == null || fastaFileName == null/*|| searchGUIFileId == null || psmFileId == null || mgfFiles.isEmpty()  || mgfFilesIndexes.isEmpty()*/);
    }

    public void setProteinInformation(Set<Comparable> selectedIds) {
        if (selectedIds == null || selectedIds.isEmpty() || selectedIds.contains(null + "") || processFastaFileTask.getFastaProteinSequenceMap() == null) {
            return;
        }
        selectedIds.stream().map((id) -> processProteinsTask.getProteinsMap().get(id.toString())).forEachOrdered((prot) -> {
            checkAndUpdateProtein(prot.getAccession());
            prot.getProteinGroupSet().forEach((relatedProt) -> {
                checkAndUpdateProtein(relatedProt);
            });
        });

    }

    private void checkAndUpdateProtein(String id) {
        if (processProteinsTask.getProteinsMap().containsKey(id) && processProteinsTask.getProteinsMap().get(id).getSequence() != null) {
            return;
        }
        if (processProteinsTask.getProteinsMap().containsKey(id) && processProteinsTask.getProteinsMap().get(id).getSequence() == null) {
            setProteinInformation(processProteinsTask.getProteinsMap().get(id));
        } else if (!processFastaFileTask.getFastaProteinMap().containsKey(id) && processFastaFileTask.getFastaProteinSequenceMap().containsKey(id)) {
            initFromFastaFile(id);
        }

    }

    public void setProteinInformation(ProteinObject protein) {
        ProteinSequence entry = processFastaFileTask.getFastaProteinSequenceMap().get(protein.getAccession());
        String[] descArr = entry.getDescription().split("\\s");
        protein.setDescription(descArr[0].replace("OS", "").trim());
        protein.setSequence(entry.getSequenceAsString());
        protein.setProteinEvidence(proteinEvedence[Integer.parseInt(descArr[descArr.length - 2].replace("PE=", "").trim())]);
        processFastaFileTask.getFastaProteinMap().put(protein.getAccession(), protein);

    }

    private ProcessPeptidesTask processPeptidesTask;
    private ProcessProteinsTask processProteinsTask;
    private ProcessFastaFileTask processFastaFileTask;
    private Future proteinProcessFuture;
    private Future fastaProcessFuture;
    private Future peptideProcessFuture;

    public void processDataFiles() {
        while (zipFolderThread.isAlive()) {
            System.out.println("the thread is still alive");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        processProteinsTask = new ProcessProteinsTask(proteins_file);
        proteinProcessFuture = executorService.submit(processProteinsTask);

        processFastaFileTask = new ProcessFastaFileTask(fasta_file);
        fastaProcessFuture = executorService.submit(processFastaFileTask);
        while (!proteinProcessFuture.isDone()) {
            System.out.println("the thread is still alive");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        try {
            processPeptidesTask = new ProcessPeptidesTask(peptides_file, (Map<String, ProteinObject>) processProteinsTask.call(), modificationMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        peptideProcessFuture = executorService.submit(processPeptidesTask);
        executorService.shutdown();
    }
    private boolean psmInitialized = false;

    public void initializePsmFile() {
        while (!peptideProcessFuture.isDone()) {
            System.out.println("the peptideProcessFuture  is still alive");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        if (psmInitialized) {
            return;
        }
        psmFileThread = new Thread(this::processPsmFile);
        psmFileThread.start();
        psmFileThread.setPriority(Thread.MIN_PRIORITY);
        psmInitialized = true;
    }

    private void initMgfIndexers() {

//    
    }

    public Map<String, ProteinObject> getProteinsMap() {
        try {
            while (!proteinProcessFuture.isDone()) {
                Thread.sleep(1000);
            }
            return processProteinsTask.call();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Map<Object, PeptideObject> getPeptidesMap() {
        while (!peptideProcessFuture.isDone()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        return processPeptidesTask.getPeptidesMap();
    }

    public double getMaxMW() {
        return processProteinsTask.getMaxMW();
    }

    public double getMaxMS2Quant() {
        return processProteinsTask.getMaxMS2Quant();
    }

    public int getMaxPeptideNumber() {
        return processProteinsTask.getMaxPeptideNumber();
    }

    public int getMaxPsmNumber() {
        return processProteinsTask.getMaxPsmNumber();
    }

    private void processPsmFile() {
        while (zipFolderThread.isAlive()|| !peptideProcessFuture.isDone()) {
            System.out.println("the thread is still alive");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        
        BufferedReader bufferedReader = null;
        try {//           
            bufferedReader = new BufferedReader(new FileReader(psm_file.getFile()), 1024 * 100);
            String line;
            /**
             * escape header
             */
            bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
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

                if (processPeptidesTask.getPsmMap().containsKey(psm.getModifiedSequence())) {
                    processPeptidesTask.getPsmMap().get(psm.getModifiedSequence()).add(psm);
                } else if (processPeptidesTask.getPsmMap().containsKey(psm.getModifiedSequence().replace("L", "I"))) {
//                    System.out.println("at Error for psm mapping...not exist peptide need to replace L" + psm.getModifiedSequence());
                } else if (processPeptidesTask.getPsmMap().containsKey(psm.getModifiedSequence().replace("I", "L"))) {
//                    System.out.println("at Error for psm mapping...not exist peptide need to replace I" + psm.getModifiedSequence());
                } else {
//                    System.out.println("at Error for psm mapping...not exist peptide " + psm.getModifiedSequence());
                }
            }
//            bufferedReader.close();
            System.out.println("done loading PSM");
        } catch (IOException | NumberFormatException ex) {
            ex.printStackTrace();
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex1) {
                }
            }
        }

    }

    public ModificationMatrix getModificationMatrix() {
        try {
            while (!peptideProcessFuture.isDone()) {
                Thread.sleep(1000);
            }
            return (ModificationMatrix) peptideProcessFuture.get();
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }
        System.out.println("at error will return null for matrix");
        return null;
    }

    public Map<Integer, Set<Comparable>> getChromosomeMap() {
        return processProteinsTask.getChromosomeMap();
    }

    public Map<String, Set<Comparable>> getPiMap() {
        return processProteinsTask.getPIMap();
    }

    public Map<String, Set<Comparable>> getProteinValidationMap() {
        return processProteinsTask.getProteinValidationMap();
    }

    public TreeMap<Comparable, Set<Comparable>> getProteinPeptidesNumberMap() {
        return proteinPeptidesNumberMap;
    }

    public TreeMap<Comparable, Set<Comparable>> getProteinPSMNumberMap() {
        return processProteinsTask.getProteinPSMNumberMap();
    }

    public TreeMap<Comparable, Set<Comparable>> getProteinCoverageMap() {
        return processProteinsTask.getProteinCoverageMap();
    }

    public ProteinObject getProtein(String proteinKey) {
        checkAndUpdateProtein(proteinKey);
        if (processProteinsTask.getProteinsMap().containsKey(proteinKey)) {
            return processProteinsTask.getProteinsMap().get(proteinKey);
        } else if (processFastaFileTask.getFastaProteinMap().containsKey(proteinKey)) {
            return processFastaFileTask.getFastaProteinMap().get(proteinKey);
        } else {
            ProteinObject newRelatedProt = updateProteinInformation(null, proteinKey);
            return newRelatedProt;
        }

    }

    public ProteinObject updateProteinInformation(ProteinObject protein, String accession) {
        if (protein == null) {
            if (processFastaFileTask.getFastaProteinMap().containsKey(accession)) {
                protein = processFastaFileTask.getFastaProteinMap().get(accession);
            } else if (processFastaFileTask.getFastaProteinSequenceMap().containsKey(accession)) {
                initFromFastaFile(accession);
                protein = processFastaFileTask.getFastaProteinMap().get(accession);
            } else {
                if (fastaFileReader == null) {
                    fastaFileReader = new GalaxyFastaFileReader();
                }
                protein = fastaFileReader.updateProteinInformation(protein, accession);
                processFastaFileTask.getFastaProteinMap().put(accession, protein);
            }
        }
        if (enzyme != null) {
            if (sequenceMatchingPreferences == null) {
                this.sequenceMatchingPreferences = SequenceMatchingPreferences.getDefaultSequenceMatching();
            }
            if (enzymeFactory == null) {
                this.enzymeFactory = EnzymeFactory.getInstance();
            }
            for (String str : protein.getRelatedPeptidesList()) {
                protein.updatePeptideType(str, isEnzymaticPeptide(protein.getSequence(), processPeptidesTask.getPeptidesMap().get(str).getSequence(), enzymeFactory.getEnzyme(enzyme), sequenceMatchingPreferences));
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
        if (processPeptidesTask.getProtein_peptide_Map().containsKey(proteinKey)) {
            return processPeptidesTask.getProtein_peptide_Map().get(proteinKey);
        }
        return null;

    }

    public List<PSMObject> getPSM(String peptideModifiedSequence) {
        if (processPeptidesTask.getPsmMap().containsKey(peptideModifiedSequence)) {
            return processPeptidesTask.getPsmMap().get(peptideModifiedSequence);
        } else {
            return new ArrayList<>();
        }

    }

    private void initIdintificationParameters() {
        SystemDataSet ds = new SystemDataSet();
        ds.setName(this.projectName + "-Param");
        ds.setType("Param File");
        ds.setGalaxyId(searchGUIFileDs.getGalaxyId() + "__SEARCHGUI_IdentificationParameters.par");
        ds.setDownloadUrl(galaxyUrl + "/api/histories/" + this.getHistoryId() + "/contents/" + searchGUIFileDs.getGalaxyId() + "/display?key=" + apiKey);
        GalaxyFile file = new GalaxyFile(user_folder, ds, true);
        file.setDownloadUrl("to_ext=" + file_ext);
        SearchParameters searchParameters = null;
        try {
            File f = file.getFile();
            searchParameters = SearchParameters.getIdentificationParameters(f);
            searchParameters.setFastaFile(null);
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
            System.out.println("at catched the exception");
            return;
        }
        this.identificationParameters = new IdentificationParameters(searchParameters);
    }

    public Map<Object, SpectrumInformation> getSelectedPsmData(List<PSMObject> psms, PeptideObject peptideObject) {//SpectrumPlot plot

        if (identificationParameters == null) {
            initIdintificationParameters();
        }
        Map<Object, SpectrumInformation> spectrumInformationMap = new LinkedHashMap<>();
        int maxCharge = Integer.MIN_VALUE;
        double maxError = Double.MIN_VALUE;

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
                if (ds.getName().split("-")[1].equalsIgnoreCase(selectedPsm.getSpectrumFile())) {
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
            spectrumInformation.setFragmentIonAccuracy(identificationParameters.getSearchParameters().getFragmentIonAccuracy());

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

    private class ProcessPeptidesTask implements Callable<ModificationMatrix> {

        private ModificationMatrix modificationMatrix;
        private final ConcurrentHashMap<String, Set<Comparable>> modificationMap;
        private final Map<String, Set<PeptideObject>> protein_peptide_Map;
        private final Map<String, List<PSMObject>> psmMap;
        private final Map<Object, PeptideObject> peptidesMap;

        public Map<Object, PeptideObject> getPeptidesMap() {
            return peptidesMap;
        }

        public Map<String, List<PSMObject>> getPsmMap() {
            return psmMap;
        }

        public ModificationMatrix getModificationMatrix() {
            return modificationMatrix;
        }

        public ConcurrentHashMap<String, Set<Comparable>> getModificationMap() {
            return modificationMap;
        }

        public Map<String, Set<PeptideObject>> getProtein_peptide_Map() {
            return protein_peptide_Map;
        }

        public ProcessPeptidesTask(GalaxyFile peptides_file, Map<String, ProteinObject> proteinsMap, ConcurrentHashMap<String, Set<Comparable>> modificationMap) {

            this.modificationMap = new ConcurrentHashMap<>();
            this.modificationMap.put("No Modification", new LinkedHashSet<>());
            this.modificationMap.putAll(modificationMap);
            this.peptidesMap = new LinkedHashMap<>();
            protein_peptide_Map = new HashMap<>();
            psmMap = new LinkedHashMap<>();
            BufferedReader bufferedReader = null;
            try {//           
                System.out.println("start loading peptides");
                bufferedReader = new BufferedReader(new FileReader(peptides_file.getFile()), 1024 * 100);
                String line;
                /**
                 * escape header
                 */
                bufferedReader.readLine();
                while ((line = bufferedReader.readLine()) != null) {
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

                    peptide.getProteinsSet().stream().map((prot) -> {
                        if (!protein_peptide_Map.containsKey(prot)) {
                            protein_peptide_Map.put(prot, new LinkedHashSet<>());
                        }
                        return prot;
                    }).map((prot) -> {
                        protein_peptide_Map.get(prot).add(peptide);
                        return prot;
                        //should we keep or remove it
                    }).filter((prot) -> (processFastaFileTask.getFastaProteinSequenceMap().containsKey(prot))).forEachOrdered((prot) -> {
                        ProteinObject pObj = getProtein(prot);
                        pObj.addPeptideSequence(peptide.getModifiedSequence());
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
                modificationMatrix = new ModificationMatrix(new LinkedMap<>(modificationMap));
//                bufferedReader.close();
            } catch (IOException | NumberFormatException ex) {
                System.out.println("khalas fe errorrrrrrrrrrrrrrrrrr");
                ex.printStackTrace();
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException ex1) {
                        System.out.println("khalas fe 2 errorrrrrrrrrrrrrrrrrr");
                    }
                }
            }
        }

        @Override
        public ModificationMatrix call() throws Exception {
            return this.modificationMatrix;
        }

    }

    private class ProcessProteinsTask implements Callable<Map<String, ProteinObject>> {

        private Map<String, Set<ProteinObject>> protein_relatedProteins_Map;
        private Map<String, ProteinObject> proteinsMap;
        private final Map<String, Set<Comparable>> PIMap;
        private final Map<String, Set<Comparable>> proteinValidationMap;
        private final Map<Integer, Set<Comparable>> chromosomeMap;
        private final TreeMap<Comparable, Set<Comparable>> proteinCoverageMap;
        private final TreeMap<Comparable, Set<Comparable>> proteinPSMNumberMap;
        private double maxMW = Double.MIN_VALUE;
        private double maxMS2Quant = Double.MIN_VALUE;
        private int maxPeptideNumber = Integer.MIN_VALUE;
        private int maxPsmNumber = Integer.MIN_VALUE;

        public ProcessProteinsTask(GalaxyFile proteins_file) {
            this.protein_relatedProteins_Map = new HashMap<>();
            proteinsMap = new LinkedHashMap<>();
            this.PIMap = new LinkedHashMap<>();
            this.PIMap.put("No Information", new LinkedHashSet<>());
            this.proteinValidationMap = new LinkedHashMap<>();
            this.proteinValidationMap.put("No Information", new LinkedHashSet<>());
            this.proteinValidationMap.put("Confident", new LinkedHashSet<>());
            this.proteinValidationMap.put("Doubtful", new LinkedHashSet<>());
            this.chromosomeMap = new LinkedHashMap<>();
            this.chromosomeMap.put(-2, new LinkedHashSet<>());
            this.proteinCoverageMap = new TreeMap<>();
            this.proteinPSMNumberMap = new TreeMap<>();

            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new FileReader(proteins_file.getFile()), 1024 * 100);
                String line;
                /**
                 * escape header
                 */
                bufferedReader.readLine();
                while ((line = bufferedReader.readLine()) != null) {
                    String[] arr = line.split("\\t");
                    ProteinObject protein;
                    protein = new ProteinObject();
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
                    if ((arr[9] + "").trim().equalsIgnoreCase("")) {
                        protein.setConfidentlyLocalizedModificationSites("No Modification");
                    } else {
                        protein.setConfidentlyLocalizedModificationSites(arr[9]);//.split("\\(")[0]);                  
                    }
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
//            bufferedReader.close();
                System.out.println("done loading proteins");
            } catch (IOException | NumberFormatException ex) {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException ex1) {
                    }
                }
            }
        }

        @Override
        public Map<String, ProteinObject> call() throws Exception {
            return this.proteinsMap;
        }

        public Map<String, Set<ProteinObject>> getProtein_relatedProteins_Map() {
            return protein_relatedProteins_Map;
        }

        public Map<String, ProteinObject> getProteinsMap() {
            return proteinsMap;
        }

        public Map<String, Set<Comparable>> getPIMap() {
            return PIMap;
        }

        public Map<String, Set<Comparable>> getProteinValidationMap() {
            return proteinValidationMap;
        }

        public Map<Integer, Set<Comparable>> getChromosomeMap() {
            return chromosomeMap;
        }

        public TreeMap<Comparable, Set<Comparable>> getProteinCoverageMap() {
            return proteinCoverageMap;
        }

        public TreeMap<Comparable, Set<Comparable>> getProteinPSMNumberMap() {
            return proteinPSMNumberMap;
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

    }

    private class ProcessFastaFileTask implements Callable<LinkedHashMap<String, ProteinSequence>> {

        private final Map<Object, ProteinObject> fastaProteinMap;
        private LinkedHashMap<String, ProteinSequence> fastaProteinSequenceMap;

        public ProcessFastaFileTask(GalaxyFile fasta_file) {
            this.fastaProteinMap = new LinkedHashMap<>();
            fastaProteinSequenceMap = null;
            FileInputStream inStream;
            try {
                inStream = new FileInputStream(fasta_file.getFile());
                FastaReader<ProteinSequence, AminoAcidCompound> fastaReader
                        = new FastaReader<>(
                                inStream,
                                new GenericFastaHeaderParser<>(),
                                new ProteinSequenceCreator(AminoAcidCompoundSet.getAminoAcidCompoundSet()));
                fastaProteinSequenceMap = fastaReader.process();
                System.out.println("Done processing fasta file");
            } catch (IOException | NumberFormatException ex) {

            }
        }

        public Map<Object, ProteinObject> getFastaProteinMap() {
            return fastaProteinMap;
        }

        public LinkedHashMap<String, ProteinSequence> getFastaProteinSequenceMap() {
            return fastaProteinSequenceMap;
        }

        @Override
        public LinkedHashMap<String, ProteinSequence> call() throws Exception {
            return fastaProteinSequenceMap;
        }

    }

}
