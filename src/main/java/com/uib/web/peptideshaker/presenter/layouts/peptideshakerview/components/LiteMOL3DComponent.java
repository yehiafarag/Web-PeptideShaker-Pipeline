package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components;

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
import litemol.LiteMolComponent;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * This class provides an abstraction layer for LiteMOL 3D protein structure
 *
 * @author Yehia Farag
 */
public class LiteMOL3DComponent extends VerticalLayout {

    private String pdbId;
    private final LiteMolComponent proteinStructurePanel;

    public LiteMOL3DComponent() {
        tasks = new ArrayList<>();
        LiteMOL3DComponent.this.setSizeFull();
        LiteMOL3DComponent.this.setStyleName("iframecontainer");
        LiteMOL3DComponent.this.addStyleName("litemolcontainer");
        proteinStructurePanel = new LiteMolComponent();
        proteinStructurePanel.setSizeFull();
        LiteMOL3DComponent.this.addComponent(proteinStructurePanel);

    }
    private Future update3dFuture;
    private final List<Runnable> tasks;

    public void excuteQuery(String pdbId, int entity, String chainId, HashMap<String, Integer> proteinColor, HashSet<HashMap> entriesSet) {
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
            proteinStructurePanel.setValue("query-_-" + json + "-_-" + (!pdbId.equalsIgnoreCase(LiteMOL3DComponent.this.pdbId)));
//                JavaScript.getCurrent().execute("document.getElementById('litemolframe').contentWindow.excutequery('" + json + "'," + (!pdbId.equalsIgnoreCase(LiteMOL3DComponent.this.pdbId)) + ");");
            LiteMOL3DComponent.this.pdbId = pdbId;
        } catch (IOException ex) {
            ex.printStackTrace();
        }

//        tasks.add(initExcution(pdbId, entity, chainId, proteinColor, entriesSet));
//        if (update3dFuture != null) {
//             System.out.println("time to excute query "+update3dFuture.isDone());
//            while (!update3dFuture.isDone()) {
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException ex) {
//                }
//
//            }
//        }
//        System.out.println("time to excute query "+pdbId+" "+tasks.size());
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        update3dFuture = executorService.submit(tasks.get(tasks.size() - 1));
//        executorService.shutdown();
//        tasks.clear();
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException ex) {
//        }
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
                proteinStructurePanel.setValue("query-_-" + json + "-_-" + (!pdbId.equalsIgnoreCase(LiteMOL3DComponent.this.pdbId)));
//                JavaScript.getCurrent().execute("document.getElementById('litemolframe').contentWindow.excutequery('" + json + "'," + (!pdbId.equalsIgnoreCase(LiteMOL3DComponent.this.pdbId)) + ");");
                LiteMOL3DComponent.this.pdbId = pdbId;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        };

    }
    int counter = 1;

    @Override
    public void setVisible(boolean visible) {

//        if (!visible) {
//            this.reset3DView();
//        } 
//        proteinStructurePanel.setVisible(visible);
    }

    public boolean activate3DProteinView() {
        if (pdbId == null) {
            proteinStructurePanel.setValue("reset-_-");
//            proteinStructurePanel.setVisible(false);
            return false;
        } else {
//            proteinStructurePanel.setVisible(true);
            proteinStructurePanel.setValue("update-_-");
            return true;
        }
    }

    public void reset3DView() {
        pdbId = null;
        proteinStructurePanel.setValue("reset-_-");
//        proteinStructurePanel.setVisible(false);
//        JavaScript.getCurrent().execute("document.getElementById('litemolframe').contentWindow.reset();");
    }

}
