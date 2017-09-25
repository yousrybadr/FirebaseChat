package com.pentavalue.yousry.firebasechat.fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pentavalue.yousry.firebasechat.R;
import com.pentavalue.yousry.firebasechat.activities.test.FullScreenImageActivity;
import com.pentavalue.yousry.firebasechat.adapters.UserListAdapter;
import com.pentavalue.yousry.firebasechat.holders.UserListHolder;
import com.pentavalue.yousry.firebasechat.models.Chat;
import com.pentavalue.yousry.firebasechat.models.Contact;
import com.pentavalue.yousry.firebasechat.models.CurrentUser;
import com.pentavalue.yousry.firebasechat.models.UserModel;
import com.pentavalue.yousry.firebasechat.models.UserSelectedModel;
import com.pentavalue.yousry.firebasechat.util.DatabaseRefs;
import com.pentavalue.yousry.firebasechat.util.Util;
import com.pentavalue.yousry.firebasechat.views.LoadingDialog;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class GroupChatFragment extends FragmentActivity {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final String TAG = GroupChatFragment.class.getSimpleName();
    private static final int IMAGE_GALLERY_REQUEST = 1;

    @BindView(R.id.createBtn)
    TextView createTextView;

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
    @BindDrawable(R.drawable.icon_error)
    Drawable drawableError;
    List<UserSelectedModel> modelList;
    List<Contact> chatContacts;
    List<Contact> contactList;
    UserListAdapter adapter;
    Chat chat;
    private Bitmap bitmap;
    private Uri selectedImageUri;
    private boolean isImageSelected;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private LoadingDialog dialog;

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

        isImageSelected = false;
        chat = new Chat();
        dialog = new LoadingDialog(this);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        chatContacts = new ArrayList<>();
        contactList = new ArrayList<>();
        modelList = new ArrayList<>();
        showContacts();

    }

    @OnClick(R.id.createBtn)
    void onCreateButtonClick() {
        if (!isImageSelected) {
            Toast.makeText(this, "You must select Image for this Group.", Toast.LENGTH_SHORT).show();
            imageView.setImageDrawable(drawableError);
            return;
        }
        if(nameEditText.getText() ==null || nameEditText.getText().toString().isEmpty()){
            Toast.makeText(this, "You must enter name of this Group.", Toast.LENGTH_SHORT).show();
            nameEditText.setError("Fill this Field, please.");
            return;
        }
        StorageReference storageRef = storage.getReferenceFromUrl(Util.URL_STORAGE_REFERENCE).child(Util.FOLDER_STORAGE_IMG);
        sendFileFirebase(storageRef,selectedImageUri);



    }

    @OnClick(R.id.take_imageView_group)
    void onSelectImageButtonClick() {
        photoGalleryIntent();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == IMAGE_GALLERY_REQUEST) {
            if (resultCode == RESULT_OK) {
                selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    // We need to recyle unused bitmaps
                    if (bitmap != null) {
                        bitmap.recycle();
                    }
                    InputStream stream = null;
                    try {
                        stream = getContentResolver().openInputStream(
                                data.getData());
                        bitmap = BitmapFactory.decodeStream(stream);
                        if (stream != null) {
                            stream.close();
                        }
                        isImageSelected = true;
                        imageView.setImageBitmap(bitmap);
                    } catch (IOException | RuntimeException e) {
                        Log.e(TAG,e.getMessage());
                    }
                }
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void sendFileFirebase(StorageReference storageReference, final Uri file) {
        if (storageReference != null) {
            dialog.showProgressDialog("Send Image", "Loading...", false);
            final String name = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
            StorageReference imageGalleryRef = storageReference.child(name + "_gallery");
            UploadTask uploadTask = imageGalleryRef.putFile(file);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure sendFileFirebase " + e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i(TAG, "onSuccess sendFileFirebase");
                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    chat.setChatImage(downloadUrl.toString());
                    chat.setGroup(true);
                    chat.setConversationName(nameEditText.getText().toString());
                    chat.setDateCreated(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime()));
                    chat.setMembers(adapter.getUserSelectedModels());
                    chat.addMember(CurrentUser.getInstance().getUserModel().getId());
                    chat.setGroupAdmin(CurrentUser.getInstance().getUserModel().getId());
                    String key =DatabaseRefs.mChatsDatabaseReference.push().getKey();
                    chat.setId(key);
                    DatabaseRefs.mChatsDatabaseReference.child(chat.getId()).setValue(chat);
                    createFirstTypingNode(chat);
                    dialog.hideProgressDialog();
                    finish();


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    dialog.hideProgressDialog();
                }
            });
        } else {
            //IS NULL

        }

    }

    private void createFirstTypingNode(Chat chat) {

        if (chat == null) {
            Toast.makeText(getApplicationContext(), "There is a problem, please report it", Toast.LENGTH_LONG).show();
            return;
        }
        Log.v(FullScreenImageActivity.class.getSimpleName(), "Set Child Typing");

        for (String s : chat.getMembers()) {
            DatabaseRefs.mTypingDatabaseReference.child(chat.getId()).child(s).setValue(false);
        }


    }

    private void photoGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), IMAGE_GALLERY_REQUEST);
    }

    void fillUserModels() {
        if (chatContacts.size() >= 0) {
            for (int i = 0; i < chatContacts.size(); i++) {
                modelList.add(
                        new UserSelectedModel(
                                chatContacts.get(i).getUserModel().getName(),
                                chatContacts.get(i).getUserModel().getImageUrl(),
                                chatContacts.get(i).getUserModel().getId(),
                                false
                        )
                );
            }
            adapter = new UserListAdapter(modelList, this, new UserListAdapter.OnItemCheckListener() {
                @Override
                public void onItemCheck(UserListHolder holder, int pos) {
                    adapter.getItemModelList(pos).setSelected(true);
                    holder.setNameTextViewColor(GroupChatFragment.this, 0);
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
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showContacts() {
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

    public List<Contact> ReadAllContacts() {
        List<Contact> contacts = new ArrayList<>();

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                Contact contact = new Contact();
                contact.setContact_id(cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID)));
                contact.setContact_name(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));

                if (Integer.parseInt(cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{contact.getContact_id()}, null);
                    while (pCur.moveToNext()) {

                        contact.setPhone_type(pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)));
                        contact.setPhone_number(pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER)));
                        if (contact.getPhone_type() == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
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
