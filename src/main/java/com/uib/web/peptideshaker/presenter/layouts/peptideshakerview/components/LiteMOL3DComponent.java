package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * This class provides an abstraction layer for LiteMOL 3D protein structure
 *
 * @author Yehia Farag
 */
public class LiteMOL3DComponent extends VerticalLayout {

    private String pdbId;

    public LiteMOL3DComponent() {
        tasks = new ArrayList<>();
        LiteMOL3DComponent.this.setSizeFull();
        LiteMOL3DComponent.this.setStyleName("iframecontainer");
        Label proteinStructurePanel = new Label("<iframe id=\"litemolframe\" src='litemol' style='width:100%; height:100%;border: none;'></iframe>", ContentMode.HTML);
        proteinStructurePanel.setSizeFull();
        LiteMOL3DComponent.this.addComponent(proteinStructurePanel);
    }
    private Future update3dFuture;
    private final List<Runnable> tasks;

    public void excuteQuery(String pdbId, int entity, String chainId, HashMap<String, Integer> proteinColor, HashSet<HashMap> entriesSet) {
        tasks.add(initExcution(pdbId, entity, chainId, proteinColor, entriesSet));
        if (update3dFuture != null) {
            while (!update3dFuture.isDone()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }

            }
        }
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        update3dFuture = executorService.submit(tasks.get(tasks.size() - 1));
        executorService.shutdown();
        tasks.clear();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }

    }

    private Runnable initExcution(String pdbId, int entity, String chainId, HashMap<String, Integer> proteinColor, HashSet<HashMap> entriesSet) {
        return () -> {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> jsonQuery = new HashMap();
            HashMap<String, Object> coloring = new HashMap<>();
            coloring.put("base", proteinColor);
            coloring.put("entries", entriesSet);
            jsonQuery.put("entity", entity);
            jsonQuery.put("pdbId", pdbId);
            jsonQuery.put("chainId", chainId);
            jsonQuery.put("coloring", coloring);
            try {
                String json = mapper.writeValueAsString(jsonQuery);
                JavaScript.getCurrent().execute("document.getElementById('litemolframe').contentWindow.excutequery('" + json + "'," + (!pdbId.equalsIgnoreCase(LiteMOL3DComponent.this.pdbId)) + ");");
                LiteMOL3DComponent.this.pdbId = pdbId;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        };

    }

    @Override
    public void setVisible(boolean visible) {
        if (!visible) {
            this.reset3DView();
        }

    }

    public void reset3DView() {
        pdbId = null;
        JavaScript.getCurrent().execute("document.getElementById('litemolframe').contentWindow.reset();");
    }

}
