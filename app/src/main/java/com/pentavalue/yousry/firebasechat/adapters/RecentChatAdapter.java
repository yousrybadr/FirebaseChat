package com.pentavalue.yousry.firebasechat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pentavalue.yousry.firebasechat.R;
import com.pentavalue.yousry.firebasechat.holders.ContactViewHolder;
import com.pentavalue.yousry.firebasechat.holders.RecentChatHolder;
import com.pentavalue.yousry.firebasechat.models.Chat;
import com.pentavalue.yousry.firebasechat.models.UserModel;

import java.util.List;

/**
 * Created by yousry on 9/13/2017.
 */

public class RecentChatAdapter extends RecyclerView.Adapter<RecentChatHolder> {
    List<Chat> chatList;
    RecentChatHolder holder;
    Context context;

    public RecentChatAdapter(List<Chat> chatList, Context context) {
        this.chatList = chatList;
        this.context = context;
    }

    @Override
    public RecentChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_chat_item, parent, false);
        holder =new RecentChatHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecentChatHolder holder, int position) {
        holder.bind(chatList.get(position),this.context);
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
