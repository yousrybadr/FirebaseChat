package com.pentavalue.yousry.firebasechat.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.pentavalue.yousry.firebasechat.R;
import com.pentavalue.yousry.firebasechat.activities.ChatActivity;
import com.pentavalue.yousry.firebasechat.interfaces.ClickListenerChatFirebase;
import com.pentavalue.yousry.firebasechat.models.Chat;
import com.pentavalue.yousry.firebasechat.models.Contact;
import com.pentavalue.yousry.firebasechat.models.Message;
import com.pentavalue.yousry.firebasechat.models.UserModel;
import com.pentavalue.yousry.firebasechat.util.CircleTransform;
import com.pentavalue.yousry.firebasechat.util.DatabaseRefs;
import com.pentavalue.yousry.firebasechat.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.OnClick;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import me.himanshusoni.chatmessageview.ChatMessageView;


/**
 * Created by Alessandro Barreto on 23/06/2016.
 */

public class ConversationAdapter extends FirebaseRecyclerAdapter<Message, ConversationAdapter.MyChatViewHolder> {

    public static final String TAG = ConversationAdapter.class.getSimpleName();
    private static final int RIGHT_MSG = 0;
    private static final int LEFT_MSG = 1;
    private static final int RIGHT_MSG_IMG = 2;
    private static final int LEFT_MSG_IMG = 3;
    private static final int RIGHT_MSG_AUDIO = 4;
    private static final int LEFT_MSG_AUDIO = 5;
    public static int COUNT;
    MediaPlayer mPlayer;
    // private ClickListenerChatFirebase mClickListenerChatFirebase;

    private ClickListenerChatFirebase mClickListenerChatFirebase;

    private String idUser;

    private Chat chat;

    private Context context;


    public ConversationAdapter(DatabaseReference ref, Context context, String mCurrentUser, Chat chat, ClickListenerChatFirebase listenerChatFirebase) {
        super(Message.class, R.layout.item_message_left, ConversationAdapter.MyChatViewHolder.class, ref);

        this.idUser = mCurrentUser;
        this.chat = chat;
        this.context = context;
        Log.v(TAG, "User ID = " + idUser);
        this.COUNT = 0;
        this.mClickListenerChatFirebase = listenerChatFirebase;

    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public MyChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == RIGHT_MSG) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right, parent, false);
            return new MyChatViewHolder(view);
        } else if (viewType == LEFT_MSG) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left, parent, false);
            return new MyChatViewHolder(view);
        } else if (viewType == RIGHT_MSG_IMG) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right_img, parent, false);
            return new MyChatViewHolder(view);
        } else if (viewType == RIGHT_MSG_AUDIO) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right_audio, parent, false);
            return new MyChatViewHolder(view);
        } else if (viewType == LEFT_MSG_AUDIO) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left_audio, parent, false);
            return new MyChatViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left_img, parent, false);
            return new MyChatViewHolder(view);
        }
    }

    @Override
    protected void populateViewHolder(final MyChatViewHolder viewHolder, Message model, int position) {
        //viewHolder.setIvUser("https://firebasestorage.googleapis.com/v0/b/fir-library-81a54.appspot.com/o/images%2F2017-09-07_044310_gallery?alt=media&token=bf392434-ba25-4c6a-b28a-f6546933ed97");
        //viewHolder.setIvChatPhoto("https://firebasestorage.googleapis.com/v0/b/fir-library-81a54.appspot.com/o/images%2F2017-09-07_044310_gallery?alt=media&token=bf392434-ba25-4c6a-b28a-f6546933ed97");
        //viewHolder.loadUserModel(chat);
        viewHolder.loadUserModel(model);
        try {
            viewHolder.setTvTimestamp(model.getTime());
        } catch (NumberFormatException ex) {
            Log.e(TAG, ex.getMessage());
        }

        Util.MESSAGE_TYPES types = Util.MESSAGE_TYPES.valueOf(model.getType());
        switch (types) {
            case AUDIO_TYPE:
                break;
            case FILE_TYPE:
                break;
            case IMAGE_TYPE:
                viewHolder.setIvChatPhoto(model.getPictureURL());
                break;
            case MAP_TYPE:
                viewHolder.tvIsLocation(View.VISIBLE);
                viewHolder.setIvChatPhoto(Util.local(String.valueOf(model.getLatitude()), String.valueOf(model.getLongitude())));
                break;
            case TEXT_TYPE:
                viewHolder.setTxtMessage(model.getText());
                break;
            case VIDEO_TYPE:
                break;
            default:
                break;
        }

    }


    @Override
    public int getItemViewType(int position) {
        Message message = getItem(position);

        if (message.getType().equals(Util.MESSAGE_TYPES.TEXT_TYPE.name())) {
            Log.v(TAG, "Sender ID = " + message.getSenderID());
            if (message.getSenderID().equals(idUser)) {
                return RIGHT_MSG;
            } else {
                return LEFT_MSG;
            }
        } else if (message.getType().equals(Util.MESSAGE_TYPES.IMAGE_TYPE.name())) {
            Log.v(TAG, "Sender ID = " + message.getSenderID());
            if (message.getSenderID().equals(idUser)) {
                return RIGHT_MSG_IMG;
            } else {
                return LEFT_MSG_IMG;
            }
        } else if (message.getType().equals(Util.MESSAGE_TYPES.MAP_TYPE.name())) {
            if (message.getSenderID().equals(idUser)) {
                return RIGHT_MSG_IMG;
            } else {
                return LEFT_MSG_IMG;
            }
        } else if (message.getType().equals(Util.MESSAGE_TYPES.AUDIO_TYPE.name())) {
            if (message.getSenderID().equals(idUser)) {
                return RIGHT_MSG_AUDIO;
            } else {
                return LEFT_MSG_AUDIO;
            }
        } else {
            return LEFT_MSG;
        }

    }

    private CharSequence convertTimestamp(String mSec) {
        return DateUtils.getRelativeTimeSpanString(Long.parseLong(mSec), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
    }


/*
    public int searchOnUser(String id,List<String> members){
        int result =0;
        for(int i =0;i<members.size();i++){
            if(members.get(i).equals(id)){
                result=i;
                break;
            }
        }
        return result;
    }
*/

    public class MyChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {

        TextView tvTimestamp, tvLocation, tvDuration;
        ImageButton playButton;
        ProgressBar bar;
        EmojiconTextView txtMessage;
        ImageView ivUser, ivChatPhoto;
        LinearLayout audioViewLayout;
        ChatMessageView mRoot;
        SeekBar seekBar;
        private boolean checker = true;
        private boolean isSelected;

        public MyChatViewHolder(View itemView) {
            super(itemView);
            mRoot = (ChatMessageView) itemView.findViewById(R.id.contentMessageChat);
            tvTimestamp = (TextView) itemView.findViewById(R.id.timestamp);
            txtMessage = (EmojiconTextView) itemView.findViewById(R.id.txtMessage);
            tvLocation = (TextView) itemView.findViewById(R.id.tvLocation);
            ivChatPhoto = (ImageView) itemView.findViewById(R.id.img_chat);
            ivUser = (ImageView) itemView.findViewById(R.id.ivUserChat);
            playButton = (ImageButton) itemView.findViewById(R.id.play_pause_button);
            bar = (ProgressBar) itemView.findViewById(R.id.loading);
            audioViewLayout = (LinearLayout) itemView.findViewById(R.id.root_audio);
            tvDuration = (TextView) itemView.findViewById(R.id.duration);
            seekBar = (SeekBar) itemView.findViewById(R.id.seek_bar);
            isSelected =false;
            if(ivChatPhoto != null){
                ivChatPhoto.setOnLongClickListener(this);
                ivChatPhoto.setOnClickListener(null);
            }
            if(mRoot != null){
                mRoot.setLongClickable(true);
            }
            if (playButton != null) {
                playButton.setOnClickListener(this);

            }
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mRoot.setOnLongClickListener(this);


        }

        public void setTxtMessage(String message) {
            if (txtMessage == null) return;
            txtMessage.setText(message);
        }

        public void setIvUser(String urlPhotoUser) {
            if (ivUser == null) return;
            Glide.with(ivUser.getContext()).load(urlPhotoUser).centerCrop().transform(new CircleTransform(ivUser.getContext())).override(40, 40).into(ivUser);
        }

        public void setTvTimestamp(String timestamp) {
            if (tvTimestamp == null) return;
            tvTimestamp.setText(convertTimestamp(timestamp));
        }

        public void setIvChatPhoto(String url) {
            if (ivChatPhoto == null) return;
            Glide.with(ivChatPhoto.getContext()).load(url)
                    .override(100, 100)
                    .fitCenter()
                    .into(ivChatPhoto);
            ivChatPhoto.setOnClickListener(this);
        }

        public void tvIsLocation(int visible) {
            if (tvLocation == null) return;
            tvLocation.setVisibility(visible);
        }

        public void loadUserModel(final Message model) {
            String id = model.getSenderID();
            DatabaseRefs.mUsersDatabaseReference.child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserModel model1 = dataSnapshot.getValue(UserModel.class);
                    setIvUser(model1.getImageUrl());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            final Message message = getItem(position);
            if (message.getType().equals(Util.MESSAGE_TYPES.IMAGE_TYPE.name())) {
                mClickListenerChatFirebase.clickImageChat(view, position, message.getSenderID(), message.getPictureURL(), message.getTime());
            } else if (message.getType().equals(Util.MESSAGE_TYPES.MAP_TYPE.name())) {
                mClickListenerChatFirebase.clickImageMapChat(view, position, String.valueOf(message.getLatitude()), String.valueOf(message.getLongitude()));
            } else if (message.getType().equals(Util.MESSAGE_TYPES.AUDIO_TYPE.name())) {
                //Toast.makeText(context, "For playing audio file, Press on play button.", Toast.LENGTH_SHORT).show();
                if (playButton != null) {
                    if (view.getId() == R.id.play_pause_button) {

                        try {
                            mPlayer = new MediaPlayer();
                            StorageReference ref = DatabaseRefs.nRootStorage.child(message.getAudio());

                            String filename =context.getExternalCacheDir().getAbsolutePath()+'/' +ref.getName() + "_tmp";
                            filename +=".3gp";

                            final File localFile = new File(filename);

                            if(localFile.exists()){
                                Log.v(TAG,"file is already there");
                                mPlayer.setDataSource(filename);
                                //mPlayer.setVolume();
                                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                mPlayer.prepareAsync();
                                mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mediaPlayer) {
                                        Log.v(TAG,"Media Player Starting");
                                        int duration = mPlayer.getDuration();
                                        seekBar.setMax(duration);

                                        String time = String.format("%02d min, %02d sec",
                                                TimeUnit.MILLISECONDS.toMinutes(duration),
                                                TimeUnit.MILLISECONDS.toSeconds(duration) -
                                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
                                        );
                                        tvDuration.setText(time);
                                        mPlayer.start();
                                    }
                                });
                                final Timer timer = new Timer();
                                timer.scheduleAtFixedRate(new TimerTask() {
                                    @Override
                                    public void run() {
                                        try {
                                            seekBar.setProgress(mPlayer.getCurrentPosition());
                                        }catch (NullPointerException e){
                                            this.cancel();

                                        }
                                    }
                                },0,1000);

                                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        seekBar.setMax(0);
                                        timer.cancel();
                                        mPlayer.reset();
                                        mPlayer.release();
                                        playButton.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_play));
                                        mPlayer =null;
                                        checker = true;
                                    }
                                });

                                return;
                            }

                            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                @Override
                                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                                    if(mPlayer != null)
                                        mPlayer.seekTo(i);
                                }

                                @Override
                                public void onStartTrackingTouch(SeekBar seekBar) {

                                }

                                @Override
                                public void onStopTrackingTouch(SeekBar seekBar) {

                                }
                            });
                            Log.v(TAG,"create new file");

                            final String finalFilename = filename;
                            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    try {

                                        //tvDuration.setText(""+taskSnapshot.getTotalByteCount()/1000);
                                        mPlayer = new MediaPlayer();
                                        mPlayer.setDataSource(finalFilename);



                                        mPlayer.setVolume(1.0f, 1.0f);
                                        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                        mPlayer.prepareAsync();
                                        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                            @Override
                                            public void onPrepared(MediaPlayer mediaPlayer) {
                                                Log.v(TAG,"Media Player Starting");
                                                int duration = mPlayer.getDuration();
                                                seekBar.setMax(duration);
                                                String time = String.format("%02d min, %02d sec",
                                                        TimeUnit.MILLISECONDS.toMinutes(duration),
                                                        TimeUnit.MILLISECONDS.toSeconds(duration) -
                                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
                                                );
                                                tvDuration.setText(time);
                                                mPlayer.start();
                                            }
                                        });
                                        final Timer timer = new Timer();
                                        timer.scheduleAtFixedRate(new TimerTask() {
                                            @Override
                                            public void run() {
                                                try {
                                                    seekBar.setProgress(mPlayer.getCurrentPosition());
                                                }catch (NullPointerException e){
                                                    this.cancel();

                                                }
                                            }
                                        },0,1000);

                                        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                            @Override
                                            public void onCompletion(MediaPlayer mediaPlayer) {
                                                seekBar.setMax(0);
                                                timer.cancel();
                                                mPlayer.reset();
                                                mPlayer.release();
                                                playButton.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_play));
                                                mPlayer =null;
                                                checker = true;
                                            }
                                        });

                                        //mPlayer.start();

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "onFailure MediaPlayer :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        playButton.setEnabled(true);
                        playButton.setClickable(true);
                    }
                } else {
                    mClickListenerChatFirebase.clickItemChat(view, position);
                }

            }

        }
        @Override
        public boolean onLongClick(View view) {
            isSelected =!isSelected;
            mClickListenerChatFirebase.longClickItemChat(itemView,getAdapterPosition(),getItem(getAdapterPosition()),isSelected);
            return false;
        }
    }



}

