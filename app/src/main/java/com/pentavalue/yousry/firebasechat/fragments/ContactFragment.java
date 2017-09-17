package com.pentavalue.yousry.firebasechat.fragments;

import android.content.Context;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pentavalue.yousry.firebasechat.R;
import com.pentavalue.yousry.firebasechat.adapters.ContactAdapter;
import com.pentavalue.yousry.firebasechat.adapters.SimpleSectionedRecyclerViewAdapter;
import com.pentavalue.yousry.firebasechat.models.Chat;
import com.pentavalue.yousry.firebasechat.models.Contact;
import com.pentavalue.yousry.firebasechat.models.CurrentUser;
import com.pentavalue.yousry.firebasechat.models.UserModel;
import com.pentavalue.yousry.firebasechat.util.DatabaseRefs;
import com.pentavalue.yousry.firebasechat.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ContactFragment extends Fragment {
    public static final String TAG = ContactFragment.class.getSimpleName();

    static List<Contact> contactList;

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    static List<Contact> chatContacts;

    List<UserModel> userModels;
    Unbinder unbinder;

    DatabaseReference reference = DatabaseRefs.mUsersDatabaseReference;


    ContactAdapter contactAdapter;

    public ContactFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ContactFragment newInstance() {
        ContactFragment fragment = new ContactFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();


    }


    void loadOffline() {
        Toast.makeText(getContext(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        contactList = Util.ReadAllContacts(getContext());
        contactAdapter = new ContactAdapter(contactList, getContext());
        recyclerView.setAdapter(contactAdapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        unbinder = ButterKnife.bind(this, view);

        chatContacts = new ArrayList<>();


        Log.v(TAG, "Start Fragment : " + TAG);
        //contactList =Util.ReadAllContacts(getContext());
        //contactAdapter =new ContactAdapter(contactList,getContext());
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        contactList = Util.ReadAllContacts(getContext());
        //contactAdapter =new ContactAdapter(contactList,getContext());
        //recyclerView.setAdapter(contactAdapter);
        recyclerView.setLayoutManager(llm);
        //ReloadAllContacts();
        if (!Util.verifyNetworkConnection(getContext())) {
            loadOffline();
        } else {
            checkIsMessengerOrNot();
        }


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
                    checkIsMessengerOrNot();
                    refreshLayout.setRefreshing(false);
                }
            }
        });
        // Configure the refreshing colors
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        // Inflate the layout for this fragment
        return view;
    }

    void checkIsMessengerOrNot() {
        chatContacts.clear();
        DatabaseReference ref = DatabaseRefs.mUsersDatabaseReference;
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        Log.v(TAG, "userModel is :" + userModel.toString());
                        for (int i = 0; i < contactList.size(); i++) {
                            Log.v(TAG, "contactModel is :" + contactList.get(i).toString());


                            if(contactList.get(i).getPhone_number().equals(CurrentUser.getInstance().getUserModel().getPhone())){
                                continue;
                            }
                            if (contactList.get(i).getPhone_number().equals(userModel.getPhone())) {

                                contactList.get(i).setMessengerContact(true);
                                Log.v(TAG, contactList.get(i).getPhone_number());
                                contactList.get(i).setUserId(userModel.getId());
                                contactList.get(i).setEmail(userModel.getEmail());
                                contactList.get(i).setImageURL(userModel.getImageUrl());
                                chatContacts.add(contactList.get(i));
                            }
                        }
                        Log.v(TAG, userModel.toString());
                        //refreshLayout.setRefreshing(false);
                    }
                    searchOnChats();

                }catch (NullPointerException ex){
                    Toast.makeText(getContext(),ex.getMessage(),Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }


    void loadInRecyclurView(){
        //This is the code to provide a sectioned list
        List<SimpleSectionedRecyclerViewAdapter.Section> sections =
                new ArrayList<SimpleSectionedRecyclerViewAdapter.Section>();
        //Sections
        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0, "MESSENGER"));
        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(chatContacts.size(), "ALL CONTACTS"));

        Log.v(TAG, "Size is " + chatContacts.size());

        Collections.sort(contactList, new Comparator<Contact>() {
            @Override
            public int compare(Contact contact, Contact t1) {
                boolean b1 = contact.isMessengerContact();
                boolean b2 = t1.isMessengerContact();

                if (b1 != b2) {

                    if (b1 == true) {
                        return -1;
                    }

                    if (b1 == false) {
                        return 1;
                    }
                }
                return 0;
            }
        });
        contactAdapter = new ContactAdapter(contactList, getContext());

        SimpleSectionedRecyclerViewAdapter.Section[] dummy = new SimpleSectionedRecyclerViewAdapter.Section[sections.size()];
        SimpleSectionedRecyclerViewAdapter mSectionedAdapter = new
                SimpleSectionedRecyclerViewAdapter(getContext(), R.layout.section, R.id.section_text, contactAdapter);
        mSectionedAdapter.setSections(sections.toArray(dummy));
        recyclerView.setAdapter(mSectionedAdapter);
    }


    void searchOnChats() {
        final String curUser = CurrentUser.getInstance().getUserModel().getId();
        //final List<Contact> contacts =contactList;
        final DatabaseReference ref = DatabaseRefs.mChatsDatabaseReference;
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    for (int i = 0; i < chatContacts.size(); i++) {
                        if (contactList.get(i).isMessengerContact()) {
                            if (chat.getMember(0).equals(curUser) && chat.getMember(1).equals(contactList.get(i).getUserId())) {
                                contactList.get(i).setChatID(chat.getId());
                            } else if (chat.getMember(1).equals(curUser) && chat.getMember(0).equals(contactList.get(i).getUserId())) {
                                contactList.get(i).setChatID(chat.getId());
                            }
                        }
                    }
                }
                loadInRecyclurView();



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
