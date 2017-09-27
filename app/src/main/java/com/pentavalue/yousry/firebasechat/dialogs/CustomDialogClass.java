package com.pentavalue.yousry.firebasechat.dialogs;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pentavalue.yousry.firebasechat.R;
import com.pentavalue.yousry.firebasechat.models.UserModel;
import com.pentavalue.yousry.firebasechat.util.DatabaseRefs;
import com.pentavalue.yousry.firebasechat.views.TouchImageView;

/**
 * Created by mahmoud on 2016-04-17.
 */
public class CustomDialogClass extends Dialog implements View.OnClickListener {
    private static final String TAG =CustomDialogClass.class.getSimpleName();
    public Context context;

    public ImageView cancelButton;

    ImageView imageProfile ;
    TextView nameTextView ;
    ImageView imageMessage;

    ProgressBar progressBar;
    String userId;
    String imageMessageURL;

    public CustomDialogClass(Context a, String userId, String urlImageMessage) {
        super(a);
        this.context=a;
        this.userId = userId;
        this.imageMessageURL =urlImageMessage;

    }

    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_layout);


        cancelButton = (ImageView) findViewById(R.id.cancel);
        nameTextView = (TextView) findViewById(R.id.title);
        imageProfile = (ImageView) findViewById(R.id.image_profile);
        imageMessage = findViewById(R.id.image_message);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        cancelButton.setOnClickListener(this);

        loadUserModel();


        Glide.with(context).load( imageMessageURL).asBitmap().override(640,640).fitCenter().into(new SimpleTarget<Bitmap>() {

            @Override
            public void onLoadStarted(Drawable placeholder) {
                progressBar.setVisibility(View.VISIBLE);
                imageMessage.setVisibility(View.INVISIBLE);
            }


            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                imageMessage.setVisibility(View.VISIBLE);
                imageMessage.setImageBitmap(resource);
                progressBar.setVisibility(View.INVISIBLE);
            }


            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                imageMessage.setImageDrawable(errorDrawable);
                Toast.makeText(context,"Error, reload this image again",Toast.LENGTH_LONG).show();
                //dismiss();
            }
        });

/*
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialContactPhone("+201055664477");
            }
        });
*/

    }


    void loadUserModel(){
        //Glide.with(context).load(imageMessageURL).placeholder(R.drawable.icon_message).into(imageMessage);

        DatabaseRefs.mUsersDatabaseReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserModel model =dataSnapshot.getValue(UserModel.class);
                if(model != null){
                    Glide.with(context).load(model.getImageUrl()).into(imageProfile);
                    nameTextView.setText(model.getName());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.cancel:
                dismiss();
                break;
            default:
                break;
        }
    }

    private void dialContactPhone(final String phoneNumber) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));



    }


}
