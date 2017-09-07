package com.pentavalue.yousry.firebasechat.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by yousry on 9/5/2017.
 */

@IgnoreExtraProperties
public class MessageModel {
    private String first_user;
    private String second_user;
    private String text;
    private String date;
    private int isDeleted;
    private boolean isStarred;

    public MessageModel() {
    }

    public MessageModel(String first_user, String second_user, String text, String date) {
        this.first_user = first_user;
        this.second_user = second_user;
        this.text = text;
        this.date = date;
    }

    public MessageModel(String first_user, String second_user, String text, String date, int isDeleted, boolean isStarred) {
        this.first_user = first_user;
        this.second_user = second_user;
        this.text = text;
        this.date = date;
        this.isDeleted = isDeleted;
        this.isStarred = isStarred;
    }

    public String getFirst_user() {
        return first_user;
    }

    public void setFirst_user(String first_user) {
        this.first_user = first_user;
    }

    public String getSecond_user() {
        return second_user;
    }

    public void setSecond_user(String second_user) {
        this.second_user = second_user;
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
                "first_user='" + first_user + '\'' +
                ", second_user='" + second_user + '\'' +
                ", text='" + text + '\'' +
                ", date='" + date + '\'' +
                ", isDeleted=" + isDeleted +
                ", isStarred=" + isStarred +
                '}';
    }

}
