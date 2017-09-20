package com.pentavalue.yousry.firebasechat.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.pentavalue.yousry.firebasechat.R;
import com.pentavalue.yousry.firebasechat.models.CurrentUser;
import com.pentavalue.yousry.firebasechat.models.UserTyping;
import com.pentavalue.yousry.firebasechat.util.DatabaseRefs;

import java.util.List;

public class FullScreenImageActivity extends AppCompatActivity {

    private static final String Tag = FullScreenImageActivity.class.getSimpleName();
    String chatID;
    TextView textView;
    UserTyping typing;
    String userID;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        chatID ="-KuOuryCEAk9P1MAZlkR";
        userID ="oVuKsfTUNpNLqmRQURBzn7QwJJQ2";
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textView = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.button2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(FullScreenImageActivity.class.getSimpleName(),"Set Child Typing");

                Log.v(Tag,"Key 1 : "+ DatabaseRefs.mChatsDatabaseReference.child(chatID).getKey());
                Log.v(Tag,"Key 2 : "+ DatabaseRefs.mChatsDatabaseReference.getKey());
                Log.v(Tag,"Key 3 : "+ DatabaseRefs.mChatsDatabaseReference.child("members").child("0").getKey());
                Log.v(Tag,"Key 4 : "+ DatabaseRefs.mChatsDatabaseReference.equalTo("members").getRef().getKey());
                Log.v(Tag,"Key 5 : "+ DatabaseRefs.mChatsDatabaseReference.child(chatID).child("members").getKey());

                DatabaseReference ref =DatabaseRefs.mChatsDatabaseReference.child(chatID);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot != null){
                            for( DataSnapshot snapshot : dataSnapshot.getChildren()){
                                String member = snapshot.getValue(String.class);
                                if(member == null){
                                    Log.v(Tag,"members is null");
                                }
                                else{
                                    Log.v(Tag,"members Count" + member);
                                }
                            }

                        }else{
                            Log.v(Tag,"snap = null");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();



    }



}
