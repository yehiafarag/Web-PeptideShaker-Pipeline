package com.uib.web.peptideshaker.galaxy;

import com.uib.web.peptideshaker.galaxy.dataobjects.GalaxyFile;
import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.galaxy.dataobjects.SystemDataSet;
import com.compomics.util.experiment.identification.identification_parameters.SearchParameters;
import com.github.jmchilton.blend4j.galaxy.GalaxyResponseException;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.OutputDataset;
import com.github.jmchilton.blend4j.galaxy.beans.Tool;
import com.github.jmchilton.blend4j.galaxy.beans.ToolExecution;
import com.github.jmchilton.blend4j.galaxy.beans.ToolInputs;
import com.github.jmchilton.blend4j.galaxy.beans.ToolSection;
import com.github.jmchilton.blend4j.galaxy.beans.Workflow;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;
import com.github.jmchilton.blend4j.galaxy.beans.collection.request.CollectionDescription;
import com.github.jmchilton.blend4j.galaxy.beans.collection.request.HistoryDatasetElement;
import com.github.jmchilton.blend4j.galaxy.beans.collection.response.CollectionResponse;
import com.sun.jersey.api.client.ClientResponse;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * This class responsible for interaction with tools on Galaxy server
 *
 * @author Yehia Farag
 */
public abstract class ToolsHandler {

    private boolean validToolsAvailable;
    private boolean nelsSupport;

    public boolean isNelsSupport() {
        return nelsSupport;
    }

    public String getNelsExporter_Tool_Id() {
        return nelsExporter_Tool_Id;
    }
    /**
     * The main galaxy Work-Flow Client on galaxy server.
     */
    private final WorkflowsClient galaxyWorkFlowClient;
    /**
     * The main galaxy Work-Flow Client on galaxy server.
     */
    private final HistoriesClient galaxyHistoriesClient;
    /**
     * The main galaxy Work-Flow Client on galaxy server.
     */
    private final ToolsClient galaxyToolClient;
    private Tool search_GUI_Tool = null;
    private Tool peptideShaker_Tool = null;
    private String nelsExporter_Tool_Id = null;
    private History galaxyWorkingHistory;
    /**
     * Convenience array for rewind ion type selection.
     */
    private final List<String> ions = new ArrayList(Arrays.asList(new String[]{"a", "b", "c", "x", "y", "z"}));

    // , 
    /**
     * Constructor to initialize the main data structure and other variables.
     *
     * @param galaxyToolClient
     * @param galaxyWorkFlowClient
     * @param galaxyHistoriesClient
     *
     */
    public ToolsHandler(ToolsClient galaxyToolClient, WorkflowsClient galaxyWorkFlowClient, HistoriesClient galaxyHistoriesClient) {

        this.galaxyWorkFlowClient = galaxyWorkFlowClient;
        this.galaxyHistoriesClient = galaxyHistoriesClient;
        List<History> availableHistories = galaxyHistoriesClient.getHistories();
        for (History h : availableHistories) {
            if (h.getName().equalsIgnoreCase("Online-PeptideShaker-Job-History")) {
                galaxyWorkingHistory = h;
                break;
            }
        }

        if (galaxyWorkingHistory == null) {
            galaxyWorkingHistory = galaxyHistoriesClient.getHistories().get(0);
        }
        this.galaxyToolClient = galaxyToolClient;
        /**
         * The SearchGUI tool on galaxy server.
         */
//        String galaxySearchGUIToolId = null;
        /**
         * The PeptideShaker tool on galaxy server.
         */
//        String galaxyPeptideShakerToolId = null;
        try {

            List<ToolSection> toolSections = galaxyToolClient.getTools();
            String PSVersion = "0.0.0";
            String SGVersion = "0.0.0.0";
            for (ToolSection secion : toolSections) {
                List<Tool> tools = secion.getElems();
                if (tools != null && !validToolsAvailable) {
                    for (Tool tool : tools) {
                        if (tool.getId().equalsIgnoreCase("nels_export")) {
                            nelsExporter_Tool_Id = tool.getId();
                        } else if (tool.getId().contains("toolshed.g2.bx.psu.edu/repos/galaxyp/peptideshaker/search_gui/")) {
                            String version = tool.getVersion();//getId().split("toolshed.g2.bx.psu.edu/repos/galaxyp/peptideshaker/search_gui/")[1];
                            if (isFirmwareNewer(version, SGVersion)) {
                                SGVersion = version;
                                search_GUI_Tool = tool;
                            }
                        } else if (tool.getId().contains("toolshed.g2.bx.psu.edu/repos/galaxyp/peptideshaker/peptide_shaker/")) {
                            String version = tool.getVersion();//.getId().split("toolshed.g2.bx.psu.edu/repos/galaxyp/peptideshaker/peptide_shaker/")[1];
                            boolean check = isFirmwareNewer(version, PSVersion);
                            if (check) {
                                PSVersion = version;
                                peptideShaker_Tool = tool;

                            }
                        }

                    }
                }

                if (peptideShaker_Tool != null && search_GUI_Tool != null && nelsExporter_Tool_Id != null) {
                    validToolsAvailable = true;
                    nelsSupport = true;
//                    break;
                } else if (peptideShaker_Tool != null && search_GUI_Tool != null) {
                    validToolsAvailable = true;
                }

            }
        } catch (Exception e) {
            if (e.toString().contains("Service Temporarily Unavailable")) {
                Notification.show("Service Temporarily Unavailable", Notification.Type.ERROR_MESSAGE);
                UI.getCurrent().getSession().close();
                VaadinSession.getCurrent().getSession().invalidate();

            } else {
                System.out.println("at tools are not available");
                e.printStackTrace();
            }
        }
    }

    public boolean isValidTools() {
        if (!validToolsAvailable) {
            Notification.show("PeptideShaker tools are not available on this Galaxy Server", Notification.Type.WARNING_MESSAGE);
        }
        return validToolsAvailable;
    }

    /**
     * Re-Index the files (FASTA or MGF files)
     *
     * @param id file id on galaxy server
     * @param historyId the history id that the file belong to
     * @param workHistoryId the history id that the new re-indexed file will be
     * stored in
     *
     * @return new re-indexed file id on galaxy
     *
     */
    private String reIndexFile(String id, String peptideShakerViewID, String workHistoryId) {

        String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
        File file = new File(basepath + "/VAADIN/Galaxy-Workflow-convertMGF.ga");
        String json = readWorkflowFile(file).replace("updated_MGF", peptideShakerViewID);
        Workflow selectedWf = galaxyWorkFlowClient.importWorkflow(json);

        try {
            WorkflowInputs workflowInputs = new WorkflowInputs();
            workflowInputs.setWorkflowId(selectedWf.getId());
            workflowInputs.setDestination(new WorkflowInputs.ExistingHistory(workHistoryId));
            WorkflowInputs.WorkflowInput input = new WorkflowInputs.WorkflowInput(id, WorkflowInputs.InputSourceType.HDA);
            workflowInputs.setInput("0", input);
            final WorkflowOutputs output = galaxyWorkFlowClient.runWorkflow(workflowInputs);
            galaxyWorkFlowClient.deleteWorkflowRequest(selectedWf.getId());
            return output.getOutputIds().get(0);
        } catch (Exception e) {
            galaxyWorkFlowClient.deleteWorkflowRequest(selectedWf.getId());
        }
        return null;

    }

    /**
     * Save search settings file into galaxy
     *
     * @param galaxyURL
     * @param userFolder
     * @param searchParameters searchParameters .par file
     * @param workHistoryId
     * @param searchSetiingsFilesMap
     * @param editMode
     * @return
     */
    public Map<String, GalaxyFile> saveSearchGUIParameters(String galaxyURL, File userFolder, Map<String, GalaxyFile> searchSetiingsFilesMap, String workHistoryId, SearchParameters searchParameters, boolean editMode) {

        String fileName = searchParameters.getFastaFile().getName().split("__")[1].replace(".", "_") + ".par";
        String fileId;
        if (editMode) {
            fileId = searchParameters.getFastaFile().getName().split("__")[3];
            searchSetiingsFilesMap.remove(fileId);
            //delete the file and make new one 
            this.deleteDataset(galaxyURL, workHistoryId, fileId);

        } else {
            fileId = fileName;
        }
        File file = new File(userFolder, fileId);

        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            SearchParameters.saveIdentificationParameters(searchParameters, file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        final ToolsClient.FileUploadRequest request = new ToolsClient.FileUploadRequest(workHistoryId, file);
        request.setDatasetName(fileName);

        List<OutputDataset> excList = galaxyToolClient.upload(request).getOutputs();
        if (excList != null && !excList.isEmpty()) {
            OutputDataset oDs = excList.get(0);
            SystemDataSet ds = new SystemDataSet();
            ds.setName(oDs.getName());
            ds.setType("Search Paramerters File (JSON)");
            ds.setHistoryId(workHistoryId);
            ds.setGalaxyId(oDs.getId());
            ds.setDownloadUrl(galaxyURL + "/datasets/" + ds.getGalaxyId() + "/display?to_ext=" + oDs.getDataTypeExt());
            GalaxyFile userFolderfile = new GalaxyFile(userFolder, ds, false);
            searchSetiingsFilesMap.put(ds.getGalaxyId(), userFolderfile);
            String temFileName = ds.getGalaxyId().replace("/", "_") + ds.getName();

            File updated = new File(userFolder, temFileName);
            try {
                updated.createNewFile();
                FileUtils.copyFile(file, updated);
                file.delete();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return searchSetiingsFilesMap;
    }

    /**
     * Read and convert the work-flow file into string (JSON like string) so the
     * system can execute the work-flow
     *
     * @param file the input file
     * @return the JSON string of the file content
     */
    private String readWorkflowFile(File file) {
        String json = "";
        String line;

        try {
            FileReader fileReader = new FileReader(file);
            try ( // Always wrap FileReader in BufferedReader.
                    BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                while ((line = bufferedReader.readLine()) != null) {
                    json += (line);
                }
                // Always close files.
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + "'");
        } catch (IOException ex) {
            System.out.println("Error reading file '" + "'");
        }
        return json;
    }

    public void deleteDataset(String galaxyURL, String historyId, String dsId) {
        try {
            if (dsId == null || historyId == null || galaxyURL == null) {
                return;
            }
            String userAPIKey = VaadinSession.getCurrent().getAttribute("ApiKey") + "";
            String cookiesRequestProperty = VaadinSession.getCurrent().getAttribute("cookies") + "";
            URL url = new URL(galaxyURL + "/api/histories/" + historyId + "/contents/datasets/" + dsId + "?key=" + userAPIKey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Cookie", cookiesRequestProperty);
            conn.addRequestProperty("Accept-Encoding", "gzip, deflate, sdch, br");
            conn.addRequestProperty("Accept-Language", "ar,en-US;q=0.8,en;q=0.6,en-GB;q=0.4");
            conn.addRequestProperty("Cache-Control", "no-cache");
            conn.addRequestProperty("Connection", "keep-alive");
            conn.addRequestProperty("DNT", "1");
            conn.addRequestProperty("X-Requested-With", "XMLHttpRequest");
            conn.addRequestProperty("Pragma", "no-cache");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8")) {
                final ObjectMapper mapper = new ObjectMapper();
                HashMap<String, Object> payLoadParamMap = new LinkedHashMap<>();
                payLoadParamMap.put("deleted", Boolean.TRUE);
//                if (purgeSupport) {
                payLoadParamMap.put("purged", Boolean.TRUE);
//                }
                String payload = mapper.writer().writeValueAsString(payLoadParamMap);
                writer.write(payload);
            }
            conn.connect();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder jsonString = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    jsonString.append(line);
                }
            }
            conn.disconnect();

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();

        }
    }
    private WorkflowOutputs tempDsOutput;

    /**
     * Run Online Peptide-Shaker work-flow
     *
     * @param projectName
     * @param fastaFileId FASTA file dataset id
     * @param mgfIdsList list of MGF file dataset ids
     * @param searchEnginesList List of selected search engine names
     * @param historyId galaxy history id that will store the results
     * @param searchParameters
     * @param otherSearchParameters
     * @return
     */
    public PeptideShakerVisualizationDataset executeWorkFlow(String projectName, String fastaFileId, Map<String, String> mgfIdsList, Set<String> searchEnginesList, String historyId, SearchParameters searchParameters, Map<String, Boolean> otherSearchParameters) {
        Workflow selectedWf = null;
        try {
            String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
            File file;
            WorkflowInputs.WorkflowInput input2;
            if (mgfIdsList.size() > 1) {
                file = new File(basepath + "/VAADIN/Galaxy-Workflow-Web-Peptide-Shaker-Multi-MGF-2018.ga");
                input2 = prepareWorkflowCollectionList(WorkflowInputs.InputSourceType.HDCA, mgfIdsList.keySet(), historyId);
            } else {
                file = new File(basepath + "/VAADIN/Galaxy-Workflow-Web-Peptide-Shaker-Single-MGF-2018.ga");
                input2 = new WorkflowInputs.WorkflowInput(mgfIdsList.keySet().iterator().next(), WorkflowInputs.InputSourceType.HDA);
            }
            String json = readWorkflowFile(file);
            System.out.println("setrach gui id " + search_GUI_Tool.getName() + "  " + search_GUI_Tool.getVersion() + " " + json.contains("3.2.24.0") + "  " + peptideShaker_Tool.getVersion() + "  " + json.contains("1.16.20"));
//            json = json.replace("\"toolshed.g2.bx.psu.edu/repos/galaxyp/peptideshaker/search_gui/3.2.13.3\"", "\\\"" + search_GUI_Tool .getId()+ "\\\"");
//            json = json.replace("3.2.13.3", search_GUI_Tool.split("\\/")[search_GUI_Tool.split("\\/").length - 1].trim());
            json = json.replace("3.2.24.0", search_GUI_Tool.getVersion());
            json = json.replace("SearchGUI_Label", projectName + "-SearchGUI Results").replace("ZIP_Label", projectName + "-ZIP");
            String createDecoy = searchParameters.getFastaFile().getName().split("__")[2];
            json = json.replace("\\\\\\\"create_decoy\\\\\\\": \\\\\\\"true\\\\\\\"", "\\\\\\\"create_decoy\\\\\\\": \\\\\\\"" + createDecoy + "\\\\\\\"");
            //protein_digest_options     
            switch (searchParameters.getDigestionPreferences().getCleavagePreference().index) {
                case 0:
                    if (searchParameters.getDigestionPreferences().getEnzymes().get(0).getName().equalsIgnoreCase("Trypsin")) {//
                        json = json.replace("{\\\\\\\"cleavage\\\\\\\": \\\\\\\"default\\\\\\\", \\\\\\\"missed_cleavages\\\\\\\": \\\\\\\"2\\\\\\\", \\\\\\\"__current_case__\\\\\\\": 0}}\\\",", "{\\\\\\\"cleavage\\\\\\\": \\\\\\\"default\\\\\\\", \\\\\\\"missed_cleavages\\\\\\\": \\\\\\\"" + searchParameters.getDigestionPreferences().getnMissedCleavages(searchParameters.getDigestionPreferences().getEnzymes().get(0).getName()) + "\\\\\\\", \\\\\\\"__current_case__\\\\\\\": 0}}\\\",");
                    } else {
                        json = json.replace("{\\\\\\\"cleavage\\\\\\\": \\\\\\\"default\\\\\\\", \\\\\\\"missed_cleavages\\\\\\\": \\\\\\\"2\\\\\\\", \\\\\\\"__current_case__\\\\\\\": 0}}\\\",", "{\\\\\\\"cleavage\\\\\\\": \\\\\\\"0\\\\\\\", \\\\\\\"digests\\\\\\\": [{\\\\\\\"__index__\\\\\\\": 0, \\\\\\\"enzyme\\\\\\\": \\\\\\\"" + searchParameters.getDigestionPreferences().getEnzymes().get(0).getName() + "\\\\\\\", \\\\\\\"missed_cleavages\\\\\\\": \\\\\\\"" + searchParameters.getDigestionPreferences().getnMissedCleavages(searchParameters.getDigestionPreferences().getEnzymes().get(0).getName()) + "\\\\\\\"}], \\\\\\\"__current_case__\\\\\\\": 1}}\\\",");
                    }
                    break;
                case 1:
                    json = json.replace("{\\\\\\\"cleavage\\\\\\\": \\\\\\\"default\\\\\\\", \\\\\\\"missed_cleavages\\\\\\\": \\\\\\\"2\\\\\\\", \\\\\\\"__current_case__\\\\\\\": 0}}\\\",", "{\\\\\\\"cleavage\\\\\\\": \\\\\\\"1\\\\\\\", \\\\\\\"__current_case__\\\\\\\": 2}}\\\",");
                    break;
                case 2:
                    json = json.replace("{\\\\\\\"cleavage\\\\\\\": \\\\\\\"default\\\\\\\", \\\\\\\"missed_cleavages\\\\\\\": \\\\\\\"2\\\\\\\", \\\\\\\"__current_case__\\\\\\\": 0}}\\\",", "{\\\\\\\"cleavage\\\\\\\": \\\\\\\"2\\\\\\\", \\\\\\\"__current_case__\\\\\\\": 3}}\\\",");
                    break;
            }
            //protein_modification_options           
            if (!searchParameters.getPtmSettings().getVariableModifications().isEmpty()) {
                Set<String> modifications = new HashSet<>();
                searchParameters.getPtmSettings().getVariableModifications().forEach((modification) -> {
                    modifications.add("\\\\\\\"" + modification + "\\\\\\\"");
                });
                json = json.replace("\\\\\\\"variable_modifications\\\\\\\": null", "\\\\\\\"variable_modifications\\\\\\\": " + modifications.toString());
            }
            //fixed_modifications
            if (!searchParameters.getPtmSettings().getFixedModifications().isEmpty()) {
                Set<String> modifications = new HashSet<>();
                searchParameters.getPtmSettings().getFixedModifications().forEach((modification) -> {
                    modifications.add("\\\\\\\"" + modification + "\\\\\\\"");
                });
                json = json.replace("\\\\\\\"fixed_modifications\\\\\\\": null", "\\\\\\\"fixed_modifications\\\\\\\": " + modifications.toString());
            }
////            search_engines_options 
            Set<String> search_engines = new HashSet<>();
            searchEnginesList.forEach((searchEng) -> {
                search_engines.add(("\\\\\\\"" + searchEng + "\\\\\\\"").replace(" (Select for noncommercial use only)", "").replace("+", "").replace("-", ""));
            });
            json = json.replace("{\\\\\\\"engines\\\\\\\": null}", "{\\\\\\\"engines\\\\\\\": " + search_engines.toString() + "}");
//            //  //precursor_options
            String updated = "\"{\\\\\\\"forward_ion\\\\\\\": \\\\\\\"" + ions.get(searchParameters.getForwardIons().get(0)) + "\\\\\\\", \\\\\\\"max_charge\\\\\\\": \\\\\\\"" + searchParameters.getMaxChargeSearched().value + "\\\\\\\", \\\\\\\"fragment_tol_units\\\\\\\": \\\\\\\"" + (searchParameters.getFragmentAccuracyType().ordinal() - 1) + "\\\\\\\", \\\\\\\"max_isotope\\\\\\\": \\\\\\\"" + (searchParameters.getMaxIsotopicCorrection()) + "\\\\\\\", \\\\\\\"precursor_ion_tol_units\\\\\\\": \\\\\\\"" + (searchParameters.getPrecursorAccuracyType().ordinal() + 1) + "\\\\\\\", \\\\\\\"min_isotope\\\\\\\": \\\\\\\"" + searchParameters.getMinIsotopicCorrection() + "\\\\\\\", \\\\\\\"fragment_tol\\\\\\\": \\\\\\\"" + searchParameters.getFragmentIonAccuracyInDaltons() + "\\\\\\\", \\\\\\\"min_charge\\\\\\\": \\\\\\\"" + searchParameters.getMinChargeSearched().value + "\\\\\\\", \\\\\\\"reverse_ion\\\\\\\": \\\\\\\"" + ions.get(searchParameters.getRewindIons().get(0)) + "\\\\\\\", \\\\\\\"precursor_ion_tol\\\\\\\": \\\\\\\"" + searchParameters.getPrecursorAccuracy() + "\\\\\\\"}\\\",";
            System.out.println("json contain " + json.contains("\"{\\\\\\\"forward_ion\\\\\\\": \\\\\\\"b\\\\\\\", \\\\\\\"max_charge\\\\\\\": \\\\\\\"4\\\\\\\", \\\\\\\"fragment_tol_units\\\\\\\": \\\\\\\"0\\\\\\\", \\\\\\\"max_isotope\\\\\\\": \\\\\\\"1\\\\\\\", \\\\\\\"precursor_ion_tol_units\\\\\\\": \\\\\\\"1\\\\\\\", \\\\\\\"min_isotope\\\\\\\": \\\\\\\"0\\\\\\\", \\\\\\\"fragment_tol\\\\\\\": \\\\\\\"0.5\\\\\\\", \\\\\\\"min_charge\\\\\\\": \\\\\\\"2\\\\\\\", \\\\\\\"reverse_ion\\\\\\\": \\\\\\\"y\\\\\\\", \\\\\\\"precursor_ion_tol\\\\\\\": \\\\\\\"10.0\\\\\\\"}\\\","));
            json = json.replace("\"{\\\\\\\"forward_ion\\\\\\\": \\\\\\\"b\\\\\\\", \\\\\\\"max_charge\\\\\\\": \\\\\\\"4\\\\\\\", \\\\\\\"fragment_tol_units\\\\\\\": \\\\\\\"0\\\\\\\", \\\\\\\"max_isotope\\\\\\\": \\\\\\\"1\\\\\\\", \\\\\\\"precursor_ion_tol_units\\\\\\\": \\\\\\\"1\\\\\\\", \\\\\\\"min_isotope\\\\\\\": \\\\\\\"0\\\\\\\", \\\\\\\"fragment_tol\\\\\\\": \\\\\\\"0.5\\\\\\\", \\\\\\\"min_charge\\\\\\\": \\\\\\\"2\\\\\\\", \\\\\\\"reverse_ion\\\\\\\": \\\\\\\"y\\\\\\\", \\\\\\\"precursor_ion_tol\\\\\\\": \\\\\\\"10.0\\\\\\\"}\\\",", updated);
////            json = json.replace("toolshed.g2.bx.psu.edu/repos/galaxyp/peptideshaker/peptide_shaker/1.16.20", peptideShaker_Tool);
            json = json.replace("1.16.20", peptideShaker_Tool.getVersion());//.split("\\/")[peptideShaker_Tool.split("\\/").length - 1]);            
            selectedWf = galaxyWorkFlowClient.importWorkflow(json);
            WorkflowInputs workflowInputs = new WorkflowInputs();
            workflowInputs.setWorkflowId(selectedWf.getId());
            workflowInputs.setDestination(new WorkflowInputs.ExistingHistory(historyId));

            WorkflowInputs.WorkflowInput input = new WorkflowInputs.WorkflowInput(fastaFileId, WorkflowInputs.InputSourceType.HDA);
            workflowInputs.setInput("0", input);
            workflowInputs.setInput("1", input2);
//
//            PeptideShakerVisualizationDataset tempWorkflowOutput = new PeptideShakerVisualizationDataset(projectName, null, "", "", null);
//            tempWorkflowOutput.setName(projectName);
//            tempWorkflowOutput.setGalaxyId("tempID");
//            tempWorkflowOutput.setDownloadUrl("tempID");
//            tempWorkflowOutput.setHistoryId(historyId);
//            tempWorkflowOutput.setStatus("running");
//            tempWorkflowOutput.setType("Web Peptide Shaker Dataset");
//            tempWorkflowOutput.setJobId("tempID");

            tempDsOutput = null;
            Thread t = new Thread(() -> {
                tempDsOutput = galaxyWorkFlowClient.runWorkflow(workflowInputs);
//                if (tempDsOutput != null) {
//                    tempWorkflowOutput.setOutputsIds(tempDsOutput.getOutputIds());
//                }

            });
            t.start();
            //reindex mgf files
            Thread t1 = new Thread(() -> {
                int index = 1;
                for (String mgfId : mgfIdsList.keySet()) {
                    this.reIndexFile(mgfId, projectName + "-" + mgfIdsList.get(mgfId) + "-" + (index++) + "-MGFFile", historyId);
                }

            });
            t1.start();
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException ex) {
//                ex.printStackTrace();
//            }
//            galaxyWorkFlowClient.deleteWorkflowRequest(selectedWf.getId());
            while (t.isAlive()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            return null;
//
        } catch (Exception e) {
            e.printStackTrace();
            if (selectedWf != null) {
                galaxyWorkFlowClient.deleteWorkflowRequest(selectedWf.getId());
            }
            return null;
        }
    }

    /**
     * Prepares a work flow which takes as input a collection list.
     *
     * @param inputSource The type of input source for this work flow.
     * @return A WorkflowInputs describing the work flow.
     * @throws InterruptedException
     */
    private WorkflowInputs.WorkflowInput prepareWorkflowCollectionList(WorkflowInputs.InputSourceType inputSource, Set<String> dsIds, String historyId) {

        CollectionResponse collectionResponse = constructFileCollectionList(historyId, dsIds);
        return new WorkflowInputs.WorkflowInput(collectionResponse.getId(),
                inputSource);
    }

    /**
     * Constructs a list collection from the given files within the given
     * history.
     *
     * @param historyId The id of the history to build the collection within.
     * @param inputIds The IDs of the files to add to the collection.
     * @return A CollectionResponse object for the constructed collection.
     */
    private CollectionResponse constructFileCollectionList(String historyId, Set<String> inputIds) {
        CollectionDescription collectionDescription = new CollectionDescription();
        collectionDescription.setCollectionType("list");
        collectionDescription.setName("collection");
        inputIds.stream().map((inputId) -> {
            HistoryDatasetElement element = new HistoryDatasetElement();
            element.setId(inputId);
            element.setName(inputId);
            return element;
        }).forEachOrdered((element) -> {
            collectionDescription.addDatasetElement(element);
        });
        return galaxyHistoriesClient.createDatasetCollection(historyId, collectionDescription);
    }

    private boolean isFirmwareNewer(String testFW, String baseFW) {

        int[] testVer = getVersionNumbers(testFW);
        int[] baseVer = getVersionNumbers(baseFW);
        for (int i = 0; i < testVer.length; i++) {
            if (testVer[i] != baseVer[i]) {
                return testVer[i] > baseVer[i];
            }
        }

        return true;
    }

    private int[] getVersionNumbers(String ver) {
        Matcher m;
        if (ver.split("\\.").length == 4) {
            m = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+).(\\d+)")
                    .matcher(ver);
            if (m != null && !m.matches()) {
                throw new IllegalArgumentException("Malformed FW version");
            }
            return new int[]{
                Integer.parseInt(m.group(1)), // major
                Integer.parseInt(m.group(2)), // minor
                Integer.parseInt(m.group(3)), // rev.
                Integer.parseInt(m.group(4)) // "beta3"
            };
        } else {
            m = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)")
                    .matcher(ver);
            if (m != null && !m.matches()) {
                throw new IllegalArgumentException("Malformed FW version");
            }
            return new int[]{Integer.parseInt(m.group(1)), // major
                Integer.parseInt(m.group(2)), // minor
                Integer.parseInt(m.group(3)) // rev.
        };
        }

    }

    public boolean sendToNels(String historyId, String dsId, String galaxyURL) {

        final HashMap inputDict = new HashMap();
        final HashMap values = initToolDatasetDict(dsId);
        String nelsFolder = VaadinSession.getCurrent().getAttribute("nelsFolderPath") + "";
        String nelsUserId = VaadinSession.getCurrent().getAttribute("nelsUserId") + "";

        HashMap selectedFiles = new HashMap();
        selectedFiles.put("values", values);
        inputDict.put("hist_file", selectedFiles);
        inputDict.put("bach", Boolean.FALSE);
        inputDict.put("nelsId", nelsUserId);
        inputDict.put("selectedFiles", nelsFolder);
        final ToolInputs toolInput = new ToolInputs("nels_exporter_hidden", inputDict);
        toolInput.setHistoryId(historyId);
        try {
            ToolExecution exc = galaxyToolClient.create(galaxyWorkingHistory, toolInput);
            OutputDataset ds = exc.getOutputs().iterator().next();
            if (!galaxyHistoriesClient.showHistory(galaxyWorkingHistory.getId()).isReady()) {
                Thread.sleep(1000);
            }

            deleteDataset(galaxyURL, galaxyWorkingHistory.getId(), ds.getId());
        } catch (GalaxyResponseException | InterruptedException e) {
            e.printStackTrace();
            Notification.show("Could not send it to NeLS :-( ", Notification.Type.ERROR_MESSAGE);
            return false;
        }
        updateHistoryDatastructure();
        return true;

    }

    public boolean getFromNels(String historyId, String dsId) {
        final HashMap inputDict = new HashMap();
//        final HashMap values = initToolDatasetDict(dsId);
        String nelsFolder = VaadinSession.getCurrent().getAttribute("nelsFolderPath") + "";
        String nelsUserId = VaadinSession.getCurrent().getAttribute("nelsUserId") + "";
        inputDict.put("nelsId", nelsUserId);
        inputDict.put("selectedFiles", nelsFolder + "/" + dsId);
        final ToolInputs toolInput = new ToolInputs("nels_file_browser", inputDict);
        toolInput.setHistoryId(historyId);
        try {
            ToolExecution exc = galaxyToolClient.create(galaxyWorkingHistory, toolInput);
            System.out.println("data sent :-) " + exc.getOutputs().iterator().next().getState());
        } catch (GalaxyResponseException e) {
            Notification.show("Could not send it to NeLS :-( ", Notification.Type.ERROR_MESSAGE);
            return false;
        } catch (Exception e) {
            Notification.show("Could not send it to NeLS :-( ", Notification.Type.ERROR_MESSAGE);
            return false;
        }
        updateHistoryDatastructure();
        return true;

    }

    private HashMap initToolDatasetDict(String datasetId) {
        final HashMap values = new HashMap();
        values.put("src", "hda");
        values.put("id", datasetId);
        return values;

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

    public abstract void updateHistoryDatastructure();

    /**
     * Save search settings file into galaxy
     *
     * @param galaxyURL
     * @param userFolder
     * @param searchParameters searchParameters .par file
     * @param workHistoryId
     * @param searchSetiingsFilesMap
     * @param editMode
     * @return
     */
    public void uploaderupload(String workHistoryId) {
        String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
        File file = new File(basepath + "/VAADIN/index.html");
        final ToolsClient.FileUploadRequest request = new ToolsClient.FileUploadRequest(workHistoryId, file);
        request.setDatasetName("index.html");
        ClientResponse res = galaxyToolClient.uploadRequest(request);
        System.out.println("at ~ response " + res.getProperties().values());

    }

}
