/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.model.core;

import java.util.Set;

/**
 *
 * @author yfa041
 */
public class FilteredProteins {
    private Set<Comparable>withoutTopFilterList;

    public Set<Comparable> getWithoutTopFilterList() {
        return withoutTopFilterList;
    }

    public void setWithoutTopFilterList(Set<Comparable> withoutTopFilterList) {
        this.withoutTopFilterList = withoutTopFilterList;
    }

    public Set<Comparable> getWithTopFilterList() {
        return withTopFilterList;
    }

    public void setWithTopFilterList(Set<Comparable> withTopFilterList) {
        this.withTopFilterList = withTopFilterList;
    }
    private Set<Comparable> withTopFilterList;
    
}
