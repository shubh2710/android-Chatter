package com.shubh.androidchatter.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shubh.androidchatter.MyApplication;
import com.shubh.androidchatter.R;
import com.shubh.androidchatter.extra_classes.FirebaseInctence;
import com.shubh.androidchatter.extra_classes.FirebaseViewHolder;
import com.shubh.androidchatter.informationModels.MessagesModel;

import java.util.Date;
import java.util.HashMap;

import static com.shubh.androidchatter.extra_classes.DbKeys.PREFEMAIL;

/**
 * Created by root on 21/7/17.
 */

public class fireBaseAdapter  extends FirebaseRecyclerAdapter<MessagesModel, FirebaseViewHolder> {
        private static final String TAG = fireBaseAdapter.class.getSimpleName();
        private Context context;
        RecyclerView listOfMessages;
    FirebaseDatabase database = new FirebaseInctence().getinstence();
        DatabaseReference msg=database.getReference("masseges");
        public fireBaseAdapter(RecyclerView listOfMessages,Class<MessagesModel> modelClass, int modelLayout, Class<FirebaseViewHolder> viewHolderClass, DatabaseReference ref, Context context) {
            super(modelClass, modelLayout, viewHolderClass, ref);
            this.context = context;
            this.listOfMessages=listOfMessages;
        }
        @Override
        protected void populateViewHolder(FirebaseViewHolder viewHolder, MessagesModel model,int position) {
            // Set their text
            String myemail=readPrefes(context,PREFEMAIL,null);
            Log.d("all msg",model.getMessageText());
            if(model.getMessageUser().equals(myemail)){
                Log.d("MY msg",model.getMessageText());
                viewHolder.HisLayout.setVisibility(View.GONE);
                viewHolder.myLayout.setVisibility(View.VISIBLE);
                viewHolder.MymessageText.setText(model.getMessageText());
                viewHolder.MymessageUser.setText("me");
                viewHolder.MymessageTime.setText(DateFormat.format("(HH:mm)",model.getMessageTime()));
                if(model.getMessageSyncState()){
                    viewHolder.MymessageStatus.setText("D");
                    viewHolder.MymessageStatus.setTextColor(Color.argb(100,239, 42, 78));
                }else {
                    viewHolder.MymessageStatus.setText("P");
                    viewHolder.MymessageStatus.setTextColor(Color.argb(100,239, 42, 110));
                }

            }else{
                Log.e("HIS msg",model.getMessageText());
                viewHolder.myLayout.setVisibility(View.GONE);
                viewHolder.HisLayout.setVisibility(View.VISIBLE);
                viewHolder.messageText.setText(model.getMessageText());
                viewHolder.messageUser.setText(model.getSenderUserName());
                viewHolder.messageTime.setText(DateFormat.format("(HH:mm)",model.getMessageTime()));
            }
            if(model.getMessageSyncState()==false && model.getMessageSendTo().equals(myemail))
                updateSyncState(model);
        }
    public void updateSyncState(MessagesModel model){
        final DatabaseReference updateMsg =msg.child("convensation").child(encry(model.getMessageUser()));
        updateMsg.keepSynced(true);
        Query query = updateMsg.orderByChild("messageSyncState").equalTo(false);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                String path = "/" + key;
                HashMap<String, Object> result = new HashMap<>();
                result.put("messageSyncState", true);
                Log.d("update chat",result.toString());
                updateMsg.child(path).updateChildren(result);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private String TohashCode(String code){
        String str1=code;
        Log.d("hashcode Update",str1);
        int hash1=(str1.hashCode());
        Log.d("hashcode1",hash1+"");
        return hash1+"";
    }

    private String encry(String chatTo) {
        String str1=chatTo;
        String str2=readPrefes(context,PREFEMAIL,null);
        if(str2!=null){
            int hash1=(str1.hashCode());
            int hash2=(str2.hashCode());
            return (hash1+hash2)+"";
        }else return null;
    }
    public static String readPrefes(Context context, String prefesName, String defaultValue){
        SharedPreferences sharedPrefs= MyApplication.preferences;
        return sharedPrefs.getString(prefesName,defaultValue);
    }

}

