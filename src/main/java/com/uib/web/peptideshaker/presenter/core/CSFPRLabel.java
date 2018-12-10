
package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Link;

/**
 * This class represents link to CSF-PR
 *
 * @author Yehia Farag
 */
public class CSFPRLabel extends Link implements Comparable<CSFPRLabel>{

    private final Boolean  available;
    public CSFPRLabel(String accession, boolean available) {
        this.available=available;
        if (available) {
            CSFPRLabel.this.setIcon(new ThemeResource("img/csf_logo.png"));
            CSFPRLabel.this.setResource(new ExternalResource("http://129.177.231.63/csf-pr/searchby:Protein*Accession___searchkey:"+accession+"__"));
              CSFPRLabel.this.setDescription("View in CSF-PR");
        } else {
            CSFPRLabel.this.setIcon(new ThemeResource("img/csf_logo_disable.png"));
              CSFPRLabel.this.setDescription("Protein is not available on CSF-PR");
        }
        CSFPRLabel.this.setStyleName("imgonly");
        CSFPRLabel.this.setEnabled(available);
      
        CSFPRLabel.this.setTargetName("_blank");

    }

    @Override
    public int compareTo(CSFPRLabel t) {
        return this.available.compareTo(t.available);
    }

}