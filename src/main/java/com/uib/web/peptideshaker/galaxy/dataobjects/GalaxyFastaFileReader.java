package com.uib.web.peptideshaker.galaxy.dataobjects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * This class represents FASTA file reader which allow partial reader from
 * galaxy server instead of full download for the files
 *
 * @author Yehia Farag
 */
public class GalaxyFastaFileReader {

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
                try (BufferedReader bin = new BufferedReader(new InputStreamReader(in))) {
                    String fastaHeader = bin.readLine();
                    String sequence = "";
                    String line;
                    while ((line = bin.readLine()) != null) {
                        sequence += line;
                    }
                    protein.setSequence(sequence);
                    protein.setProteinEvidence(proteinEvedence[Integer.parseInt(fastaHeader.split("PE=")[1].split(" ")[0])]);
                }
                

            } catch (IOException ex) {
                ex.printStackTrace();

            }
        }
        return protein;

    }
}
