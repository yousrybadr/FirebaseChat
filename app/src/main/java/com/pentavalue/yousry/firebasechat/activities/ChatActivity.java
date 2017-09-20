package com.pentavalue.yousry.firebasechat.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pentavalue.yousry.firebasechat.R;
import com.pentavalue.yousry.firebasechat.adapters.ConversationAdapter;
import com.pentavalue.yousry.firebasechat.models.Chat;
import com.pentavalue.yousry.firebasechat.models.Contact;
import com.pentavalue.yousry.firebasechat.models.CurrentUser;
import com.pentavalue.yousry.firebasechat.models.Message;
import com.pentavalue.yousry.firebasechat.models.UserModel;
import com.pentavalue.yousry.firebasechat.util.DatabaseRefs;
import com.pentavalue.yousry.firebasechat.util.Util;
import com.pentavalue.yousry.firebasechat.views.LoadingDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    // static Variables
    public static final String TAG = ChatActivity.class.getSimpleName();
    private static final int IMAGE_GALLERY_REQUEST = 1;
    private static final int IMAGE_CAMERA_REQUEST = 2;
    private static final int PLACE_PICKER_REQUEST = 3;

    // Firebase

    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    //models Classes

    private Chat chat;
    private Contact contact;
    private LoadingDialog dialog;
    private String currentUser;
    private String chatID;

    //Views UI

    private TextView typingTextView;
    private Toolbar toolbar;
    private CircleImageView imageProfile;
    private TextView title;
    private RecyclerView rvListMessage;
    private LinearLayoutManager mLinearLayoutManager;
    private ImageView btSendMessage, btEmoji, btAttach;
    private ImageView wallpaperImage;
    private EmojiconEditText edMessage;
    private View contentRoot;
    private EmojIconActions emojIcon;
    private SwipeRefreshLayout refreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        dialog = new LoadingDialog(this);
        bindViews();
        setChatModel();
    }

    private void setChatModel() {

        currentUser = CurrentUser.getInstance().getUserModel().getId();
        Intent intent = getIntent();
        // From RecentChat Fragment
        if (intent.hasExtra(Util.CHAT_KEY_MODEL)) {

            contact = null;
            chatID = intent.getStringExtra(Util.CHAT_KEY_MODEL);
            Log.v(TAG, chatID);
            DatabaseRefs.mChatsDatabaseReference.child(chatID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    chat = dataSnapshot.getValue(Chat.class);
                    //setTitle(chat.getConversationName());


                    if (chat == null) {
                        Toast.makeText(getApplicationContext(), "Can not open Chat", Toast.LENGTH_SHORT).show();

                        finish();
                        return;
                    }
                    setToolbarViews(chat);
                    loadMessagesFirebase(chat);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            // From Contacts Fragment
            // First Time for conversation
        } else if (intent.hasExtra(Util.FIRST_TIME_KEY)) {
            if (intent.getBooleanExtra(Util.FIRST_TIME_KEY, false)) {
                contact = (Contact) intent.getSerializableExtra(Util.CONTACT_KEY_MODEL);
                if (contact != null) Log.v(TAG, contact.toString());
                //userID =contact.getUserModel().getId();
                //Log.v(TAG,userID);
                chatID = contact.getChatID();
                chat = createFirstChatNode();
                loadMessagesFirebase(chat);
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!Util.verifyNetworkConnection(this)) {
            Util.initToast(this, getResources().getString(R.string.no_internet_connection));
            FirebaseDatabase.getInstance().goOffline();
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            //finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        typingTextView.setVisibility(View.INVISIBLE);
        if (chat != null || chatID != null) {
            onTypingState();
        }
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                if (chatID == null || chatID.isEmpty()) {
                    return;
                }
                if (Util.verifyNetworkConnection(ChatActivity.this)) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                }
                loadMessagesFirebase(chat);
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sendPhotoGallery:
                photoGalleryIntent();
                break;
            case R.id.sendLocation:
                locationPlacesIntent();
                break;
            case R.id.action_black:
                item.setChecked(true);
                wallpaperImage.setBackgroundColor(getResources().getColor(R.color.blackColor));
                break;
            case R.id.action_red:
                item.setChecked(true);
                wallpaperImage.setBackgroundColor(getResources().getColor(R.color.redColorLight));
                break;
            case R.id.action_gray:
                item.setChecked(true);
                wallpaperImage.setBackgroundColor(getResources().getColor(R.color.dark_gray));

                break;
            case R.id.action_yellow:
                item.setChecked(true);
                wallpaperImage.setBackgroundColor(getResources().getColor(R.color.yellowColorLight));
                break;


        }

        return super.onOptionsItemSelected(item);
    }

    private void photoGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), IMAGE_GALLERY_REQUEST);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void sendMessageFirebase() {
        Message message = new Message();
        message.setSenderID(currentUser);
        message.setText(edMessage.getText().toString());
        message.setTime(String.valueOf(Calendar.getInstance().getTimeInMillis()));
        message.setType("text");

        // send first message
        if (getIntent().hasExtra(Util.FIRST_TIME_KEY)) {
            if (getIntent().getBooleanExtra(Util.FIRST_TIME_KEY, false)) {
                if (contact != null) {
                    chatID = contact.getChatID();
                    DatabaseRefs.mChatsDatabaseReference.child(chatID).setValue(chat);
                    message.setId(chat.getId());
                    DatabaseRefs.mMessagesDatabaseReference.child(chat.getId()).push().setValue(message);
                    if (chatID != null) Log.v(TAG, "Chat Tag ID from Contact :" + chatID);
                    createFirstTypingNode(chat);
                }
            }
        } else {
            message.setId(chat.getId());
            DatabaseRefs.mMessagesDatabaseReference.child(chat.getId()).push().setValue(message);
        }
        mLinearLayoutManager.setStackFromEnd(true);
        //rvListMessage.scrollToPosition(firebaseAdapter.getItemCount()-1);
        rvListMessage.setLayoutManager(mLinearLayoutManager);
        edMessage.setText(null);
    }

    void setToolbarViews(Contact contact) {
        title.setText(contact.getUserModel().getName());

        try {
            Glide.with(ChatActivity.this)
                    .load(contact.getUserModel().getImageUrl())
                    .into(imageProfile);
        } catch (IllegalArgumentException ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    void setToolbarViews(Chat chat) {
        DatabaseReference ref;
        if (chat == null) {
            if (contact != null) {
                setToolbarViews(contact);
                return;
            }
            Toast.makeText(getApplicationContext(), "No Chat", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (chat.getMember(0).equals(CurrentUser.getInstance().getUserModel().getId())) {
            ref = DatabaseRefs.mUsersDatabaseReference.child(chat.getMember(1));
        } else {
            ref = DatabaseRefs.mUsersDatabaseReference.child(chat.getMember(0));
        }
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserModel model = dataSnapshot.getValue(UserModel.class);
                title.setText(model.getName());

                try {
                    Glide.with(ChatActivity.this)
                            .load(model.getImageUrl())
                            .into(imageProfile);
                } catch (IllegalArgumentException ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
        //title.setText(chat.getConversationName());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //imageProfile =null;
        Glide.clear(imageProfile);
    }

    private void loadMessagesFirebase(Chat chat) {
        Log.v(TAG, "Load Messages Firebase from " + chat.getId());
        mFirebaseDatabaseReference = DatabaseRefs.mMessagesDatabaseReference.child(chat.getId());
        final ConversationAdapter firebaseAdapter =
                new ConversationAdapter(mFirebaseDatabaseReference, currentUser, chat);

        firebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = firebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    rvListMessage.scrollToPosition(positionStart);
                }
            }
        });
        rvListMessage.setLayoutManager(mLinearLayoutManager);
        rvListMessage.setAdapter(firebaseAdapter);
    }


    private Chat createFirstChatNode() {
        Chat chat = new Chat();
        chat.addMember(currentUser);
        //userID = contact.getUserModel().getId();
        chat.setId(contact.getChatID());
        chat.addMember(contact.getUserModel().getId());
        chat.setConversationName(contact.getContact_name());
        chat.setGroup(false);
        chat.setDateCreated(new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(Calendar.getInstance().getTime()));
        setToolbarViews(contact);
        return chat;
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


    private void bindViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        imageProfile = (CircleImageView) findViewById(R.id.imageProfile);
        title = (TextView) findViewById(R.id.title);
        //setSupportActionBar(toolbar);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContiner);
        contentRoot = findViewById(R.id.contentRoot);
        edMessage = (EmojiconEditText) findViewById(R.id.editTextMessage);
        btSendMessage = (ImageView) findViewById(R.id.buttonMessage);
        wallpaperImage = (ImageView) findViewById(R.id.img);
        btSendMessage.setOnClickListener(this);
        btEmoji = (ImageView) findViewById(R.id.buttonEmoji);
        btAttach = (ImageView) findViewById(R.id.buttonAttach);
        emojIcon = new EmojIconActions(this, contentRoot, edMessage, btEmoji);
        emojIcon.ShowEmojIcon();
        rvListMessage = (RecyclerView) findViewById(R.id.messageRecyclerView);
        typingTextView = (TextView) findViewById(R.id.typingTextView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        edMessage.addTextChangedListener(this);
        // Configure the refreshing colors
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        this.finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.buttonMessage:
                sendMessageFirebase();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        StorageReference storageRef = storage.getReferenceFromUrl(Util.URL_STORAGE_REFERENCE).child(Util.FOLDER_STORAGE_IMG);

        if (requestCode == IMAGE_GALLERY_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    sendFileFirebase(storageRef, selectedImageUri);
                } else {
                    //URI IS NULL
                }
            }
        } else if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                if (place != null) {
                    LatLng latLng = place.getLatLng();
                    Message message = new Message(latLng.latitude, latLng.longitude, CurrentUser.getInstance().getUserModel().getId(),
                            "map", String.valueOf(Calendar.getInstance().getTimeInMillis()));
                    DatabaseRefs.mMessagesDatabaseReference.child(chatID).push().setValue(message);
                } else {
                    //PLACE IS NULL
                }
            }
        }

    }

    private void locationPlacesIntent() {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
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
                    Message message = new Message(downloadUrl.toString(), CurrentUser.getInstance().getUserModel().getId(),
                            String.valueOf(Calendar.getInstance().getTimeInMillis()), "image");
                    DatabaseRefs.mMessagesDatabaseReference.child(chat.getId()).push().setValue(message);
                    dialog.hideProgressDialog();

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


    private void onTypingState() {
        try {
            DatabaseRefs.mTypingDatabaseReference.child(chatID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = snapshot.getKey();


                        final boolean userTyping = snapshot.getValue(boolean.class);
                        if (key == null) {
                            Log.v(TAG, "Object UserTyping is null");
                        } else {

                            Log.v(TAG, "Key is " + key);
                            if (!key.equals(CurrentUser.getInstance().getUserModel().getId())) {
                                if (userTyping) {
                                    typingTextView.setVisibility(View.VISIBLE);
                                    break;
                                } else {
                                    typingTextView.setVisibility(View.INVISIBLE);
                                }
                            }


                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.v(TAG, "Canceling onDataChange Typing State");

                }
            });
        } catch (RuntimeException ex) {
            //Toast.makeText(getApplicationContext(),ex.getMessage(),Toast.LENGTH_SHORT).show();
            Log.e(TAG, ex.getMessage());
        }
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Log.v(FullScreenImageActivity.class.getSimpleName(), "Start Typing Transaction");

        try {
            if (charSequence.length() > 0) {
                btSendMessage.setEnabled(true);
                DatabaseRefs.mTypingDatabaseReference.child(chatID).child(CurrentUser.getInstance().getUserModel().getId()).runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        boolean typing;
                        typing = true;
                        mutableData.setValue(typing);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        Log.v(FullScreenImageActivity.class.getSimpleName(), "Complete Transaction");

                    }
                });
            } else {
                btSendMessage.setEnabled(false);
                DatabaseRefs.mTypingDatabaseReference.child(chatID).child(CurrentUser.getInstance().getUserModel().getId()).runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        boolean typing;
                        typing = false;
                        mutableData.setValue(typing);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        Log.v(FullScreenImageActivity.class.getSimpleName(), "Complete Transaction");

                    }
                });
            }
        } catch (RuntimeException ex) {
            //Toast.makeText(getApplicationContext(),ex.getMessage(),Toast.LENGTH_SHORT).show();
            Log.e(TAG, ex.getMessage());

        }


    }

    @Override
    public void afterTextChanged(Editable editable) {
        //Log.v(FullScreenImageActivity.class.getSimpleName(),"Start Transaction");


    }
}
