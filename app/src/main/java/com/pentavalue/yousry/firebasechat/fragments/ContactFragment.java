package com.pentavalue.yousry.firebasechat.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ExpandedMenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.pentavalue.yousry.firebasechat.R;
import com.pentavalue.yousry.firebasechat.activities.ChatActivity;
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

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ContactFragment extends Fragment {
    public static final String TAG = ContactFragment.class.getSimpleName();

    static List<String> names;
    static List<Contact> contactList;
    static List<Contact> chatContacts;

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    DatabaseReference ref =DatabaseRefs.mChatsDatabaseReference;
    @BindView(R.id.name_contact)
    TextView name;
    @BindView(R.id.phone_contact)
    TextView phone;
    @BindView(R.id.image_contact)
    CircleImageView photo;
    @BindView(R.id.invite_button_contact)
    Button inviteButton;
    @BindView(R.id.item_contact)
    LinearLayout item_contact;
    @BindView(R.id.cancel)
    ImageButton cancelButton;
    @BindView(R.id.viewFlipper)
    ViewFlipper viewFlipper;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.search_view)
    MaterialSearchView searchView;
    @BindView(R.id.toolbar_container)
    FrameLayout frameLayout;

    @BindDrawable(R.drawable.icon_user)
    Drawable drawable;

    Unbinder unbinder;

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
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        unbinder = ButterKnife.bind(this, view);

        viewFlipper.setDisplayedChild(0);

        frameLayout.setVisibility(View.GONE);

        names =new ArrayList<>();
        chatContacts = new ArrayList<>();
        contactList =new ArrayList<>();


        Log.v(TAG, "Start Fragment : " + TAG);
        //contactList =Util.ReadAllContacts(getContext());
        //contactAdapter =new ContactAdapter(contactList,getContext());
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        //contactList = Util.ReadAllContacts(getContext());

        showContacts();

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
                    showContacts();
                    //Collections.sort(contactList);
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
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.fragment_search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        searchView.setVoiceSearch(true);

        searchView.setSuggestions( names.toArray(new String[0]));

        searchView.setCursorDrawable(R.drawable.custom_cursor);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic

                if(query == null || query.isEmpty()){
                    return false;
                }

                final Contact contact =getSelectedNameFormContact(query);
                if(contact == null){
                    Toast.makeText(getContext(),"Contact is not exist",Toast.LENGTH_SHORT).show();

                    viewFlipper.setDisplayedChild(0);
                    return false;
                }
                viewFlipper.setDisplayedChild(1);
                name.setText(contact.getContact_name());
                phone.setText(contact.getPhone_number());
                if(contact.getUserModel() != null && contact.isMessengerContact()){
                    inviteButton.setVisibility(View.GONE);
                    Glide.with(getContext()).load(contact.getUserModel().getImageUrl()).into(photo);
                }else{
                    photo.setImageDrawable(drawable);
                    inviteButton.setVisibility(View.VISIBLE);

                }
                item_contact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.v(TAG,contact.toString());
                        if(contact.isMessengerContact()){
                            if(contact.getChatID().isEmpty() || contact.getChatID() == null){
                                ref = ref.push();
                                String chatID =ref.getKey();
                                contact.setChatID(chatID);
                                startActivity(new Intent(getContext(), ChatActivity.class)
                                        .putExtra(Util.CONTACT_KEY_MODEL,contact)
                                        .putExtra(Util.FIRST_TIME_KEY,true)
                                );
                            }else{
                                startActivity(new Intent(getContext(), ChatActivity.class)
                                        .putExtra(Util.CHAT_KEY_MODEL,contact.getChatID())
                                );
                            }



                        }
                    }
                });
                inviteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri uri = Uri.parse("smsto:"+contact.getPhone_number());


                        Intent sendIntent = new Intent(Intent.ACTION_VIEW,uri);
                        sendIntent.putExtra("sms_body", "http://www.pentavalue.com/home/invitation/downlaodapk.php");
                        sendIntent.setType("vnd.android-dir/mms-sms");
                        startActivity(sendIntent);
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        name.setText(null);
                        phone.setText(null);
                        photo.setImageDrawable(drawable);
                        viewFlipper.setDisplayedChild(0);
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
                frameLayout.setVisibility(View.VISIBLE);

            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
                frameLayout.setVisibility(View.GONE);
                //viewFlipper.setDisplayedChild(0);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                //showContacts();
                showContacts();
            } else {
                //Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void checkIsMessengerOrNot() {
        chatContacts.clear();
        DatabaseReference ref = DatabaseRefs.mUsersDatabaseReference;
        if(contactList.size() ==0 || contactList == null ){
            Toast.makeText(getContext(),"You have not Contacts",Toast.LENGTH_SHORT).show();
            return;
        }
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
                                contactList.get(i).setUserModel(userModel);
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
                    try{
                        Chat chat = snapshot.getValue(Chat.class);
                        if(chat.getMembers().size() == 0){
                            DatabaseRefs.mChatsDatabaseReference.child(chat.getId()).removeValue();
                        }
                        if(chat.getMembers().size() == 1){
                            continue;
                        }
                        for (int i = 0; i < contactList.size(); i++) {
                            if (contactList.get(i).isMessengerContact()) {
                                if (chat.getMember(0).equals(curUser) && chat.getMember(1).equals(contactList.get(i).getUserModel().getId())) {
                                    contactList.get(i).setChatID(chat.getId());
                                } else if (chat.getMember(1).equals(curUser) && chat.getMember(0).equals(contactList.get(i).getUserModel().getId())) {
                                    contactList.get(i).setChatID(chat.getId());
                                }
                            }
                        }
                    }catch (IndexOutOfBoundsException | RuntimeExecutionException | NullPointerException ex){
                        Toast.makeText(getContext(),"Error : " +ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }

                }
                try{
                    loadInRecyclurView();
                }catch (NullPointerException | RuntimeExecutionException ex){
                    Toast.makeText(getContext(),"Error :"+ex.getMessage(),Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showContacts(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getContext().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            contactList = ReadAllContacts(getContext());
            Collections.sort(contactList);

            for(Contact name : contactList){
                names.add(name.getContact_name());
            }


        }
    }

    public List<Contact> ReadAllContacts(Context context){
        List<Contact> contacts =new ArrayList<>();

        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                Contact contact =new Contact();
                contact.setContact_id(cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID)));
                contact.setContact_name(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));

                if (Integer.parseInt(cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{contact.getContact_id()}, null);
                    while (pCur.moveToNext()) {

                        contact.setPhone_type(pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)));
                        contact.setPhone_number(pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER)));
                        if(contact.getPhone_type() == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE){
                            contact.setMobile(true);
                            break;
                        }

                    }
                    pCur.close();
                }

                contacts.add(new Contact(contact));


            }
        }
        return contacts;
    }

    void loadOffline() {
        Toast.makeText(getContext(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        showContacts();
        contactAdapter = new ContactAdapter(contactList, getContext());
        recyclerView.setAdapter(contactAdapter);

    }

    private Contact getSelectedNameFormContact(String s){
        for (Contact contact :contactList){
            if(contact.getContact_name().equals(s)){
                return contact;
            }
        }
        return null;
    }

}
