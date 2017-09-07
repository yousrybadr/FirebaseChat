package com.pentavalue.yousry.firebasechat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pentavalue.yousry.firebasechat.models.CurrentUser;

public class SplashActivity extends AppCompatActivity {
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */


    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    private View mContentView;
    private boolean mVisible;

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        //startService(new Intent(SplashActivity.this,MyService.class));
        //Intent ishintent = new Intent(this, MyService.class);
        //PendingIntent pintent = PendingIntent.getService(this, 0, ishintent, 0);
        //AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //alarm.cancel(pintent);
        //alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),5000, pintent);

        //FirebaseMessaging.getInstance().subscribeToTopic("FCM");
        //startService(new Intent(SplashActivity.this,DeleteTokenService.class));
        //FirebaseInstanceId.getInstance().getToken();



        // Firebase.setAndroidContext(this);

        mVisible = true;

        mContentView = findViewById(R.id.imageSplash);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        final Animation an= AnimationUtils.loadAnimation(getBaseContext(),R.anim.rotate);
        final Animation an2= AnimationUtils.loadAnimation(getBaseContext(), R.anim.abc_fade_out);
        mContentView.startAnimation(an);
        an.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {


                //Intent myIntent=new Intent(SplashActivity.this,MyService.class);
                //startService(myIntent);
               // startService(intent);


                /*if(startService(myIntent) != null) {
                    Toast.makeText(getBaseContext(), "Service is already running", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getBaseContext(), "There is no service running, starting service..", Toast.LENGTH_SHORT).show();
                }*/
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mContentView.startAnimation(an2);
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if(firebaseUser !=null){
                    if(CurrentUser.getInstance().getUserModel() == null || !CurrentUser.getInstance().isChecked){
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(SplashActivity.this,StartActivity.class));
                    }else {
                        startActivity(new Intent(SplashActivity.this,HomeActivity.class));
                    }
                }else {
                    startActivity(new Intent(SplashActivity.this,StartActivity.class));
                }


                //String dirPath = "/storage/sdcard0/MohamedAli/Video";
                /*File projDir = new File(getExternalCacheDir()+File.separator+"Videos");
                if (!projDir.exists())
                    projDir.mkdirs();
                //dirPath = "/storage/sdcard0/MohamedAli/Image";
                File projDir2 = new File(getExternalCacheDir()+File.separator+"Images");
                if (!projDir2.exists())
                    projDir2.mkdirs();*/
                /*File file = new File(projDir +File.separator+"sexXNXX.txt");
                try {
                    FileOutputStream fileOutputStream=openFileOutput("sexXNXX.txt",MODE_PRIVATE);
                    fileOutputStream.write("Hello World!".getBytes());
                    fileOutputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                //finish();
                //startActivity(new Intent(SplashActivity.this,UpdateInfoActivity.class));

                /*SharedPreferences sharedpreferences = getSharedPreferences("User_Preference", Context.MODE_PRIVATE);
                if(!sharedpreferences.contains("initialized")){
                    Intent i = new Intent(getBaseContext(), StartActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
                else
                {
                    Student.setOurInstance(sharedpreferences);
                    Intent i= new Intent(getApplicationContext(),UpdateInfoActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }*/

            }


            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */


    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            getSupportActionBar().show();

        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * Sclhedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
