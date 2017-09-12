package com.pentavalue.yousry.firebasechat.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by yousry on 9/5/2017.
 */

@IgnoreExtraProperties
public class MessageModel {
    private String sender;
    private String receiver;
    private String text;
    private String date;
    private int isDeleted;
    private boolean isStarred;

    public MessageModel() {
    }

    public MessageModel(String sender, String receiver, String text, String date) {
        this.sender = sender;
        this.receiver = receiver;
        this.text = text;
        this.date = date;
    }

    public MessageModel(String sender, String receiver, String text, String date, int isDeleted, boolean isStarred) {
        this.sender = sender;
        this.receiver = receiver;
        this.text = text;
        this.date = date;
        this.isDeleted = isDeleted;
        this.isStarred = isStarred;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

    public boolean isStarred() {
        return isStarred;
    }

    public void setStarred(boolean starred) {
        isStarred = starred;
    }

    @Override
    public String toString() {
        return "MessageModel{" +
                "sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", text='" + text + '\'' +
                ", date='" + date + '\'' +
                ", isDeleted=" + isDeleted +
                ", isStarred=" + isStarred +
                '}';
    }

}
