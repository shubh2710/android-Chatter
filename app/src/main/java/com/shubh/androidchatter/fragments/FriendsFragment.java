package com.shubh.androidchatter.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shubh.androidchatter.Activity.OfflineUserChatActivity;
import com.shubh.androidchatter.Activity.UserChatActivity;
import com.shubh.androidchatter.adapters.RecycleViewAdapterForFriendsList;
import com.shubh.androidchatter.adapters.RecycleViewAdapterRequestList;
import com.shubh.androidchatter.database.ChatsDbHelper;
import com.shubh.androidchatter.extra_classes.DbKeys;
import com.shubh.androidchatter.informationModels.Chats;
import com.shubh.androidchatter.informationModels.FriendsModel;
import com.shubh.androidchatter.MyApplication;
import com.shubh.androidchatter.R;
import com.shubh.androidchatter.adapters.fireBaseAdapterForFriendsList;
import com.shubh.androidchatter.extra_classes.FirebaseInctence;
import com.shubh.androidchatter.extra_classes.FirebaseViewHolderForFriendsList;
import com.shubh.androidchatter.extra_classes.RecyclerTouchListner;
import com.shubh.androidchatter.informationModels.MessagesModel;

import java.util.ArrayList;
import java.util.HashMap;

import static com.shubh.androidchatter.extra_classes.DbKeys.PREFEMAIL;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ImageView profilePic;

    private RecyclerView recyclerview;
    public RecycleViewAdapterForFriendsList adaptor;
    ArrayList<FriendsModel> FrindsList;
    private SwipeRefreshLayout mSwiperefresh;
    FirebaseDatabase database = new FirebaseInctence().getinstence();
    public String getEmail;
    private Context context;
    DatabaseReference friends=database.getReference("FrindsList");
    DatabaseReference users=database.getReference("users");

    public FriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
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
        View layout=inflater.inflate(R.layout.fragment_friends, container, false);
        users.keepSynced(true);
        context=getActivity();
        mSwiperefresh=(SwipeRefreshLayout)layout.findViewById(R.id.sr_refresh);
        mSwiperefresh.setOnRefreshListener(this);
        recyclerview=(RecyclerView)layout.findViewById(R.id.rv_list_of_friends);
        FrindsList=new ArrayList<>();
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback( ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Log.e("MOVE SWIPE","onMove");
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                Log.e("MOVE SWIPE",viewHolder.getAdapterPosition()+"");

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);

        adaptor=new RecycleViewAdapterForFriendsList(context,FrindsList,context.getResources());
        recyclerview.setAdapter(adaptor);
        recyclerview.setLayoutManager(new LinearLayoutManager(context));
        itemTouchHelper.attachToRecyclerView(recyclerview);
        recyclerview.addOnItemTouchListener(new RecyclerTouchListner(context, recyclerview, new RecyclerTouchListner.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                TextView email=(TextView)view.findViewById(R.id.tv_chatEmail);
                TextView name=(TextView)view.findViewById(R.id.tv_chatname);
                Intent i=new Intent(context,OfflineUserChatActivity.class);
                i.putExtra("sendTo",email.getText().toString());
                Log.d("Chat To",email.getText().toString());
                i.putExtra("ChatName",name.getText().toString());
                checkChatInDb(email.getText().toString(),name.getText().toString());
                startActivity(i);
            }
            @Override
            public void onLongClick(final View view,final int postion) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(getActivity(),view);
                //inflating menu from xml resource
                popup.inflate(R.menu.popup);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu1:

                                break;
                            case R.id.menu2:
                                TextView email=(TextView)view.findViewById(R.id.tv_chatEmail);
                                removeFrnd(email.getText().toString(),postion);
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
        scanFriendList();
        checkMyDb();
        return layout;
    }
    private void checkChatInDb(String email,String name) {
        ChatsDbHelper dbHelper=new ChatsDbHelper(context);
        SQLiteDatabase database=dbHelper.getReadableDatabase();
        if(dbHelper.CheckChatExist(email,database)){
            dbHelper.saveChatToLocalDb(name,email,0,database);
        }
    }
    private void scanFriendList() {
        if(readPrefes(context,PREFEMAIL,null)!=null){
        final DatabaseReference friendList=friends.child("friends").child(TohashCode(readPrefes(context,PREFEMAIL,null)));
        friendList.keepSynced(true);
        friendList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("reading frnds" ,""+snapshot.getChildrenCount());
                FrindsList.clear();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    FriendsModel friend=postSnapshot.getValue(FriendsModel.class);
                    Log.e("Get Data check sycn", friend.getName());
                    FrindsList.add(friend);
                }
                mSwiperefresh.setRefreshing(false);
                adaptor.notifyDataSetChanged();
                updateDataBase();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("The read failed: " ,databaseError.getMessage());
            }
        });
        }
    }
    private void removeFrnd(String email,int pos) {
        updateRequest(email);
        FrindsList.remove(pos);
        scanFriendList();
        adaptor.notifyDataSetChanged();
        ChatsDbHelper dbHelper=new ChatsDbHelper(context);
        SQLiteDatabase database=dbHelper.getWritableDatabase();
        dbHelper.deleteChat(email,database);
    }
    private void updateRequest(final String email) {
        final DatabaseReference RequestListRef=friends.child("friends").child(TohashCode(readPrefes(context,PREFEMAIL,null)));
        RequestListRef.keepSynced(true);
        RequestListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("remove frnds" ,""+snapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    FriendsModel req=postSnapshot.getValue(FriendsModel.class);
                    Log.e("Get Data check sycn", req.getName());
                    if(req.getEmail().equals(email)){
                        RequestListRef.child(postSnapshot.getRef().getKey()).removeValue();
                        Toast.makeText(context,"Friend removed",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("The read failed: " ,databaseError.getMessage());
            }
        });
    }
    public void checkMyDb(){
        if(readPrefes(context,PREFEMAIL,null)!=null){
        final DatabaseReference friendList=friends.child("friends").child(TohashCode(readPrefes(context,PREFEMAIL,null)));
        friendList.keepSynced(true);
        friendList.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                scanFriendList();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        }
    }

    private void updateDataBase() {
        ChatsDbHelper dbHelper=new ChatsDbHelper(context);
        SQLiteDatabase database=dbHelper.getWritableDatabase();
        dbHelper.deleteFriendsUser(database);
        for(FriendsModel f:FrindsList){
            dbHelper.saveFriendToLocalDb(f.getEmail(),database);
        }
        updateChatList();
    }
    private void updateChatList() {
        ChatsDbHelper dbHelper=new ChatsDbHelper(context);
        SQLiteDatabase database=dbHelper.getReadableDatabase();
        Cursor cursor=dbHelper.readFRiendsFromLocalDb(database);
        while (cursor.moveToNext()){
            String chatTo=cursor.getString(cursor.getColumnIndex(DbKeys.FRIENDEMAIL));
            Log.e("FRIENDS IN DB",chatTo+" ");
        }
        dbHelper.close();
        cursor.close();
        adaptor.notifyDataSetChanged();
    }
    public static String readPrefes(Context context, String prefesName, String defaultValue){
        SharedPreferences sharedPrefs= MyApplication.preferences;
        return sharedPrefs.getString(prefesName,defaultValue);
    }
    private String TohashCode(String code){
        String str1=code;
        Log.d("hashcode2",str1);
        int hash1=(str1.hashCode());
        Log.d("hashcode2",hash1+"");
        return hash1+"";
    }
    public void updateMyLogicOrView(){
        Log.e("on onrefresh view","frndlist");
    }

    @Override
    public void onRefresh() {
        scanFriendList();
    }

}
