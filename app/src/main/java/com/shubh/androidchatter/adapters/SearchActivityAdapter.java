package com.shubh.androidchatter.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.shubh.androidchatter.database.ChatsDbHelper;
import com.shubh.androidchatter.extra_classes.DbKeys;
import com.shubh.androidchatter.extra_classes.FirebaseInctence;
import com.shubh.androidchatter.informationModels.FriendsModel;
import com.shubh.androidchatter.informationModels.Requests;
import com.shubh.androidchatter.informationModels.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.shubh.androidchatter.extra_classes.DbKeys.PREFEMAIL;
import static com.shubh.androidchatter.extra_classes.DbKeys.PREFNAME;

/**
 * Created by shubham on 2/25/2017.
 */

public class SearchActivityAdapter extends BaseAdapter {
    String [] result;
    Context context;
    int [] imageId;
    ArrayList<User> data;
     boolean isfound=false;
    private static LayoutInflater inflater=null;
    FirebaseDatabase database = new FirebaseInctence().getinstence();
    DatabaseReference reqList=database.getReference("requests");
    DatabaseReference friends=database.getReference("FrindsList");
    public SearchActivityAdapter(Context context, ArrayList<User> data) {
        // TODO Auto-generated constructor stub
        this.data=data;
        this.context=context;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView userName;
        TextView userEmail;
        ImageView profilePic;
        ImageButton request;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        final User model= data.get(position);
        View rowView;
        rowView = inflater.inflate(R.layout.search_show_list, null);
        holder.request=(ImageButton)rowView.findViewById(R.id.b_request);
        holder.userName = (TextView)rowView.findViewById(R.id.tv_chatname);
        holder.userEmail= (TextView)rowView.findViewById(R.id.tv_chatEmail);
        holder.profilePic=(ImageView)rowView.findViewById(R.id.iv_ChatsProfilePic);
        holder.userName.setText(model.getName());
        holder.userEmail.setText(model.getEmail());
        if(model.getEmail().equals(readPrefes(context, DbKeys.PREFEMAIL,"")) || check(model.getEmail()) ){
            holder.request.setVisibility(View.GONE);
        }
        else {
            holder.request.setVisibility(View.VISIBLE);
            holder.request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanFriendList(data.get(position).getEmail(),model);
            }
        });
        }
        if(model.getProfileUrl()!=" "){
            Glide.with(context)
                    .load(model.getProfileUrl())
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
                    .into(holder.profilePic);
        }
        else {
            Picasso.with(context)
                    .load(R.drawable.images)
                    .noFade()
                    .into(holder.profilePic);
        }
        return rowView;
    }

    private void scanFriendList(final String email,final User u) {
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
                    sendRequest(email,u,isfound);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("The read failed: " ,databaseError.getMessage());
                }
            });
        }

    private boolean check(String email) {
        ChatsDbHelper dbHelper=new ChatsDbHelper(context);
        SQLiteDatabase database=dbHelper.getReadableDatabase();
        return  dbHelper.CheckFriendExist(email,database);
    }

    private void sendRequest(final String email ,User u,boolean isfound) {
        if(!isfound){
            final DatabaseReference RequestListRef=reqList.child(TohashCode(email));
            RequestListRef.keepSynced(true);
            RequestListRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Log.e("read Requests" ,""+snapshot.getChildrenCount());
                    Boolean isfound=false;
                    Requests post=null;
                    if(snapshot.getChildrenCount()==0){
                        Toast.makeText(context, "request Added", Toast.LENGTH_LONG).show();
                        final DatabaseReference request = reqList.child(TohashCode(email));
                        Requests r = new Requests(readPrefes(context, PREFNAME, null), false, readPrefes(context, PREFEMAIL, null));
                        request.push().setValue(r);
                    }
                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        post=postSnapshot.getValue(Requests.class);
                        Log.e("Requests", post.getEmail());
                        if (post.getEmail().equals((readPrefes(context, PREFEMAIL, null)))) {
                            Log.e("Request found", post.getEmail());
                            isfound=true;
                        }
                    }
                    addRequest(isfound,post);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }else{
            Toast.makeText(context,"You already is a Friend",Toast.LENGTH_LONG).show();
        }
        addInMyFriendList(email,u);
    }

    private void addInMyFriendList(String email,User post) {
        if(!checkFriend(email)){
            DatabaseReference frndRef = friends.child("friends").child(TohashCode(readPrefes(context, PREFEMAIL, null)));
            frndRef.keepSynced(true);
            FriendsModel frndObj=new FriendsModel(post.getName(),post.getEmail(),post.isProfile(),post.getProfileUrl(),post.getLastseen(),post.getStatus());
            frndRef.push().setValue(frndObj);
        }
    }
    private boolean checkFriend(String messageUser) {
        ChatsDbHelper dbHelper=new ChatsDbHelper(context);
        SQLiteDatabase database=dbHelper.getReadableDatabase();
        return  dbHelper.CheckFriendExist(messageUser,database);
    }
    private void addRequest(Boolean isfound, Requests post) {
        if(post!=null && !isfound){
            Toast.makeText(context, "request Added", Toast.LENGTH_LONG).show();
            final DatabaseReference request = reqList.child(TohashCode(post.getEmail()));
            Requests r = new Requests(readPrefes(context, PREFNAME, null), false, readPrefes(context, PREFEMAIL, null));
            request.push().setValue(r);
        }
    }

    private String TohashCode(String code){
        String str1=code;
        Log.d("hashcode2",str1);
        int hash1=(str1.hashCode());
        Log.d("hashcode2",hash1+"");
        return hash1+"";
    }
    public boolean checkexsit(String chaTo){
        ChatsDbHelper dbHelper=new ChatsDbHelper(context);
        SQLiteDatabase database=dbHelper.getReadableDatabase();
        if(dbHelper.CheckChatExist(chaTo,database))
            return true;
        else
            return false;
    }
    public static String readPrefes(Context context, String prefesName, String defaultValue){
        SharedPreferences sharedPrefs= MyApplication.preferences;
        return sharedPrefs.getString(prefesName,defaultValue);
    }

}
