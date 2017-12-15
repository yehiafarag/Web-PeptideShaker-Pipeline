package com.uib.web.peptideshaker.model.core;

import com.compomics.util.pdbfinder.das.readers.AlignmentBlock;
import com.compomics.util.pdbfinder.das.readers.DasAlignment;
import com.compomics.util.pdbfinder.das.readers.DasAnnotationServerAlingmentReader;
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
import java.util.Vector;

/**
 * Maps UniProt protein accession numbers to PDB file IDs. updated to suit the
 * web environment.
 *
 *
 * @author Niklaas Colaert
 * @author Yehia Farag
 */
public class WebFindPdbForUniprotAccessions {

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
    private Vector<PdbParameter> iPdbs = new Vector<>();
    /**
     * The DAS reader.
     */
    private DasAnnotationServerAlingmentReader iDasReader = new DasAnnotationServerAlingmentReader("empty");
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

    /**
     * Constructor.
     *
     * @param aProteinAccession the protein accession
     */
    public WebFindPdbForUniprotAccessions(String aProteinAccession) {

        this.iProteinAccession = aProteinAccession;

        try {
            // find features
            String urlMake = "http://www.rcsb.org/pdb/rest/das/pdb_uniprot_mapping/alignment?query=" + iProteinAccession;
            readUrl(urlMake);
        } catch (Exception e) {
            e.printStackTrace();
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
        } catch (StringIndexOutOfBoundsException e) {
            System.out.println("Error in reading das pdb alignment");
            e.printStackTrace();
        }
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
    public Vector<PdbParameter> getPdbs() {
        return iPdbs;
    }

    /**
     * Tries to read the PDB URL.
     *
     * @param aUrl the PDB URL to read
     */
    private void readUrl(String aUrl) {

        urlRead = false;
        this.iUrl = aUrl;

        try {
            URL myURL = new URL(iUrl);
            StringBuilder input = new StringBuilder();
            HttpURLConnection connection = (HttpURLConnection) myURL.openConnection();
            BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
            Reader r = new InputStreamReader(in);

            int i;
            while ((i = r.read()) != -1) {
                input.append((char) i);
            }

            r.close();
            in.close();

            iDasReader = new DasAnnotationServerAlingmentReader(input.toString());
            urlRead = true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ConnectException e) {
            System.out.println("Connect exception for url " + iUrl);
            if (isFirstTry) {
                readUrl(iUrl);
            }
            isFirstTry = false;
        } catch (IOException e) {
            System.out.println("I/O exception for url " + iUrl);
        }
    }

}
