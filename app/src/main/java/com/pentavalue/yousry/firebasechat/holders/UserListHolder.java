package com.pentavalue.yousry.firebasechat.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pentavalue.yousry.firebasechat.R;
import com.pentavalue.yousry.firebasechat.models.Contact;
import com.pentavalue.yousry.firebasechat.models.UserSelectedModel;

/**
 * Created by yousry on 9/20/2017.
 */

public class UserListHolder extends RecyclerView.ViewHolder{
    private ImageView imageProfile;
    public TextView name;
    public CheckBox checkBox;
    private View view;
    public LinearLayout root;


    public UserListHolder(View itemView) {
        super(itemView);
        this.view =itemView;
    }

    public void bind(UserSelectedModel model){
        imageProfile = (ImageView) view.findViewById(R.id.image);
        name = view.findViewById(R.id.name);
        checkBox = view.findViewById(R.id.checkbox);
        root =view.findViewById(R.id.root);
        name.setText(model.getName());
    }


    public void setNameTextViewColor(Context context, int Flag){
        switch (Flag){
            case 0:
                this.name.setTextColor(context.getResources().getColor(R.color.FontSystemColor));
                break;
            case 1:
                this.name.setTextColor(context.getResources().getColor(R.color.blackColor));
                break;
            default:
                break;
        }
    }
    public void setName(String name) {
        this.name.setText(name);
    }

    public void setImageProfile(String imageURL, Context context) {
        Glide.with(context).load(imageURL).into(this.imageProfile);
    }
    public void setOnClickListener(View.OnClickListener onClickListener) {
        root.setOnClickListener(onClickListener);
    }
}
