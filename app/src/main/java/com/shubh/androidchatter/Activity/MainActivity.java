package com.shubh.androidchatter.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shubh.androidchatter.informationModels.Chats;
import com.shubh.androidchatter.MyApplication;
import com.shubh.androidchatter.R;
import com.shubh.androidchatter.informationModels.User;
import com.shubh.androidchatter.adapters.fireBaseAdapterForChatList;
import com.shubh.androidchatter.extra_classes.FirebaseNotificationServices;
import com.shubh.androidchatter.extra_classes.FirebaseViewHolderForChatList;
import com.shubh.androidchatter.extra_classes.RecyclerTouchListner;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.shubh.androidchatter.extra_classes.DbKeys.*;

public class MainActivity extends AppCompatActivity {

    private int SIGN_IN_REQUEST_CODE=11;

    private RecyclerView recyclerview;
    public fireBaseAdapterForChatList adaptor;
    ArrayList<Chats> ChatList;

    Context context=this;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    public String getEmail;
    DatabaseReference chat=database.getReference("chatList");
    DatabaseReference users=database.getReference("users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        FirebaseApp.initializeApp(this);
        FacebookSdk.sdkInitialize(this);
        ChatList=new ArrayList<>();

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
            Toast.makeText(this,
                    "Welcome " + FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getEmail()
                            ,
                    Toast.LENGTH_LONG)
                    .show();
            checkExistence(FirebaseAuth.getInstance().getCurrentUser().getEmail(),FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        }
        // Load chat room contents

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(this, "Successfully signed in. Welcome!", Toast.LENGTH_LONG).show();
                checkExistence(FirebaseAuth.getInstance().getCurrentUser().getEmail(),FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            } else {
                Toast.makeText(this, "We couldn't sign you in. Please try again later.", Toast.LENGTH_LONG).show();
                // Close the app
                finish();
            }
        }

    }

    private void checkExistence(final String email,final String name) {

      final Context context=this;
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
                Log.d("USERs", "USER created");
                User u=new User(name,email,false,"",new Date().getTime(),"");
                users.push().setValue(u);
                    saveToPref(context,PREFEMAIL,email);
                    saveToPref(context,PREFNAME,name);
                    readChatsListFromServer();

            }
            else {
                    saveToPref(context,PREFEMAIL,email);
                    saveToPref(context,PREFNAME,name);
                    readChatsListFromServer();
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

    public void readChatsListFromServer(){
       DatabaseReference chats=chat.child("chats").child(TohashCode(readPrefes(this,PREFEMAIL,null)));
        recyclerview=(RecyclerView)findViewById(R.id.rcFragemnet_chatList);
        adaptor=new fireBaseAdapterForChatList(Chats.class,R.layout.chat_show_list, FirebaseViewHolderForChatList.class,chats,this);
        recyclerview.setAdapter(adaptor);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.addOnItemTouchListener(new RecyclerTouchListner(this, recyclerview, new RecyclerTouchListner.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                TextView email=(TextView)view.findViewById(R.id.tv_chatEmail);
                updateSycnState();
                Intent i=new Intent(context,UserChatActivity.class);
                i.putExtra("sendTo",email.getText().toString());
                startActivity(i);
            }
            @Override
            public void onLongClick(View view, int postion) {
            }
        }));
    }
    private void updateSycnState() {
        final DatabaseReference chatList =chat.child("chats").child(TohashCode(readPrefes(this,PREFEMAIL,null)));
        Query query = chatList.orderByChild("SyncState");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                String path = "/" + key;
                HashMap<String, Object> result = new HashMap<>();
                result.put("SyncState", true);
                Log.d("updating chat",path);
                chatList.child(path).updateChildren(result);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
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

    public static void saveToPref(Context context, String preferenceName, String preferenceValue ){
        SharedPreferences sheredPreference= MyApplication.preferences;
        SharedPreferences.Editor editor=sheredPreference.edit();
        editor.putString(preferenceName,preferenceValue);
        editor.apply();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_sign_out:
                AuthUI.getInstance().signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(MainActivity.this,
                                        "You have been signed out.",
                                        Toast.LENGTH_LONG)
                                        .show();

                                // Close activity
                                finish();
                            }
                        });
                break;
            case R.id.menu_settings:
                Intent i=new Intent(this,SearchUserActivty.class);
                startActivity(i);
                break;
        }
        return true;
    }
}