package com.pentavalue.yousry.firebasechat.util;

import android.util.Log;

/**
 * Created by yousry on 9/7/2017.
 */

public class Logs {
    public static void LogV(String key,String value){
        Log.v(key,value);
    }
    public static void LogI(String key,String value){
        Log.i(key,value);
    }
    public static void LogD(String key,String value){
        Log.d(key,value);
    }
    public static void LogE(String key,String value){
        Log.e(key,value);
    }
    public static void LogW(String key,String value){
        Log.w(key,value);
    }


}
