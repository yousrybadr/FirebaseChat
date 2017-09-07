package com.pentavalue.yousry.firebasechat.models;

import java.util.List;

/**
 * Created by yousry on 9/5/2017.
 */

public class GroupRoomChat {
    private String name;
    private String user_admin;
    private List<String> members;
    private String created_date;
    private String image_path;
    private String wallpaper;
    private int isDelete;

    public GroupRoomChat() {
    }

    public GroupRoomChat(String name, String user_admin, List<String> members, String created_date, String image_path, String wallpaper, int isDelete) {
        this.name = name;
        this.user_admin = user_admin;
        this.members = members;
        this.created_date = created_date;
        this.image_path = image_path;
        this.wallpaper = wallpaper;
        this.isDelete = isDelete;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_admin() {
        return user_admin;
    }

    public void setUser_admin(String user_admin) {
        this.user_admin = user_admin;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getWallpaper() {
        return wallpaper;
    }

    public void setWallpaper(String wallpaper) {
        this.wallpaper = wallpaper;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }


    public void addMember(UserModel user){
        this.members.add(user.getId());
    }

    @Override
    public String toString() {
        return "GroupRoomChat{" +
                "name='" + name + '\'' +
                ", user_admin='" + user_admin + '\'' +
                ", created_date='" + created_date + '\'' +
                ", image_path='" + image_path + '\'' +
                ", wallpaper='" + wallpaper + '\'' +
                ", isDelete=" + isDelete +
                '}';
    }
}
