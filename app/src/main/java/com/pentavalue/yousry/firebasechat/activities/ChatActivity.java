package com.pentavalue.yousry.firebasechat.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dewarder.holdinglibrary.HoldingButtonLayout;
import com.dewarder.holdinglibrary.HoldingButtonLayoutListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pentavalue.yousry.firebasechat.R;
import com.pentavalue.yousry.firebasechat.activities.test.FullScreenImageActivity;
import com.pentavalue.yousry.firebasechat.adapters.ConversationAdapter;
import com.pentavalue.yousry.firebasechat.dialogs.CustomDialogClass;
import com.pentavalue.yousry.firebasechat.interfaces.ClickListenerChatFirebase;
import com.pentavalue.yousry.firebasechat.models.Chat;
import com.pentavalue.yousry.firebasechat.models.Contact;
import com.pentavalue.yousry.firebasechat.models.CurrentUser;
import com.pentavalue.yousry.firebasechat.models.Message;
import com.pentavalue.yousry.firebasechat.models.UserModel;
import com.pentavalue.yousry.firebasechat.util.DatabaseRefs;
import com.pentavalue.yousry.firebasechat.util.Logs;
import com.pentavalue.yousry.firebasechat.util.Util;
import com.pentavalue.yousry.firebasechat.views.LoadingDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher, HoldingButtonLayoutListener
,GoogleApiClient.OnConnectionFailedListener, ClickListenerChatFirebase{
    // static Variables
    public static final String TAG = ChatActivity.class.getSimpleName();
    private static final int IMAGE_GALLERY_REQUEST = 1;
    private static final int IMAGE_CAMERA_REQUEST = 2;
    private static final int PLACE_PICKER_REQUEST = 3;
    // Voice Reco (RECORD_AUDIO)
    private static final java.text.DateFormat mFormatter = new SimpleDateFormat("mm:ss:SS");
    private static final float SLIDE_TO_CANCEL_ALPHA_MULTIPLIER = 2.5f;
    private static final long TIME_INVALIDATION_FREQUENCY = 50L;
    // Selection Mode
    static List<Message> selectedMessages;
    static Button notifCount;
    static int mNotifCount = 0;
    private static String mFileName = null;
    ActionMode mActionMode;
    Menu context_menu;
    boolean isGroup = false;
    //Listeners
    private OnGroupListener onGroupListener;
    private OnChatDataChanged onChatDataChanged;
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
    private Menu mOptionsMenu;
    private TextView typingTextView;
    private Toolbar action_toolbar;
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
    private String mNameOfFile = "";
    private MediaRecorder mRecorder;
    private HoldingButtonLayout mHoldingButtonLayout;
    private TextView mTime;
    private RelativeLayout mInput;
    private View mSlideToCancel;
    private int mAnimationDuration;
    private ViewPropertyAnimator mTimeAnimator;
    private ViewPropertyAnimator mSlideToCancelAnimator;
    private ViewPropertyAnimator mInputAnimator;
    private long mStartTime;
    private Runnable mTimerRunnable;
    private File audioFile;
    private MediaPlayer mPlayer;
    private Handler seekHandler = new Handler();
    private ActionMode.Callback mActionModeCallback =new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_multi_select, menu);
            View count = menu.findItem(R.id.action_select).getActionView();
            notifCount = (Button) count.findViewById(R.id.notif_count);

            context_menu = menu;
            return true;
        }



        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_trash:
                    Toast.makeText(getApplicationContext(),""+selectedMessages.size(),Toast.LENGTH_SHORT).show();
                    //alertDialogHelper.showAlertDialog("","Delete Contact","DELETE","CANCEL",1,false);
                    return true;
                case R.id.action_forward:
                    Intent intent =new Intent(ChatActivity.this , ForwardActivity.class);
                    startActivityForResult(intent, 1212);
                    return true;
                case R.id.action_copy:
                    String text ="";
                    for( Message message :selectedMessages){
                        if(message.getType().equals(Util.MESSAGE_TYPES.TEXT_TYPE.name()) ){
                            text += "{ "+message.getText()+" }\n";
                        }

                    }
                    ClipboardManager cm = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                    ClipData cData = ClipData.newPlainText("text", text);
                    cm.setPrimaryClip(cData);
                    mActionModeCallback.onDestroyActionMode(mActionMode);
                    Toast.makeText(getApplicationContext(),"Copied", Toast.LENGTH_SHORT).show();

                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if(selectedMessages.size() >= 0){
                selectedMessages.clear();
                loadMessagesFirebase(chat);

            }
            mode.finish();

            //Toast.makeText(getApplicationContext(),"Destory",Toast.LENGTH_SHORT).show();
            mActionMode = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_MODE_OVERLAY);
        setContentView(R.layout.activity_chat);
        onGroupListener =null;
        // Record to the external cache directory for visibility
        mFileName = getCacheDir().getAbsolutePath();
        //mFileName += "/audiorecordtest.3gp";
        File path = new File(mFileName);
        if(!path.exists())
            path.mkdir();
        try {
            audioFile = File.createTempFile("audiorecordtest", "3gp", path);
        } catch (IOException e) {
//          throw new RuntimeException(
//                  "Couldn't create recording audio file", e);
        }

        this.selectedMessages =new ArrayList<>();

        mNameOfFile = "audiorecordtest.3gp";
        dialog = new LoadingDialog(this);
        bindViews();
        setChatModel();
    }

    public void setOnChatDataChanged(OnChatDataChanged onChatDataChanged) {
        this.onChatDataChanged = onChatDataChanged;
    }

    private void setChatModel() {

        currentUser = CurrentUser.getInstance().getUserModel().getId();
        if(currentUser == null){
            return;
        }
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
                    setOnChatDataChanged(new OnChatDataChanged() {
                        @Override
                        public void removeItemChat(boolean remove) {
                            if(remove){
                                if(CurrentUser.getInstance().getUserModel().getId().equals(chat.getGroupAdmin())){
                                    DatabaseRefs.mChatsDatabaseReference.child(chat.getId()).removeValue();
                                    finish();
                                }else{
                                    DatabaseRefs.mChatsDatabaseReference.child(chat.getId()).runTransaction(new Transaction.Handler() {
                                        @Override
                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                            Chat chat1 = mutableData.getValue(Chat.class);
                                            chat1.removeMember(CurrentUser.getInstance().getUserModel().getId());
                                            mutableData.setValue(chat1);
                                            return Transaction.success(mutableData);
                                        }

                                        @Override
                                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                                        }
                                    });
                                    finish();
                                }
                            }
                        }

                        @Override
                        public void addItemChat(boolean add) {

                        }
                    });
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

    private void setToolbarViews(Contact contact) {
        title.setText(contact.getUserModel().getName());

        try {
            Glide.with(ChatActivity.this)
                    .load(contact.getUserModel().getImageUrl())
                    .into(imageProfile);
        } catch (IllegalArgumentException ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setToolbarViews(final Chat chat) {
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

        //final boolean is_group =chat.isGroup();
        if(chat.isGroup()){

            title.setText(chat.getConversationName());
            Glide.with(getApplicationContext())
                    .load(chat.getChatImage())
                    .into(imageProfile);

            onUpdateMenu(new OnGroupListener() {
                @Override
                public Menu onGroupListener(Menu menu) {
                    menu.getItem(2).setVisible(true);
                    return menu;
                }
            });
            return;
        }
        onUpdateMenu(new OnGroupListener() {
            @Override
            public Menu onGroupListener(Menu menu) {
                menu.getItem(2).setVisible(false);
                return menu;
            }
        });
        //this.onGroupListener.onGroupListener(false);

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

    private void loadMessagesFirebase(Chat chat) {
        Log.v(TAG, "Load Messages Firebase from " + chat.getId());
        mFirebaseDatabaseReference = DatabaseRefs.mMessagesDatabaseReference.child(chat.getId());
        final ConversationAdapter firebaseAdapter =
                new ConversationAdapter(mFirebaseDatabaseReference, ChatActivity.this, currentUser, chat, this);

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

    public void onUpdateMenu( OnGroupListener onGroupListener) {
        this.onGroupListener =onGroupListener;
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

    private void locationPlacesIntent() {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private void photoGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), IMAGE_GALLERY_REQUEST);
    }

    private void sendMessageFirebase() {
        final Message message = new Message();
        message.setSenderID(currentUser);
        message.setText(edMessage.getText().toString());
        message.setTime(String.valueOf(Calendar.getInstance().getTimeInMillis()));
        message.setType(Util.MESSAGE_TYPES.TEXT_TYPE.name());


        // send first message
        if (getIntent().hasExtra(Util.FIRST_TIME_KEY)) {
            if (getIntent().getBooleanExtra(Util.FIRST_TIME_KEY, false)) {
                if (contact != null) {
                    chatID = contact.getChatID();
                    chat.setLastMessage(message.getText());
                    chat.setTimeLastMessage(message.getTime());
                    DatabaseRefs.mChatsDatabaseReference.child(chatID).setValue(chat);
                    //message.setId(chat.getId());

                    DatabaseReference ref =DatabaseRefs.mMessagesDatabaseReference.child(chat.getId()).push();
                    message.setId(ref.getKey());
                    ref.setValue(message);
                    if (chatID != null) Log.v(TAG, "Chat Tag ID from Contact :" + chatID);
                    createFirstTypingNode(chat);
                }
            }
        } else {
            DatabaseReference ref =DatabaseRefs.mMessagesDatabaseReference.child(chat.getId()).push();
            message.setId(ref.getKey());
            ref.setValue(message);
            DatabaseRefs.mChatsDatabaseReference.child(chat.getId()).runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    Chat chat =mutableData.getValue(Chat.class);
                    chat.setTimeLastMessage(message.getTime());
                    chat.setLastMessage(message.getText());
                    mutableData.setValue(chat);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                }
            });
        }
        mLinearLayoutManager.setStackFromEnd(true);
        //rvListMessage.scrollToPosition(firebaseAdapter.getItemCount()-1);
        rvListMessage.setLayoutManager(mLinearLayoutManager);
        edMessage.setText(null);
    }

    private void sendImageFileFirebase(StorageReference storageReference, final Uri file) {
        if (storageReference != null) {
            dialog.showProgressDialog("Send Image", "Loading...", false);
            //final String name = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString() + "_messageImage";
            StorageReference imageGalleryRef = DatabaseRefs.nImageOfMessagesStorage.child(Util.getNameOfImageMessage());
            UploadTask uploadTask = imageGalleryRef.putFile(file);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure sendImageFileFirebase " + e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i(TAG, "onSuccess sendImageFileFirebase");
                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Message message = new Message(downloadUrl.toString(), CurrentUser.getInstance().getUserModel().getId(),
                            String.valueOf(Calendar.getInstance().getTimeInMillis()), Util.MESSAGE_TYPES.IMAGE_TYPE.name());
                    DatabaseReference ref =DatabaseRefs.mMessagesDatabaseReference.child(chat.getId()).push();
                    message.setId(ref.getKey());
                    ref.setValue(message);
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

    private void sendAudioFileMessageFirebase(String File){
        Log.v(TAG, File);
        UploadTask uploadTask;

        dialog.showProgressDialog("Audio Uploading","Loading ...",false);
        final String nameOfAudio = Util.getNameOfAudio();
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("video/3gpp")
                .setCustomMetadata("Time",String.valueOf(Calendar.getInstance().getTime().getTime()))
                .build();
        StorageReference audiosRef = DatabaseRefs.nAudioStorage.child(nameOfAudio);
        java.io.File file = new File(File);

        Uri fileUri = Uri.fromFile(file);

        uploadTask = audiosRef.putFile(fileUri, metadata);
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.e(TAG, "OnFailure :" + exception.getMessage().toString());
                dialog.hideProgressDialog();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                final String downloadUrl = taskSnapshot.getMetadata().getDownloadUrl().toString();
                Logs.LogV(TAG,taskSnapshot.getMetadata().getPath());
                Logs.LogV(TAG,taskSnapshot.getDownloadUrl().toString());
                Logs.LogV(TAG,taskSnapshot.getStorage().getPath());
                Logs.LogV(TAG,taskSnapshot.getStorage().getName());
                Logs.LogV(TAG,taskSnapshot.getStorage().toString());

                //taskSnapshot.getStorage().getPath();
                Message message=new Message();
                message.setAudio(taskSnapshot.getStorage().getPath());
                message.setType(Util.MESSAGE_TYPES.AUDIO_TYPE.name());
                message.setSenderID(CurrentUser.getInstance().getUserModel().getId());
                message.setTime(String.valueOf(Calendar.getInstance().getTimeInMillis()));

                DatabaseReference ref =DatabaseRefs.mMessagesDatabaseReference.child(chatID).push();
                message.setId(ref.getKey());
                ref.setValue(message);


                Log.v(TAG, "OnSuccess :" + downloadUrl);
                dialog.hideProgressDialog();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.v(TAG, "OnProgress :" + progress);

            }
        });
    }

    private void sendLocationMessageFirebase( Intent data){
        Place place = PlacePicker.getPlace(this, data);
        if (place != null) {
            LatLng latLng = place.getLatLng();
            Message message = new Message(latLng.latitude, latLng.longitude, CurrentUser.getInstance().getUserModel().getId(),
                    Util.MESSAGE_TYPES.MAP_TYPE.name(), String.valueOf(Calendar.getInstance().getTimeInMillis()));
            DatabaseReference ref =DatabaseRefs.mMessagesDatabaseReference.child(chatID).push();
            message.setId(ref.getKey());
            ref.setValue(message);
        } else {
            //PLACE IS NULL
            Toast.makeText(getApplicationContext(), "Location Message is not sent",Toast.LENGTH_SHORT).show();
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

    private void invalidateTimer() {
        mTimerRunnable = new Runnable() {
            @Override
            public void run() {
                mTime.setText(getFormattedTime());
                invalidateTimer();
            }
        };

        mTime.postDelayed(mTimerRunnable, TIME_INVALIDATION_FREQUENCY);
    }

    private void stopTimer() {
        if (mTimerRunnable != null) {
            mTime.getHandler().removeCallbacks(mTimerRunnable);
        }
    }

    private void cancelAllAnimations() {
        if (mInputAnimator != null) {
            mInputAnimator.cancel();
        }

        if (mSlideToCancelAnimator != null) {
            mSlideToCancelAnimator.cancel();
        }

        if (mTimeAnimator != null) {
            mTimeAnimator.cancel();
        }
    }

    private String getFormattedTime() {
        return mFormatter.format(new Date(System.currentTimeMillis() - mStartTime));
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();

        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);


        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName + "/" + mNameOfFile);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        try{
            mRecorder.stop();
            Toast.makeText(this, "Action submitted! Time " + getFormattedTime(), Toast.LENGTH_SHORT).show();
            sendAudioFileMessageFirebase(mFileName + "/"+ mNameOfFile);
        } catch (RuntimeException e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        mRecorder.release();
        mRecorder = null;
    }

    private void removeItemMessage(Message message){
        for (Iterator<Message> iter = selectedMessages.listIterator(); iter.hasNext(); ) {
            Message a = iter.next();
            if (message.getId().equals(a.getId())) {
                iter.remove();
                break;
            }
        }
    }

    private void onRemoveGroup(){

        onChatDataChanged.removeItemChat(true);
    }

    private void onAddMemberOnGroup(){


        onChatDataChanged.addItemChat(true);
    }

    private void bindViews() {
        action_toolbar = (Toolbar) findViewById(R.id.action_toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mSetSupportActionToolbar(toolbar);
        imageProfile = (CircleImageView) findViewById(R.id.imageProfile);
        title = (TextView) findViewById(R.id.title);
        //setSupportActionBar(toolbar);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContiner);
        contentRoot = findViewById(R.id.contentRoot);
        edMessage = (EmojiconEditText) findViewById(R.id.editTextMessage);
        btSendMessage = (ImageView) findViewById(R.id.buttonMessage);
        wallpaperImage = (ImageView) findViewById(R.id.img);
        btSendMessage.setOnClickListener(this);
        btSendMessage.setEnabled(false);
        btEmoji = (ImageView) findViewById(R.id.buttonEmoji);
        btAttach = (ImageView) findViewById(R.id.buttonAttach);
        emojIcon = new EmojIconActions(this, contentRoot, edMessage, btEmoji);
        emojIcon.ShowEmojIcon();
        rvListMessage = (RecyclerView) findViewById(R.id.messageRecyclerView);
        typingTextView = (TextView) findViewById(R.id.typingTextView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        edMessage.addTextChangedListener(this);
        mHoldingButtonLayout = (HoldingButtonLayout) findViewById(R.id.input_holder);
        mHoldingButtonLayout.addListener(this);

        mTime = (TextView) findViewById(R.id.time);
        mInput = (RelativeLayout) findViewById(R.id.input);
        mSlideToCancel = findViewById(R.id.slide_to_cancel);

        mAnimationDuration = getResources().getInteger(android.R.integer.config_longAnimTime);
        // Configure the refreshing colors
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
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
                if(mActionMode != null){
                    mActionModeCallback.onDestroyActionMode(mActionMode);
                    refreshLayout.setRefreshing(false);
                    return;
                }
                if (chatID == null || chatID.isEmpty()) {
                    refreshLayout.setRefreshing(false);
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //imageProfile =null;
        Glide.clear(imageProfile);
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

        StorageReference storageRef = DatabaseRefs.nImageStorage;

        if (requestCode == IMAGE_GALLERY_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    sendImageFileFirebase(storageRef, selectedImageUri);
                } else {
                    //URI IS NULL
                }
            }
        } else if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                sendLocationMessageFirebase(data);
            }
        }else if(requestCode == 1212){
            if(resultCode == RESULT_OK){
                String result=data.getStringExtra("chat_id");
                for( Message message :selectedMessages){
                    String key =DatabaseRefs.mMessagesDatabaseReference.child(result).push().getKey();
                    message.setId(key);
                    message.setSenderID(CurrentUser.getInstance().getUserModel().getId());
                    DatabaseRefs.mMessagesDatabaseReference.child(result).child(key).setValue(message);
                }
                selectedMessages.clear();
                mActionModeCallback.onDestroyActionMode(mActionMode);

            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
                mActionModeCallback.onDestroyActionMode(mActionMode);

            }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
       // mOptionsMenu =menu;

        if(onGroupListener != null){
            menu = onGroupListener.onGroupListener(menu);
        }
        //;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add_group:
                Toast.makeText(getApplicationContext(),"Adding",Toast.LENGTH_SHORT).show();
                onAddMemberOnGroup();
                break;
            case R.id.action_remove_group:
                Toast.makeText(getApplicationContext(),"Removing",Toast.LENGTH_SHORT).show();
                onRemoveGroup();
                break;
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

    @Override
    public void onBeforeExpand() {
        cancelAllAnimations();

        mSlideToCancel.setTranslationX(0f);
        mSlideToCancel.setAlpha(0f);
        mSlideToCancel.setVisibility(View.VISIBLE);
        mSlideToCancelAnimator = mSlideToCancel.animate().alpha(1f).setDuration(mAnimationDuration);
        mSlideToCancelAnimator.start();

        mInputAnimator = mInput.animate().alpha(0f).setDuration(mAnimationDuration);
        mInputAnimator.setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mInput.setVisibility(View.INVISIBLE);
                mInputAnimator.setListener(null);
            }
        });
        mInputAnimator.start();

        mTime.setTranslationY(mTime.getHeight());
        mTime.setAlpha(0f);
        mTime.setVisibility(View.VISIBLE);
        mTimeAnimator = mTime.animate().translationY(0f).alpha(1f).setDuration(mAnimationDuration);
        mTimeAnimator.start();

    }

    @Override
    public void onExpand() {
        mStartTime = System.currentTimeMillis();
        invalidateTimer();
        startRecording();
    }

    @Override
    public void onBeforeCollapse() {
        cancelAllAnimations();

        mSlideToCancelAnimator = mSlideToCancel.animate().alpha(0f).setDuration(mAnimationDuration);
        mSlideToCancelAnimator.setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mSlideToCancel.setVisibility(View.INVISIBLE);
                mSlideToCancelAnimator.setListener(null);
            }
        });
        mSlideToCancelAnimator.start();

        mInput.setAlpha(0f);
        mInput.setVisibility(View.VISIBLE);
        mInputAnimator = mInput.animate().alpha(1f).setDuration(mAnimationDuration);
        mInputAnimator.start();

        mTimeAnimator = mTime.animate().translationY(mTime.getHeight()).alpha(0f).setDuration(mAnimationDuration);
        mTimeAnimator.setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mTime.setVisibility(View.GONE);
                mTimeAnimator.setListener(null);
            }
        });
        mTimeAnimator.start();
    }

    @Override
    public void onCollapse(boolean isCancel) {
        stopTimer();
        if (isCancel) {

            Toast.makeText(this, "Action canceled! Time " + getFormattedTime(), Toast.LENGTH_SHORT).show();
        } else {
            stopRecording();

        }
    }

    @Override
    public void onOffsetChanged(float offset, boolean isCancel) {
        mSlideToCancel.setTranslationX(-mHoldingButtonLayout.getWidth() * offset);
        mSlideToCancel.setAlpha(1 - SLIDE_TO_CANCEL_ALPHA_MULTIPLIER * offset);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Util.initToast(this,"Google Play Services error.");
    }

    @Override
    public void clickImageMapChat(View view, int position, String latitude, String longitude) {
        // Create a Uri from an intent string. Use the result to create an Intent.
        Uri gmmIntentUri = Uri.parse("geo:"+ latitude + ',' +longitude + "?z=10&q=MyLocation");
        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        // Make the Intent explicit by setting the Google Maps package
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            // Attempt to start an activity that can handle the Intent
            startActivity(mapIntent);
        }
    }

    @Override
    public void clickImageChat(View view, int position, String userId, String urlImage, String time) {
        CustomDialogClass customDialogClass=new CustomDialogClass(ChatActivity.this,userId,urlImage);
        customDialogClass.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        customDialogClass.show();
    }

    @Override
    public void clickItemChat(View view, int position) {

    }

    private void mSetSupportActionToolbar(Toolbar action_toolbar){
        setSupportActionBar(action_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void longClickItemChat(View view, int postion, Message model, boolean isSelected) {
        if(mActionMode == null){
            mActionMode =startActionMode(mActionModeCallback);
        }

        if(isSelected){
            view.setBackground(getResources().getDrawable(R.drawable.shadow_item));
            selectedMessages.add(model);
            notifCount.setText(""+selectedMessages.size());

            invalidateOptionsMenu();
        }else {

            removeItemMessage(model);
            if(selectedMessages.size() ==0 ){

                mActionModeCallback.onDestroyActionMode(mActionMode);
            }
            notifCount.setText(""+selectedMessages.size());
            view.setBackgroundColor(getResources().getColor(R.color.off_white));
        }
    }

    private interface OnGroupListener{
        Menu onGroupListener(Menu menu);
    }


    private interface OnChatDataChanged{
        void removeItemChat(boolean remove);
        void addItemChat(boolean add);
    }

}
