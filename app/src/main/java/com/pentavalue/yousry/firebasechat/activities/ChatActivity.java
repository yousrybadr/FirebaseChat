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
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pentavalue.yousry.firebasechat.R;
import com.pentavalue.yousry.firebasechat.adapters.ChatFirebaseAdapter;
import com.pentavalue.yousry.firebasechat.interfaces.ClickListenerChatFirebase;
import com.pentavalue.yousry.firebasechat.models.Chat;
import com.pentavalue.yousry.firebasechat.models.Contact;
import com.pentavalue.yousry.firebasechat.models.CurrentUser;
import com.pentavalue.yousry.firebasechat.models.Message;
import com.pentavalue.yousry.firebasechat.models.MessageModel;
import com.pentavalue.yousry.firebasechat.models.PrivateRoomChat;
import com.pentavalue.yousry.firebasechat.models.UserModel;
import com.pentavalue.yousry.firebasechat.util.DatabaseRefs;
import com.pentavalue.yousry.firebasechat.util.Util;
import com.pentavalue.yousry.firebasechat.views.LoadingDialog;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher{

    static final String TAG = ChatActivity.class.getSimpleName();
    private static final int IMAGE_GALLERY_REQUEST = 1;
    private static final int IMAGE_CAMERA_REQUEST = 2;
    private static final int PLACE_PICKER_REQUEST = 3;


    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    //Views UI

    private TextView typingTextView;
    private Toolbar toolbar;
    private CircleImageView imageProfile;
    private TextView title;
    private RecyclerView rvListMessage;
    private LinearLayoutManager mLinearLayoutManager;
    private ImageView btSendMessage,btEmoji;
    private EmojiconEditText edMessage;
    private View contentRoot;
    private EmojIconActions emojIcon;
    private SwipeRefreshLayout refreshLayout;
    Chat chat;
    Contact contact;

    LoadingDialog dialog;
    private String currentUser;

    DatabaseReference reference;
    String chatID;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        imageProfile = (CircleImageView) findViewById(R.id.imageProfile);
        title = (TextView) findViewById(R.id.title);

        //setSupportActionBar(toolbar);


        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContiner);


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                if(chatID ==null || chatID.isEmpty()){
                    return;
                }
                if(Util.verifyNetworkConnection(ChatActivity.this)){
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.no_internet_connection),Toast.LENGTH_LONG).show();
                }
                loadMessagesFirebase(chat);
                refreshLayout.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //getSupportActionBar().hide();
        dialog =new LoadingDialog(ChatActivity.this);
        bindViews();



        if (!Util.verifyNetworkConnection(this)){
            Util.initToast(this,getResources().getString(R.string.no_internet_connection));
            FirebaseDatabase.getInstance().goOffline();
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            //finish();
        }


        setChatModel();



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.sendPhotoGallery:
                photoGalleryIntent();
                break;
            case R.id.sendLocation:
                locationPlacesIntent();
                break;


        }

        return super.onOptionsItemSelected(item);
    }

    private void photoGalleryIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), IMAGE_GALLERY_REQUEST);
    }

    private void sendMessageFirebase(){
        Message message =new Message();
        message.setSenderID(currentUser);
        message.setText(edMessage.getText().toString());
        message.setTime(String.valueOf(Calendar.getInstance().getTimeInMillis()));
        message.setType("text");
        message.setId(chat.getId());
        DatabaseRefs.mMessagesDatabaseReference.child(chat.getId()).push().setValue(message);
        if(contact != null){
            reference.setValue(chat);
        }
        mLinearLayoutManager.setStackFromEnd(true);

        rvListMessage.setLayoutManager(mLinearLayoutManager);

        edMessage.setText(null);
    }

    void setToolbarViews(){
        DatabaseReference ref;
        if(chat.getMember(0).equals(CurrentUser.getInstance().getUserModel().getId())){
            ref = DatabaseRefs.mUsersDatabaseReference.child(chat.getMember(1));
        }else{
            ref = DatabaseRefs.mUsersDatabaseReference.child(chat.getMember(0));
        }

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserModel model =dataSnapshot.getValue(UserModel.class);
                title.setText(model.getName());

                try {
                    Glide.with(ChatActivity.this)
                        .load(model.getImageUrl())
                        .into(imageProfile);
                }catch (IllegalArgumentException ex){
                    Toast.makeText(getApplicationContext(),ex.getMessage(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();

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

    private void loadMessagesFirebase(Chat chat){
        Log.v(TAG,"Load Messages Firebase from "+chat.getId());
       mFirebaseDatabaseReference = DatabaseRefs.mMessagesDatabaseReference.child(chat.getId());
        final ChatFirebaseAdapter firebaseAdapter =
                new ChatFirebaseAdapter(mFirebaseDatabaseReference, currentUser, chat);
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

    private void setChatModel(){

        currentUser = CurrentUser.getInstance().getUserModel().getId();
        // From RecentChat Fragment
        if(getIntent().hasExtra(Util.CHAT_KEY_MODEL)){

            contact =null;
            chatID= getIntent().getStringExtra(Util.CHAT_KEY_MODEL);
            Log.v(TAG,chatID);


            DatabaseRefs.mChatsDatabaseReference.child(chatID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    chat=  dataSnapshot.getValue(Chat.class);
                    //setTitle(chat.getConversationName());
                    setToolbarViews();

                    loadMessagesFirebase(chat);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            // From Contacts Fragment
        }else if(getIntent().hasExtra(Util.CONTACT_KEY_MODEL)){
            contact = (Contact) getIntent().getSerializableExtra(Util.CONTACT_KEY_MODEL);
            if(contact != null) Log.v(TAG,contact.toString());
            userID =contact.getUserId();
            Log.v(TAG,userID);
            chat = new Chat();
            chat.addMember(currentUser);
            userID = contact.getUserId();
            chat.addMember(contact.getUserId());
            chat.setConversationName(contact.getContact_name());
            chat.setGroup(false);
            chat.setDateCreated(new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(Calendar.getInstance().getTime()));
            setToolbarViews();

            loadMessagesFirebase(chat);

            reference = DatabaseRefs.mChatsDatabaseReference.push();


            chatID = reference.getKey();
            chat.setId(chatID);
            if(chatID != null ) Log.v(TAG,"Chat Tag ID from Contact :" + chatID);
        }
    }

    private void bindViews(){
        contentRoot = findViewById(R.id.contentRoot);
        edMessage = (EmojiconEditText)findViewById(R.id.editTextMessage);
        btSendMessage = (ImageView)findViewById(R.id.buttonMessage);
        btSendMessage.setOnClickListener(this);
        btEmoji = (ImageView)findViewById(R.id.buttonEmoji);
        emojIcon = new EmojIconActions(this,contentRoot,edMessage,btEmoji);
        emojIcon.ShowEmojIcon();
        rvListMessage = (RecyclerView)findViewById(R.id.messageRecyclerView);
        typingTextView = (TextView) findViewById(R.id.typingTextView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        edMessage.addTextChangedListener(this);
    }

    @Override
    public void onClick(View view) {
        int id =view.getId();
        switch (id){
            case R.id.buttonMessage:
                if(edMessage.getText().toString().isEmpty() || edMessage.getText() ==null){
                    btSendMessage.setEnabled(false);
                    return;
                }else {
                    btSendMessage.setEnabled(true);
                }
                sendMessageFirebase();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        StorageReference storageRef = storage.getReferenceFromUrl(Util.URL_STORAGE_REFERENCE).child(Util.FOLDER_STORAGE_IMG);

        if (requestCode == IMAGE_GALLERY_REQUEST){
            if (resultCode == RESULT_OK){
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null){
                    sendFileFirebase(storageRef,selectedImageUri);
                }else{
                    //URI IS NULL
                }
            }
        }else if (requestCode == PLACE_PICKER_REQUEST){
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                if (place!=null){
                    LatLng latLng = place.getLatLng();
                    Message message =new Message(latLng.latitude,latLng.longitude,CurrentUser.getInstance().getUserModel().getId(),
                            "map",String.valueOf(Calendar.getInstance().getTimeInMillis()));
                    DatabaseRefs.mMessagesDatabaseReference.child(chatID).push().setValue(message);
                }else{
                    //PLACE IS NULL
                }
            }
        }

    }

    private void locationPlacesIntent(){
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private void sendFileFirebase(StorageReference storageReference, final Uri file){
        if (storageReference != null){
            dialog.showProgressDialog("Send Image","Loading...",false);
            final String name = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
            StorageReference imageGalleryRef = storageReference.child(name+"_gallery");
            UploadTask uploadTask = imageGalleryRef.putFile(file);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG,"onFailure sendFileFirebase "+e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i(TAG,"onSuccess sendFileFirebase");
                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Message message =new Message(downloadUrl.toString(),CurrentUser.getInstance().getUserModel().getId(),
                            String.valueOf(Calendar.getInstance().getTimeInMillis()),"image");
                    DatabaseRefs.mMessagesDatabaseReference.child(chat.getId()).push().setValue(message);
                    dialog.hideProgressDialog();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    dialog.hideProgressDialog();
                }
            });
        }else{
            //IS NULL

        }

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        typingTextView.setVisibility(View.VISIBLE);


    }

    @Override
    public void afterTextChanged(Editable editable) {


        DatabaseRefs.mChatsDatabaseReference.child(chatID).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Chat chat = mutableData.getValue(Chat.class);
                if (chat == null) {
                    return Transaction.success(mutableData);
                }
                //title.setText(chat.getConversationName());
                typingTextView.setVisibility(View.INVISIBLE);

                chat.setTyping(false);
                mutableData.setValue(chat);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });

    }
}
