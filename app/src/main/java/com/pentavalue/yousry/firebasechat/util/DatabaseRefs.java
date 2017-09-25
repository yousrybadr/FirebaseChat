package com.pentavalue.yousry.firebasechat.util;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by yousry on 9/6/2017.
 */

public class DatabaseRefs {

    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private static StorageReference mStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(Util.URL_STORAGE_REFERENCE);

    public static final DatabaseReference mRootDatabaseReference = mDatabase.child(Util.ROOT_DATABASE_REFERENCE);
    public static final DatabaseReference mUsersDatabaseReference = mDatabase.child(Util.ROOT_DATABASE_REFERENCE).child(Util.USERS_DATABASE_REFERENCE);
    public static final DatabaseReference mChatsDatabaseReference = mDatabase.child(Util.ROOT_DATABASE_REFERENCE).child(Util.CHAT_DATABASE_REFERENCE);
    public static final DatabaseReference mMessagesDatabaseReference = mDatabase.child(Util.ROOT_DATABASE_REFERENCE).child(Util.MESSAGES_DATABASE_REFERENCE);
    public static final DatabaseReference mTypingDatabaseReference = mDatabase.child(Util.ROOT_DATABASE_REFERENCE).child(Util.TYPING_DATABASE_REFERENCE);


    public static final StorageReference nRootStorage = mStorageReference;
    public static final StorageReference nAudioStorage = mStorageReference.child(Util.FOLDER_STORAGE_AUDIO);
    public static final StorageReference nImageStorage = mStorageReference.child(Util.FOLDER_STORAGE_IMG);
    public static final StorageReference nVideoStorage = mStorageReference.child(Util.FOLDER_STORAGE_VIDEO);
    public static final StorageReference nDocumentStorage = mStorageReference.child(Util.FOLDER_STORAGE_DOCUMENT);
    public static final StorageReference nImageOfUsersStorage = mStorageReference.child(Util.FOLDER_STORAGE_IMG).child(Util.FOLDER_STORAGE_IMG_USERS);
    public static final StorageReference nImageOfMessagesStorage = mStorageReference.child(Util.FOLDER_STORAGE_IMG).child(Util.FOLDER_STORAGE_IMG_MESSAGES);


}
