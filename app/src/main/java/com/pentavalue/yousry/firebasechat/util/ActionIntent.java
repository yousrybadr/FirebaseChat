package com.pentavalue.yousry.firebasechat.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.format.DateFormat;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.pentavalue.yousry.firebasechat.BuildConfig;

import java.io.File;
import java.util.Date;

/**
 * Created by yousry on 9/4/2017.
 */

public class ActionIntent {
    private Activity activity;
    private Context context;
    private String mTitle;
    private File filePathImageCamera;


    public int IMAGE_CAMERA_REQUEST =1;
    public int IMAGE_GALLERY_REQUEST =2;
    public int PLACE_PICKER_REQUEST =3;


    public ActionIntent(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
        mTitle ="";
        filePathImageCamera =null;
    }

    public ActionIntent(Activity activity, Context context, String mTitle) {
        this.activity = activity;
        this.context = context;
        this.mTitle = mTitle;
        filePathImageCamera =null;
    }

    /**
     * Open Camera
     */
    public void photoCameraIntent(){
        String nameForPhoto = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
        filePathImageCamera = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), nameForPhoto+"camera.jpg");
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoURI = FileProvider.getUriForFile(context,
                BuildConfig.APPLICATION_ID + ".provider",
                filePathImageCamera);
        it.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
        activity.startActivityForResult(it
                , IMAGE_CAMERA_REQUEST
        );
    }
    /**
     * Open Gallery
     */
    public void photoGalleryIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        if(mTitle.isEmpty() || mTitle == null){
            mTitle = activity.getClass().getSimpleName();
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(
                Intent.createChooser(
                        intent,
                        mTitle
                ), IMAGE_GALLERY_REQUEST
        );
    }

    /**
     * Open Location
     */
    public void locationPlacesIntent(){
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            activity.startActivityForResult(builder.build(activity), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Util.initToast(context,"Error: " +e.getMessage());
        }
    }


    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public File getFilePathImageCamera() {
        return filePathImageCamera;
    }

    public void setFilePathImageCamera(File filePathImageCamera) {
        this.filePathImageCamera = filePathImageCamera;
    }
}
