package com.shubh.androidchatter.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shubh.androidchatter.Activity.OfflineUserChatActivity;
import com.shubh.androidchatter.Activity.SetUpProfileActivity;
import com.shubh.androidchatter.Activity.UserChatActivity;
import com.shubh.androidchatter.adapters.RecycleViewAdapterChatList;
import com.shubh.androidchatter.database.ChatsDbHelper;
import com.shubh.androidchatter.informationModels.Chats;
import com.shubh.androidchatter.MyApplication;
import com.shubh.androidchatter.R;
import com.shubh.androidchatter.informationModels.MessagesModel;
import com.shubh.androidchatter.informationModels.User;
import com.shubh.androidchatter.adapters.fireBaseAdapterForChatList;
import com.shubh.androidchatter.extra_classes.DbKeys;
import com.shubh.androidchatter.extra_classes.FirebaseInctence;
import com.shubh.androidchatter.extra_classes.FirebaseNotificationServices;
import com.shubh.androidchatter.extra_classes.FirebaseViewHolderForChatList;
import com.shubh.androidchatter.extra_classes.RecyclerTouchListner;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.shubh.androidchatter.extra_classes.DbKeys.PREFEMAIL;
import static com.shubh.androidchatter.extra_classes.DbKeys.PREFNAME;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatsListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private int SIGN_IN_REQUEST_CODE=11;
    private RecyclerView recyclerview;
    public RecycleViewAdapterChatList adaptor;
    ArrayList<Chats> ChatList;
    FirebaseDatabase database = new FirebaseInctence().getinstence();
    public String getEmail;
    private SwipeRefreshLayout mSwiperefresh;
    private Context context;
    DatabaseReference users=database.getReference("users");
    public DatabaseReference msg;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public ChatsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatsListFragment newInstance(String param1, String param2) {
        ChatsListFragment fragment = new ChatsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout= inflater.inflate(R.layout.fragment_chats, container, false);
        users.keepSynced(true);
        msg=database.getReference("masseges");
        mSwiperefresh=(SwipeRefreshLayout)layout.findViewById(R.id.sr_refresh);
        mSwiperefresh.setOnRefreshListener(this);
        recyclerview=(RecyclerView)layout.findViewById(R.id.list_of_messages);
        FirebaseApp.initializeApp(getActivity());
        FacebookSdk.sdkInitialize(getActivity());
        ChatList=new ArrayList<>();
        context=getActivity();
        adaptor=new RecycleViewAdapterChatList(context,ChatList,getResources());
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );
        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast
            Toast.makeText(context,
                    "Welcome " + FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getEmail()
                    ,
                    Toast.LENGTH_LONG)
                    .show();
            checkExistence(FirebaseAuth.getInstance().getCurrentUser().getEmail(),FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            readChatsListFromDB();
        }
        return layout;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(context, "Successfully signed in. Welcome!", Toast.LENGTH_LONG).show();
                checkExistence(FirebaseAuth.getInstance().getCurrentUser().getEmail(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            } else {
                Toast.makeText(context, "We couldn't sign you in. Please try again later.", Toast.LENGTH_LONG).show();
                // Close the app
                getActivity().finish();
            }
        }

    }

    private void checkExistence(final String email,final String name) {

        final Context context=this.context;
        users.keepSynced(true);
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            boolean e=false;
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("Count " ,""+snapshot.getChildrenCount());
                if(snapshot.getChildrenCount()==0){
                    e=false;
                }
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    User post = postSnapshot.getValue(User.class);
                    Log.e("Get Data", post.getEmail());
                    if(post.getEmail().equals(email)){
                        Log.d("user already exisit","hai user");
                        e=true;
                        break;
                    }
                    else{
                        Log.d("user not exisit","ni hai");
                    }
                }
                setcreateUser();
            }
            void setcreateUser()
            {
                if(!e){
                    Log.d("USERs", "USER creContext ated");
                    User u=new User(name,email,false," ",new Date().getTime(),"hello i am new here");
                    users.keepSynced(true);
                    users.push().setValue(u).addOnCompleteListener(
                            new
                                    OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                    saveToPref(context,PREFEMAIL,email);
                    saveToPref(context,PREFNAME,name);
                    saveToPref(context, DbKeys.PREFPROFILE," ");
                    Intent i=new Intent(getActivity(), SetUpProfileActivity.class);
                    startActivity(i);
                    //readChatsListFromServer();
                    getActivity().finish();
                }
                else {
                    saveToPref(context,PREFEMAIL,email);
                    saveToPref(context,PREFNAME,name);
                    //saveToPref(context, DbKeys.PREFPROFILE," ");

                }

                Intent ll24Service = new Intent(context, FirebaseNotificationServices.class);
                context.startService(ll24Service);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("The read failed: " ,databaseError.getMessage());
            }
        });
    }
    private void updateChatList() {

        ChatsDbHelper dbHelper=new ChatsDbHelper(context);
        SQLiteDatabase database=dbHelper.getReadableDatabase();
        Cursor cursor=dbHelper.readChatFromLocalDb(database);
        ChatList.clear();
        while (cursor.moveToNext()){
            String chatTo=cursor.getString(cursor.getColumnIndex(DbKeys.CHAT_TO));
            String chatName=cursor.getString(cursor.getColumnIndex(DbKeys.CHAT_NAME));
            int syncState=cursor.getInt(cursor.getColumnIndex(DbKeys.SYNC_STATUS));
            ChatList.add(new Chats(chatName,chatTo,(syncState==1)?true:false));
            Log.d("msgdata",chatName+" "+chatName+" "+chatTo+" "+syncState);
        }
        dbHelper.close();
        cursor.close();
        mSwiperefresh.setRefreshing(false);
         adaptor.notifyDataSetChanged();
    }
    public void readChatsListFromDB(){

        recyclerview.setAdapter(adaptor);
        recyclerview.setLayoutManager(new LinearLayoutManager(context));
        updateChatList();
        addListenere();
        recyclerview.addOnItemTouchListener(new RecyclerTouchListner(context, recyclerview, new RecyclerTouchListner.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                TextView email=(TextView)view.findViewById(R.id.tv_chatEmail);
                TextView name=(TextView)view.findViewById(R.id.tv_chatname);
                //updateSycnState();
                Intent i=new Intent(context,OfflineUserChatActivity.class);
                i.putExtra("sendTo",email.getText().toString());
                Log.d("Chat To",email.getText().toString());
                i.putExtra("ChatName",name.getText().toString());
                startActivity(i);
            }
            @Override
            public void onLongClick(final View view,final int postion) {
                PopupMenu popup = new PopupMenu(getActivity(),view);
                //inflating menu from xml resource
                popup.inflate(R.menu.popup_chatlist);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu1:
                                TextView email=(TextView)view.findViewById(R.id.tv_chatEmail);
                                ChatsDbHelper dbHelper=new ChatsDbHelper(context);
                                SQLiteDatabase database=dbHelper.getWritableDatabase();
                                dbHelper.deleteChat(email.getText().toString(),database);
                                ChatList.remove(postion);
                                Toast.makeText(context,"chat deleted",Toast.LENGTH_LONG).show();
                                adaptor.notifyDataSetChanged();
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
            }
        }));
    }
    private void addListenere(){
        Log.e("setupService Notif","setup");
        final DatabaseReference msgList =msg.child("convensation").child(TohashCode(readPrefes(context,PREFEMAIL,null)));
        msgList.keepSynced(true);
        msgList.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("onmyChildAddedService", dataSnapshot.toString()+" ");
                Log.e("onmycountService", dataSnapshot.getChildrenCount()+" ");
                if(dataSnapshot != null){
                    MessagesModel msg=dataSnapshot.getValue(MessagesModel.class);
                    if(!msg.getMessageSyncState()){
                        if(!msg.getMessageUser().equals(readPrefes(context,DbKeys.PREFWHICHCHAT,"no"))){
                            checkChatInDb(msg);
                            updateChatList();
                        }

                    }
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("onChildChanged", dataSnapshot.toString()+" ");
                if(dataSnapshot != null){
                    updateChatList();
                }
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("onChildRemoved",dataSnapshot.toString());

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d("onChildMoved",dataSnapshot.toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("onCancelled",databaseError.toString());
            }
        });
    }
    private void checkChatInDb(MessagesModel m) {
        ChatsDbHelper dbHelper=new ChatsDbHelper(context);
        SQLiteDatabase database=dbHelper.getReadableDatabase();
        if(dbHelper.CheckChatExist(m.getMessageUser(),database)){
            dbHelper.saveChatToLocalDb(m.getSenderUserName(),m.getMessageUser(),0,database);
        }
    }

    private String TohashCode(String code){
        String str1=code;
        Log.d("hashcodeServeice",str1);
        int hash1=(str1.hashCode());
        Log.d("hashcode1",hash1+"");
        return hash1+"";
    }
    public static String readPrefes(Context context, String prefesName, String defaultValue){
        SharedPreferences sharedPrefs= MyApplication.preferences;
        return sharedPrefs.getString(prefesName,defaultValue);
    }
    @Override
    public void onResume() {
        super.onResume();
        updateChatList();
        Log.e("on resume","chatlist");
    }

    public void updateMyLogicOrView(){
        Log.e("on onrefresh view","chatlist");
        updateChatList();
    }

    public static void saveToPref(Context context, String preferenceName, String preferenceValue ){
        SharedPreferences sheredPreference= MyApplication.preferences;
        SharedPreferences.Editor editor=sheredPreference.edit();
        editor.putString(preferenceName,preferenceValue);
        editor.apply();
    }

    @Override
    public void onRefresh() {
        updateChatList();
    }
}