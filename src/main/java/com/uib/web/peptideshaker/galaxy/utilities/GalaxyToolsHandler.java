package com.uib.web.peptideshaker.galaxy.utilities;

import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyTransferableFile;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
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
import com.uib.web.peptideshaker.model.core.WebSearchParameters;
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
import pl.exsio.plupload.PluploadFile;

/**
 * This class responsible for interaction with tools on Galaxy Server
 * (Search-GUI and Peptide Shaker)
 *
 * @author Yehia Farag
 */
public abstract class GalaxyToolsHandler {

    /**
     * Galaxy Server contains valid Search-GUI and Peptide Shaker tools.
     */
    private boolean validToolsAvailable;
    /**
     * NeLS storage is supported in the system.
     */
    private boolean nelsSupport;
    /**
     * The main galaxy Work-Flow Client on Galaxy Server.
     */
    private final WorkflowsClient galaxyWorkFlowClient;
    /**
     * The main galaxy History Client on Galaxy Server.
     */
    private final HistoriesClient galaxyHistoriesClient;
    /**
     * The main galaxy Tools Client on Galaxy Server.
     */
    private final ToolsClient galaxyToolClient;
    /**
     * SearchGUI tool representation for the tool on Galaxy Server.
     */
    private Tool search_GUI_Tool = null;
    /**
     * Peptide Shaker tool representation for the tool on Galaxy Server.
     */
    private Tool peptideShaker_Tool = null;
    /**
     * NeLS exporter tool representation for the tool on Galaxy Server.
     */
    private String nelsExporter_Tool_Id = null;
    /**
     * The working history storage representation for the user on Galaxy Server.
     */
    private History galaxyWorkingHistory;
    /**
     * Convenience array for rewind ion type selection.
     */
    private final List<String> ions = new ArrayList(Arrays.asList(new String[]{"a", "b", "c", "x", "y", "z"}));

    public Tool getSearch_GUI_Tool() {
        return search_GUI_Tool;
    }

    public Tool getPeptideShaker_Tool() {
        return peptideShaker_Tool;
    }

    /**
     * Constructor to initialise the main data structure and other variables.
     *
     * @param galaxyToolClient The main galaxy Tools Client on Galaxy Server.
     * @param galaxyWorkFlowClient The main galaxy Work-Flow Client on Galaxy
     * Server.
     * @param galaxyHistoriesClient The main galaxy History Client on Galaxy
     * Server
     */
    public GalaxyToolsHandler(ToolsClient galaxyToolClient, WorkflowsClient galaxyWorkFlowClient, HistoriesClient galaxyHistoriesClient) {

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
                            //} else if (tool.getId().contains("toolshed.g2.bx.psu.edu/repos/galaxyp/peptideshaker/peptide_shaker/")) {
                        } else if (tool.getId().contains("testtoolshed.g2.bx.psu.edu/repos/carlosh/peptideshaker_tests/peptide_shaker/")) {
                            String version = tool.getVersion();//.getId().split("toolshed.g2.bx.psu.edu/repos/galaxyp/peptideshaker/peptide_shaker/")[1];
                            boolean check = isFirmwareNewer(version, PSVersion);
                            if (check) {
                                PSVersion = version;
                                peptideShaker_Tool = tool;

                            }
                        }

                    }
                }

            }
            if (peptideShaker_Tool != null && search_GUI_Tool != null && nelsExporter_Tool_Id != null) {
                validToolsAvailable = true;
                nelsSupport = true;
            } else if (peptideShaker_Tool != null && search_GUI_Tool != null) {
                validToolsAvailable = true;
            }
        } catch (Exception e) {
            if (e.toString().contains("Service Temporarily Unavailable")) {
                Notification.show("Service Temporarily Unavailable", Notification.Type.ERROR_MESSAGE);
                UI.getCurrent().getSession().close();
//                VaadinSession.getCurrent().getSession().invalidate();

            } else {
                System.out.println("at tools are not available");
                e.printStackTrace();
            }
        }
    }

    /**
     * Check if the Search-GUI and Peptide shaker tools are available on Galaxy
     * Server
     *
     * @return Tools are valid and available
     */
    public boolean isToolsAvailable() {
        if (!validToolsAvailable) {
            Notification.show("PeptideShaker tools are not available on this Galaxy Server", Notification.Type.WARNING_MESSAGE);
        }
        return validToolsAvailable;
    }

    /**
     * Re-Index the files MGF files (Convert the stored files in MGF file format
     * to Tab separated format to support byte serving on the server side)
     *
     * @param id file id on Galaxy Server
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
     * @param galaxyURL Galaxy Server web address
     * @param user_folder Personal user folder where the user temporary files
     * are stored
     * @param searchParameters searchParameters .par file
     * @param workHistoryId The working History ID on Galaxy Server
     * @param searchParametersFilesMap The Search Parameters files (.par) Map
     * @param isNew the .par file is new
     * @return updated Search Parameters files (.par) Map
     */
    public Map<String, GalaxyTransferableFile> saveSearchGUIParameters(String galaxyURL, File user_folder, Map<String, GalaxyTransferableFile> searchParametersFilesMap, String workHistoryId, WebSearchParameters searchParameters, boolean isNew) {

        String fileName = searchParameters.getParamFileName() + ".par";
//        String fileId;
//        if (!isNew) {
//            fileId = searchParameters.getFastaFile().getName().split("__")[3];
//            searchParametersFilesMap.remove(fileId);
//            //delete the file and make new one 
//            this.deleteDataset(galaxyURL, workHistoryId, fileId, false);
//
//        } else {
//            fileId = fileName;
//        }
        File file = new File(user_folder, fileName);

        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            searchParameters.saveIdentificationParameters(searchParameters, file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        final ToolsClient.FileUploadRequest request = new ToolsClient.FileUploadRequest(workHistoryId, file);
        request.setDatasetName(fileName);

        List<OutputDataset> excList = galaxyToolClient.upload(request).getOutputs();
        if (excList != null && !excList.isEmpty()) {
            OutputDataset oDs = excList.get(0);
            GalaxyFileObject ds = new GalaxyFileObject();
            ds.setName(oDs.getName());
            ds.setType("Search Parameters File (JSON)");
            ds.setHistoryId(workHistoryId);
            ds.setGalaxyId(oDs.getId());
            ds.setDownloadUrl(galaxyURL + "/datasets/" + ds.getGalaxyId() + "/display?to_ext=" + oDs.getDataTypeExt());
            GalaxyTransferableFile userFolderfile = new GalaxyTransferableFile(user_folder, ds, false);
            searchParametersFilesMap.put(ds.getGalaxyId(), userFolderfile);
            String temFileName = ds.getGalaxyId().replace("/", "_") + ds.getName();

            File updated = new File(user_folder, temFileName);
            try {
                updated.createNewFile();
                FileUtils.copyFile(file, updated);
                file.delete();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return searchParametersFilesMap;
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

    /**
     * Delete files (galaxy datasets) from Galaxy server
     *
     * @param galaxyURL Galaxy Server web address
     * @param historyId The Galaxy Server History ID where the file belong
     * @param dsId The file (galaxy dataset) ID on Galaxy Server
     */
    public void deleteDataset(String galaxyURL, String historyId, String dsId, boolean updatePresenter,boolean singleFile) {
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
                payLoadParamMap.put("purged", Boolean.TRUE);
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
            if(singleFile)
            synchronizeDataWithGalaxyServer(updatePresenter);

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();

        }
    }

    /**
     * Run Online Peptide-Shaker (Search-GUI -> Peptide Shaker) work-flow
     *
     * @param workflowFile the workflow in .ga format to be converted to json.
     * @param projectName The project name
     * @param searchEnginesList List of selected search engine names
     * @param searchParameters Search Parameter object
     * @return JSON string with the information from the .ga workflow properly
     * adapted
     */
    public String get_json_for_SearchGUI_PeptideShaker_WorkFlow(File workflowFile, String projectName, WebSearchParameters searchParameters, Set<String> searchEnginesList) {

        String json = readWorkflowFile(workflowFile);
        /**
         * @todo: find better way to override the search parameters ?
         */
        json = json.replace("3.3.5", search_GUI_Tool.getVersion().trim());
//            json = json.replace("3.3.3.0", search_GUI_Tool.getVersion().trim()); //for multi mgf workflow
        json = json.replace("SearchGUI_Label", projectName + "-SearchGUI Results").replace("ZIP_Label", projectName + "-ZIP");
        String createDecoy = "";//searchParameters.getFastaFile().getName().split("__")[2];
        json = json.replace("\\\\\\\"create_decoy\\\\\\\": \\\\\\\"true\\\\\\\"", "\\\\\\\"create_decoy\\\\\\\": \\\\\\\"" + createDecoy + "\\\\\\\"");
//        /**
//         * Protein_digest_options.
//         */
////        switch (searchParameters.getDigestionPreferences().getCleavagePreference().index) {
////            case 0:
////                if (searchParameters.getDigestionPreferences().getEnzymes().get(0).getName().equalsIgnoreCase("Trypsin")) {//
////                    json = json.replace("{\\\\\\\"cleavage\\\\\\\": \\\\\\\"default\\\\\\\", \\\\\\\"missed_cleavages\\\\\\\": \\\\\\\"2\\\\\\\", \\\\\\\"__current_case__\\\\\\\": 0}}\\\",", "{\\\\\\\"cleavage\\\\\\\": \\\\\\\"default\\\\\\\", \\\\\\\"missed_cleavages\\\\\\\": \\\\\\\"" + searchParameters.getDigestionPreferences().getnMissedCleavages(searchParameters.getDigestionPreferences().getEnzymes().get(0).getName()) + "\\\\\\\", \\\\\\\"__current_case__\\\\\\\": 0}}\\\",");
////                } else {
////                    json = json.replace("{\\\\\\\"cleavage\\\\\\\": \\\\\\\"default\\\\\\\", \\\\\\\"missed_cleavages\\\\\\\": \\\\\\\"2\\\\\\\", \\\\\\\"__current_case__\\\\\\\": 0}}\\\",", "{\\\\\\\"cleavage\\\\\\\": \\\\\\\"0\\\\\\\", \\\\\\\"digests\\\\\\\": [{\\\\\\\"__index__\\\\\\\": 0, \\\\\\\"enzyme\\\\\\\": \\\\\\\"" + searchParameters.getDigestionPreferences().getEnzymes().get(0).getName() + "\\\\\\\", \\\\\\\"missed_cleavages\\\\\\\": \\\\\\\"" + searchParameters.getDigestionPreferences().getnMissedCleavages(searchParameters.getDigestionPreferences().getEnzymes().get(0).getName()) + "\\\\\\\"}], \\\\\\\"__current_case__\\\\\\\": 1}}\\\",");
////                }
////                break;
////            case 1:
////                json = json.replace("{\\\\\\\"cleavage\\\\\\\": \\\\\\\"default\\\\\\\", \\\\\\\"missed_cleavages\\\\\\\": \\\\\\\"2\\\\\\\", \\\\\\\"__current_case__\\\\\\\": 0}}\\\",", "{\\\\\\\"cleavage\\\\\\\": \\\\\\\"1\\\\\\\", \\\\\\\"__current_case__\\\\\\\": 2}}\\\",");
////                break;
////            case 2:
////                json = json.replace("{\\\\\\\"cleavage\\\\\\\": \\\\\\\"default\\\\\\\", \\\\\\\"missed_cleavages\\\\\\\": \\\\\\\"2\\\\\\\", \\\\\\\"__current_case__\\\\\\\": 0}}\\\",", "{\\\\\\\"cleavage\\\\\\\": \\\\\\\"2\\\\\\\", \\\\\\\"__current_case__\\\\\\\": 3}}\\\",");
////                break;
////        }
//        /**
//         * Protein_modification_options.
//         */
//        if (!searchParameters.getPtmSettings().getVariableModifications().isEmpty()) {
//            Set<String> modifications = new HashSet<>();
//            searchParameters.getPtmSettings().getVariableModifications().forEach((modification) -> {
//                modifications.add("\\\\\\\"" + modification + "\\\\\\\"");
//            });
//            json = json.replace("\\\\\\\"variable_modifications\\\\\\\": null", "\\\\\\\"variable_modifications\\\\\\\": " + modifications.toString());
//        }
//        /**
//         * Fixed_modifications.
//         */
//        if (!searchParameters.getPtmSettings().getFixedModifications().isEmpty()) {
//            Set<String> modifications = new HashSet<>();
//            searchParameters.getPtmSettings().getFixedModifications().forEach((modification) -> {
//                modifications.add("\\\\\\\"" + modification + "\\\\\\\"");
//            });
//            json = json.replace("\\\\\\\"fixed_modifications\\\\\\\": null", "\\\\\\\"fixed_modifications\\\\\\\": " + modifications.toString());
//        }
//        /**
//         * Search engines options.
//         */
//        Set<String> search_engines = new HashSet<>();
//        searchEnginesList.forEach((searchEng) -> {
//            search_engines.add(("\\\\\\\"" + searchEng + "\\\\\\\"").replace(" (Select for noncommercial use only)", "").replace("+", "").replace("-", ""));
//        });
//        json = json.replace("{\\\\\\\"engines\\\\\\\": null}", "{\\\\\\\"engines\\\\\\\": " + search_engines.toString() + "}");
//        /**
//         * Precursor Options.
//         */
//        String updated = "\"{\\\\\\\"forward_ion\\\\\\\": \\\\\\\"" + ions.get(searchParameters.getForwardIons().get(0)) + "\\\\\\\", \\\\\\\"max_charge\\\\\\\": \\\\\\\"" + searchParameters.getMaxChargeSearched().value + "\\\\\\\", \\\\\\\"fragment_tol_units\\\\\\\": \\\\\\\"" + (searchParameters.getFragmentAccuracyType().ordinal() - 1) + "\\\\\\\", \\\\\\\"max_isotope\\\\\\\": \\\\\\\"" + (searchParameters.getMaxIsotopicCorrection()) + "\\\\\\\", \\\\\\\"precursor_ion_tol_units\\\\\\\": \\\\\\\"" + (searchParameters.getPrecursorAccuracyType().ordinal() + 1) + "\\\\\\\", \\\\\\\"min_isotope\\\\\\\": \\\\\\\"" + searchParameters.getMinIsotopicCorrection() + "\\\\\\\", \\\\\\\"fragment_tol\\\\\\\": \\\\\\\"" + searchParameters.getFragmentIonAccuracyInDaltons() + "\\\\\\\", \\\\\\\"min_charge\\\\\\\": \\\\\\\"" + searchParameters.getMinChargeSearched().value + "\\\\\\\", \\\\\\\"reverse_ion\\\\\\\": \\\\\\\"" + ions.get(searchParameters.getRewindIons().get(0)) + "\\\\\\\", \\\\\\\"precursor_ion_tol\\\\\\\": \\\\\\\"" + searchParameters.getPrecursorAccuracy() + "\\\\\\\"}\\\",";
//        json = json.replace("\"{\\\\\\\"forward_ion\\\\\\\": \\\\\\\"b\\\\\\\", \\\\\\\"max_charge\\\\\\\": \\\\\\\"4\\\\\\\", \\\\\\\"fragment_tol_units\\\\\\\": \\\\\\\"0\\\\\\\", \\\\\\\"max_isotope\\\\\\\": \\\\\\\"1\\\\\\\", \\\\\\\"precursor_ion_tol_units\\\\\\\": \\\\\\\"1\\\\\\\", \\\\\\\"min_isotope\\\\\\\": \\\\\\\"0\\\\\\\", \\\\\\\"fragment_tol\\\\\\\": \\\\\\\"0.5\\\\\\\", \\\\\\\"min_charge\\\\\\\": \\\\\\\"2\\\\\\\", \\\\\\\"reverse_ion\\\\\\\": \\\\\\\"y\\\\\\\", \\\\\\\"precursor_ion_tol\\\\\\\": \\\\\\\"10.0\\\\\\\"}\\\",", updated);
//        json = json.replace("1.16.31", peptideShaker_Tool.getVersion());
//            json = json.replace("1.16.4", peptideShaker_Tool.getVersion());

        return json;
    }

    /**
     * Run Online Peptide-Shaker (Search-GUI -> Peptide Shaker) work-flow
     *
     * @param projectName The project name
     * @param fastaFileId FASTA file dataset id
     * @param searchParameterFileId .par file id
     * @param inputFileIdsList list of input MGF file dataset IDs on Galaxy
     * Server
     * @param searchEnginesList List of selected search engine names
     * @param historyId Galaxy history id that will store the results
     * @param searchParameters Search Parameter object
     * @param quant full quant pipe-line
     * @return Generated PeptideShaker visualisation dataset (Temporary dataset)
     */
    public PeptideShakerVisualizationDataset execute_SearchGUI_PeptideShaker_WorkFlow(String projectName, String fastaFileId, String searchParameterFileId, Map<String, String> inputFileIdsList, Set<String> searchEnginesList, String historyId, WebSearchParameters searchParameters, boolean quant) {
        Workflow selectedWf = null;
        try {
            String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
            File workflowFile;
            WorkflowInputs.WorkflowInput workflowInput2;

            if (quant) {
                workflowFile = new File(basepath + "/VAADIN/Galaxy-Workflow-Final-full-pipeline-quant-2019.ga");//Galaxy-Workflow-Web-Peptide-Shaker-Multi-MGF-2018.ga
//            workflowInput2 = prepareWorkflowCollectionList(WorkflowInputs.InputSourceType.HDCA, inputFileIdsList.keySet(), historyId);
                workflowInput2 = new WorkflowInputs.WorkflowInput(inputFileIdsList.keySet().iterator().next(), WorkflowInputs.InputSourceType.HDA);

            } else if (inputFileIdsList.size() > 1) {
                workflowFile = new File(basepath + "/VAADIN/Galaxy-Workflow-Web-Peptide-Shaker-Multi-MGF-2018-updated-i.ga");//Galaxy-Workflow-Web-Peptide-Shaker-Multi-MGF-2018.ga
                workflowInput2 = prepareWorkflowCollectionList(WorkflowInputs.InputSourceType.HDCA, inputFileIdsList.keySet(), historyId);
            } else {
                workflowFile = new File(basepath + "/VAADIN/Galaxy-Workflow-Web-Peptide-Shaker-Single-MGF-2018-updated-i.ga");
                workflowInput2 = new WorkflowInputs.WorkflowInput(inputFileIdsList.keySet().iterator().next(), WorkflowInputs.InputSourceType.HDA);
            }
            String jsonWorkflow = readWorkflowFile(workflowFile);

            /**
             * @todo: find better way to override the search parameters ?
             */
//            json = json.replace("3.3.5", search_GUI_Tool.getVersion().trim());//4.0.1_SNAPSHOT.1    //2.0.1_SNAPSHOT.5
//            json = json.replace("3.3.3.0", search_GUI_Tool.getVersion().trim()); //for multi mgf workflow
            jsonWorkflow = jsonWorkflow.replace("Label-SearchGUI", projectName + "-SearchGUI Results").replace("Label-PS", projectName + "-ZIP").replace("Label-MOFF", projectName + "-MOFF").replace("Label-MGF", projectName + "-MGF");
//            String createDecoy = searchParameters.getFastaFile().getName().split("__")[2];
//            json = json.replace("\\\\\\\"create_decoy\\\\\\\": \\\\\\\"true\\\\\\\"", "\\\\\\\"create_decoy\\\\\\\": \\\\\\\"" + createDecoy + "\\\\\\\"");
            /**
             * Protein_digest_options. //
             */
////            switch (searchParameters.getDigestionPreferences().getCleavagePreference().index) {
////                case 0:
////                    if (searchParameters.getDigestionPreferences().getEnzymes().get(0).getName().equalsIgnoreCase("Trypsin")) {//
////                        json = json.replace("{\\\\\\\"cleavage\\\\\\\": \\\\\\\"default\\\\\\\", \\\\\\\"missed_cleavages\\\\\\\": \\\\\\\"2\\\\\\\", \\\\\\\"__current_case__\\\\\\\": 0}}\\\",", "{\\\\\\\"cleavage\\\\\\\": \\\\\\\"default\\\\\\\", \\\\\\\"missed_cleavages\\\\\\\": \\\\\\\"" + searchParameters.getDigestionPreferences().getnMissedCleavages(searchParameters.getDigestionPreferences().getEnzymes().get(0).getName()) + "\\\\\\\", \\\\\\\"__current_case__\\\\\\\": 0}}\\\",");
////                    } else {
////                        json = json.replace("{\\\\\\\"cleavage\\\\\\\": \\\\\\\"default\\\\\\\", \\\\\\\"missed_cleavages\\\\\\\": \\\\\\\"2\\\\\\\", \\\\\\\"__current_case__\\\\\\\": 0}}\\\",", "{\\\\\\\"cleavage\\\\\\\": \\\\\\\"0\\\\\\\", \\\\\\\"digests\\\\\\\": [{\\\\\\\"__index__\\\\\\\": 0, \\\\\\\"enzyme\\\\\\\": \\\\\\\"" + searchParameters.getDigestionPreferences().getEnzymes().get(0).getName() + "\\\\\\\", \\\\\\\"missed_cleavages\\\\\\\": \\\\\\\"" + searchParameters.getDigestionPreferences().getnMissedCleavages(searchParameters.getDigestionPreferences().getEnzymes().get(0).getName()) + "\\\\\\\"}], \\\\\\\"__current_case__\\\\\\\": 1}}\\\",");
////                    }
////                    break;
////                case 1:
////                    json = json.replace("{\\\\\\\"cleavage\\\\\\\": \\\\\\\"default\\\\\\\", \\\\\\\"missed_cleavages\\\\\\\": \\\\\\\"2\\\\\\\", \\\\\\\"__current_case__\\\\\\\": 0}}\\\",", "{\\\\\\\"cleavage\\\\\\\": \\\\\\\"1\\\\\\\", \\\\\\\"__current_case__\\\\\\\": 2}}\\\",");
////                    break;
////                case 2:
////                    json = json.replace("{\\\\\\\"cleavage\\\\\\\": \\\\\\\"default\\\\\\\", \\\\\\\"missed_cleavages\\\\\\\": \\\\\\\"2\\\\\\\", \\\\\\\"__current_case__\\\\\\\": 0}}\\\",", "{\\\\\\\"cleavage\\\\\\\": \\\\\\\"2\\\\\\\", \\\\\\\"__current_case__\\\\\\\": 3}}\\\",");
////                    break;
////            }
//            /**
//             * Protein_modification_options.
//             */
//            if (!searchParameters.getPtmSettings().getVariableModifications().isEmpty()) {
//                Set<String> modifications = new HashSet<>();
//                searchParameters.getPtmSettings().getVariableModifications().forEach((modification) -> {
//                    modifications.add("\\\\\\\"" + modification + "\\\\\\\"");
//                });
//                json = json.replace("\\\\\\\"variable_modifications\\\\\\\": null", "\\\\\\\"variable_modifications\\\\\\\": " + modifications.toString());
//            }

//            String jsonWorkflow = get_json_for_SearchGUI_PeptideShaker_WorkFlow(workflowFile, projectName, searchParameters, searchEnginesList);
            /**
             * Search engines options.
             */
            Set<String> search_engines = new HashSet<>();
            searchEnginesList.forEach((searchEng) -> {
                search_engines.add(("\\\\\\\"" + searchEng + "\\\\\\\"").replace(" (Select for noncommercial use only)", "").replace("+", "").replace("-", ""));
            });           
            jsonWorkflow = jsonWorkflow.replace("\\\"{\\\\\\\"engines\\\\\\\": null}\\\"", "\\\"{\\\\\\\"engines\\\\\\\": "+search_engines.toString()+"}\\\"");
            selectedWf = galaxyWorkFlowClient.importWorkflow(jsonWorkflow);

            WorkflowInputs workflowInputs = new WorkflowInputs();
            workflowInputs.setWorkflowId(selectedWf.getId());
            workflowInputs.setDestination(new WorkflowInputs.ExistingHistory(historyId));

            WorkflowInputs.WorkflowInput workflowInput0 = new WorkflowInputs.WorkflowInput(searchParameterFileId, WorkflowInputs.InputSourceType.HDA);
            WorkflowInputs.WorkflowInput workflowInput1 = new WorkflowInputs.WorkflowInput(fastaFileId, WorkflowInputs.InputSourceType.HDA);
            workflowInputs.setInput("0", workflowInput0);
            workflowInputs.setInput("1", workflowInput1);
            workflowInputs.setInput("2", workflowInput2);

            Thread t = new Thread(() -> {
                galaxyWorkFlowClient.runWorkflow(workflowInputs);
            });
            t.start();
            /**
             * Re-index MGF files.
             */
            Thread t1 = new Thread(() -> {
                int index = 1;
                if (!quant) {
                    if (inputFileIdsList.size() == 1) {
                        for (String mgfId : inputFileIdsList.keySet()) {
                            this.reIndexFile(mgfId, projectName + "-" + inputFileIdsList.get(mgfId) + "-" + (index++) + "-MGFFile", historyId);
                        }
                    } else {
                        for (String mgfId : inputFileIdsList.keySet()) {
                            this.reIndexFile(mgfId, projectName + "-" + mgfId + "-" + (index++) + "-MGFFile", historyId);
                        }
                    }
                }
            });
            t1.start();

            galaxyWorkFlowClient.deleteWorkflowRequest(selectedWf.getId());
            while (t.isAlive()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            if (selectedWf != null) {
                galaxyWorkFlowClient.deleteWorkflowRequest(selectedWf.getId());
            }
            return null;
        }
    }

    /**
     * Run Online Peptide-Shaker + PathwayMatcher (Search-GUI -> Peptide Shaker
     * -> PathwayMatcher) work-flow
     *
     * @param projectName The project name
     * @param fastaFileId FASTA file dataset id
     * @param mgfIdsList list of input MGF file dataset IDs on Galaxy Server
     * @param searchEnginesList List of selected search engine names
     * @param historyId Galaxy history id that will store the results
     * @param searchParameters Search Parameter object
     * @return Generated PeptideShaker visualisation dataset (Temporary dataset)
     */
    public PeptideShakerVisualizationDataset execute_SearchGUI_PeptideShaker_PathwayMatcher_WorkFlow(String projectName, String fastaFileId, Map<String, String> mgfIdsList, Set<String> searchEnginesList, String historyId, WebSearchParameters searchParameters) {
        Workflow selectedWf = null;
        try {
            String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
            File workflowFile;
            WorkflowInputs.WorkflowInput workflowInput2_mgf;
            if (mgfIdsList.size() > 1) {
                workflowFile = new File(basepath + "/VAADIN/Galaxy-Workflow-Web-Peptide-Shaker-Multi-MGF-Pathway-Matcher-2018-updated-i.ga");//Galaxy-Workflow-Web-Peptide-Shaker-Multi-MGF-2018.ga
                workflowInput2_mgf = prepareWorkflowCollectionList(WorkflowInputs.InputSourceType.HDCA, mgfIdsList.keySet(), historyId);
            } else {
                workflowFile = new File(basepath + "/VAADIN/Galaxy-Workflow-Web-Peptide-Shaker-Single-MGF-Pathway-Matcher-2018-updated-i.ga");
                workflowInput2_mgf = new WorkflowInputs.WorkflowInput(mgfIdsList.keySet().iterator().next(), WorkflowInputs.InputSourceType.HDA);
            }

            String jsonWorkflow = get_json_for_SearchGUI_PeptideShaker_WorkFlow(workflowFile, projectName, searchParameters, searchEnginesList);

            selectedWf = galaxyWorkFlowClient.importWorkflow(jsonWorkflow);

            WorkflowInputs workflowInputs = new WorkflowInputs();
            workflowInputs.setWorkflowId(selectedWf.getId());
            workflowInputs.setDestination(new WorkflowInputs.ExistingHistory(historyId));

            WorkflowInputs.WorkflowInput workflowInput1_fasta = new WorkflowInputs.WorkflowInput(fastaFileId, WorkflowInputs.InputSourceType.HDA);
            workflowInputs.setInput("0", workflowInput1_fasta);
            workflowInputs.setInput("1", workflowInput2_mgf);

            Thread t = new Thread(() -> {
                galaxyWorkFlowClient.runWorkflow(workflowInputs);
            });
            t.start();
            /**
             * Re-index MGF files.
             */
            Thread t1 = new Thread(() -> {
                int index = 1;
                for (String mgfId : mgfIdsList.keySet()) {
                    this.reIndexFile(mgfId, projectName + "-" + mgfIdsList.get(mgfId) + "-" + (index++) + "-MGFFile", historyId);
                }

            });
            t1.start();

            galaxyWorkFlowClient.deleteWorkflowRequest(selectedWf.getId());
            while (t.isAlive()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            return null;
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
     * @param dsIds The set of files IDs on Galaxy Server that will be used to
     * generate the collection dataset
     * @param historyId The history ID on Galaxy Server where the collection
     * will be saved
     * @return A WorkflowInputs describing the work flow.
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
     * @param historyId The history id on Galaxy Server to store the collection
     * in.
     * @param inputIds The IDs of the files (galaxy datasets) on Galaxy Server
     * that will be added to the collection.
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

    /**
     * Compare the tools versions and select the newer version.
     *
     * @param testFW the comparable version
     * @param baseFW the base version to compare with
     * @return if the testFW is newer than the baseFW
     */
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

    /**
     * Convert the version (String) into integer array.
     *
     * @param ver the string version
     * @return integer array contain the version parts
     */
    private int[] getVersionNumbers(String ver) {
        Matcher m;
        ver = ver.replace("_SNAPSHOT.", ".");
        if (ver.split("\\.").length == 4) {
            m = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+).(\\d+)").matcher(ver);
            if (m != null && !m.matches()) {
                throw new IllegalArgumentException("Malformed FW version");
            } else if (m == null) {
                throw new IllegalArgumentException("Matcher is null");
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
            } else if (m == null) {
                throw new IllegalArgumentException("Matcher is null");
            }
            return new int[]{Integer.parseInt(m.group(1)), // major
                Integer.parseInt(m.group(2)), // minor
                Integer.parseInt(m.group(3)) // rev.
        };
        }

    }

    /**
     * Store files on NeLS storage system.
     *
     * @param historyId The galaxy history ID on Galaxy Server that contains the
     * file
     * @param dsId the galaxy file (dataset) ID on Galaxy Server
     * @param galaxyURL the web address of the Galaxy Server
     * @return the file successfully stored on NeLS storage system
     */
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
            deleteDataset(galaxyURL, galaxyWorkingHistory.getId(), ds.getId(), true,true);
        } catch (GalaxyResponseException | InterruptedException e) {
            e.printStackTrace();
            Notification.show("Could not send it to NeLS :-( ", Notification.Type.ERROR_MESSAGE);
            return false;
        }
        synchronizeDataWithGalaxyServer(true);
        return true;

    }

    /**
     * Import files from NeLS storage system to Galaxy Server.
     *
     * @param historyId The galaxy history ID on Galaxy Server that will
     * contains the transferred file
     * @param dsId the file (dataset) ID on NeLS storage system
     * @return the file successfully imported from NeLS storage system to Galaxy
     * Server
     */
    public boolean getFromNels(String historyId, String dsId) {
        final HashMap inputDict = new HashMap();
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
        synchronizeDataWithGalaxyServer(true);
        return true;

    }

    /**
     * Initialise the file (dataset) to be used on Work-flow on Galaxy Server.
     *
     * @param datasetId the file (dataset) ID on NeLS storage system
     * @return HashMap that contains all the dataset information required by
     * Galaxy Server
     */
    private HashMap initToolDatasetDict(String datasetId) {
        final HashMap values = new HashMap();
        values.put("src", "hda");
        values.put("id", datasetId);
        return values;

    }

    /**
     * Convert JSON object to readable java HashMap.
     *
     * @param object the JSON object to be converted
     * @return HashMap that contains the information
     * @throws JSONException
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
     * Convert JSON object to readable java List.
     *
     * @param object the JSON object to be converted
     * @return List that contains the information
     * @throws JSONException
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
     * Upload Files (FASTA and MGF files to Galaxy Server)
     *
     * @param workHistoryId The galaxy history ID where the files will be stored
     * @param toUploadFiles list of the files to be uploaded to Galaxy Server
     * @return files are successfully uploaded to Galaxy Server
     */
    public boolean uploadToGalaxy(String workHistoryId, PluploadFile[] toUploadFiles) {
        Thread t = new Thread(() -> {
            for (PluploadFile file : toUploadFiles) {
                File tFile = (File) file.getUploadedFile();
                final ToolsClient.FileUploadRequest request = new ToolsClient.FileUploadRequest(workHistoryId, tFile);
                request.setDatasetName(file.getName());
                List<OutputDataset> results = galaxyToolClient.upload(request).getOutputs();
                tFile.delete();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            synchronizeDataWithGalaxyServer(true);
        });
        t.start();
        return true;
    }

    /**
     * Check if the system support NeLS storage.
     *
     * @return system support NeLS storage
     */
    public boolean isNelsSupport() {
        return nelsSupport;
    }

    /**
     * Synchronise and update Online PEptideShaker file system with Galaxy
     * Server.
     */
    public abstract void synchronizeDataWithGalaxyServer(boolean updatePresenterview);

}
