  package com.uib.web.peptideshaker.model.core.pdb;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class represent mapper to get Pdb acc from Uniprot acc
 *
 * @author Yehia Farag
 */
public class UniprotToPdb {

    private final String UNIPROT_SERVER = "http://www.uniprot.org/";

    public Map<String, LinkedHashSet<String>> getPdbIds(Set<String> uniprotAccessionSet) {
        String accessions = uniprotAccessionSet.toString().replace(",", "\t").replace("[", "").replace("]", "");
        return this.getPdbIds(accessions);
    }

    public Map<String, LinkedHashSet<String>> getPdbIds(String accessions) {
        String results = this.process("uploadlists", new ParameterNameValue[]{
            new ParameterNameValue("from", "ACC"),
            new ParameterNameValue("to", "PDB_ID"),
            new ParameterNameValue("format", "tab"),
            new ParameterNameValue("query", accessions),});
        Map<String, LinkedHashSet<String>> pdbMap = new LinkedHashMap<>();
        String[] lines = results.split("\n");
        for (String line : lines) {
            String[] lineArr = line.split("\t");
            if (!pdbMap.containsKey(lineArr[0])) {
                pdbMap.put(lineArr[0], new LinkedHashSet<>());
            }
            pdbMap.get(lineArr[0]).add(lineArr[1]);

        }
        return pdbMap;
    }

    private String process(String tool, ParameterNameValue[] params) {
        String results = null;
        try {
            StringBuilder locationBuilder = new StringBuilder(UNIPROT_SERVER + tool + "/?");
            for (int i = 0; i < params.length; i++) {
                if (i > 0) {
                    locationBuilder.append('&');
                }
                locationBuilder.append(params[i].name).append('=').append(params[i].value);
            }
            String location = locationBuilder.toString();
            URL url;
            url = new URL(location);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            HttpURLConnection.setFollowRedirects(true);
            conn.setDoInput(true);
            conn.connect();

            int status = conn.getResponseCode();
            while (true) {
                int wait = 0;
                String header = conn.getHeaderField("Retry-After");
                if (header != null) {
                    wait = Integer.valueOf(header);
                }
                if (wait == 0) {
                    break;
                }
                conn.disconnect();
                Thread.sleep(wait * 1000);
                conn = (HttpURLConnection) new URL(location).openConnection();
                conn.setDoInput(true);
                conn.connect();
                status = conn.getResponseCode();
            }

            if (status == HttpURLConnection.HTTP_OK) {
                InputStream reader = conn.getInputStream();
                URLConnection.guessContentTypeFromStream(reader);
                StringBuilder builder = new StringBuilder();
                int a = 0;
                while ((a = reader.read()) != -1) {
                    builder.append((char) a);
                }
                results = builder.toString();
            }

            conn.disconnect();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();

        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
        return results;
    }

    private class ParameterNameValue {

        private String name;
        private String value;

        public ParameterNameValue(String name, String value) {
            try {
                this.name = URLEncoder.encode(name, "UTF-8");
                this.value = URLEncoder.encode(value, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        }
    }
}
