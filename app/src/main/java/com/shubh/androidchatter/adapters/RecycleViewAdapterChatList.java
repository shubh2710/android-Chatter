package com.shubh.androidchatter.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shubh.androidchatter.MyApplication;
import com.shubh.androidchatter.R;
import com.shubh.androidchatter.extra_classes.FirebaseInctence;
import com.shubh.androidchatter.informationModels.Chats;
import com.shubh.androidchatter.informationModels.MessagesModel;
import com.shubh.androidchatter.informationModels.User;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.shubh.androidchatter.extra_classes.DbKeys.PREFEMAIL;


public class RecycleViewAdapterChatList extends RecyclerView.Adapter<RecycleViewAdapterChatList.MyViewHolder>{

    private Context context;
    private List<Chats> data= Collections.emptyList();
    private LayoutInflater inflater;
    Resources resources;

    FirebaseDatabase database = new FirebaseInctence().getinstence();
    DatabaseReference msg=database.getReference("masseges");
    DatabaseReference users=database.getReference("users");

    public RecycleViewAdapterChatList(Context context, List<Chats> data, Resources resources){
        inflater= LayoutInflater.from(context);
        this.data=data;
        this.resources=resources;
        this.context=context;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view=inflater.inflate(R.layout.chat_show_list,parent,false);
        MyViewHolder holder=new MyViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        Chats model=data.get(position);
        String myemail=readPrefes(context,PREFEMAIL,null);
        Log.d("all msg",model.getChatName());
        if(model!=null){
            if(model.getSyncState())
                viewHolder.ll.setBackgroundColor(Color.argb(100,100,50,20));
            else
                viewHolder.ll.setBackgroundColor(Color.argb(100,100,50,100));
            viewHolder.chatName.setText(model.getChatName());
            viewHolder.chatEmail.setText(model.getChatFor());
            getursetDetile(model.getChatFor(),viewHolder.profilePic);
        }
        Log.d("POSITION","postion  on bind"+position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
   public class MyViewHolder extends RecyclerView.ViewHolder{
       public TextView chatName;
       public TextView chatEmail;
       public ImageView profilePic;
       public LinearLayout ll;
       public MyViewHolder(View v) {
           super(v);
           ll=(LinearLayout)v.findViewById(R.id.LinearLayout);
           chatName = (TextView)v.findViewById(R.id.tv_chatname);
           chatEmail= (TextView)v.findViewById(R.id.tv_chatEmail);
           profilePic=(ImageView)v.findViewById(R.id.iv_ChatsProfilePic);
           profilePic.setImageResource(R.drawable.images);
       }

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

    private void getursetDetile(String chatTo ,final ImageView pic) {
        users.keepSynced(true);
        Query query = users.orderByChild("email").equalTo(chatTo);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                User post = nodeDataSnapshot.getValue(User.class);
                Log.e("User profile", post.getProfileUrl());
                if(post.getProfileUrl()!=" "){
                    Glide.with(context)
                            .load(post.getProfileUrl())
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
                            .into(pic);
                }
                else {
                    pic.setImageResource(R.drawable.images);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
