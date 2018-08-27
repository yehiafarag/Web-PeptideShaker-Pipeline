package com.uib.web.peptideshaker.model.core.pdb;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.HttpsURLConnection;
import static javax.ws.rs.core.HttpHeaders.USER_AGENT;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * This class represents EBI web service for PDB data
 *
 * @author Yehia Farag
 */
public class EBIRestService {

    /**
     * Match UniProt protein accession to PDB accession
     *
     * @param uniprotAccessionSet set of UniProt accessions
     * @return set of PDB accessions
     */
    public Map<String, Map<String, PDBMatch>> getPdbIds(Set<String> uniprotAccessionSet) {
        String accessions = uniprotAccessionSet.toString().replace("[", "").replace("]", "");
        return this.getPdbIds(accessions, uniprotAccessionSet.size() == 1);
    }

    /**
     * Get PDB matches set from UniProt accessions
     *
     * @param accessions set of UniProt accessions
     * @param isDoGet the HTTP method to use is GET
     * @return set of PDB matches accessions
     */
    public Map<String, Map<String, PDBMatch>> getPdbIds(String accessions, boolean isDoGet) {
        Map<String, Map<String, PDBMatch>> pdbMap = new LinkedHashMap<>();
        if (accessions == null || accessions.trim().equalsIgnoreCase("")) {
            return pdbMap;
        }
        try {
            String url = "https://www.ebi.ac.uk/pdbe/api/mappings/best_structures/";
            String urlParameters = accessions;
            String respond;
            if (isDoGet) {
                respond = sendGet(url + urlParameters);
            } else {
                respond = sendPost(url, urlParameters);
            }
            if (respond.equalsIgnoreCase("")) {
                return pdbMap;
            }
            JSONObject jsonObject = new JSONObject(respond);

            if (jsonObject != JSONObject.NULL) {
                Map<String, Object> retMap = toMap(jsonObject);
                String[] accArr = accessions.split(",");
                for (String acc : accArr) {
                    Map<String, PDBMatch> map = new LinkedHashMap<>();
                    List<Object> l = (List<Object>) retMap.get(acc.toUpperCase().trim());
                    if (l != null) {
                        l.stream().map((o) -> (Map<String, Object>) o).forEachOrdered((supMap) -> {
                            String pdbId = supMap.get("pdb_id") + "";
                            if (!map.containsKey(pdbId)) {
                                map.put(pdbId, new PDBMatch(pdbId));
                            }
                            PDBMatch tMatch = map.get(pdbId);
                            tMatch.addChainId(supMap.get("chain_id") + "");
                        });
                    } else {
                        System.out.println("not exist pdb for it " + acc);
                    }
                    pdbMap.put(acc, map);
                }

            }

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return pdbMap;
    }

    /**
     * Update PDB matches information (summary)
     *
     * @param pdbMatches map of PDB matches
     * @return update map of PDB matches
     */
    public Map<String, PDBMatch> getPdbSummary(Map<String, PDBMatch> pdbMatches) {
        try {
            String url = " https://www.ebi.ac.uk/pdbe/api/pdb/entry/summary/";
            String urlParameters = pdbMatches.keySet().toString().replace("[", "").replace("]", "");
            String respond;
            if (pdbMatches.size() == 1) {
                respond = sendGet(url + urlParameters);
            } else {
                respond = sendPost(url, urlParameters);
            }
            if (respond.equalsIgnoreCase("")) {
                return null;
            }
            JSONObject jsonObject = new JSONObject(respond);
            if (jsonObject != JSONObject.NULL) {
                Map<String, Object> retMap = toMap(jsonObject);
                pdbMatches.keySet().forEach((pdbId) -> {
                    List<Object> l = (List<Object>) retMap.get(pdbId.toLowerCase());
                    Map<String, Object> subMap = (Map<String, Object>) l.get(0);
                    PDBMatch match = pdbMatches.get(pdbId);
                    match.setDescription((String) subMap.get("title"));
                });

            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return pdbMatches;
    }

    /**
     * Update PDB matches information (full information)
     *
     * @param proteinSequence protein sequence
     * @param pdbMatch PDB match
     * @return update PDB match
     */
    public PDBMatch updatePdbInformation(PDBMatch pdbMatch, String proteinSequence) {
        try {

            String url = " https://www.ebi.ac.uk/pdbe/api/pdb/entry/molecules/";
            String urlParameters = pdbMatch.getPdbId().toLowerCase();
            String respond;
            respond = sendGet(url + urlParameters);
            JSONObject jsonObject = new JSONObject(respond);
            if (jsonObject != JSONObject.NULL) {
                Map<String, Object> retMap = toMap(jsonObject);
                List<Object> l = (List<Object>) retMap.get(urlParameters.toLowerCase());
                Map<String, Object> subMap = null;
                for (Object o : l) {

                    if (o.toString().contains("pdb_sequence") && o.toString().contains("in_chains=" + pdbMatch.getChainsIds())) {
                        subMap = (Map<String, Object>) o;
                        pdbMatch.setEntity_id(subMap.get("entity_id"));
                        break;
                    }
                }

                if (subMap != null && subMap.get("pdb_sequence") != null) {
                    pdbMatch.setSequence(subMap.get("pdb_sequence") + "");
                }
                url = " https://www.ebi.ac.uk/pdbe/api/pdb/entry/polymer_coverage/";
                respond = sendGet(url + urlParameters);
                jsonObject = new JSONObject(respond);
                if (jsonObject != JSONObject.NULL) {
                    retMap = toMap(jsonObject);
                    subMap = (Map<String, Object>) retMap.get(urlParameters.toLowerCase());
                    l = (List<Object>) subMap.get("molecules");
                    subMap = (Map<String, Object>) l.get(pdbMatch.getEntity_id() - 1);
                    l = (List<Object>) subMap.get("chains");
//
                    for (Object o : l) {
                        subMap = (Map<String, Object>) o;
                        String chain_id = subMap.get("chain_id") + "";
                        String struct_asym_id = subMap.get("struct_asym_id") + "";
                        List<Object> observed = (List<Object>) subMap.get("observed");
                        observed.stream().map((subObject) -> (Map<String, Object>) subObject).map((chainData) -> {
                            Map<String, Object> subChainData = (Map<String, Object>) chainData.get("start");
                            int start_author_residue_number = (Integer) subChainData.get("author_residue_number");
                            int start_residue_number = (Integer) subChainData.get("residue_number");
                            subChainData = (Map<String, Object>) chainData.get("end");
                            int end_author_residue_number = (Integer) subChainData.get("author_residue_number");
                            int end_residue_number = (Integer) subChainData.get("residue_number");

                            String chainSequence = pdbMatch.getSequence().substring(start_residue_number - 1, end_residue_number - 1);
                            int tstart_author_residue_number = -5;
                            int tend_author_residue_number = -5;

                            int uniprotLength = end_author_residue_number - start_author_residue_number;
                            int diffrent = (end_residue_number - start_residue_number) - uniprotLength;
                            String uProtSeq = proteinSequence.replace("L", "I");
                            String uChainSeq = chainSequence.replace("L", "I");

                            if (uProtSeq.contains(uChainSeq)) {
                                tstart_author_residue_number = uProtSeq.indexOf(uChainSeq);
                                tend_author_residue_number = tstart_author_residue_number + chainSequence.length();
                            }
//                            else if (uChainSeq.length()>25 && uProtSeq.contains(uChainSeq.substring(20))) {
////                                 System.out.println("case II");
////                                tstart_author_residue_number = uProtSeq.indexOf(uChainSeq);
////                                tend_author_residue_number = tstart_author_residue_number + chainSequence.length();
//                            } else {
//                                 System.out.println("case III");
////                                 System.out.println("at u seq "+uChainSeq+"   " );
////                                for (int i = 1; i < uChainSeq.length(); i++) {
////                                    String t = uChainSeq.substring(i);
////                                    if (uProtSeq.contains(t)) {
////                                        tstart_author_residue_number = uProtSeq.indexOf(t);
////                                        tend_author_residue_number = tstart_author_residue_number + chainSequence.length() - i + 1;
////                                        break;
////                                    }
////                                }
////
//                            }
                            if (tstart_author_residue_number < 0) {
                                tstart_author_residue_number = start_author_residue_number;
                            }
                            if (tend_author_residue_number < 0) {
                                tend_author_residue_number = end_author_residue_number;
                            }

//                              System.out.println("chain_id  "+chain_id+"   start_author_residue_number "+start_author_residue_number+"  to  "+tstart_author_residue_number+"   end_author_residue_number  "+end_author_residue_number+"  to  "+tend_author_residue_number);
//                            
                            ChainBlock chainParam = new ChainBlock(struct_asym_id, chain_id, tstart_author_residue_number, start_residue_number, tend_author_residue_number, end_residue_number, uChainSeq);
                            return chainParam;
                        }).forEachOrdered((chainParam) -> {
                            pdbMatch.addChain(chainParam);
                        });

                    }

                }

            }

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return pdbMatch;
    }

    /**
     * Send HTTP doPOST request.
     *
     * @param url link to server
     * @param urlParameters request parameters
     * @return response as string
     */
    private String sendPost(String url, String urlParameters) {

        try {
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            // Send post request
            con.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.writeBytes(urlParameters);
                wr.flush();
            }

            int responseCode = con.getResponseCode();

            StringBuilder response;
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

            //print result
            return response.toString();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "";

    }

    /**
     * Send HTTP doGET request.
     *
     * @param url link to server
     * @return response as string
     */
    private String sendGet(String url) {

        try {

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);

            StringBuilder response = new StringBuilder();;
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            } catch (Exception e) {

            }

            //print result
            return response.toString();
        } catch (MalformedURLException | ProtocolException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    /**
     * Convert JSON object to Java readable map
     *
     * @param object JSON object to be converted
     * @return Java Hash map has all the data
     * @throws JSONException in case of error in reading JSON file
     */
    public Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    /**
     * Convert JSON object to Java readable list
     *
     * @param array JSON array to be converted
     * @return Java List has all the data
     * @throws JSONException in case of error in reading JSON file
     */
    public List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

}