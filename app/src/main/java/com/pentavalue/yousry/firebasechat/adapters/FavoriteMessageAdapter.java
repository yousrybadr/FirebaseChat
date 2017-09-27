package com.pentavalue.yousry.firebasechat.adapters;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.pentavalue.yousry.firebasechat.R;
import com.pentavalue.yousry.firebasechat.models.Message;
import com.pentavalue.yousry.firebasechat.models.UserModel;
import com.pentavalue.yousry.firebasechat.util.CircleTransform;
import com.pentavalue.yousry.firebasechat.util.DatabaseRefs;
import com.pentavalue.yousry.firebasechat.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import me.himanshusoni.chatmessageview.ChatMessageView;

/**
 * Created by yousry on 9/26/2017.
 */

public class FavoriteMessageAdapter extends FirebaseRecyclerAdapter<Message,FavoriteMessageAdapter.MyChatViewHolder> {

    private static final int LEFT_MSG = 0;
    private static final int LEFT_MSG_IMG = 1;
    private static final int LEFT_MSG_AUDIO = 2;

    public FavoriteMessageAdapter(Query ref) {
        super(Message.class, R.layout.item_message_right, FavoriteMessageAdapter.MyChatViewHolder.class, ref);
    }

    @Override
    protected void populateViewHolder(MyChatViewHolder viewHolder, Message model, int position) {

    }


    // Vew Holder
    public class MyChatViewHolder extends RecyclerView.ViewHolder {

        TextView tvTimestamp, tvLocation, tvDuration;
        ImageButton playButton;
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
            audioViewLayout = (LinearLayout) itemView.findViewById(R.id.root_audio);
            tvDuration = (TextView) itemView.findViewById(R.id.duration);

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


    }
    private CharSequence convertTimestamp(String mSec) {
        return DateUtils.getRelativeTimeSpanString(Long.parseLong(mSec), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
    }
}
