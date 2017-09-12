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

    private UserModel first;
    private UserModel second;


    private String wallpaper_url;
    private List<MessageModel> messageModelList;
    private int isDelete;
    private boolean typing;

    public PrivateRoomChat() {
        this.first =new UserModel();
        this.second =new UserModel();
        this.messageModelList =new ArrayList<>();
        this.typing =false;
        this.isDelete =0;

    }

    public PrivateRoomChat(UserModel first, UserModel second, List<MessageModel> messageModelList) {
        this.first = first;
        this.second = second;
        this.messageModelList = messageModelList;
        this.typing =false;
        this.isDelete =0;
    }

    public PrivateRoomChat(UserModel first_user, UserModel second_user, String wallpaper_url, List<MessageModel> messageModelList) {
        this.first = first_user;
        this.second = second_user;
        this.wallpaper_url = wallpaper_url;
        this.messageModelList = messageModelList;
        this.isDelete =0;
        this.typing =false;
    }

    public UserModel getFirst() {
        return first;
    }

    public void setFirst(UserModel first) {
        this.first = first;
    }

    public UserModel getSecond() {
        return second;
    }

    public void setSecond(UserModel second) {
        this.second = second;
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
                "first='" + first.toString() + '\'' +
                ", second='" + second.toString() + '\'' +
                ", wallpaper_url='" + wallpaper_url + '\'' +
                ", messageModelList=" + messageModelList +
                ", isDelete=" + isDelete +
                ", typing=" + typing +
                '}';
    }
}
// [END room_class]
