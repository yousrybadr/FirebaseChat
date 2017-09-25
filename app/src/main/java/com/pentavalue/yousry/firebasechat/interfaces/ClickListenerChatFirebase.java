package com.pentavalue.yousry.firebasechat.interfaces;

import android.view.View;
import android.widget.SeekBar;

import com.pentavalue.yousry.firebasechat.models.Message;

/**
 * Created by Alessandro Barreto on 27/06/2016.
 */
public interface ClickListenerChatFirebase {

    void clickImageMapChat(View view, int position, String latitude, String longitude);

    void clickImageChat(View view, int position, String userId, String urlImage, String time);

    void clickItemChat(View view, int position);

    void longClickItemChat(View view, int postion, Message model, boolean isSelected);

}
