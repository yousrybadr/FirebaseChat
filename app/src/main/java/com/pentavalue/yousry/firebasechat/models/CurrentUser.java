package com.pentavalue.yousry.firebasechat.models;

/**
 * Created by yousry on 9/7/2017.
 */

public class CurrentUser {
    private static final CurrentUser ourInstance = new CurrentUser();

    private UserModel userModel;
    public static CurrentUser getInstance() {
        return ourInstance;
    }


    public boolean isChecked ;
    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    private CurrentUser() {
        userModel =new UserModel();
        isChecked =false;
    }
}
