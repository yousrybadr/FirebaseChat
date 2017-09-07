package com.pentavalue.yousry.firebasechat.util;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by yousry on 9/6/2017.
 */

public class DatabaseRefs {

    public static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public static DatabaseReference mRootDatabaseReference = mDatabase.child(Util.ROOT_DATABASE_REFERENCE);
    public static DatabaseReference mUsersDatabaseReference = mDatabase.child(Util.ROOT_DATABASE_REFERENCE).child(Util.USERS_DATABASE_REFERENCE);
    public static DatabaseReference mRoomsDatabaseReference = mDatabase.child(Util.ROOT_DATABASE_REFERENCE).child(Util.ROOMS_DATABASE_REFERENCE);
    public static DatabaseReference mMessagesDatabaseReference = mDatabase.child(Util.ROOT_DATABASE_REFERENCE).child(Util.MESSAGES_DATABASE_REFERENCE);
    public static DatabaseReference mGroupRoomDatabaseReference = mDatabase.child(Util.ROOT_DATABASE_REFERENCE).child(Util.GROUPS_DATABASE_REFERENCE);


}
