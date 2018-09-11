package com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects;

import com.uib.web.peptideshaker.galaxy.utilities.history.FastaFileWebService;
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
import com.uib.web.peptideshaker.galaxy.utilities.history.GalaxyDatasetServingUtil;
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
public class PeptideShakerVisualizationDataset extends GalaxyFileObject implements Comparable<PeptideShakerVisualizationDataset> {

    /**
     * Dataset (Project) name.
     */
    private final String projectName;
    /**
     * User data files folder.
     */
    private final File user_folder;
    /**
     * Galaxy Server web address.
     */
    private final String galaxyLink;
    /**
     * Galaxy server user API key.
     */
    private final String apiKey;
    /**
     * SearchGUI results file.
     */
    private GalaxyFileObject SearchGUIResultFile;
    /**
     * MGF input files list.
     */
    private final Map<String, GalaxyFileObject> inputMGFFiles;
    /**
     * PeptideShaker results file (compressed folder) ID on Galaxy Server.
     */
    private String PeptideShakerResultsFileId;
    /**
     * PeptideShaker results file (compressed folder) type.
     */
    private String file_ext;
    /**
     * Input FASTA file representation on Online PeptideShaker.
     */
    private GalaxyTransferableFile fasta_file;
    /**
     * Output proteins file representation on Online PeptideShaker.
     */
    private GalaxyTransferableFile proteins_file;
    /**
     * Output peptides file representation on Online PeptideShaker.
     */
    private GalaxyTransferableFile peptides_file;
    /**
     * Output PSM file representation on Online PeptideShaker.
     */
    private GalaxyTransferableFile psm_file;
    /**
     * Output ZIP file (folder) representation on Online PeptideShaker.
     */
    private GalaxyTransferableFile zip_file;
    /**
     * Input search parameter enzyme.
     */
    private String enzyme;
    /**
     * FASTA utilities that allow getting protein FASTA information using the
     * UniProt web service.
     */
    private FastaFileWebService FastaFileWebService;
    /**
     * MGF index file map (.cui) files.
     */
    private final Map<String, GalaxyTransferableFile> MGFFileIndexMap;
    /**
     * Imported MGF index file map (.cui).
     */
    private final Map<String, MgfIndex> importedMgfFilesIndexers;
    /**
     * Protein evidence options array.
     */
    private final String[] proteinEvidence = new String[]{"Not Available", "Protein", "Transcript", "Homology", "Predicted", "Uncertain"};
    /**
     * Protein modifications map (based on user search input).
     */
    private final ConcurrentHashMap<String, Set<Comparable>> modificationMap;
    /**
     * Protein to peptides number map (to be used in datasets filters).
     */
    private final TreeMap<Comparable, Set<Comparable>> proteinPeptidesNumberMap;
    /**
     * The sequence matching options.
     */
    private SequenceMatchingPreferences sequenceMatchingPreferences;
    /**
     * This factory will provide the implemented enzymes.
     */
    private EnzymeFactory enzymeFactory;
    /**
     * Managing the integration and data transfer between Galaxy Server and
     * Online Peptide Shaker (managing requests and responses)
     */
    private final GalaxyDatasetServingUtil galaxyDatasetServingUtil;
    /**
     * Generic class grouping the parameters used for protein identifications.
     */
    private IdentificationParameters identificationParameters;
    /**
     * Creating time for the datasets (to sort based on creation date).
     */
    private Date createTime;
    /**
     * Datasets statues (valid, not valid, or in progress).
     */
    private String status;
    /**
     * Thread that is used to read the PeptideShaker results zip folder.
     */
    private Thread PeptideShakerResultsFolderThread;
    /**
     * Thread that is used to read the output PSM file.
     */
    private Thread PSMFileThread;
    /**
     * Task used to process output peptide file.
     */
    private ProcessPeptidesTask processPeptidesTask;
    /**
     * Task used to process output protein file.
     */
    private ProcessProteinsTask processProteinsTask;
    /**
     * Task used to process input FASTA file.
     */
    private ProcessFastaFileTask processFastaFileTask;
    /**
     * Future results from input FASTA file task.
     */
    private Future proteinProcessFuture;
    /**
     * Future results from output peptide file task.
     */
    private Future peptideProcessFuture;
    /**
     * PSM File already initialised.
     */
    private boolean PSMFileInitialized = false;

    /**
     * Constructor to initialise the main variables required to visualise
     * PeptideShaker results
     *
     * @param projectName Dataset (Project) name.
     * @param user_folder User data files folder
     * @param galaxyLink Galaxy Server web address
     * @param apiKey Galaxy server user API key.
     * @param galaxyDatasetServingUtil Managing the integration and data
     * transfer between Galaxy Server and Online Peptide Shaker (managing
     * requests and responses)
     */
    public PeptideShakerVisualizationDataset(String projectName, File user_folder, String galaxyLink, String apiKey, GalaxyDatasetServingUtil galaxyDatasetServingUtil) {
        this.projectName = projectName;
        this.user_folder = user_folder;
        this.galaxyLink = galaxyLink;
        this.apiKey = apiKey;
        this.inputMGFFiles = new LinkedHashMap<>();
        this.MGFFileIndexMap = new LinkedHashMap<>();
        this.importedMgfFilesIndexers = new LinkedHashMap<>();
        this.proteinPeptidesNumberMap = new TreeMap<>();
        this.modificationMap = new ConcurrentHashMap<>();
        this.modificationMap.put("No Modification", new LinkedHashSet<>());
        this.galaxyDatasetServingUtil = galaxyDatasetServingUtil;
    }

    /**
     * Get search engines used in the SearchGUI search
     *
     * @return list of used search engines used in the search
     */
    public String getSearchEngines() {
        if (SearchGUIResultFile != null) {
            return SearchGUIResultFile.getOverview().split("DB:")[0];
        }
        return "";
    }

    /**
     * Check if decoy database added and used in the search process
     *
     * @return decoy database added
     */
    public boolean isDecoyDBAdded() {
        if (SearchGUIResultFile != null) {
            return SearchGUIResultFile.getOverview().contains("Creating decoy database");
        }
        return false;
    }

    /**
     * Get current state of the dataset
     *
     * @return dataset state
     */
    @Override
    public String getStatus() {
        if (SearchGUIResultFile == null) {
            return "Error";
        }
        if (!SearchGUIResultFile.getStatus().equalsIgnoreCase("ok")) {
            return SearchGUIResultFile.getStatus();
        }
        return this.status;
    }

    /**
     * Set current state of the dataset
     *
     * @param status dataset state
     */
    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Get dataset (Project) name.
     *
     * @return project name
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Get SearchGUI result file
     *
     * @return SearchGUI results file.
     */
    public GalaxyFileObject getSearchGUIFile() {
        return SearchGUIResultFile;
    }

    /**
     * Set SearchGUI result file
     *
     * @param searchGUIResultFile SearchGUI results file.
     */
    public void setSearchGUIResultFile(GalaxyFileObject searchGUIResultFile) {
        this.SearchGUIResultFile = searchGUIResultFile;
        if (zip_file != null) {
            initMgfIndexFiles();
        }
    }

    /**
     * Initialise MGF index files map (.cui).
     */
    private void initMgfIndexFiles() {
        MGFFileIndexMap.clear();
        for (String str : SearchGUIResultFile.getOverview().split("Spectrums:")) {
            if (str.contains("API:")) {
                String key = "data/" + str.split("\\(API:")[0].trim() + ".cui";
                GalaxyFileObject ds = new GalaxyFileObject();
                ds.setName(this.projectName + "-CUI");
                ds.setType("MGF Index File");
                ds.setGalaxyId(PeptideShakerResultsFileId + "__" + key);
                ds.setHistoryId(zip_file.getHistoryId());
                ds.setDownloadUrl(galaxyLink + "/api/histories/" + zip_file.getHistoryId() + "/contents/" + zip_file.getGalaxyId() + "/display?key=" + apiKey);
                GalaxyTransferableFile index_file = new GalaxyTransferableFile(user_folder, ds, true);
                index_file.setDownloadUrl("to_ext=" + file_ext);
                MGFFileIndexMap.put(key, index_file);
            }
        }
    }

    /**
     * Get the parameters used for protein identification.
     *
     * @return IdentificationParameters object.
     */
    public IdentificationParameters getSearchingParameters() {

        if (SearchGUIResultFile == null || (!getStatus().equalsIgnoreCase("ok"))) {
            return null;
        }
        if (identificationParameters == null) {
            initIdintificationParameters();
        }

        return identificationParameters;
    }

    /**
     * Get MGF input files list.
     *
     * @return map of MGF input files used in generating the dataset.
     */
    public Map<String, GalaxyFileObject> getInputMGFFiles() {
        return inputMGFFiles;
    }

    /**
     * Add MGF file to the dataset
     *
     * @param mgfFileID MGF file id on Galaxy Server
     * @param mgfDs MGF file representation on Online PeptideShaker
     */
    public void addMgfFiles(String mgfFileID, GalaxyFileObject mgfDs) {
        this.inputMGFFiles.put(mgfFileID, mgfDs);
    }

    /**
     * Set main file type (used for export)
     *
     * @param file_ext export file type.
     */
    public void setFile_ext(String file_ext) {
        this.file_ext = file_ext;
    }

    /**
     * Set PeptideShaker Results File Id on Galaxy Server
     *
     * @param PeptideShakerResultsFileId PeptideShaker Results File Id on Galaxy
     * Server
     */
    public void setPeptideShakerResultsFileId(String PeptideShakerResultsFileId) {
        this.PeptideShakerResultsFileId = PeptideShakerResultsFileId;
        PeptideShakerResultsFolderThread = new Thread(() -> {
            initialiseDataFiles(PeptideShakerResultsFileId);
        });
        PeptideShakerResultsFolderThread.start();

    }

    /**
     * Initialise PeptideShaker Results File and prepare inside folder files
     *
     * @param PeptideShakerResultsFileId PeptideShaker Results File Id on Galaxy
     * Server
     */
    private void initialiseDataFiles(String PeptideShakerResultsFileId) {
        //validate zipFile
        GalaxyFileObject ds = new GalaxyFileObject();
        ds.setName(this.projectName + "-ZIP");
        ds.setType("ZIP File");
        ds.setGalaxyId(PeptideShakerResultsFileId);
        ds.setDownloadUrl(galaxyLink + "/api/histories/" + this.getHistoryId() + "/contents/" + PeptideShakerResultsFileId + "/display?key=" + apiKey);
        ds.setStatus(this.status);
        zip_file = new GalaxyTransferableFile(user_folder, ds, true);
        zip_file.setDownloadUrl("to_ext=" + file_ext);
        zip_file.setHistoryId(this.getHistoryId());
        //init fasta file 
        ds = new GalaxyFileObject();
        ds.setName(this.projectName + "-FASTA");
        ds.setType("FASTA File");
        ds.setGalaxyId(PeptideShakerResultsFileId + "__data/input_database.fasta");
        ds.setDownloadUrl(galaxyLink + "/api/histories/" + this.getHistoryId() + "/contents/" + PeptideShakerResultsFileId + "/display?key=" + apiKey);
        fasta_file = new GalaxyTransferableFile(user_folder, ds, true);
        fasta_file.setDownloadUrl("to_ext=" + file_ext);
        //init protein file
        ds = new GalaxyFileObject();
        ds.setName(this.projectName + "-PROTEINS");
        ds.setType("Protein File");
        ds.setGalaxyId(PeptideShakerResultsFileId + "__reports/Default_Protein_Report.txt");
        ds.setDownloadUrl(galaxyLink + "/api/histories/" + this.getHistoryId() + "/contents/" + PeptideShakerResultsFileId + "/display?key=" + apiKey);
        proteins_file = new GalaxyTransferableFile(user_folder, ds, true);
        proteins_file.setDownloadUrl("to_ext=" + file_ext);
        //init peptides file
        ds = new GalaxyFileObject();
        ds.setName(this.projectName + "-PEPTIDES");
        ds.setType("Peptides File");
        ds.setGalaxyId(PeptideShakerResultsFileId + "__reports/Default_Peptide_Report.txt");
        ds.setDownloadUrl(galaxyLink + "/api/histories/" + this.getHistoryId() + "/contents/" + PeptideShakerResultsFileId + "/display?key=" + apiKey);
        peptides_file = new GalaxyTransferableFile(user_folder, ds, true);
        peptides_file.setDownloadUrl("to_ext=" + file_ext);
        ds = new GalaxyFileObject();
        ds.setName(this.projectName + "-PSM");
        ds.setType("PSM File");
        ds.setGalaxyId(PeptideShakerResultsFileId + "__reports/Default_PSM_Report.txt");
        ds.setDownloadUrl(galaxyLink + "/api/histories/" + this.getHistoryId() + "/contents/" + PeptideShakerResultsFileId + "/display?key=" + apiKey);
        psm_file = new GalaxyTransferableFile(user_folder, ds, true);
        psm_file.setDownloadUrl("to_ext=" + file_ext);
        if (SearchGUIResultFile != null) {
            initMgfIndexFiles();
        }

    }

    /**
     * Get Input FASTA file used in the search name
     *
     * @return FASTA file name
     */
    public String getFastaFileName() {
        if (SearchGUIResultFile != null) {
            return SearchGUIResultFile.getOverview().split("sequences:")[0].split("DB:")[1].trim();
        }

        return "Input_database.fasta";
    }

    /**
     * Get list of variable modification used as input in searching process
     *
     * @return list of variable modification
     */
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

    /**
     * Get list of fixed modification used as input in searching process
     *
     * @return list of fixed modification
     */
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

    /**
     * Initialise protein object from the provided FASTA file
     *
     * @param proteinkey protein accession used as a key
     */
    private void initialiseFromFastaFile(String proteinkey) {
        ProteinObject protein = new ProteinObject();
        protein.setAccession(proteinkey);
        ProteinSequence entry = processFastaFileTask.getFastaProteinSequenceMap().get(protein.getAccession());
        String[] descArr = entry.getDescription().split("\\s");
        protein.setDescription(descArr[0].replace("OS", "").trim());
        protein.setSequence(entry.getSequenceAsString());
        protein.setProteinEvidence(proteinEvidence[Integer.parseInt(descArr[descArr.length - 2].replace("PE=", "").trim())]);
        processFastaFileTask.getFastaProteinMap().put(proteinkey, protein);

    }

    /**
     * Convert JSON object to Java readable map
     *
     * @param object JSON object to be converted
     * @return Java Hash map has all the data
     * @throws JSONException in case of error in reading JSON file
     */
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

    /**
     * Convert JSON object to Java readable list
     *
     * @param object JSON object to be converted
     * @return Java List has all the data
     * @throws JSONException in case of error in reading JSON file
     */
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

    /**
     * Set input enzyme used in search process
     *
     * @param enzyme enzyme name.
     */
    public void setEnzyme(String enzyme) {
        this.enzyme = enzyme;
    }

    /**
     * Select and update protein information
     *
     * @param selectedIds selected protein IDs (accessions)
     */
    public void selectUpdateProteins(Set<Comparable> selectedIds) {
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

    /**
     * Update protein information
     *
     * @param id protein ID (accession)
     */
    private void checkAndUpdateProtein(String id) {
        if (processProteinsTask.getProteinsMap().containsKey(id) && processProteinsTask.getProteinsMap().get(id).getSequence() != null) {
            return;
        }
        if (processProteinsTask.getProteinsMap().containsKey(id) && processProteinsTask.getProteinsMap().get(id).getSequence() == null) {
            completeProteinInformation(processProteinsTask.getProteinsMap().get(id));
        } else if (!processFastaFileTask.getFastaProteinMap().containsKey(id) && processFastaFileTask.getFastaProteinSequenceMap().containsKey(id)) {
            initialiseFromFastaFile(id);
        }

    }

    /**
     * Add missing information to protein object
     *
     * @param protein object to be updated
     */
    public void completeProteinInformation(ProteinObject protein) {
        ProteinSequence entry = processFastaFileTask.getFastaProteinSequenceMap().get(protein.getAccession());
        String protDesc = entry.getDescription().split("OS")[0];
        String[] descArr = entry.getDescription().split("\\s");
        protein.setDescription(protDesc.replace(descArr[0], "").trim());
        protein.setSequence(entry.getSequenceAsString());
        protein.setProteinEvidence(proteinEvidence[Integer.parseInt(descArr[descArr.length - 2].replace("PE=", "").trim())]);
        processFastaFileTask.getFastaProteinMap().put(protein.getAccession(), protein);

    }

    /**
     * Process files inside the PeptideShaker results file, (execute proteins,
     * peptides,and FASTA file tasks).
     *
     */
    public void processDataFiles() {
        while (PeptideShakerResultsFolderThread.isAlive()) {
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
        executorService.submit(processFastaFileTask);
        while (!proteinProcessFuture.isDone()) {
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

    /**
     * Process output PSM file.
     */
    public void processPSMFile() {
        while (!peptideProcessFuture.isDone()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        if (PSMFileInitialized) {
            return;
        }
        PSMFileThread = new Thread(this::processPsmFile);
        PSMFileThread.start();
        PSMFileThread.setPriority(Thread.MIN_PRIORITY);
        PSMFileInitialized = true;
    }

    /**
     * Get list of proteins objects included in the dataset
     *
     * @return map of protein objects (accessions to protein objects)
     */
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

    /**
     * Get list of peptides objects included in the dataset
     *
     * @return map of peptides objects (modified sequence to protein objects)
     */
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

    /**
     * Get maximum molecular weight
     *
     * @return double value
     */
    public double getMaxMW() {
        return processProteinsTask.getMaxMW();
    }

    /**
     * Get maximum MS2 quantitative
     *
     * @return double value
     */
    public double getMaxMS2Quant() {
        return processProteinsTask.getMaxMS2Quant();
    }

    /**
     * Get maximum peptides number (highest number of peptides from a protein)
     *
     * @return number of peptides
     */
    public int getMaxPeptideNumber() {
        return processProteinsTask.getMaxPeptideNumber();
    }

    /**
     * Get maximum PSM number (highest number of PSM from a protein)
     *
     * @return number of PSM
     */
    public int getMaxPsmNumber() {
        return processProteinsTask.getMaxPsmNumber();
    }

    /**
     * Process output PSM file.
     */
    private void processPsmFile() {
        while (PeptideShakerResultsFolderThread.isAlive() || !peptideProcessFuture.isDone()) {
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
                if (psm.getMeasuredCharge().trim().equalsIgnoreCase("")) {
//                    psm.setMeasuredCharge(psm.getIdentificationCharge());
//                    System.out.println("measuredcharge was empty " + psm.getIndex() + "  " + psm.getIdentificationCharge());
                }
                if (!arr[16].equalsIgnoreCase("")) {
                    psm.setTheoreticalMass(Double.parseDouble(arr[16]));
                }
                if (!arr[17].equalsIgnoreCase("")) {
                    psm.setIsotopeNumber(Integer.parseInt(arr[17]));
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

                if (processPeptidesTask.getPSMsMap().containsKey(psm.getModifiedSequence())) {
                    processPeptidesTask.getPSMsMap().get(psm.getModifiedSequence()).add(psm);
                } else if (processPeptidesTask.getPSMsMap().containsKey(psm.getModifiedSequence().replace("L", "I"))) {
                } else if (processPeptidesTask.getPSMsMap().containsKey(psm.getModifiedSequence().replace("I", "L"))) {
                    System.out.println("at Error for psm I mapping...not exist peptide need to replace I" + psm.getModifiedSequence());
                } else {
                    System.out.println("at Error for psm II mapping...not exist peptide " + psm.getModifiedSequence());
                }
            }
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

    /**
     * Calculated matrix for Diva Matrix Layout Chart Filter
     *
     * @return Modification matrix object
     */
    public ModificationMatrix getModificationMatrix() {
        try {
            while (!peptideProcessFuture.isDone()) {
                Thread.sleep(1000);
            }
            return (ModificationMatrix) peptideProcessFuture.get();
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Get chromosome map that is used in chromosome filter
     *
     * @return map of proteins to the chromosome index
     */
    public Map<Integer, Set<Comparable>> getChromosomeMap() {
        return processProteinsTask.getChromosomeMap();
    }

    /**
     * Get protein inference map
     *
     * @return map of protein inference label to proteins
     */
    public Map<String, Set<Comparable>> getProteinInferenceMap() {
        return processProteinsTask.getProteinInferenceMap();
    }

    /**
     * Get protein validation map
     *
     * @return map of protein validation label to proteins
     */
    public Map<String, Set<Comparable>> getProteinValidationMap() {
        return processProteinsTask.getProteinValidationMap();
    }

    /**
     * Get protein to peptides number map
     *
     * @return map of peptides number to proteins
     */
    public TreeMap<Comparable, Set<Comparable>> getProteinPeptidesNumberMap() {
        return proteinPeptidesNumberMap;
    }

    /**
     * Get protein to PSM number map
     *
     * @return map of PSM number to proteins
     */
    public TreeMap<Comparable, Set<Comparable>> getProteinPSMNumberMap() {
        return processProteinsTask.getProteinPSMNumberMap();
    }

    /**
     * Get protein to coverage value map
     *
     * @return map of coverage percent to proteins
     */
    public TreeMap<Comparable, Set<Comparable>> getProteinCoverageMap() {
        return processProteinsTask.getProteinCoverageMap();
    }

    /**
     * Get protein object from the protein list
     *
     * @param proteinKey (accession)
     * @return protein object
     */
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

    /**
     * Update protein information to be display
     *
     * @param proteinObject protein object
     * @param proteinKey protein key (accession)
     * @return updated protein object
     */
    public ProteinObject updateProteinInformation(ProteinObject proteinObject, String proteinKey) {
        if (proteinObject == null) {
            if (processFastaFileTask.getFastaProteinMap().containsKey(proteinKey)) {
                proteinObject = processFastaFileTask.getFastaProteinMap().get(proteinKey);
            } else if (processFastaFileTask.getFastaProteinSequenceMap().containsKey(proteinKey)) {
                initialiseFromFastaFile(proteinKey);
                proteinObject = processFastaFileTask.getFastaProteinMap().get(proteinKey);
            } else {
                if (FastaFileWebService == null) {
                    FastaFileWebService = new FastaFileWebService();
                }
                proteinObject = FastaFileWebService.updateProteinInformation(proteinObject, proteinKey);
                processFastaFileTask.getFastaProteinMap().put(proteinKey, proteinObject);
            }
        }
        if (enzyme != null) {
            if (sequenceMatchingPreferences == null) {
                this.sequenceMatchingPreferences = SequenceMatchingPreferences.getDefaultSequenceMatching();
            }
            if (enzymeFactory == null) {
                this.enzymeFactory = EnzymeFactory.getInstance();
            }
            for (String str : proteinObject.getRelatedPeptidesList()) {
                proteinObject.updatePeptideType(str, isEnzymaticPeptide(proteinObject.getSequence(), processPeptidesTask.getPeptidesMap().get(str).getSequence(), enzymeFactory.getEnzyme(enzyme), sequenceMatchingPreferences));
            }
        }
        return proteinObject;
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
        for (Iterator<Integer> it = surroundingAminoAcids.keySet().iterator(); it.hasNext();) {
            int index = it.next();
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

    /**
     * Get set of peptides related to selected protein
     *
     * @param proteinKey protein accession
     * @return set of peptide objects
     */
    public Set<PeptideObject> getPeptides(String proteinKey) {
        if (processPeptidesTask.getProtein_peptide_Map().containsKey(proteinKey)) {
            return processPeptidesTask.getProtein_peptide_Map().get(proteinKey);
        }
        return null;

    }

    /**
     * Get related PSM to selected peptide
     *
     * @param peptideKey peptide modified sequence
     * @return list of PSM objects
     */
    public List<PSMObject> getPSM(String peptideKey) {
        if (processPeptidesTask.getPSMsMap().containsKey(peptideKey)) {
            return processPeptidesTask.getPSMsMap().get(peptideKey);
        } else {
            return new ArrayList<>();
        }

    }

    /**
     * Initialise identification parameters from identification parameter file
     * (.par).
     */
    private void initIdintificationParameters() {
        GalaxyFileObject ds = new GalaxyFileObject();
        ds.setName(this.projectName + "-Param");
        ds.setType("Param File");
        ds.setGalaxyId(SearchGUIResultFile.getGalaxyId() + "__SEARCHGUI_IdentificationParameters.par");
        ds.setDownloadUrl(galaxyLink + "/api/histories/" + this.getHistoryId() + "/contents/" + SearchGUIResultFile.getGalaxyId() + "/display?key=" + apiKey);
        GalaxyTransferableFile file = new GalaxyTransferableFile(user_folder, ds, true);
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

    /**
     * Get selected spectrum data that is related to selected peptide.
     *
     * @param PSMs selected PSMs files
     * @param peptideObject peptide object
     * @return map of Spectrum Information
     */
    public Map<Object, SpectrumInformation> getSelectedSpectrumData(List<PSMObject> PSMs, PeptideObject peptideObject) {//SpectrumPlot plot

        if (identificationParameters == null) {
            initIdintificationParameters();
        }
        Map<Object, SpectrumInformation> spectrumInformationMap = new LinkedHashMap<>();
        int maxCharge = Integer.MIN_VALUE;
        double maxError = Double.MIN_VALUE;

        for (PSMObject selectedPsm : PSMs) {
            try {
                if (!importedMgfFilesIndexers.containsKey(selectedPsm.getSpectrumFile())) {
                    importedMgfFilesIndexers.put(selectedPsm.getSpectrumFile(), (MgfIndex) SerializationUtils.readObject(MGFFileIndexMap.get("data/" + selectedPsm.getSpectrumFile() + ".cui").getFile()));
                }

            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
            MgfIndex mgfIndex = importedMgfFilesIndexers.get(selectedPsm.getSpectrumFile());
            String galaxyFileId = "";
            String galaxyHistoryId = "";
            for (GalaxyFileObject ds : inputMGFFiles.values()) {
                if (ds.getName().split("-")[1].equalsIgnoreCase(selectedPsm.getSpectrumFile())) {
                    galaxyFileId = ds.getGalaxyId();
                    galaxyHistoryId = ds.getHistoryId();
                    break;
                }
            }
            MSnSpectrum spectrum = galaxyDatasetServingUtil.getSpectrum(mgfIndex.getIndex(selectedPsm.getSpectrumTitle()), galaxyHistoryId, galaxyFileId, selectedPsm.getSpectrumFile());
            int tCharge = 0;
            if (!selectedPsm.getMeasuredCharge().trim().equalsIgnoreCase("")) {
                tCharge = Integer.parseInt(selectedPsm.getMeasuredCharge().replace("+", ""));
            }

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

    /**
     * Get creating time for the datasets (to sort based on creation date).
     *
     * @return date object
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * Set creating time for the datasets (to sort based on creation date).
     *
     * @param createTime date object
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * Compare method to sort datasets based on creation date
     *
     * @param peptideShakerVisualizationDataset
     * @return integer value
     */
    @Override
    public int compareTo(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {
        return this.createTime.compareTo(peptideShakerVisualizationDataset.createTime);
    }

    /**
     * This class is used to create task that is used to process output peptide
     * file.
     */
    private class ProcessPeptidesTask implements Callable<ModificationMatrix> {

        /**
         * Calculated matrix for Diva Matrix Layout Chart Filter.
         */
        private ModificationMatrix modificationMatrix;
        /**
         * Protein modifications map (based on user search input).
         */
        private final ConcurrentHashMap<String, Set<Comparable>> modificationMap;
        /**
         * Protein to peptides map.
         */
        private final Map<String, Set<PeptideObject>> protein_peptide_Map;
        /**
         * Peptide to PSMs map.
         */
        private final Map<String, List<PSMObject>> PSMsMap;
        /**
         * Peptide map (modified sequence to peptide objects).
         */
        private final Map<Object, PeptideObject> peptidesMap;

        /**
         * Constructor to initialise the main variables.
         *
         * @param peptides_file output peptides file
         * @param proteinsMap Protein map.
         * @param modificationMap map of modifications used in search inputs.
         */
        public ProcessPeptidesTask(GalaxyTransferableFile peptides_file, Map<String, ProteinObject> proteinsMap, ConcurrentHashMap<String, Set<Comparable>> modificationMap) {

            this.modificationMap = new ConcurrentHashMap<>();
            this.modificationMap.put("No Modification", new LinkedHashSet<>());
            this.modificationMap.putAll(modificationMap);
            this.peptidesMap = new LinkedHashMap<>();
            protein_peptide_Map = new HashMap<>();
            PSMsMap = new LinkedHashMap<>();
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
                    PSMsMap.put(peptide.getModifiedSequence(), new ArrayList<>());

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

        /**
         * Get list of peptides objects included in the dataset
         *
         * @return map of peptides objects (modified sequence to protein
         * objects)
         */
        public Map<Object, PeptideObject> getPeptidesMap() {
            return peptidesMap;
        }

        /**
         * Get PSMs map included in the datasets
         *
         * @return map of PSMs objects
         */
        public Map<String, List<PSMObject>> getPSMsMap() {
            return PSMsMap;
        }

        /**
         * Calculated matrix for Diva Matrix Layout Chart Filter
         *
         * @return Modification matrix object
         */
        public ModificationMatrix getModificationMatrix() {
            return modificationMatrix;
        }

        /**
         * Get map of modifications used in search inputs
         *
         * @return Modification map
         */
        public ConcurrentHashMap<String, Set<Comparable>> getModificationMap() {
            return modificationMap;
        }

        /**
         * Get protein to peptides map.
         *
         * @return Protein to peptides map.
         */
        public Map<String, Set<PeptideObject>> getProtein_peptide_Map() {
            return protein_peptide_Map;
        }

        /**
         * Calculated matrix for Diva Matrix Layout Chart Filter and return it
         * when the files are ready
         *
         * @return Modification matrix object
         * @throws Exception
         */
        @Override
        public ModificationMatrix call() throws Exception {
            return this.modificationMatrix;
        }

    }

    /**
     * This class is used to create task that is used to process output protein
     * file.
     */
    private class ProcessProteinsTask implements Callable<Map<String, ProteinObject>> {

        /**
         * Protein to related protein map.
         */
        private Map<String, Set<ProteinObject>> protein_relatedProteins_Map;
        /**
         * Proteins map (accession to proteins object).
         */
        private Map<String, ProteinObject> proteinsMap;
        /**
         * Protein inference map.
         */
        private final Map<String, Set<Comparable>> proteinInferenceMap;
        /**
         * Protein validation map.
         */
        private final Map<String, Set<Comparable>> proteinValidationMap;
        /**
         * Map of proteins to the chromosome index.
         */
        private final Map<Integer, Set<Comparable>> chromosomeMap;
        /**
         * Protein to coverage value map.
         */
        private final TreeMap<Comparable, Set<Comparable>> proteinCoverageMap;
        /**
         * Protein to PSM numbers map.
         */
        private final TreeMap<Comparable, Set<Comparable>> proteinPSMNumberMap;
        /**
         * Maximum molecular weight.
         */
        private double maxMW = Double.MIN_VALUE;
        /**
         * Maximum MS2 quantitative.
         */
        private double maxMS2Quant = Double.MIN_VALUE;
        /**
         * Maximum peptides number.
         */
        private int maxPeptideNumber = Integer.MIN_VALUE;
        /**
         * Maximum PSM numbers.
         */
        private int maxPsmNumber = Integer.MIN_VALUE;

        /**
         * Constructor to initialise the main variables.
         *
         * @param proteins_file output protein file
         */
        public ProcessProteinsTask(GalaxyTransferableFile proteins_file) {
            this.protein_relatedProteins_Map = new HashMap<>();
            proteinsMap = new LinkedHashMap<>();
            this.proteinInferenceMap = new LinkedHashMap<>();
            this.proteinInferenceMap.put("No Information", new LinkedHashSet<>());
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
                        proteinInferenceMap.get(protein.getProteinInference()).add(protein.getAccession());
                    } else {
                        if (!proteinInferenceMap.containsKey(protein.getProteinInference())) {
                            proteinInferenceMap.put(protein.getProteinInference(), new LinkedHashSet<>());
                        }
                        proteinInferenceMap.get(protein.getProteinInference()).add(protein.getAccession());
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
            } catch (IOException | NumberFormatException ex) {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException ex1) {
                    }
                }
            }
        }

        /**
         * Get proteins map when the files are ready
         *
         * @return Proteins map (accession to proteins object)
         * @throws Exception
         */
        @Override
        public Map<String, ProteinObject> call() throws Exception {
            return this.proteinsMap;
        }

        /**
         * Get Protein to related protein map
         *
         * @return accession to protein objects map
         */
        public Map<String, Set<ProteinObject>> getProtein_relatedProteins_Map() {
            return protein_relatedProteins_Map;
        }

        /**
         * Get proteins map
         *
         * @return Proteins map (accession to proteins object)
         */
        public Map<String, ProteinObject> getProteinsMap() {
            return proteinsMap;
        }

        /**
         * Get protein inference map.
         *
         * @return protein inference map
         */
        public Map<String, Set<Comparable>> getProteinInferenceMap() {
            return proteinInferenceMap;
        }

        /**
         * Get protein validation map.
         *
         * @return protein validation map
         */
        public Map<String, Set<Comparable>> getProteinValidationMap() {
            return proteinValidationMap;
        }

        /**
         * Get map of proteins to the chromosome index.
         *
         * @return proteins to the chromosome index map
         */
        public Map<Integer, Set<Comparable>> getChromosomeMap() {
            return chromosomeMap;
        }

        /**
         * Get protein to coverage value map.
         *
         * @return protein to coverage value map
         */
        public TreeMap<Comparable, Set<Comparable>> getProteinCoverageMap() {
            return proteinCoverageMap;
        }

        /**
         * Get protein to PSM numbers map.
         *
         * @return Protein to PSM numbers map
         */
        public TreeMap<Comparable, Set<Comparable>> getProteinPSMNumberMap() {
            return proteinPSMNumberMap;
        }

        /**
         * Get maximum molecular weight
         *
         * @return double value
         */
        public double getMaxMW() {
            return maxMW;
        }

        /**
         * Get maximum MS2 quantitative
         *
         * @return double value
         */
        public double getMaxMS2Quant() {
            return maxMS2Quant;
        }

        /**
         * Get maximum peptides number (highest number of peptides from a
         * protein)
         *
         * @return number of peptides
         */
        public int getMaxPeptideNumber() {
            return maxPeptideNumber;
        }

        /**
         * Get maximum PSM number (highest number of PSM from a protein)
         *
         * @return number of PSM
         */
        public int getMaxPsmNumber() {
            return maxPsmNumber;
        }

    }

    /**
     * This class is used to create task that is used to process FASTA file.
     */
    private class ProcessFastaFileTask implements Callable<LinkedHashMap<String, ProteinSequence>> {

        /**
         * Map of protein accession mapped to protein object imported from FASTA
         * file.
         */
        private final Map<Object, ProteinObject> fastaProteinMap;
        /**
         * Map of protein accession mapped to The representation of a
         * ProteinSequence.
         */
        private LinkedHashMap<String, ProteinSequence> fastaProteinSequenceMap;

        /**
         * Constructor to initialise the main variables.
         *
         * @param fasta_file input FASTA file
         */
        public ProcessFastaFileTask(GalaxyTransferableFile fasta_file) {
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
            } catch (IOException | NumberFormatException ex) {

            }
        }

        /**
         * Get map of protein accession mapped to protein object imported from
         * FASTA file.
         *
         * @return protein objects map
         */
        public Map<Object, ProteinObject> getFastaProteinMap() {
            return fastaProteinMap;
        }

        /**
         * get map of protein accession mapped to The representation of a
         * ProteinSequence.
         *
         * @return Protein sequence mapped to accessions
         */
        public LinkedHashMap<String, ProteinSequence> getFastaProteinSequenceMap() {
            return fastaProteinSequenceMap;
        }

        /**
         * Get protein sequences map when the files are ready
         *
         * @return protein sequence map (accession to protein sequence object)
         * @throws Exception
         */
        @Override
        public LinkedHashMap<String, ProteinSequence> call() throws Exception {
            return fastaProteinSequenceMap;
        }

    }

}
