/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.model.core.pdb;

/**
 *
 * @author y-mok
 */
public class ChainBlock {
private final String struct_asym_id;
private final String chain_id;
private final String chain_sequence;

    public String getChain_sequence() {
        return chain_sequence;
    }
private final int start_author_residue_number;
private final int start_residue_number;
private final int end_author_residue_number;
private final int end_residue_number;

    public String getStruct_asym_id() {
        return struct_asym_id;
    }

    public int getStart_author_residue_number() {
        return start_author_residue_number;
    }

    public int getStart_residue_number() {
        return start_residue_number;
    }

    public int getEnd_author_residue_number() {
        return end_author_residue_number;
    }

    public int getEnd_residue_number() {
        return end_residue_number;
    }

    public String getChain_id() {
        return chain_id;
    }

    public ChainBlock(String struct_asym_id, String chain_id, int start_author_residue_number, int start_residue_number, int end_author_residue_number, int end_residue_number,String chain_sequence) {
        this.struct_asym_id = struct_asym_id;
        this.chain_id = chain_id;
        this.start_author_residue_number = start_author_residue_number;
        this.start_residue_number = start_residue_number;
        this.end_author_residue_number = end_author_residue_number;
        this.end_residue_number = end_residue_number;
        this.chain_sequence=chain_sequence;
    }


    
}
