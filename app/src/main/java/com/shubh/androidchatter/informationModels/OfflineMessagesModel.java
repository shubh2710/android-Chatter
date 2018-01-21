package com.shubh.androidchatter.informationModels;

/**
 * Created by root on 21/7/17.
 */

import java.util.Date;

public class OfflineMessagesModel {

    private String messageText;
    private String senderUserName;
    private String reciverUserName;
    private String messageUser;
    private String messageSendTo;
    private Boolean messageSyncState;

    public OfflineMessagesModel(String messageText, String senderUserName, String reciverUserName, String messageUser, String messageSendTo, Boolean messageSyncState) {
        this.messageText = messageText;
        this.senderUserName = senderUserName;
        this.reciverUserName = reciverUserName;
        this.messageUser = messageUser;
        this.messageSendTo = messageSendTo;
        this.messageSyncState = messageSyncState;
        this.messageTime=new Date().getTime();
    }

    public String getSenderUserName() {
        return senderUserName;
    }

    public void setSenderUserName(String senderUserName) {
        this.senderUserName = senderUserName;
    }

    public String getReciverUserName() {
        return this.reciverUserName;
    }

    public void setReciverUserName(String reciverUserName) {
        this.reciverUserName= reciverUserName;
    }
    public Boolean getMessageSyncState() {
        return messageSyncState;
    }


    public void setMessageSyncState(Boolean messageSyncState) {
        this.messageSyncState = messageSyncState;
    }

    private long messageTime;
    public OfflineMessagesModel(){

    }
    public String getMessageSendTo() {
        return messageSendTo;
    }

    public void setMessageSendTo(String messageSendTo) {
        this.messageSendTo = messageSendTo;
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