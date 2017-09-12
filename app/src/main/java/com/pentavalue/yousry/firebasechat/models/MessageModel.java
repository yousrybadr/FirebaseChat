package com.pentavalue.yousry.firebasechat.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by yousry on 9/5/2017.
 */

@IgnoreExtraProperties
public class MessageModel {

    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String text;
    private String timeStamp;
    private int isDeleted;
    private boolean isStarred;

    public MessageModel() {
        userId="";
        text ="";
        timeStamp ="";
        isDeleted =0;
        isStarred =false;
    }

    public MessageModel(String userId, String text, String timeStamp) {
        this.userId= userId;
        this.text = text;
        this.timeStamp = timeStamp;
    }

    public MessageModel(String userId, String text, String timeStamp, int isDeleted, boolean isStarred) {
        this.userId = userId;
        this.text = text;
        this.timeStamp = timeStamp;
        this.isDeleted = isDeleted;
        this.isStarred = isStarred;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
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
                ", text='" + text + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", isDeleted=" + isDeleted +
                ", isStarred=" + isStarred +
                '}';
    }

}
