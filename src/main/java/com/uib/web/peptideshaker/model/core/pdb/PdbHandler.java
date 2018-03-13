package com.uib.web.peptideshaker.model.core.pdb;

import com.uib.web.peptideshaker.model.core.pdb.PdbEbiRestService;
import com.uib.web.peptideshaker.model.core.pdb.PdbMatch;
import com.uib.web.peptideshaker.model.core.pdb.UniprotToPdb;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import org.codehaus.jettison.json.JSONObject;

/**
 * Maps UniProt protein accession numbers to PDB file IDs. updated to suit the
 * web environment.
 *
 * @author Yehia Farag
 */
public class PdbHandler {

    private boolean valid = false;
    private boolean readyFile;
    private final UniprotToPdb PdbAccMapper;
    private final  Map<String,  Map<String, PdbMatch>> pdbMap;
    private final PdbEbiRestService pdbEbiRestService;
    private final Map<String, PdbMatch> pdbMachsMap;

    public PdbHandler() {
        this.pdbMap = new LinkedHashMap<>();
        this.PdbAccMapper = new UniprotToPdb();
        this.pdbEbiRestService = new PdbEbiRestService();
        this.pdbMachsMap = new LinkedHashMap<>();
    }

    public boolean isValid() {
        return valid;
    }

    public final Callable<String> updatePdbMap(Set<String> uniprotAccessions) {
        readyFile = false;
        try {

            Callable<String> task = () -> {
                try {
                    pdbMap.putAll(pdbEbiRestService.getPdbIds(uniprotAccessions));
                    readyFile = true;
                } catch (Exception e) {
                    valid = false;
                    e.printStackTrace();
//                    return "";
                }
                return "";
            };
            return task;
        } catch (Exception ex) {
            valid = false;
            ex.printStackTrace();
        }
        return null;

    }

    public Map<String, PdbMatch> getData(String uniProtAccssion) {
        final Map<String, PdbMatch> subMap;
        if (!pdbMap.containsKey(uniProtAccssion)) {
            pdbMap.putAll(pdbEbiRestService.getPdbIds(uniProtAccssion,true));
        }
        Map<String,PdbMatch> Pdbs = pdbMap.get(uniProtAccssion);
        if (Pdbs == null) {
            return null;
        }
        Map<String,PdbMatch>  subIds = new LinkedHashMap<>();
        subMap = new LinkedHashMap<>();
        Pdbs.keySet().forEach((id) -> {
            if (!pdbMachsMap.containsKey(id)) {
                subIds.put(id,Pdbs.get(id));
            } else {
                subMap.put(id, pdbMachsMap.get(id));
            }
        });
        if (!subIds.isEmpty()) {
            subMap.putAll(pdbEbiRestService.getPdbSummary(subIds));
        }
        pdbMachsMap.putAll(subMap);
        return subMap;
    }

    public PdbMatch updatePdbInformation(String pdbMatch,String protSequence) {
        if (pdbMachsMap.get(pdbMatch).getChains().isEmpty()) {
            return pdbEbiRestService.updatePdbInformation(pdbMachsMap.get(pdbMatch),protSequence);
        }else
            return pdbMachsMap.get(pdbMatch);
    }

    public boolean isReadyFile() {
        return readyFile;
    }

}
