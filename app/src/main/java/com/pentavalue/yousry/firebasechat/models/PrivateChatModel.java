package com.pentavalue.yousry.firebasechat.models;

/**
 * Created by yousry on 9/5/2017.
 */
public class PrivateChatModel {

    private String id;
    private UserModel userModel;
    private String message;
    private String timeStamp;
    private FileModel file;

    public PrivateChatModel() {
    }

    public PrivateChatModel(UserModel userModel, String message, String timeStamp, FileModel file) {
        this.userModel = userModel;
        this.message = message;
        this.timeStamp = timeStamp;
        this.file = file;
    }

    public PrivateChatModel(UserModel userModel, String timeStamp) {
        this.userModel = userModel;
        this.timeStamp = timeStamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public FileModel getFile() {
        return file;
    }

    public void setFile(FileModel file) {
        this.file = file;
    }



    @Override
    public String toString() {
        return "PrivateChatModel{" +
                ", file=" + file +
                ", timeStamp='" + timeStamp + '\'' +
                ", message='" + message + '\'' +
                ", userModel=" + userModel +
                '}';
    }
}
