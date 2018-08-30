package com.uib.web.peptideshaker.model.core.pdb;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maps UniProt protein accession numbers to PDB file IDs updated to suit the
 * web environment.
 *
 * @author Yehia Farag
 */
public class PdbHandler {

    /**
     * Protein to PDB matches map.
     */
    private final Map<String, Map<String, PDBMatch>> proteinToPDBMap;
    /**
     * EBI web service for PDB data.
     */
    private final EBIRestService EBI_Rest_Service;
    private final Map<String, PDBMatch> pdbMachesMap;

    /**
     *
     */
    public PdbHandler() {
        this.proteinToPDBMap = new LinkedHashMap<>();
        this.EBI_Rest_Service = new EBIRestService();
        this.pdbMachesMap = new LinkedHashMap<>();
    }

    /**
     * Get PDB matches for the selected protein accession
     *
     * @param uniProtAccssion UniProt accession
     * @return Map of PDB matches
     */
    public Map<String, PDBMatch> getData(String uniProtAccssion) {
        final Map<String, PDBMatch> subMap;
        if (!proteinToPDBMap.containsKey(uniProtAccssion)) {
            proteinToPDBMap.putAll(EBI_Rest_Service.getPdbIds(uniProtAccssion, true));
        }
        Map<String, PDBMatch> Pdbs = proteinToPDBMap.get(uniProtAccssion);
        if (Pdbs == null) {
            return null;
        }
        Map<String, PDBMatch> subIds = new LinkedHashMap<>();
        subMap = new LinkedHashMap<>();
        Pdbs.keySet().forEach((id) -> {
            if (!pdbMachesMap.containsKey(id)) {
                subIds.put(id, Pdbs.get(id));
            } else {
                subMap.put(id, pdbMachesMap.get(id));
            }
        });
        if (!subIds.isEmpty()) {
            subMap.putAll(EBI_Rest_Service.getPdbSummary(subIds));
        }
        pdbMachesMap.putAll(subMap);
        return subMap;
    }

    /**
     * Update PDB match information
     *
     * @param pdbMatch PDB match object
     * @param protSequence protein sequence
     * @return updated PDB match object
     */
    public PDBMatch updatePdbInformation(String pdbMatch, String protSequence) {
        if (pdbMachesMap.get(pdbMatch).getChains().isEmpty()) {
            return EBI_Rest_Service.updatePdbInformation(pdbMachesMap.get(pdbMatch), protSequence);
        } else {
            return pdbMachesMap.get(pdbMatch);
        }
    }

}
