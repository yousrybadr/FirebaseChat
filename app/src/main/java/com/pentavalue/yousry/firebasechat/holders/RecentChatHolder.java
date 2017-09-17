package com.pentavalue.yousry.firebasechat.holders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pentavalue.yousry.firebasechat.R;
import com.pentavalue.yousry.firebasechat.activities.ChatActivity;
import com.pentavalue.yousry.firebasechat.models.Chat;

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


        name.setText(chat.getConversationName());
        phone.setText("Hi");
        Glide.with(context).load(chat.getWallpaperURL())
                .into(photo);




    }

}
