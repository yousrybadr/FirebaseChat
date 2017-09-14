package com.pentavalue.yousry.firebasechat.models;

import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.pentavalue.yousry.firebasechat.util.DatabaseRefs;
import com.pentavalue.yousry.firebasechat.util.Logs;

/**
 * Created by yousry on 9/12/2017.
 */

public class Helper {
    public static final String TAG =Helper.class.getSimpleName();

    public UserModel modal =new UserModel();


    public void setUserModel(final FirebaseUser user){
        ;
    }
}
