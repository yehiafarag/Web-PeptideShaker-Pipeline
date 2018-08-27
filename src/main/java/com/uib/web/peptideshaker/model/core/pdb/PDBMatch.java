package com.uib.web.peptideshaker.model.core.pdb;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * PDB match object to store PDB match information
 *
 * @author Yehia Farag
 */
public class PDBMatch {

    /**
     * PDB name (description).
     */
    private String description;
    /**
     * PDB match id.
     */
    private final String pdbId;
    /**
     * PDB match sequence.
     */
    private String sequence;
    /**
     * List of included chain blocks.
     */
    private final List<ChainBlock> chains;
    /**
     * List of chain block IDs.
     */
    private final Set<String> chainsIds;
    /**
     * Entity ID 'default value is 1'
     */
    private int entity_id = 1;

    /**
     * Constructor to initialise main variables and data structure
     *
     * @param pdbId PDB match id.
     */
    public PDBMatch(String pdbId) {
        this.pdbId = pdbId;
        this.chains = new ArrayList<>();
        this.chainsIds = new LinkedHashSet<>();
    }

    /**
     * Get list of chain block IDs.
     *
     * @return List IDs.
     */
    public Set<String> getChainsIds() {
        return chainsIds;
    }

    /**
     * Get list of included chain blocks.
     *
     * @return list of chain blocks.
     */
    public List<ChainBlock> getChains() {
        return chains;
    }

    /**
     * Set PDB name (description).
     *
     * @param description PDB description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Add included chain block ID
     *
     * @param chainId chain block ID
     */
    public void addChainId(String chainId) {

        this.chainsIds.add(chainId);
    }

    /**
     * Add included chain block
     *
     * @param chain chain block
     */
    public void addChain(ChainBlock chain) {

        this.chains.add(chain);
    }

    /**
     * Get main PDB match sequence
     *
     * @return PDB match sequence
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * Set main PDB match sequence
     *
     * @param sequence PDB match sequence
     */
    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    /**
     * Get PDB match ID
     *
     * @return PDB match ID
     */
    public String getPdbId() {
        return pdbId;
    }

    /**
     * Get PDB match description
     *
     * @return PDB match short description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get PDB match entity ID
     *
     * @return Entity ID 'default value is 1'
     */
    public int getEntity_id() {
        return entity_id;
    }

    /**
     * Set PDB match entity ID
     *
     * @param entity_id Entity ID
     */
    public void setEntity_id(Object entity_id) {
        this.entity_id = (Integer) entity_id;
    }

}
