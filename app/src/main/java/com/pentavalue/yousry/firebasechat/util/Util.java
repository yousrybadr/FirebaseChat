package com.pentavalue.yousry.firebasechat.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.provider.ContactsContract;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.pentavalue.yousry.firebasechat.models.Contact;

import java.util.ArrayList;
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
    public static final String ROOMS_DATABASE_REFERENCE = "rooms";
    public static final String GROUPS_DATABASE_REFERENCE = "groups";
    public static final String MESSAGES_DATABASE_REFERENCE = "messages";

    public static final String SHARED_PREFERENCE_KEY ="chat_app";
    public static final String CURRENT_USER_EXTRA_KEY ="current_user";
    public static final String ITEM_USER_EXTRA_KEY ="selected_user";








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


    public static List<Contact> ReadAllContacts(Context context){
        List<Contact> contacts =new ArrayList<>();
        //Contact contact=new Contact();
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
