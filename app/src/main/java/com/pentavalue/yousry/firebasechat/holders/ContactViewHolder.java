package com.pentavalue.yousry.firebasechat.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pentavalue.yousry.firebasechat.R;
import com.pentavalue.yousry.firebasechat.models.Contact;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by yousry on 9/10/2017.
 */

public class ContactViewHolder extends RecyclerView.ViewHolder {


    private Context context;
    private View view;
    private TextView name;
    private TextView phone;
    private CircleImageView photo;
    private Button inviteButton;
    private LinearLayout item_contact;
    private Contact contact;



    public ContactViewHolder(View itemView) {
        super(itemView);
        view = itemView;
    }

    public void bind(final Contact contact, final Context context) {
        this.context =context;
        name = (TextView) view.findViewById(R.id.name_contact);
        phone = (TextView) view.findViewById(R.id.phone_contact);
        photo = (CircleImageView) view.findViewById(R.id.image_contact);
        item_contact = view.findViewById(R.id.item_contact);
        inviteButton =view.findViewById(R.id.invite_button_contact);


        name.setText(contact.getContact_name());
        phone.setText(contact.getPhone_number());

    }


    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
