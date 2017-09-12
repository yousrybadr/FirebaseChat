package com.pentavalue.yousry.firebasechat.models;

/**
 * Created by yousry on 9/12/2017.
 */

public class CurrentUser {
    private UserModel userModel;

    private static final CurrentUser ourInstance = new CurrentUser();

    public static CurrentUser getInstance() {
        return ourInstance;
    }

    private CurrentUser() {

    }

    public static void setOurInstance(UserModel model){
        //TODO Set Object
        ourInstance.setUserModel(model);
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }
}
