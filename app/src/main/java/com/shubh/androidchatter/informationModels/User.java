package com.shubh.androidchatter.informationModels;

import android.net.Uri;

/**
 * Created by root on 21/7/17.
 */

public class User {
    String name;
    String email;
    String hash;
    boolean profile;
    String profileUrl;

    Long lastseen;
    String status;

    public User(String name, String email,boolean profile, String profileUrl, Long lastseen, String status) {
        this.name = name;
        this.email = email;
        setHash();
        this.profile = profile;
        this.profileUrl = profileUrl;
        this.lastseen = lastseen;
        this.status = status;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public boolean isProfile() {
        return profile;
    }
    public void setProfile(boolean profile) {
        this.profile = profile;
    }

    public Long getLastseen() {
        return lastseen;
    }

    public void setLastseen(Long lastseen) {
        this.lastseen = lastseen;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getHash()
    {
        return hash;
    }

    public void setHash() {
        this.hash = email.hashCode()+"";
    }
    public User() {
    }
    public String getName() {
        return name;
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
}
