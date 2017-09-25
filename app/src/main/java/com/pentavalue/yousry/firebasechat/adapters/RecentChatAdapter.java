package com.pentavalue.yousry.firebasechat.adapters;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.pentavalue.yousry.firebasechat.R;
import com.pentavalue.yousry.firebasechat.activities.ChatActivity;
import com.pentavalue.yousry.firebasechat.fragments.RecentChatFragment;
import com.pentavalue.yousry.firebasechat.holders.RecentChatHolder;
import com.pentavalue.yousry.firebasechat.models.Chat;
import com.pentavalue.yousry.firebasechat.models.CurrentUser;
import com.pentavalue.yousry.firebasechat.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yousry on 9/20/2017.
 */

public class RecentChatAdapter extends FirebaseRecyclerAdapter<Chat, RecentChatHolder> {
    List<Chat> chats ;
    int size;
    public static final String TAG = RecentChatFragment.class.getSimpleName();

    private Context context;
    private static final int REMOVED_ITEM = 0;
    private static final int VIEW_ITEM = 1;


    public RecentChatAdapter(DatabaseReference ref, Context context, OnSizeChanged changed){
        super(Chat.class,R.layout.recent_chat_item,RecentChatHolder.class,ref);
        this.context =context;
        size =0;
        this.chats =new ArrayList<>();
        this.onSizeChanged =changed;
        onSizeChanged.onSizeChanged(size);
    }

    @Override
    public int getItemViewType(int position) {
        Chat model = getItem(position);
        if(checkUser(model)){
            size++;
            return VIEW_ITEM;
        }else{
            size--;
            return REMOVED_ITEM;
        }
    }


    public void setSize(OnSizeChanged size){
        this.onSizeChanged =size;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
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



    @Override
    protected void populateViewHolder(final RecentChatHolder viewHolder, final Chat model, final int position) {
        //chats.add(model);

        onSizeChanged.onSizeChanged(size);

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
                    Intent intent = new Intent(context, ChatActivity.class)
                            .putExtra(Util.CHAT_KEY_MODEL,model.getId());
                    //intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, postKey);
                    context.startActivity(intent);

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

    OnSizeChanged onSizeChanged;
    public interface OnSizeChanged{
        void onSizeChanged(int size);
    }
}
