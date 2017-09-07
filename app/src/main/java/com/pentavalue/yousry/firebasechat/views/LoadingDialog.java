package com.pentavalue.yousry.firebasechat.views;

import android.app.ProgressDialog;
import android.content.Context;

import com.pentavalue.yousry.firebasechat.R;

/**
 * Created by yousry on 9/4/2017.
 */

public class LoadingDialog {

    private ProgressDialog mProgressDialog;
    private Context context;

    public LoadingDialog(Context context) {
        this.context =context;
        this.mProgressDialog =new ProgressDialog(context);
        mProgressDialog.setIcon(R.drawable.icon_chat);

    }


    public void setTitle(String title){
        mProgressDialog.setTitle(title);
    }
    public void setMessage(String msg){
        mProgressDialog.setMessage(msg);
    }


    public void showProgressDialog(String Title,String Message,boolean Cancelable) {
        if (mProgressDialog == null) {
            mProgressDialog =new ProgressDialog(context);
            mProgressDialog.setIcon(R.drawable.icon_chat);

        }

        mProgressDialog.setCancelable(Cancelable);
        setMessage(Message);
        setTitle(Title);

        mProgressDialog.show();
    }
    public void showProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
