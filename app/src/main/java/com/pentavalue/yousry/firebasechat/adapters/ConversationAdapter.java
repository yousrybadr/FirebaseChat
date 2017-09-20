
package com.pentavalue.yousry.firebasechat.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.pentavalue.yousry.firebasechat.R;
import com.pentavalue.yousry.firebasechat.models.Chat;
import com.pentavalue.yousry.firebasechat.models.Message;
import com.pentavalue.yousry.firebasechat.models.UserModel;
import com.pentavalue.yousry.firebasechat.util.CircleTransform;
import com.pentavalue.yousry.firebasechat.util.DatabaseRefs;
import com.pentavalue.yousry.firebasechat.util.Util;

import java.util.List;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;


/**
 * Created by Alessandro Barreto on 23/06/2016.
 */

public class ConversationAdapter extends FirebaseRecyclerAdapter<Message,ConversationAdapter.MyChatViewHolder> {

    public static final String TAG = ConversationAdapter.class.getSimpleName();
    private static final int RIGHT_MSG = 0;
    private static final int LEFT_MSG = 1;
    private static final int RIGHT_MSG_IMG = 2;
    private static final int LEFT_MSG_IMG = 3;

    public static int COUNT;

   // private ClickListenerChatFirebase mClickListenerChatFirebase;

    private String idUser;

    private Chat chat;








    public ConversationAdapter(DatabaseReference ref, String mCurrentUser, Chat chat) {
        super(Message.class, R.layout.item_message_left, ConversationAdapter.MyChatViewHolder.class, ref);
        this.idUser = mCurrentUser;
        this.chat =chat;
        Log.v(TAG,"User ID = " +idUser);
        COUNT =0;

    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
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
    protected void populateViewHolder(final MyChatViewHolder viewHolder, Message model, int position) {
        //viewHolder.setIvUser("https://firebasestorage.googleapis.com/v0/b/fir-library-81a54.appspot.com/o/images%2F2017-09-07_044310_gallery?alt=media&token=bf392434-ba25-4c6a-b28a-f6546933ed97");
        //viewHolder.setIvChatPhoto("https://firebasestorage.googleapis.com/v0/b/fir-library-81a54.appspot.com/o/images%2F2017-09-07_044310_gallery?alt=media&token=bf392434-ba25-4c6a-b28a-f6546933ed97");
        //viewHolder.loadUserModel(chat);
        viewHolder.loadUserModel(model);

        try {
            viewHolder.setTvTimestamp(model.getTime());
        }catch (NumberFormatException ex){
            Log.e(TAG,ex.getMessage());
        }
        if(model.getType().equals("text")){
            viewHolder.setTxtMessage(model.getText());
        }else if(model.getType().equals("image")){
            viewHolder.setIvChatPhoto(model.getPictureURL());
        }else if(model.getType().equals("map")){
            viewHolder.tvIsLocation(View.VISIBLE);
            viewHolder.setIvChatPhoto(Util.local(String.valueOf(model.getLatitude()),String.valueOf(model.getLongitude())));

        }



    }



    @Override
    public int getItemViewType(int position) {
        Message message=getItem(position);

        if(message.getType().equals("text")){
            Log.v(TAG,"Sender ID = "+message.getSenderID());
            if(message.getSenderID().equals(idUser)){
                return RIGHT_MSG;
            }else {
                return LEFT_MSG;
            }
        }else if(message.getType().equals("image")){
            Log.v(TAG,"Sender ID = "+message.getSenderID());
            if(message.getSenderID().equals(idUser)){
                return RIGHT_MSG_IMG;
            }else {
                return LEFT_MSG_IMG;
            }
        }else if(message.getType().equals("map")){
            if(message.getSenderID().equals(idUser)){
                return RIGHT_MSG_IMG;
            }else {
                return LEFT_MSG_IMG;
            }
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

        public void loadUserModel(final Message model){
            String id =model.getSenderID();
            DatabaseRefs.mUsersDatabaseReference.child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserModel model1= dataSnapshot.getValue(UserModel.class);
                    setIvUser(model1.getImageUrl());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
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

    private CharSequence convertTimestamp(String mSec){
        return DateUtils.getRelativeTimeSpanString(Long.parseLong(mSec),System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
    }

}

