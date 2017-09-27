package com.pentavalue.yousry.firebasechat.activities;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.pentavalue.yousry.firebasechat.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteMessageActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView)RecyclerView recyclerView;
    @BindView(R.id.refresh)SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_message);
        ButterKnife.bind(this);

    }
}
