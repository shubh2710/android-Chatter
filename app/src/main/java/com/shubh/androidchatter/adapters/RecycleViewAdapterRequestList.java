package com.shubh.androidchatter.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.shubh.androidchatter.informationModels.FriendsModel;
import com.shubh.androidchatter.informationModels.MessagesModel;
import com.shubh.androidchatter.informationModels.Requests;
import com.shubh.androidchatter.informationModels.User;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.shubh.androidchatter.extra_classes.DbKeys.PREFEMAIL;


public class RecycleViewAdapterRequestList extends RecyclerView.Adapter<RecycleViewAdapterRequestList.MyViewHolder>{

    private Context context;
    private List<Requests> data= Collections.emptyList();
    private LayoutInflater inflater;
    Resources resources;

    FirebaseDatabase database = new FirebaseInctence().getinstence();
    DatabaseReference users=database.getReference("users");
    DatabaseReference friends=database.getReference("FrindsList");
    DatabaseReference reqList=database.getReference("requests");
    FriendsModel frndObj=null;

    public RecycleViewAdapterRequestList(Context context, List<Requests> data, Resources resources){
        inflater= LayoutInflater.from(context);
        this.data=data;
        this.resources=resources;
        this.context=context;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view=inflater.inflate(R.layout.request_show_list,parent,false);
        MyViewHolder holder=new MyViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, final int position) {
        Requests model=data.get(position);
        Log.d("all msg",model.getName());
        viewHolder.RequestEmail.setText(model.getEmail());
        if(model!=null){
            getursetDetile(model.getEmail(),viewHolder.profilePic,viewHolder);
        }
        viewHolder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(frndObj!=null)
                addRequest(frndObj,position);
            }
        });
        Log.d("POSITION","postion  on bind"+position);
    }
    private void addRequest(final FriendsModel frndObj, final int p) {
        DatabaseReference frndRef=friends.child("friends").child(TohashCode(readPrefes(context,PREFEMAIL,null)));
        frndRef.keepSynced(true);
        frndRef.push().setValue(frndObj).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                data.remove(p);
                notifyDataSetChanged();
                updateRequest(frndObj.getEmail());
            }
        });
    }

    private void updateRequest(final String email) {
        final DatabaseReference RequestListRef=reqList.child(TohashCode(readPrefes(context,PREFEMAIL,null)));
        RequestListRef.keepSynced(true);
        RequestListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("read Requests" ,""+snapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Requests req=postSnapshot.getValue(Requests.class);
                    Log.e("Get Data check sycn", req.getName());
                    if(req.getEmail().equals(email)){
                        RequestListRef.child(postSnapshot.getRef().getKey()).removeValue();
                        notifyDataSetChanged();
                        Toast.makeText(context,"Request acepted",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("The read failed: " ,databaseError.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
   public class MyViewHolder extends RecyclerView.ViewHolder{
       public TextView RequestName;
       public TextView RequestEmail;
       public ImageView profilePic;
       public ImageButton add;
       public LinearLayout ll;
       public MyViewHolder(View v) {
           super(v);
           ll=(LinearLayout)v.findViewById(R.id.LinearLayout);
           add=(ImageButton)v.findViewById(R.id.b_addReq);
           RequestName = (TextView)v.findViewById(R.id.tv_chatname);
           RequestEmail= (TextView)v.findViewById(R.id.tv_chatEmail);
           profilePic=(ImageView)v.findViewById(R.id.iv_ChatsProfilePic);
       }
   }
    private String TohashCode(String code){
        String str1=code;
        Log.d("hashcode Update",str1);
        int hash1=(str1.hashCode());
        Log.d("hashcode1",hash1+"");
        return hash1+"";
    }
    public static String readPrefes(Context context, String prefesName, String defaultValue){
        SharedPreferences sharedPrefs= MyApplication.preferences;
        return sharedPrefs.getString(prefesName,defaultValue);
    }

    private void getursetDetile(String chatTo ,final ImageView pic,final MyViewHolder viewHolder) {
        users.keepSynced(true);
        Query query = users.orderByChild("email").equalTo(chatTo);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                User post = nodeDataSnapshot.getValue(User.class);
                Log.e("User profile", post.getProfileUrl());
                viewHolder.RequestName.setText(post.getName());

                frndObj=new FriendsModel(post.getName(),post.getEmail(),post.isProfile(),post.getProfileUrl(),post.getLastseen(),post.getStatus());
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
                    Picasso.with(context)
                            .load(R.drawable.images)
                            .noFade()
                            .into(pic);

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}