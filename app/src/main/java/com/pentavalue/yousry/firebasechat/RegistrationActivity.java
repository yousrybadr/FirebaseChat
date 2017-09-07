package com.pentavalue.yousry.firebasechat;

/*
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pentavalue.yousry.firebasechat.models.UserModel;
import com.pentavalue.yousry.firebasechat.util.ActionIntent;
import com.pentavalue.yousry.firebasechat.util.CircleTransform;
import com.pentavalue.yousry.firebasechat.util.DatabaseRefs;
import com.pentavalue.yousry.firebasechat.util.Util;
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

public class RegistrationActivity extends AppCompatActivity {

    static final String TAG = RegistrationActivity.class.getSimpleName();


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

    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;
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

    @BindString(R.string.fieldRequired)
    String requiredMessage;

    @BindDrawable(R.drawable.icon_like)
    Drawable likeDrawable;
    @BindDrawable(R.drawable.icon_error)
    Drawable errorDrawable;

    //File
    private File filePathImageCamera;

    //Class Models
    private UserModel userModel;

    int request_code;
    //Firebase
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    StorageReference storageRef = storage.getReferenceFromUrl(Util.URL_STORAGE_REFERENCE).child(Util.FOLDER_STORAGE_IMG);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);
        actionIntent = new ActionIntent(RegistrationActivity.this, RegistrationActivity.this);

        //mDatabase = FirebaseDatabase.getInstance().getReference().push().child(Util.ROOT_DATABASE_REFERENCE);

        mAuth = FirebaseAuth.getInstance();
        dialog = new LoadingDialog(RegistrationActivity.this);
        userModel = new UserModel();
        if (requiredMessage == null) {
            requiredMessage = getResources().getString(R.string.fieldRequired);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser !=null){
            if(currentUser.getEmail() !=null || !currentUser.getEmail().isEmpty()){
                //Toast.makeText(getApplicationContext(),currentUser.getEmail(),Toast.LENGTH_LONG).show();
                FirebaseAuth.getInstance().signOut();
            }else {

            }
        }

    }

    public void onTakePictureClick(View view) {
        actionIntent.photoCameraIntent();
    }

    public void onSelectPicture(View view) {
        actionIntent.photoGalleryIntent();
    }

    public void onSignUp(View view) {
        if (imageButtonLayout.getVisibility() == View.VISIBLE) {
            if (bitmap == null || pictureURI == null) {
                photo.setImageDrawable(errorDrawable);
                return;
            }
            dialog.showProgressDialog("Title","Loading...",false);
            createAccount(email.getText().toString(), password.getText().toString());

            //startActivity(new Intent(RegistrationActivity.this, HomeActivity.class));
            //finish();
        } else {
            if (!isValid()) {
                Util.initToast(RegistrationActivity.this, "Fill All Fields, please");
            } else {
                ValidMessage msg = checkPasswordIsValid(password.getText().toString());
                if (!msg.state) {
                    password.setError(msg.message);
                } else if (!checkRePasswordValid(password.getText().toString(), repassword.getText().toString())) {
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
                    floatingActionButton.setImageDrawable(likeDrawable);

                }
            }
        }


    }

    private boolean checkEmailIsValid(String text) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(text);
        return m.matches();

    }

    private boolean checkRePasswordValid(String pass1, String pass2) {
        if (pass1.equals(pass2))
            return true;
        return false;
    }

    public ValidMessage checkPasswordIsValid(String text) {
        ValidMessage valid = new ValidMessage();

        //return true if and only if password:
        //1. have at least eight characters.
        //2. consists of only letters and digits.
        //3. must contain at least two digits.
        if (text.length() < 8) {
            valid.message = "your Password's Length must be greater than 8 letters";
            valid.state = false;
            return valid;
        } else {
            char c;
            int count = 1;
            for (int i = 0; i < text.length() - 1; i++) {
                c = text.charAt(i);
                if (!Character.isLetterOrDigit(c)) {
                    valid.message = "your Password's String must contain one Letter at least";
                    valid.state = false;
                    return valid;
                } else if (Character.isDigit(c)) {
                    count++;
                    if (count < 2) {
                        valid.message = "your Password's Length must consist of one Digit";
                        valid.state = false;
                        return valid;
                    }
                }
            }
        }
        valid.message = "true";
        valid.state = true;
        return valid;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == actionIntent.IMAGE_CAMERA_REQUEST) {
            request_code =requestCode;
            if (resultCode == RESULT_OK) {
                if (actionIntent.getFilePathImageCamera() != null && actionIntent.getFilePathImageCamera().exists()) {
                    //StorageReference imageCameraRef = storageRef.child(actionIntent.getFilePathImageCamera().getName()+"_camera");
                    //sendFileFirebase(imageCameraRef,filePathImageCamera);
                    //showImagePhoto(userModel.getImageUrl());
                    bitmap =BitmapFactory.decodeFile(filePathImageCamera.getPath());
                    photo.setImageBitmap(bitmap);
                    pictureURI = getUriFromFile(actionIntent.getFilePathImageCamera());
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
                        stream = getContentResolver().openInputStream(
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
                    showImagePhoto(userModel.getImageUrl());
            } else {
                //URI IS NULL
            }
        }
        else {
            pictureURI = null;
        }
    }





    private Uri getUriFromFile(File file){
        Uri photoURI = FileProvider.getUriForFile(RegistrationActivity.this,
                BuildConfig.APPLICATION_ID + ".provider",
                file);
        return photoURI;
    }
    private void sendFileFirebase(StorageReference storageReference, Uri photoURI,final String user){
        if (storageReference != null){

            UploadTask uploadTask = storageReference.putFile(photoURI);
            Log.v(TAG,"Start Uploading File");
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Util.initToast(RegistrationActivity.this,e.getMessage());
                    Log.v(TAG,"Error in Upload Operation");
                    dialog.hideProgressDialog();

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i(TAG,"onSuccess sendFileFirebase");
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    if (downloadUrl != null) {
                        userModel.setImageUrl(taskSnapshot.getMetadata().getPath());

                        Log.v(TAG,"Finish Uploading File");

                        //String username = usernameFromEmail(user.getEmail());

                        userModel.setId(user);

                        // Write new user
                        writeNewUser(userModel);

                        dialog.hideProgressDialog();
                        startActivity(new Intent(RegistrationActivity.this,HomeActivity.class));

                        Util.initToast(RegistrationActivity.this, "Uploading is Done");
                    }
                }
            });
        }else{
            //IS NULL
        }

    }
    private void sendFileFirebase(StorageReference storageReference, final Uri file,StorageReference imageGalleryRef, final String user){
        if (storageReference != null){

            UploadTask uploadTask = imageGalleryRef.putFile(file);
            Log.v(TAG,"Start Uploading File");

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Util.initToast(RegistrationActivity.this,e.getMessage());
                    Log.v(TAG,"Error");
                    dialog.hideProgressDialog();

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i(TAG,"onSuccess sendFileFirebase");
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    if (downloadUrl != null) {
                        userModel.setImageUrl(taskSnapshot.getMetadata().getDownloadUrl().toString());
                        Log.v(TAG,"Path :"+taskSnapshot.getMetadata().getDownloadUrl());
                        //String username = usernameFromEmail(user.getEmail());

                        userModel.setId(user);
                        dialog.hideProgressDialog();

                        startActivity(new Intent(RegistrationActivity.this,HomeActivity.class));
                        // Write new user
                        writeNewUser(userModel);
                        Log.v(TAG,"Name :"+taskSnapshot.getMetadata().getName());
                        Util.initToast(RegistrationActivity.this, "Uploading is Done");
                    }
                    //mFirebaseDatabaseReference.child(CHAT_REFERENCE).push().setValue(chatModel);
                }
            });
        }else{
            //IS NULL
            dialog.hideProgressDialog();

        }


    }
    private void showImagePhoto(String name){
        Glide.with(this)
                .load(name)
                .centerCrop()
                .transform(new CircleTransform(this))
                .into(photo);

    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);


        //dialog.showProgressDialog("Title","Loading",false);


        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //dialog.hideProgressDialog();

                        Toast.makeText(RegistrationActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        dialog.hideProgressDialog();
                    }
                })
                .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        onAuthSuccess(user);
                        //dialog.hideProgressDialog();

                    }
                })
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:complete");


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [START_EXCLUDE]

                        // [END_EXCLUDE]
                    }
                });

        // [END create_user_with_email]
    }

    private void onAuthSuccess(FirebaseUser user) {

        if(request_code == actionIntent.IMAGE_CAMERA_REQUEST){
            sendFileFirebase(storageRef,pictureURI,user.getUid());
        }else if(request_code == actionIntent.IMAGE_GALLERY_REQUEST){
            final String name = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
            StorageReference imageGalleryRef = storageRef.child(name+"_gallery");
            sendFileFirebase(storageRef,pictureURI,imageGalleryRef,user.getUid());
        }



        // Go to MainActivity

    }

    private void writeNewUser(UserModel user) {
        Log.v(TAG,user.toString());
        DatabaseReference ref = DatabaseRefs.mUsersDatabaseReference;
        ref.child(user.getId()).setValue(user);
        //mDatabase.child(user.getId()).setValue(user);

    }

    public void onBack(View view) {
        super.onBackPressed();
    }

    public class ValidMessage
    {
        public ValidMessage(){
            message = "";
            state =false;
        }
        public String message;
        public boolean state;
    }
}
*/