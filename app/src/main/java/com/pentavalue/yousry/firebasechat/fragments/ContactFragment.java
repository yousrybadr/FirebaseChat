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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pentavalue.yousry.firebasechat.R;
import com.pentavalue.yousry.firebasechat.adapters.ContactAdapter;
import com.pentavalue.yousry.firebasechat.models.Contact;
import com.pentavalue.yousry.firebasechat.models.CurrentUser;
import com.pentavalue.yousry.firebasechat.models.UserModel;
import com.pentavalue.yousry.firebasechat.util.DatabaseRefs;
import com.pentavalue.yousry.firebasechat.util.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ContactFragment extends Fragment {
    public static final String TAG = ContactFragment.class.getSimpleName();

    private List<Contact> contactList;

    private List<UserModel> userModels;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;


    Unbinder unbinder ;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_contacts, container, false);
        unbinder = ButterKnife.bind(this,view);
        //contactList =Util.ReadAllContacts(getContext());
        //contactAdapter =new ContactAdapter(contactList,getContext());
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        contactList=new ArrayList<>();
        userModels =new ArrayList<>();
        //recyclerView.setAdapter(contactAdapter);
        recyclerView.setLayoutManager(llm);
        ReloadAllContacts();



       /* refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.

                ReloadAllContacts();
            }
        });
        // Configure the refreshing colors
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
*/


        // Inflate the layout for this fragment
        return view;
    }

    private void ReloadAllContacts() {
        DatabaseReference mFirebaseDatabaseReference = DatabaseRefs.mUsersDatabaseReference;
        Query query = mFirebaseDatabaseReference;

        //contactList =Util.ReadAllContacts(getContext());
        contactList.clear();
        Log.v(TAG,"Contacts Size is :"+contactList.size());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    UserModel userModel=snapshot.getValue(UserModel.class);

                    if(userModel.getId().equals(CurrentUser.getInstance().getUserModel().getId())){
                        continue;
                    }
                    Contact contact =new Contact();
                    contact.setContact_name(userModel.getName());
                    contact.setEmail(userModel.getEmail());
                    contact.setMessengerContact(true);
                    contact.setPhone_number(userModel.getPhone());

                    Log.v(TAG,userModel.toString());
                    userModels.add(userModel);
                    contactList.add(contact);
                    //refreshLayout.setRefreshing(false);


                }
                contactAdapter =new ContactAdapter(contactList,userModels,getContext());
                contactAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(contactAdapter);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG,databaseError.getMessage());
            }
        });





       /* try {
            for (int i=0;i< 10;i++){
                final Contact contact=contactList.get(i);
                Log.v(TAG,"Line :" + i + "- Contact is "+ contact);

                query =query.equalTo(contact.getPhone_number());

                final int finalI = i;
                ValueEventListener phoneListner =new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserModel userModel =dataSnapshot.getValue(UserModel.class);
                        contact.setMessengerContact(true);
                        contact.setUserId(userModel.getId());
                        contact.setEmail(userModel.getEmail());
                        contactList.set(finalI,contact);
                        Log.v(TAG,"Reloading Start\nUser is" +dataSnapshot.toString());
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG,"Error : "+databaseError.getMessage());
                        refreshLayout.setRefreshing(false);

                    }
                };
                query.addListenerForSingleValueEvent(phoneListner);

            }
        }catch (DatabaseException ex){
            Log.e(TAG,ex.getMessage());
        }catch (IllegalArgumentException ex){
            Log.e(TAG,ex.getMessage());
        }*/
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
