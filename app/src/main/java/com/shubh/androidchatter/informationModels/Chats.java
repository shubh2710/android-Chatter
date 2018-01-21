package com.shubh.androidchatter.informationModels;

import java.util.Date;

/**
 * Created by root on 14/7/17.
 */
public class Chats {

    private String chatName;
    private String chatFor;
    private long createTime;
    private Boolean syncState;
    private String profileUrl;
    public Chats(String chatName, String chatFor,Boolean syncState,String profileUrl) {
        this.chatName = chatName;
        this.chatFor = chatFor;
        this.syncState=syncState;
        this.profileUrl=profileUrl;
        createTime = new Date().getTime();
    }
    public Chats(String chatName, String chatFor,Boolean syncState) {
        this.chatName = chatName;
        this.chatFor = chatFor;
        this.syncState=syncState;
        this.profileUrl=null;
        createTime = new Date().getTime();
    }
    public Chats(){
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public String getChatFor() {
        return chatFor;
    }

    public void setChatFor(String chatFor) {
        this.chatFor = chatFor;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
    public Boolean getSyncState() {
        return syncState;
    }

    public void setSyncState(Boolean syncState) {
        this.syncState = syncState;
    }
}