package com.pentavalue.yousry.firebasechat.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pentavalue.yousry.firebasechat.R;
import com.pentavalue.yousry.firebasechat.activities.ChatActivity;
import com.pentavalue.yousry.firebasechat.holders.ContactViewHolder;
import com.pentavalue.yousry.firebasechat.models.Contact;
import com.pentavalue.yousry.firebasechat.models.UserModel;
import com.pentavalue.yousry.firebasechat.util.Util;

import java.util.List;

/**
 * Created by yousry on 9/10/2017.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactViewHolder>  {
    ContactViewHolder holder;
    List<Contact> contacts;
    Context context;

    public ContactAdapter(List<Contact> contacts, Context context) {
        this.contacts = contacts;
        this.context = context;
    }

    public ContactAdapter(Context context) {
        this.context = context;
    }



    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        holder =new ContactViewHolder(view);
        return holder;
    }


    @Override
    public void onViewDetachedFromWindow(ContactViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        //holder.unbind();
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, final int position) {
        holder.bind(contacts.get(position),context);
       /* holder.item_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, ChatActivity.class);
                intent.putExtra(Util.ITEM_USER_EXTRA_KEY,users.get(position));
                context.startActivity(intent);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }
}
