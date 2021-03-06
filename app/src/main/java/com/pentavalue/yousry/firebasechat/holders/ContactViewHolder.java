package com.pentavalue.yousry.firebasechat.holders;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.pentavalue.yousry.firebasechat.R;
import com.pentavalue.yousry.firebasechat.activities.ChatActivity;
import com.pentavalue.yousry.firebasechat.models.Contact;
import com.pentavalue.yousry.firebasechat.util.DatabaseRefs;
import com.pentavalue.yousry.firebasechat.util.Util;

import org.w3c.dom.Text;

import butterknife.BindDrawable;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by yousry on 9/10/2017.
 */

public class ContactViewHolder extends RecyclerView.ViewHolder {

    public static final String TAG =ContactViewHolder.class.getSimpleName();

    private DatabaseReference ref;
    private Context context;
    private View view;
    private TextView name;
    private TextView phone;
    private CircleImageView photo;
    private Button inviteButton;
    public LinearLayout item_contact;
    private Contact contact;



    public ContactViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        ref = DatabaseRefs.mChatsDatabaseReference;
    }

    public void bind(final Contact contact, final Context context) {
        this.context =context;
        //ButterKnife.bind(context,view);
        name = (TextView) view.findViewById(R.id.name_contact);
        phone = (TextView) view.findViewById(R.id.phone_contact);
        photo = (CircleImageView) view.findViewById(R.id.image_contact);
        item_contact = view.findViewById(R.id.item_contact);
        inviteButton =view.findViewById(R.id.invite_button_contact);


        if(contact.isMessengerContact()){
            inviteButton.setVisibility(View.GONE);
            Glide.with(context)
                    .load(contact.getUserModel().getImageUrl())
                    .into(photo);
        }else {
            inviteButton.setVisibility(View.VISIBLE);
            Resources res =context.getResources();
            Drawable drawable =res.getDrawable(R.drawable.icon_user);
            photo.setImageDrawable(drawable);
        }
        name.setText(contact.getContact_name());
        phone.setText(contact.getPhone_number());


        item_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG,contact.toString());
                if(contact.isMessengerContact()){
                    if(contact.getChatID().isEmpty() || contact.getChatID() == null){
                        ref = ref.push();
                        String chatID =ref.getKey();
                        contact.setChatID(chatID);
                        context.startActivity(new Intent(context, ChatActivity.class)
                                .putExtra(Util.CONTACT_KEY_MODEL,contact)
                                .putExtra(Util.FIRST_TIME_KEY,true)
                        );
                    }else{
                        context.startActivity(new Intent(context, ChatActivity.class)
                                .putExtra(Util.CHAT_KEY_MODEL,contact.getChatID())
                        );
                    }



                }
            }
        });
        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("smsto:"+contact.getPhone_number());


                Intent sendIntent = new Intent(Intent.ACTION_VIEW,uri);
                sendIntent.putExtra("sms_body", "http://www.pentavalue.com/home/invitation/downlaodapk.php");
                sendIntent.setType("vnd.android-dir/mms-sms");
                context.startActivity(sendIntent);
            }
        });

    }



    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
