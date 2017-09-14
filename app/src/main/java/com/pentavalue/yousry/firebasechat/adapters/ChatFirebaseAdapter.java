
package com.pentavalue.yousry.firebasechat.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.pentavalue.yousry.firebasechat.R;
import com.pentavalue.yousry.firebasechat.interfaces.ClickListenerChatFirebase;
import com.pentavalue.yousry.firebasechat.models.MessageModel;
import com.pentavalue.yousry.firebasechat.models.PrivateRoomChat;
import com.pentavalue.yousry.firebasechat.util.CircleTransform;

import java.util.List;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;


/**
 * Created by Alessandro Barreto on 23/06/2016.
 */

public class ChatFirebaseAdapter extends FirebaseRecyclerAdapter<MessageModel,ChatFirebaseAdapter.MyChatViewHolder> {

    List<MessageModel> modelList;
    private static final int RIGHT_MSG = 0;
    private static final int LEFT_MSG = 1;
    private static final int RIGHT_MSG_IMG = 2;
    private static final int LEFT_MSG_IMG = 3;

   // private ClickListenerChatFirebase mClickListenerChatFirebase;

    private String idUser;

    private PrivateRoomChat roomChat;





    public ChatFirebaseAdapter(DatabaseReference ref, String idUser, PrivateRoomChat roomChat, List<MessageModel> list,ClickListenerChatFirebase mClickListenerChatFirebase) {
        super(MessageModel.class, R.layout.item_message_left, ChatFirebaseAdapter.MyChatViewHolder.class, ref);

        this.modelList =list;


        this.roomChat =roomChat;
        this.idUser = idUser;
        //this.mClickListenerChatFirebase = mClickListenerChatFirebase;
    }

    @Override
    public MyChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == RIGHT_MSG){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right,parent,false);
            return new MyChatViewHolder(view);
        }else if (viewType == LEFT_MSG){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left,parent,false);
            return new MyChatViewHolder(view);
        }else if (viewType == RIGHT_MSG_IMG){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right_img,parent,false);
            return new MyChatViewHolder(view);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left_img,parent,false);
            return new MyChatViewHolder(view);
        }
    }

    @Override
    protected void populateViewHolder(MyChatViewHolder viewHolder, MessageModel model, int position) {
        if(model.getUserId().equals(roomChat.getFirst().getId()))
            viewHolder.setIvUser(roomChat.getFirst().getImageUrl());
        else
            viewHolder.setIvUser(roomChat.getSecond().getImageUrl());


        viewHolder.setTxtMessage(modelList.get(position).getText());
        viewHolder.setTvTimestamp(modelList.get(position).getTimeStamp());



    }

    @Override
    public int getItemViewType(int position) {

        //MessageModel model = getItem(position);
        if (modelList.get(position).getUserId().equals(idUser)){
            return RIGHT_MSG;
        }else{
            return LEFT_MSG;
        }
    }




    public class MyChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvTimestamp,tvLocation;
        EmojiconTextView txtMessage;
        ImageView ivUser,ivChatPhoto;

        public MyChatViewHolder(View itemView) {
            super(itemView);
            tvTimestamp = (TextView)itemView.findViewById(R.id.timestamp);
            txtMessage = (EmojiconTextView)itemView.findViewById(R.id.txtMessage);
            tvLocation = (TextView)itemView.findViewById(R.id.tvLocation);
            ivChatPhoto = (ImageView)itemView.findViewById(R.id.img_chat);
            ivUser = (ImageView)itemView.findViewById(R.id.ivUserChat);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            MessageModel model = getItem(position);
            /*if (model.getMapModel() != null){
                //mClickListenerChatFirebase.clickImageMapChat(view,position,model.getMapModel().getLatitude(),model.getMapModel().getLongitude());
            }else{
                //mClickListenerChatFirebase.clickImageChat(view,position,model.getUserModel().getName(),model.getUserModel().getImageUrl(),model.getFile().getUrl_file());
            }*/
        }

        public void setTxtMessage(String message){
            if (txtMessage == null)return;
            txtMessage.setText(message);
        }

        public void setIvUser(String urlPhotoUser){
            if (ivUser == null)return;
            Glide.with(ivUser.getContext()).load(urlPhotoUser).centerCrop().transform(new CircleTransform(ivUser.getContext())).override(40,40).into(ivUser);
        }

        public void setTvTimestamp(String timestamp){
            if (tvTimestamp == null)return;
            tvTimestamp.setText(convertTimestamp(timestamp));
        }

        public void setIvChatPhoto(String url){
            if (ivChatPhoto == null)return;
            Glide.with(ivChatPhoto.getContext()).load(url)
                    .override(100, 100)
                    .fitCenter()
                    .into(ivChatPhoto);
            ivChatPhoto.setOnClickListener(this);
        }

        public void tvIsLocation(int visible){
            if (tvLocation == null)return;
            tvLocation.setVisibility(visible);
        }

    }

    private CharSequence convertTimestamp(String mSec){
        return DateUtils.getRelativeTimeSpanString(Long.parseLong(mSec),System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
    }

}

