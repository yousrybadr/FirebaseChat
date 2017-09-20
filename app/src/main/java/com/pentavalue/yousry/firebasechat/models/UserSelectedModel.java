package com.pentavalue.yousry.firebasechat.models;

/**
 * Created by yousry on 9/20/2017.
 */

public class UserSelectedModel extends UserModel {
    private boolean isSelected;


    public UserSelectedModel(String name, String imageUrl, String id, boolean isSelected) {
        super(name, imageUrl, id);
        this.isSelected = isSelected;
    }

    public UserSelectedModel(boolean isSelected, String chatID) {
        super();
        this.isSelected = isSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }



    @Override
    public String toString() {
        return "UserSelectedModel{" +
                "UserModel="+super.toString()+'\''+
                ", isSelected=" + isSelected +
                '}';
    }
}
