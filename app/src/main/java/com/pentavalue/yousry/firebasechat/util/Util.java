package com.pentavalue.yousry.firebasechat.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.text.format.DateUtils;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by yousry on 9/4/2017.
 */

public class Util {

    //Google Places Api for Locations
    public static final String API_KEY_GOOGLE = "AIzaSyAwNBlohwUY76XSn8Fr1ENWkTmKurAWqsY";


    //Database Constants
    public static final String ROOT_DATABASE_REFERENCE = "chat_app";
    public static final String USERS_DATABASE_REFERENCE = "users";
    public static final String ROOMS_DATABASE_REFERENCE = "rooms";
    public static final String GROUPS_DATABASE_REFERENCE = "groups";
    public static final String MESSAGES_DATABASE_REFERENCE = "messages";





    //Storage Constants
    public static final String URL_STORAGE_REFERENCE = "gs://fir-library-81a54.appspot.com";
    public static final String FOLDER_STORAGE_IMG = "images";
    public static final String FOLDER_STORAGE_VIDEO = "videos";
    public static final String FOLDER_STORAGE_DOCUMENT = "documents";
    public static final String FOLDER_STORAGE_AUDIO = "audioFiles";


    //Initailize Toast For Confirmation
    public static void initToast(Context c, String message){
        Toast.makeText(c,message,Toast.LENGTH_SHORT).show();
    }



    public  static boolean verifyNetworkConnection(Context context) {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        conectado = conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected();
        return conectado;
    }

    public static String local(String latitudeFinal,String longitudeFinal){
        return "https://maps.googleapis.com/maps/api/staticmap?center="+latitudeFinal+","+longitudeFinal+"&zoom=18&size=280x280&markers=color:red|"+latitudeFinal+","+longitudeFinal;
    }



    //Firebase User_ID Client
    public static String getUserid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    public static CharSequence convertTimeStamp(String mileSeconds){
        return DateUtils.getRelativeTimeSpanString(Long.parseLong(mileSeconds),System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
    }



}
