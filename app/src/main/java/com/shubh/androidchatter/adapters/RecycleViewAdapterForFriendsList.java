package com.shubh.androidchatter.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shubh.androidchatter.MyApplication;
import com.shubh.androidchatter.R;
import com.shubh.androidchatter.extra_classes.FirebaseInctence;
import com.shubh.androidchatter.informationModels.Chats;
import com.shubh.androidchatter.informationModels.FriendsModel;
import com.shubh.androidchatter.informationModels.Requests;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.shubh.androidchatter.extra_classes.DbKeys.PREFEMAIL;

public class RecycleViewAdapterForFriendsList extends RecyclerView.Adapter<RecycleViewAdapterForFriendsList.MyViewHolder>{

    private Context context;
    private ArrayList<FriendsModel> data;
    private LayoutInflater inflater;

    FirebaseDatabase database = new FirebaseInctence().getinstence();
    DatabaseReference friends=database.getReference("FrindsList");
    private Resources resources;
    public RecycleViewAdapterForFriendsList(Context context, ArrayList<FriendsModel> data, Resources resources){
        inflater= LayoutInflater.from(context);
        this.data=data;
        this.resources=resources;
        this.context=context;
        Log.d("msg count on adapter",data.size()+"");
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view=inflater.inflate(R.layout.friends_show_list,parent,false);
        MyViewHolder holder=new MyViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final FriendsModel current=data.get(position);
        holder.frndName.setText(current.getName());
        holder.frndEmail.setText(current.getEmail());

        if(current.getProfileUrl()!=" "){
            Glide.with(context)
                    .load(current.getProfileUrl())
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
                    .into(holder.profilePic);
        }
        else {
            Picasso.with(context)
                    .load(R.drawable.ic_camera_alt_black_24dp)
                    .noFade()
                    .into(holder.profilePic);
        }
        //holder.icon.setImageResource(current.iconId);
        Log.d("POSITION","postion  on bind"+position);
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
    @Override
    public int getItemCount() {
        return data.size();

    }public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView frndName;
        public TextView frndEmail;
        public ImageView profilePic;
        public MyViewHolder(View v) {
            super(v);
            frndName = (TextView)v.findViewById(R.id.tv_chatname);
            frndEmail= (TextView)v.findViewById(R.id.tv_chatEmail);
            profilePic=(ImageView)v.findViewById(R.id.iv_ChatsProfilePic);
        }

    }
}