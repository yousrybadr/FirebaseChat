package com.pentavalue.yousry.firebasechat.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.pentavalue.yousry.firebasechat.HomeActivity;
import com.pentavalue.yousry.firebasechat.R;
import com.pentavalue.yousry.firebasechat.models.CurrentUser;
import com.pentavalue.yousry.firebasechat.models.UserModel;
import com.pentavalue.yousry.firebasechat.util.DatabaseRefs;
import com.pentavalue.yousry.firebasechat.util.Logs;
import com.pentavalue.yousry.firebasechat.util.Validation;
import com.pentavalue.yousry.firebasechat.views.LoadingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;

public class LoginFragment extends Fragment {
    public static final String TAG =LoginFragment.class.getSimpleName();

    @BindView(R.id.editTextUserNameLogin)
    EditText email;
    @BindView(R.id.editTextPassordLogin) EditText password;
    @BindView(R.id.checkBoxLogin)
    CheckBox rememberMeCheck;



    @Nullable
    @BindView(R.id.buttonLogin)
    Button login;

    @Nullable
    @BindView(R.id.forgetPassword) TextView forgetClick;

    private Unbinder unbinder;
    private LoadingDialog dialog;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Optional
    @OnClick(R.id.buttonLogin) void loginButtonPressed(){
        if(Validation.isValidLogin(email.getText().toString(),password.getText().toString())){
            CurrentUser.getInstance().isChecked =rememberMeCheck.isChecked();
            signIn(email.getText().toString(),password.getText().toString());
        }else {
            email.setError("Fill All Fields");
        }
    }
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth =FirebaseAuth.getInstance();
        mDatabase =DatabaseRefs.mUsersDatabaseReference;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this,view);
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!Validation.isValidLogin(email,password)) {
            return;
        }


        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Logs.LogV(TAG, "Success :" + authResult.getUser().getEmail());
                        setUserModel(authResult.getUser());
                        startActivity(new Intent(getActivity(), HomeActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Logs.LogE(TAG,e.getMessage());
                    }
                });
        // [END sign_in_with_email]
    }

    void setUserModel(final FirebaseUser user){
        DatabaseReference userRef = mDatabase.child(user.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                if(CurrentUser.getInstance().isChecked){
                    CurrentUser.getInstance().setUserModel(userModel);
                    Logs.LogV(TAG,"Login :" + userModel.toString());
                }else {
                    CurrentUser.getInstance().setUserModel(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

}
