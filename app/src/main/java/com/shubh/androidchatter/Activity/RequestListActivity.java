package com.shubh.androidchatter.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shubh.androidchatter.MyApplication;
import com.shubh.androidchatter.R;
import com.shubh.androidchatter.adapters.RecycleViewAdapterChatList;
import com.shubh.androidchatter.adapters.RecycleViewAdapterRequestList;
import com.shubh.androidchatter.extra_classes.FirebaseInctence;
import com.shubh.androidchatter.informationModels.Chats;
import com.shubh.androidchatter.informationModels.FriendsModel;
import com.shubh.androidchatter.informationModels.Requests;

import java.util.ArrayList;

import static com.shubh.androidchatter.extra_classes.DbKeys.PREFEMAIL;

public class RequestListActivity extends AppCompatActivity {

    private RecyclerView recyclerview;
    public RecycleViewAdapterRequestList adaptor;
    ArrayList<Requests> RequestList;
    FirebaseDatabase database = new FirebaseInctence().getinstence();
    DatabaseReference reqList=database.getReference("requests");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_list);
        recyclerview=(RecyclerView)findViewById(R.id.list_of_Requests);
        RequestList=new ArrayList<>();
        adaptor=new RecycleViewAdapterRequestList(this,RequestList,getResources());
        recyclerview.setAdapter(adaptor);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        getRequestList();
    }
    private void getRequestList() {
        final DatabaseReference RequestListRef=reqList.child(TohashCode(readPrefes(this,PREFEMAIL,null)));
        RequestListRef.keepSynced(true);
        RequestListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("read Requests" ,""+snapshot.getChildrenCount());
                RequestList.clear();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Requests friend=postSnapshot.getValue(Requests.class);
                    Log.e("Get Data check sycn", friend.getName());
                    RequestList.add(friend);
                }
                adaptor.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("The read failed: " ,databaseError.getMessage());
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
}
