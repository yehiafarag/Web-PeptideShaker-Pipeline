package com.uib.web.peptideshaker.galaxy.dataobjects;

/**
 * This class represents the Galaxy Dataset in the application
 *
 * @author Yehia Farag
 */
public class SystemDataSet {

    private String name;
    private String galaxyId;
    private String historyId;
    private String reIndexedId;
    private String reIndexedHistoryId;
    private String downloadUrl;
    private String status;
    private String type;
    private String nelsKey;

    public String getNelsKey() {        
        return nelsKey;
    }

    public void setNelsKey(String nelsKey, String exitType) {
        if (nelsKey.endsWith(exitType)) {
            this.nelsKey = nelsKey;
        } else {
            this.nelsKey = nelsKey + "." + exitType;
        }
    }

    public boolean isAvailableOnGalaxy() {
        return availableOnGalaxy;
    }

    public void setAvailableOnGalaxy(boolean availableOnGalaxy) {
        this.availableOnGalaxy = availableOnGalaxy;
    }
    private double size;
    private boolean availableOnNels;
    private boolean availableOnGalaxy=true;

    public boolean isAvailableOnNels() {
        return availableOnNels;
    }

    public void setAvailableOnNels(boolean availableOnNels) {
        this.availableOnNels = availableOnNels;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGalaxyId() {
        return galaxyId;
    }

    public void setGalaxyId(String galaxyId) {
        this.galaxyId = galaxyId;
    }

    public String getHistoryId() {
        return historyId;
    }

    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }

    public String getReIndexedId() {
        return reIndexedId;
    }

    public void setReIndexedId(String reIndexedId) {
        this.reIndexedId = reIndexedId;
    }

    public String getReIndexedHistoryId() {
        return reIndexedHistoryId;
    }

    public void setReIndexedHistoryId(String reIndexedHistoryId) {
        this.reIndexedHistoryId = reIndexedHistoryId;
    }

}
