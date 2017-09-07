package com.pentavalue.yousry.firebasechat.util;


import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import com.pentavalue.yousry.firebasechat.BuildConfig;

import java.io.File;

/**
 * Created by yousry on 9/7/2017.
 */

public class Validation {
    public static boolean checkEmailIsValid(String text) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(text);
        return m.matches();

    }

    public static boolean isValidLogin(String email, String password){
        if(( !email.isEmpty()) && (!password.isEmpty())){
            return true;
        }
        return false;
    }
    public static Uri getUriFromFile(Context context, File file){
        return FileProvider.getUriForFile(context,
                BuildConfig.APPLICATION_ID + ".provider",
                file);
    }
    public static boolean checkRePasswordValid(String pass1, String pass2) {
        if (pass1.equals(pass2))
            return true;
        return false;
    }

    public static ValidMessage checkPasswordIsValid(String text) {
        ValidMessage valid = newInstanceValidMessage();

        //return true if and only if password:
        //1. have at least eight characters.
        //2. consists of only letters and digits.
        //3. must contain at least two digits.
        if (text.length() < 8) {
            valid.message = "your Password's Length must be greater than 8 letters";
            valid.state = false;
            return valid;
        } else {
            char c;
            int count = 1;
            for (int i = 0; i < text.length() - 1; i++) {
                c = text.charAt(i);
                if (!Character.isLetterOrDigit(c)) {
                    valid.message = "your Password's String must contain one Letter at least";
                    valid.state = false;
                    return valid;
                } else if (Character.isDigit(c)) {
                    count++;
                    if (count < 2) {
                        valid.message = "your Password's Length must consist of one Digit";
                        valid.state = false;
                        return valid;
                    }
                }
            }
        }
        valid.message = "true";
        valid.state = true;
        return valid;
    }
    public static ValidMessage newInstanceValidMessage(){
        return new ValidMessage();
    }

    public static class ValidMessage
    {
        public ValidMessage(){
            message = "";
            state =false;
        }
        public String message;
        public boolean state;
    }
}
