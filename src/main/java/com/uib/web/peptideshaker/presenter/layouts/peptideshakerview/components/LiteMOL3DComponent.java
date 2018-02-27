package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * This class provides an abstraction layer for LiteMOL 3D protein structure
 *
 * @author Yehia Farag
 */
public class LiteMOL3DComponent extends VerticalLayout {

    public LiteMOL3DComponent() {
        LiteMOL3DComponent.this.setSizeFull();
        LiteMOL3DComponent.this.setStyleName("iframecontainer");
        Label proteinStructurePanel = new Label("<iframe id=\"litemolframe\" src='litemol' style='width:100%; height:100%;border: none;'></iframe>", ContentMode.HTML);
        proteinStructurePanel.setSizeFull();
        LiteMOL3DComponent.this.addComponent(proteinStructurePanel);

    }

//    public void loadProtein(String pdbAccession) {
//        JavaScript.getCurrent().execute("document.getElementById('litemolframe').contentWindow.loadNewProtein('"+pdbAccession+"');");
//    }
    private String pdbId;

    public void excuteQuery(String pdbId, String chainId, HashMap<String, Integer> proteinColor, HashSet<HashMap> entriesSet) {

        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> jsonQuery = new HashMap();
        HashMap<String, Object> coloring = new HashMap<>();
        coloring.put("base", proteinColor);
        coloring.put("entries", entriesSet);
        jsonQuery.put("pdbId", pdbId);
        jsonQuery.put("chainId", chainId);
        jsonQuery.put("coloring", coloring);
        try {
            String json = mapper.writeValueAsString(jsonQuery);
            JavaScript.getCurrent().execute("document.getElementById('litemolframe').contentWindow.excutequery('" + json + "'," + (!pdbId.equalsIgnoreCase(this.pdbId)) + ");");
            this.pdbId = pdbId;
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void setVisible(boolean visible) {
        if (!visible) {
            this.reset3DView();
        }

    }

    public void reset3DView() {
        JavaScript.getCurrent().execute("document.getElementById('litemolframe').contentWindow.reset();");
    }

}
