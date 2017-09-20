package com.pentavalue.yousry.firebasechat.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
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


    CheckedTextView checkedTextView;
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
        //checkedTextView = (CheckedTextView) findViewById(R.id.checkedTextView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();



    }


    boolean checked =false;
    public void onClick(View view) {
        if(!checked){
            checkedTextView.setChecked(true);
            checked =true;
        }else{
            checkedTextView.setChecked(false);
            checked =false;
        }
    }
}
