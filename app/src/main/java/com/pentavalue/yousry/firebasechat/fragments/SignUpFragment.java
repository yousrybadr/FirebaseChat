package com.pentavalue.yousry.firebasechat.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pentavalue.yousry.firebasechat.activities.HomeActivity;
import com.pentavalue.yousry.firebasechat.R;
import com.pentavalue.yousry.firebasechat.models.CurrentUser;
import com.pentavalue.yousry.firebasechat.models.UserModel;
import com.pentavalue.yousry.firebasechat.util.ActionIntent;
import com.pentavalue.yousry.firebasechat.util.DatabaseRefs;
import com.pentavalue.yousry.firebasechat.util.Util;
import com.pentavalue.yousry.firebasechat.util.Validation;
import com.pentavalue.yousry.firebasechat.views.LoadingDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;
import static com.pentavalue.yousry.firebasechat.util.Validation.checkEmailIsValid;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignUpFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment implements View.OnClickListener{
    static final String TAG = SignUpFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;

    private Unbinder unbinder;

    @BindView(R.id.fab)
    FloatingActionButton signUpButton;
    @BindView(R.id.linear)
    LinearLayout imageButtonLayout;
    @BindView(R.id.editTextUserName)
    EditText name;
    @BindView(R.id.editTextPassword)
    EditText password;
    @BindView(R.id.editTextRepassword)
    EditText repassword;
    @BindView(R.id.editTextPhone)
    EditText phone;
    @BindView(R.id.editTextEmail)
    EditText email;
    @BindView(R.id.imageReg)
    ImageView photo;
    @BindView(R.id.imageButtonTake)
    ImageButton takePhotoButton;
    @BindView(R.id.imageButtonSelect)
    ImageButton selectPhotoButton;


    @BindString(R.string.fieldRequired)
    String requiredMessage;

    @BindDrawable(R.drawable.icon_like)
    public Drawable likeDrawable;
    @BindDrawable(R.drawable.icon_error)
    public Drawable errorDrawable;


    //File
    private File filePathImageCamera;

    //Class Models
    private UserModel userModel;

    int request_code;
    //Firebase
    ActionIntent actionIntent;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    Uri pictureURI;
    private Bitmap bitmap;
    private LoadingDialog dialog;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl(Util.URL_STORAGE_REFERENCE).child(Util.FOLDER_STORAGE_IMG);


    public SignUpFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static SignUpFragment newInstance() {
        SignUpFragment fragment = new SignUpFragment();

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionIntent =new ActionIntent(getActivity(),getContext());
        dialog =new LoadingDialog(getContext());

        mAuth = FirebaseAuth.getInstance();

        userModel =new UserModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_sign_up, container, false);
        unbinder = ButterKnife.bind(this, view);

        bindViews(this);
        mAuth = FirebaseAuth.getInstance();
        dialog = new LoadingDialog(getContext());
        userModel = new UserModel();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onSignUpPressed(UserModel user) {
        if (mListener != null) {
            mListener.onSignUpFinishedListener(user.getEmail(),user.getPassword());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == actionIntent.IMAGE_CAMERA_REQUEST) {
            request_code =requestCode;
            if (resultCode == RESULT_OK) {
                if (actionIntent.getFilePathImageCamera() != null && actionIntent.getFilePathImageCamera().exists()) {
                    //StorageReference imageCameraRef = storageRef.child(actionIntent.getFilePathImageCamera().getName()+"_camera");
                    //sendFileFirebase(imageCameraRef,filePathImageCamera);
                    //showImagePhoto(userModel.getImageUrl());
                    bitmap = BitmapFactory.decodeFile(filePathImageCamera.getPath());
                    photo.setImageBitmap(bitmap);
                    pictureURI = Validation.getUriFromFile(getContext(),actionIntent.getFilePathImageCamera());
                } else {
                    //IS NULL
                }
            }
        } else if (requestCode == actionIntent.IMAGE_GALLERY_REQUEST) {
            request_code =requestCode;
            if (resultCode == RESULT_OK) {
                pictureURI = data.getData();


                if (pictureURI != null) {
                    // We need to recyle unused bitmaps
                    if (bitmap != null) {
                        bitmap.recycle();
                    }
                    InputStream stream = null;
                    try {
                        stream = getContext().getContentResolver().openInputStream(
                                data.getData());

                        bitmap = BitmapFactory.decodeStream(stream);

                        if (stream != null) {
                            stream.close();
                        }
                        photo.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                    /*sendFileFirebase(storageRef,selectedImageUri);
                    showImagePhoto(userModel.getImageUrl());*/
            } else {
                //URI IS NULL
            }
        }
        else {
            pictureURI = null;
        }

    }

    @Override
    public void onClick(View view) {
        int id =view.getId();
        switch (id){

            case R.id.fab:
                onSignUpButtonPressed();
                break;
            case R.id.imageButtonSelect:
                actionIntent.photoGalleryIntent();
                break;
            case R.id.imageButtonTake:
                break;

        }
    }



    private void onSignUpButtonPressed() {
        if (imageButtonLayout.getVisibility() == View.VISIBLE) {
            if (bitmap == null || pictureURI == null) {
                photo.setImageDrawable(errorDrawable);
                return;
            }
            dialog.showProgressDialog("Title", "Loading...", false);
            createAccount(email.getText().toString(), password.getText().toString());

            //startActivity(new Intent(RegistrationActivity.this, HomeActivity.class));
            //finish();
        } else {
            if (!isValid()) {
                Util.initToast(getContext(), "Fill All Fields, please");
            } else {
                Validation.ValidMessage msg = Validation.checkPasswordIsValid(password.getText().toString());
                if (!msg.state) {
                    password.setError(msg.message);
                } else if (!Validation.checkRePasswordValid(password.getText().toString(), repassword.getText().toString())) {
                    repassword.setError("Re-enter password again");
                    repassword.setText(null);

                } else if (!checkEmailIsValid(email.getText().toString())) {
                    email.setError("Email is not valid, please enter your email again.");
                    email.setText(null);
                } else {
                    // Complete Sign Up

                    userModel.setName(name.getText().toString());
                    userModel.setEmail(email.getText().toString());
                    userModel.setPhone(phone.getText().toString());
                    userModel.setPassword(password.getText().toString());
                    imageButtonLayout.setVisibility(View.VISIBLE);
                    signUpButton.setImageDrawable(likeDrawable);

                }
            }
        }
    }
    private void bindViews(SignUpFragment context){
        signUpButton.setOnClickListener(context);
        takePhotoButton.setOnClickListener(context);
        selectPhotoButton.setOnClickListener(context);
    }

    private boolean isValid() {
        if (name.getText().toString().isEmpty() || name.getText() == null) {
            name.setError(requiredMessage);
            return false;
        }
        if (password.getText().toString().isEmpty() || password.getText() == null) {
            password.setError(requiredMessage);
            return false;
        }
        if (repassword.getText().toString().isEmpty() || repassword.getText() == null) {
            repassword.setError(requiredMessage);
            return false;
        }
        if (phone.getText().toString().isEmpty() || phone.getText() == null) {
            phone.setError(requiredMessage);
            return false;
        }
        if (email.getText().toString().isEmpty() || email.getText() == null) {
            email.setError(requiredMessage);
            return false;
        }


        return true;
    }
    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);


        //dialog.showProgressDialog("Title","Loading",false);


        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //dialog.hideProgressDialog();

                        dialog.hideProgressDialog();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        onAuthSuccess(user);
                        //dialog.hideProgressDialog();

                    }
                })
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:complete");


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        }
                        // [START_EXCLUDE]

                        // [END_EXCLUDE]
                    }
                });

        // [END create_user_with_email]
    }

    private void writeNewUser(UserModel user) {
        Log.v(TAG,user.toString());
        DatabaseReference ref = DatabaseRefs.mUsersDatabaseReference;
        ref.child(user.getId()).setValue(user);
        CurrentUser.setOurInstance(user);
        //mDatabase.child(user.getId()).setValue(user);

    }
    private void onAuthSuccess(FirebaseUser user) {

        if (request_code == actionIntent.IMAGE_CAMERA_REQUEST) {
            //sendFileFirebase(storageRef, pictureURI, user.getUid());
        } else if (request_code == actionIntent.IMAGE_GALLERY_REQUEST) {
            final String name = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
            StorageReference imageGalleryRef = storageRef.child(name + "_gallery");
            sendFileFirebase(storageRef, pictureURI, imageGalleryRef, user.getUid());
        }
    }
    private void sendFileFirebase(StorageReference storageReference, final Uri file,StorageReference imageGalleryRef, final String user) {
        if (storageReference != null) {

            UploadTask uploadTask = imageGalleryRef.putFile(file);
            Log.v(TAG, "Start Uploading File");

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Util.initToast(getContext(), e.getMessage());
                    Log.v(TAG, "Error");
                    dialog.hideProgressDialog();

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i(TAG, "onSuccess sendFileFirebase");
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    if (downloadUrl != null) {
                        userModel.setImageUrl(taskSnapshot.getMetadata().getDownloadUrl().toString());
                        Log.v(TAG, "Path :" + taskSnapshot.getMetadata().getDownloadUrl());
                        //String username = usernameFromEmail(user.getEmail());

                        userModel.setId(user);
                        dialog.hideProgressDialog();

                        startActivity(new Intent(getActivity(), HomeActivity.class));
                        // Write new user
                        writeNewUser(userModel);
                        Log.v(TAG, "Name :" + taskSnapshot.getMetadata().getName());
                        Util.initToast(getActivity(), "Uploading is Done");
                    }
                    //mFirebaseDatabaseReference.child(CHAT_REFERENCE).push().setValue(chatModel);
                }
            });
        } else {
            //IS NULL
            dialog.hideProgressDialog();

        }
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onSignUpFinishedListener(String email, String password);
    }
}
