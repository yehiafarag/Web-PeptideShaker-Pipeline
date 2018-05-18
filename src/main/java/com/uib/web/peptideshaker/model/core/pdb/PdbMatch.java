/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.model.core.pdb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Yehia Farag
 */
public class PdbMatch {

    private String title;
    private final String pdbId;
    private String sequence;
    private final List<ChainBlock> chains;
    private final Set<String> chainsIds;
    private int entity_id = 1;

    public Set<String> getChainsIds() {
        return chainsIds;
    }

    public List<ChainBlock> getChains() {
        return chains;
    }
    private Map<String, Object> jsonData;

    public void setJsonData(Map<String, Object> jsonData) {
        this.jsonData = jsonData;
        this.title = (String) jsonData.get("title") + "";
    }

    public PdbMatch(String id) {
        this.pdbId = id;
        this.chains = new ArrayList<>();
        this.chainsIds = new LinkedHashSet<>();
    }

    public void addChainId(String chainId) {

        this.chainsIds.add(chainId);
    }

    public void addChain(ChainBlock chain) {

        this.chains.add(chain);
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getPdbId() {
        return pdbId;
    }

    public String getTitle() {
        return title;
    }

    public int getEntity_id() {
        return entity_id;
    }

    public void setEntity_id(Object entity_id) {
        this.entity_id = (Integer) entity_id;
    }


}
