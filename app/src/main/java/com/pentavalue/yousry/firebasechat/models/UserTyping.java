package com.pentavalue.yousry.firebasechat.models;

/**
 * Created by yousry on 9/18/2017.
 */

public class UserTyping {
    String id;
    boolean typing;

    public UserTyping() {
        id ="";
        typing =false;
    }

    public UserTyping(String id, boolean typing) {
        this.id = id;
        this.typing = typing;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isTyping() {
        return typing;
    }

    public void setTyping(boolean typing) {
        this.typing = typing;
    }
}
