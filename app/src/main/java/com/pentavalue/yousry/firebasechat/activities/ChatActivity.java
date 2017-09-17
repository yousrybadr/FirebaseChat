package com.pentavalue.yousry.firebasechat.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    static final String TAG = ChatActivity.class.getSimpleName();
    static final String CHAT_REFERENCE = "chats";


    private DatabaseReference mFirebaseDatabaseReference;


    //Views UI
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
                loadMessagesFirebase(chatID);
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
            //finish();
        }


        setChatModel();
        loadMessagesFirebase(chatID);


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
        edMessage.setText(null);
    }



    void setToolbarViews(){
        title.setText(chat.getConversationName());
        Glide.with(this).load(chat.getWallpaperURL()).into(imageProfile);
    }


    private void loadMessagesFirebase(String id){

        //setToolbarViews();
        Log.v(TAG,"Load Messages Firebase from "+id);

       mFirebaseDatabaseReference = DatabaseRefs.mMessagesDatabaseReference.child(id);
        final ChatFirebaseAdapter firebaseAdapter =
                new ChatFirebaseAdapter(mFirebaseDatabaseReference, currentUser);


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
    void setChatModel(){

        currentUser = CurrentUser.getInstance().getUserModel().getId();
        // From RecentChat Fragment
        if(getIntent().hasExtra(Util.CHAT_KEY_MODEL)){

            contact =null;
            chatID= getIntent().getStringExtra(Util.CHAT_KEY_MODEL);
            Log.v(TAG,chatID);
            DatabaseRefs.mChatsDatabaseReference.child(chatID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    chat = dataSnapshot.getValue(Chat.class);
                    //setTitle(chat.getConversationName());
                    setToolbarViews();

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
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
    }

    @Override
    public void onClick(View view) {
        int id =view.getId();
        switch (id){
            case R.id.buttonMessage:
                sendMessageFirebase();
                break;
            default:
                break;
        }
    }

}
