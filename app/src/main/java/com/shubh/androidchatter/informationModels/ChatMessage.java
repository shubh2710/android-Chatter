package com.shubh.androidchatter.informationModels;

import java.util.Date;

/**
 * Created by root on 14/7/17.
 */
public class ChatMessage {

    private String messageText;
    private String messageUser;
    private long messageTime;
    private boolean messageSyncState;

    public ChatMessage(String messageText, String messageUser,boolean messageSyncState) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.messageSyncState=messageSyncState;
        // Initialize to current time
        messageTime = new Date().getTime();
    }
    public Boolean getMessageSyncState() {
        return messageSyncState;
    }

    public void setMessageSyncState(Boolean messageSyncState) {
        this.messageSyncState = messageSyncState;
    }
    public ChatMessage(){

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}