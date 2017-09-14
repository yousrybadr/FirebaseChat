package com.pentavalue.yousry.firebasechat.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.pentavalue.yousry.firebasechat.R;
import com.pentavalue.yousry.firebasechat.adapters.ContactAdapter;
import com.pentavalue.yousry.firebasechat.adapters.RecentChatAdapter;
import com.pentavalue.yousry.firebasechat.models.Contact;
import com.pentavalue.yousry.firebasechat.models.UserModel;
import com.pentavalue.yousry.firebasechat.util.DatabaseRefs;

import java.util.List;

import butterknife.BindView;
import butterknife.Unbinder;


public class RecentChatFragment extends Fragment {

    public static final String TAG = RecentChatFragment.class.getSimpleName();

    private List<Contact> contactList;

    private List<UserModel> userModels;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;



    Unbinder unbinder ;

    DatabaseReference reference = DatabaseRefs.mUsersDatabaseReference;


    RecentChatAdapter recentChatAdapter;

    public RecentChatFragment() {
        // Required empty public constructor
    }

    public static RecentChatFragment newInstance() {

        return new RecentChatFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.v(TAG,"Start Fragment : "+TAG);

        return inflater.inflate(R.layout.fragment_recent_chats, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
