package com.pentavalue.yousry.firebasechat.activities;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.pentavalue.yousry.firebasechat.R;
import com.pentavalue.yousry.firebasechat.adapters.ChatPagerAdapter;
import com.pentavalue.yousry.firebasechat.fragments.GroupChatFragment;
import com.pentavalue.yousry.firebasechat.models.CurrentUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.imageProfile)
    ImageView imageView;
    @BindView(R.id.title)
    TextView textView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);


        setSupportActionBar(toolbar);
        Glide.with(HomeActivity.this)
                .load(CurrentUser.getInstance().getUserModel().getImageUrl())
                .into(imageView);
        textView.setText(CurrentUser.getInstance().getUserModel().getName());


        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new ChatPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(3);
        // Give the PagerSlidingTabStrip the ViewPager
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void onSignOut(MenuItem item) {
        CurrentUser.setOurInstance(null);
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this,StartActivity.class));
        finish();
    }

    public void onCreateGroup(MenuItem item) {
        startActivity(new Intent(HomeActivity.this, GroupChatFragment.class));
    }
}
