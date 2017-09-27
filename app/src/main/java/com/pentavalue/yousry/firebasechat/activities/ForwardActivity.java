package com.pentavalue.yousry.firebasechat.activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.pentavalue.yousry.firebasechat.R;
import com.pentavalue.yousry.firebasechat.adapters.FavoriteMessageAdapter;
import com.pentavalue.yousry.firebasechat.adapters.ForwardAdpater;
import com.pentavalue.yousry.firebasechat.adapters.UserListAdapter;
import com.pentavalue.yousry.firebasechat.fragments.GroupChatFragment;
import com.pentavalue.yousry.firebasechat.holders.RecentChatHolder;
import com.pentavalue.yousry.firebasechat.holders.UserListHolder;
import com.pentavalue.yousry.firebasechat.models.Chat;
import com.pentavalue.yousry.firebasechat.models.Contact;
import com.pentavalue.yousry.firebasechat.models.CurrentUser;
import com.pentavalue.yousry.firebasechat.models.UserModel;
import com.pentavalue.yousry.firebasechat.models.UserSelectedModel;
import com.pentavalue.yousry.firebasechat.util.DatabaseRefs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ForwardActivity extends AppCompatActivity {
    private static final String TAG =ForwardActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private DatabaseReference ref;
    ForwardAdpater adapter;
    @BindView(R.id.recyclerForward)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward);
        ButterKnife.bind(this);
        ref =DatabaseRefs.mChatsDatabaseReference;

        // Set up Layout Manager, reverse layout
        LinearLayoutManager mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(false);
        //mManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mManager);
        adapter =new ForwardAdpater(ref,this, new ForwardAdpater.OnItemClick() {
            @Override
            public void onItemClick(RecentChatHolder view, Chat model, int position) {
                Toast.makeText(getApplicationContext(), model.getConversationName(), Toast.LENGTH_SHORT).show();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("chat_id",model.getId());
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }
}
