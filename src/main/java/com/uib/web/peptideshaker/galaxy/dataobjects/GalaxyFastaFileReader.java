package com.uib.web.peptideshaker.galaxy.dataobjects;

import com.compomics.util.experiment.identification.protein_sequences.FastaIndex;
import com.compomics.util.io.SerializationUtils;
import com.compomics.util.protein.Header;
import com.uib.web.peptideshaker.model.core.SequenceFactory;
import com.vaadin.server.VaadinSession;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * This class represents FASTA file reader which allow partial reader from
 * galaxy server instead of full download for the files
 *
 * @author Yehia Farag
 */
public class GalaxyFastaFileReader {

//    private final String name;
//    private final String fastaFileId;
//    private final String indexedFastafile;
//
//    private FastaIndex fastaFileIndexer;
//
//    /**
//     * Key word for splitting data chunk or offset.
//     */
//    private String chunkedData;
//
//    public String getIndexedFastafile() {
//        return indexedFastafile;
//    }
//    /**
//     * Re-index the location based on the server type.
//     */
//    private Long reIndexFactor;
//    /**
//     * The main cookies (optional) as string.
//     */
//    private final String cookiesRequestProperty;
//    /**
//     * The file path in the web (URL).
//     */
//    private String fileURL;
//
//    private final GalaxyFile fastaFileIndexFile;
    /**
     * Constructor to initialize the reader
     *
     * @param name FASTA file name
     * @param fastaFileId FASTA file id on galaxy
     * @param indexedFastaFile indexed FASTA file id on galaxy
     */
    public GalaxyFastaFileReader(String name, String fastaFileId, String indexedFastaFile, GalaxyFile fastaFileIndexFile) {

//        this.name = name;
//        this.fastaFileId = fastaFileId;
//        this.indexedFastafile = indexedFastaFile;
//        String userAPIKey = VaadinSession.getCurrent().getAttribute("ApiKey") + "";
//        this.cookiesRequestProperty = VaadinSession.getCurrent().getAttribute("cookies") + "";
//        String galaxyURL = VaadinSession.getCurrent().getAttribute("galaxyUrl") + "";
//        String url = galaxyURL + "/dataset/display?dataset_id=" + indexedFastaFile + "&chunk=1";
//        this.fastaFileIndexFile = fastaFileIndexFile;
//
//        URLConnection conn = initializeConnection(url);
//        if (conn == null) {
//            return;
//        }
//        if (conn.getHeaderField("Transfer-Encoding") != null && conn.getHeaderField("Transfer-Encoding").equalsIgnoreCase("chunked")) {
//            try {
//                chunkedData = "&chunk=";
//                InputStream in = conn.getInputStream();
//                reIndexFactor = in.skip(Long.MAX_VALUE);
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        } else {
//            chunkedData = "&offset=";
//            reIndexFactor = 1l;
//        }
//        fileURL = galaxyURL + "/dataset/display?dataset_id=" + indexedFastaFile + chunkedData;
//        System.out.println("at get data test " + getDataAsLines(1000, ">"));
    }

    public GalaxyFastaFileReader() {

    }

    private final String[] proteinEvedence = new String[]{"Not Available", "Protein", "Transcript", "Homology", "Predicted", "Uncertain"};

    public ProteinObject updateProteinInformation(ProteinObject protein, String accession) {
        if (protein == null) {
            protein = new ProteinObject();
            protein.setAccession(accession);
            System.out.println("itr was null protein :( ");
        }
        if (protein.getSequence() == null) {
            try {
                URL website = new URL("http://www.uniprot.org/uniprot/" + protein.getAccession() + ".fasta");
                URLConnection conn = website.openConnection();
                InputStream in = conn.getInputStream();
                BufferedReader bin = new BufferedReader(new InputStreamReader(in));
                String fastaHeader = bin.readLine();
                String sequence = "";
                String line;
                while ((line = bin.readLine()) != null) {
                    sequence += line;
                }
                protein.setSequence(sequence);
                protein.setProteinEvidence(proteinEvedence[Integer.parseInt(fastaHeader.split("PE=")[1].split(" ")[0])]);
                bin.close();
                

            } catch (IOException ex) {
                ex.printStackTrace();

            }
        }
        return protein;

    }

//    private URLConnection initializeConnection(String url) {
//        try {
//            URL website = new URL(url);
//            URLConnection conn = website.openConnection();
//            conn.addRequestProperty("Cookie", cookiesRequestProperty);
//            conn.addRequestProperty("Accept", "*/*");
//            conn.addRequestProperty("Accept-Encoding", "gzip, deflate, sdch, br");
//            conn.addRequestProperty("Accept-Language", "ar,en-US;q=0.8,en;q=0.6,en-GB;q=0.4");
//            conn.addRequestProperty("Cache-Control", "no-cache");
//            conn.addRequestProperty("Connection", "keep-alive");
//            conn.addRequestProperty("DNT", "1");
//            conn.addRequestProperty("X-Requested-With", "XMLHttpRequest");
//            conn.addRequestProperty("Pragma", "no-cache");
//            conn.setDoInput(true);
//            return conn;
//        } catch (MalformedURLException ex) {
//            ex.printStackTrace();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//
//        return null;
//    }
//    public String getName() {
//        return name;
//
//    }
//    /**
//     * Get Data from start index until the end index
//     *
//     * @param startRange start index
//     * @param endRange last index
//     * @return Set<String> that can be used for initializing different tables
//     */
//    public Set<String> getDataAsLines(long startRange, String endRangeKeyword) {
//        Set<String> dataLines = new LinkedHashSet<>();
//
//        if (fastaFileIndexer == null) {
//            fastaFileIndexer = initFastaIndexer();
//        }
//
//        SequenceFactory sequenceFactory = new SequenceFactory();
//        sequenceFactory.setFastaIndex(fastaFileIndexer);
//
////        String proteinEvidenceLevel = sequenceFactory.getHeader(("O75947")).getProteinEvidence();
////        System.out.println("at get data from fasta " + proteinEvidenceLevel);
//        int i = 0;
//        ArrayList<String> ind = new ArrayList<>(fastaFileIndexer.getIndexes().keySet());
//        startRange = ind.indexOf("O75947") * 2;
//        //if offset
//        if (chunkedData.equalsIgnoreCase("&offset=")) {
//            try {
//                String jsonStr;
//                final long startTime = System.nanoTime();
//                URL website = new URL("http://www.uniprot.org/uniprot/O75947.fasta");
//                URLConnection conn = website.openConnection();
////                URLConnection conn = (URLConnection) initializeConnection("http://www.uniprot.org/uniprot/O75947.fasta");//fileURL + startRange);
//                InputStream in = conn.getInputStream();
//                BufferedReader bin = new BufferedReader(new InputStreamReader(in));
//                String line = "";//bin.readLine().split(endRangeKeyword)[0];
//                while ((line = bin.readLine()) != null) {
//                    System.out.println("-*****-: "+line);
//                }
//
//                jsonStr = line + "\"}";
////                System.out.println("at json string " + startRange);
//                System.out.println("at json string " + jsonStr);
////                Header result = Header.parseFromFASTA(jsonStr);
////                JSONObject jsonObject = new JSONObject(jsonStr);
////                System.out.println("at -- " + jsonObject.getString("ck_data"));
////                System.out.println("at --header reader-- " + result.getAccession());
//
//                final long endTime = System.nanoTime();
////                dataLines.addAll(Arrays.asList(jsonObject.getString("ck_data").split("\\+")[1].replaceFirst("\\n", "").split("\n")));
//                System.out.println("method 1 : " + (endTime - startTime));
//
//            } catch (IOException ex) {
//                ex.printStackTrace();
//
//            }
////            catch (JSONException ex) {
////                ex.printStackTrace();
////            }
//
//        } else {
//            System.out.println("is a chunked data");
//        }
//        //if chunked
//
//        return dataLines;
//
//    }
    /**
     * Returns the desired header for the protein in the FASTA file.
     *
     * @param accession accession of the desired protein
     * @return the corresponding header
     * @throws IOException exception thrown whenever an error occurred while
     * reading the FASTA file
     * @throws IllegalArgumentException exception thrown whenever a protein is
     * not found
     * @throws InterruptedException if an InterruptedException occurs
     */
//    private Header getHeader(String accession, boolean reindex) throws IOException, IllegalArgumentException, InterruptedException, FileNotFoundException, ClassNotFoundException {
//
//        Header result = currentHeaderMap.get(accession);
//
//        if (result == null) {
//
//            Long index = fastaIndex.getIndex(accession);
//
//            if (index == null) {
//                if (reindex) {
//                    fastaIndex = getFastaIndex(true, null);
//                    result = getHeader(accession, false);
//                }
//                throw new IllegalArgumentException("Protein not found: " + accession + ".");
//            }
//
//            result = getHeader(index, 0);
//
//            currentHeaderMap.put(accession, result);
//        }
//
//        return result;
//    }
//    public String getFastaFileId() {
//        return fastaFileId;
//    }
//
//    /**
//     * Deserializes the index of an FASTA file.
//     *
//     * @return the corresponding mgf index object
//     * @throws FileNotFoundException exception thrown whenever the file was not
//     * found
//     * @throws IOException exception thrown whenever an error was encountered
//     * while reading the file
//     * @throws ClassNotFoundException exception thrown whenever an error
//     * occurred while deserializing the object
//     */
//    private FastaIndex initFastaIndexer() {
//        try {
//            System.out.println("at fata is exist " + fastaFileIndexFile.getFile().getAbsolutePath());
//            return (FastaIndex) SerializationUtils.readObject(fastaFileIndexFile.getFile());
//        } catch (IOException | ClassNotFoundException ex) {
//            ex.printStackTrace();
//        }
//        return null;
//
//    }
}
