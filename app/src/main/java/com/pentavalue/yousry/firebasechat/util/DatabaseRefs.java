package com.pentavalue.yousry.firebasechat.util;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by yousry on 9/6/2017.
 */

public class DatabaseRefs {

    public static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public static final DatabaseReference mRootDatabaseReference = mDatabase.child(Util.ROOT_DATABASE_REFERENCE);
    public static final DatabaseReference mUsersDatabaseReference = mDatabase.child(Util.ROOT_DATABASE_REFERENCE).child(Util.USERS_DATABASE_REFERENCE);
    public static final DatabaseReference mChatsDatabaseReference = mDatabase.child(Util.ROOT_DATABASE_REFERENCE).child(Util.CHAT_DATABASE_REFERENCE);
    public static final DatabaseReference mMessagesDatabaseReference = mDatabase.child(Util.ROOT_DATABASE_REFERENCE).child(Util.MESSAGES_DATABASE_REFERENCE);
    public static final DatabaseReference mTypingDatabaseReference = mDatabase.child(Util.ROOT_DATABASE_REFERENCE).child(Util.TYPING_DATABASE_REFERENCE);



}
