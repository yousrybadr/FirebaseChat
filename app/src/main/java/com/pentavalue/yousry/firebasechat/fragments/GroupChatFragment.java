package com.pentavalue.yousry.firebasechat.fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.pentavalue.yousry.firebasechat.R;
import com.pentavalue.yousry.firebasechat.adapters.UserListAdapter;
import com.pentavalue.yousry.firebasechat.holders.UserListHolder;
import com.pentavalue.yousry.firebasechat.models.Chat;
import com.pentavalue.yousry.firebasechat.models.Contact;
import com.pentavalue.yousry.firebasechat.models.CurrentUser;
import com.pentavalue.yousry.firebasechat.models.UserModel;
import com.pentavalue.yousry.firebasechat.models.UserSelectedModel;
import com.pentavalue.yousry.firebasechat.util.DatabaseRefs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class GroupChatFragment extends FragmentActivity {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100 ;
    private static final String TAG = GroupChatFragment.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.take_imageView_group)
    ImageView imageView;
    @BindView(R.id.name_editText_group)
    EditText nameEditText;
    @BindView(R.id.recyclerGroup)
    RecyclerView recyclerView;
    @BindView(R.id.backImageButton)
    ImageButton backImageButton;
    List<UserSelectedModel> modelList;
    List<Contact> chatContacts;
    List<Contact> contactList;
    UserListAdapter adapter;
    private LinearLayoutManager mLinearLayoutManager;

    public GroupChatFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static GroupChatFragment newInstance() {
        GroupChatFragment fragment = new GroupChatFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_group_chat);
        ButterKnife.bind(this);

        mLinearLayoutManager =new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        chatContacts = new ArrayList<>();
        contactList = new ArrayList<>();
        modelList =new ArrayList<>();
        showContacts();

        //fillUserModels();


    }


    @OnClick(R.id.backImageButton)
    void onBackButtonPressed() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }






    void fillUserModels(){
        if(chatContacts.size() >= 0){
            for (int i = 0 ; i < chatContacts.size() ; i++ ){
                modelList.add(
                        new UserSelectedModel(
                                chatContacts.get(i).getUserModel().getName(),
                                chatContacts.get(i).getUserModel().getImageUrl(),
                                chatContacts.get(i).getUserModel().getId(),
                                false
                        )
                );
            }
            adapter =new UserListAdapter(modelList, this, new UserListAdapter.OnItemCheckListener() {
                @Override
                public void onItemCheck(UserListHolder holder, int pos) {
                    adapter.getItemModelList(pos).setSelected(true);
                    holder.setNameTextViewColor(GroupChatFragment.this,0);
                }

                @Override
                public void onItemUncheck(UserListHolder holder, int pos) {
                    holder.setNameTextViewColor(GroupChatFragment.this, 1);
                    adapter.getItemModelList(pos).setSelected(false);
                }
            });
            recyclerView.setAdapter(adapter);

        }
    }
    void checkIsMessengerOrNot() {
        DatabaseReference ref = DatabaseRefs.mUsersDatabaseReference;
        if (contactList.size() == 0 || contactList == null) {
            Toast.makeText(this, "You have not Contacts", Toast.LENGTH_SHORT).show();
            return;
        }
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        Log.v(TAG, "userModel is :" + userModel.toString());
                        for (int i = 0; i < contactList.size(); i++) {
                            Log.v(TAG, "contactModel is :" + contactList.get(i).toString());


                            if (contactList.get(i).getPhone_number().equals(CurrentUser.getInstance().getUserModel().getPhone())) {
                                continue;
                            }
                            if (contactList.get(i).getPhone_number().equals(userModel.getPhone())) {
                                contactList.get(i).setMessengerContact(true);
                                Log.v(TAG, contactList.get(i).getPhone_number());
                                contactList.get(i).setUserModel(userModel);
                                chatContacts.add(contactList.get(i));
                            }
                        }
                        Log.v(TAG, userModel.toString());

                        //refreshLayout.setRefreshing(false);
                    }
                    //searchOnChats();
                    fillUserModels();

                } catch (NullPointerException ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void showContacts(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            contactList = ReadAllContacts();
            Collections.sort(contactList);
            checkIsMessengerOrNot();
        }
    }
    public List<Contact> ReadAllContacts(){
        List<Contact> contacts =new ArrayList<>();

        ContentResolver cr = getContentResolver();
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

}
