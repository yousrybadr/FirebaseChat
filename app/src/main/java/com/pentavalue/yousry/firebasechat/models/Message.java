package com.pentavalue.yousry.firebasechat.models;

/**
 * Created by yousry on 9/13/2017.
 */

public class Message {
    private String audio;
    private double latitude;
    private double longitude;
    private int messageNum;
    private String pictureURL;
    private String senderID;
    private String senderThimbnal;
    private String status;
    private String text;
    private String time;
    private String type;
    private String videoURL;
    private String id;

    public Message() {
        this.id ="";
        this.audio = "";
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.messageNum = 0;
        this.pictureURL = "";
        this.senderID = "";
        this.senderThimbnal = "";
        this.status = "";
        this.text = "";
        this.time = "";
        this.type = "";
        this.videoURL = "";
    }



    //Text
    public Message(String senderID, String senderThimbnal, String text, String time, String type, int messageNum) {
        this.messageNum = messageNum;
        this.senderID = senderID;
        this.senderThimbnal = senderThimbnal;
        this.time =time;
        this.text = text;
        this.type = type;
        this.id ="";
        this.audio = "";
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.pictureURL = "";
        this.status = "";
        this.videoURL = "";
    }

    public Message(String pictureURL, String senderID, String time, String type) {
        this.pictureURL = pictureURL;
        this.senderID = senderID;
        this.time = time;
        this.type = type;
        this.id ="";
        this.audio = "";
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.messageNum = 0;
        this.senderThimbnal = "";
        this.status = "";
        this.videoURL = "";
    }
    public Message(double Lat, double Lng, String SenderName, String type, String time) {
        this.pictureURL = "";
        this.senderID = SenderName;
        this.time = time;
        this.type = type;
        this.id ="";
        this.audio = "";
        this.latitude = Lat;
        this.longitude = Lng;
        this.messageNum = 0;
        this.senderThimbnal = "";
        this.status = "";
        this.videoURL = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getMessageNum() {
        return messageNum;
    }

    public void setMessageNum(int messageNum) {
        this.messageNum = messageNum;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getSenderThimbnal() {
        return senderThimbnal;
    }

    public void setSenderThimbnal(String senderThimbnal) {
        this.senderThimbnal = senderThimbnal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    @Override
    public String toString() {
        return "Message{" +
                "audio='" + audio + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", messageNum=" + messageNum +
                ", pictureURL='" + pictureURL + '\'' +
                ", senderID='" + senderID + '\'' +
                ", senderThimbnal='" + senderThimbnal + '\'' +
                ", status='" + status + '\'' +
                ", text='" + text + '\'' +
                ", time='" + time + '\'' +
                ", type='" + type + '\'' +
                ", videoURL='" + videoURL + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
