package com.pentavalue.yousry.firebasechat.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yousry on 9/5/2017.
 */

// [START room_class]
@IgnoreExtraProperties
public class PrivateRoomChat {
    private String first_user;
    private String second_user;
    private String wallpaper_url;
    private List<MessageModel> messageModelList;
    private int isDelete;
    private boolean typing;

    public PrivateRoomChat() {
        this.first_user = "";
        this.second_user = "";
        this.wallpaper_url = "";
        this.messageModelList = new ArrayList<>();
        this.isDelete =0;
        this.typing =false;
    }

    public PrivateRoomChat(String first_user, String second_user, String wallpaper_url, List<MessageModel> messageModelList) {
        this.first_user = first_user;
        this.second_user = second_user;
        this.wallpaper_url = wallpaper_url;
        this.messageModelList = messageModelList;
        this.isDelete =0;
        this.typing =false;
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

    public String getWallpaper_url() {
        return wallpaper_url;
    }

    public void setWallpaper_url(String wallpaper_url) {
        this.wallpaper_url = wallpaper_url;
    }

    public List<MessageModel> getMessageModelList() {
        return messageModelList;
    }

    public void setMessageModelList(List<MessageModel> messageModelList) {
        this.messageModelList = messageModelList;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    public boolean isTyping() {
        return typing;
    }

    public void setTyping(boolean typing) {
        this.typing = typing;
    }

    public void addMessage(MessageModel messageModel){
        this.messageModelList.add(messageModel);
    }
    public MessageModel getMessage(int position){
        return this.messageModelList.get(position);
    }



    @Override
    public String toString() {
        return "PrivateRoomChat{" +
                "first_user='" + first_user + '\'' +
                ", second_user='" + second_user + '\'' +
                ", wallpaper_url='" + wallpaper_url + '\'' +
                ", isDelete=" + isDelete +
                ", typing=" + typing +
                '}';
    }
}
// [END room_class]
