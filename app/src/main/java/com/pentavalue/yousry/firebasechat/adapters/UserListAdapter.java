package com.pentavalue.yousry.firebasechat.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pentavalue.yousry.firebasechat.R;
import com.pentavalue.yousry.firebasechat.holders.ContactViewHolder;
import com.pentavalue.yousry.firebasechat.holders.RecentChatHolder;
import com.pentavalue.yousry.firebasechat.holders.UserListHolder;
import com.pentavalue.yousry.firebasechat.models.Chat;
import com.pentavalue.yousry.firebasechat.models.UserModel;
import com.pentavalue.yousry.firebasechat.models.UserSelectedModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yousry on 9/13/2017.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListHolder> {
    private List<UserSelectedModel> userSelectedModels;
    private Context context;
    @NonNull
    private OnItemCheckListener onItemCheckListener;

    public UserListAdapter(List<UserSelectedModel> userSelectedModels, Context context, OnItemCheckListener onItemCheckListener) {
        this.userSelectedModels = userSelectedModels;
        this.context = context;
        this.onItemCheckListener = onItemCheckListener;
    }


    @Override
    public UserListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_groupcaht_item, parent, false);
        return new UserListHolder(view);
    }

    @Override
    public void onViewDetachedFromWindow(UserListHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onBindViewHolder(final UserListHolder holder, int position) {
        UserSelectedModel model = userSelectedModels.get(position);
        final int pos =holder.getAdapterPosition();
        holder.bind(model);
        holder.setImageProfile(model.getImageUrl(),context);
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.checkBox.setChecked(!holder.checkBox.isChecked());
                if (holder.checkBox.isChecked()) {
                    onItemCheckListener.onItemCheck(holder, pos);
                } else {
                    onItemCheckListener.onItemUncheck(holder, pos);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return userSelectedModels.size();
    }


    public UserSelectedModel getItemModelList(int position){
        return this.userSelectedModels.get(position);
    }


    public List<String> getUserSelectedModels(){
        List<String> tmpList =new ArrayList<>();
        if(this.userSelectedModels.size() >0){
            for(UserSelectedModel model : userSelectedModels){
                if(model.isSelected()){
                    tmpList.add(model.getId());
                }
            }
            return tmpList;
        }else{
            return null;
        }
    }
    public interface OnItemCheckListener {
        void onItemCheck( UserListHolder holder, int pos);

        void onItemUncheck( UserListHolder holder, int pos);
    }
}
