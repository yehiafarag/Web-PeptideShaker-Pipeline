package archive;

import com.compomics.util.pdbfinder.das.readers.AlignmentBlock;
import com.compomics.util.pdbfinder.das.readers.DasAlignment;
import com.compomics.util.pdbfinder.pdb.PdbBlock;
import com.compomics.util.pdbfinder.pdb.PdbParameter;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Maps UniProt protein accession numbers to PDB file IDs. updated to suit the
 * web environment.
 *
 *
 * @author Niklaas Colaert
 * @author Yehia Farag
 */
public class WebFindPdbForUniprotAccessions1 {

    /**
     * The protein accession number.
     */
    private String iProteinAccession;
    /**
     * The DAS alignment.
     */
    private DasAlignment[] iAlignments;
    /**
     * The PDB parameters.
     */
    private List<PdbParameter> iPdbs = new ArrayList<>();
    /**
     * The DAS reader.
     */
    private WebDasAnnotationServerAlingmentReader iDasReader;
    /**
     * The URL.
     */
    private String iUrl;
    /**
     * True if this is the first try.
     */
    private boolean isFirstTry = true;
    /**
     * Set to true of the PDB URL could be read, false otherwise.
     */
    private boolean urlRead = false;

    private boolean valid = false;

    public boolean isValid() {
        return valid;
    }

    /**
     * Constructor.
     *
     * @param aProteinAccession the protein accession
     */
    public WebFindPdbForUniprotAccessions1(String aProteinAccession) {
        this.iProteinAccession = aProteinAccession;
    }
    int index = 1;
    private boolean readyFile;

    public final Callable<String> reProcessInformation() {
        if (valid) {
            return null;
        }
         readyFile=false;
        try {
//            Thread t = new Thread(() -> {
//                try {            // find features iProteinAccession
//                    String urlMake = "http://www.rcsb.org/pdb/rest/das/pdb_uniprot_mapping/alignment?query=" + iProteinAccession + ";";
//                    iDasReader = readUrl(urlMake);
//
//                } catch (Exception e) {
//                    valid = false;
//                    e.printStackTrace();
//                    return;
//                    // ignore
//                }
//                iAlignments = iDasReader.getAllAlignments();
//                try {
//                    for (DasAlignment align : iAlignments) {
//                        String pdb = align.getPdbAccession().substring(0, 4);
//                        pdb = pdb.toUpperCase();
//
//                        boolean newPdb = true;
//                        PdbParameter pdbParamToAddBlock = null;
//                        for (int v = 0; v < iPdbs.size(); v++) {
//                            PdbParameter pdbParam = iPdbs.get(v);
//                            if (pdb.equalsIgnoreCase(pdbParam.getPdbaccession())) {
//                                newPdb = false;
//                                v = iPdbs.size();
//                                pdbParamToAddBlock = pdbParam;
//                            }
//                        }
//
//                        if (newPdb || pdbParamToAddBlock == null) {
//                            pdbParamToAddBlock = new PdbParameter(pdb, align.getTitle(), align.getExperimentType(), align.getResolution());
//                            for (AlignmentBlock alignBlock : align.getAlignmentBlocks()) {
//                                PdbBlock block = new PdbBlock(alignBlock.getPdbAccession().substring(5), alignBlock.getSpStart(), alignBlock.getSpEnd(), alignBlock.getPdbStart(), alignBlock.getPdbEnd());
//                                pdbParamToAddBlock.addBlock(block);
//                            }
//                            iPdbs.add(pdbParamToAddBlock);
//                        } else {
//                            for (AlignmentBlock alignBlock : align.getAlignmentBlocks()) {
//                                PdbBlock block = new PdbBlock(alignBlock.getPdbAccession().substring(5), alignBlock.getSpStart(), alignBlock.getSpEnd(), alignBlock.getPdbStart(), alignBlock.getPdbEnd());
//                                pdbParamToAddBlock.addBlock(block);
//                            }
//                        }
//                    }
////            if (!iPdbs.isEmpty()) {
////                for (PdbParameter param : iPdbs) {
////                    for (PdbBlock block : param.getBlocks()) {
////                        System.out.print(block.getBlock() + " ");
////                    }
////                    System.out.println("} ");
////                }
////
////            } else {
////                System.out.println("at pdbid " + iProteinAccession + "-- has empty view ");
////            }
//
//                } catch (StringIndexOutOfBoundsException e) {
//                    System.out.println("Error in reading das pdb alignment");
//                    valid = false;
//                    e.printStackTrace();
//                    return;
//                }
//                valid = true;
//            });
//            t.start();
//            t.join();

            Callable<String> task = () -> {
                System.out.println("the function is called to work " + iProteinAccession + "   " + index++);
                try {            // find features iProteinAccession
                    String urlMake = "http://www.rcsb.org/pdb/rest/das/pdb_uniprot_mapping/alignment?query=" + iProteinAccession + ";";
                    iDasReader = readUrl(urlMake);

                } catch (Exception e) {
                    valid = false;
                    e.printStackTrace();
                    return "";
                    // ignore
                }
                iAlignments = iDasReader.getAllAlignments();
                try {
                    for (DasAlignment align : iAlignments) {
                        String pdb = align.getPdbAccession().substring(0, 4);
                        pdb = pdb.toUpperCase();

                        boolean newPdb = true;
                        PdbParameter pdbParamToAddBlock = null;
                        for (int v = 0; v < iPdbs.size(); v++) {
                            PdbParameter pdbParam = iPdbs.get(v);
                            if (pdb.equalsIgnoreCase(pdbParam.getPdbaccession())) {
                                newPdb = false;
                                v = iPdbs.size();
                                pdbParamToAddBlock = pdbParam;
                            }
                        }

                        if (newPdb || pdbParamToAddBlock == null) {
                            pdbParamToAddBlock = new PdbParameter(pdb, align.getTitle(), align.getExperimentType(), align.getResolution());
                            for (AlignmentBlock alignBlock : align.getAlignmentBlocks()) {
                                PdbBlock block = new PdbBlock(alignBlock.getPdbAccession().substring(5), alignBlock.getSpStart(), alignBlock.getSpEnd(), alignBlock.getPdbStart(), alignBlock.getPdbEnd());
                                pdbParamToAddBlock.addBlock(block);
                            }
                            iPdbs.add(pdbParamToAddBlock);
                        } else {
                            for (AlignmentBlock alignBlock : align.getAlignmentBlocks()) {
                                PdbBlock block = new PdbBlock(alignBlock.getPdbAccession().substring(5), alignBlock.getSpStart(), alignBlock.getSpEnd(), alignBlock.getPdbStart(), alignBlock.getPdbEnd());
                                pdbParamToAddBlock.addBlock(block);
                            }
                        }
                    }
//            if (!iPdbs.isEmpty()) {
//                for (PdbParameter param : iPdbs) {
//                    for (PdbBlock block : param.getBlocks()) {
//                        System.out.print(block.getBlock() + " ");
//                    }
//                    System.out.println("} ");
//                }
//
//            } else {
//                System.out.println("at pdbid " + iProteinAccession + "-- has empty view ");
//            }

                } catch (StringIndexOutOfBoundsException e) {
                    System.out.println("Error in reading das pdb alignment");
                    valid = false;
                    e.printStackTrace();
                    return "";
                }
                valid = true;
                readyFile=true;
                return "done";
            };

//            executor.awaitTermination(10, TimeUnit.SECONDS);
//            executor.execute(() -> {
//                try {            // find features iProteinAccession
//                    System.out.println("runner excuted");
//                    String urlMake = "http://www.rcsb.org/pdb/rest/das/pdb_uniprot_mapping/alignment?query=" + iProteinAccession + ";";
//                    iDasReader = readUrl(urlMake);
//
//                } catch (Exception e) {
//                    valid = false;
//                    e.printStackTrace();
//                    return;
//                    // ignore
//                }
//                iAlignments = iDasReader.getAllAlignments();
//                try {
//                    for (DasAlignment align : iAlignments) {
//                        String pdb = align.getPdbAccession().substring(0, 4);
//                        pdb = pdb.toUpperCase();
//
//                        boolean newPdb = true;
//                        PdbParameter pdbParamToAddBlock = null;
//                        for (int v = 0; v < iPdbs.size(); v++) {
//                            PdbParameter pdbParam = iPdbs.get(v);
//                            if (pdb.equalsIgnoreCase(pdbParam.getPdbaccession())) {
//                                newPdb = false;
//                                v = iPdbs.size();
//                                pdbParamToAddBlock = pdbParam;
//                            }
//                        }
//
//                        if (newPdb || pdbParamToAddBlock == null) {
//                            pdbParamToAddBlock = new PdbParameter(pdb, align.getTitle(), align.getExperimentType(), align.getResolution());
//                            for (AlignmentBlock alignBlock : align.getAlignmentBlocks()) {
//                                PdbBlock block = new PdbBlock(alignBlock.getPdbAccession().substring(5), alignBlock.getSpStart(), alignBlock.getSpEnd(), alignBlock.getPdbStart(), alignBlock.getPdbEnd());
//                                pdbParamToAddBlock.addBlock(block);
//                            }
//                            iPdbs.add(pdbParamToAddBlock);
//                        } else {
//                            for (AlignmentBlock alignBlock : align.getAlignmentBlocks()) {
//                                PdbBlock block = new PdbBlock(alignBlock.getPdbAccession().substring(5), alignBlock.getSpStart(), alignBlock.getSpEnd(), alignBlock.getPdbStart(), alignBlock.getPdbEnd());
//                                pdbParamToAddBlock.addBlock(block);
//                            }
//                        }
//                    }
//                    executor.shutdown();
//
//                } catch (StringIndexOutOfBoundsException e) {
//                    System.out.println("Error in reading das pdb alignment");
//                    valid = false;
//                    e.printStackTrace();
//                    executor.shutdown();
//                    return;
//                }
//                valid = true;
//            });
            return task;
        } catch (Exception ex) {
            valid = false;
            ex.printStackTrace();
        }
        return null;

    }

    public boolean isReadyFile() {
        return readyFile;
    }

    /**
     * Returns true if the PDB URL was read, false otherwise.
     *
     * @return true if the PDB URL was read, false otherwise
     */
    public boolean urlWasRead() {
        return urlRead;
    }

    /**
     * Returns a vector of the PDB files mapped to the given protein accession
     * number.
     *
     * @return a vector of the PDB files
     */
    public List<PdbParameter> getPdbs() {
        return iPdbs;
    }

    /**
     * Tries to read the PDB URL.
     *
     * @param aUrl the PDB URL to read
     */
    private WebDasAnnotationServerAlingmentReader readUrl(String aUrl) {

        urlRead = false;
        this.iUrl = aUrl;
        WebDasAnnotationServerAlingmentReader tempIDasReader;
        HttpURLConnection connection = null;
        try {

            URL myURL = new URL(iUrl);
            StringBuilder input = new StringBuilder();
            connection = (HttpURLConnection) myURL.openConnection();
            try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream())) {
                if (in.available() > 500) {
                    try (Reader r = new InputStreamReader(in)) {
                        int i;
                        while ((i = r.read()) != -1) {
                            input.append((char) i);
                        }
                        r.close();
                    }
                } else {
                    valid = true;
                }
                in.close();

            }
            connection.disconnect();
            tempIDasReader = new WebDasAnnotationServerAlingmentReader(input.toString());

            urlRead = true;
            return tempIDasReader;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ConnectException e) {
            System.out.println("Connect exception for url " + iUrl);
//            if (isFirstTry) {
//                tempIDasReader = readUrl(iUrl);
//                isFirstTry = false;
//                return tempIDasReader;
//            } else {
//                isFirstTry = false;
//                return new DasAnnotationServerAlingmentReader("empty");
//            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("I/O exception for url " + iUrl);
//             if (isFirstTry) {
//                tempIDasReader = readUrl(iUrl);
//                isFirstTry = false;
//                return tempIDasReader;
//            } else {
//                isFirstTry = false;
//                return new DasAnnotationServerAlingmentReader("empty");
//            }
        }
        if (connection != null) {
            connection.disconnect();
        }
        valid = false;
        return new WebDasAnnotationServerAlingmentReader("empty");

    }

}
