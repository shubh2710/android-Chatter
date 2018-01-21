package com.shubh.androidchatter.informationModels;

/**
 * Created by root on 17/8/17.
 */

public class Requests {
    private String name;

    public Boolean getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(Boolean syncStatus) {
        this.syncStatus = syncStatus;
    }

    private Boolean syncStatus;
    public String getName() {
        return name;
    }

    public Requests(String name, Boolean syncStatus, String email) {
        this.name = name;
        this.syncStatus = syncStatus;
        this.email = email;
    }
    public Requests() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String email;
}
