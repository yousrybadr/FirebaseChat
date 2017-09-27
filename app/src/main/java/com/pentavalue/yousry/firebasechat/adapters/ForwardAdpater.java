package com.pentavalue.yousry.firebasechat.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.pentavalue.yousry.firebasechat.R;
import com.pentavalue.yousry.firebasechat.activities.ChatActivity;
import com.pentavalue.yousry.firebasechat.fragments.RecentChatFragment;
import com.pentavalue.yousry.firebasechat.holders.ContactViewHolder;
import com.pentavalue.yousry.firebasechat.holders.RecentChatHolder;
import com.pentavalue.yousry.firebasechat.models.Chat;
import com.pentavalue.yousry.firebasechat.models.Contact;
import com.pentavalue.yousry.firebasechat.models.CurrentUser;
import com.pentavalue.yousry.firebasechat.models.Message;
import com.pentavalue.yousry.firebasechat.util.Util;

import java.util.List;

/**
 * Created by yousry on 9/26/2017.
 */

public class ForwardAdpater extends FirebaseRecyclerAdapter<Chat,RecentChatHolder> {

    //List<Chat> chats;
    OnItemClick onItemClick;
    public static final String TAG = RecentChatFragment.class.getSimpleName();

    private Context context;
    private static final int REMOVED_ITEM = 0;
    private static final int VIEW_ITEM = 1;

    public ForwardAdpater(Query ref, Context context, OnItemClick onItemClick) {
        super(Chat.class, R.layout.recent_chat_item, RecentChatHolder.class, ref);
        this.onItemClick =onItemClick;
        this.context =context;
    }

    @Override
    public int getItemViewType(int position) {
        Chat model = getItem(position);
        if(checkUser(model)){
            return VIEW_ITEM;
        }else{
            return REMOVED_ITEM;
        }
    }
    @Override
    protected void populateViewHolder(final RecentChatHolder viewHolder, final Chat model, final int position) {
        if(getItemViewType(position) == REMOVED_ITEM){
            return;
        }else{
            if(CurrentUser.getInstance().getUserModel() ==null){
                return;
            }
            viewHolder.bind(model,context);
            viewHolder.item_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Launch PostDetailActivity
                    onItemClick.onItemClick(viewHolder, model, position);

                }
            });

        }

    }
    boolean checkUser(Chat model){
        for(String item :model.getMembers()){
            if(item.equals(CurrentUser.getInstance().getUserModel().getId())){
                Log.v(RecentChatHolder.class.getSimpleName(),"User is TRUE");
                return true;
            }
        }
        Log.v(RecentChatHolder.class.getSimpleName(),"User is FALSE");
        return false;
    }

    @Override
    public RecentChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == VIEW_ITEM){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_chat_item,parent,false);
            return new RecentChatHolder(view);
        }else{
            view =LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_item,parent,false);
            return new RecentChatHolder(view);
        }
    }
    public interface OnItemClick{
        void onItemClick(RecentChatHolder view, Chat model, int position);
    }
}
