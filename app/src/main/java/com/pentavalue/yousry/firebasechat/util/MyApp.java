package com.pentavalue.yousry.firebasechat.util;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by yousry on 9/6/2017.
 */

public class MyApp extends Application {

    public static DatabaseReference mDatabase ;
    @Override
    public void onCreate() {
        super.onCreate();

        //FirebaseDatabase.getInstance().getReference().setPriority(Util.ROOT_DATABASE_REFERENCE);
    }
}
