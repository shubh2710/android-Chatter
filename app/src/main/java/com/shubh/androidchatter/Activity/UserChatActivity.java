package com.shubh.androidchatter.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.shubh.androidchatter.informationModels.User;
import com.shubh.androidchatter.adapters.fireBaseAdapter;
import com.shubh.androidchatter.extra_classes.FirebaseInctence;
import com.shubh.androidchatter.extra_classes.FirebaseViewHolder;
import com.shubh.androidchatter.informationModels.MessagesModel;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;

import static com.shubh.androidchatter.extra_classes.DbKeys.PREFEMAIL;

public class UserChatActivity extends AppCompatActivity {


    private LinearLayoutManager linearLayoutManager;
    private fireBaseAdapter mAdapter;
    private Toolbar toolbar;

private Context context=this;
    private RecyclerView listOfMessages;
    FirebaseDatabase database = new FirebaseInctence().getinstence();
    DatabaseReference msg=database.getReference("masseges");
    DatabaseReference chat=database.getReference("chatList");
    DatabaseReference users=database.getReference("users");
    //private FirebaseRecyclerAdapter<Boolean,MessagesModel> adapter;
    private String chatTo;
    private EditText input;
    private String ChatName;
    private ImageView userPic;
    private TextView username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_conversation_list);
        linearLayoutManager = new LinearLayoutManager(this);
        username=(TextView)findViewById(R.id.tb_userName);
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
        showChat();
        encry(chatTo);
        input = (EditText)findViewById(R.id.input);
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
                // Read the input field and push a new instance
                // of ChatMessage to th
                if(input.getText().toString()!=null){
                msg.child("convensation").child(encry(chatTo)+"").push()
                        .setValue(new MessagesModel(input.getText().toString(),
                                FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getDisplayName(),
                                ChatName,
                                FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getEmail(),chatTo,false)
                        );
                    mAdapter.notifyDataSetChanged();
                    input.setText("");
                    updateConvensation();
                listOfMessages.scrollToPosition(mAdapter.getItemCount());
                }
            }
        });
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

    public void updateConvensation(){
        final DatabaseReference chatList =chat.child("chats").child(TohashCode(chatTo));
            chatList.keepSynced(true);
            Log.d("my email",readPrefes(this,PREFEMAIL,null));
            Query query = chatList.orderByChild("chatFor").equalTo(readPrefes(this,PREFEMAIL,null));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                    String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                    String path = "/" + key;
                    HashMap<String, Object> result = new HashMap<>();
                    result.put("syncState", false);
                    result.put("createTime", new Date().getTime());
                    chatList.child(path).updateChildren(result);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    private String TohashCode(String code){
        String str1=code;
        Log.d("hashcode1",str1);
        int hash1=(str1.hashCode());
        Log.d("hashcode1",hash1+"");
        return hash1+"";
    }

    private String encry(String chatTo) {
        String str1=chatTo;
        String str2=readPrefes(this,PREFEMAIL,null);
        if(str2!=null){
        int hash1=(str1.hashCode());
        int hash2=(str2.hashCode());
            Log.d("Chat to code",(hash1+hash2)+"");
        return (hash1+hash2)+"";
        }else return null;
    }
    public static String readPrefes(Context context, String prefesName, String defaultValue){
        SharedPreferences sharedPrefs= MyApplication.preferences;
        return sharedPrefs.getString(prefesName,defaultValue);
    }
    private void showChat() {
        mAdapter = new fireBaseAdapter(listOfMessages,MessagesModel.class, R.layout.message, FirebaseViewHolder.class,msg.child("convensation").child(encry(chatTo)+""),this);
        listOfMessages.setLayoutManager(linearLayoutManager);
        listOfMessages.setAdapter(mAdapter);
        checkNewNode();
        listOfMessages.scrollToPosition(mAdapter.getItemCount());
    }
    public void checkNewNode(){
        final DatabaseReference msgList =msg.child("convensation").child(encry(chatTo)+"");
        msg.keepSynced(true);
        msgList.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot != null){
                    listOfMessages.scrollToPosition(mAdapter.getItemCount());
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("onChildChanged", dataSnapshot.toString()+" ");
                if(dataSnapshot != null){
                    listOfMessages.smoothScrollToPosition(mAdapter.getItemCount());
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
}