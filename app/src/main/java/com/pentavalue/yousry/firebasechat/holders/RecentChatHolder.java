package com.pentavalue.yousry.firebasechat.holders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.pentavalue.yousry.firebasechat.R;
import com.pentavalue.yousry.firebasechat.activities.ChatActivity;
import com.pentavalue.yousry.firebasechat.models.Chat;
import com.pentavalue.yousry.firebasechat.models.CurrentUser;
import com.pentavalue.yousry.firebasechat.models.UserModel;
import com.pentavalue.yousry.firebasechat.util.DatabaseRefs;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by yousry on 9/13/2017.
 */

public class RecentChatHolder extends RecyclerView.ViewHolder {

    private Context context;
    private View view;
    private TextView name;
    private TextView phone;
    private CircleImageView photo;
    public LinearLayout item_chat;
    private Chat chat;


    public RecentChatHolder(View itemView) {
        super(itemView);
        this.view =itemView;
    }
    public void bind(final Chat chat, final Context context) {

        this.context =context;
        name = (TextView) view.findViewById(R.id.name_chat);
        phone = (TextView) view.findViewById(R.id.phone_chat);
        photo = (CircleImageView) view.findViewById(R.id.image_chat);
        item_chat = view.findViewById(R.id.item_chat);
        loadUserModel(chat);






    }

    private void loadUserModel(final Chat chat){
        DatabaseReference ref;
        if(chat.isGroup()){
            name.setText(chat.getConversationName());
            if(chat.getLastMessage().length() > 50){
                phone.setText(chat.getLastMessage().substring(0,49) + " ...");

            }else{
                phone.setText(chat.getLastMessage());
            }
            Glide.with(context).load(chat.getChatImage()).into(photo);
        }else{
            if(chat.getMember(0).equals(CurrentUser.getInstance().getUserModel().getId())){
                ref = DatabaseRefs.mUsersDatabaseReference.child(chat.getMember(1));
            }else{
                ref = DatabaseRefs.mUsersDatabaseReference.child(chat.getMember(0));
            }

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserModel model =dataSnapshot.getValue(UserModel.class);
                    name.setText(model.getName());
                    if(chat.getLastMessage().length() > 50){
                        phone.setText(chat.getLastMessage().substring(0,49) + " ...");

                    }else{
                        phone.setText(chat.getLastMessage());
                    }
                    Glide.with(context)
                            .load(model.getImageUrl())
                            .into(photo);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(context,databaseError.getMessage(),Toast.LENGTH_LONG).show();

                }
            });
        }

    }


}
