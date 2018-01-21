package com.shubh.androidchatter.Activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shubh.androidchatter.MyApplication;
import com.shubh.androidchatter.R;
import com.shubh.androidchatter.adapters.RecycleViewAdapter;
import com.shubh.androidchatter.adapters.fireBaseAdapter;
import com.shubh.androidchatter.database.ChatsDbHelper;
import com.shubh.androidchatter.extra_classes.DbKeys;
import com.shubh.androidchatter.extra_classes.FirebaseInctence;
import com.shubh.androidchatter.extra_classes.FirebaseViewHolder;
import com.shubh.androidchatter.informationModels.FriendsModel;
import com.shubh.androidchatter.informationModels.MessagesModel;
import com.shubh.androidchatter.informationModels.User;
import com.shubh.androidchatter.informationModels.contectReader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.shubh.androidchatter.extra_classes.DbKeys.PREFEMAIL;

public class OfflineUserChatActivity extends AppCompatActivity {


    private LinearLayoutManager linearLayoutManager;
    private RecycleViewAdapter mAdapter;
    private Toolbar toolbar;

private Context context=this;
    private RecyclerView listOfMessages;
    FirebaseDatabase database = new FirebaseInctence().getinstence();
    DatabaseReference msg=database.getReference("masseges");
    DatabaseReference users=database.getReference("users");
    DatabaseReference friends=database.getReference("FrindsList");
    //private FirebaseRecyclerAdapter<Boolean,MessagesModel> adapter;
    private String chatTo;
    private EditText input;
    private String ChatName;
    private ImageView userPic;
    private TextView frndStatus;
    private TextView username;
    private boolean isFrnd;
    private  ChildEventListener myMsgListner=null;
    private  DatabaseReference myMsgRef=null;
    private ArrayList<MessagesModel> messages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_conversation_list);
        linearLayoutManager = new LinearLayoutManager(this);
        username=(TextView)findViewById(R.id.tb_userName);
        frndStatus=(TextView)findViewById(R.id.tv_frndStatus);
        userPic=(ImageView) findViewById(R.id.iv_userPic);
        toolbar=(Toolbar)findViewById(R.id.appbar_user);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ChatName=getIntent().getStringExtra("ChatName");
        username.setText(ChatName);
        getSupportActionBar().setTitle("");
        chatTo=getIntent().getStringExtra("sendTo");
        listOfMessages = (RecyclerView) findViewById(R.id.rcFragemnet_UserConversation);
        getursetDetile(chatTo);
        messages=new ArrayList<>();
        input = (EditText)findViewById(R.id.input);
        mAdapter = new RecycleViewAdapter(this,messages,getResources());
        listOfMessages.setLayoutManager(linearLayoutManager);
        listOfMessages.setAdapter(mAdapter);
        getFriendList(chatTo);
        showChat();
        //scanContacts();
        Log.d("ISTOUCHED","touched");
        listOfMessages.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                listOfMessages.smoothScrollToPosition(mAdapter.getItemCount());
            }
        });
        FloatingActionButton fab =
                (FloatingActionButton)findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        if(input.getText().toString().length()>0){
                            MessagesModel pushMsg=new MessagesModel(input.getText().toString(),
                                    FirebaseAuth.getInstance()
                                            .getCurrentUser()
                                            .getDisplayName(),
                                    ChatName,
                                    FirebaseAuth.getInstance()
                                            .getCurrentUser()
                                            .getEmail(),chatTo,false);

                                    msg.child("convensation").child(TohashCode(chatTo)).push().setValue(pushMsg);

                                    pushInDb(pushMsg.getMessageText(),
                                    pushMsg.getSenderUserName(),
                                    pushMsg.getReciverUserName(),
                                    pushMsg.getMessageUser(),
                                    pushMsg.getMessageSendTo(),
                                    pushMsg.getMessageSyncState(),
                                    pushMsg.getMessageTime(),"send msg add");
                            showChat();
                            input.setText("");
                            //updateConvensation();
                        listOfMessages.scrollToPosition(mAdapter.getItemCount());
                        }
                     }
        });
    }

    private void FrndshipStatus(String chatTo,boolean isfrnd) {
        isFrnd=isfrnd;
        if(isfrnd){
            frndStatus.setVisibility(View.GONE);
        }else{
            frndStatus.setVisibility(View.VISIBLE);
            frndStatus.setText("Request Pending");
        }
    }
    private void getFriendList(final String email) {
        final DatabaseReference friendList=friends.child("friends").child(TohashCode(email));
        friendList.keepSynced(true);
        friendList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean isfound=false;
                Log.e("reading his frnds" ,""+snapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    FriendsModel friend=postSnapshot.getValue(FriendsModel.class);
                    if(friend.getEmail().equals(readPrefes(context,PREFEMAIL,null))){
                        isfound=true;
                    }
                }
                FrndshipStatus(email,isfound);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("The read failed: " ,databaseError.getMessage());
            }
        });
    }

    private void scanContacts() {
            // do something long
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getContect();
            }
        };
            new Thread(runnable).start();
    }

    public void getContect(){
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {

            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Log.e("Contenct","Name: " + name
                                + ", Phone No: " + phoneNo);
                    }
                    pCur.close();
                }
            }
        }
    }

    private void pushInDb(String s, String displayName, String chatName, String email, String chatTo, boolean b,long time,String msg) {
        ChatsDbHelper dbHelper=new ChatsDbHelper(context);
        SQLiteDatabase database=dbHelper.getWritableDatabase();
        dbHelper.saveMsgToLocalDb(s,chatTo,email,chatName,displayName,(b)?1:0,time,database);
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }

    private void getursetDetile(String chatTo) {
        users.keepSynced(true);
        Query query = users.orderByChild("email").equalTo(chatTo);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
               User post = nodeDataSnapshot.getValue(User.class);
                Log.e("User profile", post.getProfileUrl());
                if(post.getProfileUrl()!=" "){
                    Glide.with(context).load(post.getProfileUrl())
                            .placeholder(R.drawable.images)
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFirstResource) {
                                    return false;
                                }
                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    // viewHolder.profilePic.setImageResource();
                                    return false;
                                }

                            })
                            .dontAnimate()
                            .into(userPic);
                }
                else {
                    Picasso.with(context)
                            .load(R.drawable.images)
                            .noFade()
                            .into(userPic);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private String TohashCode(String code){
        String str1=code;
        int hash1=(str1.hashCode());
        Log.d("hashcode>",str1+"    "+hash1+"");
        return hash1+"";
    }

    public static String readPrefes(Context context, String prefesName, String defaultValue){
        SharedPreferences sharedPrefs= MyApplication.preferences;
        return sharedPrefs.getString(prefesName,defaultValue);
    }
    private void showChat() {

        updateMessageList();
        checkNewNode();
        checkMyDb();
        listOfMessages.scrollToPosition(mAdapter.getItemCount());
    }
    private void updateMessageList() {
        ChatsDbHelper dbHelper=new ChatsDbHelper(this);
        SQLiteDatabase database=dbHelper.getReadableDatabase();
        Cursor cursor=dbHelper.readMsgFromLocalDb(database);
        messages.clear();
        while (cursor.moveToNext()){
            String data=cursor.getString(cursor.getColumnIndex(DbKeys.MSG_DATA));
            String sendTo=cursor.getString(cursor.getColumnIndex(DbKeys.SEND_TO));
            String sendFrom=cursor.getString(cursor.getColumnIndex(DbKeys.SEND_FROM));
            String reciver_name=cursor.getString(cursor.getColumnIndex(DbKeys.SEND_TO_NAME));
            String sender_name=cursor.getString(cursor.getColumnIndex(DbKeys.SEND_FROM_NAME));
            int syncState=cursor.getInt(cursor.getColumnIndex(DbKeys.SYNC_STATUS));
            long sendTime=cursor.getLong(cursor.getColumnIndex(DbKeys.MSG_TIME));
            if((sendTo.equals(chatTo) && sendFrom.equals(readPrefes(this,PREFEMAIL,null))) || sendTo.equals(readPrefes(this,PREFEMAIL,null)) && sendFrom.equals(chatTo)){
                messages.add(new MessagesModel(data,sender_name,reciver_name,sendFrom,sendTo,(syncState==1)?true:false,sendTime));
                Log.d("db msgdata",data+" "+sendFrom+" "+sendTo+" "+syncState);
            }
        }
        dbHelper.close();
        cursor.close();
        mAdapter.notifyDataSetChanged();
    }
    public void checkNewNode(){
        Log.e("listener forhis Db","setup");
        final DatabaseReference msgList =msg.child("convensation").child(TohashCode(chatTo));
        msg.keepSynced(true);
        msgList.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("onhisChildAdded", dataSnapshot.toString()+" ");
                if(dataSnapshot != null){
                            MessagesModel msg=dataSnapshot.getValue(MessagesModel.class);
                   /*         if(msg.getMessageUser().equals(chatTo) && msg.getMessageSyncState()){
                                pushInDb(msg.getMessageText(),
                                msg.getSenderUserName(),
                                msg.getReciverUserName(),
                                msg.getMessageUser(),
                                msg.getMessageSendTo(),
                                msg.getMessageSyncState(),
                                msg.getMessageTime());
                                String key = dataSnapshot.getKey();
                                String path = "/" + key;
                                HashMap<String, Object> result = new HashMap<>();
                                result.put("messageSyncState", true);
                                Log.e("update chat",result.toString());
                                msgList.child(path).updateChildren(result);
                                msg.setMessageSyncState(true);
                                messages.add(msg);
                                mAdapter.notifyDataSetChanged();
                        }*/
                    listOfMessages.scrollToPosition(mAdapter.getItemCount());
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("onhisChildChanged", dataSnapshot.toString()+" ");
                if(dataSnapshot != null){
                    mAdapter.notifyDataSetChanged();
                    listOfMessages.smoothScrollToPosition(mAdapter.getItemCount());
                }
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("onhisChildRemoved",dataSnapshot.toString());
                mAdapter.notifyDataSetChanged();
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
    public void checkMyDb(){
        Log.e("listener for chat","setup");
        myMsgRef =msg.child("convensation").child(TohashCode(readPrefes(context,PREFEMAIL,"")));
        myMsgRef .keepSynced(true);
        myMsgListner= new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("onmyChildAdded", dataSnapshot.toString()+" ");
                Log.e("onmyChildAdded count", dataSnapshot.getChildrenCount()+" ");
                if(dataSnapshot != null){
                    MessagesModel msg=dataSnapshot.getValue(MessagesModel.class);
                    if(msg.getMessageUser().equals(chatTo) && !msg.getMessageSyncState()){
                        if(checkMsgExsist(msg.getMessageTime(),msg.getMessageSendTo(),msg.getMessageUser())){
                        pushInDb(msg.getMessageText(),
                                msg.getSenderUserName(),
                                msg.getReciverUserName(),
                                msg.getMessageUser(),
                                msg.getMessageSendTo(),
                                msg.getMessageSyncState(),
                                msg.getMessageTime(),"msg recived");
                        String key = dataSnapshot.getKey();
                        String path = "/" + key;
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("messageSyncState", true);
                        Log.e("update chat",result.toString());
                            myMsgRef.child(path).updateChildren(result);
                        msg.setMessageSyncState(true);
                        messages.add(msg);
                        mAdapter.notifyDataSetChanged();
                        }
                    }
                    listOfMessages.scrollToPosition(mAdapter.getItemCount());
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("onChildChanged", dataSnapshot.toString()+" ");
                if(dataSnapshot != null){

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
        };
        myMsgRef.addChildEventListener(myMsgListner);
    }

    private boolean checkMsgExsist(long messageTime,String to,String from) {
        ChatsDbHelper dbHelper=new ChatsDbHelper(context);
        SQLiteDatabase database=dbHelper.getReadableDatabase();
        return  dbHelper.checkMsgExst(messageTime+"",to,from,database);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_settings:
                Intent i=new Intent(this,SearchUserActivty.class);
                startActivity(i);
                break;
            case R.id.menu_chnage_profile:
                Intent p=new Intent(this,SetUpProfileActivity.class);
                startActivity(p);
                break;
            case R.id.searchBar:
                Intent search=new Intent(this,SearchUserActivty.class);
                startActivity(search);
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
     saveToPref(context,DbKeys.PREFWHICHCHAT,chatTo);
        Log.e("OnresumeChats",chatTo);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(myMsgListner!=null){
            myMsgRef.removeEventListener(myMsgListner);
        }

        Log.e("Onpause Remove list","no");
        saveToPref(context,DbKeys.PREFWHICHCHAT,"no");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(myMsgListner!=null){
            myMsgRef.removeEventListener(myMsgListner);
        }
        Log.e("Ondistroy Remove list","no");
        saveToPref(context,DbKeys.PREFWHICHCHAT,"no");
    }

    public static void saveToPref(Context context, String preferenceName, String preferenceValue ){
        SharedPreferences sheredPreference= MyApplication.preferences;
        SharedPreferences.Editor editor=sheredPreference.edit();
        editor.putString(preferenceName,preferenceValue);
        editor.apply();
    }
}