package com.pentavalue.yousry.firebasechat.activities;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.pentavalue.yousry.firebasechat.R;
import com.pentavalue.yousry.firebasechat.fragments.LoginFragment;
import com.pentavalue.yousry.firebasechat.fragments.SignUpFragment;
import com.pentavalue.yousry.firebasechat.util.Logs;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

public class StartActivity extends AppCompatActivity {

    private static final String TAG = StartActivity.class.getSimpleName();

    @Nullable
    @BindView(R.id.loginButton) TextView loginView;
    @Nullable
    @BindView(R.id.registrationButton) TextView regsitView;

    Fragment[] fragments ;
    FragmentManager fragmentManager;
    FragmentTransaction transaction;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        fragments =new Fragment[]{SignUpFragment.newInstance(),LoginFragment.newInstance()};

        fragmentManager =getSupportFragmentManager();

        transaction =fragmentManager.beginTransaction();

        transaction.add(R.id.fragment_container,fragments[1],"fragment").addToBackStack("fragment").commit();



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragments[0].onActivityResult(requestCode,resultCode,data);
    }

    void replaceFragment(Fragment fragment){
        try {
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            //transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();

        } catch (Exception e) {
            // TODO: handle exception
            Toast.makeText(getApplicationContext(),"Error : "+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Optional
    @OnClick(R.id.registrationButton)void onRegistreationButtonPressed(){


        replaceFragment(fragments[0]);
        Logs.LogV(TAG,"Count Fragments are "+String.valueOf(fragmentManager.getBackStackEntryCount()));
    }
    @Optional
    @OnClick(R.id.loginButton)void onLoginButtonPressed(){
        replaceFragment(fragments[1]);
        Logs.LogV(TAG,"Count Fragments are "+String.valueOf(fragmentManager.getBackStackEntryCount()));

    }

}
