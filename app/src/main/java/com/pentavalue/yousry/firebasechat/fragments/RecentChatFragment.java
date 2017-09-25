package com.pentavalue.yousry.firebasechat.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.pentavalue.yousry.firebasechat.R;
import com.pentavalue.yousry.firebasechat.activities.ChatActivity;
import com.pentavalue.yousry.firebasechat.activities.HomeActivity;
import com.pentavalue.yousry.firebasechat.adapters.RecentChatAdapter;
import com.pentavalue.yousry.firebasechat.adapters.UserListAdapter;
import com.pentavalue.yousry.firebasechat.holders.RecentChatHolder;
import com.pentavalue.yousry.firebasechat.models.Chat;
import com.pentavalue.yousry.firebasechat.models.CurrentUser;
import com.pentavalue.yousry.firebasechat.models.UserModel;
import com.pentavalue.yousry.firebasechat.util.DatabaseRefs;
import com.pentavalue.yousry.firebasechat.util.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class RecentChatFragment extends Fragment {

    public static final String TAG = RecentChatFragment.class.getSimpleName();

    private static final int REMOVED_ITEM = 0;
    private static final int VIEW_ITEM = 1;

    private List<Chat> chatList;
    private List<UserModel> userModels;

    public @BindView(R.id.empty_view)
    TextView emptyView;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<Chat, RecentChatHolder> mAdapter;

    private RecentChatAdapter recentChatAdapter;
    private LinearLayoutManager mManager;


    Unbinder unbinder ;




    public RecentChatFragment() {
        // Required empty public constructor
    }
    static RecentChatFragment fragment;

    public static RecentChatFragment newInstance() {

        fragment =new RecentChatFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recent_chats, container, false);
        unbinder = ButterKnife.bind(this,view);


        setRetainInstance(true);
        chatList =new ArrayList<>();

        mDatabase = DatabaseRefs.mChatsDatabaseReference;
        // [END create_database_reference]
        Log.v(TAG,"Start Fragment : "+TAG);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.

                if (!Util.verifyNetworkConnection(getContext())) {
                    Toast.makeText(getContext(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    refreshLayout.setRefreshing(false);
                } else {
                    getFragmentManager()
                            .beginTransaction()
                            .detach(fragment)
                            .attach(fragment)
                            .commit();
                    refreshLayout.setRefreshing(false);
                }
            }
        });
        // Configure the refreshing colors
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        //mManager.setReverseLayout(true);
        //mManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        //Query postsQuery = getQuery(mDatabase);
        recentChatAdapter =new RecentChatAdapter(mDatabase, getContext(), new RecentChatAdapter.OnSizeChanged() {
            @Override
            public void onSizeChanged(int size) {
                if(size >0){
                    emptyView.setVisibility(View.GONE);
                }else {
                    emptyView.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(),"Size = " + size, Toast.LENGTH_SHORT).show();

                }

            }
        });




        recyclerView.setAdapter(recentChatAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        /*mAdapter = new FirebaseRecyclerAdapter<Chat, RecentChatHolder>(Chat.class, R.layout.recent_chat_item,
                RecentChatHolder.class, mDatabase) {
            List<Chat> chats;


            public List<Chat> getChats() {
                return chats;
            }

            public void setChats(List<Chat> chats) {
                this.chats = chats;
            }


        };*/


        /*recentChatAdapter.setSize(new RecentChatAdapter.OnSizeChanged() {
            @Override
            public void onSizeChanged(int size) {
                if(size == 0){
                    emptyView.setVisibility(View.VISIBLE);
                }else{
                    emptyView.setVisibility(View.GONE);
                }
            }
        });*/

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
