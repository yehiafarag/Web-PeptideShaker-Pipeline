package com.uib.web.peptideshaker.model.core.pdb;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }
    /**
     * List of included chain blocks.
     */
    private final Map<String, ChainBlock> chains;
    /**
     * List of chain block IDs.
     */
    int minStartAuth = Integer.MAX_VALUE;
    int maxEndAuth = Integer.MIN_VALUE;
//    private final Set<String> chainsIds;
    /**
     * Entity ID 'default value is 1'
     */
    private int entity_id = -1;
    private List<EntityData> entities;

    public List<EntityData> getEntities() {
        return entities;
    }

    public void setEntities(List<EntityData> entities) {
        this.entities = entities;
    }

    /**
     * Constructor to initialise main variables and data structure
     *
     * @param pdbId PDB match id.
     */
    public PDBMatch(String pdbId) {
        this.pdbId = pdbId;
        this.chains = new LinkedHashMap<>();
//        this.chainsIds = new LinkedHashSet<>();
    }

    /**
     * Get list of chain block IDs.
     *
     * @return List IDs.
     */
//    public Set<String> getChainsIds() {
//        return chainsIds;
//    }
    /**
     * Get list of included chain blocks.
     *
     * @return list of chain blocks.
     */
    public Collection<ChainBlock> getChains() {
        return chains.values();
    }

    /**
     * Set PDB name (description).
     *
     * @param description PDB description
     */
    public void setDescription(String description) {
        this.description = description;
    }

//    /**
//     * Add included chain block ID
//     *
//     * @param chainId chain block ID
//     */
//    public void addChainId(String chainId) {
//
//        this.chainsIds.add(chainId);
//    }
    /**
     * Add included chain block
     *
     * @param chain chain block
     */
    public void addChain(ChainBlock chain) {

        this.chains.put(chain.getChain_id(), chain);
        if (chain.getStart_author_residue_number() < minStartAuth) {
            minStartAuth = chain.getStart_author_residue_number();
        }
        if (chain.getEnd_author_residue_number() > maxEndAuth) {
            maxEndAuth = chain.getEnd_author_residue_number();
        }
        if (minStartAuth < 0) {
            minStartAuth = 0;
        }
        if (entity_id == -1) {
            entity_id = chain.getEntityId();
        } else if (entity_id != chain.getEntityId()) {
            entity_id = -2;
        }
    }

    /**
     * Get main PDB match sequence
     *
     * @param chainId selected chain id
     * @return PDB match sequence
     */
    public String getSequence(String chainId) {
        System.out.println("at chain id " + chainId);
        switch (chainId) {
            case "All":
                return sequence.substring(minStartAuth, maxEndAuth);
            default:
                return chains.get(chainId).getChain_sequence();
        }
    }
//
//    /**
//     * Set main PDB match sequence
//     *
//     * @param sequence PDB match sequence
//     */
//    public void setSequence(String sequence) {
//        this.sequence = sequence;
//    }

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
     * @param chainId selected chain ID
     * @return Entity ID 'default value is 1'
     */
    public int getEntity_id(String chainId) {
        System.out.println("at chain id " + chainId);
        switch (chainId) {
            case "All":
                return entity_id;
            default:
                return chains.get(chainId).getEntityId();
        }
    }
}
