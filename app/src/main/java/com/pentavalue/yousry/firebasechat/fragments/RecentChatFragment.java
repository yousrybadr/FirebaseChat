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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.pentavalue.yousry.firebasechat.R;
import com.pentavalue.yousry.firebasechat.activities.ChatActivity;
import com.pentavalue.yousry.firebasechat.adapters.UserListAdapter;
import com.pentavalue.yousry.firebasechat.holders.RecentChatHolder;
import com.pentavalue.yousry.firebasechat.models.Chat;
import com.pentavalue.yousry.firebasechat.models.CurrentUser;
import com.pentavalue.yousry.firebasechat.models.UserModel;
import com.pentavalue.yousry.firebasechat.util.DatabaseRefs;
import com.pentavalue.yousry.firebasechat.util.Util;

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
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<Chat, RecentChatHolder> mAdapter;
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


        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        //Query postsQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<Chat, RecentChatHolder>(Chat.class, R.layout.recent_chat_item,
                RecentChatHolder.class, mDatabase) {
            @Override
            public int getItemViewType(int position) {
                Chat model = getItem(position);
                if(checkUser(model)){
                    return VIEW_ITEM;
                }else{
                    return REMOVED_ITEM;
                }
            }

            @Override
            public RecentChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view;
                if(viewType == VIEW_ITEM){
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_chat_item,parent,false);
                    return new RecentChatHolder(view);
                }else{
                    view =LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_item,parent,false);
                    return new RecentChatHolder(view);
                }
            }

            @Override
            protected void populateViewHolder(final RecentChatHolder viewHolder, final Chat model, final int position) {
                final DatabaseReference chatRef = getRef(position);


                if(getItemViewType(position) == REMOVED_ITEM){
                    return;
                }else{
                    if(CurrentUser.getInstance().getUserModel() ==null){
                        return;
                    }
                    final String chatRefKey = chatRef.getKey();
                    viewHolder.bind(model,getContext());
                    viewHolder.item_chat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Launch PostDetailActivity
                            Intent intent = new Intent(getActivity(), ChatActivity.class)
                                    .putExtra(Util.CHAT_KEY_MODEL,model.getId());
                            //intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, postKey);
                            startActivity(intent);

                        }
                    });

                }
                // Set click listener for the whole post view



            }
        };
        recyclerView.setAdapter(mAdapter);
    }


    boolean checkUser(Chat model){
        for(String item :model.getMembers()){
            if(item.equals(CurrentUser.getInstance().getUserModel().getId())){
                Log.v(RecentChatHolder.class.getSimpleName(),"User is TRUE");
                return true;
            }
        }
        Log.v(RecentChatHolder.class.getSimpleName(),"User is FALSE");
        return false;
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
