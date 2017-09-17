package com.pentavalue.yousry.firebasechat.models;

import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yousry on 9/13/2017.
 */

public class Chat {
    private String chatImage;
    private String conversationName;
    private String dateCreated;
    private boolean isGroup;
    private String wallpaperURL;
    private List<String> members;
    private String id;
    private boolean typing ;

    public boolean isTyping() {
        return typing;
    }

    public void setTyping(boolean typing) {
        this.typing = typing;
    }

    public Chat() {
        this.chatImage = "";
        this.conversationName = "";
        this.dateCreated = "";
        this.isGroup = false;
        this.wallpaperURL = "https://firebasestorage.googleapis.com/v0/b/fir-library-81a54.appspot.com/o/images%2F2017-09-07_044310_gallery?alt=media&token=bf392434-ba25-4c6a-b28a-f6546933ed97";
        this.members = new ArrayList<>();
        this.id = "";
        this.typing =false;
    }

    public void addMember(String userId){
        members.add(userId);
    }
    public String getMember(int pos){
        return members.get(pos);
    }
    public String getMember(String user_id){
        for(String p : members){
            if(p.equals(user_id)){
                return p;
            }
        }
        return null;
    }


    public String getChatImage() {
        return chatImage;
    }

    public void setChatImage(String chatImage) {
        this.chatImage = chatImage;
    }

    public String getConversationName() {
        return conversationName;
    }

    public void setConversationName(String conversationName) {
        this.conversationName = conversationName;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean isGroup) {
        isGroup = isGroup;
    }

    public String getWallpaperURL() {
        return wallpaperURL;
    }

    public void setWallpaperURL(String wallpaperURL) {
        this.wallpaperURL = wallpaperURL;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String listToString(){
        String s="";
        for(String ss:members){
            s += "[member:'"+ss +"']\n" ;
        }
        return s;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "chatImage='" + chatImage + '\'' +
                ", conversationName='" + conversationName + '\'' +
                ", dateCreated='" + dateCreated + '\'' +
                ", isGroup=" + isGroup +
                ", wallpaperURL='" + wallpaperURL + '\'' +
                ", members=" + listToString() +
                ", id='" + id + '\'' +
                ", typing='" + typing + '\'' +
                '}';
    }


}
