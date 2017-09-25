package com.pentavalue.yousry.firebasechat.util;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.pentavalue.yousry.firebasechat.models.Contact;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yousry on 9/4/2017.
 */

public class Util {

    //Google Places Api for Locations
    public static final String API_KEY_GOOGLE = "AIzaSyAwNBlohwUY76XSn8Fr1ENWkTmKurAWqsY";


    //Database Constants
    public static final String ROOT_DATABASE_REFERENCE = "chat_app";
    public static final String USERS_DATABASE_REFERENCE = "users";
    public static final String CHAT_DATABASE_REFERENCE = "chats";
    public static final String MESSAGES_DATABASE_REFERENCE = "messages";
    public static final String TYPING_DATABASE_REFERENCE = "typing";


    public static final String SHARED_PREFERENCE_KEY ="chat_app";
    public static final String LOGIN_PREFERENCE_KEY ="login";

    public static final String CURRENT_USER_EXTRA_KEY ="current_user";
    public static final String ITEM_USER_EXTRA_KEY ="selected_user";

    public static final String FIRST_TIME_KEY ="first_time_key";

    public static enum MESSAGE_TYPES {
        FILE_TYPE,
        IMAGE_TYPE,
        MAP_TYPE,
        AUDIO_TYPE,
        VIDEO_TYPE,
        TEXT_TYPE
    }


    /*public static enum MESSAGE_TYPES{
        FILE_TYPE ("FILE_TYPE"),
        IMAGE_TYPE ("IMAGE_TYPE"),
        MAP_TYPE ("MAP_TYPE"),
        AUDIO_TYPE ("AUDIO_TYPE"),
        VIDEO_TYPE ("VIDEO_TYPE"),
        TEXT_TYPE ("TEXT_TYPE");

        String name;
        private MESSAGE_TYPES(String name){
            this.name =name;
        }
        public boolean equalsName(String otherName) {
            // (otherName == null) check is not needed because name.equals(null) returns false
            return name.equals(otherName);
        }
        public String toString() {
            return this.name;
        }
    }*/


    public static MESSAGE_TYPES TYPES;


    //Storage Constants
    public static final String URL_STORAGE_REFERENCE = "gs://fir-library-81a54.appspot.com";
    public static final String FOLDER_STORAGE_IMG = "images";
    public static final String FOLDER_STORAGE_VIDEO = "videos";
    public static final String FOLDER_STORAGE_DOCUMENT = "documents";
    public static final String FOLDER_STORAGE_AUDIO = "audioFiles";
    public static final String FOLDER_STORAGE_IMG_USERS= "users";
    public static final String FOLDER_STORAGE_IMG_MESSAGES = "messages";


    private static String nameOfAudio ;
    private static String nameOfImageMessage ;
    private static String nameOfImageUser ;

    public static String getNameOfAudio() {
        nameOfAudio = android.text.format.DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString() + "_audioFile";
        return nameOfAudio;
    }

    public static String getNameOfImageMessage() {
        nameOfImageMessage = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString() + "_messageImage";
        return nameOfImageMessage;
    }

    public static String getNameOfImageUser() {
        nameOfImageUser = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString() + "_userImage";
        return nameOfImageUser;
    }

    public static final String CHAT_KEY_MODEL = "CHAT_KEY_MODEL";
    public static final String CONTACT_KEY_MODEL = "CONTACT_KEY_MODEL";

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


    public static List<Contact> ReadAllContacts(Context context){
        List<Contact> contacts =new ArrayList<>();
        //Contact contact=new Contact();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if( context.checkSelfPermission( Manifest.permission.READ_CONTACTS ) != PackageManager.PERMISSION_GRANTED )
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                Contact contact =new Contact();
                contact.setContact_id(cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID)));
                contact.setContact_name(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));

                if (Integer.parseInt(cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{contact.getContact_id()}, null);
                    while (pCur.moveToNext()) {

                        contact.setPhone_type(pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)));
                        contact.setPhone_number(pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER)));
                        if(contact.getPhone_type() == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE){
                            contact.setMobile(true);
                            break;
                        }

                    }
                    pCur.close();
                }

                contacts.add(new Contact(contact));


            }
        }
        return contacts;
    }

    //Firebase User_ID Client
    public static String getUserid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    public static CharSequence convertTimeStamp(String mileSeconds){
        return DateUtils.getRelativeTimeSpanString(Long.parseLong(mileSeconds),System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
    }



}
